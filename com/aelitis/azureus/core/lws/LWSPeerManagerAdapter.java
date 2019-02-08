/*     */ package com.aelitis.azureus.core.lws;
/*     */ 
/*     */ import com.aelitis.azureus.core.peermanager.PeerManagerRegistration;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerReadRequest;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerReadRequestListener;
/*     */ import org.gudy.azureus2.core3.logging.LogRelation;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManagerAdapter;
/*     */ import org.gudy.azureus2.core3.peer.PEPiece;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.core3.util.BEncoder;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
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
/*     */ public class LWSPeerManagerAdapter
/*     */   extends LogRelation
/*     */   implements PEPeerManagerAdapter
/*     */ {
/*     */   private final LightWeightSeed lws;
/*     */   private final PeerManagerRegistration peer_manager_registration;
/*     */   private final String[] enabled_networks;
/*     */   private int md_info_dict_size;
/*  58 */   private WeakReference<byte[]> md_info_dict_ref = new WeakReference(null);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public LWSPeerManagerAdapter(LightWeightSeed _lws, PeerManagerRegistration _peer_manager_registration)
/*     */   {
/*  65 */     this.lws = _lws;
/*     */     
/*  67 */     String main_net = this.lws.getNetwork();
/*     */     
/*  69 */     if (main_net.equals("Public"))
/*     */     {
/*  71 */       this.enabled_networks = AENetworkClassifier.AT_NETWORKS;
/*     */     }
/*     */     else
/*     */     {
/*  75 */       this.enabled_networks = AENetworkClassifier.AT_NON_PUBLIC;
/*     */     }
/*     */     
/*  78 */     this.peer_manager_registration = _peer_manager_registration;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDisplayName()
/*     */   {
/*  84 */     return this.lws.getName();
/*     */   }
/*     */   
/*     */ 
/*     */   public PeerManagerRegistration getPeerManagerRegistration()
/*     */   {
/*  90 */     return this.peer_manager_registration;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getUploadRateLimitBytesPerSecond()
/*     */   {
/*  96 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getDownloadRateLimitBytesPerSecond()
/*     */   {
/* 102 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPermittedBytesToReceive()
/*     */   {
/* 108 */     return Integer.MAX_VALUE;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void permittedReceiveBytesUsed(int bytes) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public int getPermittedBytesToSend()
/*     */   {
/* 120 */     return Integer.MAX_VALUE;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void permittedSendBytesUsed(int bytes) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public int getUploadPriority()
/*     */   {
/* 132 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMaxUploads()
/*     */   {
/* 138 */     return 4;
/*     */   }
/*     */   
/*     */ 
/*     */   public int[] getMaxConnections()
/*     */   {
/* 144 */     return new int[] { 0, 0 };
/*     */   }
/*     */   
/*     */ 
/*     */   public int[] getMaxSeedConnections()
/*     */   {
/* 150 */     return new int[] { 0, 0 };
/*     */   }
/*     */   
/*     */ 
/*     */   public int getExtendedMessagingMode()
/*     */   {
/* 156 */     return 2;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isPeerExchangeEnabled()
/*     */   {
/* 162 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isNetworkEnabled(String network)
/*     */   {
/* 169 */     for (String net : this.enabled_networks)
/*     */     {
/* 171 */       if (net == network)
/*     */       {
/* 173 */         return true;
/*     */       }
/*     */     }
/*     */     
/* 177 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public String[] getEnabledNetworks()
/*     */   {
/* 183 */     return this.enabled_networks;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getCryptoLevel()
/*     */   {
/* 189 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getRandomSeed()
/*     */   {
/* 195 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isPeriodicRescanEnabled()
/*     */   {
/* 201 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setStateFinishing() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setStateSeeding(boolean never_downloaded) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void restartDownload(boolean recheck)
/*     */   {
/* 219 */     Debug.out("restartDownload called for " + getDisplayName());
/*     */   }
/*     */   
/*     */ 
/*     */   public TRTrackerScraperResponse getTrackerScrapeResponse()
/*     */   {
/* 225 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getTrackerClientExtensions()
/*     */   {
/* 231 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTrackerRefreshDelayOverrides(int percent) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isMetadataDownload()
/*     */   {
/* 244 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTorrentInfoDictSize()
/*     */   {
/* 250 */     synchronized (this)
/*     */     {
/* 252 */       if (this.md_info_dict_size == 0)
/*     */       {
/* 254 */         byte[] data = getTorrentInfoDict(null);
/*     */         
/* 256 */         if (data == null)
/*     */         {
/* 258 */           this.md_info_dict_size = -1;
/*     */         }
/*     */         else
/*     */         {
/* 262 */           this.md_info_dict_size = data.length;
/*     */         }
/*     */       }
/*     */       
/* 266 */       return this.md_info_dict_size;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getTorrentInfoDict(PEPeer peer)
/*     */   {
/*     */     try
/*     */     {
/* 275 */       byte[] data = (byte[])this.md_info_dict_ref.get();
/*     */       
/* 277 */       if (data == null)
/*     */       {
/* 279 */         TOTorrent torrent = PluginCoreUtils.unwrap(this.lws.getTorrent());
/*     */         
/* 281 */         data = BEncoder.encode((Map)torrent.serialiseToMap().get("info"));
/*     */         
/* 283 */         this.md_info_dict_ref = new WeakReference(data);
/*     */       }
/*     */       
/* 286 */       return data;
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/* 290 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isNATHealthy()
/*     */   {
/* 297 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addPeer(PEPeer peer) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removePeer(PEPeer peer) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addPiece(PEPiece piece) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removePiece(PEPiece piece) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void discarded(PEPeer peer, int bytes) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void protocolBytesReceived(PEPeer peer, int bytes) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void dataBytesReceived(PEPeer peer, int bytes) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void protocolBytesSent(PEPeer peer, int bytes) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void dataBytesSent(PEPeer peer, int bytes) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void statsRequest(PEPeer originator, Map request, Map reply) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addHTTPSeed(String address, int port) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[][] getSecrets(int crypto_level)
/*     */   {
/* 378 */     return this.lws.getSecrets();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void enqueueReadRequest(PEPeer peer, DiskManagerReadRequest request, DiskManagerReadRequestListener listener)
/*     */   {
/* 387 */     this.lws.enqueueReadRequest(peer, request, listener);
/*     */   }
/*     */   
/*     */   public int getPosition()
/*     */   {
/* 392 */     return Integer.MAX_VALUE;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isPeerSourceEnabled(String peer_source)
/*     */   {
/* 399 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean hasPriorityConnection()
/*     */   {
/* 406 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void priorityConnectionChanged(boolean added) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public LogRelation getLogRelation()
/*     */   {
/* 418 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getRelationText()
/*     */   {
/* 424 */     return this.lws.getRelationText();
/*     */   }
/*     */   
/*     */ 
/*     */   public Object[] getQueryableInterfaces()
/*     */   {
/* 430 */     List interfaces = new ArrayList();
/*     */     
/* 432 */     Object[] intf = this.lws.getQueryableInterfaces();
/*     */     
/* 434 */     for (int i = 0; i < intf.length; i++)
/*     */     {
/* 436 */       if (intf[i] != null)
/*     */       {
/* 438 */         interfaces.add(intf[i]);
/*     */       }
/*     */     }
/*     */     
/* 442 */     interfaces.add(this.lws.getRelation());
/*     */     
/* 444 */     return interfaces.toArray();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/lws/LWSPeerManagerAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */