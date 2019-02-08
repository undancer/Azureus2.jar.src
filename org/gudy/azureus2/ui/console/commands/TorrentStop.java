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
/*    */ 
/*    */ public class TorrentStop
/*    */   extends TorrentCommand
/*    */ {
/*    */   public TorrentStop()
/*    */   {
/* 40 */     super("stop", "h", "Stopping");
/*    */   }
/*    */   
/*    */   protected boolean performCommand(ConsoleInput ci, DownloadManager dm, List args) {
/*    */     try {
/* 45 */       dm.stopIt(70, false, false);
/*    */     } catch (Exception e) {
/* 47 */       e.printStackTrace(ci.out);
/* 48 */       return false;
/*    */     }
/* 50 */     return true;
/*    */   }
/*    */   
/*    */   public String getCommandDescriptions() {
/* 54 */     return "stop (<torrentoptions>)\t\th\tStop torrent(s).";
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/TorrentStop.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */