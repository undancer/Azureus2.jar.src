/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*     */ 
/*     */ import java.io.File;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
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
/*     */ public class FileExtensionItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener
/*     */ {
/*  33 */   public static final Class DATASOURCE_TYPE = Download.class;
/*     */   
/*     */   public static final String COLUMN_ID = "fileext";
/*     */   
/*     */ 
/*     */   public FileExtensionItem(String sTableID)
/*     */   {
/*  40 */     super(DATASOURCE_TYPE, "fileext", 3, 50, sTableID);
/*  41 */     addDataSourceType(DiskManagerFileInfo.class);
/*  42 */     setMinWidthAuto(true);
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  46 */     info.addCategories(new String[] { "content" });
/*     */     
/*     */ 
/*  49 */     info.setProficiency((byte)1);
/*     */   }
/*     */   
/*     */ 
/*     */   public void refresh(TableCell cell)
/*     */   {
/*  55 */     Object ds = cell.getDataSource();
/*     */     
/*  57 */     String text = "";
/*     */     
/*     */ 
/*     */ 
/*  61 */     if ((ds instanceof DownloadManager))
/*     */     {
/*  63 */       DownloadManager dm = (DownloadManager)ds;
/*     */       
/*  65 */       DiskManagerFileInfo prim = dm.getDownloadState().getPrimaryFile();
/*     */       
/*  67 */       text = prim == null ? "" : prim.getFile(true).getName();
/*     */     }
/*  69 */     else if ((ds instanceof DiskManagerFileInfo))
/*     */     {
/*  71 */       DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)ds;
/*     */       
/*  73 */       DownloadManager dm = fileInfo.getDownloadManager();
/*     */       
/*  75 */       text = fileInfo.getFile(true).getName();
/*     */     }
/*     */     else
/*     */     {
/*     */       return;
/*     */     }
/*     */     DownloadManager dm;
/*  82 */     String incomp_suffix = dm == null ? null : dm.getDownloadState().getAttribute("incompfilesuffix");
/*     */     
/*  84 */     if ((incomp_suffix != null) && (text.endsWith(incomp_suffix)))
/*     */     {
/*  86 */       text = text.substring(0, text.length() - incomp_suffix.length());
/*     */     }
/*     */     
/*  89 */     int pos = text.lastIndexOf(".");
/*     */     
/*  91 */     if (pos >= 0)
/*     */     {
/*  93 */       text = text.substring(pos + 1);
/*     */     }
/*     */     else
/*     */     {
/*  97 */       text = "";
/*     */     }
/*     */     
/* 100 */     cell.setText(text);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/FileExtensionItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */