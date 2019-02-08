/*      */ package com.aelitis.azureus.core.devices.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.devices.TranscodeActionVetoException;
/*      */ import com.aelitis.azureus.core.devices.TranscodeAnalysisListener;
/*      */ import com.aelitis.azureus.core.devices.TranscodeException;
/*      */ import com.aelitis.azureus.core.devices.TranscodeFile;
/*      */ import com.aelitis.azureus.core.devices.TranscodeJob;
/*      */ import com.aelitis.azureus.core.devices.TranscodeProfile;
/*      */ import com.aelitis.azureus.core.devices.TranscodeProvider;
/*      */ import com.aelitis.azureus.core.devices.TranscodeProviderAdapter;
/*      */ import com.aelitis.azureus.core.devices.TranscodeProviderAnalysis;
/*      */ import com.aelitis.azureus.core.devices.TranscodeProviderJob;
/*      */ import com.aelitis.azureus.core.devices.TranscodeQueue;
/*      */ import com.aelitis.azureus.core.devices.TranscodeQueueActionListener;
/*      */ import com.aelitis.azureus.core.devices.TranscodeQueueListener;
/*      */ import com.aelitis.azureus.core.devices.TranscodeTarget;
/*      */ import com.aelitis.azureus.core.download.DiskManagerFileInfoURL;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.core.util.average.Average;
/*      */ import com.aelitis.azureus.core.util.average.AverageFactory;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.net.URI;
/*      */ import java.net.URL;
/*      */ import java.net.URLConnection;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DelayedEvent;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.ipc.IPCInterface;
/*      */ import org.gudy.azureus2.plugins.utils.DelayedTask;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*      */ import org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class TranscodeQueueImpl
/*      */   implements TranscodeQueue
/*      */ {
/*      */   private static final String CONFIG_FILE = "xcodejobs.config";
/*   70 */   private static final Object KEY_XCODE_ERROR = new Object();
/*      */   
/*      */   private TranscodeManagerImpl manager;
/*      */   
/*   74 */   private List<TranscodeJobImpl> queue = new ArrayList();
/*   75 */   private AESemaphore queue_sem = new AESemaphore("XcodeQ");
/*      */   
/*      */   private AEThread2 queue_thread;
/*      */   
/*      */   private volatile TranscodeJobImpl current_job;
/*   80 */   private AsyncDispatcher anaylsis_dispatcher = new AsyncDispatcher();
/*      */   
/*   82 */   private CopyOnWriteList<TranscodeQueueListener> listeners = new CopyOnWriteList();
/*   83 */   private CopyOnWriteList<TranscodeQueueActionListener> action_listeners = new CopyOnWriteList();
/*      */   
/*      */   private volatile boolean paused;
/*      */   
/*      */   private volatile int max_bytes_per_sec;
/*      */   
/*      */   private volatile boolean config_dirty;
/*      */   
/*      */ 
/*      */   protected TranscodeQueueImpl(TranscodeManagerImpl _manager)
/*      */   {
/*   94 */     this.manager = _manager;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void initialise()
/*      */   {
/*  100 */     loadConfig();
/*      */     
/*  102 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "xcode.queue.paused", "xcode.queue.maxbps" }, new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String name)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  113 */         TranscodeQueueImpl.this.paused = COConfigurationManager.getBooleanParameter("xcode.queue.paused", false);
/*  114 */         TranscodeQueueImpl.this.max_bytes_per_sec = COConfigurationManager.getIntParameter("xcode.queue.maxbps", 0);
/*      */       }
/*      */       
/*  117 */     });
/*  118 */     DelayedTask delayed_task = UtilitiesImpl.addDelayedTask("TranscodeQueue:schedule", new Runnable()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void run()
/*      */       {
/*      */ 
/*      */ 
/*  126 */         TranscodeQueueImpl.this.schedule();
/*      */       }
/*      */       
/*  129 */     });
/*  130 */     delayed_task.queue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean process(final TranscodeJobImpl job)
/*      */   {
/*  137 */     TranscodePipe pipe = null;
/*      */     
/*  139 */     this.current_job = job;
/*      */     
/*  141 */     DeviceImpl device = job.getDevice();
/*      */     
/*  143 */     device.setTranscoding(true);
/*      */     try
/*      */     {
/*  146 */       job.starts();
/*      */       
/*  148 */       TranscodeProvider provider = job.getProfile().getProvider();
/*      */       
/*  150 */       error = new TranscodeException[] { null };
/*      */       
/*  152 */       TranscodeProfile profile = job.getProfile();
/*      */       
/*  154 */       final TranscodeFileImpl transcode_file = job.getTranscodeFile();
/*      */       
/*      */       TranscodeProviderAnalysis provider_analysis;
/*      */       
/*      */       TranscodeProviderAnalysis provider_analysis;
/*      */       boolean xcode_required;
/*  160 */       if (provider == null)
/*      */       {
/*  162 */         boolean xcode_required = false;
/*      */         
/*  164 */         provider_analysis = null;
/*      */       }
/*      */       else
/*      */       {
/*  168 */         provider_analysis = analyse(job);
/*      */         
/*  170 */         xcode_required = provider_analysis.getBooleanProperty(1);
/*      */         
/*      */         int tt_req;
/*      */         int tt_req;
/*  174 */         if (job.isStream())
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  180 */           tt_req = 3;
/*      */         }
/*      */         else
/*      */         {
/*  184 */           tt_req = job.getTranscodeRequirement();
/*      */           
/*      */ 
/*      */ 
/*  188 */           if ((device instanceof TranscodeTarget))
/*      */           {
/*  190 */             if (provider_analysis.getLongProperty(4) == 0L)
/*      */             {
/*  192 */               if (((TranscodeTarget)device).isAudioCompatible(transcode_file))
/*      */               {
/*  194 */                 tt_req = 1;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  200 */         if (tt_req == 1)
/*      */         {
/*  202 */           xcode_required = false;
/*      */         }
/*  204 */         else if (tt_req == 3)
/*      */         {
/*  206 */           xcode_required = true;
/*      */           
/*  208 */           provider_analysis.setBooleanProperty(5, true);
/*      */         }
/*      */       }
/*      */       org.gudy.azureus2.plugins.disk.DiskManagerFileInfo source;
/*  212 */       if (xcode_required)
/*      */       {
/*  214 */         final AESemaphore xcode_sem = new AESemaphore("xcode:proc");
/*      */         
/*  216 */         final TranscodeProviderJob[] provider_job = { null };
/*      */         
/*  218 */         TranscodeProviderAdapter xcode_adapter = new TranscodeProviderAdapter()
/*      */         {
/*      */           private boolean resolution_updated;
/*      */           
/*      */ 
/*  223 */           private final int ETA_AVERAGE_SIZE = 10;
/*      */           
/*      */           private int last_eta;
/*      */           private int eta_samples;
/*  227 */           private Average eta_average = AverageFactory.MovingAverage(10);
/*      */           
/*      */           private int last_percent;
/*  230 */           private long initial_file_downloaded = job.getFile().getDownloaded();
/*  231 */           private long file_size = job.getFile().getLength();
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void updateProgress(int percent, int eta_secs, int new_width, int new_height)
/*      */           {
/*  240 */             this.last_eta = eta_secs;
/*  241 */             this.last_percent = percent;
/*      */             
/*  243 */             TranscodeProviderJob prov_job = provider_job[0];
/*      */             
/*  245 */             if (prov_job == null)
/*      */             {
/*  247 */               return;
/*      */             }
/*      */             
/*  250 */             int job_state = job.getState();
/*      */             
/*  252 */             if ((job_state == 4) || (job_state == 7))
/*      */             {
/*      */ 
/*  255 */               prov_job.cancel();
/*      */             }
/*  257 */             else if ((TranscodeQueueImpl.this.paused) || (job_state == 2))
/*      */             {
/*  259 */               prov_job.pause();
/*      */             }
/*      */             else
/*      */             {
/*  263 */               if (job_state == 1)
/*      */               {
/*  265 */                 prov_job.resume();
/*      */               }
/*      */               
/*  268 */               job.updateProgress(percent, eta_secs);
/*      */               
/*  270 */               prov_job.setMaxBytesPerSecond(TranscodeQueueImpl.this.max_bytes_per_sec);
/*      */               
/*  272 */               if (!this.resolution_updated)
/*      */               {
/*  274 */                 if ((new_width > 0) && (new_height > 0))
/*      */                 {
/*  276 */                   transcode_file.setResolution(new_width, new_height);
/*      */                   
/*  278 */                   this.resolution_updated = true;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void streamStats(long connect_rate, long write_speed)
/*      */           {
/*  293 */             if ((Constants.isOSX) && (job.getEnableAutoRetry()) && (job.canUseDirectInput()) && (job.getAutoRetryCount() == 0))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*  298 */               if ((connect_rate > 5L) && (this.last_percent < 100))
/*      */               {
/*  300 */                 long eta = this.eta_average.update(this.last_eta);
/*      */                 
/*  302 */                 this.eta_samples += 1;
/*      */                 
/*  304 */                 if (this.eta_samples >= 10)
/*      */                 {
/*  306 */                   long total_time = eta * 100L / (100 - this.last_percent);
/*      */                   
/*  308 */                   long total_write = total_time * write_speed;
/*      */                   
/*  310 */                   org.gudy.azureus2.plugins.disk.DiskManagerFileInfo file = job.getFile();
/*      */                   
/*  312 */                   long length = file.getLength();
/*      */                   
/*  314 */                   if (length > 0L)
/*      */                   {
/*  316 */                     double over_write = total_write / length;
/*      */                     
/*  318 */                     if (over_write > 5.0D)
/*      */                     {
/*  320 */                       failed(new TranscodeException("Overwrite limit exceeded, abandoning transcode"));
/*      */                       
/*  322 */                       provider_job[0].cancel();
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */               else {
/*  328 */                 this.eta_samples = 0;
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */           public void failed(TranscodeException e)
/*      */           {
/*      */             try
/*      */             {
/*  338 */               if (error[0] == null)
/*      */               {
/*  340 */                 error[0] = e;
/*      */               }
/*      */               
/*  343 */               if (e.isRetryDisabled())
/*      */               {
/*  345 */                 job.setEnableAutoRetry(false);
/*      */               }
/*      */             }
/*      */             finally {
/*  349 */               xcode_sem.release();
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */           public void complete()
/*      */           {
/*      */             try
/*      */             {
/*  359 */               long current_downloaded = job.getFile().getDownloaded();
/*      */               
/*  361 */               if ((this.file_size > 0L) && (this.initial_file_downloaded < this.file_size) && (current_downloaded < this.file_size))
/*      */               {
/*  363 */                 if (error[0] == null)
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  369 */                   long contiguous_downloaded = 0L;
/*      */                   try
/*      */                   {
/*  372 */                     org.gudy.azureus2.plugins.disk.DiskManagerFileInfo _file_info = job.getFile();
/*      */                     
/*  374 */                     Download download = _file_info.getDownload();
/*      */                     
/*  376 */                     org.gudy.azureus2.core3.disk.DiskManagerFileInfo file_info = PluginCoreUtils.unwrap(_file_info);
/*      */                     
/*  378 */                     TOTorrentFile torrent_file = file_info.getTorrentFile();
/*      */                     
/*  380 */                     TOTorrent torrent = torrent_file.getTorrent();
/*      */                     
/*  382 */                     TOTorrentFile[] torrent_files = torrent.getFiles();
/*      */                     
/*  384 */                     long byte_start = 0L;
/*      */                     
/*  386 */                     for (TOTorrentFile tf : torrent_files)
/*      */                     {
/*  388 */                       if (tf == torrent_file) {
/*      */                         break;
/*      */                       }
/*      */                       
/*      */ 
/*  393 */                       byte_start += tf.getLength();
/*      */                     }
/*      */                     
/*  396 */                     org.gudy.azureus2.plugins.disk.DiskManager dm = download.getDiskManager();
/*      */                     
/*  398 */                     if (dm == null)
/*      */                     {
/*  400 */                       throw new Exception("Download stopped");
/*      */                     }
/*      */                     
/*  403 */                     DiskManagerPiece[] pieces = PluginCoreUtils.unwrap(dm).getPieces();
/*      */                     
/*  405 */                     long piece_size = torrent.getPieceLength();
/*      */                     
/*  407 */                     int first_piece_index = (int)(byte_start / piece_size);
/*  408 */                     int first_piece_offset = (int)(byte_start % piece_size);
/*      */                     
/*  410 */                     int last_piece_index = torrent_file.getLastPieceNumber();
/*      */                     
/*  412 */                     DiskManagerPiece first_piece = pieces[first_piece_index];
/*      */                     
/*  414 */                     if (!first_piece.isDone())
/*      */                     {
/*  416 */                       boolean[] blocks = first_piece.getWritten();
/*      */                       
/*  418 */                       if (blocks == null)
/*      */                       {
/*  420 */                         if (first_piece.isDone())
/*      */                         {
/*  422 */                           contiguous_downloaded = first_piece.getLength() - first_piece_offset;
/*      */                         }
/*      */                       }
/*      */                       else {
/*  426 */                         int piece_offset = 0;
/*      */                         
/*  428 */                         for (int j = 0; j < blocks.length; j++)
/*      */                         {
/*  430 */                           if (blocks[j] == 0)
/*      */                             break;
/*  432 */                           int block_size = first_piece.getBlockSize(j);
/*      */                           
/*  434 */                           piece_offset += block_size;
/*      */                           
/*  436 */                           if (contiguous_downloaded == 0L)
/*      */                           {
/*  438 */                             if (piece_offset > first_piece_offset)
/*      */                             {
/*  440 */                               contiguous_downloaded = piece_offset - first_piece_offset;
/*      */                             }
/*      */                           }
/*      */                           else {
/*  444 */                             contiguous_downloaded += block_size;
/*      */                           }
/*      */                           
/*      */                         }
/*      */                         
/*      */                       }
/*      */                       
/*      */                     }
/*      */                     else
/*      */                     {
/*  454 */                       contiguous_downloaded = first_piece.getLength() - first_piece_offset;
/*      */                       
/*  456 */                       for (int i = first_piece_index + 1; i <= last_piece_index; i++)
/*      */                       {
/*  458 */                         DiskManagerPiece piece = pieces[i];
/*      */                         
/*  460 */                         if (piece.isDone())
/*      */                         {
/*  462 */                           contiguous_downloaded += piece.getLength();
/*      */                         }
/*      */                         else
/*      */                         {
/*  466 */                           boolean[] blocks = piece.getWritten();
/*      */                           
/*  468 */                           if (blocks == null)
/*      */                           {
/*  470 */                             if (!piece.isDone())
/*      */                               break;
/*  472 */                             contiguous_downloaded += piece.getLength(); break;
/*      */                           }
/*      */                           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  480 */                           for (int j = 0; j < blocks.length; j++)
/*      */                           {
/*  482 */                             if (blocks[j] == 0)
/*      */                               break;
/*  484 */                             contiguous_downloaded += piece.getBlockSize(j);
/*      */                           }
/*      */                           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  493 */                           break;
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                   catch (Throwable e) {}
/*      */                   
/*      */ 
/*      */ 
/*  502 */                   if (contiguous_downloaded < this.file_size)
/*      */                   {
/*      */ 
/*      */ 
/*  506 */                     current_downloaded = job.getFile().getDownloaded();
/*      */                     
/*  508 */                     if (current_downloaded < this.file_size)
/*      */                     {
/*  510 */                       Debug.out("Premature transcode termination: init=" + this.initial_file_downloaded + ", curr=" + current_downloaded + ", len=" + this.file_size);
/*      */                       
/*  512 */                       error[0] = new TranscodeException("Transcode terminated prematurely");
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             finally {
/*  519 */               xcode_sem.release();
/*      */             }
/*      */             
/*      */           }
/*  523 */         };
/*  524 */         boolean direct_input = job.useDirectInput();
/*      */         
/*  526 */         if (job.isStream())
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  537 */           pipe = new TranscodePipeStreamSource2(new TranscodePipeStreamSource2.streamListener()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void gotStream(InputStream is)
/*      */             {
/*      */ 
/*  544 */               job.setStream(is);
/*      */             }
/*      */             
/*  547 */           });
/*  548 */           provider_job[0] = provider.transcode(xcode_adapter, provider_analysis, direct_input, job.getFile(), profile, new URL("tcp://127.0.0.1:" + pipe.getPort()));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  559 */           File output_file = transcode_file.getCacheFile();
/*      */           
/*  561 */           provider_job[0] = provider.transcode(xcode_adapter, provider_analysis, direct_input, job.getFile(), profile, output_file.toURI().toURL());
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  571 */         provider_job[0].setMaxBytesPerSecond(this.max_bytes_per_sec);
/*      */         
/*  573 */         TranscodeQueueListener listener = new TranscodeQueueListener()
/*      */         {
/*      */           public void jobAdded(TranscodeJob job) {}
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void jobChanged(TranscodeJob changed_job)
/*      */           {
/*  586 */             if (changed_job == job)
/*      */             {
/*  588 */               int state = job.getState();
/*      */               
/*  590 */               if (state == 2)
/*      */               {
/*  592 */                 provider_job[0].pause();
/*      */               }
/*  594 */               else if (state == 1)
/*      */               {
/*  596 */                 provider_job[0].resume();
/*      */               }
/*  598 */               else if ((state == 4) || (state == 6))
/*      */               {
/*      */ 
/*  601 */                 provider_job[0].cancel();
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */           public void jobRemoved(TranscodeJob removed_job)
/*      */           {
/*  610 */             if (removed_job == job)
/*      */             {
/*  612 */               provider_job[0].cancel();
/*      */             }
/*      */           }
/*      */         };
/*      */         try
/*      */         {
/*  618 */           addListener(listener);
/*      */           
/*  620 */           xcode_sem.reserve();
/*      */         }
/*      */         finally
/*      */         {
/*  624 */           removeListener(listener);
/*      */         }
/*      */         
/*  627 */         if (error[0] != null)
/*      */         {
/*  629 */           throw error[0];
/*      */         }
/*      */         
/*      */       }
/*      */       else
/*      */       {
/*  635 */         source = job.getFile();
/*      */         
/*  637 */         transcode_file.setTranscodeRequired(false);
/*      */         
/*  639 */         if (job.isStream())
/*      */         {
/*  641 */           PluginInterface av_pi = PluginInitializer.getDefaultInterface().getPluginManager().getPluginInterfaceByID("azupnpav");
/*      */           
/*  643 */           if (av_pi == null)
/*      */           {
/*  645 */             throw new TranscodeException("Media Server plugin not found");
/*      */           }
/*      */           
/*  648 */           IPCInterface av_ipc = av_pi.getIPC();
/*      */           
/*  650 */           String url_str = (String)av_ipc.invoke("getContentURL", new Object[] { source });
/*      */           
/*      */ 
/*  653 */           if ((url_str == null) || (url_str.length() == 0))
/*      */           {
/*      */ 
/*      */ 
/*  657 */             File source_file = source.getFile();
/*      */             
/*  659 */             if (source_file.exists())
/*      */             {
/*  661 */               job.setStream(new BufferedInputStream(new FileInputStream(source_file)));
/*      */             }
/*      */             else
/*      */             {
/*  665 */               throw new TranscodeException("No UPnPAV URL and file doesn't exist");
/*      */             }
/*      */           }
/*      */           else {
/*  669 */             URL source_url = new URL(url_str);
/*      */             
/*  671 */             job.setStream(source_url.openConnection().getInputStream());
/*      */           }
/*      */         } else {
/*  674 */           boolean url_input_source = source instanceof DiskManagerFileInfoURL;
/*      */           
/*  676 */           if ((device.getAlwaysCacheFiles()) || (url_input_source))
/*      */           {
/*  678 */             PluginInterface av_pi = PluginInitializer.getDefaultInterface().getPluginManager().getPluginInterfaceByID("azupnpav");
/*      */             
/*  680 */             if (av_pi == null)
/*      */             {
/*  682 */               throw new TranscodeException("Media Server plugin not found");
/*      */             }
/*      */             
/*  685 */             IPCInterface av_ipc = av_pi.getIPC();
/*      */             
/*  687 */             String url_str = (String)av_ipc.invoke("getContentURL", new Object[] { source });
/*      */             
/*      */             long length;
/*      */             InputStream is;
/*      */             InputStream is;
/*      */             long length;
/*  693 */             if ((url_str == null) || (url_str.length() == 0))
/*      */             {
/*      */ 
/*      */ 
/*  697 */               if (url_input_source)
/*      */               {
/*  699 */                 ((DiskManagerFileInfoURL)source).download();
/*      */               }
/*      */               
/*  702 */               File source_file = source.getFile();
/*      */               long length;
/*  704 */               if (source_file.exists())
/*      */               {
/*  706 */                 Object is = new BufferedInputStream(new FileInputStream(source_file));
/*      */                 
/*  708 */                 length = source_file.length();
/*      */               }
/*      */               else
/*      */               {
/*  712 */                 throw new TranscodeException("No UPnPAV URL and file doesn't exist");
/*      */               }
/*      */             }
/*      */             else {
/*  716 */               URL source_url = new URL(url_str);
/*      */               
/*  718 */               URLConnection connection = source_url.openConnection();
/*      */               
/*  720 */               is = source_url.openConnection().getInputStream();
/*      */               
/*  722 */               String s = connection.getHeaderField("content-length");
/*      */               long length;
/*  724 */               if (s != null)
/*      */               {
/*  726 */                 length = Long.parseLong(s);
/*      */               }
/*      */               else
/*      */               {
/*  730 */                 length = -1L;
/*      */               }
/*      */             }
/*      */             
/*  734 */             OutputStream os = null;
/*      */             
/*  736 */             final boolean[] cancel_copy = { false };
/*      */             
/*  738 */             TranscodeQueueListener copy_listener = new TranscodeQueueListener()
/*      */             {
/*      */               public void jobAdded(TranscodeJob job) {}
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               public void jobChanged(TranscodeJob changed_job)
/*      */               {
/*  751 */                 if (changed_job == job)
/*      */                 {
/*  753 */                   int state = job.getState();
/*      */                   
/*  755 */                   if (state != 2)
/*      */                   {
/*  757 */                     if (state != 1)
/*      */                     {
/*  759 */                       if ((state == 4) || (state == 6))
/*      */                       {
/*      */ 
/*  762 */                         cancel_copy[0] = true;
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/*      */               public void jobRemoved(TranscodeJob removed_job)
/*      */               {
/*  771 */                 if (removed_job == job)
/*      */                 {
/*  773 */                   cancel_copy[0] = true;
/*      */                 }
/*      */               }
/*      */             };
/*      */             try
/*      */             {
/*  779 */               addListener(copy_listener);
/*      */               
/*  781 */               os = new FileOutputStream(transcode_file.getCacheFile());
/*      */               
/*  783 */               long total_copied = 0L;
/*      */               
/*  785 */               byte[] buffer = new byte[131072];
/*      */               
/*      */               for (;;)
/*      */               {
/*  789 */                 if (cancel_copy[0] != 0)
/*      */                 {
/*  791 */                   throw new TranscodeException("Copy cancelled");
/*      */                 }
/*      */                 
/*  794 */                 int len = is.read(buffer);
/*      */                 
/*  796 */                 if (len <= 0) {
/*      */                   break;
/*      */                 }
/*      */                 
/*      */ 
/*  801 */                 os.write(buffer, 0, len);
/*      */                 
/*  803 */                 total_copied += len;
/*      */                 
/*  805 */                 if (length > 0L)
/*      */                 {
/*  807 */                   job.updateProgress((int)(total_copied * 100L / length), -1);
/*      */                 }
/*      */                 
/*  810 */                 total_copied += len;
/*      */               }
/*      */             }
/*      */             finally {
/*      */               try {
/*  815 */                 is.close();
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/*  819 */                 Debug.out(e);
/*      */               }
/*      */               try
/*      */               {
/*  823 */                 if (os != null)
/*      */                 {
/*  825 */                   os.close();
/*      */                 }
/*      */               }
/*      */               catch (Throwable e) {
/*  829 */                 Debug.out(e);
/*      */               }
/*      */               
/*  832 */               removeListener(copy_listener);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  838 */       job.complete();
/*      */       
/*  840 */       return 1;
/*      */     }
/*      */     catch (Throwable e) {
/*      */       final TranscodeException[] error;
/*  844 */       job.failed(e);
/*      */       
/*  846 */       e.printStackTrace();
/*      */       
/*  848 */       if ((!job.isStream()) && (job.getEnableAutoRetry()) && (job.getAutoRetryCount() == 0) && (job.canUseDirectInput()) && (!job.useDirectInput()))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  854 */         log("Auto-retrying transcode with direct input");
/*      */         
/*  856 */         job.setUseDirectInput();
/*      */         
/*  858 */         job.setAutoRetry(true);
/*      */         
/*  860 */         this.queue_sem.release();
/*      */       }
/*      */       
/*  863 */       return 0;
/*      */     }
/*      */     finally
/*      */     {
/*  867 */       if (pipe != null)
/*      */       {
/*  869 */         pipe.destroy();
/*      */       }
/*      */       
/*  872 */       device.setTranscoding(false);
/*      */       
/*  874 */       this.current_job = null;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void schedule()
/*      */   {
/*  881 */     synchronized (this)
/*      */     {
/*  883 */       if ((this.queue.size() > 0) && (this.queue_thread == null))
/*      */       {
/*  885 */         this.queue_thread = new AEThread2("XcodeQ", true)
/*      */         {
/*      */           /* Error */
/*      */           public void run()
/*      */           {
/*      */             // Byte code:
/*      */             //   0: aload_0
/*      */             //   1: getfield 99	com/aelitis/azureus/core/devices/impl/TranscodeQueueImpl$7:this$0	Lcom/aelitis/azureus/core/devices/impl/TranscodeQueueImpl;
/*      */             //   4: invokevirtual 105	com/aelitis/azureus/core/devices/impl/TranscodeQueueImpl:checkJobStatus	()V
/*      */             //   7: aload_0
/*      */             //   8: getfield 99	com/aelitis/azureus/core/devices/impl/TranscodeQueueImpl$7:this$0	Lcom/aelitis/azureus/core/devices/impl/TranscodeQueueImpl;
/*      */             //   11: invokestatic 109	com/aelitis/azureus/core/devices/impl/TranscodeQueueImpl:access$200	(Lcom/aelitis/azureus/core/devices/impl/TranscodeQueueImpl;)Lorg/gudy/azureus2/core3/util/AESemaphore;
/*      */             //   14: ldc2_w 50
/*      */             //   17: invokevirtual 111	org/gudy/azureus2/core3/util/AESemaphore:reserve	(J)Z
/*      */             //   20: istore_1
/*      */             //   21: aconst_null
/*      */             //   22: astore_2
/*      */             //   23: aload_0
/*      */             //   24: getfield 99	com/aelitis/azureus/core/devices/impl/TranscodeQueueImpl$7:this$0	Lcom/aelitis/azureus/core/devices/impl/TranscodeQueueImpl;
/*      */             //   27: dup
/*      */             //   28: astore_3
/*      */             //   29: monitorenter
/*      */             //   30: iload_1
/*      */             //   31: ifne +37 -> 68
/*      */             //   34: aload_0
/*      */             //   35: getfield 99	com/aelitis/azureus/core/devices/impl/TranscodeQueueImpl$7:this$0	Lcom/aelitis/azureus/core/devices/impl/TranscodeQueueImpl;
/*      */             //   38: invokestatic 108	com/aelitis/azureus/core/devices/impl/TranscodeQueueImpl:access$300	(Lcom/aelitis/azureus/core/devices/impl/TranscodeQueueImpl;)Ljava/util/List;
/*      */             //   41: invokeinterface 116 1 0
/*      */             //   46: ifne +22 -> 68
/*      */             //   49: aload_0
/*      */             //   50: getfield 99	com/aelitis/azureus/core/devices/impl/TranscodeQueueImpl$7:this$0	Lcom/aelitis/azureus/core/devices/impl/TranscodeQueueImpl;
/*      */             //   53: aconst_null
/*      */             //   54: invokestatic 110	com/aelitis/azureus/core/devices/impl/TranscodeQueueImpl:access$402	(Lcom/aelitis/azureus/core/devices/impl/TranscodeQueueImpl;Lorg/gudy/azureus2/core3/util/AEThread2;)Lorg/gudy/azureus2/core3/util/AEThread2;
/*      */             //   57: pop
/*      */             //   58: aload_3
/*      */             //   59: monitorexit
/*      */             //   60: aload_0
/*      */             //   61: getfield 99	com/aelitis/azureus/core/devices/impl/TranscodeQueueImpl$7:this$0	Lcom/aelitis/azureus/core/devices/impl/TranscodeQueueImpl;
/*      */             //   64: invokevirtual 105	com/aelitis/azureus/core/devices/impl/TranscodeQueueImpl:checkJobStatus	()V
/*      */             //   67: return
/*      */             //   68: aload_0
/*      */             //   69: getfield 99	com/aelitis/azureus/core/devices/impl/TranscodeQueueImpl$7:this$0	Lcom/aelitis/azureus/core/devices/impl/TranscodeQueueImpl;
/*      */             //   72: invokestatic 108	com/aelitis/azureus/core/devices/impl/TranscodeQueueImpl:access$300	(Lcom/aelitis/azureus/core/devices/impl/TranscodeQueueImpl;)Ljava/util/List;
/*      */             //   75: invokeinterface 117 1 0
/*      */             //   80: astore 4
/*      */             //   82: aload 4
/*      */             //   84: invokeinterface 114 1 0
/*      */             //   89: ifeq +90 -> 179
/*      */             //   92: aload 4
/*      */             //   94: invokeinterface 115 1 0
/*      */             //   99: checkcast 53	com/aelitis/azureus/core/devices/impl/TranscodeJobImpl
/*      */             //   102: astore 5
/*      */             //   104: aload 5
/*      */             //   106: invokevirtual 100	com/aelitis/azureus/core/devices/impl/TranscodeJobImpl:getState	()I
/*      */             //   109: istore 6
/*      */             //   111: iload 6
/*      */             //   113: iconst_5
/*      */             //   114: if_icmpne +28 -> 142
/*      */             //   117: aload 5
/*      */             //   119: invokevirtual 103	com/aelitis/azureus/core/devices/impl/TranscodeJobImpl:isAutoRetry	()Z
/*      */             //   122: ifeq +20 -> 142
/*      */             //   125: aload 5
/*      */             //   127: iconst_0
/*      */             //   128: invokevirtual 104	com/aelitis/azureus/core/devices/impl/TranscodeJobImpl:setAutoRetry	(Z)V
/*      */             //   131: aload 5
/*      */             //   133: invokevirtual 102	com/aelitis/azureus/core/devices/impl/TranscodeJobImpl:reset	()V
/*      */             //   136: aload 5
/*      */             //   138: astore_2
/*      */             //   139: goto +40 -> 179
/*      */             //   142: iload 6
/*      */             //   144: iconst_2
/*      */             //   145: if_icmpne +9 -> 154
/*      */             //   148: aload 5
/*      */             //   150: astore_2
/*      */             //   151: goto +25 -> 176
/*      */             //   154: iload 6
/*      */             //   156: ifne +20 -> 176
/*      */             //   159: aload_2
/*      */             //   160: ifnonnull +16 -> 176
/*      */             //   163: aload 5
/*      */             //   165: invokevirtual 101	com/aelitis/azureus/core/devices/impl/TranscodeJobImpl:getDownloadETA	()J
/*      */             //   168: lconst_0
/*      */             //   169: lcmp
/*      */             //   170: ifne +6 -> 176
/*      */             //   173: aload 5
/*      */             //   175: astore_2
/*      */             //   176: goto -94 -> 82
/*      */             //   179: aload_3
/*      */             //   180: monitorexit
/*      */             //   181: goto +10 -> 191
/*      */             //   184: astore 7
/*      */             //   186: aload_3
/*      */             //   187: monitorexit
/*      */             //   188: aload 7
/*      */             //   190: athrow
/*      */             //   191: aload_0
/*      */             //   192: getfield 99	com/aelitis/azureus/core/devices/impl/TranscodeQueueImpl$7:this$0	Lcom/aelitis/azureus/core/devices/impl/TranscodeQueueImpl;
/*      */             //   195: invokevirtual 105	com/aelitis/azureus/core/devices/impl/TranscodeQueueImpl:checkJobStatus	()V
/*      */             //   198: aload_2
/*      */             //   199: ifnull +31 -> 230
/*      */             //   202: aload_0
/*      */             //   203: getfield 99	com/aelitis/azureus/core/devices/impl/TranscodeQueueImpl$7:this$0	Lcom/aelitis/azureus/core/devices/impl/TranscodeQueueImpl;
/*      */             //   206: aload_2
/*      */             //   207: invokevirtual 106	com/aelitis/azureus/core/devices/impl/TranscodeQueueImpl:process	(Lcom/aelitis/azureus/core/devices/impl/TranscodeJobImpl;)Z
/*      */             //   210: ifeq +20 -> 230
/*      */             //   213: aload_0
/*      */             //   214: getfield 99	com/aelitis/azureus/core/devices/impl/TranscodeQueueImpl$7:this$0	Lcom/aelitis/azureus/core/devices/impl/TranscodeQueueImpl;
/*      */             //   217: aload_2
/*      */             //   218: iconst_1
/*      */             //   219: invokevirtual 107	com/aelitis/azureus/core/devices/impl/TranscodeQueueImpl:remove	(Lcom/aelitis/azureus/core/devices/impl/TranscodeJobImpl;Z)V
/*      */             //   222: goto +8 -> 230
/*      */             //   225: astore_3
/*      */             //   226: aload_3
/*      */             //   227: invokestatic 113	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/Throwable;)V
/*      */             //   230: goto -230 -> 0
/*      */             //   233: astore 8
/*      */             //   235: aload_0
/*      */             //   236: getfield 99	com/aelitis/azureus/core/devices/impl/TranscodeQueueImpl$7:this$0	Lcom/aelitis/azureus/core/devices/impl/TranscodeQueueImpl;
/*      */             //   239: invokevirtual 105	com/aelitis/azureus/core/devices/impl/TranscodeQueueImpl:checkJobStatus	()V
/*      */             //   242: aload 8
/*      */             //   244: athrow
/*      */             // Line number table:
/*      */             //   Java source line #894	-> byte code offset #0
/*      */             //   Java source line #896	-> byte code offset #7
/*      */             //   Java source line #898	-> byte code offset #21
/*      */             //   Java source line #900	-> byte code offset #23
/*      */             //   Java source line #902	-> byte code offset #30
/*      */             //   Java source line #904	-> byte code offset #34
/*      */             //   Java source line #906	-> byte code offset #49
/*      */             //   Java source line #908	-> byte code offset #58
/*      */             //   Java source line #963	-> byte code offset #60
/*      */             //   Java source line #912	-> byte code offset #68
/*      */             //   Java source line #914	-> byte code offset #104
/*      */             //   Java source line #918	-> byte code offset #111
/*      */             //   Java source line #920	-> byte code offset #125
/*      */             //   Java source line #922	-> byte code offset #131
/*      */             //   Java source line #924	-> byte code offset #136
/*      */             //   Java source line #926	-> byte code offset #139
/*      */             //   Java source line #928	-> byte code offset #142
/*      */             //   Java source line #933	-> byte code offset #148
/*      */             //   Java source line #935	-> byte code offset #154
/*      */             //   Java source line #937	-> byte code offset #159
/*      */             //   Java source line #939	-> byte code offset #173
/*      */             //   Java source line #942	-> byte code offset #176
/*      */             //   Java source line #943	-> byte code offset #179
/*      */             //   Java source line #945	-> byte code offset #191
/*      */             //   Java source line #947	-> byte code offset #198
/*      */             //   Java source line #949	-> byte code offset #202
/*      */             //   Java source line #952	-> byte code offset #213
/*      */             //   Java source line #957	-> byte code offset #222
/*      */             //   Java source line #954	-> byte code offset #225
/*      */             //   Java source line #956	-> byte code offset #226
/*      */             //   Java source line #960	-> byte code offset #230
/*      */             //   Java source line #963	-> byte code offset #233
/*      */             // Local variable table:
/*      */             //   start	length	slot	name	signature
/*      */             //   0	245	0	this	7
/*      */             //   20	11	1	got	boolean
/*      */             //   22	196	2	job	TranscodeJobImpl
/*      */             //   225	2	3	e	TranscodeActionVetoException
/*      */             //   80	13	4	i$	java.util.Iterator
/*      */             //   102	72	5	j	TranscodeJobImpl
/*      */             //   109	46	6	state	int
/*      */             //   184	5	7	localObject1	Object
/*      */             //   233	10	8	localObject2	Object
/*      */             // Exception table:
/*      */             //   from	to	target	type
/*      */             //   30	60	184	finally
/*      */             //   68	181	184	finally
/*      */             //   184	188	184	finally
/*      */             //   213	222	225	com/aelitis/azureus/core/devices/TranscodeActionVetoException
/*      */             //   0	60	233	finally
/*      */             //   68	235	233	finally
/*      */           }
/*  967 */         };
/*  968 */         this.queue_thread.start();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void updateStatus(int tick_count)
/*      */   {
/*      */     int queue_size;
/*      */     
/*  979 */     synchronized (this)
/*      */     {
/*  981 */       queue_size = this.queue.size();
/*      */     }
/*      */     
/*  984 */     if (queue_size > 0)
/*      */     {
/*  986 */       TranscodeJobImpl[] jobs = getJobs();
/*      */       
/*  988 */       for (TranscodeJobImpl job : jobs)
/*      */       {
/*  990 */         job.updateStatus();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void checkJobStatus()
/*      */   {
/*  998 */     Set<DeviceImpl> devices = new HashSet(Arrays.asList(this.manager.getManager().getDevices()));
/*      */     
/* 1000 */     synchronized (this)
/*      */     {
/* 1002 */       for (TranscodeJobImpl j : this.queue)
/*      */       {
/* 1004 */         if (j.getState() == 5)
/*      */         {
/* 1006 */           DeviceImpl device = j.getDevice();
/*      */           
/* 1008 */           device.setError(KEY_XCODE_ERROR, MessageText.getString("device.error.xcodefail"));
/*      */           
/* 1010 */           devices.remove(device);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1015 */     for (DeviceImpl device : devices)
/*      */     {
/* 1017 */       device.setError(KEY_XCODE_ERROR, null);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public TranscodeJobImpl add(TranscodeTarget target, TranscodeProfile profile, org.gudy.azureus2.plugins.disk.DiskManagerFileInfo file, boolean add_stopped)
/*      */     throws TranscodeException
/*      */   {
/* 1030 */     return add(target, profile, file, add_stopped, false, -1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public TranscodeJobImpl add(TranscodeTarget target, TranscodeProfile profile, org.gudy.azureus2.plugins.disk.DiskManagerFileInfo file, int transcode_requirement, boolean add_stopped)
/*      */     throws TranscodeException
/*      */   {
/* 1043 */     return add(target, profile, file, add_stopped, false, transcode_requirement);
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
/*      */   public TranscodeJobImpl add(TranscodeTarget target, TranscodeProfile profile, org.gudy.azureus2.plugins.disk.DiskManagerFileInfo file, boolean add_stopped, boolean stream, int transcode_requirement)
/*      */     throws TranscodeException
/*      */   {
/* 1057 */     TranscodeFileImpl existing_tf = ((DeviceImpl)target.getDevice()).lookupFile(profile, file);
/*      */     
/* 1059 */     if (existing_tf != null)
/*      */     {
/* 1061 */       List<TranscodeJobImpl> to_remove = new ArrayList();
/*      */       
/* 1063 */       synchronized (this)
/*      */       {
/* 1065 */         for (TranscodeJobImpl job : this.queue)
/*      */         {
/* 1067 */           if ((job.getTarget() == target) && (job.getTranscodeFile().equals(existing_tf)))
/*      */           {
/* 1069 */             to_remove.add(job);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1074 */       for (TranscodeJobImpl job : to_remove)
/*      */       {
/* 1076 */         job.removeForce();
/*      */       }
/*      */       
/* 1079 */       if (!stream)
/*      */       {
/* 1081 */         existing_tf.delete(true);
/*      */       }
/*      */     }
/*      */     
/* 1085 */     TranscodeJobImpl job = new TranscodeJobImpl(this, target, profile, file, add_stopped, transcode_requirement, stream);
/*      */     try
/*      */     {
/* 1088 */       synchronized (this)
/*      */       {
/* 1090 */         this.queue.add(job);
/*      */         
/* 1092 */         this.queue_sem.release();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1097 */       for (TranscodeQueueListener listener : this.listeners) {
/*      */         try
/*      */         {
/* 1100 */           listener.jobAdded(job);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1104 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1109 */       schedule();
/*      */     }
/*      */     
/* 1112 */     return job;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void remove(TranscodeJobImpl job, boolean force)
/*      */     throws TranscodeActionVetoException
/*      */   {
/* 1122 */     synchronized (this)
/*      */     {
/* 1124 */       if (!this.queue.contains(job))
/*      */       {
/* 1126 */         return;
/*      */       }
/*      */     }
/*      */     
/* 1130 */     if (!force)
/*      */     {
/* 1132 */       for (TranscodeQueueActionListener l : this.action_listeners) {
/*      */         try
/*      */         {
/* 1135 */           l.jobWillBeActioned(job, 1);
/*      */         }
/*      */         catch (TranscodeActionVetoException e)
/*      */         {
/* 1139 */           throw e;
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1143 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1148 */     synchronized (this)
/*      */     {
/* 1150 */       if (!this.queue.remove(job))
/*      */       {
/* 1152 */         return;
/*      */       }
/*      */     }
/*      */     
/* 1156 */     configDirty();
/*      */     
/* 1158 */     job.destroy();
/*      */     
/* 1160 */     for (TranscodeQueueListener listener : this.listeners) {
/*      */       try
/*      */       {
/* 1163 */         listener.jobRemoved(job);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1167 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/* 1171 */     checkJobStatus();
/*      */     
/* 1173 */     schedule();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void jobChanged(TranscodeJob job, boolean schedule, boolean persistable)
/*      */   {
/* 1183 */     for (TranscodeQueueListener listener : this.listeners) {
/*      */       try
/*      */       {
/* 1186 */         listener.jobChanged(job);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1190 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/* 1194 */     if (persistable)
/*      */     {
/* 1196 */       configDirty();
/*      */     }
/*      */     
/* 1199 */     if (schedule)
/*      */     {
/* 1201 */       this.queue_sem.release();
/*      */       
/* 1203 */       schedule();
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   protected int getIndex(TranscodeJobImpl job)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: dup
/*      */     //   2: astore_2
/*      */     //   3: monitorenter
/*      */     //   4: aload_0
/*      */     //   5: getfield 768	com/aelitis/azureus/core/devices/impl/TranscodeQueueImpl:queue	Ljava/util/List;
/*      */     //   8: aload_1
/*      */     //   9: invokeinterface 919 2 0
/*      */     //   14: iconst_1
/*      */     //   15: iadd
/*      */     //   16: aload_2
/*      */     //   17: monitorexit
/*      */     //   18: ireturn
/*      */     //   19: astore_3
/*      */     //   20: aload_2
/*      */     //   21: monitorexit
/*      */     //   22: aload_3
/*      */     //   23: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1211	-> byte code offset #0
/*      */     //   Java source line #1213	-> byte code offset #4
/*      */     //   Java source line #1214	-> byte code offset #19
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	24	0	this	TranscodeQueueImpl
/*      */     //   0	24	1	job	TranscodeJobImpl
/*      */     //   2	19	2	Ljava/lang/Object;	Object
/*      */     //   19	4	3	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   4	18	19	finally
/*      */     //   19	22	19	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public TranscodeJobImpl[] getJobs()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: dup
/*      */     //   2: astore_1
/*      */     //   3: monitorenter
/*      */     //   4: aload_0
/*      */     //   5: getfield 768	com/aelitis/azureus/core/devices/impl/TranscodeQueueImpl:queue	Ljava/util/List;
/*      */     //   8: aload_0
/*      */     //   9: getfield 768	com/aelitis/azureus/core/devices/impl/TranscodeQueueImpl:queue	Ljava/util/List;
/*      */     //   12: invokeinterface 917 1 0
/*      */     //   17: anewarray 401	com/aelitis/azureus/core/devices/impl/TranscodeJobImpl
/*      */     //   20: invokeinterface 924 2 0
/*      */     //   25: checkcast 384	[Lcom/aelitis/azureus/core/devices/impl/TranscodeJobImpl;
/*      */     //   28: aload_1
/*      */     //   29: monitorexit
/*      */     //   30: areturn
/*      */     //   31: astore_2
/*      */     //   32: aload_1
/*      */     //   33: monitorexit
/*      */     //   34: aload_2
/*      */     //   35: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1220	-> byte code offset #0
/*      */     //   Java source line #1222	-> byte code offset #4
/*      */     //   Java source line #1223	-> byte code offset #31
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	36	0	this	TranscodeQueueImpl
/*      */     //   2	31	1	Ljava/lang/Object;	Object
/*      */     //   31	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   4	30	31	finally
/*      */     //   31	34	31	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public int getJobCount()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: dup
/*      */     //   2: astore_1
/*      */     //   3: monitorenter
/*      */     //   4: aload_0
/*      */     //   5: getfield 768	com/aelitis/azureus/core/devices/impl/TranscodeQueueImpl:queue	Ljava/util/List;
/*      */     //   8: invokeinterface 917 1 0
/*      */     //   13: aload_1
/*      */     //   14: monitorexit
/*      */     //   15: ireturn
/*      */     //   16: astore_2
/*      */     //   17: aload_1
/*      */     //   18: monitorexit
/*      */     //   19: aload_2
/*      */     //   20: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1229	-> byte code offset #0
/*      */     //   Java source line #1231	-> byte code offset #4
/*      */     //   Java source line #1232	-> byte code offset #16
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	21	0	this	TranscodeQueueImpl
/*      */     //   2	16	1	Ljava/lang/Object;	Object
/*      */     //   16	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   4	15	16	finally
/*      */     //   16	19	16	finally
/*      */   }
/*      */   
/*      */   public TranscodeJob getCurrentJob()
/*      */   {
/* 1238 */     return this.current_job;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isTranscoding()
/*      */   {
/* 1244 */     return this.current_job != null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected TranscodeJobImpl getJob(TranscodeFile for_file)
/*      */   {
/* 1251 */     synchronized (this)
/*      */     {
/* 1253 */       for (TranscodeJobImpl job : this.queue)
/*      */       {
/* 1255 */         if (job.getTranscodeFile().equals(for_file))
/*      */         {
/* 1257 */           return job;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1262 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void moveUp(TranscodeJobImpl job)
/*      */   {
/*      */     TranscodeJob[] updated;
/*      */     
/* 1271 */     synchronized (this)
/*      */     {
/* 1273 */       int index = this.queue.indexOf(job);
/*      */       
/* 1275 */       if ((index <= 0) || (this.queue.size() == 1))
/*      */       {
/* 1277 */         return;
/*      */       }
/*      */       
/* 1280 */       this.queue.remove(job);
/*      */       
/* 1282 */       this.queue.add(index - 1, job);
/*      */       
/* 1284 */       updated = getJobs();
/*      */     }
/*      */     
/* 1287 */     for (TranscodeJob j : updated)
/*      */     {
/* 1289 */       jobChanged(j, false, true);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void moveDown(TranscodeJobImpl job)
/*      */   {
/*      */     TranscodeJob[] updated;
/*      */     
/* 1299 */     synchronized (this)
/*      */     {
/* 1301 */       int index = this.queue.indexOf(job);
/*      */       
/* 1303 */       if ((index < 0) || (index == this.queue.size() - 1))
/*      */       {
/* 1305 */         return;
/*      */       }
/*      */       
/* 1308 */       this.queue.remove(job);
/*      */       
/* 1310 */       this.queue.add(index + 1, job);
/*      */       
/* 1312 */       updated = getJobs();
/*      */     }
/*      */     
/* 1315 */     for (TranscodeJob j : updated)
/*      */     {
/* 1317 */       jobChanged(j, false, true);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void pause()
/*      */   {
/* 1324 */     if (!this.paused)
/*      */     {
/* 1326 */       if (this.paused)
/*      */       {
/* 1328 */         COConfigurationManager.setParameter("xcode.paused", true);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isPaused()
/*      */   {
/* 1336 */     return this.paused;
/*      */   }
/*      */   
/*      */ 
/*      */   public void resume()
/*      */   {
/* 1342 */     if (this.paused)
/*      */     {
/* 1344 */       COConfigurationManager.setParameter("xcode.queue.paused", false);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public long getMaxBytesPerSecond()
/*      */   {
/* 1351 */     return this.max_bytes_per_sec;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setMaxBytesPerSecond(long max)
/*      */   {
/* 1358 */     COConfigurationManager.setParameter("xcode.queue.maxbps", max);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected TranscodeTarget lookupTarget(String target_id)
/*      */     throws TranscodeException
/*      */   {
/* 1367 */     return this.manager.lookupTarget(target_id);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected TranscodeProfile lookupProfile(String profile_id)
/*      */     throws TranscodeException
/*      */   {
/* 1376 */     TranscodeProfile profile = this.manager.getProfileFromUID(profile_id);
/*      */     
/* 1378 */     if (profile == null)
/*      */     {
/* 1380 */       throw new TranscodeException("Transcode profile with id '" + profile_id + "' not found");
/*      */     }
/*      */     
/* 1383 */     return profile;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected org.gudy.azureus2.plugins.disk.DiskManagerFileInfo lookupFile(byte[] hash, int index)
/*      */     throws TranscodeException
/*      */   {
/* 1393 */     return this.manager.lookupFile(hash, index);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void analyse(final TranscodeJobImpl job, final TranscodeAnalysisListener listener)
/*      */     throws TranscodeException
/*      */   {
/* 1403 */     this.anaylsis_dispatcher.dispatch(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */         try
/*      */         {
/* 1410 */           TranscodeProviderAnalysis analysis = TranscodeQueueImpl.this.analyse(job);
/*      */           
/* 1412 */           listener.analysisComplete(job, analysis);
/*      */         }
/*      */         catch (TranscodeException e)
/*      */         {
/* 1416 */           listener.analysisFailed(job, e);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1420 */           listener.analysisFailed(job, new TranscodeException("Analysis failed", e));
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected TranscodeProviderAnalysis analyse(final TranscodeJobImpl job)
/*      */     throws TranscodeException
/*      */   {
/* 1432 */     TranscodeProvider provider = job.getProfile().getProvider();
/*      */     
/* 1434 */     if (provider == null)
/*      */     {
/* 1436 */       throw new TranscodeException("Transcode provider not available");
/*      */     }
/*      */     
/* 1439 */     final TranscodeException[] error = { null };
/*      */     
/* 1441 */     TranscodeProfile profile = job.getProfile();
/*      */     
/* 1443 */     final AESemaphore analysis_sem = new AESemaphore("analysis:proc");
/*      */     
/* 1445 */     final boolean was_stopped = job.getState() == 6;
/*      */     
/* 1447 */     TranscodeProviderAdapter analysis_adapter = new TranscodeProviderAdapter()
/*      */     {
/*      */       public void updateProgress(int percent, int eta_secs, int width, int height) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void streamStats(long connect_rate, long write_speed) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void failed(TranscodeException e)
/*      */       {
/* 1470 */         error[0] = e;
/*      */         
/* 1472 */         analysis_sem.release();
/*      */       }
/*      */       
/*      */ 
/*      */       public void complete()
/*      */       {
/* 1478 */         analysis_sem.release();
/*      */       }
/*      */       
/* 1481 */     };
/* 1482 */     final TranscodeProviderAnalysis provider_analysis = provider.analyse(analysis_adapter, job.getFile(), profile);
/*      */     
/* 1484 */     TranscodeQueueListener analysis_q_listener = new TranscodeQueueListener()
/*      */     {
/*      */       public void jobAdded(TranscodeJob job) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void jobChanged(TranscodeJob changed_job)
/*      */       {
/* 1497 */         if (changed_job == job)
/*      */         {
/* 1499 */           int state = job.getState();
/*      */           
/* 1501 */           if (state == 4)
/*      */           {
/* 1503 */             provider_analysis.cancel();
/*      */           }
/* 1505 */           else if (state == 6)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1510 */             if (!was_stopped)
/*      */             {
/* 1512 */               provider_analysis.cancel();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void jobRemoved(TranscodeJob removed_job)
/*      */       {
/* 1522 */         if (removed_job == job)
/*      */         {
/* 1524 */           provider_analysis.cancel();
/*      */         }
/*      */       }
/*      */     };
/*      */     try
/*      */     {
/* 1530 */       addListener(analysis_q_listener);
/*      */       
/* 1532 */       analysis_sem.reserve();
/*      */     }
/*      */     finally
/*      */     {
/* 1536 */       removeListener(analysis_q_listener);
/*      */     }
/*      */     
/* 1539 */     if (error[0] != null)
/*      */     {
/* 1541 */       throw error[0];
/*      */     }
/*      */     
/* 1544 */     TranscodeFileImpl transcode_file = job.getTranscodeFile();
/*      */     
/* 1546 */     transcode_file.update(provider_analysis);
/*      */     
/* 1548 */     return provider_analysis;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void configDirty()
/*      */   {
/* 1554 */     synchronized (this)
/*      */     {
/* 1556 */       if (this.config_dirty)
/*      */       {
/* 1558 */         return;
/*      */       }
/*      */       
/* 1561 */       this.config_dirty = true;
/*      */       
/* 1563 */       new DelayedEvent("TranscodeQueue:save", 5000L, new AERunnable()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */ 
/* 1570 */           synchronized (TranscodeQueueImpl.this)
/*      */           {
/* 1572 */             if (!TranscodeQueueImpl.this.config_dirty)
/*      */             {
/* 1574 */               return;
/*      */             }
/*      */             
/* 1577 */             TranscodeQueueImpl.this.saveConfig();
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void loadConfig()
/*      */   {
/* 1587 */     if (!FileUtil.resilientConfigFileExists("xcodejobs.config"))
/*      */     {
/* 1589 */       return;
/*      */     }
/*      */     
/* 1592 */     log("Loading configuration");
/*      */     try
/*      */     {
/* 1595 */       synchronized (this)
/*      */       {
/* 1597 */         Map map = FileUtil.readResilientConfigFile("xcodejobs.config");
/*      */         
/* 1599 */         List<Map<String, Object>> l_jobs = (List)map.get("jobs");
/*      */         
/* 1601 */         if (l_jobs != null)
/*      */         {
/* 1603 */           for (Map<String, Object> m : l_jobs) {
/*      */             try
/*      */             {
/* 1606 */               TranscodeJobImpl job = new TranscodeJobImpl(this, m);
/*      */               
/* 1608 */               this.queue.add(job);
/*      */               
/* 1610 */               this.queue_sem.release();
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 1614 */               log("Failed to restore job: " + m, e);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1621 */       log("Configuration load failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void saveConfig()
/*      */   {
/* 1628 */     synchronized (this)
/*      */     {
/* 1630 */       this.config_dirty = false;
/*      */       
/* 1632 */       if (this.queue.size() == 0)
/*      */       {
/* 1634 */         FileUtil.deleteResilientConfigFile("xcodejobs.config");
/*      */       }
/*      */       else
/*      */       {
/* 1638 */         Map<String, Object> map = new HashMap();
/*      */         
/* 1640 */         List<Map<String, Object>> l_jobs = new ArrayList();
/*      */         
/* 1642 */         map.put("jobs", l_jobs);
/*      */         
/* 1644 */         for (TranscodeJobImpl job : this.queue)
/*      */         {
/* 1646 */           if (!job.isStream())
/*      */           {
/*      */ 
/*      */             try
/*      */             {
/*      */ 
/*      */ 
/* 1653 */               l_jobs.add(job.toMap());
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 1657 */               log("Failed to save job", e);
/*      */             }
/*      */           }
/*      */         }
/* 1661 */         FileUtil.writeResilientConfigFile("xcodejobs.config", map);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void close()
/*      */   {
/* 1669 */     if (this.config_dirty)
/*      */     {
/* 1671 */       saveConfig();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(TranscodeQueueListener listener)
/*      */   {
/* 1679 */     if (!this.listeners.contains(listener)) {
/* 1680 */       this.listeners.add(listener);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(TranscodeQueueListener listener)
/*      */   {
/* 1688 */     this.listeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addActionListener(TranscodeQueueActionListener listener)
/*      */   {
/* 1695 */     this.action_listeners.add(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeActionListener(TranscodeQueueActionListener listener)
/*      */   {
/* 1702 */     this.action_listeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void log(String str)
/*      */   {
/* 1709 */     this.manager.log("Queue: " + str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void log(String str, Throwable e)
/*      */   {
/* 1717 */     this.manager.log("Queue: " + str, e);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void generate(IndentWriter writer)
/*      */   {
/* 1724 */     writer.println("Transcode Queue: paused=" + this.paused + ",max_bps=" + this.max_bytes_per_sec);
/*      */     try
/*      */     {
/* 1727 */       writer.indent();
/*      */       
/* 1729 */       TranscodeJobImpl[] jobs = getJobs();
/*      */       
/* 1731 */       for (TranscodeJobImpl job : jobs)
/*      */       {
/* 1733 */         job.generate(writer);
/*      */       }
/*      */     }
/*      */     finally {
/* 1737 */       writer.exdent();
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/impl/TranscodeQueueImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */