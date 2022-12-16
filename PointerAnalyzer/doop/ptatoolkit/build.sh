if [ ! -d "build" ]; then
 mkdir build
fi

CP="build:lib/sootclasses-2.5.0.jar:lib/guava-23.0.jar"

javac -Xlint:unchecked -classpath $CP $(find src -name "*.java") -d build
