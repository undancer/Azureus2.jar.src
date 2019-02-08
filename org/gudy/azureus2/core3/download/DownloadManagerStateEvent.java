package org.gudy.azureus2.core3.download;

public abstract interface DownloadManagerStateEvent
{
  public static final int ET_ATTRIBUTE_WRITTEN = 1;
  public static final int ET_ATTRIBUTE_WILL_BE_READ = 2;
  
  public abstract int getType();
  
  public abstract Object getData();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/download/DownloadManagerStateEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */