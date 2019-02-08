/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.gudy.azureus2.core3.disk.DiskManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.download.DownloadTypeIncomplete;
/*     */ import org.gudy.azureus2.plugins.ui.Graphic;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellDisposeListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.SWTThread;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTGraphic;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTGraphicImpl;
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
/*     */ public class PiecesItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellAddedListener, TableCellRefreshListener, TableCellDisposeListener
/*     */ {
/*  54 */   public static final Class DATASOURCE_TYPE = DownloadTypeIncomplete.class;
/*     */   
/*     */ 
/*     */   private static final int INDEX_COLOR_NONEAVAIL = 10;
/*     */   
/*     */ 
/*     */   private static final int borderHorizontalSize = 1;
/*     */   
/*     */   private static final int borderVerticalSize = 1;
/*     */   
/*     */   private static final int borderSplit = 1;
/*     */   
/*     */   private static final int completionHeight = 2;
/*     */   
/*     */   public static final String COLUMN_ID = "pieces";
/*     */   
/*  70 */   private int marginHeight = -1;
/*     */   
/*     */   public PiecesItem(String sTableID)
/*     */   {
/*  74 */     this(sTableID, -1);
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  78 */     info.addCategories(new String[] { "content", "progress" });
/*     */     
/*     */ 
/*     */ 
/*  82 */     info.setProficiency((byte)1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PiecesItem(String sTableID, int marginHeight)
/*     */   {
/*  91 */     super(DATASOURCE_TYPE, "pieces", 1, 100, sTableID);
/*  92 */     this.marginHeight = marginHeight;
/*  93 */     initializeAsGraphic(-1, 100);
/*  94 */     setMinWidth(100);
/*     */   }
/*     */   
/*     */   public void cellAdded(TableCell cell) {
/*  98 */     if (this.marginHeight != -1) {
/*  99 */       cell.setMarginHeight(this.marginHeight);
/*     */     }
/* 101 */     cell.setFillCell(true);
/*     */   }
/*     */   
/*     */   public void dispose(TableCell cell)
/*     */   {
/* 106 */     DownloadManager infoObj = (DownloadManager)cell.getDataSource();
/* 107 */     if (infoObj == null) {
/* 108 */       return;
/*     */     }
/* 110 */     Image img = (Image)infoObj.getUserData("PiecesImage");
/* 111 */     if ((img != null) && (!img.isDisposed())) {
/* 112 */       img.dispose();
/*     */     }
/* 114 */     infoObj.setUserData("PiecesImageBuffer", null);
/* 115 */     infoObj.setUserData("PiecesImage", null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void refresh(TableCell cell)
/*     */   {
/* 125 */     DownloadManager infoObj = (DownloadManager)cell.getDataSource();
/* 126 */     long lCompleted = infoObj == null ? 0L : infoObj.getStats().getCompleted();
/*     */     
/* 128 */     boolean bForce = (infoObj != null) && (infoObj.getUserData("PiecesImage") == null);
/*     */     
/* 130 */     if ((!cell.setSortValue(lCompleted)) && (cell.isValid()) && (!bForce)) {
/* 131 */       return;
/*     */     }
/*     */     
/* 134 */     if (infoObj == null) {
/* 135 */       return;
/*     */     }
/*     */     
/* 138 */     int newWidth = cell.getWidth();
/* 139 */     if (newWidth <= 0)
/* 140 */       return;
/* 141 */     int newHeight = cell.getHeight();
/*     */     
/* 143 */     int x0 = 1;
/* 144 */     int x1 = newWidth - 1 - 1;
/* 145 */     int y0 = 4;
/* 146 */     int y1 = newHeight - 1 - 1;
/* 147 */     int drawWidth = x1 - x0 + 1;
/* 148 */     if ((drawWidth < 10) || (y1 < 3))
/* 149 */       return;
/* 150 */     boolean bImageBufferValid = true;
/* 151 */     int[] imageBuffer = (int[])infoObj.getUserData("PiecesImageBuffer");
/* 152 */     if ((imageBuffer == null) || (imageBuffer.length != drawWidth)) {
/* 153 */       imageBuffer = new int[drawWidth];
/* 154 */       bImageBufferValid = false;
/*     */     }
/*     */     
/* 157 */     Image image = (Image)infoObj.getUserData("PiecesImage");
/*     */     
/*     */     boolean bImageChanged;
/*     */     boolean bImageChanged;
/* 161 */     if ((image == null) || (image.isDisposed())) {
/* 162 */       bImageChanged = true;
/*     */     } else {
/* 164 */       Rectangle imageBounds = image.getBounds();
/* 165 */       bImageChanged = (imageBounds.width != newWidth) || (imageBounds.height != newHeight);
/*     */     }
/*     */     GC gcImage;
/* 168 */     if (bImageChanged) {
/* 169 */       if ((image != null) && (!image.isDisposed())) {
/* 170 */         image.dispose();
/*     */       }
/* 172 */       image = new Image(SWTThread.getInstance().getDisplay(), newWidth, newHeight);
/*     */       
/* 174 */       Rectangle imageBounds = image.getBounds();
/* 175 */       bImageBufferValid = false;
/*     */       
/*     */ 
/* 178 */       GC gcImage = new GC(image);
/* 179 */       gcImage.setForeground(Colors.grey);
/*     */       
/*     */ 
/* 182 */       gcImage.drawRectangle(0, 0, newWidth - 1, newHeight - 1);
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
/* 193 */       gcImage.setForeground(Colors.white);
/* 194 */       gcImage.drawLine(x0, 3, x1, 3);
/*     */     }
/*     */     else
/*     */     {
/* 198 */       gcImage = new GC(image);
/*     */     }
/*     */     
/* 201 */     DiskManager disk_manager = infoObj.getDiskManager();
/*     */     
/* 203 */     DiskManagerPiece[] pieces = disk_manager == null ? null : disk_manager.getPieces();
/*     */     
/*     */ 
/* 206 */     int nbPieces = infoObj.getNbPieces();
/*     */     
/*     */     try
/*     */     {
/* 210 */       int nbComplete = 0;
/*     */       
/* 212 */       int a1 = 0;
/* 213 */       for (int i = 0; i < drawWidth; i++) { int a0;
/* 214 */         if (i == 0)
/*     */         {
/* 216 */           int a0 = 0;
/* 217 */           a1 = nbPieces / drawWidth;
/* 218 */           if (a1 == 0) {
/* 219 */             a1 = 1;
/*     */           }
/*     */         } else {
/* 222 */           a0 = a1;
/* 223 */           a1 = (i + 1) * nbPieces / drawWidth;
/*     */         }
/*     */         
/*     */         int index;
/*     */         int index;
/* 228 */         if (a1 <= a0) {
/* 229 */           index = imageBuffer[(i - 1)];
/*     */         } else {
/* 231 */           int nbAvailable = 0;
/* 232 */           for (int j = a0; j < a1; j++)
/* 233 */             if ((pieces != null) && (pieces[j].isDone()))
/* 234 */               nbAvailable++;
/* 235 */           nbComplete += nbAvailable;
/* 236 */           index = nbAvailable * 9 / (a1 - a0);
/*     */         }
/*     */         
/*     */ 
/* 240 */         if ((!bImageBufferValid) || (imageBuffer[i] != index)) {
/* 241 */           imageBuffer[i] = index;
/* 242 */           bImageChanged = true;
/* 243 */           gcImage.setForeground(index == 10 ? Colors.red : Colors.blues[index]);
/*     */           
/* 245 */           gcImage.drawLine(i + x0, y0, i + x0, y1);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 252 */       int limit = nbPieces == 0 ? 0 : drawWidth * nbComplete / nbPieces;
/*     */       
/* 254 */       if (limit < drawWidth) {
/* 255 */         gcImage.setBackground(Colors.blues[0]);
/* 256 */         gcImage.fillRectangle(limit + x0, 1, x1 - limit, 2);
/*     */       }
/*     */       
/*     */ 
/* 260 */       gcImage.setBackground(Colors.colorProgressBar);
/* 261 */       gcImage.fillRectangle(x0, 1, limit, 2);
/*     */     } catch (Exception e) {
/* 263 */       System.out.println("Error Drawing PiecesItem");
/* 264 */       Debug.printStackTrace(e);
/*     */     }
/* 266 */     gcImage.dispose();
/*     */     
/* 268 */     Image oldImage = null;
/* 269 */     Graphic graphic = cell.getGraphic();
/* 270 */     if ((graphic instanceof UISWTGraphic)) {
/* 271 */       oldImage = ((UISWTGraphic)graphic).getImage();
/*     */     }
/* 273 */     if ((bImageChanged) || (image != oldImage) || (!cell.isValid())) {
/* 274 */       if ((cell instanceof TableCellSWT)) {
/* 275 */         ((TableCellSWT)cell).setGraphic(image);
/*     */       } else {
/* 277 */         cell.setGraphic(new UISWTGraphicImpl(image));
/*     */       }
/* 279 */       if (bImageChanged) {
/* 280 */         cell.invalidate();
/*     */       }
/* 282 */       infoObj.setUserData("PiecesImage", image);
/* 283 */       infoObj.setUserData("PiecesImageBuffer", imageBuffer);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/PiecesItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */