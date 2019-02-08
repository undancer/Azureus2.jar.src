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
/*    */ 
/*    */ public class NetworksItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 42 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   
/*    */   public static final String COLUMN_ID = "networks";
/*    */   
/*    */   public NetworksItem(String sTableID)
/*    */   {
/* 48 */     super(DATASOURCE_TYPE, "networks", 1, 70, sTableID);
/* 49 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 53 */     info.addCategories(new String[] { "tracker", "swarm" });
/*    */     
/*    */ 
/*    */ 
/* 57 */     info.setProficiency((byte)2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 61 */     String networks = "";
/* 62 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 63 */     if (dm != null) {
/* 64 */       String[] nets = dm.getDownloadState().getNetworks();
/*    */       
/* 66 */       for (int i = 0; i < nets.length; i++)
/*    */       {
/* 68 */         networks = networks + (i == 0 ? "" : ",") + nets[i];
/*    */       }
/*    */     }
/* 71 */     cell.setText(networks);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/NetworksItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */