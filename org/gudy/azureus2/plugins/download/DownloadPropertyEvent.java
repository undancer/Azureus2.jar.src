package org.gudy.azureus2.plugins.download;

public abstract interface DownloadPropertyEvent
{
  public static final int PT_TORRENT_ATTRIBUTE_WRITTEN = 1;
  public static final int PT_TORRENT_ATTRIBUTE_WILL_BE_READ = 2;
  
  public abstract int getType();
  
  public abstract Object getData();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/download/DownloadPropertyEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */