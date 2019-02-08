package org.gudy.azureus2.plugins.ui.config;

import org.gudy.azureus2.plugins.config.ConfigParameter;

public abstract interface Parameter
  extends ConfigParameter
{
  public static final int MODE_BEGINNER = 0;
  public static final int MODE_INTERMEDIATE = 1;
  public static final int MODE_ADVANCED = 2;
  
  public abstract void setEnabled(boolean paramBoolean);
  
  public abstract boolean isEnabled();
  
  public abstract int getMinimumRequiredUserMode();
  
  public abstract void setMinimumRequiredUserMode(int paramInt);
  
  public abstract void setVisible(boolean paramBoolean);
  
  public abstract boolean isVisible();
  
  public abstract void setGenerateIntermediateEvents(boolean paramBoolean);
  
  public abstract boolean getGenerateIntermediateEvents();
  
  public abstract void addListener(ParameterListener paramParameterListener);
  
  public abstract void removeListener(ParameterListener paramParameterListener);
  
  public abstract String getLabelText();
  
  public abstract void setLabelText(String paramString);
  
  public abstract String getLabelKey();
  
  public abstract void setLabelKey(String paramString);
  
  public abstract String getConfigKeyName();
  
  public abstract boolean hasBeenSet();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/config/Parameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */