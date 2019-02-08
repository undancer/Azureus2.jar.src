/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.myshares;
/*    */ 
/*    */ import org.gudy.azureus2.plugins.sharing.ShareResource;
/*    */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.pluginsimpl.local.torrent.TorrentManagerImpl;
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
/*    */ public class CategoryItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 42 */   protected static final TorrentAttribute category_attribute = TorrentManagerImpl.getSingleton().getAttribute("Category");
/*    */   
/*    */ 
/*    */ 
/*    */   public CategoryItem()
/*    */   {
/* 48 */     super("category", -2, 400, "MyShares");
/*    */     
/* 50 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 56 */     ShareResource item = (ShareResource)cell.getDataSource();
/*    */     
/* 58 */     if (item == null)
/*    */     {
/* 60 */       cell.setText("");
/*    */     }
/*    */     else
/*    */     {
/* 64 */       String value = item.getAttribute(category_attribute);
/*    */       
/* 66 */       cell.setText(value == null ? "" : value);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/myshares/CategoryItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */