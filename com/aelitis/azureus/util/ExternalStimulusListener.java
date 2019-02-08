package com.aelitis.azureus.util;

import java.util.Map;

public abstract interface ExternalStimulusListener
{
  public abstract boolean receive(String paramString, Map paramMap);
  
  public abstract int query(String paramString, Map paramMap);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/util/ExternalStimulusListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */