package net.teachernews.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * StaticMetaModel for entity Subscription. Needed by JPA2 Criteria Query.
 * @see DAO.scala
 *
 * @author Ingo Fischer
 * @version 1.0
 */
@StaticMetamodel(Subscription.class)
public class Subscription_ {

    public static volatile SingularAttribute<Subscription, Long> id;
    public static volatile SingularAttribute<Subscription, User> subscriber;
    public static volatile SingularAttribute<Subscription, User> sender;
}
