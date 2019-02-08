/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.peers;
/*    */ 
/*    */ import org.gudy.azureus2.core3.peer.PEPeer;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class IpItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener, ObfusticateCellText
/*    */ {
/*    */   public static final String COLUMN_ID = "ip";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 45 */     info.addCategories(new String[] { "identification", "connection" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public IpItem(String table_id)
/*    */   {
/* 53 */     super("ip", -2, 100, table_id);
/* 54 */     setObfustication(true);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 58 */     PEPeer peer = (PEPeer)cell.getDataSource();
/* 59 */     String sText = peer == null ? "" : peer.getIp();
/*    */     
/* 61 */     if ((cell.setText(sText)) || (!cell.isValid())) {
/* 62 */       String[] sBlocks = sText.split("\\.");
/* 63 */       if (sBlocks.length == 4)
/*    */         try {
/* 65 */           long l = (Long.parseLong(sBlocks[0]) << 24) + (Long.parseLong(sBlocks[1]) << 16) + (Long.parseLong(sBlocks[2]) << 8) + Long.parseLong(sBlocks[3]);
/*    */           
/*    */ 
/*    */ 
/* 69 */           cell.setSortValue(l);
/* 70 */         } catch (Exception e) { e.printStackTrace();
/*    */         }
/*    */     }
/*    */   }
/*    */   
/*    */   public String getObfusticatedText(TableCell cell) {
/* 76 */     return cell.getText().substring(0, 3);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/IpItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */