package org.amila.wordanalyzer.beans;

/**
 * Created by Amila on 11/6/2014.
 */
public class WordBean {
    long id;
    String word;
    long frequency;
    String partsOfSpeech;
    int rank = 10000;
    int rank100k = 1000000;
    String variations;
    String remarks = "";
    int senses;
    int size;
    String stem;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public long getFrequency() {
        return frequency;
    }

    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    public String getPartsOfSpeech() {
        return partsOfSpeech;
    }

    public void setPartsOfSpeech(String partsOfSpeech) {
        this.partsOfSpeech = partsOfSpeech;
    }

    public long getId() {

        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getVariations() {
        return variations;
    }

    public void setVariations(String variations) {
        this.variations = variations;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public int getRank100k() {
        return rank100k;
    }

    public void setRank100k(int rank100k) {
        this.rank100k = rank100k;
    }

    public int getSenses() {
        return senses;
    }

    public void setSenses(int senses) {
        this.senses = senses;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
