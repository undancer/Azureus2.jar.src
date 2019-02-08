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
/*     */ public class SeedsItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellAddedListener, ParameterListener
/*     */ {
/*  62 */   public static final Class DATASOURCE_TYPE = Download.class;
/*     */   
/*     */   private static final String CFG_FC_SEEDSTART = "StartStopManager_iFakeFullCopySeedStart";
/*     */   
/*     */   private static final String CFG_FC_NUMPEERS = "StartStopManager_iNumPeersAsFullCopy";
/*     */   private static final String CFG_SHOW_ICON = "SeedsColumn.showNetworkIcon";
/*     */   public static final String COLUMN_ID = "seeds";
/*     */   private static String textStarted;
/*     */   private static String textStartedOver;
/*     */   private static String textNotStarted;
/*     */   private static String textStartedNoScrape;
/*     */   private static String textNotStartedNoScrape;
/*     */   private boolean showIcon;
/*     */   private static Image i2p_img;
/*     */   private static Image none_img;
/*     */   private int iFC_MinSeeds;
/*     */   private int iFC_NumPeers;
/*     */   
/*     */   static
/*     */   {
/*  82 */     MessageText.addAndFireListener(new MessageText.MessageTextListener() {
/*     */       public void localeChanged(Locale old_locale, Locale new_locale) {
/*  84 */         SeedsItem.access$002(MessageText.getString("Column.seedspeers.started"));
/*  85 */         SeedsItem.access$102(MessageText.getString("Column.seedspeers.started.over"));
/*  86 */         SeedsItem.access$202(MessageText.getString("Column.seedspeers.notstarted"));
/*  87 */         SeedsItem.access$302(MessageText.getString("Column.seedspeers.started.noscrape"));
/*  88 */         SeedsItem.access$402(MessageText.getString("Column.seedspeers.notstarted.noscrape"));
/*     */       }
/*     */       
/*  91 */     });
/*  92 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*     */     
/*  94 */     i2p_img = imageLoader.getImage("net_I2P_x");
/*  95 */     none_img = imageLoader.getImage("net_None_x");
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  99 */     info.addCategories(new String[] { "swarm" });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SeedsItem(String sTableID)
/*     */   {
/* 110 */     super(DATASOURCE_TYPE, "seeds", 3, 60, sTableID);
/* 111 */     setRefreshInterval(-2);
/* 112 */     setMinWidthAuto(true);
/*     */     
/* 114 */     this.iFC_MinSeeds = COConfigurationManager.getIntParameter("StartStopManager_iFakeFullCopySeedStart");
/* 115 */     this.iFC_NumPeers = COConfigurationManager.getIntParameter("StartStopManager_iNumPeersAsFullCopy");
/* 116 */     this.showIcon = COConfigurationManager.getBooleanParameter("SeedsColumn.showNetworkIcon");
/*     */     
/* 118 */     COConfigurationManager.addParameterListener("StartStopManager_iFakeFullCopySeedStart", this);
/* 119 */     COConfigurationManager.addParameterListener("StartStopManager_iNumPeersAsFullCopy", this);
/* 120 */     COConfigurationManager.addParameterListener("SeedsColumn.showNetworkIcon", this);
/*     */     
/*     */ 
/*     */ 
/* 124 */     TableContextMenuItem menuShowIcon = addContextMenuItem("ConfigView.section.style.showNetworksIcon", 1);
/*     */     
/* 126 */     menuShowIcon.setStyle(2);
/* 127 */     menuShowIcon.addFillListener(new MenuItemFillListener() {
/*     */       public void menuWillBeShown(MenuItem menu, Object data) {
/* 129 */         menu.setData(Boolean.valueOf(SeedsItem.this.showIcon));
/*     */       }
/*     */       
/* 132 */     });
/* 133 */     menuShowIcon.addMultiListener(new MenuItemListener() {
/*     */       public void selected(MenuItem menu, Object target) {
/* 135 */         COConfigurationManager.setParameter("SeedsColumn.showNetworkIcon", ((Boolean)menu.getData()).booleanValue());
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void reset()
/*     */   {
/* 142 */     super.reset();
/*     */     
/* 144 */     COConfigurationManager.removeParameter("SeedsColumn.showNetworkIcon");
/*     */   }
/*     */   
/*     */   protected void finalize() throws Throwable {
/* 148 */     super.finalize();
/* 149 */     COConfigurationManager.removeParameterListener("StartStopManager_iFakeFullCopySeedStart", this);
/* 150 */     COConfigurationManager.removeParameterListener("StartStopManager_iNumPeersAsFullCopy", this);
/* 151 */     COConfigurationManager.removeParameterListener("SeedsColumn.showNetworkIcon", this);
/*     */   }
/*     */   
/*     */   public void cellAdded(TableCell cell) {
/* 155 */     new Cell(cell);
/*     */   }
/*     */   
/*     */   public void parameterChanged(String parameterName) {
/* 159 */     this.iFC_MinSeeds = COConfigurationManager.getIntParameter("StartStopManager_iFakeFullCopySeedStart");
/* 160 */     this.iFC_NumPeers = COConfigurationManager.getIntParameter("StartStopManager_iNumPeersAsFullCopy");
/* 161 */     setShowIcon(COConfigurationManager.getBooleanParameter("SeedsColumn.showNetworkIcon"));
/*     */   }
/*     */   
/*     */   public void setShowIcon(boolean b) {
/* 165 */     this.showIcon = b;
/* 166 */     invalidateCells();
/*     */   }
/*     */   
/*     */   private class Cell
/*     */     extends AbstractTrackerCell
/*     */     implements TableCellMouseListener
/*     */   {
/* 173 */     private long lTotalPeers = 0L;
/*     */     
/* 175 */     private long lTotalSeeds = -1L;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Cell(TableCell cell)
/*     */     {
/* 183 */       super();
/*     */     }
/*     */     
/*     */     public void scrapeResult(TRTrackerScraperResponse response) {
/* 187 */       if (checkScrapeResult(response)) {
/* 188 */         this.lTotalSeeds = response.getSeeds();
/* 189 */         this.lTotalPeers = response.getPeers();
/*     */       }
/*     */     }
/*     */     
/*     */     public void refresh(TableCell cell) {
/* 194 */       super.refresh(cell);
/*     */       
/* 196 */       long lConnectedSeeds = 0L;
/* 197 */       DownloadManager dm = (DownloadManager)cell.getDataSource();
/*     */       
/* 199 */       if (dm != null) {
/* 200 */         lConnectedSeeds = dm.getNbSeeds();
/*     */         
/* 202 */         if (this.lTotalSeeds == -1L) {
/* 203 */           TRTrackerScraperResponse response = dm.getTrackerScrapeResponse();
/* 204 */           if ((response != null) && (response.isValid())) {
/* 205 */             this.lTotalSeeds = response.getSeeds();
/* 206 */             this.lTotalPeers = response.getPeers();
/*     */           }
/*     */         }
/*     */         
/* 210 */         if ((cell instanceof TableCellSWT))
/*     */         {
/* 212 */           int[] i2p_info = (int[])dm.getUserData(DHTTrackerPlugin.DOWNLOAD_USER_DATA_I2P_SCRAPE_KEY);
/*     */           
/* 214 */           Image icon = SeedsItem.none_img;
/*     */           
/* 216 */           if ((i2p_info != null) && (SeedsItem.this.showIcon))
/*     */           {
/* 218 */             int totalI2PSeeds = i2p_info[0];
/*     */             
/* 220 */             if (totalI2PSeeds > 0)
/*     */             {
/* 222 */               icon = SeedsItem.i2p_img;
/*     */             }
/*     */           }
/*     */           
/* 226 */           ((TableCellSWT)cell).setIcon(icon);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 231 */       long value = lConnectedSeeds << 42;
/* 232 */       if (this.lTotalSeeds > 0L)
/* 233 */         value += (this.lTotalSeeds << 21);
/* 234 */       if (this.lTotalPeers > 0L) {
/* 235 */         value += this.lTotalPeers;
/*     */       }
/*     */       
/*     */       String text;
/* 239 */       if (dm != null) {
/* 240 */         boolean bCompleteTorrent = dm.getAssumedComplete();
/*     */         
/* 242 */         int state = dm.getState();
/* 243 */         boolean started = (state == 60) || (state == 50);
/* 244 */         boolean hasScrape = this.lTotalSeeds >= 0L;
/*     */         String text;
/* 246 */         if (started) {
/* 247 */           text = hasScrape ? SeedsItem.textStarted : lConnectedSeeds > this.lTotalSeeds ? SeedsItem.textStartedOver : SeedsItem.textStartedNoScrape;
/*     */         }
/*     */         else {
/* 250 */           text = hasScrape ? SeedsItem.textNotStarted : SeedsItem.textNotStartedNoScrape;
/*     */         }
/*     */         
/* 253 */         if (text.length() == 0)
/*     */         {
/* 255 */           value = -2147483648L;
/*     */           
/* 257 */           long cache = dm.getDownloadState().getLongAttribute("scrapecache");
/*     */           
/* 259 */           if (cache != -1L)
/*     */           {
/* 261 */             int seeds = (int)(cache >> 32 & 0xFFFFFF);
/*     */             
/* 263 */             value += seeds + 1;
/*     */           }
/*     */         }
/* 266 */         if ((!cell.setSortValue(value)) && (cell.isValid()))
/*     */         {
/* 268 */           return;
/*     */         }
/*     */         
/* 271 */         String text = text.replaceAll("%1", String.valueOf(lConnectedSeeds));
/* 272 */         String param2 = "?";
/* 273 */         if (this.lTotalSeeds != -1L) {
/* 274 */           param2 = String.valueOf(this.lTotalSeeds);
/* 275 */           if ((bCompleteTorrent) && (SeedsItem.this.iFC_NumPeers > 0) && (this.lTotalSeeds >= SeedsItem.this.iFC_MinSeeds) && (this.lTotalPeers > 0L))
/*     */           {
/* 277 */             long lSeedsToAdd = this.lTotalPeers / SeedsItem.this.iFC_NumPeers;
/* 278 */             if (lSeedsToAdd > 0L) {
/* 279 */               param2 = param2 + "+" + lSeedsToAdd;
/*     */             }
/*     */           }
/*     */         }
/* 283 */         text = text.replaceAll("%2", param2);
/*     */       } else {
/* 285 */         text = "";
/* 286 */         value = -2147483648L;
/*     */         
/* 288 */         if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 289 */           return;
/*     */         }
/*     */       }
/*     */       
/* 293 */       cell.setText(text);
/*     */     }
/*     */     
/*     */     public void cellHover(TableCell cell) {
/* 297 */       super.cellHover(cell);
/*     */       
/* 299 */       long lConnectedSeeds = 0L;
/* 300 */       DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 301 */       if (dm != null) {
/* 302 */         lConnectedSeeds = dm.getNbSeeds();
/*     */         
/* 304 */         String sToolTip = lConnectedSeeds + " " + MessageText.getString("GeneralView.label.connected") + "\n";
/*     */         
/* 306 */         if (this.lTotalSeeds != -1L) {
/* 307 */           sToolTip = sToolTip + this.lTotalSeeds + " " + MessageText.getString("GeneralView.label.in_swarm");
/*     */         }
/*     */         else {
/* 310 */           TRTrackerScraperResponse response = dm.getTrackerScrapeResponse();
/* 311 */           sToolTip = sToolTip + "?? " + MessageText.getString("GeneralView.label.in_swarm");
/* 312 */           if (response != null)
/* 313 */             sToolTip = sToolTip + "(" + response.getStatusString() + ")";
/*     */         }
/* 315 */         boolean bCompleteTorrent = dm.getAssumedComplete();
/* 316 */         if ((bCompleteTorrent) && (SeedsItem.this.iFC_NumPeers > 0) && (this.lTotalSeeds >= SeedsItem.this.iFC_MinSeeds) && (this.lTotalPeers > 0L))
/*     */         {
/* 318 */           long lSeedsToAdd = this.lTotalPeers / SeedsItem.this.iFC_NumPeers;
/* 319 */           sToolTip = sToolTip + "\n" + MessageText.getString("TableColumn.header.seeds.fullcopycalc", new String[] { "" + this.lTotalPeers, "" + lSeedsToAdd });
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 327 */         long cache = dm.getDownloadState().getLongAttribute("scrapecache");
/*     */         
/* 329 */         if (cache != -1L)
/*     */         {
/* 331 */           int seeds = (int)(cache >> 32 & 0xFFFFFF);
/*     */           
/* 333 */           if (seeds != this.lTotalSeeds) {
/* 334 */             sToolTip = sToolTip + "\n" + seeds + " " + MessageText.getString("Scrape.status.cached").toLowerCase(Locale.US);
/*     */           }
/*     */         }
/*     */         
/* 338 */         int[] i2p_info = (int[])dm.getUserData(DHTTrackerPlugin.DOWNLOAD_USER_DATA_I2P_SCRAPE_KEY);
/*     */         
/* 340 */         if (i2p_info != null)
/*     */         {
/* 342 */           int totalI2PSeeds = i2p_info[0];
/*     */           
/* 344 */           if (totalI2PSeeds > 0)
/*     */           {
/* 346 */             sToolTip = sToolTip + "\n" + MessageText.getString("TableColumn.header.peers.i2p", new String[] { String.valueOf(totalI2PSeeds) });
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 352 */         cell.setToolTip(sToolTip);
/*     */       } else {
/* 354 */         cell.setToolTip("");
/*     */       }
/*     */     }
/*     */     
/*     */     public void cellMouseTrigger(TableCellMouseEvent event) {
/* 359 */       DownloadManager dm = (DownloadManager)event.cell.getDataSource();
/* 360 */       if (dm == null) { return;
/*     */       }
/* 362 */       if (event.eventType != 2) { return;
/*     */       }
/* 364 */       event.skipCoreFunctionality = true;
/*     */       
/*     */ 
/* 367 */       int[] i2p_info = (int[])dm.getUserData(DHTTrackerPlugin.DOWNLOAD_USER_DATA_I2P_SCRAPE_KEY);
/*     */       
/* 369 */       if ((i2p_info != null) && (i2p_info[0] > 0)) {
/* 370 */         Utils.launch(MessageText.getString("privacy.view.wiki.url"));
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/SeedsItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */