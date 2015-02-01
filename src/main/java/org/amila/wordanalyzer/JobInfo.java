package org.amila.wordanalyzer;

/**
 * Created by Amila on 10/19/2014.
 */
public class JobInfo {
    private String title;
    private int originalWords;
    private int distinctWords;
    private int masteredWords;
    private int commonWords;
    private int nonDictionaryWords;
    private int afterFilter;
    private int groups;
    private int progress;
    private String status;
    private int statusCode;

    @Override
    public String toString() {
        return "JobInfo{" +
                "title='" + title + '\'' +
                ", originalWords='" + originalWords + '\'' +
                ", distinctWords='" + distinctWords + '\'' +
                ", masteredWords='" + masteredWords + '\'' +
                ", commonWords='" + commonWords + '\'' +
                ", nonDictionaryWords='" + nonDictionaryWords + '\'' +
                ", afterFilter='" + afterFilter + '\'' +
                ", groups='" + groups + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOriginalWords() {
        return originalWords;
    }

    public void setOriginalWords(int originalWords) {
        this.originalWords = originalWords;
    }

    public int getDistinctWords() {
        return distinctWords;
    }

    public void setDistinctWords(int distinctWords) {
        this.distinctWords = distinctWords;
    }

    public int getMasteredWords() {
        return masteredWords;
    }

    public void setMasteredWords(int masteredWords) {
        this.masteredWords = masteredWords;
    }

    public int getCommonWords() {
        return commonWords;
    }

    public void setCommonWords(int commonWords) {
        this.commonWords = commonWords;
    }

    public int getNonDictionaryWords() {
        return nonDictionaryWords;
    }

    public void setNonDictionaryWords(int nonDictionaryWords) {
        this.nonDictionaryWords = nonDictionaryWords;
    }

    public int getAfterFilter() {
        return afterFilter;
    }

    public void setAfterFilter(int afterFilter) {
        this.afterFilter = afterFilter;
    }

    public int getGroups() {
        return groups;
    }

    public void setGroups(int groups) {
        this.groups = groups;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
