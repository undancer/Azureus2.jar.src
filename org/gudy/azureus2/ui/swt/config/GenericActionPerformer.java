/*    */ package org.gudy.azureus2.ui.swt.config;
/*    */ 
/*    */ import org.eclipse.swt.widgets.Composite;
/*    */ import org.eclipse.swt.widgets.Control;
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
/*    */ public abstract class GenericActionPerformer
/*    */   implements IAdditionalActionPerformer
/*    */ {
/*    */   protected Control[] controls;
/*    */   
/*    */   public GenericActionPerformer(Control[] controls)
/*    */   {
/* 40 */     this.controls = controls;
/*    */   }
/*    */   
/*    */ 
/*    */   public void setIntValue(int value) {}
/*    */   
/*    */ 
/*    */   public void setSelected(boolean selected) {}
/*    */   
/*    */   public void setStringValue(String value) {}
/*    */   
/*    */   public void controlsSetEnabled(Control[] controls, boolean bEnabled)
/*    */   {
/* 53 */     for (int i = 0; i < controls.length; i++) {
/* 54 */       if ((controls[i] instanceof Composite))
/* 55 */         controlsSetEnabled(((Composite)controls[i]).getChildren(), bEnabled);
/* 56 */       controls[i].setEnabled(bEnabled);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/GenericActionPerformer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */