// Creating BridgeDb Ensembl Gene Databases
// Copyright 2012-2021 BiGCaT Bioinformatics
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
package org.bridgedb.creator;

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
		return "BioMartReference [queryName=" + queryName + ", idName=" + idName + ", gpmlName=" + gpmlName + "]";
	}

}
