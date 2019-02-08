/*     */ package com.aelitis.azureus.core.networkmanager;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.VirtualChannelSelectorImpl;
/*     */ import java.nio.channels.ServerSocketChannel;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.nio.channels.spi.AbstractSelectableChannel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map.Entry;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
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
/*     */ public class VirtualChannelSelector
/*     */ {
/*  36 */   private static final LogIDs LOGID = LogIDs.NWMAN;
/*     */   
/*     */   public static final int OP_ACCEPT = 16;
/*     */   public static final int OP_CONNECT = 8;
/*     */   public static final int OP_READ = 1;
/*     */   public static final int OP_WRITE = 4;
/*  42 */   private boolean SAFE_SELECTOR_MODE_ENABLED = COConfigurationManager.getBooleanParameter("network.tcp.enable_safe_selector_mode");
/*     */   
/*     */   private static final boolean TEST_SAFE_MODE = false;
/*     */   
/*  46 */   private static final int MAX_CHANNELS_PER_SAFE_SELECTOR = COConfigurationManager.getIntParameter("network.tcp.safe_selector_mode.chunk_size");
/*     */   
/*  48 */   private static final int MAX_SAFEMODE_SELECTORS = 20000 / MAX_CHANNELS_PER_SAFE_SELECTOR;
/*     */   
/*     */ 
/*     */   private final String name;
/*     */   
/*     */   private VirtualChannelSelectorImpl selector_impl;
/*     */   
/*     */   private volatile boolean destroyed;
/*     */   
/*     */   private HashMap<VirtualChannelSelectorImpl, ArrayList<AbstractSelectableChannel>> selectors;
/*     */   
/*     */   private HashSet<VirtualChannelSelectorImpl> selectors_keyset_cow;
/*     */   
/*     */   private AEMonitor selectors_mon;
/*     */   
/*     */   private final int op;
/*     */   
/*     */   private final boolean pause;
/*     */   
/*     */   private boolean randomise_keys;
/*     */   
/*     */ 
/*     */   public VirtualChannelSelector(String name, int interest_op, boolean pause_after_select)
/*     */   {
/*  72 */     this.name = name;
/*  73 */     this.op = interest_op;
/*  74 */     this.pause = pause_after_select;
/*     */     
/*  76 */     if (this.SAFE_SELECTOR_MODE_ENABLED) {
/*  77 */       initSafeMode();
/*     */     }
/*     */     else {
/*  80 */       this.selector_impl = new VirtualChannelSelectorImpl(this, this.op, this.pause, this.randomise_keys);
/*  81 */       this.selectors = null;
/*  82 */       this.selectors_keyset_cow = null;
/*  83 */       this.selectors_mon = null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  90 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */   private void initSafeMode()
/*     */   {
/*  96 */     if (Logger.isEnabled()) {
/*  97 */       Logger.log(new LogEvent(LOGID, "***************** SAFE SOCKET SELECTOR MODE ENABLED *****************"));
/*     */     }
/*     */     
/* 100 */     this.selector_impl = null;
/* 101 */     this.selectors = new HashMap();
/* 102 */     this.selectors_mon = new AEMonitor("VirtualChannelSelector:FM");
/* 103 */     this.selectors.put(new VirtualChannelSelectorImpl(this, this.op, this.pause, this.randomise_keys), new ArrayList());
/* 104 */     this.selectors_keyset_cow = new HashSet(this.selectors.keySet());
/*     */   }
/*     */   
/*     */   public void register(SocketChannel channel, VirtualSelectorListener listener, Object attachment)
/*     */   {
/* 109 */     registerSupport(channel, listener, attachment);
/*     */   }
/*     */   
/* 112 */   public void register(ServerSocketChannel channel, VirtualAcceptSelectorListener listener, Object attachment) { registerSupport(channel, listener, attachment); }
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
/*     */   protected void registerSupport(AbstractSelectableChannel channel, VirtualAbstractSelectorListener listener, Object attachment)
/*     */   {
/* 128 */     if (this.SAFE_SELECTOR_MODE_ENABLED) {
/* 129 */       try { this.selectors_mon.enter();
/*     */         
/* 131 */         for (Map.Entry<VirtualChannelSelectorImpl, ArrayList<AbstractSelectableChannel>> entry : this.selectors.entrySet())
/*     */         {
/* 133 */           VirtualChannelSelectorImpl sel = (VirtualChannelSelectorImpl)entry.getKey();
/* 134 */           ArrayList<AbstractSelectableChannel> channels = (ArrayList)entry.getValue();
/*     */           
/* 136 */           if (channels.size() >= MAX_CHANNELS_PER_SAFE_SELECTOR)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 142 */             Iterator<AbstractSelectableChannel> chan_it = channels.iterator();
/*     */             
/* 144 */             while (chan_it.hasNext())
/*     */             {
/* 146 */               AbstractSelectableChannel chan = (AbstractSelectableChannel)chan_it.next();
/*     */               
/* 148 */               if (!chan.isOpen())
/*     */               {
/* 150 */                 Debug.out("Selector '" + getName() + "' - removing orphaned safe channel registration");
/*     */                 
/* 152 */                 chan_it.remove();
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 157 */           if (channels.size() < MAX_CHANNELS_PER_SAFE_SELECTOR)
/*     */           {
/* 159 */             sel.register(channel, listener, attachment);
/* 160 */             channels.add(channel); return;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 169 */         if (this.selectors.size() >= MAX_SAFEMODE_SELECTORS) {
/* 170 */           String msg = "Error: MAX_SAFEMODE_SELECTORS reached [" + this.selectors.size() + "], no more socket channels can be registered. Too many peer connections.";
/* 171 */           Debug.out(msg);
/* 172 */           selectFailure(listener, channel, attachment, new Throwable(msg)); return;
/*     */         }
/*     */         
/*     */ 
/* 176 */         if (this.destroyed) {
/* 177 */           String msg = "socket registered after controller destroyed";
/* 178 */           Debug.out(msg);
/* 179 */           selectFailure(listener, channel, attachment, new Throwable(msg)); return;
/*     */         }
/*     */         
/*     */ 
/* 183 */         VirtualChannelSelectorImpl sel = new VirtualChannelSelectorImpl(this, this.op, this.pause, this.randomise_keys);
/*     */         
/* 185 */         ArrayList<AbstractSelectableChannel> chans = new ArrayList();
/*     */         
/* 187 */         this.selectors.put(sel, chans);
/*     */         
/* 189 */         sel.register(channel, listener, attachment);
/*     */         
/* 191 */         chans.add(channel);
/*     */         
/* 193 */         this.selectors_keyset_cow = new HashSet(this.selectors.keySet());
/*     */       } finally {
/* 195 */         this.selectors_mon.exit();
/*     */       }
/*     */     } else {
/* 198 */       this.selector_impl.register(channel, listener, attachment);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void pauseSelects(AbstractSelectableChannel channel)
/*     */   {
/* 209 */     if (this.SAFE_SELECTOR_MODE_ENABLED) {
/* 210 */       try { this.selectors_mon.enter();
/*     */         
/* 212 */         for (Map.Entry<VirtualChannelSelectorImpl, ArrayList<AbstractSelectableChannel>> entry : this.selectors.entrySet())
/*     */         {
/* 214 */           VirtualChannelSelectorImpl sel = (VirtualChannelSelectorImpl)entry.getKey();
/* 215 */           ArrayList<AbstractSelectableChannel> channels = (ArrayList)entry.getValue();
/*     */           
/* 217 */           if (channels.contains(channel)) {
/* 218 */             sel.pauseSelects(channel); return;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 223 */         Debug.out("pauseSelects():: channel not found!");
/*     */       } finally {
/* 225 */         this.selectors_mon.exit();
/*     */       }
/*     */     } else {
/* 228 */       this.selector_impl.pauseSelects(channel);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void resumeSelects(AbstractSelectableChannel channel)
/*     */   {
/* 239 */     if (this.SAFE_SELECTOR_MODE_ENABLED) {
/* 240 */       try { this.selectors_mon.enter();
/*     */         
/* 242 */         for (Map.Entry<VirtualChannelSelectorImpl, ArrayList<AbstractSelectableChannel>> entry : this.selectors.entrySet())
/*     */         {
/* 244 */           VirtualChannelSelectorImpl sel = (VirtualChannelSelectorImpl)entry.getKey();
/* 245 */           ArrayList<AbstractSelectableChannel> channels = (ArrayList)entry.getValue();
/*     */           
/* 247 */           if (channels.contains(channel)) {
/* 248 */             sel.resumeSelects(channel); return;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 253 */         Debug.out("resumeSelects():: channel not found!");
/*     */       } finally {
/* 255 */         this.selectors_mon.exit();
/*     */       }
/*     */     } else {
/* 258 */       this.selector_impl.resumeSelects(channel);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void cancel(AbstractSelectableChannel channel)
/*     */   {
/* 269 */     if (this.SAFE_SELECTOR_MODE_ENABLED) {
/* 270 */       try { this.selectors_mon.enter();
/*     */         
/* 272 */         for (Map.Entry<VirtualChannelSelectorImpl, ArrayList<AbstractSelectableChannel>> entry : this.selectors.entrySet())
/*     */         {
/* 274 */           VirtualChannelSelectorImpl sel = (VirtualChannelSelectorImpl)entry.getKey();
/* 275 */           ArrayList<AbstractSelectableChannel> channels = (ArrayList)entry.getValue();
/*     */           
/* 277 */           if (channels.remove(channel)) {
/* 278 */             sel.cancel(channel); return;
/*     */           }
/*     */         }
/*     */       }
/*     */       finally {
/* 283 */         this.selectors_mon.exit();
/*     */       }
/*     */       
/* 286 */     } else if (this.selector_impl != null) { this.selector_impl.cancel(channel);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setRandomiseKeys(boolean _rk)
/*     */   {
/* 294 */     this.randomise_keys = _rk;
/*     */     
/* 296 */     if (this.SAFE_SELECTOR_MODE_ENABLED) {
/* 297 */       try { this.selectors_mon.enter();
/* 298 */         for (VirtualChannelSelectorImpl sel : this.selectors.keySet()) {
/* 299 */           sel.setRandomiseKeys(this.randomise_keys);
/*     */         }
/*     */       } finally {
/* 302 */         this.selectors_mon.exit();
/*     */       }
/*     */       
/* 305 */     } else if (this.selector_impl != null) { this.selector_impl.setRandomiseKeys(this.randomise_keys);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int select(long timeout)
/*     */   {
/* 317 */     if (this.SAFE_SELECTOR_MODE_ENABLED) {
/* 318 */       boolean was_destroyed = this.destroyed;
/*     */       try
/*     */       {
/* 321 */         int count = 0;
/*     */         
/* 323 */         for (VirtualChannelSelectorImpl sel : this.selectors_keyset_cow)
/*     */         {
/* 325 */           count += sel.select(timeout);
/*     */         }
/*     */         
/* 328 */         return count;
/*     */       }
/*     */       finally
/*     */       {
/* 332 */         if (was_destroyed)
/*     */         {
/*     */           try
/*     */           {
/*     */ 
/* 337 */             this.selectors_mon.enter();
/*     */             
/* 339 */             this.selectors.clear();
/* 340 */             this.selectors_keyset_cow = new HashSet();
/*     */           }
/*     */           finally {
/* 343 */             this.selectors_mon.exit();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 349 */     return this.selector_impl.select(timeout);
/*     */   }
/*     */   
/*     */   public void destroy()
/*     */   {
/* 354 */     this.destroyed = true;
/*     */     
/* 356 */     if (this.SAFE_SELECTOR_MODE_ENABLED)
/*     */     {
/* 358 */       for (VirtualChannelSelectorImpl sel : this.selectors_keyset_cow)
/*     */       {
/* 360 */         sel.destroy();
/*     */       }
/*     */     } else {
/* 363 */       this.selector_impl.destroy();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDestroyed()
/*     */   {
/* 370 */     return this.destroyed;
/*     */   }
/*     */   
/* 373 */   public boolean isSafeSelectionModeEnabled() { return this.SAFE_SELECTOR_MODE_ENABLED; }
/*     */   
/*     */   public void enableSafeSelectionMode() {
/* 376 */     if (!this.SAFE_SELECTOR_MODE_ENABLED) {
/* 377 */       this.SAFE_SELECTOR_MODE_ENABLED = true;
/* 378 */       COConfigurationManager.setParameter("network.tcp.enable_safe_selector_mode", true);
/* 379 */       initSafeMode();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean selectSuccess(VirtualAbstractSelectorListener listener, AbstractSelectableChannel sc, Object attachment)
/*     */   {
/* 389 */     if (this.op == 16)
/*     */     {
/* 391 */       return ((VirtualAcceptSelectorListener)listener).selectSuccess(this, (ServerSocketChannel)sc, attachment);
/*     */     }
/*     */     
/* 394 */     return ((VirtualSelectorListener)listener).selectSuccess(this, (SocketChannel)sc, attachment);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void selectFailure(VirtualAbstractSelectorListener listener, AbstractSelectableChannel sc, Object attachment, Throwable msg)
/*     */   {
/* 405 */     if (this.op == 16)
/*     */     {
/* 407 */       ((VirtualAcceptSelectorListener)listener).selectFailure(this, (ServerSocketChannel)sc, attachment, msg);
/*     */     }
/*     */     else {
/* 410 */       ((VirtualSelectorListener)listener).selectFailure(this, (SocketChannel)sc, attachment, msg);
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface VirtualAbstractSelectorListener {}
/*     */   
/*     */   public static abstract interface VirtualAcceptSelectorListener
/*     */     extends VirtualChannelSelector.VirtualAbstractSelectorListener
/*     */   {
/*     */     public abstract boolean selectSuccess(VirtualChannelSelector paramVirtualChannelSelector, ServerSocketChannel paramServerSocketChannel, Object paramObject);
/*     */     
/*     */     public abstract void selectFailure(VirtualChannelSelector paramVirtualChannelSelector, ServerSocketChannel paramServerSocketChannel, Object paramObject, Throwable paramThrowable);
/*     */   }
/*     */   
/*     */   public static abstract interface VirtualSelectorListener
/*     */     extends VirtualChannelSelector.VirtualAbstractSelectorListener
/*     */   {
/*     */     public abstract boolean selectSuccess(VirtualChannelSelector paramVirtualChannelSelector, SocketChannel paramSocketChannel, Object paramObject);
/*     */     
/*     */     public abstract void selectFailure(VirtualChannelSelector paramVirtualChannelSelector, SocketChannel paramSocketChannel, Object paramObject, Throwable paramThrowable);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/VirtualChannelSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */