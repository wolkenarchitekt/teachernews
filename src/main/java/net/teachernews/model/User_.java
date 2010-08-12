package net.teachernews.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * StaticMetaModel for entity User. Needed by JPA2 Criteria Query.
 * 
 * @see DAO.scala
 * 
 * @author Ingo Fischer
 * @version 1.0
 */
@StaticMetamodel(User.class)
public class User_ {
  public static volatile SingularAttribute<User, Long> id;
  public static volatile SingularAttribute<User, String> email;
  public static volatile SingularAttribute<User, Title> title;
  public static volatile SingularAttribute<User, String> name;
  public static volatile SingularAttribute<User, String> firstName;
  public static volatile SingularAttribute<User, RoleType> role;
  public static volatile SingularAttribute<User, String> password;
}
