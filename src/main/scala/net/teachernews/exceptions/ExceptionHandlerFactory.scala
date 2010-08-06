package net.teachernews.exceptions

import javax.faces.application.ViewExpiredException
import javax.faces.context.{ExceptionHandler,ExceptionHandlerWrapper,FacesContext}
import javax.faces.event.{ExceptionQueuedEvent,ExceptionQueuedEventContext}
import javax.faces.FacesException

import org.jboss.weld.context.NonexistentConversationException
import org.jboss.weld.context.ContextNotActiveException

import org.slf4j.{Logger, LoggerFactory}

import net.teachernews.services.Security
import net.teachernews.services.Application


/**
 * ExceptionHandlerFactory to make global exception handling possible.
 * Delegates exceptions to CustomExceptionHandler
 * 
 * @author Ingo Fischer
 * @version 1.0
 */
class ExceptionHandlerFactory(private val parent:javax.faces.context.ExceptionHandlerFactory) 
extends javax.faces.context.ExceptionHandlerFactory {
  override def getExceptionHandler:ExceptionHandler = {
    val result = parent.getExceptionHandler
    new CustomExceptionHandler(result)
  }
}

/**
 * Global Exception Handler:
 * Handles Framework Exceptions and Custom Exceptions
 * 
 * @author Ingo Fischer
 * @version 1.0
 */
class CustomExceptionHandler(private val parent: ExceptionHandler) extends ExceptionHandlerWrapper {

  override def getWrapped:ExceptionHandler = parent
  
//  val ctx:Context = new InitialContext
//  val logger = ctx.lookup("java:global/teachernews/UserEJB").asInstanceOf[UserEJB]
  
  val log:Logger = LoggerFactory.getLogger(classOf[CustomExceptionHandler]);
  
  /**
   * Handle unhandled exceptions  
   */
  @throws(classOf[FacesException])
  override def handle {
    val events = getUnhandledExceptionQueuedEvents
    val eventsIterator = events.iterator
    
    val fc = FacesContext.getCurrentInstance
    val nav = fc.getApplication.getNavigationHandler
    val flash = fc.getExternalContext.getFlash
    
    var currentPage = ""
    if (fc != null && fc.getViewRoot != null)
      currentPage = fc.getViewRoot.getViewId
    
    // Iterate through all exceptions in the queue.
    // Remove specific exceptions from the queue.
    while (eventsIterator.hasNext) {
      val event:ExceptionQueuedEvent = eventsIterator.next
      val context = event.getSource.asInstanceOf[ExceptionQueuedEventContext]
      val exception = context.getException
      
      // Handle Framework Exceptions
      if (exception.isInstanceOf[ViewExpiredException] ||
          exception.isInstanceOf[NonexistentConversationException] ||
          exception.isInstanceOf[ContextNotActiveException]) {
        // Navigate to error page, display exception message
        try {
          flash.put("exceptionType", "exception.SessionTimeoutException")
          val sec:Security = Application.getSecurityBean
          val user = sec.user
          log.error("Logged in user:" + user + "experienced Exception:" + exception.toString)
          nav.handleNavigation(fc, null, currentPage)
          fc.renderResponse
        } finally {
          // remove exception from queue
          eventsIterator.remove
        }
      }
      // Handle Custom Exceptions
      else {
        // Search for a RuntimeException in Queue
        var appExc:ApplicationException = null
        try {
          appExc = exception.getCause.getCause.getCause.asInstanceOf[ApplicationException]
        }catch {
          case ex => ;// Do nothing; other exceptions are handled by the parent
        }
        if (appExc != null)
          try {
            // Exception wrapped:
            // FacesException.FacesException.EvaluationException.ApplicationException
            flash.put("exceptionType", "exception." + appExc.message)
            flash.put("exceptionCause", appExc.cause)
            nav.handleNavigation(fc, null, currentPage)
            fc.renderResponse
          } finally {
            // remove exception from queue
            eventsIterator.remove
          }
      }
      
      // At this point, the queue will not conatin any ViewExpiredEvents.
      // Thererfore, let the parent handle them
      getWrapped.handle       
    }
  }
}