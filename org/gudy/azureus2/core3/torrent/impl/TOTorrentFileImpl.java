/*     */ package org.gudy.azureus2.core3.torrent.impl;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.Vector;
/*     */ import org.gudy.azureus2.core3.internat.LocaleTorrentUtil;
/*     */ import org.gudy.azureus2.core3.internat.LocaleUtil;
/*     */ import org.gudy.azureus2.core3.internat.LocaleUtilDecoder;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.LightHashMap;
/*     */ import org.gudy.azureus2.core3.util.StringInterner;
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
/*     */ public class TOTorrentFileImpl
/*     */   implements TOTorrentFile
/*     */ {
/*     */   private final TOTorrent torrent;
/*     */   private final int index;
/*     */   private final long file_length;
/*     */   private final byte[][] path_components;
/*     */   private final byte[][] path_components_utf8;
/*     */   private final int first_piece_number;
/*     */   private final int last_piece_number;
/*     */   private Map additional_properties_maybe_null;
/*     */   private final boolean is_utf8;
/*     */   
/*     */   protected TOTorrentFileImpl(TOTorrent _torrent, int _index, long _torrent_offset, long _len, String _path)
/*     */     throws TOTorrentException
/*     */   {
/*  60 */     this.torrent = _torrent;
/*  61 */     this.index = _index;
/*  62 */     this.file_length = _len;
/*     */     
/*  64 */     this.first_piece_number = ((int)(_torrent_offset / this.torrent.getPieceLength()));
/*  65 */     this.last_piece_number = ((int)((_torrent_offset + this.file_length - 1L) / this.torrent.getPieceLength()));
/*     */     
/*  67 */     this.is_utf8 = true;
/*     */     
/*     */ 
/*     */     try
/*     */     {
/*  72 */       Vector temp = new Vector();
/*     */       
/*  74 */       int pos = 0;
/*     */       
/*     */       for (;;)
/*     */       {
/*  78 */         int p1 = _path.indexOf(File.separator, pos);
/*     */         
/*  80 */         if (p1 == -1)
/*     */         {
/*  82 */           temp.add(_path.substring(pos).getBytes("UTF8"));
/*     */           
/*  84 */           break;
/*     */         }
/*     */         
/*  87 */         temp.add(_path.substring(pos, p1).getBytes("UTF8"));
/*     */         
/*  89 */         pos = p1 + 1;
/*     */       }
/*     */       
/*  92 */       this.path_components = new byte[temp.size()][];
/*     */       
/*  94 */       temp.copyInto(this.path_components);
/*     */       
/*  96 */       this.path_components_utf8 = new byte[temp.size()][];
/*     */       
/*  98 */       temp.copyInto(this.path_components_utf8);
/*     */       
/* 100 */       checkComponents();
/*     */     }
/*     */     catch (UnsupportedEncodingException e)
/*     */     {
/* 104 */       throw new TOTorrentException("Unsupported encoding for '" + _path + "'", 7);
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
/*     */   protected TOTorrentFileImpl(TOTorrent _torrent, int _index, long _torrent_offset, long _len, byte[][] _path_components)
/*     */     throws TOTorrentException
/*     */   {
/* 119 */     this.torrent = _torrent;
/* 120 */     this.index = _index;
/* 121 */     this.file_length = _len;
/* 122 */     this.path_components = _path_components;
/* 123 */     this.path_components_utf8 = ((byte[][])null);
/*     */     
/* 125 */     this.first_piece_number = ((int)(_torrent_offset / this.torrent.getPieceLength()));
/* 126 */     this.last_piece_number = ((int)((_torrent_offset + this.file_length - 1L) / this.torrent.getPieceLength()));
/*     */     
/* 128 */     this.is_utf8 = false;
/*     */     
/* 130 */     checkComponents();
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
/*     */   protected TOTorrentFileImpl(TOTorrent _torrent, int _index, long _torrent_offset, long _len, byte[][] _path_components, byte[][] _path_components_utf8)
/*     */     throws TOTorrentException
/*     */   {
/* 144 */     this.torrent = _torrent;
/* 145 */     this.index = _index;
/* 146 */     this.file_length = _len;
/* 147 */     this.path_components = _path_components;
/* 148 */     this.path_components_utf8 = _path_components_utf8;
/*     */     
/* 150 */     this.first_piece_number = ((int)(_torrent_offset / this.torrent.getPieceLength()));
/* 151 */     this.last_piece_number = ((int)((_torrent_offset + this.file_length - 1L) / this.torrent.getPieceLength()));
/*     */     
/* 153 */     this.is_utf8 = false;
/*     */     
/* 155 */     checkComponents();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void checkComponents()
/*     */     throws TOTorrentException
/*     */   {
/* 163 */     byte[][][] to_do = { this.path_components, this.path_components_utf8 };
/*     */     
/* 165 */     for (byte[][] pc : to_do)
/*     */     {
/* 167 */       if (pc != null)
/*     */       {
/*     */ 
/*     */ 
/* 171 */         for (int i = 0; i < pc.length; i++)
/*     */         {
/* 173 */           byte[] comp = pc[i];
/* 174 */           if ((comp.length == 2) && (comp[0] == 46) && (comp[1] == 46)) {
/* 175 */             throw new TOTorrentException("Torrent file contains illegal '..' component", 6);
/*     */           }
/*     */           
/* 178 */           if (i < pc.length - 1) {
/* 179 */             pc[i] = StringInterner.internBytes(pc[i]);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public TOTorrent getTorrent() {
/* 187 */     return this.torrent;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getIndex()
/*     */   {
/* 193 */     return this.index;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLength()
/*     */   {
/* 199 */     return this.file_length;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[][] getPathComponentsBasic()
/*     */   {
/* 205 */     return this.path_components;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[][] getPathComponents()
/*     */   {
/* 211 */     return this.path_components_utf8 == null ? this.path_components : this.path_components_utf8;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[][] getPathComponentsUTF8()
/*     */   {
/* 217 */     return this.path_components_utf8;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean isUTF8()
/*     */   {
/* 224 */     return this.is_utf8;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setAdditionalProperty(String name, Object value)
/*     */   {
/* 232 */     if (this.additional_properties_maybe_null == null)
/*     */     {
/* 234 */       this.additional_properties_maybe_null = new LightHashMap();
/*     */     }
/*     */     
/* 237 */     this.additional_properties_maybe_null.put(name, value);
/*     */   }
/*     */   
/*     */ 
/*     */   public Map getAdditionalProperties()
/*     */   {
/* 243 */     return this.additional_properties_maybe_null;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getFirstPieceNumber()
/*     */   {
/* 249 */     return this.first_piece_number;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLastPieceNumber()
/*     */   {
/* 255 */     return this.last_piece_number;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNumberOfPieces()
/*     */   {
/* 261 */     return getLastPieceNumber() - getFirstPieceNumber() + 1;
/*     */   }
/*     */   
/*     */   public String getRelativePath() {
/* 265 */     if (this.torrent == null) {
/* 266 */       return "";
/*     */     }
/*     */     
/* 269 */     byte[][] pathComponentsUTF8 = getPathComponentsUTF8();
/* 270 */     if (pathComponentsUTF8 != null) {
/* 271 */       StringBuilder sRelativePathSB = null;
/*     */       
/* 273 */       for (int j = 0; j < pathComponentsUTF8.length; j++) {
/*     */         try
/*     */         {
/*     */           try
/*     */           {
/* 278 */             comp = new String(pathComponentsUTF8[j], "utf8");
/*     */           } catch (UnsupportedEncodingException e) {
/* 280 */             System.out.println("file - unsupported encoding!!!!");
/* 281 */             comp = "UnsupportedEncoding";
/*     */           }
/*     */           
/* 284 */           String comp = FileUtil.convertOSSpecificChars(comp, j != pathComponentsUTF8.length - 1);
/*     */           
/* 286 */           if (j == 0) {
/* 287 */             if (pathComponentsUTF8.length == 1) {
/* 288 */               return comp;
/*     */             }
/* 290 */             sRelativePathSB = new StringBuilder(512);
/*     */           }
/*     */           else {
/* 293 */             sRelativePathSB.append(File.separator);
/*     */           }
/*     */           
/* 296 */           sRelativePathSB.append(comp);
/*     */         } catch (Exception ex) {
/* 298 */           Debug.out(ex);
/*     */         }
/*     */       }
/*     */       
/* 302 */       return sRelativePathSB == null ? "" : sRelativePathSB.toString();
/*     */     }
/*     */     
/* 305 */     LocaleUtilDecoder decoder = null;
/*     */     try {
/* 307 */       decoder = LocaleTorrentUtil.getTorrentEncodingIfAvailable(this.torrent);
/* 308 */       if (decoder == null) {
/* 309 */         LocaleUtil localeUtil = LocaleUtil.getSingleton();
/* 310 */         decoder = localeUtil.getSystemDecoder();
/*     */       }
/*     */     }
/*     */     catch (Exception e) {}
/*     */     
/*     */ 
/* 316 */     if (decoder != null) {
/* 317 */       StringBuilder sRelativePathSB = null;
/* 318 */       byte[][] components = getPathComponents();
/* 319 */       for (int j = 0; j < components.length; j++) {
/*     */         try
/*     */         {
/*     */           try
/*     */           {
/* 324 */             comp = decoder.decodeString(components[j]);
/*     */           } catch (UnsupportedEncodingException e) {
/* 326 */             System.out.println("file - unsupported encoding!!!!");
/*     */             try {
/* 328 */               comp = new String(components[j]);
/*     */             } catch (Exception e2) {
/* 330 */               comp = "UnsupportedEncoding";
/*     */             }
/*     */           }
/*     */           
/* 334 */           String comp = FileUtil.convertOSSpecificChars(comp, j != components.length - 1);
/*     */           
/* 336 */           if (j == 0) {
/* 337 */             if (components.length == 1) {
/* 338 */               return comp;
/*     */             }
/* 340 */             sRelativePathSB = new StringBuilder(512);
/*     */           }
/*     */           else {
/* 343 */             sRelativePathSB.append(File.separator);
/*     */           }
/*     */           
/* 346 */           sRelativePathSB.append(comp);
/*     */         } catch (Exception ex) {
/* 348 */           Debug.out(ex);
/*     */         }
/*     */       }
/*     */       
/* 352 */       return sRelativePathSB == null ? "" : sRelativePathSB.toString();
/*     */     }
/* 354 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map serializeToMap()
/*     */   {
/* 364 */     Map file_map = new HashMap();
/*     */     
/* 366 */     file_map.put("length", new Long(getLength()));
/*     */     
/* 368 */     List path = new ArrayList();
/*     */     
/* 370 */     file_map.put("path", path);
/*     */     
/* 372 */     byte[][] path_comps = getPathComponentsBasic();
/*     */     
/* 374 */     if (path_comps != null) {
/* 375 */       for (int j = 0; j < path_comps.length; j++)
/*     */       {
/* 377 */         path.add(path_comps[j]);
/*     */       }
/*     */     }
/*     */     
/* 381 */     if ((path_comps != null) && (isUTF8()))
/*     */     {
/* 383 */       List utf8_path = new ArrayList();
/*     */       
/* 385 */       file_map.put("path.utf-8", utf8_path);
/*     */       
/* 387 */       for (int j = 0; j < path_comps.length; j++)
/*     */       {
/* 389 */         utf8_path.add(path_comps[j]);
/*     */       }
/*     */     }
/*     */     else {
/* 393 */       byte[][] utf8_path_comps = getPathComponentsUTF8();
/*     */       
/* 395 */       if (utf8_path_comps != null) {
/* 396 */         List utf8_path = new ArrayList();
/*     */         
/* 398 */         file_map.put("path.utf-8", utf8_path);
/*     */         
/* 400 */         for (int j = 0; j < utf8_path_comps.length; j++)
/*     */         {
/* 402 */           utf8_path.add(utf8_path_comps[j]);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 407 */     Map file_additional_properties = getAdditionalProperties();
/*     */     
/* 409 */     if (file_additional_properties != null)
/*     */     {
/* 411 */       Iterator prop_it = file_additional_properties.keySet().iterator();
/*     */       
/* 413 */       while (prop_it.hasNext())
/*     */       {
/* 415 */         String key = (String)prop_it.next();
/*     */         
/* 417 */         file_map.put(key, file_additional_properties.get(key));
/*     */       }
/*     */     }
/*     */     
/* 421 */     return file_map;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/torrent/impl/TOTorrentFileImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */