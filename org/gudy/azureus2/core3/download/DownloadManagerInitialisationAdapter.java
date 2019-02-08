package org.gudy.azureus2.core3.download;

public abstract interface DownloadManagerInitialisationAdapter
{
  public static final int ACT_NONE = 0;
  public static final int ACT_ASSIGNS_TAGS = 1;
  public static final int ACT_PROCESSES_TAGS = 2;
  
  public abstract void initialised(DownloadManager paramDownloadManager, boolean paramBoolean);
  
  public abstract int getActions();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/download/DownloadManagerInitialisationAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */