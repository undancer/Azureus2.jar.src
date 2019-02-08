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
/*    */ 
/*    */ public class UpDownRatioItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public UpDownRatioItem(String table_id)
/*    */   {
/* 39 */     super("UpDownRatio", 2, -1, 70, table_id);
/* 40 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 44 */     info.addCategories(new String[] { "sharing" });
/*    */   }
/*    */   
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 50 */     PEPeer peer = (PEPeer)cell.getDataSource();
/* 51 */     float value = 0.0F;
/* 52 */     long lDivisor = 0L;
/* 53 */     long lDivident = 0L;
/* 54 */     if (peer != null) {
/* 55 */       lDivisor = peer.getStats().getTotalDataBytesReceived() - peer.getStats().getTotalBytesDiscarded();
/* 56 */       lDivident = peer.getStats().getTotalDataBytesSent();
/*    */       
/* 58 */       if (lDivisor > 1024L) {
/* 59 */         value = (float)lDivident / (float)lDivisor;
/* 60 */         if (value == 0.0F)
/* 61 */           value = -1.0F;
/* 62 */       } else if (lDivident > 0L) {
/* 63 */         value = Float.MAX_VALUE;
/*    */       }
/*    */     }
/* 66 */     if ((!cell.setSortValue((value * 1000.0D))) && (cell.isValid()))
/*    */       return;
/*    */     String s;
/*    */     String s;
/* 70 */     if (lDivisor <= 0L) {
/* 71 */       s = ""; } else { String s;
/* 72 */       if (value == Float.MAX_VALUE) {
/* 73 */         s = "∞:1"; } else { String s;
/* 74 */         if (value == -1.0F) {
/* 75 */           s = "1:∞";
/*    */         } else
/* 77 */           s = DisplayFormatters.formatDecimal(value, 2) + ":1";
/*    */       } }
/* 79 */     cell.setText(s);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/UpDownRatioItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */