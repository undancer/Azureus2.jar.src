/*     */ package org.gudy.azureus2.ui.console.commands;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import java.io.PrintStream;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManager;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStub;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStub.DownloadStubFile;
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
/*     */ public class Archive
/*     */   extends IConsoleCommand
/*     */ {
/*     */   public Archive()
/*     */   {
/*  37 */     super("archive", "ar");
/*     */   }
/*     */   
/*     */   public String getCommandDescriptions()
/*     */   {
/*  42 */     return "archive\t\tar\tLists, and allows the restoration of, archived downloads.";
/*     */   }
/*     */   
/*     */   public void printHelpExtra(PrintStream out, List args) {
/*  46 */     out.println("> -----");
/*  47 */     out.println("Subcommands:");
/*  48 */     out.println("list\t\tl\t\tList archived downloads");
/*  49 */     out.println("show <num>\ts\t\tShow archived download");
/*  50 */     out.println("restore <num>\tres\t\tRestore archived download");
/*  51 */     out.println("delete <num>\tdel\t\tDelete archived download");
/*  52 */     out.println("> -----");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void execute(String commandName, ConsoleInput ci, List<String> args)
/*     */   {
/*  59 */     if (args.size() > 0)
/*     */     {
/*  61 */       PluginInterface pi = ci.getCore().getPluginManager().getDefaultPluginInterface();
/*     */       
/*  63 */       DownloadStub[] stubs = pi.getDownloadManager().getDownloadStubs();
/*     */       
/*  65 */       String sub = (String)args.get(0);
/*     */       
/*  67 */       int index = -1;
/*     */       
/*  69 */       if (args.size() > 1)
/*     */       {
/*  71 */         String index_str = (String)args.get(1);
/*     */         try
/*     */         {
/*  74 */           index = Integer.parseInt(index_str);
/*     */           
/*  76 */           index--;
/*     */           
/*  78 */           if ((index < 0) || (index >= stubs.length))
/*     */           {
/*  80 */             index = -1;
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {}
/*     */         
/*     */ 
/*  86 */         if (index == -1)
/*     */         {
/*  88 */           ci.out.println("Invalid archive index: " + index_str);
/*     */         }
/*     */       }
/*     */       
/*  92 */       if ((sub.equals("list")) || (sub.equals("l")))
/*     */       {
/*  94 */         int pos = 1;
/*     */         
/*  96 */         ci.out.println("> -----");
/*     */         
/*  98 */         for (DownloadStub stub : stubs)
/*     */         {
/* 100 */           System.out.println(" " + pos++ + "\t" + stub.getName() + " (" + DisplayFormatters.formatByteCountToKiBEtc(stub.getTorrentSize()) + ")");
/*     */         }
/*     */         
/* 103 */         ci.out.println("> -----");
/*     */       }
/* 105 */       else if ((index != -1) && ((sub.equals("show")) || (sub.equals("s"))))
/*     */       {
/*     */         try {
/* 108 */           DownloadStub stub = stubs[index];
/*     */           
/* 110 */           ci.out.println("> -----");
/* 111 */           ci.out.println("  " + stub.getName() + " - hash=" + ByteFormatter.encodeString(stub.getTorrentHash()));
/*     */           
/* 113 */           DownloadStub.DownloadStubFile[] files = stub.getStubFiles();
/*     */           
/* 115 */           ci.out.println("  Files: " + files.length);
/*     */           
/* 117 */           for (DownloadStub.DownloadStubFile file : files)
/*     */           {
/* 119 */             long length = file.getLength();
/*     */             
/* 121 */             ci.out.println("    " + file.getFile() + " - " + (length < 0L ? "Not downloaded" : DisplayFormatters.formatByteCountToKiBEtc(length)));
/*     */           }
/*     */           
/* 124 */           ci.out.println("> -----");
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 128 */           ci.out.print(e);
/*     */         }
/*     */       }
/* 131 */       else if ((index != -1) && ((sub.equals("restore")) || (sub.equals("res"))))
/*     */       {
/*     */         try {
/* 134 */           Download d = stubs[index].destubbify();
/*     */           
/* 136 */           ci.out.println("> Restore of " + d.getName() + " succeeded.");
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 140 */           ci.out.print(e);
/*     */         }
/*     */       }
/* 143 */       else if ((index != -1) && ((sub.equals("delete")) || (sub.equals("del"))))
/*     */       {
/*     */         try {
/* 146 */           DownloadStub stub = stubs[index];
/*     */           
/* 148 */           String name = stub.getName();
/*     */           
/* 150 */           stub.remove();
/*     */           
/* 152 */           ci.out.println("> Delete of " + name + " succeeded.");
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 156 */           ci.out.print(e);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 161 */         ci.out.println("Unsupported sub-command: " + sub);
/*     */         
/* 163 */         return;
/*     */       }
/*     */     }
/*     */     else {
/* 167 */       printHelp(ci.out, args);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/Archive.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */