package script;

import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.DataSourceTxt;
import org.bridgedb.creator.BridgeDbCreator;
import org.bridgedb.creator.DbBuilder;
import org.bridgedb.tools.qc.BridgeQC;
import org.pathvisio.core.model.ConverterException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.wikipathways.reportbots.OutdatedIdsReport;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

public class EnsemblREST {
	static HashMap<Xref, HashSet<Xref>>  dbEntries; 	//Contains the mapping from Ensembl to external database 
	static HashMap<Xref, GeneAttributes>  geneSet;	//Contains the gene attributes of the Ensembl gene id
	static Document doc;
	public static void main(String[] args) throws Exception {
		DataSourceTxt.init(); //Initialize BrideDb data source

		File dir = new File (args[0]);
		String path = args[1];
		String pathOld = null;
		Boolean qc = false;
		if (args.length>2){
			pathOld = args[2];
			qc = true;
		}
		if (dir.isDirectory()){
			FilenameFilter textFilter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					String lowercaseName = name.toLowerCase();
					if (lowercaseName.endsWith(".configbact")) {
						return true;
					} else {
						return false;
					}
				}
			};
			File[] listFiles = dir.listFiles(textFilter);

			for(File f : listFiles){
				SpeciesConfiguration config = new SpeciesConfiguration(f.getAbsolutePath());
				queryXml(config);
				parseXrefs();
				bdbCreate(path,pathOld,config);
				report(qc,pathOld,path,config);
			}
		}
		else{
			SpeciesConfiguration config = new SpeciesConfiguration(dir.getAbsolutePath());
			queryXml(config);
			parseXrefs();
			bdbCreate(path,pathOld,config);
			report(qc,pathOld,path,config);
		}		
	}
	public static void queryXml(SpeciesConfiguration config) 
			throws IOException, ParserConfigurationException, SAXException{
		String server = "http://rest.ensemblgenomes.org";
		String ext = "/lookup/genome/"+config.getSpecies()+"?xrefs=1&level=translation";
		URL url = new URL(server + ext);
		
		
		URLConnection connection = url.openConnection();
		HttpURLConnection httpConnection = (HttpURLConnection)connection;
		httpConnection.setRequestProperty("Content-Type", "text/xml");
		InputStream response = connection.getInputStream();
		int responseCode = httpConnection.getResponseCode();
		if(responseCode != 200) {
			throw new RuntimeException("Response code was not 200. Detected response was "+responseCode);
		}
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		doc = db.parse(response);
		doc.getDocumentElement().normalize();		
	}
	
	public static void parseXrefs(){
		dbEntries = new HashMap<Xref, HashSet<Xref>>();	
		geneSet = new HashMap<Xref, GeneAttributes>();
		NodeList aList = doc.getElementsByTagName("xrefs");
		for (int atemp = 0; atemp < aList.getLength(); atemp++) {
			Node aNode = aList.item(atemp);
			if (aNode.getParentNode().getNodeName()=="translations"){
				Node data = aNode.getParentNode().getParentNode().getParentNode();
				Element eElement = (Element) aNode;
				Element eData = (Element) data;
				
				Xref mainXref = new Xref(eData.getAttribute("id"), DataSource.getExistingBySystemCode("En"));
				DataSource ds = null;
				if (eElement.getAttribute("dbname").equals("Uniprot/SWISSPROT") 
						||eElement.getAttribute("dbname").equals("Uniprot/SPTREMBL")  ){
					ds = DataSource.getExistingByFullName("Uniprot-TrEMBL");
				}
				if (eElement.getAttribute("dbname").equals("GO")){
					ds = DataSource.getExistingByFullName("GeneOntology");
				}
				String chromosome = String.valueOf(eData.getAttribute("start").charAt(0));
				if (ds!=null){
					Xref xref = new Xref(eElement.getAttribute("display_id"),ds);
//					GeneAttributes(String description, String chromosome, String symbol,String type)
					GeneAttributes gene = new GeneAttributes(
							eData.getAttribute("description"),
							chromosome,
							eData.getAttribute("name"),
							eData.getAttribute("biotype"));

					geneSet.put(mainXref, gene);
					geneSet.put(xref, gene);
					HashSet<Xref> xrefSet = dbEntries.get(mainXref);
					if (xrefSet==null){
						HashSet<Xref> database = new HashSet<Xref>();
						database.add(xref);
						dbEntries.put(mainXref, database);
					}
					else{
						xrefSet.add(xref);
					}
				}
			}
		}
	}

	public static void bdbCreate(String path,String pathOld, SpeciesConfiguration config) 
			throws IDMapperException, SQLException, FileNotFoundException{
		BridgeDbCreator creator = new BridgeDbCreator(dbEntries);
		
		creator.setOutputFilePath(path+config.getFileName());
		creator.setDbSourceName("Ensembl");
		creator.setDbVersion("1");
		creator.setDbSeries(config.getDBName());
		creator.setDbDataType("GeneProduct");
		
		DbBuilder dbBuilder = new DbBuilder(creator);
		dbBuilder.createNewDb();
		dbBuilder.addEntry(dbEntries,geneSet);
		dbBuilder.finalizeDb();
		System.out.println(dbBuilder.getError()+" errors (duplicates) occurred"+ dbBuilder.getErrorString());
		
	}
	public static void report(boolean qc,String pathOld, String path,SpeciesConfiguration config) 
			throws IDMapperException, SQLException, ClassNotFoundException, IOException, ConverterException{
		if (qc){
			File dir = new File (pathOld);
			FilenameFilter textFilter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					String lowercaseName = name.toLowerCase();
					if (lowercaseName.endsWith(".bridge")) {
						return true;
					} else {
						return false;
					}
				}
			};
			File[] listFiles = dir.listFiles(textFilter);
			String symbol = ""+config.getFileName().toUpperCase().charAt(0)+config.getFileName().charAt(1);
			String patholdDB = "";
			File oldDB = null;
			for (File f : listFiles){
				if (f.getName().startsWith(symbol)){
					patholdDB = f.getAbsolutePath();
					oldDB = f;
				}
			}
			runQC(oldDB, path, config);
			String current = path+config.getFileName()+".bridge";
			String report = path+config.getFileName()+"_outdatedQC";
			OutdatedIdsReport.run(patholdDB,current,report);
		}
	}
	public static void runQC(File oldDB, String path,SpeciesConfiguration config) 
			throws IDMapperException, SQLException, FileNotFoundException{
		String fileName = path+"report_"+config.getFileName()+".qc";
		OutputStream out = new FileOutputStream(fileName);
		BridgeQC main = new BridgeQC (
			oldDB,
			new File(path+config.getFileName()+".bridge"),
			out
		);	
		main.run();
		out.flush();
		out.close();
	}
}
