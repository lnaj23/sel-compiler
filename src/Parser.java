import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    private LexicalAnalyzer scanner;  
    private Symbol current;            
    private List<Integer> derivation;  

    
    public Parser(LexicalAnalyzer scanner) {
        // initialise les attributs
        // appelle advance() pour charger le premier token
		this.scanner = scanner;
		this.current = null;
		advance();
    }

	public ParseTree parse() {
		parseS();


		return null;
	}

	private void advance() {
		try {
        current = scanner.nextToken();
    	} catch (IOException e) {
        throw new RuntimeException("Scanner error: " + e.getMessage());
    	}
	}

	private void rule(int n) {
		derivation.add(n);
	}

	private ParseTree match(LexicalUnit expected) {
		if (current.getType() != expected) {
			throw new RuntimeException(
				"Syntax error at line " + current.getLine()
				+ ": expected " + expected
				+ " but got " + current.getType()
			);
		}
		ParseTree leaf = new ParseTree(current);
		advance();
		return leaf;
	}	

    private ParseTree parseS() {
		List<ParseTree> children = new ArrayList<>();
		rule(1);
		children.add(match(LexicalUnit.MAIN));
		children.add(match(LexicalUnit.LPAREN));
		children.add(match(LexicalUnit.RPAREN));
		children.add(match(LexicalUnit.LBRACK));
		children.add(parseCode());
		children.add(match(LexicalUnit.RBRACK));
		return new ParseTree(new Symbol(null, "<S>"), children);

	}
    // private ParseTree parseCode()        { ... }
    // private ParseTree parseInstruction() { ... }
    // private ParseTree parseIf()          { ... }
    // private ParseTree parseLoop()        { ... }
    // private ParseTree parseExprArith()   { ... }
    // private ParseTree parseExprArithP()  { ... }  // pour +/-
    // private ParseTree parseTerm()        { ... }
    // private ParseTree parseTermP()       { ... }  // pour *//
    // private ParseTree parseFactor()      { ... }
    // private ParseTree parseCond()        { ... }
    // private ParseTree parseCondP()       { ... }  // pour ||
    // private ParseTree parseCondTerm()    { ... }
    // private ParseTree parseCondTermP()   { ... }  // pour &&
    // private ParseTree parseCondAtom()    { ... }
    // private ParseTree parseComp()        { ... }
}