# P2M2 References Databases Metabolomics

## Databases

| Database | URL |
| ------ | ----- | 
| Foodb   | https://foodb.ca/ | 
| FlavonoidsCombinatoire | - | 

## Configuration

default value [run.sh](./run.sh#L22)

```bash
COMPOSE_PROJECT_NAME="P2M2_RCA_Databases"
LISTEN_PORT="8089"
PASSWORD="pass123"
GRAPH="http://p2m2.metabohub.org/"
```

## generation 

```
./parser/workflows.sh
./run.sh start
```
