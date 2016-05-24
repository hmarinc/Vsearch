# Vsearch
This repository contains the source code of VSearch, a vertical search tool to crawl the Deep Web by using a unified Web Query Interface (WQI).

Before executing VSearch it is needed to install the following components:

1. JVM 1.7 or higher
2. Jericho parser. Make sure that the .class files are correctly reached from the CLASSPATH variable. Use the SRC files of Jericho available from this distribution of VSearch as several files in the original distribution were modified. 
3. The Java API Berico-Similarity. This library is used to determine string similarity. 
4. JWNL library. It is used to connect to WORDNET
5. WORDNET. Make sure that the file “FILE_PROPERTIES.XML” correctly addresses to the dictionaries. For example <PARAM NAME="DICTIONARY_PATH" VALUE="C:\PROGRAM FILES\WORDNET\2.1\DICT"/>
6. The file “FILE_PROPERTIES.XML” must be in the same directory of the VSearch package. 
7. The domain dictionary for the domain(s) of interest (.dic file in the same directory of the VSearch package)

The main file to launch the VSearch tool is com.integratedGI.MainGUI.

Use the next command to execute VSearch:

java -classpath classpath=.;C:\JAVA_LIBS\cobra-0.98.4\src;C:\JAVA_LIBS\jwnl14-rc2\jwnl14-rc2\jwnl.jar;C:\JAVA_LIBS\js-1.7R3.jar\js-1.7R3.jar;C:\JAVA_LIBS\berico;C:\JAVA_LIBS\commons-io-2.2.jar;C:\JAVA_LIBS\commons-logging-1.1.3.jar;C:\JAVA_LIBS\jericho-html-3.1\src\java; com.integratedGI.MainGUI 

Be sure that all the required libraries (7) are downloaded. Use the correct path for each of them in the previous command.

When the program launches, it request the .xml file containing the URLs where the WQIs will be detected, classified and integrated to produce a single unified WQI for a domain of interest. This path of that file is in the "settings.txt" file. These distribution provides  4 .xml files for datasets containing 50, 100, 200, and 350 URLs.
In the same folder of the com folder must be the "setting.txt" file, but the dataset can be in any other location.
All the results, render and pruned tree as well as the unified WQI are stored in a folder named "Books_XML_FILE_NAME", where XML_FILE_NAME is the name of the .xml file being used. This distribution provides the results of two tests in folders Books_Database50.xml and Books_Database50. 


Main software modules:
 
1. MainGUI: Displays the graphical user interface of VSearch

2. TopicalWQI: Module in charge of discovery and detection of WQIs related to a given domain. 

3. ClassifyWQIs: Uses domain-dictionaries to classify WQIs belonging to a given domain. 

4. IntegrationMain: Constructs the unified WQI. Modules invoked: PrunedTree (constructs the hierarchical models of web forms detected), Preprocessing (normalization of labels in the web forms detected), Clustering (groups similar labels across the different web forms detected), Mapping (homogenization of schemes), and IntegrationTree (matrix representation of VR-Trees and computation of the precedence vector to construct a unified VR-Tree that represents the unified WQI). 
