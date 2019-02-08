package com.aelitis.azureus.core.content;

import org.gudy.azureus2.plugins.download.Download;

public abstract interface AzureusContentDownload
{
  public abstract Download getDownload();
  
  public abstract Object getProperty(String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/content/AzureusContentDownload.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */