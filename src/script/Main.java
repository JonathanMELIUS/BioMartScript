package script;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.DataSourceTxt;
import org.bridgedb.tools.qc.BridgeQC;

public class Main {

	public static void main(String[] args) throws ClassNotFoundException, IDMapperException, IOException {
		DataSourceTxt.init(); //Initialize BrideDb data source

		//You can programmatically query Biomart
		Date date = new Date();	
		System.out.println(date);

		HashMap<Xref, HashSet<Xref>>  dbEntries = new HashMap<Xref, HashSet<Xref>>();	
		HashMap<Xref, GeneAttributes>  geneSet = new HashMap<Xref, GeneAttributes>();

		BioMartAttributes bio = new BioMartAttributes();
		bio.init();


		String filename = args[0];		
		String path = args[1];


		//		String filename = "AnophelesGambiae.config";		
		//		String path = "/home/bigcat-jonathan/LinkTest/derby_test/";
		//		String pathOld = "/home/bigcat-jonathan/LinkTest/derby_old/";

		SpeciesConfiguration config = new SpeciesConfiguration(filename);
		BioMart2Bdb mart = new BioMart2Bdb(config,bio,dbEntries,geneSet);
		List<String> filter = config.filterDatasource(config.getDatasource(),bio);

		String organism = config.getSpecies();
		QueryBioMart.martAttributes(bio,organism,config.getEndpoint());

		for (String probe:config.getProbeSet()){
			mart.query(organism,probe,true);
		}

		//		for (String probe:config.getProbe()){
		//			for(BioMartReference ref :bio.getReference(probe)){
		//				mart.query(organism,ref.getQueryName(),true);
		//			}
		//		}

//		for (String ds:filter){
//			mart.query(organism,ds,true);
//		}

		date = new Date();	
		System.out.println(date);

		mart.bridgedbCreator(dbEntries,geneSet,path,config.getFileName());

		date = new Date();	
		System.out.println(date);

		if (args.length>2){
			String pathOld = args[2];		

			BridgeQC main = new BridgeQC (new File(pathOld
					+config.getSpecies().toUpperCase().charAt(0)
					+config.getSpecies().charAt(1)
					+"_Derby_20130701.bridge"),
					new File(path+config.getFileName()+".bridge"));
			try {
				main.run();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


	/*
	public static void main(String[] args) throws ClassNotFoundException, IDMapperException, IOException {
		DataSourceTxt.init(); //Initialize BrideDb data source

		//You can programmatically query Biomart
		Date date = new Date();	
		System.out.println(date);

		bio = new BioMartAttributes();
		bio.init();
		private static BioMartAttributes bio;
		private static HashMap<Xref, HashSet<Xref>>  dbEntries = new HashMap<Xref, HashSet<Xref>>();	
		private static HashMap<Xref, GeneAttributes>  geneSet = new HashMap<Xref, GeneAttributes>();
		private static SpeciesConfiguration config;
		private static String path = "/home/bigcat-jonathan/LinkTest/derby_test/";
		private static String pathOld = "/home/bigcat-jonathan/LinkTest/derby_old/";
		BioMart2Bdb mart = new BioMart2Bdb();

//		String filename = "config.properties";
//		String filename = "BosTaurus.config";
//		String filename = "Canisfamiliaris.config";
//		String filename = "Daniorerio.config";
//		String filename = "Drosophilamelanogaster.config";
//		String filename = "EquusCaballus.config";
//		String filename = "GallusGallus.config";
//		String filename = "PanTroglodytes.config";
//		String filename = "RattusNorvegicus.config";
//		String filename = "SaccharomycesCerevisiae.config";
//		String filename = "SusScrofa.config";
//		String filename = "XenopusTropicalis.config";
//		String filename = "HomoSapiens.config";
//		String filename = "MusMusculus.config";

//		String filename = "ArabidopsisThaliana.config";
//		String filename = "GlycineMax.config";
//		String filename = "HordeumVulgare.config";
//		String filename = "OryzaSativaJaponica.config";
//		String filename = "PopulusTrichocarpa.config";
//		String filename = "SolanumLycopersicum.config";
//		String filename = "VitisVinifera.config";
//		String filename = "ZeaMays.config";		

		String filename = "AnophelesGambiae.config";

//		String filename = "PlasmodiumFalciparum.config";

		config = new SpeciesConfiguration(filename);

		List<String> filter = config.filterDatasource(config.getDatasource(),bio);

		String organism = config.getSpecies();
		QueryBioMart.martAttributes(bio,organism,config.getEndpoint());

		for (String probe:config.getProbe()){
			for(BioMartReference ref :bio.getReference(probe)){
				System.out.println(ref.getQueryName());
				mart.query(organism,ref.getQueryName(),true);
			}
		}

		for (String probe:filter){
			mart.query(organism,probe,true);
		}

		date = new Date();	
		System.out.println(date);

		mart.bridgedbCreator(dbEntries,geneSet,config.getFileName());

		date = new Date();	
		System.out.println(date);

		BridgeQC main = new BridgeQC (new File(pathOld
				+config.getSpecies().toUpperCase().charAt(0)
				+config.getSpecies().charAt(1)
				+"_Derby_20130701.bridge"),
				new File(path+config.getFileName()+".bridge"));
		try {
			main.run();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}*/

}
