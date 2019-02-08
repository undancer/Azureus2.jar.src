/*    */ package org.gudy.azureus2.ui.swt.shells;
/*    */ 
/*    */ import org.gudy.azureus2.core3.internat.MessageText;
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
/*    */ public class BrowserShell
/*    */ {
/*    */   public BrowserShell(String title_resource, String url, int width, int height)
/*    */   {
/* 35 */     MessageBoxShell boxShell = new MessageBoxShell(MessageText.getString(title_resource), "", new String[0], 0);
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 44 */     boxShell.setUrl(url);
/*    */     
/* 46 */     boxShell.setBrowserFollowLinks(true);
/*    */     
/* 48 */     boxShell.setSquish(true);
/*    */     
/* 50 */     boxShell.setSize(width, height);
/*    */     
/* 52 */     boxShell.open(null);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/shells/BrowserShell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */