package net.teachernews.model

import java.util.Date

import javax.persistence.{Entity, Id, Lob, GeneratedValue, ManyToOne, Temporal, TemporalType}
import javax.validation.constraints.NotNull

import scala.reflect.BeanProperty

/**
 * A message which is sent by a teacher to all subscribers
 * 
 * @author Ingo Fischer
 * @version 1.0
 */
@Entity @serializable
class Message extends EntityBase {
  @Id @GeneratedValue @BeanProperty
  var id: Long = _
  
  @NotNull @BeanProperty
  @Lob
  var content: String = _
  
  @NotNull @BeanProperty
  @Temporal(TemporalType.TIMESTAMP)
  var expirationDate: Date = _
  
  @NotNull @BeanProperty
  @ManyToOne
  var regards: User = _
  
  override def toString = 
    classOf[Message].getName + 
    " id=[" + id + "]" +  
    " content=[" + content  + "]" + 
    " expirationDate=[" + expirationDate + "]" + 
    " regards=[" + regards + "]" + 
    " version=[" + version + "]"
    
  override def equals(other:Any):Boolean = 
    other match {
      case that: Message => id == that.id
      case _ => false
    }
}