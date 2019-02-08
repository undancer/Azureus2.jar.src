package com.aelitis.azureus.core.subs;

import java.net.URL;
import java.util.Map;
import org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl.PluginSubscriptionManager;

public abstract interface SubscriptionManager
  extends UtilitiesImpl.PluginSubscriptionManager
{
  public abstract Subscription create(String paramString1, boolean paramBoolean, String paramString2)
    throws SubscriptionException;
  
  public abstract Subscription createRSS(String paramString, URL paramURL, int paramInt, Map paramMap)
    throws SubscriptionException;
  
  public abstract Subscription createRSS(String paramString, URL paramURL, int paramInt, boolean paramBoolean, Map paramMap)
    throws SubscriptionException;
  
  public abstract Subscription createSingletonRSS(String paramString, URL paramURL, int paramInt, boolean paramBoolean)
    throws SubscriptionException;
  
  public abstract Subscription createFromURI(String paramString)
    throws SubscriptionException;
  
  public abstract int getKnownSubscriptionCount();
  
  public abstract int getSubscriptionCount(boolean paramBoolean);
  
  public abstract Subscription[] getSubscriptions();
  
  public abstract Subscription[] getSubscriptions(boolean paramBoolean);
  
  public abstract Subscription getSubscriptionByID(String paramString);
  
  public abstract SubscriptionAssociationLookup lookupAssociations(byte[] paramArrayOfByte, SubscriptionLookupListener paramSubscriptionLookupListener)
    throws SubscriptionException;
  
  public abstract SubscriptionAssociationLookup lookupAssociations(byte[] paramArrayOfByte, String[] paramArrayOfString, SubscriptionLookupListener paramSubscriptionLookupListener)
    throws SubscriptionException;
  
  public abstract Subscription[] getKnownSubscriptions(byte[] paramArrayOfByte);
  
  public abstract Subscription[] getLinkedSubscriptions(byte[] paramArrayOfByte);
  
  public abstract SubscriptionScheduler getScheduler();
  
  public abstract int getMaxNonDeletedResults();
  
  public abstract void setMaxNonDeletedResults(int paramInt);
  
  public abstract boolean getAutoStartDownloads();
  
  public abstract void setAutoStartDownloads(boolean paramBoolean);
  
  public abstract int getAutoStartMinMB();
  
  public abstract void setAutoStartMinMB(int paramInt);
  
  public abstract int getAutoStartMaxMB();
  
  public abstract void setAutoStartMaxMB(int paramInt);
  
  public abstract int getAutoDownloadMarkReadAfterDays();
  
  public abstract void setAutoDownloadMarkReadAfterDays(int paramInt);
  
  public abstract boolean isRSSPublishEnabled();
  
  public abstract void setRSSPublishEnabled(boolean paramBoolean);
  
  public abstract boolean isSearchEnabled();
  
  public abstract void setSearchEnabled(boolean paramBoolean);
  
  public abstract boolean isSubsDownloadEnabled();
  
  public abstract void setSubsDownloadEnabled(boolean paramBoolean);
  
  public abstract boolean hideSearchTemplates();
  
  public abstract void setActivateSubscriptionOnChange(boolean paramBoolean);
  
  public abstract boolean getActivateSubscriptionOnChange();
  
  public abstract String getRSSLink();
  
  public abstract void setRateLimits(String paramString);
  
  public abstract String getRateLimits();
  
  public abstract void addListener(SubscriptionManagerListener paramSubscriptionManagerListener);
  
  public abstract void removeListener(SubscriptionManagerListener paramSubscriptionManagerListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/subs/SubscriptionManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */