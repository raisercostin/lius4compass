#!/bin/bash
mvn install:install-file -DgroupId=org.freedesktop.standards -DartifactId=shared-mime-info -Dversion=1.0 -Dpackaging=jar -Dfile=lib\MimeType.jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.jcraft -DartifactId=jorbis -Dversion=0.0.15 -Dpackaging=jar -Dfile=lib\jorbis-0.0.15.jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.jcraft -DartifactId=jogg -Dversion=0.0.7 -Dpackaging=jar -Dfile=lib\jogg-0.0.7.jar -DgeneratePom=true
mvn install:install-file -DgroupId=net.javazoom -DartifactId=vorbisspi -Dversion=1.0 -Dpackaging=jar -Dfile=lib\vorbisspi1.0.jar -DgeneratePom=true
mvn install:install-file -DgroupId=net.javazoom -DartifactId=javalayer -Dversion=0.4 -Dpackaging=jar -Dfile=lib\javalayer-0.4.jar -DgeneratePom=true
mvn install:install-file -DgroupId=net.javazoom -DartifactId=mp3spi -Dversion=1.9.1 -Dpackaging=jar -Dfile=lib\mp3spi1.9.1.jar -DgeneratePom=true
mvn install:install-file -DgroupId=org.tritonus -DartifactId=tritonus-share -Dversion=0.3.6 -Dpackaging=jar -Dfile=lib\tritonus_share.jar  -DgeneratePom=true
mvn install:install-file -DgroupId=org.tritonus -DartifactId=tritonus-jorbis -Dversion=0.3.6 -Dpackaging=jar -Dfile=lib\tritonus_jorbis.jar  -DgeneratePom=true
mvn install:install-file -DgroupId=org.tritonus -DartifactId=tritonus-remaining -Dversion=0.3.6 -Dpackaging=jar -Dfile=lib\tritonus_remaining.jar  -DgeneratePom=true
mvn install:install-file -DgroupId=org.tritonus -DartifactId=tritonus-mp3 -Dversion=0.3.6 -Dpackaging=jar -Dfile=lib\tritonus_mp3.jar  -DgeneratePom=true
