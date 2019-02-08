/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.peers;
/*    */ 
/*    */ import org.gudy.azureus2.core3.peer.PEPeer;
/*    */ import org.gudy.azureus2.core3.peer.PEPeerStats;
/*    */ import org.gudy.azureus2.core3.util.DisplayFormatters;
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
/*    */ public class UpRatioItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public UpRatioItem(String table_id)
/*    */   {
/* 38 */     super("UpRatio", 2, -1, 70, table_id);
/* 39 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 43 */     info.addCategories(new String[] { "sharing" });
/*    */   }
/*    */   
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 49 */     PEPeer peer = (PEPeer)cell.getDataSource();
/* 50 */     float value = 0.0F;
/* 51 */     long lDivisor = 0L;
/* 52 */     long lDivident = 0L;
/* 53 */     if (peer != null) {
/* 54 */       lDivisor = peer.getStats().getTotalBytesDownloadedByPeer() - peer.getStats().getTotalDataBytesSent();
/* 55 */       lDivident = peer.getStats().getTotalDataBytesSent();
/*    */       
/* 57 */       if (lDivisor > 1024L) {
/* 58 */         value = (float)lDivident / (float)lDivisor;
/* 59 */         if (value == 0.0F)
/* 60 */           value = -1.0F;
/* 61 */       } else if (lDivident > 0L) {
/* 62 */         value = Float.MAX_VALUE;
/*    */       }
/*    */     }
/* 65 */     if ((!cell.setSortValue((value * 1000.0D))) && (cell.isValid()))
/*    */       return;
/*    */     String s;
/*    */     String s;
/* 69 */     if (lDivisor <= 0L) {
/* 70 */       s = ""; } else { String s;
/* 71 */       if (value == Float.MAX_VALUE) {
/* 72 */         s = "∞:1"; } else { String s;
/* 73 */         if (value == -1.0F) {
/* 74 */           s = "1:∞";
/*    */         } else
/* 76 */           s = DisplayFormatters.formatDecimal(value, 2) + ":1";
/*    */       } }
/* 78 */     cell.setText(s);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/UpRatioItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */