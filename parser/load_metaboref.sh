#!/bin/bash

if [[ ! -v LOGIN_PASSWORD ]]; then
    echo "LOGIN_PASSWORD is not set"
fi

if [[ ! -v DBA_PASSWORD ]]; then
    echo "LOGIN_PASSWORD is not set"
fi

LOGIN_DBA=${LOGIN_PASSWORD:dba}
PASSWORD=${DBA_PASSWORD:dba}

basedir="/usr/local/virtuoso-opensource/share/virtuoso/vad/dumps/"

function load_metaboref() {

  if [ ! -d "$basedir" ] ; then
    echo "$basedir does not exist."
    mkdir -p "$basedir"
    cp $(find ./parser -name "*.ttl") "$basedir/"
  fi

  # here all database to manage
  foodb
  FlavonoidsCombinatoire
  newdb
}

function foodb() {
  echo " -- FooDB --"
  nameg=$(basename "`ls $basedir/foo*`" ".ttl")

  echo " -- Loading $nameg"
  ISQLV_CMD="ld_dir('/usr/local/virtuoso-opensource/share/virtuoso/vad/dumps/', 'foodb*.ttl', 'http://p2m2.metabohub.org/"$nameg"'); rdf_loader_run();"

  isql-v 1111 ${LOGIN_DBA} "${PASSWORD}" exec="${ISQLV_CMD}"

  echo " -- Foodb Loaded."
}

function FlavonoidsCombinatoire() {
  echo " -- FlavonoidsCombinatoire --"
  nameg="FlavonoidsCombinatoire"

  echo " -- Loading $nameg"
  ISQLV_CMD="ld_dir('/usr/local/virtuoso-opensource/share/virtuoso/vad/dumps/', 'flavonoids_combinatoire.ttl', 'http://p2m2.metabohub.org/"$nameg"'); rdf_loader_run();"

  isql-v 1111 ${LOGIN_DBA} "${PASSWORD}" exec="${ISQLV_CMD}"

  echo " -- FlavonoidsCombinatoire Loaded."
}


function newdb() {
   echo " -- new db --"
}

echo " -- Load_MetaboRef $(date) -- "

load_metaboref
