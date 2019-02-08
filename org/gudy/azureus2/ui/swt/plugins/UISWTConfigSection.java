package org.gudy.azureus2.ui.swt.plugins;

import org.eclipse.swt.widgets.Composite;
import org.gudy.azureus2.plugins.ui.config.ConfigSection;

public abstract interface UISWTConfigSection
  extends ConfigSection
{
  public abstract Composite configSectionCreate(Composite paramComposite);
  
  public abstract int maxUserMode();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/plugins/UISWTConfigSection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */