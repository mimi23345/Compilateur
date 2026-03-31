import java.util.*;

/**
 * Parser - Analyseur Syntaxique Complet
 * Supporte : Variables, Affectation, Comparaison (==, !=), Incrémentation,
 *            if/elif/else, while, do/while, for, switch/case
 */
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

        if (tc.getType() == Token.TokenType.EOF && r) {
            System.out.println("✓ Analyse syntaxique réussie !");
            return true;
        } else {
            if (!errors.isEmpty()) {
                System.out.println("✗ Erreurs détectées :");
                for (String error : errors) {
                    System.out.println("  " + error);
                }
            }
            return false;
        }
    }

    /**
     * RÈGLE : Program
     */
    private static void Program() {
        skipNewlines();
        while (tc.getType() != Token.TokenType.EOF && r) {
            Statement();
            skipNewlines();
        }
    }

    /**
     * RÈGLE : Statement
     */
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
            advance();
        } else if (tc.getType() == Token.TokenType.ELSE) {
            error("'else' sans 'if' correspondant");
            advance();
        } else if (tc.getType() == Token.TokenType.IDENTIFIER) {
            AssignmentOrExpression();
        } else if (tc.getType() == Token.TokenType.BREAK) {
            advance();
            skipNewlines();
        } else if (tc.getType() == Token.TokenType.CONTINUE) {
            advance();
            skipNewlines();
        } else if (tc.getType() == Token.TokenType.PASS) {
            advance();
            skipNewlines();
        } else if (tc.getType() == Token.TokenType.NEWLINE) {
            advance();
        } else if (tc.getType() != Token.TokenType.EOF &&
                tc.getType() != Token.TokenType.RBRACE &&
                tc.getType() != Token.TokenType.CASE &&
                tc.getType() != Token.TokenType.DEFAULT) {
            error("Instruction non reconnue : " + tc.getValue());
            advance();
        }
    }

    /**
     * RÈGLE : IfStatement
     */
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

    /**
     * RÈGLE : WhileStatement
     */
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

    /**
     * RÈGLE : DoWhileStatement (do/while)
     */
    private static void DoWhileStatement() {
        match(Token.TokenType.DO, "'do' attendu");
        skipNewlines();

        // Corps du do
        while (tc.getType() != Token.TokenType.WHILE &&
               tc.getType() != Token.TokenType.EOF && r) {
            Statement();
            skipNewlines();
        }

        match(Token.TokenType.WHILE, "'while' attendu après 'do'");
        Expression();
        
        // Point-virgule optionnel (style C) ou deux-points (style Python)
        if (tc.getType() == Token.TokenType.SEMICOLON || 
            tc.getType() == Token.TokenType.COLON) {
            advance();
        }
    }

    /**
     * RÈGLE : ForStatement
     */
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

    /**
     * RÈGLE : SwitchStatement
     */
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

        while (tc.getType() == Token.TokenType.CASE && r) {
            CaseClause();
        }

        if (tc.getType() == Token.TokenType.DEFAULT && r) {
            DefaultClause();
        }

        match(Token.TokenType.RBRACE, "'}' attendu pour fermer le bloc switch");
    }

    /**
     * RÈGLE : CaseClause
     */
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

    /**
     * RÈGLE : DefaultClause
     */
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

    /**
     * RÈGLE : AssignmentOrExpression
     * Supporte : =, +=, -=, ++, --
     */
    private static void AssignmentOrExpression() {
        if (tc.getType() == Token.TokenType.IDENTIFIER) {
            advance();

            // Affectation : =, +=, -=
            if (tc.getType() == Token.TokenType.ASSIGN ||
                tc.getType() == Token.TokenType.PLUS_ASSIGN ||
                tc.getType() == Token.TokenType.MINUS_ASSIGN) {
                advance();
                Expression();
            }
            // Incrémentation/Décrémentation : ++, --
            else if (tc.getType() == Token.TokenType.INCREMENT ||
                     tc.getType() == Token.TokenType.DECREMENT) {
                advance();
            }
            // Accès aux attributs, méthodes, tableaux
            else {
                while ((tc.getType() == Token.TokenType.DOT ||
                        tc.getType() == Token.TokenType.LBRACKET ||
                        tc.getType() == Token.TokenType.LPAREN) && r) {
                    
                    if (tc.getType() == Token.TokenType.DOT) {
                        advance();
                        if (tc.getType() == Token.TokenType.IDENTIFIER) {
                            advance();
                        } else {
                            error("Identifiant attendu après '.'");
                        }
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

    /**
     * EXPRESSIONS - Hiérarchie de précédence
     */
    private static void Expression() {
        LogicalOr();
    }

    private static void LogicalOr() {
        LogicalAnd();
        while (tc.getType() == Token.TokenType.OR && r) {
            advance();
            LogicalAnd();
        }
    }

    private static void LogicalAnd() {
        Equality();
        while (tc.getType() == Token.TokenType.AND && r) {
            advance();
            Equality();
        }
    }

    /**
     * Equality : == et !=
     */
    private static void Equality() {
        Comparison();
        while ((tc.getType() == Token.TokenType.EQUAL ||      // ==
                tc.getType() == Token.TokenType.NOT_EQUAL) && r) {  // !=
            advance();
            Comparison();
        }
    }

    /**
     * Comparison : <, >, <=, >=
     */
    private static void Comparison() {
        Term();
        while ((tc.getType() == Token.TokenType.LESS ||
                tc.getType() == Token.TokenType.LESS_EQUAL ||
                tc.getType() == Token.TokenType.GREATER ||
                tc.getType() == Token.TokenType.GREATER_EQUAL) && r) {
            advance();
            Term();
        }
    }

    private static void Term() {
        Factor();
        while ((tc.getType() == Token.TokenType.PLUS ||
                tc.getType() == Token.TokenType.MINUS) && r) {
            advance();
            Factor();
        }
    }

    private static void Factor() {
        Unary();
        while ((tc.getType() == Token.TokenType.MULTIPLY ||
                tc.getType() == Token.TokenType.DIVIDE ||
                tc.getType() == Token.TokenType.MODULO) && r) {
            advance();
            Unary();
        }
    }

    private static void Unary() {
        if (tc.getType() == Token.TokenType.NOT ||
            tc.getType() == Token.TokenType.MINUS ||
            tc.getType() == Token.TokenType.INCREMENT ||
            tc.getType() == Token.TokenType.DECREMENT) {
            advance();
            Unary();
        } else {
            Primary();
        }
    }

    private static void Primary() {
        // Littéraux
        if (tc.getType() == Token.TokenType.INTEGER ||
            tc.getType() == Token.TokenType.FLOAT ||
            tc.getType() == Token.TokenType.STRING ||
            tc.getType() == Token.TokenType.BOOLEAN) {
            advance();
            return;
        }

        // Identifiant
        if (tc.getType() == Token.TokenType.IDENTIFIER) {
            advance();
            while ((tc.getType() == Token.TokenType.DOT ||
                    tc.getType() == Token.TokenType.LBRACKET ||
                    tc.getType() == Token.TokenType.LPAREN) && r) {
                
                if (tc.getType() == Token.TokenType.DOT) {
                    advance();
                    if (tc.getType() == Token.TokenType.IDENTIFIER) {
                        advance();
                    } else {
                        error("Identifiant attendu après '.'");
                    }
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
            return;
        }

        // Expression entre parenthèses
        if (tc.getType() == Token.TokenType.LPAREN) {
            advance();
            Expression();
            match(Token.TokenType.RPAREN, "')' attendu");
            return;
        }

        // Liste
        if (tc.getType() == Token.TokenType.LBRACKET) {
            advance();
            if (tc.getType() != Token.TokenType.RBRACKET) {
                Expression();
                while (tc.getType() == Token.TokenType.COMMA && r) {
                    advance();
                    Expression();
                }
            }
            match(Token.TokenType.RBRACKET, "']' attendu");
            return;
        }

        error("Expression invalide : " + tc.getValue());
    }

    private static void ArgumentList() {
        if (tc.getType() != Token.TokenType.RPAREN) {
            Expression();
            while (tc.getType() == Token.TokenType.COMMA && r) {
                advance();
                Expression();
            }
        }
    }

    /**
     * UTILITAIRES
     */
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
        System.out.println("✗ " + errorMsg);
        r = false;
    }

    public List<String> getErrors() {
        return errors;
    }
}