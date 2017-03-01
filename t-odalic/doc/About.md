### Description

This DPU allows to run conversion tasks on a remote Odalic Semantic Table Interpretation server. This requires an existing account and issued authentication token (which may expire in time) and exported prototype task configuration from that server.

The DPU expects a single CSV formatted file as the input and outputs both the annotated extended CSV file and serialized RDF data. 

### Configuration parameters

| Name                   | Description                                                           |
|:-----------------------|:----------------------------------------------------------------------|
|**Host**                | Odalic server URI.                                                    |
|**Authorization token** | Authentication token issued by the server for the user.               |
|**Task configuration**  | Task configuration exported from the server.                          |
|**Input character set** | Name of the character encoding used in the input file.                |
|**Records delimiter**   | CSV file row records delimiting character.                            |
|**Ignore empty lines**  | Boolean flag turning on and off ignoring of empty lines in the input. |
|**Quoting character**   | Character used to quote string containing the delimiter.              |
|**Escaping character**  | Character used to escape other special characters.                    |
|**Comment marker**      | Comment marker.                                                       |

### Inputs and outputs

|Name                 |Type | DataUnit     | Description               | Mandatory |
|:--------------------|:---:|:------------:|:--------------------------|:---------:|
|input                |i    |FilesDataUnit |Input file                 |x          |
|extendedCsvOutput    |o    |FilesDataUnit |Extended CSV file          |           |
|annotatedTableOutput |o    |FilesDataUnit |Annotated table            |           |
|turtleOutput         |o    |FilesDataUnit |Turtle-serialized RDF data |           |
