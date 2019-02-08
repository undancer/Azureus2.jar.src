/*    */ package org.gudy.azureus2.ui.swt.pluginsimpl;
/*    */ 
/*    */ import org.gudy.azureus2.pluginsimpl.local.ui.AbstractUIInputReceiver;
/*    */ import org.gudy.azureus2.ui.swt.plugins.UISWTInputReceiver;
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
/*    */ public abstract class AbstractUISWTInputReceiver
/*    */   extends AbstractUIInputReceiver
/*    */   implements UISWTInputReceiver
/*    */ {
/* 30 */   protected boolean select_preentered_text = true;
/* 31 */   protected int[] select_preentered_text_range = null;
/*    */   
/*    */   public void selectPreenteredText(boolean select) {
/* 34 */     assertPrePrompt();
/* 35 */     this.select_preentered_text = select;
/*    */   }
/*    */   
/*    */   public void selectPreenteredTextRange(int[] range) {
/* 39 */     assertPrePrompt();
/* 40 */     this.select_preentered_text_range = range;
/*    */   }
/*    */   
/* 43 */   protected int line_height = -1;
/*    */   
/* 45 */   public void setLineHeight(int line_height) { assertPrePrompt();
/* 46 */     this.line_height = line_height;
/*    */   }
/*    */   
/* 49 */   protected int width_hint = -1;
/*    */   
/* 51 */   public void setWidthHint(int width) { assertPrePrompt();
/* 52 */     this.width_hint = width;
/*    */   }
/*    */   
/* 55 */   protected String[] choices = null;
/* 56 */   protected boolean choices_allow_edit = true;
/* 57 */   protected int choices_default = -1;
/*    */   
/*    */   public void setPreenteredText(String text, boolean as_suggested) {
/* 60 */     if (!this.choices_allow_edit) {
/* 61 */       throw new RuntimeException("cannot set pre-entered text if you have chosen to use non editable selected items");
/*    */     }
/* 63 */     super.setPreenteredText(text, as_suggested);
/*    */   }
/*    */   
/*    */   public void setSelectableItems(String[] choices, int default_choice, boolean allow_edit) {
/* 67 */     assertPrePrompt();
/* 68 */     if ((!allow_edit) && (this.preentered_text != null)) {
/* 69 */       throw new RuntimeException("cannot set allow_edit to false if you have already set pre-entered text");
/*    */     }
/*    */     
/*    */ 
/* 73 */     if (choices.length == 0) { return;
/*    */     }
/* 75 */     this.choices = choices;
/* 76 */     this.choices_allow_edit = allow_edit;
/* 77 */     this.choices_default = default_choice;
/*    */     
/* 79 */     if ((default_choice >= 0) && (default_choice < choices.length)) {
/* 80 */       this.preentered_text = choices[default_choice];
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/pluginsimpl/AbstractUISWTInputReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */