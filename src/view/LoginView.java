package view;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {

    // Eingabefelder
    private final JTextField tfUsername = new JTextField(18);
    private final JPasswordField pfPassword = new JPasswordField(18);

    // Buttons
    private final JButton btnLogin = new JButton("Einloggen");
    private final JButton btnRegister = new JButton("Registrieren");
    private final JButton btnClear = new JButton("Leeren");

    // Status/Info
    private final JLabel lblStatus = new JLabel(" ");

    public LoginView() {
        super("Vocabify - Login / Registrierung");
        buildUi();
    }

    private void buildUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 230);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setContentPane(content);

        // Formular
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Benutzername:"), gbc);

        gbc.gridx = 1;
        form.add(tfUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("Passwort:"), gbc);

        gbc.gridx = 1;
        form.add(pfPassword, gbc);

        content.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttons.add(btnLogin);
        buttons.add(btnRegister);
        buttons.add(btnClear);

        content.add(buttons, BorderLayout.SOUTH);

        // Status oben
        lblStatus.setForeground(new Color(30, 30, 30));
        content.add(lblStatus, BorderLayout.NORTH);

        // Clear-Button Verhalten (nur UI)
        btnClear.addActionListener(e -> clearFields());
    }

    // ===== Getter für Controller =====
    public String getUsername() {
        return tfUsername.getText().trim();
    }

    public String getPassword() {
        return new String(pfPassword.getPassword());
    }

    public JButton getBtnLogin() {
        return btnLogin;
    }

    public JButton getBtnRegister() {
        return btnRegister;
    }

    public JButton getBtnClear() {
        return btnClear;
    }

    // ===== UI-Hilfen =====
    public void setStatus(String message) {
        lblStatus.setText(message);
    }

    public void showMessage(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public void clearFields() {
        tfUsername.setText("");
        pfPassword.setText("");
        tfUsername.requestFocusInWindow();
    }
}
