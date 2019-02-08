package org.gudy.azureus2.core3.disk.impl.piecemapper;

import org.gudy.azureus2.core3.disk.impl.DiskManagerFileInfoImpl;

public abstract interface DMPieceMapEntry
{
  public abstract DiskManagerFileInfoImpl getFile();
  
  public abstract long getOffset();
  
  public abstract int getLength();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/piecemapper/DMPieceMapEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */