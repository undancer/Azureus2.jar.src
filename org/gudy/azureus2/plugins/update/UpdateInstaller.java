package org.gudy.azureus2.plugins.update;

import java.io.InputStream;

public abstract interface UpdateInstaller
{
  public abstract void addResource(String paramString, InputStream paramInputStream)
    throws UpdateException;
  
  public abstract void addResource(String paramString, InputStream paramInputStream, boolean paramBoolean)
    throws UpdateException;
  
  public abstract String getInstallDir();
  
  public abstract String getUserDir();
  
  public abstract void addMoveAction(String paramString1, String paramString2)
    throws UpdateException;
  
  public abstract void addChangeRightsAction(String paramString1, String paramString2)
    throws UpdateException;
  
  public abstract void addRemoveAction(String paramString)
    throws UpdateException;
  
  public abstract void installNow(UpdateInstallerListener paramUpdateInstallerListener)
    throws UpdateException;
  
  public abstract void destroy();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/update/UpdateInstaller.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */