/*     */ package org.gudy.azureus2.ui.console.commands;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.dht.DHT;
/*     */ import com.aelitis.azureus.core.dht.control.DHTControl;
/*     */ import com.aelitis.azureus.core.dht.control.DHTControlStats;
/*     */ import com.aelitis.azureus.core.dht.db.DHTDB;
/*     */ import com.aelitis.azureus.core.dht.db.DHTDBStats;
/*     */ import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPosition;
/*     */ import com.aelitis.azureus.core.dht.router.DHTRouter;
/*     */ import com.aelitis.azureus.core.dht.router.DHTRouterStats;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransport;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportStats;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*     */ import com.aelitis.azureus.core.peermanager.piecepicker.PiecePicker;
/*     */ import com.aelitis.azureus.core.stats.AzureusCoreStats;
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagManager;
/*     */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*     */ import com.aelitis.azureus.plugins.dht.DHTPlugin;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.text.DecimalFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.category.Category;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManagerStats;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManagerStats;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerStats;
/*     */ import org.gudy.azureus2.core3.peer.PEPiece;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.core3.util.IndentWriter;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.ui.console.ConsoleInput;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Show
/*     */   extends IConsoleCommand
/*     */ {
/*     */   public Show()
/*     */   {
/*  67 */     super("show", "sh");
/*     */   }
/*     */   
/*     */   public String getCommandDescriptions() {
/*  71 */     return "show [<various options>]\tsh\tShow info. Use without parameter to get a list of available options.";
/*     */   }
/*     */   
/*     */   public void printHelpExtra(PrintStream out, List args) {
/*  75 */     out.println("> -----");
/*  76 */     out.println("'show' options: ");
/*  77 */     out.println("<#>\t\t\t\tFurther info on a single torrent - args from [peers|pieces]+. Run 'show torrents' first for the number.");
/*  78 */     out.println("options\t\t\to\tShow list of options for 'set' (also available by 'set' without parameters).");
/*  79 */     out.println("files\t\t\tf\tShow list of files found from the 'add -f' command (also available by 'add -l')");
/*  80 */     out.println("dht\t\t\td\tShow distributed database statistics");
/*  81 */     out.println("nat\t\t\tn\tShow NAT status");
/*  82 */     out.println("stats [pattern] [on|off]\ts\tShow stats [with a given pattern] [turn averages on/off]");
/*  83 */     out.println("torrents [opts] [expr]\tt\tShow list of torrents. torrent options may be any (or none) of:");
/*  84 */     out.println("\t\ttransferring\tx\tShow only transferring torrents.");
/*  85 */     out.println("\t\tactive\t\ta\tShow only active torrents.");
/*  86 */     out.println("\t\tcomplete\tc\tShow only complete torrents.");
/*  87 */     out.println("\t\tincomplete\ti\tShow only incomplete torrents.");
/*  88 */     out.println("\t\tdead [days]\td [days]Show only dead torrents (complete and not uploaded for [days] (default 7) uptime (NOT elapsed)).");
/*  89 */     out.println("\te.g. show t a *Az* - shows all active torrents with 'Az' occurring in their name.");
/*  90 */     out.println("> -----");
/*     */   }
/*     */   
/*     */   public void execute(String commandName, ConsoleInput ci, List args) {
/*  94 */     if (args.isEmpty())
/*     */     {
/*  96 */       printHelp(ci.out, args);
/*  97 */       return;
/*     */     }
/*  99 */     String subCommand = (String)args.remove(0);
/* 100 */     if ((subCommand.equalsIgnoreCase("options")) || (subCommand.equalsIgnoreCase("o"))) {
/* 101 */       ci.invokeCommand("set", null);
/* 102 */     } else if ((subCommand.equalsIgnoreCase("files")) || (subCommand.equalsIgnoreCase("f"))) {
/* 103 */       ci.invokeCommand("add", Arrays.asList(new String[] { "--list" }));
/* 104 */     } else if ((subCommand.equalsIgnoreCase("torrents")) || (subCommand.equalsIgnoreCase("t"))) {
/* 105 */       ci.out.println("> -----");
/* 106 */       ci.torrents.clear();
/* 107 */       ci.torrents.addAll(ci.getGlobalManager().getDownloadManagers());
/* 108 */       Collections.sort(ci.torrents, new IConsoleCommand.TorrentComparator());
/*     */       
/* 110 */       if (ci.torrents.isEmpty()) {
/* 111 */         ci.out.println("No Torrents");
/* 112 */         ci.out.println("> -----");
/* 113 */         return;
/*     */       }
/*     */       
/* 116 */       long totalReceived = 0L;
/* 117 */       long totalSent = 0L;
/* 118 */       long totalDiscarded = 0L;
/* 119 */       int connectedSeeds = 0;
/* 120 */       int connectedPeers = 0;
/*     */       
/* 122 */       boolean bShowOnlyActive = false;
/* 123 */       boolean bShowOnlyComplete = false;
/* 124 */       boolean bShowOnlyIncomplete = false;
/* 125 */       boolean bShowOnlyTransferring = false;
/* 126 */       int bShowDeadForDays = 0;
/*     */       
/* 128 */       for (ListIterator<String> iter = args.listIterator(); iter.hasNext();) {
/* 129 */         String arg = (String)iter.next();
/* 130 */         if (("active".equalsIgnoreCase(arg)) || ("a".equalsIgnoreCase(arg))) {
/* 131 */           bShowOnlyActive = true;
/* 132 */           iter.remove();
/* 133 */         } else if (("complete".equalsIgnoreCase(arg)) || ("c".equalsIgnoreCase(arg))) {
/* 134 */           bShowOnlyComplete = true;
/* 135 */           iter.remove();
/* 136 */         } else if (("incomplete".equalsIgnoreCase(arg)) || ("i".equalsIgnoreCase(arg))) {
/* 137 */           bShowOnlyIncomplete = true;
/* 138 */           iter.remove();
/* 139 */         } else if (("transferring".equalsIgnoreCase(arg)) || ("x".equalsIgnoreCase(arg))) {
/* 140 */           bShowOnlyTransferring = true;
/* 141 */           bShowOnlyActive = true;
/* 142 */           iter.remove();
/* 143 */         } else if (("dead".equalsIgnoreCase(arg)) || ("d".equalsIgnoreCase(arg)))
/*     */         {
/* 145 */           iter.remove();
/*     */           
/* 147 */           bShowDeadForDays = 7;
/*     */           
/* 149 */           if (iter.hasNext())
/*     */           {
/* 151 */             String days = (String)iter.next();
/*     */             try
/*     */             {
/* 154 */               bShowDeadForDays = Integer.parseInt(days);
/*     */               
/* 156 */               iter.remove();
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 160 */               iter.previous();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */       Iterator torrent;
/*     */       Iterator torrent;
/* 168 */       if (args.size() > 0)
/*     */       {
/* 170 */         List matchedTorrents = new TorrentFilter().getTorrents(ci.torrents, args);
/* 171 */         torrent = matchedTorrents.iterator();
/*     */       }
/*     */       else {
/* 174 */         torrent = ci.torrents.iterator();
/*     */       }
/* 176 */       List shown_torrents = new ArrayList();
/*     */       
/* 178 */       while (torrent.hasNext())
/*     */       {
/* 180 */         DownloadManager dm = (DownloadManager)torrent.next();
/*     */         
/* 182 */         DownloadManagerStats stats = dm.getStats();
/*     */         
/* 184 */         boolean bDownloadCompleted = stats.getDownloadCompleted(false) == 1000;
/* 185 */         boolean bCanShow = (bShowOnlyComplete == bShowOnlyIncomplete) || ((bDownloadCompleted) && (bShowOnlyComplete)) || ((!bDownloadCompleted) && (bShowOnlyIncomplete));
/*     */         
/* 187 */         if ((bCanShow) && (bShowOnlyActive)) {
/* 188 */           int dmstate = dm.getState();
/* 189 */           bCanShow = (dmstate == 60) || (dmstate == 50) || (dmstate == 30) || (dmstate == 5) || (dmstate == 20);
/*     */         }
/*     */         PEPeerManagerStats ps;
/* 192 */         if ((bCanShow) && (bShowOnlyTransferring)) {
/*     */           try {
/* 194 */             ps = dm.getPeerManager().getStats();
/* 195 */             bCanShow = (ps.getDataSendRate() > 0L) || (ps.getDataReceiveRate() > 0L);
/*     */           }
/*     */           catch (Exception e) {}
/*     */         }
/*     */         
/* 200 */         if ((bCanShow) && (bShowDeadForDays > 0))
/*     */         {
/* 202 */           int dmstate = dm.getState();
/*     */           
/* 204 */           bCanShow = false;
/*     */           
/* 206 */           if ((dmstate == 60) || ((bDownloadCompleted) && ((dmstate == 75) || (dmstate == 70))))
/*     */           {
/*     */ 
/* 209 */             long seeding_secs = stats.getSecondsOnlySeeding();
/*     */             
/* 211 */             long seeding_days = seeding_secs / 86400L;
/*     */             
/* 213 */             if (seeding_days >= bShowDeadForDays)
/*     */             {
/* 215 */               int secs_since_last_up = stats.getTimeSinceLastDataSentInSeconds();
/*     */               
/* 217 */               if (secs_since_last_up == -1)
/*     */               {
/*     */ 
/*     */ 
/* 221 */                 bCanShow = true;
/*     */               }
/*     */               else
/*     */               {
/* 225 */                 int days_since_last_up = secs_since_last_up / 86400;
/*     */                 
/* 227 */                 if (days_since_last_up >= bShowDeadForDays)
/*     */                 {
/* 229 */                   bCanShow = true;
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/* 235 */         if (bCanShow)
/*     */         {
/* 237 */           shown_torrents.add(dm);
/*     */           try
/*     */           {
/* 240 */             PEPeerManager pm = dm.getPeerManager();
/* 241 */             ps = pm == null ? null : pm.getStats();
/*     */           } catch (Exception e) {
/* 243 */             ps = null;
/*     */           }
/* 245 */           if (ps != null) {
/* 246 */             totalReceived += dm.getStats().getTotalDataBytesReceived();
/* 247 */             totalSent += dm.getStats().getTotalDataBytesSent();
/* 248 */             totalDiscarded += ps.getTotalDiscarded();
/* 249 */             connectedSeeds += dm.getNbSeeds();
/* 250 */             connectedPeers += dm.getNbPeers();
/*     */           }
/* 252 */           ci.out.print((shown_torrents.size() < 10 ? " " : "") + shown_torrents.size() + " ");
/* 253 */           ci.out.println(getTorrentSummary(dm));
/* 254 */           ci.out.println();
/*     */         }
/*     */       }
/*     */       
/* 258 */       ci.torrents.clear();
/* 259 */       ci.torrents.addAll(shown_torrents);
/*     */       
/* 261 */       GlobalManager gm = ci.getGlobalManager();
/*     */       
/* 263 */       ci.out.println("Total Speed (down/up): " + DisplayFormatters.formatByteCountToKiBEtcPerSec(gm.getStats().getDataReceiveRate() + gm.getStats().getProtocolReceiveRate()) + " / " + DisplayFormatters.formatByteCountToKiBEtcPerSec(gm.getStats().getDataSendRate() + gm.getStats().getProtocolSendRate()));
/*     */       
/* 265 */       ci.out.println("Transferred Volume (down/up/discarded): " + DisplayFormatters.formatByteCountToKiBEtc(totalReceived) + " / " + DisplayFormatters.formatByteCountToKiBEtc(totalSent) + " / " + DisplayFormatters.formatByteCountToKiBEtc(totalDiscarded));
/* 266 */       ci.out.println("Total Connected Peers (seeds/peers): " + Integer.toString(connectedSeeds) + " / " + Integer.toString(connectedPeers));
/* 267 */       ci.out.println("> -----");
/* 268 */     } else if ((subCommand.equalsIgnoreCase("dht")) || (subCommand.equalsIgnoreCase("d")))
/*     */     {
/* 270 */       showDHTStats(ci);
/*     */     }
/* 272 */     else if ((subCommand.equalsIgnoreCase("nat")) || (subCommand.equalsIgnoreCase("n")))
/*     */     {
/* 274 */       IndentWriter iw = new IndentWriter(new PrintWriter(ci.out));
/*     */       
/* 276 */       iw.setForce(true);
/*     */       
/* 278 */       NetworkAdmin.getSingleton().logNATStatus(iw);
/*     */     }
/* 280 */     else if ((subCommand.equalsIgnoreCase("stats")) || (subCommand.equalsIgnoreCase("s")))
/*     */     {
/* 282 */       String pattern = ".*";
/*     */       
/* 284 */       if (args.size() > 0)
/*     */       {
/* 286 */         pattern = (String)args.get(0);
/*     */         
/* 288 */         if (pattern.equals("*"))
/*     */         {
/* 290 */           pattern = ".*";
/*     */         }
/*     */       }
/*     */       
/* 294 */       if (args.size() > 1)
/*     */       {
/* 296 */         AzureusCoreStats.setEnableAverages(((String)args.get(1)).equalsIgnoreCase("on"));
/*     */       }
/*     */       
/* 299 */       Set types = new HashSet();
/*     */       
/* 301 */       types.add(pattern);
/*     */       
/* 303 */       Map reply = AzureusCoreStats.getStats(types);
/*     */       
/* 305 */       Iterator it = reply.entrySet().iterator();
/*     */       
/* 307 */       List lines = new ArrayList();
/*     */       
/* 309 */       while (it.hasNext())
/*     */       {
/* 311 */         Map.Entry entry = (Map.Entry)it.next();
/*     */         
/* 313 */         lines.add(entry.getKey() + " -> " + entry.getValue());
/*     */       }
/*     */       
/* 316 */       Collections.sort(lines);
/*     */       
/* 318 */       for (int i = 0; i < lines.size(); i++)
/*     */       {
/* 320 */         ci.out.println(lines.get(i));
/*     */       }
/* 322 */     } else if ((subCommand.equalsIgnoreCase("diag")) || (subCommand.equalsIgnoreCase("z")))
/*     */     {
/*     */       try {
/* 325 */         ci.out.println("Writing diagnostics to file 'az.diag'");
/*     */         
/* 327 */         FileWriter fw = new FileWriter("az.diag");
/*     */         
/* 329 */         PrintWriter pw = new PrintWriter(fw);
/*     */         
/* 331 */         AEDiagnostics.generateEvidence(pw);
/*     */         
/* 333 */         pw.flush();
/*     */         
/* 335 */         fw.close();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 339 */         ci.out.println(e);
/*     */       }
/*     */     }
/*     */     else {
/* 343 */       if ((ci.torrents == null) || ((ci.torrents != null) && (ci.torrents.isEmpty()))) {
/* 344 */         ci.out.println("> Command 'show': No torrents in list (try 'show torrents' first).");
/* 345 */         return;
/*     */       }
/*     */       try {
/* 348 */         int number = Integer.parseInt(subCommand);
/* 349 */         if ((number == 0) || (number > ci.torrents.size())) {
/* 350 */           ci.out.println("> Command 'show': Torrent #" + number + " unknown.");
/* 351 */           return;
/*     */         }
/* 353 */         DownloadManager dm = (DownloadManager)ci.torrents.get(number - 1);
/* 354 */         printTorrentDetails(ci.out, dm, number, args);
/*     */       }
/*     */       catch (Exception e) {
/* 357 */         ci.out.println("> Command 'show': Subcommand '" + subCommand + "' unknown.");
/* 358 */         return;
/*     */       }
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
/*     */   private static void printTorrentDetails(PrintStream out, DownloadManager dm, int torrentNum, List<String> args)
/*     */   {
/* 372 */     String name = dm.getDisplayName();
/* 373 */     if (name == null)
/* 374 */       name = "?";
/* 375 */     out.println("> -----");
/* 376 */     out.println("Info on Torrent #" + torrentNum + " (" + name + ")");
/* 377 */     out.println("- General Info -");
/* 378 */     String[] health = { "- no info -", "stopped", "no remote connections", "no tracker", "OK", "ko" };
/*     */     try {
/* 380 */       out.println("Health: " + health[dm.getHealthStatus()]);
/*     */     } catch (Exception e) {
/* 382 */       out.println("Health: " + health[0]);
/*     */     }
/* 384 */     out.println("State: " + Integer.toString(dm.getState()));
/* 385 */     if (dm.getState() == 100)
/* 386 */       out.println("Error: " + dm.getErrorDetails());
/* 387 */     out.println("Hash: " + TorrentUtils.nicePrintTorrentHash(dm.getTorrent(), true));
/* 388 */     out.println("- Torrent file -");
/* 389 */     out.println("Torrent Filename: " + dm.getTorrentFileName());
/* 390 */     out.println("Saving to: " + dm.getSaveLocation());
/* 391 */     out.println("Created By: " + dm.getTorrentCreatedBy());
/* 392 */     out.println("Comment: " + dm.getTorrentComment());
/* 393 */     Category cat = dm.getDownloadState().getCategory();
/* 394 */     if (cat != null) {
/* 395 */       out.println("Category: " + cat.getName());
/*     */     }
/* 397 */     List<Tag> tags = TagManagerFactory.getTagManager().getTagsForTaggable(3, dm);
/*     */     String tags_str;
/* 399 */     String tags_str; if (tags.size() == 0) {
/* 400 */       tags_str = "None";
/*     */     } else {
/* 402 */       tags_str = "";
/* 403 */       for (Tag t : tags) {
/* 404 */         tags_str = tags_str + (tags_str.length() == 0 ? "" : ",") + t.getTagName(true);
/*     */       }
/*     */     }
/* 407 */     out.println("Tags: " + tags_str);
/* 408 */     out.println("- Tracker Info -");
/* 409 */     TRTrackerAnnouncer trackerclient = dm.getTrackerClient();
/* 410 */     if (trackerclient != null) {
/* 411 */       out.println("URL: " + trackerclient.getTrackerURL());
/*     */       String timestr;
/*     */       try {
/* 414 */         int time = trackerclient.getTimeUntilNextUpdate();
/* 415 */         String timestr; if (time < 0) {
/* 416 */           timestr = MessageText.getString("GeneralView.label.updatein.querying");
/*     */         } else {
/* 418 */           int minutes = time / 60;
/* 419 */           int seconds = time % 60;
/* 420 */           String strSeconds = "" + seconds;
/* 421 */           if (seconds < 10) {
/* 422 */             strSeconds = "0" + seconds;
/*     */           }
/* 424 */           timestr = minutes + ":" + strSeconds;
/*     */         }
/*     */       } catch (Exception e) {
/* 427 */         timestr = "unknown";
/*     */       }
/* 429 */       out.println("Time till next Update: " + timestr);
/* 430 */       out.println("Status: " + trackerclient.getStatusString());
/*     */     } else {
/* 432 */       out.println("  Not available");
/*     */     }
/* 434 */     out.println("- Files Info -");
/* 435 */     DiskManagerFileInfo[] files = dm.getDiskManagerFileInfo();
/* 436 */     if (files != null)
/* 437 */       for (int i = 0; i < files.length; i++) {
/* 438 */         out.print((i < 9 ? "   " : "  ") + Integer.toString(i + 1) + " (");
/*     */         
/* 440 */         String tmp = ">";
/* 441 */         if (files[i].getPriority() > 0)
/* 442 */           tmp = "+";
/* 443 */         if (files[i].isSkipped())
/* 444 */           tmp = "!";
/* 445 */         out.print(tmp + ") ");
/* 446 */         if (files[i] != null) {
/* 447 */           long fLen = files[i].getLength();
/* 448 */           if (fLen > 0L) {
/* 449 */             DecimalFormat df = new DecimalFormat("000.0%");
/* 450 */             out.print(df.format(files[i].getDownloaded() * 1.0D / fLen));
/*     */             
/* 452 */             out.println("\t" + files[i].getFile(true).getName());
/*     */           } else {
/* 454 */             out.println("Info not available.");
/*     */           }
/* 456 */         } else { out.println("Info not available.");
/*     */         }
/*     */       } else {
/* 459 */       out.println("  Info not available.");
/*     */     }
/*     */     
/* 462 */     for (String arg : args)
/*     */     {
/* 464 */       arg = arg.toLowerCase();
/*     */       
/* 466 */       if (arg.startsWith("pie"))
/*     */       {
/* 468 */         out.println("Pieces");
/*     */         
/* 470 */         PEPeerManager pm = dm.getPeerManager();
/*     */         
/* 472 */         if (pm != null)
/*     */         {
/* 474 */           PiecePicker picker = pm.getPiecePicker();
/*     */           
/* 476 */           PEPiece[] pieces = pm.getPieces();
/*     */           
/* 478 */           String line = "";
/*     */           
/* 480 */           for (int i = 0; i < pieces.length; i++)
/*     */           {
/* 482 */             String str = picker.getPieceString(i);
/*     */             
/* 484 */             line = line + (line.length() == 0 ? i + " " : ",") + str;
/*     */             
/* 486 */             PEPiece piece = pieces[i];
/*     */             
/* 488 */             if (piece != null)
/*     */             {
/* 490 */               line = line + ":" + piece.getString();
/*     */             }
/*     */             
/* 493 */             if ((i + 1) % 10 == 0)
/*     */             {
/* 495 */               out.println(line);
/*     */               
/* 497 */               line = "";
/*     */             }
/*     */           }
/*     */           
/* 501 */           if (line.length() > 0)
/*     */           {
/* 503 */             out.println(line);
/*     */           }
/*     */         }
/* 506 */       } else if (arg.startsWith("pee"))
/*     */       {
/* 508 */         out.println("Peers");
/*     */         
/* 510 */         PEPeerManager pm = dm.getPeerManager();
/*     */         
/* 512 */         if (pm != null)
/*     */         {
/* 514 */           List<PEPeer> peers = pm.getPeers();
/*     */           
/* 516 */           out.println("\tConnected to " + peers.size() + " peers");
/*     */           
/* 518 */           for (PEPeer peer : peers)
/*     */           {
/* 520 */             PEPeerStats stats = peer.getStats();
/*     */             
/* 522 */             System.out.println("\t\t" + peer.getIp() + ", in=" + (peer.isIncoming() ? "Y" : "N") + ", prot=" + peer.getProtocol() + ", choked=" + (peer.isChokingMe() ? "Y" : "N") + ", up=" + DisplayFormatters.formatByteCountToKiBEtcPerSec(stats.getDataSendRate() + stats.getProtocolSendRate()) + ", down=" + DisplayFormatters.formatByteCountToKiBEtcPerSec(stats.getDataReceiveRate() + stats.getProtocolReceiveRate()) + ", in_req=" + peer.getIncomingRequestCount() + ", out_req=" + peer.getOutgoingRequestCount());
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 535 */     out.println("> -----");
/*     */   }
/*     */   
/*     */ 
/*     */   protected void showDHTStats(ConsoleInput ci)
/*     */   {
/*     */     try
/*     */     {
/* 543 */       PluginInterface def = ci.azureus_core.getPluginManager().getDefaultPluginInterface();
/*     */       
/* 545 */       PluginInterface dht_pi = def.getPluginManager().getPluginInterfaceByClass(DHTPlugin.class);
/*     */       
/*     */ 
/* 548 */       if (dht_pi == null)
/*     */       {
/* 550 */         ci.out.println("\tDHT isn't present");
/*     */         
/* 552 */         return;
/*     */       }
/*     */       
/* 555 */       DHTPlugin dht_plugin = (DHTPlugin)dht_pi.getPlugin();
/*     */       
/* 557 */       if (dht_plugin.getStatus() != 3)
/*     */       {
/* 559 */         ci.out.println("\tDHT isn't running yet (disabled or initialising)");
/*     */         
/* 561 */         return;
/*     */       }
/*     */       
/* 564 */       DHT[] dhts = dht_plugin.getDHTs();
/*     */       
/* 566 */       for (int i = 0; i < dhts.length; i++)
/*     */       {
/* 568 */         if (i > 0) {
/* 569 */           ci.out.println("");
/*     */         }
/*     */         
/* 572 */         DHT dht = dhts[i];
/*     */         
/* 574 */         DHTTransport transport = dht.getTransport();
/*     */         
/* 576 */         DHTTransportStats t_stats = transport.getStats();
/* 577 */         DHTDBStats d_stats = dht.getDataBase().getStats();
/* 578 */         DHTControlStats c_stats = dht.getControl().getStats();
/* 579 */         DHTRouterStats r_stats = dht.getRouter().getStats();
/*     */         
/* 581 */         long[] rs = r_stats.getStats();
/*     */         
/* 583 */         DHTNetworkPosition[] nps = transport.getLocalContact().getNetworkPositions();
/*     */         
/* 585 */         String np_str = "";
/*     */         
/* 587 */         for (int j = 0; j < nps.length; j++) {
/* 588 */           np_str = np_str + (j == 0 ? "" : ",") + nps[j];
/*     */         }
/*     */         
/* 591 */         ci.out.println("DHT:ip=" + transport.getLocalContact().getAddress() + ",net=" + transport.getNetwork() + ",prot=V" + transport.getProtocolVersion() + ",np=" + np_str + ",sleeping=" + dht.isSleeping());
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 597 */         ci.out.println("Router:nodes=" + rs[0] + ",leaves=" + rs[1] + ",contacts=" + rs[2] + ",replacement=" + rs[3] + ",live=" + rs[4] + ",unknown=" + rs[5] + ",failing=" + rs[6]);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 607 */         ci.out.println("Transport:" + t_stats.getString());
/*     */         
/*     */ 
/*     */ 
/* 611 */         int[] dbv_details = d_stats.getValueDetails();
/*     */         
/* 613 */         ci.out.println("Control:dht=" + c_stats.getEstimatedDHTSize() + ", Database:keys=" + d_stats.getKeyCount() + ",vals=" + dbv_details[0] + ",loc=" + dbv_details[1] + ",dir=" + dbv_details[2] + ",ind=" + dbv_details[3] + ",div_f=" + dbv_details[4] + ",div_s=" + dbv_details[5]);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 623 */         dht.getRouter().print();
/*     */       }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 628 */       e.printStackTrace(ci.out);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/Show.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */