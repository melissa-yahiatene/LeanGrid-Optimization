#!/bin/bash

echo "Compilation en cours..."
javac -d out $(find src/main/java -name "*.java")

echo "Exécution de LeanGridApp..."
java -cp out org.leanGrid.LeanGridApp ImportedFiles/instance1.txt 10
