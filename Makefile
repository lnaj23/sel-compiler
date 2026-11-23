FILE ?= test/err7.sel

.PHONY: all lexer run tree doc clean

all:
	mkdir -p bin dist
	javac -d bin src/*.java
	jar cfe dist/part2.jar Main -C bin .

lexer:
	jflex -d src src/LexicalAnalyzer.flex

run:
	java -jar dist/part2.jar $(FILE)

tree:
	java -jar dist/part2.jar -wt arbre.tex $(FILE)

doc:
	javadoc -private -d doc src/*.java

clean:
	rm -rf bin dist/part2.jar