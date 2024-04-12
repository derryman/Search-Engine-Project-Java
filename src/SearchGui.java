import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Objects;

/**
 * This class provides a graphical user interface to search for text within files.
 */

public class SearchGui implements ActionListener {
    //Swing Components
    private JFrame frame;
    private String searchedWord;
    private String selectedItem;

    private FilterGui filterGui;


    private JComboBox<String> dropdown;
    private String[] options = new String[Constants.MAX_SIZE]; // Options for the dropdown

    private JPanel contentPanel;
    private JPanel upPanel;
    private JPanel eastPanel;
    private JPanel midPanel;

    private JLabel searchLabel;
    private JLabel textFileLabel;
    private JLabel midTitle;
    private JTextArea resultsTextArea;

    private JTextField searchText;

    private JButton ok;
    private JButton reset;
    private JButton help;
    private JButton filter;
    private JButton about;
    private JButton darkModeToggle;

    private boolean darkMode = false;
    //Constructor initializes the GUI components and configures the frame.
    public SearchGui() {
        populateDropdown();// Populates the dropdown with file choices

        frame = new JFrame("Search Engine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT);

        contentPanel = new JPanel(new BorderLayout());
        frame.add(contentPanel);

        upPanel = new JPanel(new GridLayout(Constants.UP_ROW, Constants.UP_COL, Constants.H_GAP, Constants.V_GAP));
        upPanel.setBorder(new EmptyBorder(Constants.EMPTY_BORDER, Constants.EMPTY_BORDER, Constants.EMPTY_BORDER, Constants.EMPTY_BORDER));
        contentPanel.add(upPanel, BorderLayout.NORTH);

        searchLabel = new JLabel("Search:");
        searchText = new JTextField();
        textFileLabel = new JLabel("Text Files:");
        dropdown = new JComboBox<>(options);

        upPanel.add(searchLabel);
        upPanel.add(searchText);
        upPanel.add(textFileLabel);
        upPanel.add(dropdown);

        midPanel = new JPanel(new BorderLayout());
        midPanel.setBorder(new EmptyBorder(Constants.EMPTY_BORDER, Constants.EMPTY_BORDER, Constants.EMPTY_BORDER,Constants.EMPTY_BORDER));
        contentPanel.add(midPanel, BorderLayout.CENTER);

        midTitle = new JLabel("Results");
        midTitle.setFont(new Font("Arial", Font.BOLD, Constants.MID_FONT_SIZE));
        midPanel.add(midTitle, BorderLayout.NORTH);

        resultsTextArea = new JTextArea("No results for now");
        resultsTextArea.setRows(Constants.RESULT_AREA_ROW);
        resultsTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultsTextArea);
        midPanel.add(scrollPane, BorderLayout.CENTER);

        eastPanel = new JPanel(new GridLayout(Constants.EAST_ROW, Constants.EAST_COL, Constants.H_GAP, Constants.V_GAP));
        eastPanel.setBorder(new EmptyBorder(Constants.EMPTY_BORDER, Constants.EMPTY_BORDER, Constants.EMPTY_BORDER, Constants.EMPTY_BORDER));
        contentPanel.add(eastPanel, BorderLayout.EAST);

        ok = new JButton("OK");
        reset = new JButton("Reset");
        help = new JButton("Help");
        filter = new JButton("Filter");
        about = new JButton("About");
        darkModeToggle = new JButton("Dark Mode");
        // Register action listeners for buttons
        ok.addActionListener(this);
        reset.addActionListener(this);
        help.addActionListener(this);
        filter.addActionListener(this);
        about.addActionListener(this);
        darkModeToggle.addActionListener(this);
        // Set initial button colors
        setButtonColor(ok, Color.GREEN);
        setButtonColor(reset, Color.RED);
        setButtonColor(help, Color.WHITE);
        setButtonColor(filter, Color.WHITE);
        setButtonColor(about, Color.WHITE);
        setButtonColor(darkModeToggle, Color.GRAY);

        eastPanel.add(ok);
        eastPanel.add(reset);
        eastPanel.add(help);
        eastPanel.add(filter);
        eastPanel.add(about);
        eastPanel.add(darkModeToggle);

        frame.setVisible(true);

        setupShortcuts();// Configure keyboard shortcuts
    }
    //Handles actions performed on GUI components.
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "OK":
                if (filterGui == null) {
                    JOptionPane.showMessageDialog(frame, "Please set filters first.");
                    return; // Exit actionPerformed if filters are not set
                }
                resultsTextArea.setText("Searching...");
                searchedWord = searchText.getText();
                selectedItem = (String) dropdown.getSelectedItem();

                new Thread(() -> {
                    TextFileReader textFileReader = new TextFileReader(selectedItem, searchedWord,filterGui.getFilterItem());

                    if (Objects.equals(selectedItem, Constants.DIRECTORY_PATH)) {
                        textFileReader.readAll_TextFiles();
                    } else {
                        textFileReader.amendFilePath();
                        textFileReader.read_TextFile();
                    }

                    SwingUtilities.invokeLater(() -> {
                        resultsTextArea.setText(textFileReader.getResultText());
                    });
                }).start();
                break;

            case "Reset":
                resetGui();// Reset the GUI components to default state
                break;

            case "Filter":
                if (filterGui == null) {
                    filterGui = new FilterGui();// Initialize the filter GUI if not already done
                }
                break;


            case "About":// Display help information
                JOptionPane.showMessageDialog(frame, "This program searches for words and phrases in stored text files.\n Enter the chosen word/phrase into the search bar and press OK \nto run the program");
                break;

            case "Help":
                JOptionPane.showMessageDialog(frame, "No results found = there are no appearances of the searched word within the text files\nUse Ctrl + R/D/H/A/F for shortcuts");
                break;

            case "Dark Mode":
                toggleDarkMode(); // Toggle between dark and light themes
                break;

            default:
                break;
        }
    }
    //Toggles the UI between dark and light mode.
    private void toggleDarkMode() {
        darkMode = !darkMode;
        Color background = darkMode ? Color.DARK_GRAY : null;
        Color foreground = darkMode ? Color.WHITE : null;

        frame.getContentPane().setBackground(background);
        contentPanel.setBackground(background);
        upPanel.setBackground(background);
        eastPanel.setBackground(background);
        midPanel.setBackground(background);
        searchLabel.setForeground(foreground);
        textFileLabel.setForeground(foreground);
        midTitle.setForeground(foreground);
        resultsTextArea.setForeground(foreground);
        resultsTextArea.setBackground(background);
        darkModeToggle.setBackground(darkMode ? Color.LIGHT_GRAY : Color.GRAY);
    }
    // Set the color for buttons
    private void setButtonColor(JButton button, Color color) {
        button.setBackground(color);
        button.setOpaque(true);
    }
    // Populate the dropdown with file choices
    private void populateDropdown() {
        options[0] = Constants.DIRECTORY_PATH;
        for (int i = 2; i <= Constants.MAX_SIZE; i++) {
            options[i - 1] = i + ".txt";
        }
    }
    // Reset GUI components to their default states
    private void resetGui() {
        searchText.setText("");
        dropdown.setSelectedItem(Constants.DIRECTORY_PATH);
        resultsTextArea.setText("No results for now");
    }
    // Setup keyboard shortcuts for GUI operations
    private void setupShortcuts() {
        InputMap inputMap = contentPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = contentPanel.getActionMap();
        // Enter key to trigger search
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "searchPressed");
        actionMap.put("searchPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionPerformed(new ActionEvent(ok, ActionEvent.ACTION_PERFORMED, "OK"));
            }
        });

        // Ctrl + D for dark mode toggle
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "darkModeToggle");
        actionMap.put("darkModeToggle", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                darkModeToggle.doClick();
            }
        });

        // Ctrl + R for reset
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "reset");
        actionMap.put("reset", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset.doClick();
            }
        });

        // Ctrl + H for help
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "help");
        actionMap.put("help", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                help.doClick();
            }
        });

        // Ctrl + F for filter
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "filter");
        actionMap.put("filter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filter.doClick();
            }
        });

        // Ctrl + A for about
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "about");
        actionMap.put("about", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                about.doClick();
            }
        });
    }
}
