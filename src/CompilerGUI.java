import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.List;

public class CompilerGUI extends JFrame {

    private JTextArea codeArea;
    private JTextArea resultArea;
    private JPanel mainPanel;
    private JPanel welcomePanel;

    // 🎨 Palette de couleurs - BLEU ET GRIS
    private static final Color DARK_GRAY = new Color(40, 40, 40);
    private static final Color MEDIUM_GRAY = new Color(60, 60, 60);
    private static final Color LIGHT_GRAY = new Color(80, 80, 80);
    private static final Color BLUE_ACCENT = new Color(33, 150, 243);    // Bleu principal
    private static final Color BLUE_DARK = new Color(25, 118, 210);      // Bleu foncé
    private static final Color BLUE_HOVER = new Color(66, 165, 245);     // Bleu hover
    private static final Color WHITE = new Color(240, 240, 240);
    private static final Color BLACK_TEXT = Color.BLACK;                 // Texte noir pour boutons

    public CompilerGUI() {
        setTitle("🔷 Mini Compilateur Python");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Initialiser les panneaux
        createWelcomePanel();
        createMainPanel();

        // Afficher l'écran de bienvenue au démarrage
        setContentPane(welcomePanel);
        setVisible(true);
    }

    /**
     * Crée l'écran de bienvenue
     */
    private void createWelcomePanel() {
        welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(DARK_GRAY);

        // 🎨 Header avec logo - BLEU
        JPanel header = new JPanel();
        header.setBackground(BLUE_ACCENT);
        header.setPreferredSize(new Dimension(1000, 100));
        
        JLabel logo = new JLabel("🔷 MINI-COMPILATEUR PYTHON", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        logo.setForeground(Color.WHITE);
        header.add(logo);

        // 💬 Message de bienvenue
        JPanel welcomeContent = new JPanel();
        welcomeContent.setBackground(DARK_GRAY);
        welcomeContent.setLayout(new BoxLayout(welcomeContent, BoxLayout.Y_AXIS));

        JLabel welcomeTitle = new JLabel(" Bonjour et bienvenue !", SwingConstants.CENTER);
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeTitle.setForeground(BLUE_ACCENT);
        welcomeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeTitle.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));

        JLabel welcomeText = new JLabel("<html><div style='text-align: center; color: white; font-size: 15px; line-height: 1.8;'>" +
                "Vous utilisez le <b>Mini-Compilateur Python</b><br>" +
                "Développé dans le cadre du module <b>Compilation</b><br>" +
                "Université A/Mira de Béjaïa - Département d'Informatique</div></html>", SwingConstants.CENTER);
        welcomeText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        welcomeText.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeText.setBorder(BorderFactory.createEmptyBorder(10, 50, 30, 50));

        // 👥 Équipe de développement
        JLabel teamTitle = new JLabel(" Réalisé par :", SwingConstants.CENTER);
        teamTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        teamTitle.setForeground(WHITE);
        teamTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel teamNames = new JLabel("<html><div style='text-align: center; color: " + 
                toHex(BLUE_ACCENT) + "; font-size: 17px; font-weight: bold; line-height: 2.2;'>" +
                "• Khereddine Amira<br>" +
                "• Hedjres Tesnim<br>" +
                "• Mechtaoui Tarek</div></html>", SwingConstants.CENTER);
        teamNames.setFont(new Font("Segoe UI", Font.BOLD, 16));
        teamNames.setAlignmentX(Component.CENTER_ALIGNMENT);
        teamNames.setBorder(BorderFactory.createEmptyBorder(10, 0, 40, 0));

        // 🚀 Bouton "Commencer" - TEXTE NOIR
        JButton startButton = new JButton(" CLIQUER ICI POUR COMMENCER");
        styleStartButton(startButton);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(e -> startCompilation());

        welcomeContent.add(welcomeTitle);
        welcomeContent.add(welcomeText);
        welcomeContent.add(teamTitle);
        welcomeContent.add(teamNames);
        welcomeContent.add(startButton);

        welcomePanel.add(header, BorderLayout.NORTH);
        welcomePanel.add(welcomeContent, BorderLayout.CENTER);

        // 📝 Footer
        JLabel footer = new JLabel("© 2026 - Projet Compilation - ING3", SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        footer.setForeground(LIGHT_GRAY);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        welcomePanel.add(footer, BorderLayout.SOUTH);
    }

    /**
     * Crée le panneau principal de compilation
     */
    private void createMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(DARK_GRAY);

        // 🔥 Header - BLEU
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BLUE_ACCENT);
        
        JLabel title = new JLabel("  COMPILATEUR PYTHON ", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        header.add(title, BorderLayout.CENTER);

        // Bouton retour accueil
        JButton backBtn = new JButton(" Accueil");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        backBtn.setBackground(MEDIUM_GRAY);
        backBtn.setForeground(BLACK_TEXT);  // ✅ TEXTE NOIR
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> showWelcome());
        header.add(backBtn, BorderLayout.EAST);

        // ✍ Zone de code
        codeArea = new JTextArea();
        codeArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        codeArea.setBackground(MEDIUM_GRAY);
        codeArea.setForeground(WHITE);
        codeArea.setCaretColor(BLUE_ACCENT);
        codeArea.setLineWrap(true);
        codeArea.setWrapStyleWord(true);

        JScrollPane codeScroll = new JScrollPane(codeArea);
        codeScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BLUE_ACCENT, 2),
                " 📝 Code Source ",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 12),
                BLUE_ACCENT
        ));

        // 📋 Zone de résultats
        resultArea = new JTextArea();
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        resultArea.setEditable(false);
        resultArea.setBackground(DARK_GRAY);
        resultArea.setForeground(BLUE_ACCENT);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);

        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BLUE_ACCENT, 2),
                "  Résultat de la Compilation ",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 12),
                BLUE_ACCENT
        ));

        // 🎛 Panneau de boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        buttonPanel.setBackground(DARK_GRAY);

        // ✅ Boutons avec TEXTE NOIR
        JButton compileBtn = createStyledButton("⚡ Compiler", BLUE_ACCENT, BLACK_TEXT);
        JButton loadBtn = createStyledButton(" Charger Fichier", LIGHT_GRAY, BLACK_TEXT);
        JButton clearBtn = createStyledButton(" Effacer", new Color(200, 60, 60), BLACK_TEXT);

        compileBtn.addActionListener(e -> compilerCode());
        loadBtn.addActionListener(e -> chargerFichier());
        clearBtn.addActionListener(e -> {
            codeArea.setText("");
            resultArea.setText("");
        });

        buttonPanel.add(compileBtn);
        buttonPanel.add(loadBtn);
        buttonPanel.add(clearBtn);

        // 🧩 Split pane pour code/résultats
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, codeScroll, resultScroll);
        split.setDividerLocation(320);
        split.setResizeWeight(0.5);
        split.setBackground(DARK_GRAY);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(split, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Style pour le bouton de démarrage - TEXTE NOIR
     */
    private void styleStartButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(BLUE_ACCENT);
        button.setForeground(BLACK_TEXT);  // ✅ TEXTE NOIR
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 35, 12, 35));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Effet hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BLUE_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BLUE_ACCENT);
            }
        });
    }

    /**
     * Crée un bouton stylisé - AVEC TEXTE NOIR
     */
    private JButton createStyledButton(String text, Color bgColor, Color textColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bgColor);
        btn.setForeground(textColor);  // ✅ TEXTE NOIR
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
            }
        });
        
        return btn;
    }

    /**
     * Affiche l'écran de bienvenue
     */
    private void showWelcome() {
        setContentPane(welcomePanel);
        revalidate();
        repaint();
    }

    /**
     * Démarre l'interface de compilation
     */
    private void startCompilation() {
        setContentPane(mainPanel);
        revalidate();
        repaint();
        codeArea.requestFocus();
    }

    /**
     * Compile le code
     */
    private void compilerCode() {
        resultArea.setText("");
        String code = codeArea.getText();

        if (code.trim().isEmpty()) {
            resultArea.setForeground(new Color(255, 180, 0));
            resultArea.append(" Veuillez entrer du code à compiler !\n");
            return;
        }

        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);

        boolean syntaxe = parser.parse();

        if (lexer.getErrors().isEmpty() && syntaxe) {
            resultArea.setForeground(BLUE_ACCENT);
            resultArea.append(" COMPILATION RÉUSSIE \n\n");
            resultArea.append("✓ Analyse lexicale : OK\n");
            resultArea.append("✓ Analyse syntaxique : OK\n");
            resultArea.append("✓ Le code est valide !\n");
        } else {
            resultArea.setForeground(new Color(255, 80, 80));
            resultArea.append(" COMPILATION ÉCHOUÉE \n\n");

            if (!lexer.getErrors().isEmpty()) {
                resultArea.append(" Erreurs Lexicales :\n");
                for (String err : lexer.getErrors()) {
                    resultArea.append("   • " + err + "\n");
                }
                resultArea.append("\n");
            }
            if (!parser.getErrors().isEmpty()) {
                resultArea.append(" Erreurs Syntaxiques :\n");
                for (String err : parser.getErrors()) {
                    resultArea.append("   • " + err + "\n");
                }
            }
        }
    }

    /**
     * Charge un fichier
     */
    private void chargerFichier() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Sélectionner un fichier Python");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Fichiers Python (*.py)", "py"));
        
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                codeArea.setText("");
                codeArea.read(br, null);
                resultArea.setForeground(BLUE_ACCENT);
                resultArea.setText(" Fichier '" + file.getName() + "' chargé avec succès !\n");
            } catch (IOException e) {
                resultArea.setForeground(new Color(255, 80, 80));
                resultArea.setText(" Erreur de lecture : " + e.getMessage() + "\n");
            }
        }
    }

    /**
     * Convertit Color en hex pour HTML
     */
    private String toHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    public static void main(String[] args) {
        // Look and feel moderne
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new CompilerGUI();
        });
    }
}