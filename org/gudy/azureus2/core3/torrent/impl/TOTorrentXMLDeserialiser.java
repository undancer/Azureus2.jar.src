/*     */ package org.gudy.azureus2.core3.torrent.impl;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Vector;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.xml.simpleparser.SimpleXMLParserDocumentFactory;
/*     */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocument;
/*     */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentAttribute;
/*     */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentException;
/*     */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentNode;
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
/*     */ public class TOTorrentXMLDeserialiser
/*     */ {
/*     */   public TOTorrent deserialise(File file)
/*     */     throws TOTorrentException
/*     */   {
/*     */     try
/*     */     {
/*  59 */       SimpleXMLParserDocument doc = SimpleXMLParserDocumentFactory.create(file);
/*     */       
/*  61 */       return decodeRoot(doc);
/*     */ 
/*     */     }
/*     */     catch (SimpleXMLParserDocumentException e)
/*     */     {
/*     */ 
/*  67 */       throw new TOTorrentException("XML Parse Fails: " + e.getMessage(), 6);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected TOTorrent decodeRoot(SimpleXMLParserDocument doc)
/*     */     throws TOTorrentException
/*     */   {
/*  77 */     String root_name = doc.getName();
/*     */     
/*  79 */     if (root_name.equalsIgnoreCase("TORRENT"))
/*     */     {
/*  81 */       TOTorrentImpl torrent = new TOTorrentImpl();
/*     */       
/*  83 */       SimpleXMLParserDocumentNode[] kids = doc.getChildren();
/*     */       
/*  85 */       URL announce_url = null;
/*     */       
/*  87 */       byte[] torrent_hash = null;
/*  88 */       byte[] torrent_hash_override = null;
/*     */       
/*  90 */       for (int i = 0; i < kids.length; i++)
/*     */       {
/*  92 */         SimpleXMLParserDocumentNode kid = kids[i];
/*     */         
/*  94 */         String name = kid.getName();
/*     */         
/*  96 */         if (name.equalsIgnoreCase("ANNOUNCE_URL"))
/*     */         {
/*     */           try
/*     */           {
/* 100 */             announce_url = new URL(kid.getValue());
/*     */           }
/*     */           catch (MalformedURLException e)
/*     */           {
/* 104 */             throw new TOTorrentException("ANNOUNCE_URL malformed", 6);
/*     */           }
/*     */         }
/* 107 */         else if (name.equalsIgnoreCase("ANNOUNCE_LIST"))
/*     */         {
/* 109 */           SimpleXMLParserDocumentNode[] set_nodes = kid.getChildren();
/*     */           
/* 111 */           TOTorrentAnnounceURLGroup group = torrent.getAnnounceURLGroup();
/*     */           
/* 113 */           TOTorrentAnnounceURLSet[] sets = new TOTorrentAnnounceURLSet[set_nodes.length];
/*     */           
/* 115 */           for (int j = 0; j < sets.length; j++)
/*     */           {
/* 117 */             SimpleXMLParserDocumentNode[] url_nodes = set_nodes[j].getChildren();
/*     */             
/* 119 */             URL[] urls = new URL[url_nodes.length];
/*     */             
/* 121 */             for (int k = 0; k < urls.length; k++)
/*     */             {
/*     */               try
/*     */               {
/* 125 */                 urls[k] = new URL(url_nodes[k].getValue());
/*     */               }
/*     */               catch (MalformedURLException e)
/*     */               {
/* 129 */                 throw new TOTorrentException("ANNOUNCE_LIST malformed", 6);
/*     */               }
/*     */             }
/*     */             
/* 133 */             sets[j] = group.createAnnounceURLSet(urls);
/*     */           }
/*     */           
/* 136 */           group.setAnnounceURLSets(sets);
/*     */         }
/* 138 */         else if (name.equalsIgnoreCase("COMMENT"))
/*     */         {
/* 140 */           torrent.setComment(readLocalisableString(kid));
/*     */         }
/* 142 */         else if (name.equalsIgnoreCase("CREATED_BY"))
/*     */         {
/* 144 */           torrent.setCreatedBy(readLocalisableString(kid));
/*     */         }
/* 146 */         else if (name.equalsIgnoreCase("CREATION_DATE"))
/*     */         {
/* 148 */           torrent.setCreationDate(readGenericLong(kid).longValue());
/*     */         }
/* 150 */         else if (name.equalsIgnoreCase("TORRENT_HASH"))
/*     */         {
/* 152 */           torrent_hash = readGenericBytes(kid);
/*     */         }
/* 154 */         else if (name.equalsIgnoreCase("TORRENT_HASH_OVERRIDE"))
/*     */         {
/* 156 */           torrent_hash_override = readGenericBytes(kid);
/*     */         }
/* 158 */         else if (name.equalsIgnoreCase("INFO"))
/*     */         {
/* 160 */           decodeInfo(kid, torrent);
/*     */         }
/*     */         else
/*     */         {
/* 164 */           mapEntry entry = readGenericMapEntry(kid);
/*     */           
/* 166 */           torrent.addAdditionalProperty(entry.name, entry.value);
/*     */         }
/*     */       }
/*     */       
/* 170 */       if (announce_url == null)
/*     */       {
/* 172 */         throw new TOTorrentException("ANNOUNCE_URL missing", 6);
/*     */       }
/*     */       
/* 175 */       torrent.setAnnounceURL(announce_url);
/*     */       
/* 177 */       if (torrent_hash_override != null) {
/*     */         try
/*     */         {
/* 180 */           torrent.setHashOverride(torrent_hash_override);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 184 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */       
/* 188 */       if (torrent_hash != null)
/*     */       {
/* 190 */         if (!Arrays.equals(torrent.getHash(), torrent_hash))
/*     */         {
/* 192 */           throw new TOTorrentException("Hash differs - declared TORRENT_HASH and computed hash differ. If this really is the intent (unlikely) then remove the TORRENT_HASH element", 6);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 197 */       return torrent;
/*     */     }
/*     */     
/* 200 */     throw new TOTorrentException("Invalid root element", 6);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void decodeInfo(SimpleXMLParserDocumentNode doc, TOTorrentImpl torrent)
/*     */     throws TOTorrentException
/*     */   {
/* 211 */     SimpleXMLParserDocumentNode[] kids = doc.getChildren();
/*     */     
/* 213 */     byte[] torrent_name = null;
/* 214 */     long torrent_length = 0L;
/*     */     
/* 216 */     SimpleXMLParserDocumentNode[] file_nodes = null;
/*     */     
/* 218 */     for (int i = 0; i < kids.length; i++)
/*     */     {
/* 220 */       SimpleXMLParserDocumentNode kid = kids[i];
/*     */       
/* 222 */       String name = kid.getName();
/*     */       
/* 224 */       if (name.equalsIgnoreCase("PIECE_LENGTH"))
/*     */       {
/* 226 */         torrent.setPieceLength(readGenericLong(kid).longValue());
/*     */       }
/* 228 */       else if (name.equalsIgnoreCase("LENGTH"))
/*     */       {
/* 230 */         torrent.setSimpleTorrent(true);
/*     */         
/* 232 */         torrent_length = readGenericLong(kid).longValue();
/*     */       }
/* 234 */       else if (name.equalsIgnoreCase("NAME"))
/*     */       {
/* 236 */         torrent.setName(readLocalisableString(kid));
/*     */       }
/* 238 */       else if (name.equalsIgnoreCase("FILES"))
/*     */       {
/* 240 */         torrent.setSimpleTorrent(false);
/*     */         
/* 242 */         file_nodes = kid.getChildren();
/*     */       }
/* 244 */       else if (name.equalsIgnoreCase("PIECES"))
/*     */       {
/* 246 */         SimpleXMLParserDocumentNode[] piece_nodes = kid.getChildren();
/*     */         
/* 248 */         byte[][] pieces = new byte[piece_nodes.length][];
/*     */         
/* 250 */         for (int j = 0; j < pieces.length; j++)
/*     */         {
/* 252 */           pieces[j] = readGenericBytes(piece_nodes[j]);
/*     */         }
/*     */         
/* 255 */         torrent.setPieces(pieces);
/*     */       }
/*     */       else
/*     */       {
/* 259 */         mapEntry entry = readGenericMapEntry(kid);
/*     */         
/* 261 */         torrent.addAdditionalInfoProperty(entry.name, entry.value);
/*     */       }
/*     */     }
/*     */     
/* 265 */     if (torrent.isSimpleTorrent())
/*     */     {
/* 267 */       torrent.setFiles(new TOTorrentFileImpl[] { new TOTorrentFileImpl(torrent, 0, 0L, torrent_length, new byte[][] { torrent.getName() }) });
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/*     */ 
/* 276 */       if (file_nodes == null)
/*     */       {
/* 278 */         throw new TOTorrentException("FILES element missing", 6);
/*     */       }
/*     */       
/* 281 */       TOTorrentFileImpl[] files = new TOTorrentFileImpl[file_nodes.length];
/*     */       
/* 283 */       long offset = 0L;
/*     */       
/* 285 */       for (int j = 0; j < files.length; j++)
/*     */       {
/* 287 */         SimpleXMLParserDocumentNode file_node = file_nodes[j];
/*     */         
/* 289 */         SimpleXMLParserDocumentNode[] file_entries = file_node.getChildren();
/*     */         
/* 291 */         long file_length = 0L;
/*     */         
/* 293 */         boolean length_entry_found = false;
/*     */         
/* 295 */         byte[][] path_comps = (byte[][])null;
/*     */         
/* 297 */         Vector additional_props = new Vector();
/*     */         
/* 299 */         for (int k = 0; k < file_entries.length; k++)
/*     */         {
/* 301 */           SimpleXMLParserDocumentNode file_entry = file_entries[k];
/*     */           
/* 303 */           String entry_name = file_entry.getName();
/*     */           
/* 305 */           if (entry_name.equalsIgnoreCase("LENGTH"))
/*     */           {
/* 307 */             file_length = readGenericLong(file_entry).longValue();
/*     */             
/* 309 */             length_entry_found = true;
/*     */           }
/* 311 */           else if (entry_name.equalsIgnoreCase("PATH"))
/*     */           {
/* 313 */             SimpleXMLParserDocumentNode[] path_nodes = file_entry.getChildren();
/*     */             
/* 315 */             path_comps = new byte[path_nodes.length][];
/*     */             
/* 317 */             for (int n = 0; n < path_nodes.length; n++)
/*     */             {
/* 319 */               path_comps[n] = readLocalisableString(path_nodes[n]);
/*     */             }
/*     */           }
/*     */           else {
/* 323 */             additional_props.addElement(readGenericMapEntry(file_entry));
/*     */           }
/*     */         }
/*     */         
/* 327 */         if ((!length_entry_found) || (path_comps == null))
/*     */         {
/* 329 */           throw new TOTorrentException("FILE element invalid (file length = " + file_length + ")", 6);
/*     */         }
/*     */         
/* 332 */         files[j] = new TOTorrentFileImpl(torrent, j, offset, file_length, path_comps);
/*     */         
/* 334 */         offset += file_length;
/*     */         
/* 336 */         for (int k = 0; k < additional_props.size(); k++)
/*     */         {
/* 338 */           mapEntry entry = (mapEntry)additional_props.elementAt(k);
/*     */           
/* 340 */           files[j].setAdditionalProperty(entry.name, entry.value);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 345 */       torrent.setFiles(files);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected mapEntry readGenericMapEntry(SimpleXMLParserDocumentNode node)
/*     */     throws TOTorrentException
/*     */   {
/* 355 */     if (!node.getName().equalsIgnoreCase("KEY"))
/*     */     {
/* 357 */       throw new TOTorrentException("Additional property invalid, must be KEY node", 6);
/*     */     }
/*     */     
/* 360 */     String name = node.getAttribute("name").getValue();
/*     */     
/* 362 */     SimpleXMLParserDocumentNode[] kids = node.getChildren();
/*     */     
/* 364 */     if (kids.length != 1)
/*     */     {
/* 366 */       throw new TOTorrentException("Additional property invalid, KEY must have one child", 6);
/*     */     }
/*     */     
/* 369 */     String type = kids[0].getName();
/*     */     
/* 371 */     Object value = readGenericValue(kids[0]);
/*     */     
/* 373 */     return new mapEntry(name, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Object readGenericValue(SimpleXMLParserDocumentNode node)
/*     */     throws TOTorrentException
/*     */   {
/* 382 */     String name = node.getName();
/*     */     
/* 384 */     if (name.equalsIgnoreCase("BYTES"))
/*     */     {
/* 386 */       return readGenericBytes(node);
/*     */     }
/* 388 */     if (name.equalsIgnoreCase("LONG"))
/*     */     {
/* 390 */       return readGenericLong(node);
/*     */     }
/* 392 */     if (name.equalsIgnoreCase("LIST"))
/*     */     {
/* 394 */       return readGenericList(node);
/*     */     }
/* 396 */     if (name.equalsIgnoreCase("MAP"))
/*     */     {
/* 398 */       return readGenericMap(node);
/*     */     }
/*     */     
/*     */ 
/* 402 */     throw new TOTorrentException("Additional property invalid, sub-key '" + name + "' not recognised", 6);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected byte[] readGenericBytes(SimpleXMLParserDocumentNode node)
/*     */     throws TOTorrentException
/*     */   {
/* 412 */     String value = node.getValue();
/*     */     
/* 414 */     byte[] res = new byte[value.length() / 2];
/*     */     
/* 416 */     for (int i = 0; i < res.length; i++)
/*     */     {
/* 418 */       res[i] = ((byte)Integer.parseInt(value.substring(i * 2, i * 2 + 2), 16));
/*     */     }
/*     */     
/*     */ 
/* 422 */     return res;
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
/*     */   protected Long readGenericLong(SimpleXMLParserDocumentNode node)
/*     */     throws TOTorrentException
/*     */   {
/* 441 */     String value = node.getValue();
/*     */     
/*     */     try
/*     */     {
/* 445 */       return new Long(value);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 449 */       throw new TOTorrentException("long value invalid for '" + node.getName() + "'", 6);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Map readGenericMap(SimpleXMLParserDocumentNode node)
/*     */     throws TOTorrentException
/*     */   {
/* 459 */     Map res = new HashMap();
/*     */     
/* 461 */     SimpleXMLParserDocumentNode[] kids = node.getChildren();
/*     */     
/* 463 */     for (int i = 0; i < kids.length; i++)
/*     */     {
/* 465 */       mapEntry entry = readGenericMapEntry(kids[i]);
/*     */       
/* 467 */       res.put(entry.name, entry.value);
/*     */     }
/*     */     
/* 470 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected byte[] readLocalisableString(SimpleXMLParserDocumentNode kid)
/*     */     throws TOTorrentException
/*     */   {
/* 479 */     SimpleXMLParserDocumentAttribute attr = kid.getAttribute("encoding");
/*     */     
/* 481 */     if ((attr == null) || (attr.getValue().equalsIgnoreCase("bytes")))
/*     */     {
/* 483 */       return readGenericBytes(kid);
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 488 */       return kid.getValue().getBytes("UTF8");
/*     */     }
/*     */     catch (UnsupportedEncodingException e)
/*     */     {
/* 492 */       throw new TOTorrentException("bytes invalid - unsupported encoding", 6);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected List readGenericList(SimpleXMLParserDocumentNode node)
/*     */     throws TOTorrentException
/*     */   {
/* 502 */     List res = new ArrayList();
/*     */     
/* 504 */     SimpleXMLParserDocumentNode[] kids = node.getChildren();
/*     */     
/* 506 */     for (int i = 0; i < kids.length; i++)
/*     */     {
/* 508 */       res.add(readGenericValue(kids[i]));
/*     */     }
/*     */     
/* 511 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   protected static class mapEntry
/*     */   {
/*     */     final String name;
/*     */     
/*     */     final Object value;
/*     */     
/*     */ 
/*     */     mapEntry(String _name, Object _value)
/*     */     {
/* 524 */       this.name = _name;
/* 525 */       this.value = _value;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/torrent/impl/TOTorrentXMLDeserialiser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */