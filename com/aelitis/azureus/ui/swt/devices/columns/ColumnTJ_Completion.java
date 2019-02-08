/*     */ package com.aelitis.azureus.ui.swt.devices.columns;
/*     */ 
/*     */ import com.aelitis.azureus.core.devices.TranscodeFile;
/*     */ import com.aelitis.azureus.core.devices.TranscodeJob;
/*     */ import com.aelitis.azureus.ui.common.table.impl.TableColumnImpl;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*     */ import com.aelitis.azureus.ui.swt.utils.FontUtils;
/*     */ import java.text.NumberFormat;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.internat.MessageText.MessageTextListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellDisposeListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnExtraInfoListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter;
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
/*     */ public class ColumnTJ_Completion
/*     */   implements TableCellAddedListener, TableCellRefreshListener, TableCellDisposeListener, TableCellSWTPaintListener, TableColumnExtraInfoListener
/*     */ {
/*     */   private static final int borderWidth = 1;
/*     */   public static final String COLUMN_ID = "trancode_completion";
/*     */   private static Font fontText;
/*  60 */   private Map mapCellLastPercentDone = new HashMap();
/*     */   
/*  62 */   private int marginHeight = -1;
/*     */   
/*     */   private String na_text;
/*     */   
/*     */   Color textColor;
/*     */   
/*  68 */   NumberFormat percentage_format = NumberFormat.getPercentInstance();
/*     */   
/*  70 */   public ColumnTJ_Completion(final TableColumn column) { this.percentage_format.setMinimumFractionDigits(0);
/*  71 */     this.percentage_format.setMaximumFractionDigits(0);
/*     */     
/*     */ 
/*     */ 
/*  75 */     column.initialize(1, -2, 145);
/*  76 */     column.addListeners(this);
/*     */     
/*     */ 
/*  79 */     ((TableColumnImpl)column).addCellOtherListener("SWTPaint", this);
/*  80 */     column.setType(2);
/*  81 */     column.setRefreshInterval(-1);
/*     */     
/*  83 */     MessageText.addAndFireListener(new MessageText.MessageTextListener()
/*     */     {
/*     */       public void localeChanged(Locale old_locale, Locale new_locale) {
/*  86 */         ColumnTJ_Completion.this.na_text = MessageText.getString("general.na.short");
/*     */         
/*  88 */         column.invalidateCells();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  94 */     info.addCategories(new String[] { "essential" });
/*     */     
/*     */ 
/*  97 */     info.setProficiency((byte)0);
/*     */   }
/*     */   
/*     */   public void cellAdded(TableCell cell)
/*     */   {
/* 102 */     if (this.marginHeight != -1) {
/* 103 */       cell.setMarginHeight(this.marginHeight);
/*     */     } else {
/* 105 */       cell.setMarginHeight(2);
/*     */     }
/*     */   }
/*     */   
/*     */   public void dispose(TableCell cell)
/*     */   {
/* 111 */     this.mapCellLastPercentDone.remove(cell);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell)
/*     */   {
/* 116 */     TranscodeFile tf = (TranscodeFile)cell.getDataSource();
/*     */     
/* 118 */     int percentDone = getPerThouDone(tf);
/*     */     
/* 120 */     Integer intObj = (Integer)this.mapCellLastPercentDone.get(cell);
/* 121 */     int lastPercentDone = intObj == null ? 0 : intObj.intValue();
/*     */     
/* 123 */     if ((!cell.setSortValue(percentDone)) && (cell.isValid()) && (lastPercentDone == percentDone)) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void cellPaint(GC gcImage, TableCellSWT cell)
/*     */   {
/* 131 */     TranscodeFile tf = (TranscodeFile)cell.getDataSource();
/*     */     
/* 133 */     int perThouDone = getPerThouDone(tf);
/*     */     
/* 135 */     Rectangle bounds = cell.getBounds();
/*     */     
/* 137 */     int yOfs = (bounds.height - 13) / 2;
/* 138 */     int x1 = bounds.width - 1 - 2;
/* 139 */     int y1 = bounds.height - 3 - yOfs;
/*     */     
/* 141 */     if ((x1 < 10) || (y1 < 3)) {
/* 142 */       return;
/*     */     }
/*     */     
/* 145 */     this.mapCellLastPercentDone.put(cell, new Integer(perThouDone));
/*     */     
/* 147 */     ImageLoader imageLoader = ImageLoader.getInstance();
/* 148 */     Image imgEnd = imageLoader.getImage("tc_bar_end");
/* 149 */     Image img0 = imageLoader.getImage("tc_bar_0");
/* 150 */     Image img1 = imageLoader.getImage("tc_bar_1");
/*     */     
/*     */ 
/* 153 */     if (!imgEnd.isDisposed()) {
/* 154 */       gcImage.drawImage(imgEnd, bounds.x, bounds.y + yOfs);
/* 155 */       gcImage.drawImage(imgEnd, bounds.x + x1 + 1, bounds.y + yOfs);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 160 */     int limit = x1 * perThouDone / 1000;
/*     */     
/* 162 */     if ((!img1.isDisposed()) && (limit > 0)) {
/* 163 */       Rectangle imgBounds = img1.getBounds();
/* 164 */       gcImage.drawImage(img1, 0, 0, imgBounds.width, imgBounds.height, bounds.x + 1, bounds.y + yOfs, limit, imgBounds.height);
/*     */     }
/*     */     
/* 167 */     if ((perThouDone < 1000) && (!img0.isDisposed())) {
/* 168 */       Rectangle imgBounds = img0.getBounds();
/* 169 */       gcImage.drawImage(img0, 0, 0, imgBounds.width, imgBounds.height, bounds.x + limit + 1, bounds.y + yOfs, x1 - limit, imgBounds.height);
/*     */     }
/*     */     
/*     */ 
/* 173 */     imageLoader.releaseImage("tc_bar_end");
/* 174 */     imageLoader.releaseImage("tc_bar_0");
/* 175 */     imageLoader.releaseImage("tc_bar_1");
/*     */     
/* 177 */     if (this.textColor == null) {
/* 178 */       this.textColor = ColorCache.getColor(gcImage.getDevice(), "#006600");
/*     */     }
/*     */     
/* 181 */     gcImage.setForeground(this.textColor);
/*     */     
/* 183 */     if (fontText == null) {
/* 184 */       fontText = FontUtils.getFontWithHeight(gcImage.getFont(), gcImage, 10);
/*     */     }
/*     */     
/* 187 */     gcImage.setFont(fontText);
/*     */     
/*     */     String sText;
/*     */     String sText;
/* 191 */     if ((tf != null) && (perThouDone == 1000) && (!tf.getTranscodeRequired()))
/*     */     {
/* 193 */       sText = this.na_text;
/*     */     }
/*     */     else
/*     */     {
/* 197 */       sText = this.percentage_format.format(perThouDone / 1000.0D);
/*     */       
/* 199 */       if ((tf != null) && (perThouDone < 1000))
/*     */       {
/* 201 */         String eta = getETA(tf);
/*     */         
/* 203 */         if (eta != null)
/*     */         {
/* 205 */           sText = sText + " - " + eta;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 210 */     GCStringPrinter.printString(gcImage, sText, new Rectangle(bounds.x + 4, bounds.y + yOfs, bounds.width - 4, 13), true, false, 16777216);
/*     */   }
/*     */   
/*     */ 
/*     */   private int getPerThouDone(TranscodeFile tf)
/*     */   {
/* 216 */     if (tf == null) {
/* 217 */       return 0;
/*     */     }
/* 219 */     TranscodeJob job = tf.getJob();
/* 220 */     if (job == null) {
/* 221 */       return tf.isComplete() ? 1000 : 0;
/*     */     }
/* 223 */     return job.getPercentComplete() * 10;
/*     */   }
/*     */   
/*     */   private String getETA(TranscodeFile tf) {
/* 227 */     if (tf == null) {
/* 228 */       return null;
/*     */     }
/* 230 */     TranscodeJob job = tf.getJob();
/* 231 */     if (job == null) {
/* 232 */       return null;
/*     */     }
/* 234 */     return job.getETA();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/devices/columns/ColumnTJ_Completion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */