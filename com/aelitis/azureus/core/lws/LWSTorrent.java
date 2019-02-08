/*     */ package com.aelitis.azureus.core.lws;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.net.URL;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.logging.LogRelation;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentListener;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
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
/*     */ public class LWSTorrent
/*     */   extends LogRelation
/*     */   implements TOTorrent
/*     */ {
/*  46 */   private static final TOTorrentAnnounceURLGroup announce_group = new TOTorrentAnnounceURLGroup()
/*     */   {
/*     */ 
/*  49 */     private TOTorrentAnnounceURLSet[] sets = new TOTorrentAnnounceURLSet[0];
/*     */     
/*     */ 
/*     */     public TOTorrentAnnounceURLSet[] getAnnounceURLSets()
/*     */     {
/*  54 */       return this.sets;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void setAnnounceURLSets(TOTorrentAnnounceURLSet[] _sets)
/*     */     {
/*  61 */       this.sets = _sets;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public TOTorrentAnnounceURLSet createAnnounceURLSet(final URL[] _urls)
/*     */     {
/*  68 */       new TOTorrentAnnounceURLSet()
/*     */       {
/*     */ 
/*  71 */         private URL[] urls = _urls;
/*     */         
/*     */ 
/*     */         public URL[] getAnnounceURLs()
/*     */         {
/*  76 */           return this.urls;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void setAnnounceURLs(URL[] _urls)
/*     */         {
/*  83 */           this.urls = _urls;
/*     */         }
/*     */       };
/*     */     }
/*     */   };
/*     */   private final LightWeightSeed lws;
/*     */   
/*     */   private static void notSupported()
/*     */   {
/*  92 */     Debug.out("Not Supported");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected LWSTorrent(LightWeightSeed _lws)
/*     */   {
/* 103 */     this.lws = _lws;
/*     */   }
/*     */   
/*     */ 
/*     */   protected TOTorrent getDelegate()
/*     */   {
/* 109 */     return this.lws.getTOTorrent(true);
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getName()
/*     */   {
/* 115 */     return this.lws.getName().getBytes();
/*     */   }
/*     */   
/*     */   public String getUTF8Name() {
/* 119 */     return this.lws.getName();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isSimpleTorrent()
/*     */   {
/* 125 */     return getDelegate().isSimpleTorrent();
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getComment()
/*     */   {
/* 131 */     return getDelegate().getComment();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setComment(String comment)
/*     */   {
/* 138 */     getDelegate().setComment(comment);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getCreationDate()
/*     */   {
/* 144 */     return getDelegate().getCreationDate();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDecentralised()
/*     */   {
/* 150 */     return getDelegate().isDecentralised();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setCreationDate(long date)
/*     */   {
/* 157 */     getDelegate().setCreationDate(date);
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getCreatedBy()
/*     */   {
/* 163 */     return getDelegate().getCreatedBy();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setCreatedBy(byte[] cb)
/*     */   {
/* 170 */     getDelegate().setCreatedBy(cb);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isCreated()
/*     */   {
/* 176 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public URL getAnnounceURL()
/*     */   {
/* 182 */     return this.lws.getAnnounceURL();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean setAnnounceURL(URL url)
/*     */   {
/* 189 */     notSupported();
/*     */     
/* 191 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public TOTorrentAnnounceURLGroup getAnnounceURLGroup()
/*     */   {
/* 197 */     return announce_group;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[][] getPieces()
/*     */     throws TOTorrentException
/*     */   {
/* 205 */     return getDelegate().getPieces();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPieces(byte[][] pieces)
/*     */     throws TOTorrentException
/*     */   {
/* 215 */     getDelegate().setPieces(pieces);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getPieceLength()
/*     */   {
/* 221 */     return getDelegate().getPieceLength();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNumberOfPieces()
/*     */   {
/* 227 */     return getDelegate().getNumberOfPieces();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSize()
/*     */   {
/* 233 */     return this.lws.getSize();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getFileCount()
/*     */   {
/* 239 */     return getDelegate().getFileCount();
/*     */   }
/*     */   
/*     */ 
/*     */   public TOTorrentFile[] getFiles()
/*     */   {
/* 245 */     return getDelegate().getFiles();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] getHash()
/*     */     throws TOTorrentException
/*     */   {
/* 253 */     return this.lws.getHash().getBytes();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public HashWrapper getHashWrapper()
/*     */     throws TOTorrentException
/*     */   {
/* 261 */     return this.lws.getHash();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setHashOverride(byte[] hash)
/*     */     throws TOTorrentException
/*     */   {
/* 270 */     throw new TOTorrentException("Not supported", 8);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean hasSameHashAs(TOTorrent other)
/*     */   {
/*     */     try
/*     */     {
/* 278 */       byte[] other_hash = other.getHash();
/*     */       
/* 280 */       return Arrays.equals(getHash(), other_hash);
/*     */     }
/*     */     catch (TOTorrentException e)
/*     */     {
/* 284 */       Debug.printStackTrace(e);
/*     */     }
/* 286 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean getPrivate()
/*     */   {
/* 293 */     return false;
/*     */   }
/*     */   
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
/*     */ 
/*     */ 
/*     */   public void setAdditionalStringProperty(String name, String value)
/*     */   {
/* 310 */     getDelegate().setAdditionalStringProperty(name, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getAdditionalStringProperty(String name)
/*     */   {
/* 317 */     return getDelegate().getAdditionalStringProperty(name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAdditionalByteArrayProperty(String name, byte[] value)
/*     */   {
/* 325 */     getDelegate().setAdditionalByteArrayProperty(name, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] getAdditionalByteArrayProperty(String name)
/*     */   {
/* 332 */     return getDelegate().getAdditionalByteArrayProperty(name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAdditionalLongProperty(String name, Long value)
/*     */   {
/* 340 */     getDelegate().setAdditionalLongProperty(name, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Long getAdditionalLongProperty(String name)
/*     */   {
/* 347 */     return getDelegate().getAdditionalLongProperty(name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAdditionalListProperty(String name, List value)
/*     */   {
/* 356 */     getDelegate().setAdditionalListProperty(name, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public List getAdditionalListProperty(String name)
/*     */   {
/* 363 */     return getDelegate().getAdditionalListProperty(name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAdditionalMapProperty(String name, Map value)
/*     */   {
/* 371 */     getDelegate().setAdditionalMapProperty(name, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Map getAdditionalMapProperty(String name)
/*     */   {
/* 378 */     return getDelegate().getAdditionalMapProperty(name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getAdditionalProperty(String name)
/*     */   {
/* 385 */     if ((name.equals("url-list")) || (name.equals("httpseeds")))
/*     */     {
/* 387 */       return null;
/*     */     }
/*     */     
/* 390 */     return getDelegate().getAdditionalProperty(name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAdditionalProperty(String name, Object value)
/*     */   {
/* 398 */     getDelegate().setAdditionalProperty(name, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeAdditionalProperty(String name)
/*     */   {
/* 405 */     getDelegate().removeAdditionalProperty(name);
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeAdditionalProperties()
/*     */   {
/* 411 */     getDelegate().removeAdditionalProperties();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialiseToBEncodedFile(File file)
/*     */     throws TOTorrentException
/*     */   {
/* 420 */     getDelegate().serialiseToBEncodedFile(file);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(TOTorrentListener l)
/*     */   {
/* 427 */     getDelegate().addListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(TOTorrentListener l)
/*     */   {
/* 434 */     getDelegate().removeListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Map serialiseToMap()
/*     */     throws TOTorrentException
/*     */   {
/* 442 */     return getDelegate().serialiseToMap();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialiseToXMLFile(File file)
/*     */     throws TOTorrentException
/*     */   {
/* 451 */     getDelegate().serialiseToXMLFile(file);
/*     */   }
/*     */   
/*     */ 
/*     */   public AEMonitor getMonitor()
/*     */   {
/* 457 */     return getDelegate().getMonitor();
/*     */   }
/*     */   
/*     */ 
/*     */   public void print()
/*     */   {
/* 463 */     getDelegate().print();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getRelationText()
/*     */   {
/* 469 */     return "LWTorrent: '" + new String(getName()) + "'";
/*     */   }
/*     */   
/*     */ 
/*     */   public Object[] getQueryableInterfaces()
/*     */   {
/* 475 */     return new Object[] { this.lws };
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/lws/LWSTorrent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */