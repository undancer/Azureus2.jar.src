/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.files;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCoreOperation;
/*     */ import com.aelitis.azureus.core.AzureusCoreOperationTask;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellDisposeListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellInplaceEditorListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellLightRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
/*     */ import org.gudy.azureus2.ui.swt.ImageRepository;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.debug.ObfusticateCellText;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
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
/*     */   implements TableCellLightRefreshListener, ObfusticateCellText, TableCellDisposeListener, TableCellInplaceEditorListener
/*     */ {
/*     */   private static boolean bShowIcon;
/*     */   final TableContextMenuItem menuItem;
/*     */   
/*     */   static
/*     */   {
/*  61 */     COConfigurationManager.addAndFireParameterListener("NameColumn.showProgramIcon", new ParameterListener()
/*     */     {
/*     */       public void parameterChanged(String parameterName) {
/*  64 */         NameItem.access$002(COConfigurationManager.getBooleanParameter("NameColumn.showProgramIcon"));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public NameItem()
/*     */   {
/*  73 */     super("name", 1, -2, 300, "Files");
/*     */     
/*  75 */     setObfustication(true);
/*  76 */     setInplaceEditorListener(this);
/*  77 */     setType(1);
/*  78 */     this.menuItem = addContextMenuItem("FilesView.name.fastRename", 1);
/*     */     
/*  80 */     this.menuItem.setStyle(2);
/*     */     
/*  82 */     this.menuItem.setData(Boolean.valueOf(hasInplaceEditorListener()));
/*     */     
/*  84 */     this.menuItem.addMultiListener(new MenuItemListener() {
/*     */       public void selected(MenuItem menu, Object target) {
/*  86 */         menu.setData(Boolean.valueOf(!NameItem.this.hasInplaceEditorListener()));
/*  87 */         NameItem.this.setInplaceEditorListener(NameItem.this.hasInplaceEditorListener() ? null : NameItem.this);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  93 */     info.addCategories(new String[] { "content" });
/*     */     
/*     */ 
/*  96 */     info.setProficiency((byte)0);
/*     */   }
/*     */   
/*     */   public void postConfigLoad() {
/* 100 */     setInplaceEditorListener(getUserData("noInplaceEdit") == null ? null : this);
/* 101 */     this.menuItem.setData(Boolean.valueOf(hasInplaceEditorListener()));
/*     */   }
/*     */   
/*     */   public void preConfigSave() {
/* 105 */     if (hasInplaceEditorListener()) {
/* 106 */       removeUserData("noInplaceEdit");
/*     */     } else {
/* 108 */       setUserData("noInplaceEdit", new Integer(1));
/*     */     }
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell, boolean sortOnlyRefresh) {
/* 113 */     final DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)cell.getDataSource();
/* 114 */     String name = fileInfo == null ? "" : fileInfo.getFile(true).getName();
/* 115 */     if (name == null) {
/* 116 */       name = "";
/*     */     }
/* 118 */     if (((cell.setText(name)) || (!cell.isValid())) && 
/* 119 */       (bShowIcon) && (!sortOnlyRefresh)) {
/* 120 */       Image icon = null;
/*     */       
/* 122 */       final TableCellSWT _cell = (TableCellSWT)cell;
/*     */       
/* 124 */       if (fileInfo == null) {
/* 125 */         icon = null;
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/* 130 */       else if (Utils.isSWTThread())
/*     */       {
/* 132 */         icon = ImageRepository.getPathIcon(fileInfo.getFile(true).getPath(), cell.getHeight() > 32, false);
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 138 */         Utils.execSWTThread(new Runnable()
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/*     */ 
/* 144 */             Image icon = ImageRepository.getPathIcon(fileInfo.getFile(true).getPath(), _cell.getHeight() > 32, false);
/*     */             
/*     */ 
/* 147 */             _cell.setIcon(icon);
/*     */             
/* 149 */             _cell.redraw();
/*     */           }
/*     */         });
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 158 */       if (icon != null) {
/* 159 */         _cell.setIcon(icon);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void refresh(TableCell cell)
/*     */   {
/* 167 */     refresh(cell, false);
/*     */   }
/*     */   
/*     */   public String getObfusticatedText(TableCell cell) {
/* 171 */     DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)cell.getDataSource();
/* 172 */     String name = fileInfo.getIndex() + ": " + Debug.secretFileName(fileInfo.getFile(true).getName());
/*     */     
/* 174 */     return name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void disposeCellIcon(TableCell cell)
/*     */   {
/* 181 */     Image img = ((TableCellSWT)cell).getIcon();
/* 182 */     if (img != null) {
/* 183 */       ((TableCellSWT)cell).setIcon(null);
/* 184 */       if (!img.isDisposed()) {
/* 185 */         img.dispose();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean inplaceValueSet(TableCell cell, String value, boolean finalEdit) {
/* 191 */     if ((value.equalsIgnoreCase(cell.getText())) || ("".equals(value)) || ("".equals(cell.getText())))
/* 192 */       return true;
/* 193 */     final DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)cell.getDataSource();
/*     */     
/*     */     final File target;
/*     */     try
/*     */     {
/* 198 */       target = new File(fileInfo.getFile(true).getParentFile(), value).getCanonicalFile();
/*     */     }
/*     */     catch (IOException e) {
/* 201 */       return false;
/*     */     }
/*     */     
/* 204 */     if (!finalEdit) {
/* 205 */       return !target.exists();
/*     */     }
/*     */     
/* 208 */     if (target.exists()) {
/* 209 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 213 */     final boolean[] result = { false };
/* 214 */     boolean paused = fileInfo.getDownloadManager().pause();
/* 215 */     FileUtil.runAsTask(new AzureusCoreOperationTask()
/*     */     {
/*     */       public void run(AzureusCoreOperation operation) {
/* 218 */         result[0] = fileInfo.setLink(target);
/*     */       }
/*     */     });
/* 221 */     if (paused) {
/* 222 */       fileInfo.getDownloadManager().resume();
/*     */     }
/* 224 */     if (result[0] == 0)
/*     */     {
/* 226 */       new MessageBoxShell(33, MessageText.getString("FilesView.rename.failed.title"), MessageText.getString("FilesView.rename.failed.text")).open(null);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 231 */     return true;
/*     */   }
/*     */   
/*     */   public void dispose(TableCell cell) {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/files/NameItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */