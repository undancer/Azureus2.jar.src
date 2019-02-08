/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import java.io.File;
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.util.Debug;
/*    */ import org.gudy.azureus2.plugins.download.Download;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*    */ import org.gudy.azureus2.ui.swt.debug.ObfusticateCellText;
/*    */ import org.gudy.azureus2.ui.swt.views.table.CoreTableColumnSWT;
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
/*    */ public class SavePathItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener, ObfusticateCellText
/*    */ {
/* 40 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   
/*    */   public static final String COLUMN_ID = "savepath";
/*    */   
/*    */   public SavePathItem(String sTableID)
/*    */   {
/* 46 */     super(DATASOURCE_TYPE, "savepath", 1, 150, sTableID);
/* 47 */     setObfustication(true);
/* 48 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 52 */     info.addCategories(new String[] { "content" });
/*    */     
/*    */ 
/* 55 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 59 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/*    */     
/* 61 */     cell.setText(dm == null ? "" : dm.getSaveLocation().toString());
/*    */   }
/*    */   
/*    */   public String getObfusticatedText(TableCell cell) {
/* 65 */     return Debug.secretFileName(cell.getText());
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/SavePathItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */