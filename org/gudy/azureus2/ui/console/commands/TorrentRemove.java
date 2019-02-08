/*    */ package org.gudy.azureus2.ui.console.commands;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.util.List;
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.global.GlobalManager;
/*    */ import org.gudy.azureus2.core3.global.GlobalManagerDownloadRemovalVetoException;
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
/*    */ public class TorrentRemove
/*    */   extends TorrentCommand
/*    */ {
/*    */   public TorrentRemove()
/*    */   {
/* 38 */     super("remove", "r", "Removing");
/*    */   }
/*    */   
/*    */   protected boolean performCommand(ConsoleInput ci, DownloadManager dm, List args) {
/*    */     try {
/* 43 */       ci.getGlobalManager().removeDownloadManager(dm);
/*    */     } catch (GlobalManagerDownloadRemovalVetoException e) {
/* 45 */       ci.out.println("> Veto when removing torrent (" + e.getMessage() + ")");
/* 46 */       return false;
/*    */     } catch (Exception e) {
/* 48 */       e.printStackTrace(ci.out);
/* 49 */       return false;
/*    */     }
/* 51 */     return true;
/*    */   }
/*    */   
/*    */   public String getCommandDescriptions() {
/* 55 */     return "remove (<torrentoptions>)\tr\tRemove torrent(s).";
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/TorrentRemove.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */