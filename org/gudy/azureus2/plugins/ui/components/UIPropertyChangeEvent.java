package org.gudy.azureus2.plugins.ui.components;

public abstract interface UIPropertyChangeEvent
{
  public abstract UIComponent getSource();
  
  public abstract String getPropertyType();
  
  public abstract Object getNewPropertyValue();
  
  public abstract Object getOldPropertyValue();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/components/UIPropertyChangeEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */