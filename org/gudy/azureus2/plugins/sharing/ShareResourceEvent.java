package org.gudy.azureus2.plugins.sharing;

public abstract interface ShareResourceEvent
{
  public static final int ET_ATTRIBUTE_CHANGED = 1;
  
  public abstract int getType();
  
  public abstract Object getData();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/sharing/ShareResourceEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */