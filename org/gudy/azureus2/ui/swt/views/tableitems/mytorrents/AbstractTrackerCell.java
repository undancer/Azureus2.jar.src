/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*     */ 
/*     */ import java.net.URL;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerTrackerListener;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerResponse;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellDisposeListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellToolTipListener;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class AbstractTrackerCell
/*     */   implements TableCellRefreshListener, TableCellToolTipListener, TableCellDisposeListener, DownloadManagerTrackerListener
/*     */ {
/*     */   TableCell cell;
/*     */   private DownloadManager dm;
/*     */   
/*     */   public AbstractTrackerCell(TableCell cell)
/*     */   {
/*  45 */     this.cell = cell;
/*  46 */     cell.addListeners(this);
/*     */     
/*  48 */     this.dm = ((DownloadManager)cell.getDataSource());
/*  49 */     if (this.dm == null)
/*  50 */       return;
/*  51 */     this.dm.addTrackerListener(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public void announceResult(TRTrackerAnnouncerResponse response) {}
/*     */   
/*     */   public boolean checkScrapeResult(TRTrackerScraperResponse response)
/*     */   {
/*  59 */     if (response != null) {
/*  60 */       TableCell cell_ref = this.cell;
/*     */       
/*  62 */       if (cell_ref == null) {
/*  63 */         return false;
/*     */       }
/*     */       
/*  66 */       DownloadManager dm = (DownloadManager)this.cell.getDataSource();
/*  67 */       if ((dm == null) || (dm != this.dm)) {
/*  68 */         return false;
/*     */       }
/*  70 */       TOTorrent torrent = dm.getTorrent();
/*     */       
/*  72 */       if (torrent == null) {
/*  73 */         return false;
/*     */       }
/*  75 */       URL announceURL = torrent.getAnnounceURL();
/*  76 */       URL responseURL = response.getURL();
/*  77 */       if ((announceURL != responseURL) && (announceURL != null) && (responseURL != null) && (!announceURL.toString().equals(responseURL.toString())))
/*     */       {
/*     */ 
/*  80 */         return false;
/*     */       }
/*     */       
/*  83 */       cell_ref.invalidate();
/*     */       
/*  85 */       return response.isValid();
/*     */     }
/*     */     
/*  88 */     return false;
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell) {
/*  92 */     DownloadManager oldDM = this.dm;
/*  93 */     this.dm = ((DownloadManager)cell.getDataSource());
/*     */     
/*     */ 
/*  96 */     if (this.dm != oldDM) {
/*  97 */       if (oldDM != null) {
/*  98 */         oldDM.removeTrackerListener(this);
/*     */       }
/* 100 */       if (this.dm != null) {
/* 101 */         this.dm.addTrackerListener(this);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void cellHover(TableCell cell) {}
/*     */   
/*     */   public void cellHoverComplete(TableCell cell) {
/* 109 */     cell.setToolTip(null);
/*     */   }
/*     */   
/*     */   public void dispose(TableCell cell) {
/* 113 */     if (this.dm != null)
/* 114 */       this.dm.removeTrackerListener(this);
/* 115 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 116 */     if ((dm != null) && (dm != this.dm))
/* 117 */       dm.removeTrackerListener(this);
/* 118 */     dm = null;
/* 119 */     cell = null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/AbstractTrackerCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */