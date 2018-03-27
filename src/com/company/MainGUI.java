package com.company;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class MainGUI extends JFrame implements ActionListener {

    //Main entry point
    public static void main(String[] args) {
        //Create a MainGUI object to use as the GUI and input gatherer
        MainGUI gui = new MainGUI();


        //Fill sets using user input
        ArrayList<String> rootSet = new ArrayList<>();
        ArrayList<String> prefixSet = new ArrayList<>();
        ArrayList<String> suffixSet = new ArrayList<>();

        int fillResult;
        do {
            File vocabFile = gui.askUserFile();
            fillResult = gui.fillSets(vocabFile, rootSet, prefixSet, suffixSet);
        } while ( fillResult == 1 );

        //Create a deriver object and give it to the MainGUI
        gui.linkDeriver(new Deriver(rootSet, prefixSet, suffixSet));

        //After this, leave it up to the MainGUI gui instance!
    }


    //Fields
    Deriver deriver = null;
    String lastFileLoaded;

    //Component Fields
    JPanel mainPanel; //Overarching mainPanel
    JButton generateButton; //Button to generate a word
    JLabel loadedText; //Text displaying status of loading
    JFormattedTextField minPrefixes;
    JFormattedTextField maxPrefixes;
    JFormattedTextField minSuffixes;
    JFormattedTextField maxSuffixes;
    JLabel displayedWord; //Label to display the results of the derivation process
    JLabel errorMessage; //Label to display possible errors
    JTextArea variableArea;
    JTextArea rulesArea;

    //Constructor
    public MainGUI() {
        //Set up mainPanel, layout and add mainPanel to the context pane
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        setLayout(new FlowLayout());
        this.getContentPane().add(mainPanel);

        //Add components

        //Make a text label on the status of loading
        loadedText = new JLabel("No morphology template loaded");
        loadedText.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Make a "generate" button
        generateButton = new JButton("Generate");
        generateButton.addActionListener(this); //Hook up to actionPerformed()
        generateButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Make a label to display results on
        displayedWord = new JLabel();
        displayedWord.setAlignmentX(Component.CENTER_ALIGNMENT);
        displayedWord.setFont(new Font("Serif", Font.PLAIN, 24));

        //Make label to display errors on
        errorMessage = new JLabel();
        errorMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorMessage.setForeground(Color.red);

        //Setup for text boxes
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(10);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);

        //Make a minimum prefixes text field
        JPanel minPrefixPanel = new JPanel();
        minPrefixes = createLabelJFT(minPrefixPanel, formatter, "Minimum Prefixes: ");

        //Make a maximum prefixes text field
        JPanel maxPrefixPanel = new JPanel();
        maxPrefixes = createLabelJFT(maxPrefixPanel, formatter, "Maximum Prefixes: ");

        //Make a minimum suffixes text field
        JPanel minSuffixPanel = new JPanel();
        minSuffixes = createLabelJFT(minSuffixPanel, formatter, "Minimum Suffixes: ");

        //Make a maximum suffixes text field
        JPanel maxSuffixPanel = new JPanel();
        maxSuffixes = createLabelJFT(maxSuffixPanel, formatter, "Maximum Suffixes: ");

        //Make a SCA variable text area
        JPanel varPanel = new JPanel();
        variableArea = createLabelJTA(varPanel, "Remapping variables: ");

        //Make a SCA rule text area
        JPanel rulePanel = new JPanel();
        rulesArea = createLabelJTA(rulePanel, "Remapping rules: ");

        //Layout
        mainPanel.add(loadedText); //Status text
        mainPanel.add(errorMessage); //Error message result

        mainPanel.add(minPrefixPanel); //Minimum Prefix
        mainPanel.add(maxPrefixPanel); //Maximum Prefix

        mainPanel.add(minSuffixPanel); //Maximum Prefix
        mainPanel.add(maxSuffixPanel); //Minimum Prefix

        mainPanel.add(varPanel); //SCA User variables
        mainPanel.add(rulePanel); //SCA User rules

        mainPanel.add(generateButton); //Button to generate words
        mainPanel.add(Box.createVerticalStrut(64));
        mainPanel.add(displayedWord);

        //Set up window
        setTitle("Language Deriver");
        setSize(640, 480); //In pixels
        setLocationRelativeTo(null); //Centers window
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //Tell application to close on window close
        setVisible(true);
    }


    Dimension labelJFTDimension = new Dimension(64, 24);

    public JFormattedTextField createLabelJFT(JPanel labelJFTPanel, NumberFormatter formatter, String text) {
        labelJFTPanel.setLayout(new FlowLayout());

        //Make the formatted text field
        JFormattedTextField textField = new JFormattedTextField(formatter);
        textField.setPreferredSize(labelJFTDimension);
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Create the label
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setLabelFor(textField);

        labelJFTPanel.add(label);
        labelJFTPanel.add(textField);

        return textField;
    }

    Dimension labelJTADimension = new Dimension(256, 64);

    public JTextArea createLabelJTA(JPanel labelJTAPanel, String text) {
        labelJTAPanel.setLayout(new FlowLayout());

        //Make the formatted text field
        JTextArea textField = new JTextArea();
        textField.setPreferredSize(labelJTADimension);
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Create the label
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setLabelFor(textField);

        //Add labels to the panel
        labelJTAPanel.add(label);
        labelJTAPanel.add(textField);

        return textField;
    }

    //Give the MainGUI instance a deriver object
    public void linkDeriver(Deriver deriver) {
        this.deriver = deriver;
        loadedText.setText("Morphology template loaded from " + lastFileLoaded);

        minPrefixes.setText(String.valueOf(deriver.getMinPrefixes()));
        maxPrefixes.setText(String.valueOf(deriver.getMaxPrefixes()));

        minSuffixes.setText(String.valueOf(deriver.getMinSuffixes()));
        maxSuffixes.setText(String.valueOf(deriver.getMaxSuffixes()));
    }


    //Evaluate actions
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == generateButton && deriver != null) {
            if (updateDeriver()) {
                return; //If error, exit
            }
            displayedWord.setText(deriver.makeDerivedWord());
        }
    }

    //Return true if error
    private boolean updateDeriver() {
        int minPer = Integer.valueOf(minPrefixes.getText());
        int maxPer = Integer.valueOf(maxPrefixes.getText());
        int minSuf = Integer.valueOf(minSuffixes.getText());
        int maxSuf = Integer.valueOf(maxSuffixes.getText());

        if (minPer > maxPer) {
            errorMessage.setText("Maximum prefix smaller than minimum prefix");
            return true;
        }

        if (minSuf > maxSuf) {
            errorMessage.setText("Minimum suffix smaller than minimum suffix");
            return true;
        }

        deriver.setMinPrefixes(minPer);
        deriver.setMaxPrefixes(maxPer);
        deriver.setMinSuffixes(minSuf);
        deriver.setMaxSuffixes(maxSuf);

        //Derivation update complete with no errors, reset error message
        errorMessage.setText("");
        return false;
    }


    //Categories for set loading
    enum Category {
        none,
        root,
        prefix,
        suffix
    }

    //Returns a error code (0 for no errors, 1 for errors or problems)
    public int fillSets(File file, ArrayList<String> rootSet, ArrayList<String> preSet, ArrayList<String> suffixSet) {
        //If cancelled or other issues
        if (file == null) {
            return 1;
        }

        try {
            //Throw the user file in a Scanner object
            Scanner vocabScanner = new Scanner(file);

            Category mode = Category.none;

            //Read file one word per line, add to lists based off of ">"s - For your own formats, change this
            while (vocabScanner.hasNextLine()) {
                String word = vocabScanner.nextLine().trim().toLowerCase();

                //Mode changers
                if (word.equals(">roots")) {
                    mode = Category.root;
                    continue;
                } else if (word.equals(">prefixes")) {
                    mode = Category.prefix;
                    continue;
                } else if (word.equals(">suffixes")) {
                    mode = Category.suffix;
                    continue;
                }
                //If word is empty, do not put into a set
                else if (word.isEmpty()) {
                    continue;
                }

                //Mode based placement
                switch (mode) {
                    case root:
                        rootSet.add(word);
                        break;
                    case prefix:
                        preSet.add(word);
                        break;
                    case suffix:
                        suffixSet.add(word);
                        break;
                    default:
                }
            }

            vocabScanner.close();
            lastFileLoaded = file.getName();
            return 0; //Error free
        } catch (FileNotFoundException ex) {
            return 1; //hit errors
        }
    }

    //Gets a file from the user using a JFileChooser dialog
    public File askUserFile() {
        final JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Text Files", "txt", "text"));
        fc.setDialogTitle("Select a root/morpheme file");
        int chooserReturn = fc.showOpenDialog(MainGUI.this);

        if (chooserReturn == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        } else {
            return null;
        }
    }
}
