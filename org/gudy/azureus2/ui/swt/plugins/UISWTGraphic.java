package org.gudy.azureus2.ui.swt.plugins;

import org.eclipse.swt.graphics.Image;
import org.gudy.azureus2.plugins.ui.Graphic;

public abstract interface UISWTGraphic
  extends Graphic
{
  public abstract Image getImage();
  
  public abstract boolean setImage(Image paramImage);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/plugins/UISWTGraphic.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */