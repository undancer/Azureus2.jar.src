/*    */ package org.gudy.azureus2.core3.torrentdownloader.impl;
/*    */ 
/*    */ import java.io.File;
/*    */ import org.apache.log4j.Logger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TorrentDownloaderLoggedImpl
/*    */   extends TorrentDownloaderImpl
/*    */ {
/*    */   public void notifyListener()
/*    */   {
/* 27 */     super.notifyListener();
/* 28 */     switch (getDownloadState()) {
/*    */     case 0: 
/* 30 */       Logger.getLogger("azureus2.torrentdownloader").info("Download of '" + getFile().getName() + "' queued.");
/* 31 */       break;
/*    */     case 1: 
/* 33 */       Logger.getLogger("azureus2.torrentdownloader").info("Download of '" + getFile().getName() + "' started.");
/* 34 */       break;
/*    */     case 3: 
/* 36 */       Logger.getLogger("azureus2.torrentdownloader").info("Download of '" + getFile().getName() + "' finished.");
/* 37 */       break;
/*    */     case 4: 
/* 39 */       Logger.getLogger("azureus2.torrentdownloader").error(getError());
/* 40 */       break;
/*    */     case 5: 
/* 42 */       Logger.getLogger("azureus2.torrentdownloader").error("Download of '" + getFile().getName() + "' cancelled. File is already queued or downloading.");
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/torrentdownloader/impl/TorrentDownloaderLoggedImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */