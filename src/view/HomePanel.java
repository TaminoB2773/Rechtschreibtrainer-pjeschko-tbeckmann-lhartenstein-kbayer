package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HomePanel extends JPanel {

    public HomePanel(MainController controller) {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 247));
        setBorder(new EmptyBorder(50, 50, 50, 50));

        // Titel-Sektion
        JLabel titleLabel = new JLabel("Rechtschreibtrainer Deluxe", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 38));
        titleLabel.setForeground(new Color(33, 33, 33));
        add(titleLabel, BorderLayout.NORTH);

        // Center-Sektion: Drei gleich große Spiele-Kacheln
        JPanel grid = new JPanel(new GridLayout(1, 3, 25, 25));
        grid.setOpaque(false);

        // Kachel 1: Quiz
        grid.add(createMenuButton("Quiz", "<html><div style='text-align: center;'>Testen Sie Ihr Wissen!<br>(10 Fragen)</div></html>", "📝", e -> controller.showQuiz()));

        // Kachel 2: Hangman
        grid.add(createMenuButton("Hangman", "<html><div style='text-align: center;'>Raten Sie das Wort,<br>bevor es zu spät ist!</div></html>", "🪓", e -> controller.showHangman()));

        // Kachel 3: Anagramm
        grid.add(createMenuButton("Anagramm", "<html><div style='text-align: center;'>Bringen Sie die Buchstaben<br>in die richtige Ordnung!</div></html>", "🧩", e -> controller.showAnagram()));

        add(grid, BorderLayout.CENTER);

        // Footer-Sektion
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JLabel versionLabel = new JLabel("Version 1.0 - Viel Erfolg beim Lernen!", SwingConstants.LEFT);
        versionLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));

        JButton btnManage = new JButton("⚙️ Fragen-Verwaltung");
        btnManage.setFocusPainted(false);
        btnManage.addActionListener(e -> controller.showManage());

        footerPanel.add(versionLabel, BorderLayout.WEST);
        footerPanel.add(btnManage, BorderLayout.EAST);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private JButton createMenuButton(String title, String subtitle, String icon, java.awt.event.ActionListener action) {
        JButton button = new JButton();
        // BoxLayout auf der Y-Achse erlaubt vertikales Stapeln
        button.setLayout(new BoxLayout(button, BoxLayout.Y_AXIS));
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(25, 10, 25, 10)
        ));

        // Icon
        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 65));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Titel
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Beschreibung (Subtitle) - Hier nutzen wir HTML für die Zentrierung innerhalb des Labels
        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSub.setForeground(Color.GRAY);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSub.setHorizontalAlignment(SwingConstants.CENTER); // Wichtig für die Label-interne Zentrierung

        // Zusammenbau mit Abständen
        button.add(Box.createVerticalGlue());
        button.add(lblIcon);
        button.add(Box.createVerticalStrut(15));
        button.add(lblTitle);
        button.add(Box.createVerticalStrut(10));
        button.add(lblSub);
        button.add(Box.createVerticalGlue());

        button.addActionListener(action);

        // Hover-Effekte
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(235, 245, 255));
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });

        return button;
    }
}