/*    */ package org.gudy.azureus2.ui.console.commands;
/*    */ 
/*    */ import java.util.List;
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TorrentQueue
/*    */   extends TorrentCommand
/*    */ {
/* 37 */   public TorrentQueue() { super("queue", "q", "Queueing"); }
/*    */   
/*    */   protected boolean performCommand(ConsoleInput ci, DownloadManager dm, List args) {
/*    */     try {
/* 41 */       if (dm.getState() == 70) {
/* 42 */         dm.setStateQueued();
/* 43 */       } else if ((dm.getState() == 50) || (dm.getState() == 60)) {
/* 44 */         dm.stopIt(75, false, false);
/*    */       } else
/* 46 */         return false;
/*    */     } catch (Exception e) {
/* 48 */       e.printStackTrace(ci.out);
/* 49 */       return false;
/*    */     }
/* 51 */     return true;
/*    */   }
/*    */   
/*    */   public String getCommandDescriptions() {
/* 55 */     return "queue (<torrentoptions>)\tq\tQueue torrent(s).";
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/TorrentQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */