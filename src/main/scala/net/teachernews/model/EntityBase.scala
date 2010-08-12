package net.teachernews.model

import javax.persistence.{ MappedSuperclass, Version }

/**
 * Superclass for common Entity attributes
 *
 * @author Ingo Fischer
 * @version 1.0
 */
@MappedSuperclass
abstract class EntityBase {
  @Version
  var version: Long = _

  /**
   * In Hibernate, When ID is inherited from superclass, MetaModel doesn't recognize the
   * ID from superclass. See: http://opensource.atlassian.com/projects/hibernate/browse/HHH-5024
   * Therefore subclassing entities cannot be searched for the ID with the MetaModel. 
   * Since the DAOS in teachernews built heavily on the MetaModel, the id 
   * in the EntityBase is commented out and implemented in every Entity 
   *
   * @Id @GeneratedValue @BeanProperty
   * protected var id: Long = _
   */

  def equals(other: Any): Boolean
}