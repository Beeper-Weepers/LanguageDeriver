package com.company;

import java.util.ArrayList;

public class SCA {
    private final char seper = '/'; //Separator indicator
    private final char starter = '('; //Start line indicator
    private final char ender = ')'; //End line indicator

    private ArrayList<String[]> userVariables;
    private ArrayList<String[]> userRules;


    public SCA() {
        userVariables = new ArrayList<>();
    }

    public String morphWord(String baseWord) {
        //TODO: Apply rules

        return baseWord;
    }

    /*Return int for error code
        0 - normal
        1 - incorrect quantity of separators
    */

    //Adding functions

    public int addVariables(String varsStr) {
        String[] rulesA = varsStr.split("\n");
        for (int i = 0; i < rulesA.length; i++) {
            int varRes = addVariable(rulesA[i]);
            if (varRes != 0) {
                userVariables.clear();
                return varRes;
            }
        }

        return 0;
    }

    public int addVariable(String varStr) {
        varStr = varStr.trim();

        String[] varA = varStr.split("/");

        if (varA.length != 3) {
            return 1;
        }

        userVariables.add(varA);
        return 0;
    }

    public int addRules(String rulesStr) {
        String[] rulesA = rulesStr.split("\n");
        for (int i = 0; i < rulesA.length; i++) {
            int rulRes = addRule(rulesA[i]);
            if (rulRes != 0) {
                userRules.clear();
                return rulRes;
            }
        }

        return 0;
    }

    public int addRule(String ruleStr) {
        ruleStr = ruleStr.trim();

        String[] ruleA = ruleStr.split("/");

        if (ruleA.length != 3) {
            return 1;
        }

        userVariables.add(ruleA);
        return 0;
    }
}
