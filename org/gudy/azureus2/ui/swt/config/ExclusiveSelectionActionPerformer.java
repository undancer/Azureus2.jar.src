/*    */ package org.gudy.azureus2.ui.swt.config;
/*    */ 
/*    */ import org.eclipse.swt.widgets.Button;
/*    */ import org.eclipse.swt.widgets.Event;
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
/*    */ public class ExclusiveSelectionActionPerformer
/*    */   implements IAdditionalActionPerformer
/*    */ {
/* 35 */   boolean selected = false;
/*    */   Button[] buttons;
/*    */   
/*    */   public ExclusiveSelectionActionPerformer(Button[] buttons)
/*    */   {
/* 40 */     this.buttons = buttons;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void performAction()
/*    */   {
/* 47 */     if (this.buttons == null)
/* 48 */       return;
/* 49 */     if (!this.selected)
/* 50 */       return;
/* 51 */     for (int i = 0; i < this.buttons.length; i++) {
/* 52 */       this.buttons[i].setSelection(false);
/* 53 */       this.buttons[i].notifyListeners(13, new Event());
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void setIntValue(int value) {}
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void setSelected(boolean selected)
/*    */   {
/* 67 */     this.selected = selected;
/*    */   }
/*    */   
/*    */   public void setStringValue(String value) {}
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/ExclusiveSelectionActionPerformer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */