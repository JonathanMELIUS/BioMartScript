package script;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.DataSourceTxt;
import org.bridgedb.tools.qc.BridgeQC;
import org.wikipathways.reportbots.OutdatedIdsReport;

public class Main {
	protected static Logger logger;
	
	/**
	 * java -jar BioMart2BridgeDb.jar arg1 arg2 arg3 arg4
	 * arg1: location of configuration file
	 * arg2: location for the new database
	 * arg3: (optional) directory of the old database - run QC
	 * arg4: (optional) use inclusive BridgeDb list
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws IDMapperException
	 * @throws IOException
	 */
	public static void main(String[] args) 
			throws ClassNotFoundException, IDMapperException, IOException {
		logInit();
		DataSourceTxt.init(); //Initialize BrideDb data source
		File dir = new File (args[0]);
		String path = args[1];
		String pathOld = null;
		Boolean qc = false;	
		Boolean include = false;
		
		switch (args.length) {
	        case 3: pathOld = args[2];
					qc = true;
	                break;
	        case 4:	include = true;	 
	        		break;
		}		
		if (dir.isDirectory()){
			FilenameFilter textFilter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					String lowercaseName = name.toLowerCase();
					if (lowercaseName.endsWith(".config")) {
						return true;
					} else {
						return false;
					}
				}
			};
			File[] listFiles = dir.listFiles(textFilter);			
			for(File f : listFiles){
				SpeciesConfiguration config = new SpeciesConfiguration(f.getAbsolutePath());
				runDB(config, path,include);
				if (qc) report(false,pathOld,path,config);
			}
			OutdatedIdsReport.writeAll("All_ids.tsv");
		}
		else{
			SpeciesConfiguration config = new SpeciesConfiguration(dir.getAbsolutePath());
			runDB(config, path,include);
			if (qc) report(false,pathOld,path,config);			
		}
	}
	
	/**
	 * Run the creation of the new database
	 * @param config Configuration file for this species
	 * @param path  A pathname string for the new generated database
	 * @param inducle_Filter If true, select only the datasources form the BridgeDb inclusive list 
	 * (see {@link script.SpeciesConfiguration})
	 * @throws ClassNotFoundException
	 * @throws IDMapperException
	 */
	public static void runDB(SpeciesConfiguration config,String path, Boolean inducle_Filter) 
			throws ClassNotFoundException, IDMapperException{

		Date date = new Date();	
		System.out.println(date);

		HashMap<Xref, HashSet<Xref>>  dbEntries = new HashMap<Xref, HashSet<Xref>>();	//Contains the mapping from Ensembl to external database 
		HashMap<Xref, GeneAttributes>  geneSet = new HashMap<Xref, GeneAttributes>();	//Contains the gene attributes of the Ensembl gene id

		BioMartAttributes bio = new BioMartAttributes();
		// Initialize the loading of the default mapping between BioMart and BridgeDb 
		bio.init();		
		//Query the BioMart attributes for probes ids.
		QueryBioMart.loadBiomartAttributes(bio,config);		
		BioMart2Bdb mart = new BioMart2Bdb(config,bio,dbEntries,geneSet);
		
		// Probe queries
		try{
			for (String probe:config.getProbeSet()){
				mart.query(probe,true,true);
			}
		}
		catch (NullPointerException ne){
			for (String probe:config.getProbe()){
				for(BioMartReference ref :bio.getReference(probe)){
					mart.query(ref.getQueryName(),true,true);
				}
			}
		}
		// Genes & proteins queries
		List<String> datasources;
		if (inducle_Filter)
			datasources = config.filterDatasource(config.getDatasource(),bio);
		else
			datasources = config.getDatasource();
		for (String ds:	datasources){
			mart.query(ds,true,true);
		}
		System.out.println(date);
		mart.bridgedbCreator(dbEntries,geneSet,path);
		System.out.println(date);
	}
	
	/**
	 * Run the creation of the QC & WP reports of the new database
	 * @param allSpecies If true create the report with all the outdated ids for all the species of Wikipathways
	 * @param pathOld A pathname string for the previous databases
	 * @param path A pathname string for the of new generated database
	 * @param config Configuration file for this species
	 */
	public static void report(Boolean allSpecies,String pathOld,String path,SpeciesConfiguration config) {

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
		System.out.println(symbol);
		String patholdDB = "";
		File oldDB = null;
		for (File f : listFiles){
			if (f.getName().startsWith(symbol)){
				patholdDB = f.getAbsolutePath();
				oldDB = f;
			}
		}
		String current = path+config.getFileName()+".bridge";
		String report = path+config.getFileName()+"_outdatedQC";
		try{
			runQC(oldDB, path, config.getFileName());
			OutdatedIdsReport.run(patholdDB,current,report);
			if (allSpecies)OutdatedIdsReport.runAll(patholdDB,current);
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * Run the Quality Control between the previous database and the new generated database.<br>
	 * Create the .qc file at the location provided
	 * @param oldDB File - location - of the previous database 
	 * @param path Path of new generated database
	 * @param dbName Filename of the new generated database
	 */
	public static void runQC(File oldDB, String path, String dbName){
		try {
			BridgeQC main = new BridgeQC (oldDB,
					new File(path+dbName+".bridge"));	
			main.run();
			String fileName = path+"report_"+dbName+".qc";
			PrintWriter pw  = new PrintWriter(new FileOutputStream(fileName));	
			pw.println(main.getOutput());
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Please check the path of the old database");
		}
	

	}
	/**
	 * Initialization of the log file
	 */
	public static void logInit(){
		logger = Logger.getLogger("Bdb_creation");  
	    FileHandler fh;
	    try { 
	        // This block configure the logger with handler and formatter  
	        fh = new FileHandler("Bdb_creation.log"); 
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);
	    } catch (SecurityException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }
	}
}
