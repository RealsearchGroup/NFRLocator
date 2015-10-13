NFRLocator
==========

Project to find and categorize non-functional requirements within unconstrained natural language documents.  Contains NFR category listing, labeled documents, and software

Source Code:
NFRLocator contains an Eclipse which uses Maven do the builds.  Java 1.8 is currently used.

The project does require a WordNetDictionary to be available - you can download the dictionary at http://wordnetcode.princeton.edu/wn3.1.dict.tar.gz
(https://wordnet.princeton.edu/wordnet/download/current-version/)

To start the program in Eclipse, you'll need to use the following run configuration:  (you'll need to change "C:/NLP/WordNetDictionary" as appropriate.
Program Arguments: -w "C:/NLP/WordNetDictionary" -l
VM Arguments: -Xmx4096m


To complete
- summary document
- need copyright status added for the various documents