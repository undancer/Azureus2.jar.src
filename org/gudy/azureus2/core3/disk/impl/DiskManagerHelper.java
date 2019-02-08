package org.gudy.azureus2.core3.disk.impl;

import com.aelitis.azureus.core.diskmanager.access.DiskAccessController;
import org.gudy.azureus2.core3.disk.DiskManager;
import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceList;
import org.gudy.azureus2.core3.download.DownloadManagerState;
import org.gudy.azureus2.core3.torrent.TOTorrent;
import org.gudy.azureus2.core3.torrent.TOTorrentException;

public abstract interface DiskManagerHelper
  extends DiskManager
{
  public abstract DiskAccessController getDiskAccessController();
  
  public abstract DMPieceList getPieceList(int paramInt);
  
  public abstract byte[] getPieceHash(int paramInt)
    throws TOTorrentException;
  
  public abstract void setFailed(String paramString);
  
  public abstract void setFailed(DiskManagerFileInfo paramDiskManagerFileInfo, String paramString);
  
  public abstract long getAllocated();
  
  public abstract void setAllocated(long paramLong);
  
  public abstract void setPercentDone(int paramInt);
  
  public abstract void setPieceDone(DiskManagerPieceImpl paramDiskManagerPieceImpl, boolean paramBoolean);
  
  public abstract TOTorrent getTorrent();
  
  public abstract String[] getStorageTypes();
  
  public abstract String getStorageType(int paramInt);
  
  public abstract void accessModeChanged(DiskManagerFileInfoImpl paramDiskManagerFileInfoImpl, int paramInt1, int paramInt2);
  
  public abstract void skippedFileSetChanged(DiskManagerFileInfo paramDiskManagerFileInfo);
  
  public abstract void priorityChanged(DiskManagerFileInfo paramDiskManagerFileInfo);
  
  public abstract String getInternalName();
  
  public abstract DownloadManagerState getDownloadState();
  
  public abstract DiskManagerRecheckScheduler getRecheckScheduler();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/DiskManagerHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */