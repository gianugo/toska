#!/bin/sh
# $Id: build.sh,v 1.4 2005/03/15 23:38:35 nito007 Exp $
# ----- Verify and Set Required Environment Variables -------------------------

if [ "$JAVA_HOME" = "" ] ; then
  echo You must set JAVA_HOME to point at your Java Development Kit installation
  exit 1
fi

chmod u+x ./tools/ant/bin/antRun
chmod u+x ./tools/ant/bin/ant

# ----- Verify and Set Required Environment Variables -------------------------

if [ "$TERM" = "cygwin" ] ; then
  S=';'
else
  S=':'
fi

# ----- Set Up The Runtime Classpath ------------------------------------------

OLD_ANT_HOME=$ANT_HOME
unset ANT_HOME

CP=$CLASSPATH
export CP
unset CLASSPATH

CLASSPATH="`echo ./lib/*.jar | tr ' ' $S`"
export CLASSPATH

echo Using classpath: \"$CLASSPATH\"
$PWD/./tools/ant/bin/ant -emacs -logger org.apache.tools.ant.NoBannerLogger $@ 

unset CLASSPATH

CLASSPATH=$CP
export CLASSPATH
ANT_HOME=OLD_ANT_HOME
export ANT_HOME

# Create now the Binary version
files="toska/lib toska/LICENSE.txt toska/TODO toska/CHANGELOG tosk/README toska/run.bat toska/run.sh"
version=`sed -ne  's/.*<property[[:space:]][[:space:]]*name="version"[[:space:]][[:space:]]*value="\([^"]*\)".*/\1/p' build.xml`
echo "Building toska/dist/toska-$version.tgz"
cd ..
tar cf - $files | gzip -c > toska/dist/toska-$version.tgz
cd toska
