#!/bin/bash

if ! command -v ttl &> /dev/null
then
    echo "COMMAND could not be found"
    npm install turtle-validator
fi

ttldir=$PWD"/dumps"
echo "Create dumps directory"
mkdir -p $ttldir

### 1. FOODB

re='href="([a-zA-Z0-9_\/]*json.zip)"'
download_html=$(curl -s https://foodb.ca/downloads)

if [[ "$download_html" =~ $re ]]; then
        url=${BASH_REMATCH[1]}
        echo " -- Download foodb $url -- "
	      curl -s https://foodb.ca/$url -o down.zip
        echo " -- unzip -- "
	      unzip down.zip
        release=""
	      rerelease="([0-9][0-9_]+)_json.zip"
	      if [[ "$url" =~ $rerelease  ]]; then
		      release=${BASH_REMATCH[1]}
	      fi

        pushd $(dirname $0)
          pathcompoundjson=$(find . -name "Compound.json")
          echo " -- transform $pathcompoundjson -- "
          sbt "foodb/run $pathcompoundjson $ttldir/foodb${release}.ttl"
          ttl $ttldir/foodb${release}.ttl
        popd
else
	echo " -- Can not find json to download at https://foodb.ca/downloads -- "
        exit 1
fi

### 2.



