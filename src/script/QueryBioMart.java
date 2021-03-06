package script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

public class QueryBioMart {	
	private static Logger logger = Logger.getLogger("Bdb_creation");
	
	public static String docToString(Document xml) {
		LSSerializer ls =  ((DOMImplementationLS) xml.getImplementation()).createLSSerializer();
		LSOutput lsOut = ((DOMImplementationLS) xml.getImplementation()).createLSOutput();
		lsOut.setEncoding("UTF-8");
		StringWriter stringWriter = new StringWriter();
		lsOut.setCharacterStream(stringWriter);			
		ls.write(xml, lsOut);
		return stringWriter.toString();
	}
	
	public static InputStream getDataStream(Document xml, String endpoint) {
		try {
			String encodedXml = docToString(xml);
			encodedXml = URLEncoder.encode(encodedXml, "UTF-8"); // encode to url
			URL url = new URL(endpoint+"martservice/result?query=" + encodedXml);

//			System.out.println("Biomart query URL: " + url.toString());
//			logger.info("Biomart query URL: " + url.toString());
			HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
			urlc.setDoOutput(true);
			int code = urlc.getResponseCode();
			if(code != 200) {
				System.out.println("HTTP Response code: " + urlc.getResponseCode());
			}
			return urlc.getInputStream();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getStringFromInputStream(InputStream is) {
		int count = 0;
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String line;

		try {
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
				count++;		
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		sb.deleteCharAt(sb.length()-1);
		if(count == 1){
			return("Invalid");
		} else {
			return sb.toString();
		}
	}
	public static Document createQuery(SpeciesConfiguration config, String externalSource, 
			Boolean attributes,Boolean chromosome_Filter) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbf.newDocumentBuilder();

			// create doc
			Document query = docBuilder.newDocument();
			DOMImplementation domImpl = query.getImplementation();
			
			/* specify the parameters to use */
			DocumentType doctype = domImpl.createDocumentType("Query", "", "");
			query.appendChild(doctype);
			Element root = query.createElement("Query");
			root.setAttribute("client", "true");
			root.setAttribute("formatter", "TSV");
			root.setAttribute("virtualSchemaName",config.getSchema());
			root.setAttribute("header", "1");
			root.setAttribute("uniqueRows", "1" );
			root.setAttribute("count", "" );
			root.setAttribute("datasetConfigVersion", "0.6" );
			query.appendChild(root);

			/* specify the dataset to use */
			Element dataset = query.createElement("Dataset");
			dataset.setAttribute("name", config.getSpecies());
			root.appendChild(dataset);
			
			if (chromosome_Filter){
				Element chr = query.createElement("Filter");
				chr.setAttribute("name", "chromosome_name");
				chr.setAttribute("value", config.getChromosome());
				dataset.appendChild(chr);
			}
			
			/* add attributes specified in app */
			Element gene_id = query.createElement("Attribute");
			gene_id.setAttribute("name", "ensembl_gene_id");
			dataset.appendChild(gene_id);

			gene_id = query.createElement("Attribute");
			gene_id.setAttribute("name",externalSource);
			dataset.appendChild(gene_id);
			
			if (attributes){
			gene_id = query.createElement("Attribute");
			gene_id.setAttribute("name","description");
			dataset.appendChild(gene_id);
			gene_id = query.createElement("Attribute");
			gene_id.setAttribute("name","chromosome_name");
			dataset.appendChild(gene_id);			
			gene_id = query.createElement("Attribute");	
			// Difference between the main Ensembl or the others(Plants...)
			if (config.getSchema().equals("default"))
				gene_id.setAttribute("name","external_gene_name");
			else
				gene_id.setAttribute("name","external_gene_id");
			dataset.appendChild(gene_id);
			gene_id = query.createElement("Attribute");
			gene_id.setAttribute("name","gene_biotype");
			dataset.appendChild(gene_id);
			}
			return query;

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null; 
	}
	public static void loadBiomartAttributes(BioMartAttributes bio, SpeciesConfiguration config ){
		
		String biomart = config.getEndpoint()+"martservice?type=attributes&dataset="+config.getSpecies();
	
		String extProbe = config.getSpecies()+"__eFG";

		InputStream is = null;

		try {
			URL url = new URL(biomart );

			System.out.println("Biomart query URL: " + url.toString());

			HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
			urlc.setDoOutput(true);
			int code = urlc.getResponseCode();
			if(code != 200) {
				System.out.println("HTTP Response code: " + urlc.getResponseCode());
			}
			is = urlc.getInputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		BufferedReader br = null;
		String line;
		String[] split = null;
		try {
			br = new BufferedReader(new InputStreamReader(is));
			line = br.readLine();
			split = line.split("\t");
			split[1].toString();
			while ((line = br.readLine()) != null) {
				split = line.split("\t");				
				if ( line.contains(extProbe) || line.contains("efg")  ){
					if (split[1].contains("Affy")){
						BioMartReference ref = new BioMartReference(split[0], split[1], "Affy");
						bio.addReference(ref);
					}
					if (split[1].contains("Agilent")){
						BioMartReference ref = new BioMartReference(split[0], split[1], "Agilent");
						bio.addReference(ref);
					}
					if (split[1].contains("Illumina")){
						BioMartReference ref = new BioMartReference(split[0], split[1], "Illumina");
						bio.addReference(ref);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (ArrayIndexOutOfBoundsException ae){
			logger.info("Incorrect dataset name:  "+ split[0]+"\n"+
					"Please check the dataset name here: "+
					config.getEndpoint()+"martservice?type=datasets&mart="+config.getSchema()
					);
		}
	}
}
