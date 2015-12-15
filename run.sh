#!/bin/sh

if [ -z $JAVA_HOME ] 
then
  echo "Please set your JAVA_HOME environment variable"
  exit 1
fi

$JAVA_HOME/jre/bin/java -jar lib/toska.jar
