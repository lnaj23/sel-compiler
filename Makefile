JAVA_FILES := $(wildcard *.java) Lexer.java

all: Main.class

Lexer.java: lexer.flex
	jflex lexer.flex

Main.class: ${JAVA_FILES}
	javac ${JAVA_FILES}
