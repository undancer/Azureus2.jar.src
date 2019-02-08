package org.gudy.azureus2.plugins.disk;

public abstract interface DiskManagerChannel
{
  public abstract DiskManagerRequest createRequest();
  
  public abstract DiskManagerFileInfo getFile();
  
  public abstract long getPosition();
  
  public abstract boolean isDestroyed();
  
  public abstract void destroy();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/disk/DiskManagerChannel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */