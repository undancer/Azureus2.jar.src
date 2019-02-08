/*    */ package com.aelitis.azureus.ui.swt.columns.tagdiscovery;
/*    */ 
/*    */ import com.aelitis.azureus.core.tag.TagDiscovery;
/*    */ import com.aelitis.azureus.ui.UIFunctions;
/*    */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*    */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*    */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*    */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnExtraInfoListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
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
/*    */ public class ColumnTagDiscoveryTorrent
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/* 32 */   public static String COLUMN_ID = "tag.discovery.torrent";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 35 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 38 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public ColumnTagDiscoveryTorrent(TableColumn column)
/*    */   {
/* 43 */     column.setWidth(400);
/* 44 */     column.addListeners(this);
/* 45 */     TableContextMenuItem menuShowTorrent = column.addContextMenuItem("ConfigView.option.dm.dblclick.details", 2);
/*    */     
/*    */ 
/*    */ 
/* 49 */     menuShowTorrent.addListener(new MenuItemListener() {
/*    */       public void selected(MenuItem menu, Object target) {
/* 51 */         if ((target instanceof TagDiscovery)) {
/* 52 */           UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 53 */           if (uiFunctions != null) {
/* 54 */             byte[] hash = ((TagDiscovery)target).getHash();
/* 55 */             uiFunctions.getMDI().showEntryByID("DMDetails", hash);
/*    */           }
/*    */         }
/*    */       }
/*    */     });
/*    */   }
/*    */   
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 65 */     TagDiscovery discovery = (TagDiscovery)cell.getDataSource();
/* 66 */     cell.setText(discovery.getTorrentName());
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/tagdiscovery/ColumnTagDiscoveryTorrent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */