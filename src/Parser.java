import java.util.*;

public class Parser {
    private static List<Token> tokens;
    private static int index;
    private static Token tc;
    private static boolean r;
    private static List<String> errors;

    public Parser(List<Token> tokenList) {
        tokens = tokenList;
        index = 0;
        r = true;
        errors = new ArrayList<>();
        if (tokens.size() > 0) {
            tc = tokens.get(index);
        }
    }

    public boolean parse() {
        Program();
        if (tc.getType() == Token.TokenType.EOF) {
            return r;
        } else {
            if (!errors.isEmpty()) {
                System.out.println("✗ Analyse terminée avec des erreurs.");
            }
            return false;
        }
    }

    private static void Program() {
    skipNewlines();
    while (tc.getType() != Token.TokenType.EOF) {
        Statement();
        if (!r) break; // ⬅️ COUPE LA BOUCLE DÈS LA 1ÈRE ERREUR (évite le spam infini)
        skipNewlines();
    }
}

    private static void Statement() {
        skipNewlines();

        if (tc.getType() == Token.TokenType.SWITCH) {
            SwitchStatement();
        } else if (tc.getType() == Token.TokenType.IF) {
            IfStatement();
        } else if (tc.getType() == Token.TokenType.WHILE) {
            WhileStatement();
        } else if (tc.getType() == Token.TokenType.DO) {
            DoWhileStatement();
        } else if (tc.getType() == Token.TokenType.FOR) {
            ForStatement();
        } else if (tc.getType() == Token.TokenType.ELIF) {
            error("'elif' sans 'if' correspondant");
            synchronize();
        } else if (tc.getType() == Token.TokenType.ELSE) {
            error("'else' sans 'if' correspondant");
            synchronize();
        } else if (tc.getType() == Token.TokenType.IDENTIFIER) {
            AssignmentOrExpression();
        } else if (tc.getType() == Token.TokenType.BREAK || 
                   tc.getType() == Token.TokenType.CONTINUE || 
                   tc.getType() == Token.TokenType.PASS) {
            advance();
            skipNewlines();
        } else if (tc.getType() == Token.TokenType.NEWLINE) {
            advance();
        } else if (tc.getType() != Token.TokenType.EOF &&
                   tc.getType() != Token.TokenType.RBRACE &&
                   tc.getType() != Token.TokenType.CASE &&
                   tc.getType() != Token.TokenType.DEFAULT) {
            error("Instruction non reconnue : " + tc.getValue());
            synchronize();
        }
    }

    private static void IfStatement() {
        match(Token.TokenType.IF, "'if' attendu");
        Expression();
        match(Token.TokenType.COLON, "':' attendu après la condition if");
        skipNewlines();

        while (tc.getType() != Token.TokenType.ELIF &&
               tc.getType() != Token.TokenType.ELSE &&
               tc.getType() != Token.TokenType.EOF &&
               tc.getType() != Token.TokenType.RBRACE && r) {
            Statement();
            skipNewlines();
        }

        while (tc.getType() == Token.TokenType.ELIF && r) {
            advance();
            Expression();
            match(Token.TokenType.COLON, "':' attendu après la condition elif");
            skipNewlines();
            
            while (tc.getType() != Token.TokenType.ELIF &&
                   tc.getType() != Token.TokenType.ELSE &&
                   tc.getType() != Token.TokenType.EOF &&
                   tc.getType() != Token.TokenType.RBRACE && r) {
                Statement();
                skipNewlines();
            }
        }

        if (tc.getType() == Token.TokenType.ELSE && r) {
            advance();
            match(Token.TokenType.COLON, "':' attendu après 'else'");
            skipNewlines();
            
            while (tc.getType() != Token.TokenType.ELIF &&
                   tc.getType() != Token.TokenType.ELSE &&
                   tc.getType() != Token.TokenType.EOF &&
                   tc.getType() != Token.TokenType.RBRACE && r) {
                Statement();
                skipNewlines();
            }
        }
    }

    private static void WhileStatement() {
        match(Token.TokenType.WHILE, "'while' attendu");
        Expression();
        match(Token.TokenType.COLON, "':' attendu après la condition while");
        skipNewlines();

        while (tc.getType() != Token.TokenType.EOF &&
               tc.getType() != Token.TokenType.RBRACE && r) {
            Statement();
            skipNewlines();
        }
    }

    private static void DoWhileStatement() {
        match(Token.TokenType.DO, "'do' attendu");
        skipNewlines();

        while (tc.getType() != Token.TokenType.WHILE &&
               tc.getType() != Token.TokenType.EOF && r) {
            Statement();
            skipNewlines();
        }

        match(Token.TokenType.WHILE, "'while' attendu après 'do'");
        Expression();
        
        if (tc.getType() == Token.TokenType.SEMICOLON || 
            tc.getType() == Token.TokenType.COLON) {
            advance();
        }
    }

    private static void ForStatement() {
        match(Token.TokenType.FOR, "'for' attendu");
        
        if (tc.getType() == Token.TokenType.IDENTIFIER) {
            advance();
        } else {
            error("Identifiant attendu après 'for'");
            return;
        }

        match(Token.TokenType.IN, "'in' attendu après l'identifiant");
        Expression();
        match(Token.TokenType.COLON, "':' attendu après l'expression for");
        skipNewlines();

        while (tc.getType() != Token.TokenType.EOF &&
               tc.getType() != Token.TokenType.RBRACE && r) {
            Statement();
            skipNewlines();
        }
    }

    private static void SwitchStatement() {
        match(Token.TokenType.SWITCH, "'switch' attendu");
        match(Token.TokenType.LPAREN, "'(' attendu après 'switch'");
        Expression();
        match(Token.TokenType.RPAREN, "')' attendu après l'expression");
        match(Token.TokenType.LBRACE, "'{' attendu pour ouvrir le bloc switch");
        skipNewlines();

        if (tc.getType() != Token.TokenType.CASE && tc.getType() != Token.TokenType.DEFAULT) {
            error("Au moins un 'case' ou 'default' attendu dans le switch");
        }

        while (tc.getType() == Token.TokenType.CASE && r) { CaseClause(); }
        if (tc.getType() == Token.TokenType.DEFAULT && r) { DefaultClause(); }

        match(Token.TokenType.RBRACE, "'}' attendu pour fermer le bloc switch");
    }

    private static void CaseClause() {
        match(Token.TokenType.CASE, "'case' attendu");
        Expression();
        match(Token.TokenType.COLON, "':' attendu après la valeur du case");
        skipNewlines();

        while (tc.getType() != Token.TokenType.CASE &&
               tc.getType() != Token.TokenType.DEFAULT &&
               tc.getType() != Token.TokenType.RBRACE &&
               tc.getType() != Token.TokenType.EOF && r) {
            if (tc.getType() == Token.TokenType.BREAK) {
                advance();
                skipNewlines();
                return;
            }
            Statement();
            skipNewlines();
        }
    }

    private static void DefaultClause() {
        match(Token.TokenType.DEFAULT, "'default' attendu");
        match(Token.TokenType.COLON, "':' attendu après 'default'");
        skipNewlines();

        while (tc.getType() != Token.TokenType.RBRACE &&
               tc.getType() != Token.TokenType.EOF && r) {
            if (tc.getType() == Token.TokenType.BREAK) {
                advance();
                skipNewlines();
                return;
            }
            Statement();
            skipNewlines();
        }
    }

    private static void AssignmentOrExpression() {
        if (tc.getType() == Token.TokenType.IDENTIFIER) {
            advance();
            if (tc.getType() == Token.TokenType.ASSIGN ||
                tc.getType() == Token.TokenType.PLUS_ASSIGN ||
                tc.getType() == Token.TokenType.MINUS_ASSIGN) {
                advance();
                Expression();
            } else if (tc.getType() == Token.TokenType.INCREMENT ||
                       tc.getType() == Token.TokenType.DECREMENT) {
                advance();
            } else {
                while ((tc.getType() == Token.TokenType.DOT ||
                        tc.getType() == Token.TokenType.LBRACKET ||
                        tc.getType() == Token.TokenType.LPAREN) && r) {
                    
                    if (tc.getType() == Token.TokenType.DOT) {
                        advance();
                        if (tc.getType() == Token.TokenType.IDENTIFIER) advance();
                        else error("Identifiant attendu après '.'");
                    } else if (tc.getType() == Token.TokenType.LBRACKET) {
                        advance();
                        Expression();
                        match(Token.TokenType.RBRACKET, "']' attendu");
                    } else if (tc.getType() == Token.TokenType.LPAREN) {
                        advance();
                        ArgumentList();
                        match(Token.TokenType.RPAREN, "')' attendu");
                    }
                }
            }
        } else {
            error("Identifiant attendu");
        }
    }

    private static void Expression() { LogicalOr(); }
    private static void LogicalOr() {
        LogicalAnd();
        while (tc.getType() == Token.TokenType.OR && r) { advance(); LogicalAnd(); }
    }
    private static void LogicalAnd() {
        Equality();
        while (tc.getType() == Token.TokenType.AND && r) { advance(); Equality(); }
    }
    private static void Equality() {
        Comparison();
        while ((tc.getType() == Token.TokenType.EQUAL || tc.getType() == Token.TokenType.NOT_EQUAL) && r) {
            advance(); Comparison();
        }
    }
    private static void Comparison() {
        Term();
        while ((tc.getType() == Token.TokenType.LESS || tc.getType() == Token.TokenType.LESS_EQUAL ||
                tc.getType() == Token.TokenType.GREATER || tc.getType() == Token.TokenType.GREATER_EQUAL) && r) {
            advance(); Term();
        }
    }
    private static void Term() {
        Factor();
        while ((tc.getType() == Token.TokenType.PLUS || tc.getType() == Token.TokenType.MINUS) && r) {
            advance(); Factor();
        }
    }
    private static void Factor() {
        Unary();
        while ((tc.getType() == Token.TokenType.MULTIPLY || tc.getType() == Token.TokenType.DIVIDE ||
                tc.getType() == Token.TokenType.MODULO) && r) {
            advance(); Unary();
        }
    }
    private static void Unary() {
        if (tc.getType() == Token.TokenType.NOT || tc.getType() == Token.TokenType.MINUS ||
            tc.getType() == Token.TokenType.INCREMENT || tc.getType() == Token.TokenType.DECREMENT) {
            advance(); Unary();
        } else { Primary(); }
    }
      

// 2️⃣ Remplace Primary() par celle-ci :
private static void Primary() {
    // ✅ FIX : Gérer range(5) ou range(1, 10, 2) avec ses parenthèses
    if (tc.getType() == Token.TokenType.RANGE) {
        advance();
        if (tc.getType() == Token.TokenType.LPAREN) {
            advance();
            if (tc.getType() != Token.TokenType.RPAREN) {
                Expression();
                while (tc.getType() == Token.TokenType.COMMA && r) { 
                    advance(); 
                    Expression(); 
                }
            }
            match(Token.TokenType.RPAREN, "')' attendu après range");
        }
        return;
    }

    if (tc.getType() == Token.TokenType.INTEGER || tc.getType() == Token.TokenType.FLOAT ||
        tc.getType() == Token.TokenType.STRING || tc.getType() == Token.TokenType.BOOLEAN) {
        advance();
        return;
    }
    if (tc.getType() == Token.TokenType.IDENTIFIER) {
        advance();
        while ((tc.getType() == Token.TokenType.DOT ||
                tc.getType() == Token.TokenType.LBRACKET ||
                tc.getType() == Token.TokenType.LPAREN) && r) {
            if (tc.getType() == Token.TokenType.DOT) {
                advance();
                if (tc.getType() == Token.TokenType.IDENTIFIER) advance();
                else error("Identifiant attendu après '.'");
            } else if (tc.getType() == Token.TokenType.LBRACKET) {
                advance(); Expression();
                match(Token.TokenType.RBRACKET, "']' attendu");
            } else if (tc.getType() == Token.TokenType.LPAREN) {
                advance(); ArgumentList();
                match(Token.TokenType.RPAREN, "')' attendu");
            }
        }
        return;
    }
    if (tc.getType() == Token.TokenType.LPAREN) {
        advance(); Expression();
        match(Token.TokenType.RPAREN, "')' attendu");
        return;
    }
    if (tc.getType() == Token.TokenType.LBRACKET) {
        advance();
        if (tc.getType() != Token.TokenType.RBRACKET) {
            Expression();
            while (tc.getType() == Token.TokenType.COMMA && r) { advance(); Expression(); }
        }
        match(Token.TokenType.RBRACKET, "']' attendu");
        return;
    }
    error("Expression invalide : " + tc.getValue());
}
    private static void ArgumentList() {
        if (tc.getType() != Token.TokenType.RPAREN) {
            Expression();
            while (tc.getType() == Token.TokenType.COMMA && r) { advance(); Expression(); }
        }
    }

    private static void match(Token.TokenType expected, String message) {
        if (tc.getType() == expected) {
            advance();
        } else {
            error(message);
        }
    }

    private static void advance() {
        if (index < tokens.size() - 1) {
            index++;
            tc = tokens.get(index);
        } else {
            tc = new Token(Token.TokenType.EOF, "", tc.getLine(), tc.getColumn());
        }
    }

    private static void skipNewlines() {
        while (tc.getType() == Token.TokenType.NEWLINE && index < tokens.size() - 1) {
            advance();
        }
    }

    private static void error(String message) {
        String errorMsg = String.format("Erreur ligne %d, colonne %d: %s (trouvé '%s')",
                tc.getLine(), tc.getColumn(), message, tc.getValue());
        errors.add(errorMsg);
        r = false;
        synchronize();
    }

    private static void synchronize() {
        int safetyCounter = 0;
        while (tc.getType() != Token.TokenType.EOF && safetyCounter < 1000) {
            if (tc.getType() == Token.TokenType.NEWLINE ||
                tc.getType() == Token.TokenType.IF ||
                tc.getType() == Token.TokenType.WHILE ||
                tc.getType() == Token.TokenType.FOR ||
                tc.getType() == Token.TokenType.SWITCH ||
                tc.getType() == Token.TokenType.DO ||
                tc.getType() == Token.TokenType.ELIF ||
                tc.getType() == Token.TokenType.ELSE ||
                tc.getType() == Token.TokenType.RBRACE) {
                return;
            }
            advance();
            safetyCounter++;
        }
    }

    public List<String> getErrors() {
        return errors;
    }
}