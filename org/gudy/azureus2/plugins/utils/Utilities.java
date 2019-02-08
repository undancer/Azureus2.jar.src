package org.gudy.azureus2.plugins.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import org.gudy.azureus2.plugins.PluginException;
import org.gudy.azureus2.plugins.ddb.DistributedDatabase;
import org.gudy.azureus2.plugins.tag.Tag;
import org.gudy.azureus2.plugins.tag.TagManager;
import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException;
import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
import org.gudy.azureus2.plugins.utils.resourceuploader.ResourceUploaderFactory;
import org.gudy.azureus2.plugins.utils.search.SearchException;
import org.gudy.azureus2.plugins.utils.search.SearchInitiator;
import org.gudy.azureus2.plugins.utils.search.SearchProvider;
import org.gudy.azureus2.plugins.utils.security.SESecurityManager;
import org.gudy.azureus2.plugins.utils.subscriptions.SubscriptionException;
import org.gudy.azureus2.plugins.utils.subscriptions.SubscriptionManager;
import org.gudy.azureus2.plugins.utils.xml.rss.RSSFeed;
import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentException;
import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentFactory;

public abstract interface Utilities
{
  public abstract String getAzureusUserDir();
  
  public abstract String getAzureusProgramDir();
  
  public abstract boolean isCVSVersion();
  
  public abstract boolean isWindows();
  
  public abstract boolean isLinux();
  
  public abstract boolean isSolaris();
  
  public abstract boolean isOSX();
  
  public abstract boolean isUnix();
  
  public abstract boolean isFreeBSD();
  
  public abstract InputStream getImageAsStream(String paramString);
  
  public abstract Semaphore getSemaphore();
  
  public abstract Monitor getMonitor();
  
  public abstract ByteBuffer allocateDirectByteBuffer(int paramInt);
  
  public abstract void freeDirectByteBuffer(ByteBuffer paramByteBuffer);
  
  public abstract PooledByteBuffer allocatePooledByteBuffer(int paramInt);
  
  public abstract PooledByteBuffer allocatePooledByteBuffer(byte[] paramArrayOfByte);
  
  public abstract PooledByteBuffer allocatePooledByteBuffer(Map paramMap)
    throws IOException;
  
  public abstract Formatters getFormatters();
  
  public abstract LocaleUtilities getLocaleUtilities();
  
  public abstract UTTimer createTimer(String paramString);
  
  public abstract UTTimer createTimer(String paramString, boolean paramBoolean);
  
  public abstract UTTimer createTimer(String paramString, int paramInt);
  
  public abstract void createThread(String paramString, Runnable paramRunnable);
  
  public abstract void createProcess(String paramString)
    throws PluginException;
  
  public abstract ResourceDownloaderFactory getResourceDownloaderFactory();
  
  public abstract ResourceUploaderFactory getResourceUploaderFactory();
  
  public abstract SESecurityManager getSecurityManager();
  
  public abstract SimpleXMLParserDocumentFactory getSimpleXMLParserDocumentFactory();
  
  /**
   * @deprecated
   */
  public abstract RSSFeed getRSSFeed(InputStream paramInputStream)
    throws SimpleXMLParserDocumentException;
  
  public abstract RSSFeed getRSSFeed(URL paramURL, InputStream paramInputStream)
    throws SimpleXMLParserDocumentException;
  
  public abstract RSSFeed getRSSFeed(URL paramURL)
    throws ResourceDownloaderException, SimpleXMLParserDocumentException;
  
  /**
   * @deprecated
   */
  public abstract RSSFeed getRSSFeed(ResourceDownloader paramResourceDownloader)
    throws ResourceDownloaderException, SimpleXMLParserDocumentException;
  
  public abstract RSSFeed getRSSFeed(URL paramURL, ResourceDownloader paramResourceDownloader)
    throws ResourceDownloaderException, SimpleXMLParserDocumentException;
  
  public abstract InetAddress getPublicAddress();
  
  public abstract InetAddress getPublicAddress(boolean paramBoolean);
  
  public abstract String reverseDNSLookup(InetAddress paramInetAddress);
  
  public abstract long getCurrentSystemTime();
  
  public abstract ByteArrayWrapper createWrapper(byte[] paramArrayOfByte);
  
  public abstract AggregatedDispatcher createAggregatedDispatcher(long paramLong1, long paramLong2);
  
  public abstract AggregatedList createAggregatedList(AggregatedListAcceptor paramAggregatedListAcceptor, long paramLong1, long paramLong2);
  
  public abstract Map readResilientBEncodedFile(File paramFile, String paramString, boolean paramBoolean);
  
  public abstract void writeResilientBEncodedFile(File paramFile, String paramString, Map paramMap, boolean paramBoolean);
  
  public abstract void deleteResilientBEncodedFile(File paramFile, String paramString, boolean paramBoolean);
  
  public abstract int compareVersions(String paramString1, String paramString2);
  
  public abstract String normaliseFileName(String paramString);
  
  public abstract DelayedTask createDelayedTask(Runnable paramRunnable);
  
  public abstract void registerSearchProvider(SearchProvider paramSearchProvider)
    throws SearchException;
  
  public abstract void unregisterSearchProvider(SearchProvider paramSearchProvider)
    throws SearchException;
  
  public abstract SearchInitiator getSearchInitiator()
    throws SearchException;
  
  public abstract SubscriptionManager getSubscriptionManager()
    throws SubscriptionException;
  
  public abstract FeatureManager getFeatureManager();
  
  public abstract boolean supportsPowerStateControl(int paramInt);
  
  public abstract void addPowerManagementListener(PowerManagementListener paramPowerManagementListener);
  
  public abstract void removePowerManagementListener(PowerManagementListener paramPowerManagementListener);
  
  public abstract List<LocationProvider> getLocationProviders();
  
  public abstract void addLocationProvider(LocationProvider paramLocationProvider);
  
  public abstract void removeLocationProvider(LocationProvider paramLocationProvider);
  
  public abstract void addLocationProviderListener(LocationProviderListener paramLocationProviderListener);
  
  public abstract void removeLocationProviderListener(LocationProviderListener paramLocationProviderListener);
  
  public abstract void registerJSONRPCServer(JSONServer paramJSONServer);
  
  public abstract void unregisterJSONRPCServer(JSONServer paramJSONServer);
  
  public abstract void registerJSONRPCClient(JSONClient paramJSONClient);
  
  public abstract void unregisterJSONRPCClient(JSONClient paramJSONClient);
  
  public abstract List<DistributedDatabase> getDistributedDatabases(String[] paramArrayOfString);
  
  public abstract List<DistributedDatabase> getDistributedDatabases(String[] paramArrayOfString, Map<String, Object> paramMap);
  
  public abstract List<ScriptProvider> getScriptProviders();
  
  public abstract void registerScriptProvider(ScriptProvider paramScriptProvider);
  
  public abstract void unregisterScriptProvider(ScriptProvider paramScriptProvider);
  
  public abstract void addScriptProviderListener(ScriptProvider.ScriptProviderListener paramScriptProviderListener);
  
  public abstract void removeScriptProviderListener(ScriptProvider.ScriptProviderListener paramScriptProviderListener);
  
  public abstract TagManager getTagManager();
  
  public abstract Tag lookupTag(String paramString);
  
  public static abstract interface JSONClient
  {
    public abstract void serverRegistered(Utilities.JSONServer paramJSONServer);
    
    public abstract void serverUnregistered(Utilities.JSONServer paramJSONServer);
  }
  
  public static abstract interface JSONServer
  {
    public abstract String getName();
    
    public abstract List<String> getSupportedMethods();
    
    public abstract Map call(String paramString, Map paramMap)
      throws PluginException;
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/Utilities.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */