package script;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.DataSourceTxt;
import org.bridgedb.creator.BridgeDbCreator;
import org.bridgedb.creator.DbBuilder;
import org.w3c.dom.Document;

public class BioMart2Bdb {

	final static String IDENTIFIERS_ORG_PREFIX = "http://identifiers.org/";

	public static void main(String[] args) throws ClassNotFoundException, IDMapperException, IOException {
		// TODO Auto-generated method stub
		DataSourceTxt.init();
		
		//You can programmatically query Biomart
		Document result = QueryBioMart.createQuery("hsapiens_gene_ensembl", "TSV" );
		InputStream is = QueryBioMart.getDataStream(result);
		String inputStream = QueryBioMart.getStringFromInputStream(is);
		
		String name = "biomart.txt";
		OutputStream ttl = new FileOutputStream(name);
		PrintWriter fileWriter = new PrintWriter(ttl);
		fileWriter.println(inputStream);
		fileWriter.close();		
		
		
		//You can also provide directly a tsv file download manually 
		//		String name = "mart_export.txt";
		
		//Then parse the file and create the database
		HashMap<Xref, List<Xref>>  dbEntries = new HashMap<Xref, List<Xref>>();		
		parseFile(dbEntries,name);
		bridgedbCreator(dbEntries,"testBDB");
	}

	public static HashMap<Xref, List<Xref>> parseFile(HashMap<Xref, List<Xref>> dbEntries, String name) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(name));

		String line = br.readLine();
		line = br.readLine(); //Skip the header 

		String[] split;		

		while (line != null) {	
			split = line.split("\t");
			Xref mainXref = new Xref(split[0], DataSource.getExistingBySystemCode("En"));
			if (split.length>1){ // only parse if there is a external reference in this Ensembl id
				// in this example we parse a Ensembl -> OMIM file
				DataSource ds = DataSource.getExistingBySystemCode("Om"); // TODO recognize automatically the external source
				
				System.out.println(split[0]);
				Xref xref = new Xref(split[1],ds);
				if (!dbEntries.containsKey(split[0])) {
					ArrayList<Xref> database = new ArrayList<Xref>();
					database.add(xref);
					dbEntries.put(mainXref, database);
				}
				else{					
					dbEntries.get(mainXref).add(xref);
				}
			}
			line = br.readLine();
		}
		br.close();
		return dbEntries;
	}

	public static void bridgedbCreator(HashMap<Xref, List<Xref>> dbEntries, String name) throws IDMapperException, ClassNotFoundException{



		BridgeDbCreator creator = new BridgeDbCreator(dbEntries);

		creator.setOutputFilePath(name);
		creator.setDbSourceName("Ensembl");
		creator.setDbVersion("0.1");
		creator.setDbSeries("Homo sapiens genes and proteins");
		creator.setDbDataType("GeneProduct");

		DbBuilder dbBuilder = new DbBuilder(creator);
		dbBuilder.createNewDb();

		dbBuilder.addEntry(dbEntries);

		dbBuilder.finalizeDb();

		System.out.println(dbBuilder.getError()+" errors (duplicates) occurred"+ dbBuilder.getErrorString());
	}

}
