package org.gudy.azureus2.plugins.disk;

import java.io.File;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.download.DownloadException;

public abstract interface DiskManagerFileInfo
{
  public static final int READ = 1;
  public static final int WRITE = 2;
  public static final int PRIORITY_LOW = -1;
  public static final int PRIORITY_NORMAL = 0;
  public static final int PRIORITY_HIGH = 1;
  
  public abstract void setPriority(boolean paramBoolean);
  
  public abstract void setNumericPriority(int paramInt);
  
  public abstract void setSkipped(boolean paramBoolean);
  
  public abstract void setDeleted(boolean paramBoolean);
  
  public abstract void setLink(File paramFile);
  
  public abstract File getLink();
  
  public abstract int getAccessMode();
  
  public abstract long getDownloaded();
  
  public abstract long getLength();
  
  public abstract File getFile();
  
  public abstract File getFile(boolean paramBoolean);
  
  public abstract int getIndex();
  
  public abstract int getFirstPieceNumber();
  
  public abstract long getPieceSize();
  
  public abstract int getNumPieces();
  
  public abstract boolean isPriority();
  
  /**
   * @deprecated
   */
  public abstract int getNumericPriorty();
  
  public abstract int getNumericPriority();
  
  public abstract boolean isSkipped();
  
  public abstract boolean isDeleted();
  
  public abstract byte[] getDownloadHash()
    throws DownloadException;
  
  public abstract Download getDownload()
    throws DownloadException;
  
  public abstract DiskManagerChannel createChannel()
    throws DownloadException;
  
  public abstract DiskManagerRandomReadRequest createRandomReadRequest(long paramLong1, long paramLong2, boolean paramBoolean, DiskManagerListener paramDiskManagerListener)
    throws DownloadException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/disk/DiskManagerFileInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */