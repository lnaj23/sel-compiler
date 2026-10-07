JFLEX = jflex

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