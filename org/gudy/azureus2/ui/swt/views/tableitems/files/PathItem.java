/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.files;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.ui.swt.views.FilesView;
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
/*     */ public class PathItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener
/*     */ {
/*     */   public PathItem()
/*     */   {
/*  44 */     super("path", 1, -2, 200, "Files");
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  48 */     info.addCategories(new String[] { "content" });
/*     */     
/*     */ 
/*  51 */     info.setProficiency((byte)0);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell) {
/*  55 */     DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)cell.getDataSource();
/*  56 */     cell.setText(determinePath(fileInfo));
/*     */   }
/*     */   
/*     */   protected static String determinePath(DiskManagerFileInfo fileInfo)
/*     */   {
/*  61 */     if (fileInfo == null) {
/*  62 */       return "";
/*     */     }
/*     */     
/*  65 */     boolean has_link = fileInfo.getLink() != null;
/*  66 */     boolean show_full_path = FilesView.show_full_path;
/*     */     
/*  68 */     DownloadManager dm = fileInfo.getDownloadManager();
/*     */     
/*  70 */     File dl_save_path_file = dm.getAbsoluteSaveLocation();
/*     */     
/*  72 */     TOTorrent torrent = dm.getTorrent();
/*     */     
/*  74 */     if ((torrent != null) && (torrent.isSimpleTorrent()))
/*     */     {
/*  76 */       dl_save_path_file = dl_save_path_file.getParentFile();
/*     */     }
/*     */     
/*  79 */     String dl_save_path = dl_save_path_file.getPath();
/*  80 */     if (!dl_save_path.endsWith(File.separator)) {
/*  81 */       dl_save_path = dl_save_path + File.separator;
/*     */     }
/*     */     
/*  84 */     File file = fileInfo.getFile(true);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  92 */     if ((has_link) && (!show_full_path)) {
/*  93 */       show_full_path = !file.getAbsolutePath().startsWith(dl_save_path);
/*     */     }
/*  95 */     String path = "";
/*     */     
/*  97 */     if (show_full_path)
/*     */     {
/*     */       try {
/* 100 */         path = file.getParentFile().getCanonicalPath();
/*     */       }
/*     */       catch (IOException e) {
/* 103 */         path = file.getParentFile().getAbsolutePath();
/*     */       }
/*     */       
/* 106 */       if (!path.endsWith(File.separator))
/*     */       {
/* 108 */         path = path + File.separator;
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 113 */       path = file.getAbsolutePath().substring(dl_save_path.length());
/* 114 */       if (path.length() == 0) {
/* 115 */         path = File.separator;
/*     */       }
/*     */       else {
/* 118 */         if (path.charAt(0) == File.separatorChar) {
/* 119 */           path = path.substring(1);
/*     */         }
/* 121 */         int pos = path.lastIndexOf(File.separator);
/*     */         
/* 123 */         if (pos > 0) {
/* 124 */           path = File.separator + path.substring(0, pos);
/*     */         }
/*     */         else {
/* 127 */           path = File.separator;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 132 */     if (fileInfo.isSkipped())
/*     */     {
/* 134 */       String dnd_sf = dm.getDownloadState().getAttribute("dnd_sf");
/*     */       
/* 136 */       if (dnd_sf != null)
/*     */       {
/* 138 */         dnd_sf = dnd_sf.trim();
/*     */         
/* 140 */         if (dnd_sf.length() > 0)
/*     */         {
/* 142 */           if (show_full_path)
/*     */           {
/* 144 */             dnd_sf = dnd_sf + File.separatorChar;
/*     */             
/* 146 */             if (path.endsWith(dnd_sf))
/*     */             {
/* 148 */               path = path.substring(0, path.length() - dnd_sf.length());
/*     */             }
/*     */             
/*     */           }
/* 152 */           else if (path.endsWith(dnd_sf))
/*     */           {
/* 154 */             path = path.substring(0, path.length() - dnd_sf.length());
/*     */             
/* 156 */             if ((path.length() > 1) && (path.endsWith(File.separator)))
/*     */             {
/* 158 */               path = path.substring(0, path.length() - 1);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 166 */     return path;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/files/PathItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */