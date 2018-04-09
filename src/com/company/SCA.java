package com.company;

import java.util.ArrayList;
import java.util.HashMap;

public class SCA {
    private final String seper = "/"; //Separator indicator
    private final char placer = '#'; //Position indicator
    private final char target = '_'; //Target reference indicator in the environment
    private final char varEquals = '=';

    //This system requires that rules be processed after variables
    private HashMap<String, StringBuilder> userVarsMap;
    private ArrayList<String> userRulesPattern;
    private ArrayList<String> userRulesMatch;

    //Constructor
    SCA() {
        userRulesPattern = new ArrayList<>();
        userRulesMatch = new ArrayList<>();
        userVarsMap = new HashMap<>();
    }

    //Main public interaction method
    public String morphWord(String baseWord) {
        String word = baseWord;

        for (int i = 0; i < userRulesPattern.size(); i++) {
            word = word.replaceAll(userRulesPattern.get(i), "$1" + userRulesMatch.get(i) + "$2");
        }

        return word;
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
        userVarsMap.clear();

        if (varsStr.isEmpty()) { return 0; }

        String[] rulesA = varsStr.split("\n");

        for (String rule : rulesA) {
            int varRes = addVariable(rule);
            if (varRes != 0) {
                userVarsMap.clear();
                return varRes;
            }
        }

        return 0;
    }

    private int addVariable(String varStr) {
        varStr = varStr.replaceAll(" ", "");

        int sepPoint = varStr.indexOf(varEquals);
        //Variable must be only one character long
        if (sepPoint == 1) {
            String varName = varStr.substring(0, sepPoint);
            StringBuilder variableConstituents = new StringBuilder(varStr.substring(sepPoint + 1));
            variableConstituents.append("]"); variableConstituents.insert(0, "[");
            userVarsMap.put(varName, variableConstituents);
        } else {
            return 1;
        }

        return 0;
    }

    public int setRules(String rulesStr) {
        userRulesPattern.clear();
        userRulesMatch.clear();

        if (rulesStr.isEmpty()) { return 0; }

        String[] rulesA = rulesStr.split("\n");
        for (String rule : rulesA) {
            int rulRes = addRule(rule);
            if (rulRes != 0) {
                userRulesPattern.clear();
                userRulesMatch.clear();
                return rulRes;
            }
        }

        return 0;
    }

    private int addRule(String ruleStr) {
        ruleStr = ruleStr.replaceAll(" ", "");

        String[] ruleA = ruleStr.split(seper);

        //Return an error if the length is not correct
        if (ruleA.length != 3) {
            return 1;
        }

        //Return an error if an identifier is not present
        if (countOccurrences(ruleA[2], target) != 1) {
            return 2;
        }

        //Return an error if too many starters or enders are present
        int startCount = countOccurrences(ruleA[2], placer);
        if (startCount > 1) {
            return 3;
        }


        //Construct regex expression (both halves) based on this rule


        StringBuilder ruleMatch = new StringBuilder();
        StringBuilder ruleReplace = new StringBuilder();

        //Generate regex match pattern from environment
        int targetPos = -1; //Position of target character in the environment string
        for (int i = 0; i < ruleA[2].length(); i++) {
            char c = ruleA[2].charAt(i);
            StringBuilder userVarCons = userVarsMap.get(String.valueOf(c)); //User variable constituents
            //Appending the 'variable' to the match string
            if (userVarCons != null) {
                ruleMatch.append(userVarCons);
            } else if (c == target) {
                targetPos = ruleMatch.length();
            }
            //Replaces the "placer" char with a regex equivalent
            else if (c == placer) {
                //If before target
                if (targetPos == -1) {
                    ruleMatch.append('^');
                }
                //If after target
                else {
                    ruleMatch.append('$');
                }
            } else {
                ruleMatch.append(c);
            }
        }

        //Build target regex expression
        StringBuilder ruleTarget = new StringBuilder();
        for (int i = 0; i < ruleA[0].length(); i++) {
            char c = ruleA[0].charAt(i);
            //Replace variables with variable constituents
            StringBuilder userVarCons = userVarsMap.get(String.valueOf(c)); //User variable constituents
            if (userVarCons != null) {
                ruleTarget.append(userVarCons);
            } else {
                ruleTarget.append(c);
            }
        }

        //Create capture group facing outside
        ruleTarget.insert(0, ')');
        ruleTarget.append('(');

        //Insert target regex into environment regex to create a complete match regex
        ruleMatch.insert(targetPos, ruleTarget);

        //Create capture group from inside the rule match
        ruleMatch.insert(0, '(');
        ruleMatch.append(')');

        //Generate replace regex
        ruleReplace = new StringBuilder(ruleA[1]);

        //System.out.println(ruleMatch.toString());
        //System.out.println(ruleReplace.toString());

        //Add regex strings to lists
        userRulesPattern.add(ruleMatch.toString());
        userRulesMatch.add(ruleReplace.toString());

        return 0;
    }

    //Helpers

    /*private static boolean arrayContains(ArrayList<String> a, String t) {
        for (String item : a) {
            if (item.equals(t)) { return true; }
        }
        return false;
    }

    private static String stringDelete(String str, int pos) {
        return str.substring(0, pos) + str.substring(pos + 1, str.length() - 1);
    }*/

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
