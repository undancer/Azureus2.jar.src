/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*     */ import java.io.File;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.ui.UIInputReceiver;
/*     */ import org.gudy.azureus2.plugins.ui.UIInputReceiverListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellDisposeListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellLightRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
/*     */ import org.gudy.azureus2.ui.swt.ImageRepository;
/*     */ import org.gudy.azureus2.ui.swt.SimpleTextEntryWindow;
/*     */ import org.gudy.azureus2.ui.swt.debug.ObfusticateCellText;
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
/*     */ public class NameItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellLightRefreshListener, ObfusticateCellText, TableCellDisposeListener
/*     */ {
/*  54 */   public static final Class DATASOURCE_TYPE = Download.class;
/*     */   
/*     */   public static final String COLUMN_ID = "name";
/*     */   private boolean showIcon;
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info)
/*     */   {
/*  61 */     info.addCategories(new String[] { "essential", "content" });
/*  62 */     info.setProficiency((byte)0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public NameItem(String sTableID)
/*     */   {
/*  70 */     super(DATASOURCE_TYPE, "name", 1, 250, sTableID);
/*  71 */     setObfustication(true);
/*  72 */     setRefreshInterval(-2);
/*  73 */     setType(1);
/*  74 */     setMinWidth(100);
/*     */     
/*  76 */     TableContextMenuItem menuItem = addContextMenuItem("MyTorrentsView.menu.rename.displayed");
/*  77 */     menuItem.addMultiListener(new MenuItemListener() {
/*     */       public void selected(MenuItem menu, Object target) {
/*  79 */         if (target == null) {
/*  80 */           return;
/*     */         }
/*  82 */         Object[] o = (Object[])target;
/*  83 */         for (Object object : o) {
/*  84 */           if ((object instanceof TableRowCore)) {
/*  85 */             object = ((TableRowCore)object).getDataSource(true);
/*     */           }
/*  87 */           if ((object instanceof DownloadManager)) {
/*  88 */             final DownloadManager dm = (DownloadManager)object;
/*  89 */             String msg_key_prefix = "MyTorrentsView.menu.rename.displayed.enter.";
/*     */             
/*  91 */             SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow(msg_key_prefix + "title", msg_key_prefix + "message");
/*     */             
/*  93 */             entryWindow.setPreenteredText(dm.getDisplayName(), false);
/*  94 */             entryWindow.maintainWhitespace(true);
/*  95 */             entryWindow.prompt(new UIInputReceiverListener() {
/*     */               public void UIInputReceiverClosed(UIInputReceiver entryWindow) {
/*  97 */                 if (!entryWindow.hasSubmittedInput()) {
/*  98 */                   return;
/*     */                 }
/* 100 */                 String value = entryWindow.getSubmittedInput();
/* 101 */                 if ((value != null) && (value.length() > 0)) {
/* 102 */                   dm.getDownloadState().setDisplayName(value);
/*     */                 }
/*     */                 
/*     */               }
/*     */               
/*     */             });
/*     */           }
/*     */         }
/*     */       }
/* 111 */     });
/* 112 */     COConfigurationManager.addAndFireParameterListener("NameColumn.showProgramIcon", new ParameterListener()
/*     */     {
/*     */       public void parameterChanged(String parameterName) {
/* 115 */         NameItem.this.setShowIcon(COConfigurationManager.getBooleanParameter("NameColumn.showProgramIcon"));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void reset() {
/* 121 */     super.reset();
/* 122 */     COConfigurationManager.removeParameter("NameColumn.showProgramIcon");
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell)
/*     */   {
/* 127 */     refresh(cell, false);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell, boolean sortOnlyRefresh)
/*     */   {
/* 132 */     String name = null;
/* 133 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 134 */     if (dm != null)
/* 135 */       name = dm.getDisplayName();
/* 136 */     if (name == null) {
/* 137 */       name = "";
/*     */     }
/*     */     
/* 140 */     if (((cell.setText(name)) || (!cell.isValid())) && 
/* 141 */       (dm != null) && (isShowIcon()) && (!sortOnlyRefresh) && ((cell instanceof TableCellSWT)))
/*     */     {
/* 143 */       DiskManagerFileInfo fileInfo = dm.getDownloadState().getPrimaryFile();
/* 144 */       if (fileInfo != null)
/*     */       {
/* 146 */         TOTorrent torrent = dm.getTorrent();
/* 147 */         Image icon = ImageRepository.getPathIcon(fileInfo.getFile(false).getName(), false, (torrent != null) && (!torrent.isSimpleTorrent()));
/*     */         
/*     */ 
/* 150 */         ((TableCellSWT)cell).setIcon(icon);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public String getObfusticatedText(TableCell cell)
/*     */   {
/* 157 */     String name = null;
/* 158 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 159 */     if (dm != null) {
/* 160 */       name = dm.toString();
/* 161 */       int i = name.indexOf('#');
/* 162 */       if (i > 0) {
/* 163 */         name = name.substring(i + 1);
/*     */       }
/*     */     }
/*     */     
/* 167 */     if (name == null)
/* 168 */       name = "";
/* 169 */     return name;
/*     */   }
/*     */   
/*     */ 
/*     */   public void dispose(TableCell cell) {}
/*     */   
/*     */   private void disposeCellIcon(TableCell cell)
/*     */   {
/* 177 */     if (!(cell instanceof TableCellSWT)) {
/* 178 */       return;
/*     */     }
/* 180 */     Image img = ((TableCellSWT)cell).getIcon();
/* 181 */     if (img != null) {
/* 182 */       ((TableCellSWT)cell).setIcon(null);
/* 183 */       if (!img.isDisposed()) {
/* 184 */         img.dispose();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setShowIcon(boolean showIcon)
/*     */   {
/* 194 */     this.showIcon = showIcon;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isShowIcon()
/*     */   {
/* 202 */     return this.showIcon;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/NameItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */