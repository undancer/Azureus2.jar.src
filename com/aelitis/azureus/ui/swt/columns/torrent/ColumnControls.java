/*     */ package com.aelitis.azureus.ui.swt.columns.torrent;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.columns.utils.ColumnImageClickArea;
/*     */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.ui.Graphic;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellDisposeListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellVisibilityListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.SWTThread;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTGraphic;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTGraphicImpl;
/*     */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter;
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
/*     */ public class ColumnControls
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellAddedListener
/*     */ {
/*  56 */   public static final Class DATASOURCE_TYPE = Download.class;
/*     */   
/*  58 */   public static String COLUMN_ID = "Controls";
/*     */   
/*     */ 
/*     */   private static final int COLUMN_WIDTH = 32;
/*     */   
/*     */ 
/*     */   private static final boolean DEBUG = false;
/*     */   
/*     */   private Display display;
/*     */   
/*  68 */   List listClickAreas = new ArrayList();
/*     */   
/*     */   public ColumnControls(String sTableID) {
/*  71 */     super(DATASOURCE_TYPE, COLUMN_ID, 1, 32, sTableID);
/*  72 */     initializeAsGraphic(32);
/*  73 */     setMinWidth(32);
/*  74 */     setMaxWidth(32);
/*     */     
/*  76 */     this.display = SWTThread.getInstance().getDisplay();
/*     */     
/*     */ 
/*     */ 
/*  80 */     ColumnImageClickArea clickArea = new ColumnImageClickArea(COLUMN_ID, "up", "image.torrent.up");
/*  81 */     clickArea.setPosition(0, 0);
/*  82 */     this.listClickAreas.add(clickArea);
/*     */     
/*  84 */     clickArea = new ColumnImageClickArea(COLUMN_ID, "down", "image.torrent.down");
/*  85 */     clickArea.setPosition(16, 0);
/*  86 */     this.listClickAreas.add(clickArea);
/*     */   }
/*     */   
/*     */   public void cellAdded(TableCell cell)
/*     */   {
/*  91 */     new Cell(cell);
/*     */   }
/*     */   
/*     */   private class Cell
/*     */     implements TableCellRefreshListener, TableCellDisposeListener, TableCellMouseListener, TableCellVisibilityListener
/*     */   {
/*     */     public Cell(TableCell cell)
/*     */     {
/*  99 */       cell.addListeners(this);
/* 100 */       cell.setMarginHeight(1);
/* 101 */       cell.setMarginWidth(0);
/* 102 */       cell.setFillCell(true);
/*     */       
/* 104 */       for (Iterator iter = ColumnControls.this.listClickAreas.iterator(); iter.hasNext();) {
/* 105 */         ColumnImageClickArea clickArea = (ColumnImageClickArea)iter.next();
/* 106 */         clickArea.addCell(cell);
/*     */       }
/*     */     }
/*     */     
/*     */     public void dispose(TableCell cell) {
/* 111 */       disposeExisting(cell);
/*     */     }
/*     */     
/*     */     public void refresh(TableCell cell) {
/* 115 */       refresh(cell, false);
/*     */     }
/*     */     
/*     */     private void refresh(TableCell cell, boolean bForce) {
/* 119 */       DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 120 */       if (dm == null) {
/* 121 */         disposeExisting(cell);
/* 122 */         return;
/*     */       }
/* 124 */       int position = dm.getPosition();
/*     */       
/* 126 */       int cellWidth = cell.getWidth();
/* 127 */       int cellHeight = cell.getHeight();
/*     */       
/* 129 */       Image image = null;
/* 130 */       Graphic graphic = cell.getGraphic();
/* 131 */       if ((graphic instanceof UISWTGraphic)) {
/* 132 */         image = ((UISWTGraphic)graphic).getImage();
/*     */       }
/* 134 */       if (image != null) {
/* 135 */         Rectangle bounds = image.getBounds();
/* 136 */         if ((!cell.setSortValue(position)) && (cell.isValid()) && (bounds.width == cellWidth) && (bounds.height == cellHeight))
/*     */         {
/* 138 */           return;
/*     */         }
/*     */       } else {
/* 141 */         cell.setSortValue(position);
/*     */       }
/*     */       
/* 144 */       disposeExisting(cell);
/* 145 */       image = new Image(ColumnControls.this.display, cellWidth, cellHeight);
/*     */       
/* 147 */       GC gcImage = new GC(image);
/*     */       try {
/* 149 */         Color background = ColorCache.getColor(ColumnControls.this.display, cell.getBackground());
/* 150 */         if (background != null) {
/* 151 */           gcImage.setBackground(background);
/* 152 */           gcImage.fillRectangle(0, 0, cellWidth, cellHeight);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 160 */         int[] fg = cell.getForeground();
/* 161 */         if (fg != null) {
/* 162 */           gcImage.setForeground(ColorCache.getColor(ColumnControls.this.display, fg[0], fg[1], fg[2]));
/*     */         }
/* 164 */         Rectangle bounds = image.getBounds();
/* 165 */         GCStringPrinter.printString(gcImage, "" + position + (dm.getAssumedComplete() ? "^" : "v"), bounds, true, false, 16778240);
/*     */         
/* 167 */         gcImage.setFont(null);
/*     */         
/* 169 */         for (iter = ColumnControls.this.listClickAreas.iterator(); iter.hasNext();) {
/* 170 */           ColumnImageClickArea clickArea = (ColumnImageClickArea)iter.next();
/* 171 */           clickArea.drawImage(cell, gcImage);
/*     */         }
/*     */       } finally { Iterator iter;
/* 174 */         gcImage.dispose();
/*     */       }
/*     */       
/* 177 */       disposeExisting(cell);
/*     */       
/* 179 */       if ((cell instanceof TableCellSWT)) {
/* 180 */         ((TableCellSWT)cell).setGraphic(image);
/*     */       } else {
/* 182 */         cell.setGraphic(new UISWTGraphicImpl(image));
/*     */       }
/*     */     }
/*     */     
/*     */     public void cellMouseTrigger(TableCellMouseEvent event)
/*     */     {
/* 188 */       if (((event.data instanceof ColumnImageClickArea)) && (event.cell != null)) {
/* 189 */         DownloadManager dm = (DownloadManager)event.cell.getDataSource();
/* 190 */         if (dm == null) {
/* 191 */           return;
/*     */         }
/*     */         
/* 194 */         ColumnImageClickArea clickArea = (ColumnImageClickArea)event.data;
/* 195 */         log(event.cell, "CLICK ON " + clickArea.getId());
/* 196 */         if (clickArea.getId().equals("up")) {
/* 197 */           dm.getGlobalManager().moveUp(dm);
/* 198 */         } else if (clickArea.getId().equals("down")) {
/* 199 */           dm.getGlobalManager().moveDown(dm);
/*     */         }
/* 201 */         event.cell.getTableColumn().invalidateCells();
/*     */       }
/*     */     }
/*     */     
/*     */     public void cellVisibilityChanged(TableCell cell, int visibility) {
/* 206 */       if (visibility == 1)
/*     */       {
/* 208 */         disposeExisting(cell);
/* 209 */       } else if (visibility == 0)
/*     */       {
/* 211 */         refresh(cell, true);
/*     */       }
/*     */     }
/*     */     
/*     */     private void disposeExisting(TableCell cell) {
/* 216 */       Graphic oldGraphic = cell.getGraphic();
/*     */       
/* 218 */       if ((oldGraphic instanceof UISWTGraphic)) {
/* 219 */         Image oldImage = ((UISWTGraphic)oldGraphic).getImage();
/* 220 */         if ((oldImage != null) && (!oldImage.isDisposed()))
/*     */         {
/* 222 */           cell.setGraphic(null);
/* 223 */           oldImage.dispose();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     private void log(TableCell cell, String s) {}
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/torrent/ColumnControls.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */