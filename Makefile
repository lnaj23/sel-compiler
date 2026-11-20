FILE ?= test/Euclid.sel

all:
	jflex -d src src/LexicalAnalyzer.flex
	javac -d bin src/*.java
	jar cfe dist/part1.jar Main -C bin .

run:
	java -jar dist/part1.jar $(FILE)

tree:
	java -jar dist/part1.jar -wt arbre.tex $(FILE)


doc:
	javadoc -private -d doc/ src/*.java

clean:
	rm -rf bin dist/part1.jar src/LexicalAnalyzer.java