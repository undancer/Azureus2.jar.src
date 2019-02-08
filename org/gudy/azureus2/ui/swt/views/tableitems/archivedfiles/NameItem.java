/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.archivedfiles;
/*     */ 
/*     */ import java.io.File;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStub.DownloadStubFile;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellLightRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.ui.swt.debug.ObfusticateCellText;
/*     */ import org.gudy.azureus2.ui.swt.views.ArchivedFilesView;
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
/*     */ public class NameItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellLightRefreshListener, ObfusticateCellText
/*     */ {
/*     */   public NameItem(String tableID)
/*     */   {
/*  41 */     super("name", 1, -2, 400, tableID);
/*     */     
/*  43 */     setType(1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void fillTableColumnInfo(TableColumnInfo info)
/*     */   {
/*  50 */     info.addCategories(new String[] { "content" });
/*     */     
/*     */ 
/*     */ 
/*  54 */     info.setProficiency((byte)0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void refresh(TableCell cell, boolean sortOnlyRefresh)
/*     */   {
/*  62 */     DownloadStub.DownloadStubFile fileInfo = (DownloadStub.DownloadStubFile)cell.getDataSource();
/*     */     
/*     */     String name;
/*     */     String name;
/*  66 */     if (fileInfo == null)
/*     */     {
/*  68 */       name = "";
/*     */     }
/*     */     else
/*     */     {
/*  72 */       File f = fileInfo.getFile();
/*     */       String name;
/*  74 */       if (ArchivedFilesView.show_full_path)
/*     */       {
/*  76 */         name = f.getAbsolutePath();
/*     */       }
/*     */       else {
/*  79 */         name = f.getName();
/*     */       }
/*     */     }
/*     */     
/*  83 */     cell.setText(name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void refresh(TableCell cell)
/*     */   {
/*  90 */     refresh(cell, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getObfusticatedText(TableCell cell)
/*     */   {
/*  97 */     DownloadStub.DownloadStubFile fileInfo = (DownloadStub.DownloadStubFile)cell.getDataSource();
/*     */     
/*  99 */     String name = fileInfo == null ? "" : Debug.secretFileName(fileInfo.getFile().getName());
/*     */     
/* 101 */     return name;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/archivedfiles/NameItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */