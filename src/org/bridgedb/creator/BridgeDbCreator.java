package org.bridgedb.creator;
// BridgeDbCreator,
// a small wizard for easily creating BridgeDb Derby databases
// Copyright 2012 BiGCaT Bioinformatics
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.bridgedb.bio.BioDataSource;

/**
 * The main class of BridgeDbCreator, that creates a {@link CreatorWizard}. 
 * Information on the input and output files are stored in this class.
 * @author Stefan
 */
public class BridgeDbCreator {

	private File inputFile;
	private String outputFilePath;
	private List<DataSource> datasources;
	
	private Map<Xref, HashSet<Xref>> dbEntries;

	private String dbVersion;
	private String dbSeries;
	private String dbSourceName;
	private String dbDataType;

	// Constructor
	public BridgeDbCreator(Map<Xref, HashSet<Xref>> dbEntries) {
		datasources = new ArrayList<DataSource>();
		this.dbEntries = dbEntries;		
	}

	public File getInputFile() {
		return inputFile;
	}
	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}
	public String getOutputFilePath() {
		return outputFilePath;
	}
	public void setOutputFilePath(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}
	public List<DataSource> getDataSources() {
		return datasources;
	}
	public String getDbVersion() {
		return dbVersion;
	}
	public void setDbVersion(String dbVersion) {
		this.dbVersion = dbVersion;
	}
	public String getDbSeries() {
		return dbSeries;
	}
	public void setDbSeries(String dbSeries) {
		this.dbSeries = dbSeries;
	}
	public String getDbSourceName() {
		return dbSourceName;
	}
	public void setDbSourceName(String dbSourceName) {
		this.dbSourceName = dbSourceName;
	}
	public String getDbDataType() {
		return dbDataType;
	}
	public void setDbDataType(String dbDataType) {
		this.dbDataType = dbDataType;
	}
	public Map<Xref, HashSet<Xref>> getDbEntries() {
		return dbEntries;
	}
	public void setDbEntries(Map<Xref, HashSet<Xref>> dbEntries) {
		this.dbEntries = dbEntries;
	}
}
