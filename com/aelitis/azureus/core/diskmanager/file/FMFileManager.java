package com.aelitis.azureus.core.diskmanager.file;

import com.aelitis.azureus.core.util.LinkFileMap;
import java.io.File;
import org.gudy.azureus2.core3.torrent.TOTorrent;

public abstract interface FMFileManager
{
  public abstract FMFile createFile(FMFileOwner paramFMFileOwner, File paramFile, int paramInt)
    throws FMFileManagerException;
  
  public abstract void setFileLinks(TOTorrent paramTOTorrent, LinkFileMap paramLinkFileMap);
  
  public abstract File getFileLink(TOTorrent paramTOTorrent, int paramInt, File paramFile);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/file/FMFileManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */