FROM ubuntu:xenial
LABEL multi.org.label-schema.name = "OpenIoE" \
      multi.org.label-schema.description = "OpenIoE is an Open-source middleware platform for building, managing, and integrating connected products with the Internet of Everything." \
      multi.org.label-schema.url="https://github.com/scorelab/OpenIoE/blob/master/README.md" \
      multi.org.label-schema.vcs-url = "https://github.com/scorelab/OpenIoE" \
      multi.org.label-schema.vcs-ref = "279FA63" \
      multi.org.label-schema.vendor = "Sustainable Computing Research Group" \
      multi.org.label-schema.version = "9-december-2017" \
      multi.org.label-schema.schema-version = "1.0"
RUN \
  # configure the "jhipster" user
  groupadd jhipster && \
  useradd jhipster -s /bin/bash -m -g jhipster -G sudo && \
  echo 'jhipster:jhipster' |chpasswd && \
  mkdir /home/jhipster/app && \

  # install open-jdk 8
  apt-get update && \
  apt-get install -y openjdk-8-jdk && \

  # install utilities
  apt-get install -y \
     wget \
     curl \
     vim \
     git \
     zip \
     bzip2 \
     fontconfig \
     python \
     g++ \
     build-essential && \

  # install node.js
  curl -sL https://deb.nodesource.com/setup_5.x | bash && \
  apt-get install -y nodejs && \

  # upgrade npm
  npm install -g npm && \

  # install yeoman bower gulp
  npm install -g \
    yo \
    bower \
    gulp-cli && \

  # cleanup
  apt-get clean && \
  rm -rf \
    /var/lib/apt/lists/* \
    /tmp/* \
    /var/tmp/*

RUN echo '{ "allow_root": true }' > /root/.bowerrc
