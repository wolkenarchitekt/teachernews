package net.teachernews.model;

/**
 * The role of a user. Used for security.
 * 
 * @author Ingo Fischer
 * @version 1.0
 */
public enum RoleType {
  STUDENT("STUDENT"), TEACHER("TEACHER"), DEANERY("DEANERY");

  private final String label;

  private RoleType(String label) {
    this.label = label;
  }

  @Override
  public String toString() {
    return this.label;
  }
}
