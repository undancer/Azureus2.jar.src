/*    */ package com.aelitis.azureus.ui.swt.columns.tag;
/*    */ 
/*    */ import com.aelitis.azureus.core.tag.Tag;
/*    */ import com.aelitis.azureus.core.tag.TagFeatureRateLimit;
/*    */ import com.aelitis.azureus.ui.swt.columns.ColumnCheckBox;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
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
/*    */ public class ColumnTagUploadPriority
/*    */   extends ColumnCheckBox
/*    */ {
/* 38 */   public static String COLUMN_ID = "tag.upload_priority";
/*    */   
/*    */ 
/*    */ 
/*    */   public ColumnTagUploadPriority(TableColumn column)
/*    */   {
/* 44 */     super(column);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 53 */     super.fillTableColumnInfo(info);
/*    */     
/* 55 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   protected Boolean getCheckBoxState(Object datasource)
/*    */   {
/* 63 */     Tag tag = (Tag)datasource;
/*    */     
/* 65 */     if ((tag instanceof TagFeatureRateLimit))
/*    */     {
/* 67 */       int pri = ((TagFeatureRateLimit)tag).getTagUploadPriority();
/*    */       
/* 69 */       if (pri >= 0)
/*    */       {
/* 71 */         return Boolean.valueOf(pri > 0);
/*    */       }
/*    */     }
/*    */     
/* 75 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected void setCheckBoxState(Object datasource, boolean set)
/*    */   {
/* 84 */     TagFeatureRateLimit tag = (TagFeatureRateLimit)datasource;
/*    */     
/* 86 */     tag.setTagUploadPriority(set ? 1 : 0);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/tag/ColumnTagUploadPriority.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */