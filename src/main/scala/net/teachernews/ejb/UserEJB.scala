package net.teachernews.ejb

import scala.collection.JavaConversions._

import java.lang.Long
import java.util.ArrayList

import javax.annotation.security.{ DeclareRoles, RolesAllowed }
import javax.ejb.{ Stateless, LocalBean }
import javax.persistence.metamodel.SingularAttribute

import net.teachernews.model.User
import net.teachernews.services.DAO

/**
 * DAO for users
 * 
 * @author Ingo Fischer
 * @version 1.0
 */
@Stateless
@LocalBean
@DeclareRoles(Array("DEANERY"))
class UserEJB extends DAO[User, Long] {

  @RolesAllowed(Array("DEANERY"))
  override def remove(u: User) = em.remove(em.merge(u)) 

  override def findAll: ArrayList[User] = {
    val cq = cb.createQuery(classOf[User])
    val userRoot =
      cq.from(classOf[User])
    val results = em.createQuery(cq).getResultList
    results.asInstanceOf[ArrayList[User]]
  }

  override type AttributeValuePair[T] = Pair[SingularAttribute[User, T], T]

  override def findBy[T](attributes: AttributeValuePair[_]*): ArrayList[User] = {
    val cq = cb.createQuery(classOf[User])
    val queryRoot = cq.from(classOf[User])
    var criteria = cb.conjunction
    for (pair <- attributes) {
      criteria = cb.and(cb.equal(queryRoot.get(pair._1), pair._2))
    }
    cq.where(Seq(criteria): _*)
    val results = em.createQuery(cq).getResultList
    results.asInstanceOf[ArrayList[User]]
  }
}
