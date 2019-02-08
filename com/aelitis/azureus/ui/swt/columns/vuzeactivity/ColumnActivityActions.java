/*     */ package com.aelitis.azureus.ui.swt.columns.vuzeactivity;
/*     */ 
/*     */ import com.aelitis.azureus.activities.VuzeActivitiesEntry;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinFactory;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinProperties;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.TorrentListViewsUtils;
/*     */ import com.aelitis.azureus.util.PlayUtils;
/*     */ import com.aelitis.azureus.util.StringCompareUtils;
/*     */ import com.aelitis.azureus.util.UrlFilter;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.FontData;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseMoveListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
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
/*     */ public class ColumnActivityActions
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellSWTPaintListener, TableCellRefreshListener, TableCellMouseMoveListener, TableCellAddedListener
/*     */ {
/*     */   public static final String COLUMN_ID = "activityActions";
/*     */   private Color colorLinkNormal;
/*     */   private Color colorLinkHover;
/*  61 */   private static Font font = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ColumnActivityActions(String tableID)
/*     */   {
/*  68 */     super("activityActions", tableID);
/*  69 */     initializeAsGraphic(150);
/*     */     
/*  71 */     SWTSkinProperties skinProperties = SWTSkinFactory.getInstance().getSkinProperties();
/*  72 */     this.colorLinkNormal = skinProperties.getColor("color.links.normal");
/*  73 */     this.colorLinkHover = skinProperties.getColor("color.links.hover");
/*     */   }
/*     */   
/*     */   public void cellPaint(GC gc, TableCellSWT cell)
/*     */   {
/*  78 */     VuzeActivitiesEntry entry = (VuzeActivitiesEntry)cell.getDataSource();
/*  79 */     if (entry == null) {
/*  80 */       return;
/*     */     }
/*     */     
/*  83 */     TableRow row = cell.getTableRow();
/*  84 */     if (row == null) {
/*  85 */       return;
/*     */     }
/*  87 */     String text = (String)row.getData("text");
/*     */     
/*  89 */     if ((text != null) && (text.length() > 0)) {
/*  90 */       if (font == null) {
/*  91 */         FontData[] fontData = gc.getFont().getFontData();
/*  92 */         fontData[0].setStyle(1);
/*  93 */         font = new Font(gc.getDevice(), fontData);
/*     */       }
/*  95 */       gc.setFont(font);
/*     */       
/*  97 */       Rectangle bounds = getDrawBounds(cell);
/*     */       
/*  99 */       GCStringPrinter sp = new GCStringPrinter(gc, text, bounds, true, true, 16777280);
/*     */       
/*     */ 
/* 102 */       sp.calculateMetrics();
/*     */       
/* 104 */       if (sp.hasHitUrl()) {
/* 105 */         GCStringPrinter.URLInfo[] hitUrlInfo = sp.getHitUrlInfo();
/* 106 */         for (int i = 0; i < hitUrlInfo.length; i++) {
/* 107 */           GCStringPrinter.URLInfo info = hitUrlInfo[i];
/*     */           
/*     */ 
/* 110 */           info.urlUnderline = ((cell.getTableRow() == null) || (cell.getTableRow().isSelected()));
/* 111 */           if (info.urlUnderline) {
/* 112 */             info.urlColor = null;
/*     */           } else {
/* 114 */             info.urlColor = this.colorLinkNormal;
/*     */           }
/*     */         }
/* 117 */         int[] mouseOfs = cell.getMouseOffset();
/* 118 */         if (mouseOfs != null) {
/* 119 */           Rectangle realBounds = cell.getBounds();
/* 120 */           GCStringPrinter.URLInfo hitUrl = sp.getHitUrl(mouseOfs[0] + realBounds.x, mouseOfs[1] + realBounds.y);
/*     */           
/* 122 */           if (hitUrl != null) {
/* 123 */             hitUrl.urlColor = this.colorLinkHover;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 128 */       sp.printString();
/*     */     }
/*     */   }
/*     */   
/*     */   public void cellAdded(TableCell cell)
/*     */   {
/* 134 */     cell.setMarginHeight(0);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell)
/*     */   {
/* 139 */     VuzeActivitiesEntry entry = (VuzeActivitiesEntry)cell.getDataSource();
/*     */     
/* 141 */     if (entry == null) { return;
/*     */     }
/* 143 */     String[] actions = entry.getActions();
/*     */     
/* 145 */     String sort_value = "";
/*     */     
/* 147 */     for (String action : actions)
/*     */     {
/* 149 */       sort_value = sort_value + "," + action;
/*     */     }
/*     */     
/* 152 */     if (sort_value.isEmpty())
/*     */     {
/* 154 */       sort_value = entry.getTypeID();
/*     */     }
/*     */     
/* 157 */     if ((!cell.setSortValue(sort_value)) && (cell.isValid())) {
/* 158 */       return;
/*     */     }
/*     */     
/* 161 */     DownloadManager dm = entry.getDownloadManger();
/* 162 */     boolean canPlay = PlayUtils.canPlayDS(entry, -1, false);
/* 163 */     boolean canDL = (dm == null) && (entry.getDownloadManger() == null) && ((entry.getTorrent() != null) || (entry.getAssetHash() != null));
/*     */     
/* 165 */     boolean canRun = (!canPlay) && (dm != null);
/* 166 */     if ((canRun) && (dm != null) && (!dm.getAssumedComplete())) {
/* 167 */       canRun = false;
/*     */     }
/*     */     
/* 170 */     StringBuilder sb = new StringBuilder();
/* 171 */     if (canDL) {
/* 172 */       if (sb.length() > 0) {
/* 173 */         sb.append(" | ");
/*     */       }
/* 175 */       sb.append("<A HREF=\"download\">Download</A>");
/*     */     }
/*     */     
/* 178 */     if (canPlay) {
/* 179 */       if (sb.length() > 0) {
/* 180 */         sb.append(" | ");
/*     */       }
/* 182 */       sb.append("<A HREF=\"play\">Play</A>");
/*     */     }
/*     */     
/* 185 */     if (canRun) {
/* 186 */       if (sb.length() > 0) {
/* 187 */         sb.append(", ");
/*     */       }
/* 189 */       sb.append("<A HREF=\"launch\">Launch</A>");
/*     */     }
/*     */     
/* 192 */     for (String action : actions) {
/* 193 */       if (sb.length() > 0) {
/* 194 */         sb.append(", ");
/*     */       }
/* 196 */       sb.append("<A HREF=\"action:").append(action).append("\">").append(action).append("</A>");
/*     */     }
/*     */     
/* 199 */     cell.getTableRow().setData("text", sb.toString());
/*     */   }
/*     */   
/*     */   public void cellMouseTrigger(TableCellMouseEvent event)
/*     */   {
/* 204 */     VuzeActivitiesEntry entry = (VuzeActivitiesEntry)event.cell.getDataSource();
/*     */     
/* 206 */     String tooltip = null;
/* 207 */     boolean invalidateAndRefresh = false;
/*     */     
/* 209 */     Rectangle bounds = ((TableCellSWT)event.cell).getBounds();
/* 210 */     String text = (String)event.cell.getTableRow().getData("text");
/* 211 */     if (text == null) {
/* 212 */       return;
/*     */     }
/*     */     
/* 215 */     GCStringPrinter sp = null;
/* 216 */     GC gc = new GC(Display.getDefault());
/*     */     try {
/* 218 */       if (font != null) {
/* 219 */         gc.setFont(font);
/*     */       }
/* 221 */       Rectangle drawBounds = getDrawBounds((TableCellSWT)event.cell);
/* 222 */       sp = new GCStringPrinter(gc, text, drawBounds, true, true, 16777280);
/*     */       
/* 224 */       sp.calculateMetrics();
/*     */     } catch (Exception e) {
/* 226 */       Debug.out(e);
/*     */     } finally {
/* 228 */       gc.dispose();
/*     */     }
/*     */     
/* 231 */     if (sp != null) {
/* 232 */       GCStringPrinter.URLInfo hitUrl = sp.getHitUrl(event.x + bounds.x, event.y + bounds.y);
/*     */       int newCursor;
/* 234 */       if (hitUrl != null) {
/* 235 */         if (event.eventType == 1) {
/* 236 */           if (hitUrl.url.equals("download")) {
/* 237 */             String referal = null;
/* 238 */             Object ds = event.cell.getDataSource();
/* 239 */             if ((ds instanceof VuzeActivitiesEntry)) {
/* 240 */               referal = "dashboardactivity-" + ((VuzeActivitiesEntry)ds).getTypeID();
/*     */             }
/*     */             
/* 243 */             TorrentListViewsUtils.downloadDataSource(ds, false, referal);
/*     */           }
/* 245 */           else if (hitUrl.url.equals("play")) {
/* 246 */             String referal = null;
/* 247 */             Object ds = event.cell.getDataSource();
/* 248 */             if ((ds instanceof VuzeActivitiesEntry)) {
/* 249 */               referal = "playdashboardactivity-" + ((VuzeActivitiesEntry)ds).getTypeID();
/*     */             }
/*     */             
/* 252 */             TorrentListViewsUtils.playOrStreamDataSource(ds, referal, false, true);
/*     */           }
/* 254 */           else if (hitUrl.url.equals("launch"))
/*     */           {
/* 256 */             Object ds = event.cell.getDataSource();
/* 257 */             TorrentListViewsUtils.playOrStreamDataSource(ds, "launch", false, true);
/*     */ 
/*     */           }
/* 260 */           else if (hitUrl.url.startsWith("action:"))
/*     */           {
/* 262 */             entry.invokeCallback(hitUrl.url.substring(7));
/*     */           }
/* 264 */           else if (!UrlFilter.getInstance().urlCanRPC(hitUrl.url)) {
/* 265 */             Utils.launch(hitUrl.url);
/*     */           } else {
/* 267 */             UIFunctionsSWT uif = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 268 */             if (uif != null) {
/* 269 */               String target = hitUrl.target;
/* 270 */               if (target == null) {
/* 271 */                 target = "browse";
/*     */               }
/* 273 */               uif.viewURL(hitUrl.url, target, "column.activity.action");
/* 274 */               return;
/*     */             }
/*     */           }
/*     */         }
/* 278 */         Object ds = event.cell.getDataSource();
/*     */         
/* 280 */         int newCursor = 21;
/* 281 */         if (UrlFilter.getInstance().urlCanRPC(hitUrl.url)) {
/* 282 */           tooltip = hitUrl.title;
/*     */         } else {
/* 284 */           tooltip = hitUrl.url;
/*     */         }
/*     */       } else {
/* 287 */         newCursor = 0;
/*     */       }
/*     */       
/* 290 */       int oldCursor = ((TableCellSWT)event.cell).getCursorID();
/* 291 */       if (oldCursor != newCursor) {
/* 292 */         invalidateAndRefresh = true;
/* 293 */         ((TableCellSWT)event.cell).setCursorID(newCursor);
/*     */       }
/*     */     }
/*     */     
/* 297 */     Object o = event.cell.getToolTip();
/* 298 */     if ((o == null) || ((o instanceof String))) {
/* 299 */       String oldTooltip = (String)o;
/* 300 */       if (!StringCompareUtils.equals(oldTooltip, tooltip)) {
/* 301 */         invalidateAndRefresh = true;
/* 302 */         event.cell.setToolTip(tooltip);
/*     */       }
/*     */     }
/*     */     
/* 306 */     if (invalidateAndRefresh) {
/* 307 */       event.cell.invalidate();
/* 308 */       ((TableCellSWT)event.cell).redraw();
/*     */     }
/*     */   }
/*     */   
/* 312 */   boolean bMouseDowned = false;
/*     */   
/*     */   private Rectangle getDrawBounds(TableCellSWT cell) {
/* 315 */     Rectangle bounds = cell.getBounds();
/* 316 */     bounds.height -= 12;
/* 317 */     bounds.y += 6;
/* 318 */     bounds.x += 4;
/* 319 */     bounds.width -= 4;
/*     */     
/* 321 */     return bounds;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/vuzeactivity/ColumnActivityActions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */