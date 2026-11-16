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
		this.derivation = new ArrayList<>();
		advance();
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

	private LexicalUnit current() {
    	return current.getType();
	}

	public String getDerivation() {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < derivation.size(); i++) {
			if (i > 0) s.append(' ');
			s.append(derivation.get(i));
		}
		return s.toString();
	}

	private ParseTree match(LexicalUnit expected) {
		if (current() != expected) {
			throw new RuntimeException(
				"Syntax error at line " + current.getLine()
				+ ": expected " + expected
				+ " but got " + current()
			);
		}
		ParseTree leaf = new ParseTree(current);
		advance();
		return leaf;
	}

	public ParseTree parse() {
		ParseTree tree = parseS();
		if (current() != LexicalUnit.EOS) {
			throw new RuntimeException("Syntax error: unexpected token '" + current.getValue() + "'");
		}
		return tree;
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
    
	private ParseTree parseCode() {
		List<ParseTree> children = new ArrayList<>();
		switch (current()) {
        	case VARNAME:
        	case IF:
        	case LOOP:
        	case AROBASE:
        	case IN:
            	rule(2);
            	children.add(parseInstruction());
            	children.add(parseCode());
            	break;
			default:
				rule(3);
				break;
    	}
    
    	return new ParseTree(new Symbol(null, "<Code>"), children);
	}
    private ParseTree parseInstruction() {
		List<ParseTree> children = new ArrayList<>();
		
		switch (current()) {
			case VARNAME:
				rule(4);
				children.add(parseAssign());
				children.add(match(LexicalUnit.SEMI));
				break;
			case IF:
				rule(6);
				children.add(parseIf());
				break;
			case LOOP:
				rule(5);
				children.add(parseLoop());
				break;
			case AROBASE:
				rule(7);
				children.add(match(LexicalUnit.AROBASE));
				children.add(match(LexicalUnit.VARNAME));
				children.add(match(LexicalUnit.SEMI));
				break;
			case IN:
				rule(8);
				children.add(match(LexicalUnit.IN));
				children.add(match(LexicalUnit.VARNAME));
				children.add(match(LexicalUnit.SEMI));
				break;
			default:
				throw new RuntimeException(
					"Syntax error at line " + current.getLine()
					+ ": unexpected token '" + current.getValue() + "'"
				);
		}
    
    	return new ParseTree(new Symbol(null, "<Instruction>"), children);
	}

	private ParseTree parseAssign() {
		List<ParseTree> children = new ArrayList<>();
		rule(9);
		children.add(match(LexicalUnit.VARNAME));
		children.add(match(LexicalUnit.ASSIGN));
		children.add(parseExprArith());
		return new ParseTree(new Symbol(null, "<Assign>"), children);
	}
    
	private ParseTree parseIf() {
		List<ParseTree> children = new ArrayList<>();
		rule(36);
		children.add(match(LexicalUnit.IF));
		children.add(match(LexicalUnit.LPAREN));
		children.add(parseCond());
		children.add(match(LexicalUnit.RPAREN));
		children.add(match(LexicalUnit.LBRACK));
		children.add(parseCode());
		children.add(match(LexicalUnit.RBRACK));
		return new ParseTree(new Symbol(null, "<If>"), children);
	}
    
	private ParseTree parseLoop() {
		List<ParseTree> children = new ArrayList<>();
		rule(37);
		children.add(match(LexicalUnit.LOOP));
		children.add(match(LexicalUnit.LPAREN));
		children.add(parseCond());
		children.add(match(LexicalUnit.RPAREN));
		children.add(match(LexicalUnit.LBRACK));
		children.add(parseCode());
		children.add(match(LexicalUnit.RBRACK));
		return new ParseTree(new Symbol(null, "<Loop>"), children);
	}
    
	private ParseTree parseExprArith() {
		List<ParseTree> children = new ArrayList<>();
		rule(10);
		children.add(parseTerm());
		children.add(parseExprArithP());
		return new ParseTree(new Symbol(null, "<ExprArith>"), children);
	}

	private ParseTree parseTerm() {
		List<ParseTree> children = new ArrayList<>();
		rule(14);
		children.add(parseFactor());
		children.add(parseTermP());
		return new ParseTree(new Symbol(null, "<Term>"), children);
	}

    private ParseTree parseExprArithP() {
		List<ParseTree> children = new ArrayList<>();
		switch (current()) {
			case PLUS:
				rule(11);
				children.add(match(LexicalUnit.PLUS));
				children.add(parseTerm());
				children.add(parseExprArithP());
				break;
			case MINUS:
				rule(12);
				children.add(match(LexicalUnit.MINUS));
				children.add(parseTerm());
				children.add(parseExprArithP());
				break;
			default:
				rule(13);
				break;
		}
		
		return new ParseTree(new Symbol(null, "<ExprArith'>"), children);
	}
    
    private ParseTree parseTermP() {
		List<ParseTree> children = new ArrayList<>();
		switch (current()) {
			case TIMES:
				rule(15);
				children.add(match(LexicalUnit.TIMES));
				children.add(parseFactor());
				children.add(parseTermP());
				break;
			case DIVIDE:
				rule(16);
				children.add(match(LexicalUnit.DIVIDE));
				children.add(parseFactor());
				children.add(parseTermP());
				break;
			default:
				rule(17); // ε
				break;
		}
		
		return new ParseTree(new Symbol(null, "<Term'>"), children);
	}

    private ParseTree parseFactor() {
		List<ParseTree> children = new ArrayList<>();
		
		switch (current()) {
			case VARNAME:
				rule(18);
				children.add(match(LexicalUnit.VARNAME));
				break;
			case NUMBER:
				rule(19);
				children.add(match(LexicalUnit.NUMBER));
				break;
			case LPAREN:
				rule(20);
				children.add(match(LexicalUnit.LPAREN));
				children.add(parseExprArith());
				children.add(match(LexicalUnit.RPAREN));
				break;
			case MINUS:
				rule(21);
				children.add(match(LexicalUnit.MINUS));
				children.add(parseFactor());
				break;
			default:
				throw new RuntimeException(
					"Syntax error at line " + current.getLine()
					+ ": unexpected token '" + current.getValue() + "'"
				);
		}
		
		return new ParseTree(new Symbol(null, "<Factor>"), children);
	}
    
	private ParseTree parseCond() {
		List<ParseTree> children = new ArrayList<>();
		rule(22);
		children.add(parseCondAnd());
		children.add(parseCondP());
		return new ParseTree(new Symbol(null, "<Cond>"), children);
	}

    private ParseTree parseCondP() {
		List<ParseTree> children = new ArrayList<>();
		
		switch (current()) {
			case OR:
				rule(23);
				children.add(match(LexicalUnit.OR));
				children.add(parseCondAnd());
				children.add(parseCondP());
				break;
			default:
				rule(24); // ε
				break;
		}
		
		return new ParseTree(new Symbol(null, "<Cond'>"), children);
	}

	private ParseTree parseCondAnd() {
		List<ParseTree> children = new ArrayList<>();
		rule(25);
		children.add(parseCondAtom());
		children.add(parseCondAndP());
		return new ParseTree(new Symbol(null, "<CondAnd>"), children);
	}

	private ParseTree parseCondAndP() {
		List<ParseTree> children = new ArrayList<>();
		
		switch (current()) {
			case AND:
				rule(26);
				children.add(match(LexicalUnit.AND));
				children.add(parseCondAtom());
				children.add(parseCondAndP());
				break;
			default:
				rule(27); // ε
				break;
		}
		
		return new ParseTree(new Symbol(null, "<CondAnd'>"), children);
	}

	private ParseTree parseCondAtom() {
		List<ParseTree> children = new ArrayList<>();
		
		switch (current()) {
			case FALSE:
				rule(28);
				children.add(match(LexicalUnit.FALSE));
				break;
			case TRUE:
				rule(29);
				children.add(match(LexicalUnit.TRUE));
				break;
			case LSQUARE:
				rule(30);
				children.add(match(LexicalUnit.LSQUARE));
				children.add(parseCond());
				children.add(match(LexicalUnit.RSQUARE));
				break;
			case NEGATION:
				rule(31);
				children.add(match(LexicalUnit.NEGATION));
				children.add(parseCondAtom());
				break;
			case VARNAME:
			case NUMBER:
			case MINUS:
			case LPAREN:
				rule(32);
				children.add(parseExprArith());
				children.add(parseComp());
				children.add(parseExprArith());
				break;
			default:
				throw new RuntimeException(
					"Syntax error at line " + current.getLine()
					+ ": unexpected token '" + current.getValue() + "'"
				);
		}
		
		return new ParseTree(new Symbol(null, "<CondAtom>"), children);
	}

	private ParseTree parseComp() {
		List<ParseTree> children = new ArrayList<>();
		
		switch (current()) {
			case EQUAL:
				rule(33);
				children.add(match(LexicalUnit.EQUAL));
				break;
			case SMALEQ:
				rule(34);
				children.add(match(LexicalUnit.SMALEQ));
				break;
			case SMALLER:
				rule(35);
				children.add(match(LexicalUnit.SMALLER));
				break;
			default:
				throw new RuntimeException(
					"Syntax error at line " + current.getLine()
					+ ": expected comparison operator but got '" + current.getValue() + "'"
				);
		}
		
		return new ParseTree(new Symbol(null, "<Comp>"), children);
	}
}