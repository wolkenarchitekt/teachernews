package net.teachernews.ejb

import java.lang.Long
import java.util.ArrayList

import javax.annotation.security.RolesAllowed
import javax.annotation.security.DeclareRoles;
import javax.ejb.{Stateless, LocalBean}
import javax.inject.Inject
import javax.persistence.metamodel.SingularAttribute

import net.teachernews.model.Message
import net.teachernews.services.DAO

import org.slf4j.Logger

/**
 * DAO for messages
 * 
 * @author Ingo Fischer
 * @version 1.0
 */
@Stateless
@LocalBean
@DeclareRoles(Array("TEACHER", "DEANERY"))
class MessageEJB extends DAO[Message, Long] {
  @Inject @transient      				
  var log:Logger = _
  
  @RolesAllowed(Array("TEACHER", "DEANERY"))
  override def persist(m:Message) = em.persist(m)
  
  @RolesAllowed(Array("TEACHER", "DEANERY"))
  override def update(m:Message):Message = {
    em.merge(m)
  }
  
  @RolesAllowed(Array("TEACHER", "DEANERY"))
  override def remove(m:Message) = {
    em.remove(em.merge(m))
  }
  
  override def findAll:ArrayList[Message] = {
    val cq = cb.createQuery(classOf[Message])
    val userRoot = cq.from(classOf[Message])
    val results = em.createQuery(cq).getResultList
    results.asInstanceOf[ArrayList[Message]]
  }
  
  override type AttributeValuePair[T] = Pair[SingularAttribute[Message, T], T]
  
  override def findBy[T](attributes:AttributeValuePair[_]*):ArrayList[Message] = {
    val cq = cb.createQuery(classOf[Message])
    val queryRoot = cq.from(classOf[Message])
    var criteria = cb.conjunction
    for (pair <- attributes)
      criteria = cb.and(cb.equal(queryRoot.get(pair._1), pair._2 ))
    cq.where(Seq(criteria):_*)
    val results = em.createQuery(cq).getResultList
    results.asInstanceOf[ArrayList[Message]]
  }
}
