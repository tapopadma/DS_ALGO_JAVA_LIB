BINARY=$1

find ds -name "*.java" > all_sources.txt
javac -d bin @all_sources.txt
java -cp bin ${BINARY}