trap terminate SIGINT
terminate(){
    pkill -SIGINT -P $$
    exit
}

cd backend
mvn -f common/pom.xml clean install -Dmaven.test.skip=true
for dir in ./*/
do
  [dir != "./common/"] && mvn -f $dir/pom.xml clean compile exec:java -Dmaven.test.skip=true &
fi
wait
