/*    */ package org.gudy.azureus2.ui.console.commands;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.util.List;
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.plugins.download.Download;
/*    */ import org.gudy.azureus2.plugins.download.DownloadRemovalVetoException;
/*    */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
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
/*    */ public class TorrentArchive
/*    */   extends TorrentCommand
/*    */ {
/*    */   public TorrentArchive()
/*    */   {
/* 36 */     super("torrent_archive", "tar", "Archiving");
/*    */   }
/*    */   
/*    */   protected boolean performCommand(ConsoleInput ci, DownloadManager dm, List args) {
/*    */     try {
/* 41 */       Download download = PluginCoreUtils.wrap(dm);
/*    */       
/* 43 */       if (!download.canStubbify())
/*    */       {
/* 45 */         ci.out.println("> Can't archive as torrent is not in archiveable state");
/*    */       }
/*    */       else
/*    */       {
/* 49 */         download.stubbify();
/*    */         
/* 51 */         return true;
/*    */       }
/*    */     } catch (DownloadRemovalVetoException e) {
/* 54 */       ci.out.println("> Veto when archiving torrent (" + e.getMessage() + ")");
/*    */     }
/*    */     catch (Exception e) {
/* 57 */       e.printStackTrace(ci.out);
/*    */     }
/*    */     
/* 60 */     return false;
/*    */   }
/*    */   
/*    */   public String getCommandDescriptions() {
/* 64 */     return "torrent_archive (<torrentoptions>)\tr\tArchive torrent(s).";
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/TorrentArchive.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */