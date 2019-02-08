/*    */ package com.aelitis.azureus.ui.swt.columns.tag;
/*    */ 
/*    */ import com.aelitis.azureus.core.tag.Tag;
/*    */ import com.aelitis.azureus.core.tag.TagFeatureRSSFeed;
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
/*    */ public class ColumnTagRSSFeed
/*    */   extends ColumnCheckBox
/*    */ {
/* 35 */   public static String COLUMN_ID = "tag.rssfeed";
/*    */   
/*    */ 
/*    */ 
/*    */   public ColumnTagRSSFeed(TableColumn column)
/*    */   {
/* 41 */     super(column);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   protected Boolean getCheckBoxState(Object datasource)
/*    */   {
/* 49 */     Tag tag = (Tag)datasource;
/*    */     
/* 51 */     if ((tag instanceof TagFeatureRSSFeed))
/*    */     {
/* 53 */       return Boolean.valueOf(((TagFeatureRSSFeed)tag).isTagRSSFeedEnabled());
/*    */     }
/*    */     
/* 56 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected void setCheckBoxState(Object datasource, boolean set)
/*    */   {
/* 65 */     Tag tag = (Tag)datasource;
/*    */     
/* 67 */     if ((tag instanceof TagFeatureRSSFeed))
/*    */     {
/* 69 */       ((TagFeatureRSSFeed)tag).setTagRSSFeedEnabled(set);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/tag/ColumnTagRSSFeed.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */