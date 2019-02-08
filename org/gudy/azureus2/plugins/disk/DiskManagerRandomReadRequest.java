package org.gudy.azureus2.plugins.disk;

public abstract interface DiskManagerRandomReadRequest
{
  public abstract DiskManagerFileInfo getFile();
  
  public abstract long getOffset();
  
  public abstract long getLength();
  
  public abstract boolean isReverse();
  
  public abstract void cancel();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/disk/DiskManagerRandomReadRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */