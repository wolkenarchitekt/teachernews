package net.teachernews.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import java.util.Date;

/**
 * StaticMetaModel for entity Message. Needed by JPA2 Criteria Query.
 * @see DAO.scala
 *
 * @author Ingo Fischer
 * @version 1.0
 */
@StaticMetamodel(Message.class)
public class Message_ {

    public static volatile SingularAttribute<Message, Long> id;
    public static volatile SingularAttribute<Message, String> content;
    public static volatile SingularAttribute<Message, Date> expirationDate;
    public static volatile SingularAttribute<Message, User> regards;
}
