package net.teachernews.producers

import java.util.Locale

import javax.enterprise.inject.Produces
import javax.faces.context.FacesContext
import javax.inject.Inject

/**
 * Produces current FacesLocale for injection
 *
 * @author Ingo Fischer
 * @version 1.0
 */
class FacesLocaleResolver 
{
  @Inject
  var facesContext:FacesContext = _

  def isActive:Boolean =
    (facesContext != null) && (facesContext.getCurrentPhaseId != null)

  /**
   * Producer method. If Locale not available, calculate it.
   * @return Locale current Locale
   */
  @Produces
  def getLocale:Locale = 
    if (facesContext.getViewRoot != null)
      facesContext.getViewRoot.getLocale;
    else
      facesContext.getApplication.getViewHandler.calculateLocale(facesContext);
}
