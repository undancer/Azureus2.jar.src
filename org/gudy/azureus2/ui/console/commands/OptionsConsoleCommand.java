/*     */ package org.gudy.azureus2.ui.console.commands;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.apache.commons.cli.CommandLineParser;
/*     */ import org.apache.commons.cli.HelpFormatter;
/*     */ import org.apache.commons.cli.Options;
/*     */ import org.apache.commons.cli.ParseException;
/*     */ import org.apache.commons.cli.PosixParser;
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
/*     */ public abstract class OptionsConsoleCommand
/*     */   extends IConsoleCommand
/*     */ {
/*  46 */   private Options options = new Options();
/*  47 */   private CommandLineParser parser = null;
/*     */   
/*     */   public OptionsConsoleCommand(String main_name) {
/*  50 */     super(main_name);
/*     */   }
/*     */   
/*     */   public OptionsConsoleCommand(String main_name, String short_name) {
/*  54 */     super(main_name, short_name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void execute(String commandName, ConsoleInput console, List arguments)
/*     */   {
/*  61 */     CommandLineParser parser = getParser();
/*     */     
/*     */     try
/*     */     {
/*  65 */       String[] args = new String[arguments.size()];
/*  66 */       int i = 0;
/*  67 */       for (Iterator iter = arguments.iterator(); iter.hasNext();) {
/*  68 */         String arg = (String)iter.next();
/*  69 */         args[(i++)] = arg;
/*     */       }
/*  71 */       CommandLine line = parser.parse(getOptions(), args);
/*  72 */       execute(commandName, console, line);
/*     */     }
/*     */     catch (ParseException e) {
/*  75 */       console.out.println(">> Invalid arguments: " + e.getMessage());
/*     */       
/*  77 */       printHelp(console.out, arguments);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void printHelpExtra(PrintStream out, List args)
/*     */   {
/*  86 */     HelpFormatter formatter = new HelpFormatter();
/*  87 */     PrintWriter writer = new PrintWriter(out);
/*  88 */     writer.println("> -----");
/*  89 */     writer.println(getCommandDescriptions());
/*     */     
/*  91 */     formatter.printOptions(writer, 80, getOptions(), 4, 4);
/*  92 */     writer.println("> -----");
/*  93 */     writer.flush();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void execute(String paramString, ConsoleInput paramConsoleInput, CommandLine paramCommandLine);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected CommandLineParser getParser()
/*     */   {
/* 108 */     if (this.parser == null)
/* 109 */       this.parser = new PosixParser();
/* 110 */     return this.parser;
/*     */   }
/*     */   
/*     */ 
/*     */   protected Options getOptions()
/*     */   {
/* 116 */     return this.options;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/OptionsConsoleCommand.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */