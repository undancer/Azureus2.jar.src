/*     */ package org.gudy.azureus2.pluginsimpl.local.peers;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*     */ import com.aelitis.azureus.core.peermanager.peerdb.PeerItem;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerListener;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerReadRequest;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManagerListener;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManagerListenerAdapter;
/*     */ import org.gudy.azureus2.core3.peer.PEPiece;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadException;
/*     */ import org.gudy.azureus2.plugins.peers.Peer;
/*     */ import org.gudy.azureus2.plugins.peers.PeerDescriptor;
/*     */ import org.gudy.azureus2.plugins.peers.PeerManager;
/*     */ import org.gudy.azureus2.plugins.peers.PeerManagerEvent;
/*     */ import org.gudy.azureus2.plugins.peers.PeerManagerListener;
/*     */ import org.gudy.azureus2.plugins.peers.PeerManagerListener2;
/*     */ import org.gudy.azureus2.plugins.peers.PeerManagerStats;
/*     */ import org.gudy.azureus2.plugins.peers.PeerReadRequest;
/*     */ import org.gudy.azureus2.plugins.peers.PeerStats;
/*     */ import org.gudy.azureus2.plugins.peers.Piece;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.utils.PooledByteBuffer;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*     */ import org.gudy.azureus2.pluginsimpl.local.disk.DiskManagerImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.download.DownloadManagerImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.PooledByteBufferImpl;
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
/*     */ public class PeerManagerImpl
/*     */   implements PeerManager
/*     */ {
/*  57 */   private static final String PEPEER_DATA_KEY = PeerManagerImpl.class.getName();
/*     */   
/*     */   protected PEPeerManager manager;
/*     */   
/*  61 */   protected static AEMonitor pm_map_mon = new AEMonitor("PeerManager:Map");
/*     */   
/*     */ 
/*     */   public static PeerManagerImpl getPeerManager(PEPeerManager _manager)
/*     */   {
/*     */     try
/*     */     {
/*  68 */       pm_map_mon.enter();
/*     */       
/*  70 */       PeerManagerImpl res = (PeerManagerImpl)_manager.getData("PluginPeerManager");
/*     */       
/*  72 */       if (res == null)
/*     */       {
/*  74 */         res = new PeerManagerImpl(_manager);
/*     */         
/*  76 */         _manager.setData("PluginPeerManager", res);
/*     */       }
/*     */       
/*  79 */       return res;
/*     */     }
/*     */     finally {
/*  82 */       pm_map_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*  86 */   private Map foreign_map = new HashMap();
/*     */   
/*  88 */   private Map<PeerManagerListener, PEPeerManagerListener> listener_map1 = new HashMap();
/*  89 */   private Map<PeerManagerListener2, CoreListener> listener_map2 = new HashMap();
/*     */   
/*  91 */   protected AEMonitor this_mon = new AEMonitor("PeerManager");
/*     */   
/*     */   private final DiskManagerPiece[] dm_pieces;
/*     */   
/*     */   private final PEPiece[] pe_pieces;
/*     */   
/*     */   private pieceFacade[] piece_facades;
/*     */   
/*     */   private boolean destroyed;
/*     */   
/*     */   protected PeerManagerImpl(PEPeerManager _manager)
/*     */   {
/* 103 */     this.manager = _manager;
/*     */     
/* 105 */     this.dm_pieces = _manager.getDiskManager().getPieces();
/* 106 */     this.pe_pieces = _manager.getPieces();
/*     */     
/* 108 */     this.manager.addListener(new PEPeerManagerListenerAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void peerRemoved(PEPeerManager manager, PEPeer peer)
/*     */       {
/*     */ 
/*     */ 
/* 116 */         PeerImpl dele = PeerManagerImpl.getPeerForPEPeer(peer);
/*     */         
/* 118 */         if (dele != null)
/*     */         {
/* 120 */           dele.closed();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */       public void destroyed()
/*     */       {
/* 127 */         synchronized (PeerManagerImpl.this.foreign_map)
/*     */         {
/* 129 */           PeerManagerImpl.this.destroyed = true;
/*     */           
/* 131 */           Iterator it = PeerManagerImpl.this.foreign_map.values().iterator();
/*     */           
/* 133 */           while (it.hasNext()) {
/*     */             try
/*     */             {
/* 136 */               ((PeerForeignDelegate)it.next()).stop();
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 140 */               Debug.printStackTrace(e);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public PEPeerManager getDelegate()
/*     */   {
/* 151 */     return this.manager;
/*     */   }
/*     */   
/*     */ 
/*     */   public org.gudy.azureus2.plugins.disk.DiskManager getDiskManager()
/*     */   {
/* 157 */     return new DiskManagerImpl(this.manager.getDiskManager());
/*     */   }
/*     */   
/*     */ 
/*     */   public PeerManagerStats getStats()
/*     */   {
/* 163 */     return new PeerManagerStatsImpl(this.manager);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isSeeding()
/*     */   {
/* 170 */     return this.manager.getDiskManager().getRemainingExcludingDND() == 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isSuperSeeding()
/*     */   {
/* 176 */     return this.manager.isSuperSeedMode();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Download getDownload()
/*     */     throws DownloadException
/*     */   {
/* 184 */     return DownloadManagerImpl.getDownloadStatic(this.manager.getDiskManager().getTorrent());
/*     */   }
/*     */   
/*     */ 
/*     */   public Piece[] getPieces()
/*     */   {
/* 190 */     if (this.piece_facades == null)
/*     */     {
/* 192 */       pieceFacade[] pf = new pieceFacade[this.manager.getDiskManager().getNbPieces()];
/*     */       
/* 194 */       for (int i = 0; i < pf.length; i++)
/*     */       {
/* 196 */         pf[i] = new pieceFacade(i);
/*     */       }
/*     */       
/* 199 */       this.piece_facades = pf;
/*     */     }
/*     */     
/* 202 */     return this.piece_facades;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public PeerStats createPeerStats(Peer peer)
/*     */   {
/* 209 */     PEPeer delegate = mapForeignPeer(peer);
/*     */     
/* 211 */     return new PeerStatsImpl(this, peer, this.manager.createPeerStats(delegate));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void requestComplete(PeerReadRequest request, PooledByteBuffer data, Peer sender)
/*     */   {
/* 221 */     this.manager.writeBlock(request.getPieceNumber(), request.getOffset(), ((PooledByteBufferImpl)data).getBuffer(), mapForeignPeer(sender), false);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 228 */     PeerForeignDelegate delegate = lookupForeignPeer(sender);
/*     */     
/* 230 */     if (delegate != null)
/*     */     {
/* 232 */       delegate.dataReceived();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void requestCancelled(PeerReadRequest request, Peer sender)
/*     */   {
/* 241 */     this.manager.requestCanceled((DiskManagerReadRequest)request);
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getPartitionID()
/*     */   {
/* 247 */     return this.manager.getPartitionID();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addPeer(Peer peer)
/*     */   {
/* 258 */     this.manager.addPeer(mapForeignPeer(peer));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removePeer(Peer peer)
/*     */   {
/* 265 */     this.manager.removePeer(mapForeignPeer(peer));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void removePeer(Peer peer, String reason)
/*     */   {
/* 273 */     this.manager.removePeer(mapForeignPeer(peer), reason);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addPeer(String ip_address, int tcp_port)
/*     */   {
/* 281 */     addPeer(ip_address, tcp_port, 0, NetworkManager.getCryptoRequired(0));
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
/*     */   public void addPeer(String ip_address, int tcp_port, boolean use_crypto)
/*     */   {
/* 294 */     addPeer(ip_address, tcp_port, 0, use_crypto);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addPeer(String ip_address, int tcp_port, int udp_port, boolean use_crypto)
/*     */   {
/* 304 */     addPeer(ip_address, tcp_port, udp_port, use_crypto, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addPeer(String ip_address, int tcp_port, int udp_port, boolean use_crypto, Map user_data)
/*     */   {
/* 315 */     checkIfPrivate();
/*     */     
/* 317 */     if (pluginPeerSourceEnabled())
/*     */     {
/* 319 */       this.manager.addPeer(ip_address, tcp_port, udp_port, use_crypto, user_data);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void peerDiscovered(String peer_source, String ip_address, int tcp_port, int udp_port, boolean use_crypto)
/*     */   {
/* 331 */     checkIfPrivate();
/*     */     
/* 333 */     if (this.manager.isPeerSourceEnabled(peer_source))
/*     */     {
/* 335 */       this.manager.peerDiscovered(peer_source, ip_address, tcp_port, udp_port, use_crypto);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean pluginPeerSourceEnabled()
/*     */   {
/* 342 */     if (this.manager.isPeerSourceEnabled("Plugin"))
/*     */     {
/* 344 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 348 */     Debug.out("Plugin peer source disabled for " + this.manager.getDisplayName());
/*     */     
/* 350 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void checkIfPrivate()
/*     */   {
/*     */     Download dl;
/*     */     
/*     */     try
/*     */     {
/* 360 */       dl = getDownload();
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */ 
/* 366 */       return;
/*     */     }
/*     */     
/* 369 */     Torrent t = dl.getTorrent();
/*     */     
/* 371 */     if (t != null)
/*     */     {
/* 373 */       if (TorrentUtils.isReallyPrivate(PluginCoreUtils.unwrap(t)))
/*     */       {
/* 375 */         throw new RuntimeException("Torrent is private, peer addition not permitted");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Peer[] getPeers()
/*     */   {
/* 383 */     List l = this.manager.getPeers();
/*     */     
/* 385 */     Peer[] res = new Peer[l.size()];
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 390 */     for (int i = 0; i < res.length; i++)
/*     */     {
/* 392 */       res[i] = getPeerForPEPeer((PEPeer)l.get(i));
/*     */     }
/*     */     
/* 395 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Peer[] getPeers(String address)
/*     */   {
/* 402 */     List l = this.manager.getPeers(address);
/*     */     
/* 404 */     Peer[] res = new Peer[l.size()];
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 409 */     for (int i = 0; i < res.length; i++)
/*     */     {
/* 411 */       res[i] = getPeerForPEPeer((PEPeer)l.get(i));
/*     */     }
/*     */     
/* 414 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public PeerDescriptor[] getPendingPeers()
/*     */   {
/* 421 */     return this.manager.getPendingPeers();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public PeerDescriptor[] getPendingPeers(String address)
/*     */   {
/* 428 */     return this.manager.getPendingPeers(address);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getTimeSinceConnectionEstablished(Peer peer)
/*     */   {
/* 435 */     if ((peer instanceof PeerImpl))
/*     */     {
/* 437 */       return ((PeerImpl)peer).getDelegate().getTimeSinceConnectionEstablished();
/*     */     }
/* 439 */     PeerForeignDelegate delegate = lookupForeignPeer(peer);
/*     */     
/* 441 */     if (delegate != null)
/*     */     {
/* 443 */       return delegate.getTimeSinceConnectionEstablished();
/*     */     }
/*     */     
/*     */ 
/* 447 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public PEPeer mapForeignPeer(Peer _foreign)
/*     */   {
/* 455 */     if ((_foreign instanceof PeerImpl))
/*     */     {
/* 457 */       return ((PeerImpl)_foreign).getDelegate();
/*     */     }
/*     */     
/* 460 */     synchronized (this.foreign_map)
/*     */     {
/* 462 */       PEPeer local = (PEPeer)this.foreign_map.get(_foreign);
/*     */       
/* 464 */       if ((local != null) && (local.isClosed()))
/*     */       {
/* 466 */         this.foreign_map.remove(_foreign);
/*     */         
/* 468 */         local = null;
/*     */       }
/*     */       
/* 471 */       if (local == null)
/*     */       {
/* 473 */         if (this.destroyed)
/*     */         {
/* 475 */           Debug.out("Peer added to destroyed peer manager");
/*     */           
/* 477 */           return null;
/*     */         }
/*     */         
/* 480 */         local = new PeerForeignDelegate(this, _foreign);
/*     */         
/* 482 */         _foreign.setUserData(PeerManagerImpl.class, local);
/*     */         
/* 484 */         this.foreign_map.put(_foreign, local);
/*     */       }
/*     */       
/* 487 */       return local;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected PeerForeignDelegate lookupForeignPeer(Peer _foreign)
/*     */   {
/* 495 */     return (PeerForeignDelegate)_foreign.getUserData(PeerManagerImpl.class);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public List mapForeignPeers(Peer[] _foreigns)
/*     */   {
/* 502 */     List res = new ArrayList();
/*     */     
/* 504 */     for (int i = 0; i < _foreigns.length; i++)
/*     */     {
/* 506 */       PEPeer local = mapForeignPeer(_foreigns[i]);
/*     */       
/*     */ 
/*     */ 
/* 510 */       if (!res.contains(local))
/*     */       {
/* 512 */         res.add(local);
/*     */       }
/*     */     }
/*     */     
/* 516 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static PeerImpl getPeerForPEPeer(PEPeer pe_peer)
/*     */   {
/* 523 */     PeerImpl peer = (PeerImpl)pe_peer.getData(PEPEER_DATA_KEY);
/*     */     
/* 525 */     if (peer == null)
/*     */     {
/* 527 */       peer = new PeerImpl(pe_peer);
/*     */       
/* 529 */       pe_peer.setData(PEPEER_DATA_KEY, peer);
/*     */     }
/*     */     
/* 532 */     return peer;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getUploadRateLimitBytesPerSecond()
/*     */   {
/* 538 */     return this.manager.getUploadRateLimitBytesPerSecond();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getDownloadRateLimitBytesPerSecond()
/*     */   {
/* 544 */     return this.manager.getDownloadRateLimitBytesPerSecond();
/*     */   }
/*     */   
/*     */ 
/*     */   public void addListener(final PeerManagerListener l)
/*     */   {
/*     */     try
/*     */     {
/* 552 */       this.this_mon.enter();
/*     */       
/* 554 */       final Map peer_map = new HashMap();
/*     */       
/* 556 */       PEPeerManagerListener core_listener = new PEPeerManagerListenerAdapter() {
/*     */         public void peerAdded(PEPeerManager manager, PEPeer peer) {
/* 558 */           PeerImpl pi = PeerManagerImpl.getPeerForPEPeer(peer);
/* 559 */           peer_map.put(peer, pi);
/* 560 */           l.peerAdded(PeerManagerImpl.this, pi);
/*     */         }
/*     */         
/*     */         public void peerRemoved(PEPeerManager manager, PEPeer peer) {
/* 564 */           PeerImpl pi = (PeerImpl)peer_map.remove(peer);
/*     */           
/* 566 */           if (pi != null)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 572 */             l.peerRemoved(PeerManagerImpl.this, pi);
/*     */           }
/*     */           
/*     */         }
/* 576 */       };
/* 577 */       this.listener_map1.put(l, core_listener);
/*     */       
/* 579 */       this.manager.addListener(core_listener);
/*     */     }
/*     */     finally {
/* 582 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeListener(PeerManagerListener l)
/*     */   {
/*     */     try
/*     */     {
/* 591 */       this.this_mon.enter();
/*     */       
/* 593 */       PEPeerManagerListener core_listener = (PEPeerManagerListener)this.listener_map1.remove(l);
/*     */       
/* 595 */       if (core_listener != null)
/*     */       {
/* 597 */         this.manager.removeListener(core_listener);
/*     */       }
/*     */     }
/*     */     finally {
/* 601 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void addListener(PeerManagerListener2 l)
/*     */   {
/*     */     try
/*     */     {
/* 610 */       this.this_mon.enter();
/*     */       
/* 612 */       CoreListener core_listener = new CoreListener(l, null);
/*     */       
/* 614 */       this.listener_map2.put(l, core_listener);
/*     */       
/* 616 */       this.manager.addListener(core_listener);
/*     */       
/* 618 */       this.manager.getDiskManager().addListener(core_listener);
/*     */     }
/*     */     finally {
/* 621 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeListener(PeerManagerListener2 l)
/*     */   {
/*     */     try
/*     */     {
/* 630 */       this.this_mon.enter();
/*     */       
/* 632 */       CoreListener core_listener = (CoreListener)this.listener_map2.remove(l);
/*     */       
/* 634 */       if (core_listener != null)
/*     */       {
/* 636 */         this.manager.removeListener(core_listener);
/*     */         
/* 638 */         this.manager.getDiskManager().removeListener(core_listener);
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 643 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected class pieceFacade
/*     */     implements Piece
/*     */   {
/*     */     private final int index;
/*     */     
/*     */ 
/*     */     protected pieceFacade(int _index)
/*     */     {
/* 657 */       this.index = _index;
/*     */     }
/*     */     
/*     */ 
/*     */     public int getIndex()
/*     */     {
/* 663 */       return this.index;
/*     */     }
/*     */     
/*     */ 
/*     */     public int getLength()
/*     */     {
/* 669 */       return PeerManagerImpl.this.dm_pieces[this.index].getLength();
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isDone()
/*     */     {
/* 675 */       return PeerManagerImpl.this.dm_pieces[this.index].isDone();
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isNeeded()
/*     */     {
/* 681 */       return PeerManagerImpl.this.dm_pieces[this.index].isNeeded();
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isDownloading()
/*     */     {
/* 687 */       return PeerManagerImpl.this.pe_pieces[this.index] != null;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isFullyAllocatable()
/*     */     {
/* 693 */       if (PeerManagerImpl.this.pe_pieces[this.index] != null)
/*     */       {
/* 695 */         return false;
/*     */       }
/*     */       
/* 698 */       return PeerManagerImpl.this.dm_pieces[this.index].isInteresting();
/*     */     }
/*     */     
/*     */ 
/*     */     public int getAllocatableRequestCount()
/*     */     {
/* 704 */       PEPiece pe_piece = PeerManagerImpl.this.pe_pieces[this.index];
/*     */       
/* 706 */       if (pe_piece != null)
/*     */       {
/* 708 */         return pe_piece.getNbUnrequested();
/*     */       }
/*     */       
/* 711 */       if (PeerManagerImpl.this.dm_pieces[this.index].isInteresting())
/*     */       {
/* 713 */         return PeerManagerImpl.this.dm_pieces[this.index].getNbBlocks();
/*     */       }
/*     */       
/* 716 */       return 0;
/*     */     }
/*     */     
/*     */ 
/*     */     public Peer getReservedFor()
/*     */     {
/* 722 */       PEPiece piece = PeerManagerImpl.this.pe_pieces[this.index];
/*     */       
/* 724 */       if (piece != null)
/*     */       {
/* 726 */         String ip = piece.getReservedBy();
/*     */         
/* 728 */         if (ip != null)
/*     */         {
/* 730 */           List<PEPeer> peers = PeerManagerImpl.this.manager.getPeers(ip);
/*     */           
/* 732 */           if (peers.size() > 0)
/*     */           {
/* 734 */             return PeerManagerImpl.getPeerForPEPeer((PEPeer)peers.get(0));
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 739 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void setReservedFor(Peer peer)
/*     */     {
/* 746 */       PEPiece piece = PeerManagerImpl.this.pe_pieces[this.index];
/*     */       
/* 748 */       PEPeer mapped_peer = PeerManagerImpl.this.mapForeignPeer(peer);
/*     */       
/* 750 */       if ((piece != null) && (mapped_peer != null))
/*     */       {
/* 752 */         piece.setReservedBy(peer.getIp());
/*     */         
/* 754 */         mapped_peer.addReservedPieceNumber(this.index);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private class CoreListener
/*     */     implements PEPeerManagerListener, DiskManagerListener
/*     */   {
/*     */     private PeerManagerListener2 listener;
/* 764 */     private Map<PEPeer, Peer> peer_map = new HashMap();
/*     */     
/*     */ 
/*     */ 
/*     */     private CoreListener(PeerManagerListener2 _listener)
/*     */     {
/* 770 */       this.listener = _listener;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void peerAdded(PEPeerManager manager, PEPeer peer)
/*     */     {
/* 777 */       PeerImpl pi = PeerManagerImpl.getPeerForPEPeer(peer);
/*     */       
/* 779 */       this.peer_map.put(peer, pi);
/*     */       
/* 781 */       fireEvent(1, pi, null, null);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void peerRemoved(PEPeerManager manager, PEPeer peer)
/*     */     {
/* 793 */       PeerImpl pi = (PeerImpl)this.peer_map.remove(peer);
/*     */       
/* 795 */       if (pi != null)
/*     */       {
/*     */ 
/*     */ 
/* 799 */         fireEvent(2, pi, null, null);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void peerDiscovered(PEPeerManager manager, PeerItem peer_item, PEPeer finder)
/*     */     {
/*     */       PeerImpl pi;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 815 */       if (finder != null)
/*     */       {
/* 817 */         PeerImpl pi = PeerManagerImpl.getPeerForPEPeer(finder);
/*     */         
/* 819 */         this.peer_map.put(finder, pi);
/*     */       }
/*     */       else
/*     */       {
/* 823 */         pi = null;
/*     */       }
/*     */       
/* 826 */       fireEvent(3, pi, peer_item, null);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void pieceAdded(PEPeerManager manager, PEPiece piece, PEPeer for_peer)
/*     */     {
/* 839 */       PeerImpl pi = for_peer == null ? null : PeerManagerImpl.getPeerForPEPeer(for_peer);
/*     */       
/* 841 */       fireEvent(5, pi, null, new PeerManagerImpl.pieceFacade(PeerManagerImpl.this, piece.getPieceNumber()));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void pieceRemoved(PEPeerManager manager, PEPiece piece)
/*     */     {
/* 853 */       fireEvent(6, null, null, new PeerManagerImpl.pieceFacade(PeerManagerImpl.this, piece.getPieceNumber()));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void peerSentBadData(PEPeerManager manager, PEPeer peer, int pieceNumber)
/*     */     {
/* 866 */       PeerImpl pi = PeerManagerImpl.getPeerForPEPeer(peer);
/*     */       
/* 868 */       this.peer_map.put(peer, pi);
/*     */       
/* 870 */       fireEvent(4, pi, null, new Integer(pieceNumber));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void pieceCorrupted(PEPeerManager manager, int piece_number) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void stateChanged(int oldState, int newState) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void filePriorityChanged(DiskManagerFileInfo file) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void pieceDoneChanged(DiskManagerPiece piece)
/*     */     {
/* 904 */       fireEvent(7, null, null, new PeerManagerImpl.pieceFacade(PeerManagerImpl.this, piece.getPieceNumber()));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void fileAccessModeChanged(DiskManagerFileInfo file, int old_mode, int new_mode) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     protected void fireEvent(final int type, final Peer peer, final PeerItem peer_item, final Object data)
/*     */     {
/* 926 */       this.listener.eventOccurred(new PeerManagerEvent()
/*     */       {
/*     */ 
/*     */         public PeerManager getPeerManager()
/*     */         {
/*     */ 
/* 932 */           return PeerManagerImpl.this;
/*     */         }
/*     */         
/*     */ 
/*     */         public int getType()
/*     */         {
/* 938 */           return type;
/*     */         }
/*     */         
/*     */ 
/*     */         public Peer getPeer()
/*     */         {
/* 944 */           return peer;
/*     */         }
/*     */         
/*     */ 
/*     */         public PeerDescriptor getPeerDescriptor()
/*     */         {
/* 950 */           return peer_item;
/*     */         }
/*     */         
/*     */ 
/*     */         public Object getData()
/*     */         {
/* 956 */           return data;
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */     public void destroyed() {}
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/peers/PeerManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */