BINARY=$1
# javac -cp "ds/algo/java/lib/*.java" ${BINARY}.java
# java -cp "ds/algo/java/lib/*.java" ${BINARY}

find ds -name "*.java" > all_sources.txt
javac -d bin @all_sources.txt
java -cp bin ${BINARY}