#!/bin/bash

set -e

while getopts p: option
do
    case "${option}" in
  p)
		p_port=${OPTARG}
		;;
    esac
done

echo "Start Build"
mvn clean install


echo "Running"
if [ -z "$p_port" ]
then
  java -jar target/revolut_test-1.0-SNAPSHOT-jar-with-dependencies.jar
else
  java -jar target/revolut_test-1.0-SNAPSHOT-jar-with-dependencies.jar "$p_port"
fi