import java.util.*;

public class Lexer {
    private String input;
    private int position;
    private int line;
    private int column;
    private List<String> errors;

    private static final Map<String, Token.TokenType> KEYWORDS = new HashMap<>();

    static {
        KEYWORDS.put("switch", Token.TokenType.SWITCH);
        KEYWORDS.put("case", Token.TokenType.CASE);
        KEYWORDS.put("default", Token.TokenType.DEFAULT);
        KEYWORDS.put("break", Token.TokenType.BREAK);
        KEYWORDS.put("if", Token.TokenType.IF);
        KEYWORDS.put("elif", Token.TokenType.ELIF);
        KEYWORDS.put("else", Token.TokenType.ELSE);
        KEYWORDS.put("while", Token.TokenType.WHILE);
        KEYWORDS.put("do", Token.TokenType.DO);
        KEYWORDS.put("for", Token.TokenType.FOR);
        KEYWORDS.put("in", Token.TokenType.IN);
        KEYWORDS.put("range", Token.TokenType.RANGE);
        KEYWORDS.put("def", Token.TokenType.DEF);
        KEYWORDS.put("class", Token.TokenType.CLASS);
        KEYWORDS.put("return", Token.TokenType.RETURN);
        KEYWORDS.put("continue", Token.TokenType.CONTINUE);
        KEYWORDS.put("pass", Token.TokenType.PASS);
        KEYWORDS.put("and", Token.TokenType.AND);
        KEYWORDS.put("or", Token.TokenType.OR);
        KEYWORDS.put("not", Token.TokenType.NOT);
        KEYWORDS.put("True", Token.TokenType.BOOLEAN);
        KEYWORDS.put("False", Token.TokenType.BOOLEAN);
        KEYWORDS.put("BENOUADFEL", Token.TokenType.BENOUADFEL);
        KEYWORDS.put("Yacine", Token.TokenType.Yacine);
    }

    public Lexer(String input) {
        this.input = input;
        this.position = 0;
        this.line = 1;
        this.column = 1;
        this.errors = new ArrayList<>();
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (position < input.length()) {
            Token token = nextToken();
            if (token != null) {
                if (token.getType() == Token.TokenType.ERROR) {
                    errors.add(String.format(
                        "Erreur lexicale ligne %d, colonne %d: Caractère invalide '%s'",
                        token.getLine(), token.getColumn(), token.getValue()));
                }
                if (token.getType() != Token.TokenType.COMMENT) {
                    tokens.add(token);
                }
            }
        }
        tokens.add(new Token(Token.TokenType.EOF, "", line, column));
        return tokens;
    }

    private Token nextToken() {
        skipWhitespace();
        if (position >= input.length()) return null;
        
        char current = input.charAt(position);
        int startLine = line;
        int startColumn = column;

        if (current == '#') return scanComment();
        if (Character.isLetter(current) || current == '_') return scanIdentifier();
        if (Character.isDigit(current)) return scanNumber();
        if (current == '"' || current == '\'') return scanString(current);
        return scanOperator(startLine, startColumn);
    }

    private Token scanIdentifier() {
        int startLine = line;
        int startColumn = column;
        StringBuilder sb = new StringBuilder();
        while (position < input.length() &&
               (Character.isLetterOrDigit(input.charAt(position)) || input.charAt(position) == '_')) {
            sb.append(input.charAt(position));
            position++;
            column++;
        }
        String value = sb.toString();
        Token.TokenType type = KEYWORDS.getOrDefault(value, Token.TokenType.IDENTIFIER);
        return new Token(type, value, startLine, startColumn);
    }

    private Token scanNumber() {
        int startLine = line;
        int startColumn = column;
        StringBuilder sb = new StringBuilder();
        boolean isFloat = false;
        while (position < input.length()) {
            char c = input.charAt(position);
            if (Character.isDigit(c)) {
                sb.append(c);
                position++;
                column++;
            } else if (c == '.' && !isFloat) {
                isFloat = true;
                sb.append(c);
                position++;
                column++;
            } else {
                break;
            }
        }
        Token.TokenType type = isFloat ? Token.TokenType.FLOAT : Token.TokenType.INTEGER;
        return new Token(type, sb.toString(), startLine, startColumn);
    }

    private Token scanString(char quote) {
        int startLine = line;
        int startColumn = column;
        StringBuilder sb = new StringBuilder();
        position++; column++;
        while (position < input.length() && input.charAt(position) != quote) {
            if (input.charAt(position) == '\\' && position + 1 < input.length()) {
                position++; column++;
                sb.append(input.charAt(position));
            } else {
                sb.append(input.charAt(position));
            }
            position++;
            column++;
        }
        if (position < input.length()) {
            position++; column++;
        } else {
            errors.add(String.format(
                "Erreur lexicale ligne %d, colonne %d: Chaîne non terminée",
                startLine, startColumn));
        }
        return new Token(Token.TokenType.STRING, sb.toString(), startLine, startColumn);
    }

    private Token scanOperator(int startLine, int startColumn) {
        char current = input.charAt(position);
        switch (current) {
            // === PLUS et dérivés ===
            case '+':
                if (peek() == '+') {
                    position += 2; column += 2;
                    return new Token(Token.TokenType.INCREMENT, "++", startLine, startColumn);
                } else if (peek() == '=') {
                    position += 2; column += 2;
                    return new Token(Token.TokenType.PLUS_ASSIGN, "+=", startLine, startColumn);
                } else {
                    position++; column++;
                    return new Token(Token.TokenType.PLUS, "+", startLine, startColumn);
                }

            // === MINUS et dérivés ===
            case '-':
                if (peek() == '-') {
                    position += 2; column += 2;
                    return new Token(Token.TokenType.DECREMENT, "--", startLine, startColumn);
                } else if (peek() == '=') {
                    position += 2; column += 2;
                    return new Token(Token.TokenType.MINUS_ASSIGN, "-=", startLine, startColumn);
                } else {
                    position++; column++;
                    return new Token(Token.TokenType.MINUS, "-", startLine, startColumn);
                }

            // === MULTIPLY / DIVIDE / MODULO ===
            case '*':
                position++; column++;
                return new Token(Token.TokenType.MULTIPLY, "*", startLine, startColumn);
            case '/':
                position++; column++;
                return new Token(Token.TokenType.DIVIDE, "/", startLine, startColumn);
            case '%':
                position++; column++;
                return new Token(Token.TokenType.MODULO, "%", startLine, startColumn);

            // === EQUAL / ASSIGN (CORRIGÉ ✅) ===
            case '=':
                if (peek() == '=') {
                    position += 2; column += 2;
                    return new Token(Token.TokenType.EQUAL, "==", startLine, startColumn);
                } else {
                    position++; column++;
                    return new Token(Token.TokenType.ASSIGN, "=", startLine, startColumn);
                }

            // === NOT / NOT_EQUAL (CORRIGÉ ✅) ===
            case '!':
                if (peek() == '=') {
                    position += 2; column += 2;
                    return new Token(Token.TokenType.NOT_EQUAL, "!=", startLine, startColumn);
                } else {
                    position++; column++;
                    return new Token(Token.TokenType.NOT, "!", startLine, startColumn);
                }

            // === LESS / LESS_EQUAL (CORRIGÉ ✅) ===
            case '<':
                if (peek() == '=') {
                    position += 2; column += 2;
                    return new Token(Token.TokenType.LESS_EQUAL, "<=", startLine, startColumn);
                } else {
                    position++; column++;
                    return new Token(Token.TokenType.LESS, "<", startLine, startColumn);
                }

            // === GREATER / GREATER_EQUAL (CORRIGÉ ✅) ===
            case '>':
                if (peek() == '=') {
                    position += 2; column += 2;
                    return new Token(Token.TokenType.GREATER_EQUAL, ">=", startLine, startColumn);
                } else {
                    position++; column++;
                    return new Token(Token.TokenType.GREATER, ">", startLine, startColumn);
                }

            // === Parenthèses / Accolades / Crochets ===
            case '(':
                position++; column++;
                return new Token(Token.TokenType.LPAREN, "(", startLine, startColumn);
            case ')':
                position++; column++;
                return new Token(Token.TokenType.RPAREN, ")", startLine, startColumn);
            case '{':
                position++; column++;
                return new Token(Token.TokenType.LBRACE, "{", startLine, startColumn);
            case '}':
                position++; column++;
                return new Token(Token.TokenType.RBRACE, "}", startLine, startColumn);
            case '[':
                position++; column++;
                return new Token(Token.TokenType.LBRACKET, "[", startLine, startColumn);
            case ']':
                position++; column++;
                return new Token(Token.TokenType.RBRACKET, "]", startLine, startColumn);

            // === Autres délimiteurs ===
            case ',':
                position++; column++;
                return new Token(Token.TokenType.COMMA, ",", startLine, startColumn);
            case ':':
                position++; column++;
                return new Token(Token.TokenType.COLON, ":", startLine, startColumn);
            case ';':
                position++; column++;
                return new Token(Token.TokenType.SEMICOLON, ";", startLine, startColumn);
            case '.':
                position++; column++;
                return new Token(Token.TokenType.DOT, ".", startLine, startColumn);

            // === Nouvelle ligne ===
            case '\n':
                position++; line++; column = 1;
                return new Token(Token.TokenType.NEWLINE, "\\n", startLine, startColumn);

            // === Caractère inconnu ===
            default:
                position++; column++;
                return new Token(Token.TokenType.ERROR, String.valueOf(current), startLine, startColumn);
        }
    }

    private Token scanComment() {
        int startLine = line;
        int startColumn = column;
        StringBuilder sb = new StringBuilder();
        while (position < input.length() && input.charAt(position) != '\n') {
            sb.append(input.charAt(position));
            position++;
            column++;
        }
        return new Token(Token.TokenType.COMMENT, sb.toString(), startLine, startColumn);
    }

    private void skipWhitespace() {
        while (position < input.length()) {
            char c = input.charAt(position);
            if (c == ' ' || c == '\t' || c == '\r') {
                position++;
                column++;
            } else {
                break;
            }
        }
    }

    private char peek() {
        if (position + 1 < input.length()) {
            return input.charAt(position + 1);
        }
        return '\0';
    }

    public List<String> getErrors() {
        return errors;
    }
}