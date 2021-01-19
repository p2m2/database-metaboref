from openjdk:17-slim-buster
run apt-get update && apt-get install -y curl gnupg2 unzip
run echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list && \
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | apt-key add && \
    apt-get update && apt-get install -y sbt


run curl -sL https://deb.nodesource.com/setup_15.x | bash - && apt-get install -y nodejs
run npm install -g turtle-validator

workdir /work/metaboref
copy . /work/metaboref/

workdir /ttl
CMD ["/work/metaboref/parser/workflows.sh","/ttl"]
