package org.gudy.azureus2.ui.swt.config;

import org.eclipse.swt.widgets.Control;

public abstract interface IParameter
{
  public abstract void setLayoutData(Object paramObject);
  
  public abstract Control getControl();
  
  public abstract Control[] getControls();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/IParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */