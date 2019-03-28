package script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class BioMartAttributes {
	private ArrayList<BioMartReference> array;
	public BioMartAttributes(ArrayList<BioMartReference> array) {
		this.array = array;
	}
	public BioMartAttributes() {
		this.array =  new ArrayList<BioMartReference>();
	}
	public void init(){
		try{
			InputStream is = BioMartAttributes.class.getClassLoader().getResourceAsStream("BioMartSources.tsv");	
			loadAnInputStream(is);
		}
		catch (IOException ex)
		{
			throw new Error(ex);
		}
	}
	protected void loadAnInputStream(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader (
				new InputStreamReader (is));
		String line;
		while ((line = reader.readLine()) != null) {
			String[] fields = line.split ("\\t");
			BioMartReference ref = new BioMartReference(fields[0], fields[1], fields[2]);
			array.add(ref);
		}		
	}
	public ArrayList<BioMartReference> getArray() {
		return array;
	}
	public void setArray(ArrayList<BioMartReference> array) {
		this.array = array;
	}
	public void addReference(BioMartReference ref){
		array.add(ref);
	}
	public ArrayList<BioMartReference> getReference(String name){
		ArrayList<BioMartReference> match = new ArrayList<BioMartReference>();
		for (BioMartReference ref:array){
			if (ref.getGpmlName().equals(name)||ref.getIdName().equals(name)||ref.getQueryName().equals(name)){
				match.add(ref);
			}		
		}		
		return match;
	}	
	@Override
	public String toString() {
		return "BioMartAttributes [array=" + array + "]";
	}
}
