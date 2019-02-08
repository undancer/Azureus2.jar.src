/*     */ package com.aelitis.azureus.ui.swt.columns.vuzeactivity;
/*     */ 
/*     */ import com.aelitis.azureus.activities.VuzeActivitiesEntry;
/*     */ import com.aelitis.azureus.core.util.GeneralUtils;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinFactory;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinProperties;
/*     */ import com.aelitis.azureus.util.StringCompareUtils;
/*     */ import com.aelitis.azureus.util.UrlFilter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URLDecoder;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.FontData;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseMoveListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellToolTipListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableRow;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter;
/*     */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter.URLInfo;
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
/*     */ 
/*     */ 
/*     */ public class ColumnActivityText
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellSWTPaintListener, TableCellRefreshListener, TableCellMouseMoveListener, TableCellToolTipListener
/*     */ {
/*     */   public static final String COLUMN_ID = "activityText";
/*     */   private Color colorLinkNormal;
/*     */   private Color colorLinkHover;
/*  67 */   private static Font font = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ColumnActivityText(String tableID)
/*     */   {
/*  74 */     super("activityText", tableID);
/*     */     
/*  76 */     initializeAsGraphic(600);
/*  77 */     SWTSkinProperties skinProperties = SWTSkinFactory.getInstance().getSkinProperties();
/*  78 */     this.colorLinkNormal = skinProperties.getColor("color.links.normal");
/*  79 */     this.colorLinkHover = skinProperties.getColor("color.links.hover");
/*     */   }
/*     */   
/*     */   public void cellPaint(GC gc, TableCellSWT cell)
/*     */   {
/*  84 */     GCStringPrinter sp = setupStringPrinter(gc, cell);
/*     */     
/*  86 */     if (sp.hasHitUrl()) {
/*  87 */       GCStringPrinter.URLInfo[] hitUrlInfo = sp.getHitUrlInfo();
/*  88 */       for (int i = 0; i < hitUrlInfo.length; i++) {
/*  89 */         GCStringPrinter.URLInfo info = hitUrlInfo[i];
/*  90 */         info.urlUnderline = cell.getTableRow().isSelected();
/*  91 */         if (info.urlUnderline) {
/*  92 */           info.urlColor = null;
/*     */         } else {
/*  94 */           info.urlColor = this.colorLinkNormal;
/*     */         }
/*     */       }
/*  97 */       int[] mouseOfs = cell.getMouseOffset();
/*  98 */       if (mouseOfs != null) {
/*  99 */         Rectangle realBounds = cell.getBounds();
/* 100 */         GCStringPrinter.URLInfo hitUrl = sp.getHitUrl(mouseOfs[0] + realBounds.x, mouseOfs[1] + realBounds.y);
/*     */         
/* 102 */         if (hitUrl != null) {
/* 103 */           hitUrl.urlColor = this.colorLinkHover;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 108 */     sp.printString();
/* 109 */     gc.setFont(null);
/*     */   }
/*     */   
/*     */   private GCStringPrinter setupStringPrinter(GC gc, TableCellSWT cell) {
/* 113 */     VuzeActivitiesEntry entry = (VuzeActivitiesEntry)cell.getDataSource();
/* 114 */     String text = entry.getText();
/* 115 */     Rectangle drawBounds = getDrawBounds(cell);
/*     */     
/* 117 */     entry.setViewed();
/*     */     
/* 119 */     if (!entry.isRead()) {
/* 120 */       if (font == null) {
/* 121 */         FontData[] fontData = gc.getFont().getFontData();
/* 122 */         fontData[0].setStyle(1);
/* 123 */         font = new Font(gc.getDevice(), fontData);
/*     */       }
/* 125 */       gc.setFont(font);
/*     */     }
/*     */     
/* 128 */     int style = 64;
/*     */     
/* 130 */     GCStringPrinter sp = new GCStringPrinter(gc, text, drawBounds, true, true, style);
/*     */     
/*     */ 
/* 133 */     sp.calculateMetrics();
/*     */     
/* 135 */     return sp;
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell)
/*     */   {
/* 140 */     VuzeActivitiesEntry entry = (VuzeActivitiesEntry)cell.getDataSource();
/*     */     
/* 142 */     cell.setSortValue(entry.getText());
/*     */   }
/*     */   
/*     */   public void cellMouseTrigger(TableCellMouseEvent event)
/*     */   {
/* 147 */     String tooltip = null;
/* 148 */     boolean invalidateAndRefresh = false;
/*     */     
/* 150 */     VuzeActivitiesEntry entry = (VuzeActivitiesEntry)event.cell.getDataSource();
/*     */     
/* 152 */     Rectangle bounds = ((TableCellSWT)event.cell).getBounds();
/*     */     
/* 154 */     String text = entry.getText();
/*     */     
/* 156 */     GC gc = new GC(Display.getDefault());
/* 157 */     GCStringPrinter sp = null;
/*     */     try {
/* 159 */       sp = setupStringPrinter(gc, (TableCellSWT)event.cell);
/*     */     } catch (Exception e) {
/* 161 */       Debug.out(e);
/*     */     } finally {
/* 163 */       gc.dispose();
/*     */     }
/*     */     
/* 166 */     if (sp != null) {
/* 167 */       GCStringPrinter.URLInfo hitUrl = sp.getHitUrl(event.x + bounds.x, event.y + bounds.y);
/*     */       int newCursor;
/* 169 */       if (hitUrl != null) {
/* 170 */         String url = hitUrl.url;
/* 171 */         boolean ourUrl = (UrlFilter.getInstance().urlCanRPC(url)) || (url.startsWith("/")) || (url.startsWith("#"));
/*     */         
/* 173 */         if ((event.eventType == 0) && (event.button == 1)) {
/* 174 */           if (!ourUrl) {
/* 175 */             if (UrlUtils.isInternalProtocol(url)) {
/*     */               try {
/* 177 */                 UIFunctionsManagerSWT.getUIFunctionsSWT().doSearch(url);
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 181 */                 Debug.out(e);
/*     */               }
/*     */             } else {
/* 184 */               Utils.launch(url);
/*     */             }
/*     */           } else {
/* 187 */             UIFunctionsSWT uif = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 188 */             if (uif != null) {
/* 189 */               String target = hitUrl.target;
/* 190 */               if (target == null) {
/* 191 */                 target = "browse";
/*     */               }
/* 193 */               uif.viewURL(hitUrl.url, target, "column.activity.text");
/* 194 */               return;
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 199 */         int newCursor = 21;
/* 200 */         if (ourUrl) {
/*     */           try {
/* 202 */             tooltip = hitUrl.title == null ? null : URLDecoder.decode(hitUrl.title, "utf-8");
/*     */ 
/*     */           }
/*     */           catch (UnsupportedEncodingException e) {}
/*     */         } else {
/* 207 */           tooltip = hitUrl.url;
/*     */         }
/*     */       } else {
/* 210 */         newCursor = 0;
/*     */       }
/*     */       
/* 213 */       int oldCursor = ((TableCellSWT)event.cell).getCursorID();
/* 214 */       if (oldCursor != newCursor) {
/* 215 */         invalidateAndRefresh = true;
/* 216 */         ((TableCellSWT)event.cell).setCursorID(newCursor);
/*     */       }
/*     */     }
/*     */     
/* 220 */     Object o = event.cell.getToolTip();
/* 221 */     if ((o == null) || ((o instanceof String))) {
/* 222 */       String oldTooltip = (String)o;
/* 223 */       if (!StringCompareUtils.equals(oldTooltip, tooltip)) {
/* 224 */         invalidateAndRefresh = true;
/* 225 */         event.cell.setToolTip(tooltip);
/*     */       }
/*     */     }
/*     */     
/* 229 */     if (invalidateAndRefresh) {
/* 230 */       event.cell.invalidate();
/* 231 */       ((TableCellSWT)event.cell).redraw();
/*     */     }
/*     */   }
/*     */   
/*     */   private Rectangle getDrawBounds(TableCellSWT cell) {
/* 236 */     Rectangle bounds = cell.getBounds();
/* 237 */     bounds.x += 4;
/* 238 */     bounds.width -= 4;
/*     */     
/* 240 */     return bounds;
/*     */   }
/*     */   
/*     */   public void cellHover(TableCell cell)
/*     */   {
/* 245 */     if (cell.getToolTip() != null) {
/* 246 */       return;
/*     */     }
/* 248 */     if (!(cell instanceof TableCellSWT)) {
/* 249 */       return;
/*     */     }
/* 251 */     if (!Utils.isThisThreadSWT()) {
/* 252 */       System.err.println("you broke it");
/* 253 */       return;
/*     */     }
/* 255 */     GC gc = new GC(Display.getDefault());
/*     */     try {
/* 257 */       GCStringPrinter sp = setupStringPrinter(gc, (TableCellSWT)cell);
/*     */       
/* 259 */       if (sp.isCutoff()) {
/* 260 */         cell.setToolTip(GeneralUtils.stripOutHyperlinks(sp.getText()));
/*     */       }
/*     */     } catch (Throwable t) {
/* 263 */       Debug.out(t);
/*     */     } finally {
/* 265 */       gc.dispose();
/*     */     }
/*     */   }
/*     */   
/*     */   public void cellHoverComplete(TableCell cell)
/*     */   {
/* 271 */     cell.setToolTip(null);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/vuzeactivity/ColumnActivityText.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */