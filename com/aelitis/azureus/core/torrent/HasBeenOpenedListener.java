package com.aelitis.azureus.core.torrent;

import org.gudy.azureus2.core3.download.DownloadManager;

public abstract interface HasBeenOpenedListener
{
  public abstract void hasBeenOpenedChanged(DownloadManager paramDownloadManager, boolean paramBoolean);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/torrent/HasBeenOpenedListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */