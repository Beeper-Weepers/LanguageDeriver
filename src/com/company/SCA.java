package com.company;

import java.util.ArrayList;
import java.util.HashMap;

public class SCA {
    private final char seper = '/'; //Separator indicator
    private final char starter = '('; //Start line indicator
    private final char ender = ')'; //End line indicator
    private final char varEquals = '=';

    private ArrayList<String> userVariables;
    private HashMap<String, char[]> userVarsMap;
    private ArrayList<String[]> userRules;


    public SCA() {
        userVariables = new ArrayList<>();
        userRules = new ArrayList<>();
        userVarsMap = new HashMap<>();
    }

    public String morphWord(String baseWord) {
        StringBuilder word = new StringBuilder(baseWord);

        //TODO: Apply rules

        return word.toString();
    }

    /*Return int for error code
        0 - normal
        Rules:
            1 - incorrect quantity of separators
        Variables:
            1 - Variable name longer than one character
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
        varStr = varStr.replaceAll(" ", "");

        int sepPoint = varStr.indexOf(varEquals);
        //Variable must be only one character long
        if (sepPoint == 1) {
            String varName = varStr.substring(0, sepPoint);
            char[] variableConstituents = varStr.substring(sepPoint + 1).toCharArray();
            userVariables.add(varName);
            userVarsMap.put(varName, variableConstituents);
        } else {
            return 1;
        }

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

        userRules.add(ruleA);
        return 0;
    }
}
