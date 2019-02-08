/*    */ package org.gudy.azureus2.pluginsimpl.local.ui.components;
/*    */ 
/*    */ import org.gudy.azureus2.plugins.ui.components.UIProgressBar;
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
/*    */ public class UIProgressBarImpl
/*    */   extends UIComponentImpl
/*    */   implements UIProgressBar
/*    */ {
/*    */   public UIProgressBarImpl()
/*    */   {
/* 37 */     setPercentageComplete(0);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setPercentageComplete(int percentage)
/*    */   {
/* 44 */     setProperty("value", new Integer(percentage));
/*    */   }
/*    */   
/*    */ 
/*    */   public int getPercentageComplete()
/*    */   {
/* 50 */     return ((Integer)getProperty("value")).intValue();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/components/UIProgressBarImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */