import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main extends JFrame {
    private JTextArea resultArea;
    private JTextField wordField;
    private JCheckBox caseSensitiveCheckBox; // Checkbox pour la sensibilité à la casse
    private final JFileChooser fileChooser;

    public Main() {
        super("Text Search App");
        setLayout(new FlowLayout());

        // Champ de texte pour le mot à rechercher
        wordField = new JTextField(20);
        add(new JLabel("Mot à chercher :"));
        add(wordField);

        // Checkbox pour la sensibilité à la casse
        caseSensitiveCheckBox = new JCheckBox("Sensible à la casse", false);
        add(caseSensitiveCheckBox);

        // Zone de texte pour afficher les résultats
        resultArea = new JTextArea(20, 50);
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea));

        // Sélectionneur de fichiers
        fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);

        // Bouton pour choisir les fichiers et démarrer la recherche
        JButton searchButton = new JButton("Chercher dans les fichiers");
        searchButton.addActionListener(e -> performSearch());
        add(searchButton);

        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void performSearch() {
        resultArea.setText("");
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            for (var file : fileChooser.getSelectedFiles()) {
                try {
                    searchWordInFile(file.getPath(), wordField.getText(), caseSensitiveCheckBox.isSelected());
                } catch (IOException e) {
                    resultArea.append("Erreur lors de la lecture du fichier : " + file.getName() + "\n");
                    e.printStackTrace();
                }
            }
        }
    }

    private void searchWordInFile(String filePath, String wordToFind, boolean caseSensitive) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!caseSensitive) {
                line = line.toLowerCase();
                wordToFind = wordToFind.toLowerCase();
            }
            if (line.contains(wordToFind)) {
                resultArea.append("Mot \"" + wordToFind + "\" trouvé dans " + filePath + " à la ligne " + (i + 1) + "\n");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
