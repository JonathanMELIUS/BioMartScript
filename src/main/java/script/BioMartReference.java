package script;

public class BioMartReference {	
	private String queryName;
	private String idName;
	private String gpmlName;	
	public BioMartReference(String queryName, String idName, String gpmlName) {
		this.queryName = queryName;
		this.idName = idName;
		this.gpmlName = gpmlName;	
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	public String getIdName() {
		return idName;
	}

	public void setIdName(String idName) {
		this.idName = idName;
	}

	public String getGpmlName() {
		return gpmlName;
	}

	public void setGpmlName(String gpmlName) {
		this.gpmlName = gpmlName;
	}

	@Override
	public String toString() {
		return "BioMartReference [queryName=" + queryName + ", idName="
				+ idName + ", gpmlName=" + gpmlName + "]";
	}

}
