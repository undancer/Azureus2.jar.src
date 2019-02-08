/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpFilter;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpFilterManager;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpFilterManagerFactory;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTGraphic;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IPFilterItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener
/*     */ {
/*  48 */   private static IpFilter ipfilter = null;
/*     */   
/*  50 */   public static final Class DATASOURCE_TYPE = Download.class;
/*     */   
/*     */   private static UISWTGraphic tick_icon;
/*     */   
/*     */   private static UISWTGraphic cross_icon;
/*     */   
/*     */   public static final String COLUMN_ID = "ipfilter";
/*     */   
/*     */   public IPFilterItem(String sTableID)
/*     */   {
/*  60 */     super(DATASOURCE_TYPE, "ipfilter", 3, 100, sTableID);
/*  61 */     setRefreshInterval(-2);
/*  62 */     initializeAsGraphic(-1, 100);
/*  63 */     setMinWidth(20);
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  67 */     info.addCategories(new String[] { "connection" });
/*     */     
/*     */ 
/*  70 */     info.setProficiency((byte)1);
/*     */   }
/*     */   
/*     */ 
/*     */   public void refresh(TableCell cell)
/*     */   {
/*  76 */     UISWTGraphic icon = null;
/*  77 */     int sort = 0;
/*     */     
/*  79 */     if (ipfilter == null) {
/*  80 */       ipfilter = IpFilterManagerFactory.getSingleton().getIPFilter();
/*     */     }
/*     */     
/*  83 */     if (ipfilter.isEnabled()) {
/*  84 */       if (tick_icon == null) {
/*  85 */         tick_icon = new UISWTGraphicImpl(ImageLoader.getInstance().getImage("tick_mark"));
/*  86 */         cross_icon = new UISWTGraphicImpl(ImageLoader.getInstance().getImage("cross_mark"));
/*     */       }
/*  88 */       DownloadManager dm = (DownloadManager)cell.getDataSource();
/*  89 */       if (dm != null) {
/*  90 */         boolean excluded = dm.getDownloadState().getFlag(256L);
/*     */         
/*  92 */         if (excluded)
/*     */         {
/*  94 */           icon = cross_icon;
/*  95 */           sort = 1;
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 100 */           icon = tick_icon;
/* 101 */           sort = 2;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 107 */     cell.setSortValue(sort);
/*     */     
/* 109 */     if (cell.getGraphic() != icon)
/*     */     {
/* 111 */       cell.setGraphic(icon);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/IPFilterItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */