package script;

public class SnpAttributes {
	
	private String chromosome;
	private String symbol;
	private String chrom_start;
	private String chrom_end;
	private String chrom_strand;
	private String allele;
	private String minor_allele;
	private String minor_allele_freq;
	private String minor_allele_count;
	private String validated;


	public SnpAttributes(String chromosome, String symbol, String chrom_start,
			String chrom_end, String chrom_strand,
			String minor_allele, String minor_allele_freq,
			String minor_allele_count, String validated, String allele) {
		super();
		this.chromosome = chromosome;
		this.symbol = symbol;
		this.chrom_start = chrom_start;
		this.chrom_end = chrom_end;
		this.chrom_strand = chrom_strand;
		this.allele = allele;
		this.minor_allele = minor_allele;
		this.minor_allele_freq = minor_allele_freq;
		this.minor_allele_count = minor_allele_count;
		this.validated = validated;
	}
	public String getChromosme() {
		return chromosome;
	}
	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SnpAttributes other = (SnpAttributes) obj;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		return true;
	}
	public String getChrom_start() {
		return chrom_start;
	}
	public void setChrom_start(String chrom_start) {
		this.chrom_start = chrom_start;
	}
	public String getChrom_end() {
		return chrom_end;
	}
	public void setChrom_end(String chrom_end) {
		this.chrom_end = chrom_end;
	}
	public String getChrom_strand() {
		return chrom_strand;
	}
	public void setChrom_strand(String chrom_strand) {
		this.chrom_strand = chrom_strand;
	}
	public String getAllele() {
		return allele;
	}
	public void setAllele(String allele) {
		this.allele = allele;
	}
	public String getMinor_allele() {
		return minor_allele;
	}
	public void setMinor_allele(String minor_allele) {
		this.minor_allele = minor_allele;
	}
	public String getMinor_allele_freq() {
		return minor_allele_freq;
	}
	public void setMinor_allele_freq(String minor_allele_freq) {
		this.minor_allele_freq = minor_allele_freq;
	}
	public String getMinor_allele_count() {
		return minor_allele_count;
	}
	public void setMinor_allele_count(String minor_allele_count) {
		this.minor_allele_count = minor_allele_count;
	}
	public String getValidated() {
		return validated;
	}
	public void setValidated(String validated) {
		this.validated = validated;
	}
	public String getChromosome() {
		return chromosome;
	}
}
