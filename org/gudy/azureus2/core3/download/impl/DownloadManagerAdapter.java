package org.gudy.azureus2.core3.download.impl;

import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.core3.download.DownloadManagerListener;

public class DownloadManagerAdapter
  implements DownloadManagerListener
{
  public void stateChanged(DownloadManager manager, int state) {}
  
  public void downloadComplete(DownloadManager manager) {}
  
  public void completionChanged(DownloadManager manager, boolean bCompleted) {}
  
  public void positionChanged(DownloadManager download, int oldPosition, int newPosition) {}
  
  public void filePriorityChanged(DownloadManager download, DiskManagerFileInfo file) {}
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/download/impl/DownloadManagerAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */