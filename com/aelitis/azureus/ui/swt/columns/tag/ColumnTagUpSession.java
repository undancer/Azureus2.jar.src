/*    */ package com.aelitis.azureus.ui.swt.columns.tag;
/*    */ 
/*    */ import com.aelitis.azureus.core.tag.Tag;
/*    */ import com.aelitis.azureus.core.tag.TagFeatureRateLimit;
/*    */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnExtraInfoListener;
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
/*    */ public class ColumnTagUpSession
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/* 29 */   public static String COLUMN_ID = "tag.upsession";
/*    */   
/*    */ 
/*    */ 
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 35 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/*    */ 
/* 39 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public ColumnTagUpSession(TableColumn column)
/*    */   {
/* 46 */     column.setWidth(60);
/* 47 */     column.setRefreshInterval(-2);
/* 48 */     column.setAlignment(2);
/* 49 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 53 */     Tag tag = (Tag)cell.getDataSource();
/* 54 */     if ((tag instanceof TagFeatureRateLimit)) {
/* 55 */       TagFeatureRateLimit rl = (TagFeatureRateLimit)tag;
/*    */       
/* 57 */       if (rl.supportsTagRates())
/*    */       {
/* 59 */         long[] up = rl.getTagSessionUploadTotal();
/*    */         
/* 61 */         if (up != null)
/*    */         {
/* 63 */           long tot = 0L;
/*    */           
/* 65 */           for (long l : up)
/*    */           {
/* 67 */             tot += l;
/*    */           }
/*    */           
/* 70 */           if ((!cell.setSortValue(tot)) && (cell.isValid())) {
/* 71 */             return;
/*    */           }
/*    */           
/* 74 */           if (!cell.isShown()) {
/* 75 */             return;
/*    */           }
/*    */           
/* 78 */           cell.setText(DisplayFormatters.formatByteCountToKiBEtc(tot));
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/tag/ColumnTagUpSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */