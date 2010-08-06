package net.teachernews.model;

/**
 * The title of a user
 * 
 * @author Ingo Fischer
 * @version 1.0
 */
public enum Title {

    MR("Mr"), MS("Ms");

    private final String title;

    private Title(String title) {
        this.title = title;
    }

    public String toString() {
        return this.title;
    }
}
