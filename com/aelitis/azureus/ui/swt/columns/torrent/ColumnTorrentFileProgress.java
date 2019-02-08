/*     */ package com.aelitis.azureus.ui.swt.columns.torrent;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinFactory;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinProperties;
/*     */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*     */ import com.aelitis.azureus.ui.swt.utils.FontUtils;
/*     */ import java.util.Arrays;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableRow;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.TorrentUtil;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter;
/*     */ import org.gudy.azureus2.ui.swt.views.FilesViewMenuUtil;
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
/*     */ public class ColumnTorrentFileProgress
/*     */ {
/*     */   private Image imgArrowButton;
/*     */   private Image imgPriHi;
/*     */   private Image imgPriNormal;
/*     */   private Image imgPriStopped;
/*     */   private Image imgBGfile;
/*     */   private Font progressFont;
/*     */   private Display display;
/*     */   private Color cBGdl;
/*     */   private Color cBGcd;
/*     */   private Color cBGskipped;
/*     */   
/*     */   public ColumnTorrentFileProgress(Display display)
/*     */   {
/*  68 */     this.display = display;
/*     */     
/*  70 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*  71 */     this.imgArrowButton = imageLoader.getImage("image.fileprogress.arrowbtn");
/*  72 */     this.imgPriHi = imageLoader.getImage("image.fileprogress.pri.hi");
/*  73 */     this.imgPriNormal = imageLoader.getImage("image.fileprogress.pri.normal");
/*  74 */     this.imgPriStopped = imageLoader.getImage("image.fileprogress.pri.stopped");
/*  75 */     this.imgBGfile = imageLoader.getImage("image.progress.bg.file");
/*     */     
/*  77 */     SWTSkinProperties skinProperties = SWTSkinFactory.getInstance().getSkinProperties();
/*  78 */     this.cBGdl = skinProperties.getColor("color.progress.bg.dl");
/*  79 */     if (this.cBGdl == null) {
/*  80 */       this.cBGdl = Colors.blues[9];
/*     */     }
/*  82 */     this.cBGcd = skinProperties.getColor("color.progress.bg.cd");
/*  83 */     if (this.cBGcd == null) {
/*  84 */       this.cBGcd = Colors.green;
/*     */     }
/*  86 */     this.cBGskipped = skinProperties.getColor("color.progress.bg.cd");
/*     */   }
/*     */   
/*     */   void fillInfoProgressETA(TableRowCore row, GC gc, DiskManagerFileInfo fileInfo, Rectangle cellArea)
/*     */   {
/*  91 */     long percent = 0L;
/*  92 */     long bytesDownloaded = fileInfo.getDownloaded();
/*  93 */     long length = fileInfo.getLength();
/*     */     
/*  95 */     if (this.cBGskipped == null) {
/*  96 */       this.cBGskipped = ColorCache.getSchemedColor(this.display, "#a6bdce");
/*     */     }
/*     */     
/*  99 */     if (bytesDownloaded < 0L)
/*     */     {
/* 101 */       return;
/*     */     }
/* 103 */     if (length == 0L)
/*     */     {
/* 105 */       percent = 1000L;
/*     */     }
/* 107 */     else if (fileInfo.getLength() != 0L)
/*     */     {
/* 109 */       percent = 1000L * bytesDownloaded / length;
/*     */     }
/*     */     
/* 112 */     gc.setAdvanced(true);
/* 113 */     gc.setTextAntialias(1);
/*     */     
/* 115 */     int BUTTON_WIDTH = this.imgArrowButton.getBounds().width;
/* 116 */     int HILOW_WIDTH = this.imgPriHi.getBounds().width;
/* 117 */     int BUTTON_HEIGHT = this.imgArrowButton.getBounds().height;
/* 118 */     int HILOW_HEIGHT = this.imgPriHi.getBounds().height;
/* 119 */     int PADDING_X = 12;
/* 120 */     int PADDING_TEXT = 5;
/* 121 */     int PROGRESS_HEIGHT = this.imgBGfile.getBounds().height;
/* 122 */     int PROGRESS_TO_HILOW_GAP = 3;
/* 123 */     int HILOW_TO_BUTTON_GAP = 3;
/*     */     
/* 125 */     cellArea.width -= 3;
/*     */     
/* 127 */     int ofsX = 12;
/* 128 */     int ofsY = cellArea.height / 2 - PROGRESS_HEIGHT / 2 - 1;
/* 129 */     int progressWidth = cellArea.width - ofsX * 2 - 3 - HILOW_WIDTH - 3 - BUTTON_WIDTH;
/*     */     
/*     */ 
/* 132 */     if (progressWidth > 0) {
/* 133 */       if (this.progressFont == null) {
/* 134 */         this.progressFont = FontUtils.getFontWithHeight(gc.getFont(), gc, PROGRESS_HEIGHT - 2);
/*     */       }
/*     */       
/* 137 */       gc.setFont(this.progressFont);
/* 138 */       gc.setForeground(ColorCache.getSchemedColor(this.display, fileInfo.isSkipped() ? "#95a6b2" : "#88acc1"));
/*     */       
/* 140 */       gc.drawRectangle(cellArea.x + ofsX, cellArea.y + ofsY - 1, progressWidth, PROGRESS_HEIGHT + 1);
/*     */       
/*     */ 
/* 143 */       int pctWidth = (int)(percent * (progressWidth - 1) / 1000L);
/* 144 */       gc.setBackground((percent == 1000L) || (fileInfo.getDownloadManager().isDownloadComplete(false)) ? this.cBGcd : fileInfo.isSkipped() ? this.cBGskipped : this.cBGdl);
/*     */       
/*     */ 
/* 147 */       gc.fillRectangle(cellArea.x + ofsX + 1, cellArea.y + ofsY, pctWidth, PROGRESS_HEIGHT);
/*     */       
/* 149 */       gc.setBackground(Colors.white);
/* 150 */       gc.fillRectangle(cellArea.x + ofsX + pctWidth + 1, cellArea.y + ofsY, progressWidth - pctWidth - 1, PROGRESS_HEIGHT);
/*     */       
/*     */ 
/* 153 */       Rectangle boundsImgBG = this.imgBGfile.getBounds();
/* 154 */       gc.drawImage(this.imgBGfile, boundsImgBG.x, boundsImgBG.y, boundsImgBG.width, boundsImgBG.height, cellArea.x + ofsX + 1, cellArea.y + ofsY, progressWidth - 1, PROGRESS_HEIGHT);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 159 */     Color colorText = ColorCache.getSchemedColor(this.display, fileInfo.isSkipped() ? "#556875" : "#2678b1");
/*     */     
/*     */ 
/* 162 */     Rectangle printBounds = new Rectangle(cellArea.x + 12 + 5, cellArea.y, progressWidth - 10, cellArea.height);
/*     */     
/*     */ 
/* 165 */     ofsY = cellArea.height / 2 - BUTTON_HEIGHT / 2 - 1;
/*     */     
/* 167 */     Rectangle buttonBounds = new Rectangle(cellArea.x + cellArea.width - BUTTON_WIDTH - 12, cellArea.y + ofsY, BUTTON_WIDTH, BUTTON_HEIGHT);
/*     */     
/*     */ 
/* 170 */     row.setData("buttonBounds", buttonBounds);
/*     */     
/* 172 */     ofsY = cellArea.height / 2 - HILOW_HEIGHT / 2 - 1;
/* 173 */     Rectangle hilowBounds = new Rectangle(buttonBounds.x - 3 - HILOW_WIDTH, cellArea.y + ofsY, HILOW_WIDTH, HILOW_HEIGHT);
/*     */     
/* 175 */     row.setData("hilowBounds", hilowBounds);
/*     */     
/* 177 */     gc.setForeground(colorText);
/*     */     
/* 179 */     String s = DisplayFormatters.formatPercentFromThousands((int)percent);
/* 180 */     GCStringPrinter.printString(gc, s, printBounds, true, false, 16384);
/*     */     
/*     */ 
/*     */ 
/* 184 */     String tmp = null;
/* 185 */     if (fileInfo.getDownloadManager().getState() == 70) {
/* 186 */       tmp = MessageText.getString("FileProgress.stopped");
/*     */     }
/*     */     else {
/* 189 */       int st = fileInfo.getStorageType();
/* 190 */       if (((st == 2) || (st == 4)) && (fileInfo.isSkipped()))
/*     */       {
/* 192 */         tmp = MessageText.getString("FileProgress.deleted");
/* 193 */       } else if (fileInfo.isSkipped()) {
/* 194 */         tmp = MessageText.getString("FileProgress.stopped");
/* 195 */       } else if (fileInfo.getPriority() > 0)
/*     */       {
/* 197 */         int pri = fileInfo.getPriority();
/*     */         
/* 199 */         if (pri > 1) {
/* 200 */           tmp = MessageText.getString("FileItem.high");
/* 201 */           tmp = tmp + " (" + pri + ")";
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 208 */     if (tmp != null) {
/* 209 */       GCStringPrinter.printString(gc, tmp.toUpperCase(), printBounds, false, false, 131072);
/*     */     }
/*     */     
/*     */ 
/* 213 */     gc.drawImage(this.imgArrowButton, buttonBounds.x, buttonBounds.y);
/* 214 */     Image imgPriority = fileInfo.getPriority() > 0 ? this.imgPriHi : fileInfo.isSkipped() ? this.imgPriStopped : this.imgPriNormal;
/*     */     
/* 216 */     gc.drawImage(imgPriority, hilowBounds.x, hilowBounds.y);
/*     */     
/*     */ 
/*     */ 
/* 220 */     hilowBounds.y -= cellArea.y;
/* 221 */     hilowBounds.x -= cellArea.x;
/* 222 */     buttonBounds.x -= cellArea.x;
/* 223 */     buttonBounds.y -= cellArea.y;
/*     */   }
/*     */   
/*     */   public void fileInfoMouseTrigger(TableCellMouseEvent event) {
/* 227 */     if (event.eventType != 0) {
/* 228 */       return;
/*     */     }
/* 230 */     final Object dataSource = ((TableRowCore)event.row).getDataSource(true);
/* 231 */     if ((dataSource instanceof DiskManagerFileInfo)) {
/* 232 */       final DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)dataSource;
/* 233 */       Rectangle hilowBounds = (Rectangle)event.row.getData("hilowBounds");
/* 234 */       if ((event.button == 1) && (hilowBounds != null) && (hilowBounds.contains(event.x, event.y)))
/*     */       {
/* 236 */         if (fileInfo.getPriority() > 0) {
/* 237 */           fileInfo.setPriority(0);
/*     */         } else {
/* 239 */           fileInfo.setPriority(1);
/*     */         }
/* 241 */         ((TableRowCore)event.row).redraw();
/*     */       }
/*     */       
/* 244 */       Rectangle buttonBounds = (Rectangle)event.row.getData("buttonBounds");
/*     */       
/* 246 */       if ((buttonBounds != null) && (buttonBounds.contains(event.x, event.y))) {
/* 247 */         Menu menu = new Menu(Display.getDefault().getActiveShell(), 8);
/*     */         
/* 249 */         MenuItem itemHigh = new MenuItem(menu, 16);
/* 250 */         Messages.setLanguageText(itemHigh, "priority.high");
/* 251 */         itemHigh.addListener(13, new Listener() {
/*     */           public void handleEvent(Event event) {
/* 253 */             FilesViewMenuUtil.changePriority(FilesViewMenuUtil.PRIORITY_HIGH, Arrays.asList(new DiskManagerFileInfo[] { fileInfo }));
/*     */ 
/*     */           }
/*     */           
/*     */ 
/* 258 */         });
/* 259 */         itemHigh.setSelection(fileInfo.getPriority() != 0);
/*     */         
/* 261 */         MenuItem itemNormal = new MenuItem(menu, 16);
/* 262 */         Messages.setLanguageText(itemNormal, "priority.normal");
/* 263 */         itemNormal.addListener(13, new Listener() {
/*     */           public void handleEvent(Event event) {
/* 265 */             FilesViewMenuUtil.changePriority(FilesViewMenuUtil.PRIORITY_NORMAL, Arrays.asList(new DiskManagerFileInfo[] { fileInfo }));
/*     */ 
/*     */           }
/*     */           
/*     */ 
/* 270 */         });
/* 271 */         itemNormal.setSelection(fileInfo.getPriority() == 0);
/*     */         
/* 273 */         new MenuItem(menu, 2);
/*     */         
/* 275 */         boolean canStart = (fileInfo.isSkipped()) || (fileInfo.getDownloadManager().getState() == 70);
/*     */         
/* 277 */         MenuItem itemStop = new MenuItem(menu, 8);
/* 278 */         Messages.setLanguageText(itemStop, "v3.MainWindow.button.stop");
/* 279 */         itemStop.addListener(13, new Listener() {
/*     */           public void handleEvent(Event event) {
/* 281 */             FilesViewMenuUtil.changePriority(FilesViewMenuUtil.PRIORITY_SKIPPED, Arrays.asList(new DiskManagerFileInfo[] { fileInfo }));
/*     */ 
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 287 */         });
/* 288 */         itemStop.setEnabled(!canStart);
/*     */         
/* 290 */         MenuItem itemStart = new MenuItem(menu, 8);
/* 291 */         Messages.setLanguageText(itemStart, "v3.MainWindow.button.start");
/* 292 */         itemStart.addListener(13, new Listener() {
/*     */           public void handleEvent(Event event) {
/* 294 */             if (fileInfo.getDownloadManager().getState() == 70) {
/* 295 */               TorrentUtil.queueDataSources(new Object[] { dataSource }, true);
/*     */             }
/*     */             
/* 298 */             FilesViewMenuUtil.changePriority(FilesViewMenuUtil.PRIORITY_NORMAL, Arrays.asList(new DiskManagerFileInfo[] { fileInfo }));
/*     */ 
/*     */           }
/*     */           
/*     */ 
/* 303 */         });
/* 304 */         itemStart.setEnabled(canStart);
/*     */         
/* 306 */         new MenuItem(menu, 2);
/*     */         
/* 308 */         MenuItem itemDelete = new MenuItem(menu, 8);
/* 309 */         Messages.setLanguageText(itemDelete, "v3.MainWindow.button.delete");
/* 310 */         itemDelete.addListener(13, new Listener() {
/*     */           public void handleEvent(Event event) {
/* 312 */             FilesViewMenuUtil.changePriority(FilesViewMenuUtil.PRIORITY_DELETE, Arrays.asList(new DiskManagerFileInfo[] { fileInfo }));
/*     */ 
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 318 */         });
/* 319 */         menu.setVisible(true);
/* 320 */         event.skipCoreFunctionality = true;
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/torrent/ColumnTorrentFileProgress.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */