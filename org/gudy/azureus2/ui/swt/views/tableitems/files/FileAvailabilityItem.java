/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.files;
/*    */ 
/*    */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.peer.PEPeerManager;
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
/*    */ 
/*    */ 
/*    */ public class FileAvailabilityItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   private static final String zeros = "0000";
/* 47 */   private static final int numZeros = "0000".length();
/*    */   
/*    */   private int iTimesBy;
/*    */   
/*    */   public FileAvailabilityItem()
/*    */   {
/* 53 */     super("availability", 1, -1, 60, "Files");
/* 54 */     setRefreshInterval(-2);
/* 55 */     setMinWidthAuto(true);
/*    */     
/* 57 */     this.iTimesBy = 1;
/* 58 */     for (int i = 1; i < numZeros; i++)
/* 59 */       this.iTimesBy *= 10;
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 63 */     info.addCategories(new String[] { "swarm" });
/*    */     
/*    */ 
/* 66 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 70 */     String sText = "";
/* 71 */     DiskManagerFileInfo file = (DiskManagerFileInfo)cell.getDataSource();
/* 72 */     if (file == null) {
/* 73 */       return;
/*    */     }
/* 75 */     if (file.getLength() == 0L) {
/* 76 */       sText = "-";
/* 77 */       cell.setSortValue(Long.MAX_VALUE);
/*    */     } else {
/* 79 */       PEPeerManager pm = file.getDownloadManager().getPeerManager();
/* 80 */       if (pm != null) {
/* 81 */         float f = pm.getMinAvailability(file.getIndex());
/* 82 */         if ((!cell.setSortValue((f * 1000.0F))) && (cell.isValid())) {
/* 83 */           return;
/*    */         }
/* 85 */         sText = String.valueOf((int)(f * this.iTimesBy));
/* 86 */         if (numZeros - sText.length() > 0)
/* 87 */           sText = "0000".substring(0, numZeros - sText.length()) + sText;
/* 88 */         sText = sText.substring(0, sText.length() - numZeros + 1) + "." + sText.substring(sText.length() - numZeros + 1);
/*    */       }
/*    */       else
/*    */       {
/* 92 */         cell.setSortValue(0L);
/*    */       }
/*    */     }
/* 95 */     cell.setText(sText);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/files/FileAvailabilityItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */