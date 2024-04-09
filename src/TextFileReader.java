import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class TextFileReader
{
    private String filePath;
    private String textFilePath = "";
    private String word;
    private StringBuilder resultText = new StringBuilder();
    private ArrayList<WordCount> wordCounts = new ArrayList<>();


    public TextFileReader(String filePath, String word) {
        this.filePath = filePath;
        this.word = word;
    }

    public void amendFilePath() {
        textFilePath += Constants.DIRECTORY_PATH + "/" + filePath ; // Appending the new string to the existing filePath

    }

    public String getResultText() {
        return resultText.toString();
    }

    public void readAll_TextFiles() {
        // Creates a Path object representing the directory specified by directoryPath above
        Path directory = Paths.get(filePath);

        if (Files.exists(directory) && Files.isDirectory(directory)) {
            try
            {
                Files.walk(directory)
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".txt"))
                        .forEach(path -> {
                            WordCount wordCount = processFile(path);
                            assert wordCount != null;

                            if (wordCount.getCount() != 0) {
                                wordCounts.add(wordCount);
                            }
                        });

                // Hard coded atm (5), will be included in the constructor eventually in this class
                ResultsDisplay(5);


            }
            catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Directory does not exist or is not a directory.");
        }
    }

    // Reads just a single text file
    public void read_TextFile() {
        Path file = Paths.get(textFilePath);

        if (Files.exists(file) && Files.isRegularFile(file) && file.toString().endsWith(".txt")) {
            WordCount wordCount = processFile(file);
            assert wordCount != null;

            if (wordCount.getCount() != 0) {
                wordCounts.add(wordCount);
            }

            // Hard coded atm (5), will be included in the constructor eventually in this class
            ResultsDisplay(5);
        } else {
            System.err.println("File does not exist or is not a valid text file.");
        }
    }




    private WordCount processFile(Path filePath)
    {
        try
        {
            // Read the content of the file as a string
            String content = Files.readString(filePath);

            // Create a Scanner to tokenize the content
            Scanner scanner = new Scanner(content);
            int count = 0;

            // Loop through each token in the content
            while (scanner.hasNext())
            {
                String token = scanner.next();
                token = token.replaceAll("[^a-zA-Z]", ""); // Only keeps letters
                if (token.equals(word)) {count++;}
            }

            // Close the Scanner
            scanner.close();

            return new WordCount(filePath.getFileName().toString(), count);

        }

        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private void ResultsDisplay(int ResultsAmount)
    {
        // Sort the list based on the count in descending order
        wordCounts.sort(Comparator.comparingInt(WordCount::getCount).reversed());

        // Display the top x (ResultsAmount) entries
        int count = Math.min(ResultsAmount, wordCounts.size());


        if (wordCounts.isEmpty()) {
            resultText.append("No results found");
        } else {
            for (int i = 0; i < count; i++)
            {
                // retrieves the object stored at index i in the wordCounts ArrayList.
                // and assigns it to a variable called wordCount. Makes it easier to work with
                WordCount wordCount = wordCounts.get(i);
                resultText.append((i + 1) + ".) File: ").append(wordCount.getFileName()).append(", Count: ")
                        .append(wordCount.getCount()).append("\n");
            }
        }



    }

}
