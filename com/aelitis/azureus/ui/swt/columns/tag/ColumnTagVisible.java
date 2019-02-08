/*    */ package com.aelitis.azureus.ui.swt.columns.tag;
/*    */ 
/*    */ import com.aelitis.azureus.core.tag.Tag;
/*    */ import com.aelitis.azureus.ui.swt.columns.ColumnCheckBox;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
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
/*    */ public class ColumnTagVisible
/*    */   extends ColumnCheckBox
/*    */ {
/* 36 */   public static String COLUMN_ID = "tag.visible";
/*    */   
/*    */ 
/*    */ 
/*    */   public ColumnTagVisible(TableColumn column)
/*    */   {
/* 42 */     super(column, 60);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   protected Boolean getCheckBoxState(Object datasource)
/*    */   {
/* 50 */     Tag tag = (Tag)datasource;
/*    */     
/* 52 */     if (tag != null)
/*    */     {
/* 54 */       return Boolean.valueOf(tag.isVisible());
/*    */     }
/*    */     
/* 57 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected void setCheckBoxState(Object datasource, boolean set)
/*    */   {
/* 66 */     Tag tag = (Tag)datasource;
/*    */     
/* 68 */     if (tag != null)
/*    */     {
/* 70 */       tag.setVisible(set);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/tag/ColumnTagVisible.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */