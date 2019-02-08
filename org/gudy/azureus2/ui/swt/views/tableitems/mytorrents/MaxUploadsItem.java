/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemFillListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuManager;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
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
/*     */ public class MaxUploadsItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener
/*     */ {
/*  41 */   public static final Class DATASOURCE_TYPE = Download.class;
/*     */   
/*     */   public static final String COLUMN_ID = "maxuploads";
/*     */   
/*     */   public MaxUploadsItem(String sTableID)
/*     */   {
/*  47 */     super(DATASOURCE_TYPE, "maxuploads", 2, 30, sTableID);
/*  48 */     setRefreshInterval(-2);
/*  49 */     setMinWidthAuto(true);
/*     */     
/*  51 */     TableContextMenuItem menuItem = addContextMenuItem("TableColumn.menu.maxuploads");
/*  52 */     menuItem.setStyle(5);
/*  53 */     menuItem.addFillListener(new MenuItemFillListener()
/*     */     {
/*     */       public void menuWillBeShown(MenuItem menu, Object data) {
/*  56 */         menu.removeAllChildItems();
/*     */         
/*  58 */         PluginInterface pi = PluginInitializer.getDefaultInterface();
/*  59 */         UIManager uim = pi.getUIManager();
/*  60 */         MenuManager menuManager = uim.getMenuManager();
/*     */         
/*  62 */         int iStart = COConfigurationManager.getIntParameter("Max Uploads") - 2;
/*  63 */         if (iStart < 2) iStart = 2;
/*  64 */         for (int i = iStart; i < iStart + 6; i++) {
/*  65 */           MenuItem item = menuManager.addMenuItem(menu, "MaxUploads." + i);
/*  66 */           item.setText(String.valueOf(i));
/*  67 */           item.setData(new Long(i));
/*  68 */           item.addMultiListener(new MenuItemListener() {
/*     */             public void selected(MenuItem item, Object target) {
/*  70 */               if ((target instanceof Object[])) {
/*  71 */                 Object[] targets = (Object[])target;
/*  72 */                 for (Object object : targets) {
/*  73 */                   if ((object instanceof TableRowCore)) {
/*  74 */                     TableRowCore rowCore = (TableRowCore)object;
/*  75 */                     object = rowCore.getDataSource(true);
/*     */                   }
/*  77 */                   DownloadManager dm = (DownloadManager)object;
/*  78 */                   int value = ((Long)item.getData()).intValue();
/*  79 */                   dm.setMaxUploads(value);
/*     */                 }
/*     */               }
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  90 */     info.addCategories(new String[] { "settings" });
/*     */     
/*     */ 
/*  93 */     info.setProficiency((byte)2);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell) {
/*  97 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/*  98 */     long value = dm == null ? 0L : dm.getEffectiveMaxUploads();
/*     */     
/* 100 */     if ((!cell.setSortValue(value)) && (cell.isValid()))
/* 101 */       return;
/* 102 */     cell.setText(String.valueOf(value));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/MaxUploadsItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */