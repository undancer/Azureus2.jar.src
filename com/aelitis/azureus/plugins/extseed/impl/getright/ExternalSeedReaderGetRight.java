/*     */ package com.aelitis.azureus.plugins.extseed.impl.getright;
/*     */ 
/*     */ import com.aelitis.azureus.core.peermanager.piecepicker.PiecePicker;
/*     */ import com.aelitis.azureus.core.peermanager.piecepicker.PiecePriorityProvider;
/*     */ import com.aelitis.azureus.plugins.extseed.ExternalSeedException;
/*     */ import com.aelitis.azureus.plugins.extseed.ExternalSeedPlugin;
/*     */ import com.aelitis.azureus.plugins.extseed.ExternalSeedReader;
/*     */ import com.aelitis.azureus.plugins.extseed.impl.ExternalSeedReaderImpl;
/*     */ import com.aelitis.azureus.plugins.extseed.impl.ExternalSeedReaderRequest;
/*     */ import com.aelitis.azureus.plugins.extseed.util.ExternalSeedHTTPDownloader;
/*     */ import com.aelitis.azureus.plugins.extseed.util.ExternalSeedHTTPDownloaderLinear;
/*     */ import com.aelitis.azureus.plugins.extseed.util.ExternalSeedHTTPDownloaderListener;
/*     */ import com.aelitis.azureus.plugins.extseed.util.ExternalSeedHTTPDownloaderRange;
/*     */ import java.net.URL;
/*     */ import java.net.URLEncoder;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.peers.PeerManager;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*     */ import org.gudy.azureus2.pluginsimpl.local.torrent.TorrentImpl;
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
/*     */ public class ExternalSeedReaderGetRight
/*     */   extends ExternalSeedReaderImpl
/*     */   implements PiecePriorityProvider
/*     */ {
/*     */   private static final int TARGET_REQUEST_SIZE_DEFAULT = 262144;
/*     */   private URL url;
/*     */   private int port;
/*     */   private ExternalSeedHTTPDownloader[] http_downloaders;
/*     */   private long[] downloader_offsets;
/*     */   private long[] downloader_lengths;
/*     */   private int piece_size;
/*     */   private int piece_group_size;
/*     */   private long[] piece_priorities;
/*     */   private boolean linear_download;
/*     */   
/*     */   protected ExternalSeedReaderGetRight(ExternalSeedPlugin _plugin, Torrent _torrent, URL _url, Map _params)
/*     */     throws Exception
/*     */   {
/*  76 */     super(_plugin, _torrent, _url.getHost(), _params);
/*     */     
/*  78 */     int target_request_size = getIntParam(_params, "req_size", 262144);
/*     */     
/*  80 */     this.linear_download = getBooleanParam(_params, "linear", false);
/*     */     
/*  82 */     this.url = _url;
/*     */     
/*  84 */     this.port = this.url.getPort();
/*     */     
/*  86 */     if (this.port == -1)
/*     */     {
/*  88 */       this.port = this.url.getDefaultPort();
/*     */     }
/*     */     
/*  91 */     this.piece_size = ((int)getTorrent().getPieceSize());
/*     */     
/*  93 */     this.piece_group_size = (target_request_size / this.piece_size);
/*     */     
/*  95 */     if (this.piece_group_size == 0)
/*     */     {
/*  97 */       this.piece_group_size = 1;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setupDownloaders()
/*     */   {
/* 106 */     synchronized (this)
/*     */     {
/* 108 */       if (this.http_downloaders != null)
/*     */       {
/* 110 */         return;
/*     */       }
/*     */       
/* 113 */       TOTorrent to_torrent = ((TorrentImpl)getTorrent()).getTorrent();
/*     */       
/* 115 */       String ua = getUserAgent();
/*     */       
/* 117 */       if (to_torrent.isSimpleTorrent())
/*     */       {
/* 119 */         this.http_downloaders = new ExternalSeedHTTPDownloader[] { this.linear_download ? new ExternalSeedHTTPDownloaderLinear(this.url, ua) : new ExternalSeedHTTPDownloaderRange(this.url, ua) };
/*     */         
/*     */ 
/*     */ 
/* 123 */         this.downloader_offsets = new long[] { 0L };
/* 124 */         this.downloader_lengths = new long[] { to_torrent.getSize() };
/*     */       }
/*     */       else
/*     */       {
/* 128 */         TOTorrentFile[] files = to_torrent.getFiles();
/*     */         
/* 130 */         this.http_downloaders = new ExternalSeedHTTPDownloader[files.length];
/*     */         
/* 132 */         this.downloader_offsets = new long[files.length];
/* 133 */         this.downloader_lengths = new long[files.length];
/*     */         
/* 135 */         long offset = 0L;
/*     */         
/*     */ 
/*     */ 
/* 139 */         String base_url = this.url.toString();
/*     */         
/* 141 */         if (base_url.endsWith("/"))
/*     */         {
/* 143 */           base_url = base_url.substring(0, base_url.length() - 1);
/*     */         }
/*     */         try
/*     */         {
/* 147 */           base_url = base_url + "/" + URLEncoder.encode(new String(to_torrent.getName(), "ISO-8859-1"), "ISO-8859-1").replaceAll("\\+", "%20");
/*     */           
/* 149 */           for (int i = 0; i < files.length; i++)
/*     */           {
/* 151 */             TOTorrentFile file = files[i];
/*     */             
/* 153 */             long length = file.getLength();
/*     */             
/* 155 */             String file_url_str = base_url;
/*     */             
/* 157 */             byte[][] bits = file.getPathComponents();
/*     */             
/* 159 */             for (int j = 0; j < bits.length; j++)
/*     */             {
/* 161 */               file_url_str = file_url_str + "/" + URLEncoder.encode(new String(bits[j], "ISO-8859-1"), "ISO-8859-1").replaceAll("\\+", "%20");
/*     */             }
/*     */             
/* 164 */             this.http_downloaders[i] = (this.linear_download ? new ExternalSeedHTTPDownloaderLinear(new URL(file_url_str), ua) : new ExternalSeedHTTPDownloaderRange(new URL(file_url_str), ua));
/*     */             
/* 166 */             this.downloader_offsets[i] = offset;
/* 167 */             this.downloader_lengths[i] = length;
/*     */             
/* 169 */             offset += length;
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {
/* 173 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean sameAs(ExternalSeedReader other)
/*     */   {
/* 183 */     if ((other instanceof ExternalSeedReaderGetRight))
/*     */     {
/* 185 */       return this.url.toString().equals(((ExternalSeedReaderGetRight)other).url.toString());
/*     */     }
/*     */     
/* 188 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 194 */     return "HTTP Seed: " + this.url;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getType()
/*     */   {
/* 200 */     return "HTTP Seed";
/*     */   }
/*     */   
/*     */ 
/*     */   public URL getURL()
/*     */   {
/* 206 */     return this.url;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 212 */     return this.port;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getPieceGroupSize()
/*     */   {
/* 218 */     return this.piece_group_size;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean getRequestCanSpanPieces()
/*     */   {
/* 224 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setActiveSupport(PeerManager peer_manager, boolean active)
/*     */   {
/* 232 */     if (this.linear_download)
/*     */     {
/*     */ 
/*     */ 
/* 236 */       if (peer_manager != null)
/*     */       {
/* 238 */         PiecePicker picker = PluginCoreUtils.unwrap(peer_manager).getPiecePicker();
/*     */         
/* 240 */         if (active)
/*     */         {
/* 242 */           this.piece_priorities = new long[peer_manager.getPieces().length];
/*     */           
/* 244 */           for (int i = 0; i < this.piece_priorities.length; i++)
/*     */           {
/* 246 */             this.piece_priorities[i] = (10000 + i);
/*     */           }
/*     */           
/* 249 */           picker.addPriorityProvider(this);
/*     */         }
/*     */         else
/*     */         {
/* 253 */           this.piece_priorities = null;
/*     */           
/* 255 */           picker.removePriorityProvider(this);
/*     */         }
/*     */       }
/*     */       
/* 259 */       if (!active)
/*     */       {
/*     */         boolean downloaders_set;
/*     */         
/* 263 */         synchronized (this)
/*     */         {
/* 265 */           downloaders_set = this.http_downloaders != null;
/*     */         }
/*     */         
/* 268 */         if (downloaders_set)
/*     */         {
/* 270 */           for (ExternalSeedHTTPDownloader d : this.http_downloaders)
/*     */           {
/* 272 */             d.deactivate();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long[] updatePriorities(PiecePicker picker)
/*     */   {
/* 283 */     return this.piece_priorities;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void calculatePriorityOffsets(PeerManager peer_manager, int[] base_priorities)
/*     */   {
/* 291 */     if (this.linear_download)
/*     */     {
/*     */ 
/*     */ 
/* 295 */       for (int i = 0; i < base_priorities.length; i++)
/*     */       {
/* 297 */         base_priorities[i] = (100000 + base_priorities.length - (i + 1));
/*     */       }
/*     */       
/*     */     } else {
/* 301 */       super.calculatePriorityOffsets(peer_manager, base_priorities);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void readData(ExternalSeedReaderRequest request)
/*     */     throws ExternalSeedException
/*     */   {
/* 311 */     readData(request.getStartPieceNumber(), request.getStartPieceOffset(), request.getLength(), request);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void readData(int start_piece_number, int start_piece_offset, int length, final ExternalSeedHTTPDownloaderListener listener)
/*     */     throws ExternalSeedException
/*     */   {
/* 323 */     setupDownloaders();
/*     */     
/* 325 */     setReconnectDelay(30000, false);
/*     */     
/* 327 */     long request_start = start_piece_number * this.piece_size + start_piece_offset;
/* 328 */     int request_length = length;
/*     */     
/* 330 */     if (this.http_downloaders.length == 1)
/*     */     {
/* 332 */       ExternalSeedHTTPDownloader http_downloader = this.http_downloaders[0];
/*     */       try
/*     */       {
/* 335 */         http_downloader.downloadRange(request_start, request_length, listener, isTransient());
/*     */ 
/*     */ 
/*     */       }
/*     */       catch (ExternalSeedException ese)
/*     */       {
/*     */ 
/*     */ 
/* 343 */         if ((http_downloader.getLastResponse() == 503) && (http_downloader.getLast503RetrySecs() >= 0))
/*     */         {
/* 345 */           int retry_secs = http_downloader.getLast503RetrySecs();
/*     */           
/* 347 */           setReconnectDelay(retry_secs * 1000, true);
/*     */           
/* 349 */           throw new ExternalSeedException("Server temporarily unavailable, retrying in " + retry_secs + " seconds");
/*     */         }
/*     */         
/*     */ 
/* 353 */         throw ese;
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 358 */       long request_end = request_start + request_length;
/*     */       
/*     */ 
/*     */ 
/* 362 */       final byte[][] overlap_buffer = { null };
/* 363 */       final int[] overlap_buffer_position = { 0 };
/*     */       
/*     */ 
/*     */ 
/* 367 */       for (int i = 0; i < this.http_downloaders.length; i++)
/*     */       {
/* 369 */         long this_start = this.downloader_offsets[i];
/* 370 */         long this_end = this_start + this.downloader_lengths[i];
/*     */         
/* 372 */         if (this_end > request_start)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 377 */           if (this_start >= request_end) {
/*     */             break;
/*     */           }
/*     */           
/*     */ 
/* 382 */           long sub_request_start = Math.max(request_start, this_start);
/* 383 */           long sub_request_end = Math.min(request_end, this_end);
/*     */           
/* 385 */           final int sub_len = (int)(sub_request_end - sub_request_start);
/*     */           
/* 387 */           if (sub_len != 0)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 392 */             ExternalSeedHTTPDownloader http_downloader = this.http_downloaders[i];
/*     */             
/*     */ 
/*     */ 
/* 396 */             ExternalSeedHTTPDownloaderListener sub_request = new ExternalSeedHTTPDownloaderListener()
/*     */             {
/*     */               private int bytes_read;
/*     */               
/*     */ 
/* 401 */               private byte[] current_buffer = overlap_buffer[0];
/* 402 */               private int current_buffer_position = overlap_buffer_position[0];
/* 403 */               private int current_buffer_length = this.current_buffer == null ? -1 : Math.min(this.current_buffer.length, this.current_buffer_position + sub_len);
/*     */               
/*     */ 
/*     */ 
/*     */               public byte[] getBuffer()
/*     */                 throws ExternalSeedException
/*     */               {
/* 410 */                 if (this.current_buffer == null)
/*     */                 {
/* 412 */                   this.current_buffer = listener.getBuffer();
/* 413 */                   this.current_buffer_position = 0;
/* 414 */                   this.current_buffer_length = Math.min(this.current_buffer.length, sub_len - this.bytes_read);
/*     */                 }
/*     */                 
/* 417 */                 return this.current_buffer;
/*     */               }
/*     */               
/*     */ 
/*     */ 
/*     */               public void setBufferPosition(int position)
/*     */               {
/* 424 */                 this.current_buffer_position = position;
/*     */                 
/* 426 */                 listener.setBufferPosition(position);
/*     */               }
/*     */               
/*     */ 
/*     */               public int getBufferPosition()
/*     */               {
/* 432 */                 return this.current_buffer_position;
/*     */               }
/*     */               
/*     */ 
/*     */               public int getBufferLength()
/*     */               {
/* 438 */                 return this.current_buffer_length;
/*     */               }
/*     */               
/*     */ 
/*     */ 
/*     */               public int getPermittedBytes()
/*     */                 throws ExternalSeedException
/*     */               {
/* 446 */                 return listener.getPermittedBytes();
/*     */               }
/*     */               
/*     */ 
/*     */               public int getPermittedTime()
/*     */               {
/* 452 */                 return listener.getPermittedTime();
/*     */               }
/*     */               
/*     */ 
/*     */ 
/*     */               public void reportBytesRead(int num)
/*     */               {
/* 459 */                 this.bytes_read += num;
/*     */                 
/* 461 */                 listener.reportBytesRead(num);
/*     */               }
/*     */               
/*     */ 
/*     */               public boolean isCancelled()
/*     */               {
/* 467 */                 return listener.isCancelled();
/*     */               }
/*     */               
/*     */ 
/*     */ 
/*     */ 
/*     */               public void done()
/*     */               {
/* 475 */                 int rem = this.current_buffer.length - this.current_buffer_length;
/*     */                 
/* 477 */                 if (this.bytes_read == sub_len)
/*     */                 {
/*     */ 
/*     */ 
/*     */ 
/* 482 */                   if (rem == 0)
/*     */                   {
/* 484 */                     overlap_buffer[0] = null;
/* 485 */                     overlap_buffer_position[0] = 0;
/*     */                   }
/*     */                   else
/*     */                   {
/* 489 */                     overlap_buffer[0] = this.current_buffer;
/* 490 */                     overlap_buffer_position[0] = this.current_buffer_length;
/*     */                   }
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/* 496 */                 this.current_buffer = null;
/*     */                 
/* 498 */                 if (rem == 0)
/*     */                 {
/* 500 */                   listener.done();
/*     */                 }
/*     */               }
/*     */             };
/*     */             try
/*     */             {
/* 506 */               http_downloader.downloadRange(sub_request_start - this_start, sub_len, sub_request, isTransient());
/*     */ 
/*     */ 
/*     */             }
/*     */             catch (ExternalSeedException ese)
/*     */             {
/*     */ 
/*     */ 
/* 514 */               if ((http_downloader.getLastResponse() == 503) && (http_downloader.getLast503RetrySecs() >= 0))
/*     */               {
/* 516 */                 int retry_secs = http_downloader.getLast503RetrySecs();
/*     */                 
/* 518 */                 setReconnectDelay(retry_secs * 1000, true);
/*     */                 
/* 520 */                 throw new ExternalSeedException("Server temporarily unavailable, retrying in " + retry_secs + " seconds");
/*     */               }
/*     */               
/*     */ 
/* 524 */               throw ese;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/extseed/impl/getright/ExternalSeedReaderGetRight.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */