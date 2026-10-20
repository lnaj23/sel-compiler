FILE ?= test/Euclid.sel

all:
	jflex -d src src/LexicalAnalyzer.flex
	javac -d bin src/*.java
	jar cfe dist/part1.jar Main -C bin .

run:
	java -jar dist/part1.jar $(FILE)

clean:
	rm -rf bin dist/part1.jar src/LexicalAnalyzer.java