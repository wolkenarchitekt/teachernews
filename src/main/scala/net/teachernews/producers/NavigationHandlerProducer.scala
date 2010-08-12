package net.teachernews.producers

import javax.enterprise.context.RequestScoped
import javax.enterprise.inject.Produces
import javax.faces.application.NavigationHandler
import javax.faces.context.FacesContext
import javax.inject.Inject

/**
 * Produces NavigationHandler for injection.
 * 
 * @author Ingo Fischer
 * @version 1.0
 */
class NavigationHandlerProducer {
  @Inject
  var context: FacesContext = _

  /**
   * Producer method
   * @return NavigationHandler
   */
  @Produces
  @RequestScoped
  def getNavigationHandler: NavigationHandler = {
    if (context != null) {
      val application = context.getApplication
      if (application != null)
        application.getNavigationHandler
    }
    null
  }
}