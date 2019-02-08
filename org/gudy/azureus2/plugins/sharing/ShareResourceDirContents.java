package org.gudy.azureus2.plugins.sharing;

import java.io.File;

public abstract interface ShareResourceDirContents
  extends ShareResource
{
  public abstract File getRoot();
  
  public abstract boolean isRecursive();
  
  public abstract ShareResource[] getChildren();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/sharing/ShareResourceDirContents.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */