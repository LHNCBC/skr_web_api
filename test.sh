#!/bin/sh

if [ $# -lt 1 ]; then
    echo "usage: $0 [jvmoptions] class [programoptions]"
    exit 0
fi


CP=lib/httpclient-4.1.1.jar:lib/httpclient-cache-4.1.1.jar
CP=$CP:lib/httpcore-4.1.jar:lib/httpcore-nio-4.1.jar:lib/httpmime-4.1.1.jar
CP=$CP:lib/commons-logging-1.1.1.jar
CP=$CP:lib/skrAPI.jar

java -cp ./classes:$CP $*
