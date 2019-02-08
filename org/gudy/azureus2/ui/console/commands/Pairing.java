/*    */ package org.gudy.azureus2.ui.console.commands;
/*    */ 
/*    */ import com.aelitis.azureus.core.pairing.PairingManager;
/*    */ import com.aelitis.azureus.core.pairing.PairingManagerFactory;
/*    */ import java.io.PrintStream;
/*    */ import java.util.List;
/*    */ import org.gudy.azureus2.core3.util.Debug;
/*    */ import org.gudy.azureus2.ui.console.ConsoleInput;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Pairing
/*    */   extends IConsoleCommand
/*    */ {
/*    */   public Pairing()
/*    */   {
/* 33 */     super("pairing", "pair");
/*    */   }
/*    */   
/*    */   public String getCommandDescriptions()
/*    */   {
/* 38 */     return "pairing\t\tpair\tShows and modified the current Vuze remote pairing state.";
/*    */   }
/*    */   
/*    */   public void printHelpExtra(PrintStream out, List args) {
/* 42 */     out.println("> -----");
/* 43 */     out.println("Subcommands:");
/* 44 */     out.println("enable\tEnable remote pairing");
/* 45 */     out.println("disable\tDisable remote pairing");
/* 46 */     out.println("> -----");
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void execute(String commandName, ConsoleInput ci, List<String> args)
/*    */   {
/* 53 */     PairingManager pm = PairingManagerFactory.getSingleton();
/*    */     
/* 55 */     if (args.size() > 0)
/*    */     {
/* 57 */       String sub = (String)args.get(0);
/*    */       
/* 59 */       if (sub.equals("enable"))
/*    */       {
/* 61 */         pm.setEnabled(true);
/*    */       }
/* 63 */       else if (sub.equals("disable"))
/*    */       {
/* 65 */         pm.setEnabled(false);
/*    */       }
/*    */       else
/*    */       {
/* 69 */         ci.out.println("Unsupported sub-command: " + sub);
/*    */         
/* 71 */         return;
/*    */       }
/*    */     }
/*    */     
/* 75 */     ci.out.println("Current pairing state:");
/*    */     
/* 77 */     if (pm.isEnabled())
/*    */     {
/* 79 */       ci.out.println("\tStatus:      " + pm.getStatus());
/*    */       try
/*    */       {
/* 82 */         ci.out.println("\tAccess code: " + pm.getAccessCode());
/*    */       }
/*    */       catch (Throwable e)
/*    */       {
/* 86 */         ci.out.println("Failed to get access code: " + Debug.getNestedExceptionMessage(e));
/*    */       }
/*    */     } else {
/* 89 */       ci.out.println("\tdisabled");
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/Pairing.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */