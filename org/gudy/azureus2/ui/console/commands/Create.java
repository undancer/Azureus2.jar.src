/*     */ package org.gudy.azureus2.ui.console.commands;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentCreator;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentProgressListener;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Create
/*     */   extends IConsoleCommand
/*     */ {
/*     */   public Create()
/*     */   {
/*  39 */     super("create");
/*     */   }
/*     */   
/*     */   public String getCommandDescriptions() {
/*  43 */     return "create <input file or folder> <output torrent file> <tracker url> [another tracker url]*\t\t\tCreate a torrent file";
/*     */   }
/*     */   
/*     */   public void printHelpExtra(PrintStream out, List args) {
/*  47 */     out.println("> -----");
/*  48 */     out.println("create <input file or folder> <output torrent file> <tracker url> [another tracker url]*");
/*  49 */     out.println("\tFor example: create /tmp/file.dat /tmp/file.dat.torrent http://tracker.there.com:6969/announce");
/*  50 */     out.println("> -----");
/*     */   }
/*     */   
/*     */   public void execute(String commandName, final ConsoleInput ci, List<String> args)
/*     */   {
/*  55 */     if (args.size() < 3)
/*     */     {
/*  57 */       printHelp(ci.out, args);
/*  58 */       return;
/*     */     }
/*     */     
/*  61 */     File input_file = new File((String)args.get(0));
/*     */     
/*  63 */     if (!input_file.exists())
/*     */     {
/*  65 */       ci.out.println("Input file '" + input_file.getAbsolutePath() + "' doesn't exist");
/*     */       
/*  67 */       return;
/*     */     }
/*     */     
/*  70 */     File output_file = new File((String)args.get(1));
/*     */     
/*  72 */     if (output_file.exists())
/*     */     {
/*  74 */       ci.out.println("Output file '" + input_file.getAbsolutePath() + "' already exists");
/*     */       
/*  76 */       return;
/*     */     }
/*     */     
/*  79 */     List<URL> urls = new ArrayList();
/*     */     
/*  81 */     for (int i = 2; i < args.size(); i++) {
/*     */       try
/*     */       {
/*  84 */         urls.add(new URL((String)args.get(i)));
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*  88 */         ci.out.println("Invalid URL: " + (String)args.get(i));
/*     */         
/*  90 */         return;
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/*  95 */       TOTorrentCreator creator = TOTorrentFactory.createFromFileOrDirWithComputedPieceLength(input_file, (URL)urls.get(0));
/*     */       
/*  97 */       creator.addListener(new TOTorrentProgressListener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void reportProgress(int percent_complete)
/*     */         {
/*     */ 
/* 104 */           ci.out.println("\t\t" + percent_complete + "%");
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void reportCurrentTask(String task_description)
/*     */         {
/* 111 */           ci.out.println("\t" + task_description);
/*     */         }
/*     */         
/* 114 */       });
/* 115 */       TOTorrent torrent = creator.create();
/*     */       
/* 117 */       if (urls.size() > 1)
/*     */       {
/* 119 */         TOTorrentAnnounceURLGroup group = torrent.getAnnounceURLGroup();
/*     */         
/* 121 */         TOTorrentAnnounceURLSet[] sets = new TOTorrentAnnounceURLSet[urls.size()];
/*     */         
/* 123 */         for (int i = 0; i < urls.size(); i++)
/*     */         {
/* 125 */           sets[i] = group.createAnnounceURLSet(new URL[] { (URL)urls.get(i) });
/*     */           
/* 127 */           ci.out.println("\tAdded URL '" + urls.get(i) + "'");
/*     */         }
/*     */         
/* 130 */         group.setAnnounceURLSets(sets);
/*     */       }
/*     */       
/* 133 */       torrent.serialiseToBEncodedFile(output_file);
/*     */       
/* 135 */       ci.out.println("\tTorrent written to '" + output_file + "'");
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 139 */       ci.out.println("Failed to create torrent: " + Debug.getNestedExceptionMessage(e));
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/Create.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */