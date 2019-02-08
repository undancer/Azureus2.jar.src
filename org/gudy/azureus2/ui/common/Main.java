/*     */ package org.gudy.azureus2.ui.common;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreException;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.launcher.Launcher;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileReader;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.Socket;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Collection;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.StringTokenizer;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.apache.commons.cli.CommandLineParser;
/*     */ import org.apache.commons.cli.HelpFormatter;
/*     */ import org.apache.commons.cli.OptionBuilder;
/*     */ import org.apache.commons.cli.Options;
/*     */ import org.apache.commons.cli.ParseException;
/*     */ import org.apache.commons.cli.PosixParser;
/*     */ import org.apache.log4j.Appender;
/*     */ import org.apache.log4j.ConsoleAppender;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.log4j.PatternLayout;
/*     */ import org.apache.log4j.varia.DenyAllFilter;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.Constants;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Main
/*     */ {
/*  62 */   public static String DEFAULT_UI = "swt";
/*     */   
/*  64 */   public static StartServer start = null;
/*     */   
/*     */   protected static AzureusCore core;
/*     */   
/*     */   private static CommandLine parseCommands(String[] args, boolean constart)
/*     */   {
/*  70 */     if (args == null) {
/*  71 */       return null;
/*     */     }
/*  73 */     CommandLineParser parser = new PosixParser();
/*  74 */     Options options = new Options();
/*  75 */     options.addOption("h", "help", false, "Show this help.");
/*     */     
/*  77 */     OptionBuilder.withLongOpt("exec");
/*  78 */     OptionBuilder.hasArg();
/*  79 */     OptionBuilder.withArgName("file");
/*  80 */     OptionBuilder.withDescription("Execute script file. The file should end with 'logout', otherwise the parser thread doesn't stop.");
/*  81 */     options.addOption(OptionBuilder.create('e'));
/*     */     
/*  83 */     OptionBuilder.withLongOpt("command");
/*  84 */     OptionBuilder.hasArg();
/*  85 */     OptionBuilder.withArgName("command");
/*  86 */     OptionBuilder.withDescription("Execute single script command. Try '-c help' for help on commands.");
/*  87 */     options.addOption(OptionBuilder.create('c'));
/*     */     
/*  89 */     OptionBuilder.withLongOpt("ui");
/*  90 */     OptionBuilder.withDescription("Run <uis>. ',' separated list of user interfaces to run. The first one given will respond to requests without determinable source UI (e.g. further torrents added via command line).");
/*  91 */     OptionBuilder.withArgName("uis");
/*  92 */     OptionBuilder.hasArg();
/*  93 */     options.addOption(OptionBuilder.create('u'));
/*     */     
/*  95 */     CommandLine commands = null;
/*     */     try {
/*  97 */       commands = parser.parse(options, args, true);
/*     */     } catch (ParseException exp) {
/*  99 */       Logger.getLogger("azureus2").error("Parsing failed.  Reason: " + exp.getMessage(), exp);
/* 100 */       if (constart)
/* 101 */         System.exit(2);
/*     */     }
/* 103 */     if ((commands != null) && (commands.hasOption('h')) && 
/* 104 */       (constart)) {
/* 105 */       HelpFormatter hf = new HelpFormatter();
/* 106 */       hf.printHelp("java org.gudy.azureus2.ui.common.Main", "Optionally you can put torrent files to add to the end of the command line.\r\n", options, "Available User Interfaces: swt (default), web, console\r\nThe default interface is not started if you give either the '-e' or '-c' option (But you can start it by hand with '-u').", true);
/* 107 */       System.exit(0);
/*     */     }
/*     */     
/* 110 */     return commands;
/*     */   }
/*     */   
/*     */   public static void initRootLogger() {
/* 114 */     if (Logger.getRootLogger().getAppender("ConsoleAppender") == null)
/*     */     {
/* 116 */       Appender app = new ConsoleAppender(new PatternLayout("%r [%t] %p %c %x - %m%n"));
/* 117 */       app.setName("ConsoleAppender");
/* 118 */       app.addFilter(new DenyAllFilter());
/* 119 */       Logger.getRootLogger().addAppender(app);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 124 */     if (Launcher.checkAndLaunch(Main.class, args)) {
/* 125 */       return;
/*     */     }
/*     */     
/*     */ 
/* 129 */     COConfigurationManager.preInitialise();
/*     */     
/* 131 */     String mi_str = System.getProperty("MULTI_INSTANCE");
/*     */     
/* 133 */     boolean mi = (mi_str != null) && (mi_str.equalsIgnoreCase("true"));
/*     */     
/* 135 */     initRootLogger();
/*     */     try
/*     */     {
/* 138 */       CommandLine commands = parseCommands(args, true);
/*     */       
/* 140 */       if ((commands != null) && (directLaunch(args, commands)))
/*     */       {
/* 142 */         return;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 147 */       if (mi)
/*     */       {
/* 149 */         System.out.println("MULTI_INSTANCE enabled");
/*     */         
/* 151 */         core = AzureusCoreFactory.create();
/*     */         
/* 153 */         processArgs(args, core, commands);
/*     */         
/* 155 */         return;
/*     */       }
/*     */       
/* 158 */       start = new StartServer();
/*     */       
/* 160 */       if ((start == null) || (start.getServerState() == 0))
/*     */       {
/*     */ 
/* 163 */         new StartSocket(args);
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 168 */         core = AzureusCoreFactory.create();
/*     */         
/*     */ 
/* 171 */         start.start();
/*     */         
/* 173 */         processArgs(args, core, commands);
/*     */       }
/*     */     }
/*     */     catch (AzureusCoreException e) {
/* 177 */       System.out.println("Start fails:");
/*     */       
/* 179 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public static void shutdown() {
/* 184 */     if (start != null)
/*     */     {
/* 186 */       start.stopIt();
/*     */     }
/*     */     
/* 189 */     if (core != null) {
/*     */       try {
/* 191 */         core.stop();
/*     */       }
/*     */       catch (AzureusCoreException e)
/*     */       {
/* 195 */         System.out.println("Stop fails:");
/*     */         
/* 197 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */     
/* 201 */     SimpleDateFormat temp = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
/* 202 */     Logger.getLogger("azureus2").fatal("Azureus stopped at " + temp.format(new Date()));
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
/*     */   public static boolean directLaunch(String[] args, CommandLine commands)
/*     */   {
/* 215 */     if (commands.hasOption('u'))
/*     */     {
/* 217 */       String uinames = commands.getOptionValue('u');
/*     */       
/* 219 */       if (uinames.indexOf(',') != -1)
/*     */       {
/* 221 */         return false;
/*     */       }
/*     */       
/* 224 */       if (!uinames.equalsIgnoreCase(DEFAULT_UI))
/*     */       {
/* 226 */         return false;
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/* 231 */       String uiclass = "org.gudy.azureus2.ui." + DEFAULT_UI + ".Main";
/*     */       
/* 233 */       Class main_class = Class.forName(uiclass);
/*     */       
/* 235 */       Method main_method = main_class.getMethod("main", new Class[] { String[].class });
/*     */       
/* 237 */       main_method.invoke(null, new Object[] { commands.getArgs() });
/*     */       
/* 239 */       return true;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 243 */       e.printStackTrace();
/*     */     }
/* 245 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void processArgs(String[] args, AzureusCore new_core, CommandLine commands)
/*     */   {
/* 255 */     if (commands == null) {
/* 256 */       commands = parseCommands(args, false);
/*     */     }
/* 258 */     if ((commands != null) && ((args.length > 0) || (new_core != null))) {
/* 259 */       if (UIConst.UIS == null) {
/* 260 */         UIConst.UIS = new HashMap();
/*     */       }
/* 262 */       if (commands.hasOption('u')) {
/* 263 */         String uinames = commands.getOptionValue('u');
/* 264 */         if (uinames.indexOf(',') == -1) {
/* 265 */           if (!UIConst.UIS.containsKey(uinames))
/* 266 */             UIConst.UIS.put(uinames, UserInterfaceFactory.getUI(uinames));
/*     */         } else {
/* 268 */           StringTokenizer stok = new StringTokenizer(uinames, ",");
/* 269 */           while (stok.hasMoreTokens()) {
/* 270 */             String uin = stok.nextToken();
/* 271 */             if (!UIConst.UIS.containsKey(uin)) {
/* 272 */               UIConst.UIS.put(uin, UserInterfaceFactory.getUI(uin));
/*     */             }
/*     */           }
/*     */         }
/* 276 */       } else if ((UIConst.UIS.isEmpty()) && (!commands.hasOption('c')) && (!commands.hasOption('e'))) {
/* 277 */         UIConst.UIS.put(DEFAULT_UI, UserInterfaceFactory.getUI(DEFAULT_UI));
/*     */       }
/*     */       
/* 280 */       Iterator uis = UIConst.UIS.values().iterator();
/* 281 */       boolean isFirst = true;
/* 282 */       String[] theRest = commands.getArgs();
/* 283 */       while (uis.hasNext()) {
/* 284 */         IUserInterface ui = (IUserInterface)uis.next();
/* 285 */         ui.init(isFirst, UIConst.UIS.size() > 1);
/* 286 */         theRest = ui.processArgs(theRest);
/* 287 */         isFirst = false;
/*     */       }
/*     */       
/* 290 */       if (new_core != null)
/*     */       {
/* 292 */         SimpleDateFormat temp = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
/*     */         
/* 294 */         UIConst.startTime = new Date();
/*     */         
/* 296 */         Logger.getLogger("azureus2").fatal("Azureus started at " + temp.format(UIConst.startTime));
/*     */         
/* 298 */         UIConst.setAzureusCore(new_core);
/*     */       }
/*     */       
/* 301 */       uis = UIConst.UIS.values().iterator();
/* 302 */       while (uis.hasNext()) {
/* 303 */         ((IUserInterface)uis.next()).startUI();
/*     */       }
/*     */       
/* 306 */       Constructor conConsoleInput = null;
/*     */       try {
/* 308 */         Class clConsoleInput = Class.forName("org.gudy.azureus2.ui.console.ConsoleInput");
/*     */         
/*     */ 
/*     */ 
/* 312 */         Class[] params = { String.class, AzureusCore.class, Reader.class, PrintStream.class, Boolean.class };
/*     */         
/* 314 */         conConsoleInput = clConsoleInput.getConstructor(params);
/*     */       } catch (Exception e) {
/* 316 */         e.printStackTrace();
/*     */       }
/* 318 */       if (commands.hasOption('e')) {
/* 319 */         if (conConsoleInput != null) {
/*     */           try {
/* 321 */             Object[] params = { commands.getOptionValue('e'), new_core, new FileReader(commands.getOptionValue('e')), System.out, Boolean.FALSE };
/* 322 */             conConsoleInput.newInstance(params);
/*     */           } catch (FileNotFoundException e) {
/* 324 */             Logger.getLogger("azureus2").error("Script file not found: " + e.toString());
/*     */           } catch (Exception e) {
/* 326 */             Logger.getLogger("azureus2").error("Error invocating the script processor: " + e.toString());
/*     */           }
/*     */         } else {
/* 329 */           Logger.getLogger("azureus2").error("ConsoleInput class not found. You need the console ui package to use '-e'");
/*     */         }
/*     */       }
/* 332 */       if (commands.hasOption('c')) {
/* 333 */         if (conConsoleInput != null) {
/* 334 */           String comm = commands.getOptionValue('c');
/* 335 */           comm = comm + "\nlogout\n";
/* 336 */           Object[] params = { commands.getOptionValue('c'), UIConst.getAzureusCore(), new StringReader(comm), System.out, Boolean.FALSE };
/*     */           try {
/* 338 */             conConsoleInput.newInstance(params);
/*     */           } catch (Exception e) {
/* 340 */             Logger.getLogger("azureus2").error("Error invocating the script processor: " + e.toString());
/*     */           }
/*     */         } else {
/* 343 */           Logger.getLogger("azureus2").error("ConsoleInput class not found. You need the console ui package to use '-e'");
/*     */         }
/*     */       }
/* 346 */       openTorrents(theRest);
/*     */     } else {
/* 348 */       Logger.getLogger("azureus2").error("No commands to process");
/*     */     }
/*     */   }
/*     */   
/*     */   public static void openTorrents(String[] torrents) {
/* 353 */     if ((UIConst.UIS != null) && (!UIConst.UIS.isEmpty()) && (torrents.length > 0)) {
/* 354 */       for (int l = 0; l < torrents.length; l++) {
/* 355 */         ((IUserInterface)UIConst.UIS.values().toArray()[0]).openTorrent(torrents[l]);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static class StartSocket {
/*     */     public StartSocket(String[] args) {
/* 362 */       Socket sck = null;
/* 363 */       PrintWriter pw = null;
/*     */       try {
/* 365 */         System.out.println("StartSocket: passing startup args to already-running process.");
/*     */         
/*     */ 
/*     */ 
/* 369 */         sck = new Socket("127.0.0.1", Constants.INSTANCE_PORT);
/* 370 */         pw = new PrintWriter(new OutputStreamWriter(sck.getOutputStream()));
/* 371 */         StringBuilder buffer = new StringBuilder("Azureus Start Server Access;args;");
/* 372 */         for (int i = 0; i < args.length; i++) {
/* 373 */           String arg = args[i].replaceAll("&", "&&").replaceAll(";", "&;");
/* 374 */           buffer.append(arg);
/* 375 */           buffer.append(';');
/*     */         }
/* 377 */         pw.println(buffer.toString());
/* 378 */         pw.flush(); return;
/*     */       } catch (Exception e) {
/* 380 */         e.printStackTrace();
/*     */       } finally {
/*     */         try {
/* 383 */           if (pw != null) {
/* 384 */             pw.close();
/*     */           }
/*     */         } catch (Exception e) {}
/*     */         try {
/* 388 */           if (sck != null) {
/* 389 */             sck.close();
/*     */           }
/*     */         }
/*     */         catch (Exception e) {}
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/common/Main.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */