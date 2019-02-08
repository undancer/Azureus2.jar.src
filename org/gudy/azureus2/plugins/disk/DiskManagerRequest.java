package org.gudy.azureus2.plugins.disk;

public abstract interface DiskManagerRequest
{
  public static final int REQUEST_READ = 1;
  
  public abstract void setType(int paramInt);
  
  public abstract void setOffset(long paramLong);
  
  public abstract void setLength(long paramLong);
  
  public abstract long getAvailableBytes();
  
  public abstract long getRemaining();
  
  public abstract void run();
  
  public abstract void cancel();
  
  public abstract void setUserAgent(String paramString);
  
  public abstract void setMaximumReadChunkSize(int paramInt);
  
  public abstract void addListener(DiskManagerListener paramDiskManagerListener);
  
  public abstract void removeListener(DiskManagerListener paramDiskManagerListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/disk/DiskManagerRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */