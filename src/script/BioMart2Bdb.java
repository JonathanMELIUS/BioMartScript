package script;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.DataSourceTxt;
import org.bridgedb.creator.BridgeDbCreator;
import org.bridgedb.creator.DbBuilder;
import org.bridgedb.tools.qc.BridgeQC;
import org.w3c.dom.Document;

public class BioMart2Bdb {

	private BioMartAttributes bio;
	private HashMap<Xref, HashSet<Xref>>  dbEntries = new HashMap<Xref, HashSet<Xref>>();	
	private HashMap<Xref, GeneAttributes>  geneSet = new HashMap<Xref, GeneAttributes>();
	private SpeciesConfiguration config;
	
	
	public BioMart2Bdb(SpeciesConfiguration config,BioMartAttributes bio,HashMap<Xref, 
			HashSet<Xref>>  dbEntries,HashMap<Xref, GeneAttributes>  geneSet){
		this.config=config;
		this.bio=bio;
		this.dbEntries=dbEntries;
		this.geneSet=geneSet;
	}
	
	
	public void query(String organism, String externalSource,Boolean attributes){
		Document result = QueryBioMart.createQuery(organism,externalSource, config.getSchema(),attributes);
		InputStream is = QueryBioMart.getDataStream(result,config.getEndpoint());
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		try {
			parse(br);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void parse(BufferedReader br) throws IOException{

		String line = br.readLine();
		String[] split = line.split("\t");
		DataSource ds=null;
		try{
			if (split[1].equals("UniProt/SwissProt Accession") 
				|| split[1].equals("UniProtKB/SwissProt ID")
				|| split[1].equals("UniProt/SwissProt ID")){
				split[1]="UniProt/TrEMBL Accession";
			}
			System.out.println(split[1]);
			ds = DataSource.getExistingByFullName(bio.getReference(split[1]).get(0).getGpmlName());
			line = br.readLine(); //Skip the header
			while (line != null) {	
				split = line.split("\t");
				Xref mainXref = new Xref(split[0], DataSource.getExistingBySystemCode("En"));
				if (split.length>1){ // only parse if there is a external reference in this Ensembl id
					Xref xref = new Xref(split[1],ds);
					GeneAttributes gene = new GeneAttributes(split[2], split[3], split[4], split[5]);
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
				line = br.readLine();
			}
		}
		catch (ArrayIndexOutOfBoundsException ae){
			System.err.println("Incorrect datasource	"+split[0]);			
		}
		br.close();
	}

	public void bridgedbCreator(Map<Xref, HashSet<Xref>> dbEntries,
			Map<Xref, GeneAttributes>  geneSet,String path, String name) 
					throws IDMapperException, ClassNotFoundException{

		BridgeDbCreator creator = new BridgeDbCreator(dbEntries);

		creator.setOutputFilePath(path+name);
		creator.setDbSourceName("Ensembl");
		creator.setDbVersion("0.1");
		creator.setDbSeries(config.getDBName());
		creator.setDbDataType("GeneProduct");

		DbBuilder dbBuilder = new DbBuilder(creator);
		dbBuilder.createNewDb();

		dbBuilder.addEntry(dbEntries,geneSet);

		dbBuilder.finalizeDb();

		System.out.println(dbBuilder.getError()+" errors (duplicates) occurred"+ dbBuilder.getErrorString());
	}
	public HashMap<Xref, HashSet<Xref>> getDbEntries() {
		return dbEntries;
	}
	public void setDbEntries(HashMap<Xref, HashSet<Xref>> dbEntries) {
		this.dbEntries = dbEntries;
	}
	public BioMartAttributes getBio() {
		return bio;
	}
	public void setBio(BioMartAttributes bio) {
		this.bio = bio;
	}
	public HashMap<Xref, GeneAttributes> getGeneSet() {
		return geneSet;
	}
	public void setGeneSet(HashMap<Xref, GeneAttributes> geneSet) {
		this.geneSet = geneSet;
	}
}
