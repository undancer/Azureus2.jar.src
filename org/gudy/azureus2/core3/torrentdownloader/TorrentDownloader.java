package org.gudy.azureus2.core3.torrentdownloader;

import java.io.File;

public abstract interface TorrentDownloader
{
  public static final int STATE_NON_INIT = -1;
  public static final int STATE_INIT = 0;
  public static final int STATE_START = 1;
  public static final int STATE_DOWNLOADING = 2;
  public static final int STATE_FINISHED = 3;
  public static final int STATE_ERROR = 4;
  public static final int STATE_DUPLICATE = 5;
  public static final int STATE_CANCELLED = 6;
  
  public abstract void start();
  
  public abstract void cancel();
  
  public abstract void setDownloadPath(String paramString1, String paramString2);
  
  public abstract int getDownloadState();
  
  public abstract File getFile();
  
  public abstract int getPercentDone();
  
  public abstract int getTotalRead();
  
  public abstract String getError();
  
  public abstract String getStatus();
  
  public abstract String getURL();
  
  public abstract int getLastReadCount();
  
  public abstract byte[] getLastReadBytes();
  
  public abstract boolean getDeleteFileOnCancel();
  
  public abstract void setDeleteFileOnCancel(boolean paramBoolean);
  
  public abstract boolean isIgnoreReponseCode();
  
  public abstract void setIgnoreReponseCode(boolean paramBoolean);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/torrentdownloader/TorrentDownloader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */