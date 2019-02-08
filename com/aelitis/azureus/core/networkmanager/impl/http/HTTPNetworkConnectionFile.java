/*     */ package com.aelitis.azureus.core.networkmanager.impl.http;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnection;
/*     */ import com.aelitis.azureus.core.util.HTTPUtils;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.URLDecoder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ import org.gudy.azureus2.core3.disk.DiskManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.peer.impl.PEPeerControl;
/*     */ import org.gudy.azureus2.core3.peer.impl.PEPeerTransport;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class HTTPNetworkConnectionFile
/*     */   extends HTTPNetworkConnection
/*     */ {
/*     */   private boolean switching;
/*     */   
/*     */   protected HTTPNetworkConnectionFile(HTTPNetworkManager _manager, NetworkConnection _connection, PEPeerTransport _peer)
/*     */   {
/*  54 */     super(_manager, _connection, _peer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void decodeHeader(HTTPMessageDecoder decoder, final String header)
/*     */     throws IOException
/*     */   {
/*  64 */     if (this.switching)
/*     */     {
/*  66 */       Debug.out("new header received while paused");
/*     */       
/*  68 */       throw new IOException("Bork");
/*     */     }
/*     */     
/*  71 */     if (!isSeed())
/*     */     {
/*  73 */       return;
/*     */     }
/*     */     
/*  76 */     PEPeerControl control = getPeerControl();
/*     */     
/*  78 */     DiskManager dm = control.getDiskManager();
/*     */     
/*  80 */     if (dm == null)
/*     */     {
/*  82 */       Debug.out("Disk manager is null");
/*     */       
/*  84 */       throw new IOException("Disk manager unavailable");
/*     */     }
/*     */     
/*  87 */     TOTorrent to_torrent = dm.getTorrent();
/*     */     
/*  89 */     char[] chars = header.toCharArray();
/*     */     
/*  91 */     int last_pos = 0;
/*  92 */     int line_num = 0;
/*     */     
/*  94 */     String target_str = null;
/*     */     
/*  96 */     DiskManagerFileInfo target_file = null;
/*     */     
/*  98 */     long file_offset = 0L;
/*     */     
/* 100 */     List<long[]> ranges = new ArrayList();
/*     */     
/* 102 */     boolean keep_alive = false;
/*     */     
/* 104 */     for (int i = 1; i < chars.length; i++)
/*     */     {
/* 106 */       if ((chars[(i - 1)] == '\r') && (chars[i] == '\n'))
/*     */       {
/* 108 */         String line = new String(chars, last_pos, i - last_pos).trim();
/*     */         
/* 110 */         last_pos = i;
/*     */         
/* 112 */         line_num++;
/*     */         
/*     */ 
/*     */ 
/* 116 */         if (line_num == 1)
/*     */         {
/* 118 */           line = line.substring(line.indexOf("files/") + 6);
/*     */           
/* 120 */           int hash_end = line.indexOf("/");
/*     */           
/* 122 */           final byte[] old_hash = control.getHash();
/*     */           
/* 124 */           final byte[] new_hash = URLDecoder.decode(line.substring(0, hash_end), "ISO-8859-1").getBytes("ISO-8859-1");
/*     */           
/* 126 */           if (!Arrays.equals(new_hash, old_hash))
/*     */           {
/* 128 */             this.switching = true;
/*     */             
/* 130 */             decoder.pauseInternally();
/*     */             
/* 132 */             flushRequests(new HTTPNetworkConnection.flushListener()
/*     */             {
/*     */               private boolean triggered;
/*     */               
/*     */ 
/*     */ 
/*     */               public void flushed()
/*     */               {
/* 140 */                 synchronized (this)
/*     */                 {
/* 142 */                   if (this.triggered)
/*     */                   {
/* 144 */                     return;
/*     */                   }
/*     */                   
/* 147 */                   this.triggered = true;
/*     */                 }
/*     */                 
/* 150 */                 HTTPNetworkConnectionFile.this.getManager().reRoute(HTTPNetworkConnectionFile.this, old_hash, new_hash, header);
/*     */ 
/*     */               }
/*     */               
/*     */ 
/* 155 */             });
/* 156 */             return;
/*     */           }
/*     */           
/*     */ 
/* 160 */           line = line.substring(hash_end + 1);
/*     */           
/* 162 */           line = line.substring(0, line.lastIndexOf(' '));
/*     */           
/* 164 */           String file = line;
/*     */           
/* 166 */           if (to_torrent.isSimpleTorrent())
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 171 */             target_file = dm.getFiles()[0];
/*     */           }
/*     */           else
/*     */           {
/* 175 */             target_str = file;
/*     */             
/* 177 */             StringTokenizer tok = new StringTokenizer(file, "/");
/*     */             
/* 179 */             List<byte[]> bits = new ArrayList();
/*     */             
/* 181 */             while (tok.hasMoreTokens())
/*     */             {
/* 183 */               bits.add(URLDecoder.decode(tok.nextToken(), "ISO-8859-1").getBytes("ISO-8859-1"));
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 189 */             if ((!to_torrent.isSimpleTorrent()) && (bits.size() > 1))
/*     */             {
/* 191 */               if (Arrays.equals(to_torrent.getName(), (byte[])bits.get(0)))
/*     */               {
/* 193 */                 bits.remove(0);
/*     */               }
/*     */             }
/*     */             
/* 197 */             DiskManagerFileInfo[] files = dm.getFiles();
/*     */             
/* 199 */             file_offset = 0L;
/*     */             
/* 201 */             for (int j = 0; j < files.length; j++)
/*     */             {
/* 203 */               TOTorrentFile torrent_file = files[j].getTorrentFile();
/*     */               
/* 205 */               byte[][] comps = torrent_file.getPathComponents();
/*     */               
/* 207 */               if (comps.length == bits.size())
/*     */               {
/* 209 */                 boolean match = true;
/*     */                 
/* 211 */                 for (int k = 0; k < comps.length; k++)
/*     */                 {
/* 213 */                   if (!Arrays.equals(comps[k], (byte[])bits.get(k)))
/*     */                   {
/* 215 */                     match = false;
/*     */                     
/* 217 */                     break;
/*     */                   }
/*     */                 }
/*     */                 
/* 221 */                 if (match)
/*     */                 {
/* 223 */                   target_file = files[j];
/*     */                   
/* 225 */                   break;
/*     */                 }
/*     */               }
/*     */               
/* 229 */               file_offset += torrent_file.getLength();
/*     */             }
/*     */           }
/*     */         }
/*     */         else {
/* 234 */           line = line.toLowerCase(MessageText.LOCALE_ENGLISH);
/*     */           
/* 236 */           if ((line.startsWith("range")) && (target_file != null))
/*     */           {
/* 238 */             line = line.substring(5).trim();
/*     */             
/* 240 */             if (line.startsWith(":"))
/*     */             {
/* 242 */               String range_str = line.substring(1).trim();
/*     */               
/* 244 */               if (range_str.startsWith("bytes="))
/*     */               {
/* 246 */                 long file_length = target_file.getLength();
/*     */                 
/* 248 */                 StringTokenizer tok2 = new StringTokenizer(range_str.substring(6), ",");
/*     */                 
/* 250 */                 while (tok2.hasMoreTokens())
/*     */                 {
/* 252 */                   String range = tok2.nextToken();
/*     */                   try
/*     */                   {
/* 255 */                     int pos = range.indexOf('-');
/*     */                     
/* 257 */                     if (pos != -1)
/*     */                     {
/* 259 */                       String lhs = range.substring(0, pos);
/* 260 */                       String rhs = range.substring(pos + 1);
/*     */                       
/*     */                       long start;
/*     */                       long start;
/*     */                       long end;
/* 265 */                       if (lhs.length() == 0)
/*     */                       {
/*     */ 
/*     */ 
/* 269 */                         long end = file_length - 1L;
/* 270 */                         start = file_length - Long.parseLong(rhs);
/*     */                       } else { long start;
/* 272 */                         if (rhs.length() == 0)
/*     */                         {
/* 274 */                           long end = file_length - 1L;
/* 275 */                           start = Long.parseLong(lhs);
/*     */                         }
/*     */                         else
/*     */                         {
/* 279 */                           start = Long.parseLong(lhs);
/* 280 */                           end = Long.parseLong(rhs);
/*     */                         }
/*     */                       }
/* 283 */                       ranges.add(new long[] { start, end });
/*     */                     }
/*     */                   }
/*     */                   catch (Throwable e) {}
/*     */                 }
/*     */               }
/*     */               
/* 290 */               if (ranges.size() == 0)
/*     */               {
/* 292 */                 log("Invalid range specification: '" + line + "'");
/*     */                 
/* 294 */                 sendAndClose(getManager().getRangeNotSatisfiable());
/*     */                 
/* 296 */                 return;
/*     */               }
/*     */             }
/* 299 */           } else if (line.contains("keep-alive"))
/*     */           {
/* 301 */             keep_alive = true;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 307 */     if (target_file == null)
/*     */     {
/* 309 */       log("Failed to find file '" + target_str + "'");
/*     */       
/* 311 */       sendAndClose(getManager().getNotFound());
/*     */       
/* 313 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 317 */       String name = target_file.getFile(true).getName();
/*     */       
/* 319 */       int pos = name.lastIndexOf(".");
/*     */       
/* 321 */       if (pos != -1)
/*     */       {
/* 323 */         setContentType(HTTPUtils.guessContentTypeFromFileType(name.substring(pos + 1)));
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/* 328 */     long file_length = target_file.getLength();
/*     */     
/* 330 */     boolean partial_content = ranges.size() > 0;
/*     */     
/* 332 */     if (!partial_content)
/*     */     {
/* 334 */       ranges.add(new long[] { 0L, file_length - 1L });
/*     */     }
/*     */     
/* 337 */     long[] offsets = new long[ranges.size()];
/* 338 */     long[] lengths = new long[ranges.size()];
/*     */     
/* 340 */     for (int i = 0; i < ranges.size(); i++)
/*     */     {
/* 342 */       long[] range = (long[])ranges.get(i);
/*     */       
/* 344 */       long start = range[0];
/* 345 */       long end = range[1];
/*     */       
/* 347 */       if ((start < 0L) || (start >= file_length) || (end < 0L) || (end >= file_length) || (start > end))
/*     */       {
/*     */ 
/*     */ 
/* 351 */         log("Invalid range specification: '" + start + "-" + end + "'");
/*     */         
/* 353 */         sendAndClose(getManager().getRangeNotSatisfiable());
/*     */         
/* 355 */         return;
/*     */       }
/*     */       
/* 358 */       offsets[i] = (file_offset + start);
/* 359 */       lengths[i] = (end - start + 1L);
/*     */     }
/*     */     
/* 362 */     addRequest(new HTTPNetworkConnection.httpRequest(offsets, lengths, file_length, partial_content, keep_alive));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkConnectionFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */