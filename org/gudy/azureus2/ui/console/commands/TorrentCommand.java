/*     */ package org.gudy.azureus2.ui.console.commands;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHost;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrent;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.ui.console.ConsoleInput;
/*     */ import org.gudy.azureus2.ui.console.UserProfile;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class TorrentCommand
/*     */   extends IConsoleCommand
/*     */ {
/*     */   private final String action;
/*     */   
/*     */   public TorrentCommand(String main_name, String short_name, String action)
/*     */   {
/*  42 */     super(main_name, short_name);
/*  43 */     this.action = action;
/*     */   }
/*     */   
/*     */   protected String getAction()
/*     */   {
/*  48 */     return this.action;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract boolean performCommand(ConsoleInput paramConsoleInput, DownloadManager paramDownloadManager, List<String> paramList);
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean performCommand(ConsoleInput ci, TRHostTorrent torrent, List<String> args)
/*     */   {
/*  60 */     return false;
/*     */   }
/*     */   
/*     */   public void execute(String commandName, ConsoleInput ci, List<String> args)
/*     */   {
/*  65 */     if (!args.isEmpty()) {
/*  66 */       String subcommand = (String)args.remove(0);
/*  67 */       if (ci.torrents.isEmpty()) {
/*  68 */         ci.out.println("> Command '" + getCommandName() + "': No torrents in list (Maybe you forgot to 'show torrents' first).");
/*     */       }
/*     */       else {
/*     */         try
/*     */         {
/*  73 */           int number = Integer.parseInt(subcommand);
/*  74 */           if ((number > 0) && (number <= ci.torrents.size())) {
/*  75 */             DownloadManager dm = (DownloadManager)ci.torrents.get(number - 1);
/*  76 */             String name; String name; if (dm.getDisplayName() == null) {
/*  77 */               name = "?";
/*     */             } else
/*  79 */               name = dm.getDisplayName();
/*  80 */             performCommandIfAllowed(ci, args, dm, "#" + subcommand, name);
/*     */           } else {
/*  82 */             ci.out.println("> Command '" + getCommandName() + "': Torrent #" + subcommand + " unknown.");
/*     */           }
/*  84 */         } catch (NumberFormatException e) { if ("all".equalsIgnoreCase(subcommand)) {
/*  85 */             Iterator torrent = ci.torrents.iterator();
/*  86 */             while (torrent.hasNext()) {
/*  87 */               DownloadManager dm = (DownloadManager)torrent.next();
/*  88 */               String name; String name; if (dm.getDisplayName() == null) {
/*  89 */                 name = "?";
/*     */               } else
/*  91 */                 name = dm.getDisplayName();
/*  92 */               performCommandIfAllowed(ci, args, dm, subcommand, name);
/*     */             }
/*  94 */           } else if ("hash".equalsIgnoreCase(subcommand)) {
/*  95 */             String hash = (String)args.remove(0);
/*  96 */             List torrents = ci.getGlobalManager().getDownloadManagers();
/*  97 */             boolean foundit = false;
/*  98 */             Iterator torrent = torrents.iterator();
/*  99 */             while (torrent.hasNext()) {
/* 100 */               DownloadManager dm = (DownloadManager)torrent.next();
/* 101 */               if (hash.equals(TorrentUtils.nicePrintTorrentHash(dm.getTorrent(), true))) { String name;
/* 102 */                 String name; if (dm.getDisplayName() == null) {
/* 103 */                   name = "?";
/*     */                 } else {
/* 105 */                   name = dm.getDisplayName();
/*     */                 }
/*     */                 
/* 108 */                 performCommandIfAllowed(ci, args, dm, hash, name);
/* 109 */                 foundit = true;
/*     */               }
/*     */             }
/*     */             
/* 113 */             if (!foundit)
/*     */             {
/*     */ 
/*     */ 
/* 117 */               TRHost host = ci.getCore().getTrackerHost();
/*     */               
/* 119 */               if (host != null)
/*     */               {
/* 121 */                 TRHostTorrent[] h_torrents = host.getTorrents();
/*     */                 
/* 123 */                 for (int i = 0; i < h_torrents.length; i++)
/*     */                 {
/* 125 */                   TRHostTorrent ht = h_torrents[i];
/*     */                   
/* 127 */                   if (hash.equals(TorrentUtils.nicePrintTorrentHash(ht.getTorrent(), true)))
/*     */                   {
/* 129 */                     String name = TorrentUtils.getLocalisedName(ht.getTorrent());
/*     */                     
/*     */ 
/*     */ 
/* 133 */                     performCommandIfAllowed(ci, args, ht, hash, name);
/* 134 */                     foundit = true;
/* 135 */                     break;
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/*     */ 
/* 142 */             if (!foundit) {
/* 143 */               ci.out.println("> Command '" + getCommandName() + "': Hash '" + hash + "' unknown.");
/*     */             }
/*     */           } else {
/* 146 */             ci.out.println("> Command '" + getCommandName() + "': Subcommand '" + subcommand + "' unknown.");
/*     */           }
/*     */         }
/*     */       }
/*     */     } else {
/* 151 */       ci.out.println("> Missing subcommand for '" + getCommandName() + "'");
/* 152 */       printHelp(ci.out, args);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void performCommandIfAllowed(ConsoleInput ci, List args, DownloadManager dm, String desc, String name)
/*     */   {
/* 168 */     if (!"admin".equals(ci.getUserProfile().getUserType()))
/*     */     {
/* 170 */       if ("user".equals(ci.getUserProfile().getUserType()))
/*     */       {
/* 172 */         String owner = dm.getDownloadState().getAttribute("user");
/* 173 */         if (!ci.getUserProfile().getUsername().equals(owner))
/*     */         {
/* 175 */           ci.out.println("> " + getAction() + " torrent " + desc + " (" + name + ") failed: Permission Denied. Users can only modify their own torrents");
/* 176 */           return;
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 181 */         ci.out.println("> " + getAction() + " torrent " + desc + " (" + name + ") failed: Permission Denied. Guests cannot modify torrents");
/* 182 */         return;
/*     */       }
/*     */     }
/* 185 */     if (performCommand(ci, dm, args)) {
/* 186 */       ci.out.println("> " + getAction() + " Torrent " + desc + " (" + name + ") succeeded.");
/*     */     } else {
/* 188 */       ci.out.println("> " + getAction() + " Torrent " + desc + " (" + name + ") failed.");
/*     */     }
/*     */   }
/*     */   
/*     */   private void performCommandIfAllowed(ConsoleInput ci, List args, TRHostTorrent torrent, String desc, String name) {
/* 193 */     if (!"admin".equals(ci.getUserProfile().getUserType()))
/*     */     {
/* 195 */       if (!"user".equals(ci.getUserProfile().getUserType()))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 201 */         ci.out.println("> " + getAction() + " torrent " + desc + " (" + name + ") failed: Permission Denied. Guests cannot modify torrents");
/* 202 */         return;
/*     */       }
/*     */     }
/* 205 */     if (performCommand(ci, torrent, args)) {
/* 206 */       ci.out.println("> " + getAction() + " Torrent " + desc + " (" + name + ") succeeded.");
/*     */     } else {
/* 208 */       ci.out.println("> " + getAction() + " Torrent " + desc + " (" + name + ") failed.");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void printHelpExtra(PrintStream out, List args)
/*     */   {
/* 216 */     out.println("> " + getCommandName() + " syntax: " + getCommandName() + " (<#>|all|hash <hash>)");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/TorrentCommand.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */