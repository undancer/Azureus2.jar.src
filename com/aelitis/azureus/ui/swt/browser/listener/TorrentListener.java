/*     */ package com.aelitis.azureus.ui.swt.browser.listener;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManager;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManagerFactory;
/*     */ import com.aelitis.azureus.core.messenger.ClientMessageContext;
/*     */ import com.aelitis.azureus.core.messenger.ClientMessageContext.torrentURLHandler;
/*     */ import com.aelitis.azureus.core.messenger.browser.BrowserMessage;
/*     */ import com.aelitis.azureus.core.messenger.browser.listeners.AbstractBrowserMessageListener;
/*     */ import com.aelitis.azureus.core.torrent.PlatformTorrentUtils;
/*     */ import com.aelitis.azureus.ui.selectedcontent.DownloadUrlInfo;
/*     */ import com.aelitis.azureus.ui.selectedcontent.DownloadUrlInfoContentNetwork;
/*     */ import com.aelitis.azureus.ui.swt.utils.TorrentUIUtilsV3;
/*     */ import com.aelitis.azureus.util.MapUtils;
/*     */ import java.io.File;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.bouncycastle.util.encoders.Base64;
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
/*     */ public class TorrentListener
/*     */   extends AbstractBrowserMessageListener
/*     */ {
/*     */   public static final String DEFAULT_LISTENER_ID = "torrent";
/*     */   public static final String OP_LOAD_TORRENT_OLD = "loadTorrent";
/*     */   public static final String OP_LOAD_TORRENT = "load-torrent";
/*     */   private ClientMessageContext.torrentURLHandler torrentURLHandler;
/*     */   
/*     */   public TorrentListener(String id)
/*     */   {
/*  56 */     super(id);
/*     */   }
/*     */   
/*     */   public TorrentListener() {
/*  60 */     this("torrent");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setTorrentURLHandler(ClientMessageContext.torrentURLHandler handler)
/*     */   {
/*  67 */     this.torrentURLHandler = handler;
/*     */   }
/*     */   
/*     */   public void setShell(Shell shell) {}
/*     */   
/*     */   public void handleMessage(final BrowserMessage message)
/*     */   {
/*  74 */     String opid = message.getOperationId();
/*  75 */     if (("load-torrent".equals(opid)) || ("loadTorrent".equals(opid))) {
/*  76 */       final Map decodedMap = message.getDecodedMap();
/*  77 */       String url = MapUtils.getMapString(decodedMap, "url", null);
/*  78 */       final boolean playNow = MapUtils.getMapBoolean(decodedMap, "play-now", false);
/*  79 */       final boolean playPrepare = MapUtils.getMapBoolean(decodedMap, "play-prepare", false);
/*     */       
/*  81 */       final boolean bringToFront = MapUtils.getMapBoolean(decodedMap, "bring-to-front", true);
/*     */       
/*  83 */       if (url != null) {
/*  84 */         if (this.torrentURLHandler != null) {
/*     */           try
/*     */           {
/*  87 */             this.torrentURLHandler.handleTorrentURL(url);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/*  91 */             Debug.printStackTrace(e);
/*     */           }
/*     */         }
/*  94 */         final DownloadUrlInfo dlInfo = new DownloadUrlInfoContentNetwork(url, ContentNetworkManagerFactory.getSingleton().getContentNetwork(this.context.getContentNetworkID()));
/*     */         
/*     */ 
/*  97 */         dlInfo.setReferer(message.getReferer());
/*     */         
/*  99 */         AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */           public void azureusCoreRunning(AzureusCore core) {
/* 101 */             TorrentUIUtilsV3.loadTorrent(dlInfo, playNow, playPrepare, bringToFront);
/*     */           }
/*     */         });
/*     */       }
/*     */       else {
/* 106 */         AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */           public void azureusCoreRunning(AzureusCore core) {
/* 108 */             TorrentListener.loadTorrentByB64(core, message, MapUtils.getMapString(decodedMap, "b64", null));
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */     else {
/* 114 */       throw new IllegalArgumentException("Unknown operation: " + opid);
/*     */     }
/*     */   }
/*     */   
/*     */   public static boolean loadTorrentByB64(AzureusCore core, String b64) {
/* 119 */     return loadTorrentByB64(core, null, b64);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean loadTorrentByB64(AzureusCore core, BrowserMessage message, String b64)
/*     */   {
/* 129 */     if (b64 == null) {
/* 130 */       return false;
/*     */     }
/*     */     
/* 133 */     byte[] decodedTorrent = Base64.decode(b64);
/*     */     
/*     */     try
/*     */     {
/* 137 */       File tempTorrentFile = File.createTempFile("AZU", ".torrent");
/* 138 */       tempTorrentFile.deleteOnExit();
/* 139 */       String filename = tempTorrentFile.getAbsolutePath();
/* 140 */       FileUtil.writeBytesAsFile(filename, decodedTorrent);
/*     */       
/* 142 */       TOTorrent torrent = TorrentUtils.readFromFile(tempTorrentFile, false);
/*     */       
/* 144 */       if (!PlatformTorrentUtils.isPlatformTracker(torrent)) {
/* 145 */         Debug.out("stopped loading torrent because it's not in whitelist");
/* 146 */         return false;
/*     */       }
/*     */       
/* 149 */       String savePath = COConfigurationManager.getStringParameter("Default save path");
/* 150 */       if ((savePath == null) || (savePath.length() == 0)) {
/* 151 */         savePath = ".";
/*     */       }
/*     */       
/* 154 */       core.getGlobalManager().addDownloadManager(filename, savePath);
/*     */     } catch (Throwable t) {
/* 156 */       if (message != null) {
/* 157 */         message.debug("loadUrl error", t);
/*     */       } else {
/* 159 */         Debug.out(t);
/*     */       }
/* 161 */       return false;
/*     */     }
/* 163 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/browser/listener/TorrentListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */