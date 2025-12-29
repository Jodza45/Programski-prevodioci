import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

public class SyntaxAnalyzer {

    // Reference na lexer i stek
    private MojLekser lexer;
    private Stack<Integer> stack;

    // Token za kraj fajla (pretpostavka da je sym.EOF definisan)
    private static final int EOF = sym.EOF;

    // --- DEFINICIJA NETERMINALA ---
    // Dodeljujemo im vrednosti vece od najveceg ID-a u sym.java
    private static final int READ_EXPRESSION      = 30;
    private static final int STATEMENT_LIST       = 31;
    private static final int STATEMENT_LIST_PRIME = 32;
    private static final int STATEMENT            = 33;
    private static final int ASSIGN_RHS           = 34;

    // --- MATRICA PRODUKCIJA (Pravila gramatike) ---
    private static final int[][] productions = {
            {}, // 0: epsilon (ne koristi se direktno, ali je dobro imati)

            // 1. ReadExpression -> read ( ID in ID ) do StatementList
            { sym.READ, sym.LPAREN, sym.ID, sym.IN, sym.ID, sym.RPAREN, sym.DO, STATEMENT_LIST },

            // 2. StatementList -> Statement StatementList'
            { STATEMENT, STATEMENT_LIST_PRIME },

            // 3. StatementList' -> ; Statement StatementList'
            { sym.SEMICOLON, STATEMENT, STATEMENT_LIST_PRIME },

            // 4. StatementList' -> epsilon
            { },

            // 5. Statement -> ID = AssignRHS
            { sym.ID, sym.ASSIGN, ASSIGN_RHS },

            // 6. AssignRHS -> CONST
            { sym.CONST },

            // 7. AssignRHS -> ID
            { sym.ID }
    };

    // --- LL(1) TABELA PARSIRANJA ---
    // [Neterminal][Terminal]
    private int[][] parsingTable;

    public SyntaxAnalyzer(MojLekser lexer) {
        this.lexer = lexer;
        this.stack = new Stack<>();
        initParsingTable();
    }

    /**
     * Inicijalizacija LL(1) tabele parsiranja.
     */
    private void initParsingTable() {
        // Imamo 5 neterminala (indeksi 0-4), 30 kolona je dovoljno za terminale
        parsingTable = new int[5][30];

        // --- Popunjavanje tabele na osnovu FIRST i FOLLOW skupova ---

        // Red: READ_EXPRESSION (30 -> index 0)
        // FIRST(ReadExpression) = { read }
        parsingTable[getIndex(READ_EXPRESSION)][sym.READ] = 1;

        // Red: STATEMENT_LIST (31 -> index 1)
        // FIRST(StatementList) = { ID }
        parsingTable[getIndex(STATEMENT_LIST)][sym.ID] = 2;

        // Red: STATEMENT_LIST_PRIME (32 -> index 2)
        // FIRST(StatementList') = { ; } -> Pravilo 3
        parsingTable[getIndex(STATEMENT_LIST_PRIME)][sym.SEMICOLON] = 3;
        // FOLLOW(StatementList') = { EOF } -> Epsilon smena (Pravilo 4)
        parsingTable[getIndex(STATEMENT_LIST_PRIME)][sym.EOF] = 4;

        // Red: STATEMENT (33 -> index 3)
        // FIRST(Statement) = { ID }
        parsingTable[getIndex(STATEMENT)][sym.ID] = 5;

        // Red: ASSIGN_RHS (34 -> index 4)
        // FIRST(AssignRHS) = { CONST, ID }
        parsingTable[getIndex(ASSIGN_RHS)][sym.CONST] = 6;
        parsingTable[getIndex(ASSIGN_RHS)][sym.ID] = 7;
    }

    /**
     * Mapira ID neterminala u indeks niza (0-4).
     */
    private int getIndex(int nonTerminal) {
        return nonTerminal - 30;
    }

    /**
     * Glavna metoda za parsiranje.
     */
    public void parse() {
        try {
            stack.push(EOF);
            stack.push(READ_EXPRESSION); // Startni simbol je ReadExpression

            Yytoken currentToken = lexer.next_token();
            System.out.println("Pocetak sintaksne analize...");

            while (!stack.isEmpty()) {
                int top = stack.peek();
                int input = currentToken.m_index;

                // Slucaj A: Terminal na vrhu steka (ili EOF)
                if (top < 30) {
                    if (top == input) {
                        stack.pop();
                        if (top == EOF) {
                            System.out.println("\n=== SINTAKSNA ANALIZA USPESNA! ===");
                            return;
                        }
                        currentToken = lexer.next_token();
                    } else {
                        error("Neocekivan token! Ocekivan: " + decode(top) + ", Dobijen: " + decode(input) +
                              " (" + currentToken.m_text + ") na liniji " + currentToken.m_line);
                        return;
                    }
                }
                // Slucaj B: Neterminal na vrhu steka
                else {
                    // Provera da li je ulazni token validan indeks za tabelu
                    if (input >= 30 || input < 0) { // Zastita ako token ID nije validan
                        error("Nepoznat ili neispravan token ID: " + input);
                        return;
                    }

                    int rule = parsingTable[getIndex(top)][input];

                    if (rule == 0) {
                        error("Neocekivan token: " + decode(input) + " (" + currentToken.m_text + ")" +
                              " dok se ocekivao pocetak za: " + decode(top) +
                              " na liniji " + currentToken.m_line);
                        return;
                    }

                    stack.pop();
                    pushRuleInverse(rule);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pushRuleInverse(int ruleNumber) {
        System.out.println("Primenjuje se pravilo " + ruleNumber);
        int[] rightSide = productions[ruleNumber];
        for (int i = rightSide.length - 1; i >= 0; i--) {
            stack.push(rightSide[i]);
        }
    }

    private void error(String message) {
        System.err.println("GRESKA: " + message);
    }

    
    // Pomocna funkcija za ispisivanje simbola
    private String decode(int symbol) {
        // Potrebno je imati sym.java sa svim definisanim terminalima
        switch(symbol) {
            // Neterminali
            case READ_EXPRESSION: return "ReadExpression";
            case STATEMENT_LIST: return "StatementList";
            case STATEMENT_LIST_PRIME: return "StatementList'";
            case STATEMENT: return "Statement";
            case ASSIGN_RHS: return "AssignRHS";
            // Terminali
            case sym.ID: return "ID";
            case sym.CONST: return "CONST";
            case sym.READ: return "read";
            case sym.IN: return "in";
            case sym.DO: return "do";
            case sym.ASSIGN: return "=";
            case sym.SEMICOLON: return ";";
            case sym.LPAREN: return "(";
            case sym.RPAREN: return ")";
            case sym.EOF: return "EOF";
            default: return "Nepoznat_Simbol(" + symbol + ")";
        }
    }

    public static void main(String[] args) {
        try {
            String fileName = "src/testinput.txt"; // Ime tvog test fajla
            if (args.length > 0) fileName = args[0];
            
            java.io.Reader reader = new FileReader(fileName);
            // Ime tvog lexer-a je MojLekser
            MojLekser lexer = new MojLekser(reader); 
            SyntaxAnalyzer parser = new SyntaxAnalyzer(lexer);
            
            parser.parse();
            
        } catch (Exception e) {
            System.err.println("Greska pri pokretanju parsera: " + e.getMessage());
            e.printStackTrace();
        }
    }
}