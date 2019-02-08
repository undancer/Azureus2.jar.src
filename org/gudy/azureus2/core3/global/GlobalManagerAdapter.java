package org.gudy.azureus2.core3.global;

import org.gudy.azureus2.core3.download.DownloadManager;

public class GlobalManagerAdapter
  implements GlobalManagerListener
{
  public void downloadManagerAdded(DownloadManager dm) {}
  
  public void downloadManagerRemoved(DownloadManager dm) {}
  
  public void destroyInitiated() {}
  
  public void destroyed() {}
  
  public void seedingStatusChanged(boolean seeding_only_mode, boolean potentially_seeding_only_mode) {}
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/global/GlobalManagerAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */