import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private Symbol current;
    private final LexicalAnalyzer scanner;
    private final List<Integer> rules = new ArrayList<>();

    public Parser(Reader source, LexicalAnalyzer scanner) throws IOException{
        this.scanner= scanner;
        this.current = scanner.nextToken();
    }
    public List<Integer> getRules(){
        return this.rules;
    }
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
    public ParseTree parse(){
        ParseTree Tree = program();
        if(current.getType()!= LexicalUnit.EOS){
            throw new RuntimeException("Text after the end of the program");
        }
        return Tree;
    }
/// [1]  <S> → Main() { <Code> }
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
        }


      ParseTree parseTreeOrigin = new ParseTree(new Symbol(LexicalUnit.PROGRAM,"Program"),chdn);
        return parseTreeOrigin;



    }

    /** [2]  <Code>        → <Instruction> <Code>
     [3]                → ε */

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

        }
        ParseTree codeTree = new ParseTree(new Symbol(null,"Code"),chdn_code);
        return codeTree;
    }
  /**  [4]  <Instruction> → <Assign> ;
[5]                → <Loop>
[6]                → <If>
[7]                → @ [VarName] ;
[8]                → IN [VarName] ; */
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

        }
        ParseTree instruTree = new ParseTree(new Symbol(null,"Instructions"),chdn_instru);
    return instruTree;
    }
///  [9]  <Assign>      → [/VarName] := <ExprArith>
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
        }

        return assignTree;
    }
 ///[10] <ExprArith>   → <Prod> <ExprArith'>
    private ParseTree exprArith(){
        List<ParseTree> chdn_exprArith = new ArrayList<ParseTree>();
        ParseTree exprArithTree = new ParseTree(new Symbol(null,"ExprArith"),chdn_exprArith);

        switch (current.getType()){
            case VARNAME: case MINUS: case NUMBER: case LPAREN:
                rules.add(10);
                chdn_exprArith.add(prod());
                chdn_exprArith.add(exprArithP());
                break;
        }
        return exprArithTree;
    }
    /** [11] <ExprArith'>  → + <Prod> <ExprArith'>
            [12]               → - <Prod> <ExprArith'>
            [13]               → ε */
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
            }
        return exprArithPRIMETree;
    }

    ///[14] <Prod>        → <None> <Prod'>
    ///
    private ParseTree prod(){
        List<ParseTree> chdn_prod = new ArrayList<ParseTree>();
        ParseTree prodTree = new ParseTree(new Symbol(null,"Prod"),chdn_prod);
        switch (current.getType()) {
            case VARNAME: case MINUS: case NUMBER: case LPAREN:
            rules.add(14);
            chdn_prod.add(none());
            chdn_prod.add(prodP());
            break;
        }
        return prodTree;
    }

    /** [15] <Prod'>       → * <None> <Prod'>
     [16]               → / <None> <Prod'>
     [17]               → ε */

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

        }

        return prodPRIMETree;

    }
   /** [18] <None>        → - <None>
    [19]               → <Atom>
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
        }
        return noneTree;
    }

    /** [20] <Atom>        → [VarName]
     [21]               → [Number]
     [22]               → ( <ExprArith> ) */

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
         }
         return atomTree;

    }


    /// [23] <If>          → If ( <Cond> ) { <Code> }

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
        }
        return ifTree;
    }

    ///[24] <Cond>        → <And> <Cond'>
    private ParseTree cond(){
        List<ParseTree> chdn_cond = new ArrayList<ParseTree>();
        ParseTree condTree = new ParseTree(new Symbol(null,"Cond"), chdn_cond);
        switch (current.getType()) {
            case VARNAME: case MINUS: case NUMBER: case LPAREN: case NEGATION: case FALSE: case TRUE: case LSQUARE:
            rules.add(24);
            chdn_cond.add(and());
            chdn_cond.add(condP());
            break;
        }
        return condTree;
        }
    /** [25] <Cond'>       → || <And> <Cond'>
     [26]               → ε */
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

        }
    return condPRIMETree;
    }

    /// [27] <And>         → <Nope> <And'>
    private ParseTree and(){
        List<ParseTree> chdn_and = new ArrayList<ParseTree>();
        ParseTree andTree = new ParseTree(new Symbol(null,"And"),chdn_and);
        switch (current.getType()) {
            case VARNAME: case MINUS: case NUMBER: case LPAREN: case NEGATION: case FALSE: case TRUE: case LSQUARE:
            rules.add(27);
            chdn_and.add(nope());
            chdn_and.add(andP());
            break;
        }
        return andTree;
    }

    /** [28] <And'>        → && <Nope> <And'>
     [29]               → ε */

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
        }

        return andPRIMETree;

    }

    /** [30] <Nope>        → ! <Nope>
     [31]               → <Atom2> */
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
        }
        return nopeTree;
    }

    /** [32] <Atom2>       → FALSE
     [33]               → TRUE
     [34]               → [ <Cond> ]
     [35]               → <ExprArith> <Comp> <ExprArith> */


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
            case VARNAME: case MINUS: case NUMBER: case LPAREN: ///ATOATAIAOTIJAOTI
                rules.add(35);
                chdn_atom2.add(exprArith());
                chdn_atom2.add(comp());
                chdn_atom2.add(exprArith());
                break;

        }

        return atom2Tree;

    }

    /** [36] <Comp>        → ==
     [37]               → <=
     [38]               → < */

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
        }
        return compTree;
    }
    /// [39] <Loop>        → LOOP ( <Cond> ) { <Code>}
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
        }

        return loopTree;
    }

}
