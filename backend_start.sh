#!/bin/bash
trap terminate SIGINT
terminate(){
  echo "Stopping all processes"
    pkill -SIGINT -P $$
    exit
}

cd backend || exit
echo "Building common"
mvn -q -f common/pom.xml clean install -Dmaven.test.skip=true
for dir in ./*/
do
  [ "$dir" != "./common/" ] \
    && echo "Building $dir" \
    && mvn -q -f "$dir/pom.xml" clean compile exec:java -Dmaven.test.skip=true -Dexec.args="$1" &
done
wait
