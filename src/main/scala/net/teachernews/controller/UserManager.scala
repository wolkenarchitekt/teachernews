package net.teachernews.controller

import javax.enterprise.context.SessionScoped
import javax.ejb.EJB
import javax.inject.{Named,Inject}
import scala.reflect.BeanProperty
import javax.faces.model.SelectItem
import javax.faces.application.NavigationHandler
import javax.faces.context.{FacesContext, Flash}
import java.util.{Locale, ArrayList, ResourceBundle}
import org.slf4j.Logger

import net.teachernews.exceptions.{ApplicationException, ExceptionType}
import net.teachernews.ejb.{UserEJB, EmailEJB}
import net.teachernews.model.{User, Title, User_, RoleType}
import net.teachernews.services.Constants.REDIRECT
import net.teachernews.services.{Application => TNApplication}
import net.teachernews.services.Application.md5hash
import net.teachernews.services.Security

/**
 * Handle all user related actions, mainly CRUD
 * 
 * @author Ingo Fischer
 * @version 1.0
 */
@SessionScoped
@Named @serializable
class UserManager {
  @EJB          			var userEJB:UserEJB = _
  @Inject       			var facesContext: FacesContext = _
  @Inject 						var locale: Locale = _
  @Inject       			var nav:NavigationHandler = _
  @Inject 						var emailEJB:EmailEJB = _
  @Inject 						var security:Security = _

  @Inject @transient	var log:Logger = _
  @Inject @transient 	var bundle:ResourceBundle = _
  @Inject @transient 	var flash:Flash = _

  @BeanProperty 			var user:User = new User
  @BeanProperty 			var confirmPassword:String = _
  
  var oldMail:String = _
  var oldPWHash:String = _
  
  def getUsers:ArrayList[User] = userEJB.findAll
  
  /**
   * @return available Title-Enum values (localized) 
   */
  def getTitleOptions:Array[SelectItem] = 
    for(titleValue <- Title.values)
      yield new SelectItem(titleValue, bundle.getString(titleValue.toString))

  /**
   * @return available RoleType-Enum values (localized)
   */
  def getRoleOptions:Array[SelectItem] = 
    for(pStatusValue <- RoleType.values)
      yield new SelectItem(pStatusValue, bundle.getString(pStatusValue.toString))
  
  /**
   * Check passwords for equality
   */
  def checkPasswordEquality = 
    if (user.getPassword != confirmPassword) 
      throw new ApplicationException(ExceptionType.WrongPasswordConfirmation)
  
  /**
   * Check if user with E-Mail already exists
   */
  def checkEmailAvailable = {
    val userList = userEJB.findBy(User_.email -> user.email);                         
    if (userList.size > 0) 
      throw new ApplicationException(ExceptionType.EmailExistsException)
  }
  
  /**
   * Register new user. Login when register successful 
   * @return <strong>navigate to:</strong> /home.xhtml
   */
  def registerUser:String = {
  	checkPasswordEquality
  	checkEmailAvailable
    user.role = RoleType.STUDENT
    // save password before its hashed for login
    val clearPW = user.password
    userEJB.persist(user)
    security.user.email = this.user.email
    security.user.password = clearPW
    security.login
  }
  
  /**
   * Set Session user for editing. save old e-mail and hashed 
   * password for later check 
   * @param user User to put into session
   * @return <strong>navigate to:</strong> /user/edituser.xhtml
   */
  def updateUser(user:User):String = {
  	oldMail = user.email
    oldPWHash = user.password
    this.confirmPassword = oldPWHash
    this.user = user
    "/user/edituser?" + REDIRECT
  }
  
  /**
   * Save modifications to session user 
   * @return <strong>navigate to:</strong> /user/edituser.xhtml 
   */
  def confirmModifications = {
  	if (user.password != oldPWHash) {
  		checkPasswordEquality
  		user.password = md5hash(user.password)
  	}
  	if (user.email != oldMail) 
  		checkEmailAvailable
    user = userEJB.update(user)
    //if user edits himself, refresh security user accordingly
    if (user.id == security.user.id)
    	security.user = user
    flash.put("info", "application.modificationsSaved")
  	updateUser(user)
  }
  
  /**
   * Remove user given as parameter, or if parameter null, 
   * remove existing user in session.
   * @param user User to remove (optional)
   * @return <strong>navigate to:</strong> /deanery/users.xhtml 
   */
  def removeUser(user:User = null):String = {
    if (user != null) this.user = user
    userEJB.remove(user)
    flash.put("info", "user.removed")
    "/deanery/users?" + REDIRECT 
  }
  
  /**
   * Generate new password for session user, 
   * Send password to user as E-Mail
   * @return <strong>navigate to:</strong> /forgotpw.xhtml 
   */
  def forgotPassword:String  = {
  	val email = flash.get("user.email").toString
  	val userList = userEJB.findBy(User_.email -> email) 
  	if (userList.size == 0)
  		throw new ApplicationException(ExceptionType.EmailNotFoundException)
  	val newPassword = TNApplication.generatePW
  	val user = userList.get(0)
  	user.password = md5hash(newPassword)
  	userEJB.update(user)
  	emailEJB.sendPassword(email, newPassword)
  	"/forgotpw?" + REDIRECT
  }
  
}