package org.gudy.azureus2.core3.disk;

import org.gudy.azureus2.core3.util.DirectByteBuffer;

public abstract interface DiskManagerWriteRequest
  extends DiskManagerRequest
{
  public abstract int getPieceNumber();
  
  public abstract int getOffset();
  
  public abstract DirectByteBuffer getBuffer();
  
  public abstract Object getUserData();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/DiskManagerWriteRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */