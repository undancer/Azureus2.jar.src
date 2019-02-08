/*    */ package com.aelitis.azureus.ui.swt.views.skin;
/*    */ 
/*    */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*    */ import com.aelitis.azureus.ui.swt.columns.utils.TableColumnCreatorV3;
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
/*    */ public class SBC_LibraryTableView_Big
/*    */   extends SBC_LibraryTableView
/*    */ {
/*    */   public String getUpdateUIName()
/*    */   {
/* 31 */     return "SBC_LibraryTableView_Big";
/*    */   }
/*    */   
/*    */   public int getTableMode() {
/* 35 */     return 0;
/*    */   }
/*    */   
/*    */   public boolean useBigTable() {
/* 39 */     return true;
/*    */   }
/*    */   
/*    */   public TableColumnCore[] getColumns() {
/* 43 */     TableColumnCore[] columns = null;
/* 44 */     if (this.torrentFilterMode == 1) {
/* 45 */       columns = TableColumnCreatorV3.createCompleteDM("MySeeders.big", true);
/*    */ 
/*    */     }
/* 48 */     else if (this.torrentFilterMode == 2) {
/* 49 */       columns = TableColumnCreatorV3.createIncompleteDM("MyTorrents.big", true);
/*    */ 
/*    */     }
/* 52 */     else if (this.torrentFilterMode == 3) {
/* 53 */       columns = TableColumnCreatorV3.createUnopenedDM("Unopened.big", true);
/*    */ 
/*    */     }
/* 56 */     else if (this.torrentFilterMode == 0) {
/* 57 */       columns = TableColumnCreatorV3.createAllDM("MyLibrary.big", true);
/*    */     }
/*    */     
/* 60 */     if (columns == null) {
/* 61 */       return null;
/*    */     }
/* 63 */     return columns;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/SBC_LibraryTableView_Big.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */