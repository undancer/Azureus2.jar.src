/*      */ package org.gudy.azureus2.core3.torrent.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileDescriptor;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.logging.LogRelation;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentListener;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.LightHashMap;
/*      */ import org.gudy.azureus2.core3.util.SHA1Hasher;
/*      */ import org.gudy.azureus2.core3.util.StringInterner;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ 
/*      */ 
/*      */ 
/*      */ public class TOTorrentImpl
/*      */   extends LogRelation
/*      */   implements TOTorrent
/*      */ {
/*      */   protected static final String TK_ANNOUNCE = "announce";
/*      */   protected static final String TK_ANNOUNCE_LIST = "announce-list";
/*      */   protected static final String TK_COMMENT = "comment";
/*      */   protected static final String TK_CREATION_DATE = "creation date";
/*      */   protected static final String TK_CREATED_BY = "created by";
/*      */   protected static final String TK_INFO = "info";
/*      */   protected static final String TK_NAME = "name";
/*      */   protected static final String TK_LENGTH = "length";
/*      */   protected static final String TK_PATH = "path";
/*      */   protected static final String TK_FILES = "files";
/*      */   protected static final String TK_PIECE_LENGTH = "piece length";
/*      */   protected static final String TK_PIECES = "pieces";
/*      */   protected static final String TK_PRIVATE = "private";
/*      */   protected static final String TK_NAME_UTF8 = "name.utf-8";
/*      */   protected static final String TK_PATH_UTF8 = "path.utf-8";
/*      */   protected static final String TK_COMMENT_UTF8 = "comment.utf-8";
/*      */   protected static final String TK_WEBSEED_BT = "httpseeds";
/*      */   protected static final String TK_WEBSEED_GR = "url-list";
/*      */   protected static final String TK_HASH_OVERRIDE = "hash-override";
/*   66 */   protected static final List TK_ADDITIONAL_OK_ATTRS = Arrays.asList(new String[] { "comment.utf-8", "azureus_properties", "httpseeds", "url-list" });
/*      */   
/*      */   private byte[] torrent_name;
/*      */   
/*      */   private byte[] torrent_name_utf8;
/*      */   
/*      */   private byte[] comment;
/*      */   private URL announce_url;
/*   74 */   private final TOTorrentAnnounceURLGroupImpl announce_group = new TOTorrentAnnounceURLGroupImpl(this);
/*      */   
/*      */   private long piece_length;
/*      */   
/*      */   private byte[][] pieces;
/*      */   
/*      */   private int number_of_pieces;
/*      */   
/*      */   private byte[] torrent_hash_override;
/*      */   
/*      */   private byte[] torrent_hash;
/*      */   
/*      */   private HashWrapper torrent_hash_wrapper;
/*      */   private boolean simple_torrent;
/*      */   private TOTorrentFileImpl[] files;
/*      */   private long creation_date;
/*      */   private byte[] created_by;
/*   91 */   private Map additional_properties = new LightHashMap(4);
/*   92 */   private final Map additional_info_properties = new LightHashMap(4);
/*      */   
/*      */   private boolean created;
/*      */   
/*      */   private boolean serialising;
/*      */   
/*      */   private List<TOTorrentListener> listeners;
/*   99 */   protected final AEMonitor this_mon = new AEMonitor("TOTorrent");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected TOTorrentImpl() {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected TOTorrentImpl(String _torrent_name, URL _announce_url, boolean _simple_torrent)
/*      */     throws TOTorrentException
/*      */   {
/*  122 */     this.created = true;
/*      */     
/*      */     try
/*      */     {
/*  126 */       this.torrent_name = _torrent_name.getBytes("UTF8");
/*      */       
/*  128 */       this.torrent_name_utf8 = this.torrent_name;
/*      */       
/*  130 */       setAnnounceURL(_announce_url);
/*      */       
/*  132 */       this.simple_torrent = _simple_torrent;
/*      */     }
/*      */     catch (UnsupportedEncodingException e)
/*      */     {
/*  136 */       throw new TOTorrentException("Unsupported encoding for '" + _torrent_name + "'", 7);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void serialiseToBEncodedFile(File output_file)
/*      */     throws TOTorrentException
/*      */   {
/*  150 */     if (this.created)
/*      */     {
/*  152 */       TorrentUtils.addCreatedTorrent(this);
/*      */     }
/*      */     
/*  155 */     byte[] res = serialiseToByteArray();
/*      */     
/*  157 */     BufferedOutputStream bos = null;
/*      */     try
/*      */     {
/*  160 */       File parent = output_file.getParentFile();
/*  161 */       if (parent == null) {
/*  162 */         throw new TOTorrentException("Path '" + output_file + "' is invalid", 5);
/*      */       }
/*      */       
/*      */ 
/*  166 */       if (!parent.isDirectory())
/*      */       {
/*      */ 
/*  169 */         boolean dir_created = FileUtil.mkdirs(parent);
/*      */         
/*      */ 
/*  172 */         if (!dir_created)
/*      */         {
/*      */ 
/*  175 */           if (parent.exists())
/*      */           {
/*      */ 
/*  178 */             if (!parent.isDirectory())
/*      */             {
/*      */ 
/*  181 */               throw new TOTorrentException("Path '" + output_file + "' is invalid", 5);
/*      */ 
/*      */ 
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*      */ 
/*      */ 
/*  194 */             throw new TOTorrentException("Failed to create directory '" + parent + "'", 5);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  201 */       File temp = new File(parent, output_file.getName() + ".saving");
/*      */       
/*  203 */       if (temp.exists())
/*      */       {
/*  205 */         if (!temp.delete())
/*      */         {
/*  207 */           throw new TOTorrentException("Insufficient permissions to delete '" + temp + "'", 5);
/*      */         }
/*      */       }
/*      */       else {
/*  211 */         boolean ok = false;
/*      */         try
/*      */         {
/*  214 */           ok = temp.createNewFile();
/*      */         }
/*      */         catch (Throwable e) {}
/*      */         
/*      */ 
/*  219 */         if (!ok)
/*      */         {
/*  221 */           throw new TOTorrentException("Insufficient permissions to write '" + temp + "'", 5);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  226 */       FileOutputStream fos = new FileOutputStream(temp, false);
/*      */       
/*  228 */       bos = new BufferedOutputStream(fos, 8192);
/*      */       
/*  230 */       bos.write(res);
/*      */       
/*  232 */       bos.flush();
/*      */       
/*      */ 
/*      */ 
/*  236 */       if (!Constants.isCVSVersion())
/*      */       {
/*  238 */         fos.getFD().sync();
/*      */       }
/*      */       
/*  241 */       bos.close();
/*      */       
/*  243 */       bos = null;
/*      */       
/*      */ 
/*      */ 
/*  247 */       if (temp.length() > 1L) {
/*  248 */         output_file.delete();
/*  249 */         temp.renameTo(output_file);
/*      */       }
/*      */       return;
/*      */     }
/*      */     catch (TOTorrentException e) {
/*  254 */       throw e;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  258 */       throw new TOTorrentException("Failed to serialise torrent: " + Debug.getNestedExceptionMessage(e), 5);
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/*  263 */       if (bos != null) {
/*      */         try
/*      */         {
/*  266 */           bos.close();
/*      */         }
/*      */         catch (IOException e)
/*      */         {
/*  270 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected byte[] serialiseToByteArray()
/*      */     throws TOTorrentException
/*      */   {
/*  281 */     if (this.created)
/*      */     {
/*  283 */       TorrentUtils.addCreatedTorrent(this);
/*      */     }
/*      */     
/*  286 */     Map root = serialiseToMap();
/*      */     try
/*      */     {
/*  289 */       return BEncoder.encode(root);
/*      */     }
/*      */     catch (IOException e)
/*      */     {
/*  293 */       throw new TOTorrentException("Failed to serialise torrent: " + Debug.getNestedExceptionMessage(e), 5);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Map serialiseToMap()
/*      */     throws TOTorrentException
/*      */   {
/*  307 */     if ((this.created) && (!this.serialising)) {
/*      */       try
/*      */       {
/*  310 */         this.serialising = true;
/*      */         
/*  312 */         TorrentUtils.addCreatedTorrent(this);
/*      */       }
/*      */       finally
/*      */       {
/*  316 */         this.serialising = false;
/*      */       }
/*      */     }
/*      */     
/*  320 */     Object root = new HashMap();
/*      */     
/*      */ 
/*      */ 
/*  324 */     writeStringToMetaData((Map)root, "announce", (this.announce_url == null ? TorrentUtils.getDecentralisedEmptyURL() : this.announce_url).toString());
/*      */     
/*  326 */     TOTorrentAnnounceURLSet[] sets = this.announce_group.getAnnounceURLSets();
/*      */     
/*  328 */     if (sets.length > 0)
/*      */     {
/*  330 */       List announce_list = new ArrayList();
/*      */       
/*  332 */       for (int i = 0; i < sets.length; i++)
/*      */       {
/*  334 */         TOTorrentAnnounceURLSet set = sets[i];
/*      */         
/*  336 */         URL[] urls = set.getAnnounceURLs();
/*      */         
/*  338 */         if (urls.length != 0)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  343 */           List sub_list = new ArrayList();
/*      */           
/*  345 */           announce_list.add(sub_list);
/*      */           
/*  347 */           for (int j = 0; j < urls.length; j++)
/*      */           {
/*  349 */             sub_list.add(writeStringToMetaData(urls[j].toString()));
/*      */           }
/*      */         }
/*      */       }
/*  353 */       if (announce_list.size() > 0)
/*      */       {
/*  355 */         ((Map)root).put("announce-list", announce_list);
/*      */       }
/*      */     }
/*      */     
/*  359 */     if (this.comment != null)
/*      */     {
/*  361 */       ((Map)root).put("comment", this.comment);
/*      */     }
/*      */     
/*  364 */     if (this.creation_date != 0L)
/*      */     {
/*  366 */       ((Map)root).put("creation date", new Long(this.creation_date));
/*      */     }
/*      */     
/*  369 */     if (this.created_by != null)
/*      */     {
/*  371 */       ((Map)root).put("created by", this.created_by);
/*      */     }
/*      */     
/*  374 */     Map info = new HashMap();
/*      */     
/*  376 */     ((Map)root).put("info", info);
/*      */     
/*  378 */     info.put("piece length", new Long(this.piece_length));
/*      */     
/*  380 */     if (this.pieces == null)
/*      */     {
/*  382 */       throw new TOTorrentException("Pieces is null", 5);
/*      */     }
/*      */     
/*  385 */     byte[] flat_pieces = new byte[this.pieces.length * 20];
/*      */     
/*  387 */     for (int i = 0; i < this.pieces.length; i++)
/*      */     {
/*  389 */       System.arraycopy(this.pieces[i], 0, flat_pieces, i * 20, 20);
/*      */     }
/*      */     
/*  392 */     info.put("pieces", flat_pieces);
/*      */     
/*  394 */     info.put("name", this.torrent_name);
/*      */     
/*  396 */     if (this.torrent_name_utf8 != null)
/*      */     {
/*  398 */       info.put("name.utf-8", this.torrent_name_utf8);
/*      */     }
/*      */     
/*  401 */     if (this.torrent_hash_override != null)
/*      */     {
/*  403 */       info.put("hash-override", this.torrent_hash_override);
/*      */     }
/*      */     
/*  406 */     if (this.simple_torrent)
/*      */     {
/*  408 */       TOTorrentFile file = this.files[0];
/*      */       
/*  410 */       info.put("length", new Long(file.getLength()));
/*      */     }
/*      */     else
/*      */     {
/*  414 */       List meta_files = new ArrayList();
/*      */       
/*  416 */       info.put("files", meta_files);
/*      */       
/*  418 */       for (int i = 0; i < this.files.length; i++)
/*      */       {
/*  420 */         TOTorrentFileImpl file = this.files[i];
/*      */         
/*  422 */         Map file_map = file.serializeToMap();
/*      */         
/*  424 */         meta_files.add(file_map);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  429 */     Iterator info_it = this.additional_info_properties.keySet().iterator();
/*      */     
/*  431 */     while (info_it.hasNext())
/*      */     {
/*  433 */       String key = (String)info_it.next();
/*      */       
/*  435 */       info.put(key, this.additional_info_properties.get(key));
/*      */     }
/*      */     
/*  438 */     Iterator it = this.additional_properties.keySet().iterator();
/*      */     
/*  440 */     while (it.hasNext())
/*      */     {
/*  442 */       String key = (String)it.next();
/*      */       
/*  444 */       Object value = this.additional_properties.get(key);
/*      */       
/*  446 */       if (value != null)
/*      */       {
/*  448 */         ((Map)root).put(key, value);
/*      */       }
/*      */     }
/*      */     
/*  452 */     return (Map)root;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void serialiseToXMLFile(File file)
/*      */     throws TOTorrentException
/*      */   {
/*  461 */     if (this.created)
/*      */     {
/*  463 */       TorrentUtils.addCreatedTorrent(this);
/*      */     }
/*      */     
/*  466 */     TOTorrentXMLSerialiser serialiser = new TOTorrentXMLSerialiser(this);
/*      */     
/*  468 */     serialiser.serialiseToFile(file);
/*      */   }
/*      */   
/*      */ 
/*      */   public byte[] getName()
/*      */   {
/*  474 */     return this.torrent_name;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setName(byte[] _name)
/*      */   {
/*  481 */     this.torrent_name = _name;
/*      */   }
/*      */   
/*      */   public String getUTF8Name()
/*      */   {
/*      */     try
/*      */     {
/*  488 */       return this.torrent_name_utf8 == null ? null : new String(this.torrent_name_utf8, "utf8");
/*      */     }
/*      */     catch (UnsupportedEncodingException e) {}
/*  491 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setNameUTF8(byte[] _name)
/*      */   {
/*  499 */     this.torrent_name_utf8 = _name;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isSimpleTorrent()
/*      */   {
/*  505 */     return this.simple_torrent;
/*      */   }
/*      */   
/*      */ 
/*      */   public byte[] getComment()
/*      */   {
/*  511 */     return this.comment;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setComment(byte[] _comment)
/*      */   {
/*  519 */     this.comment = _comment;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setComment(String _comment)
/*      */   {
/*      */     try
/*      */     {
/*  528 */       byte[] utf8_comment = _comment.getBytes("UTF8");
/*      */       
/*  530 */       setComment(utf8_comment);
/*      */       
/*  532 */       setAdditionalByteArrayProperty("comment.utf-8", utf8_comment);
/*      */     }
/*      */     catch (UnsupportedEncodingException e)
/*      */     {
/*  536 */       Debug.printStackTrace(e);
/*      */       
/*  538 */       this.comment = null;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public URL getAnnounceURL()
/*      */   {
/*  545 */     return this.announce_url;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean setAnnounceURL(URL url)
/*      */   {
/*  552 */     URL newURL = anonymityTransform(url);
/*  553 */     String s0 = newURL == null ? "" : newURL.toString();
/*  554 */     String s1 = this.announce_url == null ? "" : this.announce_url.toString();
/*  555 */     if (s0.equals(s1)) {
/*  556 */       return false;
/*      */     }
/*  558 */     if (newURL == null)
/*      */     {
/*      */ 
/*      */ 
/*  562 */       newURL = TorrentUtils.getDecentralisedEmptyURL();
/*      */     }
/*      */     
/*  565 */     this.announce_url = StringInterner.internURL(newURL);
/*      */     
/*  567 */     fireChanged(1);
/*      */     
/*  569 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isDecentralised()
/*      */   {
/*  575 */     return TorrentUtils.isDecentralised(getAnnounceURL());
/*      */   }
/*      */   
/*      */ 
/*      */   public long getCreationDate()
/*      */   {
/*  581 */     return this.creation_date;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setCreationDate(long _creation_date)
/*      */   {
/*  591 */     long now_secs = SystemTime.getCurrentTime() / 1000L;
/*      */     
/*  593 */     if (_creation_date > now_secs + 3153600000L)
/*      */     {
/*  595 */       _creation_date /= 1000L;
/*      */     }
/*      */     
/*  598 */     this.creation_date = _creation_date;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setCreatedBy(byte[] _created_by)
/*      */   {
/*  605 */     this.created_by = _created_by;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setCreatedBy(String _created_by)
/*      */   {
/*      */     try
/*      */     {
/*  614 */       setCreatedBy(_created_by.getBytes("UTF8"));
/*      */     }
/*      */     catch (UnsupportedEncodingException e)
/*      */     {
/*  618 */       Debug.printStackTrace(e);
/*      */       
/*  620 */       this.created_by = null;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public byte[] getCreatedBy()
/*      */   {
/*  627 */     return this.created_by;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isCreated()
/*      */   {
/*  633 */     return this.created;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public byte[] getHash()
/*      */     throws TOTorrentException
/*      */   {
/*  641 */     if (this.torrent_hash == null)
/*      */     {
/*  643 */       Map root = serialiseToMap();
/*      */       
/*  645 */       Map info = (Map)root.get("info");
/*      */       
/*  647 */       setHashFromInfo(info);
/*      */     }
/*      */     
/*  650 */     return this.torrent_hash;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public HashWrapper getHashWrapper()
/*      */     throws TOTorrentException
/*      */   {
/*  658 */     if (this.torrent_hash_wrapper == null)
/*      */     {
/*  660 */       getHash();
/*      */     }
/*      */     
/*  663 */     return this.torrent_hash_wrapper;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean hasSameHashAs(TOTorrent other)
/*      */   {
/*      */     try
/*      */     {
/*  671 */       byte[] other_hash = other.getHash();
/*      */       
/*  673 */       return Arrays.equals(getHash(), other_hash);
/*      */     }
/*      */     catch (TOTorrentException e)
/*      */     {
/*  677 */       Debug.printStackTrace(e);
/*      */     }
/*  679 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setHashFromInfo(Map info)
/*      */     throws TOTorrentException
/*      */   {
/*      */     try
/*      */     {
/*  690 */       if (this.torrent_hash_override == null)
/*      */       {
/*  692 */         SHA1Hasher s = new SHA1Hasher();
/*      */         
/*  694 */         this.torrent_hash = s.calculateHash(BEncoder.encode(info));
/*      */       }
/*      */       else
/*      */       {
/*  698 */         this.torrent_hash = this.torrent_hash_override;
/*      */       }
/*      */       
/*  701 */       this.torrent_hash_wrapper = new HashWrapper(this.torrent_hash);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  705 */       throw new TOTorrentException("Failed to calculate hash: " + Debug.getNestedExceptionMessage(e), 8);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setHashOverride(byte[] hash)
/*      */     throws TOTorrentException
/*      */   {
/*  716 */     if (this.torrent_hash_override != null)
/*      */     {
/*  718 */       if (Arrays.equals(hash, this.torrent_hash_override))
/*      */       {
/*  720 */         return;
/*      */       }
/*      */       
/*      */ 
/*  724 */       throw new TOTorrentException("Hash override can only be set once", 8);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  738 */     this.torrent_hash_override = hash;
/*      */     
/*  740 */     this.torrent_hash = null;
/*      */     
/*  742 */     getHash();
/*      */   }
/*      */   
/*      */ 
/*      */   protected byte[] getHashOverride()
/*      */   {
/*  748 */     return this.torrent_hash_override;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setPrivate(boolean _private_torrent)
/*      */     throws TOTorrentException
/*      */   {
/*  757 */     this.additional_info_properties.put("private", new Long(_private_torrent ? 1L : 0L));
/*      */     
/*      */ 
/*      */ 
/*  761 */     this.torrent_hash = null;
/*      */     
/*  763 */     getHash();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getPrivate()
/*      */   {
/*  769 */     Object o = this.additional_info_properties.get("private");
/*      */     
/*  771 */     if ((o instanceof Long))
/*      */     {
/*  773 */       return ((Long)o).intValue() != 0;
/*      */     }
/*      */     
/*  776 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public TOTorrentAnnounceURLGroup getAnnounceURLGroup()
/*      */   {
/*  782 */     return this.announce_group;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void addTorrentAnnounceURLSet(URL[] urls)
/*      */   {
/*  789 */     this.announce_group.addSet(new TOTorrentAnnounceURLSetImpl(this, urls));
/*      */   }
/*      */   
/*      */ 
/*      */   public long getSize()
/*      */   {
/*  795 */     long res = 0L;
/*      */     
/*  797 */     for (int i = 0; i < this.files.length; i++)
/*      */     {
/*  799 */       res += this.files[i].getLength();
/*      */     }
/*      */     
/*  802 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getPieceLength()
/*      */   {
/*  808 */     return this.piece_length;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setPieceLength(long _length)
/*      */   {
/*  815 */     this.piece_length = _length;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getNumberOfPieces()
/*      */   {
/*  826 */     if (this.number_of_pieces == 0)
/*      */     {
/*  828 */       this.number_of_pieces = ((int)((getSize() + (this.piece_length - 1L)) / this.piece_length));
/*      */     }
/*      */     
/*  831 */     return this.number_of_pieces;
/*      */   }
/*      */   
/*      */ 
/*      */   public byte[][] getPieces()
/*      */   {
/*  837 */     return this.pieces;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setPieces(byte[][] _pieces)
/*      */   {
/*  844 */     this.pieces = _pieces;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getFileCount()
/*      */   {
/*  850 */     return this.files.length;
/*      */   }
/*      */   
/*      */ 
/*      */   public TOTorrentFile[] getFiles()
/*      */   {
/*  856 */     return this.files;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setFiles(TOTorrentFileImpl[] _files)
/*      */   {
/*  863 */     this.files = _files;
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean getSimpleTorrent()
/*      */   {
/*  869 */     return this.simple_torrent;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setSimpleTorrent(boolean _simple_torrent)
/*      */   {
/*  876 */     this.simple_torrent = _simple_torrent;
/*      */   }
/*      */   
/*      */ 
/*      */   protected Map getAdditionalProperties()
/*      */   {
/*  882 */     return this.additional_properties;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setAdditionalStringProperty(String name, String value)
/*      */   {
/*      */     try
/*      */     {
/*  892 */       setAdditionalByteArrayProperty(name, writeStringToMetaData(value));
/*      */ 
/*      */     }
/*      */     catch (TOTorrentException e)
/*      */     {
/*      */ 
/*  898 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getAdditionalStringProperty(String name)
/*      */   {
/*      */     try
/*      */     {
/*  908 */       return readStringFromMetaData(getAdditionalByteArrayProperty(name));
/*      */ 
/*      */     }
/*      */     catch (TOTorrentException e)
/*      */     {
/*      */ 
/*  914 */       Debug.printStackTrace(e);
/*      */     }
/*  916 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setAdditionalByteArrayProperty(String name, byte[] value)
/*      */   {
/*  925 */     this.additional_properties.put(name, value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setAdditionalProperty(String name, Object value)
/*      */   {
/*  933 */     if ((value instanceof String))
/*      */     {
/*  935 */       setAdditionalStringProperty(name, (String)value);
/*      */     }
/*      */     else
/*      */     {
/*  939 */       this.additional_properties.put(name, value);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public byte[] getAdditionalByteArrayProperty(String name)
/*      */   {
/*  947 */     Object obj = this.additional_properties.get(name);
/*      */     
/*  949 */     if ((obj instanceof byte[]))
/*      */     {
/*  951 */       return (byte[])obj;
/*      */     }
/*      */     
/*  954 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setAdditionalLongProperty(String name, Long value)
/*      */   {
/*  962 */     this.additional_properties.put(name, value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Long getAdditionalLongProperty(String name)
/*      */   {
/*  969 */     Object obj = this.additional_properties.get(name);
/*      */     
/*  971 */     if ((obj instanceof Long))
/*      */     {
/*  973 */       return (Long)obj;
/*      */     }
/*      */     
/*  976 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setAdditionalListProperty(String name, List value)
/*      */   {
/*  984 */     this.additional_properties.put(name, value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public List getAdditionalListProperty(String name)
/*      */   {
/*  991 */     Object obj = this.additional_properties.get(name);
/*      */     
/*  993 */     if ((obj instanceof List))
/*      */     {
/*  995 */       return (List)obj;
/*      */     }
/*      */     
/*  998 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setAdditionalMapProperty(String name, Map value)
/*      */   {
/* 1006 */     this.additional_properties.put(name, value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Map getAdditionalMapProperty(String name)
/*      */   {
/* 1013 */     Object obj = this.additional_properties.get(name);
/*      */     
/* 1015 */     if ((obj instanceof Map))
/*      */     {
/* 1017 */       return (Map)obj;
/*      */     }
/*      */     
/* 1020 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Object getAdditionalProperty(String name)
/*      */   {
/* 1027 */     return this.additional_properties.get(name);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeAdditionalProperty(String name)
/*      */   {
/* 1034 */     this.additional_properties.remove(name);
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeAdditionalProperties()
/*      */   {
/* 1040 */     Map new_props = new HashMap();
/*      */     
/* 1042 */     Iterator it = this.additional_properties.keySet().iterator();
/*      */     
/* 1044 */     while (it.hasNext())
/*      */     {
/* 1046 */       String key = (String)it.next();
/*      */       
/* 1048 */       if (TK_ADDITIONAL_OK_ATTRS.contains(key))
/*      */       {
/* 1050 */         new_props.put(key, this.additional_properties.get(key));
/*      */       }
/*      */     }
/*      */     
/* 1054 */     this.additional_properties = new_props;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void addAdditionalProperty(String name, Object value)
/*      */   {
/* 1062 */     this.additional_properties.put(name, value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void addAdditionalInfoProperty(String name, Object value)
/*      */   {
/* 1070 */     this.additional_info_properties.put(name, value);
/*      */   }
/*      */   
/*      */ 
/*      */   protected Map getAdditionalInfoProperties()
/*      */   {
/* 1076 */     return this.additional_info_properties;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String readStringFromMetaData(Map meta_data, String name)
/*      */     throws TOTorrentException
/*      */   {
/* 1086 */     Object obj = meta_data.get(name);
/*      */     
/* 1088 */     if ((obj instanceof byte[]))
/*      */     {
/* 1090 */       return readStringFromMetaData((byte[])obj);
/*      */     }
/*      */     
/* 1093 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected String readStringFromMetaData(byte[] value)
/*      */     throws TOTorrentException
/*      */   {
/*      */     try
/*      */     {
/* 1103 */       if (value == null)
/*      */       {
/* 1105 */         return null;
/*      */       }
/*      */       
/* 1108 */       return new String(value, "UTF8");
/*      */     }
/*      */     catch (UnsupportedEncodingException e)
/*      */     {
/* 1112 */       throw new TOTorrentException("Unsupported encoding for '" + value + "'", 7);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void writeStringToMetaData(Map meta_data, String name, String value)
/*      */     throws TOTorrentException
/*      */   {
/* 1125 */     meta_data.put(name, writeStringToMetaData(value));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected byte[] writeStringToMetaData(String value)
/*      */     throws TOTorrentException
/*      */   {
/*      */     try
/*      */     {
/* 1136 */       return value.getBytes("UTF8");
/*      */     }
/*      */     catch (UnsupportedEncodingException e)
/*      */     {
/* 1140 */       throw new TOTorrentException("Unsupported encoding for '" + value + "'", 7);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected URL anonymityTransform(URL url)
/*      */   {
/* 1178 */     return url;
/*      */   }
/*      */   
/*      */   public void print()
/*      */   {
/*      */     try
/*      */     {
/* 1185 */       byte[] hash = getHash();
/*      */       
/* 1187 */       System.out.println("name = " + this.torrent_name);
/* 1188 */       System.out.println("announce url = " + this.announce_url);
/* 1189 */       System.out.println("announce group = " + this.announce_group.getAnnounceURLSets().length);
/* 1190 */       System.out.println("creation date = " + this.creation_date);
/* 1191 */       System.out.println("creation by = " + this.created_by);
/* 1192 */       System.out.println("comment = " + this.comment);
/* 1193 */       System.out.println("hash = " + ByteFormatter.nicePrint(hash));
/* 1194 */       System.out.println("piece length = " + getPieceLength());
/* 1195 */       System.out.println("pieces = " + getNumberOfPieces());
/*      */       
/* 1197 */       Iterator info_it = this.additional_info_properties.keySet().iterator();
/*      */       
/* 1199 */       while (info_it.hasNext())
/*      */       {
/* 1201 */         String key = (String)info_it.next();
/* 1202 */         Object value = this.additional_info_properties.get(key);
/*      */         
/*      */         try
/*      */         {
/* 1206 */           System.out.println("info prop '" + key + "' = '" + ((value instanceof byte[]) ? new String((byte[])value, "UTF8") : value.toString()) + "'");
/*      */         }
/*      */         catch (UnsupportedEncodingException e)
/*      */         {
/* 1210 */           System.out.println("info prop '" + key + "' = unsupported encoding!!!!");
/*      */         }
/*      */       }
/*      */       
/* 1214 */       Iterator it = this.additional_properties.keySet().iterator();
/*      */       
/* 1216 */       while (it.hasNext())
/*      */       {
/* 1218 */         String key = (String)it.next();
/* 1219 */         Object value = this.additional_properties.get(key);
/*      */         
/*      */         try
/*      */         {
/* 1223 */           System.out.println("prop '" + key + "' = '" + ((value instanceof byte[]) ? new String((byte[])value, "UTF8") : value.toString()) + "'");
/*      */         }
/*      */         catch (UnsupportedEncodingException e)
/*      */         {
/* 1227 */           System.out.println("prop '" + key + "' = unsupported encoding!!!!");
/*      */         }
/*      */       }
/*      */       
/* 1231 */       if (this.pieces == null)
/*      */       {
/* 1233 */         System.out.println("\tpieces = null");
/*      */       }
/*      */       else {
/* 1236 */         for (int i = 0; i < this.pieces.length; i++)
/*      */         {
/* 1238 */           System.out.println("\t" + ByteFormatter.nicePrint(this.pieces[i]));
/*      */         }
/*      */       }
/*      */       
/* 1242 */       for (int i = 0; i < this.files.length; i++)
/*      */       {
/* 1244 */         byte[][] path_comps = this.files[i].getPathComponents();
/*      */         
/* 1246 */         String path_str = "";
/*      */         
/* 1248 */         for (int j = 0; j < path_comps.length; j++)
/*      */         {
/*      */           try
/*      */           {
/* 1252 */             path_str = path_str + (j == 0 ? "" : File.separator) + new String(path_comps[j], "UTF8");
/*      */           }
/*      */           catch (UnsupportedEncodingException e)
/*      */           {
/* 1256 */             System.out.println("file - unsupported encoding!!!!");
/*      */           }
/*      */         }
/*      */         
/* 1260 */         System.out.println("\t" + path_str + " (" + this.files[i].getLength() + ")");
/*      */       }
/*      */     }
/*      */     catch (TOTorrentException e) {
/* 1264 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void fireChanged(int type)
/*      */   {
/* 1272 */     List<TOTorrentListener> to_fire = null;
/*      */     try
/*      */     {
/* 1275 */       this.this_mon.enter();
/*      */       
/* 1277 */       if (this.listeners != null)
/*      */       {
/* 1279 */         to_fire = new ArrayList(this.listeners);
/*      */       }
/*      */     }
/*      */     finally {
/* 1283 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 1286 */     if (to_fire != null)
/*      */     {
/* 1288 */       for (TOTorrentListener l : to_fire) {
/*      */         try
/*      */         {
/* 1291 */           l.torrentChanged(this, type);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1295 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void addListener(TOTorrentListener l)
/*      */   {
/*      */     try
/*      */     {
/* 1306 */       this.this_mon.enter();
/*      */       
/* 1308 */       if (this.listeners == null)
/*      */       {
/* 1310 */         this.listeners = new ArrayList();
/*      */       }
/*      */       
/* 1313 */       this.listeners.add(l);
/*      */     }
/*      */     finally
/*      */     {
/* 1317 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeListener(TOTorrentListener l)
/*      */   {
/*      */     try
/*      */     {
/* 1326 */       this.this_mon.enter();
/*      */       
/* 1328 */       if (this.listeners != null)
/*      */       {
/* 1330 */         this.listeners.remove(l);
/*      */         
/* 1332 */         if (this.listeners.size() == 0)
/*      */         {
/* 1334 */           this.listeners = null;
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1339 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public AEMonitor getMonitor()
/*      */   {
/* 1346 */     return this.this_mon;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getRelationText()
/*      */   {
/* 1353 */     return "Torrent: '" + new String(this.torrent_name) + "'";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Object[] getQueryableInterfaces()
/*      */   {
/*      */     try
/*      */     {
/* 1362 */       return new Object[] { AzureusCoreFactory.getSingleton().getGlobalManager().getDownloadManager(this) };
/*      */     }
/*      */     catch (Exception e) {}
/*      */     
/*      */ 
/* 1367 */     return null;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/torrent/impl/TOTorrentImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */