package net.teachernews.model

import javax.persistence.{Entity, Id, GeneratedValue, ManyToOne}
import javax.validation.constraints.NotNull
import scala.reflect.BeanProperty

/**
 * Object describing relation between subscribers and senders 
 * 
 * @author Ingo Fischer
 * @version 1.0
 */
@Entity @serializable
class Subscription extends EntityBase {
  @Id @GeneratedValue @BeanProperty
  var id: Long = _

  @NotNull @BeanProperty
  @ManyToOne
  var subscriber: User = _
  
  @NotNull @BeanProperty
  @ManyToOne
  var sender: User = _

  override def toString = 
    classOf[Subscription].getName
    " subscriber=[" + subscriber + "]" 
    " sender=[" + sender + "]"
    
  override def equals(other:Any):Boolean = 
    other match {
      case that: Subscription => id == that.id
      case _ => false
    }
}