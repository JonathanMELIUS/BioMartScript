package script;

import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.DataSourceTxt;
import org.bridgedb.creator.BridgeDbCreator;
import org.bridgedb.creator.DbBuilder;
import org.bridgedb.tools.qc.BridgeQC;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

public class EnsemblREST {
	static HashMap<Xref, HashSet<Xref>>  dbEntries = new HashMap<Xref, HashSet<Xref>>();	//Contains the mapping from Ensembl to external database 
	static HashMap<Xref, GeneAttributes>  geneSet = new HashMap<Xref, GeneAttributes>();	//Contains the gene attributes of the Ensembl gene id
	static Document doc;
	public static void main(String[] args) throws Exception {
		DataSourceTxt.init(); //Initialize BrideDb data source

		//http://rest.ensemblgenomes.org/lookup/genome/escherichia_coli_str_k_12_substr_mg1655?content-type=application/json&xrefs=1&level=translation
		String server = "http://rest.ensemblgenomes.org";
		//		String ext = "/lookup/genome/campylobacter_jejuni_subsp_jejuni_bh_01_0142?";
		//		String ext = "/lookup/genome/campylobacter_jejuni_subsp_jejuni_bh_01_0142?xrefs=1&level=translation";
		//		String ext = "/lookup/genome/escherichia_coli_str_k_12_substr_mg1655?xrefs=1&level=translation";
		//		String ext = "/lookup/genome/mycobacterium_tuberculosis_h37rv?xrefs=1&level=translation";
		String ext = "/lookup/genome/bacillus_subtilis_bsn5?xrefs=1&level=translation";
		URL url = new URL(server + ext);

//		URLConnection connection = url.openConnection();
//		HttpURLConnection httpConnection = (HttpURLConnection)connection;
//
//		//		httpConnection.setRequestProperty("Content-Type", "application/json");
//		httpConnection.setRequestProperty("Content-Type", "text/xml");
//
//
//		InputStream response = connection.getInputStream();
//		int responseCode = httpConnection.getResponseCode();
//
//		if(responseCode != 200) {
//			throw new RuntimeException("Response code was not 200. Detected response was "+responseCode);
//		}



		File fXmlFile = new File("mapping_ec.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		//		Document doc = dBuilder.parse(fXmlFile);
		//		Document doc = dBuilder.parse(response);
		doc = dBuilder.parse(fXmlFile);
//		doc = dBuilder.parse(response);
		doc.getDocumentElement().normalize();
		
		parseXrefs();
		bdbCreate();
		
		
		//		NodeList nList = doc.getElementsByTagName("data");
		//		System.out.println(nList.getLength());
		//		System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		//Contains the mapping from Ensembl to external database
		//		HashMap<Xref, HashSet<Xref>>  dbEntries = new HashMap<Xref, HashSet<Xref>>();	
		//Contains the gene attributes of the Ensembl gene id
		//		HashMap<Xref, GeneAttributes>  geneSet = new HashMap<Xref, GeneAttributes>();	
	}

	public static void writeXML(InputStream response) throws ParserConfigurationException, IOException{
		String output;
		Reader reader = null;

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc;
		try {
			reader = new BufferedReader(new InputStreamReader(response, "UTF-8"));


			File targetFile = new File("mapping_bs2.xml");
			OutputStream outStream = new FileOutputStream(targetFile);
			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = response.read(bytes)) != -1) {
				outStream.write(bytes, 0, read);
			}
			outStream.close();
			System.out.println("Done!");

			//			doc = dBuilder.parse(response);

			//			StringBuilder builder = new StringBuilder();
			//			char[] buffer = new char[8192];
			//			int read;
			//			while ((read = reader.read(buffer, 0, buffer.length)) > 0) {
			//				builder.append(buffer, 0, read);
			//			}
			//			output = builder.toString();
		} 
		finally {

			if (reader != null) try {
				reader.close(); 
			} catch (IOException logOrIgnore) {
				logOrIgnore.printStackTrace();
			}
		}

		//		System.out.println(output);
	}
	public static void parseXrefs(){
		NodeList aList = doc.getElementsByTagName("xrefs");
		for (int atemp = 0; atemp < aList.getLength(); atemp++) {	 //aList.getLength()
			Node aNode = aList.item(atemp);

			//			System.out.println("\nCurrent Element :" + aNode.getNodeName());
			//			System.out.println("\n Parent : "+aNode.getParentNode().getNodeName());
			if (aNode.getParentNode().getNodeName()=="translations"){
				//				System.out.println(aNode.getParentNode().getParentNode().getParentNode().getNodeName());
				Node data = aNode.getParentNode().getParentNode().getParentNode();
				Element eElement = (Element) aNode;
				Element eData = (Element) data;
				//				System.out.println("id : " + eData.getAttribute("id"));
				//				System.out.println("name : " + eData.getAttribute("name"));
				//				System.out.println("biotype : " + eData.getAttribute("biotype"));
				//				System.out.println("description : " + eData.getAttribute("description"));
				//				System.out.println("chromosome : " + eData.getAttribute("start").charAt(0));
				//				System.out.println("dbname id : " + eElement.getAttribute("dbname"));
				//				System.out.println("display_id : " + eElement.getAttribute("display_id"));


				Xref mainXref = new Xref(eData.getAttribute("id"), DataSource.getExistingBySystemCode("En"));
				DataSource ds = null;
				if (eElement.getAttribute("dbname").equals("Uniprot/SWISSPROT") ||eElement.getAttribute("dbname").equals("Uniprot/SPTREMBL")  ){
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

	public static void bdbCreate() throws IDMapperException, SQLException, FileNotFoundException{
		BridgeDbCreator creator = new BridgeDbCreator(dbEntries);

		creator.setOutputFilePath("/home/bigcat-jonathan/LinkTest/derby_test/Ec_Derby_Ensembl_80");
		creator.setDbSourceName("Ensembl");
		creator.setDbVersion("0.1");
		creator.setDbSeries("Escherichia coli genes and proteins");
//		creator.setDbSeries("Bacillus subtilis genes and proteins");
//		creator.setDbSeries("Mycobacterium Tuberculosis genes and proteins");
		creator.setDbDataType("GeneProduct");

		DbBuilder dbBuilder = new DbBuilder(creator);
		dbBuilder.createNewDb();

		dbBuilder.addEntry(dbEntries,geneSet);

		dbBuilder.finalizeDb();

		System.out.println(dbBuilder.getError()+" errors (duplicates) occurred"+ dbBuilder.getErrorString());
		
		
		BridgeQC main = new BridgeQC (new File("/home/bigcat-jonathan/LinkTest/derby_old/"
				+"Ec_Derby_20130701.bridge"),
				new File("/home/bigcat-jonathan/LinkTest/derby_test/"
						+"Ec_Derby_Ensembl_80.bridge"));	
		main.run();
		String fileName = "/home/bigcat-jonathan/LinkTest/derby_test/"+"report_"+"Ec_Derby_Ensembl_80"+".qc";
		PrintWriter pw  = new PrintWriter(new FileOutputStream(fileName));	
		pw.println(main.getOutput());
		pw.close();
	}
	public void parseXML(){
		/*
		NodeList nList = doc.getElementsByTagName("data");

		//				for (int temp = 0; temp < nList.getLength(); temp++) {
		for (int temp = 0; temp < 1; temp++) {	 
			Node nNode = nList.item(temp);
			//			System.out.println(nNode.getNodeType());
			//			System.out.println(nNode.getAttributes().item(0));

			System.out.println("\nCurrent Element :" + nNode.getNodeName());
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;
				System.out.println("id : " + eElement.getAttribute("id"));
				//				System.out.println("dbname id : " + eElement.getAttribute("dbname"));
				//				System.out.println("primary_id : " + eElement.getAttribute("primary_id"));
			}
			NodeList aList = doc.getElementsByTagName("xrefs");
			for (int atemp = 0; atemp < 2; atemp++) {	 //aList.getLength()
				Node aNode = aList.item(atemp);

				System.out.println("\nCurrent Element :" + aNode.getNodeName());
				System.out.println("\n Parent : "+aNode.getParentNode().getNodeName());
				if (aNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) aNode;
					//					System.out.println("id : " + eElement.getAttribute("id"));
					System.out.println("dbname id : " + eElement.getAttribute("dbname"));
					System.out.println("primary_id : " + eElement.getAttribute("primary_id"));
				}
			}
		}*/

		//		System.out.println("----------------------------");
	}
}