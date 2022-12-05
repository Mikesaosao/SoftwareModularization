package com.nju.bysj.softwaremodularisation.evolution.entity;

import java.util.ArrayList;
import java.util.Set;

public class Commit {
    private String fileName;
    private Set<String> commits;
    private Set<String> developers;

    public Commit(String fileName, Set<String> commits, Set<String> developers) {
        this.fileName = fileName;
        this.commits = commits;
        this.developers = developers;
    }

    public void addCommit(String commitID) {
        this.commits.add(commitID);
    }

    public void addDevelopers(String developer) {
        this.developers.add(developer);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Set<String> getCommits() {
        return commits;
    }

    public void setCommits(Set<String> commits) {
        this.commits = commits;
    }

    public Set<String> getDevelopers() {
        return developers;
    }

    public void setDevelopers(Set<String> developers) {
        this.developers = developers;
    }
}
