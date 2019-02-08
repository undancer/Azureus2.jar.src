/*    */ package org.gudy.azureus2.pluginsimpl.local.ui.components;
/*    */ 
/*    */ import org.gudy.azureus2.plugins.ui.components.UITextField;
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
/*    */ public class UITextFieldImpl
/*    */   extends UIComponentImpl
/*    */   implements UITextField
/*    */ {
/*    */   public UITextFieldImpl()
/*    */   {
/* 37 */     setText("");
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setText(String text)
/*    */   {
/* 44 */     setProperty("value", text);
/*    */   }
/*    */   
/*    */ 
/*    */   public String getText()
/*    */   {
/* 50 */     return (String)getProperty("value");
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/components/UITextFieldImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */