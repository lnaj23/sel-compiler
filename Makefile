JFLEX = jflex
FILE ?= test/err2.sel

all: jar

generate:
	$(JFLEX) -d src src/LexicalAnalyzer.flex

compile: generate
	mkdir -p bin
	javac -d bin src/*.java

jar: compile
	mkdir -p dist
	jar cfe dist/part1.jar Main -C bin .

doc:
	mkdir -p doc
	javadoc -d doc src/*.java

clean:
	rm -rf bin dist/part1.jar src/LexicalAnalyzer.java

run: jar
	java -jar dist/part1.jar $(FILE)

check: jar
	java -jar dist/part1.jar test/Euclid.sel > mon.out
	diff mon.out test/Euclid.output