package org.gudy.azureus2.plugins.disk;

public abstract interface DiskManagerReadRequest
{
  public abstract int getPieceNumber();
  
  public abstract int getOffset();
  
  public abstract int getLength();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/disk/DiskManagerReadRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */