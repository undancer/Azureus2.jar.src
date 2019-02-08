package com.aelitis.azureus.core.lws;

import java.io.File;
import java.net.URL;
import org.gudy.azureus2.core3.torrent.TOTorrent;

public abstract interface LightWeightSeedAdapter
{
  public abstract TOTorrent getTorrent(byte[] paramArrayOfByte, URL paramURL, File paramFile)
    throws Exception;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/lws/LightWeightSeedAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */