import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * Performs a search operation.
     *
     * @param  None    This function does not take any parameters.
     * @return None    This function does not return any value.
     */
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

    /**
     * Search for a specific word in a file and display the context around the word if found.
     *
     * @param  filePath       the path to the file to search in
     * @param  wordToFind     the word to search for in the file
     * @param  caseSensitive  flag to indicate if the search should be case sensitive
     * @return                void
     */
    private void searchWordInFile(String filePath, String wordToFind, boolean caseSensitive) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        String regex = "\\b(?<=\\W|^)" + Pattern.quote(wordToFind) + "(?=\\W|$)\\b"; // Construit un regex pour le mot
        Pattern pattern = Pattern.compile(regex, caseSensitive ? 0 : Pattern.CASE_INSENSITIVE);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                // Extrait le contexte : 3 mots avant et après le mot trouvé
                String context = extractContext(line, matcher.start(), matcher.end());
                resultArea.append("Mot \"" + wordToFind + "\" trouvé dans " + filePath + " à la ligne " + (i + 1) + " : \"" + context + "\"\n");
            }
        }
    }

    /**
     * A function that extracts a context from a given line based on start and end indexes.
     *
     * @param  line       the input line from which to extract the context
     * @param  startIdx   the starting index for the context extraction
     * @param  endIdx     the ending index for the context extraction
     * @return            the extracted context based on the provided indexes
     */
    private String extractContext(String line, int startIdx, int endIdx) {
        String[] words = line.split("\\s+");
        int foundWordIndex = -1;
        
        // Recherche l'index du mot trouvé dans le tableau des mots
        for (int i = 0; i < words.length; i++) {
            if (line.indexOf(words[i]) >= startIdx) {
                foundWordIndex = i;
                break;
            }
        }
        
        if (foundWordIndex == -1) return ""; // Si le mot n'est pas trouvé pour une raison quelconque

        // Calcule les index de début et de fin pour extraire le contexte
        int start = Math.max(foundWordIndex - 3, 0);
        int end = Math.min(foundWordIndex + 4, words.length);
        
        // Reconstitue le contexte avec les mots sélectionnés
        StringBuilder contextBuilder = new StringBuilder();
        for (int i = start; i < end; i++) {
            contextBuilder.append(words[i]).append(" ");
        }
        
        return contextBuilder.toString().trim();
    }


    /**
     * The main method of the Java program. It is the entry point of the program.
     *
     * @param  args  the command line arguments passed to the program
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
