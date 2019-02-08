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
/*    */ public class TorrentForceStart
/*    */   extends TorrentCommand
/*    */ {
/* 36 */   public TorrentForceStart() { super("forcestart", null, "Force Starting"); }
/*    */   
/*    */   protected boolean performCommand(ConsoleInput ci, DownloadManager dm, List args) {
/*    */     try {
/* 40 */       dm.setForceStart(true);
/*    */     } catch (Exception e) {
/* 42 */       e.printStackTrace(ci.out);
/* 43 */       return false;
/*    */     }
/* 45 */     return true;
/*    */   }
/*    */   
/*    */   public String getCommandDescriptions() {
/* 49 */     return "forcestart (<torrentoptions>)\t\tStart torrent ignoring other limits/rules.";
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/TorrentForceStart.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */