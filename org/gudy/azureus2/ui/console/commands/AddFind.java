/*     */ package org.gudy.azureus2.ui.console.commands;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCoreException;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.List;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.apache.commons.cli.OptionBuilder;
/*     */ import org.apache.commons.cli.Options;
/*     */ import org.gudy.azureus2.ui.console.ConsoleInput;
/*     */ import org.pf.file.FileFinder;
/*     */ import org.pf.text.StringUtil;
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
/*     */ public class AddFind
/*     */   extends OptionsConsoleCommand
/*     */ {
/*     */   public AddFind()
/*     */   {
/*  37 */     super("add", "a");
/*     */     
/*  39 */     OptionBuilder.withArgName("outputDir");
/*  40 */     OptionBuilder.withLongOpt("output");
/*  41 */     OptionBuilder.hasArg();
/*  42 */     OptionBuilder.withDescription("override default download directory");
/*  43 */     OptionBuilder.withType(File.class);
/*  44 */     getOptions().addOption(OptionBuilder.create('o'));
/*  45 */     getOptions().addOption("r", "recurse", false, "recurse sub-directories.");
/*  46 */     getOptions().addOption("f", "find", false, "only find files, don't add.");
/*  47 */     getOptions().addOption("h", "help", false, "display help about this command");
/*  48 */     getOptions().addOption("l", "list", false, "list previous find results");
/*     */   }
/*     */   
/*     */   public String getCommandDescriptions()
/*     */   {
/*  53 */     return "add [addoptions] [.torrent path|url]\t\ta\tAdd a download from the given .torrent file path or url. Example: 'add /path/to/the.torrent' or 'add http://www.url.com/to/the.torrent'";
/*     */   }
/*     */   
/*     */   public void execute(String commandName, ConsoleInput ci, CommandLine commands)
/*     */   {
/*  58 */     if (commands.hasOption('l'))
/*     */     {
/*  60 */       ci.out.println("> -----");
/*  61 */       showAdds(ci);
/*  62 */       ci.out.println("> -----");
/*  63 */       return;
/*     */     }
/*  65 */     if ((commands.hasOption('h')) || (commands.getArgs().length == 0))
/*     */     {
/*  67 */       printHelp(ci.out, (String)null);
/*  68 */       return;
/*     */     }
/*  70 */     String outputDir = ".";
/*  71 */     if (commands.hasOption('o')) {
/*  72 */       outputDir = commands.getOptionValue('o');
/*     */     } else {
/*  74 */       outputDir = ci.getDefaultSaveDirectory();
/*     */     }
/*  76 */     File f = new File(outputDir);
/*  77 */     if (!f.isAbsolute())
/*     */     {
/*     */       try
/*     */       {
/*  81 */         outputDir = new File(".", outputDir).getCanonicalPath();
/*     */       } catch (IOException e) {
/*  83 */         throw new AzureusCoreException("exception occurred while converting directory: ./" + outputDir + " to its canonical path");
/*     */       }
/*     */     }
/*  86 */     boolean scansubdir = commands.hasOption('r');
/*  87 */     boolean finding = commands.hasOption('f');
/*     */     
/*  89 */     String[] whatelse = commands.getArgs();
/*  90 */     for (int i = 0; i < whatelse.length; i++) {
/*  91 */       String arg = whatelse[i];
/*     */       try
/*     */       {
/*  94 */         new URL(arg);
/*  95 */         addRemote(ci, arg, outputDir);
/*     */       }
/*     */       catch (MalformedURLException e)
/*     */       {
/*  99 */         addLocal(ci, arg, outputDir, scansubdir, finding);
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
/*     */   protected void addRemote(ConsoleInput ci, String arg, String outputDir)
/*     */   {
/* 112 */     ci.out.println("> Starting Download of " + arg + " ...");
/*     */     try {
/* 114 */       ci.downloadRemoteTorrent(arg, outputDir);
/*     */     } catch (Exception e) {
/* 116 */       ci.out.println("An error occurred while downloading torrent: " + e.getMessage());
/* 117 */       e.printStackTrace(ci.out);
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
/*     */   protected void addLocal(ConsoleInput ci, String arg, String outputDir, boolean scansubdir, boolean finding)
/*     */   {
/* 132 */     arg = transformLocalArgument(arg);
/*     */     
/* 134 */     File test = new File(arg);
/* 135 */     if (test.exists()) {
/* 136 */       if (test.isDirectory()) {
/* 137 */         File[] toadd = FileFinder.findFiles(arg, "*.torrent;*.tor", scansubdir);
/* 138 */         if ((toadd != null) && (toadd.length > 0)) {
/* 139 */           addFiles(ci, toadd, finding, outputDir);
/*     */         } else {
/* 141 */           ci.adds = null;
/* 142 */           ci.out.println("> Directory '" + arg + "' seems to contain no torrent files.");
/*     */         }
/*     */       } else {
/* 145 */         ci.downloadTorrent(arg, outputDir);
/* 146 */         ci.out.println("> '" + arg + "' added.");
/* 147 */         ci.torrents.clear();
/*     */       }
/* 149 */       return;
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 154 */       int id = Integer.parseInt(arg);
/* 155 */       if ((ci.adds != null) && (ci.adds.length > id))
/*     */       {
/* 157 */         String torrentPath = ci.adds[id].getAbsolutePath();
/* 158 */         ci.downloadTorrent(torrentPath, outputDir);
/* 159 */         ci.out.println("> '" + torrentPath + "' added.");
/* 160 */         ci.torrents.clear();
/*     */       }
/*     */       else
/*     */       {
/* 164 */         ci.out.println("> No such file id '" + id + "'. Try \"add -l\" to list available files");
/*     */       }
/* 166 */       return;
/*     */ 
/*     */     }
/*     */     catch (NumberFormatException e)
/*     */     {
/* 171 */       String dirName = test.getParent();
/* 172 */       if (dirName == null)
/* 173 */         dirName = ".";
/* 174 */       String filePattern = test.getName();
/* 175 */       File[] files = FileFinder.findFiles(dirName, filePattern, false);
/* 176 */       if ((files != null) && (files.length > 0)) {
/* 177 */         addFiles(ci, files, finding, outputDir);
/*     */       } else {
/* 179 */         ci.adds = null;
/* 180 */         ci.out.println("> No files found. Searched for '" + filePattern + "' in '" + dirName + "'");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String transformLocalArgument(String arg)
/*     */   {
/* 190 */     if ((arg.startsWith("~/")) || (arg.equals("~")))
/*     */     {
/* 192 */       arg = StringUtil.current().replaceAll(arg, "~", System.getProperty("user.home"));
/*     */     }
/* 194 */     return arg;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void addFiles(ConsoleInput ci, File[] toadd, boolean finding, String outputDir)
/*     */   {
/* 205 */     ci.out.println("> -----");
/* 206 */     ci.out.println("> Found " + toadd.length + " files:");
/*     */     
/* 208 */     if (finding)
/*     */     {
/* 210 */       ci.adds = toadd;
/* 211 */       showAdds(ci);
/*     */     }
/*     */     else
/*     */     {
/* 215 */       for (int i = 0; i < toadd.length; i++) {
/* 216 */         ci.downloadTorrent(toadd[i].getAbsolutePath(), outputDir);
/* 217 */         ci.out.println("> '" + toadd[i].getAbsolutePath() + "' added.");
/* 218 */         ci.torrents.clear();
/*     */       }
/*     */     }
/* 221 */     ci.out.println("> -----");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void showAdds(ConsoleInput ci)
/*     */   {
/* 229 */     if ((ci.adds == null) || (ci.adds.length == 0))
/*     */     {
/* 231 */       ci.out.println("> No files found. Try \"add -f <path>\" first");
/* 232 */       return;
/*     */     }
/* 234 */     for (int i = 0; i < ci.adds.length; i++) {
/* 235 */       ci.out.print(">\t" + i + ":\t");
/*     */       try {
/* 237 */         ci.out.println(ci.adds[i].getCanonicalPath());
/*     */       } catch (Exception e) {
/* 239 */         ci.out.println(ci.adds[i].getAbsolutePath());
/*     */       }
/*     */     }
/* 242 */     ci.out.println("> To add, simply type 'add <id>'");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/AddFind.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */