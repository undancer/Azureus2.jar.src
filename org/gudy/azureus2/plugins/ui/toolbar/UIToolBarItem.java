package org.gudy.azureus2.plugins.ui.toolbar;

public abstract interface UIToolBarItem
{
  public static final long STATE_ENABLED = 1L;
  public static final long STATE_DOWN = 2L;
  
  public abstract String getID();
  
  public abstract String getTextID();
  
  public abstract void setTextID(String paramString);
  
  public abstract String getImageID();
  
  public abstract void setImageID(String paramString);
  
  public abstract boolean isAlwaysAvailable();
  
  public abstract long getState();
  
  public abstract void setState(long paramLong);
  
  public abstract boolean triggerToolBarItem(long paramLong, Object paramObject);
  
  public abstract void setDefaultActivationListener(UIToolBarActivationListener paramUIToolBarActivationListener);
  
  public abstract String getGroupID();
  
  public abstract void setGroupID(String paramString);
  
  public abstract void setToolTip(String paramString);
  
  public abstract String getToolTip();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/toolbar/UIToolBarItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */