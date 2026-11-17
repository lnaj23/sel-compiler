%%

%class LexicalAnalyzer
%unicode
%line
%column
%type Symbol
%function nextToken
%xstate LONGCOMMENT

%eofval{
    return new Symbol(LexicalUnit.EOS, yyline, yycolumn);
%eofval}

VarName = [a-z][a-zA-Z0-9]*
Number  = [0-9]+

%%

"Main"      { return new Symbol(LexicalUnit.MAIN,     yyline, yycolumn, yytext()); }
"If"        { return new Symbol(LexicalUnit.IF,       yyline, yycolumn, yytext()); }
"LOOP"      { return new Symbol(LexicalUnit.LOOP,     yyline, yycolumn, yytext()); }
"IN"        { return new Symbol(LexicalUnit.IN,       yyline, yycolumn, yytext()); }

{VarName}   { return new Symbol(LexicalUnit.VARNAME,  yyline, yycolumn, yytext()); }
{Number}    { return new Symbol(LexicalUnit.NUMBER,   yyline, yycolumn, yytext()); }

";"         { return new Symbol(LexicalUnit.SEMI,     yyline, yycolumn, yytext()); }
":="        { return new Symbol(LexicalUnit.ASSIGN,   yyline, yycolumn, yytext()); }
"("         { return new Symbol(LexicalUnit.LPAREN,   yyline, yycolumn, yytext()); }
")"         { return new Symbol(LexicalUnit.RPAREN,   yyline, yycolumn, yytext()); }
"{"         { return new Symbol(LexicalUnit.LBRACK,   yyline, yycolumn, yytext()); }
"}"         { return new Symbol(LexicalUnit.RBRACK,   yyline, yycolumn, yytext()); }

"-"         { return new Symbol(LexicalUnit.MINUS,    yyline, yycolumn, yytext()); }
"+"         { return new Symbol(LexicalUnit.PLUS,     yyline, yycolumn, yytext()); }
"*"         { return new Symbol(LexicalUnit.TIMES,    yyline, yycolumn, yytext()); }
"/"         { return new Symbol(LexicalUnit.DIVIDE,   yyline, yycolumn, yytext()); }
"@"         { return new Symbol(LexicalUnit.AROBASE,  yyline, yycolumn, yytext()); }

"!"         { return new Symbol(LexicalUnit.NEGATION, yyline, yycolumn, yytext()); }
"&&"        { return new Symbol(LexicalUnit.AND,      yyline, yycolumn, yytext()); }
"||"        { return new Symbol(LexicalUnit.OR,       yyline, yycolumn, yytext()); }

"=="        { return new Symbol(LexicalUnit.EQUAL,    yyline, yycolumn, yytext()); }
"<="        { return new Symbol(LexicalUnit.SMALEQ,   yyline, yycolumn, yytext()); }
"<"         { return new Symbol(LexicalUnit.SMALLER,  yyline, yycolumn, yytext()); }

"##"        { yybegin(LONGCOMMENT); } // There we enter the longcomment mode
"$"[^\r\n]* { }

<LONGCOMMENT> {
    "##"      { yybegin(YYINITIAL); } // If ## is obtained -> switch to intial scanner mode
    [^]       { } // Every other caracters ignore
    <<EOF>>   { throw new RuntimeException("Long comment not closed on line " + yyline); } // If we're on the end-of-file (no ##), then throw error
}

[ \t\r\n]+  { }
.           { throw new RuntimeException("Unknown symbol: " + yytext()); }