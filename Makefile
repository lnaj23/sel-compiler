
default: build

jflex:
	jflex src/LexicalAnalyzer.flex

build: jflex
	javac -d more -cp src/ src/Main.java
	jar cfe dist/part2.jar Main -C bin .
	# javadoc -private src/*.java -d doc/javadoc

testing:
	java -jar dist/part2.jar test/euclid.sel

all: build testing
