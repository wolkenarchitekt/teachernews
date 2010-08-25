package net.teachernews.services

import javax.ejb.EJB
import javax.enterprise.context.SessionScoped
import javax.inject.Inject
import javax.inject.Named
import javax.servlet.http.{ HttpSession, HttpServletRequest }

import scala.reflect.{ BooleanBeanProperty, BeanProperty }

import net.teachernews.ejb.UserEJB
import net.teachernews.exceptions.{ ApplicationException, ExceptionType }
import net.teachernews.model.{ RoleType, User, User_ }
import net.teachernews.services.Constants.REDIRECT
import net.teachernews.services.Application.md5hash

/**
 * Security Manager.
 * Handle login, provide current credentials
 *
 * @author Ingo Fischer
 * @version 1.0
 */
@SessionScoped
@Named
@serializable
class Security {
  @Inject var userEJB: UserEJB = _
  @Inject var session: HttpSession = _
  
  @Inject @transient var request: HttpServletRequest = _
  
  @BeanProperty
  var user: User = new User
  
  @BooleanBeanProperty
  var loggedIn = false

  def isDeanery = user.role == RoleType.DEANERY
  def isTeacher = user.role == RoleType.TEACHER

  /**
   * Logout. Clear session.
   */
  def logout: String = {
    request.logout
    if (session != null)
      session.invalidate
    "/home?" + REDIRECT
  }

  /**
   * Authenticate user using JavaEE6 authentication realm
   * @return <strong>navigate to:</strong> /home.xhtml if credentials are correct
   */
  def login: String = {
    try {
      val hpw = md5hash(user.password)
      request.login(user.email, hpw)
    } catch {
      case _ => {
        request.getSession(true).invalidate
        throw new ApplicationException(ExceptionType.LoginException)
      }
    }
    loggedIn = true;
    user = userEJB.findBy(User_.email -> user.email).get(0)
    "/home?" + REDIRECT
  }
}
