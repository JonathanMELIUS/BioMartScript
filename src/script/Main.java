package script;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.DataSourceTxt;
import org.bridgedb.tools.qc.BridgeQC;
import org.pathvisio.core.model.ConverterException;
import org.wikipathways.reportbots.OutdatedIdsReport;

public class Main {

	public static void main(String[] args) throws ClassNotFoundException, IDMapperException, IOException, SQLException, ConverterException {
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
//				runDB(config, path);
				report(qc,pathOld,path,config);
			}
		}
		else{
			SpeciesConfiguration config = new SpeciesConfiguration(dir.getAbsolutePath());
			runDB(config, path);
			report(qc,pathOld,path,config);			
		}
	}
	
	public static void runDB(SpeciesConfiguration config,String path) throws ClassNotFoundException, IDMapperException{
		DataSourceTxt.init(); //Initialize BrideDb data source

		Date date = new Date();	
		System.out.println(date);

		HashMap<Xref, HashSet<Xref>>  dbEntries = new HashMap<Xref, HashSet<Xref>>();	//Contains the mapping from Ensembl to external database 
		HashMap<Xref, GeneAttributes>  geneSet = new HashMap<Xref, GeneAttributes>();	//Contains the gene attributes of the Ensembl gene id

		BioMartAttributes bio = new BioMartAttributes();
		// Initialize the loading of the default mapping between BioMart and BridgeDb 
		bio.init();		
		String organism = config.getSpecies();
		//Query the BioMart attributes for probes ids.
		QueryBioMart.loadBiomartAttributes(bio,organism,config.getEndpoint());
		
		BioMart2Bdb mart = new BioMart2Bdb(config,bio,dbEntries,geneSet);
		
		List<String> filter = config.filterDatasource(config.getDatasource(),bio);
		
		String chrFilter = config.getChromosome();

		try{
			for (String probe:config.getProbeSet()){
				mart.query(organism,probe,chrFilter,true,true);
			}
		}
		catch (NullPointerException ne){
			for (String probe:config.getProbe()){
				for(BioMartReference ref :bio.getReference(probe)){
					mart.query(organism,ref.getQueryName(),chrFilter,true,true);
				}
			}
		}

		for (String ds:filter){
			mart.query(organism,ds,chrFilter,true,true);
		}

		System.out.println(date);
		mart.bridgedbCreator(dbEntries,geneSet,path);
		System.out.println(date);
	}
	public static void report(boolean qc,String pathOld, String path,SpeciesConfiguration config) throws IDMapperException, SQLException, ClassNotFoundException, IOException, ConverterException{
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
			System.out.println(symbol);
			String patholdDB = "";
			File oldDB = null;
			for (File f : listFiles){
				if (f.getName().startsWith(symbol)){
					patholdDB = f.getAbsolutePath();
					oldDB = f;
				}
			}
			try{
			runQC(oldDB, path, config);
			}
			catch (Exception e){
				
			}
//			String old = pathOld
//					+config.getFileName().toUpperCase().charAt(0)							
//					+config.getFileName().charAt(1)
//					+"_Derby_20130701.bridge";
			String current = path+config.getFileName()+".bridge";
			String report = path+config.getFileName().toUpperCase().charAt(0)
					+config.getFileName().charAt(1)+"_outdatedQC_81";
//			OutdatedIdsReport.run(patholdDB,current,report);
		}
	}
	public static void runQC(File oldDB, String path,SpeciesConfiguration config) throws IDMapperException, SQLException, FileNotFoundException{
//		BridgeQC main = new BridgeQC (new File(pathOld
//				+config.getFileName().toUpperCase().charAt(0)
//				+config.getFileName().charAt(1)
//				+"_Derby_20130701.bridge"),
//				new File(path+config.getFileName()+".bridge"));
		BridgeQC main = new BridgeQC (oldDB,
				new File(path+config.getFileName()+".bridge"));	
		main.run();
		String fileName = path+"report_"+config.getFileName()+".qc";
		PrintWriter pw  = new PrintWriter(new FileOutputStream(fileName));	
		pw.println(main.getOutput());
		pw.close();
	}
}


//		BridgeQC main = new BridgeQC (new File(
//		"/home/bigcat-jonathan/LinkTest/derby_test/v0.4/Hs_Ensembl_r80.bridge"),
//		new File(path+config.getFileName()+".bridge"));	main.run();