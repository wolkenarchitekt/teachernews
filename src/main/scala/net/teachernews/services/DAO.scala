package net.teachernews.services

import java.io.Serializable

import javax.persistence.{EntityManager,PersistenceContext}
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.metamodel.SingularAttribute;

import java.util.ArrayList

/**
 * A read-only DAO
 * @param T type of entities handled by this DAO
 * @param K type of the key that is used to identitfy entities
 *
 * @author Ingo Fischer
 * @version 1.0
 */
trait DAO[T, K <: Serializable] {
  @PersistenceContext
  var em:EntityManager = _
  
  lazy val cb:CriteriaBuilder = em.getCriteriaBuilder
  
  /**
   * Pair of SingularAttribute and corresponding value, to make querying
   * for multiple attributes possible
   */
  type AttributeValuePair[A] = Pair[SingularAttribute[T, A], A]

  /**
   * Persist entity
   * @param entity entity to persist
   */
  def persist(entity: T)
  
  /**
   * Update entity
   * @param entity
   */
  def update(entity: T):T
  
  /**
   * Delete entity matching this id
   * @param id of entity to remove
   * @return
   */
  def remove(entity: T)

  /**
   * Retrieve all entites known by that DAO
   * @return the list of all entities for that DAO
   */
  def findAll(): ArrayList[T]
  
  /**
   * Retrieve entities, filter by given attribute-value pairs.
   * Predicates are concatenated with AND
   * @param attributes
   * @return resultList 
   */
  def findBy[A](attributes:AttributeValuePair[_]*):ArrayList[T]
  
}


