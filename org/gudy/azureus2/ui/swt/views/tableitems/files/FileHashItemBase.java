/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.files;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.table.TableCellCore;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.security.MessageDigest;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.zip.CRC32;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*     */ import org.gudy.azureus2.core3.util.BEncoder;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableRow;
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
/*     */ public class FileHashItemBase
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener, TableCellMouseListener
/*     */ {
/*     */   protected static final String HT_CRC32 = "crc32";
/*     */   protected static final String HT_MD5 = "md5";
/*     */   protected static final String HT_SHA1 = "sha1";
/*     */   final String hash_type;
/*     */   final TableContextMenuItem menuItem;
/*     */   
/*     */   public FileHashItemBase(String _hash_type, int width)
/*     */   {
/*  71 */     super(_hash_type, 1, -1, width, "Files");
/*     */     
/*  73 */     this.hash_type = _hash_type;
/*     */     
/*  75 */     setType(1);
/*     */     
/*  77 */     setRefreshInterval(-2);
/*     */     
/*  79 */     this.menuItem = addContextMenuItem("FilesView." + this.hash_type + ".calculate");
/*     */     
/*  81 */     this.menuItem.setStyle(1);
/*     */     
/*  83 */     this.menuItem.addMultiListener(new MenuItemListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void selected(MenuItem menu, Object target)
/*     */       {
/*     */ 
/*  90 */         Object[] files = (Object[])target;
/*     */         
/*  92 */         for (Object _file : files)
/*     */         {
/*  94 */           if ((_file instanceof TableRow)) {
/*  95 */             _file = ((TableRow)_file).getDataSource();
/*     */           }
/*  97 */           DiskManagerFileInfo file = (DiskManagerFileInfo)_file;
/*     */           
/*  99 */           FileHashItemBase.updateHash(FileHashItemBase.this.hash_type, file);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void fillTableColumnInfo(TableColumnInfo info)
/*     */   {
/* 109 */     info.addCategories(new String[] { "content" });
/*     */     
/*     */ 
/* 112 */     info.setProficiency((byte)2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void cellMouseTrigger(TableCellMouseEvent event)
/*     */   {
/* 119 */     DiskManagerFileInfo file = (DiskManagerFileInfo)event.cell.getDataSource();
/*     */     
/* 121 */     if (file == null)
/*     */     {
/* 123 */       return;
/*     */     }
/*     */     
/* 126 */     TableCellCore core_cell = (TableCellCore)event.cell;
/*     */     
/* 128 */     if (!event.cell.getText().startsWith("<"))
/*     */     {
/* 130 */       core_cell.setCursorID(0);
/* 131 */       core_cell.setToolTip(null);
/*     */       
/* 133 */       return;
/*     */     }
/*     */     
/* 136 */     if (event.eventType == 4)
/*     */     {
/* 138 */       core_cell.setCursorID(21);
/* 139 */       core_cell.setToolTip(MessageText.getString("FilesView.click.info"));
/*     */     }
/* 141 */     else if (event.eventType == 5)
/*     */     {
/* 143 */       core_cell.setCursorID(0);
/* 144 */       core_cell.setToolTip(null);
/*     */     }
/*     */     
/* 147 */     if (event.eventType != 1)
/*     */     {
/* 149 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 154 */     if (event.button != 1)
/*     */     {
/* 156 */       return;
/*     */     }
/*     */     
/* 159 */     event.skipCoreFunctionality = true;
/*     */     
/* 161 */     updateHash(this.hash_type, file);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void refresh(TableCell cell)
/*     */   {
/* 168 */     DiskManagerFileInfo file = (DiskManagerFileInfo)cell.getDataSource();
/*     */     
/* 170 */     if (file == null)
/*     */     {
/* 172 */       return;
/*     */     }
/*     */     
/* 175 */     cell.setText(getHash(this.hash_type, file));
/*     */   }
/*     */   
/*     */ 
/* 179 */   private static AsyncDispatcher dispatcher = new AsyncDispatcher();
/*     */   
/* 181 */   private static Map<DiskManagerFileInfo, Set<String>> pending = new HashMap();
/*     */   
/*     */   private static volatile DiskManagerFileInfo active;
/*     */   
/*     */   private static volatile String active_hash;
/*     */   
/*     */   private static volatile int active_percent;
/*     */   
/*     */   private static boolean isFileReady(DiskManagerFileInfo file)
/*     */   {
/* 191 */     if ((file == null) || (file.getLength() != file.getDownloaded()) || (file.getAccessMode() != 1))
/*     */     {
/*     */ 
/*     */ 
/* 195 */       return false;
/*     */     }
/*     */     
/* 198 */     File f = file.getFile(true);
/*     */     
/* 200 */     if ((f.length() != file.getLength()) || (!f.canRead()))
/*     */     {
/* 202 */       return false;
/*     */     }
/*     */     
/* 205 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void updateHash(final String hash_type, DiskManagerFileInfo file)
/*     */   {
/* 213 */     if (!isFileReady(file))
/*     */     {
/* 215 */       return;
/*     */     }
/*     */     
/* 218 */     synchronized (pending)
/*     */     {
/* 220 */       Set<String> hashes = (Set)pending.get(file);
/*     */       
/* 222 */       if ((hashes != null) && (hashes.contains(hash_type)))
/*     */       {
/* 224 */         return;
/*     */       }
/*     */       
/* 227 */       if (hashes == null)
/*     */       {
/* 229 */         hashes = new HashSet();
/*     */         
/* 231 */         pending.put(file, hashes);
/*     */       }
/*     */       
/* 234 */       hashes.add(hash_type);
/*     */     }
/*     */     
/* 237 */     dispatcher.dispatch(new AERunnable()
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */         try
/*     */         {
/* 244 */           DownloadManager dm = this.val$file.getDownloadManager();
/*     */           
/* 246 */           if (dm == null)
/*     */           {
/*     */             Set<String> hashes;
/*     */             return;
/*     */           }
/* 251 */           if (!FileHashItemBase.isFileReady(this.val$file))
/*     */           {
/*     */             Set<String> hashes;
/*     */             return;
/*     */           }
/* 256 */           FileHashItemBase.access$202(0);
/* 257 */           FileHashItemBase.access$302(hash_type);
/* 258 */           FileHashItemBase.access$402(this.val$file);
/*     */           
/* 260 */           File f = this.val$file.getFile(true);
/*     */           
/* 262 */           CRC32 crc32 = null;
/* 263 */           MessageDigest md = null;
/*     */           
/* 265 */           if (hash_type == "crc32")
/*     */           {
/* 267 */             crc32 = new CRC32();
/*     */           }
/* 269 */           else if (hash_type == "md5")
/*     */           {
/* 271 */             md = MessageDigest.getInstance("md5");
/*     */           }
/*     */           else
/*     */           {
/* 275 */             md = MessageDigest.getInstance("SHA1");
/*     */           }
/*     */           
/*     */ 
/* 279 */           FileInputStream fis = new FileInputStream(f);
/*     */           
/* 281 */           long size = f.length();
/* 282 */           long done = 0L;
/*     */           
/* 284 */           if (size == 0L)
/*     */           {
/* 286 */             size = 1L;
/*     */           }
/*     */           try
/*     */           {
/* 290 */             byte[] buffer = new byte[524288];
/*     */             
/*     */             for (;;)
/*     */             {
/* 294 */               int len = fis.read(buffer);
/*     */               
/* 296 */               if (len <= 0) {
/*     */                 break;
/*     */               }
/*     */               
/*     */ 
/* 301 */               if (crc32 != null)
/*     */               {
/* 303 */                 crc32.update(buffer, 0, len);
/*     */               }
/*     */               
/* 306 */               if (md != null)
/*     */               {
/* 308 */                 md.update(buffer, 0, len);
/*     */               }
/*     */               
/* 311 */               done += len;
/*     */               
/* 313 */               FileHashItemBase.access$202((int)(1000L * done / size));
/*     */             }
/*     */             
/*     */             byte[] hash;
/*     */             byte[] hash;
/* 318 */             if (crc32 != null)
/*     */             {
/* 320 */               long val = crc32.getValue();
/*     */               
/* 322 */               hash = ByteFormatter.intToByteArray(val);
/*     */             }
/*     */             else
/*     */             {
/* 326 */               hash = md.digest();
/*     */             }
/*     */             
/* 329 */             Map other_hashes = dm.getDownloadState().getMapAttribute("fileotherhashes");
/*     */             
/* 331 */             if (other_hashes == null)
/*     */             {
/* 333 */               other_hashes = new HashMap();
/*     */             }
/*     */             else
/*     */             {
/* 337 */               other_hashes = BEncoder.cloneMap(other_hashes);
/*     */             }
/*     */             
/* 340 */             Map file_hashes = (Map)other_hashes.get(String.valueOf(this.val$file.getIndex()));
/*     */             
/* 342 */             if (file_hashes == null)
/*     */             {
/* 344 */               file_hashes = new HashMap();
/*     */               
/* 346 */               other_hashes.put(String.valueOf(this.val$file.getIndex()), file_hashes);
/*     */             }
/*     */             
/* 349 */             file_hashes.put(hash_type, hash);
/*     */             
/* 351 */             dm.getDownloadState().setMapAttribute("fileotherhashes", other_hashes);
/*     */           }
/*     */           finally
/*     */           {
/* 355 */             fis.close();
/*     */           }
/*     */         } catch (Throwable e) {
/*     */           Set<String> hashes;
/* 359 */           Debug.out(e);
/*     */         }
/*     */         finally
/*     */         {
/* 363 */           synchronized (FileHashItemBase.pending) {
/*     */             Set<String> hashes;
/* 365 */             Set<String> hashes = (Set)FileHashItemBase.pending.get(this.val$file);
/*     */             
/* 367 */             hashes.remove(hash_type);
/*     */             
/* 369 */             if (hashes.size() == 0)
/*     */             {
/* 371 */               FileHashItemBase.pending.remove(this.val$file);
/*     */             }
/*     */             
/* 374 */             FileHashItemBase.access$402(null);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String getHash(String hash_type, DiskManagerFileInfo file)
/*     */   {
/* 386 */     if (file == null)
/*     */     {
/* 388 */       return "";
/*     */     }
/*     */     
/* 391 */     DownloadManager dm = file.getDownloadManager();
/*     */     
/* 393 */     if (dm == null)
/*     */     {
/* 395 */       return "";
/*     */     }
/*     */     
/* 398 */     synchronized (pending)
/*     */     {
/* 400 */       Set<String> hashes = (Set)pending.get(file);
/*     */       
/* 402 */       if ((hashes != null) && (hashes.contains(hash_type)))
/*     */       {
/* 404 */         if ((active == file) && (active_hash == hash_type))
/*     */         {
/* 406 */           return DisplayFormatters.formatPercentFromThousands(active_percent);
/*     */         }
/*     */         
/*     */ 
/* 410 */         return "...";
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 415 */     Map other_hashes = dm.getDownloadState().getMapAttribute("fileotherhashes");
/*     */     
/* 417 */     if (other_hashes != null)
/*     */     {
/* 419 */       Map file_hashes = (Map)other_hashes.get(String.valueOf(file.getIndex()));
/*     */       
/* 421 */       if (file_hashes != null)
/*     */       {
/* 423 */         byte[] hash = (byte[])file_hashes.get(hash_type);
/*     */         
/* 425 */         if (hash != null)
/*     */         {
/* 427 */           return ByteFormatter.encodeString(hash).toLowerCase();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 432 */     if (!isFileReady(file))
/*     */     {
/* 434 */       return "";
/*     */     }
/*     */     
/* 437 */     return "<" + MessageText.getString("FilesView.click") + ">";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/files/FileHashItemBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */