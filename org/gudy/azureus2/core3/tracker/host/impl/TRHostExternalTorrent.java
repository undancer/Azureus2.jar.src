/*     */ package org.gudy.azureus2.core3.tracker.host.impl;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.net.URL;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.internat.LocaleTorrentUtil;
/*     */ import org.gudy.azureus2.core3.internat.LocaleUtilEncodingException;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentListener;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TRHostExternalTorrent
/*     */   implements TOTorrent
/*     */ {
/*     */   private final byte[] name;
/*     */   private final byte[] hash;
/*     */   private final HashWrapper hash_wrapper;
/*     */   private final URL announce_url;
/*  47 */   protected final Map additional_properties = new HashMap();
/*     */   
/*  49 */   protected final AEMonitor this_mon = new AEMonitor("TRHostExternalTorrent");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected TRHostExternalTorrent(byte[] _hash, URL _announce_url)
/*     */   {
/*  56 */     this.hash = _hash;
/*  57 */     this.hash_wrapper = new HashWrapper(this.hash);
/*  58 */     this.announce_url = _announce_url;
/*     */     
/*  60 */     this.name = ByteFormatter.nicePrint(this.hash, true).getBytes();
/*     */     try
/*     */     {
/*  63 */       LocaleTorrentUtil.setDefaultTorrentEncoding(this);
/*     */     }
/*     */     catch (LocaleUtilEncodingException e)
/*     */     {
/*  67 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getName()
/*     */   {
/*  74 */     return this.name;
/*     */   }
/*     */   
/*     */   public String getUTF8Name() {
/*  78 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isSimpleTorrent()
/*     */   {
/*  85 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] getComment()
/*     */   {
/*  92 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setComment(String comment) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getCreationDate()
/*     */   {
/* 105 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setCreationDate(long date) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] getCreatedBy()
/*     */   {
/* 117 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setCreatedBy(byte[] cb) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isCreated()
/*     */   {
/* 129 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDecentralised()
/*     */   {
/* 135 */     return TorrentUtils.isDecentralised(getAnnounceURL());
/*     */   }
/*     */   
/*     */ 
/*     */   public URL getAnnounceURL()
/*     */   {
/* 141 */     return this.announce_url;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean setAnnounceURL(URL url)
/*     */   {
/* 148 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public TOTorrentAnnounceURLGroup getAnnounceURLGroup()
/*     */   {
/* 154 */     new TOTorrentAnnounceURLGroup()
/*     */     {
/*     */ 
/*     */       public TOTorrentAnnounceURLSet[] getAnnounceURLSets()
/*     */       {
/*     */ 
/* 160 */         return new TOTorrentAnnounceURLSet[0];
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void setAnnounceURLSets(TOTorrentAnnounceURLSet[] sets) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public TOTorrentAnnounceURLSet createAnnounceURLSet(URL[] urls)
/*     */       {
/* 173 */         new TOTorrentAnnounceURLSet()
/*     */         {
/*     */ 
/*     */           public URL[] getAnnounceURLs()
/*     */           {
/* 178 */             return new URL[0];
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           public void setAnnounceURLs(URL[] urls) {}
/*     */         };
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addTorrentAnnounceURLSet(URL[] urls) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[][] getPieces()
/*     */   {
/* 201 */     return new byte[0][];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPieces(byte[][] b) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public int getNumberOfPieces()
/*     */   {
/* 213 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getPieceLength()
/*     */   {
/* 219 */     return -1L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSize()
/*     */   {
/* 225 */     return -1L;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getFileCount()
/*     */   {
/* 231 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public TOTorrentFile[] getFiles()
/*     */   {
/* 237 */     return new TOTorrentFile[0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] getHash()
/*     */     throws TOTorrentException
/*     */   {
/* 245 */     return this.hash;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public HashWrapper getHashWrapper()
/*     */     throws TOTorrentException
/*     */   {
/* 253 */     return this.hash_wrapper;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setHashOverride(byte[] hash)
/*     */     throws TOTorrentException
/*     */   {
/* 262 */     throw new TOTorrentException("Not supported", 8);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getPrivate()
/*     */   {
/* 268 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPrivate(boolean _private)
/*     */     throws TOTorrentException
/*     */   {}
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean hasSameHashAs(TOTorrent other)
/*     */   {
/*     */     try
/*     */     {
/* 284 */       byte[] other_hash = other.getHash();
/*     */       
/* 286 */       return Arrays.equals(this.hash, other_hash);
/*     */     }
/*     */     catch (TOTorrentException e)
/*     */     {
/* 290 */       Debug.printStackTrace(e);
/*     */     }
/* 292 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAdditionalStringProperty(String name, String value)
/*     */   {
/*     */     try
/*     */     {
/* 303 */       this.additional_properties.put(name, value.getBytes("UTF8"));
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 307 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getAdditionalStringProperty(String name)
/*     */   {
/*     */     try
/*     */     {
/* 316 */       Object obj = this.additional_properties.get(name);
/*     */       
/* 318 */       if (obj == null)
/*     */       {
/* 320 */         return null;
/*     */       }
/*     */       
/* 323 */       if (!(obj instanceof byte[]))
/*     */       {
/* 325 */         Debug.out("property '" + name + "' is not a byte[]: " + obj);
/*     */         
/* 327 */         return null;
/*     */       }
/*     */       
/* 330 */       return new String((byte[])obj, "UTF8");
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 334 */       Debug.printStackTrace(e);
/*     */     }
/* 336 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAdditionalByteArrayProperty(String name, byte[] value)
/*     */   {
/* 345 */     this.additional_properties.put(name, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] getAdditionalByteArrayProperty(String name)
/*     */   {
/* 352 */     return (byte[])this.additional_properties.get(name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAdditionalLongProperty(String name, Long value)
/*     */   {
/* 360 */     this.additional_properties.put(name, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAdditionalProperty(String name, Object value)
/*     */   {
/* 368 */     if ((value instanceof String))
/*     */     {
/* 370 */       setAdditionalStringProperty(name, (String)value);
/*     */     }
/*     */     else
/*     */     {
/* 374 */       this.additional_properties.put(name, value);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Long getAdditionalLongProperty(String name)
/*     */   {
/* 382 */     return (Long)this.additional_properties.get(name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAdditionalListProperty(String name, List value)
/*     */   {
/* 391 */     this.additional_properties.put(name, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public List getAdditionalListProperty(String name)
/*     */   {
/* 398 */     return (List)this.additional_properties.get(name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAdditionalMapProperty(String name, Map value)
/*     */   {
/* 406 */     this.additional_properties.put(name, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Map getAdditionalMapProperty(String name)
/*     */   {
/* 413 */     return (Map)this.additional_properties.get(name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getAdditionalProperty(String name)
/*     */   {
/* 420 */     return this.additional_properties.get(name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeAdditionalProperty(String name)
/*     */   {
/* 427 */     this.additional_properties.remove(name);
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeAdditionalProperties()
/*     */   {
/* 433 */     this.additional_properties.clear();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialiseToBEncodedFile(File file)
/*     */     throws TOTorrentException
/*     */   {
/* 442 */     throw new TOTorrentException("External Torrent", 5);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map serialiseToMap()
/*     */     throws TOTorrentException
/*     */   {
/* 451 */     throw new TOTorrentException("External Torrent", 5);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialiseToXMLFile(File file)
/*     */     throws TOTorrentException
/*     */   {
/* 460 */     throw new TOTorrentException("External Torrent", 5);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addListener(TOTorrentListener l) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeListener(TOTorrentListener l) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public AEMonitor getMonitor()
/*     */   {
/* 478 */     return this.this_mon;
/*     */   }
/*     */   
/*     */   public void print() {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/host/impl/TRHostExternalTorrent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */