/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*     */ 
/*     */ import com.aelitis.azureus.plugins.startstoprules.defaultplugin.DefaultRankCalculator;
/*     */ import com.aelitis.azureus.plugins.startstoprules.defaultplugin.StartStopRulesDefaultPlugin;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.core3.util.StringInterner;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStats;
/*     */ import org.gudy.azureus2.plugins.download.DownloadTypeComplete;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UpSpeedItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellAddedListener
/*     */ {
/*  51 */   public static final Class DATASOURCE_TYPE = Download.class;
/*     */   
/*     */ 
/*     */ 
/*     */   public static final String COLUMN_ID = "upspeed";
/*     */   
/*     */ 
/*     */ 
/*     */   public UpSpeedItem(String sTableID)
/*     */   {
/*  61 */     super(DATASOURCE_TYPE, "upspeed", 2, 70, sTableID);
/*  62 */     setType(1);
/*  63 */     setRefreshInterval(-2);
/*  64 */     setUseCoreDataSource(false);
/*  65 */     setMinWidthAuto(true);
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  69 */     info.addCategories(new String[] { "sharing", "bytes" });
/*     */     
/*     */ 
/*     */ 
/*  73 */     Class dsType = info.getColumn().getForDataSourceType();
/*  74 */     if (DownloadTypeComplete.class == dsType) {
/*  75 */       info.addCategories(new String[] { "essential" });
/*     */       
/*     */ 
/*  78 */       info.setProficiency((byte)0);
/*     */     }
/*     */   }
/*     */   
/*     */   public void cellAdded(TableCell cell) {
/*  83 */     cell.addRefreshListener(new RefreshListener(null));
/*     */   }
/*     */   
/*     */   private static class RefreshListener implements TableCellRefreshListener {
/*     */     private int iLastState;
/*  88 */     private int loop = 0;
/*     */     
/*     */     public void refresh(TableCell cell) {
/*  91 */       Download dm = (Download)cell.getDataSource();
/*     */       long value;
/*     */       int iState;
/*  94 */       long value; if (dm == null) {
/*  95 */         int iState = -1;
/*  96 */         value = 0L;
/*     */       } else {
/*  98 */         iState = dm.getState();
/*  99 */         value = dm.getStats().getUploadAverage();
/*     */       }
/*     */       
/* 102 */       boolean bChangeColor = ++this.loop % 10 == 0;
/*     */       
/* 104 */       if ((cell.setSortValue(value)) || (!cell.isValid()) || (iState != this.iLastState)) {
/* 105 */         cell.setText(value == 0L ? "" : StringInterner.intern(DisplayFormatters.formatByteCountToKiBEtcPerSec(value)));
/*     */         
/*     */ 
/* 108 */         bChangeColor = true;
/*     */       }
/*     */       
/* 111 */       if ((bChangeColor) && (dm != null)) {
/* 112 */         changeColor(cell, dm, iState);
/* 113 */         this.loop = 0;
/*     */       }
/*     */     }
/*     */     
/*     */     private void changeColor(TableCell cell, Download dl, int iState) {
/*     */       try {
/* 119 */         DefaultRankCalculator calc = StartStopRulesDefaultPlugin.getRankCalculator(dl);
/*     */         
/* 121 */         Color newFG = null;
/* 122 */         if ((calc != null) && (dl.getState() == 5) && (!calc.getActivelySeeding()))
/*     */         {
/* 124 */           newFG = Colors.colorWarning;
/*     */         }
/* 126 */         cell.setForeground(Utils.colorToIntArray(newFG));
/*     */         
/* 128 */         this.iLastState = iState;
/*     */       } catch (Exception e) {
/* 130 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/UpSpeedItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */