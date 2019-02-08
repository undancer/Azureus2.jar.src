/*     */ package com.aelitis.azureus.ui.swt.columns.searchsubs;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.swt.search.SBC_SearchResultsView;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinFactory;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinProperties;
/*     */ import com.aelitis.azureus.ui.swt.utils.SearchSubsResultBase;
/*     */ import com.aelitis.azureus.util.StringCompareUtils;
/*     */ import java.net.URL;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.FontData;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseMoveListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableRow;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter;
/*     */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter.URLInfo;
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
/*     */ public class ColumnSearchSubResultActions
/*     */   implements TableCellSWTPaintListener, TableCellRefreshListener, TableCellMouseMoveListener, TableCellAddedListener
/*     */ {
/*     */   public static final String COLUMN_ID = "actions";
/*     */   private Color colorLinkNormal;
/*     */   private Color colorLinkHover;
/*  81 */   private static Font font = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ColumnSearchSubResultActions(TableColumn column)
/*     */   {
/*  88 */     column.initialize(1, -2, 180);
/*  89 */     column.addListeners(this);
/*  90 */     column.setRefreshInterval(-3);
/*  91 */     column.setType(2);
/*     */     
/*  93 */     if ((column instanceof TableColumnCore)) {
/*  94 */       ((TableColumnCore)column).setUseCoreDataSource(true);
/*  95 */       ((TableColumnCore)column).addCellOtherListener("SWTPaint", this);
/*     */     }
/*     */     
/*  98 */     SWTSkinProperties skinProperties = SWTSkinFactory.getInstance().getSkinProperties();
/*  99 */     this.colorLinkNormal = skinProperties.getColor("color.links.normal");
/* 100 */     this.colorLinkHover = skinProperties.getColor("color.links.hover");
/*     */   }
/*     */   
/*     */   public void cellPaint(GC gc, TableCellSWT cell) {
/* 104 */     SearchSubsResultBase entry = (SearchSubsResultBase)cell.getDataSource();
/* 105 */     if (entry == null) {
/* 106 */       return;
/*     */     }
/*     */     
/* 109 */     TableRow row = cell.getTableRow();
/* 110 */     if (row == null) {
/* 111 */       return;
/*     */     }
/* 113 */     String text = (String)row.getData("text");
/*     */     
/* 115 */     if ((text != null) && (text.length() > 0)) {
/* 116 */       if (font == null) {
/* 117 */         FontData[] fontData = gc.getFont().getFontData();
/* 118 */         fontData[0].setStyle(1);
/* 119 */         font = new Font(gc.getDevice(), fontData);
/*     */       }
/* 121 */       gc.setFont(font);
/*     */       
/* 123 */       Rectangle bounds = getDrawBounds(cell);
/*     */       
/* 125 */       GCStringPrinter sp = new GCStringPrinter(gc, text, bounds, true, true, 16777280);
/*     */       
/*     */ 
/* 128 */       sp.calculateMetrics();
/*     */       
/* 130 */       if (sp.hasHitUrl()) {
/* 131 */         GCStringPrinter.URLInfo[] hitUrlInfo = sp.getHitUrlInfo();
/* 132 */         for (int i = 0; i < hitUrlInfo.length; i++) {
/* 133 */           GCStringPrinter.URLInfo info = hitUrlInfo[i];
/*     */           
/*     */ 
/* 136 */           info.urlUnderline = ((cell.getTableRow() == null) || (cell.getTableRow().isSelected()));
/* 137 */           if (info.urlUnderline) {
/* 138 */             info.urlColor = null;
/*     */           } else {
/* 140 */             info.urlColor = this.colorLinkNormal;
/*     */           }
/*     */         }
/* 143 */         int[] mouseOfs = cell.getMouseOffset();
/* 144 */         if (mouseOfs != null) {
/* 145 */           Rectangle realBounds = cell.getBounds();
/* 146 */           GCStringPrinter.URLInfo hitUrl = sp.getHitUrl(mouseOfs[0] + realBounds.x, mouseOfs[1] + realBounds.y);
/*     */           
/* 148 */           if (hitUrl != null) {
/* 149 */             hitUrl.urlColor = this.colorLinkHover;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 154 */       sp.printString();
/*     */     }
/*     */   }
/*     */   
/*     */   public void cellAdded(TableCell cell) {
/* 159 */     cell.setMarginHeight(0);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell) {
/* 163 */     SearchSubsResultBase entry = (SearchSubsResultBase)cell.getDataSource();
/*     */     
/* 165 */     if (entry == null) { return;
/*     */     }
/* 167 */     String link = entry.getTorrentLink();
/* 168 */     String details = entry.getDetailsLink();
/*     */     
/* 170 */     if ((!cell.setSortValue(link)) && (cell.isValid())) {
/* 171 */       return;
/*     */     }
/*     */     
/* 174 */     boolean canDL = (link != null) && (link.length() > 0);
/* 175 */     boolean canDetails = (details != null) && (details.length() > 0);
/*     */     
/* 177 */     StringBuilder sb = new StringBuilder();
/* 178 */     if (canDL) {
/* 179 */       if (sb.length() > 0) {
/* 180 */         sb.append(" | ");
/*     */       }
/*     */       String action;
/*     */       String action;
/* 184 */       if (link.startsWith("chat:"))
/*     */       {
/* 186 */         action = MessageText.getString("label.view");
/*     */       } else { String action;
/* 188 */         if (link.startsWith("azplug:?id=subscription"))
/*     */         {
/* 190 */           action = MessageText.getString("subscriptions.listwindow.subscribe");
/*     */         }
/*     */         else
/*     */         {
/* 194 */           action = MessageText.getString("label.download");
/*     */         }
/*     */       }
/* 197 */       sb.append("<A HREF=\"download\">" + action + "</A>");
/*     */     }
/*     */     
/* 200 */     if (canDetails) {
/* 201 */       if (sb.length() > 0) {
/* 202 */         sb.append(", ");
/*     */       }
/* 204 */       sb.append("<A HREF=\"details\">" + MessageText.getString("popup.error.details") + "</A>");
/*     */     }
/*     */     
/* 207 */     cell.getTableRow().setData("text", sb.toString());
/*     */   }
/*     */   
/*     */   public void cellMouseTrigger(TableCellMouseEvent event) {
/* 211 */     SearchSubsResultBase entry = (SearchSubsResultBase)event.cell.getDataSource();
/*     */     
/* 213 */     String tooltip = null;
/* 214 */     boolean invalidateAndRefresh = false;
/*     */     
/* 216 */     Rectangle bounds = ((TableCellSWT)event.cell).getBounds();
/* 217 */     String text = (String)event.cell.getTableRow().getData("text");
/* 218 */     if (text == null) {
/* 219 */       return;
/*     */     }
/*     */     
/* 222 */     GCStringPrinter sp = null;
/* 223 */     GC gc = new GC(Display.getDefault());
/*     */     try {
/* 225 */       if (font != null) {
/* 226 */         gc.setFont(font);
/*     */       }
/* 228 */       Rectangle drawBounds = getDrawBounds((TableCellSWT)event.cell);
/* 229 */       sp = new GCStringPrinter(gc, text, drawBounds, true, true, 16777280);
/*     */       
/* 231 */       sp.calculateMetrics();
/*     */     } catch (Exception e) {
/* 233 */       Debug.out(e);
/*     */     } finally {
/* 235 */       gc.dispose();
/*     */     }
/*     */     
/* 238 */     if (sp != null) {
/* 239 */       GCStringPrinter.URLInfo hitUrl = sp.getHitUrl(event.x + bounds.x, event.y + bounds.y);
/*     */       int newCursor;
/* 241 */       int newCursor; if (hitUrl != null) {
/* 242 */         if ((event.eventType == 1) && (event.button == 1)) {
/* 243 */           if (hitUrl.url.equals("download"))
/*     */           {
/* 245 */             SBC_SearchResultsView.downloadAction(entry);
/*     */           }
/* 247 */           else if (hitUrl.url.equals("details"))
/*     */           {
/* 249 */             String details_url = entry.getDetailsLink();
/*     */             try
/*     */             {
/* 252 */               Utils.launch(new URL(details_url));
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 256 */               Debug.out(e);
/*     */             }
/*     */           }
/*     */         }
/* 260 */         else if (hitUrl.url.equals("download")) {
/* 261 */           tooltip = entry.getTorrentLink();
/* 262 */         } else if (hitUrl.url.equals("details")) {
/* 263 */           tooltip = entry.getDetailsLink();
/*     */         }
/*     */         
/*     */ 
/* 267 */         newCursor = 21;
/*     */       }
/*     */       else {
/* 270 */         newCursor = 0;
/*     */       }
/*     */       
/* 273 */       int oldCursor = ((TableCellSWT)event.cell).getCursorID();
/* 274 */       if (oldCursor != newCursor) {
/* 275 */         invalidateAndRefresh = true;
/* 276 */         ((TableCellSWT)event.cell).setCursorID(newCursor);
/*     */       }
/*     */     }
/*     */     
/* 280 */     Object o = event.cell.getToolTip();
/* 281 */     if ((o == null) || ((o instanceof String))) {
/* 282 */       String oldTooltip = (String)o;
/* 283 */       if (!StringCompareUtils.equals(oldTooltip, tooltip)) {
/* 284 */         invalidateAndRefresh = true;
/* 285 */         event.cell.setToolTip(tooltip);
/*     */       }
/*     */     }
/*     */     
/* 289 */     if (invalidateAndRefresh) {
/* 290 */       event.cell.invalidate();
/* 291 */       ((TableCellSWT)event.cell).redraw();
/*     */     }
/*     */   }
/*     */   
/* 295 */   boolean bMouseDowned = false;
/*     */   
/*     */   private Rectangle getDrawBounds(TableCellSWT cell) {
/* 298 */     Rectangle bounds = cell.getBounds();
/* 299 */     bounds.height -= 12;
/* 300 */     bounds.y += 6;
/* 301 */     bounds.x += 4;
/* 302 */     bounds.width -= 4;
/*     */     
/* 304 */     return bounds;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/searchsubs/ColumnSearchSubResultActions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */