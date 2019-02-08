/*     */ package com.aelitis.azureus.util;
/*     */ 
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentManager;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
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
/*     */ public class DownloadUtils
/*     */ {
/*     */   private static TorrentAttribute ta_tracker_extensions;
/*     */   
/*     */   public static synchronized void initialise()
/*     */   {
/*  36 */     if (ta_tracker_extensions == null)
/*     */     {
/*  38 */       TorrentManager tm = PluginInitializer.getDefaultInterface().getTorrentManager();
/*     */       
/*  40 */       ta_tracker_extensions = tm.getAttribute("TrackerClientExtensions");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static synchronized void addTrackerExtension(Download download, String extension_prefix, String extension_value)
/*     */   {
/*  50 */     String extension = "&" + extension_prefix + "=" + extension_value;
/*     */     
/*  52 */     String value = download.getAttribute(ta_tracker_extensions);
/*     */     
/*  54 */     if (value != null)
/*     */     {
/*     */ 
/*     */ 
/*  58 */       if (value.contains(extension))
/*     */       {
/*  60 */         return;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*  65 */       if (value.contains(extension_prefix))
/*     */       {
/*  67 */         String[] bits = value.split("&");
/*     */         
/*  69 */         value = "";
/*     */         
/*  71 */         for (int i = 0; i < bits.length; i++)
/*     */         {
/*  73 */           String bit = bits[i].trim();
/*     */           
/*  75 */           if (bit.length() != 0)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*  80 */             if (!bit.startsWith(extension_prefix + "="))
/*     */             {
/*  82 */               value = value + "&" + bit;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*  87 */       value = value + extension;
/*     */     }
/*     */     else
/*     */     {
/*  91 */       value = extension;
/*     */     }
/*     */     
/*  94 */     download.setAttribute(ta_tracker_extensions, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static synchronized String getTrackerExtensions(Download download)
/*     */   {
/* 101 */     return download.getAttribute(ta_tracker_extensions);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static synchronized void removeTrackerExtension(Download download, String extension_prefix)
/*     */   {
/* 109 */     String value = download.getAttribute(ta_tracker_extensions);
/*     */     
/* 111 */     if (value != null)
/*     */     {
/* 113 */       int pos = value.indexOf(extension_prefix);
/*     */       
/* 115 */       if (pos == -1)
/*     */       {
/* 117 */         return;
/*     */       }
/*     */       
/* 120 */       String[] bits = value.split("&");
/*     */       
/* 122 */       value = "";
/*     */       
/* 124 */       for (int i = 0; i < bits.length; i++)
/*     */       {
/* 126 */         String bit = bits[i].trim();
/*     */         
/* 128 */         if (bit.length() != 0)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 133 */           if (!bit.startsWith(extension_prefix + "="))
/*     */           {
/* 135 */             value = value + "&" + bit;
/*     */           }
/*     */         }
/*     */       }
/* 139 */       if (value.length() == 0)
/*     */       {
/* 141 */         value = null;
/*     */       }
/*     */       
/* 144 */       download.setAttribute(ta_tracker_extensions, value);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/util/DownloadUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */