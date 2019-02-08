/*      */ package org.gudy.azureus2.pluginsimpl.local.disk;
/*      */ 
/*      */ import com.aelitis.azureus.core.peermanager.piecepicker.PiecePicker;
/*      */ import com.aelitis.azureus.core.peermanager.piecepicker.PieceRTAProvider;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import java.io.IOException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.TreeSet;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerPeerListener;
/*      */ import org.gudy.azureus2.core3.peer.PEPeer;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.Average;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.plugins.disk.DiskManagerChannel;
/*      */ import org.gudy.azureus2.plugins.disk.DiskManagerEvent;
/*      */ import org.gudy.azureus2.plugins.disk.DiskManagerListener;
/*      */ import org.gudy.azureus2.plugins.disk.DiskManagerRequest;
/*      */ import org.gudy.azureus2.plugins.download.DownloadException;
/*      */ import org.gudy.azureus2.plugins.utils.PooledByteBuffer;
/*      */ import org.gudy.azureus2.pluginsimpl.local.download.DownloadImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.utils.PooledByteBufferImpl;
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
/*      */ public class DiskManagerChannelImpl
/*      */   implements DiskManagerChannel, DiskManagerFileInfoListener, DownloadManagerPeerListener, PieceRTAProvider
/*      */ {
/*      */   private static int DEFAULT_BUFFER_MILLIS;
/*      */   private static int DEFAULT_MIN_PIECES_TO_BUFFER;
/*      */   private static final boolean TRACE = false;
/*      */   private static final int COMPACT_DELAY = 32;
/*      */   private static final int MAX_READ_CHUNK_DEFAULT = 65536;
/*      */   
/*      */   static
/*      */   {
/*   62 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "filechannel.rt.buffer.millis", "filechannel.rt.buffer.pieces" }, new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String parameterName)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*   73 */         DiskManagerChannelImpl.access$002(COConfigurationManager.getIntParameter("filechannel.rt.buffer.millis"));
/*   74 */         DiskManagerChannelImpl.access$102(COConfigurationManager.getIntParameter("filechannel.rt.buffer.pieces"));
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   86 */   private static final Comparator<dataEntry> comparator = new Comparator()
/*      */   {
/*      */ 
/*      */ 
/*      */     public int compare(DiskManagerChannelImpl.dataEntry o1, DiskManagerChannelImpl.dataEntry o2)
/*      */     {
/*      */ 
/*      */ 
/*   94 */       long offset1 = o1.getOffset();
/*   95 */       long length1 = o1.getLength();
/*      */       
/*   97 */       long offset2 = o2.getOffset();
/*   98 */       long length2 = o2.getLength();
/*      */       
/*      */       long res;
/*      */       
/*      */       long res;
/*  103 */       if (offset1 == offset2)
/*      */       {
/*  105 */         res = length1 - length2;
/*      */       }
/*      */       else
/*      */       {
/*  109 */         res = offset1 - offset2;
/*      */       }
/*      */       
/*  112 */       if (res == 0L)
/*  113 */         return 0;
/*  114 */       if (res < 0L) {
/*  115 */         return -1;
/*      */       }
/*  117 */       return 1;
/*      */     }
/*      */   };
/*      */   
/*      */ 
/*      */   private static final String channel_key = "DiskManagerChannel";
/*      */   
/*      */ 
/*      */   private static int channel_id_next;
/*      */   
/*  127 */   private static CopyOnWriteList<channelCreateListener> listeners = new CopyOnWriteList();
/*      */   private DownloadImpl download;
/*      */   private DiskManagerFileInfoImpl plugin_file;
/*      */   private org.gudy.azureus2.core3.disk.DiskManagerFileInfo core_file;
/*      */   
/*      */   public static void addListener(channelCreateListener l) {
/*  133 */     listeners.add(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void removeListener(channelCreateListener l)
/*      */   {
/*  140 */     listeners.remove(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static void reportCreated(DiskManagerChannel channel)
/*      */   {
/*  147 */     Iterator<channelCreateListener> it = listeners.iterator();
/*      */     
/*  149 */     while (it.hasNext()) {
/*      */       try
/*      */       {
/*  152 */         ((channelCreateListener)it.next()).channelCreated(channel);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  156 */         Debug.printStackTrace(e);
/*      */       }
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
/*  174 */   private Set<dataEntry> data_written = new TreeSet(comparator);
/*      */   
/*  176 */   private int compact_delay = 32;
/*      */   
/*  178 */   private List<AESemaphore> waiters = new ArrayList();
/*      */   
/*      */   private long file_offset_in_torrent;
/*      */   
/*      */   private long piece_size;
/*  183 */   private Average byte_rate = Average.getInstance(1000, 20);
/*      */   
/*      */ 
/*      */   private long start_position;
/*      */   
/*      */   private long start_time;
/*      */   
/*      */   private volatile long current_position;
/*      */   
/*      */   private request current_request;
/*      */   
/*      */   private long buffer_millis_override;
/*      */   
/*      */   private long buffer_delay_millis;
/*      */   
/*      */   private PEPeerManager peer_manager;
/*      */   
/*      */   private long[] rtas;
/*      */   
/*      */   private int channel_id;
/*      */   
/*      */   private volatile boolean destroyed;
/*      */   
/*      */ 
/*      */   protected DiskManagerChannelImpl(DownloadImpl _download, DiskManagerFileInfoImpl _plugin_file)
/*      */     throws DownloadException
/*      */   {
/*  210 */     this.download = _download;
/*  211 */     this.plugin_file = _plugin_file;
/*      */     
/*  213 */     this.core_file = this.plugin_file.getCore();
/*      */     
/*  215 */     DownloadManager core_download = this.core_file.getDownloadManager();
/*      */     
/*  217 */     if (core_download.getTorrent() == null)
/*      */     {
/*  219 */       throw new DownloadException("Torrent invalid");
/*      */     }
/*      */     
/*  222 */     if (core_download.isDestroyed())
/*      */     {
/*  224 */       Debug.out("Download has been removed");
/*      */       
/*  226 */       throw new DownloadException("Download has been removed");
/*      */     }
/*      */     
/*  229 */     synchronized (DiskManagerChannelImpl.class)
/*      */     {
/*  231 */       this.channel_id = (channel_id_next++);
/*      */     }
/*      */     
/*  234 */     TOTorrentFile tf = this.core_file.getTorrentFile();
/*      */     
/*  236 */     TOTorrent torrent = tf.getTorrent();
/*      */     
/*  238 */     TOTorrentFile[] tfs = torrent.getFiles();
/*      */     
/*  240 */     this.rtas = new long[torrent.getNumberOfPieces()];
/*      */     
/*  242 */     core_download.addPeerListener(this);
/*      */     
/*  244 */     for (int i = 0; i < this.core_file.getIndex(); i++)
/*      */     {
/*  246 */       this.file_offset_in_torrent += tfs[i].getLength();
/*      */     }
/*      */     
/*  249 */     this.piece_size = tf.getTorrent().getPieceLength();
/*      */     
/*  251 */     this.core_file.addListener(this);
/*      */     
/*  253 */     reportCreated(this);
/*      */   }
/*      */   
/*      */ 
/*      */   public org.gudy.azureus2.plugins.disk.DiskManagerFileInfo getFile()
/*      */   {
/*  259 */     return this.plugin_file;
/*      */   }
/*      */   
/*      */ 
/*      */   public DiskManagerRequest createRequest()
/*      */   {
/*  265 */     if (this.core_file.getDownloaded() != this.core_file.getLength())
/*      */     {
/*  267 */       boolean is_paused = this.download.isPaused();
/*      */       
/*  269 */       if ((this.core_file.isSkipped()) && (!is_paused))
/*      */       {
/*      */ 
/*      */ 
/*  273 */         this.core_file.setSkipped(false);
/*      */       }
/*      */       
/*  276 */       boolean force_start = this.download.isForceStart();
/*      */       
/*  278 */       if (!force_start)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  284 */         if (!is_paused)
/*      */         {
/*  286 */           synchronized (this.download)
/*      */           {
/*  288 */             Map dl_state = (Map)this.download.getDownload().getData("DiskManagerChannel");
/*      */             
/*  290 */             if (dl_state == null)
/*      */             {
/*  292 */               dl_state = new HashMap();
/*      */               
/*  294 */               this.download.getDownload().setData("DiskManagerChannel", dl_state);
/*      */             }
/*      */             
/*  297 */             dl_state.put("" + this.channel_id, "");
/*      */           }
/*      */           
/*  300 */           this.download.setForceStart(true);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  305 */     this.current_request = new request();
/*      */     
/*  307 */     return this.current_request;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getPosition()
/*      */   {
/*  313 */     return this.current_position;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isDestroyed()
/*      */   {
/*  319 */     return this.destroyed;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void dataWritten(long offset, long length)
/*      */   {
/*  331 */     dataEntry entry = new dataEntry(offset, length);
/*      */     
/*  333 */     synchronized (this.data_written)
/*      */     {
/*  335 */       this.data_written.add(entry);
/*      */       
/*  337 */       this.compact_delay -= 1;
/*      */       
/*  339 */       if (this.compact_delay == 0)
/*      */       {
/*  341 */         this.compact_delay = 32;
/*      */         
/*  343 */         Iterator<dataEntry> it = this.data_written.iterator();
/*      */         
/*  345 */         dataEntry prev_e = null;
/*      */         
/*  347 */         while (it.hasNext())
/*      */         {
/*  349 */           dataEntry this_e = (dataEntry)it.next();
/*      */           
/*  351 */           if (prev_e == null)
/*      */           {
/*  353 */             prev_e = this_e;
/*      */           }
/*      */           else
/*      */           {
/*  357 */             long prev_offset = prev_e.getOffset();
/*  358 */             long prev_length = prev_e.getLength();
/*  359 */             long this_offset = this_e.getOffset();
/*  360 */             long this_length = this_e.getLength();
/*      */             
/*  362 */             if (this_offset <= prev_offset + prev_length)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  368 */               it.remove();
/*      */               
/*  370 */               prev_e.setLength(Math.max(prev_offset + prev_length, this_offset + this_length) - prev_offset);
/*      */ 
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/*      */ 
/*      */ 
/*  378 */               prev_e = this_e;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  384 */       for (int i = 0; i < this.waiters.size(); i++)
/*      */       {
/*  386 */         ((AESemaphore)this.waiters.get(i)).release();
/*      */       }
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
/*      */   public void peerManagerAdded(PEPeerManager manager)
/*      */   {
/*  409 */     this.peer_manager = manager;
/*      */     
/*  411 */     manager.getPiecePicker().addRTAProvider(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void peerManagerRemoved(PEPeerManager manager)
/*      */   {
/*  418 */     this.peer_manager = null;
/*      */     
/*  420 */     manager.getPiecePicker().removeRTAProvider(this);
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
/*      */   public long[] updateRTAs(PiecePicker picker)
/*      */   {
/*  439 */     long overall_pos = this.current_position + this.file_offset_in_torrent;
/*      */     
/*  441 */     int first_piece = (int)(overall_pos / this.piece_size);
/*      */     
/*  443 */     long rate = this.byte_rate.getAverage();
/*      */     
/*  445 */     int buffer_millis = (int)(this.buffer_millis_override == 0L ? DEFAULT_BUFFER_MILLIS : this.buffer_millis_override);
/*      */     
/*  447 */     long buffer_bytes = buffer_millis * rate / 1000L;
/*      */     
/*  449 */     int pieces_to_buffer = (int)(buffer_bytes / this.piece_size);
/*      */     
/*  451 */     if (pieces_to_buffer < 1)
/*      */     {
/*  453 */       pieces_to_buffer = 1;
/*      */     }
/*      */     
/*  456 */     int millis_per_piece = buffer_millis / pieces_to_buffer;
/*      */     
/*  458 */     if (pieces_to_buffer < DEFAULT_MIN_PIECES_TO_BUFFER)
/*      */     {
/*  460 */       pieces_to_buffer = DEFAULT_MIN_PIECES_TO_BUFFER;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  465 */     Arrays.fill(this.rtas, 0L);
/*      */     
/*  467 */     long now = SystemTime.getCurrentTime();
/*      */     
/*  469 */     now += this.buffer_delay_millis;
/*      */     
/*  471 */     for (int i = first_piece; (i < first_piece + pieces_to_buffer) && (i < this.rtas.length); i++)
/*      */     {
/*  473 */       this.rtas[i] = (now + (i - first_piece) * millis_per_piece);
/*      */     }
/*      */     
/*  476 */     return this.rtas;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getStartTime()
/*      */   {
/*  482 */     return this.start_time;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getStartPosition()
/*      */   {
/*  488 */     return this.file_offset_in_torrent + this.start_position;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getCurrentPosition()
/*      */   {
/*  494 */     return this.file_offset_in_torrent + this.current_position;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getBlockingPosition()
/*      */   {
/*  500 */     request r = this.current_request;
/*      */     
/*  502 */     if (r == null)
/*      */     {
/*  504 */       return this.file_offset_in_torrent + this.current_position;
/*      */     }
/*      */     
/*  507 */     return this.file_offset_in_torrent + this.current_position + r.getAvailableBytes();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setBufferMillis(long millis, long delay_millis)
/*      */   {
/*  515 */     this.buffer_millis_override = millis;
/*  516 */     this.buffer_delay_millis = delay_millis;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getUserAgent()
/*      */   {
/*  522 */     request r = this.current_request;
/*      */     
/*  524 */     if (r == null)
/*      */     {
/*  526 */       return null;
/*      */     }
/*      */     
/*  529 */     return r.getUserAgent();
/*      */   }
/*      */   
/*      */ 
/*      */   public void destroy()
/*      */   {
/*  535 */     this.destroyed = true;
/*      */     
/*  537 */     this.core_file.removeListener(this);
/*      */     
/*  539 */     this.core_file.getDownloadManager().removePeerListener(this);
/*      */     
/*  541 */     this.core_file.close();
/*      */     
/*  543 */     if (this.peer_manager != null)
/*      */     {
/*  545 */       this.peer_manager.getPiecePicker().removeRTAProvider(this);
/*      */     }
/*      */     
/*  548 */     boolean stop_force_start = false;
/*      */     
/*  550 */     synchronized (this.download)
/*      */     {
/*  552 */       Map dl_state = (Map)this.download.getDownload().getData("DiskManagerChannel");
/*      */       
/*  554 */       if (dl_state != null)
/*      */       {
/*  556 */         dl_state.remove("" + this.channel_id);
/*      */         
/*  558 */         if (dl_state.size() == 0)
/*      */         {
/*  560 */           stop_force_start = true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  565 */     if (stop_force_start)
/*      */     {
/*  567 */       this.download.setForceStart(false); }
/*      */   }
/*      */   
/*      */   public void dataChecked(long offset, long length) {}
/*      */   
/*      */   public void peerManagerWillBeAdded(PEPeerManager manager) {}
/*      */   
/*      */   protected class request implements DiskManagerRequest {
/*      */     private int request_type;
/*      */     private long request_offset;
/*      */     private long request_length;
/*  578 */     private List<DiskManagerListener> listeners = new ArrayList();
/*      */     
/*      */     private String user_agent;
/*      */     
/*  582 */     private int max_read_chunk = 65536;
/*      */     
/*      */     private volatile boolean cancelled;
/*      */     
/*  586 */     AESemaphore wait_sem = new AESemaphore("DiskManagerChannelImpl:wait");
/*      */     
/*      */ 
/*      */     protected request()
/*      */     {
/*  591 */       DiskManagerChannelImpl.this.start_time = SystemTime.getCurrentTime();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setType(int _type)
/*      */     {
/*  598 */       this.request_type = _type;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setOffset(long _offset)
/*      */     {
/*  605 */       this.request_offset = _offset;
/*  606 */       DiskManagerChannelImpl.this.start_position = this.request_offset;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setLength(long _length)
/*      */     {
/*  613 */       if (_length < 0L)
/*      */       {
/*  615 */         throw new RuntimeException("Illegal argument");
/*      */       }
/*      */       
/*  618 */       this.request_length = _length;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setMaximumReadChunkSize(int size)
/*      */     {
/*  625 */       this.max_read_chunk = size;
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public long getRemaining()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 283	org/gudy/azureus2/pluginsimpl/local/disk/DiskManagerChannelImpl$request:this$0	Lorg/gudy/azureus2/pluginsimpl/local/disk/DiskManagerChannelImpl;
/*      */       //   4: invokestatic 308	org/gudy/azureus2/pluginsimpl/local/disk/DiskManagerChannelImpl:access$400	(Lorg/gudy/azureus2/pluginsimpl/local/disk/DiskManagerChannelImpl;)Ljava/util/Set;
/*      */       //   7: dup
/*      */       //   8: astore_1
/*      */       //   9: monitorenter
/*      */       //   10: aload_0
/*      */       //   11: getfield 277	org/gudy/azureus2/pluginsimpl/local/disk/DiskManagerChannelImpl$request:request_length	J
/*      */       //   14: aload_0
/*      */       //   15: getfield 283	org/gudy/azureus2/pluginsimpl/local/disk/DiskManagerChannelImpl$request:this$0	Lorg/gudy/azureus2/pluginsimpl/local/disk/DiskManagerChannelImpl;
/*      */       //   18: invokestatic 303	org/gudy/azureus2/pluginsimpl/local/disk/DiskManagerChannelImpl:access$500	(Lorg/gudy/azureus2/pluginsimpl/local/disk/DiskManagerChannelImpl;)J
/*      */       //   21: aload_0
/*      */       //   22: getfield 278	org/gudy/azureus2/pluginsimpl/local/disk/DiskManagerChannelImpl$request:request_offset	J
/*      */       //   25: lsub
/*      */       //   26: lsub
/*      */       //   27: aload_1
/*      */       //   28: monitorexit
/*      */       //   29: lreturn
/*      */       //   30: astore_2
/*      */       //   31: aload_1
/*      */       //   32: monitorexit
/*      */       //   33: aload_2
/*      */       //   34: athrow
/*      */       // Line number table:
/*      */       //   Java source line #631	-> byte code offset #0
/*      */       //   Java source line #633	-> byte code offset #10
/*      */       //   Java source line #634	-> byte code offset #30
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	35	0	this	request
/*      */       //   8	24	1	Ljava/lang/Object;	Object
/*      */       //   30	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   10	29	30	finally
/*      */       //   30	33	30	finally
/*      */     }
/*      */     
/*      */     public void setUserAgent(String str)
/*      */     {
/*  641 */       this.user_agent = str;
/*      */     }
/*      */     
/*      */ 
/*      */     protected String getUserAgent()
/*      */     {
/*  647 */       return this.user_agent;
/*      */     }
/*      */     
/*      */ 
/*      */     public long getAvailableBytes()
/*      */     {
/*  653 */       if (DiskManagerChannelImpl.this.plugin_file.getDownloaded() == DiskManagerChannelImpl.this.plugin_file.getLength())
/*      */       {
/*  655 */         return getRemaining();
/*      */       }
/*      */       
/*  658 */       int download_state = DiskManagerChannelImpl.this.download.getState();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  663 */       if ((download_state != 4) && (download_state != 5))
/*      */       {
/*      */ 
/*  666 */         return -1L;
/*      */       }
/*      */       
/*  669 */       synchronized (DiskManagerChannelImpl.this.data_written)
/*      */       {
/*  671 */         Iterator<DiskManagerChannelImpl.dataEntry> it = DiskManagerChannelImpl.this.data_written.iterator();
/*      */         
/*      */ 
/*      */ 
/*  675 */         DiskManagerChannelImpl.dataEntry last_entry = null;
/*      */         
/*  677 */         while (it.hasNext())
/*      */         {
/*  679 */           DiskManagerChannelImpl.dataEntry entry = (DiskManagerChannelImpl.dataEntry)it.next();
/*      */           
/*  681 */           long entry_offset = entry.getOffset();
/*  682 */           long entry_length = entry.getLength();
/*      */           
/*  684 */           if (last_entry == null)
/*      */           {
/*  686 */             if (entry_offset > DiskManagerChannelImpl.this.current_position) {
/*      */               break;
/*      */             }
/*      */             
/*      */ 
/*  691 */             if ((entry_offset <= DiskManagerChannelImpl.this.current_position) && (DiskManagerChannelImpl.this.current_position < entry_offset + entry_length))
/*      */             {
/*  693 */               last_entry = entry;
/*      */             }
/*      */           }
/*      */           else {
/*  697 */             if (last_entry.getOffset() + last_entry.getLength() != entry.getOffset())
/*      */               break;
/*  699 */             last_entry = entry;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  708 */         if (last_entry == null)
/*      */         {
/*  710 */           return 0L;
/*      */         }
/*      */         
/*      */ 
/*  714 */         return last_entry.getOffset() + last_entry.getLength() - DiskManagerChannelImpl.this.current_position;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void run()
/*      */     {
/*  722 */       long rem = this.request_length;
/*      */       
/*  724 */       long pos = this.request_offset;
/*      */       
/*  726 */       long download_not_running_time = 0L;
/*      */       
/*      */       try
/*      */       {
/*  730 */         while ((rem > 0L) && (!this.cancelled))
/*      */         {
/*  732 */           long len = 0L;
/*      */           
/*  734 */           synchronized (DiskManagerChannelImpl.this.data_written)
/*      */           {
/*  736 */             DiskManagerChannelImpl.this.current_position = pos;
/*      */             
/*  738 */             Iterator<DiskManagerChannelImpl.dataEntry> it = DiskManagerChannelImpl.this.data_written.iterator();
/*      */             
/*  740 */             while (it.hasNext())
/*      */             {
/*  742 */               DiskManagerChannelImpl.dataEntry entry = (DiskManagerChannelImpl.dataEntry)it.next();
/*      */               
/*  744 */               long entry_offset = entry.getOffset();
/*      */               
/*  746 */               if (entry_offset > pos) {
/*      */                 break;
/*      */               }
/*      */               
/*      */ 
/*  751 */               long entry_length = entry.getLength();
/*      */               
/*  753 */               long available = entry_offset + entry_length - pos;
/*      */               
/*  755 */               if (available > 0L)
/*      */               {
/*  757 */                 len = available;
/*      */                 
/*  759 */                 break;
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*  764 */           if (len > 0L)
/*      */           {
/*  766 */             if (len > rem)
/*      */             {
/*  768 */               len = rem;
/*      */             }
/*      */             
/*  771 */             if (len > this.max_read_chunk)
/*      */             {
/*  773 */               len = this.max_read_chunk;
/*      */             }
/*      */             
/*  776 */             DirectByteBuffer buffer = DiskManagerChannelImpl.this.core_file.read(pos, (int)len);
/*      */             
/*      */ 
/*      */ 
/*  780 */             int read = buffer.position((byte)1);
/*      */             
/*  782 */             if (read != len)
/*      */             {
/*  784 */               throw new IOException("EOF: insufficient bytes read (expected=" + len + ", actual=" + read + ")");
/*      */             }
/*      */             
/*  787 */             inform(new event(new PooledByteBufferImpl(buffer), pos, (int)len));
/*      */             
/*  789 */             pos += len;
/*      */             
/*  791 */             rem -= len;
/*      */             
/*  793 */             synchronized (DiskManagerChannelImpl.this.data_written)
/*      */             {
/*  795 */               DiskManagerChannelImpl.this.byte_rate.addValue(len);
/*      */               
/*  797 */               DiskManagerChannelImpl.this.current_position = pos;
/*      */             }
/*      */           }
/*      */           else {
/*  801 */             inform(new event(pos));
/*      */             
/*  803 */             synchronized (DiskManagerChannelImpl.this.data_written)
/*      */             {
/*  805 */               DiskManagerChannelImpl.this.waiters.add(this.wait_sem);
/*      */             }
/*      */             
/*      */             try
/*      */             {
/*  810 */               while (!this.cancelled)
/*      */               {
/*  812 */                 if (this.wait_sem.reserve(500L)) {
/*      */                   break;
/*      */                 }
/*      */                 
/*      */ 
/*  817 */                 DownloadManager dm = DiskManagerChannelImpl.this.core_file.getDownloadManager();
/*      */                 
/*  819 */                 if (dm.isDestroyed())
/*      */                 {
/*  821 */                   throw new Exception("Download has been removed");
/*      */                 }
/*  823 */                 if (DiskManagerChannelImpl.this.core_file.isSkipped())
/*      */                 {
/*  825 */                   throw new Exception("File is 'do not download'");
/*      */                 }
/*      */                 
/*      */ 
/*  829 */                 int state = dm.getState();
/*      */                 
/*  831 */                 if ((state == 100) || (state == 70))
/*      */                 {
/*  833 */                   long now = SystemTime.getMonotonousTime();
/*      */                   
/*  835 */                   if (download_not_running_time == 0L)
/*      */                   {
/*  837 */                     download_not_running_time = now;
/*      */                   }
/*  839 */                   else if (now - download_not_running_time > 15000L)
/*      */                   {
/*  841 */                     if (dm.isPaused())
/*      */                     {
/*  843 */                       throw new Exception("Download has been paused");
/*      */                     }
/*      */                     
/*      */ 
/*  847 */                     throw new Exception("Download has been stopped");
/*      */                   }
/*      */                 }
/*      */                 else
/*      */                 {
/*  852 */                   download_not_running_time = 0L;
/*      */                 }
/*      */               }
/*      */             }
/*      */             finally
/*      */             {
/*  858 */               synchronized (DiskManagerChannelImpl.this.data_written)
/*      */               {
/*  860 */                 DiskManagerChannelImpl.this.waiters.remove(this.wait_sem);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/*  867 */         inform(e);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public void cancel()
/*      */     {
/*  874 */       this.cancelled = true;
/*      */       
/*  876 */       inform(new Throwable("Request cancelled"));
/*      */       
/*  878 */       this.wait_sem.release();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void inform(Throwable e)
/*      */     {
/*  885 */       inform(new event(e));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void inform(event ev)
/*      */     {
/*  892 */       for (int i = 0; i < this.listeners.size(); i++) {
/*      */         try
/*      */         {
/*  895 */           ((DiskManagerListener)this.listeners.get(i)).eventOccurred(ev);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  899 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void addListener(DiskManagerListener listener)
/*      */     {
/*  908 */       this.listeners.add(listener);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void removeListener(DiskManagerListener listener)
/*      */     {
/*  915 */       this.listeners.remove(listener);
/*      */     }
/*      */     
/*      */ 
/*      */     protected class event
/*      */       implements DiskManagerEvent
/*      */     {
/*      */       private int event_type;
/*      */       
/*      */       private Throwable error;
/*      */       
/*      */       private PooledByteBuffer buffer;
/*      */       private long event_offset;
/*      */       private int event_length;
/*      */       
/*      */       protected event(Throwable _error)
/*      */       {
/*  932 */         this.event_type = 2;
/*  933 */         this.error = _error;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       protected event(long _offset)
/*      */       {
/*  940 */         this.event_type = 3;
/*      */         
/*  942 */         this.event_offset = _offset;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       protected event(PooledByteBuffer _buffer, long _offset, int _length)
/*      */       {
/*  951 */         this.event_type = 1;
/*  952 */         this.buffer = _buffer;
/*  953 */         this.event_offset = _offset;
/*  954 */         this.event_length = _length;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getType()
/*      */       {
/*  960 */         return this.event_type;
/*      */       }
/*      */       
/*      */ 
/*      */       public DiskManagerRequest getRequest()
/*      */       {
/*  966 */         return DiskManagerChannelImpl.request.this;
/*      */       }
/*      */       
/*      */ 
/*      */       public long getOffset()
/*      */       {
/*  972 */         return this.event_offset;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getLength()
/*      */       {
/*  978 */         return this.event_length;
/*      */       }
/*      */       
/*      */ 
/*      */       public PooledByteBuffer getBuffer()
/*      */       {
/*  984 */         return this.buffer;
/*      */       }
/*      */       
/*      */ 
/*      */       public Throwable getFailure()
/*      */       {
/*  990 */         return this.error;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void peerAdded(PEPeer peer) {}
/*      */   
/*      */   public void peerRemoved(PEPeer peer) {}
/*      */   
/*      */   protected static class dataEntry
/*      */   {
/*      */     private long offset;
/*      */     private long length;
/*      */     
/*      */     protected dataEntry(long _offset, long _length)
/*      */     {
/* 1006 */       this.offset = _offset;
/* 1007 */       this.length = _length;
/*      */     }
/*      */     
/*      */ 
/*      */     protected long getOffset()
/*      */     {
/* 1013 */       return this.offset;
/*      */     }
/*      */     
/*      */ 
/*      */     protected long getLength()
/*      */     {
/* 1019 */       return this.length;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setLength(long _length)
/*      */     {
/* 1026 */       this.length = _length;
/*      */     }
/*      */     
/*      */ 
/*      */     protected String getString()
/*      */     {
/* 1032 */       return "offset=" + this.offset + ",length=" + this.length;
/*      */     }
/*      */   }
/*      */   
/*      */   public static abstract interface channelCreateListener
/*      */   {
/*      */     public abstract void channelCreated(DiskManagerChannel paramDiskManagerChannel);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/disk/DiskManagerChannelImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */