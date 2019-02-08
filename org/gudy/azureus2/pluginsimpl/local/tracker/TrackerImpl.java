/*     */ package org.gudy.azureus2.pluginsimpl.local.tracker;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHost;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostAuthenticationListener;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostFactory;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostListener2;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrent;
/*     */ import org.gudy.azureus2.core3.tracker.util.TRTrackerUtils;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.tracker.Tracker;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerException;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerListener;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerTorrent;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerAuthenticationAdapter;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerAuthenticationListener;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebContext;
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
/*     */ public class TrackerImpl
/*     */   extends TrackerWCHelper
/*     */   implements Tracker, TRHostListener2, TRHostAuthenticationListener
/*     */ {
/*     */   private static TrackerImpl singleton;
/*  49 */   private static AEMonitor class_mon = new AEMonitor("Tracker");
/*     */   
/*  51 */   private List listeners = new ArrayList();
/*     */   
/*     */   private TRHost host;
/*     */   
/*  55 */   private List<TrackerAuthenticationListener> auth_listeners = new ArrayList();
/*     */   
/*     */ 
/*     */   public static Tracker getSingleton()
/*     */   {
/*     */     try
/*     */     {
/*  62 */       class_mon.enter();
/*     */       
/*  64 */       if (singleton == null)
/*     */       {
/*  66 */         singleton = new TrackerImpl(TRHostFactory.getSingleton());
/*     */       }
/*     */       
/*  69 */       return singleton;
/*     */     }
/*     */     finally
/*     */     {
/*  73 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected TrackerImpl(TRHost _host)
/*     */   {
/*  81 */     setTracker(this);
/*     */     
/*  83 */     this.host = _host;
/*     */     
/*  85 */     this.host.addListener2(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  91 */     return this.host.getName();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setEnableKeepAlive(boolean enable)
/*     */   {
/*  98 */     Debug.out("Keep alive setting ignored for tracker");
/*     */   }
/*     */   
/*     */ 
/*     */   public URL[] getURLs()
/*     */   {
/* 104 */     URL[][] url_sets = TRTrackerUtils.getAnnounceURLs();
/*     */     
/* 106 */     URL[] res = new URL[url_sets.length];
/*     */     
/* 108 */     for (int i = 0; i < res.length; i++)
/*     */     {
/* 110 */       res[i] = url_sets[i][0];
/*     */     }
/*     */     
/* 113 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public InetAddress getBindIP()
/*     */   {
/* 119 */     return this.host.getBindIP();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TrackerTorrent host(Torrent _torrent, boolean _persistent)
/*     */     throws TrackerException
/*     */   {
/* 129 */     return host(_torrent, _persistent, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TrackerTorrent host(Torrent _torrent, boolean _persistent, boolean _passive)
/*     */     throws TrackerException
/*     */   {
/* 140 */     TorrentImpl torrent = (TorrentImpl)_torrent;
/*     */     try
/*     */     {
/* 143 */       return new TrackerTorrentImpl(this.host.hostTorrent(torrent.getTorrent(), _persistent, _passive));
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 147 */       throw new TrackerException("Tracker: host operation fails", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public TrackerTorrent publish(Torrent _torrent)
/*     */     throws TrackerException
/*     */   {
/* 157 */     TorrentImpl torrent = (TorrentImpl)_torrent;
/*     */     try
/*     */     {
/* 160 */       return new TrackerTorrentImpl(this.host.publishTorrent(torrent.getTorrent()));
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 164 */       throw new TrackerException("Tracker: publish operation fails", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public TrackerTorrent[] getTorrents()
/*     */   {
/* 171 */     TRHostTorrent[] hts = this.host.getTorrents();
/*     */     
/* 173 */     TrackerTorrent[] res = new TrackerTorrent[hts.length];
/*     */     
/* 175 */     for (int i = 0; i < hts.length; i++)
/*     */     {
/* 177 */       res[i] = new TrackerTorrentImpl(hts[i]);
/*     */     }
/*     */     
/* 180 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TrackerTorrent getTorrent(Torrent torrent)
/*     */   {
/* 187 */     TRHostTorrent ht = this.host.getHostTorrent(((TorrentImpl)torrent).getTorrent());
/*     */     
/* 189 */     if (ht == null)
/*     */     {
/* 191 */       return null;
/*     */     }
/*     */     
/* 194 */     return new TrackerTorrentImpl(ht);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TrackerWebContext createWebContext(int port, int protocol)
/*     */     throws TrackerException
/*     */   {
/* 204 */     return new TrackerWebContextImpl(this, null, port, protocol, null, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TrackerWebContext createWebContext(String name, int port, int protocol)
/*     */     throws TrackerException
/*     */   {
/* 215 */     return new TrackerWebContextImpl(this, name, port, protocol, null, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TrackerWebContext createWebContext(String name, int port, int protocol, InetAddress bind_ip)
/*     */     throws TrackerException
/*     */   {
/* 227 */     return new TrackerWebContextImpl(this, name, port, protocol, bind_ip, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TrackerWebContext createWebContext(String name, int port, int protocol, InetAddress bind_ip, Map<String, Object> properties)
/*     */     throws TrackerException
/*     */   {
/* 240 */     return new TrackerWebContextImpl(this, name, port, protocol, bind_ip, properties);
/*     */   }
/*     */   
/*     */ 
/*     */   public void torrentAdded(TRHostTorrent t)
/*     */   {
/*     */     try
/*     */     {
/* 248 */       this.this_mon.enter();
/*     */       
/* 250 */       for (int i = 0; i < this.listeners.size(); i++)
/*     */       {
/* 252 */         ((TrackerListener)this.listeners.get(i)).torrentAdded(new TrackerTorrentImpl(t));
/*     */       }
/*     */     }
/*     */     finally {
/* 256 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void torrentChanged(TRHostTorrent t)
/*     */   {
/* 264 */     for (int i = 0; i < this.listeners.size(); i++)
/*     */     {
/* 266 */       ((TrackerListener)this.listeners.get(i)).torrentChanged(new TrackerTorrentImpl(t));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void torrentRemoved(TRHostTorrent t)
/*     */   {
/*     */     try
/*     */     {
/* 276 */       this.this_mon.enter();
/*     */       
/* 278 */       for (int i = 0; i < this.listeners.size(); i++)
/*     */       {
/* 280 */         ((TrackerListener)this.listeners.get(i)).torrentRemoved(new TrackerTorrentImpl(t));
/*     */       }
/*     */     }
/*     */     finally {
/* 284 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(TrackerListener listener)
/*     */   {
/*     */     try
/*     */     {
/* 294 */       this.this_mon.enter();
/*     */       
/* 296 */       this.listeners.add(listener);
/*     */       
/* 298 */       TrackerTorrent[] torrents = getTorrents();
/*     */       
/* 300 */       for (int i = 0; i < torrents.length; i++)
/*     */       {
/* 302 */         listener.torrentAdded(torrents[i]);
/*     */       }
/*     */     }
/*     */     finally {
/* 306 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeListener(TrackerListener listener)
/*     */   {
/*     */     try
/*     */     {
/* 315 */       this.this_mon.enter();
/*     */       
/* 317 */       this.listeners.remove(listener);
/*     */     }
/*     */     finally
/*     */     {
/* 321 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean authenticate(String headers, URL resource, String user, String password)
/*     */   {
/* 332 */     for (int i = 0; i < this.auth_listeners.size(); i++) {
/*     */       try
/*     */       {
/* 335 */         TrackerAuthenticationListener listener = (TrackerAuthenticationListener)this.auth_listeners.get(i);
/*     */         
/*     */         boolean res;
/*     */         boolean res;
/* 339 */         if ((listener instanceof TrackerAuthenticationAdapter))
/*     */         {
/* 341 */           res = ((TrackerAuthenticationAdapter)listener).authenticate(headers, resource, user, password);
/*     */         }
/*     */         else
/*     */         {
/* 345 */           res = listener.authenticate(resource, user, password);
/*     */         }
/*     */         
/* 348 */         if (res)
/*     */         {
/* 350 */           return true;
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 354 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     
/* 358 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] authenticate(URL resource, String user)
/*     */   {
/* 366 */     for (int i = 0; i < this.auth_listeners.size(); i++) {
/*     */       try
/*     */       {
/* 369 */         byte[] res = ((TrackerAuthenticationListener)this.auth_listeners.get(i)).authenticate(resource, user);
/*     */         
/* 371 */         if (res != null)
/*     */         {
/* 373 */           return res;
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 377 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     
/* 381 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public void addAuthenticationListener(TrackerAuthenticationListener l)
/*     */   {
/*     */     try
/*     */     {
/* 389 */       this.this_mon.enter();
/*     */       
/* 391 */       this.auth_listeners.add(l);
/*     */       
/* 393 */       if (this.auth_listeners.size() == 1)
/*     */       {
/* 395 */         this.host.addAuthenticationListener(this);
/*     */       }
/*     */     }
/*     */     finally {
/* 399 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeAuthenticationListener(TrackerAuthenticationListener l)
/*     */   {
/*     */     try
/*     */     {
/* 408 */       this.this_mon.enter();
/*     */       
/* 410 */       this.auth_listeners.remove(l);
/*     */       
/* 412 */       if (this.auth_listeners.size() == 0)
/*     */       {
/* 414 */         this.host.removeAuthenticationListener(this);
/*     */       }
/*     */     }
/*     */     finally {
/* 418 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 425 */     super.destroy();
/*     */     
/* 427 */     this.auth_listeners.clear();
/*     */     
/* 429 */     this.host.removeAuthenticationListener(this);
/*     */     
/* 431 */     this.listeners.clear();
/*     */     
/* 433 */     this.host.close();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/tracker/TrackerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */