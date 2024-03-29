import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main extends JFrame {
    private JTextArea resultArea;
    private JTextField wordField;
    private final JFileChooser fileChooser;

    public Main() {
        super("Text Search App");
        setLayout(new BorderLayout());

        // Champ de texte pour le mot à rechercher
        wordField = new JTextField(20);
        JPanel northPanel = new JPanel();
        northPanel.add(new JLabel("Mot à chercher :"));
        northPanel.add(wordField);
        add(northPanel, BorderLayout.NORTH);

        // Zone de texte pour afficher les résultats
        resultArea = new JTextArea(20, 50);
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // Sélectionneur de fichiers
        fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);

        // Bouton pour choisir les fichiers et démarrer la recherche
        JButton searchButton = new JButton("Chercher dans les fichiers");
        searchButton.addActionListener(e -> performSearch());
        JPanel southPanel = new JPanel();
        southPanel.add(searchButton);
        add(southPanel, BorderLayout.SOUTH);

        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void performSearch() {
        resultArea.setText("");
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            for (var file : fileChooser.getSelectedFiles()) {
                try {
                    searchWordInFile(file.getPath(), wordField.getText());
                } catch (IOException e) {
                    resultArea.append("Erreur lors de la lecture du fichier : " + file.getName() + "\n");
                    e.printStackTrace();
                }
            }
        }
    }

    private void searchWordInFile(String filePath, String wordToFind) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains(wordToFind)) {
                resultArea.append("Mot \"" + wordToFind + "\" trouvé dans " + filePath + " à la ligne " + (i + 1) + "\n");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
