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
/*    */ public class ColumnTagPublic
/*    */   extends ColumnCheckBox
/*    */ {
/* 36 */   public static String COLUMN_ID = "tag.public";
/*    */   
/*    */ 
/*    */ 
/*    */   public ColumnTagPublic(TableColumn column)
/*    */   {
/* 42 */     super(column);
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
/* 54 */       if (tag.canBePublic())
/*    */       {
/* 56 */         return Boolean.valueOf(tag.isPublic());
/*    */       }
/*    */     }
/*    */     
/* 60 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected void setCheckBoxState(Object datasource, boolean set)
/*    */   {
/* 69 */     Tag tag = (Tag)datasource;
/*    */     
/* 71 */     if (tag != null)
/*    */     {
/* 73 */       if (tag.canBePublic())
/*    */       {
/* 75 */         tag.setPublic(set);
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/tag/ColumnTagPublic.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */