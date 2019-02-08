/*     */ package com.aelitis.azureus.ui.swt.columns.torrent;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.PlatformTorrentUtils;
/*     */ import com.aelitis.azureus.ui.common.table.TableCellCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import com.aelitis.azureus.ui.swt.utils.TorrentUIUtilsV3;
/*     */ import com.aelitis.azureus.ui.swt.utils.TorrentUIUtilsV3.ContentImageLoadedListener;
/*     */ import java.io.File;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.ui.UIInputReceiver;
/*     */ import org.gudy.azureus2.plugins.ui.UIInputReceiverListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemFillListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellClipboardListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellDisposeListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellLightRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseMoveListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableRow;
/*     */ import org.gudy.azureus2.ui.swt.ImageRepository;
/*     */ import org.gudy.azureus2.ui.swt.SimpleTextEntryWindow;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.debug.ObfusticateCellText;
/*     */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter;
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
/*     */ public class ColumnThumbAndName
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellLightRefreshListener, ObfusticateCellText, TableCellDisposeListener, TableCellSWTPaintListener, TableCellClipboardListener, TableCellMouseMoveListener
/*     */ {
/*  71 */   public static final Class<?>[] DATASOURCE_TYPES = { Download.class, org.gudy.azureus2.plugins.disk.DiskManagerFileInfo.class };
/*     */   
/*     */ 
/*     */   public static final String COLUMN_ID = "name";
/*     */   
/*     */ 
/*     */   private static final String ID_EXPANDOHITAREA = "expandoHitArea";
/*     */   
/*     */   private static final String ID_EXPANDOHITAREASHOW = "expandoHitAreaShow";
/*     */   
/*  81 */   private static final boolean NEVER_SHOW_TWISTY = !COConfigurationManager.getBooleanParameter("Table.useTree");
/*     */   
/*     */   private boolean showIcon;
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info)
/*     */   {
/*  87 */     info.addCategories(new String[] { "essential", "content" });
/*     */     
/*     */ 
/*     */ 
/*  91 */     info.setProficiency((byte)0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ColumnThumbAndName(String sTableID)
/*     */   {
/*  99 */     super("name", 250, sTableID);
/* 100 */     setAlignment(1);
/* 101 */     addDataSourceTypes(DATASOURCE_TYPES);
/* 102 */     setObfustication(true);
/* 103 */     setRefreshInterval(-2);
/* 104 */     initializeAsGraphic(250);
/* 105 */     setMinWidth(100);
/*     */     
/* 107 */     TableContextMenuItem menuItem = addContextMenuItem("MyTorrentsView.menu.rename.displayed");
/* 108 */     menuItem.addMultiListener(new MenuItemListener() {
/*     */       public void selected(MenuItem menu, Object target) {
/* 110 */         if (target == null) {
/* 111 */           return;
/*     */         }
/* 113 */         Object[] o = (Object[])target;
/* 114 */         for (Object object : o) {
/* 115 */           if ((object instanceof TableRowCore)) {
/* 116 */             TableRowCore row = (TableRowCore)object;
/* 117 */             object = row.getDataSource(true);
/*     */           }
/* 119 */           if ((object instanceof DownloadManager)) {
/* 120 */             final DownloadManager dm = (DownloadManager)object;
/* 121 */             String msg_key_prefix = "MyTorrentsView.menu.rename.displayed.enter.";
/*     */             
/* 123 */             SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow(msg_key_prefix + "title", msg_key_prefix + "message");
/*     */             
/* 125 */             entryWindow.setPreenteredText(dm.getDisplayName(), false);
/* 126 */             entryWindow.maintainWhitespace(true);
/* 127 */             entryWindow.prompt(new UIInputReceiverListener() {
/*     */               public void UIInputReceiverClosed(UIInputReceiver entryWindow) {
/* 129 */                 if (!entryWindow.hasSubmittedInput()) {
/* 130 */                   return;
/*     */                 }
/* 132 */                 String value = entryWindow.getSubmittedInput();
/* 133 */                 if ((value != null) && (value.length() > 0)) {
/* 134 */                   dm.getDownloadState().setDisplayName(value);
/*     */                 }
/*     */                 
/*     */               }
/*     */             });
/*     */           }
/*     */         }
/*     */       }
/* 142 */     });
/* 143 */     TableContextMenuItem menuShowIcon = addContextMenuItem("ConfigView.section.style.showProgramIcon", 1);
/*     */     
/* 145 */     menuShowIcon.setStyle(2);
/* 146 */     menuShowIcon.addFillListener(new MenuItemFillListener() {
/*     */       public void menuWillBeShown(MenuItem menu, Object data) {
/* 148 */         menu.setData(Boolean.valueOf(ColumnThumbAndName.this.showIcon));
/*     */       }
/* 150 */     });
/* 151 */     final String CFG_SHOWPROGRAMICON = "NameColumn.showProgramIcon." + getTableID();
/*     */     
/* 153 */     menuShowIcon.addMultiListener(new MenuItemListener() {
/*     */       public void selected(MenuItem menu, Object target) {
/* 155 */         COConfigurationManager.setParameter(CFG_SHOWPROGRAMICON, ((Boolean)menu.getData()).booleanValue());
/*     */       }
/*     */       
/*     */ 
/* 159 */     });
/* 160 */     COConfigurationManager.addAndFireParameterListener(CFG_SHOWPROGRAMICON, new ParameterListener()
/*     */     {
/*     */       public void parameterChanged(String parameterName) {
/* 163 */         ColumnThumbAndName.this.setShowIcon(COConfigurationManager.getBooleanParameter(CFG_SHOWPROGRAMICON, COConfigurationManager.getBooleanParameter("NameColumn.showProgramIcon")));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public void reset()
/*     */   {
/* 171 */     super.reset();
/*     */     
/* 173 */     COConfigurationManager.removeParameter("NameColumn.showProgramIcon." + getTableID());
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell)
/*     */   {
/* 178 */     refresh(cell, false);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell, boolean sortOnlyRefresh) {
/* 182 */     String name = null;
/* 183 */     Object ds = cell.getDataSource();
/* 184 */     if ((ds instanceof org.gudy.azureus2.core3.disk.DiskManagerFileInfo)) {
/* 185 */       org.gudy.azureus2.core3.disk.DiskManagerFileInfo fileInfo = (org.gudy.azureus2.core3.disk.DiskManagerFileInfo)ds;
/* 186 */       if ((fileInfo.isSkipped()) && ((fileInfo.getStorageType() == 2) || (fileInfo.getStorageType() == 4)))
/*     */       {
/* 188 */         TableRowCore row = (TableRowCore)cell.getTableRow();
/* 189 */         if (row != null) {
/* 190 */           row.getParentRowCore().removeSubRow(ds);
/*     */         }
/*     */       }
/* 193 */       return;
/*     */     }
/* 195 */     DownloadManager dm = (DownloadManager)ds;
/* 196 */     if (dm != null) {
/* 197 */       name = dm.getDisplayName();
/*     */     }
/* 199 */     if (name == null) {
/* 200 */       name = "";
/*     */     }
/*     */     
/* 203 */     cell.setSortValue(name);
/*     */   }
/*     */   
/*     */   public void cellPaint(GC gc, final TableCellSWT cell) {
/* 207 */     Object ds = cell.getDataSource();
/* 208 */     if ((ds instanceof org.gudy.azureus2.core3.disk.DiskManagerFileInfo)) {
/* 209 */       cellPaintFileInfo(gc, cell, (org.gudy.azureus2.core3.disk.DiskManagerFileInfo)ds);
/* 210 */       return;
/*     */     }
/*     */     
/* 213 */     Rectangle cellBounds = cell.getBounds();
/*     */     
/* 215 */     int textX = cellBounds.x;
/*     */     
/* 217 */     TableRowCore rowCore = cell.getTableRowCore();
/* 218 */     if (rowCore != null) {
/* 219 */       int numSubItems = rowCore.getSubItemCount();
/* 220 */       int paddingX = 3;
/* 221 */       int width = 7;
/*     */       
/*     */       boolean show_twisty;
/*     */       boolean show_twisty;
/* 225 */       if (NEVER_SHOW_TWISTY)
/*     */       {
/* 227 */         show_twisty = false;
/*     */       } else { boolean show_twisty;
/* 229 */         if (numSubItems > 1)
/*     */         {
/* 231 */           show_twisty = true;
/*     */         }
/*     */         else {
/* 234 */           Boolean show = (Boolean)rowCore.getData("expandoHitAreaShow");
/*     */           
/* 236 */           if (show == null)
/*     */           {
/* 238 */             DownloadManager dm = (DownloadManager)ds;
/*     */             
/* 240 */             TOTorrent torrent = dm.getTorrent();
/*     */             
/* 242 */             boolean show_twisty = (torrent != null) && (!dm.getTorrent().isSimpleTorrent());
/*     */             
/* 244 */             rowCore.setData("expandoHitAreaShow", Boolean.valueOf(show_twisty));
/*     */           }
/*     */           else {
/* 247 */             show_twisty = show.booleanValue();
/*     */           }
/*     */         }
/*     */       }
/* 251 */       if (show_twisty) {
/* 252 */         int middleY = cellBounds.y + cellBounds.height / 2 - 1;
/* 253 */         int startX = cellBounds.x + paddingX;
/* 254 */         int halfHeight = 2;
/* 255 */         Color bg = gc.getBackground();
/* 256 */         gc.setBackground(gc.getForeground());
/* 257 */         gc.setAntialias(1);
/* 258 */         gc.setAdvanced(true);
/* 259 */         if (rowCore.isExpanded()) {
/* 260 */           gc.fillPolygon(new int[] { startX, middleY - halfHeight, startX + width, middleY - halfHeight, startX + width / 2, middleY + halfHeight * 2 + 1 });
/*     */ 
/*     */ 
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/*     */ 
/* 269 */           gc.fillPolygon(new int[] { startX, middleY - halfHeight, startX + width, middleY + halfHeight, startX, middleY + halfHeight * 2 + 1 });
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 278 */         gc.setBackground(bg);
/* 279 */         Rectangle hitArea = new Rectangle(paddingX, middleY - halfHeight - cellBounds.y, width, halfHeight * 4 + 1);
/*     */         
/* 281 */         rowCore.setData("expandoHitArea", hitArea);
/*     */       }
/*     */       
/* 284 */       if (!NEVER_SHOW_TWISTY) {
/* 285 */         cellBounds.x += paddingX * 2 + width;
/* 286 */         cellBounds.width -= paddingX * 2 + width;
/*     */       }
/*     */     }
/*     */     
/* 290 */     if (!this.showIcon) {
/* 291 */       cellBounds.x += 2;
/* 292 */       cellBounds.width -= 4;
/* 293 */       cellPaintName(cell, gc, cellBounds, cellBounds.x);
/* 294 */       return;
/*     */     }
/*     */     
/* 297 */     Image[] imgThumbnail = TorrentUIUtilsV3.getContentImage(ds, cellBounds.height >= 20, new TorrentUIUtilsV3.ContentImageLoadedListener()
/*     */     {
/*     */       public void contentImageLoaded(Image image, boolean wasReturned) {
/* 300 */         if (!wasReturned)
/*     */         {
/*     */ 
/* 303 */           cell.invalidate();
/*     */         }
/*     */       }
/*     */     });
/*     */     
/* 308 */     if ((imgThumbnail != null) && (ImageLoader.isRealImage(imgThumbnail[0]))) {
/*     */       try
/*     */       {
/* 311 */         if (cellBounds.height > 30) {
/* 312 */           cellBounds.y += 1;
/* 313 */           cellBounds.height -= 3;
/*     */         }
/* 315 */         Rectangle imgBounds = imgThumbnail[0].getBounds();
/*     */         int dstWidth;
/*     */         int dstWidth;
/*     */         int dstHeight;
/* 319 */         if (imgBounds.height > cellBounds.height) {
/* 320 */           int dstHeight = cellBounds.height;
/* 321 */           dstWidth = imgBounds.width * cellBounds.height / imgBounds.height; } else { int dstHeight;
/* 322 */           if (imgBounds.width > cellBounds.width) {
/* 323 */             int dstWidth = cellBounds.width - 4;
/* 324 */             dstHeight = imgBounds.height * cellBounds.width / imgBounds.width;
/*     */           } else {
/* 326 */             dstWidth = imgBounds.width;
/* 327 */             dstHeight = imgBounds.height;
/*     */           }
/*     */         }
/* 330 */         if (cellBounds.height <= 18) {
/* 331 */           dstWidth = Math.min(dstWidth, cellBounds.height);
/* 332 */           dstHeight = Math.min(dstHeight, cellBounds.height);
/* 333 */           if (imgBounds.width > 16) {
/* 334 */             cellBounds.y += 1;
/* 335 */             dstHeight -= 2;
/*     */           }
/*     */         }
/*     */         try
/*     */         {
/* 340 */           gc.setAdvanced(true);
/* 341 */           gc.setInterpolation(2);
/*     */         }
/*     */         catch (Exception e) {}
/* 344 */         int x = cellBounds.x;
/* 345 */         textX = x + dstWidth + 3;
/* 346 */         int minWidth = dstHeight * 7 / 4;
/* 347 */         int imgPad = 0;
/* 348 */         if ((dstHeight > 25) && 
/* 349 */           (dstWidth < minWidth)) {
/* 350 */           imgPad = (minWidth - dstWidth + 1) / 2;
/* 351 */           x = cellBounds.x + imgPad;
/* 352 */           textX = cellBounds.x + minWidth + 3;
/*     */         }
/*     */         
/* 355 */         if ((cellBounds.width - dstWidth - imgPad * 2 < 100) && (dstHeight > 18)) {
/* 356 */           dstWidth = Math.min(32, dstHeight);
/* 357 */           x = cellBounds.x + (32 - dstWidth + 1) / 2;
/* 358 */           dstHeight = imgBounds.height * dstWidth / imgBounds.width;
/* 359 */           textX = cellBounds.x + dstWidth + 3;
/*     */         }
/* 361 */         int y = cellBounds.y + (cellBounds.height - dstHeight + 1) / 2;
/* 362 */         if ((dstWidth > 0) && (dstHeight > 0) && (!imgBounds.isEmpty()))
/*     */         {
/* 364 */           Rectangle lastClipping = gc.getClipping();
/*     */           try {
/* 366 */             Utils.setClipping(gc, cellBounds);
/*     */             
/* 368 */             boolean hack_adv = (Constants.isWindows8OrHigher) && (gc.getAdvanced());
/*     */             
/* 370 */             if (hack_adv)
/*     */             {
/* 372 */               gc.setAdvanced(false);
/*     */             }
/*     */             
/* 375 */             for (int i = 0; i < imgThumbnail.length; i++) {
/* 376 */               Image image = imgThumbnail[i];
/* 377 */               if ((image != null) && (!image.isDisposed()))
/*     */               {
/*     */ 
/* 380 */                 Rectangle srcBounds = image.getBounds();
/* 381 */                 if (i == 0) {
/* 382 */                   int w = dstWidth;
/* 383 */                   int h = dstHeight;
/* 384 */                   if (imgThumbnail.length > 1) {
/* 385 */                     w = w * 9 / 10;
/* 386 */                     h = h * 9 / 10;
/*     */                   }
/* 388 */                   gc.drawImage(image, srcBounds.x, srcBounds.y, srcBounds.width, srcBounds.height, x, y, w, h);
/*     */                 }
/*     */                 else {
/* 391 */                   int w = dstWidth * 3 / 8;
/* 392 */                   int h = dstHeight * 3 / 8;
/* 393 */                   gc.drawImage(image, srcBounds.x, srcBounds.y, srcBounds.width, srcBounds.height, x + dstWidth - w, y + dstHeight - h, w, h);
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/* 398 */             if (hack_adv) {
/* 399 */               gc.setAdvanced(true);
/*     */             }
/*     */           } catch (Exception e) {
/* 402 */             Debug.out(e);
/*     */           } finally {
/* 404 */             Utils.setClipping(gc, lastClipping);
/*     */           }
/*     */         }
/*     */         
/* 408 */         TorrentUIUtilsV3.releaseContentImage(ds);
/*     */       } catch (Throwable t) {
/* 410 */         Debug.out(t);
/*     */       }
/*     */     }
/*     */     
/* 414 */     cellPaintName(cell, gc, cellBounds, textX);
/*     */   }
/*     */   
/*     */   private void cellPaintFileInfo(GC gc, TableCellSWT cell, org.gudy.azureus2.core3.disk.DiskManagerFileInfo fileInfo)
/*     */   {
/* 419 */     Rectangle cellBounds = cell.getBounds();
/*     */     
/* 421 */     int padding = 5 + cellBounds.height;
/* 422 */     cellBounds.x += padding;
/* 423 */     cellBounds.width -= padding;
/*     */     
/* 425 */     int textX = cellBounds.x;
/*     */     
/* 427 */     Image[] imgThumbnail = { ImageRepository.getPathIcon(fileInfo.getFile(true).getPath(), cellBounds.height >= 20 ? 1 : false, false) };
/*     */     
/*     */ 
/* 430 */     if ((imgThumbnail != null) && (ImageLoader.isRealImage(imgThumbnail[0]))) {
/*     */       try
/*     */       {
/* 433 */         if (cellBounds.height > 30) {
/* 434 */           cellBounds.y += 1;
/* 435 */           cellBounds.height -= 3;
/*     */         }
/* 437 */         Rectangle imgBounds = imgThumbnail[0].getBounds();
/*     */         int dstWidth;
/*     */         int dstWidth;
/*     */         int dstHeight;
/* 441 */         if (imgBounds.height > cellBounds.height) {
/* 442 */           int dstHeight = cellBounds.height;
/* 443 */           dstWidth = imgBounds.width * cellBounds.height / imgBounds.height; } else { int dstHeight;
/* 444 */           if (imgBounds.width > cellBounds.width) {
/* 445 */             int dstWidth = cellBounds.width - 4;
/* 446 */             dstHeight = imgBounds.height * cellBounds.width / imgBounds.width;
/*     */           } else {
/* 448 */             dstWidth = imgBounds.width;
/* 449 */             dstHeight = imgBounds.height;
/*     */           }
/*     */         }
/* 452 */         if (cellBounds.height <= 18) {
/* 453 */           dstWidth = Math.min(dstWidth, cellBounds.height);
/* 454 */           dstHeight = Math.min(dstHeight, cellBounds.height);
/* 455 */           if (imgBounds.width > 16) {
/* 456 */             cellBounds.y += 1;
/* 457 */             dstHeight -= 2;
/*     */           }
/*     */         }
/*     */         try
/*     */         {
/* 462 */           gc.setAdvanced(true);
/* 463 */           gc.setInterpolation(2);
/*     */         }
/*     */         catch (Exception e) {}
/* 466 */         int x = cellBounds.x;
/* 467 */         textX = x + dstWidth + 3;
/* 468 */         int minWidth = dstHeight;
/* 469 */         int imgPad = 0;
/* 470 */         if ((dstHeight > 25) && 
/* 471 */           (dstWidth < minWidth)) {
/* 472 */           imgPad = (minWidth - dstWidth + 1) / 2;
/* 473 */           x = cellBounds.x + imgPad;
/* 474 */           textX = cellBounds.x + minWidth + 3;
/*     */         }
/*     */         
/* 477 */         if ((cellBounds.width - dstWidth - imgPad * 2 < 100) && (dstHeight > 18)) {
/* 478 */           dstWidth = Math.min(32, dstHeight);
/* 479 */           x = cellBounds.x + (32 - dstWidth + 1) / 2;
/* 480 */           dstHeight = imgBounds.height * dstWidth / imgBounds.width;
/* 481 */           textX = cellBounds.x + dstWidth + 3;
/*     */         }
/* 483 */         int y = cellBounds.y + (cellBounds.height - dstHeight + 1) / 2;
/* 484 */         if ((dstWidth > 0) && (dstHeight > 0) && (!imgBounds.isEmpty()))
/*     */         {
/* 486 */           Rectangle lastClipping = gc.getClipping();
/*     */           try {
/* 488 */             Utils.setClipping(gc, cellBounds);
/*     */             
/* 490 */             boolean hack_adv = (Constants.isWindows8OrHigher) && (gc.getAdvanced());
/*     */             
/* 492 */             if (hack_adv)
/*     */             {
/* 494 */               gc.setAdvanced(false);
/*     */             }
/*     */             
/* 497 */             for (int i = 0; i < imgThumbnail.length; i++) {
/* 498 */               Image image = imgThumbnail[i];
/* 499 */               if ((image != null) && (!image.isDisposed()))
/*     */               {
/*     */ 
/* 502 */                 Rectangle srcBounds = image.getBounds();
/* 503 */                 if (i == 0) {
/* 504 */                   int w = dstWidth;
/* 505 */                   int h = dstHeight;
/* 506 */                   if (imgThumbnail.length > 1) {
/* 507 */                     w = w * 9 / 10;
/* 508 */                     h = h * 9 / 10;
/*     */                   }
/* 510 */                   gc.drawImage(image, srcBounds.x, srcBounds.y, srcBounds.width, srcBounds.height, x, y, w, h);
/*     */                 }
/*     */                 else {
/* 513 */                   int w = dstWidth * 3 / 8;
/* 514 */                   int h = dstHeight * 3 / 8;
/* 515 */                   gc.drawImage(image, srcBounds.x, srcBounds.y, srcBounds.width, srcBounds.height, x + dstWidth - w, y + dstHeight - h, w, h);
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/* 520 */             if (hack_adv) {
/* 521 */               gc.setAdvanced(true);
/*     */             }
/*     */           } catch (Exception e) {
/* 524 */             Debug.out(e);
/*     */           } finally {
/* 526 */             Utils.setClipping(gc, lastClipping);
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable t) {
/* 531 */         Debug.out(t);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 537 */     String prefix = fileInfo.getDownloadManager().getSaveLocation().toString();
/* 538 */     String s = fileInfo.getFile(true).toString();
/* 539 */     if (s.startsWith(prefix)) {
/* 540 */       s = s.substring(prefix.length() + 1);
/*     */     }
/* 542 */     if (fileInfo.isSkipped())
/*     */     {
/* 544 */       String dnd_sf = fileInfo.getDownloadManager().getDownloadState().getAttribute("dnd_sf");
/*     */       
/* 546 */       if (dnd_sf != null)
/*     */       {
/* 548 */         dnd_sf = dnd_sf.trim();
/*     */         
/* 550 */         if (dnd_sf.length() > 0)
/*     */         {
/* 552 */           dnd_sf = dnd_sf + File.separatorChar;
/*     */           
/* 554 */           int pos = s.indexOf(dnd_sf);
/*     */           
/* 556 */           if (pos != -1)
/*     */           {
/* 558 */             s = s.substring(0, pos) + s.substring(pos + dnd_sf.length());
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 564 */     cellBounds.width -= textX - cellBounds.x;
/* 565 */     cellBounds.x = textX;
/*     */     
/* 567 */     boolean over = GCStringPrinter.printString(gc, s, cellBounds, true, false, 16448);
/*     */     
/* 569 */     cell.setToolTip(over ? null : s);
/*     */   }
/*     */   
/*     */   private void cellPaintName(TableCell cell, GC gc, Rectangle cellBounds, int textX)
/*     */   {
/* 574 */     String name = null;
/* 575 */     Object ds = cell.getDataSource();
/* 576 */     if ((ds instanceof org.gudy.azureus2.core3.disk.DiskManagerFileInfo)) {
/* 577 */       return;
/*     */     }
/* 579 */     DownloadManager dm = (DownloadManager)ds;
/* 580 */     if (dm != null)
/* 581 */       name = dm.getDisplayName();
/* 582 */     if (name == null) {
/* 583 */       name = "";
/*     */     }
/* 585 */     boolean over = GCStringPrinter.printString(gc, name, new Rectangle(textX, cellBounds.y, cellBounds.x + cellBounds.width - textX, cellBounds.height), true, true, getTableID().endsWith(".big") ? 64 : 0);
/*     */     
/*     */ 
/*     */ 
/* 589 */     String tooltip = over ? "" : name;
/*     */     
/* 591 */     if (dm != null) {
/*     */       try {
/* 593 */         String desc = PlatformTorrentUtils.getContentDescription(dm.getTorrent());
/*     */         
/* 595 */         if ((desc != null) && (desc.length() > 0)) {
/* 596 */           tooltip = tooltip + (tooltip.length() == 0 ? "" : "\r\n") + desc;
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/* 601 */     cell.setToolTip(tooltip.length() == 0 ? null : tooltip);
/*     */   }
/*     */   
/*     */   public String getObfusticatedText(TableCell cell) {
/* 605 */     String name = null;
/* 606 */     Object ds = cell.getDataSource();
/* 607 */     if ((ds instanceof org.gudy.azureus2.core3.disk.DiskManagerFileInfo)) {
/* 608 */       org.gudy.azureus2.core3.disk.DiskManagerFileInfo fileInfo = (org.gudy.azureus2.core3.disk.DiskManagerFileInfo)cell.getDataSource();
/* 609 */       return fileInfo.getIndex() + ": " + Debug.secretFileName(fileInfo.getFile(true).getName());
/*     */     }
/* 611 */     DownloadManager dm = (DownloadManager)ds;
/* 612 */     if (dm != null) {
/* 613 */       name = dm.toString();
/* 614 */       int i = name.indexOf('#');
/* 615 */       if (i > 0) {
/* 616 */         name = name.substring(i + 1);
/*     */       }
/*     */     }
/*     */     
/* 620 */     if (name == null)
/* 621 */       name = "";
/* 622 */     return name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void dispose(TableCell cell) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void setShowIcon(boolean showIcon)
/*     */   {
/* 633 */     this.showIcon = showIcon;
/* 634 */     invalidateCells();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isShowIcon()
/*     */   {
/* 641 */     return this.showIcon;
/*     */   }
/*     */   
/*     */   public String getClipboardText(TableCell cell) {
/* 645 */     String name = null;
/* 646 */     Object ds = cell.getDataSource();
/* 647 */     if ((ds instanceof org.gudy.azureus2.core3.disk.DiskManagerFileInfo)) {
/* 648 */       return null;
/*     */     }
/* 650 */     DownloadManager dm = (DownloadManager)ds;
/* 651 */     if (dm != null)
/* 652 */       name = dm.getDisplayName();
/* 653 */     if (name == null)
/* 654 */       name = "";
/* 655 */     return name;
/*     */   }
/*     */   
/*     */   public void cellMouseTrigger(TableCellMouseEvent event) {
/* 659 */     if ((event.eventType == 3) || (event.eventType == 0))
/*     */     {
/* 661 */       TableRow row = event.cell.getTableRow();
/* 662 */       if (row == null) {
/* 663 */         return;
/*     */       }
/* 665 */       Object data = row.getData("expandoHitArea");
/* 666 */       if ((data instanceof Rectangle)) {
/* 667 */         Rectangle hitArea = (Rectangle)data;
/* 668 */         boolean inExpando = hitArea.contains(event.x, event.y);
/*     */         
/* 670 */         if (event.eventType == 3) {
/* 671 */           ((TableCellCore)event.cell).setCursorID(inExpando ? 21 : 0);
/*     */         }
/* 673 */         else if ((inExpando) && 
/* 674 */           ((row instanceof TableRowCore))) {
/* 675 */           TableRowCore rowCore = (TableRowCore)row;
/* 676 */           rowCore.setExpanded(!rowCore.isExpanded());
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/torrent/ColumnThumbAndName.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */