#!/usr/bin/env bash
pwd
cd src
export LD_LIBRARY_PATH=/opt/libjpeg-turbo/lib32:$LD_LIBRARY_PATH
javac -cp ../lib/freenect-1.0.jar:../lib/turbojpeg.jar:/usr/share/java/jna.jar sem/group5/*.java
java -cp ../lib/freenect-1.0.jar:../lib/turbojpeg.jar:/usr/share/java/jna.jar:. sem.group5.Main
