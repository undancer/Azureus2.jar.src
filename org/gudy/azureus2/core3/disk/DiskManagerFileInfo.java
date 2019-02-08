package org.gudy.azureus2.core3.disk;

import java.io.File;
import java.io.IOException;
import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.core3.torrent.TOTorrentFile;
import org.gudy.azureus2.core3.util.DirectByteBuffer;

public abstract interface DiskManagerFileInfo
{
  public static final int READ = 1;
  public static final int WRITE = 2;
  public static final int ST_LINEAR = 1;
  public static final int ST_COMPACT = 2;
  public static final int ST_REORDER = 3;
  public static final int ST_REORDER_COMPACT = 4;
  
  public abstract void setPriority(int paramInt);
  
  public abstract void setSkipped(boolean paramBoolean);
  
  public abstract boolean setLink(File paramFile);
  
  public abstract boolean setLinkAtomic(File paramFile);
  
  public abstract File getLink();
  
  public abstract boolean setStorageType(int paramInt);
  
  public abstract int getStorageType();
  
  public abstract int getAccessMode();
  
  public abstract long getDownloaded();
  
  public abstract String getExtension();
  
  public abstract int getFirstPieceNumber();
  
  public abstract int getLastPieceNumber();
  
  public abstract long getLength();
  
  public abstract int getNbPieces();
  
  public abstract int getPriority();
  
  public abstract boolean isSkipped();
  
  public abstract int getIndex();
  
  public abstract DownloadManager getDownloadManager();
  
  public abstract DiskManager getDiskManager();
  
  public abstract File getFile(boolean paramBoolean);
  
  public abstract TOTorrentFile getTorrentFile();
  
  public abstract DirectByteBuffer read(long paramLong, int paramInt)
    throws IOException;
  
  public abstract void flushCache()
    throws Exception;
  
  public abstract int getReadBytesPerSecond();
  
  public abstract int getWriteBytesPerSecond();
  
  public abstract long getETA();
  
  public abstract void close();
  
  public abstract void addListener(DiskManagerFileInfoListener paramDiskManagerFileInfoListener);
  
  public abstract void removeListener(DiskManagerFileInfoListener paramDiskManagerFileInfoListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/DiskManagerFileInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */