package org.gudy.azureus2.plugins.update;

import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;

public abstract interface UpdateChecker
{
  public abstract UpdatableComponent getComponent();
  
  public abstract UpdateCheckInstance getCheckInstance();
  
  public abstract Update addUpdate(String paramString1, String[] paramArrayOfString, String paramString2, String paramString3, ResourceDownloader paramResourceDownloader, int paramInt);
  
  public abstract Update addUpdate(String paramString1, String[] paramArrayOfString, String paramString2, String paramString3, ResourceDownloader[] paramArrayOfResourceDownloader, int paramInt);
  
  public abstract UpdateInstaller createInstaller()
    throws UpdateException;
  
  public abstract void completed();
  
  public abstract void failed();
  
  public abstract void reportProgress(String paramString);
  
  public abstract void addListener(UpdateCheckerListener paramUpdateCheckerListener);
  
  public abstract void removeListener(UpdateCheckerListener paramUpdateCheckerListener);
  
  public abstract void addProgressListener(UpdateProgressListener paramUpdateProgressListener);
  
  public abstract void removeProgressListener(UpdateProgressListener paramUpdateProgressListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/update/UpdateChecker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */