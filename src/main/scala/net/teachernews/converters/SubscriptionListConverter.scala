package net.teachernews.converters

import scala.collection.JavaConversions._

import javax.faces.convert.FacesConverter
import javax.faces.context.FacesContext
import javax.faces.component.UIComponent
import javax.faces.convert.Converter
import javax.naming.{ Context, InitialContext }

import net.teachernews.model.{ User, User_ }
import net.teachernews.ejb.UserEJB

/**
 * Convert users forward and backward to be used in Picklist
 * @see /user/subscriptions.xhtml
 *
 * @author Ingo Fischer
 * @version 1.0
 */
@FacesConverter(value = "subscriptionListConverter")
class SubscriptionListConverter extends Converter {
  val ctx: Context = new InitialContext
  val userEJB = ctx.lookup("java:global/teachernews/UserEJB").asInstanceOf[UserEJB]

  def getAsObject(ctx: FacesContext, comp: UIComponent, value: String): Object = {
    val id: java.lang.Long = java.lang.Long.parseLong(value)
    userEJB.findBy(User_.id -> id).get(0)
  }

  def getAsString(ctx: FacesContext, comp: UIComponent, value: Object): String = {
    val user: User = value.asInstanceOf[User]
    user.id.toString
  }
}