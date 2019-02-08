/*     */ package com.aelitis.azureus.ui.swt.columns.torrent;
/*     */ 
/*     */ import com.aelitis.azureus.core.torrent.PlatformTorrentUtils;
/*     */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefresher;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTGraphicImpl;
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
/*     */ public class ColumnUnopened
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellAddedListener, TableCellRefreshListener, TableCellMouseListener
/*     */ {
/*  49 */   public static final Class<?> DATASOURCE_TYPE = Download.class;
/*     */   public static final String COLUMN_ID = "unopened";
/*     */   private static UISWTGraphicImpl graphicCheck;
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  54 */     info.addCategories(new String[] { "content", "essential" });
/*  55 */     info.setProficiency((byte)0);
/*     */   }
/*     */   
/*     */ 
/*     */   private static UISWTGraphicImpl graphicUnCheck;
/*     */   
/*     */   private static UISWTGraphicImpl[] graphicsProgress;
/*  62 */   private static int WIDTH = 38;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ColumnUnopened(String tableID)
/*     */   {
/*  70 */     super("unopened", tableID);
/*     */     
/*  72 */     synchronized (ColumnUnopened.class)
/*     */     {
/*  74 */       if (graphicCheck == null) {
/*  75 */         Image img = ImageLoader.getInstance().getImage("image.unopened");
/*  76 */         graphicCheck = new UISWTGraphicImpl(img);
/*     */       }
/*  78 */       if (graphicUnCheck == null) {
/*  79 */         Image img = ImageLoader.getInstance().getImage("image.opened");
/*  80 */         graphicUnCheck = new UISWTGraphicImpl(img);
/*     */       }
/*     */       
/*  83 */       if (graphicsProgress == null)
/*     */       {
/*  85 */         Image[] imgs = ImageLoader.getInstance().getImages("image.sidebar.vitality.dl");
/*  86 */         graphicsProgress = new UISWTGraphicImpl[imgs.length];
/*  87 */         for (int i = 0; i < imgs.length; i++) {
/*  88 */           graphicsProgress[i] = new UISWTGraphicImpl(imgs[i]);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*  94 */     TableContextMenuItem menuItem = addContextMenuItem("label.toggle.new.marker");
/*     */     
/*  96 */     menuItem.addMultiListener(new MenuItemListener() {
/*     */       public void selected(MenuItem menu, Object target) {
/*  98 */         Object[] dataSources = (Object[])target;
/*     */         
/* 100 */         for (Object _ds : dataSources)
/*     */         {
/* 102 */           if ((_ds instanceof TableRowCore)) {
/* 103 */             TableRowCore row = (TableRowCore)_ds;
/* 104 */             _ds = row.getDataSource(true);
/*     */           }
/*     */           
/* 107 */           if ((_ds instanceof DownloadManager))
/*     */           {
/* 109 */             DownloadManager dm = (DownloadManager)_ds;
/*     */             
/* 111 */             boolean x = PlatformTorrentUtils.getHasBeenOpened(dm);
/*     */             
/* 113 */             PlatformTorrentUtils.setHasBeenOpened(dm, !x);
/*     */           }
/*     */           
/*     */         }
/*     */       }
/* 118 */     });
/* 119 */     initializeAsGraphic(WIDTH);
/*     */   }
/*     */   
/*     */   public void cellAdded(TableCell cell)
/*     */   {
/* 124 */     cell.setMarginWidth(0);
/* 125 */     cell.setMarginHeight(0);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell)
/*     */   {
/* 130 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 131 */     if (dm == null) {
/* 132 */       return;
/*     */     }
/*     */     
/* 135 */     boolean complete = dm.getAssumedComplete();
/* 136 */     boolean hasBeenOpened = false;
/* 137 */     int sortVal; int sortVal; if (complete) {
/* 138 */       hasBeenOpened = PlatformTorrentUtils.getHasBeenOpened(dm);
/* 139 */       sortVal = hasBeenOpened ? 1 : 0;
/*     */     } else {
/* 141 */       sortVal = isSortAscending() ? 2 : -1;
/*     */     }
/*     */     
/* 144 */     if ((!cell.setSortValue(sortVal)) && (cell.isValid()) && 
/* 145 */       (complete)) {
/* 146 */       return;
/*     */     }
/*     */     
/* 149 */     if (!cell.isShown()) {
/* 150 */       return;
/*     */     }
/*     */     
/* 153 */     if (complete) {
/* 154 */       cell.setGraphic(hasBeenOpened ? graphicUnCheck : graphicCheck);
/*     */     }
/* 156 */     else if (dm.getState() == 50) {
/* 157 */       int i = TableCellRefresher.getRefreshIndex(1, graphicsProgress.length);
/* 158 */       cell.setGraphic(graphicsProgress[i]);
/* 159 */       TableCellRefresher.addCell(this, cell);
/*     */     } else {
/* 161 */       cell.setGraphic(null);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void cellMouseTrigger(TableCellMouseEvent event)
/*     */   {
/* 169 */     if ((event.eventType == 1) && (event.button == 1)) {
/* 170 */       DownloadManager dm = (DownloadManager)event.cell.getDataSource();
/* 171 */       boolean complete = dm.getAssumedComplete();
/* 172 */       if (!complete) return;
/* 173 */       boolean hasBeenOpened = !PlatformTorrentUtils.getHasBeenOpened(dm);
/* 174 */       PlatformTorrentUtils.setHasBeenOpened(dm, hasBeenOpened);
/* 175 */       event.cell.setGraphic(hasBeenOpened ? graphicUnCheck : graphicCheck);
/* 176 */       event.cell.invalidate();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/torrent/ColumnUnopened.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */