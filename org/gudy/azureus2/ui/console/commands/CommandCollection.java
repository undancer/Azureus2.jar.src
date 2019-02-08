/*     */ package org.gudy.azureus2.ui.console.commands;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ public class CommandCollection
/*     */ {
/*  43 */   private final Map subCommands = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void execute(String commandName, ConsoleInput ci, List args)
/*     */   {
/*  54 */     IConsoleCommand command = get(commandName);
/*  55 */     command.execute(commandName, ci, args);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getCommandDescriptions()
/*     */   {
/*  65 */     StringWriter sw = new StringWriter();
/*  66 */     PrintWriter out = new PrintWriter(sw);
/*  67 */     for (Iterator iter = iterator(); iter.hasNext();) {
/*  68 */       IConsoleCommand cmd = (IConsoleCommand)iter.next();
/*  69 */       out.println(cmd.getCommandDescriptions());
/*     */     }
/*  71 */     return sw.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public IConsoleCommand get(String commandName)
/*     */   {
/*  81 */     return (IConsoleCommand)this.subCommands.get(commandName);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void add(IConsoleCommand command)
/*     */   {
/*  92 */     for (Iterator iter = command.getCommandNames().iterator(); iter.hasNext();) {
/*  93 */       String cmdName = (String)iter.next();
/*  94 */       this.subCommands.put(cmdName, command);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Iterator iterator()
/*     */   {
/* 105 */     return new HashSet(this.subCommands.values()).iterator();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/CommandCollection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */