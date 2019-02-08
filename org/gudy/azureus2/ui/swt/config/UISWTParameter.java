/*    */ package org.gudy.azureus2.ui.swt.config;
/*    */ 
/*    */ import org.eclipse.swt.widgets.Control;
/*    */ import org.gudy.azureus2.ui.swt.Utils;
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
/*    */ public class UISWTParameter
/*    */   extends Parameter
/*    */ {
/*    */   private Control control;
/*    */   
/*    */   public UISWTParameter(Control control, String name)
/*    */   {
/* 34 */     super(name);
/* 35 */     this.control = control;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void setValue(Object value) {}
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public Control getControl()
/*    */   {
/* 50 */     return this.control;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setLayoutData(Object layoutData)
/*    */   {
/* 57 */     Utils.adjustPXForDPI(layoutData);
/* 58 */     this.control.setLayoutData(layoutData);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/UISWTParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */