# Web API

This Java-based API to the Indexing Initiative Scheduler facility was
created to provide users with the ability to programmatically submit
jobs to the Scheduler Batch and Interactive facilities instead of
using the web-based interface.

Three programs are accessible via the Web API: MetaMap, the NLM
Medical Text Indexer (MTI), and SemRep. The functionality in these
programs includes: automatic indexing of MEDLINE citations,
concept-based query expansion, analysis of complex Metathesaurus
strings, accurate identification of the terminology and relationships
in anatomical documents, and the extraction of chemical binding
relations from biomedical text.

## How do I Compile and Run?

* To compile the source jar file and/or any of the examples using
  Ant - change to the top-level directory and run:

    ant jar

* To compile any of the examples without Ant, change to the examples
  directory first:

    cd examples

Unix:

    javac -cp ../classes:../lib/skrAPI.jar:../lib/commons-logging-1.1.1.jar:../lib/httpclient-cache-4.1.1.jar:../lib/httpcore-nio-4.1.jar:../lib/httpclient-4.1.1.jar:../lib/httpcore-4.1.jar:../lib/httpmime-4.1.1.jar -d ../classes GenericBatch.java

or, you can use the "compile.sh" script provided

     ../compile.sh GenericBatch.java

Windows:

    javac -cp ..\classes;..\lib\skrAPI.jar;..\lib\commons-logging-1.1.1.jar;..\lib\httpclient-cache-4.1.1.jar;..\lib\httpcore-nio-4.1.jar;..\lib\httpclient-4.1.1.jar;..\lib\httpcore-4.1.jar;..\lib\httpmime-4.1.1.jar -d ..\classes GenericBatch.java


## Setting the UTS API Key


One way to set the API key is to use the environmental variable
UTS\_API\_KEY in shell.

    $ export UTS_API_KEY=“1234-5678-9ABC-DEF1”

You can also pass the API Key to the constructors GenericObject(String
apiKey) and GenericObject(int whichInteractive, String apiKey):

    GenericObject genObj = new GenericObject(“1234-5678-9ABC-DEF1”);

You can also set the System property uts.apikey before calling the
GenericObject constructor:

    System.setProperty(“uts.apikey”, “1234-5678-9ABC-DEF1”);
    GenericObject genObj = new GenericObject();
