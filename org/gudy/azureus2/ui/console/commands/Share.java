/*     */ package org.gudy.azureus2.ui.console.commands;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareException;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareItem;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareManager;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResource;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResourceDir;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResourceDirContents;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResourceFile;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentManager;
/*     */ import org.gudy.azureus2.ui.console.ConsoleInput;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Share
/*     */   extends IConsoleCommand
/*     */ {
/*     */   public Share()
/*     */   {
/*  36 */     super("share");
/*     */   }
/*     */   
/*     */   public String getCommandDescriptions() {
/*  40 */     return "share <type> <path> [<properties>]\t\t\tShare a file or folder(s). Use without parameters to get a list of available options.";
/*     */   }
/*     */   
/*     */   public void printHelpExtra(PrintStream out, List args) {
/*  44 */     out.println("> -----");
/*  45 */     out.println("[share <type> <path> [<properties>]");
/*  46 */     out.println("type options:");
/*  47 */     out.println("file           Share a single file.");
/*  48 */     out.println("folder         Share a folder as a single multi-file torrent.");
/*  49 */     out.println("contents       Share files and sub-dirs in a folder as single and multi-file torrents.");
/*  50 */     out.println("rcontents      Share files and sub-dir files in a folder as separate torrents.");
/*  51 */     out.println("list           List the shares (path not required)");
/*  52 */     out.println("remove         Remove a share given its path");
/*  53 */     out.println("remove hash <hash>   Remove a share given its hash");
/*     */     
/*  55 */     out.println("      <properties> is semicolon separated <name>=<value> list.");
/*  56 */     out.println("      Defined values are 'category=<cat>', 'private=<true/false>', 'dht_backup=<true/false>' and 'comment=<comment>' ('_' in <comment> are replaced with spaces)");
/*  57 */     out.println("          currently only 'category' can be applied to file/folder and the rest only apply to items added *after* the share has been defined");
/*  58 */     out.println("      For example: share contents /music category=music;private=true;comment=Great_Stuff");
/*  59 */     out.println("> -----");
/*     */   }
/*     */   
/*     */   public void execute(String commandName, final ConsoleInput ci, List args)
/*     */   {
/*  64 */     if (args.isEmpty())
/*     */     {
/*  66 */       printHelp(ci.out, args); return;
/*     */     }
/*     */     final ShareManager share_manager;
/*     */     try
/*     */     {
/*  71 */       share_manager = ci.azureus_core.getPluginManager().getDefaultPluginInterface().getShareManager();
/*     */     } catch (ShareException e) {
/*  73 */       ci.out.println("ERROR: " + e.getMessage() + " ::");
/*  74 */       Debug.printStackTrace(e);
/*  75 */       return;
/*     */     }
/*     */     
/*  78 */     final String arg = (String)args.remove(0);
/*     */     
/*  80 */     if ((args.isEmpty()) && ("list".equalsIgnoreCase(arg))) {
/*  81 */       ShareResource[] shares = share_manager.getShares();
/*  82 */       if (shares.length == 0) {
/*  83 */         ci.out.println("> No shares found");
/*     */       }
/*     */       else {
/*  86 */         HashSet share_map = new HashSet();
/*     */         
/*  88 */         int share_num = 0;
/*     */         
/*  90 */         for (int i = 0; i < shares.length; i++)
/*     */         {
/*  92 */           ShareResource share = shares[i];
/*     */           
/*  94 */           if ((share instanceof ShareResourceDirContents))
/*     */           {
/*  96 */             share_map.add(share);
/*     */           }
/*  98 */           else if (share.getParent() == null)
/*     */           {
/*     */ 
/*     */ 
/* 102 */             ci.out.println("> " + share_num++ + ": " + shares[i].getName());
/*     */           }
/*     */         }
/*     */         
/* 106 */         Iterator it = share_map.iterator();
/*     */         
/* 108 */         TorrentManager tm = ci.azureus_core.getPluginManager().getDefaultPluginInterface().getTorrentManager();
/*     */         
/* 110 */         TorrentAttribute category_attribute = tm.getAttribute("Category");
/* 111 */         TorrentAttribute props_attribute = tm.getAttribute("ShareProperties");
/*     */         
/* 113 */         while (it.hasNext())
/*     */         {
/* 115 */           ShareResourceDirContents root = (ShareResourceDirContents)it.next();
/*     */           
/* 117 */           String cat = root.getAttribute(category_attribute);
/* 118 */           String props = root.getAttribute(props_attribute);
/*     */           
/* 120 */           String extra = ",cat=" + cat;
/*     */           
/* 122 */           extra = extra + (props == null ? "" : new StringBuilder().append(",props=").append(props).toString());
/*     */           
/* 124 */           ci.out.println("> " + share_num++ + ": " + root.getName() + extra);
/*     */           
/* 126 */           outputChildren(ci, "    ", root);
/*     */         }
/*     */       }
/* 129 */       return;
/*     */     }
/*     */     
/* 132 */     String first_arg = (String)args.get(0);
/*     */     
/* 134 */     if ((first_arg.equals("hash")) && (args.size() > 1))
/*     */     {
/* 136 */       byte[] hash = ByteFormatter.decodeString((String)args.get(1));
/*     */       
/* 138 */       boolean force = false;
/*     */       
/* 140 */       if (args.size() > 2)
/*     */       {
/* 142 */         force = ((String)args.get(2)).equalsIgnoreCase("true");
/*     */       }
/*     */       
/* 145 */       if ("remove".equalsIgnoreCase(arg))
/*     */       {
/* 147 */         ShareResource[] shares = share_manager.getShares();
/*     */         
/* 149 */         boolean done = false;
/*     */         
/* 151 */         for (int i = 0; i < shares.length; i++)
/*     */         {
/* 153 */           ShareResource share = shares[i];
/*     */           
/* 155 */           ShareItem item = null;
/*     */           
/* 157 */           if ((share instanceof ShareResourceFile))
/*     */           {
/* 159 */             item = ((ShareResourceFile)share).getItem();
/*     */           }
/* 161 */           else if ((share instanceof ShareResourceDir))
/*     */           {
/* 163 */             item = ((ShareResourceDir)share).getItem();
/*     */           }
/*     */           
/* 166 */           if (item != null) {
/*     */             try
/*     */             {
/* 169 */               byte[] item_hash = item.getTorrent().getHash();
/*     */               
/* 171 */               if (Arrays.equals(hash, item_hash))
/*     */               {
/* 173 */                 share.delete(force);
/*     */                 
/* 175 */                 ci.out.println("> Share " + share.getName() + " removed");
/*     */                 
/* 177 */                 done = true;
/*     */                 
/* 179 */                 break;
/*     */               }
/*     */             }
/*     */             catch (Throwable e) {
/* 183 */               ci.out.println("ERROR: " + e.getMessage() + " ::");
/*     */               
/* 185 */               Debug.printStackTrace(e);
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 190 */         if (!done)
/*     */         {
/* 192 */           ci.out.println("> Share with hash " + ByteFormatter.encodeString(hash) + " not found");
/*     */         }
/*     */       }
/*     */       else {
/* 196 */         ci.out.println("ERROR: Unsupported hash based command '" + arg + "'");
/*     */       }
/*     */       
/* 199 */       return;
/*     */     }
/*     */     
/* 202 */     final File path = new File(first_arg);
/* 203 */     if (!path.exists()) {
/* 204 */       ci.out.println("ERROR: path [" + path + "] does not exist.");
/* 205 */       return;
/*     */     }
/*     */     
/* 208 */     if ("remove".equalsIgnoreCase(arg))
/*     */     {
/* 210 */       ShareResource[] shares = share_manager.getShares();
/*     */       
/* 212 */       boolean done = false;
/*     */       
/* 214 */       for (int i = 0; i < shares.length; i++)
/*     */       {
/* 216 */         if (shares[i].getName().equals(path.toString())) {
/*     */           try
/*     */           {
/* 219 */             shares[i].delete();
/*     */             
/* 221 */             ci.out.println("> Share " + path.toString() + " removed");
/*     */             
/* 223 */             done = true;
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 227 */             ci.out.println("ERROR: " + e.getMessage() + " ::");
/*     */             
/* 229 */             Debug.printStackTrace(e);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 236 */       if (!done)
/*     */       {
/* 238 */         ci.out.println("> Share " + path.toString() + " not found");
/*     */       }
/*     */       
/* 241 */       return;
/*     */     }
/*     */     
/* 244 */     String category = null;
/* 245 */     String props = null;
/*     */     
/* 247 */     if (args.size() == 2)
/*     */     {
/* 249 */       String properties = (String)args.get(1);
/*     */       
/* 251 */       StringTokenizer tok = new StringTokenizer(properties, ";");
/*     */       
/* 253 */       while (tok.hasMoreTokens())
/*     */       {
/* 255 */         String token = tok.nextToken();
/*     */         
/* 257 */         int pos = token.indexOf('=');
/*     */         
/* 259 */         if (pos == -1)
/*     */         {
/* 261 */           ci.out.println("ERROR: invalid properties string '" + properties + "'");
/*     */           
/* 263 */           return;
/*     */         }
/*     */         
/*     */ 
/* 267 */         String lhs = token.substring(0, pos).trim().toLowerCase();
/* 268 */         String rhs = token.substring(pos + 1).trim();
/*     */         
/* 270 */         if (lhs.equals("category"))
/*     */         {
/* 272 */           category = rhs;
/*     */ 
/*     */ 
/*     */         }
/* 276 */         else if ((lhs.equals("private")) || (lhs.equals("dht_backup")) || (lhs.equals("comment")))
/*     */         {
/*     */ 
/*     */ 
/* 280 */           if (props == null)
/*     */           {
/* 282 */             props = "";
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 287 */           if (lhs.equals("comment"))
/*     */           {
/* 289 */             rhs = rhs.replace('_', ' ');
/*     */           }
/*     */           
/* 292 */           if (rhs.length() > 0)
/*     */           {
/* 294 */             props = props + (props.length() == 0 ? "" : ";") + lhs + "=" + rhs;
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 299 */           ci.out.println("ERROR: invalid properties string '" + properties + "'");
/*     */           
/* 301 */           return;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 308 */     final String f_category = category;
/* 309 */     final String f_props = props;
/*     */     
/* 311 */     new AEThread("shareFile")
/*     */     {
/*     */       public void runSupport()
/*     */       {
/*     */         try
/*     */         {
/* 317 */           ShareResource resource = share_manager.getShare(path);
/*     */           
/* 319 */           if ("file".equalsIgnoreCase(arg))
/*     */           {
/* 321 */             ci.out.println("File [" + path + "] share being processed in background...");
/*     */             
/* 323 */             if (resource == null)
/*     */             {
/* 325 */               resource = share_manager.addFile(path);
/*     */             }
/*     */           }
/* 328 */           else if ("folder".equalsIgnoreCase(arg))
/*     */           {
/* 330 */             ci.out.println("Folder [" + path + "] share being processed in background...");
/*     */             
/* 332 */             if (resource == null)
/*     */             {
/* 334 */               resource = share_manager.addDir(path);
/*     */             }
/*     */           }
/* 337 */           else if ("contents".equalsIgnoreCase(arg))
/*     */           {
/* 339 */             ci.out.println("Folder contents [" + path + "] share being processed in background...");
/*     */             
/* 341 */             if (resource == null)
/*     */             {
/* 343 */               resource = share_manager.addDirContents(path, false);
/*     */             }
/*     */           }
/* 346 */           else if ("rcontents".equalsIgnoreCase(arg))
/*     */           {
/* 348 */             ci.out.println("Folder contents recursive [" + path + "] share being processed in background...");
/*     */             
/* 350 */             if (resource == null)
/*     */             {
/* 352 */               resource = share_manager.addDirContents(path, true);
/*     */             }
/*     */           }
/*     */           else {
/* 356 */             ci.out.println("ERROR: type '" + arg + "' unknown.");
/*     */           }
/*     */           
/*     */ 
/* 360 */           if (resource != null)
/*     */           {
/* 362 */             TorrentManager tm = ci.azureus_core.getPluginManager().getDefaultPluginInterface().getTorrentManager();
/*     */             
/* 364 */             String cat = f_category;
/*     */             
/* 366 */             if (cat != null)
/*     */             {
/* 368 */               if (cat.length() == 0)
/*     */               {
/* 370 */                 cat = null;
/*     */               }
/*     */               
/* 373 */               resource.setAttribute(tm.getAttribute("Category"), cat);
/*     */             }
/*     */             
/* 376 */             String pro = f_props;
/*     */             
/* 378 */             if (pro != null)
/*     */             {
/* 380 */               if (pro.length() == 0)
/*     */               {
/* 382 */                 pro = null;
/*     */               }
/*     */               
/* 385 */               resource.setAttribute(tm.getAttribute("ShareProperties"), pro);
/*     */             }
/*     */           }
/*     */           
/* 389 */           if (resource != null)
/*     */           {
/* 391 */             ci.out.println("... processing complete");
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {
/* 395 */           ci.out.println("ERROR: " + e.getMessage() + " ::");
/*     */           
/* 397 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void outputChildren(ConsoleInput ci, String indent, ShareResourceDirContents node)
/*     */   {
/* 409 */     ShareResource[] kids = node.getChildren();
/*     */     
/* 411 */     for (int i = 0; i < kids.length; i++)
/*     */     {
/* 413 */       ShareResource kid = kids[i];
/*     */       
/* 415 */       ci.out.println(indent + kid.getName());
/*     */       
/* 417 */       if ((kid instanceof ShareResourceDirContents))
/*     */       {
/* 419 */         outputChildren(ci, indent + "    ", (ShareResourceDirContents)kid);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/Share.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */