package net.teachernews.producers

import javax.enterprise.context.RequestScoped
import javax.enterprise.inject.Produces
import javax.faces.context.FacesContext
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest

/**
 * Produces HttpServletRequest context for injection 
 * Since HttpServletRequest is not serializable, it has to be injected into a transient attribute. 
 * 
 * @author Ingo Fischer
 * @version 1.0
 */
class RequestContextProducer {
  @Inject
  var facesContext: FacesContext = _

  /**
   * Producer method
   * @return HttpServletRequest
   */
  @Produces
  @RequestScoped
  def getFlash: HttpServletRequest =
    facesContext.getExternalContext.getRequest.asInstanceOf[HttpServletRequest]
}