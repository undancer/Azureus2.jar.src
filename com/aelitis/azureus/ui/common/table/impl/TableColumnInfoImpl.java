/*    */ package com.aelitis.azureus.ui.common.table.impl;
/*    */ 
/*    */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
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
/*    */ public class TableColumnInfoImpl
/*    */   implements TableColumnInfo
/*    */ {
/*    */   String[] categories;
/* 37 */   byte proficiency = 1;
/*    */   
/*    */ 
/*    */   private final TableColumnCore column;
/*    */   
/*    */ 
/*    */   public TableColumnInfoImpl(TableColumnCore column)
/*    */   {
/* 45 */     this.column = column;
/*    */   }
/*    */   
/*    */   public TableColumnCore getColumn() {
/* 49 */     return this.column;
/*    */   }
/*    */   
/*    */   public String[] getCategories()
/*    */   {
/* 54 */     return this.categories;
/*    */   }
/*    */   
/*    */   public void addCategories(String[] categories)
/*    */   {
/* 59 */     if ((categories == null) || (categories.length == 0))
/*    */       return;
/*    */     int pos;
/*    */     String[] newCategories;
/*    */     int pos;
/* 64 */     if (this.categories == null) {
/* 65 */       String[] newCategories = new String[categories.length];
/* 66 */       pos = 0;
/*    */     } else {
/* 68 */       newCategories = new String[categories.length + this.categories.length];
/* 69 */       pos = this.categories.length;
/* 70 */       System.arraycopy(this.categories, 0, newCategories, 0, pos);
/*    */     }
/* 72 */     System.arraycopy(categories, pos, newCategories, 0, categories.length);
/* 73 */     this.categories = newCategories;
/*    */   }
/*    */   
/*    */   public byte getProficiency()
/*    */   {
/* 78 */     return this.proficiency;
/*    */   }
/*    */   
/*    */   public void setProficiency(byte proficiency)
/*    */   {
/* 83 */     this.proficiency = proficiency;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/common/table/impl/TableColumnInfoImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */