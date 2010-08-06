package net.teachernews.producers

import javax.enterprise.context.ContextNotActiveException
import javax.enterprise.context.RequestScoped
import javax.enterprise.inject.Produces
import javax.faces.context.FacesContext

/**
 * Poduces FacesContext for injection
 *
 * @author Ingo Fischer
 * @version 1.0
 */
class FacesContextProducer {

  /**
   * @return FacesContext
   */
  @Produces @RequestScoped
  def getFacesContext:FacesContext = {
    val ctx = FacesContext.getCurrentInstance;
    if (ctx == null) 
      throw new ContextNotActiveException("FacesContext is not active")
    ctx
  }
}