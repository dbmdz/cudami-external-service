# cudami-external-service

REST-Webservice providing the public APIs for [cudami Repository Server](https://github.com/dbmdz/cudami/tree/main/dc-cudami-server)

## Endpoints

### DFG METS/MODS export

Endpoint for providing DigitalObject representation in DFG-METS/MODS XML format.
The METS-export is intended to be used in the [DFG-Viewer](https://dfg-viewer.de/) of the [DFG (Deutsche Forschungsgemeinschaft)](https://www.dfg.de/).
The Format specifications are available at [DFG-Viewer Metadaten](https://dfg-viewer.de/metadaten).

Example request: "/mets/v1/digitalobjects/e774778a-a298-4349-8e47-6c1a00ce95b7"