/*     */ package org.gudy.azureus2.ui.console.commands;
/*     */ 
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagManager;
/*     */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*     */ import com.aelitis.azureus.core.tag.TagType;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.category.Category;
/*     */ import org.gudy.azureus2.core3.category.CategoryManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.ui.console.ConsoleInput;
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
/*     */ public class Hack
/*     */   extends TorrentCommand
/*     */ {
/*  39 */   private final CommandCollection subCommands = new CommandCollection();
/*     */   
/*     */   public Hack()
/*     */   {
/*  43 */     super("hack", "#", "Hacking");
/*  44 */     this.subCommands.add(new HackFile());
/*  45 */     this.subCommands.add(new HackTracker());
/*  46 */     this.subCommands.add(new HackDownloadSpeed());
/*  47 */     this.subCommands.add(new HackUploadSpeed());
/*  48 */     this.subCommands.add(new HackUploads());
/*  49 */     this.subCommands.add(new HackCategory());
/*  50 */     this.subCommands.add(new HackTag());
/*     */   }
/*     */   
/*     */ 
/*     */   public String getCommandDescriptions()
/*     */   {
/*  56 */     return "hack [<various options>]\t#\tModify torrent settings. Use without parameters for further help.";
/*     */   }
/*     */   
/*     */   public void printHelpExtra(PrintStream out, List args) {
/*  60 */     out.println("> -----");
/*  61 */     out.println("'hack' syntax:");
/*  62 */     if (args.size() > 0) {
/*  63 */       String command = (String)args.remove(0);
/*  64 */       IConsoleCommand cmd = this.subCommands.get(command);
/*  65 */       if (cmd != null)
/*  66 */         cmd.printHelp(out, args);
/*  67 */       return;
/*     */     }
/*  69 */     out.println("hack <torrent id> <command> <command options>");
/*  70 */     out.println();
/*  71 */     out.println("<torrent id> can be one of the following:");
/*  72 */     out.println("<#>\t\tNumber of a torrent. You have to use 'show torrents' first as the number is taken from there.");
/*  73 */     out.println("hash <hash>\tApplied to torrent with the hash <hash> as given in the xml output or extended torrent info ('show <#>').");
/*  74 */     out.println("help\t\tDetailed help for <command>");
/*  75 */     out.println();
/*  76 */     out.println("Available <command>s:");
/*  77 */     for (Iterator iter = this.subCommands.iterator(); iter.hasNext();) {
/*  78 */       TorrentSubCommand cmd = (TorrentSubCommand)iter.next();
/*  79 */       out.println(cmd.getCommandDescriptions());
/*     */     }
/*  81 */     out.println("> -----");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean performCommand(ConsoleInput ci, DownloadManager dm, List args)
/*     */   {
/*  92 */     if (args.isEmpty()) {
/*  93 */       ci.out.println("> Not enough parameters for command '" + getCommandName() + "'.");
/*  94 */       return false;
/*     */     }
/*  96 */     String subCommandName = (String)args.remove(0);
/*  97 */     TorrentSubCommand cmd = (TorrentSubCommand)this.subCommands.get(subCommandName);
/*  98 */     if (cmd != null) {
/*  99 */       return cmd.performCommand(ci, dm, args);
/*     */     }
/*     */     
/* 102 */     ci.out.println("> Command 'hack': Command parameter '" + subCommandName + "' unknown.");
/* 103 */     return false;
/*     */   }
/*     */   
/*     */   private static class HackDownloadSpeed
/*     */     extends TorrentSubCommand
/*     */   {
/*     */     public HackDownloadSpeed()
/*     */     {
/* 111 */       super("d");
/*     */     }
/*     */     
/*     */     public String getCommandDescriptions() {
/* 115 */       return "downloadspeed\td\tSet max download speed [in kbps]of a torrent (0 for unlimited).";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean performCommand(ConsoleInput ci, DownloadManager dm, List args)
/*     */     {
/* 123 */       if (args.isEmpty()) {
/* 124 */         ci.out.println("> Command 'hack': Not enough parameters for subcommand '" + getCommandName() + "'");
/* 125 */         return false;
/*     */       }
/* 127 */       int newSpeed = Math.max(-1, Integer.parseInt((String)args.get(0)));
/* 128 */       dm.getStats().setDownloadRateLimitBytesPerSecond(newSpeed * DisplayFormatters.getKinB());
/* 129 */       return true;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class HackUploadSpeed extends TorrentSubCommand
/*     */   {
/*     */     public HackUploadSpeed()
/*     */     {
/* 137 */       super("u");
/*     */     }
/*     */     
/*     */     public String getCommandDescriptions() {
/* 141 */       return "uploadspeed\tu\tSet max upload speed [in kbps] of a torrent (0 for unlimited).";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean performCommand(ConsoleInput ci, DownloadManager dm, List args)
/*     */     {
/* 149 */       if (args.isEmpty()) {
/* 150 */         ci.out.println("> Command 'hack': Not enough parameters for subcommand '" + getCommandName() + "'");
/* 151 */         return false;
/*     */       }
/* 153 */       int newSpeed = Math.max(-1, Integer.parseInt((String)args.get(0)));
/* 154 */       dm.getStats().setUploadRateLimitBytesPerSecond(newSpeed * DisplayFormatters.getKinB());
/* 155 */       return true;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class HackUploads extends TorrentSubCommand
/*     */   {
/*     */     public HackUploads()
/*     */     {
/* 163 */       super("v");
/*     */     }
/*     */     
/*     */     public String getCommandDescriptions() {
/* 167 */       return "uploads\tv\tSet max upload slots of a torrent.";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean performCommand(ConsoleInput ci, DownloadManager dm, List args)
/*     */     {
/* 175 */       if (args.isEmpty()) {
/* 176 */         ci.out.println("> Command 'hack': Not enough parameters for subcommand '" + getCommandName() + "'");
/* 177 */         return false;
/*     */       }
/* 179 */       int newSlots = Math.max(-1, Integer.parseInt((String)args.get(0)));
/* 180 */       dm.setMaxUploads(newSlots);
/* 181 */       return true;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class HackTracker extends TorrentSubCommand
/*     */   {
/* 187 */     private final CommandCollection subCommands = new CommandCollection();
/*     */     
/*     */     public HackTracker()
/*     */     {
/* 191 */       super("t");
/* 192 */       this.subCommands.add(new Hack.HackHost());
/* 193 */       this.subCommands.add(new Hack.HackPort());
/* 194 */       this.subCommands.add(new Hack.HackURL());
/*     */     }
/*     */     
/*     */     public void printHelpExtra(PrintStream out, List args)
/*     */     {
/* 199 */       out.println("hack <torrent id> tracker [command] <new value>");
/* 200 */       out.println();
/* 201 */       out.println("[command] can be one of the following:");
/* 202 */       for (Iterator iter = this.subCommands.iterator(); iter.hasNext();) {
/* 203 */         TorrentSubCommand cmd = (TorrentSubCommand)iter.next();
/* 204 */         out.println(cmd.getCommandDescriptions());
/*     */       }
/* 206 */       out.println();
/* 207 */       out.println("You can also omit [command] and only give a new full URL (just like the [command] 'url').");
/* 208 */       out.println("> -----");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean performCommand(ConsoleInput ci, DownloadManager dm, List args)
/*     */     {
/* 216 */       if (args.isEmpty()) {
/* 217 */         ci.out.println("> Command 'hack': Not enough parameters for subcommand '" + getCommandName() + "'");
/* 218 */         return false;
/*     */       }
/* 220 */       String trackercommand = (String)args.remove(0);
/* 221 */       TRTrackerAnnouncer client = dm.getTrackerClient();
/*     */       
/* 223 */       if (client == null) {
/* 224 */         ci.out.println("> Command 'hack': Tracker interface not available.");
/* 225 */         return false;
/*     */       }
/* 227 */       TorrentSubCommand cmd = (TorrentSubCommand)this.subCommands.get(trackercommand);
/* 228 */       if (cmd == null)
/*     */       {
/* 230 */         args.add(trackercommand);
/* 231 */         cmd = (TorrentSubCommand)this.subCommands.get("url");
/*     */       }
/*     */       
/* 234 */       return cmd.performCommand(ci, dm, args);
/*     */     }
/*     */     
/*     */     public String getCommandDescriptions() {
/* 238 */       return "tracker\t\tt\tModify Tracker URL of a torrent.";
/*     */     }
/*     */   }
/*     */   
/*     */   private static class HackFile extends TorrentSubCommand
/*     */   {
/*     */     public HackFile()
/*     */     {
/* 246 */       super("f");
/*     */     }
/*     */     
/*     */     public void printHelpExtra(PrintStream out, List args) {
/* 250 */       out.println("hack <torrent id> file <#> <priority>");
/* 251 */       out.println();
/* 252 */       out.println("<#> Number of the file.");
/* 253 */       out.println();
/* 254 */       out.println("<priority> can be one of the following:");
/* 255 */       out.println("normal\t\tn\tNormal Priority");
/* 256 */       out.println("high\t\th|+\tHigh Priority");
/* 257 */       out.println("nodownload\t!|-\tDon't download this file.");
/* 258 */       out.println("> -----");
/*     */     }
/*     */     
/*     */     public boolean performCommand(ConsoleInput ci, DownloadManager dm, List args) {
/* 262 */       if (args.size() < 2) {
/* 263 */         ci.out.println("> Command 'hack': Not enough parameters for subcommand 'file'.");
/* 264 */         return false;
/*     */       }
/*     */       try {
/* 267 */         DiskManager disk = dm.getDiskManager();
/* 268 */         DiskManagerFileInfo[] files = disk.getFiles();
/* 269 */         int file = Integer.parseInt((String)args.get(0));
/* 270 */         String c = (String)args.get(1);
/* 271 */         if ((c.equalsIgnoreCase("normal")) || (c.equalsIgnoreCase("n"))) {
/* 272 */           files[(file - 1)].setSkipped(false);
/* 273 */           files[(file - 1)].setPriority(0);
/* 274 */           ci.out.println("> Set file '" + files[(file - 1)].getFile(true).getName() + "' to normal priority.");
/* 275 */         } else if ((c.equalsIgnoreCase("high")) || (c.equalsIgnoreCase("h")) || (c.equalsIgnoreCase("+"))) {
/* 276 */           files[(file - 1)].setSkipped(false);
/* 277 */           files[(file - 1)].setPriority(1);
/* 278 */           ci.out.println("> Set file '" + files[(file - 1)].getFile(true).getName() + "' to high priority.");
/* 279 */         } else if ((c.equalsIgnoreCase("nodownload")) || (c.equalsIgnoreCase("!")) || (c.equalsIgnoreCase("-"))) {
/* 280 */           files[(file - 1)].setSkipped(true);
/* 281 */           files[(file - 1)].setPriority(0);
/* 282 */           ci.out.println("> Stopped to download file '" + files[(file - 1)].getFile(true).getName() + "'.");
/*     */         } else {
/* 284 */           ci.out.println("> Command 'hack': Unknown priority '" + c + "' for command parameter 'file'.");
/* 285 */           return false;
/*     */         }
/* 287 */         return true;
/*     */       } catch (Exception e) {
/* 289 */         ci.out.println("> Command 'hack': Exception while executing subcommand 'file': " + e.getMessage()); }
/* 290 */       return false;
/*     */     }
/*     */     
/*     */     public String getCommandDescriptions()
/*     */     {
/* 295 */       return "file\t\tf\tModify priority of a single file of a batch torrent.";
/*     */     }
/*     */   }
/*     */   
/*     */   private static class HackPort extends TorrentSubCommand
/*     */   {
/*     */     public HackPort()
/*     */     {
/* 303 */       super("p");
/*     */     }
/*     */     
/*     */     public boolean performCommand(ConsoleInput ci, DownloadManager dm, List args) {
/* 307 */       if (args.isEmpty()) {
/* 308 */         ci.out.println("> Command 'hack': Not enough parameters for subcommand parameter 'port'.");
/* 309 */         return false;
/*     */       }
/* 311 */       TRTrackerAnnouncer client = dm.getTrackerClient();
/*     */       try {
/* 313 */         URI uold = new URI(client.getTrackerURL().toString());
/* 314 */         String portStr = (String)args.get(0);
/* 315 */         URI unew = new URI(uold.getScheme(), uold.getUserInfo(), uold.getHost(), Integer.parseInt(portStr), uold.getPath(), uold.getQuery(), uold.getFragment());
/* 316 */         client.setTrackerURL(new URL(unew.toString()));
/* 317 */         ci.out.println("> Set Tracker URL for '" + dm.getSaveLocation() + "' to '" + unew.toString() + "'");
/*     */       } catch (Exception e) {
/* 319 */         ci.out.println("> Command 'hack': Assembling new tracker url failed: " + e.getMessage());
/* 320 */         return false;
/*     */       }
/* 322 */       return true;
/*     */     }
/*     */     
/* 325 */     public String getCommandDescriptions() { return "port\t\tp\tChange the port."; }
/*     */   }
/*     */   
/*     */   private static class HackHost extends TorrentSubCommand
/*     */   {
/*     */     public HackHost()
/*     */     {
/* 332 */       super("h");
/*     */     }
/*     */     
/*     */     public boolean performCommand(ConsoleInput ci, DownloadManager dm, List args) {
/* 336 */       if (args.isEmpty()) {
/* 337 */         ci.out.println("> Command 'hack': Not enough parameters for subcommand parameter 'host'.");
/* 338 */         return false;
/*     */       }
/* 340 */       TRTrackerAnnouncer client = dm.getTrackerClient();
/*     */       try {
/* 342 */         URI uold = new URI(client.getTrackerURL().toString());
/* 343 */         URI unew = new URI(uold.getScheme(), uold.getUserInfo(), (String)args.get(0), uold.getPort(), uold.getPath(), uold.getQuery(), uold.getFragment());
/* 344 */         client.setTrackerURL(new URL(unew.toString()));
/* 345 */         ci.out.println("> Set Tracker URL for '" + dm.getSaveLocation() + "' to '" + unew.toString() + "'");
/*     */       } catch (Exception e) {
/* 347 */         ci.out.println("> Command 'hack': Assembling new tracker url failed: " + e.getMessage());
/* 348 */         return false;
/*     */       }
/* 350 */       return true;
/*     */     }
/*     */     
/* 353 */     public String getCommandDescriptions() { return "host\t\th\tChange the host."; }
/*     */   }
/*     */   
/*     */   private static class HackURL extends TorrentSubCommand
/*     */   {
/*     */     public HackURL()
/*     */     {
/* 360 */       super("u");
/*     */     }
/*     */     
/*     */     public boolean performCommand(ConsoleInput ci, DownloadManager dm, List args) {
/* 364 */       if (args.isEmpty()) {
/* 365 */         ci.out.println("> Command 'hack': Not enough parameters for subcommand parameter 'url'.");
/* 366 */         return false;
/*     */       }
/* 368 */       TRTrackerAnnouncer client = dm.getTrackerClient();
/*     */       try
/*     */       {
/* 371 */         String uriStr = (String)args.get(0);
/* 372 */         URI uri = new URI(uriStr);
/* 373 */         client.setTrackerURL(new URL(uri.toString()));
/* 374 */         ci.out.println("> Set Tracker URL for '" + dm.getSaveLocation() + "' to '" + uri + "'");
/*     */       } catch (Exception e) {
/* 376 */         ci.out.println("> Command 'hack': Parsing tracker url failed: " + e.getMessage());
/* 377 */         return false;
/*     */       }
/* 379 */       return true;
/*     */     }
/*     */     
/* 382 */     public String getCommandDescriptions() { return "url\t\tu\tChange the full URL (Note: you have to include the '/announce' part)."; }
/*     */   }
/*     */   
/*     */   private static class HackCategory
/*     */     extends TorrentSubCommand
/*     */   {
/*     */     public HackCategory()
/*     */     {
/* 390 */       super("cat");
/*     */     }
/*     */     
/*     */     public String getCommandDescriptions() {
/* 394 */       return "category [set <category_name>|clear]\t\tcat\tSet or clear the torrent's. Category will be created if necessary.";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean performCommand(ConsoleInput ci, DownloadManager dm, List<String> args)
/*     */     {
/* 402 */       if (args.size() < 1) {
/* 403 */         ci.out.println("> Command 'hack': Not enough parameters for subcommand '" + getCommandName() + "'");
/* 404 */         return false;
/*     */       }
/*     */       
/* 407 */       String op = (String)args.get(0);
/*     */       
/* 409 */       if (op.equals("set"))
/*     */       {
/* 411 */         if (args.size() < 2) {
/* 412 */           ci.out.println("> Command 'hack': Not enough parameters for subcommand '" + getCommandName() + "'");
/* 413 */           return false;
/*     */         }
/*     */         
/* 416 */         String cat_name = (String)args.get(1);
/*     */         
/* 418 */         Category cat = CategoryManager.getCategory(cat_name);
/*     */         
/* 420 */         if (cat == null)
/*     */         {
/* 422 */           cat = CategoryManager.createCategory(cat_name);
/*     */         }
/*     */         
/* 425 */         dm.getDownloadState().setCategory(cat);
/*     */       }
/* 427 */       else if (op.equals("clear"))
/*     */       {
/* 429 */         dm.getDownloadState().setCategory(null);
/*     */       }
/*     */       
/* 432 */       return true;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class HackTag extends TorrentSubCommand
/*     */   {
/*     */     public HackTag()
/*     */     {
/* 440 */       super("tag");
/*     */     }
/*     */     
/*     */     public String getCommandDescriptions() {
/* 444 */       return "tag [add|remove] <tag_name>\t\tAdd or remove a tag. Tag will be created if necessary.";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean performCommand(ConsoleInput ci, DownloadManager dm, List<String> args)
/*     */     {
/* 452 */       if (args.size() < 2) {
/* 453 */         ci.out.println("> Command 'hack': Not enough parameters for subcommand '" + getCommandName() + "'");
/* 454 */         return false;
/*     */       }
/*     */       try
/*     */       {
/* 458 */         String op = (String)args.get(0);
/* 459 */         String tag_name = (String)args.get(1);
/*     */         
/* 461 */         TagManager tm = TagManagerFactory.getTagManager();
/*     */         
/* 463 */         TagType tt = tm.getTagType(3);
/*     */         
/* 465 */         Tag tag = tt.getTag(tag_name, true);
/*     */         
/* 467 */         if (op.equals("add"))
/*     */         {
/* 469 */           if (tag == null)
/*     */           {
/* 471 */             tag = tt.createTag(tag_name, true);
/*     */             
/* 473 */             ci.out.println("Tag '" + tag_name + "' created");
/*     */           }
/*     */           
/* 476 */           tag.addTaggable(dm);
/*     */         }
/* 478 */         else if (op.equals("remove"))
/*     */         {
/* 480 */           if (tag == null)
/*     */           {
/* 482 */             ci.out.println("Tag '" + tag_name + "' not found");
/*     */           }
/*     */           else
/*     */           {
/* 486 */             tag.removeTaggable(dm);
/*     */           }
/*     */         }
/*     */         else {
/* 490 */           ci.out.println("> Command 'hack': Invalid parameters for '" + getCommandName() + "'");
/* 491 */           return false;
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 495 */         ci.out.println("Command failed: " + Debug.getNestedExceptionMessage(e));
/*     */         
/* 497 */         return false;
/*     */       }
/*     */       
/* 500 */       return true;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/Hack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */