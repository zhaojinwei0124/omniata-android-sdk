#!/bin/sh

if [ ! -n "$1" ]; then
    echo "Give the version as a parameter"
    exit -1
fi

VERSION=$1


RELEASE_JAR=omniata-android/release/omniata-android-sdk.jar
if [ ! -f "$RELEASE_JAR" ]; then
    echo "Release jar-file missing: $RELEASE_JAR. It should be built by running the 'exportJar' task in gradle"
    exit -1
fi



PAGES_REPOSITY_DIR=/Users/junliu/Desktop/workshop/Omniata.github.io
if [ ! -d "$PAGES_REPOSITY_DIR" ]; then
    echo "No Github pages clone in $PAGES_REPOSITY_DIR. Clone that first with 'git clone git@github.com:Omniata/Omniata.github.io.git'"
    exit -1
fi

# Deploy docs and binary
echo "Copying to Omniata repository"
DIR=`pwd`
PAGES_DIR_RELATIVE=docs/sdks/android/$VERSION
PAGES_DIR=$PAGES_REPOSITY_DIR/$PAGES_DIR_RELATIVE

echo $PAGES_DIR_RELATIVE
echo $PAGES_DIR

rm -rf $PAGES_DIR
mkdir $PAGES_DIR
mkdir $PAGES_DIR/apidoc
cp -r omniata-android/build/docs/javadoc/* $PAGES_DIR/apidoc/
cp $RELEASE_JAR $PAGES_DIR/

echo "Commiting and pushing"
cd $PAGES_REPOSITY_DIR
git pull
git add $PAGES_DIR_RELATIVE
git commit -m "Android SDK ${VERSION}" $PAGES_DIR_RELATIVE
git push -u origin master

echo "Ready version $VERSION"
