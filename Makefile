default: build

jflex:
	jflex src/LexicalAnalyzer.flex -d src/

build: jflex
	mkdir -p bin
	javac -d bin -cp src/ src/*.java
	jar cfe dist/part2.jar Main -C bin .
	# javadoc -private src/*.java -d doc/javadoc

testing:
	java -jar dist/part2.jar test/euclid.sel

all: build testing