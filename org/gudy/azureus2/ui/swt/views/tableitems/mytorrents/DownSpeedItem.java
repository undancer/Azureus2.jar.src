/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*     */ 
/*     */ import com.aelitis.azureus.plugins.startstoprules.defaultplugin.DefaultRankCalculator;
/*     */ import com.aelitis.azureus.plugins.startstoprules.defaultplugin.StartStopRulesDefaultPlugin;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStats;
/*     */ import org.gudy.azureus2.plugins.download.DownloadTypeIncomplete;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
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
/*     */ 
/*     */ 
/*     */ public class DownSpeedItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellAddedListener
/*     */ {
/*  52 */   public static final Class DATASOURCE_TYPE = DownloadTypeIncomplete.class;
/*     */   
/*     */   public static final String COLUMN_ID = "downspeed";
/*     */   
/*     */   public DownSpeedItem(String sTableID)
/*     */   {
/*  58 */     super(DATASOURCE_TYPE, "downspeed", 2, 60, sTableID);
/*  59 */     setType(1);
/*  60 */     addDataSourceType(org.gudy.azureus2.core3.disk.DiskManagerFileInfo.class);
/*  61 */     setRefreshInterval(-2);
/*  62 */     setUseCoreDataSource(false);
/*  63 */     setMinWidthAuto(true);
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  67 */     info.addCategories(new String[] { "essential", "bytes" });
/*     */     
/*     */ 
/*     */ 
/*  71 */     info.setProficiency((byte)0);
/*     */   }
/*     */   
/*     */   public void cellAdded(TableCell cell) {
/*  75 */     cell.addRefreshListener(new RefreshListener(null));
/*     */   }
/*     */   
/*     */   private static class RefreshListener implements TableCellRefreshListener {
/*     */     private int iLastState;
/*  80 */     private int loop = 0;
/*     */     
/*     */     public void refresh(TableCell cell) {
/*  83 */       Object ds = cell.getDataSource();
/*     */       
/*     */ 
/*  86 */       if ((ds instanceof org.gudy.azureus2.plugins.disk.DiskManagerFileInfo)) {
/*     */         try
/*     */         {
/*  89 */           org.gudy.azureus2.core3.disk.DiskManagerFileInfo fileInfo = PluginCoreUtils.unwrap((org.gudy.azureus2.plugins.disk.DiskManagerFileInfo)ds);
/*     */           
/*  91 */           int speed = fileInfo.getWriteBytesPerSecond();
/*     */           
/*  93 */           if ((!cell.setSortValue(speed)) && (cell.isValid()))
/*     */           {
/*  95 */             return;
/*     */           }
/*     */           
/*  98 */           cell.setText(speed == 0 ? "" : DisplayFormatters.formatByteCountToKiBEtcPerSec(speed));
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */       
/*     */ 
/* 104 */       Download dm = (Download)ds;
/*     */       long value;
/*     */       int iState;
/* 107 */       long value; if (dm == null) {
/* 108 */         int iState = -1;
/* 109 */         value = 0L;
/*     */       } else {
/* 111 */         iState = dm.getState();
/* 112 */         value = dm.getStats().getDownloadAverage();
/*     */       }
/*     */       
/* 115 */       boolean bChangeColor = ++this.loop % 10 == 0;
/*     */       
/* 117 */       if ((cell.setSortValue(value)) || (!cell.isValid()) || (iState != this.iLastState)) {
/* 118 */         cell.setText(value == 0L ? "" : DisplayFormatters.formatByteCountToKiBEtcPerSec(value));
/* 119 */         bChangeColor = true;
/*     */       }
/*     */       
/* 122 */       if ((bChangeColor) && (dm != null)) {
/* 123 */         changeColor(cell, dm, iState);
/* 124 */         this.loop = 0;
/*     */       }
/*     */     }
/*     */     
/*     */     private void changeColor(TableCell cell, Download dl, int iState)
/*     */     {
/*     */       try {
/* 131 */         DefaultRankCalculator calc = StartStopRulesDefaultPlugin.getRankCalculator(dl);
/*     */         
/* 133 */         Color newFG = null;
/* 134 */         if ((calc != null) && (dl.getState() == 4) && (!calc.getActivelyDownloading()))
/*     */         {
/* 136 */           newFG = Colors.colorWarning;
/*     */         }
/* 138 */         cell.setForeground(Utils.colorToIntArray(newFG));
/*     */         
/* 140 */         this.iLastState = iState;
/*     */       } catch (Exception e) {
/* 142 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/DownSpeedItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */