/*    */ package org.gudy.azureus2.ui.common;
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
/*    */ public abstract class UITemplate
/*    */   implements IUserInterface
/*    */ {
/* 31 */   private boolean started = false;
/*    */   
/*    */ 
/*    */   public void init(boolean first, boolean others) {}
/*    */   
/*    */ 
/*    */   public abstract void openTorrent(String paramString);
/*    */   
/*    */ 
/*    */   public abstract String[] processArgs(String[] paramArrayOfString);
/*    */   
/*    */   public void startUI()
/*    */   {
/* 44 */     this.started = true;
/*    */   }
/*    */   
/*    */   public boolean isStarted() {
/* 48 */     return this.started;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/common/UITemplate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */