/*    */ package org.gudy.azureus2.ui.console.commands;
/*    */ 
/*    */ import com.aelitis.azureus.core.AzureusCore;
/*    */ import java.util.List;
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*    */ import org.gudy.azureus2.core3.tracker.host.TRHost;
/*    */ import org.gudy.azureus2.core3.tracker.host.TRHostException;
/*    */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrent;
/*    */ import org.gudy.azureus2.ui.console.ConsoleInput;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TorrentPublish
/*    */   extends TorrentCommand
/*    */ {
/*    */   public TorrentPublish()
/*    */   {
/* 41 */     super("publish", null, "Publishing");
/*    */   }
/*    */   
/*    */   protected boolean performCommand(ConsoleInput ci, DownloadManager dm, List args) {
/* 45 */     TOTorrent torrent = dm.getTorrent();
/* 46 */     if (torrent != null) {
/*    */       try {
/* 48 */         TRHost host = ci.azureus_core.getTrackerHost();
/*    */         
/* 50 */         TRHostTorrent existing = host.getHostTorrent(torrent);
/*    */         
/* 52 */         if (existing == null)
/*    */         {
/* 54 */           host.publishTorrent(torrent);
/*    */         } else {
/*    */           try {
/* 57 */             existing.remove();
/*    */           }
/*    */           catch (Throwable e)
/*    */           {
/* 61 */             e.printStackTrace();
/*    */           }
/*    */         }
/*    */       } catch (TRHostException e) {
/* 65 */         e.printStackTrace(ci.out);
/* 66 */         return false;
/*    */       }
/* 68 */       return true;
/*    */     }
/* 70 */     return false;
/*    */   }
/*    */   
/*    */   public String getCommandDescriptions() {
/* 74 */     return "publish (<torrentoptions>)\t\tPublish or stop publishing torrent(s).";
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/TorrentPublish.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */