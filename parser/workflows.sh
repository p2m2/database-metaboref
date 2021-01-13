#!/bin/bash

if ! command -v ttl &> /dev/null
then
    echo "COMMAND could not be found"
    npm install turtle-validator
fi

ttldir=$PWD"/dumps"

echo "Create dumps directory"
mkdir -p $ttldir

workdir=$(mktemp -d)

### 1. FOODB

re='href="([a-zA-Z0-9_\/]*json.zip)"'
download_html=$(curl -s https://foodb.ca/downloads)

if [[ "$download_html" =~ $re ]]; then
        url=${BASH_REMATCH[1]}
        echo " -- Download foodb $url -- "
	      curl -s https://foodb.ca/$url -o down.zip
        echo " -- unzip -- "
	      unzip down.zip -d $workdir
        release=""
	      rerelease="([0-9][0-9_]+)_json.zip"
	      if [[ "$url" =~ $rerelease  ]]; then
		      release=${BASH_REMATCH[1]}
	      fi

        pushd $(dirname $0)
          pathcompoundjson=$(find $workdir -name "Compound.json")
          echo " -- transform $pathcompoundjson -- "
          sbt "foodb/run $pathcompoundjson $ttldir/foodb${release}.ttl"
          ttl $ttldir/foodb${release}.ttl
        popd
else
	echo " -- Can not find json to download at https://foodb.ca/downloads -- "
        exit 1
fi

### 2. FlavonoidsCombinatoire
pushd $(dirname $0)
sbt "flavonoids/run resources/FlavonoidsCombinatoire-Updatenov2018.xlsx $ttldir/flavonoids_combinatoire.ttl"
ttl $ttldir/flavonoids_combinatoire.ttl
popd


