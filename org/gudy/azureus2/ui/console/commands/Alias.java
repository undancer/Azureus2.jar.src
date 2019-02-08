/*     */ package org.gudy.azureus2.ui.console.commands;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.apache.commons.cli.Option;
/*     */ import org.apache.commons.cli.Options;
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
/*     */ public class Alias
/*     */   extends OptionsConsoleCommand
/*     */ {
/*     */   public Alias()
/*     */   {
/*  43 */     super("alias");
/*  44 */     getOptions().addOption(new Option("d", "delete", false, "delete the specified alias"));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getCommandDescriptions()
/*     */   {
/*  51 */     return "alias [-d] [aliasname] [arguments...]\tadd/modify/delete aliases. use with no argument to show existing aliases";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void execute(String commandName, ConsoleInput console, CommandLine commandLine)
/*     */   {
/*  59 */     List args = commandLine.getArgList();
/*  60 */     if (args.isEmpty())
/*     */     {
/*  62 */       if (commandLine.hasOption('d')) {
/*  63 */         console.out.println(commandName + " --delete requires the name of an alias to remove");
/*     */       } else
/*  65 */         printAliases(console);
/*  66 */       return;
/*     */     }
/*  68 */     if (commandLine.hasOption('d')) {
/*  69 */       deleteAlias(console, (String)args.get(0));
/*     */     }
/*     */     else {
/*  72 */       String aliasName = (String)args.remove(0);
/*  73 */       if (args.isEmpty())
/*     */       {
/*  75 */         printAlias(console, aliasName);
/*     */       }
/*     */       else {
/*  78 */         addAlias(console, aliasName, args);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void printAlias(ConsoleInput ci, String aliasName)
/*     */   {
/*  86 */     String aliasText = (String)ci.aliases.get(aliasName);
/*  87 */     if (aliasText == null)
/*     */     {
/*  89 */       ci.out.println("> Error: Alias '" + aliasName + "' does not exist");
/*     */     }
/*     */     else
/*     */     {
/*  93 */       ci.out.println("> " + aliasName + "=" + aliasText);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void deleteAlias(ConsoleInput ci, String aliasName)
/*     */   {
/* 102 */     if (ci.aliases.remove(aliasName) == null)
/*     */     {
/* 104 */       ci.out.println("> Error: Alias '" + aliasName + "' does not exist");
/*     */     }
/*     */     else
/*     */     {
/* 108 */       ci.out.println("> Alias: '" + aliasName + "' deleted");
/* 109 */       ci.saveAliases();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void addAlias(ConsoleInput ci, String aliasName, List argList)
/*     */   {
/* 119 */     StringBuilder aliasText = new StringBuilder();
/* 120 */     for (Iterator iter = argList.iterator(); iter.hasNext();) {
/* 121 */       String arg = (String)iter.next();
/* 122 */       if (arg.contains(" ")) {
/* 123 */         aliasText.append("\"").append(arg).append("\"");
/*     */       } else
/* 125 */         aliasText.append(arg);
/* 126 */       aliasText.append(" ");
/*     */     }
/* 128 */     ci.aliases.put(aliasName, aliasText.toString());
/* 129 */     ci.saveAliases();
/* 130 */     printAlias(ci, aliasName);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void printAliases(ConsoleInput ci)
/*     */   {
/* 138 */     for (Iterator iter = ci.aliases.keySet().iterator(); iter.hasNext();) {
/* 139 */       String aliasName = (String)iter.next();
/* 140 */       printAlias(ci, aliasName);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/Alias.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */