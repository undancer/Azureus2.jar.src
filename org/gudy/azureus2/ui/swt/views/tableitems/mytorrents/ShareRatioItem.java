/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
/*     */ import org.gudy.azureus2.ui.swt.SimpleTextEntryWindow;
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
/*     */ public class ShareRatioItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener, ParameterListener
/*     */ {
/*  53 */   public static final Class DATASOURCE_TYPE = Download.class;
/*     */   
/*     */   private static final String CONFIG_ID = "StartStopManager_iFirstPriority_ShareRatio";
/*     */   public static final String COLUMN_ID = "shareRatio";
/*     */   private int iMinShareRatio;
/*  58 */   private boolean changeFG = true;
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  61 */     info.addCategories(new String[] { "sharing", "swarm" });
/*     */   }
/*     */   
/*     */   public ShareRatioItem(String sTableID)
/*     */   {
/*  66 */     super(DATASOURCE_TYPE, "shareRatio", 2, 73, sTableID);
/*  67 */     setType(1);
/*  68 */     setRefreshInterval(-2);
/*  69 */     setMinWidthAuto(true);
/*     */     
/*  71 */     setPosition(-2);
/*     */     
/*  73 */     this.iMinShareRatio = COConfigurationManager.getIntParameter("StartStopManager_iFirstPriority_ShareRatio");
/*  74 */     COConfigurationManager.addParameterListener("StartStopManager_iFirstPriority_ShareRatio", this);
/*     */     
/*  76 */     TableContextMenuItem menuItem = addContextMenuItem("label.set.share.ratio");
/*     */     
/*  78 */     menuItem.setStyle(1);
/*     */     
/*  80 */     menuItem.addMultiListener(new MenuItemListener() {
/*     */       public void selected(MenuItem menu, Object target) {
/*  82 */         Object[] dms = (Object[])target;
/*     */         
/*  84 */         SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow("set.share.ratio.win.title", "set.share.ratio.win.msg");
/*     */         
/*     */ 
/*  87 */         entryWindow.setPreenteredText("1.000", false);
/*  88 */         entryWindow.selectPreenteredText(true);
/*     */         
/*  90 */         entryWindow.prompt();
/*     */         
/*  92 */         if (entryWindow.hasSubmittedInput()) {
/*     */           try
/*     */           {
/*  95 */             String str = entryWindow.getSubmittedInput().trim();
/*     */             
/*  97 */             int share_ratio = (int)(Float.parseFloat(str) * 1000.0F);
/*     */             
/*  99 */             for (Object object : dms) {
/* 100 */               if ((object instanceof TableRowCore)) {
/* 101 */                 object = ((TableRowCore)object).getDataSource(true);
/*     */               }
/*     */               
/* 104 */               DownloadManager dm = (DownloadManager)object;
/*     */               
/* 106 */               dm.getStats().setShareRatio(share_ratio);
/*     */             }
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 111 */             Debug.out(e);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   protected void finalize() throws Throwable {
/* 119 */     super.finalize();
/* 120 */     COConfigurationManager.removeParameterListener("StartStopManager_iFirstPriority_ShareRatio", this);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell) {
/* 124 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/*     */     
/* 126 */     int sr = dm == null ? 0 : dm.getStats().getShareRatio();
/*     */     
/* 128 */     if (sr == Integer.MAX_VALUE) {
/* 129 */       sr = 2147483646;
/*     */     }
/* 131 */     if (sr == -1) {
/* 132 */       sr = Integer.MAX_VALUE;
/*     */     }
/*     */     
/* 135 */     if ((!cell.setSortValue(sr)) && (cell.isValid())) {
/* 136 */       return;
/*     */     }
/* 138 */     String shareRatio = "";
/*     */     
/* 140 */     if (sr == Integer.MAX_VALUE) {
/* 141 */       shareRatio = "∞";
/*     */     } else {
/* 143 */       shareRatio = DisplayFormatters.formatDecimal(sr / 1000.0D, 3);
/*     */     }
/*     */     
/* 146 */     if ((cell.setText(shareRatio)) && (this.changeFG)) {
/* 147 */       Color color = sr < this.iMinShareRatio ? Colors.colorWarning : null;
/* 148 */       cell.setForeground(Utils.colorToIntArray(color));
/*     */     }
/*     */   }
/*     */   
/*     */   public void parameterChanged(String parameterName) {
/* 153 */     this.iMinShareRatio = COConfigurationManager.getIntParameter("StartStopManager_iFirstPriority_ShareRatio");
/* 154 */     invalidateCells();
/*     */   }
/*     */   
/*     */   public boolean isChangeFG() {
/* 158 */     return this.changeFG;
/*     */   }
/*     */   
/*     */   public void setChangeFG(boolean changeFG) {
/* 162 */     this.changeFG = changeFG;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/ShareRatioItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */