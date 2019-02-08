/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.files;
/*    */ 
/*    */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*    */ import org.gudy.azureus2.ui.swt.views.MyTorrentsView;
/*    */ import org.gudy.azureus2.ui.swt.views.ViewUtils;
/*    */ import org.gudy.azureus2.ui.swt.views.ViewUtils.CustomDateFormat;
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
/*    */ public class FileETAItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   private ViewUtils.CustomDateFormat cdf;
/*    */   
/*    */   public FileETAItem()
/*    */   {
/* 37 */     super("file_eta", 2, -1, 60, "Files");
/*    */     
/* 39 */     setRefreshInterval(-2);
/*    */     
/* 41 */     this.cdf = ViewUtils.addCustomDateFormat(this);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 48 */     info.addCategories(new String[] { "progress" });
/*    */     
/*    */ 
/*    */ 
/* 52 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 59 */     DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)cell.getDataSource();
/*    */     
/* 61 */     long eta = -1L;
/*    */     
/* 63 */     if (fileInfo != null)
/*    */     {
/* 65 */       eta = fileInfo.getETA();
/*    */     }
/*    */     
/* 68 */     if ((!cell.setSortValue(eta)) && (cell.isValid()))
/*    */     {
/* 70 */       return;
/*    */     }
/*    */     
/* 73 */     cell.setText(ViewUtils.formatETA(eta, MyTorrentsView.eta_absolute, this.cdf.getDateFormat()));
/*    */   }
/*    */   
/*    */ 
/*    */   public void postConfigLoad()
/*    */   {
/* 79 */     super.postConfigLoad();
/*    */     
/* 81 */     this.cdf.update();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/files/FileETAItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */