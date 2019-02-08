/*    */ package org.gudy.azureus2.core3.category;
/*    */ 
/*    */ import org.gudy.azureus2.core3.category.impl.CategoryManagerImpl;
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
/*    */ 
/*    */ public class CategoryManager
/*    */ {
/*    */   public static void addCategoryManagerListener(CategoryManagerListener l)
/*    */   {
/* 37 */     CategoryManagerImpl.getInstance().addCategoryManagerListener(l);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static void removeCategoryManagerListener(CategoryManagerListener l)
/*    */   {
/* 45 */     CategoryManagerImpl.getInstance().removeCategoryManagerListener(l);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static Category createCategory(String name)
/*    */   {
/* 53 */     return CategoryManagerImpl.getInstance().createCategory(name);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static void removeCategory(Category category)
/*    */   {
/* 60 */     CategoryManagerImpl.getInstance().removeCategory(category);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static Category[] getCategories()
/*    */   {
/* 72 */     return CategoryManagerImpl.getInstance().getCategories();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static Category getCategory(String name)
/*    */   {
/* 80 */     return CategoryManagerImpl.getInstance().getCategory(name);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static Category getCategory(int type)
/*    */   {
/* 90 */     return CategoryManagerImpl.getInstance().getCategory(type);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/category/CategoryManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */