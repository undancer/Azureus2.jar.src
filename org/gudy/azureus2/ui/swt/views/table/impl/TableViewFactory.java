/*    */ package org.gudy.azureus2.ui.swt.views.table.impl;
/*    */ 
/*    */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*    */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*    */ import org.gudy.azureus2.ui.swt.views.table.painted.TableViewPainted;
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
/*    */ public class TableViewFactory
/*    */ {
/*    */   public static <V> TableViewSWT<V> createTableViewSWT(Class<?> pluginDataSourceType, String _sTableID, String _sPropertiesPrefix, TableColumnCore[] _basicItems, String _sDefaultSortOn, int _iTableStyle)
/*    */   {
/* 32 */     return new TableViewPainted(pluginDataSourceType, _sTableID, _sPropertiesPrefix, _basicItems, _sDefaultSortOn, _iTableStyle);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/table/impl/TableViewFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */