/*    */ package org.gudy.azureus2.ui.console.multiuser.commands;
/*    */ 
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.download.DownloadManagerState;
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
/*    */ public class Show
/*    */   extends org.gudy.azureus2.ui.console.commands.Show
/*    */ {
/*    */   protected String getDefaultSummaryFormat()
/*    */   {
/* 45 */     return "[%o] " + super.getDefaultSummaryFormat();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   protected String expandVariable(char variable, DownloadManager dm)
/*    */   {
/* 52 */     switch (variable)
/*    */     {
/*    */     case 'o': 
/* 55 */       String user = dm.getDownloadState().getAttribute("user");
/* 56 */       if (user == null)
/* 57 */         user = "admin";
/* 58 */       return user;
/*    */     }
/* 60 */     return super.expandVariable(variable, dm);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/multiuser/commands/Show.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */