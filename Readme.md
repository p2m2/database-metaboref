# P2M2 References Databases Metabolomics

## Databases

| Database | URL |
| ------ | ----- | 
| Foodb   | https://foodb.ca/ | 
| FlavonoidsCombinatoire | - | 

## generation

```bash
./parser/workflows.sh
```

## rdf load 
```bash
./load_metaboref.sh <tenforce/virtuoso_docker_name>
```

## Docker usage

```bash
docker build . -t inraep2m2/metaboref:0.0.X  
docker push inraep2m2/metaboref:0.0.X
docker run -v $(pwd)/dumps:/ttl/ -t inraep2m2/metaboref:0.0.X 
ls ./dumps
```
