package net.teachernews.controller

import javax.annotation.PostConstruct
import javax.enterprise.context.{SessionScoped,Conversation}
import javax.ejb.EJB
import javax.faces.application.NavigationHandler
import javax.faces.context.{FacesContext, Flash}
import javax.inject.{Named,Inject}
import java.util.{Locale, ArrayList, ResourceBundle}

import org.primefaces.model.DualListModel;
import org.slf4j.Logger

import scala.collection.JavaConversions._
import scala.reflect.{BeanProperty, BooleanBeanProperty}

import net.teachernews.services.Constants.REDIRECT
import net.teachernews.services.Security
import net.teachernews.ejb.{MessageEJB, SubscriptionEJB, UserEJB}
import net.teachernews.model.{Message, Message_, Subscription, Subscription_, User, User_, RoleType}

/**
 * Manage subscriptions
 * 
 * @author Ingo Fischer
 * @version 1.0
 */
@SessionScoped
@Named @serializable
@Inject
class SubscriptionManager {
  @EJB          				var subscriptionEJB:SubscriptionEJB = _
  @EJB          				var userEJB:UserEJB = _
  @EJB          				var messageEJB:MessageEJB = _
  @Inject        				var security:Security = _
  @Inject @transient		var log:Logger = _
  @Inject       				var conversation:Conversation = _
  @Inject       				var facesContext: FacesContext = _
  @Inject       				var locale: Locale = _
  @Inject       				var nav:NavigationHandler = _
  @Inject @transient 		var bundle:ResourceBundle = _
  @Inject @transient 		var flash:Flash = _
  
  @BeanProperty 				var confirmPassword:String = _
  @BooleanBeanProperty 	var sendAsEmail:Boolean = false
  
  // Current subscriptions
  var subscriptions:DualListModel[User] = _
  
  // Old subscriptions to compare 
  var subsBefore:DualListModel[User] = _
  
  // When subscriptions have been changed, reload them 
  var subsDirty = true
  
  /**
   * Build up subscription lists for picklist.
   * target list consist of teachers already subscribed to 
   * source list  consist of teachers not subscribed to
   * @see /user/subscriptions.xhtml
   */
  @PostConstruct
  def initSubscriptionList = {
    // Get subscriptions from logged-in user
    val subscribedTo = subscriptionEJB.findBy(Subscription_.subscriber -> security.user)
    
    // Extract users for TargetList
    val buffer = for (val sub <- subscribedTo) yield sub.sender
    val subscribedToUsers = new ArrayList(buffer)
    
    // Get all teachers not subscribed to
    val sourceList = userEJB.findBy(User_.role -> RoleType.TEACHER)
    sourceList.removeAll(subscribedToUsers)
    
    // Remove currently logged in user
    sourceList -= security.user
		
    // Save subscription
    subsBefore = new DualListModel[User](sourceList, subscribedToUsers)
		subscriptions = new DualListModel[User](sourceList, subscribedToUsers)
    
    subsDirty = false
  }
  
  /**
   * Dual list to be used by PrimeFaces Picklist component, 
   * @see /user/subscriptions.xhtml
   * @return dual list 
   * (source:users not subscribed to, target:users subscribed to) 
   */
  def getSubscriptions:DualListModel[User] = {
    if (subsDirty) 
    	initSubscriptionList
    subscriptions
  }
  def setSubscriptions(subs:DualListModel[User]) = this.subscriptions = subs 
  
  /**
   * Get all messages from users the given user subscribed to. 
   * @param subscriber
   * @return messages regarding users the subscriber subscribed to
   */
  def getSubMessages(user:User):ArrayList[Message] = {
		// Get all subscriptions
		val subList = subscriptionEJB.findBy(Subscription_.subscriber -> user)
		// Extract users
		var results = new ArrayList[Message] 
		val subUsers = for (val sub <- subList) yield sub.sender
		// Build up array of messages regarding the found users
		for (user <- subUsers)
			results ++ messageEJB.findBy(Message_.regards -> user)
		results
  }
  
  /**
   * When subscriptions changed (subdsDirty=true), persist changed 
   * subscriptions to database or remove if needed
   * @return <strong>navigate to: </strong> /user/subscriptions.xhtml
   */
  def updateSubscriptions:String = {
  	// No changes
    if (subscriptions.getTarget.size == subsBefore.getTarget.size) {
      subsDirty = false
      return "/user/subscriptions?" + REDIRECT
    }
  	
    // If new subscriptions where added 
    if (subscriptions.getTarget.size > subsBefore.getTarget.size) {
      // Create new subscriptions
      subsDirty = true
      subscriptions.getTarget.removeAll(subsBefore.getTarget)
      for (val user <- subscriptions.getTarget) {
        val s = new Subscription
        s.subscriber = security.user
        s.sender = user
        subscriptionEJB.persist(s)
      }
      flash.put("info", "subscriptions.updated")
      return "/user/subscriptions?" + REDIRECT
    }
    
    // If subscriptions where removed
    else {
      // Remove subscriptions
      subsDirty = true
      subsBefore.getTarget.removeAll(subscriptions.getTarget)
      for (val user <- subsBefore.getTarget) {
        val subsToRemove = subscriptionEJB.findBy(
          Subscription_.sender -> user,
          Subscription_.subscriber -> security.user
        )
        val sub = subsToRemove.get(0)
        subscriptionEJB.remove(sub)
      }
      flash.put("info", "subscriptions.updated")
      return "/user/subscriptions?" + REDIRECT
    }
  }
  
  /**
   * Get number of subscribers for currently logged in user
   * @return number of subscribers 
   */
  def getSubscribersCount:Integer = 
  	subscriptionEJB.findBy(Subscription_.sender -> security.user).size
}