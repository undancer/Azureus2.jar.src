package org.gudy.azureus2.plugins.download.savelocation;

import org.gudy.azureus2.plugins.download.Download;

public abstract interface DefaultSaveLocationManager
  extends SaveLocationManager
{
  public abstract SaveLocationChange testOnCompletion(Download paramDownload, boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract boolean isInDefaultSaveDir(Download paramDownload);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/download/savelocation/DefaultSaveLocationManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */