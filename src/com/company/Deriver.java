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


    //Accessors
    public int getMaxPrefixes() { return maxPrefixes; }
    public int getMinPrefixes() { return minPrefixes; }
    public void setMaxPrefixes(int morphs) { maxPrefixes = morphs; }
    public void setMinPrefixes(int morphs) { minPrefixes = morphs; }
    public int getMaxSuffixes() { return maxSuffixes; }
    public int getMinSuffixes() { return maxSuffixes; }
    public void setMaxSuffixes(int morphs) { maxSuffixes = morphs; }
    public void setMinSuffixes(int morphs) { maxSuffixes = morphs; }


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

        addMorphemesToBuilder(baseWord, minPrefixes, maxPrefixes, false);
        addMorphemesToBuilder(baseWord, minSuffixes, maxSuffixes, true);

        String word = sca.morphWord(baseWord.toString());

        return word;
    }


    //Helper methods

    private void addMorphemesToBuilder(StringBuilder builder, int minMorphs, int maxMorphs, boolean append) {
        int i;
        if (minMorphs == maxMorphs) {
            i = maxMorphs;
        } else {
            i = ThreadLocalRandom.current().nextInt(minMorphs, maxMorphs);
        }

        if (append) {
            for (; i > 0; i--) {
                builder.append(pickRandomFrom(suffixSet));
            }
        } else {
            for (; i > 0; i--) {
                builder.insert(0, pickRandomFrom(prefixSet));
            }
        }
    }

    private String pickRandomFrom(ArrayList<String> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }
}
