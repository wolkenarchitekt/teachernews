package net.teachernews.producers

import javax.enterprise.inject.Produces
import javax.faces.context.FacesContext
import javax.inject.Inject
import java.util.{ ResourceBundle, Locale }

/**
 * Produces ResourceBundle for injection.
 * Since ResourceBundle is not serializable, it has to be injected into a transient attribute.  
 *
 * @author Ingo Fischer
 * @version 1.0
 */
class ResourceBundleProducer {
  @Inject
  var locale: Locale = _

  @Inject
  var facesContext: FacesContext = _

  /**
   * Producer method
   * @return ResourceBundle
   */
  @Produces
  def getResourceBundle: ResourceBundle =
    ResourceBundle.getBundle("/messages", locale)
}
