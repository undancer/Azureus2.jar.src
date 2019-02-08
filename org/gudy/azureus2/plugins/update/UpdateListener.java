package org.gudy.azureus2.plugins.update;

public abstract interface UpdateListener
{
  public abstract void complete(Update paramUpdate);
  
  public abstract void cancelled(Update paramUpdate);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/update/UpdateListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */