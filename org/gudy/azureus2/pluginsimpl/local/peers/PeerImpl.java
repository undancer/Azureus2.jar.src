/*     */ package org.gudy.azureus2.pluginsimpl.local.peers;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.LimitedRateGroup;
/*     */ import com.aelitis.azureus.core.peermanager.piecepicker.util.BitFlags;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.logging.LogRelation;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerListener;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.network.Connection;
/*     */ import org.gudy.azureus2.plugins.network.ConnectionStub;
/*     */ import org.gudy.azureus2.plugins.network.RateLimiter;
/*     */ import org.gudy.azureus2.plugins.peers.Peer;
/*     */ import org.gudy.azureus2.plugins.peers.PeerEvent;
/*     */ import org.gudy.azureus2.plugins.peers.PeerListener;
/*     */ import org.gudy.azureus2.plugins.peers.PeerListener2;
/*     */ import org.gudy.azureus2.plugins.peers.PeerManager;
/*     */ import org.gudy.azureus2.plugins.peers.PeerReadRequest;
/*     */ import org.gudy.azureus2.plugins.peers.PeerStats;
/*     */ import org.gudy.azureus2.pluginsimpl.local.messaging.MessageAdapter;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl.PluginLimitedRateGroup;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl.PluginLimitedRateGroupListener;
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
/*     */ 
/*     */ public class PeerImpl
/*     */   extends LogRelation
/*     */   implements Peer
/*     */ {
/*     */   protected PeerManagerImpl manager;
/*     */   protected PEPeer delegate;
/*     */   private HashMap<Object, PEPeerListener> peer_listeners;
/*     */   private UtilitiesImpl.PluginLimitedRateGroupListener up_rg_listener;
/*     */   private UtilitiesImpl.PluginLimitedRateGroupListener down_rg_listener;
/*     */   private volatile boolean closed;
/*     */   
/*     */   protected PeerImpl(PEPeer _delegate)
/*     */   {
/*  72 */     this.delegate = _delegate;
/*     */     
/*  74 */     this.manager = PeerManagerImpl.getPeerManager(this.delegate.getManager());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void bindConnection(ConnectionStub stub) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public PeerManager getManager()
/*     */   {
/*  86 */     return this.manager;
/*     */   }
/*     */   
/*     */ 
/*     */   public PEPeer getDelegate()
/*     */   {
/*  92 */     return this.delegate;
/*     */   }
/*     */   
/*     */   public Connection getConnection() {
/*  96 */     return this.delegate.getPluginConnection();
/*     */   }
/*     */   
/*     */   public boolean supportsMessaging()
/*     */   {
/* 101 */     return this.delegate.supportsMessaging();
/*     */   }
/*     */   
/*     */   public org.gudy.azureus2.plugins.messaging.Message[] getSupportedMessages()
/*     */   {
/* 106 */     com.aelitis.azureus.core.peermanager.messaging.Message[] core_msgs = this.delegate.getSupportedMessages();
/*     */     
/* 108 */     org.gudy.azureus2.plugins.messaging.Message[] plug_msgs = new org.gudy.azureus2.plugins.messaging.Message[core_msgs.length];
/*     */     
/* 110 */     for (int i = 0; i < core_msgs.length; i++) {
/* 111 */       plug_msgs[i] = new MessageAdapter(core_msgs[i]);
/*     */     }
/*     */     
/* 114 */     return plug_msgs;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getState()
/*     */   {
/* 122 */     int state = this.delegate.getPeerState();
/*     */     
/* 124 */     switch (state)
/*     */     {
/*     */ 
/*     */     case 10: 
/* 128 */       return 10;
/*     */     
/*     */ 
/*     */     case 50: 
/* 132 */       return 50;
/*     */     
/*     */ 
/*     */     case 20: 
/* 136 */       return 20;
/*     */     
/*     */ 
/*     */     case 30: 
/* 140 */       return 30;
/*     */     }
/*     */     
/*     */     
/* 144 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getId()
/*     */   {
/* 152 */     byte[] id = this.delegate.getId();
/*     */     
/* 154 */     if (id == null)
/*     */     {
/* 156 */       return new byte[0];
/*     */     }
/*     */     
/* 159 */     byte[] copy = new byte[id.length];
/*     */     
/* 161 */     System.arraycopy(id, 0, copy, 0, copy.length);
/*     */     
/* 163 */     return copy;
/*     */   }
/*     */   
/*     */   public String getIp()
/*     */   {
/* 168 */     return this.delegate.getIp();
/*     */   }
/*     */   
/*     */   public int getPort()
/*     */   {
/* 173 */     return this.delegate.getPort();
/*     */   }
/*     */   
/* 176 */   public int getTCPListenPort() { return this.delegate.getTCPListenPort(); }
/* 177 */   public int getUDPListenPort() { return this.delegate.getUDPListenPort(); }
/* 178 */   public int getUDPNonDataListenPort() { return this.delegate.getUDPNonDataListenPort(); }
/*     */   
/*     */   public boolean isLANLocal()
/*     */   {
/* 182 */     return this.delegate.isLANLocal();
/*     */   }
/*     */   
/*     */   public final boolean[] getAvailable() {
/* 186 */     BitFlags bf = this.delegate.getAvailable();
/* 187 */     if (bf == null) {
/* 188 */       return null;
/*     */     }
/* 190 */     return bf.flags;
/*     */   }
/*     */   
/*     */   public boolean isPieceAvailable(int pieceNumber)
/*     */   {
/* 195 */     return this.delegate.isPieceAvailable(pieceNumber);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isTransferAvailable()
/*     */   {
/* 201 */     return this.delegate.transferAvailable();
/*     */   }
/*     */   
/*     */   public boolean isDownloadPossible()
/*     */   {
/* 206 */     return this.delegate.isDownloadPossible();
/*     */   }
/*     */   
/*     */   public boolean isChoked()
/*     */   {
/* 211 */     return this.delegate.isChokingMe();
/*     */   }
/*     */   
/*     */   public boolean isChoking()
/*     */   {
/* 216 */     return this.delegate.isChokedByMe();
/*     */   }
/*     */   
/*     */   public boolean isInterested()
/*     */   {
/* 221 */     return this.delegate.isInteresting();
/*     */   }
/*     */   
/*     */   public boolean isInteresting()
/*     */   {
/* 226 */     return this.delegate.isInterested();
/*     */   }
/*     */   
/*     */   public boolean isSeed()
/*     */   {
/* 231 */     return this.delegate.isSeed();
/*     */   }
/*     */   
/*     */   public boolean isSnubbed()
/*     */   {
/* 236 */     return this.delegate.isSnubbed();
/*     */   }
/*     */   
/*     */   public long getSnubbedTime()
/*     */   {
/* 241 */     return this.delegate.getSnubbedTime();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setSnubbed(boolean b)
/*     */   {
/* 248 */     this.delegate.setSnubbed(b);
/*     */   }
/*     */   
/*     */   public PeerStats getStats()
/*     */   {
/* 253 */     return new PeerStatsImpl(this.manager, this, this.delegate.getStats());
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isIncoming()
/*     */   {
/* 259 */     return this.delegate.isIncoming();
/*     */   }
/*     */   
/*     */   public int getPercentDone()
/*     */   {
/* 264 */     return this.delegate.getPercentDoneInThousandNotation();
/*     */   }
/*     */   
/*     */   public int getOutgoingRequestCount() {
/* 268 */     return this.delegate.getOutgoingRequestCount();
/*     */   }
/*     */   
/*     */   public int[] getOutgoingRequestedPieceNumbers()
/*     */   {
/* 273 */     return this.delegate.getOutgoingRequestedPieceNumbers();
/*     */   }
/*     */   
/*     */   public int getPercentDoneInThousandNotation()
/*     */   {
/* 278 */     return this.delegate.getPercentDoneInThousandNotation();
/*     */   }
/*     */   
/*     */   public String getClient()
/*     */   {
/* 283 */     return this.delegate.getClient();
/*     */   }
/*     */   
/*     */   public boolean isOptimisticUnchoke()
/*     */   {
/* 288 */     return this.delegate.isOptimisticUnchoke();
/*     */   }
/*     */   
/*     */   public void setOptimisticUnchoke(boolean is_optimistic) {
/* 292 */     this.delegate.setOptimisticUnchoke(is_optimistic);
/*     */   }
/*     */   
/*     */ 
/*     */   public void initialize()
/*     */   {
/* 298 */     throw new RuntimeException("not supported");
/*     */   }
/*     */   
/*     */ 
/*     */   public List getExpiredRequests()
/*     */   {
/* 304 */     throw new RuntimeException("not supported");
/*     */   }
/*     */   
/*     */ 
/*     */   public List getRequests()
/*     */   {
/* 310 */     throw new RuntimeException("not supported");
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNumberOfRequests()
/*     */   {
/* 316 */     throw new RuntimeException("not supported");
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMaximumNumberOfRequests()
/*     */   {
/* 322 */     throw new RuntimeException("not supported");
/*     */   }
/*     */   
/*     */ 
/*     */   public int[] getPriorityOffsets()
/*     */   {
/* 328 */     throw new RuntimeException("not supported");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean requestAllocationStarts(int[] base_priorities)
/*     */   {
/* 335 */     throw new RuntimeException("not supported");
/*     */   }
/*     */   
/*     */ 
/*     */   public void requestAllocationComplete()
/*     */   {
/* 341 */     throw new RuntimeException("not supported");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void cancelRequest(PeerReadRequest request)
/*     */   {
/* 348 */     throw new RuntimeException("not supported");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean addRequest(PeerReadRequest request)
/*     */   {
/* 356 */     throw new RuntimeException("not supported");
/*     */   }
/*     */   
/*     */ 
/*     */   private void createRGListeners()
/*     */   {
/* 362 */     this.up_rg_listener = new UtilitiesImpl.PluginLimitedRateGroupListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void disabledChanged(UtilitiesImpl.PluginLimitedRateGroup group, boolean is_disabled)
/*     */       {
/*     */ 
/*     */ 
/* 370 */         if (PeerImpl.this.closed)
/*     */         {
/* 372 */           group.removeListener(this);
/*     */         }
/*     */         
/* 375 */         PeerImpl.this.delegate.setUploadDisabled(group, is_disabled);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void sync(UtilitiesImpl.PluginLimitedRateGroup group, boolean is_disabled)
/*     */       {
/* 383 */         if (PeerImpl.this.closed)
/*     */         {
/* 385 */           group.removeListener(this);
/*     */         }
/*     */         
/*     */       }
/* 389 */     };
/* 390 */     this.down_rg_listener = new UtilitiesImpl.PluginLimitedRateGroupListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void disabledChanged(UtilitiesImpl.PluginLimitedRateGroup group, boolean is_disabled)
/*     */       {
/*     */ 
/*     */ 
/* 398 */         if (PeerImpl.this.closed)
/*     */         {
/* 400 */           group.removeListener(this);
/*     */         }
/*     */         
/* 403 */         PeerImpl.this.delegate.setDownloadDisabled(group, is_disabled);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void sync(UtilitiesImpl.PluginLimitedRateGroup group, boolean is_disabled)
/*     */       {
/* 411 */         if (PeerImpl.this.closed)
/*     */         {
/* 413 */           group.removeListener(this);
/*     */         }
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addRateLimiter(RateLimiter limiter, boolean is_upload)
/*     */   {
/* 423 */     synchronized (this)
/*     */     {
/* 425 */       if (this.closed)
/*     */       {
/* 427 */         return;
/*     */       }
/*     */       
/* 430 */       UtilitiesImpl.PluginLimitedRateGroup wrapped_limiter = UtilitiesImpl.wrapLimiter(limiter, true);
/*     */       
/* 432 */       if (this.up_rg_listener == null)
/*     */       {
/* 434 */         createRGListeners();
/*     */       }
/*     */       
/* 437 */       if (is_upload)
/*     */       {
/* 439 */         wrapped_limiter.addListener(this.up_rg_listener);
/*     */       }
/*     */       else
/*     */       {
/* 443 */         wrapped_limiter.addListener(this.down_rg_listener);
/*     */       }
/*     */       
/* 446 */       this.delegate.addRateLimiter(wrapped_limiter, is_upload);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeRateLimiter(RateLimiter limiter, boolean is_upload)
/*     */   {
/* 455 */     synchronized (this)
/*     */     {
/* 457 */       UtilitiesImpl.PluginLimitedRateGroup wrapped_limiter = UtilitiesImpl.wrapLimiter(limiter, true);
/*     */       
/* 459 */       if (this.up_rg_listener != null)
/*     */       {
/* 461 */         if (is_upload)
/*     */         {
/* 463 */           wrapped_limiter.removeListener(this.up_rg_listener);
/*     */         }
/*     */         else
/*     */         {
/* 467 */           wrapped_limiter.removeListener(this.down_rg_listener);
/*     */         }
/*     */       }
/*     */       
/* 471 */       this.delegate.removeRateLimiter(wrapped_limiter, is_upload);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public RateLimiter[] getRateLimiters(boolean is_upload)
/*     */   {
/* 479 */     LimitedRateGroup[] limiters = this.delegate.getRateLimiters(is_upload);
/*     */     
/* 481 */     RateLimiter[] result = new RateLimiter[limiters.length];
/*     */     
/* 483 */     int pos = 0;
/*     */     
/* 485 */     for (LimitedRateGroup l : limiters)
/*     */     {
/* 487 */       if ((l instanceof UtilitiesImpl.PluginLimitedRateGroup))
/*     */       {
/* 489 */         result[(pos++)] = UtilitiesImpl.unwrapLmiter((UtilitiesImpl.PluginLimitedRateGroup)l);
/*     */       }
/*     */     }
/*     */     
/* 493 */     if (pos == result.length)
/*     */     {
/* 495 */       return result;
/*     */     }
/*     */     
/* 498 */     RateLimiter[] result_mod = new RateLimiter[pos];
/*     */     
/* 500 */     System.arraycopy(result, 0, result_mod, 0, pos);
/*     */     
/* 502 */     return result_mod;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void close(String reason, boolean closedOnError, boolean attemptReconnect)
/*     */   {
/* 511 */     this.manager.removePeer(this, reason);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int readBytes(int max)
/*     */   {
/* 518 */     throw new RuntimeException("not supported");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int writeBytes(int max)
/*     */   {
/* 525 */     throw new RuntimeException("not supported");
/*     */   }
/*     */   
/*     */ 
/*     */   protected void closed()
/*     */   {
/* 531 */     synchronized (this)
/*     */     {
/* 533 */       this.closed = true;
/*     */       
/* 535 */       if (this.up_rg_listener != null)
/*     */       {
/*     */ 
/*     */ 
/* 539 */         LimitedRateGroup[] limiters = this.delegate.getRateLimiters(true);
/*     */         
/* 541 */         for (LimitedRateGroup l : limiters)
/*     */         {
/* 543 */           if ((l instanceof UtilitiesImpl.PluginLimitedRateGroup))
/*     */           {
/* 545 */             ((UtilitiesImpl.PluginLimitedRateGroup)l).removeListener(this.up_rg_listener);
/*     */           }
/*     */           
/* 548 */           this.delegate.removeRateLimiter(l, true);
/*     */         }
/*     */         
/* 551 */         limiters = this.delegate.getRateLimiters(false);
/*     */         
/* 553 */         for (LimitedRateGroup l : limiters)
/*     */         {
/* 555 */           if ((l instanceof UtilitiesImpl.PluginLimitedRateGroup))
/*     */           {
/* 557 */             ((UtilitiesImpl.PluginLimitedRateGroup)l).removeListener(this.down_rg_listener);
/*     */           }
/*     */           
/* 560 */           this.delegate.removeRateLimiter(l, false);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 565 */     if ((this.delegate instanceof PeerForeignDelegate))
/*     */     {
/* 567 */       ((PeerForeignDelegate)this.delegate).stop();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPercentDoneOfCurrentIncomingRequest()
/*     */   {
/* 574 */     return this.delegate.getPercentDoneOfCurrentIncomingRequest();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPercentDoneOfCurrentOutgoingRequest()
/*     */   {
/* 580 */     return this.delegate.getPercentDoneOfCurrentOutgoingRequest();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(final PeerListener l)
/*     */   {
/* 587 */     PEPeerListener core_listener = new PEPeerListener()
/*     */     {
/*     */ 
/*     */       public void stateChanged(PEPeer peer, int new_state)
/*     */       {
/*     */ 
/*     */         try
/*     */         {
/*     */ 
/* 596 */           l.stateChanged(new_state);
/*     */         }
/*     */         catch (Throwable e) {
/* 599 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void sentBadChunk(PEPeer peer, int piece_num, int total_bad_chunks)
/*     */       {
/*     */         try
/*     */         {
/* 610 */           l.sentBadChunk(piece_num, total_bad_chunks);
/*     */         }
/*     */         catch (Throwable e) {
/* 613 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void addAvailability(PEPeer peer, BitFlags peerHavePieces) {}
/*     */       
/*     */ 
/*     */ 
/*     */       public void removeAvailability(PEPeer peer, BitFlags peerHavePieces) {}
/* 625 */     };
/* 626 */     this.delegate.addListener(core_listener);
/*     */     
/* 628 */     synchronized (this)
/*     */     {
/* 630 */       if (this.peer_listeners == null)
/*     */       {
/* 632 */         this.peer_listeners = new HashMap();
/*     */       }
/*     */       
/* 635 */       this.peer_listeners.put(l, core_listener);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeListener(PeerListener l)
/*     */   {
/* 644 */     PEPeerListener core_listener = null;
/*     */     
/* 646 */     synchronized (this)
/*     */     {
/* 648 */       if (this.peer_listeners != null)
/*     */       {
/* 650 */         core_listener = (PEPeerListener)this.peer_listeners.remove(l);
/*     */       }
/*     */     }
/*     */     
/* 654 */     if (core_listener != null)
/*     */     {
/* 656 */       this.delegate.removeListener(core_listener);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(final PeerListener2 l)
/*     */   {
/* 664 */     PEPeerListener core_listener = new PEPeerListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void stateChanged(PEPeer peer, int new_state)
/*     */       {
/*     */ 
/*     */ 
/* 672 */         fireEvent(1, new Integer(new_state));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void sentBadChunk(PEPeer peer, int piece_num, int total_bad_chunks)
/*     */       {
/* 681 */         fireEvent(2, new Integer[] { new Integer(piece_num), new Integer(total_bad_chunks) });
/*     */       }
/*     */       
/*     */       public void addAvailability(PEPeer peer, BitFlags peerHavePieces)
/*     */       {
/* 686 */         fireEvent(3, peerHavePieces.flags);
/*     */       }
/*     */       
/*     */       public void removeAvailability(PEPeer peer, BitFlags peerHavePieces)
/*     */       {
/* 691 */         fireEvent(4, peerHavePieces.flags);
/*     */       }
/*     */       
/*     */ 
/*     */       protected void fireEvent(final int type, final Object data)
/*     */       {
/*     */         try
/*     */         {
/* 699 */           l.eventOccurred(new PeerEvent()
/*     */           {
/*     */ 
/* 702 */             public int getType() { return type; }
/* 703 */             public Object getData() { return data; }
/*     */           });
/*     */         }
/*     */         catch (Throwable e) {
/* 707 */           Debug.printStackTrace(e);
/*     */         }
/*     */         
/*     */       }
/* 711 */     };
/* 712 */     this.delegate.addListener(core_listener);
/*     */     
/* 714 */     synchronized (this)
/*     */     {
/* 716 */       if (this.peer_listeners == null)
/*     */       {
/* 718 */         this.peer_listeners = new HashMap();
/*     */       }
/*     */       
/* 721 */       this.peer_listeners.put(l, core_listener);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeListener(PeerListener2 l)
/*     */   {
/* 730 */     PEPeerListener core_listener = null;
/*     */     
/* 732 */     synchronized (this)
/*     */     {
/* 734 */       if (this.peer_listeners != null)
/*     */       {
/* 736 */         core_listener = (PEPeerListener)this.peer_listeners.remove(l);
/*     */       }
/*     */     }
/*     */     
/* 740 */     if (core_listener != null)
/*     */     {
/* 742 */       this.delegate.removeListener(core_listener);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isPriorityConnection()
/*     */   {
/* 749 */     return this.delegate.isPriorityConnection();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setPriorityConnection(boolean is_priority)
/*     */   {
/* 756 */     this.delegate.setPriorityConnection(is_priority);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setUserData(Object key, Object value)
/*     */   {
/* 764 */     this.delegate.setUserData(key, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getUserData(Object key)
/*     */   {
/* 771 */     return this.delegate.getUserData(key);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(Object other)
/*     */   {
/* 781 */     if ((other instanceof PeerImpl))
/*     */     {
/* 783 */       return this.delegate == ((PeerImpl)other).delegate;
/*     */     }
/*     */     
/* 786 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 792 */     return this.delegate.hashCode();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public PEPeer getPEPeer()
/*     */   {
/* 800 */     return this.delegate;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getRelationText()
/*     */   {
/* 809 */     return propogatedRelationText(this.delegate);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object[] getQueryableInterfaces()
/*     */   {
/* 816 */     return new Object[] { this.delegate };
/*     */   }
/*     */   
/*     */   public byte[] getHandshakeReservedBytes() {
/* 820 */     return this.delegate.getHandshakeReservedBytes();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/peers/PeerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */