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


		String filename = args[0];	// Config file
		String path = args[1];	// Location to create the file .bridge

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

		for (String ds:filter){
			mart.query(organism,ds,true);
		}

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
}
