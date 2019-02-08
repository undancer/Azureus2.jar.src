/*     */ package com.aelitis.azureus.plugins.extseed.impl.webseed;
/*     */ 
/*     */ import com.aelitis.azureus.plugins.extseed.ExternalSeedException;
/*     */ import com.aelitis.azureus.plugins.extseed.ExternalSeedPlugin;
/*     */ import com.aelitis.azureus.plugins.extseed.ExternalSeedReader;
/*     */ import com.aelitis.azureus.plugins.extseed.impl.ExternalSeedReaderImpl;
/*     */ import com.aelitis.azureus.plugins.extseed.util.ExternalSeedHTTPDownloader;
/*     */ import com.aelitis.azureus.plugins.extseed.util.ExternalSeedHTTPDownloaderListener;
/*     */ import com.aelitis.azureus.plugins.extseed.util.ExternalSeedHTTPDownloaderRange;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.net.URLEncoder;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ 
/*     */ 
/*     */ public class ExternalSeedReaderWebSeed
/*     */   extends ExternalSeedReaderImpl
/*     */ {
/*     */   private URL url;
/*     */   private int port;
/*     */   private String url_prefix;
/*     */   private boolean supports_503;
/*     */   
/*     */   protected ExternalSeedReaderWebSeed(ExternalSeedPlugin _plugin, Torrent _torrent, URL _url, Map _params)
/*     */   {
/*  55 */     super(_plugin, _torrent, _url.getHost(), _params);
/*     */     
/*  57 */     this.supports_503 = getBooleanParam(_params, "supports_503", true);
/*     */     
/*  59 */     this.url = _url;
/*     */     
/*  61 */     this.port = this.url.getPort();
/*     */     
/*  63 */     if (this.port == -1)
/*     */     {
/*  65 */       this.port = this.url.getDefaultPort();
/*     */     }
/*     */     try
/*     */     {
/*  69 */       String hash_str = URLEncoder.encode(new String(_torrent.getHash(), "ISO-8859-1"), "ISO-8859-1").replaceAll("\\+", "%20");
/*     */       
/*  71 */       this.url_prefix = (this.url.toString() + "?info_hash=" + hash_str);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  75 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean sameAs(ExternalSeedReader other)
/*     */   {
/*  83 */     if ((other instanceof ExternalSeedReaderWebSeed))
/*     */     {
/*  85 */       return this.url.toString().equals(((ExternalSeedReaderWebSeed)other).url.toString());
/*     */     }
/*     */     
/*  88 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  94 */     return "WebSeed: " + this.url;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getType()
/*     */   {
/* 100 */     return "WebSeed";
/*     */   }
/*     */   
/*     */ 
/*     */   public URL getURL()
/*     */   {
/* 106 */     return this.url;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 112 */     return this.port;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected int getPieceGroupSize()
/*     */   {
/* 119 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean getRequestCanSpanPieces()
/*     */   {
/* 125 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void readData(int piece_number, int piece_offset, int length, ExternalSeedHTTPDownloaderListener listener)
/*     */     throws ExternalSeedException
/*     */   {
/* 137 */     long piece_end = piece_offset + length - 1;
/*     */     
/* 139 */     String str = this.url_prefix + "&piece=" + piece_number + "&ranges=" + piece_offset + "-" + piece_end;
/*     */     
/* 141 */     setReconnectDelay(30000, false);
/*     */     
/* 143 */     ExternalSeedHTTPDownloader http_downloader = null;
/*     */     try
/*     */     {
/* 146 */       http_downloader = new ExternalSeedHTTPDownloaderRange(new URL(str), getUserAgent());
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 153 */       if (this.supports_503)
/*     */       {
/* 155 */         http_downloader.downloadSocket(length, listener, isTransient());
/*     */       }
/*     */       else
/*     */       {
/* 159 */         http_downloader.download(length, listener, isTransient());
/*     */       }
/*     */     }
/*     */     catch (ExternalSeedException ese)
/*     */     {
/* 164 */       if ((http_downloader.getLastResponse() == 503) && (http_downloader.getLast503RetrySecs() >= 0))
/*     */       {
/* 166 */         int retry_secs = http_downloader.getLast503RetrySecs();
/*     */         
/* 168 */         setReconnectDelay(retry_secs * 1000, true);
/*     */         
/* 170 */         throw new ExternalSeedException("Server temporarily unavailable, retrying in " + retry_secs + " seconds");
/*     */       }
/*     */       
/*     */ 
/* 174 */       throw ese;
/*     */     }
/*     */     catch (MalformedURLException e)
/*     */     {
/* 178 */       throw new ExternalSeedException("URL encode fails", e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/extseed/impl/webseed/ExternalSeedReaderWebSeed.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */