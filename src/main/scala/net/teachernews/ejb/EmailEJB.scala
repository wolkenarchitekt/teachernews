package net.teachernews.ejb

import java.util.{ Locale, Properties, ResourceBundle }
import java.text.SimpleDateFormat

import java.util.concurrent.Future
import javax.annotation.Resource
import javax.ejb.{ Asynchronous, Stateful, AsyncResult }
import javax.mail.{ Message, Session, Transport }
import javax.mail.internet.{ InternetAddress, MimeMessage }
import javax.ejb.LocalBean
import javax.faces.context.Flash
import javax.inject.Inject

import org.slf4j.Logger

import scala.collection.JavaConversions._

import net.teachernews.model.{ User, Message => TNMessage, Subscription_ }
import net.teachernews.exceptions.{ ApplicationException, ExceptionType }

/**
 * Send Emails
 * 
 * @author Ingo Fischer
 * @version 1.0
 */
@Stateful
@LocalBean
class EmailEJB {
  /**
   * Inject Email settings 
   */
  @Resource(name = "mail/teachernews")
  @transient
  var mailSession: Session = _

  @Inject var subsEJB: SubscriptionEJB = _
  @Inject var locale: Locale = _
  
  @Inject @transient var bundle: ResourceBundle = _
  @Inject @transient var log: Logger = _
  @Inject @transient var flash: Flash = _

  /**
   * Get properties for mail-FROM from session
   * @param Message where FROM-params are inserted
   */
  def setFromParameters(msg: Message) = {
    val mailProperties = mailSession.getProperties
    val mailfromAddress = mailProperties.getProperty("mail.from.address")
    val mailfromName = mailProperties.getProperty("mail.from.name")
    msg.addFrom(Array(new InternetAddress(mailfromAddress, mailfromName)))
  }

  /**
   * Send new password to a user
   * @param email
   * @param password
   */
  def sendPassword(email: String, password: String) = {
    val msg: javax.mail.Message = new MimeMessage(mailSession)
    setFromParameters(msg)
    msg.setSubject(bundle.getString("application.name") + ": " +
      bundle.getString("user.mail.newpassword"))
    msg.setText(bundle.getString("user.mail.newpassword.text") + password)
    msg.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(email))
    try {
      Transport.send(msg)
    } catch {
      case ex: Exception =>
        throw new ApplicationException(ExceptionType.SendMailFailed, ex)
    }
    //val test = javax.faces.application.
    flash.put("info", "user.mail.newpassword.sent")
  }

  /**
   * Send emails asynchronously. Updates progress value.
   * @param sender The sender
   * @param message The message to be sent
   * @param sessionId The ID of the associated Session
   * @return Future value of operation. 
   * 1 if successful, -1 if errors occured.
   */
  @Asynchronous
  def sendEmails(sender: User, message: TNMessage): Future[ApplicationException] = {
    log.info("Sending mail.")
    // Reset progress
    val msg: javax.mail.Message = new MimeMessage(mailSession)
    setFromParameters(msg)
    // Find all users subscribed to this teacher
    val subscriptions = subsEJB.findBy(Subscription_.sender -> sender)
    val dateString = locale.toString match {
      case "de" => new SimpleDateFormat("dd.MM.yyyy").format(message.expirationDate)
      case _ => new SimpleDateFormat("dd/MM/yyyy").format(message.expirationDate)
    }
    for (sub <- subscriptions)
      msg.addRecipient(javax.mail.Message.RecipientType.BCC,
        new InternetAddress(sub.subscriber.email))
    msg.setSubject(bundle.getString("message.mail.title") + " " +
      sender.name + ", " + sender.firstName + ", " +
      bundle.getString("message.mail.expiration") + " " +
      dateString)
    msg.setText(message.content + bundle.getString("message.mail.appendix"))
    try {
      Transport.send(msg)
    } catch {
      case ex: Exception => {
        log.info("Error sending mail:" + ex.getMessage)
        return new AsyncResult(
          new ApplicationException(ExceptionType.SendMailFailed, ex))
      }
    }
    log.info("Mails send")
    return new AsyncResult(null)
  }
}