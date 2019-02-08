/*    */ package org.gudy.azureus2.ui.console.commands;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.util.List;
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.global.GlobalManager;
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
/*    */ public class Move
/*    */   extends IConsoleCommand
/*    */ {
/*    */   public Move()
/*    */   {
/* 26 */     super("move", "m");
/*    */   }
/*    */   
/*    */   public String getCommandDescriptions()
/*    */   {
/* 31 */     return "move <from #> [<to #>]\t\tm\tMove torrent from to to. If to is omitted, the torrent is moved to top or to the bottom if given negative.";
/*    */   }
/*    */   
/*    */   public void execute(String commandName, ConsoleInput ci, List args) {
/* 35 */     if (args.isEmpty())
/*    */     {
/* 37 */       ci.out.println("> Missing subcommand for 'move'\r\n> move syntax: move <#from> [<#to>]");
/* 38 */       return;
/*    */     }
/*    */     
/* 41 */     if (ci.torrents.isEmpty())
/*    */     {
/* 43 */       ci.out.println("> Command 'move': No torrents in list.");
/* 44 */       return;
/*    */     }
/*    */     
/*    */ 
/* 48 */     int nmoveto = -1;
/* 49 */     boolean moveto = false;
/*    */     int ncommand;
/* 51 */     try { ncommand = Integer.parseInt((String)args.get(0));
/* 52 */       if (args.size() > 1) {
/* 53 */         nmoveto = Integer.parseInt((String)args.get(1));
/* 54 */         moveto = true;
/*    */       }
/*    */     } catch (NumberFormatException e) {
/* 57 */       ci.out.println("> Command 'move': Subcommand '" + args.get(0) + "' unknown.");
/* 58 */       return;
/*    */     }
/* 60 */     int number = Math.abs(ncommand);
/* 61 */     if ((number == 0) || (number > ci.torrents.size())) {
/* 62 */       ci.out.println("> Command 'move': Torrent #" + Integer.toString(number) + " unknown.");
/* 63 */       return;
/*    */     }
/* 65 */     DownloadManager dm = (DownloadManager)ci.torrents.get(number - 1);
/* 66 */     String name = dm.getDisplayName();
/* 67 */     if (name == null) {
/* 68 */       name = "?";
/*    */     }
/* 70 */     GlobalManager gm = dm.getGlobalManager();
/*    */     
/* 72 */     if (moveto) {
/* 73 */       gm.moveTo(dm, nmoveto - 1);
/* 74 */       gm.fixUpDownloadManagerPositions();
/* 75 */       ci.out.println("> Torrent #" + Integer.toString(number) + " (" + name + ") moved to #" + Integer.toString(nmoveto) + ".");
/* 76 */     } else if (ncommand > 0) {
/* 77 */       if (gm.isMoveableUp(dm)) {
/* 78 */         while (gm.isMoveableUp(dm))
/* 79 */           gm.moveUp(dm);
/* 80 */         gm.fixUpDownloadManagerPositions();
/* 81 */         ci.out.println("> Torrent #" + Integer.toString(number) + " (" + name + ") moved to top.");
/*    */       } else {
/* 83 */         ci.out.println("> Torrent #" + Integer.toString(number) + " (" + name + ") already at top.");
/*    */       }
/*    */     }
/* 86 */     else if (gm.isMoveableDown(dm)) {
/* 87 */       while (gm.isMoveableDown(dm))
/* 88 */         gm.moveDown(dm);
/* 89 */       gm.fixUpDownloadManagerPositions();
/* 90 */       ci.out.println("> Torrent #" + Integer.toString(number) + " (" + name + ") moved to bottom.");
/*    */     } else {
/* 92 */       ci.out.println("> Torrent #" + Integer.toString(number) + " (" + name + ") already at bottom.");
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/Move.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */