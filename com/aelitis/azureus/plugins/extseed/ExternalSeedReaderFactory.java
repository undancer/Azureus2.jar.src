package com.aelitis.azureus.plugins.extseed;

import java.util.Map;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.torrent.Torrent;

public abstract interface ExternalSeedReaderFactory
{
  public abstract ExternalSeedReader[] getSeedReaders(ExternalSeedPlugin paramExternalSeedPlugin, Torrent paramTorrent);
  
  public abstract ExternalSeedReader[] getSeedReaders(ExternalSeedPlugin paramExternalSeedPlugin, Download paramDownload);
  
  public abstract ExternalSeedReader[] getSeedReaders(ExternalSeedPlugin paramExternalSeedPlugin, Download paramDownload, Map paramMap);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/extseed/ExternalSeedReaderFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */