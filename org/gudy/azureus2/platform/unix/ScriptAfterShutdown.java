/*    */ package org.gudy.azureus2.platform.unix;
/*    */ 
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.PrintStream;
/*    */ import org.gudy.azureus2.core3.config.COConfigurationManager;
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
/*    */ public class ScriptAfterShutdown
/*    */ {
/*    */   private static PrintStream sysout;
/*    */   
/*    */   public static void main(String[] args)
/*    */   {
/* 31 */     System.setProperty("transitory.startup", "1");
/*    */     
/*    */ 
/*    */ 
/* 35 */     sysout = System.out;
/*    */     try {
/* 37 */       System.setOut(new PrintStream(new FileOutputStream("/dev/stderr")));
/*    */     }
/*    */     catch (FileNotFoundException e) {}
/*    */     
/* 41 */     String extraCmds = COConfigurationManager.getStringParameter("scriptaftershutdown", null);
/*    */     
/* 43 */     if (extraCmds != null) {
/* 44 */       boolean exit = COConfigurationManager.getBooleanParameter("scriptaftershutdown.exit", false);
/*    */       
/* 46 */       if (exit) {
/* 47 */         COConfigurationManager.removeParameter("scriptaftershutdown.exit");
/*    */       }
/* 49 */       COConfigurationManager.removeParameter("scriptaftershutdown");
/* 50 */       COConfigurationManager.save();
/* 51 */       sysout.println(extraCmds);
/* 52 */       if (exit) {
/* 53 */         sysout.println("exit");
/*    */       }
/*    */     } else {
/* 56 */       log("No shutdown tasks to do");
/*    */     }
/*    */   }
/*    */   
/*    */   public static void addExtraCommand(String s) {
/* 61 */     String extraCmds = COConfigurationManager.getStringParameter("scriptaftershutdown", null);
/*    */     
/* 63 */     if (extraCmds == null) {
/* 64 */       extraCmds = s + "\n";
/*    */     } else {
/* 66 */       extraCmds = extraCmds + s + "\n";
/*    */     }
/* 68 */     COConfigurationManager.setParameter("scriptaftershutdown", extraCmds);
/*    */   }
/*    */   
/*    */   public static void setRequiresExit(boolean requiresExit) {
/* 72 */     if (requiresExit) {
/* 73 */       COConfigurationManager.setParameter("scriptaftershutdown.exit", true);
/*    */     }
/*    */   }
/*    */   
/*    */   private static void log(String string) {
/* 78 */     sysout.println("echo \"" + string.replaceAll("\"", "\\\"") + "\"");
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/platform/unix/ScriptAfterShutdown.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */