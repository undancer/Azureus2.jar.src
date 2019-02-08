/*     */ package org.gudy.azureus2.core3.torrent.impl;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.zip.GZIPOutputStream;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentProgressListener;
/*     */ import org.gudy.azureus2.core3.util.BEncoder;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TOTorrentCreateImpl
/*     */   extends TOTorrentImpl
/*     */   implements TOTorrentFileHasherListener
/*     */ {
/*     */   private static final Comparator<File> file_comparator;
/*     */   private File torrent_base;
/*     */   private long piece_length;
/*     */   private TOTorrentFileHasher file_hasher;
/*     */   
/*     */   static
/*     */   {
/*  42 */     if (System.getProperty("az.create.torrent.alphanumeric.sort", "0").equals("1"))
/*     */     {
/*  44 */       file_comparator = new Comparator()
/*     */       {
/*     */ 
/*     */ 
/*     */         public int compare(File f1, File f2)
/*     */         {
/*     */ 
/*     */ 
/*  52 */           String s1 = f1.getName();
/*  53 */           String s2 = f2.getName();
/*     */           
/*  55 */           int l1 = s1.length();
/*  56 */           int l2 = s2.length();
/*     */           
/*  58 */           int c1_pos = 0;
/*  59 */           int c2_pos = 0;
/*     */           
/*  61 */           while ((c1_pos < l1) && (c2_pos < l2))
/*     */           {
/*  63 */             char c1 = s1.charAt(c1_pos++);
/*  64 */             char c2 = s2.charAt(c2_pos++);
/*     */             
/*  66 */             if ((Character.isDigit(c1)) && (Character.isDigit(c2)))
/*     */             {
/*  68 */               int n1_pos = c1_pos - 1;
/*  69 */               int n2_pos = c2_pos - 1;
/*     */               
/*  71 */               while (c1_pos < l1)
/*     */               {
/*  73 */                 if (!Character.isDigit(s1.charAt(c1_pos))) {
/*     */                   break;
/*     */                 }
/*     */                 
/*     */ 
/*  78 */                 c1_pos++;
/*     */               }
/*     */               
/*  81 */               while (c2_pos < l2)
/*     */               {
/*  83 */                 if (!Character.isDigit(s2.charAt(c2_pos))) {
/*     */                   break;
/*     */                 }
/*     */                 
/*     */ 
/*  88 */                 c2_pos++;
/*     */               }
/*     */               
/*  91 */               int n1_length = c1_pos - n1_pos;
/*  92 */               int n2_length = c2_pos - n2_pos;
/*     */               
/*  94 */               if (n1_length != n2_length)
/*     */               {
/*  96 */                 return n1_length - n2_length;
/*     */               }
/*     */               
/*  99 */               for (int i = 0; i < n1_length; i++)
/*     */               {
/* 101 */                 char nc1 = s1.charAt(n1_pos++);
/* 102 */                 char nc2 = s2.charAt(n2_pos++);
/*     */                 
/* 104 */                 if (nc1 != nc2)
/*     */                 {
/* 106 */                   return nc1 - nc2;
/*     */                 }
/*     */                 
/*     */               }
/*     */             }
/*     */             else
/*     */             {
/* 113 */               c1 = Character.toLowerCase(c1);
/*     */               
/* 115 */               c2 = Character.toLowerCase(c2);
/*     */               
/*     */ 
/* 118 */               if (c1 != c2)
/*     */               {
/* 120 */                 return c1 - c2;
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 125 */           return l1 - l2;
/*     */         }
/*     */         
/*     */       };
/*     */     } else {
/* 130 */       file_comparator = null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 139 */   private long total_file_size = -1L;
/* 140 */   private long total_file_count = 0L;
/*     */   
/*     */   private long piece_count;
/*     */   
/*     */   private boolean add_other_hashes;
/* 145 */   private final List<TOTorrentProgressListener> progress_listeners = new ArrayList();
/*     */   
/*     */   private int reported_progress;
/*     */   
/* 149 */   private Set<String> ignore_set = new HashSet();
/*     */   
/*     */   private Map<String, File> linkage_map;
/* 152 */   private final Map<String, String> linked_tf_map = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean cancelled;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected TOTorrentCreateImpl(Map<String, File> _linkage_map, File _torrent_base, URL _announce_url, boolean _add_other_hashes, long _piece_length)
/*     */     throws TOTorrentException
/*     */   {
/* 166 */     super(_torrent_base.getName(), _announce_url, _torrent_base.isFile());
/*     */     
/* 168 */     this.linkage_map = _linkage_map;
/* 169 */     this.torrent_base = _torrent_base;
/* 170 */     this.piece_length = _piece_length;
/* 171 */     this.add_other_hashes = _add_other_hashes;
/*     */   }
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
/*     */   protected TOTorrentCreateImpl(Map<String, File> _linkage_map, File _torrent_base, URL _announce_url, boolean _add_other_hashes, long _piece_min_size, long _piece_max_size, long _piece_num_lower, long _piece_num_upper)
/*     */     throws TOTorrentException
/*     */   {
/* 187 */     super(_torrent_base.getName(), _announce_url, _torrent_base.isFile());
/*     */     
/* 189 */     this.linkage_map = _linkage_map;
/* 190 */     this.torrent_base = _torrent_base;
/* 191 */     this.add_other_hashes = _add_other_hashes;
/*     */     
/* 193 */     long total_size = calculateTotalFileSize(_torrent_base);
/*     */     
/* 195 */     this.piece_length = getComputedPieceSize(total_size, _piece_min_size, _piece_max_size, _piece_num_lower, _piece_num_upper);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void create()
/*     */     throws TOTorrentException
/*     */   {
/* 203 */     int ignored = constructFixed(this.torrent_base, this.piece_length);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 208 */     if ((this.linkage_map.size() > 0) && (this.linkage_map.size() != this.linked_tf_map.size() + ignored))
/*     */     {
/*     */ 
/* 211 */       throw new TOTorrentException("TOTorrentCreate: unresolved linkages: required=" + this.linkage_map + ", resolved=" + this.linked_tf_map, 6);
/*     */     }
/*     */     
/*     */ 
/* 215 */     if (this.linked_tf_map.size() > 0)
/*     */     {
/* 217 */       Map m = getAdditionalMapProperty("azureus_private_properties");
/*     */       
/* 219 */       if (m == null)
/*     */       {
/* 221 */         m = new HashMap();
/*     */         
/* 223 */         setAdditionalMapProperty("azureus_private_properties", m);
/*     */       }
/*     */       
/* 226 */       if (this.linked_tf_map.size() < 100)
/*     */       {
/* 228 */         m.put("initial_linkage", this.linked_tf_map);
/*     */       }
/*     */       else
/*     */       {
/* 232 */         ByteArrayOutputStream baos = new ByteArrayOutputStream(102400);
/*     */         try
/*     */         {
/* 235 */           GZIPOutputStream gos = new GZIPOutputStream(baos);
/*     */           
/* 237 */           gos.write(BEncoder.encode(this.linked_tf_map));
/*     */           
/* 239 */           gos.close();
/*     */           
/* 241 */           m.put("initial_linkage2", baos.toByteArray());
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 245 */           throw new TOTorrentException("Failed to serialise linkage", 5);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int constructFixed(File _torrent_base, long _piece_length)
/*     */     throws TOTorrentException
/*     */   {
/* 258 */     setIgnoreList();
/*     */     
/* 260 */     setCreationDate(SystemTime.getCurrentTime() / 1000L);
/*     */     
/* 262 */     setCreatedBy("Azureus/5.7.6.0");
/*     */     
/* 264 */     setPieceLength(_piece_length);
/*     */     
/* 266 */     report("Torrent.create.progress.piecelength", _piece_length);
/*     */     
/* 268 */     this.piece_count = calculateNumberOfPieces(_torrent_base, _piece_length);
/*     */     
/* 270 */     if (this.piece_count == 0L)
/*     */     {
/* 272 */       throw new TOTorrentException("TOTorrentCreate: specified files have zero total length", 2);
/*     */     }
/*     */     
/*     */ 
/* 276 */     report("Torrent.create.progress.hashing");
/*     */     
/* 278 */     for (int i = 0; i < this.progress_listeners.size(); i++)
/*     */     {
/* 280 */       ((TOTorrentProgressListener)this.progress_listeners.get(i)).reportProgress(0);
/*     */     }
/*     */     
/* 283 */     boolean add_other_per_file_hashes = (this.add_other_hashes) && (!getSimpleTorrent());
/*     */     
/* 285 */     this.file_hasher = new TOTorrentFileHasher(this.add_other_hashes, add_other_per_file_hashes, (int)_piece_length, this.progress_listeners.size() == 0 ? null : this);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 292 */     int ignored = 0;
/*     */     try
/*     */     {
/* 295 */       if (this.cancelled)
/*     */       {
/* 297 */         throw new TOTorrentException("TOTorrentCreate: operation cancelled", 9);
/*     */       }
/*     */       
/*     */ 
/* 301 */       if (getSimpleTorrent())
/*     */       {
/* 303 */         File link = (File)this.linkage_map.get(_torrent_base.getName());
/*     */         
/* 305 */         if (link != null)
/*     */         {
/* 307 */           this.linked_tf_map.put("0", link.getAbsolutePath());
/*     */         }
/*     */         
/* 310 */         long length = this.file_hasher.add(link == null ? _torrent_base : link);
/*     */         
/* 312 */         setFiles(new TOTorrentFileImpl[] { new TOTorrentFileImpl(this, 0, 0L, length, new byte[][] { getName() }) });
/*     */         
/* 314 */         setPieces(this.file_hasher.getPieces());
/*     */       }
/*     */       else
/*     */       {
/* 318 */         List<TOTorrentFileImpl> encoded = new ArrayList();
/*     */         
/* 320 */         ignored = processDir(this.file_hasher, _torrent_base, encoded, _torrent_base.getName(), "");
/*     */         
/* 322 */         TOTorrentFileImpl[] files = new TOTorrentFileImpl[encoded.size()];
/*     */         
/* 324 */         encoded.toArray(files);
/*     */         
/* 326 */         setFiles(files);
/*     */       }
/*     */       
/* 329 */       setPieces(this.file_hasher.getPieces());
/*     */       byte[] sha1_digest;
/* 331 */       if (this.add_other_hashes)
/*     */       {
/* 333 */         sha1_digest = this.file_hasher.getSHA1Digest();
/* 334 */         byte[] ed2k_digest = this.file_hasher.getED2KDigest();
/*     */         
/* 336 */         addAdditionalInfoProperty("sha1", sha1_digest);
/* 337 */         addAdditionalInfoProperty("ed2k", ed2k_digest);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 343 */       return ignored;
/*     */     }
/*     */     finally
/*     */     {
/* 347 */       this.file_hasher = null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int processDir(TOTorrentFileHasher hasher, File dir, List<TOTorrentFileImpl> encoded, String base_name, String root)
/*     */     throws TOTorrentException
/*     */   {
/* 361 */     File[] dir_file_list = dir.listFiles();
/*     */     
/* 363 */     if (dir_file_list == null)
/*     */     {
/* 365 */       throw new TOTorrentException("TOTorrentCreate: directory '" + dir.getAbsolutePath() + "' returned error when listing files in it", 1);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 372 */     List<File> file_list = new ArrayList(Arrays.asList(dir_file_list));
/*     */     
/* 374 */     if (file_comparator == null)
/*     */     {
/* 376 */       Collections.sort(file_list);
/*     */     }
/*     */     else
/*     */     {
/* 380 */       Collections.sort(file_list, file_comparator);
/*     */     }
/*     */     
/* 383 */     long offset = 0L;
/*     */     
/* 385 */     int ignored = 0;
/*     */     
/* 387 */     for (int i = 0; i < file_list.size(); i++)
/*     */     {
/* 389 */       File file = (File)file_list.get(i);
/*     */       
/* 391 */       String file_name = file.getName();
/*     */       
/* 393 */       if ((!file_name.equals(".")) && (!file_name.equals("..")))
/*     */       {
/* 395 */         if (file.isDirectory())
/*     */         {
/* 397 */           if (root.length() > 0)
/*     */           {
/* 399 */             file_name = root + File.separator + file_name;
/*     */           }
/*     */           
/* 402 */           ignored += processDir(hasher, file, encoded, base_name, file_name);
/*     */ 
/*     */ 
/*     */         }
/* 406 */         else if (ignoreFile(file_name))
/*     */         {
/* 408 */           ignored++;
/*     */         }
/*     */         else
/*     */         {
/* 412 */           if (root.length() > 0)
/*     */           {
/* 414 */             file_name = root + File.separator + file_name;
/*     */           }
/*     */           
/* 417 */           File link = (File)this.linkage_map.get(base_name + File.separator + file_name);
/*     */           
/* 419 */           if (link != null)
/*     */           {
/* 421 */             this.linked_tf_map.put(String.valueOf(encoded.size()), link.getAbsolutePath());
/*     */           }
/*     */           
/* 424 */           long length = hasher.add(link == null ? file : link);
/*     */           
/* 426 */           TOTorrentFileImpl tf = new TOTorrentFileImpl(this, i, offset, length, file_name);
/*     */           
/* 428 */           offset += length;
/*     */           
/* 430 */           if (this.add_other_hashes)
/*     */           {
/* 432 */             byte[] ed2k_digest = hasher.getPerFileED2KDigest();
/* 433 */             byte[] sha1_digest = hasher.getPerFileSHA1Digest();
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 438 */             tf.setAdditionalProperty("sha1", sha1_digest);
/* 439 */             tf.setAdditionalProperty("ed2k", ed2k_digest);
/*     */           }
/*     */           
/* 442 */           encoded.add(tf);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 448 */     return ignored;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void pieceHashed(int piece_number)
/*     */   {
/* 455 */     for (int i = 0; i < this.progress_listeners.size(); i++)
/*     */     {
/* 457 */       int this_progress = (int)(piece_number * 100 / this.piece_count);
/*     */       
/* 459 */       if (this_progress != this.reported_progress)
/*     */       {
/* 461 */         this.reported_progress = this_progress;
/*     */         
/* 463 */         ((TOTorrentProgressListener)this.progress_listeners.get(i)).reportProgress(this.reported_progress);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected long calculateNumberOfPieces(File _file, long _piece_length)
/*     */     throws TOTorrentException
/*     */   {
/* 475 */     long res = getPieceCount(calculateTotalFileSize(_file), _piece_length);
/*     */     
/* 477 */     report("Torrent.create.progress.piececount", "" + res);
/*     */     
/* 479 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected long calculateTotalFileSize(File file)
/*     */     throws TOTorrentException
/*     */   {
/* 488 */     if (this.total_file_size == -1L)
/*     */     {
/* 490 */       this.total_file_size = getTotalFileSize(file);
/*     */     }
/*     */     
/* 493 */     return this.total_file_size;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected long getTotalFileSize(File file)
/*     */     throws TOTorrentException
/*     */   {
/* 502 */     report("Torrent.create.progress.parsingfiles");
/*     */     
/* 504 */     long res = getTotalFileSizeSupport(file, "");
/*     */     
/* 506 */     report("Torrent.create.progress.totalfilesize", res);
/*     */     
/* 508 */     report("Torrent.create.progress.totalfilecount", "" + this.total_file_count);
/*     */     
/* 510 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected long getTotalFileSizeSupport(File file, String root)
/*     */     throws TOTorrentException
/*     */   {
/* 520 */     String name = file.getName();
/*     */     
/* 522 */     if ((name.equals(".")) || (name.equals("..")))
/*     */     {
/* 524 */       return 0L;
/*     */     }
/*     */     
/* 527 */     if (!file.exists())
/*     */     {
/* 529 */       throw new TOTorrentException("TOTorrentCreate: file '" + file.getName() + "' doesn't exist", 1);
/*     */     }
/*     */     
/*     */ 
/* 533 */     if (file.isFile())
/*     */     {
/* 535 */       if (!ignoreFile(name))
/*     */       {
/* 537 */         this.total_file_count += 1L;
/*     */         
/* 539 */         if (root.length() > 0)
/*     */         {
/* 541 */           name = root + File.separator + name;
/*     */         }
/*     */         
/* 544 */         File link = (File)this.linkage_map.get(name);
/*     */         
/* 546 */         return link == null ? file.length() : link.length();
/*     */       }
/*     */       
/*     */ 
/* 550 */       return 0L;
/*     */     }
/*     */     
/*     */ 
/* 554 */     File[] dir_files = file.listFiles();
/*     */     
/* 556 */     if (dir_files == null)
/*     */     {
/* 558 */       throw new TOTorrentException("TOTorrentCreate: directory '" + file.getAbsolutePath() + "' returned error when listing files in it", 1);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 563 */     long length = 0L;
/*     */     
/* 565 */     if (root.length() == 0)
/*     */     {
/* 567 */       root = name;
/*     */     }
/*     */     else {
/* 570 */       root = root + File.separator + name;
/*     */     }
/*     */     
/* 573 */     for (int i = 0; i < dir_files.length; i++)
/*     */     {
/* 575 */       length += getTotalFileSizeSupport(dir_files[i], root);
/*     */     }
/*     */     
/* 578 */     return length;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void report(String resource_key)
/*     */   {
/* 586 */     report(resource_key, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void report(String resource_key, long bytes)
/*     */   {
/* 594 */     if (this.progress_listeners.size() > 0)
/*     */     {
/* 596 */       report(resource_key, DisplayFormatters.formatByteCountToKiBEtc(bytes));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void report(String resource_key, String additional_text)
/*     */   {
/* 605 */     if (this.progress_listeners.size() > 0)
/*     */     {
/* 607 */       String prefix = MessageText.getString(resource_key);
/*     */       
/* 609 */       for (int i = 0; i < this.progress_listeners.size(); i++)
/*     */       {
/* 611 */         ((TOTorrentProgressListener)this.progress_listeners.get(i)).reportCurrentTask(prefix + (additional_text == null ? "" : additional_text));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static long getComputedPieceSize(long total_size, long _piece_min_size, long _piece_max_size, long _piece_num_lower, long _piece_num_upper)
/*     */   {
/* 624 */     long piece_length = -1L;
/*     */     
/* 626 */     long current_piece_size = _piece_min_size;
/*     */     
/* 628 */     while (current_piece_size <= _piece_max_size)
/*     */     {
/* 630 */       long pieces = total_size / current_piece_size;
/*     */       
/* 632 */       if (pieces <= _piece_num_upper)
/*     */       {
/* 634 */         piece_length = current_piece_size;
/*     */         
/* 636 */         break;
/*     */       }
/*     */       
/* 639 */       current_piece_size <<= 1;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 645 */     if (piece_length == -1L)
/*     */     {
/*     */ 
/*     */ 
/* 649 */       piece_length = _piece_max_size;
/*     */     }
/*     */     
/* 652 */     return piece_length;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static long getPieceCount(long total_size, long piece_size)
/*     */   {
/* 660 */     return (total_size + (piece_size - 1L)) / piece_size;
/*     */   }
/*     */   
/*     */   protected void setIgnoreList()
/*     */   {
/*     */     try
/*     */     {
/* 667 */       this.ignore_set = TorrentUtils.getIgnoreSet();
/*     */     }
/*     */     catch (NoClassDefFoundError e) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean ignoreFile(String file)
/*     */   {
/* 679 */     if (this.ignore_set.contains(file.toLowerCase()))
/*     */     {
/* 681 */       report("Torrent.create.progress.ignoringfile", " '" + file + "'");
/*     */       
/* 683 */       return true;
/*     */     }
/*     */     
/* 686 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void cancel()
/*     */   {
/* 692 */     if (!this.cancelled)
/*     */     {
/* 694 */       report("Torrent.create.progress.cancelled");
/*     */       
/* 696 */       this.cancelled = true;
/*     */       
/* 698 */       if (this.file_hasher != null)
/*     */       {
/* 700 */         this.file_hasher.cancel();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void addListener(TOTorrentProgressListener listener)
/*     */   {
/* 709 */     this.progress_listeners.add(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void removeListener(TOTorrentProgressListener listener)
/*     */   {
/* 716 */     this.progress_listeners.remove(listener);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/torrent/impl/TOTorrentCreateImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */