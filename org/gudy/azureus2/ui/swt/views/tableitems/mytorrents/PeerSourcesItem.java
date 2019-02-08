/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*    */ import org.gudy.azureus2.plugins.download.Download;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PeerSourcesItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 41 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   
/*    */   public static final String COLUMN_ID = "peersources";
/*    */   
/*    */   public PeerSourcesItem(String sTableID)
/*    */   {
/* 47 */     super(DATASOURCE_TYPE, "peersources", 1, 70, sTableID);
/* 48 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 52 */     info.addCategories(new String[] { "swarm" });
/*    */     
/*    */ 
/* 55 */     info.setProficiency((byte)2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 59 */     String ps = "";
/* 60 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 61 */     if (dm != null) {
/* 62 */       String[] nets = dm.getDownloadState().getPeerSources();
/*    */       
/* 64 */       for (int i = 0; i < nets.length; i++)
/*    */       {
/* 66 */         ps = ps + (i == 0 ? "" : ",") + nets[i];
/*    */       }
/*    */     }
/* 69 */     cell.setText(ps);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/PeerSourcesItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */