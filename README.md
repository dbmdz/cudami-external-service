# cudami-external-service

REST-Webservice providing the public APIs for [cudami Repository Server](https://github.com/dbmdz/cudami/tree/main/dc-cudami-server)

## Endpoints

### DFG METS/MODS export

Endpoint for providing DigitalObject representation in DFG-METS/MODS XML format.
The METS-export is intended to be used in the [DFG-Viewer](https://dfg-viewer.de/) of the [DFG (Deutsche Forschungsgemeinschaft)](https://www.dfg.de/).
The Format specifications are available at [DFG-Viewer Metadaten](https://dfg-viewer.de/metadaten).

Example request: "http://localhost:8080/mets/mods/v1/digitalobjects/4624c822-1d16-4bfa-9d10-3aa45cfab4aa/dfg"

### OAI-PMH service

Endpoint implementing OAI-PMH specification <http://www.openarchives.org/OAI/openarchivesprotocol.html>
for supporting harvesting and change tracking.