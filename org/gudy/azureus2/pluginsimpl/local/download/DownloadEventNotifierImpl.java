/*     */ package org.gudy.azureus2.pluginsimpl.local.download;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadActivationEvent;
/*     */ import org.gudy.azureus2.plugins.download.DownloadActivationListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadAnnounceResult;
/*     */ import org.gudy.azureus2.plugins.download.DownloadAttributeListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadCompletionListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadEventNotifier;
/*     */ import org.gudy.azureus2.plugins.download.DownloadListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManager;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManagerListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadPeerListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadPropertyEvent;
/*     */ import org.gudy.azureus2.plugins.download.DownloadPropertyListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadRemovalVetoException;
/*     */ import org.gudy.azureus2.plugins.download.DownloadScrapeResult;
/*     */ import org.gudy.azureus2.plugins.download.DownloadTrackerListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadWillBeRemovedListener;
/*     */ import org.gudy.azureus2.plugins.peers.PeerManager;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
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
/*     */ public class DownloadEventNotifierImpl
/*     */   implements DownloadEventNotifier
/*     */ {
/*     */   private DownloadActivationNotifier download_activation_notifier;
/*     */   private DownloadNotifier download_notifier;
/*     */   private DownloadPeerNotifier download_peer_notifier;
/*     */   private DownloadPropertyNotifier download_property_notifier;
/*     */   private DownloadTrackerNotifier download_tracker_notifier;
/*     */   private DownloadTrackerNotifier download_tracker_notifier_instant;
/*     */   private DownloadWillBeRemovedNotifier download_will_be_removed_notifier;
/*     */   private DownloadCompletionNotifier download_completion_notifier;
/*     */   private DownloadManager dm;
/*     */   private HashMap read_attribute_listeners;
/*     */   private HashMap write_attribute_listeners;
/*     */   
/*     */   public DownloadEventNotifierImpl(DownloadManager dm)
/*     */   {
/*  63 */     this.dm = dm;
/*  64 */     this.download_activation_notifier = new DownloadActivationNotifier();
/*  65 */     this.download_notifier = new DownloadNotifier();
/*  66 */     this.download_peer_notifier = new DownloadPeerNotifier();
/*  67 */     this.download_property_notifier = new DownloadPropertyNotifier();
/*  68 */     this.download_tracker_notifier = new DownloadTrackerNotifier(false);
/*  69 */     this.download_tracker_notifier_instant = new DownloadTrackerNotifier(true);
/*  70 */     this.download_will_be_removed_notifier = new DownloadWillBeRemovedNotifier();
/*  71 */     this.download_completion_notifier = new DownloadCompletionNotifier();
/*     */     
/*  73 */     this.read_attribute_listeners = new HashMap();
/*  74 */     this.write_attribute_listeners = new HashMap();
/*     */   }
/*     */   
/*     */   public void addActivationListener(DownloadActivationListener l) {
/*  78 */     this.download_activation_notifier.addListener(l);
/*     */   }
/*     */   
/*     */   public void addCompletionListener(DownloadCompletionListener l) {
/*  82 */     this.download_completion_notifier.addListener(l);
/*     */   }
/*     */   
/*     */   public void addDownloadWillBeRemovedListener(DownloadWillBeRemovedListener l) {
/*  86 */     this.download_will_be_removed_notifier.addListener(l);
/*     */   }
/*     */   
/*     */   public void addListener(DownloadListener l) {
/*  90 */     this.download_notifier.addListener(l);
/*     */   }
/*     */   
/*     */   public void addPeerListener(DownloadPeerListener l) {
/*  94 */     this.download_peer_notifier.addListener(l);
/*     */   }
/*     */   
/*     */   public void addPropertyListener(DownloadPropertyListener l) {
/*  98 */     this.download_property_notifier.addListener(l);
/*     */   }
/*     */   
/*     */   public void addTrackerListener(DownloadTrackerListener l) {
/* 102 */     this.download_tracker_notifier.addListener(l);
/*     */   }
/*     */   
/*     */   public void addTrackerListener(DownloadTrackerListener l, boolean immediateTrigger) {
/* 106 */     (immediateTrigger ? this.download_tracker_notifier_instant : this.download_tracker_notifier).addListener(l);
/*     */   }
/*     */   
/*     */   public void removeActivationListener(DownloadActivationListener l) {
/* 110 */     this.download_activation_notifier.removeListener(l);
/*     */   }
/*     */   
/*     */   public void removeCompletionListener(DownloadCompletionListener l) {
/* 114 */     this.download_completion_notifier.removeListener(l);
/*     */   }
/*     */   
/*     */   public void removeDownloadWillBeRemovedListener(DownloadWillBeRemovedListener l) {
/* 118 */     this.download_will_be_removed_notifier.removeListener(l);
/*     */   }
/*     */   
/*     */   public void removeListener(DownloadListener l) {
/* 122 */     this.download_notifier.removeListener(l);
/*     */   }
/*     */   
/*     */   public void removePeerListener(DownloadPeerListener l) {
/* 126 */     this.download_peer_notifier.removeListener(l);
/*     */   }
/*     */   
/*     */   public void removePropertyListener(DownloadPropertyListener l) {
/* 130 */     this.download_property_notifier.removeListener(l);
/*     */   }
/*     */   
/*     */   public void removeTrackerListener(DownloadTrackerListener l)
/*     */   {
/* 135 */     this.download_tracker_notifier.removeListener(l);
/* 136 */     this.download_tracker_notifier_instant.removeListener(l);
/*     */   }
/*     */   
/*     */   public void addAttributeListener(DownloadAttributeListener listener, TorrentAttribute ta, int event_type) {
/* 140 */     Map attr_map = getAttributeListenerMap(event_type);
/* 141 */     DownloadAttributeNotifier l = (DownloadAttributeNotifier)attr_map.get(ta);
/* 142 */     if (l == null) {
/* 143 */       l = new DownloadAttributeNotifier(ta, event_type);
/* 144 */       attr_map.put(ta, l);
/*     */     }
/* 146 */     l.addListener(listener);
/*     */   }
/*     */   
/*     */   public void removeAttributeListener(DownloadAttributeListener listener, TorrentAttribute ta, int event_type) {
/* 150 */     Map attr_map = getAttributeListenerMap(event_type);
/* 151 */     DownloadAttributeNotifier l = (DownloadAttributeNotifier)attr_map.get(ta);
/* 152 */     if (l == null) return;
/* 153 */     l.removeListener(listener);
/*     */   }
/*     */   
/*     */   private abstract class BaseDownloadListener implements DownloadManagerListener {
/* 157 */     protected ArrayList listeners = new ArrayList();
/*     */     private AEMonitor this_mon;
/*     */     
/*     */     private BaseDownloadListener() {
/* 161 */       this.this_mon = new AEMonitor(getClass().getName());
/*     */     }
/*     */     
/*     */     void addListener(Object o) {
/* 165 */       boolean register_with_downloads = false;
/*     */       try {
/* 167 */         this.this_mon.enter();
/* 168 */         register_with_downloads = this.listeners.isEmpty();
/* 169 */         ArrayList new_listeners = new ArrayList(this.listeners);
/* 170 */         new_listeners.add(o);
/* 171 */         this.listeners = new_listeners;
/*     */       }
/*     */       finally {
/* 174 */         this.this_mon.exit();
/*     */       }
/* 176 */       if (register_with_downloads) {
/* 177 */         DownloadEventNotifierImpl.this.dm.addListener(this, true);
/*     */       }
/*     */     }
/*     */     
/*     */     void removeListener(Object o) {
/* 182 */       boolean unregister_from_downloads = false;
/*     */       try {
/* 184 */         this.this_mon.enter();
/* 185 */         ArrayList new_listeners = new ArrayList(this.listeners);
/* 186 */         new_listeners.remove(o);
/* 187 */         this.listeners = new_listeners;
/* 188 */         unregister_from_downloads = this.listeners.isEmpty();
/*     */       }
/*     */       finally {
/* 191 */         this.this_mon.exit();
/*     */       }
/* 193 */       if (unregister_from_downloads)
/* 194 */         DownloadEventNotifierImpl.this.dm.removeListener(this, true);
/*     */     }
/*     */   }
/*     */   
/*     */   public class DownloadActivationNotifier extends DownloadEventNotifierImpl.BaseDownloadListener
/*     */     implements DownloadActivationListener {
/* 200 */     public DownloadActivationNotifier() { super(null); }
/* 201 */     public void downloadAdded(Download download) { download.addActivationListener(this); }
/* 202 */     public void downloadRemoved(Download download) { download.removeActivationListener(this); }
/*     */     
/* 204 */     public boolean activationRequested(DownloadActivationEvent event) { Iterator itr = this.listeners.iterator();
/* 205 */       while (itr.hasNext()) {
/* 206 */         try { if (((DownloadActivationListener)itr.next()).activationRequested(event)) return true; } catch (Throwable t) {}
/* 207 */         Debug.printStackTrace(t);
/*     */       }
/* 209 */       return false;
/*     */     }
/*     */   }
/*     */   
/* 213 */   public class DownloadCompletionNotifier extends DownloadEventNotifierImpl.BaseDownloadListener implements DownloadCompletionListener { public DownloadCompletionNotifier() { super(null); }
/* 214 */     public void downloadAdded(Download download) { download.addCompletionListener(this); }
/* 215 */     public void downloadRemoved(Download download) { download.removeCompletionListener(this); }
/*     */     
/* 217 */     public void onCompletion(Download download) { Iterator itr = this.listeners.iterator();
/* 218 */       while (itr.hasNext()) {
/* 219 */         try { ((DownloadCompletionListener)itr.next()).onCompletion(download); } catch (Throwable t) {}
/* 220 */         Debug.printStackTrace(t);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/* 225 */   public class DownloadNotifier extends DownloadEventNotifierImpl.BaseDownloadListener implements DownloadListener { public DownloadNotifier() { super(null); }
/* 226 */     public void downloadAdded(Download download) { download.addListener(this); }
/* 227 */     public void downloadRemoved(Download download) { download.removeListener(this); }
/*     */     
/* 229 */     public void stateChanged(Download download, int old_state, int new_state) { Iterator itr = this.listeners.iterator();
/* 230 */       while (itr.hasNext()) {
/* 231 */         try { ((DownloadListener)itr.next()).stateChanged(download, old_state, new_state); } catch (Throwable t) {}
/* 232 */         Debug.printStackTrace(t);
/*     */       }
/*     */     }
/*     */     
/* 236 */     public void positionChanged(Download download, int old_position, int new_position) { Iterator itr = this.listeners.iterator();
/* 237 */       while (itr.hasNext()) {
/* 238 */         try { ((DownloadListener)itr.next()).positionChanged(download, old_position, new_position); } catch (Throwable t) {}
/* 239 */         Debug.printStackTrace(t);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/* 244 */   public class DownloadPeerNotifier extends DownloadEventNotifierImpl.BaseDownloadListener implements DownloadPeerListener { public DownloadPeerNotifier() { super(null); }
/* 245 */     public void downloadAdded(Download download) { download.addPeerListener(this); }
/* 246 */     public void downloadRemoved(Download download) { download.removePeerListener(this); }
/*     */     
/* 248 */     public void peerManagerAdded(Download download, PeerManager peer_manager) { Iterator itr = this.listeners.iterator();
/* 249 */       while (itr.hasNext()) {
/* 250 */         try { ((DownloadPeerListener)itr.next()).peerManagerAdded(download, peer_manager); } catch (Throwable t) {}
/* 251 */         Debug.printStackTrace(t);
/*     */       }
/*     */     }
/*     */     
/* 255 */     public void peerManagerRemoved(Download download, PeerManager peer_manager) { Iterator itr = this.listeners.iterator();
/* 256 */       while (itr.hasNext()) {
/* 257 */         try { ((DownloadPeerListener)itr.next()).peerManagerRemoved(download, peer_manager); } catch (Throwable t) {}
/* 258 */         Debug.printStackTrace(t);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/* 263 */   public class DownloadPropertyNotifier extends DownloadEventNotifierImpl.BaseDownloadListener implements DownloadPropertyListener { public DownloadPropertyNotifier() { super(null); }
/* 264 */     public void downloadAdded(Download download) { download.addPropertyListener(this); }
/* 265 */     public void downloadRemoved(Download download) { download.removePropertyListener(this); }
/*     */     
/* 267 */     public void propertyChanged(Download download, DownloadPropertyEvent event) { Iterator itr = this.listeners.iterator();
/* 268 */       while (itr.hasNext()) {
/* 269 */         try { ((DownloadPropertyListener)itr.next()).propertyChanged(download, event); } catch (Throwable t) {}
/* 270 */         Debug.printStackTrace(t);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/* 275 */   public class DownloadWillBeRemovedNotifier extends DownloadEventNotifierImpl.BaseDownloadListener implements DownloadWillBeRemovedListener { public DownloadWillBeRemovedNotifier() { super(null); }
/* 276 */     public void downloadAdded(Download download) { download.addDownloadWillBeRemovedListener(this); }
/* 277 */     public void downloadRemoved(Download download) { download.removeDownloadWillBeRemovedListener(this); }
/*     */     
/* 279 */     public void downloadWillBeRemoved(Download download) throws DownloadRemovalVetoException { Iterator itr = this.listeners.iterator();
/* 280 */       while (itr.hasNext()) {
/* 281 */         try { ((DownloadWillBeRemovedListener)itr.next()).downloadWillBeRemoved(download);
/* 282 */         } catch (DownloadRemovalVetoException e) { throw e; } catch (Throwable t) {}
/* 283 */         Debug.printStackTrace(t);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public class DownloadAttributeNotifier extends DownloadEventNotifierImpl.BaseDownloadListener implements DownloadAttributeListener { private TorrentAttribute ta;
/*     */     private int event_type;
/*     */     
/* 291 */     public DownloadAttributeNotifier(TorrentAttribute ta, int event_type) { super(null);
/* 292 */       this.ta = ta;this.event_type = event_type;
/*     */     }
/*     */     
/* 295 */     public void downloadAdded(Download d) { d.addAttributeListener(this, this.ta, this.event_type); }
/* 296 */     public void downloadRemoved(Download d) { d.removeAttributeListener(this, this.ta, this.event_type); }
/*     */     
/* 298 */     public void attributeEventOccurred(Download d, TorrentAttribute ta, int event_type) { Iterator itr = this.listeners.iterator();
/* 299 */       while (itr.hasNext()) {
/* 300 */         try { ((DownloadAttributeListener)itr.next()).attributeEventOccurred(d, ta, event_type); } catch (Throwable t) {}
/* 301 */         Debug.printStackTrace(t);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public class DownloadTrackerNotifier extends DownloadEventNotifierImpl.BaseDownloadListener implements DownloadTrackerListener { private boolean instant_notify;
/*     */     
/* 308 */     public DownloadTrackerNotifier(boolean instant_notify) { super(null);this.instant_notify = instant_notify; }
/* 309 */     public void downloadAdded(Download download) { download.addTrackerListener(this, this.instant_notify); }
/* 310 */     public void downloadRemoved(Download download) { download.removeTrackerListener(this); }
/*     */     
/* 312 */     public void scrapeResult(DownloadScrapeResult result) { Iterator itr = this.listeners.iterator();
/* 313 */       while (itr.hasNext()) {
/* 314 */         try { ((DownloadTrackerListener)itr.next()).scrapeResult(result); } catch (Throwable t) {}
/* 315 */         Debug.printStackTrace(t);
/*     */       }
/*     */     }
/*     */     
/* 319 */     public void announceResult(DownloadAnnounceResult result) { Iterator itr = this.listeners.iterator();
/* 320 */       while (itr.hasNext()) {
/* 321 */         try { ((DownloadTrackerListener)itr.next()).announceResult(result); } catch (Throwable t) {}
/* 322 */         Debug.printStackTrace(t);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private Map getAttributeListenerMap(int event_type) {
/* 328 */     if (event_type == 1)
/* 329 */       return this.write_attribute_listeners;
/* 330 */     if (event_type == 2)
/* 331 */       return this.read_attribute_listeners;
/* 332 */     throw new IllegalArgumentException("invalid event type " + event_type);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/download/DownloadEventNotifierImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */