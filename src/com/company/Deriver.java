package com.company;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Deriver {
    private SCA sca;

    private ArrayList<String> rootSet;
    private ArrayList<String> prefixSet;
    private ArrayList<String> suffixSet;

    private int maxPrefixes = 1;
    private int minPrefixes = 1;

    private int maxSuffixes = 1;
    private int minSuffixes = 1;

    String[] lastGeneratedParts;


    //Accessors and Setters
    public int getMaxPrefixes() { return maxPrefixes; }
    public int getMinPrefixes() { return minPrefixes; }
    public void setMaxPrefixes(int morphs) { maxPrefixes = morphs; }
    public void setMinPrefixes(int morphs) { minPrefixes = morphs; }
    public int getMaxSuffixes() { return maxSuffixes; }
    public int getMinSuffixes() { return maxSuffixes; }
    public void setMaxSuffixes(int morphs) { maxSuffixes = morphs; }
    public void setMinSuffixes(int morphs) { maxSuffixes = morphs; }
    public String[] getGeneratedParts() { return lastGeneratedParts; }


    //Constructor
    public Deriver(ArrayList<String> roots, ArrayList<String> prefixes, ArrayList<String> suffixes) {
        rootSet = roots;
        prefixSet = prefixes;
        suffixSet = suffixes;
    }

    public void linkSCA(SCA sca) {
        this.sca = sca;
    }

    //Main function, creates a derived word based on loaded sets
    public String makeDerivedWord() {
        StringBuilder baseWord = new StringBuilder(pickRandomFrom(rootSet));

        //Calculate amounts of pre and post morphemes
        int prefixes = getRandom(minPrefixes, maxPrefixes);
        int suffixes = getRandom(minSuffixes, maxSuffixes);

        //Create new array for wordMorphemesOut
        String[] wordMorphemesOut = new String[prefixes + suffixes + 1];
        int place = 0;
        wordMorphemesOut[wordMorphemesOut.length - (suffixes + 1)] = baseWord.toString();

        //Prefix adding
        for (; prefixes > 0; prefixes--) {
            String pre = pickRandomFrom(prefixSet);
            baseWord.insert(0, pre);
            wordMorphemesOut[place++] = pre;
        }

        place++; //Skip for base word

        //Suffix adding
        for (; suffixes > 0; suffixes--) {
            String suf = pickRandomFrom(suffixSet);
            baseWord.append(suf);
            wordMorphemesOut[place++] = suf;
        }

        //Convert from StringBuilder to String and run through the SCA
        String word = sca.morphWord(baseWord.toString());

        //wordMorphemesOut becomes lastGeneratedParts
        lastGeneratedParts = wordMorphemesOut;

        return word;
    }

    //Helper methods

    private int getRandom(int min, int max) {
        if (min == max) {
            return min;
        } else {
            return ThreadLocalRandom.current().nextInt(min, max);
        }
    }

    private String pickRandomFrom(ArrayList<String> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }
}
