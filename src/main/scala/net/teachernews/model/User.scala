package net.teachernews.model

import javax.persistence.{ Column, Entity, EnumType, GeneratedValue, Id, PrePersist, Table }
import javax.validation.constraints.NotNull
import javax.persistence.Enumerated;

import org.hibernate.validator.constraints.{ Email, Length }

import scala.reflect.BeanProperty
import scala.collection.mutable

import net.teachernews.services.Application.md5hash

/**
 * A user
 *
 * @author Ingo Fischer
 * @version 1.0
 */
@Entity
@serializable // User is a reserved keyword in Apache Derby, rename table
@Table(name = "tnuser")
class User extends EntityBase {
  @Id
  @GeneratedValue
  @BeanProperty
  var id: Long = _

  @NotNull
  @BeanProperty
  @Email
  @Column(unique = true)
  var email: String = _

  @NotNull
  @BeanProperty
  @Enumerated(EnumType.STRING)
  var title: Title = _

  @NotNull
  @BeanProperty
  var name: String = _

  @NotNull
  @BeanProperty
  var firstName: String = _

  @NotNull
  @BeanProperty
  @Enumerated(EnumType.STRING)
  var role: RoleType = _

  @NotNull
  @BeanProperty
  @Length(min = 8)
  var password: String = _

  @PrePersist
  private def hashPassword: Unit =
    password = md5hash(password)

  override def toString =
    classOf[User].getName +
      " id=[" + id + "]" +
      " email=[" + email + "]" +
      " name=[" + name + "]" +
      " firstName=[" + firstName + "]" +
      " role=[" + role + "]" +
      " version=[" + version + "]"

  override def equals(other: Any): Boolean =
    other match {
      case that: User => id == that.id
      case _ => false
    }
}