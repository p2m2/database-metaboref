#!/bin/bash

### ------------------------------------------------------------------------------------------------------
### database : FooDB, FlavonoidsCombinatoire
###
###

if ! command -v ttl &> /dev/null
then
    echo "COMMAND could not be found"
    npm install turtle-validator
fi
echo " -- start -- "

if [ "$#" -ne  "1" ]
then
     echo "args : output turtle file directory"
     exit
fi

ttldir=$(realpath $1)
workdir=$(mktemp -d --tmpdir=$ttldir)

### 1. FOODB
### ------------------------------------------------------------------------------------------------------
if false;then
echo " -- start FooDB : $(date)"

re='href="([a-zA-Z0-9_\/]*json.zip)"'
download_html=$(curl -s https://foodb.ca/downloads)

foodbworkdir=$(mktemp -d --tmpdir=$workdir)

if [[ "$download_html" =~ $re ]]; then
        url=${BASH_REMATCH[1]}
        echo " -- Download foodb $url -- "
	      curl -s https://foodb.ca/$url -o $foodbworkdir/down.zip
        echo " -- unzip -- "
	      unzip $foodbworkdir/down.zip -d $foodbworkdir
        release=""
	      rerelease="([0-9][0-9_]+)_json.zip"
	      if [[ "$url" =~ $rerelease  ]]; then
		      release=${BASH_REMATCH[1]}
	      fi

        pathcompoundjson=$(find $foodbworkdir -name "Compound.json")

        pushd $(dirname $0)
          echo " -- transform $pathcompoundjson -- "
          sbt "foodb/run $pathcompoundjson $ttldir/foodb${release}.ttl"
          ttl $ttldir/foodb${release}.ttl
        popd
else
	echo " -- Can not find json to download at https://foodb.ca/downloads -- "
        exit 1
fi

fi

### 2. FlavonoidsCombinatoire
### ------------------------------------------------------------------------------------------------------
echo " -- start FlavonoidsCombinatoire : $(date)"

pushd $(dirname $0)
sbt "flavonoids/run resources/FlavonoidsCombinatoire-Updatenov2018.xlsx $ttldir/flavonoids_combinatoire.ttl"
ttl $ttldir/flavonoids_combinatoire.ttl
popd

## --------------------------------------------------------------------------------------------------------
## End workflow

## Copy load_metaboref
cp $(dirname $0)/load_metaboref.sh ${ttldir}/

echo " -- remove ${workdir} -- "
rm -rf $workdir
