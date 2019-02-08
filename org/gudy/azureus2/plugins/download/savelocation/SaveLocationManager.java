package org.gudy.azureus2.plugins.download.savelocation;

import org.gudy.azureus2.plugins.download.Download;

public abstract interface SaveLocationManager
{
  public abstract SaveLocationChange onInitialization(Download paramDownload, boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract SaveLocationChange onCompletion(Download paramDownload, boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract SaveLocationChange onRemoval(Download paramDownload, boolean paramBoolean1, boolean paramBoolean2);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/download/savelocation/SaveLocationManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */