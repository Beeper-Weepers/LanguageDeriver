package com.company;

public class TestMain {
    public static void main(String[] args) {
        SCA sca = new SCA();

        int res = sca.addVariable("C=nmt");
        if (res != 0) {
            System.out.println("Add variable aborted with error code " + res);
            return;
        }

        res = sca.addRule("a/gre/C_");
        if (res != 0) {
            System.out.println("Add rule aborted with error code " + res);
            return;
        }

        String morph = sca.morphWord("map");

        System.out.println(morph);
    }
}
