/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHost;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrent;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.ui.swt.views.table.CoreTableColumnSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWTPaintListener;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HealthItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellAddedListener, TableCellRefreshListener, TableCellSWTPaintListener
/*     */ {
/*  52 */   public static final Class DATASOURCE_TYPE = Download.class;
/*     */   
/*     */   static final int COLUMN_WIDTH = 16;
/*     */   public static final String COLUMN_ID = "health";
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info)
/*     */   {
/*  59 */     info.addCategories(new String[] { "essential" });
/*     */   }
/*     */   
/*  62 */   static TRHost tracker_host = null;
/*     */   
/*     */   public HealthItem(String sTableID)
/*     */   {
/*  66 */     super(DATASOURCE_TYPE, "health", 3, 16, sTableID);
/*  67 */     initializeAsGraphic(-2, 16);
/*  68 */     setMinWidth(16);
/*  69 */     setIconReference("st_stopped", true);
/*     */   }
/*     */   
/*     */   public void cellAdded(TableCell cell) {
/*  73 */     cell.setMarginWidth(0);
/*  74 */     cell.setMarginHeight(0);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell) {
/*  78 */     if (tracker_host == null) {
/*     */       try {
/*  80 */         tracker_host = AzureusCoreFactory.getSingleton().getTrackerHost();
/*     */       }
/*     */       catch (Throwable t) {}
/*  83 */       if (tracker_host == null) {
/*  84 */         return;
/*     */       }
/*     */     }
/*     */     
/*  88 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/*     */     TRHostTorrent ht;
/*     */     int health;
/*     */     TRHostTorrent ht;
/*  92 */     if (dm == null) {
/*  93 */       int health = 0;
/*  94 */       ht = null;
/*     */     } else {
/*  96 */       health = dm.getHealthStatus();
/*  97 */       ht = tracker_host.getHostTorrent(dm.getTorrent());
/*     */     }
/*     */     
/* 100 */     if ((!cell.setSortValue(health + (ht == null ? 0 : 256))) && (cell.isValid())) {
/* 101 */       return;
/*     */     }
/*     */     
/* 104 */     String sHelpID = null;
/*     */     
/* 106 */     if (health == 5) {
/* 107 */       sHelpID = "health.explain.red";
/* 108 */     } else if (health == 4) {
/* 109 */       sHelpID = "health.explain.green";
/* 110 */     } else if (health == 2) {
/* 111 */       sHelpID = "health.explain.blue";
/* 112 */     } else if (health == 3) {
/* 113 */       sHelpID = "health.explain.yellow";
/* 114 */     } else if (health != 6)
/*     */     {
/* 116 */       sHelpID = "health.explain.grey";
/*     */     }
/*     */     
/* 119 */     String sToolTip = (health == 6) && (dm != null) ? dm.getErrorDetails() : MessageText.getString(sHelpID);
/*     */     
/* 121 */     if (ht != null)
/* 122 */       sToolTip = sToolTip + "\n" + MessageText.getString("health.explain.share");
/* 123 */     cell.setToolTip(sToolTip);
/*     */   }
/*     */   
/*     */ 
/*     */   public void cellPaint(GC gc, TableCellSWT cell)
/*     */   {
/* 129 */     Comparable sortValue = cell.getSortValue();
/* 130 */     if (!(sortValue instanceof Long)) {
/* 131 */       return;
/*     */     }
/* 133 */     boolean isShare = false;
/* 134 */     long health = ((Long)sortValue).longValue();
/* 135 */     if (health >= 256L) {
/* 136 */       health -= 256L;
/* 137 */       isShare = true;
/*     */     }
/*     */     
/*     */     String image_name;
/*     */     String image_name;
/* 142 */     if (health == 5L) {
/* 143 */       image_name = "st_ko"; } else { String image_name;
/* 144 */       if (health == 4L) {
/* 145 */         image_name = "st_ok"; } else { String image_name;
/* 146 */         if (health == 2L) {
/* 147 */           image_name = "st_no_tracker"; } else { String image_name;
/* 148 */           if (health == 3L) {
/* 149 */             image_name = "st_no_remote"; } else { String image_name;
/* 150 */             if (health == 6L) {
/* 151 */               image_name = "st_error";
/*     */             } else
/* 153 */               image_name = "";
/*     */           }
/*     */         } } }
/* 156 */     if (isShare) {
/* 157 */       image_name = image_name + "_shared";
/*     */     }
/*     */     
/* 160 */     ImageLoader imageLoader = ImageLoader.getInstance();
/* 161 */     Image img = imageLoader.getImage(image_name);
/*     */     try
/*     */     {
/* 164 */       Rectangle cellBounds = cell.getBounds();
/*     */       
/* 166 */       if (ImageLoader.isRealImage(img)) {
/* 167 */         Rectangle imgBounds = img.getBounds();
/* 168 */         gc.drawImage(img, cellBounds.x + (cellBounds.width - imgBounds.width) / 2, cellBounds.y + (cellBounds.height - imgBounds.height) / 2);
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 173 */       imageLoader.releaseImage(image_name);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/HealthItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */