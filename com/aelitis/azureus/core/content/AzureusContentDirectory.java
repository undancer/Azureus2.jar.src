package com.aelitis.azureus.core.content;

import java.util.Map;

public abstract interface AzureusContentDirectory
{
  public static final String AT_BTIH = "btih";
  public static final String AT_FILE_INDEX = "file_index";
  
  public abstract AzureusContent lookupContent(Map paramMap);
  
  public abstract AzureusContentFile lookupContentFile(Map paramMap);
  
  public abstract AzureusContentDownload lookupContentDownload(Map paramMap);
  
  public abstract void addListener(AzureusContentDirectoryListener paramAzureusContentDirectoryListener);
  
  public abstract void removeListener(AzureusContentDirectoryListener paramAzureusContentDirectoryListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/content/AzureusContentDirectory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */