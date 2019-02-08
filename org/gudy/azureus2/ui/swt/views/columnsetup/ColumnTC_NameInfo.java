/*     */ package org.gudy.azureus2.ui.swt.views.columnsetup;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.table.TableCellCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableView;
/*     */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*     */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.FontData;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseMoveListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellToolTipListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableRow;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
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
/*     */ public class ColumnTC_NameInfo
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener, TableCellSWTPaintListener, TableCellMouseMoveListener, TableCellToolTipListener
/*     */ {
/*     */   public static final String COLUMN_ID = "TableColumnNameInfo";
/*  52 */   public static Font fontHeader = null;
/*     */   
/*  54 */   private static String[] profText = { "beginner", "intermediate", "advanced" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ColumnTC_NameInfo(String tableID)
/*     */   {
/*  61 */     super("TableColumnNameInfo", tableID);
/*  62 */     initialize(5, -1, 415, -3);
/*     */     
/*  64 */     setType(2);
/*  65 */     setDefaultSortAscending(true);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell)
/*     */   {
/*  70 */     TableColumnCore column = (TableColumnCore)cell.getDataSource();
/*  71 */     String key = column.getTitleLanguageKey();
/*  72 */     cell.setSortValue(MessageText.getString(key, column.getName()));
/*     */   }
/*     */   
/*     */   public void cellPaint(GC gc, TableCellSWT cell)
/*     */   {
/*  77 */     TableColumnCore column = (TableColumnCore)cell.getDataSource();
/*  78 */     String raw_key = column.getTitleLanguageKey(false);
/*  79 */     String current_key = column.getTitleLanguageKey(true);
/*  80 */     Rectangle bounds = cell.getBounds();
/*  81 */     if ((bounds == null) || (bounds.isEmpty())) {
/*  82 */       return;
/*     */     }
/*     */     
/*  85 */     Font fontDefault = gc.getFont();
/*  86 */     if (fontHeader == null) {
/*  87 */       FontData[] fontData = gc.getFont().getFontData();
/*  88 */       fontData[0].setStyle(1);
/*  89 */       fontData[0].setHeight(fontData[0].getHeight() + 1);
/*  90 */       fontHeader = new Font(gc.getDevice(), fontData);
/*     */     }
/*     */     
/*  93 */     gc.setFont(fontHeader);
/*     */     
/*  95 */     bounds.y += 3;
/*  96 */     bounds.x += 7;
/*  97 */     bounds.width -= 14;
/*  98 */     String name = MessageText.getString(raw_key, column.getName());
/*     */     
/* 100 */     if (!raw_key.equals(current_key)) {
/* 101 */       String rename = MessageText.getString(current_key, "");
/* 102 */       if (rename.length() > 0) {
/* 103 */         name = name + " (->" + rename + ")";
/*     */       }
/*     */     }
/* 106 */     GCStringPrinter sp = new GCStringPrinter(gc, name, bounds, 1, 128);
/* 107 */     sp.printString();
/*     */     
/* 109 */     Point titleSize = sp.getCalculatedSize();
/*     */     
/* 111 */     gc.setFont(fontDefault);
/* 112 */     String info = MessageText.getString(raw_key + ".info", "");
/* 113 */     Rectangle infoBounds = new Rectangle(bounds.x + 10, bounds.y + titleSize.y + 5, bounds.width - 15, bounds.height - 20);
/*     */     
/* 115 */     GCStringPrinter.printString(gc, info, infoBounds, true, false);
/*     */     
/* 117 */     TableColumnInfo columnInfo = (TableColumnInfo)cell.getTableRow().getData("columninfo");
/*     */     
/* 119 */     if (columnInfo == null) {
/* 120 */       TableColumnManager tcm = TableColumnManager.getInstance();
/* 121 */       columnInfo = tcm.getColumnInfo(column.getForDataSourceType(), column.getTableID(), column.getName());
/*     */       
/* 123 */       cell.getTableRowCore().setData("columninfo", columnInfo);
/*     */     }
/* 125 */     Rectangle profBounds = new Rectangle(bounds.width - 100, bounds.y - 2, 100, 20);
/* 126 */     byte proficiency = columnInfo.getProficiency();
/* 127 */     if ((proficiency > 0) && (proficiency < profText.length)) {
/* 128 */       int alpha = gc.getAlpha();
/* 129 */       gc.setAlpha(160);
/* 130 */       GCStringPrinter.printString(gc, MessageText.getString("ConfigView.section.mode." + profText[proficiency]), profBounds, true, false, 131200);
/*     */       
/*     */ 
/*     */ 
/* 134 */       gc.setAlpha(alpha);
/*     */     }
/*     */     
/*     */ 
/* 138 */     TableView<?> tv = cell.getTableRowCore().getView();
/* 139 */     TableColumnSetupWindow tvs = (TableColumnSetupWindow)tv.getParentDataSource();
/* 140 */     Rectangle hitArea; Rectangle hitArea; if (tvs.isColumnAdded(column)) {
/* 141 */       hitArea = Utils.EMPTY_RECT;
/*     */     } else {
/* 143 */       int x = bounds.x + titleSize.x + 15;
/* 144 */       int y = bounds.y - 1;
/* 145 */       int h = 15;
/*     */       
/* 147 */       String textAdd = MessageText.getString("Button.add");
/* 148 */       GCStringPrinter sp2 = new GCStringPrinter(gc, textAdd, new Rectangle(x, y, 500, h), true, false, 16777216);
/*     */       
/* 150 */       sp2.calculateMetrics();
/* 151 */       int w = sp2.getCalculatedSize().x + 12;
/*     */       
/* 153 */       gc.setAdvanced(true);
/* 154 */       gc.setAntialias(1);
/* 155 */       gc.setBackground(ColorCache.getColor(gc.getDevice(), 255, 255, 255));
/* 156 */       gc.fillRoundRectangle(x, y, w, h, 15, h);
/* 157 */       gc.setBackground(ColorCache.getColor(gc.getDevice(), 215, 215, 215));
/* 158 */       gc.fillRoundRectangle(x + 2, y + 2, w, h, 15, h);
/* 159 */       gc.setForeground(ColorCache.getColor(gc.getDevice(), 145, 145, 145));
/* 160 */       gc.drawRoundRectangle(x, y, w, h, 15, h);
/*     */       
/* 162 */       gc.setForeground(ColorCache.getColor(gc.getDevice(), 50, 50, 50));
/* 163 */       hitArea = new Rectangle(x, y, w + 2, h);
/* 164 */       sp2.printString(gc, hitArea, 16777216);
/* 165 */       bounds = cell.getBounds();
/* 166 */       hitArea.x -= bounds.x;
/* 167 */       hitArea.y -= bounds.y;
/*     */     }
/* 169 */     cell.getTableRowCore().setData("AddHitArea", hitArea);
/*     */   }
/*     */   
/*     */   public void cellMouseTrigger(TableCellMouseEvent event)
/*     */   {
/* 174 */     if ((event.button == 1) && (event.eventType == 1) && ((event.cell instanceof TableCellCore)))
/*     */     {
/*     */ 
/* 177 */       Object data = event.cell.getTableRow().getData("AddHitArea");
/* 178 */       if ((data instanceof Rectangle)) {
/* 179 */         Rectangle hitArea = (Rectangle)data;
/* 180 */         if (hitArea.contains(event.x, event.y)) {
/* 181 */           TableView<?> tv = ((TableCellCore)event.cell).getTableRowCore().getView();
/* 182 */           TableColumnSetupWindow tvs = (TableColumnSetupWindow)tv.getParentDataSource();
/* 183 */           Object dataSource = event.cell.getDataSource();
/* 184 */           if ((dataSource instanceof TableColumnCore)) {
/* 185 */             TableColumnCore column = (TableColumnCore)dataSource;
/* 186 */             tvs.chooseColumn(column);
/*     */           }
/*     */         }
/*     */       }
/* 190 */     } else if (event.eventType == 3) {
/* 191 */       Object data = event.cell.getTableRow().getData("AddHitArea");
/* 192 */       if ((data instanceof Rectangle)) {
/* 193 */         Rectangle hitArea = (Rectangle)data;
/* 194 */         if (hitArea.contains(event.x, event.y)) {
/* 195 */           ((TableCellSWT)event.cell).setCursorID(21);
/* 196 */           return;
/*     */         }
/*     */       }
/* 199 */       ((TableCellSWT)event.cell).setCursorID(0);
/*     */     }
/*     */   }
/*     */   
/*     */   public void cellHover(TableCell cell) {}
/*     */   
/*     */   public void cellHoverComplete(TableCell cell) {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/columnsetup/ColumnTC_NameInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */