/*    */ package org.gudy.azureus2.ui.swt;
/*    */ 
/*    */ import org.eclipse.swt.widgets.Display;
/*    */ import org.gudy.azureus2.core3.category.Category;
/*    */ import org.gudy.azureus2.core3.category.CategoryManager;
/*    */ import org.gudy.azureus2.ui.swt.views.utils.TagUIUtils;
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
/*    */ public class CategoryAdderWindow
/*    */ {
/*    */   private Category newCategory;
/*    */   
/*    */   public CategoryAdderWindow(Display displayNotUsed)
/*    */   {
/* 35 */     SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow("CategoryAddWindow.title", "CategoryAddWindow.message");
/*    */     
/* 37 */     entryWindow.prompt();
/* 38 */     if (entryWindow.hasSubmittedInput())
/*    */     {
/* 40 */       TagUIUtils.checkTagSharing(false);
/*    */       
/* 42 */       this.newCategory = CategoryManager.createCategory(entryWindow.getSubmittedInput());
/*    */     }
/*    */   }
/*    */   
/*    */   public Category getNewCategory() {
/* 47 */     return this.newCategory;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/CategoryAdderWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */