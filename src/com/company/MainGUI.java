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
            fillResult = gui.userFileFillSets(rootSet, prefixSet, suffixSet);
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
    JLabel displayedWord; //Panel to display the results of the derivation process

    //Constructor
    public MainGUI() {
        //Set up mainPanel, layout and add mainPanel to the context pane
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
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

        //Setup for min and max morphemes format
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(10);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        Dimension morphBoxSize = new Dimension(64, 24);

        //Make a minimum morphemes text field
        JLabel minMorphLabel = new JLabel("Minimum Morphemes: ");
        minMorphLabel.setLabelFor(minPrefixes);
        minMorphLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        minPrefixes = new JFormattedTextField(formatter);
        minPrefixes.setMaximumSize(morphBoxSize);
        minPrefixes.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Make a maximum morphemes text field
        JLabel maxMorphLabel = new JLabel("Maximum Morphemes: ");
        maxMorphLabel.setLabelFor(minPrefixes);
        maxMorphLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        maxPrefixes = new JFormattedTextField(formatter);
        maxPrefixes.setMaximumSize(morphBoxSize);
        maxPrefixes.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Layout
        mainPanel.add(loadedText);
        mainPanel.add(minMorphLabel);
        mainPanel.add(minPrefixes);
        mainPanel.add(maxMorphLabel);
        mainPanel.add(maxPrefixes);
        mainPanel.add(generateButton);
        mainPanel.add(Box.createVerticalStrut(64));
        mainPanel.add(displayedWord);


        //Set up window
        setTitle("Language Deriver");
        setSize(640, 480); //In pixels
        setLocationRelativeTo(null); //Centers window
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //Tell application to close on window close
        setVisible(true);
    }


    //Give the MainGUI instance a deriver object
    public void linkDeriver(Deriver deriver) {
        this.deriver = deriver;
        loadedText.setText("Morphology template loaded from " + lastFileLoaded);

        minPrefixes.setText(String.valueOf(deriver.getMinPrefixes()));
        maxPrefixes.setText(String.valueOf(deriver.getMaxPrefixes()));
    }


    //Evaluate actions
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == generateButton && deriver != null) {
            updateDeriver();
            displayedWord.setText(deriver.makeDerivedWord());
        }
    }

    private void updateDeriver() {
        deriver.setMinPrefixes(Integer.valueOf(minPrefixes.getText()));
        deriver.setMaxPrefixes(Integer.valueOf(maxPrefixes.getText()));
    }

    //Categories for set loading
    enum Category {
        none,
        root,
        prefix,
        suffix
    }

    //Returns a error code (0 for no errors, 1 for errors or problems)
    public int userFileFillSets(ArrayList<String> rootSet, ArrayList<String> preSet, ArrayList<String> suffixSet) {
        //Get a user file for a vocabulary
        File file = askUserFile();

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
                        System.out.println("Root: " + word);
                        break;
                    case prefix:
                        preSet.add(word);
                        System.out.println("Prefix: " + word);
                        break;
                    case suffix:
                        suffixSet.add(word);
                        System.out.println("Suffix: " + word);
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
