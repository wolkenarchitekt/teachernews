package net.teachernews.controller

import java.util.ArrayList
import java.util.concurrent.Future

import javax.enterprise.context.{Conversation, ConversationScoped}
import javax.ejb.EJB
import javax.faces.application.NavigationHandler
import javax.faces.context.{FacesContext, Flash}
import javax.inject.{Named,Inject}
import javax.persistence.metamodel.SingularAttribute

import org.slf4j.Logger

import scala.collection.JavaConversions._
import scala.reflect.{BeanProperty, BooleanBeanProperty}

import net.teachernews.ejb.{MessageEJB, EmailEJB, SubscriptionEJB}
import net.teachernews.exceptions._
import net.teachernews.model.{Message, Message_, User, Subscription, Subscription_}
import net.teachernews.services.Constants.{REDIRECT,REDIRECT_WITH_CONV}
import net.teachernews.services.Security 

/**
 * Persist Messages and send E-Mails. 
 * Conversation-scoped, so client can invoke asynchronous method sendMails on multiple instances. 
 * 
 * @author Ingo Fischer
 * @version 1.0
 */
@ConversationScoped
@Named @serializable
class MessageManager {
  @EJB                  var messageEJB:MessageEJB = _
  @EJB                  var emailEJB:EmailEJB = _
  @EJB                  var subscriptionEJB:SubscriptionEJB = _
  @Inject               var security:Security = _
  @Inject @transient    var flash:Flash = _
  @Inject @transient    var log:Logger = _
  @Inject                var nav:NavigationHandler = _
  @Inject                var fc:FacesContext = _
  
  @BeanProperty         var message:Message = new Message
  @BooleanBeanProperty   var sendAsEmail:Boolean = false
  
  @Inject                var conversation:Conversation = _
  
  var progress:Future[ApplicationException] = _
  
  /**
   * If exception during sending emails occured, this variable holds it
   * and can be accessed to be shown on the view
   */
  @BeanProperty
  var exception:ApplicationException = _
  
  def getMessages:ArrayList[Message] = messageEJB.findAll
  
  /**
   * Begin conversation. 
   * @return <strong>navigate to:</strong> messages_create.xhtml
   */
  def startConversation:String = {
    exception = null
    if (conversation.isTransient) conversation.begin
    "/teacher/message_create?" + REDIRECT_WITH_CONV + conversation.getId
  }
  
  /**
   * Shows if e-mails are currently sent by checking value of Future value.
   * If an exception ocurred, it is saved and accessible for the view.
   * @return busy status. true, if asynchronous method is still processing, else false.
   */
  def getBusy:Boolean = 
    progress match {
      case x:Future[ApplicationException] => {
        if (x.isDone) {
          // Sending mails finished. Error ocurred.
          if (x.get != null) {
            this.exception = x.get
            false
          }
          // Sending mails finished successfully.
          else { 
            if (conversation.isTransient) 
              conversation.end
            false
          }
        }
        // Sending mails is still in process.
        else true
      }
      // No process running.
      case _ => false
    }

  /**
   * Save message and if needed send it as E-Mail
   * @return <strong>navigate to:</strong> messages_create.xhtml
   */
  def sendMessage:String = {
    message.regards = security.user
    messageEJB.persist(message)
    if (sendAsEmail) {
      progress = emailEJB.sendEmails(security.user, message)
      message = new Message  
      return "/teacher/message_status?" + REDIRECT_WITH_CONV + conversation.getId
    }
    message = new Message
    flash.put("info", "message.created")
    return "/teacher/message_create?" + REDIRECT
  }
  
  /**
   * Remove message
   * @param message to remove
   * @return <strong>navigate to:</strong> messages.xhtml
   */
  def removeMessage(message:Message):String = {
    messageEJB.remove(message)
    flash.put("info", "message.removed")
    "/messages?" + REDIRECT 
  }
  
  /**
   * View message
   * @param message to view
   * @return <strong>navigate to:</strong> message_view.xhtml
   */
  def viewMessage(message:Message):String = {
    if (conversation.isTransient) 
      conversation.begin
    this.message = message
    "/message_details?" + REDIRECT_WITH_CONV + conversation.getId
  }
}
