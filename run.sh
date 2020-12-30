#!/bin/sh

if [ $# -lt 1 ]; then
    echo "usage: $0 [jvmoptions] class [programoptions]"
    exit 0
fi

BASEDIR=$(dirname $0)

CP=$BASEDIR/lib/httpclient-4.5.13.jar:$BASEDIR/lib/httpclient-cache-4.5.13.jar
CP=$CP:$BASEDIR/lib/httpcore-4.4.13.jar:$BASEDIR/lib/httpmime-4.5.13.jar
CP=$CP:$BASEDIR/lib/commons-logging-1.2.jar
CP=$CP:$BASEDIR/lib/skrAPI.jar

java -cp $BASEDIR/classes:$CP $*

exit 0
