# Indexing Initiative Web API

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

See [Indexing Initiative's Web API page]
(https://ii.nlm.nih.gov/Web_API/index.shtml) for more information.


## Prerequisites:

* To access either the the Interactive Mode or Batch Mode
  facilities, you must have accepted the terms of the [UMLS
  Metathesaurus License Agreement]
  (https://uts.nlm.nih.gov/license.html), which requires you
  to respect the copyrights of the constituent vocabularies
  and to file a brief annual report on your use of the
  UMLS. You also must have activated a [UMLS Terminology
  Services (UTS) account] (https://uts.nlm.nih.gov/home.html).
  See [UTS Account Information page]
  (http://skr.nlm.nih.gov/Help/umlsks.shtml) for information
  on how we use UTS authentication.

* A running version of Java. Information Mark Symbol: Help
  about Java We have tested and confirmed that the Web API
  program compiles and runs under the following versions of
  Java: 1.6.0

* We have tested and confirmed that the Web API program runs
  on the following operating systems: Linux, Mac OS/X, Microsoft
  Windows XP.

* Apache's Jakarta Project ANT program is required if you wish
  to modify and/or compile the Web API source code using the
  existing build file: Use ANT 1.6 or better
  (available from: http://ant.apache.org).

* Your data in a recognized format. Please see the Supported
  File Formats section of the SKR Help Information web page
  for detailed information and examples of valid input
  formats.
  
## Third Party and associated licenses

The http*.jar files are from the Apache HttpComponents(tm) project
(http://hc.apache.org/) which is under th Apache License, Version 2.0
(http://www.apache.org/licenses/).

The Json libraries are from JSON.org (http://json.org/) which uses the
JSON License (http://www.json.org/license.html)

The Password Masking in the Java Programming Language software is from
http://java.sun.com/developer/technicalArticles/Security/pwordmask/. Author:
Qusay H. Mahmoud with contributions from Alan Sommerer

Information on the CAS RESTful API Protocol used by this API can be
found at: https://wiki.jasig.org/display/CASUM/RESTful+API .
  

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

Preliminary support for Maven has been provided.  However, the
examples are not compiled when using the project file (pom.xml) in its
current configuration.

The following command will build the Web API without the examples:

    mvn package
	
The classes and jar file will be in the directory "target".


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
