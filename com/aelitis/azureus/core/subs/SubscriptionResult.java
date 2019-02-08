package com.aelitis.azureus.core.subs;

import java.util.Map;
import org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl.PluginSubscriptionResult;

public abstract interface SubscriptionResult
  extends UtilitiesImpl.PluginSubscriptionResult
{
  public abstract String getID();
  
  public abstract Map toJSONMap();
  
  public abstract Map<Integer, Object> toPropertyMap();
  
  public abstract String getDownloadLink();
  
  public abstract String getPlayLink();
  
  public abstract long getTimeFound();
  
  public abstract void setRead(boolean paramBoolean);
  
  public abstract boolean getRead();
  
  public abstract void delete();
  
  public abstract boolean isDeleted();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/subs/SubscriptionResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */