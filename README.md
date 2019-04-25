# BridgeDb database building for gene databases

Introduction
============
A script to create a gene-focussed BrigdeDb database based on Ensembl BioMART.

Installation
============

Install the jars in the lib/ folder:

```shell
cd lib
mvn install:install-file -Dfile=org.pathvisio.core.jar -DgroupId=org.pathvisio -DartifactId=pathvisio-core -Dversion=3.3.0 -Dpackaging=jar
mvn install:install-file -Dfile=org.wikipathways.webservice.api.lib.jar -DgroupId=org.pathvisio -DartifactId=wikipathways-client -Dversion=3.2.1 -Dpackaging=jar
mvn install:install-file -Dfile=nl.helixsoft.xml.jar -DgroupId=nl.helixsoft -DartifactId=xml -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=reportbots-0.0.1-SNAPSHOT.jar -DgroupId=org.wikipathways -DartifactId=reportbots -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar
mvn install:install-file -Dfile=org.bridgedb.tools.qc-2.4.0-SNAPSHOT.jar -DgroupId=org.bridgedb -DartifactId=org.bridgedb.tools.qc -Dversion=2.4.0-SNAPSHOT -Dpackaging=jar
```

And then compile the code:

```shell
mvn clean assembly:single
cp target/BioMart2BridgeDb-jar-with-dependencies.jar BioMart2BridgeDb.jar
``` 

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

Configuration files can be found in https://github.com/bridgedb/create-bridgedb-genedb-config/tree/master/resource .

Example: [Bos taurus config file](https://raw.githubusercontent.com/bridgedb/create-bridgedb-genedb-config/master/resource/BosTaurus.config)

How to create your own config file
============
* Give the version of Ensembl BioMart to query:

    e.g: http://www.ensembl.org/biomart/, http://oct2014.archive.ensembl.org/biomart/,	http://metazoa.ensembl.org/biomart/

    **endpoint**=http://plants.ensembl.org/biomart/
    
    You can find an overview of releases in the [Ensembl Archive](http://www.ensembl.org/info/website/archives/index.html).

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
