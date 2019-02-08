/*     */ package org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.Proxy;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderDelayedFactory;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ResourceDownloaderFactoryImpl
/*     */   implements ResourceDownloaderFactory
/*     */ {
/*  44 */   private static final LogIDs LOGID = LogIDs.CORE;
/*  45 */   protected static ResourceDownloaderFactoryImpl singleton = new ResourceDownloaderFactoryImpl();
/*     */   
/*     */ 
/*  48 */   private static final String[] SF_MIRRORS = { "jaist", "nchc", "keihanna", "optusnet", "peterhost", "ovh", "puzzle", "switch", "mesh", "kent", "surfnet", "heanet", "citkit", "internap", "cogent", "umn", "easynews", "ufpr" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ResourceDownloaderFactory getSingleton()
/*     */   {
/*  57 */     return singleton;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ResourceDownloader create(File file)
/*     */   {
/*  64 */     return new ResourceDownloaderFileImpl(null, file);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResourceDownloader create(URL url)
/*     */   {
/*  72 */     if (url.getProtocol().equalsIgnoreCase("file")) {
/*     */       try
/*     */       {
/*  75 */         return new ResourceDownloaderFileImpl(null, new File(new URI(url.toString())));
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*  79 */         return new ResourceDownloaderURLImpl(null, url);
/*     */       }
/*     */     }
/*     */     
/*  83 */     return new ResourceDownloaderURLImpl(null, url);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResourceDownloader createWithAutoPluginProxy(URL url)
/*     */   {
/*  91 */     ResourceDownloader rd = create(url);
/*  92 */     if ((rd instanceof ResourceDownloaderURLImpl)) {
/*  93 */       ((ResourceDownloaderURLImpl)rd).setAutoPluginProxy();
/*     */     }
/*  95 */     return rd;
/*     */   }
/*     */   
/*     */   public ResourceDownloader create(URL url, boolean force_no_proxy) {
/*  99 */     ResourceDownloader rd = create(url);
/* 100 */     if ((force_no_proxy) && ((rd instanceof ResourceDownloaderURLImpl))) {
/* 101 */       ((ResourceDownloaderURLImpl)rd).setForceNoProxy(force_no_proxy);
/*     */     }
/* 103 */     return rd;
/*     */   }
/*     */   
/*     */   public ResourceDownloader create(URL url, Proxy proxy) {
/* 107 */     ResourceDownloader rd = create(url);
/* 108 */     if ((proxy != null) && ((rd instanceof ResourceDownloaderURLImpl))) {
/* 109 */       ((ResourceDownloaderURLImpl)rd).setForceProxy(proxy);
/*     */     }
/* 111 */     return rd;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResourceDownloader create(URL url, String postData)
/*     */   {
/* 119 */     return new ResourceDownloaderURLImpl(null, url, postData.getBytes(), false, null, null);
/*     */   }
/*     */   
/*     */   public ResourceDownloader create(URL url, String postData, Proxy proxy) {
/* 123 */     ResourceDownloader rd = create(url, postData);
/* 124 */     if ((proxy != null) && ((rd instanceof ResourceDownloaderURLImpl))) {
/* 125 */       ((ResourceDownloaderURLImpl)rd).setForceProxy(proxy);
/*     */     }
/* 127 */     return rd;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResourceDownloader create(URL url, byte[] postData)
/*     */   {
/* 135 */     return new ResourceDownloaderURLImpl(null, url, postData, false, null, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResourceDownloader create(URL url, String user_name, String password)
/*     */   {
/* 144 */     return new ResourceDownloaderURLImpl(null, url, user_name, password);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ResourceDownloader create(ResourceDownloaderDelayedFactory factory)
/*     */   {
/* 151 */     return new ResourceDownloaderDelayedImpl(null, factory);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResourceDownloader getRetryDownloader(ResourceDownloader downloader, int retry_count)
/*     */   {
/* 159 */     ResourceDownloader res = new ResourceDownloaderRetryImpl(null, downloader, retry_count);
/*     */     
/* 161 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResourceDownloader getTimeoutDownloader(ResourceDownloader downloader, int timeout_millis)
/*     */   {
/* 169 */     ResourceDownloader res = new ResourceDownloaderTimeoutImpl(null, downloader, timeout_millis);
/*     */     
/* 171 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ResourceDownloader getAlternateDownloader(ResourceDownloader[] downloaders)
/*     */   {
/* 178 */     return getAlternateDownloader(downloaders, -1, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResourceDownloader getAlternateDownloader(ResourceDownloader[] downloaders, int max_to_try)
/*     */   {
/* 186 */     return getAlternateDownloader(downloaders, max_to_try, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ResourceDownloader getRandomDownloader(ResourceDownloader[] downloaders)
/*     */   {
/* 193 */     return getAlternateDownloader(downloaders, -1, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResourceDownloader getRandomDownloader(ResourceDownloader[] downloaders, int max_to_try)
/*     */   {
/* 201 */     return getAlternateDownloader(downloaders, max_to_try, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ResourceDownloader getAlternateDownloader(ResourceDownloader[] downloaders, int max_to_try, boolean random)
/*     */   {
/* 210 */     ResourceDownloaderBaseImpl res = new ResourceDownloaderAlternateImpl(null, downloaders, max_to_try, random);
/*     */     
/* 212 */     boolean anon = false;
/*     */     
/* 214 */     for (ResourceDownloaderBaseImpl kid : res.getChildren())
/*     */     {
/* 216 */       if (kid.isAnonymous())
/*     */       {
/* 218 */         anon = true;
/*     */       }
/*     */     }
/*     */     
/* 222 */     if (anon) {
/*     */       try
/*     */       {
/* 225 */         res.setPropertyRecursive("Anonymous", Boolean.valueOf(true));
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 229 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */     
/* 233 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ResourceDownloader getMetaRefreshDownloader(ResourceDownloader downloader)
/*     */   {
/* 240 */     ResourceDownloader res = new ResourceDownloaderMetaRefreshImpl(null, downloader);
/*     */     
/* 242 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResourceDownloader getTorrentDownloader(ResourceDownloader downloader, boolean persistent)
/*     */   {
/* 250 */     return getTorrentDownloader(downloader, persistent, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResourceDownloader getTorrentDownloader(ResourceDownloader downloader, boolean persistent, File download_directory)
/*     */   {
/* 259 */     return new ResourceDownloaderTorrentImpl(null, downloader, persistent, download_directory);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ResourceDownloader getSuffixBasedDownloader(ResourceDownloader _downloader)
/*     */   {
/* 266 */     ResourceDownloaderBaseImpl dl = (ResourceDownloaderBaseImpl)_downloader;
/*     */     
/* 268 */     URL target = null;
/*     */     
/*     */     for (;;)
/*     */     {
/* 272 */       List kids = dl.getChildren();
/*     */       
/* 274 */       if (kids.size() == 0)
/*     */       {
/* 276 */         target = ((ResourceDownloaderURLImpl)dl).getURL();
/*     */         
/* 278 */         break;
/*     */       }
/*     */       
/* 281 */       dl = (ResourceDownloaderBaseImpl)kids.get(0);
/*     */     }
/*     */     
/*     */     ResourceDownloader result;
/*     */     ResourceDownloader result;
/* 286 */     if (target == null)
/*     */     {
/* 288 */       if (Logger.isEnabled()) {
/* 289 */         Logger.log(new LogEvent(LOGID, "ResourceDownloader: suffix based downloader failed to find leaf"));
/*     */       }
/*     */       
/* 292 */       result = _downloader;
/*     */     } else {
/*     */       ResourceDownloader result;
/* 295 */       if (target.getPath().toLowerCase().endsWith(".torrent"))
/*     */       {
/* 297 */         result = getTorrentDownloader(_downloader, true);
/*     */       }
/*     */       else
/*     */       {
/* 301 */         result = _downloader;
/*     */       }
/*     */     }
/*     */     
/* 305 */     if (COConfigurationManager.getBooleanParameter("update.anonymous"))
/*     */     {
/*     */ 
/*     */       try
/*     */       {
/*     */ 
/* 311 */         ((ResourceDownloaderBaseImpl)result).setPropertyRecursive("Anonymous", Boolean.valueOf(true));
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 315 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */     
/* 319 */     return result;
/*     */   }
/*     */   
/*     */   public ResourceDownloader[] getSourceforgeDownloaders(String project_name, String filename) {
/* 323 */     String template = "http://%s.dl.sourceforge.net/sourceforge/" + project_name + "/" + filename;
/* 324 */     ResourceDownloader[] result = new ResourceDownloader[SF_MIRRORS.length];
/*     */     
/* 326 */     for (int i = 0; i < result.length; i++) {
/* 327 */       String url = template.replaceFirst("%s", SF_MIRRORS[i]);
/* 328 */       try { result[i] = create(new URL(url));
/* 329 */       } catch (MalformedURLException me) { throw new RuntimeException(me);
/*     */       } }
/* 331 */     return result;
/*     */   }
/*     */   
/*     */   public ResourceDownloader getSourceforgeDownloader(String project_name, String filename) {
/* 335 */     return getRandomDownloader(getSourceforgeDownloaders(project_name, filename));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderFactoryImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */