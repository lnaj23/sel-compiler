%%

%class LexicalAnalyzer
%unicode
%line
%column
%type Symbol
%function nextToken

%eofval{
    return new Symbol(LexicalUnit.EOS, yyline, yycolumn);
%eofval}

%%

"Main"      { return new Symbol(LexicalUnit.MAIN, yyline, yycolumn, yytext()); }
[ \t\r\n]+  { }
.           { throw new RuntimeException("Unknown symbol: " + yytext()); }