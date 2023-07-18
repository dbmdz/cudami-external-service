# cudami-external-service

REST-Webservice providing the public APIs for [cudami Repository Server](https://github.com/dbmdz/cudami/tree/main/dc-cudami-server)

## Endpoints

### DFG METS/MODS export

Endpoint for providing DigitalObject representation in DFG-METS/MODS XML format.
The METS-export is intended to be used in the [DFG-Viewer](https://dfg-viewer.de/) of the [DFG (Deutsche Forschungsgemeinschaft)](https://www.dfg.de/).
The Format specifications are available at [DFG-Viewer Metadaten](https://dfg-viewer.de/metadaten).

#### Documentation

METS (Metadata Encoding & Transmission Standard) documentation:

* Official Website: <https://www.loc.gov/standards/mets/>
* Tutorials: [METS Primer (PDF)](https://www.loc.gov/standards/mets/METSPrimer.pdf), [METS Overview & Tutorial](https://www.loc.gov/standards/mets/METSOverview.v3_en.html)

MODS (Metadata Object Description Schema) documentation:

* Official Website: <https://www.loc.gov/standards/mods/>
* MODS 3.8 User Guidelines: <https://www.loc.gov/standards/mods/userguide/>

MIX (Metadata For Images in XML):

* Official Website: <https://www.loc.gov/standards/mix/>

Other:

* [MODS, METS, and other metadata standards (PDF)](https://bibliotecas.uaslp.mx/autoridades/sem_internacional/talleres/Taller4/Mexico-3-modsmets.pdf)
* [Using Metadata Standards in Digital Libraries:
Introduction to Implementing METS, MODS, PREMIS and MIX](https://www.loc.gov/standards/mods//presentations/intro-diglibstandards-ala07/) + [Example XML](https://www.loc.gov/standards/premis/louis.xml)

#### Example request

<http://localhost:8080/mets/mods/v1/digitalobjects/4624c822-1d16-4bfa-9d10-3aa45cfab4aa/dfg>

### OAI-PMH service

Endpoint implementing OAI-PMH (Open Archives Initiative - Protocol for Metadata Harvesting) specification <http://www.openarchives.org/OAI/openarchivesprotocol.html> for supporting harvesting and change tracking.

#### Documentation

* Official Website: <http://www.openarchives.org/pmh/>
* Specification OAI-PMH 2.0: <http://www.openarchives.org/OAI/openarchivesprotocol.html>
* Implementation Guidelines for the Open Archives Initiative Protocol for Metadata Harvesting: <http://www.openarchives.org/OAI/2.0/guidelines.htm>
* Specification and XML Schema for the OAI Identifier Format <http://www.openarchives.org/OAI/2.0/guidelines-oai-identifier.htm>

#### Example requests

Every request must specify the wanted action through `verb`. See <http://www.openarchives.org/OAI/openarchivesprotocol.html#ProtocolMessages> for supported verbs.

* Identify: <http://localhost:8080/oai/v2?verb=Identify>
* ListMetadataFormats:

    * List the metadata formats that can be disseminated from the repository: <http://localhost:8080/oai/v2?verb=ListMetadataFormats>
    * List the metadata formats that can be disseminated from the repository for the item with unique identifier 4624c822-1d16-4bfa-9d10-3aa45cfab4aa: <http://localhost:8080/oai/v2?verb=ListMetadataFormats&identifier=4624c822-1d16-4bfa-9d10-3aa45cfab4aa>

* ListIdentifiers: List the headers of records with existing Dublin Core metadata format that are added, modified or deleted since January 1, 2022 in the set newspapers: <http://localhost:8080/oai/v2?verb=ListIdentifiers&from=2022-01-01&metadataPrefix=oai_dc&set=newspapers>
* ListIdentifiers: get list of next page of a previous ListIdentifiers request: <http://localhost:8080/oai/v2?verb=ListIdentifiers&resumptionToken=eyJsa.......wfQ==>
* ListRecords: Request records in the oai_dc metadata format, modified or added between 2:15pm and 2:20pm UTC on May 1st 2002.: <http://localhost:8080/oai/v2?verb=ListRecords&from=2002-05-01T14:15:00Z&until=2002-05-01T14:20:00Z&metadataPrefix=oai_dc>
* GetRecord: Request a record in the Dublin Core metadata format:  <http://localhost:8080/oai/v2?verb=GetRecord&identifier=oai:f611580a-1106-4c74-851f-9a6d12cb662c&metadataPrefix=oai_dc>
* ListSets: List all sets of the repository: <http://localhost:8080/oai/v2?verb=ListSets>
