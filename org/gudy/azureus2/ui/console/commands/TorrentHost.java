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
/*    */ public class TorrentHost
/*    */   extends TorrentCommand
/*    */ {
/*    */   public TorrentHost()
/*    */   {
/* 41 */     super("host", null, "Hosting");
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
/* 54 */           ci.azureus_core.getTrackerHost().hostTorrent(torrent, true, false);
/*    */         } else {
/*    */           try
/*    */           {
/* 58 */             existing.remove();
/*    */           }
/*    */           catch (Throwable e)
/*    */           {
/* 62 */             e.printStackTrace();
/*    */           }
/*    */         }
/*    */       }
/*    */       catch (TRHostException e) {
/* 67 */         e.printStackTrace(ci.out);
/* 68 */         return false;
/*    */       }
/* 70 */       return true;
/*    */     }
/* 72 */     return false;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   protected boolean performCommand(ConsoleInput ci, TRHostTorrent torrent, List args)
/*    */   {
/*    */     try
/*    */     {
/* 81 */       torrent.remove();
/*    */       
/* 83 */       return true;
/*    */     }
/*    */     catch (Throwable e)
/*    */     {
/* 87 */       e.printStackTrace();
/*    */     }
/*    */     
/* 90 */     return false;
/*    */   }
/*    */   
/*    */   public String getCommandDescriptions() {
/* 94 */     return "host (<torrentoptions>)\t\t\tHost or stop hosting torrent(s).";
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/TorrentHost.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */