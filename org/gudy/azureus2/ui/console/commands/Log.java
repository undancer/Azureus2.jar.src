/*     */ package org.gudy.azureus2.ui.console.commands;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.apache.commons.cli.Option;
/*     */ import org.apache.commons.cli.Options;
/*     */ import org.apache.log4j.Appender;
/*     */ import org.apache.log4j.ConsoleAppender;
/*     */ import org.apache.log4j.FileAppender;
/*     */ import org.apache.log4j.PatternLayout;
/*     */ import org.apache.log4j.varia.DenyAllFilter;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
/*     */ import org.gudy.azureus2.plugins.logging.LoggerChannelListener;
/*     */ import org.gudy.azureus2.ui.console.ConsoleInput;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Log
/*     */   extends OptionsConsoleCommand
/*     */ {
/*  35 */   private Map channel_listener_map = new HashMap();
/*     */   
/*     */   public Log()
/*     */   {
/*  39 */     super("log", "l");
/*     */   }
/*     */   
/*     */   protected Options getOptions()
/*     */   {
/*  44 */     Options options = new Options();
/*  45 */     options.addOption(new Option("f", "filename", true, "filename to write log to"));
/*  46 */     return options;
/*     */   }
/*     */   
/*     */   public void execute(String commandName, final ConsoleInput ci, CommandLine commandLine)
/*     */   {
/*  51 */     Appender con = org.apache.log4j.Logger.getRootLogger().getAppender("ConsoleAppender");
/*  52 */     List args = commandLine.getArgList();
/*  53 */     if ((con != null) && (!args.isEmpty())) {
/*  54 */       String subcommand = (String)args.get(0);
/*  55 */       if ("off".equalsIgnoreCase(subcommand)) {
/*  56 */         if (args.size() == 1) {
/*  57 */           con.addFilter(new DenyAllFilter());
/*  58 */           ci.out.println("> Console logging off");
/*     */         }
/*     */         else {
/*  61 */           String name = (String)args.get(1);
/*     */           
/*  63 */           Object[] entry = (Object[])this.channel_listener_map.remove(name);
/*     */           
/*  65 */           if (entry == null)
/*     */           {
/*  67 */             ci.out.println("> Channel '" + name + "' not being logged");
/*     */           }
/*     */           else
/*     */           {
/*  71 */             ((LoggerChannel)entry[0]).removeListener((LoggerChannelListener)entry[1]);
/*     */             
/*  73 */             ci.out.println("> Channel '" + name + "' logging off");
/*     */           }
/*     */         }
/*  76 */       } else if ("on".equalsIgnoreCase(subcommand))
/*     */       {
/*  78 */         if (args.size() == 1)
/*     */         {
/*  80 */           if (commandLine.hasOption('f'))
/*     */           {
/*     */ 
/*  83 */             String filename = commandLine.getOptionValue('f');
/*     */             
/*     */             try
/*     */             {
/*  87 */               Appender newAppender = new FileAppender(new PatternLayout("%d{ISO8601} %c{1}-%p: %m%n"), filename, true);
/*  88 */               newAppender.setName("ConsoleAppender");
/*  89 */               org.apache.log4j.Logger.getRootLogger().removeAppender(con);
/*  90 */               org.apache.log4j.Logger.getRootLogger().addAppender(newAppender);
/*  91 */               ci.out.println("> Logging to filename: " + filename);
/*     */             }
/*     */             catch (IOException e) {
/*  94 */               ci.out.println("> Unable to log to file: " + filename + ": " + e);
/*     */             }
/*     */           }
/*     */           else
/*     */           {
/*  99 */             if (!(con instanceof ConsoleAppender))
/*     */             {
/* 101 */               org.apache.log4j.Logger.getRootLogger().removeAppender(con);
/* 102 */               con = new ConsoleAppender(new PatternLayout("%r [%t] %p %c %x - %m%n"));
/* 103 */               con.setName("ConsoleAppender");
/* 104 */               org.apache.log4j.Logger.getRootLogger().addAppender(con);
/*     */             }
/*     */             
/* 107 */             ci.out.println("> Console logging on");
/*     */           }
/*     */           
/* 110 */           con.clearFilters();
/*     */         }
/*     */         else
/*     */         {
/* 114 */           Map channel_map = getChannelMap(ci);
/*     */           
/* 116 */           final String name = (String)args.get(1);
/*     */           
/* 118 */           LoggerChannel channel = (LoggerChannel)channel_map.get(name);
/*     */           
/* 120 */           if (channel == null)
/*     */           {
/* 122 */             ci.out.println("> Channel '" + name + "' not found");
/*     */           }
/* 124 */           else if (this.channel_listener_map.get(name) != null)
/*     */           {
/* 126 */             ci.out.println("> Channel '" + name + "' already being logged");
/*     */           }
/*     */           else
/*     */           {
/* 130 */             LoggerChannelListener l = new LoggerChannelListener()
/*     */             {
/*     */ 
/*     */ 
/*     */               public void messageLogged(int type, String content)
/*     */               {
/*     */ 
/*     */ 
/* 138 */                 ci.out.println("[" + name + "] " + content);
/*     */               }
/*     */               
/*     */ 
/*     */ 
/*     */ 
/*     */               public void messageLogged(String str, Throwable error)
/*     */               {
/* 146 */                 ci.out.println("[" + name + "] " + str);
/*     */                 
/* 148 */                 error.printStackTrace(ci.out);
/*     */               }
/*     */               
/* 151 */             };
/* 152 */             channel.addListener(l);
/*     */             
/* 154 */             this.channel_listener_map.put(name, new Object[] { channel, l });
/*     */             
/* 156 */             ci.out.println("> Channel '" + name + "' on");
/*     */           }
/*     */         }
/*     */       }
/* 160 */       else if (subcommand.equalsIgnoreCase("list"))
/*     */       {
/* 162 */         Map channel_map = getChannelMap(ci);
/*     */         
/* 164 */         Iterator it = channel_map.keySet().iterator();
/*     */         
/* 166 */         while (it.hasNext())
/*     */         {
/* 168 */           String name = (String)it.next();
/*     */           
/* 170 */           ci.out.println("  " + name + " [" + (this.channel_listener_map.get(name) == null ? "off" : "on") + "]");
/*     */         }
/*     */       }
/*     */       else {
/* 174 */         ci.out.println("> Command 'log': Subcommand '" + subcommand + "' unknown.");
/*     */       }
/*     */     } else {
/* 177 */       ci.out.println("> Console logger not found or missing subcommand for 'log'\r\n> log syntax: log [-f filename] (on [name]|off [name]|list)");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected Map getChannelMap(ConsoleInput ci)
/*     */   {
/* 185 */     Map channel_map = new HashMap();
/*     */     
/* 187 */     PluginInterface[] pis = ci.azureus_core.getPluginManager().getPluginInterfaces();
/*     */     
/* 189 */     for (int i = 0; i < pis.length; i++)
/*     */     {
/* 191 */       LoggerChannel[] logs = pis[i].getLogger().getChannels();
/*     */       
/* 193 */       if (logs.length > 0)
/*     */       {
/* 195 */         if (logs.length == 1)
/*     */         {
/* 197 */           channel_map.put(pis[i].getPluginName(), logs[0]);
/*     */         }
/*     */         else
/*     */         {
/* 201 */           for (int j = 0; j < logs.length; j++)
/*     */           {
/* 203 */             channel_map.put(pis[i].getPluginName() + "." + logs[j].getName(), logs[j]);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 209 */     return channel_map;
/*     */   }
/*     */   
/*     */   public static void commandLogtest(ConsoleInput ci, List args) {
/* 213 */     org.apache.log4j.Logger.getLogger("azureus2").fatal("Logging test" + ((args == null) || (args.isEmpty()) ? "" : new StringBuilder().append(": ").append(args.get(0).toString()).toString()));
/*     */   }
/*     */   
/*     */   public String getCommandDescriptions()
/*     */   {
/* 218 */     return "log [-f filename] (on [name]|off [name]|list)\t\t\tl\tTurn on/off console logging";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/Log.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */