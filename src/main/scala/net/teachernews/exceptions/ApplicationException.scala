package net.teachernews.exceptions

import scala.reflect.BeanProperty
/**
 * ApplicationException: 
 * These exceptions are catched in the CustomExceptionHandler.
 * Exceptions are displayed in the view as localized Strings.
 * 
 * @param message - The ExceptionType Enum Value. used as key for localization.
 * @param cause 
 * 
 * @see /default.xhtml, ExceptionHandlerFactory.scala 
 * 
 * @author Ingo Fischer
 * @version 1.0
 */
@javax.ejb.ApplicationException
class ApplicationException(var message:ExceptionType.Value, val cause: Throwable=null)
    extends RuntimeException(message.toString, cause) 

/**
 * ExceptionType Enumeration for use with ApplicationException.
 * Each ExceptionType has its represantation as a key in the message bundle, 
 * e.g. value: LoginException -> key: exception.LoginException
 * 
 * @see net.teachernews.exceptions.ExceptionHandlerFactory.scala
 * 
 * @author Ingo Fischer
 * @version 1.0
 */
object ExceptionType extends Enumeration {
  val LoginException = Value
  val EmailExistsException = Value
  val EmailNotFoundException = Value
  val WrongPasswordConfirmation = Value
  val LoadingSettingsException = Value
  val SendMailFailed = Value
  val SendMailBusy = Value
  val AsynchronousException = Value
}
