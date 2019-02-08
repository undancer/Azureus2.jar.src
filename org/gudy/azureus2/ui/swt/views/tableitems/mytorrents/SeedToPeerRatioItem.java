/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*     */ 
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.ui.swt.views.table.CoreTableColumnSWT;
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
/*     */ public class SeedToPeerRatioItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener
/*     */ {
/*  40 */   public static final Class DATASOURCE_TYPE = Download.class;
/*     */   
/*     */   public static final String COLUMN_ID = "seed_to_peer_ratio";
/*     */   
/*     */   public SeedToPeerRatioItem(String sTableID)
/*     */   {
/*  46 */     super(DATASOURCE_TYPE, "seed_to_peer_ratio", 2, 70, sTableID);
/*  47 */     setRefreshInterval(-2);
/*  48 */     setMinWidthAuto(true);
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  52 */     info.addCategories(new String[] { "swarm" });
/*     */     
/*     */ 
/*  55 */     info.setProficiency((byte)2);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell) {
/*  59 */     float ratio = -1.0F;
/*     */     
/*     */ 
/*  62 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/*  63 */     if (dm != null) {
/*  64 */       TRTrackerScraperResponse response = dm.getTrackerScrapeResponse();
/*     */       
/*     */       int seeds;
/*     */       int peers;
/*  68 */       if ((response != null) && (response.isValid())) {
/*  69 */         int seeds = Math.max(dm.getNbSeeds(), response.getSeeds());
/*     */         
/*  71 */         int trackerPeerCount = response.getPeers();
/*  72 */         int peers = dm.getNbPeers();
/*  73 */         if ((peers == 0) || (trackerPeerCount > peers)) {
/*  74 */           if (trackerPeerCount <= 0) {
/*  75 */             peers = dm.getActivationCount();
/*     */           } else {
/*  77 */             peers = trackerPeerCount;
/*     */           }
/*     */         }
/*     */       }
/*     */       else {
/*  82 */         seeds = dm.getNbSeeds();
/*  83 */         peers = dm.getNbPeers();
/*     */       }
/*     */       
/*  86 */       if ((peers < 0) || (seeds < 0)) {
/*  87 */         ratio = 0.0F;
/*     */       }
/*  89 */       else if (peers == 0) {
/*  90 */         if (seeds == 0) {
/*  91 */           ratio = 0.0F;
/*     */         } else
/*  93 */           ratio = Float.POSITIVE_INFINITY;
/*     */       } else {
/*  95 */         ratio = seeds / peers;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 100 */     if ((!cell.setSortValue(ratio)) && (cell.isValid())) {
/* 101 */       return;
/*     */     }
/*     */     
/* 104 */     if (ratio == -1.0F) {
/* 105 */       cell.setText("");
/* 106 */     } else if (ratio == 0.0F) {
/* 107 */       cell.setText("??");
/*     */     } else {
/* 109 */       cell.setText(DisplayFormatters.formatDecimal(ratio, 3));
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/SeedToPeerRatioItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */