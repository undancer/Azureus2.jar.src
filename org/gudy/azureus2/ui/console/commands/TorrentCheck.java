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
/*    */ public class TorrentCheck
/*    */   extends TorrentCommand
/*    */ {
/*    */   public TorrentCheck()
/*    */   {
/* 37 */     super("check", "c", "Initiating recheck of");
/*    */   }
/*    */   
/*    */   protected boolean performCommand(ConsoleInput ci, DownloadManager dm, List args) {
/*    */     try {
/* 42 */       if (dm.canForceRecheck()) {
/* 43 */         dm.forceRecheck();
/* 44 */         return true;
/*    */       }
/* 46 */       return false;
/*    */     } catch (Exception e) {
/* 48 */       e.printStackTrace(ci.out); }
/* 49 */     return false;
/*    */   }
/*    */   
/*    */   public String getCommandDescriptions()
/*    */   {
/* 54 */     return "check (<torrentoptions>)\tc\tForce recheck on torrent(s).";
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/TorrentCheck.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */