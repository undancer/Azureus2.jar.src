/*    */ package org.gudy.azureus2.ui.swt.config;
/*    */ 
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
/*    */ public class DualChangeSelectionActionPerformer
/*    */   implements IAdditionalActionPerformer
/*    */ {
/*    */   ChangeSelectionActionPerformer enabler;
/*    */   ChangeSelectionActionPerformer disabler;
/*    */   
/*    */   public DualChangeSelectionActionPerformer(Control[] controlsToEnable, Control[] controlsToDisable)
/*    */   {
/* 38 */     this.enabler = new ChangeSelectionActionPerformer(controlsToEnable, false);
/* 39 */     this.disabler = new ChangeSelectionActionPerformer(controlsToDisable, true);
/*    */   }
/*    */   
/*    */   public void setIntValue(int value) {}
/*    */   
/*    */   public void setStringValue(String value) {}
/*    */   
/*    */   public void setSelected(boolean selected) {
/* 47 */     this.enabler.setSelected(selected);
/* 48 */     this.disabler.setSelected(selected);
/*    */   }
/*    */   
/*    */   public void performAction() {
/* 52 */     this.enabler.performAction();
/* 53 */     this.disabler.performAction();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/DualChangeSelectionActionPerformer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */