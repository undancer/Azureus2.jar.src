/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*     */ 
/*     */ import java.net.URL;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.core3.util.StringInterner;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellToolTipListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.ui.swt.views.MyTorrentsView;
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
/*     */ public class TrackerNameItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener, TableCellToolTipListener
/*     */ {
/*  46 */   public static final Class DATASOURCE_TYPE = Download.class;
/*     */   public static final String COLUMN_ID = "trackername";
/*     */   
/*     */   public TrackerNameItem(String sTableID)
/*     */   {
/*  51 */     super(DATASOURCE_TYPE, "trackername", 1, 120, sTableID);
/*  52 */     setRefreshInterval(5);
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  56 */     info.addCategories(new String[] { "tracker" });
/*     */     
/*     */ 
/*  59 */     info.setProficiency((byte)1);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell) {
/*  63 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/*  64 */     String name = "";
/*     */     
/*  66 */     if ((dm != null) && (dm.getTorrent() != null)) {
/*  67 */       TOTorrent torrent = dm.getTorrent();
/*     */       
/*  69 */       name = getTrackerName(torrent);
/*     */     }
/*     */     
/*     */ 
/*  73 */     if ((cell.setText(name)) || (!cell.isValid())) {
/*  74 */       TrackerCellUtils.updateColor(cell, dm, false);
/*     */     }
/*     */   }
/*     */   
/*     */   public static String getTrackerName(TOTorrent torrent) {
/*  79 */     String name = "";
/*     */     
/*  81 */     Set<String> pref_names = MyTorrentsView.preferred_tracker_names;
/*     */     
/*  83 */     URL url = null;
/*     */     
/*  85 */     if (pref_names != null)
/*     */     {
/*  87 */       TOTorrentAnnounceURLSet[] sets = torrent.getAnnounceURLGroup().getAnnounceURLSets();
/*     */       
/*  89 */       if (sets.length > 0)
/*     */       {
/*  91 */         String host = torrent.getAnnounceURL().getHost();
/*     */         
/*  93 */         if (pref_names.contains(host))
/*     */         {
/*  95 */           url = torrent.getAnnounceURL();
/*     */         }
/*     */         else
/*     */         {
/*  99 */           for (TOTorrentAnnounceURLSet set : sets)
/*     */           {
/* 101 */             URL[] urls = set.getAnnounceURLs();
/*     */             
/* 103 */             for (URL u : urls)
/*     */             {
/* 105 */               if (pref_names.contains(u.getHost()))
/*     */               {
/* 107 */                 url = u;
/*     */                 
/* 109 */                 break;
/*     */               }
/*     */             }
/*     */             
/* 113 */             if (url != null) {
/*     */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 122 */     if (url == null)
/*     */     {
/* 124 */       url = torrent.getAnnounceURL();
/*     */     }
/*     */     
/* 127 */     String host = url.getHost();
/*     */     
/* 129 */     if (host.endsWith(".dht"))
/*     */     {
/* 131 */       name = "dht";
/*     */ 
/*     */ 
/*     */     }
/* 135 */     else if (AENetworkClassifier.categoriseAddress(host) == "Public") {
/* 136 */       String[] parts = host.split("\\.");
/*     */       
/* 138 */       int used = 0;
/* 139 */       for (int i = parts.length - 1; i >= 0; i--) {
/* 140 */         if (used > 4) break;
/* 141 */         String chunk = parts[i];
/* 142 */         if ((used >= 2) && (chunk.length() >= 11)) break;
/* 143 */         if (used == 0) name = chunk; else
/* 144 */           name = chunk + "." + name;
/* 145 */         used++;
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 150 */       name = host;
/*     */     }
/*     */     
/*     */ 
/* 154 */     if (name.equals(host))
/*     */     {
/* 156 */       name = host;
/*     */     }
/*     */     else
/*     */     {
/* 160 */       name = StringInterner.intern(name);
/*     */     }
/*     */     
/* 163 */     return name;
/*     */   }
/*     */   
/*     */   public void cellHover(TableCell cell) {
/* 167 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 168 */     cell.setToolTip(TrackerCellUtils.getTooltipText(cell, dm, false));
/*     */   }
/*     */   
/*     */   public void cellHoverComplete(TableCell cell) {
/* 172 */     cell.setToolTip(null);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/TrackerNameItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */