CP="bin:lib/sootclasses-2.5.0.jar:lib/guava-23.0.jar"
MAIN="ptatoolkit.zipper.doop.Main"

#echo
#echo $*

java -Xmx48g -cp $CP $MAIN $*
