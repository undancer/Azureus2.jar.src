/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.files;
/*     */ 
/*     */ import java.io.File;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellDisposeListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellLightRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.ui.swt.ImageRepository;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
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
/*     */ public class PathNameItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellLightRefreshListener, ObfusticateCellText, TableCellDisposeListener
/*     */ {
/*     */   private static boolean bShowIcon;
/*     */   
/*     */   static
/*     */   {
/*  44 */     COConfigurationManager.addAndFireParameterListener("NameColumn.showProgramIcon", new ParameterListener()
/*     */     {
/*     */       public void parameterChanged(String parameterName) {
/*  47 */         PathNameItem.access$002(COConfigurationManager.getBooleanParameter("NameColumn.showProgramIcon"));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public PathNameItem()
/*     */   {
/*  54 */     super("pathname", 1, -1, 500, "Files");
/*     */     
/*  56 */     setObfustication(true);
/*  57 */     setType(1);
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  61 */     info.addCategories(new String[] { "content" });
/*     */     
/*     */ 
/*  64 */     info.setProficiency((byte)1);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell, boolean sortOnlyRefresh)
/*     */   {
/*  69 */     final DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)cell.getDataSource();
/*  70 */     String file_name = fileInfo == null ? "" : fileInfo.getFile(true).getName();
/*  71 */     if (file_name == null)
/*  72 */       file_name = "";
/*  73 */     String file_path = PathItem.determinePath(fileInfo);
/*     */     
/*  75 */     if (!file_path.isEmpty())
/*     */     {
/*  77 */       if (!file_path.endsWith(File.separator))
/*     */       {
/*  79 */         file_path = file_path + File.separator;
/*     */       }
/*     */       
/*  82 */       file_name = file_path + file_name;
/*     */     }
/*     */     
/*  85 */     if (((cell.setText(file_name)) || (!cell.isValid())) && 
/*  86 */       (bShowIcon) && (!sortOnlyRefresh)) {
/*  87 */       Image icon = null;
/*     */       
/*  89 */       final TableCellSWT _cell = (TableCellSWT)cell;
/*     */       
/*  91 */       if (fileInfo == null) {
/*  92 */         icon = null;
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/*  97 */       else if (Utils.isSWTThread())
/*     */       {
/*  99 */         icon = ImageRepository.getPathIcon(fileInfo.getFile(true).getPath(), cell.getHeight() > 32, false);
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 105 */         Utils.execSWTThread(new Runnable()
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/*     */ 
/* 111 */             Image icon = ImageRepository.getPathIcon(fileInfo.getFile(true).getPath(), _cell.getHeight() > 32, false);
/*     */             
/*     */ 
/* 114 */             _cell.setIcon(icon);
/*     */             
/* 116 */             _cell.redraw();
/*     */           }
/*     */         });
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 125 */       if (icon != null) {
/* 126 */         _cell.setIcon(icon);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void refresh(TableCell cell)
/*     */   {
/* 134 */     refresh(cell, false);
/*     */   }
/*     */   
/*     */   public String getObfusticatedText(TableCell cell) {
/* 138 */     DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)cell.getDataSource();
/* 139 */     String name = fileInfo.getIndex() + ": " + Debug.secretFileName(fileInfo.getFile(true).getName());
/*     */     
/* 141 */     return name;
/*     */   }
/*     */   
/*     */   public void dispose(TableCell cell) {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/files/PathNameItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */