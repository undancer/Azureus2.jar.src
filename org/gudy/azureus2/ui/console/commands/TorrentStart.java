/*    */ package org.gudy.azureus2.ui.console.commands;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.util.List;
/*    */ import java.util.Vector;
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
/*    */ 
/*    */ public class TorrentStart
/*    */   extends TorrentCommand
/*    */ {
/*    */   private boolean startNow;
/*    */   
/* 43 */   public TorrentStart() { super("start", "s", "Starting"); }
/*    */   
/*    */   public void execute(String commandName, ConsoleInput console, List<String> args) {
/* 46 */     this.startNow = false;
/* 47 */     Vector newargs = new Vector(args);
/* 48 */     if ((!newargs.isEmpty()) && (newargs.contains("now"))) {
/* 49 */       newargs.removeElement("now");
/* 50 */       this.startNow = true;
/*    */     }
/* 52 */     super.execute(commandName, console, args);
/*    */   }
/*    */   
/*    */   protected boolean performCommand(ConsoleInput ci, DownloadManager dm, List args) {
/*    */     try {
/* 57 */       int state = dm.getState();
/*    */       
/* 59 */       if (state != 70)
/*    */       {
/* 61 */         ci.out.println("Torrent isn't stopped");
/*    */         
/* 63 */         return false;
/*    */       }
/*    */       
/* 66 */       if (this.startNow)
/*    */       {
/* 68 */         ci.out.println("'now' option has been deprecated, use forcestart");
/*    */       }
/*    */       
/* 71 */       dm.stopIt(75, false, false);
/*    */     }
/*    */     catch (Exception e) {
/* 74 */       e.printStackTrace(ci.out);
/* 75 */       return false;
/*    */     }
/* 77 */     return true;
/*    */   }
/*    */   
/*    */   public String getCommandDescriptions() {
/* 81 */     return "start (<torrentoptions>) \ts\tStart torrent(s).";
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/TorrentStart.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */