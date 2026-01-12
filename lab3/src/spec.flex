import java_cup.runtime.Symbol;

%%

%class MPLexer
%cup
%line
%column

%{
    // Pomocna funkcija za kreiranje Simbola
    private Symbol newSymbol(int type) {
        return new Symbol(type, yyline+1, yycolumn);
    }
    
    private Symbol newSymbol(int type, Object value) {
        return new Symbol(type, yyline+1, yycolumn, value);
    }
%}

%xstate COMMENT

// Makroi
belina = [ \t\n\r]+
slovo = [a-zA-Z_]
cifra = [0-9]
identifikator = {slovo}({slovo}|{cifra})*

// Konstante prema tekstu zadatka
heksCifra = [0-9a-fA-F]
oktCifra = [0-7]

// 1. Integer konstante (Hex, Oct, Dec)
intConst = 0[xX]{heksCifra}+ | 0{oktCifra}* | [1-9]{cifra}*

// 2. Char konstante ('c')
charConst = \'[^\']\'

// 3. String konstante (''text'') - Paznja: dva apostrofa
stringConst = \'\'[^\']*\'\'

%%

/* --- KOMENTARI --- */
"(*"            { yybegin(COMMENT); }
<COMMENT> "*)"  { yybegin(YYINITIAL); }
<COMMENT> .|\n  { ; }

/* --- BELINE --- */
{belina}        { ; }

/* --- KLJUCNE RECI --- */
"program"   { return newSymbol(sym.PROGRAM); }
"return"    { return newSymbol(sym.RETURN); }
"begin"     { return newSymbol(sym.BEGIN); }
"end"       { return newSymbol(sym.END); }
"integer"   { return newSymbol(sym.INTEGER); }
"char"      { return newSymbol(sym.CHAR); }
"string"    { return newSymbol(sym.STRING); }
"file"      { return newSymbol(sym.FILE); }
"open"      { return newSymbol(sym.OPEN); }
"read"      { return newSymbol(sym.READ); }
"in"        { return newSymbol(sym.IN); }
"do"        { return newSymbol(sym.DO); }

/* --- OPERATORI I SEPARATORI --- */
"+"         { return newSymbol(sym.PLUS); }
"-"         { return newSymbol(sym.MINUS); }
"="         { return newSymbol(sym.ASSIGN); }
":"         { return newSymbol(sym.COLON); }
";"         { return newSymbol(sym.SEMI); }
"("         { return newSymbol(sym.LPAREN); }
")"         { return newSymbol(sym.RPAREN); }

/* --- VREDNOSTI (Tokeni sa atributima) --- */
/* U zadatku pise da su svi oni CONST, ali ih parsiramo istim tokenom CONST i saljemo vrednost */

{intConst}    { return newSymbol(sym.CONST, new Integer(yytext())); } // Ili parsirati hex/oct
{charConst}   { return newSymbol(sym.CONST, yytext()); }
{stringConst} { return newSymbol(sym.CONST, yytext()); }

{identifikator} { return newSymbol(sym.ID, yytext()); }

/* --- GRESKE --- */
. { System.err.println("Leksicka greska (" + yytext() + ") na liniji " + (yyline+1) + ", kolona " + yycolumn); }