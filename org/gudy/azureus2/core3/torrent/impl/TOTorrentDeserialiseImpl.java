/*     */ package org.gudy.azureus2.core3.torrent.impl;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.Vector;
/*     */ import java.util.zip.GZIPInputStream;
/*     */ import org.gudy.azureus2.core3.html.HTMLUtils;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.util.BDecoder;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.LightHashMapEx;
/*     */ import org.gudy.azureus2.core3.util.StringInterner;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
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
/*     */ public class TOTorrentDeserialiseImpl
/*     */   extends TOTorrentImpl
/*     */ {
/*     */   public TOTorrentDeserialiseImpl(File file)
/*     */     throws TOTorrentException
/*     */   {
/*  48 */     if (!file.exists()) {
/*  49 */       throw new TOTorrentException("Torrent file '" + file.toString() + "' does not exist", 1);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  54 */     if (!file.isFile())
/*     */     {
/*  56 */       throw new TOTorrentException("Torrent must be a file ('" + file.toString() + "')", 1);
/*     */     }
/*     */     
/*     */ 
/*  60 */     if (file.length() == 0L)
/*     */     {
/*  62 */       throw new TOTorrentException("Torrent is zero length ('" + file.toString() + "')", 2);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  70 */     InputStream fis = null;
/*     */     
/*     */     try
/*     */     {
/*  74 */       fis = new FileInputStream(file);
/*     */       
/*  76 */       construct(fis); return;
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */ 
/*     */       try
/*     */       {
/*  84 */         if (fis != null)
/*     */         {
/*  86 */           fis.close();
/*     */           
/*  88 */           fis = null;
/*     */         }
/*     */         
/*  91 */         fis = new GZIPInputStream(new FileInputStream(file));
/*     */         
/*  93 */         construct(fis);
/*     */       }
/*     */       catch (Throwable f)
/*     */       {
/*  97 */         throw new TOTorrentException("Error reading torrent file '" + file.toString() + " - " + Debug.getNestedExceptionMessage(e), 4);
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 102 */       if (fis != null)
/*     */       {
/*     */         try
/*     */         {
/* 106 */           fis.close();
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/* 110 */           Debug.printStackTrace(e);
/*     */         }
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
/*     */ 
/*     */   public TOTorrentDeserialiseImpl(InputStream is)
/*     */     throws TOTorrentException
/*     */   {
/* 153 */     construct(is);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TOTorrentDeserialiseImpl(byte[] bytes)
/*     */     throws TOTorrentException
/*     */   {
/* 163 */     construct(bytes);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TOTorrentDeserialiseImpl(Map map)
/*     */     throws TOTorrentException
/*     */   {
/* 171 */     construct(map);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void construct(InputStream is)
/*     */     throws TOTorrentException
/*     */   {
/* 180 */     ByteArrayOutputStream metaInfo = new ByteArrayOutputStream(65536);
/*     */     try
/*     */     {
/* 183 */       byte[] buf = new byte[32768];
/*     */       
/*     */ 
/* 186 */       int iFirstByte = is.read();
/*     */       
/* 188 */       if ((iFirstByte != 100) && (iFirstByte != 101) && (iFirstByte != 105) && ((iFirstByte < 48) || (iFirstByte > 57)))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         try
/*     */         {
/*     */ 
/*     */ 
/* 197 */           metaInfo.write(iFirstByte);
/*     */           
/*     */           int nbRead;
/*     */           
/* 201 */           while (((nbRead = is.read(buf)) > 0) && (metaInfo.size() < 32000))
/*     */           {
/* 203 */             metaInfo.write(buf, 0, nbRead);
/*     */           }
/*     */           
/* 206 */           String char_data = new String(metaInfo.toByteArray());
/*     */           
/* 208 */           if (char_data.toLowerCase().contains("html"))
/*     */           {
/* 210 */             char_data = HTMLUtils.convertHTMLToText2(char_data);
/*     */             
/* 212 */             char_data = HTMLUtils.splitWithLineLength(char_data, 80);
/*     */             
/* 214 */             if (char_data.length() > 400)
/*     */             {
/* 216 */               char_data = char_data.substring(0, 400) + "...";
/*     */             }
/*     */             
/* 219 */             throw new TOTorrentException("Contents maybe HTML:\n" + char_data, 6);
/*     */           }
/*     */           
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 225 */           if ((e instanceof TOTorrentException))
/*     */           {
/* 227 */             throw ((TOTorrentException)e);
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 233 */         throw new TOTorrentException("Contents invalid - bad header", 6);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 240 */       metaInfo.write(iFirstByte);
/*     */       
/*     */       int nbRead;
/*     */       
/* 244 */       while ((nbRead = is.read(buf)) > 0)
/*     */       {
/* 246 */         metaInfo.write(buf, 0, nbRead);
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 250 */       throw new TOTorrentException("Error reading torrent: " + Debug.getNestedExceptionMessage(e), 4);
/*     */     }
/*     */     
/*     */ 
/* 254 */     construct(metaInfo.toByteArray());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void construct(byte[] bytes)
/*     */     throws TOTorrentException
/*     */   {
/*     */     try
/*     */     {
/* 264 */       BDecoder decoder = new BDecoder();
/*     */       
/* 266 */       decoder.setVerifyMapOrder(true);
/*     */       
/* 268 */       Map meta_data = decoder.decodeByteArray(bytes);
/*     */       
/*     */ 
/*     */ 
/* 272 */       construct(meta_data);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 276 */       throw new TOTorrentException("Error reading torrent: " + Debug.getNestedExceptionMessage(e), 6, e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void construct(Map meta_data)
/*     */     throws TOTorrentException
/*     */   {
/*     */     try
/*     */     {
/* 289 */       String announce_url = null;
/*     */       
/* 291 */       boolean got_announce = false;
/* 292 */       boolean got_announce_list = false;
/*     */       
/* 294 */       boolean bad_announce = false;
/*     */       
/*     */ 
/*     */ 
/* 298 */       Iterator root_it = meta_data.keySet().iterator();
/*     */       
/* 300 */       while (root_it.hasNext())
/*     */       {
/* 302 */         String key = (String)root_it.next();
/*     */         
/* 304 */         if (key.equalsIgnoreCase("announce"))
/*     */         {
/* 306 */           got_announce = true;
/*     */           
/* 308 */           announce_url = readStringFromMetaData(meta_data, "announce");
/*     */           
/* 310 */           if ((announce_url == null) || (announce_url.trim().length() == 0))
/*     */           {
/* 312 */             bad_announce = true;
/*     */           }
/*     */           else
/*     */           {
/* 316 */             announce_url = announce_url.replaceAll(" ", "");
/*     */             
/*     */             try
/*     */             {
/* 320 */               setAnnounceURL(new URL(announce_url));
/*     */             }
/*     */             catch (MalformedURLException e)
/*     */             {
/* 324 */               if (!announce_url.contains("://"))
/*     */               {
/* 326 */                 announce_url = "http:/" + (announce_url.startsWith("/") ? "" : "/") + announce_url;
/*     */               }
/* 328 */               else if (announce_url.startsWith("utp:"))
/*     */               {
/*     */ 
/*     */ 
/* 332 */                 announce_url = "udp" + announce_url.substring(3);
/*     */               }
/*     */               
/*     */               try
/*     */               {
/* 337 */                 setAnnounceURL(new URL(announce_url));
/*     */               }
/*     */               catch (MalformedURLException f)
/*     */               {
/* 341 */                 Debug.out("Invalid announce url: " + announce_url);
/*     */                 
/* 343 */                 bad_announce = true;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/* 348 */         else if (key.equalsIgnoreCase("announce-list"))
/*     */         {
/* 350 */           got_announce_list = true;
/*     */           
/* 352 */           List announce_list = null;
/*     */           
/* 354 */           Object ann_list = meta_data.get("announce-list");
/*     */           
/* 356 */           if ((ann_list instanceof List))
/*     */           {
/* 358 */             announce_list = (List)ann_list;
/*     */           }
/*     */           
/* 361 */           if ((announce_list != null) && (announce_list.size() > 0))
/*     */           {
/* 363 */             announce_url = readStringFromMetaData(meta_data, "announce");
/*     */             
/* 365 */             if (announce_url != null)
/*     */             {
/* 367 */               announce_url = announce_url.replaceAll(" ", "");
/*     */             }
/*     */             
/* 370 */             boolean announce_url_found = false;
/*     */             
/* 372 */             for (int i = 0; i < announce_list.size(); i++)
/*     */             {
/* 374 */               Object temp = announce_list.get(i);
/*     */               
/*     */ 
/*     */ 
/* 378 */               if ((temp instanceof byte[]))
/*     */               {
/* 380 */                 List l = new ArrayList();
/*     */                 
/* 382 */                 l.add(temp);
/*     */                 
/* 384 */                 temp = l;
/*     */               }
/*     */               
/* 387 */               if ((temp instanceof List))
/*     */               {
/* 389 */                 Vector urls = new Vector();
/*     */                 
/* 391 */                 List set = (List)temp;
/*     */                 
/* 393 */                 while (set.size() > 0)
/*     */                 {
/* 395 */                   Object temp2 = set.remove(0);
/*     */                   try
/*     */                   {
/* 398 */                     if ((temp2 instanceof List))
/*     */                     {
/* 400 */                       List junk = (List)temp2;
/*     */                       
/* 402 */                       if (junk.size() > 0)
/*     */                       {
/* 404 */                         set.add(junk.get(0));
/*     */                       }
/*     */                       
/*     */                     }
/*     */                     else
/*     */                     {
/* 410 */                       String url_str = readStringFromMetaData((byte[])temp2);
/*     */                       
/* 412 */                       url_str = url_str.replaceAll(" ", "");
/*     */                       
/*     */ 
/*     */                       try
/*     */                       {
/* 417 */                         urls.add(new URL(StringInterner.intern(url_str)));
/*     */                         
/* 419 */                         if (url_str.equalsIgnoreCase(announce_url))
/*     */                         {
/* 421 */                           announce_url_found = true;
/*     */                         }
/*     */                       }
/*     */                       catch (MalformedURLException e)
/*     */                       {
/* 426 */                         if (!url_str.contains("://"))
/*     */                         {
/* 428 */                           url_str = "http:/" + (url_str.startsWith("/") ? "" : "/") + url_str;
/*     */                         }
/* 430 */                         else if (url_str.startsWith("utp:"))
/*     */                         {
/*     */ 
/*     */ 
/* 434 */                           url_str = "udp" + url_str.substring(3);
/*     */                         }
/*     */                         try
/*     */                         {
/* 438 */                           urls.add(new URL(StringInterner.intern(url_str)));
/*     */                           
/* 440 */                           if (url_str.equalsIgnoreCase(announce_url))
/*     */                           {
/* 442 */                             announce_url_found = true;
/*     */                           }
/*     */                         }
/*     */                         catch (MalformedURLException f)
/*     */                         {
/* 447 */                           Debug.out("Invalid url: " + url_str, f);
/*     */                         }
/*     */                       }
/*     */                     }
/*     */                   } catch (Throwable e) {
/* 452 */                     Debug.out("Torrent has invalid url-list entry (" + temp2 + ") - ignoring: meta=" + meta_data, e);
/*     */                   }
/*     */                 }
/*     */                 
/* 456 */                 if (urls.size() > 0)
/*     */                 {
/* 458 */                   URL[] url_array = new URL[urls.size()];
/*     */                   
/* 460 */                   urls.copyInto(url_array);
/*     */                   
/* 462 */                   addTorrentAnnounceURLSet(url_array);
/*     */                 }
/*     */               }
/*     */               else {
/* 466 */                 Debug.out("Torrent has invalid url-list entry (" + temp + ") - ignoring: meta=" + meta_data);
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 473 */             if ((!announce_url_found) && (announce_url != null) && (announce_url.length() > 0)) {
/*     */               try {
/* 475 */                 Vector urls = new Vector();
/* 476 */                 urls.add(new URL(StringInterner.intern(announce_url)));
/* 477 */                 URL[] url_array = new URL[urls.size()];
/* 478 */                 urls.copyInto(url_array);
/* 479 */                 addTorrentAnnounceURLSet(url_array);
/*     */               }
/*     */               catch (Exception e) {
/* 482 */                 Debug.out("Invalid URL '" + announce_url + "' - meta=" + meta_data, e);
/*     */               }
/*     */             }
/*     */           }
/* 486 */         } else if (key.equalsIgnoreCase("comment"))
/*     */         {
/* 488 */           setComment((byte[])meta_data.get("comment"));
/*     */         }
/* 490 */         else if (key.equalsIgnoreCase("created by"))
/*     */         {
/* 492 */           setCreatedBy((byte[])meta_data.get("created by"));
/*     */         }
/* 494 */         else if (key.equalsIgnoreCase("creation date"))
/*     */         {
/*     */ 
/*     */           try
/*     */           {
/*     */ 
/* 500 */             Long creation_date = (Long)meta_data.get("creation date");
/*     */             
/* 502 */             if (creation_date != null)
/*     */             {
/* 504 */               setCreationDate(creation_date.longValue());
/*     */             }
/*     */           }
/*     */           catch (Exception e) {
/* 508 */             System.out.println("creation_date extraction fails, ignoring");
/*     */           }
/*     */         }
/* 511 */         else if (!key.equalsIgnoreCase("info"))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 517 */           Object prop = meta_data.get(key);
/*     */           
/* 519 */           if ((prop instanceof byte[]))
/*     */           {
/* 521 */             setAdditionalByteArrayProperty(key, (byte[])prop);
/*     */           }
/* 523 */           else if ((prop instanceof Long))
/*     */           {
/* 525 */             setAdditionalLongProperty(key, (Long)prop);
/*     */           }
/* 527 */           else if ((prop instanceof List))
/*     */           {
/* 529 */             setAdditionalListProperty(key, (List)prop);
/*     */           }
/*     */           else
/*     */           {
/* 533 */             setAdditionalMapProperty(key, (Map)prop);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 538 */       if (bad_announce)
/*     */       {
/* 540 */         if (got_announce_list)
/*     */         {
/* 542 */           TOTorrentAnnounceURLSet[] sets = getAnnounceURLGroup().getAnnounceURLSets();
/*     */           
/* 544 */           if (sets.length > 0)
/*     */           {
/* 546 */             setAnnounceURL(sets[0].getAnnounceURLs()[0]);
/*     */           }
/*     */           else {
/* 549 */             throw new TOTorrentException("ANNOUNCE_URL malformed ('" + announce_url + "' and no usable announce list)", 6);
/*     */           }
/*     */           
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 556 */           throw new TOTorrentException("ANNOUNCE_URL malformed ('" + announce_url + "'", 6);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 561 */       if ((!got_announce_list) && (!got_announce))
/*     */       {
/* 563 */         setAnnounceURL(TorrentUtils.getDecentralisedEmptyURL());
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 568 */       if (getAnnounceURL() == null)
/*     */       {
/* 570 */         boolean done = false;
/*     */         
/* 572 */         if (got_announce_list)
/*     */         {
/* 574 */           TOTorrentAnnounceURLSet[] sets = getAnnounceURLGroup().getAnnounceURLSets();
/*     */           
/* 576 */           if (sets.length > 0)
/*     */           {
/* 578 */             setAnnounceURL(sets[0].getAnnounceURLs()[0]);
/*     */             
/* 580 */             done = true;
/*     */           }
/*     */         }
/*     */         
/* 584 */         if (!done)
/*     */         {
/* 586 */           setAnnounceURL(TorrentUtils.getDecentralisedEmptyURL());
/*     */         }
/*     */       }
/*     */       
/* 590 */       Map info = (Map)meta_data.get("info");
/*     */       
/* 592 */       if (info == null)
/*     */       {
/* 594 */         throw new TOTorrentException("Decode fails, 'info' element not found'", 6);
/*     */       }
/*     */       
/*     */ 
/* 598 */       boolean hasUTF8Keys = info.containsKey("name.utf-8");
/*     */       
/* 600 */       setName((byte[])info.get("name"));
/*     */       
/* 602 */       long piece_length = ((Long)info.get("piece length")).longValue();
/*     */       
/* 604 */       if (piece_length <= 0L)
/*     */       {
/* 606 */         throw new TOTorrentException("Decode fails, piece-length is invalid", 6);
/*     */       }
/*     */       
/*     */ 
/* 610 */       setPieceLength(piece_length);
/*     */       
/* 612 */       setHashFromInfo(info);
/*     */       
/* 614 */       Long simple_file_length = (Long)info.get("length");
/*     */       
/* 616 */       long total_length = 0L;
/*     */       
/* 618 */       String encoding = getAdditionalStringProperty("encoding");
/* 619 */       hasUTF8Keys &= ((encoding == null) || (encoding.equals("utf8 keys")));
/*     */       
/* 621 */       if (simple_file_length != null)
/*     */       {
/* 623 */         setSimpleTorrent(true);
/*     */         
/* 625 */         total_length = simple_file_length.longValue();
/*     */         
/* 627 */         if (hasUTF8Keys) {
/* 628 */           setNameUTF8((byte[])info.get("name.utf-8"));
/* 629 */           setAdditionalStringProperty("encoding", "utf8 keys");
/*     */         }
/*     */         
/* 632 */         setFiles(new TOTorrentFileImpl[] { new TOTorrentFileImpl(this, 0, 0L, total_length, new byte[][] { getName() }) });
/*     */       }
/*     */       else
/*     */       {
/* 636 */         setSimpleTorrent(false);
/*     */         
/* 638 */         List meta_files = (List)info.get("files");
/*     */         
/* 640 */         TOTorrentFileImpl[] files = new TOTorrentFileImpl[meta_files.size()];
/*     */         
/* 642 */         if (hasUTF8Keys) {
/* 643 */           for (int i = 0; i < files.length; i++) {
/* 644 */             Map file_map = (Map)meta_files.get(i);
/*     */             
/* 646 */             hasUTF8Keys &= file_map.containsKey("path.utf-8");
/* 647 */             if (!hasUTF8Keys) {
/*     */               break;
/*     */             }
/*     */           }
/*     */           
/* 652 */           if (hasUTF8Keys) {
/* 653 */             setNameUTF8((byte[])info.get("name.utf-8"));
/* 654 */             setAdditionalStringProperty("encoding", "utf8 keys");
/*     */           }
/*     */         }
/*     */         
/* 658 */         for (int i = 0; i < files.length; i++)
/*     */         {
/* 660 */           Map file_map = (Map)meta_files.get(i);
/*     */           
/* 662 */           long len = ((Long)file_map.get("length")).longValue();
/*     */           
/* 664 */           List paths = (List)file_map.get("path");
/* 665 */           List paths8 = (List)file_map.get("path.utf-8");
/*     */           
/* 667 */           byte[][] path_comps = (byte[][])null;
/* 668 */           if (paths != null) {
/* 669 */             path_comps = new byte[paths.size()][];
/*     */             
/* 671 */             for (int j = 0; j < paths.size(); j++)
/*     */             {
/* 673 */               path_comps[j] = ((byte[])(byte[])paths.get(j));
/*     */             }
/*     */           }
/*     */           
/*     */           TOTorrentFileImpl file;
/*     */           TOTorrentFileImpl file;
/* 679 */           if (hasUTF8Keys) {
/* 680 */             byte[][] path_comps8 = new byte[paths8.size()][];
/*     */             
/* 682 */             for (int j = 0; j < paths8.size(); j++)
/*     */             {
/* 684 */               path_comps8[j] = ((byte[])(byte[])paths8.get(j));
/*     */             }
/*     */             
/* 687 */             file = files[i] = new TOTorrentFileImpl(this, i, total_length, len, path_comps, path_comps8);
/*     */           } else {
/* 689 */             file = files[i] = new TOTorrentFileImpl(this, i, total_length, len, path_comps);
/*     */           }
/*     */           
/* 692 */           total_length += len;
/*     */           
/*     */ 
/*     */ 
/* 696 */           Iterator file_it = file_map.keySet().iterator();
/*     */           
/* 698 */           while (file_it.hasNext())
/*     */           {
/* 700 */             String key = (String)file_it.next();
/*     */             
/* 702 */             if ((!key.equals("length")) && (!key.equals("path")))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 709 */               file.setAdditionalProperty(key, file_map.get(key));
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 714 */         setFiles(files);
/*     */       }
/*     */       
/* 717 */       byte[] flat_pieces = (byte[])info.get("pieces");
/*     */       
/*     */ 
/*     */ 
/* 721 */       int pieces_required = (int)((total_length + (piece_length - 1L)) / piece_length);
/*     */       
/* 723 */       int pieces_supplied = flat_pieces.length / 20;
/*     */       
/* 725 */       if (pieces_supplied < pieces_required)
/*     */       {
/* 727 */         throw new TOTorrentException("Decode fails, insufficient pieces supplied", 6);
/*     */       }
/*     */       
/*     */ 
/* 731 */       if (pieces_supplied > pieces_required)
/*     */       {
/* 733 */         Debug.out("Torrent '" + new String(getName()) + "' has too many pieces (required=" + pieces_required + ",supplied=" + pieces_supplied + ") - ignoring excess");
/*     */       }
/*     */       
/* 736 */       byte[][] pieces = new byte[pieces_supplied][20];
/*     */       
/* 738 */       for (int i = 0; i < pieces.length; i++)
/*     */       {
/* 740 */         System.arraycopy(flat_pieces, i * 20, pieces[i], 0, 20);
/*     */       }
/*     */       
/* 743 */       setPieces(pieces);
/*     */       
/*     */ 
/*     */ 
/* 747 */       Iterator info_it = info.keySet().iterator();
/*     */       
/* 749 */       while (info_it.hasNext())
/*     */       {
/* 751 */         String key = (String)info_it.next();
/*     */         
/* 753 */         if ((!key.equals("name")) && (!key.equals("length")) && (!key.equals("files")) && (!key.equals("piece length")) && (!key.equals("pieces")))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 763 */           addAdditionalInfoProperty(key, info.get(key));
/*     */         }
/*     */       }
/*     */       try
/*     */       {
/* 768 */         byte[] ho = (byte[])info.get("hash-override");
/*     */         
/* 770 */         if (ho != null)
/*     */         {
/* 772 */           setHashOverride(ho);
/*     */ 
/*     */ 
/*     */         }
/* 776 */         else if ((info instanceof LightHashMapEx))
/*     */         {
/* 778 */           LightHashMapEx info_ex = (LightHashMapEx)info;
/*     */           
/* 780 */           if (info_ex.getFlag((byte)1))
/*     */           {
/* 782 */             String name = getUTF8Name();
/*     */             
/* 784 */             if (name == null)
/*     */             {
/* 786 */               name = new String(getName());
/*     */             }
/*     */             
/* 789 */             String message = MessageText.getString("torrent.decode.info.order.bad", new String[] { name });
/*     */             
/* 791 */             LogAlert alert = new LogAlert(this, false, 1, message);
/*     */             
/* 793 */             alert.forceNotify = true;
/*     */             
/* 795 */             Logger.log(alert);
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 801 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 805 */       if ((e instanceof TOTorrentException))
/*     */       {
/* 807 */         throw ((TOTorrentException)e);
/*     */       }
/*     */       
/* 810 */       throw new TOTorrentException("Torrent decode fails '" + Debug.getNestedExceptionMessageAndStack(e) + "'", 6, e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void printMap()
/*     */   {
/*     */     try
/*     */     {
/* 821 */       print("", "root", serialiseToMap());
/*     */     }
/*     */     catch (TOTorrentException e)
/*     */     {
/* 825 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void print(String indent, String name, Map map)
/*     */   {
/* 835 */     System.out.println(indent + name + "{map}");
/*     */     
/* 837 */     Iterator it = map.keySet().iterator();
/*     */     
/* 839 */     while (it.hasNext())
/*     */     {
/* 841 */       String key = (String)it.next();
/*     */       
/* 843 */       Object value = map.get(key);
/*     */       
/* 845 */       if ((value instanceof Map))
/*     */       {
/* 847 */         print(indent + "  ", key, (Map)value);
/*     */       }
/* 849 */       else if ((value instanceof List))
/*     */       {
/* 851 */         print(indent + "  ", key, (List)value);
/*     */       }
/* 853 */       else if ((value instanceof Long))
/*     */       {
/* 855 */         print(indent + "  ", key, (Long)value);
/*     */       }
/*     */       else
/*     */       {
/* 859 */         print(indent + "  ", key, (byte[])value);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void print(String indent, String name, List list)
/*     */   {
/* 870 */     System.out.println(indent + name + "{list}");
/*     */     
/* 872 */     Iterator it = list.iterator();
/*     */     
/* 874 */     int index = 0;
/*     */     
/* 876 */     while (it.hasNext())
/*     */     {
/* 878 */       Object value = it.next();
/*     */       
/* 880 */       if ((value instanceof Map))
/*     */       {
/* 882 */         print(indent + "  ", "[" + index + "]", (Map)value);
/*     */       }
/* 884 */       else if ((value instanceof List))
/*     */       {
/* 886 */         print(indent + "  ", "[" + index + "]", (List)value);
/*     */       }
/* 888 */       else if ((value instanceof Long))
/*     */       {
/* 890 */         print(indent + "  ", "[" + index + "]", (Long)value);
/*     */       }
/*     */       else
/*     */       {
/* 894 */         print(indent + "  ", "[" + index + "]", (byte[])value);
/*     */       }
/*     */       
/* 897 */       index++;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void print(String indent, String name, Long value)
/*     */   {
/* 906 */     System.out.println(indent + name + "{long} = " + value.longValue());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void print(String indent, String name, byte[] value)
/*     */   {
/* 915 */     String x = new String(value);
/*     */     
/* 917 */     boolean print = true;
/*     */     
/* 919 */     for (int i = 0; i < x.length(); i++)
/*     */     {
/* 921 */       char c = x.charAt(i);
/*     */       
/* 923 */       if (c >= '')
/*     */       {
/*     */ 
/*     */ 
/* 927 */         print = false;
/*     */         
/* 929 */         break;
/*     */       }
/*     */     }
/*     */     
/* 933 */     if (print)
/*     */     {
/* 935 */       System.out.println(indent + name + "{byte[]} = " + x);
/*     */     }
/*     */     else
/*     */     {
/* 939 */       System.out.println(indent + name + "{byte[], length " + value.length + "}");
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/torrent/impl/TOTorrentDeserialiseImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */