/*     */ package com.aelitis.azureus.plugins.extseed.impl.getright;
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
/*     */ public class ExternalSeedReaderFactoryGetRight
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
/*  67 */       Object obj = torrent.getAdditionalProperty("url-list");
/*     */       
/*  69 */       if (obj != null)
/*     */       {
/*  71 */         config.put("url-list", obj);
/*     */       }
/*     */       
/*  74 */       obj = torrent.getAdditionalProperty("url-list-params");
/*     */       
/*  76 */       if (obj != null)
/*     */       {
/*  78 */         config.put("url-list-params", obj);
/*     */       }
/*     */       
/*  81 */       obj = torrent.getAdditionalProperty("url-list-params2");
/*     */       
/*  83 */       if (obj != null)
/*     */       {
/*  85 */         config.put("url-list-params2", obj);
/*     */       }
/*     */       
/*  88 */       return getSeedReaders(plugin, name, torrent, config);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  92 */       e.printStackTrace();
/*     */     }
/*     */     
/*  95 */     return new ExternalSeedReader[0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ExternalSeedReader[] getSeedReaders(ExternalSeedPlugin plugin, Download download, Map config)
/*     */   {
/* 104 */     return getSeedReaders(plugin, download.getName(), download.getTorrent(), config);
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
/* 115 */       Object obj = config.get("url-list");
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 120 */       if ((obj instanceof byte[])) {
/* 121 */         List l = new ArrayList();
/* 122 */         l.add(obj);
/* 123 */         obj = l;
/*     */       }
/*     */       
/* 126 */       if ((obj instanceof List))
/*     */       {
/* 128 */         List urls = (List)obj;
/*     */         
/* 130 */         List readers = new ArrayList();
/*     */         
/* 132 */         Object _global_params = config.get("url-list-params");
/* 133 */         Object _specific_params = config.get("url-list-params2");
/*     */         
/* 135 */         Map global_params = (_global_params instanceof Map) ? (Map)_global_params : new HashMap();
/* 136 */         List specific_params = (_specific_params instanceof List) ? (List)_specific_params : new ArrayList();
/*     */         
/* 138 */         Collections.shuffle(urls);
/*     */         
/* 140 */         for (int i = 0; i < urls.size(); i++)
/*     */         {
/* 142 */           if (readers.size() > 10) {
/*     */             break;
/*     */           }
/*     */           
/*     */ 
/* 147 */           Map my_params = global_params;
/*     */           
/* 149 */           if (i < specific_params.size())
/*     */           {
/* 151 */             Object o = specific_params.get(i);
/*     */             
/* 153 */             if ((o instanceof Map))
/*     */             {
/* 155 */               my_params = (Map)o;
/*     */             }
/*     */           }
/*     */           try
/*     */           {
/* 160 */             String url_str = new String((byte[])urls.get(i), "UTF-8");
/*     */             
/*     */ 
/*     */ 
/* 164 */             url_str = url_str.replaceAll(" ", "%20");
/*     */             
/* 166 */             if (url_str.length() > 0)
/*     */             {
/* 168 */               URL url = new URL(url_str);
/*     */               
/* 170 */               String protocol = url.getProtocol().toLowerCase();
/*     */               
/* 172 */               if (protocol.startsWith("http"))
/*     */               {
/* 174 */                 readers.add(new ExternalSeedReaderGetRight(plugin, torrent, url, my_params));
/*     */               }
/*     */               else
/*     */               {
/* 178 */                 plugin.log(name + ": GR unsupported protocol: " + url);
/*     */               }
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {
/* 183 */             Object o = urls.get(i);
/*     */             
/* 185 */             String str = (o instanceof byte[]) ? new String((byte[])o) : String.valueOf(o);
/*     */             
/* 187 */             Debug.out("GR seed invalid: " + str, e);
/*     */           }
/*     */         }
/*     */         
/* 191 */         ExternalSeedReader[] res = new ExternalSeedReader[readers.size()];
/*     */         
/* 193 */         readers.toArray(res);
/*     */         
/* 195 */         return res;
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 199 */       e.printStackTrace();
/*     */     }
/*     */     
/* 202 */     return new ExternalSeedReader[0];
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/extseed/impl/getright/ExternalSeedReaderFactoryGetRight.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */