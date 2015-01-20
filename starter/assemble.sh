#!/bin/bash

header='<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
        <modelVersion>4.0.0</modelVersion>
        <groupId>org.projectsforge.swap.dist</groupId>
        <artifactId>dichromacysimulation</artifactId>
        <version>0.1</version>
        <name>SWAP dist assembler</name>

        <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-dependency-plugin</artifactId>
        <version>2.8</version>
        <configuration>
          <prependGroupId>true</prependGroupId>
        </configuration>
      </plugin>
    </plugins>
  </build>
  <dependencies>
        '

footer="</dependencies></project>"

checkstatus() {
    if (( $1 == 0 )); then
	echo "done"
    else
	echo "ERROR (see $logfile for details)"
	exit
    fi
}

assemble() {
    project=$(basename $1)        
    
    echo "  * Assembling $project..."
    
    
    
    logfile=temp/logs/$project-$(date +%Y-%m-%d-%H-%M-%S)
    echo -n "" > $logfile

    echo -n "    --> Cleaning old files...    "
    rm -rf temp/assembled/$project 2>&1 >> $logfile
    rm -f temp/$project-dependency-pom.xml 2>&1 >> $logfile
    echo "done"

    echo -n "    --> Generating pom.xml...    "
    echo $header > temp/$project-dependency-pom.xml
    for d in $(<$1); do
	groupId=$(echo $d | cut -d ':' -f1)
	artifactId=$(echo $d | cut -d ':' -f2)
	version=$(echo $d | cut -d ':' -f3)
	echo -e "<dependency>\n  <groupId>$groupId</groupId>\n  <artifactId>$artifactId</artifactId>\n  <version>$version</version>\n</dependency>" >> temp/$project-dependency-pom.xml
    done
    echo $footer >> temp/$project-dependency-pom.xml
    echo "done"

    echo -n "    --> Collecting dependencies...    "
    mvn -U -up -f temp/$project-dependency-pom.xml dependency:copy-dependencies -DoutputDirectory=assembled/$project/lib -DprependGroupId=true  2>&1 >> $logfile
    checkstatus $?
    
    echo -n "    --> Copying starter files...    "
    cp target/swap-starter.jar temp/assembled/$project/  2>&1 >> $logfile
    cp src/misc/start.sh temp/assembled/$project/ 2>&1 >> $logfile
    cp src/misc/start.bat temp/assembled/$project/ 2>&1 >> $logfile
    mkdir -p temp/assembled/$project/i586 temp/assembled/$project/x64 2>&1 >> $logfile
    tar --strip-components 1 -xvzf src/misc/jre-7u25-windows-i586.tar.gz -C temp/assembled/$project/i586 2>&1 >> $logfile
    tar --strip-components 1 -xvzf src/misc/jre-7u25-windows-x64.tar.gz -C temp/assembled/$project/x64 2>&1 >> $logfile
    
    chmod +x temp/assembled/$project/start.sh 2>&1 >> $logfile
    echo "done"
    
    echo -n "    --> Building $project.zip...    "
    oldpwd=$(pwd)
    cd temp/assembled/$project 
    rm -rf ../../zip/$project.zip 2>&1 >> ../../../$logfile
    zip -r ../../zip/$project.zip .  2>&1 >> ../../../$logfile
    cd "$oldpwd"
    checkstatus $?
    
    echo "  => Assembling $project done"
}


init() {
    mkdir -p temp/assembled
    mkdir -p temp/logs
    mkdir -p temp/zip
}

echo -n "Building starter..."
logfile=temp/starter-$(date +%Y-%m-%d-%H-%M-%S)
mvn clean install 2>&1 > $logfile
checkstatus $?

if [[ -z $1 ]]; then
    echo "Cleaning already existing assemblies"
    rm -rf temp

    init

    echo "Processing all assemblies in assemblyconfig :"
    for f in assemblyconfig/*; do
	assemble "$f"
    done
else
    init

    assemble $1
fi



