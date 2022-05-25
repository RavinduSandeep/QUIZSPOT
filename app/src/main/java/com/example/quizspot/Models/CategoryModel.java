package com.example.quizspot.Models;

public class CategoryModel {

    private String docID;
    private String name;
    private int noOftest;

    public CategoryModel(String docID, String name, int noOftest) {
        this.docID = docID;
        this.name = name;
        this.noOftest = noOftest;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNoOftest() {
        return noOftest;
    }

    public void setNoOftest(int noOftest) {
        this.noOftest = noOftest;
    }
}
