# BioMartScript

Introduction
============
A script to create a BrigdeDb database from BioMart queries.

Run
============
In your terminal:

java -jar BioMart2BridgeDb.jar \<configFile\> \<outputPath\> \<oldDB\> \<inclusive\>

- \<configFile\>: location of configuration file

- \<outputPath\>: Path for the new database

- \<oldDB\>: (optional) directory of the old database - run QC

- \<inclusive\>: (optional) use inclusive BridgeDb list

List of default config files:
============

https://github.com/JonathanMELIUS/BioMartScript/tree/bc62ed7fba16dd143292ee10fb4e261d196dd7a6/src/resource

Example: [Danio rerio config file](https://raw.githubusercontent.com/JonathanMELIUS/BioMartScript/bc62ed7fba16dd143292ee10fb4e261d196dd7a6/src/resource/DanioRerio.config)

How to create your own config file
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

* Optional filters (chromosome list)

    e.g: **chromosome_name**=1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,X,MT
 
    http://may2015.archive.ensembl.org/biomart/martservice?type=filters&dataset=btaurus_gene_ensembl
