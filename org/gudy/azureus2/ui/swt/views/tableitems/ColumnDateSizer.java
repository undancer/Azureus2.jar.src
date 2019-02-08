/*     */ package org.gudy.azureus2.ui.swt.views.tableitems;
/*     */ 
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.FontData;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemFillListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellToolTipListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.views.ViewUtils;
/*     */ import org.gudy.azureus2.ui.swt.views.ViewUtils.CustomDateFormat;
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
/*     */ public abstract class ColumnDateSizer
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener, TableCellToolTipListener
/*     */ {
/*  51 */   private static int PADDING = 10;
/*  52 */   int curFormat = 0;
/*     */   
/*  54 */   int[] maxWidthUsed = new int[TimeFormatter.DATEFORMATS_DESC.length];
/*     */   
/*  56 */   Date[] maxWidthDate = new Date[TimeFormatter.DATEFORMATS_DESC.length];
/*     */   
/*  58 */   private boolean showTime = true;
/*     */   
/*  60 */   private boolean multiline = true;
/*     */   
/*  62 */   private String tableFormatOverride = "";
/*     */   
/*     */ 
/*     */   private static Font fontBold;
/*     */   
/*     */ 
/*     */   private ViewUtils.CustomDateFormat cdf;
/*     */   
/*     */ 
/*     */   public ColumnDateSizer(Class forDataSourceType, String columnID, int width, String tableID)
/*     */   {
/*  73 */     super(forDataSourceType, columnID, 2, width, tableID);
/*     */     
/*  75 */     final TableContextMenuItem menuShowTime = addContextMenuItem("TableColumn.menu.date_added.time", 1);
/*     */     
/*  77 */     menuShowTime.setStyle(2);
/*  78 */     menuShowTime.addFillListener(new MenuItemFillListener() {
/*     */       public void menuWillBeShown(MenuItem menu, Object data) {
/*  80 */         menu.setData(Boolean.valueOf(ColumnDateSizer.this.showTime));
/*     */       }
/*  82 */     });
/*  83 */     menuShowTime.addListener(new MenuItemListener() {
/*     */       public void selected(MenuItem menu, Object target) {
/*  85 */         ColumnDateSizer.this.showTime = (!ColumnDateSizer.this.showTime);
/*  86 */         ColumnDateSizer.this.setUserData("showTime", new Long(ColumnDateSizer.this.showTime ? 1L : 0L));
/*  87 */         ColumnDateSizer.this.maxWidthUsed = new int[TimeFormatter.DATEFORMATS_DESC.length];
/*  88 */         ColumnDateSizer.this.maxWidthDate = new Date[TimeFormatter.DATEFORMATS_DESC.length];
/*  89 */         ColumnDateSizer.this.curFormat = -1;
/*  90 */         ColumnDateSizer.this.recalcWidth(new Date(), null);
/*  91 */         if (ColumnDateSizer.this.curFormat < 0) {
/*  92 */           ColumnDateSizer.this.curFormat = (TimeFormatter.DATEFORMATS_DESC.length - 1);
/*     */         }
/*     */         
/*     */       }
/*  96 */     });
/*  97 */     COConfigurationManager.addAndFireParameterListener("Table.column.dateformat", new ParameterListener()
/*     */     {
/*     */       public void parameterChanged(String parameterName) {
/* 100 */         ColumnDateSizer.this.tableFormatOverride = COConfigurationManager.getStringParameter("Table.column.dateformat", "");
/*     */         
/* 102 */         if (ColumnDateSizer.this.tableFormatOverride == null) {
/* 103 */           ColumnDateSizer.this.tableFormatOverride = "";
/*     */         }
/* 105 */         ColumnDateSizer.this.curFormat = -1;
/* 106 */         if (ColumnDateSizer.this.tableFormatOverride.length() == 0) {
/* 107 */           ColumnDateSizer.this.recalcWidth(new Date(), null);
/* 108 */           if (ColumnDateSizer.this.curFormat < 0) {
/* 109 */             ColumnDateSizer.this.curFormat = (TimeFormatter.DATEFORMATS_DESC.length - 1);
/*     */           }
/* 111 */           menuShowTime.setVisible(true);
/*     */         } else {
/* 113 */           ColumnDateSizer.this.invalidateCells();
/* 114 */           menuShowTime.setVisible(false);
/*     */         }
/*     */         
/*     */       }
/* 118 */     });
/* 119 */     this.cdf = ViewUtils.addCustomDateFormat(this);
/*     */   }
/*     */   
/*     */   public void postConfigLoad()
/*     */   {
/* 124 */     boolean oldShowTime = this.showTime;
/* 125 */     Object oShowTime = getUserData("showTime");
/* 126 */     if ((oShowTime instanceof Number)) {
/* 127 */       Number nShowTime = (Number)oShowTime;
/* 128 */       this.showTime = (nShowTime.byteValue() == 1);
/*     */     } else {
/* 130 */       int userMode = COConfigurationManager.getIntParameter("User Mode");
/* 131 */       this.showTime = (userMode > 1);
/*     */     }
/*     */     
/* 134 */     this.cdf.update();
/* 135 */     if (oldShowTime != this.showTime) {
/* 136 */       recalcWidth(new Date(), null);
/*     */     }
/*     */     
/* 139 */     super.postConfigLoad();
/*     */   }
/*     */   
/*     */   public final void refresh(TableCell cell)
/*     */   {
/* 144 */     refresh(cell, 0L);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell, long timestamp)
/*     */   {
/* 149 */     refresh(cell, timestamp, timestamp, null);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell, long timestamp, String prefix) {
/* 153 */     refresh(cell, timestamp, timestamp, prefix);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell, final long timestamp, long sort_order, final String prefix) {
/* 157 */     if ((!cell.setSortValue(sort_order)) && (cell.isValid())) {
/* 158 */       return;
/*     */     }
/*     */     
/* 161 */     if ((timestamp <= 0L) || (timestamp == Long.MAX_VALUE)) {
/* 162 */       cell.setText(prefix);
/* 163 */       return;
/*     */     }
/*     */     
/* 166 */     SimpleDateFormat format = this.cdf.getDateFormat();
/*     */     
/* 168 */     if (format != null) {
/* 169 */       Date date = new Date(timestamp);
/*     */       try {
/* 171 */         String date_str = format.format(date);
/* 172 */         if (prefix != null) {
/* 173 */           date_str = prefix + date_str;
/*     */         }
/* 175 */         cell.setText(date_str);
/* 176 */         return;
/*     */       }
/*     */       catch (Exception e) {}
/*     */     }
/*     */     
/* 181 */     if (this.tableFormatOverride.length() > 0) {
/* 182 */       Date date = new Date(timestamp);
/*     */       try {
/* 184 */         SimpleDateFormat temp = new SimpleDateFormat(this.tableFormatOverride);
/* 185 */         String date_str = temp.format(date);
/* 186 */         if (prefix != null) {
/* 187 */           date_str = prefix + date_str;
/*     */         }
/* 189 */         cell.setText(date_str);
/* 190 */         return;
/*     */       }
/*     */       catch (Exception e) {}
/*     */     }
/*     */     
/*     */ 
/* 196 */     Utils.execSWTThread(new AERunnable()
/*     */     {
/*     */       public void runSupport() {
/* 199 */         Date date = new Date(timestamp);
/*     */         
/* 201 */         if (ColumnDateSizer.this.curFormat >= 0) {
/* 202 */           if ((ColumnDateSizer.this.multiline) && (prefix.getHeight() < 20)) {
/* 203 */             ColumnDateSizer.this.multiline = false;
/*     */           }
/* 205 */           String suffix = (ColumnDateSizer.this.showTime) && (!ColumnDateSizer.this.multiline) ? " hh:mm a" : "";
/*     */           
/* 207 */           int newWidth = ColumnDateSizer.this.calcWidth(date, TimeFormatter.DATEFORMATS_DESC[ColumnDateSizer.this.curFormat] + suffix, this.val$prefix);
/*     */           
/*     */ 
/*     */ 
/* 211 */           if (newWidth > prefix.getWidth() - ColumnDateSizer.PADDING) {
/* 212 */             if (newWidth > ColumnDateSizer.this.maxWidthUsed[ColumnDateSizer.this.curFormat]) {
/* 213 */               ColumnDateSizer.this.maxWidthUsed[ColumnDateSizer.this.curFormat] = newWidth;
/* 214 */               ColumnDateSizer.this.maxWidthDate[ColumnDateSizer.this.curFormat] = date;
/*     */             }
/* 216 */             ColumnDateSizer.this.recalcWidth(date, this.val$prefix);
/*     */           }
/*     */           
/* 219 */           String s = TimeFormatter.DATEFORMATS_DESC[ColumnDateSizer.this.curFormat] + suffix;
/* 220 */           SimpleDateFormat temp = new SimpleDateFormat(s + ((ColumnDateSizer.this.showTime) && (ColumnDateSizer.this.multiline) ? "\nh:mm a" : ""));
/*     */           
/* 222 */           String date_str = temp.format(date);
/* 223 */           if (this.val$prefix != null) {
/* 224 */             date_str = this.val$prefix + date_str;
/*     */           }
/* 226 */           prefix.setText(date_str);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public void setWidthPX(int width)
/*     */   {
/* 235 */     int oldWidth = getWidth();
/* 236 */     super.setWidthPX(width);
/*     */     
/* 238 */     if (oldWidth == width) {
/* 239 */       return;
/*     */     }
/* 241 */     if ((this.maxWidthDate != null) && (this.curFormat >= 0)) {
/* 242 */       if (this.maxWidthDate[this.curFormat] == null) {
/* 243 */         this.maxWidthDate[this.curFormat] = new Date();
/*     */       }
/* 245 */       recalcWidth(this.maxWidthDate[this.curFormat], null);
/*     */     }
/*     */   }
/*     */   
/*     */   private void recalcWidth(Date date, String prefix) {
/* 250 */     String suffix = (this.showTime) && (!this.multiline) ? " hh:mm a" : "";
/*     */     
/* 252 */     int width = getWidth();
/*     */     
/* 254 */     if (this.maxWidthDate == null) {
/* 255 */       this.maxWidthUsed = new int[TimeFormatter.DATEFORMATS_DESC.length];
/* 256 */       this.maxWidthDate = new Date[TimeFormatter.DATEFORMATS_DESC.length];
/*     */     }
/*     */     
/* 259 */     int idxFormat = TimeFormatter.DATEFORMATS_DESC.length - 1;
/*     */     
/* 261 */     GC gc = new GC(Display.getDefault());
/* 262 */     if (fontBold == null) {
/* 263 */       FontData[] fontData = gc.getFont().getFontData();
/* 264 */       for (int i = 0; i < fontData.length; i++) {
/* 265 */         FontData fd = fontData[i];
/* 266 */         fd.setStyle(1);
/*     */       }
/* 268 */       fontBold = new Font(gc.getDevice(), fontData);
/*     */     }
/* 270 */     gc.setFont(fontBold);
/*     */     try
/*     */     {
/* 273 */       Point minSize = new Point(99999, 0);
/* 274 */       for (int i = 0; i < TimeFormatter.DATEFORMATS_DESC.length; i++)
/* 275 */         if (this.maxWidthUsed[i] <= width - PADDING)
/*     */         {
/*     */ 
/* 278 */           SimpleDateFormat temp = new SimpleDateFormat(TimeFormatter.DATEFORMATS_DESC[i] + suffix);
/*     */           
/* 280 */           String date_str = temp.format(date);
/* 281 */           if (prefix != null) {
/* 282 */             date_str = prefix + date_str;
/*     */           }
/* 284 */           Point newSize = gc.stringExtent(date_str);
/* 285 */           if (newSize.x < width - PADDING) {
/* 286 */             idxFormat = i;
/* 287 */             if (this.maxWidthUsed[i] >= newSize.x) break;
/* 288 */             this.maxWidthUsed[i] = newSize.x;
/* 289 */             this.maxWidthDate[i] = date; break;
/*     */           }
/*     */           
/*     */ 
/* 293 */           if (newSize.x < minSize.x) {
/* 294 */             minSize = newSize;
/* 295 */             idxFormat = i;
/*     */           }
/*     */         }
/*     */     } catch (Throwable t) {
/*     */       return;
/*     */     } finally {
/* 301 */       gc.dispose();
/*     */     }
/*     */     
/* 304 */     if (this.curFormat != idxFormat)
/*     */     {
/* 306 */       this.curFormat = idxFormat;
/* 307 */       invalidateCells();
/*     */     }
/*     */   }
/*     */   
/*     */   private int calcWidth(Date date, String format, String prefix) {
/* 312 */     GC gc = new GC(Display.getDefault());
/* 313 */     if (fontBold == null) {
/* 314 */       FontData[] fontData = gc.getFont().getFontData();
/* 315 */       for (int i = 0; i < fontData.length; i++) {
/* 316 */         FontData fd = fontData[i];
/* 317 */         fd.setStyle(1);
/*     */       }
/* 319 */       fontBold = new Font(gc.getDevice(), fontData);
/*     */     }
/* 321 */     gc.setFont(fontBold);
/* 322 */     SimpleDateFormat temp = new SimpleDateFormat(format);
/* 323 */     String date_str = temp.format(date);
/* 324 */     if (prefix != null)
/*     */     {
/* 326 */       date_str = prefix + date_str;
/*     */     }
/*     */     
/* 329 */     Point newSize = gc.stringExtent(date_str);
/* 330 */     gc.dispose();
/* 331 */     return newSize.x;
/*     */   }
/*     */   
/*     */   public boolean getShowTime() {
/* 335 */     return this.showTime;
/*     */   }
/*     */   
/*     */   public void setShowTime(boolean showTime) {
/* 339 */     this.showTime = showTime;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isMultiline()
/*     */   {
/* 346 */     return this.multiline;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setMultiline(boolean multiline)
/*     */   {
/* 353 */     this.multiline = multiline;
/*     */   }
/*     */   
/*     */   public void cellHover(TableCell cell) {
/* 357 */     Object ds = cell.getSortValue();
/* 358 */     if ((ds instanceof Number)) {
/* 359 */       long timestamp = ((Number)ds).longValue();
/*     */       
/* 361 */       if (timestamp > 0L) {
/* 362 */         long eta = (SystemTime.getCurrentTime() - timestamp) / 1000L;
/* 363 */         if (eta > 0L) {
/* 364 */           cell.setToolTip(DisplayFormatters.formatETA(eta, false) + " " + MessageText.getString("label.ago"));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void cellHoverComplete(TableCell cell) {
/* 371 */     cell.setToolTip(null);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/ColumnDateSizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */