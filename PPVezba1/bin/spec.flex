
%%

%class MojLekser
%function next_token
%line
%column
%debug
%type Yytoken
%eofval{
    return new Yytoken(sym.EOF, "EOF", yyline, yycolumn);
%eofval}



%{
    private KWTable kwTable = new KWTable();

    private Yytoken getKW()
    {
        int sym_code = kwTable.find(yytext());
        return new Yytoken(sym_code, yytext(), yyline, yycolumn);
    }
%}



%xstate COMMENT

belina = [ \t\n\r]+

slovo = [a-zA-Z_]
cifra = [0-9]
identifikator = {slovo}({slovo}|{cifra})*

heksCifra = [0-9a-fA-F]
oktCifra = [0-7]


%%


\(\*        { yybegin(COMMENT); }      
<COMMENT> \*\) { yybegin(YYINITIAL); }   
<COMMENT> .    { ; }


{belina}    { ; }


{slovo}+ { return getKW(); }
{identifikator} { return new Yytoken(sym.ID, yytext(),yyline, yycolumn ); }


0[xX]{heksCifra}+ | 0{oktCifra}* | [1-9]{cifra}*   { return new Yytoken(sym.CONST, yytext(), yyline, yycolumn); }


\'[^\']\'  { return new Yytoken(sym.CONST, yytext(), yyline, yycolumn); }


\"[^\']*\" { return new Yytoken(sym.CONST, yytext(), yyline, yycolumn); }

 =      { return new Yytoken(sym.ASSIGN, yytext(), yyline, yycolumn); }
\+      { return new Yytoken(sym.PLUS, yytext(), yyline, yycolumn); }
\-      { return new Yytoken(sym.MINUS, yytext(), yyline, yycolumn); }
 ;      { return new Yytoken(sym.SEMICOLON, yytext(), yyline, yycolumn); }
 :      { return new Yytoken(sym.COLON, yytext(), yyline, yycolumn); }
\(      { return new Yytoken(sym.LPAREN, yytext(), yyline, yycolumn); }
\)      { return new Yytoken(sym.RPAREN, yytext(), yyline, yycolumn); }


. { if (yytext() != null && yytext().length() > 0) System.out.println( "ERROR: " + yytext() ); }