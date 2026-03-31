public class Token {
    private TokenType type;
    private String value;
    private int line;
    private int column;

    public Token(TokenType type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public TokenType getType() { return type; }
    public String getValue() { return value; }
    public int getLine() { return line; }
    public int getColumn() { return column; }

    public enum TokenType {
        // === Mots-clés de contrôle ===
        IF, ELIF, ELSE,
        SWITCH, CASE, DEFAULT, BREAK,
        WHILE, DO, FOR, IN,          // ← AJOUTEZ "DO" ICI
        DEF, CLASS, RETURN, CONTINUE, PASS,
        
        // === Opérateurs logiques ===
        AND, OR, NOT,
        
        // === Littéraux ===
        BOOLEAN, INTEGER, FLOAT, STRING, IDENTIFIER,
        
        // === Comparaison ===
        EQUAL, NOT_EQUAL,
        LESS, GREATER, LESS_EQUAL, GREATER_EQUAL,
        
        // === Affectation ===
        ASSIGN, PLUS_ASSIGN, MINUS_ASSIGN,
        
        // === Incrémentation/Décrémentation ===
        INCREMENT, DECREMENT,
        
        // === Arithmétiques ===
        PLUS, MINUS, MULTIPLY, DIVIDE, MODULO,
        
        // === Délimiteurs ===
        LPAREN, RPAREN,
        LBRACE, RBRACE,
        LBRACKET, RBRACKET,
        COMMA, COLON, SEMICOLON, DOT,
        
        // === Spéciaux ===
        NEWLINE, COMMENT, ERROR, EOF,
        
        // === Personnalisés ===
        BENOUADFEL, Yacine, RANGE
    }
}