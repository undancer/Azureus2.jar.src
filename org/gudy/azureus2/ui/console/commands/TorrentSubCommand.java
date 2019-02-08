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
/*    */ 
/*    */ 
/*    */ public abstract class TorrentSubCommand
/*    */   extends TorrentCommand
/*    */ {
/*    */   public TorrentSubCommand(String command_name, String short_name)
/*    */   {
/* 42 */     super(command_name, short_name, null);
/*    */   }
/*    */   
/*    */   public abstract boolean performCommand(ConsoleInput paramConsoleInput, DownloadManager paramDownloadManager, List<String> paramList);
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/TorrentSubCommand.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */