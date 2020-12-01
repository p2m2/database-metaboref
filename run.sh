#!/bin/bash

source $(dirname $0)/db.sh

#set -o xtrace
# CMD           what to do (start | stop | clean)
# LISTEN_PORT   Port to listen
# ---- ENDPOINT      type of endpoint
# ---- SSL           enable SSL (YES | NO) - unused
# PASSWORD      admin password
# GRAPH         default graph name (iri)
# ALLOW_UDPATE  allow sparql update (YES | NO)

if [ "$#" -ne  "1" ]
then
     echo "what to do (start | stop | clean)"
     exit
fi

CMD=$1
COMPOSE_PROJECT_NAME="compareDB"
LISTEN_PORT="8089"
PASSWORD="pass123"
GRAPH="http://p2m2#"
ALLOW_UDPATE="true"

function waitStarted() {
    set +e
    RES=1
    echo -n "Wait for start "
    until [ $RES -eq 0 ]; do
        echo -n "."
        sleep 1
        docker logs ${CONTAINER_NAME} 2>&1 | grep "Server online at 1111" > /dev/null
        RES=$?
    done
    echo ""
    set -e
}

function virtuosoControler() {
    echo " -- Virtuoso controler"
    echo " --"

    echo " -- Generating docker-compose"
    COMPOSE_FILE=docker-compose-${LISTEN_PORT}.yml
    COMPOSE_CMD="docker-compose -p ${COMPOSE_PROJECT_NAME} -f ${COMPOSE_FILE}"
    CONTAINER_NAME="${COMPOSE_PROJECT_NAME}_virtuoso_${LISTEN_PORT}"
    NETWORK_NAME="virtuoso_${LISTEN_PORT}_net"
    OUT_NETWORK_NAME="${COMPOSE_PROJECT_NAME}_${NETWORK_NAME}"
    DATA=$(realpath ${CONTAINER_NAME}_data)
    cat << EOF | tee ${COMPOSE_FILE}
version: '3.7'
services:
    virtuoso:
         image: tenforce/virtuoso
         container_name: ${CONTAINER_NAME}
         environment:
              VIRT_Parameters_NumberOfBuffers: 340000   # See virtuoso/README.md to adapt value of this line
              VIRT_Parameters_MaxDirtyBuffers: 250000    # See virtuoso/README.md to adapt value of this line
              VIRT_Parameters_TN_MAX_memory: 4000000000
              VIRT_SPARQL_ResultSetMaxRows: 100000000
              VIRT_SPARQL_MaxDataSourceSize: 1000000000
              VIRT_Flags_TN_MAX_memory: 4000000000
              VIRT_Parameters_StopCompilerWhenXOverRunTime: 1
              VIRT_SPARQL_MaxQueryCostEstimationTime: 0       # query time estimation
              VIRT_SPARQL_MaxQueryExecutionTime: 300          # 5 min
              VIRT_Parameters_MaxMemPoolSize: 800000000
              VIRT_Database_LockFile: virtuoso.lck
              DBA_PASSWORD: "${PASSWORD}"
              SPARQL_UPDATE: "${ALLOW_UDPATE}    "
              DEFAULT_GRAPH: "${GRAPH}"
         volumes:
            - ${DATA}:/data
            - ./dumps:/usr/local/virtuoso-opensource/share/virtuoso/vad/dumps
         ports:
            - ${LISTEN_PORT}:8890
         networks:
            - ${NETWORK_NAME}

networks:
    ${NETWORK_NAME}:
EOF

    case $CMD in
        start)
            if [ -d ${DATA} ]; then
                echo " -- Already generated."
                echo " -- Start Container"
                ${COMPOSE_CMD} up -d
                waitStarted
            else
                echo "---------------------------------"
                echo "$(ls)"

                echo " -- Pull Images"
                ${COMPOSE_CMD} pull
                echo " -- Start Container"
                ${COMPOSE_CMD} up -d

                waitStarted
                echo " -- Container started."
                
		db
            fi
        ;;
        stop)
            ${COMPOSE_CMD} stop
        ;;
        clean)
            if [ -d ${DATA} ]; then
                ${COMPOSE_CMD} down
                set +e
                docker run --rm \
                    --mount type=bind,source="${DATA}",target=/data \
                    tenforce/virtuoso \
                    bash -c "rm -rf /tmp/data /usr/local/virtuoso-opensource/share/virtuoso/vad/dumps/"
                set -e
                rm -rf "${DATA}"
            else
                echo " -- Instance not present. Skipping cleaning."
            fi
        ;;
        *)
            rm ${COMPOSE_FILE}
            echo "Error: unsupported command $CMD"
            exit 1
        ;;
    esac
    rm ${COMPOSE_FILE}
    exit 0
}

virtuosoControler
