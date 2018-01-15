package com.blogspot.fossipol.duendecat;

/**
 * Created by patarapolw on 1/15/18.
 */

public class Sentence {
    private int id;
    private String sentence;
    private String pinyin;
    private String english;
    private String note;
    private String valid;
    private String grammar;
    private String structure;
    private String resource;
    private String old_tag;
    private String grammar_lv;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public String getGrammar() {
        return grammar;
    }

    public void setGrammar(String grammar) {
        this.grammar = grammar;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getOld_tag() {
        return old_tag;
    }

    public void setOld_tag(String old_tag) {
        this.old_tag = old_tag;
    }

    public String getGrammar_lv() {
        return grammar_lv;
    }

    public void setGrammar_lv(String grammar_lv) {
        this.grammar_lv = grammar_lv;
    }
}
