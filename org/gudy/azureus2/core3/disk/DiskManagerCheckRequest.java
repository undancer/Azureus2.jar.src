package org.gudy.azureus2.core3.disk;

public abstract interface DiskManagerCheckRequest
  extends DiskManagerRequest
{
  public abstract int getPieceNumber();
  
  public abstract Object getUserData();
  
  public abstract void setLowPriority(boolean paramBoolean);
  
  public abstract boolean isLowPriority();
  
  public abstract void setAdHoc(boolean paramBoolean);
  
  public abstract boolean isAdHoc();
  
  public abstract void setHash(byte[] paramArrayOfByte);
  
  public abstract byte[] getHash();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/DiskManagerCheckRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */