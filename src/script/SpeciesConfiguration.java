package script;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class SpeciesConfiguration {
	Properties prop;
	InputStream input;
//	static String includeList = "En,Mb,L,X,Il,Ag,Q,Om,U,Rf,S,Ip,T,Pd,H,M,R,D,Z,F,W,Gg,A,Ti,Ir,N,Uc,Pl,Gm,Bg,Ec,Wg,Gw,Kg,Bc,Tb";
	static String includeList = "Ensembl,miRBase Sequence,Entrez Gene,Affy,Illumina,Agilent,RefSeq,OMIM,"
			+ "UniGene,Rfam,Uniprot-TrEMBL,IPI,GeneOntology,PDB,HGNC,MGI,RGD,SGD,ZFIN,FlyBase,WormBase,Gramene Genes DB,"
			+ "TAIR,TIGR,IRGSP Gene,NASC Gene,UCSC Genome Browser,PlantGDB,BioGrid,EcoGene,WikiGenes,Gene Wiki,KEGG Genes,"
			+ "BioCyc,TubercuList,Uniprot-SwissProt";
	public SpeciesConfiguration(String filename){
		prop = new Properties();
//		prop.stringPropertyNames();
		try {
			input = new FileInputStream(filename);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			System.out.println("Sorry, unable to find " + filename);
		}
		//load a properties file from class path, inside static method
		try {
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally{
			if(input!=null){
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}	
	public String getEndpoint(){
		return prop.getProperty("endpoint");
	}
	public String getSchema(){
		return prop.getProperty("schema");
	}
	public String getSpecies(){
		return prop.getProperty("species");
	}
	public String getDBName(){
		return prop.getProperty("database_name");
	}
	public String getFileName(){
		return prop.getProperty("file_name");
	}
	public List<String> getProbe(){
		List<String> probe = Arrays.asList(prop.getProperty("probe_datasource").split(","));
		return probe;
	}
	public List<String> getProbeSet(){
		List<String> probe = Arrays.asList(prop.getProperty("probe_set").split(","));
		return probe;
	}
	public List<String> getDatasource(){
		List<String> probe = Arrays.asList(prop.getProperty("gene_datasource").split(","));
		return probe;
	}
	public String  getChromosome(){
//		List<String> chr = Arrays.asList(prop.getProperty("chromosome_name").split(","));
		return prop.getProperty("chromosome_name");
	}
	public List<String> filterDatasource(List<String> list,BioMartAttributes bio){
		List<String> filter = new ArrayList<String>();
		List<String> arr = Arrays.asList(includeList.split(","));
		for (String ds:list){
			String check="";
			try{
				check = bio.getReference(ds).get(0).getGpmlName();				
			}
			catch(IndexOutOfBoundsException ie){
				System.err.println("Incorrect datasource, not recognize by BridgeDb");	
			}
			if (arr.contains(check)){
				filter.add(ds);
			}
			else{
				System.out.println("Filtered out: "+check);
			}				
		}
		return filter;
	}
}
