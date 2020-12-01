#!/bin/bash

basedir="./dumps"

function db() {
   
  if [ ! -d "$basedir" ] ; then
    echo "$basedir does not exist."
    mkdir -p "$basedir"
    cp $(find ./parser -name "*.ttl") "$basedir/"
  fi

  # here all databse to manage
  foodb
  newdb
}

function foodb() {
   
  nameg=$(pushd $basedir;ls foodb*;popd)
  nameg=$(basename $nameg)

  echo " -- Loading $nameg"
  ISQLV_CMD="ld_dir('/usr/local/virtuoso-opensource/share/virtuoso/vad/dumps/', 'foodb*.ttl', 'http://p2m2.metabohub.org/"$nameg"'); rdf_loader_run();"

  docker exec \
          ${CONTAINER_NAME} \
          isql-v 1111 dba "${PASSWORD}" exec="${ISQLV_CMD}"

  echo " -- Foodb Loaded."
}

function newdb() {
   echo " -- new db --"
}

