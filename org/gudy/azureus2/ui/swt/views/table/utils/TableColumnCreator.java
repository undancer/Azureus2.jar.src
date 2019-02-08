/*     */ package org.gudy.azureus2.ui.swt.views.table.utils;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.AlertsItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.AvailabilityItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.BadAvailTimeItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.ColumnSizeWithDND;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.ColumnTorrentSpeed;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.CommentIconItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.CompletionItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.DateAddedItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.DateFileCompletedItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.DescriptionItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.DownSpeedItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.DownSpeedLimitItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.FilesDoneItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.MaxUploadsItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.OnlyCDing4Item;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.PeakDownItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.PeerSourcesItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.RankItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.RemainingItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.SavePathItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.SeedsItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.SessionUpItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.ShareRatioProgressItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.SizeItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.StatusItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.SwarmAverageCompletion;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.SwarmAverageSpeed;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.TorrentCreateDateItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.TrackerNameItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.TrackerNextAccessItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.TrackerStatusItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.UpSpeedLimitItem;
/*     */ 
/*     */ public class TableColumnCreator
/*     */ {
/*  45 */   public static int DATE_COLUMN_WIDTH = 110;
/*     */   
/*     */   public static TableColumnCore[] createIncompleteDM(String tableID) {
/*  48 */     String[] defaultVisibleOrder = { "health", "#", "name", "TorrentStream", "azsubs.ui.column.subs", "RatingColumn", "Info", "commenticon", "size", "down", "done", "status", "seeds", "peers", "downspeed", "upspeed", "eta", "shareRatio", "tracker" };
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
/*  70 */     TableColumnManager tcManager = TableColumnManager.getInstance();
/*  71 */     Map mapTCs = tcManager.getTableColumnsAsMap(org.gudy.azureus2.plugins.download.DownloadTypeIncomplete.class, tableID);
/*     */     
/*  73 */     tcManager.setDefaultColumnNames(tableID, defaultVisibleOrder);
/*     */     
/*  75 */     if ((!tcManager.loadTableColumnSettings(org.gudy.azureus2.plugins.download.DownloadTypeIncomplete.class, tableID)) || (areNoneVisible(mapTCs)))
/*     */     {
/*     */ 
/*  78 */       setVisibility(mapTCs, defaultVisibleOrder);
/*  79 */       RankItem tc = (RankItem)mapTCs.get("#");
/*  80 */       if (tc != null) {
/*  81 */         tcManager.setDefaultSortColumnName(tableID, "#");
/*  82 */         tc.setSortAscending(true);
/*     */       }
/*     */     }
/*     */     
/*  86 */     return (TableColumnCore[])mapTCs.values().toArray(new TableColumnCore[0]);
/*     */   }
/*     */   
/*     */   public static TableColumnCore[] createCompleteDM(String tableID) {
/*  90 */     String[] defaultVisibleOrder = { "health", "#", "SeedingRank", "name", "azsubs.ui.column.subs", "RatingColumn", "Info", "RateIt", "commenticon", "size", "status", "seeds", "peers", "upspeed", "shareRatio", "up", "tracker" };
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
/* 110 */     TableColumnManager tcManager = TableColumnManager.getInstance();
/* 111 */     Map mapTCs = tcManager.getTableColumnsAsMap(org.gudy.azureus2.plugins.download.DownloadTypeComplete.class, tableID);
/*     */     
/* 113 */     tcManager.setDefaultColumnNames(tableID, defaultVisibleOrder);
/*     */     
/* 115 */     if ((!tcManager.loadTableColumnSettings(org.gudy.azureus2.plugins.download.DownloadTypeComplete.class, tableID)) || (areNoneVisible(mapTCs)))
/*     */     {
/*     */ 
/* 118 */       setVisibility(mapTCs, defaultVisibleOrder);
/*     */       
/* 120 */       RankItem tc = (RankItem)mapTCs.get("#");
/* 121 */       if (tc != null) {
/* 122 */         tcManager.setDefaultSortColumnName(tableID, "#");
/* 123 */         tc.setSortAscending(true);
/*     */       }
/*     */     }
/*     */     
/* 127 */     return (TableColumnCore[])mapTCs.values().toArray(new TableColumnCore[0]);
/*     */   }
/*     */   
/*     */   private static boolean areNoneVisible(Map mapTCs) {
/* 131 */     boolean noneVisible = true;
/* 132 */     for (Iterator iter = mapTCs.values().iterator(); iter.hasNext();) {
/* 133 */       TableColumn tc = (TableColumn)iter.next();
/* 134 */       if (tc.isVisible()) {
/* 135 */         noneVisible = false;
/* 136 */         break;
/*     */       }
/*     */     }
/* 139 */     return noneVisible;
/*     */   }
/*     */   
/*     */   private static void setVisibility(Map mapTCs, String[] defaultVisibleOrder) {
/* 143 */     for (Iterator iter = mapTCs.values().iterator(); iter.hasNext();) {
/* 144 */       TableColumnCore tc = (TableColumnCore)iter.next();
/* 145 */       Long force_visible = (Long)tc.getUserData("ud_fv");
/* 146 */       if ((force_visible == null) || (force_visible.longValue() == 0L))
/*     */       {
/* 148 */         tc.setVisible(false);
/*     */       }
/*     */     }
/*     */     
/* 152 */     for (int i = 0; i < defaultVisibleOrder.length; i++) {
/* 153 */       String id = defaultVisibleOrder[i];
/* 154 */       TableColumnCore tc = (TableColumnCore)mapTCs.get(id);
/* 155 */       if (tc != null) {
/* 156 */         tc.setVisible(true);
/* 157 */         tc.setPositionNoShift(i);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void initCoreColumns()
/*     */   {
/* 169 */     Map<String, cInfo> c = new org.gudy.azureus2.core3.util.LightHashMap(50);
/* 170 */     Class tc = TableColumn.class;
/*     */     
/* 172 */     c.put("#", new cInfo(RankItem.class, RankItem.DATASOURCE_TYPE));
/* 173 */     c.put("name", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.NameItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.NameItem.DATASOURCE_TYPE));
/* 174 */     c.put("size", new cInfo(SizeItem.class, SizeItem.DATASOURCE_TYPE));
/* 175 */     c.put("sizewithdnd", new cInfo(ColumnSizeWithDND.class, ColumnSizeWithDND.DATASOURCE_TYPE));
/* 176 */     c.put("done", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.DoneItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.DoneItem.DATASOURCE_TYPE));
/* 177 */     c.put("donewithdnd", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.ColumnDoneWithDND.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.ColumnDoneWithDND.DATASOURCE_TYPE));
/* 178 */     c.put("status", new cInfo(StatusItem.class, StatusItem.DATASOURCE_TYPE));
/* 179 */     c.put("eta", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.ETAItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.ETAItem.DATASOURCE_TYPE));
/* 180 */     c.put("health", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.HealthItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.HealthItem.DATASOURCE_TYPE));
/* 181 */     c.put("commenticon", new cInfo(CommentIconItem.class, CommentIconItem.DATASOURCE_TYPE));
/* 182 */     c.put("down", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.DownItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.DownItem.DATASOURCE_TYPE));
/* 183 */     c.put("seeds", new cInfo(SeedsItem.class, SeedsItem.DATASOURCE_TYPE));
/* 184 */     c.put("peers", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.PeersItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.PeersItem.DATASOURCE_TYPE));
/* 185 */     c.put("downspeed", new cInfo(DownSpeedItem.class, DownSpeedItem.DATASOURCE_TYPE));
/* 186 */     c.put("upspeed", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.UpSpeedItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.UpSpeedItem.DATASOURCE_TYPE));
/* 187 */     c.put("maxupspeed", new cInfo(UpSpeedLimitItem.class, UpSpeedLimitItem.DATASOURCE_TYPE));
/* 188 */     c.put("tracker", new cInfo(TrackerStatusItem.class, TrackerStatusItem.DATASOURCE_TYPE));
/* 189 */     c.put("completed", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.CompletedItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.CompletedItem.DATASOURCE_TYPE));
/* 190 */     c.put("shareRatio", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.ShareRatioItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.ShareRatioItem.DATASOURCE_TYPE));
/* 191 */     c.put("sr_prog", new cInfo(ShareRatioProgressItem.class, ShareRatioProgressItem.DATASOURCE_TYPE));
/* 192 */     c.put("up", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.UpItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.UpItem.DATASOURCE_TYPE));
/* 193 */     c.put("remaining", new cInfo(RemainingItem.class, RemainingItem.DATASOURCE_TYPE));
/* 194 */     c.put("pieces", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.PiecesItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.PiecesItem.DATASOURCE_TYPE));
/* 195 */     c.put("completion", new cInfo(CompletionItem.class, CompletionItem.DATASOURCE_TYPE));
/* 196 */     c.put("comment", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.CommentItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.CommentItem.DATASOURCE_TYPE));
/* 197 */     c.put("maxuploads", new cInfo(MaxUploadsItem.class, MaxUploadsItem.DATASOURCE_TYPE));
/* 198 */     c.put("totalspeed", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.TotalSpeedItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.TotalSpeedItem.DATASOURCE_TYPE));
/* 199 */     c.put("filesdone", new cInfo(FilesDoneItem.class, FilesDoneItem.DATASOURCE_TYPE));
/* 200 */     c.put("fileslinked", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.FilesLinkedItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.FilesLinkedItem.DATASOURCE_TYPE));
/* 201 */     c.put("fileext", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.FileExtensionItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.FileExtensionItem.DATASOURCE_TYPE));
/* 202 */     c.put("savepath", new cInfo(SavePathItem.class, SavePathItem.DATASOURCE_TYPE));
/* 203 */     c.put("torrentpath", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.TorrentPathItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.TorrentPathItem.DATASOURCE_TYPE));
/* 204 */     c.put("category", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.CategoryItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.CategoryItem.DATASOURCE_TYPE));
/* 205 */     c.put("tags", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.TagsItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.TagsItem.DATASOURCE_TYPE));
/* 206 */     c.put("tag_colors", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.TagColorsItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.TagColorsItem.DATASOURCE_TYPE));
/* 207 */     c.put("tag_added_to", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.TagAddedToDateItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.TagAddedToDateItem.DATASOURCE_TYPE));
/* 208 */     c.put("networks", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.NetworksItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.NetworksItem.DATASOURCE_TYPE));
/* 209 */     c.put("peersources", new cInfo(PeerSourcesItem.class, PeerSourcesItem.DATASOURCE_TYPE));
/* 210 */     c.put("availability", new cInfo(AvailabilityItem.class, AvailabilityItem.DATASOURCE_TYPE));
/* 211 */     c.put("AvgAvail", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.AvgAvailItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.AvgAvailItem.DATASOURCE_TYPE));
/* 212 */     c.put("secondsseeding", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.SecondsSeedingItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.SecondsSeedingItem.DATASOURCE_TYPE));
/* 213 */     c.put("secondsdownloading", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.SecondsDownloadingItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.SecondsDownloadingItem.DATASOURCE_TYPE));
/* 214 */     c.put("timesincedownload", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.TimeSinceDownloadItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.TimeSinceDownloadItem.DATASOURCE_TYPE));
/* 215 */     c.put("timesinceupload", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.TimeSinceUploadItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.TimeSinceUploadItem.DATASOURCE_TYPE));
/* 216 */     c.put("OnlyCDing4", new cInfo(OnlyCDing4Item.class, OnlyCDing4Item.DATASOURCE_TYPE));
/* 217 */     c.put("trackernextaccess", new cInfo(TrackerNextAccessItem.class, TrackerNextAccessItem.DATASOURCE_TYPE));
/* 218 */     c.put("trackername", new cInfo(TrackerNameItem.class, TrackerNameItem.DATASOURCE_TYPE));
/* 219 */     c.put("seed_to_peer_ratio", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.SeedToPeerRatioItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.SeedToPeerRatioItem.DATASOURCE_TYPE));
/* 220 */     c.put("maxdownspeed", new cInfo(DownSpeedLimitItem.class, DownSpeedLimitItem.DATASOURCE_TYPE));
/* 221 */     c.put("swarm_average_speed", new cInfo(SwarmAverageSpeed.class, SwarmAverageSpeed.DATASOURCE_TYPE));
/* 222 */     c.put("swarm_average_completion", new cInfo(SwarmAverageCompletion.class, SwarmAverageCompletion.DATASOURCE_TYPE));
/* 223 */     c.put("bad_avail_time", new cInfo(BadAvailTimeItem.class, BadAvailTimeItem.DATASOURCE_TYPE));
/* 224 */     c.put("filecount", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.ColumnFileCount.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.ColumnFileCount.DATASOURCE_TYPE));
/* 225 */     c.put("torrentspeed", new cInfo(ColumnTorrentSpeed.class, ColumnTorrentSpeed.DATASOURCE_TYPE));
/*     */     
/* 227 */     c.put("DateCompleted", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.DateCompletedItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.DateCompletedItem.DATASOURCE_TYPE));
/* 228 */     c.put("DateFileCompleted", new cInfo(DateFileCompletedItem.class, DateFileCompletedItem.DATASOURCE_TYPE));
/* 229 */     c.put("date_added", new cInfo(DateAddedItem.class, DateAddedItem.DATASOURCE_TYPE));
/* 230 */     c.put("DateTorrentLastActive", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.DateLastActiveItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.DateLastActiveItem.DATASOURCE_TYPE));
/* 231 */     c.put("ipfilter", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.IPFilterItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.IPFilterItem.DATASOURCE_TYPE));
/* 232 */     c.put("alerts", new cInfo(AlertsItem.class, AlertsItem.DATASOURCE_TYPE));
/* 233 */     c.put("torrent_created", new cInfo(TorrentCreateDateItem.class, TorrentCreateDateItem.DATASOURCE_TYPE));
/*     */     
/* 235 */     c.put("TableColumnNameInfo", new cInfo(org.gudy.azureus2.ui.swt.views.columnsetup.ColumnTC_NameInfo.class, tc));
/* 236 */     c.put("TableColumnSample", new cInfo(org.gudy.azureus2.ui.swt.views.columnsetup.ColumnTC_Sample.class, tc));
/* 237 */     c.put("TableColumnChosenColumn", new cInfo(org.gudy.azureus2.ui.swt.views.columnsetup.ColumnTC_ChosenColumn.class, tc));
/*     */     
/* 239 */     c.put("peakup", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.PeakUpItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.PeakUpItem.DATASOURCE_TYPE));
/* 240 */     c.put("peakdown", new cInfo(PeakDownItem.class, PeakDownItem.DATASOURCE_TYPE));
/* 241 */     c.put("smoothup", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.SmoothedUpItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.SmoothedUpItem.DATASOURCE_TYPE));
/* 242 */     c.put("smoothdown", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.SmoothedDownItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.SmoothedDownItem.DATASOURCE_TYPE));
/* 243 */     c.put("smootheta", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.SmoothedETAItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.SmoothedETAItem.DATASOURCE_TYPE));
/* 244 */     c.put("min_sr", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.MinSRItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.MinSRItem.DATASOURCE_TYPE));
/* 245 */     c.put("max_sr", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.MaxSRItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.MaxSRItem.DATASOURCE_TYPE));
/*     */     
/* 247 */     c.put("sessionup", new cInfo(SessionUpItem.class, SessionUpItem.DATASOURCE_TYPE));
/* 248 */     c.put("sessiondown", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.SessionDownItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.SessionDownItem.DATASOURCE_TYPE));
/* 249 */     c.put("mergeddata", new cInfo(org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.MergedDataItem.class, org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.MergedDataItem.DATASOURCE_TYPE));
/*     */     
/* 251 */     c.put("description", new cInfo(DescriptionItem.class, DescriptionItem.DATASOURCE_TYPE));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 257 */     TableColumnManager tcManager = TableColumnManager.getInstance();
/*     */     
/* 259 */     com.aelitis.azureus.ui.common.table.TableColumnCoreCreationListener tcCreator = new com.aelitis.azureus.ui.common.table.TableColumnCoreCreationListener()
/*     */     {
/*     */       public TableColumnCore createTableColumnCore(Class forDataSourceType, String tableID, String columnID)
/*     */       {
/* 263 */         TableColumnCreator.cInfo info = (TableColumnCreator.cInfo)this.val$c.get(columnID);
/*     */         try
/*     */         {
/* 266 */           Constructor constructor = info.cla.getDeclaredConstructor(new Class[] { String.class });
/*     */           
/*     */ 
/* 269 */           return (TableColumnCore)constructor.newInstance(new Object[] { tableID });
/*     */ 
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 274 */           org.gudy.azureus2.core3.util.Debug.out(e);
/*     */         }
/*     */         
/* 277 */         return null;
/*     */       }
/*     */       
/*     */ 
/*     */       public void tableColumnCreated(TableColumn column) {}
/*     */     };
/*     */     
/* 284 */     for (Iterator iter = c.keySet().iterator(); iter.hasNext();) {
/* 285 */       String id = (String)iter.next();
/* 286 */       cInfo info = (cInfo)c.get(id);
/*     */       
/* 288 */       tcManager.registerColumn(info.forDataSourceType, id, tcCreator);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class cInfo
/*     */   {
/*     */     public Class cla;
/*     */     public Class forDataSourceType;
/*     */     
/*     */     public cInfo(Class cla, Class forDataSourceType) {
/* 298 */       this.cla = cla;
/* 299 */       this.forDataSourceType = forDataSourceType;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/table/utils/TableColumnCreator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */