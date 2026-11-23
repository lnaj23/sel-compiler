import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Recursive-descent LL(1) parser for the SEL language.
 * <p>
 * Each grammar variable is handled by a private method. The parser records
 * the number of every rule it applies, which gives the leftmost derivation,
 * and builds the corresponding parse tree.
 */
public class Parser {
    /** The current lookahead token. */
    private Symbol current;
    /** The lexical analyzer providing the tokens. */
    private final LexicalAnalyzer scanner;
    /** The numbers of the rules applied, in order (leftmost derivation). */
    private final List<Integer> rules = new ArrayList<>();

    /**
     * Creates a parser and reads the first token.
     *
     * @param source  the input source
     * @param scanner the lexical analyzer reading {@code source}
     * @throws IOException if the first token cannot be read
     */
    public Parser(Reader source, LexicalAnalyzer scanner) throws IOException{
        this.scanner= scanner;
        this.current = scanner.nextToken();
    }

    /**
     * Returns the rules applied during parsing.
     *
     * @return the list of rule numbers forming the leftmost derivation
     */
    public List<Integer> getRules(){
        return this.rules;
    }

    /**
     * Checks that the current token has the expected type and consumes it.
     *
     * @param expected the expected token type
     * @return a leaf of the parse tree containing the consumed token
     * @throws RuntimeException if the current token is not of the expected type
     */
    private ParseTree match(LexicalUnit expected){
        if(current.getType() != expected){
            throw new RuntimeException(expected.toString());
        }
        ParseTree leaf = new ParseTree(current); //  Tree with chld empty = leaf
        try {
            current = scanner.nextToken();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return leaf;
    }

    /**
     * Parses the whole program and checks that nothing follows it.
     *
     * @return the parse tree of the program
     * @throws RuntimeException if a syntax error is found
     */
    public ParseTree parse(){
        ParseTree Tree = program();
        if(current.getType()!= LexicalUnit.EOS){
            throw new RuntimeException("Text after the end of the program");
        }
        return Tree;
    }

    /**
     * <pre>{@code
     * [1]  <Program>     → Main ( ) { <Code> }
     * }</pre>
     * @return the parse tree of Program
     */
    private ParseTree program(){
        List<ParseTree> chdn = new ArrayList<ParseTree>();
        switch (current.getType()) {
            case MAIN:
            rules.add(1);

            chdn.add(match(LexicalUnit.MAIN));
            chdn.add(match(LexicalUnit.LPAREN));
            chdn.add(match(LexicalUnit.RPAREN));
            chdn.add(match(LexicalUnit.LBRACK));

            chdn.add(code());
            chdn.add(match(LexicalUnit.RBRACK));
            
            break;

        default:
    throw new RuntimeException("Syntax error: unexpected " + current.getType());   
        }


      ParseTree parseTreeOrigin = new ParseTree(new Symbol(LexicalUnit.PROGRAM,"Program"),chdn);
        return parseTreeOrigin;



    }

    /**
     * <pre>{@code
     * [2]  <Code>        → <Instruction> <Code>
     * [3]                → ε
     * }</pre>
     * @return the parse tree of Code
     */
    private ParseTree code(){
        List<ParseTree> chdn_code = new ArrayList<ParseTree>();


        switch (current.getType()){
            case AROBASE: case IN: case VARNAME: case IF: case LOOP:
                rules.add(2);
                chdn_code.add(instructions());
                chdn_code.add(code());
                break;
            case RBRACK:
                rules.add(3);
                break;
            default:
                 throw new RuntimeException("Syntax error: unexpected " + current.getType());

        }
        ParseTree codeTree = new ParseTree(new Symbol(null,"Code"),chdn_code);
        return codeTree;
    }

    /**
     * <pre>{@code
     * [4]  <Instruction> → <Assign> ;
     * [5]                → <Loop>
     * [6]                → <If>
     * [7]                → @ [VarName] ;
     * [8]                → IN [VarName] ;
     * }</pre>
     * @return the parse tree of Instruction
     */
    private ParseTree instructions(){
        List<ParseTree> chdn_instru = new ArrayList<ParseTree>();


        switch (current.getType()){
            case VARNAME:
                rules.add(4);
                chdn_instru.add(assign());
                chdn_instru.add(match(LexicalUnit.SEMI));
                break;
            case LOOP:
                rules.add(5);
                chdn_instru.add(loop());
                break;
            case IF:
                rules.add(6);
                chdn_instru.add(si());
                break;
            case AROBASE:
                rules.add(7);
                chdn_instru.add(match(LexicalUnit.AROBASE));
                chdn_instru.add(match(LexicalUnit.VARNAME));
                chdn_instru.add(match(LexicalUnit.SEMI));
                break;
            case IN:
                rules.add(8);
                chdn_instru.add(match(LexicalUnit.IN));
                chdn_instru.add(match(LexicalUnit.VARNAME));
                chdn_instru.add(match(LexicalUnit.SEMI));
                break;
            default:
                throw new RuntimeException("Syntax error: unexpected " + current.getType());

        }
        ParseTree instruTree = new ParseTree(new Symbol(null,"Instructions"),chdn_instru);
    return instruTree;
    }

    /**
     * <pre>{@code
     * [9]  <Assign>      → [VarName] := <ExprArith>
     * }</pre>
     * @return the parse tree of Assign
     */
    private ParseTree assign(){
        List<ParseTree> chdn_assign = new ArrayList<ParseTree>();
        ParseTree assignTree = new ParseTree(new Symbol(null,"Assign"),chdn_assign);
        switch (current.getType()) {
            case VARNAME:
            rules.add(9);
            chdn_assign.add(match(LexicalUnit.VARNAME));
            chdn_assign.add(match(LexicalUnit.ASSIGN));
            chdn_assign.add(exprArith());
            break;
        default:
        throw new RuntimeException("Syntax error: unexpected " + current.getType());    
        }

        return assignTree;
    }

    /**
     * <pre>{@code
     * [10] <ExprArith>   → <Prod> <ExprArith'>
     * }</pre>
     * @return the parse tree of ExprArith
     */
    private ParseTree exprArith(){
        List<ParseTree> chdn_exprArith = new ArrayList<ParseTree>();
        ParseTree exprArithTree = new ParseTree(new Symbol(null,"ExprArith"),chdn_exprArith);

        switch (current.getType()){
            case VARNAME: case MINUS: case NUMBER: case LPAREN:
                rules.add(10);
                chdn_exprArith.add(prod());
                chdn_exprArith.add(exprArithP());
                break;
            default:
                throw new RuntimeException("Syntax error: unexpected " + current.getType());    
        }    
        
        return exprArithTree;
    }

    /**
     * <pre>{@code
     * [11] <ExprArith'>  → + <Prod> <ExprArith'>
     * [12]               → - <Prod> <ExprArith'>
     * [13]               → ε
     * }</pre>
     * @return the parse tree of ExprArith'
     */
    private ParseTree exprArithP(){
        List<ParseTree> chdn_exprArithP = new ArrayList<ParseTree>();
        ParseTree exprArithPRIMETree = new ParseTree(new Symbol(null,"ExprArithPRIME"),chdn_exprArithP);
            switch (current.getType()){
                case PLUS:
                    rules.add(11);
                    chdn_exprArithP.add(match(LexicalUnit.PLUS));
                    chdn_exprArithP.add(prod());
                    chdn_exprArithP.add(exprArithP());
                    break;
                case MINUS:
                    rules.add(12);
                    chdn_exprArithP.add(match(LexicalUnit.MINUS));
                    chdn_exprArithP.add(prod());
                    chdn_exprArithP.add(exprArithP());
                    break;
                case SEMI: case RPAREN: case OR: case AND: case EQUAL: case SMALEQ: case SMALLER: case RSQUARE:
                    rules.add(13);
                    break;
                 default:
                throw new RuntimeException("Syntax error: unexpected " + current.getType());     
            }
        return exprArithPRIMETree;
    }

    /**
     * <pre>{@code
     * [14] <Prod>        → <None> <Prod'>
     * }</pre>
     * @return the parse tree of Prod
     */
    private ParseTree prod(){
        List<ParseTree> chdn_prod = new ArrayList<ParseTree>();
        ParseTree prodTree = new ParseTree(new Symbol(null,"Prod"),chdn_prod);
        switch (current.getType()) {
            case VARNAME: case MINUS: case NUMBER: case LPAREN:
            rules.add(14);
            chdn_prod.add(none());
            chdn_prod.add(prodP());
            break;
            default:
                throw new RuntimeException("Syntax error: unexpected " + current.getType()); 
        }
        return prodTree;
    }

    /**
     * <pre>{@code
     * [15] <Prod'>       → * <None> <Prod'>
     * [16]               → / <None> <Prod'>
     * [17]               → ε
     * }</pre>
     * @return the parse tree of Prod'
     */
    private ParseTree prodP(){
        List<ParseTree> chdn_proP = new ArrayList<ParseTree>();
        ParseTree prodPRIMETree = new ParseTree(new Symbol(null,"ProdPRIME"),chdn_proP);

        switch (current.getType()){
            case TIMES :
                rules.add(15);
                chdn_proP.add(match(LexicalUnit.TIMES));
                chdn_proP.add(none());
                chdn_proP.add(prodP());
                break;
            case DIVIDE:
                rules.add(16);
                chdn_proP.add(match(LexicalUnit.DIVIDE));
                chdn_proP.add(none());
                chdn_proP.add(prodP());
                break;
            case SEMI:case PLUS: case MINUS: case RPAREN: case OR: case SMALEQ: case SMALLER: case EQUAL: case AND: case RSQUARE:
                rules.add(17);
                break;
            default:
                throw new RuntimeException("Syntax error: unexpected " + current.getType()); 
        }

        return prodPRIMETree;

    }

    /**
     * <pre>{@code
     * [18] <None>        → - <None>
     * [19]               → <Atom>
     * }</pre>
     * @return the parse tree of None
     */
    private ParseTree none(){
        List<ParseTree> chdn_none = new ArrayList<ParseTree>();
        ParseTree noneTree = new ParseTree(new Symbol(null,"None"),chdn_none);

        switch (current.getType()){
            case MINUS:
                rules.add(18);
                chdn_none.add(match(LexicalUnit.MINUS));
                chdn_none.add(none());
                break;
            case VARNAME:case NUMBER: case LPAREN:
                rules.add(19);
                chdn_none.add(atom());
                break;
            default:
                throw new RuntimeException("Syntax error: unexpected " + current.getType()); 
        }
        return noneTree;
    }

    /**
     * <pre>{@code
     * [20] <Atom>        → [VarName]
     * [21]               → [Number]
     * [22]               → ( <ExprArith> )
     * }</pre>
     * @return the parse tree of Atom
     */
    private ParseTree atom(){
        List<ParseTree> chdn_atom = new ArrayList<ParseTree>();
        ParseTree atomTree = new ParseTree(new Symbol(null,"Atom"), chdn_atom);
         switch (current.getType()){
             case VARNAME:
                 rules.add(20);
                 chdn_atom.add(match(LexicalUnit.VARNAME));
                 break;
             case NUMBER:
                 rules.add(21);
                 chdn_atom.add(match(LexicalUnit.NUMBER));
                 break;
             case LPAREN:
                 rules.add(22);
                 chdn_atom.add(match(LexicalUnit.LPAREN));
                 chdn_atom.add(exprArith());
                 chdn_atom.add(match(LexicalUnit.RPAREN));
                 break;
            default:
                throw new RuntimeException("Syntax error: unexpected " + current.getType()); 
         }
         return atomTree;

    }

    /**
     * <pre>{@code
     * [23] <If>          → If ( <Cond> ) { <Code> }
     * }</pre>
     * @return the parse tree of If
     */
    private ParseTree si(){
        List<ParseTree> chdn_if = new ArrayList<ParseTree>();
        ParseTree ifTree = new ParseTree(new Symbol(null,"If"), chdn_if);
        switch (current.getType()) {
            case IF:
            rules.add(23);
            chdn_if.add(match(LexicalUnit.IF));
            chdn_if.add(match(LexicalUnit.LPAREN));
            chdn_if.add(cond());
            chdn_if.add(match(LexicalUnit.RPAREN));
            chdn_if.add(match(LexicalUnit.LBRACK));
            chdn_if.add(code());
            chdn_if.add(match(LexicalUnit.RBRACK));
            break;
            default:
                throw new RuntimeException("Syntax error: unexpected " + current.getType()); 
        }
        return ifTree;
    }

    /**
     * <pre>{@code
     * [24] <Cond>        → <And> <Cond'>
     * }</pre>
     * @return the parse tree of Cond
     */
    private ParseTree cond(){
        List<ParseTree> chdn_cond = new ArrayList<ParseTree>();
        ParseTree condTree = new ParseTree(new Symbol(null,"Cond"), chdn_cond);
        switch (current.getType()) {
            case VARNAME: case MINUS: case NUMBER: case LPAREN: case NEGATION: case FALSE: case TRUE: case LSQUARE:
            rules.add(24);
            chdn_cond.add(and());
            chdn_cond.add(condP());
            break;
            default:
                throw new RuntimeException("Syntax error: unexpected " + current.getType()); 
        }
        return condTree;
        }

    /**
     * <pre>{@code
     * [25] <Cond'>       → || <And> <Cond'>
     * [26]               → ε
     * }</pre>
     * @return the parse tree of Cond'
     */
    private ParseTree condP(){
        List<ParseTree> chdn_condP = new ArrayList<ParseTree>();
        ParseTree condPRIMETree = new ParseTree(new Symbol(null,"CondPRIME"), chdn_condP);
        switch (current.getType()){
            case OR :
                rules.add(25);
                chdn_condP.add(match(LexicalUnit.OR));
                chdn_condP.add(and());
                chdn_condP.add(condP());
                break;
            case RPAREN: case RSQUARE:
                rules.add(26);
                break;
            default:
                throw new RuntimeException("Syntax error: unexpected " + current.getType()); 

        }
    return condPRIMETree;
    }

    /**
     * <pre>{@code
     * [27] <And>         → <Nope> <And'>
     * }</pre>
     * @return the parse tree of And
     */
    private ParseTree and(){
        List<ParseTree> chdn_and = new ArrayList<ParseTree>();
        ParseTree andTree = new ParseTree(new Symbol(null,"And"),chdn_and);
        switch (current.getType()) {
            case VARNAME: case MINUS: case NUMBER: case LPAREN: case NEGATION: case FALSE: case TRUE: case LSQUARE:
            rules.add(27);
            chdn_and.add(nope());
            chdn_and.add(andP());
            break;
            default:
                throw new RuntimeException("Syntax error: unexpected " + current.getType()); 
        }
        return andTree;
    }

    /**
     * <pre>{@code
     * [28] <And'>        → && <Nope> <And'>
     * [29]               → ε
     * }</pre>
     * @return the parse tree of And'
     */
    private ParseTree andP(){
        List<ParseTree> chdn_andP = new ArrayList<ParseTree>();
        ParseTree andPRIMETree = new ParseTree(new Symbol(null,"AndPRIME"), chdn_andP);

        switch (current.getType()){
            case AND:
                rules.add(28);
                chdn_andP.add(match(LexicalUnit.AND));
                chdn_andP.add(nope());
                chdn_andP.add(andP());
                break;
            case RPAREN: case OR: case RSQUARE:
                rules.add(29);
                break;

            default:
                throw new RuntimeException("Syntax error: unexpected " + current.getType()); 
        }

        return andPRIMETree;

    }

    /**
     * <pre>{@code
     * [30] <Nope>        → ! <Nope>
     * [31]               → <Atom2>
     * }</pre>
     * @return the parse tree of Nope
     */
    private ParseTree nope(){
        List<ParseTree> chdn_nope = new ArrayList<ParseTree>();
        ParseTree nopeTree = new ParseTree(new Symbol(null,"Nope"), chdn_nope);

        switch (current.getType()){
            case NEGATION:
                rules.add(30);
                chdn_nope.add(match(LexicalUnit.NEGATION));
                chdn_nope.add(nope());
                break;

            case VARNAME: case MINUS: case NUMBER: case LPAREN:case LSQUARE: case FALSE: case TRUE:
                rules.add(31);
                chdn_nope.add(atom2());
                break;
            default:
                throw new RuntimeException("Syntax error: unexpected " + current.getType()); 
        }
        return nopeTree;
    }

    /**
     * <pre>{@code
     * [32] <Atom2>       → FALSE
     * [33]               → TRUE
     * [34]               → [ <Cond> ]
     * [35]               → <ExprArith> <Comp> <ExprArith>
     * }</pre>
     * @return the parse tree of Atom2
     */
    private ParseTree atom2(){
        List<ParseTree> chdn_atom2 = new ArrayList<ParseTree>();
        ParseTree atom2Tree = new ParseTree(new Symbol(null,"Atom2"), chdn_atom2);
        switch (current.getType()){
            case FALSE:
                rules.add(32);
                chdn_atom2.add(match(LexicalUnit.FALSE));
                break;
            case TRUE:
                rules.add(33);
                chdn_atom2.add(match(LexicalUnit.TRUE));
                break;
            case LSQUARE:
                rules.add(34);
                chdn_atom2.add(match(LexicalUnit.LSQUARE));
                chdn_atom2.add(cond());
                chdn_atom2.add(match(LexicalUnit.RSQUARE));
                break;
            case VARNAME: case MINUS: case NUMBER: case LPAREN:
                rules.add(35);
                chdn_atom2.add(exprArith());
                chdn_atom2.add(comp());
                chdn_atom2.add(exprArith());
                break;
            default:
                throw new RuntimeException("Syntax error: unexpected " + current.getType()); 

        }

        return atom2Tree;

    }

    /**
     * <pre>{@code
     * [36] <Comp>        → ==
     * [37]               → <=
     * [38]               → <
     * }</pre>
     * @return the parse tree of Comp
     */
    private ParseTree comp(){
        List<ParseTree> chdn_comp = new ArrayList<ParseTree>();
        ParseTree compTree = new ParseTree(new Symbol(null,"Comp"),chdn_comp);

        switch (current.getType()){
            case EQUAL:
                rules.add(36);
                chdn_comp.add(match(LexicalUnit.EQUAL));
                break;

            case SMALEQ:
                rules.add(37);
                chdn_comp.add(match(LexicalUnit.SMALEQ));
                break;

            case SMALLER:
                rules.add(38);
                chdn_comp.add(match(LexicalUnit.SMALLER));
                break;
            default:
                throw new RuntimeException("Syntax error: unexpected " + current.getType()); 
        }
        return compTree;
    }

    /**
     * <pre>{@code
     * [39] <Loop>        → LOOP ( <Cond> ) { <Code> }
     * }</pre>
     * @return the parse tree of Loop
     */
    private ParseTree loop(){
        List<ParseTree> chdn_loop = new ArrayList<ParseTree>();
        ParseTree loopTree = new ParseTree(new Symbol(null,"Loop"), chdn_loop);
        switch (current.getType()) {
            case LOOP:
            rules.add(39);
            chdn_loop.add(match(LexicalUnit.LOOP));
            chdn_loop.add(match(LexicalUnit.LPAREN));
            chdn_loop.add(cond());
            chdn_loop.add(match(LexicalUnit.RPAREN));
            chdn_loop.add(match(LexicalUnit.LBRACK));
            chdn_loop.add(code());
            chdn_loop.add(match(LexicalUnit.RBRACK));
            break;
            default:
                throw new RuntimeException("Syntax error: unexpected " + current.getType()); 
        }

        return loopTree;
    }

}
