/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytracker;
/*    */ 
/*    */ import com.aelitis.azureus.core.AzureusCore;
/*    */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*    */ import org.gudy.azureus2.core3.category.Category;
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*    */ import org.gudy.azureus2.core3.global.GlobalManager;
/*    */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*    */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrent;
/*    */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
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
/*    */ public class CategoryItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   protected static GlobalManager gm;
/*    */   
/*    */   public CategoryItem()
/*    */   {
/* 53 */     super("category", -1, 400, "MyTracker");
/*    */     
/* 55 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 61 */     TRHostTorrent tr_torrent = (TRHostTorrent)cell.getDataSource();
/*    */     
/* 63 */     if (tr_torrent == null)
/*    */     {
/* 65 */       cell.setText("");
/*    */     }
/*    */     else
/*    */     {
/* 69 */       TOTorrent torrent = tr_torrent.getTorrent();
/*    */       
/* 71 */       if (gm == null) {
/* 72 */         if (AzureusCoreFactory.isCoreRunning()) {
/* 73 */           return;
/*    */         }
/* 75 */         gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/*    */       }
/*    */       
/* 78 */       DownloadManager dm = gm.getDownloadManager(torrent);
/*    */       
/* 80 */       String cat_str = null;
/*    */       
/* 82 */       if (dm != null)
/*    */       {
/* 84 */         Category cat = dm.getDownloadState().getCategory();
/*    */         
/* 86 */         if (cat != null)
/*    */         {
/* 88 */           cat_str = cat.getName();
/*    */         }
/*    */         
/*    */       }
/*    */       else
/*    */       {
/* 94 */         cat_str = TorrentUtils.getPluginStringProperty(torrent, "azcoreplugins.category");
/*    */       }
/*    */       
/* 97 */       cell.setText(cat_str == null ? "" : cat_str);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytracker/CategoryItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */