package org.gudy.azureus2.core3.disk.impl.piecemapper;

import java.io.File;
import org.gudy.azureus2.core3.disk.impl.DiskManagerFileInfoImpl;
import org.gudy.azureus2.core3.torrent.TOTorrentFile;

public abstract interface DMPieceMapperFile
{
  public abstract long getLength();
  
  public abstract File getDataFile();
  
  public abstract TOTorrentFile getTorrentFile();
  
  public abstract DiskManagerFileInfoImpl getFileInfo();
  
  public abstract void setFileInfo(DiskManagerFileInfoImpl paramDiskManagerFileInfoImpl);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/piecemapper/DMPieceMapperFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */