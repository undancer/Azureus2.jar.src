package org.gudy.azureus2.plugins.sharing;

import java.io.File;

public abstract interface ShareResourceFile
  extends ShareResource
{
  public abstract File getFile();
  
  public abstract ShareItem getItem();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/sharing/ShareResourceFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */