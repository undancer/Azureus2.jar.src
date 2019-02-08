/*    */ package com.aelitis.azureus.ui.swt.views.skin;
/*    */ 
/*    */ import com.aelitis.azureus.core.AzureusCore;
/*    */ import com.aelitis.azureus.core.torrent.PlatformTorrentUtils;
/*    */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*    */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*    */ import org.eclipse.swt.widgets.Composite;
/*    */ import org.eclipse.swt.widgets.Text;
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.plugins.download.Download;
/*    */ import org.gudy.azureus2.plugins.download.DownloadTypeComplete;
/*    */ import org.gudy.azureus2.plugins.download.DownloadTypeIncomplete;
/*    */ import org.gudy.azureus2.ui.swt.Utils;
/*    */ import org.gudy.azureus2.ui.swt.views.MyTorrentsView;
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
/*    */ public class MyTorrentsView_Big
/*    */   extends MyTorrentsView
/*    */ {
/*    */   private final int torrentFilterMode;
/*    */   private int defaultRowHeight;
/*    */   
/*    */   public MyTorrentsView_Big(AzureusCore _azureus_core, int torrentFilterMode, TableColumnCore[] basicItems, Text txtFilter, Composite cCatsTags)
/*    */   {
/* 44 */     super(true);
/* 45 */     this.defaultRowHeight = Utils.adjustPXForDPI(40);
/* 46 */     this.torrentFilterMode = torrentFilterMode;
/* 47 */     this.txtFilter = txtFilter;
/* 48 */     this.cCategoriesAndTags = cCatsTags;
/*    */     Class<?> forDataSourceType;
/* 50 */     switch (torrentFilterMode) {
/*    */     case 1: 
/*    */     case 3: 
/* 53 */       forDataSourceType = DownloadTypeComplete.class;
/* 54 */       break;
/*    */     
/*    */     case 2: 
/* 57 */       forDataSourceType = DownloadTypeIncomplete.class;
/* 58 */       break;
/*    */     
/*    */     default: 
/* 61 */       forDataSourceType = Download.class;
/*    */     }
/*    */     
/* 64 */     init(_azureus_core, SB_Transfers.getTableIdFromFilterMode(torrentFilterMode, true), forDataSourceType, basicItems);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public boolean isOurDownloadManager(DownloadManager dm)
/*    */   {
/* 73 */     if (PlatformTorrentUtils.isAdvancedViewOnly(dm)) {
/* 74 */       return false;
/*    */     }
/*    */     
/* 77 */     if (this.torrentFilterMode == 3) {
/* 78 */       if (PlatformTorrentUtils.getHasBeenOpened(dm)) {
/* 79 */         return false;
/*    */       }
/* 81 */     } else if (this.torrentFilterMode == 0) {
/* 82 */       if (!isInCurrentTag(dm)) {
/* 83 */         return false;
/*    */       }
/* 85 */       return isInCurrentTag(dm);
/*    */     }
/*    */     
/* 88 */     return super.isOurDownloadManager(dm);
/*    */   }
/*    */   
/*    */   public void defaultSelected(TableRowCore[] rows, int stateMask)
/*    */   {
/* 93 */     boolean neverPlay = DownloadTypeIncomplete.class.equals(getForDataSourceType());
/* 94 */     SBC_LibraryTableView.doDefaultClick(rows, stateMask, neverPlay);
/*    */   }
/*    */   
/*    */   protected int getRowDefaultHeight() {
/* 98 */     return this.defaultRowHeight;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/MyTorrentsView_Big.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */