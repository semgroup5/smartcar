#!/usr/bin/env bash
pwd
cd src
javac -cp ../lib/freenect-1.0.jar sem/group5/Main.java
java -cp ../lib/freenect-1.0.jar:/usr/share/java/jna.jar:. sem.group5.Main
