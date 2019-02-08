/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.peer.PEPeerManager;
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
/*    */ 
/*    */ 
/*    */ public class AvailabilityItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 44 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   
/*    */ 
/*    */   private static final String zeros = "0000";
/*    */   
/* 49 */   private static final int numZeros = "0000".length();
/*    */   
/*    */   public static final String COLUMN_ID = "availability";
/*    */   private int iTimesBy;
/*    */   
/*    */   public AvailabilityItem(String sTableID)
/*    */   {
/* 56 */     super(DATASOURCE_TYPE, "availability", 2, 50, sTableID);
/* 57 */     setRefreshInterval(-2);
/* 58 */     setMinWidthAuto(true);
/*    */     
/* 60 */     this.iTimesBy = 1;
/* 61 */     for (int i = 1; i < numZeros; i++)
/* 62 */       this.iTimesBy *= 10;
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 66 */     info.addCategories(new String[] { "swarm" });
/*    */     
/*    */ 
/* 69 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 73 */     String sText = "";
/* 74 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 75 */     if (dm == null) {
/* 76 */       return;
/*    */     }
/* 78 */     PEPeerManager pm = dm.getPeerManager();
/* 79 */     if (pm != null) {
/* 80 */       float f = pm.getMinAvailability();
/* 81 */       if ((!cell.setSortValue((f * 1000.0F))) && (cell.isValid())) {
/* 82 */         return;
/*    */       }
/* 84 */       sText = String.valueOf((int)(f * this.iTimesBy));
/* 85 */       if (numZeros - sText.length() > 0)
/* 86 */         sText = "0000".substring(0, numZeros - sText.length()) + sText;
/* 87 */       sText = sText.substring(0, sText.length() - numZeros + 1) + "." + sText.substring(sText.length() - numZeros + 1);
/*    */     }
/*    */     else
/*    */     {
/* 91 */       cell.setSortValue(0L);
/*    */     }
/* 93 */     cell.setText(sText);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/AvailabilityItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */