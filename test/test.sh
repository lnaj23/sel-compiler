#!/bin/bash
baseDir="/Users/ggeeraer/Nextcloud/Académique/Cours/Compil/2025-2026/Project/"
subDir="${baseDir}/SubmissionPart1"
solDir="${baseDir}/SolutionPart1"

echo "------------------- $1 -------------------"

oldWd=`pwd`
cd $subDir/$1
read -r jar < <(find ./ -maxdepth 3 -name "*.jar")
read -r mkFile < <(find ./ -maxdepth 3 -name "Makefile")

if [[ "$jar" == "" || "$2" == "compile" ]]
then
    echo "RECOMPILING!"
    mkFileDir=$(dirname "${mkFile}")
    cd "${mkFileDir}"
    make
    cd $subDir/$1
    read -r jar < <(find ./ -maxdepth 3 -name "*.jar")
fi

mkdir -p testsResults
for testFile in "$solDir/"test/*.ycc; do
    b=$(basename $testFile .ycc)
    echo $b
    java -jar "$jar" "$testFile" &> "testsResults/"$b.out
    diff -b "$solDir/test-out/"$b.out "testsResults/"$b.out
    echo ""
done

cd $oldWd


echo "--------END-------- $1 --------END--------"
