default: build

jflex:
	jflex src/LexicalAnalyzer.flex -d src/

build: jflex
	mkdir -p bin
	javac -d bin -cp src/ src/*.java
	jar cfe dist/part2.jar Main -C more .
	javadoc -private src/*.java -d doc/javadoc

testing:
	java -jar dist/part2.jar test/euclid.sel

testing-wt:
	java -jar dist/part2.jar -wt test/euclid.tex test/euclid.sel

tex-to-pdf:
	pdflatex test/euclid.tex

all: build testing

all-wt: build testing-wt