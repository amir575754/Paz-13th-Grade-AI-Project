package com.apps.paz.bullseye;

public class GameResult { //An object that is used to keep track of a grade for a certain guess.
    private int bangOn;
    private int hits;

    public GameResult() {
        this.bangOn = 0;
        this.hits = 0;
    }

    public GameResult(int bangOn, int hits) {
        this.bangOn = bangOn;
        this.hits = hits;
    }

    public int getBangOn() {
        return bangOn;
    }

    public void setBangOn(int bangOn) {
        this.bangOn = bangOn;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public void addBangOn() {
        this.bangOn++;
    }

    public void addHits() {
        this.hits++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameResult)) return false;
        GameResult that = (GameResult) o;
        return getBangOn() == that.getBangOn() &&
                getHits() == that.getHits();
    }

    @Override
    public String toString() {
        return "BangOn = " + this.bangOn + ", hits = " + this.hits;
    }
}
