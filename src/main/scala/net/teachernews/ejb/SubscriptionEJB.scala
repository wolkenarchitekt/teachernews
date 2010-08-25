package net.teachernews.ejb

import java.lang.Long
import java.util.ArrayList

import javax.annotation.security.DeclareRoles;
import javax.ejb.{ Stateless, LocalBean }

import javax.persistence.metamodel.SingularAttribute

import net.teachernews.model.Subscription
import net.teachernews.services.DAO

/**
 * DAO for users
 * 
 * @author Ingo Fischer
 * @version 1.0
 */
@Stateless
@LocalBean
class SubscriptionEJB extends DAO[Subscription] {
  
  override def findAll: ArrayList[Subscription] = {
    val cq = cb.createQuery(classOf[Subscription])
    val userRoot =
      cq.from(classOf[Subscription])
    val results = em.createQuery(cq).getResultList
    results.asInstanceOf[ArrayList[Subscription]]
  }

  override type AttributeValuePair[T] = Pair[SingularAttribute[Subscription, T], T]

  override def findBy[T](attributes: AttributeValuePair[_]*): ArrayList[Subscription] = {
    val cq = cb.createQuery(classOf[Subscription])
    val userRoot = cq.from(classOf[Subscription])
    var criteria = cb.conjunction
    for (pair <- attributes)
      criteria = cb.and(cb.equal(userRoot.get(pair._1), pair._2))
    cq.where(Seq(criteria): _*)
    val results = em.createQuery(cq).getResultList
    results.asInstanceOf[ArrayList[Subscription]]
  }
}
