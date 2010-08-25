package net.teachernews.services

import java.security.MessageDigest
import java.util.{ Locale, ResourceBundle }

import javax.enterprise.context.ContextNotActiveException
import javax.enterprise.inject.spi.{ Bean, BeanManager }
import javax.faces.context.FacesContext
import javax.naming.InitialContext

/**
 * Global convenience and helper methods
 */
object Application {
  /**
   * Convenience method to get Resource Bundle without Dependency Injection
   */
  def getResourceBundle: ResourceBundle = {
    // Get facescontext
    val facesContext = FacesContext.getCurrentInstance;
    if (facesContext == null)
      throw new ContextNotActiveException("FacesContext is not active")

    // Get locale
    var locale: Locale = null
    if (facesContext.getViewRoot() != null)
      locale = facesContext.getViewRoot().getLocale();
    else
      locale = facesContext.getApplication().getViewHandler().calculateLocale(facesContext);

    // Get ResourceBundle
    ResourceBundle.getBundle("/messages", locale)
  }

  /**
   * Get Security-CDI-Bean where CDI injection is not possible
   * @return Security bean instance
   */
  def getSecurityBean: Security = {
    val manager: BeanManager =
      new InitialContext().lookup("java:comp/BeanManager").asInstanceOf[BeanManager];
    val myBean: Bean[Security] =
      manager.getBeans(classOf[Security]).iterator.next.asInstanceOf[Bean[Security]]
    manager.getReference(myBean, classOf[Security],
      manager.createCreationalContext(myBean)).asInstanceOf[Security]
  }

  /**
   * Hash password with MD5 to save it in a database
   * @param password as plain text
   * @return Hashed text
   */
  def md5hash(text: String): String = {
    val md5 = MessageDigest.getInstance("MD5")
    md5.reset()
    md5.update(text.getBytes)
    val hexString = for (element <- md5.digest) yield Integer.toHexString(0xFF & element)
    hexString.mkString
  }

  /**
   * Generate a simple random Password
   * @return Random password hash
   */
  def generatePW: String = {
    val rand = new java.util.Random
    val genPW = new StringBuilder
    val loops = 2
    (0 to loops).foreach {
      genPW.append(Integer.toHexString(Math.abs(rand.nextInt)))
    }
    genPW.toString
  }

}