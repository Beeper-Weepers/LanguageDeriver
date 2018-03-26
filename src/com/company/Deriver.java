package com.company;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Deriver {
    ArrayList<String> rootSet;
    ArrayList<String> prefixSet;
    ArrayList<String> suffixSet;

    private int maxMorphs = 3;
    private int minMorphs = 1;

    public Deriver(ArrayList<String> roots, ArrayList<String> prefixes, ArrayList<String> suffixes) {
        rootSet = roots;
        prefixSet = prefixes;
        suffixSet = suffixes;
    }

    //Main function, creates a derived word based on loaded sets
    public String makeDerivedWord() {
        StringBuilder word = new StringBuilder(pickRandomFrom(rootSet));
        for (int i = ThreadLocalRandom.current().nextInt(minMorphs, maxMorphs); i >= 0; i--) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                word.insert(0, pickRandomFrom(prefixSet));
            } else {
                word.append(pickRandomFrom(suffixSet));
            }
        }
        return word.toString();
    }

    //Helper methods

    private String pickRandomFrom(ArrayList<String> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }
}
