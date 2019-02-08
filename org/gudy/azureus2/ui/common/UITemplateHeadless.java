/*    */ package org.gudy.azureus2.ui.common;
/*    */ 
/*    */ import org.gudy.azureus2.ui.common.util.LGLogger2Log4j;
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
/*    */ public abstract class UITemplateHeadless
/*    */   extends UITemplate
/*    */   implements IUserInterface
/*    */ {
/*    */   public void init(boolean first, boolean others)
/*    */   {
/* 38 */     super.init(first, others);
/* 39 */     if (first) {
/* 40 */       LGLogger2Log4j.set();
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/common/UITemplateHeadless.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */