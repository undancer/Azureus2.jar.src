/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerListener;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManagerListener;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemFillListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
/*     */ import org.gudy.azureus2.ui.swt.views.table.CoreTableColumnSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWT;
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
/*     */ public class RankItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener
/*     */ {
/*  62 */   public static final Class DATASOURCE_TYPE = Download.class;
/*     */   
/*     */   public static final String COLUMN_ID = "#";
/*     */   private String showIconKey;
/*     */   private boolean showIcon;
/*     */   private Image imgUp;
/*     */   private Image imgDown;
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info)
/*     */   {
/*  72 */     info.addCategories(new String[] { "content" });
/*     */   }
/*     */   
/*  75 */   private boolean bInvalidByTrigger = false;
/*     */   
/*     */   public RankItem(String sTableID)
/*     */   {
/*  79 */     super(DATASOURCE_TYPE, "#", 2, 50, sTableID);
/*  80 */     setRefreshInterval(-3);
/*  81 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/*  83 */         core.getGlobalManager().addListener(new RankItem.GMListener(RankItem.this));
/*     */       }
/*  85 */     });
/*  86 */     setMaxWidthAuto(true);
/*  87 */     setMinWidthAuto(true);
/*     */     
/*  89 */     this.showIconKey = ("RankColumn.showUpDownIcon." + (sTableID.endsWith(".big") ? "big" : "small"));
/*     */     
/*  91 */     TableContextMenuItem menuShowIcon = addContextMenuItem("ConfigView.section.style.showRankIcon", 1);
/*     */     
/*  93 */     menuShowIcon.setStyle(2);
/*  94 */     menuShowIcon.addFillListener(new MenuItemFillListener() {
/*     */       public void menuWillBeShown(MenuItem menu, Object data) {
/*  96 */         menu.setData(Boolean.valueOf(RankItem.this.showIcon));
/*     */       }
/*     */       
/*  99 */     });
/* 100 */     menuShowIcon.addMultiListener(new MenuItemListener() {
/*     */       public void selected(MenuItem menu, Object target) {
/* 102 */         COConfigurationManager.setParameter(RankItem.this.showIconKey, ((Boolean)menu.getData()).booleanValue());
/*     */       }
/*     */       
/*     */ 
/* 106 */     });
/* 107 */     COConfigurationManager.addAndFireParameterListener(this.showIconKey, new ParameterListener()
/*     */     {
/*     */       public void parameterChanged(String parameterName) {
/* 110 */         RankItem.this.invalidateCells();
/* 111 */         RankItem.this.showIcon = COConfigurationManager.getBooleanParameter(RankItem.this.showIconKey);
/*     */       }
/*     */       
/* 114 */     });
/* 115 */     ImageLoader imageLoader = ImageLoader.getInstance();
/* 116 */     this.imgUp = imageLoader.getImage("image.torrentspeed.up");
/* 117 */     this.imgDown = imageLoader.getImage("image.torrentspeed.down");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void remove()
/*     */   {
/* 124 */     super.remove();
/*     */     
/* 126 */     COConfigurationManager.removeParameter(this.showIconKey);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell) {
/* 130 */     this.bInvalidByTrigger = false;
/*     */     
/* 132 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 133 */     long value = dm == null ? 0L : dm.getPosition();
/* 134 */     String text = "" + value;
/*     */     
/* 136 */     boolean complete = dm == null ? false : dm.getAssumedComplete();
/* 137 */     if (complete) {
/* 138 */       value += 65536L;
/*     */     }
/*     */     
/* 141 */     cell.setSortValue(value);
/* 142 */     cell.setText(text);
/*     */     
/* 144 */     if ((cell instanceof TableCellSWT)) {
/* 145 */       if ((this.showIcon) && (dm != null)) {
/* 146 */         Image img = dm.getAssumedComplete() ? this.imgUp : this.imgDown;
/* 147 */         ((TableCellSWT)cell).setIcon(img);
/*     */       } else {
/* 149 */         ((TableCellSWT)cell).setIcon(null);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private class GMListener implements GlobalManagerListener {
/*     */     DownloadManagerListener listener;
/*     */     
/*     */     public GMListener() {
/* 158 */       this.listener = new DownloadManagerListener()
/*     */       {
/*     */         public void completionChanged(DownloadManager manager, boolean bCompleted) {}
/*     */         
/*     */ 
/*     */ 
/*     */         public void downloadComplete(DownloadManager manager) {}
/*     */         
/*     */ 
/*     */         public void positionChanged(DownloadManager download, int oldPosition, int newPosition)
/*     */         {
/* 169 */           if (RankItem.this.bInvalidByTrigger)
/* 170 */             return;
/* 171 */           RankItem.this.invalidateCells();
/* 172 */           RankItem.this.bInvalidByTrigger = true;
/*     */         }
/*     */         
/*     */ 
/*     */         public void stateChanged(DownloadManager manager, int state) {}
/*     */         
/*     */ 
/*     */         public void filePriorityChanged(DownloadManager download, DiskManagerFileInfo file) {}
/*     */       };
/*     */     }
/*     */     
/*     */     public void destroyed() {}
/*     */     
/*     */     public void destroyInitiated()
/*     */     {
/*     */       try
/*     */       {
/* 189 */         GlobalManager gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/* 190 */         gm.removeListener(this);
/*     */       } catch (Exception e) {
/* 192 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */     
/*     */     public void downloadManagerAdded(DownloadManager dm) {
/* 197 */       dm.addListener(this.listener);
/*     */     }
/*     */     
/*     */     public void downloadManagerRemoved(DownloadManager dm) {
/* 201 */       dm.removeListener(this.listener);
/*     */     }
/*     */     
/*     */     public void seedingStatusChanged(boolean seeding_only_mode, boolean b) {}
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/RankItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */