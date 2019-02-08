/*     */ package com.aelitis.azureus.ui.swt.columns.utils;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.table.TableCellCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseMoveListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableRow;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableRowMouseEvent;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableRowMouseListener;
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
/*     */ public class ColumnImageClickArea
/*     */   implements TableCellMouseMoveListener, TableRowMouseListener
/*     */ {
/*     */   private static final boolean DEBUG = false;
/*     */   private String imageID;
/*     */   private final String columnID;
/*     */   private Rectangle area;
/*     */   private String id;
/*     */   private Image image;
/*     */   private Rectangle imageArea;
/*     */   private Image imgOnRow;
/*     */   private Image imgOver;
/*     */   private Image imgOffRow;
/*     */   private boolean mouseDownOn;
/*     */   private boolean cellContainsMouse;
/*     */   private TableRow rowContainingMouse;
/*  67 */   private float scale = 1.0F;
/*     */   
/*     */   private String tooltip;
/*     */   
/*  71 */   private boolean isVisible = true;
/*     */   
/*     */ 
/*     */ 
/*     */   public ColumnImageClickArea(String columnID, String id, String imageID)
/*     */   {
/*  77 */     this.columnID = columnID;
/*  78 */     this.id = id;
/*     */     
/*  80 */     setImageID(imageID);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setImageID(String imageID)
/*     */   {
/*  89 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*  90 */     if (this.imgOver != null) {
/*  91 */       imageLoader.releaseImage(this.imageID + "-over");
/*     */     }
/*  93 */     if (this.imgOnRow != null) {
/*  94 */       imageLoader.releaseImage(this.imageID + "-mouseonrow");
/*     */     }
/*  96 */     if (this.imgOffRow != null) {
/*  97 */       imageLoader.releaseImage(this.imageID);
/*     */     }
/*     */     
/* 100 */     this.imageID = imageID;
/* 101 */     if (imageID == null) {
/* 102 */       this.imgOffRow = null;
/* 103 */       this.imgOnRow = null;
/*     */     } else {
/* 105 */       this.imgOnRow = imageLoader.getImage(imageID + "-mouseonrow");
/* 106 */       this.imgOver = imageLoader.getImage(imageID + "-over");
/* 107 */       this.imgOffRow = imageLoader.getImage(imageID);
/* 108 */       if (!ImageLoader.isRealImage(this.imgOnRow)) {
/* 109 */         this.imgOnRow = this.imgOffRow;
/*     */       }
/* 111 */       if (!ImageLoader.isRealImage(this.imgOver)) {
/* 112 */         this.imgOver = this.imgOffRow;
/*     */       }
/*     */     }
/* 115 */     this.image = null;
/*     */   }
/*     */   
/*     */   public void addCell(TableCell cell) {
/* 119 */     cell.addListeners(this);
/* 120 */     TableRow row = cell.getTableRow();
/* 121 */     if (row != null) {
/* 122 */       row.addMouseListener(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Rectangle getArea()
/*     */   {
/* 130 */     if (this.area == null) {
/* 131 */       this.area = new Rectangle(0, 0, 0, 0);
/*     */     }
/* 133 */     return this.area;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setArea(Rectangle area)
/*     */   {
/* 140 */     this.area = area;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getId()
/*     */   {
/* 147 */     return this.id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Image getImage()
/*     */   {
/* 154 */     return this.image;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setImage(Image image)
/*     */   {
/* 161 */     if (!ImageLoader.isRealImage(image)) {
/* 162 */       this.image = null;
/* 163 */       this.imageArea = new Rectangle(0, 0, 0, 0);
/*     */     } else {
/* 165 */       this.image = image;
/* 166 */       this.imageArea = image.getBounds();
/*     */     }
/*     */     
/* 169 */     if (this.area == null) {
/* 170 */       this.area = new Rectangle(this.imageArea.x, this.imageArea.y, this.imageArea.width, this.imageArea.height);
/*     */       
/* 172 */       return;
/*     */     }
/* 174 */     this.area.width = ((int)(this.imageArea.width * this.scale));
/* 175 */     this.area.height = ((int)(this.imageArea.height * this.scale));
/*     */   }
/*     */   
/*     */   public void setPosition(int x, int y)
/*     */   {
/* 180 */     if (this.area == null) {
/* 181 */       this.area = new Rectangle(x, y, 0, 0);
/* 182 */       return;
/*     */     }
/* 184 */     this.area.x = x;
/* 185 */     this.area.y = y;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void drawImage(TableCell cell, GC gcImage)
/*     */   {
/* 194 */     if (!this.isVisible) {
/* 195 */       return;
/*     */     }
/*     */     
/* 198 */     Image image = this.image;
/* 199 */     if (image == null) {
/* 200 */       if ((this.cellContainsMouse) && (ImageLoader.isRealImage(this.imgOver))) {
/* 201 */         image = this.imgOver;
/* 202 */       } else if ((this.rowContainingMouse == cell.getTableRow()) && (ImageLoader.isRealImage(this.imgOnRow)))
/*     */       {
/* 204 */         image = this.imgOnRow;
/*     */       } else {
/* 206 */         image = this.imgOffRow;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 218 */     if (ImageLoader.isRealImage(image)) {
/* 219 */       this.imageArea = image.getBounds();
/*     */       
/* 221 */       Rectangle area = getArea();
/* 222 */       area.width = ((int)(this.imageArea.width * this.scale));
/* 223 */       area.height = ((int)(this.imageArea.height * this.scale));
/*     */       
/* 225 */       gcImage.drawImage(image, this.imageArea.x, this.imageArea.y, this.imageArea.width, this.imageArea.height, area.x, area.y, area.width, area.height);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void cellMouseTrigger(TableCellMouseEvent event)
/*     */   {
/* 232 */     if (!this.isVisible) {
/* 233 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 238 */     if (event.eventType == 0) {
/* 239 */       this.mouseDownOn = false;
/* 240 */       Point pt = new Point(event.x, event.y);
/* 241 */       this.mouseDownOn = getArea().contains(pt);
/* 242 */       TableCellCore cell = (TableCellCore)event.row.getTableCell(this.columnID);
/* 243 */       if (cell != null) {
/* 244 */         cell.invalidate();
/* 245 */         cell.refreshAsync();
/*     */       }
/* 247 */     } else if ((event.eventType == 1) && (this.mouseDownOn))
/*     */     {
/* 249 */       this.mouseDownOn = false;
/* 250 */       TableCellMouseEvent mouseEvent = new TableCellMouseEvent();
/* 251 */       mouseEvent.button = event.button;
/* 252 */       mouseEvent.cell = event.cell;
/* 253 */       mouseEvent.eventType = 1;
/* 254 */       mouseEvent.keyboardState = event.keyboardState;
/* 255 */       mouseEvent.skipCoreFunctionality = event.skipCoreFunctionality;
/* 256 */       mouseEvent.x = event.x;
/* 257 */       mouseEvent.y = event.y;
/* 258 */       mouseEvent.data = this;
/* 259 */       ((TableColumnCore)event.cell.getTableColumn()).invokeCellMouseListeners(mouseEvent);
/* 260 */       ((TableCellCore)event.cell).invokeMouseListeners(mouseEvent);
/* 261 */     } else if (event.eventType == 3) {
/* 262 */       boolean contains = getArea().contains(event.x, event.y);
/* 263 */       setContainsMouse(event.cell, contains);
/* 264 */     } else if (event.eventType == 5) {
/* 265 */       setContainsMouse(event.cell, false);
/* 266 */     } else if (event.eventType == 2) {
/* 267 */       event.skipCoreFunctionality = true;
/*     */     }
/*     */   }
/*     */   
/*     */   public void rowMouseTrigger(TableRowMouseEvent event)
/*     */   {
/* 273 */     if (!this.isVisible) {
/* 274 */       return;
/*     */     }
/* 276 */     if (event.eventType == 5) {
/* 277 */       if (this.rowContainingMouse == event.row) {
/* 278 */         this.rowContainingMouse = null;
/*     */       }
/* 280 */       setContainsMouse(null, false);
/*     */       
/* 282 */       TableCellCore cell = (TableCellCore)event.row.getTableCell(this.columnID);
/* 283 */       if (cell != null) {
/* 284 */         cell.invalidate();
/* 285 */         cell.refreshAsync();
/*     */       }
/* 287 */     } else if (event.eventType == 4) {
/* 288 */       this.rowContainingMouse = event.row;
/*     */       
/*     */ 
/* 291 */       TableCellCore cell = (TableCellCore)event.row.getTableCell(this.columnID);
/* 292 */       if (cell != null) {
/* 293 */         cell.invalidate();
/* 294 */         cell.refreshAsync();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void setContainsMouse(TableCell cell, boolean contains) {
/* 300 */     if (this.cellContainsMouse != contains) {
/* 301 */       this.cellContainsMouse = contains;
/*     */       
/* 303 */       if (cell != null) {
/* 304 */         TableCellCore cellCore = (TableCellCore)cell;
/* 305 */         cellCore.invalidate();
/* 306 */         cellCore.refreshAsync();
/* 307 */         cellCore.setCursorID(this.cellContainsMouse ? 21 : 0);
/*     */         
/* 309 */         if (this.tooltip != null) {
/* 310 */           if (this.cellContainsMouse) {
/* 311 */             cellCore.setToolTip(this.tooltip);
/*     */           } else {
/* 313 */             Object oldTT = cellCore.getToolTip();
/* 314 */             if (this.tooltip.equals(oldTT)) {
/* 315 */               cellCore.setToolTip(null);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public float getScale() {
/* 324 */     return this.scale;
/*     */   }
/*     */   
/*     */   public void setScale(float scale) {
/* 328 */     this.scale = scale;
/* 329 */     setImage(this.image);
/*     */   }
/*     */   
/*     */   public Rectangle getImageArea() {
/* 333 */     return new Rectangle(this.imageArea.x, this.imageArea.y, this.imageArea.width, this.imageArea.height);
/*     */   }
/*     */   
/*     */   public String getTooltip()
/*     */   {
/* 338 */     return this.tooltip;
/*     */   }
/*     */   
/*     */   public void setTooltip(String tooltip) {
/* 342 */     this.tooltip = tooltip;
/*     */   }
/*     */   
/*     */   public boolean isVisible() {
/* 346 */     return this.isVisible;
/*     */   }
/*     */   
/*     */   public void setVisible(boolean isVisible) {
/* 350 */     this.isVisible = isVisible;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/utils/ColumnImageClickArea.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */