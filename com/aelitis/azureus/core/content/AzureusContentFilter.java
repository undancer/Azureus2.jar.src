package com.aelitis.azureus.core.content;

import java.util.Map;

public abstract interface AzureusContentFilter
{
  public abstract boolean isVisible(AzureusContentDownload paramAzureusContentDownload, Map<String, Object> paramMap);
  
  public abstract boolean isVisible(AzureusContentFile paramAzureusContentFile, Map<String, Object> paramMap);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/content/AzureusContentFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */