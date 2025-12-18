package view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class QuestionManagementPanel extends JPanel {

    public static final String CMD_ADD_TEXT = "qm_add_text";
    public static final String CMD_ADD_IMAGE = "qm_add_image";
    public static final String CMD_DELETE = "qm_delete";
    public static final String CMD_SAVE = "qm_save";
    public static final String CMD_LOAD = "qm_load";
    public static final String CMD_BROWSE_IMAGE = "qm_browse_image";

    private DefaultListModel<String> listModel;
    private JList<String> questionList;

    private JTextField tfTextQuestion;
    private JTextField tfTextAnswer;

    private JTextField tfImagePath;
    private JButton btnBrowseImage;
    private JTextField tfImageAnswer;

    private JButton btnAddText;
    private JButton btnAddImage;
    private JButton btnDelete;
    private JButton btnSave;
    private JButton btnLoad;

    private JLabel lblInfo;

    public QuestionManagementPanel(ActionListener controller) {
        super(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createListPanel(), BorderLayout.CENTER);
        add(createRightSide(controller), BorderLayout.EAST);

        lblInfo = new JLabel(" ");
        add(lblInfo, BorderLayout.SOUTH);
    }

    private JPanel createListPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(new TitledBorder("Vorhandene Fragen"));

        listModel = new DefaultListModel<String>();
        questionList = new JList<String>(listModel);
        questionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(questionList);
        scroll.setPreferredSize(new Dimension(520, 420));

        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel createRightSide(ActionListener controller) {
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setPreferredSize(new Dimension(360, 420));

        right.add(createTextQuestionPanel(controller));
        right.add(Box.createVerticalStrut(10));
        right.add(createImageQuestionPanel(controller));
        right.add(Box.createVerticalStrut(10));
        right.add(createActionsPanel(controller));

        return right;
    }

    private JPanel createTextQuestionPanel(ActionListener controller) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new TitledBorder("TextQuestion hinzufügen"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblQ = new JLabel("Frage:");
        JLabel lblA = new JLabel("Antwort:");

        tfTextQuestion = new JTextField();
        tfTextAnswer = new JTextField();

        btnAddText = new JButton("TextQuestion hinzufügen");
        btnAddText.setActionCommand(CMD_ADD_TEXT);
        btnAddText.addActionListener(controller);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        p.add(lblQ, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1;
        p.add(tfTextQuestion, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        p.add(lblA, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1;
        p.add(tfTextAnswer, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 1;
        p.add(btnAddText, gbc);

        return p;
    }

    private JPanel createImageQuestionPanel(ActionListener controller) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new TitledBorder("ImageQuestion hinzufügen"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblPath = new JLabel("Bildpfad:");
        JLabel lblA = new JLabel("Antwort:");

        tfImagePath = new JTextField();
        tfImagePath.setEditable(false);

        btnBrowseImage = new JButton("Bild wählen…");
        btnBrowseImage.setActionCommand(CMD_BROWSE_IMAGE);
        btnBrowseImage.addActionListener(controller);

        tfImageAnswer = new JTextField();

        btnAddImage = new JButton("ImageQuestion hinzufügen");
        btnAddImage.setActionCommand(CMD_ADD_IMAGE);
        btnAddImage.addActionListener(controller);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        p.add(lblPath, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1;
        p.add(tfImagePath, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weightx = 1;
        p.add(btnBrowseImage, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        p.add(lblA, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1;
        p.add(tfImageAnswer, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.weightx = 1;
        p.add(btnAddImage, gbc);

        return p;
    }

    private JPanel createActionsPanel(ActionListener controller) {
        JPanel p = new JPanel(new GridLayout(2, 2, 8, 8));
        p.setBorder(new TitledBorder("Aktionen"));

        btnDelete = new JButton("Ausgewählte löschen");
        btnDelete.setActionCommand(CMD_DELETE);
        btnDelete.addActionListener(controller);

        btnSave = new JButton("Speichern");
        btnSave.setActionCommand(CMD_SAVE);
        btnSave.addActionListener(controller);

        btnLoad = new JButton("Laden");
        btnLoad.setActionCommand(CMD_LOAD);
        btnLoad.addActionListener(controller);

        JButton btnDummy = new JButton(" ");
        btnDummy.setEnabled(false);

        p.add(btnDelete);
        p.add(btnSave);
        p.add(btnLoad);
        p.add(btnDummy);

        return p;
    }

    public void setQuestionList(String[] items) {
        listModel.clear();
        if (items == null) {
            return;
        }
        for (int i = 0; i < items.length; i++) {
            listModel.addElement(items[i]);
        }
    }

    public int getSelectedQuestionIndex() {
        return questionList.getSelectedIndex();
    }

    public String getTextQuestionInput() {
        return tfTextQuestion.getText();
    }

    public String getTextAnswerInput() {
        return tfTextAnswer.getText();
    }

    public String getImagePathInput() {
        return tfImagePath.getText();
    }

    public String getImageAnswerInput() {
        return tfImageAnswer.getText();
    }

    public void setImagePathInput(String path) {
        tfImagePath.setText(path == null ? "" : path);
    }

    public void clearTextInputs() {
        tfTextQuestion.setText("");
        tfTextAnswer.setText("");
    }

    public void clearImageInputs() {
        tfImagePath.setText("");
        tfImageAnswer.setText("");
    }

    public void setInfoText(String text) {
        lblInfo.setText(text == null ? " " : text);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public String chooseImageFilePath() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Bild auswählen");
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }
}
