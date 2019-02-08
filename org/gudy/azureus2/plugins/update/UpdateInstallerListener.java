package org.gudy.azureus2.plugins.update;

public abstract interface UpdateInstallerListener
{
  public abstract void reportProgress(String paramString);
  
  public abstract void complete();
  
  public abstract void failed(UpdateException paramUpdateException);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/update/UpdateInstallerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */