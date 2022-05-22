package com.ederbraz.magicapi.enums;

public enum Language {
    PT_BR("pt-br"), EN("en"), JP("jp");

    public final String label;

    Language(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
