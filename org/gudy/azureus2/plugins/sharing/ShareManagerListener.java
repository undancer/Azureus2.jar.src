package org.gudy.azureus2.plugins.sharing;

public abstract interface ShareManagerListener
{
  public abstract void resourceAdded(ShareResource paramShareResource);
  
  public abstract void resourceModified(ShareResource paramShareResource1, ShareResource paramShareResource2);
  
  public abstract void resourceDeleted(ShareResource paramShareResource);
  
  public abstract void reportProgress(int paramInt);
  
  public abstract void reportCurrentTask(String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/sharing/ShareManagerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */