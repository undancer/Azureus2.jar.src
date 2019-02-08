/*    */ package org.gudy.azureus2.ui.swt.views.table;
/*    */ 
/*    */ import com.aelitis.azureus.ui.common.table.impl.CoreTableColumn;
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
/*    */ public abstract class CoreTableColumnSWT
/*    */   extends CoreTableColumn
/*    */ {
/*    */   public CoreTableColumnSWT(String sName, int iAlignment, int iPosition, int iWidth, String sTableID)
/*    */   {
/* 37 */     super(sName, iAlignment, iPosition, iWidth, sTableID);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public CoreTableColumnSWT(Class forDataSourceType, String sName, int iAlignment, int iWidth, String sTableID)
/*    */   {
/* 48 */     super(forDataSourceType, sName, iAlignment, iWidth, sTableID);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public CoreTableColumnSWT(String sName, int iPosition, int iWidth, String sTableID)
/*    */   {
/* 58 */     super(sName, iPosition, iWidth, sTableID);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public CoreTableColumnSWT(String sName, int iWidth, String sTableID)
/*    */   {
/* 67 */     super(sName, iWidth, sTableID);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public CoreTableColumnSWT(String sName, String sTableID)
/*    */   {
/* 75 */     super(sName, sTableID);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void addListeners(Object listenerObject)
/*    */   {
/* 82 */     if ((listenerObject instanceof TableCellSWTPaintListener))
/*    */     {
/* 84 */       super.addCellOtherListener("SWTPaint", listenerObject);
/*    */     }
/*    */     
/* 87 */     super.addListeners(listenerObject);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/table/CoreTableColumnSWT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */