/*     */ package org.gudy.azureus2.ui.swt.views;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableDataSourceChangedListener;
/*     */ import com.aelitis.azureus.ui.common.table.TableLifeCycleListener;
/*     */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStub;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStub.DownloadStubFile;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWTMenuFillListener;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewFactory;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewTab;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.archivedfiles.NameItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.archivedfiles.SizeItem;
/*     */ import org.gudy.azureus2.ui.swt.views.utils.ManagerUtils;
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
/*     */ public class ArchivedFilesView
/*     */   extends TableViewTab<DownloadStub.DownloadStubFile>
/*     */   implements TableLifeCycleListener, TableDataSourceChangedListener, TableViewSWTMenuFillListener
/*     */ {
/*     */   private static final String TABLE_ID = "ArchivedFiles";
/*  57 */   private static final TableColumnCore[] basicItems = { new NameItem("ArchivedFiles"), new SizeItem("ArchivedFiles") };
/*     */   public static final String MSGID_PREFIX = "ArchivedFilesView";
/*     */   private TableViewSWT<DownloadStub.DownloadStubFile> tv;
/*     */   private DownloadStub current_download;
/*     */   public static boolean show_full_path;
/*     */   
/*  63 */   static { TableColumnManager tcManager = TableColumnManager.getInstance();
/*     */     
/*  65 */     tcManager.setDefaultColumnNames("ArchivedFiles", basicItems);
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
/*  77 */     COConfigurationManager.addAndFireParameterListener("ArchivedFilesView.show.full.path", new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String parameterName)
/*     */       {
/*     */ 
/*     */ 
/*  85 */         ArchivedFilesView.show_full_path = COConfigurationManager.getBooleanParameter("ArchivedFilesView.show.full.path");
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public ArchivedFilesView()
/*     */   {
/*  93 */     super("ArchivedFilesView");
/*     */   }
/*     */   
/*     */ 
/*     */   public TableViewSWT<DownloadStub.DownloadStubFile> initYourTableView()
/*     */   {
/*  99 */     this.tv = TableViewFactory.createTableViewSWT(DownloadStub.DownloadStubFile.class, "ArchivedFiles", getPropertiesPrefix(), basicItems, basicItems[0].getName(), 268500994);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 107 */     this.tv.addLifeCycleListener(this);
/* 108 */     this.tv.addMenuFillListener(this);
/* 109 */     this.tv.addTableDataSourceChangedListener(this, true);
/*     */     
/* 111 */     this.tv.setEnableTabViews(false, true, null);
/*     */     
/* 113 */     return this.tv;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void fillMenu(String sColumnName, Menu menu)
/*     */   {
/* 120 */     List<Object> ds = this.tv.getSelectedDataSources();
/*     */     
/* 122 */     final List<DownloadStub.DownloadStubFile> files = new ArrayList();
/*     */     
/* 124 */     for (Object o : ds)
/*     */     {
/* 126 */       files.add((DownloadStub.DownloadStubFile)o);
/*     */     }
/*     */     
/* 129 */     boolean hasSelection = files.size() > 0;
/*     */     
/*     */ 
/*     */ 
/* 133 */     final boolean use_open_containing_folder = COConfigurationManager.getBooleanParameter("MyTorrentsView.menu.show_parent_folder_enabled");
/*     */     
/* 135 */     MenuItem itemExplore = new MenuItem(menu, 8);
/*     */     
/* 137 */     Messages.setLanguageText(itemExplore, "MyTorrentsView.menu." + (use_open_containing_folder ? "open_parent_folder" : "explore"));
/*     */     
/*     */ 
/* 140 */     itemExplore.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/* 145 */         for (DownloadStub.DownloadStubFile file : files)
/*     */         {
/* 147 */           ManagerUtils.open(new File(file.getFile().getAbsolutePath()), use_open_containing_folder);
/*     */         }
/*     */         
/*     */       }
/* 151 */     });
/* 152 */     itemExplore.setEnabled(hasSelection);
/*     */     
/* 154 */     new MenuItem(menu, 2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addThisColumnSubMenu(String columnName, Menu menuThisColumn)
/*     */   {
/* 162 */     if (columnName.equals("name"))
/*     */     {
/* 164 */       new MenuItem(menuThisColumn, 2);
/*     */       
/* 166 */       final MenuItem path_item = new MenuItem(menuThisColumn, 32);
/*     */       
/* 168 */       path_item.setSelection(show_full_path);
/*     */       
/* 170 */       Messages.setLanguageText(path_item, "FilesView.fullpath");
/*     */       
/* 172 */       path_item.addListener(13, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 174 */           ArchivedFilesView.show_full_path = path_item.getSelection();
/* 175 */           ArchivedFilesView.this.tv.columnInvalidate("name");
/* 176 */           ArchivedFilesView.this.tv.refreshTable(false);
/* 177 */           COConfigurationManager.setParameter("ArchivedFilesView.show.full.path", ArchivedFilesView.show_full_path);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void tableDataSourceChanged(Object ds)
/*     */   {
/* 187 */     if (ds == this.current_download)
/*     */     {
/* 189 */       this.tv.setEnabled(ds != null);
/*     */       
/* 191 */       return;
/*     */     }
/*     */     
/* 194 */     boolean enabled = true;
/*     */     
/* 196 */     if ((ds instanceof DownloadStub))
/*     */     {
/* 198 */       this.current_download = ((DownloadStub)ds);
/*     */     }
/* 200 */     else if ((ds instanceof Object[]))
/*     */     {
/* 202 */       Object[] objs = (Object[])ds;
/*     */       
/* 204 */       if (objs.length != 1)
/*     */       {
/* 206 */         enabled = false;
/*     */       }
/*     */       else
/*     */       {
/* 210 */         DownloadStub stub = (DownloadStub)objs[0];
/*     */         
/* 212 */         if (stub == this.current_download)
/*     */         {
/* 214 */           return;
/*     */         }
/*     */         
/* 217 */         this.current_download = stub;
/*     */       }
/*     */     }
/*     */     else {
/* 221 */       this.current_download = null;
/*     */       
/* 223 */       enabled = false;
/*     */     }
/*     */     
/* 226 */     if (!this.tv.isDisposed())
/*     */     {
/* 228 */       this.tv.removeAllTableRows();
/*     */       
/* 230 */       this.tv.setEnabled(enabled);
/*     */       
/* 232 */       if (enabled)
/*     */       {
/* 234 */         if (this.current_download != null)
/*     */         {
/* 236 */           addExistingDatasources();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void tableViewInitialized()
/*     */   {
/* 245 */     if (this.current_download != null)
/*     */     {
/* 247 */       addExistingDatasources();
/*     */     }
/*     */     else
/*     */     {
/* 251 */       this.tv.setEnabled(false);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void addExistingDatasources()
/*     */   {
/* 263 */     if ((this.current_download == null) || (this.tv.isDisposed()))
/*     */     {
/* 265 */       return;
/*     */     }
/*     */     
/* 268 */     DownloadStub.DownloadStubFile[] files = this.current_download.getStubFiles();
/*     */     
/* 270 */     this.tv.addDataSources(files);
/*     */     
/* 272 */     this.tv.processDataSourceQueueSync();
/*     */   }
/*     */   
/*     */   public void tableViewDestroyed() {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/ArchivedFilesView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */