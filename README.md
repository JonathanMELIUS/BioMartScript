# BioMartScript

Introduction
============
A script to create a BrigdeDb database from BioMart queries.

Run
============

java -jar BioMart2BridgeDb.jar arg1 arg2 arg3 arg4

- arg1: location of configuration file

- arg2: location for the new database

- arg3: (optional) directory of the old database - run QC

- arg4: (optional) use inclusive BridgeDb list

Config file
============
* Give the version of Ensembl BioMart to query:

    e.g: http://www.ensembl.org/biomart/, http://oct2014.archive.ensembl.org/biomart/,	http://metazoa.ensembl.org/biomart/

    **endpoint**=http://plants.ensembl.org/biomart/

* MartRegistry can be found there:
    
    http://plants.ensembl.org/biomart/martservice?type=registry

    e.g: protists_mart_27, metazoa_mart_27, default

    **schema**=plants_mart_27

* Code name of the species: http://www.ensembl.org/biomart/martservice?type=datasets&mart=ENSEMBL_MART_ENSEMBL

    **species**=athaliana_eg_gene

* The name of the bridge database

    **database_name**=Arabidopsis thaliana genes and proteins

* The name of the file .bridge created

    **file_name**=At_Derby_Ensembl_Plant_28

* The different data source code name can be found like there:

    http://plants.ensembl.org/biomart/martservice?type=attributes&mart=plants_mart_27&dataset=athaliana_eg_gene

    **probe_set**=affy_ath1_121501
    **gene_datasource**=refseq_mrna,refseq_ncrna,refseq_peptide,uniprot_sptrembl,pdb,tair_locus,go_accession,unigene,entrezgene,wikigene_id,nasc_gene_id,uniprot_swissprot_accession

* Filters
 
    http://may2015.archive.ensembl.org/biomart/martservice?type=filters&dataset=drerio_gene_ensembl
