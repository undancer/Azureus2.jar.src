/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*     */ 
/*     */ import com.aelitis.azureus.plugins.tracker.dht.DHTTrackerPlugin;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import java.util.Locale;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.internat.MessageText.MessageTextListener;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemFillListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.views.table.CoreTableColumnSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWT;
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
/*     */ public class PeersItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellAddedListener, ParameterListener
/*     */ {
/*  70 */   public static final Class DATASOURCE_TYPE = Download.class;
/*     */   
/*     */   public static final String COLUMN_ID = "peers";
/*     */   
/*     */   private static final String CFG_SHOW_ICON = "PeersColumn.showNetworkIcon";
/*     */   private static String textStarted;
/*     */   private static String textStartedOver;
/*     */   private static String textNotStarted;
/*     */   private static String textStartedNoScrape;
/*     */   private static String textNotStartedNoScrape;
/*     */   private static Image i2p_img;
/*     */   private static Image none_img;
/*     */   private boolean showIcon;
/*     */   
/*     */   static
/*     */   {
/*  86 */     MessageText.addAndFireListener(new MessageText.MessageTextListener() {
/*     */       public void localeChanged(Locale old_locale, Locale new_locale) {
/*  88 */         PeersItem.access$002(MessageText.getString("Column.seedspeers.started"));
/*  89 */         PeersItem.access$102(MessageText.getString("Column.seedspeers.started.over"));
/*  90 */         PeersItem.access$202(MessageText.getString("Column.seedspeers.notstarted"));
/*  91 */         PeersItem.access$302(MessageText.getString("Column.seedspeers.started.noscrape"));
/*  92 */         PeersItem.access$402(MessageText.getString("Column.seedspeers.notstarted.noscrape"));
/*     */       }
/*     */       
/*  95 */     });
/*  96 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*     */     
/*  98 */     i2p_img = imageLoader.getImage("net_I2P_x");
/*  99 */     none_img = imageLoader.getImage("net_None_x");
/*     */   }
/*     */   
/*     */ 
/*     */   public void fillTableColumnInfo(TableColumnInfo info)
/*     */   {
/* 105 */     info.addCategories(new String[] { "swarm" });
/*     */   }
/*     */   
/*     */   public PeersItem(String sTableID)
/*     */   {
/* 110 */     super(DATASOURCE_TYPE, "peers", 3, 60, sTableID);
/* 111 */     setRefreshInterval(-2);
/* 112 */     setMinWidthAuto(true);
/*     */     
/* 114 */     this.showIcon = COConfigurationManager.getBooleanParameter("PeersColumn.showNetworkIcon");
/*     */     
/* 116 */     COConfigurationManager.addParameterListener("PeersColumn.showNetworkIcon", this);
/*     */     
/*     */ 
/* 119 */     TableContextMenuItem menuShowIcon = addContextMenuItem("ConfigView.section.style.showNetworksIcon", 1);
/*     */     
/* 121 */     menuShowIcon.setStyle(2);
/* 122 */     menuShowIcon.addFillListener(new MenuItemFillListener() {
/*     */       public void menuWillBeShown(MenuItem menu, Object data) {
/* 124 */         menu.setData(Boolean.valueOf(PeersItem.this.showIcon));
/*     */       }
/*     */       
/* 127 */     });
/* 128 */     menuShowIcon.addMultiListener(new MenuItemListener() {
/*     */       public void selected(MenuItem menu, Object target) {
/* 130 */         COConfigurationManager.setParameter("PeersColumn.showNetworkIcon", ((Boolean)menu.getData()).booleanValue());
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void reset()
/*     */   {
/* 137 */     super.reset();
/*     */     
/* 139 */     COConfigurationManager.removeParameter("PeersColumn.showNetworkIcon");
/*     */   }
/*     */   
/*     */   protected void finalize() throws Throwable {
/* 143 */     super.finalize();
/* 144 */     COConfigurationManager.removeParameterListener("PeersColumn.showNetworkIcon", this);
/*     */   }
/*     */   
/* 147 */   public void cellAdded(TableCell cell) { new Cell(cell); }
/*     */   
/*     */   public void parameterChanged(String parameterName)
/*     */   {
/* 151 */     setShowIcon(COConfigurationManager.getBooleanParameter("PeersColumn.showNetworkIcon"));
/*     */   }
/*     */   
/*     */   public void setShowIcon(boolean b) {
/* 155 */     this.showIcon = b;
/* 156 */     invalidateCells();
/*     */   }
/*     */   
/*     */   private class Cell
/*     */     extends AbstractTrackerCell
/*     */     implements TableCellMouseListener
/*     */   {
/* 163 */     long lTotalPeers = -1L;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Cell(TableCell cell)
/*     */     {
/* 171 */       super();
/*     */     }
/*     */     
/*     */     public void scrapeResult(TRTrackerScraperResponse response) {
/* 175 */       if (checkScrapeResult(response)) {
/* 176 */         this.lTotalPeers = response.getPeers();
/*     */       }
/*     */     }
/*     */     
/*     */     public void refresh(TableCell cell) {
/* 181 */       super.refresh(cell);
/* 182 */       DownloadManager dm = (DownloadManager)cell.getDataSource();
/*     */       
/* 184 */       long lConnectedPeers = 0L;
/* 185 */       if (dm != null) {
/* 186 */         lConnectedPeers = dm.getNbPeers();
/*     */         
/* 188 */         if (this.lTotalPeers == -1L) {
/* 189 */           TRTrackerScraperResponse response = dm.getTrackerScrapeResponse();
/* 190 */           if ((response != null) && (response.isValid())) {
/* 191 */             this.lTotalPeers = response.getPeers();
/*     */           }
/*     */         }
/*     */         
/* 195 */         if ((cell instanceof TableCellSWT))
/*     */         {
/* 197 */           int[] i2p_info = (int[])dm.getUserData(DHTTrackerPlugin.DOWNLOAD_USER_DATA_I2P_SCRAPE_KEY);
/*     */           
/* 199 */           Image icon = PeersItem.none_img;
/*     */           
/* 201 */           if ((PeersItem.this.showIcon) && (i2p_info != null))
/*     */           {
/* 203 */             int totalI2PLeechers = i2p_info[1];
/*     */             
/* 205 */             if (totalI2PLeechers > 0)
/*     */             {
/* 207 */               icon = PeersItem.i2p_img;
/*     */             }
/*     */           }
/*     */           
/* 211 */           ((TableCellSWT)cell).setIcon(icon);
/*     */         }
/*     */       }
/*     */       
/* 215 */       long totalPeers = this.lTotalPeers;
/* 216 */       if ((totalPeers <= 0L) && 
/* 217 */         (dm != null)) {
/* 218 */         totalPeers = dm.getActivationCount();
/*     */       }
/*     */       
/*     */ 
/* 222 */       long value = lConnectedPeers * 10000000L;
/* 223 */       if (totalPeers > 0L) {
/* 224 */         value += totalPeers;
/*     */       }
/*     */       
/*     */       String text;
/* 228 */       if (dm != null) {
/* 229 */         int state = dm.getState();
/* 230 */         boolean started = (state == 60) || (state == 50);
/*     */         
/* 232 */         boolean hasScrape = this.lTotalPeers >= 0L;
/*     */         String text;
/* 234 */         if (started) {
/* 235 */           text = hasScrape ? PeersItem.textStarted : lConnectedPeers > this.lTotalPeers ? PeersItem.textStartedOver : PeersItem.textStartedNoScrape;
/*     */         }
/*     */         else {
/* 238 */           text = hasScrape ? PeersItem.textNotStarted : PeersItem.textNotStartedNoScrape;
/*     */         }
/*     */         
/* 241 */         if ((text.length() == 0) && 
/* 242 */           (text.length() == 0))
/*     */         {
/* 244 */           value = -2147483648L;
/*     */           
/* 246 */           long cache = dm.getDownloadState().getLongAttribute("scrapecache");
/*     */           
/* 248 */           if (cache != -1L)
/*     */           {
/* 250 */             int leechers = (int)(cache & 0xFFFFFF);
/*     */             
/* 252 */             value += leechers + 1;
/*     */           }
/*     */         }
/* 255 */         if ((!cell.setSortValue(value)) && (cell.isValid()))
/*     */         {
/* 257 */           return;
/*     */         }
/*     */         
/* 260 */         String text = text.replaceAll("%1", String.valueOf(lConnectedPeers));
/* 261 */         text = text.replaceAll("%2", String.valueOf(totalPeers));
/*     */       }
/*     */       else {
/* 264 */         text = "";
/* 265 */         value = -2147483648L;
/*     */         
/* 267 */         if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 268 */           return;
/*     */         }
/*     */       }
/*     */       
/* 272 */       cell.setText(text);
/*     */     }
/*     */     
/*     */     public void cellHover(TableCell cell) {
/* 276 */       super.cellHover(cell);
/*     */       
/* 278 */       long lConnectedPeers = 0L;
/* 279 */       DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 280 */       if (dm != null) {
/* 281 */         lConnectedPeers = dm.getNbPeers();
/*     */         
/* 283 */         String sToolTip = lConnectedPeers + " " + MessageText.getString("GeneralView.label.connected") + "\n";
/*     */         
/* 285 */         if (this.lTotalPeers != -1L) {
/* 286 */           sToolTip = sToolTip + this.lTotalPeers + " " + MessageText.getString("GeneralView.label.in_swarm");
/*     */         }
/*     */         else {
/* 289 */           TRTrackerScraperResponse response = dm.getTrackerScrapeResponse();
/* 290 */           sToolTip = sToolTip + "?? " + MessageText.getString("GeneralView.label.in_swarm");
/* 291 */           if (response != null) {
/* 292 */             sToolTip = sToolTip + "(" + response.getStatusString() + ")";
/*     */           }
/*     */         }
/* 295 */         int activationCount = dm.getActivationCount();
/* 296 */         if (activationCount > 0) {
/* 297 */           sToolTip = sToolTip + "\n" + MessageText.getString("PeerColumn.activationCount", new String[] { "" + activationCount });
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 302 */         long cache = dm.getDownloadState().getLongAttribute("scrapecache");
/*     */         
/* 304 */         if (cache != -1L)
/*     */         {
/* 306 */           int leechers = (int)(cache & 0xFFFFFF);
/*     */           
/* 308 */           if (leechers != this.lTotalPeers) {
/* 309 */             sToolTip = sToolTip + "\n" + leechers + " " + MessageText.getString("Scrape.status.cached").toLowerCase(Locale.US);
/*     */           }
/*     */         }
/*     */         
/* 313 */         int[] i2p_info = (int[])dm.getUserData(DHTTrackerPlugin.DOWNLOAD_USER_DATA_I2P_SCRAPE_KEY);
/*     */         
/* 315 */         if (i2p_info != null)
/*     */         {
/* 317 */           int totalI2PPeers = i2p_info[1];
/*     */           
/* 319 */           if (totalI2PPeers > 0)
/*     */           {
/* 321 */             sToolTip = sToolTip + "\n" + MessageText.getString("TableColumn.header.peers.i2p", new String[] { String.valueOf(totalI2PPeers) });
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 327 */         cell.setToolTip(sToolTip);
/*     */       } else {
/* 329 */         cell.setToolTip("");
/*     */       }
/*     */     }
/*     */     
/*     */     public void cellMouseTrigger(TableCellMouseEvent event) {
/* 334 */       DownloadManager dm = (DownloadManager)event.cell.getDataSource();
/* 335 */       if (dm == null) { return;
/*     */       }
/* 337 */       if (event.eventType != 2) { return;
/*     */       }
/* 339 */       event.skipCoreFunctionality = true;
/*     */       
/*     */ 
/* 342 */       int[] i2p_info = (int[])dm.getUserData(DHTTrackerPlugin.DOWNLOAD_USER_DATA_I2P_SCRAPE_KEY);
/*     */       
/* 344 */       if ((i2p_info != null) && (i2p_info[1] > 0)) {
/* 345 */         Utils.launch(MessageText.getString("privacy.view.wiki.url"));
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/PeersItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */