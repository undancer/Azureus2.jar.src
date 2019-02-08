/*     */ package com.aelitis.azureus.plugins.extseed.impl.webseed;
/*     */ 
/*     */ import com.aelitis.azureus.plugins.extseed.ExternalSeedPlugin;
/*     */ import com.aelitis.azureus.plugins.extseed.ExternalSeedReader;
/*     */ import com.aelitis.azureus.plugins.extseed.ExternalSeedReaderFactory;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ExternalSeedReaderFactoryWebSeed
/*     */   implements ExternalSeedReaderFactory
/*     */ {
/*     */   public ExternalSeedReader[] getSeedReaders(ExternalSeedPlugin plugin, Download download)
/*     */   {
/*  47 */     return getSeedReaders(plugin, download.getName(), download.getTorrent());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ExternalSeedReader[] getSeedReaders(ExternalSeedPlugin plugin, Torrent torrent)
/*     */   {
/*  55 */     return getSeedReaders(plugin, torrent.getName(), torrent);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private ExternalSeedReader[] getSeedReaders(ExternalSeedPlugin plugin, String name, Torrent torrent)
/*     */   {
/*     */     try
/*     */     {
/*  65 */       Map config = new HashMap();
/*     */       
/*  67 */       Object obj = torrent.getAdditionalProperty("httpseeds");
/*     */       
/*  69 */       if (obj != null)
/*     */       {
/*  71 */         config.put("httpseeds", obj);
/*     */       }
/*     */       
/*  74 */       return getSeedReaders(plugin, name, torrent, config);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  78 */       e.printStackTrace();
/*     */     }
/*     */     
/*  81 */     return new ExternalSeedReader[0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ExternalSeedReader[] getSeedReaders(ExternalSeedPlugin plugin, Download download, Map config)
/*     */   {
/*  90 */     return getSeedReaders(plugin, download.getName(), download.getTorrent(), config);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private ExternalSeedReader[] getSeedReaders(ExternalSeedPlugin plugin, String name, Torrent torrent, Map config)
/*     */   {
/*     */     try
/*     */     {
/* 101 */       Object obj = config.get("httpseeds");
/*     */       
/*     */ 
/*     */ 
/* 105 */       if ((obj instanceof byte[]))
/*     */       {
/* 107 */         List l = new ArrayList();
/*     */         
/* 109 */         l.add(obj);
/*     */         
/* 111 */         obj = l;
/*     */       }
/*     */       
/* 114 */       if ((obj instanceof List))
/*     */       {
/* 116 */         List urls = (List)obj;
/*     */         
/* 118 */         List readers = new ArrayList();
/*     */         
/* 120 */         Object _params = config.get("httpseeds-params");
/*     */         
/* 122 */         Map params = (_params instanceof Map) ? (Map)_params : new HashMap();
/*     */         
/* 124 */         Collections.shuffle(urls);
/*     */         
/* 126 */         for (int i = 0; i < urls.size(); i++)
/*     */         {
/* 128 */           if (readers.size() > 10) {
/*     */             break;
/*     */           }
/*     */           
/*     */           try
/*     */           {
/* 134 */             String url_str = new String((byte[])urls.get(i));
/*     */             
/*     */ 
/*     */ 
/* 138 */             url_str = url_str.replaceAll(" ", "%20");
/*     */             
/* 140 */             URL url = new URL(url_str);
/*     */             
/* 142 */             String protocol = url.getProtocol().toLowerCase();
/*     */             
/* 144 */             if (protocol.equals("http"))
/*     */             {
/* 146 */               readers.add(new ExternalSeedReaderWebSeed(plugin, torrent, url, params));
/*     */             }
/*     */             else
/*     */             {
/* 150 */               plugin.log(name + ": WS unsupported protocol: " + url);
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {
/* 154 */             Object o = urls.get(i);
/*     */             
/* 156 */             String str = (o instanceof byte[]) ? new String((byte[])o) : String.valueOf(o);
/*     */             
/* 158 */             Debug.out("WS seed invalid: " + str, e);
/*     */           }
/*     */         }
/*     */         
/* 162 */         ExternalSeedReader[] res = new ExternalSeedReader[readers.size()];
/*     */         
/* 164 */         readers.toArray(res);
/*     */         
/* 166 */         return res;
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 170 */       e.printStackTrace();
/*     */     }
/*     */     
/* 173 */     return new ExternalSeedReader[0];
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/extseed/impl/webseed/ExternalSeedReaderFactoryWebSeed.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */