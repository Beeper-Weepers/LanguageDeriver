package com.company;

import java.util.ArrayList;
import java.util.HashMap;

public class SCA {
    private final char seper = '/'; //Separator indicator
    private final char placer = '#'; //Position indicator
    private final char target = '_'; //Target reference indicator in the environment
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
        for (int i = 0; i < userRules.size(); i++) {
            String[] rule = userRules.get(i);

            String ruleLoc = rule[2];
            int loopType = 0; //How to loop through the string. 0 - normal (pass-through), 1 - start, 2 - end
            int targetLocation = -1; //Location of target in the rule (the default is nowhere)
            //Collect loopType, targetLocation and modify rule for simplification
            for (int j = 0; j < rule[2].length(); i++) {
                char c = rule[2].charAt(j);
                if (c == placer) {
                    //Set the loop type based on where targetLocation has been set yet
                    if (targetLocation == -1) {
                        loopType = 1;
                    } else {
                        loopType = 2;
                    }
                    //Delete the placer part in the ruleLoc string
                    stringDelete(ruleLoc, j--);
                } else if (c == target) {
                    targetLocation = j;
                }
            }

            //TODO: Check each part of word for match
            //Calculate widths on either side of target
            int lL = targetLocation;
            int rL = word.length() - (targetLocation + 1);

            int j = lL;
            int range = word.length() - rL;
            int dir = 1;
            if (loopType == 1) {
                range = Math.min(1, range);
            } else if (loopType == 2) {

            }
            for (; j < range; j += dir) {

            }

            //TODO: Replace with replacement
        }

        return word.toString();
    }


    /*Return int for error code
        0 - normal
        Rules:
            1 - incorrect quantity of separators
            2 - No target present
            3 - Too many positional informants
        Variables:
            1 - Variable name longer than one character
    */

    //Adding functions

    public int setVariables(String varsStr) {
        userVariables.clear();
        userVarsMap.clear();

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

    public int setRules(String rulesStr) {
        userRules.clear();

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
        ruleStr = ruleStr.replaceAll(" ", "");

        String[] ruleA = ruleStr.split("/");

        //Return an error if the length is not correct or an identifier is not present
        if (ruleA.length != 3 || countOccurrences(ruleA[2], target) != 1) {
            return 1;
        }

        //Return an error if too many starters or enders are present
        int startCount = countOccurrences(ruleA[2], placer);
        if (startCount > 1) {
            return 2;
        }

        userRules.add(ruleA);
        return 0;
    }

    private static String stringDelete(String str, int pos) {
        return str.substring(0, pos) + str.substring(pos + 1, str.length() - 1);
    }

    private static int countOccurrences(String haystack, char needle) {
        int count = 0;
        for (int i = 0; i < haystack.length(); i++) {
            if (haystack.charAt(i) == needle) {
                count++;
            }
        }
        return count;
    }
}
