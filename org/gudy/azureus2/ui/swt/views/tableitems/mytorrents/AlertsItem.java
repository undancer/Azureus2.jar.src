/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
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
/*     */ 
/*     */ public class AlertsItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener
/*     */ {
/*  48 */   public static final Class DATASOURCE_TYPE = Download.class;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  54 */   private static final UISWTGraphic black_tick_icon = new UISWTGraphicImpl(ImageLoader.getInstance().getImage("blacktick"));
/*  55 */   private static final UISWTGraphic gray_tick_icon = new UISWTGraphicImpl(ImageLoader.getInstance().getImage("graytick"));
/*     */   
/*     */   public static final String COLUMN_ID = "alerts";
/*     */   
/*     */   public AlertsItem(String sTableID)
/*     */   {
/*  61 */     super(DATASOURCE_TYPE, "alerts", 3, 60, sTableID);
/*  62 */     addDataSourceType(DiskManagerFileInfo.class);
/*  63 */     setRefreshInterval(-2);
/*  64 */     initializeAsGraphic(-1, 60);
/*  65 */     setMinWidth(20);
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  69 */     info.addCategories(new String[] { "connection" });
/*     */     
/*     */ 
/*  72 */     info.setProficiency((byte)1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void refresh(TableCell cell)
/*     */   {
/*  79 */     UISWTGraphic icon = null;
/*  80 */     int sort = 0;
/*     */     
/*  82 */     Object ds = cell.getDataSource();
/*     */     
/*     */     String prefix;
/*  85 */     if ((ds instanceof DownloadManager))
/*     */     {
/*  87 */       DownloadManager dm = (DownloadManager)ds;
/*     */       
/*  89 */       Map<String, String> map = dm.getDownloadState().getMapAttribute("df_alerts");
/*     */       
/*  91 */       if ((map != null) && (map.size() > 0))
/*     */       {
/*  93 */         for (String k : map.keySet())
/*     */         {
/*  95 */           if (k.length() > 0)
/*     */           {
/*  97 */             if (Character.isDigit(k.charAt(0)))
/*     */             {
/*  99 */               icon = gray_tick_icon;
/* 100 */               sort = 1;
/*     */             }
/*     */             else
/*     */             {
/* 104 */               icon = black_tick_icon;
/* 105 */               sort = 2;
/*     */               
/* 107 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 112 */     } else if ((ds instanceof DiskManagerFileInfo))
/*     */     {
/* 114 */       DiskManagerFileInfo fi = (DiskManagerFileInfo)ds;
/*     */       
/* 116 */       DownloadManager dm = fi.getDownloadManager();
/*     */       
/* 118 */       Map<String, String> map = dm.getDownloadState().getMapAttribute("df_alerts");
/*     */       
/* 120 */       if ((map != null) && (map.size() > 0))
/*     */       {
/* 122 */         prefix = fi.getIndex() + ".";
/*     */         
/* 124 */         for (String k : map.keySet())
/*     */         {
/* 126 */           if (k.startsWith(prefix))
/*     */           {
/* 128 */             icon = black_tick_icon;
/* 129 */             sort = 2;
/*     */             
/* 131 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 137 */     cell.setSortValue(sort);
/*     */     
/* 139 */     if (cell.getGraphic() != icon)
/*     */     {
/* 141 */       cell.setGraphic(icon);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/AlertsItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */