/*     */ package org.gudy.azureus2.ui.console.multiuser;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import java.io.PrintStream;
/*     */ import java.io.Reader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.ui.console.ConsoleInput;
/*     */ import org.gudy.azureus2.ui.console.UserProfile;
/*     */ import org.gudy.azureus2.ui.console.commands.IConsoleCommand;
/*     */ import org.gudy.azureus2.ui.console.multiuser.commands.Show;
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
/*     */ public class MultiUserConsoleInput
/*     */   extends ConsoleInput
/*     */ {
/*     */   private List adminCommands;
/*     */   private List userCommands;
/*     */   
/*     */   public MultiUserConsoleInput(String con, AzureusCore _azureus_core, Reader _in, PrintStream _out, Boolean _controlling, UserProfile profile)
/*     */   {
/*  65 */     super(con, _azureus_core, _in, _out, _controlling, profile);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void initialise()
/*     */   {
/*  73 */     this.adminCommands = new ArrayList();
/*  74 */     this.adminCommands.add("quit");
/*  75 */     this.adminCommands.add("share");
/*  76 */     this.adminCommands.add("user");
/*     */     
/*     */ 
/*  79 */     this.adminCommands.add("move");
/*  80 */     this.adminCommands.add("log");
/*  81 */     this.adminCommands.add("ui");
/*     */     
/*  83 */     this.userCommands = new ArrayList();
/*  84 */     this.userCommands.add("set");
/*  85 */     this.userCommands.add("alias");
/*  86 */     this.userCommands.add("add");
/*     */     
/*  88 */     super.initialise();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void registerCommands()
/*     */   {
/*  95 */     super.registerCommands();
/*     */     
/*  97 */     registerCommand(new Show());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void registerCommand(IConsoleCommand command)
/*     */   {
/*     */     Iterator iter;
/*     */     
/* 106 */     if (!"admin".equals(getUserProfile().getUserType()))
/*     */     {
/* 108 */       Set commandNames = command.getCommandNames();
/* 109 */       for (iter = commandNames.iterator(); iter.hasNext();) {
/* 110 */         String cmdName = (String)iter.next();
/* 111 */         if (this.adminCommands.contains(cmdName))
/* 112 */           return;
/* 113 */         if (!"user".equals(getUserProfile().getUserType()))
/*     */         {
/* 115 */           if (this.userCommands.contains(cmdName))
/* 116 */             return;
/*     */         }
/*     */       }
/*     */     }
/* 120 */     super.registerCommand(command);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/multiuser/MultiUserConsoleInput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */