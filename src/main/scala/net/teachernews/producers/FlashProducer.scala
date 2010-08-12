package net.teachernews.producers

import javax.enterprise.inject.Produces
import javax.faces.context.{ FacesContext, Flash }
import javax.inject.Inject

/**
 * Produces Flash context for injection 
 * Since Flash is not serializable, it has to be injected into a transient attribute. 
 * 
 * @author Ingo Fischer
 * @version 1.0
 */
class FlashProducer {
  @Inject
  var facesContext: FacesContext = _

  /**
   * Producer method
   * @return Flash
   */
  @Produces
  def getFlash: Flash = {
    facesContext.getExternalContext.getFlash
  }
}