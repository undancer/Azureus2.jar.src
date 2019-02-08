/*     */ package com.aelitis.azureus.ui.utils;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderAdapter;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader.ResourceDownloaderFactoryImpl;
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
/*     */ public class ImageBytesDownloader
/*     */ {
/*  41 */   static final Map<String, List<ImageDownloaderListener>> map = new HashMap();
/*     */   
/*  43 */   static final AEMonitor mon_map = new AEMonitor("ImageDownloaderMap");
/*     */   
/*     */   public static void loadImage(String url, ImageDownloaderListener l)
/*     */   {
/*  47 */     mon_map.enter();
/*     */     try {
/*  49 */       List<ImageDownloaderListener> list = (List)map.get(url);
/*  50 */       if (list != null) {
/*  51 */         list.add(l); return;
/*     */       }
/*     */       
/*     */ 
/*  55 */       list = new ArrayList(1);
/*  56 */       list.add(l);
/*  57 */       map.put(url, list);
/*     */     } finally {
/*  59 */       mon_map.exit();
/*     */     }
/*     */     try
/*     */     {
/*  63 */       URL u = new URL(url);
/*     */       
/*     */       ResourceDownloader rd;
/*     */       ResourceDownloader rd;
/*  67 */       if (AENetworkClassifier.categoriseAddress(u.getHost()) == "Public")
/*     */       {
/*  69 */         rd = ResourceDownloaderFactoryImpl.getSingleton().create(u);
/*     */       }
/*     */       else
/*     */       {
/*  73 */         rd = ResourceDownloaderFactoryImpl.getSingleton().createWithAutoPluginProxy(u);
/*     */       }
/*     */       
/*  76 */       rd.addListener(new ResourceDownloaderAdapter() {
/*     */         public boolean completed(ResourceDownloader downloader, InputStream is) {
/*  78 */           ImageBytesDownloader.mon_map.enter();
/*     */           try {
/*  80 */             List<ImageBytesDownloader.ImageDownloaderListener> list = (List)ImageBytesDownloader.map.get(this.val$url);
/*     */             
/*  82 */             if (list != null) {
/*     */               try {
/*  84 */                 if ((is != null) && (is.available() > 0)) {
/*  85 */                   newImageBytes = new byte[is.available()];
/*  86 */                   is.read(newImageBytes);
/*     */                   
/*  88 */                   for (ImageBytesDownloader.ImageDownloaderListener l : list)
/*     */                     try {
/*  90 */                       l.imageDownloaded(newImageBytes);
/*     */                     } catch (Exception e) {
/*  92 */                       Debug.out(e);
/*     */                     }
/*     */                 }
/*     */               } catch (Exception e) {
/*     */                 byte[] newImageBytes;
/*  97 */                 Debug.out(e);
/*     */               }
/*     */             }
/*     */             
/*     */ 
/* 102 */             ImageBytesDownloader.map.remove(this.val$url);
/*     */           } finally {
/* 104 */             ImageBytesDownloader.mon_map.exit();
/*     */           }
/*     */           
/* 107 */           return false;
/*     */         }
/* 109 */       });
/* 110 */       rd.asyncDownload();
/*     */     } catch (Exception e) {
/* 112 */       Debug.out(url, e);
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface ImageDownloaderListener
/*     */   {
/*     */     public abstract void imageDownloaded(byte[] paramArrayOfByte);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/utils/ImageBytesDownloader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */