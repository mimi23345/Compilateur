import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.List;

public class CompilerGUI extends JFrame {
    private JTextArea codeArea;
    private JTextArea resultArea;
    private JPanel mainPanel;
    private JPanel welcomePanel;
    private JLabel statusBar;

    // Thème sombre professionnel
    private static final Color BG_DARK = new Color(28, 28, 30);
    private static final Color BG_PANEL = new Color(40, 40, 45);
    private static final Color BG_EDITOR = new Color(35, 35, 38);
    private static final Color ACCENT = new Color(59, 130, 246);
    private static final Color SUCCESS = new Color(34, 197, 94);
    private static final Color ERROR = new Color(239, 68, 68);
    private static final Color TEXT_LIGHT = new Color(220, 220, 225);
    private static final Color TEXT_DIM = new Color(156, 163, 175);

    public CompilerGUI() {
        setTitle("Mini Compilateur Python");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 500));
        createWelcomePanel();
        createMainPanel();
        setContentPane(welcomePanel);
        setVisible(true);
    }

    private void createWelcomePanel() {
        welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(BG_DARK);

        JPanel header = new JPanel();
        header.setBackground(ACCENT);
        header.setPreferredSize(new Dimension(1000, 80));
        JLabel title = new JLabel("MINI-COMPILATEUR PYTHON", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        header.add(title);

        JPanel center = new JPanel();
        center.setBackground(BG_DARK);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JLabel welcome = new JLabel("Bienvenue", SwingConstants.CENTER);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcome.setForeground(ACCENT);
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcome.setBorder(BorderFactory.createEmptyBorder(40, 0, 10, 0));

        JLabel desc = new JLabel("<html><div style='text-align:center; color:#a0a5b0; font-size:15px; line-height:1.7;'>"+
                "Analyse lexicale & syntaxique d'un sous-ensemble Python.<br>"+
                "Developpe pour le module <b>Compilation</b> | Universite A/Mira de Bejaia</div></html>", SwingConstants.CENTER);
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);
        desc.setBorder(BorderFactory.createEmptyBorder(10, 40, 30, 40));

        JLabel team = new JLabel("<html><div style='text-align:center; color:#8b949e; font-size:13px; line-height:1.6;'>"+
                "Equipe : Khereddine Amira | Hedjres Tesnim | Mechtaoui Tarek</div></html>", SwingConstants.CENTER);
        team.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        team.setAlignmentX(Component.CENTER_ALIGNMENT);
        team.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JButton startBtn = createButton("DEMMARRER L'ENVIRONNEMENT", ACCENT, Color.WHITE, 220, 45);
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startBtn.addActionListener(e -> startCompilation());

        center.add(welcome);
        center.add(desc);
        center.add(team);
        center.add(startBtn);

        welcomePanel.add(header, BorderLayout.NORTH);
        welcomePanel.add(center, BorderLayout.CENTER);
    }

    private void createMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_DARK);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toolbar.setBackground(BG_PANEL);
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60,60,65)));

        JLabel logo = new JLabel(">> COMPILATEUR");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(ACCENT);

        JButton compileBtn = createButton("COMPILER", SUCCESS, Color.WHITE, 110, 34);
        JButton loadBtn = createButton("CHARGER", ACCENT, Color.WHITE, 100, 34);
        JButton clearBtn = createButton("EFFACER", ERROR, Color.WHITE, 100, 34);
        JButton backBtn = createButton("<- ACCUEIL", new Color(70,70,75), TEXT_LIGHT, 100, 34);

        compileBtn.addActionListener(e -> compilerCode());
        loadBtn.addActionListener(e -> chargerFichier());
        clearBtn.addActionListener(e -> { codeArea.setText(""); resultArea.setText(""); updateStatus("Pret"); });
        backBtn.addActionListener(e -> { setContentPane(welcomePanel); revalidate(); repaint(); });

        toolbar.add(logo);
        toolbar.add(Box.createHorizontalStrut(20));
        toolbar.add(compileBtn);
        toolbar.add(loadBtn);
        toolbar.add(clearBtn);
        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(backBtn);

        codeArea = new JTextArea();
        codeArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        codeArea.setBackground(BG_EDITOR);
        codeArea.setForeground(TEXT_LIGHT);
        codeArea.setCaretColor(ACCENT);
        codeArea.setLineWrap(true);
        codeArea.setWrapStyleWord(true);
        codeArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane codeScroll = new JScrollPane(codeArea);
        codeScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60,60,65), 2), " CODE SOURCE ", 0, 0, new Font("Segoe UI", Font.BOLD, 12), TEXT_DIM));

        resultArea = new JTextArea();
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(22, 22, 24));
        resultArea.setForeground(TEXT_DIM);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60,60,65), 2), " CONSOLE DE COMPILATION ", 0, 0, new Font("Segoe UI", Font.BOLD, 12), TEXT_DIM));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, codeScroll, resultScroll);
        split.setDividerLocation(450);
        split.setResizeWeight(0.6);
        split.setBorder(null);
        split.setDividerSize(6);
        split.setBackground(BG_DARK);

        statusBar = new JLabel("Pret");
        statusBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusBar.setForeground(TEXT_DIM);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        statusBar.setBackground(new Color(30,30,33));
        statusBar.setOpaque(true);

        mainPanel.add(toolbar, BorderLayout.NORTH);
        mainPanel.add(split, BorderLayout.CENTER);
        mainPanel.add(statusBar, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, Color bg, Color fg, int w, int h) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(w, h));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(bg.darker()); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(bg); }
        });
        return btn;
    }

    private void startCompilation() {
        setContentPane(mainPanel);
        revalidate();
        repaint();
        codeArea.requestFocus();
        updateStatus("Environnement initialise");
    }

    private void compilerCode() {
   
    new Thread(() -> {
        updateStatus("Compilation en cours...");
        
        SwingUtilities.invokeLater(() -> resultArea.setText(""));
        String code = codeArea.getText();
        
        if (code.trim().isEmpty()) {
            SwingUtilities.invokeLater(() -> {
                resultArea.setForeground(ERROR);
                resultArea.append("[AVERTISSEMENT] Aucun code detecte.\n");
                updateStatus("Code vide");
            });
            return;
        }

        try {
            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.tokenize();
            Parser parser = new Parser(tokens);
            boolean ok = parser.parse();

            SwingUtilities.invokeLater(() -> {
                if (lexer.getErrors().isEmpty() && ok) {
                    resultArea.setForeground(SUCCESS);
                    resultArea.append("[SUCCES] COMPILATION REUSSIE\n");
                    resultArea.append("  |- Analyse lexicale : OK\n");
                    resultArea.append("  |- Analyse syntaxique : OK\n");
                    resultArea.append("  └- Le code est valide.\n");
                    
                    resultArea.append("\n LISTE DES TOKENS :\n");
                    resultArea.append("────────────────────────────────\n");
                    int count = 0;
                    for (Token token : tokens) {
                        if (token.getType() != Token.TokenType.EOF && 
                            token.getType() != Token.TokenType.NEWLINE) {
                            count++;
                            resultArea.append(String.format("  %3d. %-18s : '%s'\n", 
                                count, token.getType(), token.getValue()));
                        }
                    }
                    resultArea.append("────────────────────────────────\n");
                    resultArea.append("Total : " + count + " token(s)\n");
                    updateStatus("Compilation terminee (succes)");
                } else {
                    resultArea.setForeground(ERROR);
                    resultArea.append("[ERREUR] COMPILATION ECHEOUEE\n\n");
                    if (!lexer.getErrors().isEmpty()) {
                        resultArea.append("[LEXICAL] Erreurs:\n");
                        lexer.getErrors().forEach(e -> resultArea.append("  * " + e + "\n"));
                    }
                    if (!parser.getErrors().isEmpty()) {
                        resultArea.append("[SYNTAXE] Erreurs:\n");
                        parser.getErrors().forEach(e -> resultArea.append("  * " + e + "\n"));
                    }
                    updateStatus("Compilation echouee");
                }
            });
        } catch (Exception ex) {
            SwingUtilities.invokeLater(() -> {
                resultArea.setForeground(ERROR);
                resultArea.append("[CRITIQUE] Erreur : " + ex.getMessage() + "\n");
                updateStatus("Erreur d'execution");
            });
        }
    }).start();  
}


    private void chargerFichier() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Charger un fichier Python");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Fichiers Python (*.py)", "py"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                codeArea.setText("");
                codeArea.read(br, null);
                resultArea.setForeground(ACCENT);
                resultArea.append("[INFO] Fichier '" + file.getName() + "' charge.\n");
                updateStatus("Fichier charge : " + file.getName());
            } catch (IOException ex) {
                resultArea.setForeground(ERROR);
                resultArea.append("[ERREUR] Impossible de lire le fichier : " + ex.getMessage() + "\n");
                updateStatus("Erreur de fichier");
            }
        }
    }

    private void updateStatus(String msg) {
        statusBar.setText("> " + msg);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new CompilerGUI());
    }
}