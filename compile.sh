#!/bin/sh

BASEDIR=$(dirname $0)

CP=$BASEDIR/lib/httpclient-4.5.13.jar:$BASEDIR/lib/httpclient-cache-4.5.13.jar
CP=$CP:$BASEDIR/lib/httpcore-4.4.13.jar:$BASEDIR/lib/httpmime-4.5.13.jar
CP=$CP:$BASEDIR/lib/commons-logging-1.2.jar
CP=$CP:$BASEDIR/lib/skrAPI.jar

javac -cp $BASEDIR/classes:$CP -d $BASEDIR/classes $*

exit 0
