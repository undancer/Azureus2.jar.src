/*     */ package com.aelitis.azureus.ui.swt.columns.torrent;
/*     */ 
/*     */ import com.aelitis.azureus.core.download.DownloadManagerEnhancer;
/*     */ import com.aelitis.azureus.core.download.EnhancedDownloadManager;
/*     */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinFactory;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinProperties;
/*     */ import com.aelitis.azureus.ui.swt.utils.FontUtils;
/*     */ import java.io.PrintStream;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.download.DownloadTypeIncomplete;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemFillListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableRow;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.SWTThread;
/*     */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter;
/*     */ import org.gudy.azureus2.ui.swt.views.MyTorrentsView;
/*     */ import org.gudy.azureus2.ui.swt.views.ViewUtils;
/*     */ import org.gudy.azureus2.ui.swt.views.ViewUtils.CustomDateFormat;
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
/*     */ public class ColumnProgressETA
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellAddedListener, TableCellMouseListener, TableCellRefreshListener, TableCellSWTPaintListener
/*     */ {
/*  66 */   public static final Class DATASOURCE_TYPE = DownloadTypeIncomplete.class;
/*     */   
/*     */   public static final String COLUMN_ID = "ProgressETA";
/*     */   
/*     */   private static final int borderWidth = 1;
/*     */   
/*     */   private static final int COLUMN_WIDTH = 200;
/*     */   
/*     */   public static final long SHOW_ETA_AFTER_MS = 30000L;
/*     */   
/*  76 */   private static final Object CLICK_KEY = new Object();
/*     */   
/*     */   protected static final String CFG_SHOWETA = "ColumnProgressETA.showETA";
/*     */   
/*     */   protected static final String CFG_SHOWSPEED = "ColumnProgressETA.showSpeed";
/*     */   
/*  82 */   private static Font fontText = null;
/*     */   
/*     */ 
/*     */   Display display;
/*     */   
/*     */   private Color cBGdl;
/*     */   
/*     */   private Color cBGcd;
/*     */   
/*     */   private Color cBorder;
/*     */   
/*     */   private Color cText;
/*     */   
/*     */   Color textColor;
/*     */   
/*     */   private Image imgBGTorrent;
/*     */   
/*     */   private Color cTextDrop;
/*     */   
/*     */   private ViewUtils.CustomDateFormat cdf;
/*     */   
/*     */   private ColumnTorrentFileProgress fileProgress;
/*     */   
/*     */   protected boolean showETA;
/*     */   
/*     */   protected boolean showSpeed;
/*     */   
/*     */ 
/*     */   public ColumnProgressETA(String sTableID)
/*     */   {
/* 112 */     super(DATASOURCE_TYPE, "ProgressETA", 3, 200, sTableID);
/* 113 */     addDataSourceType(DiskManagerFileInfo.class);
/* 114 */     initializeAsGraphic(200);
/* 115 */     setAlignment(1);
/* 116 */     setMinWidth(200);
/*     */     
/* 118 */     this.display = SWTThread.getInstance().getDisplay();
/*     */     
/* 120 */     SWTSkinProperties skinProperties = SWTSkinFactory.getInstance().getSkinProperties();
/* 121 */     this.cBGdl = skinProperties.getColor("color.progress.bg.dl");
/* 122 */     if (this.cBGdl == null) {
/* 123 */       this.cBGdl = Colors.blues[9];
/*     */     }
/* 125 */     this.cBGcd = skinProperties.getColor("color.progress.bg.cd");
/* 126 */     if (this.cBGcd == null) {
/* 127 */       this.cBGcd = Colors.green;
/*     */     }
/* 129 */     this.cBorder = skinProperties.getColor("color.progress.border");
/* 130 */     if (this.cBorder == null) {
/* 131 */       this.cBorder = Colors.grey;
/*     */     }
/* 133 */     this.cText = skinProperties.getColor("color.progress.text");
/* 134 */     if (this.cText == null) {
/* 135 */       this.cText = Colors.black;
/*     */     }
/* 137 */     this.cTextDrop = skinProperties.getColor("color.progress.text.drop");
/*     */     
/* 139 */     this.cdf = ViewUtils.addCustomDateFormat(this);
/*     */     
/* 141 */     ImageLoader imageLoader = ImageLoader.getInstance();
/* 142 */     this.imgBGTorrent = imageLoader.getImage("image.progress.bg.torrent");
/*     */     
/* 144 */     this.fileProgress = new ColumnTorrentFileProgress(this.display);
/*     */     
/* 146 */     TableContextMenuItem menuShowETA = addContextMenuItem("ColumnProgressETA.showETA", 1);
/*     */     
/* 148 */     menuShowETA.setStyle(2);
/* 149 */     menuShowETA.addFillListener(new MenuItemFillListener() {
/*     */       public void menuWillBeShown(MenuItem menu, Object data) {
/* 151 */         menu.setData(Boolean.valueOf(ColumnProgressETA.this.showETA));
/*     */       }
/* 153 */     });
/* 154 */     menuShowETA.addMultiListener(new MenuItemListener() {
/*     */       public void selected(MenuItem menu, Object target) {
/* 156 */         ColumnProgressETA.this.showETA = ((Boolean)menu.getData()).booleanValue();
/* 157 */         ColumnProgressETA.this.setUserData("ColumnProgressETA.showETA", Integer.valueOf(ColumnProgressETA.this.showETA ? 1 : 0));
/*     */       }
/*     */       
/* 160 */     });
/* 161 */     TableContextMenuItem menuShowSpeed = addContextMenuItem("ColumnProgressETA.showSpeed", 1);
/*     */     
/* 163 */     menuShowSpeed.setStyle(2);
/* 164 */     menuShowSpeed.addFillListener(new MenuItemFillListener() {
/*     */       public void menuWillBeShown(MenuItem menu, Object data) {
/* 166 */         menu.setData(Boolean.valueOf(ColumnProgressETA.this.showSpeed));
/*     */       }
/* 168 */     });
/* 169 */     menuShowSpeed.addMultiListener(new MenuItemListener() {
/*     */       public void selected(MenuItem menu, Object target) {
/* 171 */         ColumnProgressETA.this.showSpeed = ((Boolean)menu.getData()).booleanValue();
/* 172 */         ColumnProgressETA.this.setUserData("ColumnProgressETA.showSpeed", Integer.valueOf(ColumnProgressETA.this.showSpeed ? 1 : 0));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void fillTableColumnInfo(TableColumnInfo info)
/*     */   {
/* 181 */     info.addCategories(new String[] { "content", "essential", "time" });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 186 */     info.setProficiency((byte)0);
/*     */   }
/*     */   
/*     */   public void cellAdded(TableCell cell) {
/* 190 */     cell.setMarginHeight(3);
/* 191 */     cell.setMarginWidth(2);
/*     */   }
/*     */   
/*     */   public void cellMouseTrigger(TableCellMouseEvent event)
/*     */   {
/* 196 */     Object ds = event.cell.getDataSource();
/* 197 */     if ((ds instanceof DiskManagerFileInfo)) {
/* 198 */       this.fileProgress.fileInfoMouseTrigger(event);
/* 199 */       return;
/*     */     }
/*     */     
/* 202 */     DownloadManager dm = (DownloadManager)ds;
/* 203 */     if (dm == null) {
/* 204 */       return;
/*     */     }
/*     */     
/* 207 */     String clickable = (String)dm.getUserData(CLICK_KEY);
/*     */     
/* 209 */     if (clickable == null)
/*     */     {
/* 211 */       return;
/*     */     }
/*     */     
/* 214 */     event.skipCoreFunctionality = true;
/*     */     
/* 216 */     if (event.eventType == 1)
/*     */     {
/* 218 */       String url = UrlUtils.getURL(clickable);
/*     */       
/* 220 */       if (url != null)
/*     */       {
/* 222 */         Utils.launch(url);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell) {
/* 228 */     Object ds = cell.getDataSource();
/*     */     
/*     */ 
/* 231 */     long percentDone = getPercentDone(ds);
/*     */     
/* 233 */     long sortValue = 0L;
/*     */     
/* 235 */     if ((ds instanceof DownloadManager)) {
/* 236 */       DownloadManager dm = (DownloadManager)cell.getDataSource();
/*     */       
/* 238 */       int hashCode = Math.abs(DisplayFormatters.formatDownloadStatus(dm).hashCode());
/*     */       
/* 240 */       long completedTime = dm.getDownloadState().getLongParameter("stats.download.completed.time");
/*     */       
/* 242 */       if ((completedTime <= 0L) || (!dm.isDownloadComplete(false))) {
/* 243 */         sortValue = (percentDone << 31) + hashCode;
/*     */       } else {
/* 245 */         sortValue = (completedTime / 1000L << 31) + hashCode;
/*     */       }
/*     */     }
/* 248 */     else if ((ds instanceof DiskManagerFileInfo)) {
/* 249 */       DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)ds;
/* 250 */       int st = fileInfo.getStorageType();
/* 251 */       if (((st == 2) || (st == 4)) && (fileInfo.isSkipped()))
/*     */       {
/* 253 */         sortValue = 1L;
/* 254 */       } else if (fileInfo.isSkipped()) {
/* 255 */         sortValue = 2L;
/* 256 */       } else if (fileInfo.getPriority() > 0)
/*     */       {
/* 258 */         int pri = fileInfo.getPriority();
/* 259 */         sortValue = 4L;
/*     */         
/* 261 */         if (pri > 1) {
/* 262 */           sortValue += pri;
/*     */         }
/*     */       } else {
/* 265 */         sortValue = 3L;
/*     */       }
/* 267 */       sortValue = fileInfo.getDownloadManager().getState() * 10000 + percentDone + sortValue;
/*     */     }
/*     */     
/*     */ 
/* 271 */     long eta = this.showETA ? getETA(cell) : 0L;
/* 272 */     long speed = this.showSpeed ? getSpeed(ds) : 0L;
/*     */     
/*     */ 
/* 275 */     Comparable old = cell.getSortValue();
/* 276 */     boolean sortChanged = cell.setSortValue(sortValue);
/*     */     
/* 278 */     if ((sortChanged) && (old != null) && (!(old instanceof String))) {
/* 279 */       UIFunctionsManagerSWT.getUIFunctionsSWT().refreshIconBar();
/*     */     }
/*     */     
/* 282 */     long lastETA = 0L;
/* 283 */     long lastSpeed = 0L;
/* 284 */     TableRow row = cell.getTableRow();
/* 285 */     if (row != null) {
/* 286 */       if (this.showETA) {
/* 287 */         Object data = row.getData("lastETA");
/* 288 */         if ((data instanceof Number)) {
/* 289 */           lastETA = ((Number)data).longValue();
/*     */         }
/* 291 */         row.setData("lastETA", new Long(eta));
/*     */       }
/* 293 */       if (this.showSpeed) {
/* 294 */         Object data = row.getData("lastSpeed");
/* 295 */         if ((data instanceof Number)) {
/* 296 */           lastSpeed = ((Number)data).longValue();
/*     */         }
/* 298 */         row.setData("lastSpeed", new Long(speed));
/*     */       }
/*     */     }
/*     */     
/* 302 */     if ((!sortChanged) && ((lastETA != eta) || (lastSpeed != speed))) {
/* 303 */       cell.invalidate();
/*     */     }
/*     */   }
/*     */   
/*     */   public void cellPaint(GC gc, TableCellSWT cell)
/*     */   {
/* 309 */     Object ds = cell.getDataSource();
/* 310 */     if ((ds instanceof DiskManagerFileInfo)) {
/* 311 */       TableRowCore row = cell.getTableRowCore();
/* 312 */       if (row != null) {
/* 313 */         this.fileProgress.fillInfoProgressETA(row, gc, (DiskManagerFileInfo)ds, cell.getBounds());
/*     */       }
/*     */       
/* 316 */       return;
/*     */     }
/*     */     
/* 319 */     if (!(ds instanceof DownloadManager)) {
/* 320 */       return;
/*     */     }
/*     */     
/* 323 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/*     */     
/* 325 */     int percentDone = getPercentDone(ds);
/* 326 */     long eta = this.showETA ? getETA(cell) : 0L;
/*     */     
/*     */ 
/* 329 */     int newWidth = cell.getWidth();
/* 330 */     if (newWidth <= 0) {
/* 331 */       return;
/*     */     }
/* 333 */     int newHeight = cell.getHeight();
/*     */     
/* 335 */     Color fgFirst = gc.getForeground();
/*     */     
/* 337 */     Color fgOriginal = fgFirst;
/*     */     
/* 339 */     Rectangle cellBounds = cell.getBounds();
/*     */     
/* 341 */     int xStart = cellBounds.x;
/* 342 */     int yStart = cellBounds.y;
/*     */     
/* 344 */     int xRelProgressFillStart = 1;
/* 345 */     int yRelProgressFillStart = 1;
/* 346 */     int xRelProgressFillEnd = newWidth - xRelProgressFillStart - 1;
/* 347 */     int yRelProgressFillEnd = yRelProgressFillStart + 13;
/* 348 */     boolean showSecondLine = yRelProgressFillEnd + 10 < newHeight;
/*     */     
/* 350 */     if (xRelProgressFillEnd < 10) {
/* 351 */       return;
/*     */     }
/* 353 */     String sStatusLine = null;
/*     */     
/*     */ 
/*     */     Rectangle boundsImgBG;
/*     */     
/*     */     Rectangle boundsImgBG;
/*     */     
/* 360 */     if (!ImageLoader.isRealImage(this.imgBGTorrent)) {
/* 361 */       boundsImgBG = new Rectangle(0, 0, 0, 13);
/*     */     } else {
/* 363 */       boundsImgBG = this.imgBGTorrent.getBounds();
/*     */     }
/*     */     
/* 366 */     if (fontText == null) {
/* 367 */       fontText = FontUtils.getFontWithHeight(gc.getFont(), gc, boundsImgBG.height - 3);
/*     */     }
/*     */     
/*     */ 
/* 371 */     if (!showSecondLine) {
/* 372 */       yRelProgressFillStart = cellBounds.height / 2 - boundsImgBG.height / 2;
/*     */     }
/*     */     
/*     */ 
/* 376 */     yRelProgressFillEnd = yRelProgressFillStart + boundsImgBG.height;
/*     */     
/* 378 */     int progressWidth = newWidth - 1;
/* 379 */     gc.setForeground(this.cBorder);
/* 380 */     gc.drawRectangle(xStart + xRelProgressFillStart - 1, yStart + yRelProgressFillStart - 1, progressWidth + 1, boundsImgBG.height + 1);
/*     */     
/*     */ 
/* 383 */     int pctWidth = percentDone * progressWidth / 1000;
/* 384 */     gc.setBackground((percentDone == 1000) || (dm.isDownloadComplete(false)) ? this.cBGcd : this.cBGdl);
/* 385 */     gc.fillRectangle(xStart + xRelProgressFillStart, yStart + yRelProgressFillStart, pctWidth, boundsImgBG.height);
/*     */     
/* 387 */     if (progressWidth > pctWidth) {
/* 388 */       gc.setBackground(Colors.white);
/* 389 */       gc.fillRectangle(xStart + xRelProgressFillStart + pctWidth, yStart + yRelProgressFillStart, progressWidth - pctWidth, boundsImgBG.height);
/*     */     }
/*     */     
/*     */ 
/* 393 */     if (boundsImgBG.width > 0) {
/* 394 */       gc.drawImage(this.imgBGTorrent, 0, 0, boundsImgBG.width, boundsImgBG.height, xStart + xRelProgressFillStart, yStart + yRelProgressFillStart, progressWidth, boundsImgBG.height);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 399 */     if (sStatusLine == null) {
/* 400 */       if (dm.isUnauthorisedOnTracker()) {
/* 401 */         sStatusLine = dm.getTrackerStatus();
/*     */ 
/*     */       }
/* 404 */       else if ((this.showETA) && (eta > 0L)) {
/* 405 */         String sETA = ViewUtils.formatETA(eta, MyTorrentsView.progress_eta_absolute, this.cdf.getDateFormat());
/*     */         
/* 407 */         sStatusLine = MessageText.getString("MyTorrents.column.ColumnProgressETA.2ndLine", new String[] { sETA });
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 412 */         sStatusLine = DisplayFormatters.formatDownloadStatus(dm).toUpperCase();
/*     */       }
/*     */       
/*     */       int cursor_id;
/*     */       
/*     */       int cursor_id;
/* 418 */       if ((sStatusLine != null) && (!sStatusLine.contains("http://")))
/*     */       {
/* 420 */         dm.setUserData(CLICK_KEY, null);
/*     */         
/* 422 */         cursor_id = 0;
/*     */       }
/*     */       else
/*     */       {
/* 426 */         dm.setUserData(CLICK_KEY, sStatusLine);
/*     */         
/* 428 */         cursor_id = 21;
/*     */         
/* 430 */         if (!cell.getTableRow().isSelected())
/*     */         {
/* 432 */           fgFirst = Colors.blue;
/*     */         }
/*     */       }
/*     */       
/* 436 */       cell.setCursorID(cursor_id);
/*     */     }
/*     */     
/* 439 */     gc.setTextAntialias(1);
/* 440 */     gc.setFont(fontText);
/* 441 */     if ((showSecondLine) && (sStatusLine != null)) {
/* 442 */       gc.setForeground(fgFirst);
/* 443 */       boolean over = GCStringPrinter.printString(gc, sStatusLine, new Rectangle(cellBounds.x, yStart + yRelProgressFillEnd, cellBounds.width, newHeight - yRelProgressFillEnd), true, false, 16777216);
/*     */       
/*     */ 
/*     */ 
/* 447 */       cell.setToolTip(over ? sStatusLine : null);
/* 448 */       gc.setForeground(fgOriginal);
/*     */     }
/*     */     
/* 451 */     String sSpeed = "";
/* 452 */     if (this.showSpeed) {
/* 453 */       long lSpeed = getSpeed(ds);
/* 454 */       if (lSpeed > 0L) {
/* 455 */         sSpeed = " (" + DisplayFormatters.formatByteCountToKiBEtcPerSec(lSpeed, true) + ")";
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 461 */     String sPercent = DisplayFormatters.formatPercentFromThousands(percentDone);
/*     */     
/* 463 */     Rectangle area = new Rectangle(xStart + xRelProgressFillStart + 3, yStart + yRelProgressFillStart, xRelProgressFillEnd - xRelProgressFillStart - 6, yRelProgressFillEnd - yRelProgressFillStart);
/*     */     
/*     */ 
/* 466 */     GCStringPrinter sp = new GCStringPrinter(gc, sPercent + sSpeed, area, true, false, 16384);
/*     */     
/* 468 */     if (this.cTextDrop != null) {
/* 469 */       area.x += 1;
/* 470 */       area.y += 1;
/* 471 */       gc.setForeground(this.cTextDrop);
/* 472 */       sp.printString();
/* 473 */       area.x -= 1;
/* 474 */       area.y -= 1;
/*     */     }
/* 476 */     gc.setForeground(this.cText);
/* 477 */     sp.printString();
/* 478 */     Point pctExtent = sp.getCalculatedSize();
/*     */     
/* 480 */     area.width -= pctExtent.x + 3;
/* 481 */     area.x += pctExtent.x + 3;
/*     */     
/* 483 */     if ((!showSecondLine) && (sStatusLine != null)) {
/* 484 */       boolean fit = GCStringPrinter.printString(gc, sStatusLine, area.intersection(cellBounds), true, false, 131072);
/*     */       
/* 486 */       cell.setToolTip(fit ? null : sStatusLine);
/*     */     }
/*     */     
/* 489 */     gc.setFont(null);
/*     */   }
/*     */   
/*     */   private int getPercentDone(Object ds) {
/* 493 */     if ((ds instanceof DownloadManager))
/* 494 */       return ((DownloadManager)ds).getStats().getPercentDoneExcludingDND();
/* 495 */     if ((ds instanceof DiskManagerFileInfo)) {
/* 496 */       DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)ds;
/* 497 */       long length = fileInfo.getLength();
/* 498 */       if (length == 0L) {
/* 499 */         return 1000;
/*     */       }
/* 501 */       return (int)(fileInfo.getDownloaded() * 1000L / length);
/*     */     }
/* 503 */     return 0;
/*     */   }
/*     */   
/*     */   private long getETA(TableCell cell) {
/* 507 */     Object ds = cell.getDataSource();
/* 508 */     if ((ds instanceof DiskManagerFileInfo)) {
/* 509 */       return 0L;
/*     */     }
/* 511 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/*     */     
/* 513 */     long diff = SystemTime.getCurrentTime() - dm.getStats().getTimeStarted();
/* 514 */     if (diff > 30000L) {
/* 515 */       return dm.getStats().getSmoothedETA();
/*     */     }
/* 517 */     return 0L;
/*     */   }
/*     */   
/*     */   private int getState(TableCell cell) {
/* 521 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 522 */     if (dm == null) {
/* 523 */       return 100;
/*     */     }
/* 525 */     return dm.getState();
/*     */   }
/*     */   
/*     */   private boolean isStopped(TableCell cell) {
/* 529 */     int state = getState(cell);
/* 530 */     return (state == 75) || (state == 70) || (state == 65) || (state == 100);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private long getSpeed(Object ds)
/*     */   {
/* 537 */     if (!(ds instanceof DownloadManager)) {
/* 538 */       return 0L;
/*     */     }
/*     */     
/* 541 */     return ((DownloadManager)ds).getStats().getDataReceiveRate();
/*     */   }
/*     */   
/*     */   public EnhancedDownloadManager getEDM(DownloadManager dm) {
/* 545 */     DownloadManagerEnhancer dmEnhancer = DownloadManagerEnhancer.getSingleton();
/* 546 */     if (dmEnhancer == null) {
/* 547 */       return null;
/*     */     }
/* 549 */     return dmEnhancer.getEnhancedDownload(dm);
/*     */   }
/*     */   
/*     */   private void log(TableCell cell, String s) {
/* 553 */     System.out.println(((TableRowCore)cell.getTableRow()).getIndex() + ":" + System.currentTimeMillis() + ": " + s);
/*     */   }
/*     */   
/*     */   public void postConfigLoad()
/*     */   {
/* 558 */     super.postConfigLoad();
/*     */     
/* 560 */     Object oShowETA = getUserData("ColumnProgressETA.showETA");
/* 561 */     if (oShowETA == null) {
/* 562 */       this.showETA = false;
/* 563 */     } else if ((oShowETA instanceof Number)) {
/* 564 */       this.showETA = (((Number)oShowETA).intValue() == 1);
/*     */     }
/*     */     
/* 567 */     Object oShowSpeed = getUserData("ColumnProgressETA.showSpeed");
/* 568 */     if (oShowSpeed == null) {
/* 569 */       this.showSpeed = false;
/* 570 */     } else if ((oShowSpeed instanceof Number)) {
/* 571 */       this.showSpeed = (((Number)oShowSpeed).intValue() == 1);
/*     */     }
/*     */     
/* 574 */     this.cdf.update();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/torrent/ColumnProgressETA.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */