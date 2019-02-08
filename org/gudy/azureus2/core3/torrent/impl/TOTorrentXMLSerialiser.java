/*     */ package org.gudy.azureus2.core3.torrent.impl;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.xml.util.XUXmlWriter;
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
/*     */ public class TOTorrentXMLSerialiser
/*     */   extends XUXmlWriter
/*     */ {
/*     */   protected final TOTorrentImpl torrent;
/*     */   
/*     */   protected TOTorrentXMLSerialiser(TOTorrentImpl _torrent)
/*     */   {
/*  47 */     this.torrent = _torrent;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void serialiseToFile(File file)
/*     */     throws TOTorrentException
/*     */   {
/*  56 */     resetIndent();
/*     */     
/*     */     try
/*     */     {
/*  60 */       setOutputStream(new FileOutputStream(file));
/*     */       
/*  62 */       writeRoot(); return;
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*  66 */       throw new TOTorrentException("TOTorrentXMLSerialiser: file write fails: " + e.toString(), 5);
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/*  73 */         closeOutputStream();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*  77 */         throw new TOTorrentException("TOTorrentXMLSerialiser: file close fails: " + e.toString(), 5);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void writeRoot()
/*     */     throws TOTorrentException
/*     */   {
/*  88 */     writeLineRaw("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
/*  89 */     writeLineRaw("<tor:TORRENT");
/*  90 */     writeLineRaw("\txmlns:tor=\"http://azureus.sourceforge.net/files\"");
/*  91 */     writeLineRaw("\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
/*  92 */     writeLineRaw("\txsi:schemaLocation=\"http://azureus.sourceforge.net/files http://azureus.sourceforge.net/files/torrent.xsd\">");
/*     */     try
/*     */     {
/*  95 */       indent();
/*     */       
/*  97 */       writeTag("ANNOUNCE_URL", this.torrent.getAnnounceURL().toString());
/*     */       
/*  99 */       TOTorrentAnnounceURLSet[] sets = this.torrent.getAnnounceURLGroup().getAnnounceURLSets();
/*     */       
/* 101 */       if (sets.length > 0)
/*     */       {
/* 103 */         writeLineRaw("<ANNOUNCE_LIST>");
/*     */         try
/*     */         {
/* 106 */           indent();
/*     */           
/* 108 */           for (int i = 0; i < sets.length; i++)
/*     */           {
/* 110 */             TOTorrentAnnounceURLSet set = sets[i];
/*     */             
/* 112 */             URL[] urls = set.getAnnounceURLs();
/*     */             
/* 114 */             writeLineRaw("<ANNOUNCE_ENTRY>");
/*     */             try
/*     */             {
/* 117 */               indent();
/*     */               
/* 119 */               for (int j = 0; j < urls.length; j++)
/*     */               {
/* 121 */                 writeTag("ANNOUNCE_URL", urls[j].toString());
/*     */               }
/*     */             }
/*     */             finally {}
/*     */             
/*     */ 
/*     */ 
/* 128 */             writeLineRaw("</ANNOUNCE_ENTRY>");
/*     */           }
/*     */         }
/*     */         finally {}
/*     */         
/*     */ 
/* 134 */         writeLineRaw("</ANNOUNCE_LIST>");
/*     */       }
/*     */       
/* 137 */       byte[] comment = this.torrent.getComment();
/*     */       
/* 139 */       if (comment != null)
/*     */       {
/* 141 */         writeLocalisableTag("COMMENT", comment);
/*     */       }
/*     */       
/* 144 */       long creation_date = this.torrent.getCreationDate();
/*     */       
/* 146 */       if (creation_date != 0L)
/*     */       {
/* 148 */         writeTag("CREATION_DATE", creation_date);
/*     */       }
/*     */       
/* 151 */       byte[] created_by = this.torrent.getCreatedBy();
/*     */       
/* 153 */       if (created_by != null)
/*     */       {
/* 155 */         writeLocalisableTag("CREATED_BY", created_by);
/*     */       }
/*     */       
/* 158 */       writeTag("TORRENT_HASH", this.torrent.getHash());
/*     */       
/* 160 */       byte[] hash_override = this.torrent.getHashOverride();
/*     */       
/* 162 */       if (hash_override != null)
/*     */       {
/* 164 */         writeTag("TORRENT_HASH_OVERRIDE", hash_override);
/*     */       }
/*     */       
/* 167 */       writeInfo();
/*     */       
/* 169 */       Map additional_properties = this.torrent.getAdditionalProperties();
/*     */       
/* 171 */       Iterator it = additional_properties.keySet().iterator();
/*     */       
/* 173 */       while (it.hasNext())
/*     */       {
/* 175 */         String key = (String)it.next();
/*     */         
/* 177 */         writeGenericMapEntry(key, additional_properties.get(key));
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 182 */       exdent();
/*     */     }
/* 184 */     writeLineRaw("</tor:TORRENT>");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void writeInfo()
/*     */     throws TOTorrentException
/*     */   {
/* 192 */     writeLineRaw("<INFO>");
/*     */     try
/*     */     {
/* 195 */       indent();
/*     */       
/* 197 */       writeLocalisableTag("NAME", this.torrent.getName());
/*     */       
/* 199 */       writeTag("PIECE_LENGTH", this.torrent.getPieceLength());
/*     */       
/* 201 */       TOTorrentFileImpl[] files = (TOTorrentFileImpl[])this.torrent.getFiles();
/*     */       
/* 203 */       if (this.torrent.isSimpleTorrent())
/*     */       {
/* 205 */         writeTag("LENGTH", files[0].getLength());
/*     */       }
/*     */       else
/*     */       {
/* 209 */         writeLineRaw("<FILES>");
/*     */         try
/*     */         {
/* 212 */           indent();
/*     */           
/* 214 */           for (int i = 0; i < files.length; i++)
/*     */           {
/* 216 */             writeLineRaw("<FILE>");
/*     */             
/*     */             try
/*     */             {
/* 220 */               indent();
/*     */               
/* 222 */               TOTorrentFileImpl file = files[i];
/*     */               
/* 224 */               writeTag("LENGTH", file.getLength());
/*     */               
/* 226 */               writeLineRaw("<PATH>");
/*     */               
/*     */               try
/*     */               {
/* 230 */                 indent();
/*     */                 
/* 232 */                 byte[][] path_comps = file.getPathComponents();
/*     */                 
/* 234 */                 for (int j = 0; j < path_comps.length; j++)
/*     */                 {
/* 236 */                   writeLocalisableTag("COMPONENT", path_comps[j]);
/*     */                 }
/*     */               }
/*     */               finally {}
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 244 */               writeLineRaw("</PATH>");
/*     */               
/* 246 */               Map additional_properties = file.getAdditionalProperties();
/*     */               
/* 248 */               if (additional_properties != null)
/*     */               {
/* 250 */                 Iterator prop_it = additional_properties.keySet().iterator();
/*     */                 
/* 252 */                 while (prop_it.hasNext())
/*     */                 {
/* 254 */                   String key = (String)prop_it.next();
/*     */                   
/* 256 */                   writeGenericMapEntry(key, additional_properties.get(key));
/*     */                 }
/*     */               }
/*     */             }
/*     */             finally {}
/*     */             
/*     */ 
/*     */ 
/* 264 */             writeLineRaw("</FILE>");
/*     */           }
/*     */         }
/*     */         finally {}
/*     */         
/*     */ 
/*     */ 
/* 271 */         writeLineRaw("</FILES>");
/*     */       }
/*     */       
/* 274 */       writeLineRaw("<PIECES>");
/*     */       try
/*     */       {
/* 277 */         indent();
/*     */         
/* 279 */         byte[][] pieces = this.torrent.getPieces();
/*     */         
/* 281 */         for (int i = 0; i < pieces.length; i++)
/*     */         {
/* 283 */           writeGeneric(pieces[i]);
/*     */         }
/*     */       }
/*     */       finally {}
/*     */       
/*     */ 
/* 289 */       writeLineRaw("</PIECES>");
/*     */       
/* 291 */       Map additional_properties = this.torrent.getAdditionalInfoProperties();
/*     */       
/* 293 */       Iterator it = additional_properties.keySet().iterator();
/*     */       
/* 295 */       while (it.hasNext())
/*     */       {
/* 297 */         String key = (String)it.next();
/*     */         
/* 299 */         writeGenericMapEntry(key, additional_properties.get(key));
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 304 */       exdent();
/*     */     }
/*     */     
/* 307 */     writeLineRaw("</INFO>");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/torrent/impl/TOTorrentXMLSerialiser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */