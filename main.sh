BINARY=$1

find src -name "*.java" > all_src_sources.txt
javac -cp "lib/*" -d bin @all_src_sources.txt
java -cp "bin:lib/*" ${BINARY}