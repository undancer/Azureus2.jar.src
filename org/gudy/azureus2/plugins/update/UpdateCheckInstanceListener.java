package org.gudy.azureus2.plugins.update;

public abstract interface UpdateCheckInstanceListener
{
  public abstract void cancelled(UpdateCheckInstance paramUpdateCheckInstance);
  
  public abstract void complete(UpdateCheckInstance paramUpdateCheckInstance);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/update/UpdateCheckInstanceListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */