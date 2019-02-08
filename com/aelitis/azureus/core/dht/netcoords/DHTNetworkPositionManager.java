/*     */ package com.aelitis.azureus.core.dht.netcoords;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.DHTStorageAdapter;
/*     */ import com.aelitis.azureus.core.dht.impl.DHTLog;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
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
/*     */ public class DHTNetworkPositionManager
/*     */ {
/*  41 */   private static DHTNetworkPositionProvider[] providers = new DHTNetworkPositionProvider[0];
/*  42 */   private static final Object providers_lock = new Object();
/*     */   
/*  44 */   private static DHTStorageAdapter storage_adapter = null;
/*     */   
/*  46 */   private static final CopyOnWriteList<DHTNetworkPositionProviderListener> provider_listeners = new CopyOnWriteList();
/*     */   
/*     */   private static volatile CopyOnWriteList<DHTNetworkPositionListener> position_listeners;
/*  49 */   private static final DHTNetworkPosition[] NP_EMPTY_ARRAY = new DHTNetworkPosition[0];
/*     */   
/*     */ 
/*     */ 
/*     */   public static void initialise(DHTStorageAdapter adapter)
/*     */   {
/*  55 */     synchronized (providers_lock)
/*     */     {
/*  57 */       if (storage_adapter == null)
/*     */       {
/*  59 */         storage_adapter = adapter;
/*     */         
/*  61 */         for (int i = 0; i < providers.length; i++)
/*     */         {
/*  63 */           DHTNetworkPositionProvider provider = providers[i];
/*     */           try
/*     */           {
/*  66 */             startUp(provider);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/*  70 */             Debug.printStackTrace(e);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static void startUp(DHTNetworkPositionProvider provider)
/*     */   {
/*  81 */     byte[] data = null;
/*     */     
/*  83 */     if (storage_adapter != null)
/*     */     {
/*  85 */       data = storage_adapter.getStorageForKey("NPP:" + provider.getPositionType());
/*     */     }
/*     */     
/*  88 */     if (data == null)
/*     */     {
/*  90 */       data = new byte[0];
/*     */     }
/*     */     try
/*     */     {
/*  94 */       provider.startUp(new DataInputStream(new ByteArrayInputStream(data)));
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  98 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static void shutDown(DHTNetworkPositionProvider provider)
/*     */   {
/*     */     try
/*     */     {
/* 107 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*     */       
/* 109 */       DataOutputStream dos = new DataOutputStream(baos);
/*     */       
/* 111 */       provider.shutDown(dos);
/*     */       
/* 113 */       dos.flush();
/*     */       
/* 115 */       byte[] data = baos.toByteArray();
/*     */       
/* 117 */       storage_adapter.setStorageForKey("NPP:" + provider.getPositionType(), data);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 121 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void destroy(DHTStorageAdapter adapter)
/*     */   {
/* 129 */     synchronized (providers_lock)
/*     */     {
/* 131 */       if (storage_adapter == adapter)
/*     */       {
/* 133 */         for (int i = 0; i < providers.length; i++)
/*     */         {
/* 135 */           shutDown(providers[i]);
/*     */         }
/*     */         
/* 138 */         storage_adapter = null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static DHTNetworkPositionProviderInstance registerProvider(DHTNetworkPositionProvider provider)
/*     */   {
/* 147 */     boolean fire_added = false;
/*     */     
/* 149 */     synchronized (providers_lock)
/*     */     {
/* 151 */       boolean found = false;
/* 152 */       DHTNetworkPositionProvider type_found = null;
/*     */       
/* 154 */       for (DHTNetworkPositionProvider p : providers)
/*     */       {
/* 156 */         if (p == provider)
/*     */         {
/* 158 */           found = true;
/*     */           
/* 160 */           break;
/*     */         }
/* 162 */         if (p.getPositionType() == provider.getPositionType())
/*     */         {
/* 164 */           type_found = p;
/*     */         }
/*     */       }
/*     */       
/* 168 */       if (!found)
/*     */       {
/* 170 */         if (type_found != null)
/*     */         {
/* 172 */           Debug.out("Registration of " + provider + " found previous provider for same position type, removing it");
/*     */           
/* 174 */           unregisterProviderSupport(type_found);
/*     */         }
/*     */         
/* 177 */         DHTNetworkPositionProvider[] new_providers = new DHTNetworkPositionProvider[providers.length + 1];
/*     */         
/* 179 */         System.arraycopy(providers, 0, new_providers, 0, providers.length);
/*     */         
/* 181 */         new_providers[providers.length] = provider;
/*     */         
/* 183 */         providers = new_providers;
/*     */         
/* 185 */         if (storage_adapter != null)
/*     */         {
/* 187 */           startUp(provider);
/*     */         }
/*     */         
/* 190 */         fire_added = true;
/*     */       }
/*     */     }
/*     */     
/* 194 */     if (fire_added)
/*     */     {
/* 196 */       for (DHTNetworkPositionProviderListener l : provider_listeners)
/*     */       {
/*     */         try
/*     */         {
/* 200 */           l.providerAdded(provider);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 204 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 209 */     new DHTNetworkPositionProviderInstance()
/*     */     {
/*     */ 
/*     */       public void log(String log)
/*     */       {
/*     */ 
/* 215 */         DHTLog.log("NetPos " + this.val$provider.getPositionType() + ": " + log);
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void unregisterProvider(DHTNetworkPositionProvider provider)
/*     */   {
/* 224 */     if (unregisterProviderSupport(provider))
/*     */     {
/* 226 */       for (DHTNetworkPositionProviderListener l : provider_listeners)
/*     */       {
/*     */         try
/*     */         {
/* 230 */           l.providerRemoved(provider);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 234 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static boolean unregisterProviderSupport(DHTNetworkPositionProvider provider)
/*     */   {
/* 244 */     boolean removed = false;
/*     */     
/* 246 */     synchronized (providers_lock)
/*     */     {
/* 248 */       if (providers.length == 0)
/*     */       {
/* 250 */         return false;
/*     */       }
/*     */       
/* 253 */       DHTNetworkPositionProvider[] new_providers = new DHTNetworkPositionProvider[providers.length - 1];
/*     */       
/* 255 */       int pos = 0;
/*     */       
/* 257 */       for (int i = 0; i < providers.length; i++)
/*     */       {
/* 259 */         if (providers[i] == provider)
/*     */         {
/* 261 */           if (storage_adapter != null)
/*     */           {
/* 263 */             shutDown(provider);
/*     */           }
/*     */         }
/*     */         else {
/* 267 */           new_providers[(pos++)] = providers[i];
/*     */         }
/*     */       }
/* 270 */       if (pos == new_providers.length)
/*     */       {
/* 272 */         providers = new_providers;
/*     */         
/* 274 */         removed = true;
/*     */       }
/*     */     }
/*     */     
/* 278 */     return removed;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static DHTNetworkPositionProvider getProvider(byte type)
/*     */   {
/* 285 */     synchronized (providers_lock)
/*     */     {
/* 287 */       for (int i = 0; i < providers.length; i++)
/*     */       {
/* 289 */         if (providers[i].getPositionType() == type)
/*     */         {
/* 291 */           return providers[i];
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 296 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public static DHTNetworkPosition[] getLocalPositions()
/*     */   {
/* 302 */     DHTNetworkPositionProvider[] prov = providers;
/*     */     
/* 304 */     List<DHTNetworkPosition> res = new ArrayList();
/*     */     
/* 306 */     for (int i = 0; i < prov.length; i++) {
/*     */       try
/*     */       {
/* 309 */         DHTNetworkPosition pos = prov[i].getLocalPosition();
/*     */         
/* 311 */         if (pos != null)
/*     */         {
/* 313 */           res.add(pos);
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 317 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     
/* 321 */     return (DHTNetworkPosition[])res.toArray(new DHTNetworkPosition[res.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static DHTNetworkPosition getBestLocalPosition()
/*     */   {
/* 328 */     DHTNetworkPosition best_position = null;
/*     */     
/* 330 */     DHTNetworkPosition[] positions = getLocalPositions();
/*     */     
/* 332 */     byte best_provider = 0;
/*     */     
/* 334 */     for (int i = 0; i < positions.length; i++)
/*     */     {
/* 336 */       DHTNetworkPosition position = positions[i];
/*     */       
/* 338 */       int type = position.getPositionType();
/*     */       
/* 340 */       if (type > best_provider)
/*     */       {
/* 342 */         best_position = position;
/*     */       }
/*     */     }
/*     */     
/* 346 */     return best_position;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DHTNetworkPosition[] createPositions(byte[] ID, boolean is_local)
/*     */   {
/* 354 */     DHTNetworkPositionProvider[] prov = providers;
/*     */     
/* 356 */     if (prov.length == 0)
/*     */     {
/* 358 */       return NP_EMPTY_ARRAY;
/*     */     }
/*     */     
/* 361 */     DHTNetworkPosition[] res = new DHTNetworkPosition[prov.length];
/*     */     
/* 363 */     int skipped = 0;
/*     */     
/* 365 */     for (int i = 0; i < res.length; i++) {
/*     */       try
/*     */       {
/* 368 */         res[i] = prov[i].create(ID, is_local);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 372 */         Debug.printStackTrace(e);
/*     */         
/* 374 */         skipped++;
/*     */       }
/*     */     }
/*     */     
/* 378 */     if (skipped > 0)
/*     */     {
/* 380 */       DHTNetworkPosition[] x = new DHTNetworkPosition[res.length - skipped];
/*     */       
/* 382 */       int pos = 0;
/*     */       
/* 384 */       for (int i = 0; i < res.length; i++)
/*     */       {
/* 386 */         if (res[i] != null)
/*     */         {
/* 388 */           x[(pos++)] = res[i];
/*     */         }
/*     */       }
/*     */       
/* 392 */       res = x;
/*     */       
/* 394 */       if (res.length == 0)
/*     */       {
/* 396 */         Debug.out("hmm");
/*     */       }
/*     */     }
/*     */     
/* 400 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static float estimateRTT(DHTNetworkPosition[] p1s, DHTNetworkPosition[] p2s)
/*     */   {
/* 409 */     byte best_provider = 0;
/*     */     
/* 411 */     float best_result = NaN.0F;
/*     */     
/* 413 */     for (int i = 0; i < p1s.length; i++)
/*     */     {
/* 415 */       DHTNetworkPosition p1 = p1s[i];
/*     */       
/* 417 */       byte p1_type = p1.getPositionType();
/*     */       
/* 419 */       for (int j = 0; j < p2s.length; j++)
/*     */       {
/* 421 */         DHTNetworkPosition p2 = p2s[j];
/*     */         
/* 423 */         if (p1_type == p2.getPositionType()) {
/*     */           try
/*     */           {
/* 426 */             float f = p1.estimateRTT(p2);
/*     */             
/* 428 */             if (!Float.isNaN(f))
/*     */             {
/* 430 */               if (p1_type > best_provider)
/*     */               {
/* 432 */                 best_result = f;
/* 433 */                 best_provider = p1_type;
/*     */               }
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {
/* 438 */             Debug.printStackTrace(e);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 446 */     return best_result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void update(DHTNetworkPosition[] local_positions, byte[] remote_id, DHTNetworkPosition[] remote_positions, float rtt)
/*     */   {
/* 456 */     for (int i = 0; i < local_positions.length; i++)
/*     */     {
/* 458 */       DHTNetworkPosition p1 = local_positions[i];
/*     */       
/* 460 */       for (int j = 0; j < remote_positions.length; j++)
/*     */       {
/* 462 */         DHTNetworkPosition p2 = remote_positions[j];
/*     */         
/* 464 */         if (p1.getPositionType() == p2.getPositionType()) {
/*     */           try
/*     */           {
/* 467 */             p1.update(remote_id, p2, rtt);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 471 */             Debug.printStackTrace(e);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte[] serialisePosition(DHTNetworkPosition pos)
/*     */     throws IOException
/*     */   {
/* 486 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*     */     
/* 488 */     DataOutputStream dos = new DataOutputStream(baos);
/*     */     
/* 490 */     dos.writeByte(1);
/* 491 */     dos.writeByte(pos.getPositionType());
/*     */     
/* 493 */     pos.serialise(dos);
/*     */     
/* 495 */     dos.close();
/*     */     
/* 497 */     return baos.toByteArray();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DHTNetworkPosition deserialisePosition(InetAddress originator, byte[] bytes)
/*     */     throws IOException
/*     */   {
/* 507 */     ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
/*     */     
/* 509 */     DataInputStream dis = new DataInputStream(bais);
/*     */     
/* 511 */     dis.readByte();
/*     */     
/* 513 */     byte position_type = dis.readByte();
/*     */     
/* 515 */     return deserialise(originator, position_type, dis);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DHTNetworkPosition deserialise(InetAddress originator, byte position_type, DataInputStream is)
/*     */     throws IOException
/*     */   {
/* 526 */     DHTNetworkPositionProvider[] prov = providers;
/*     */     
/* 528 */     is.mark(512);
/*     */     
/* 530 */     for (int i = 0; i < prov.length; i++)
/*     */     {
/* 532 */       if (prov[i].getPositionType() == position_type)
/*     */       {
/* 534 */         DHTNetworkPositionProvider provider = prov[i];
/*     */         try
/*     */         {
/* 537 */           DHTNetworkPosition np = provider.deserialisePosition(is);
/*     */           
/* 539 */           CopyOnWriteList<DHTNetworkPositionListener> listeners = position_listeners;
/*     */           
/* 541 */           if (listeners != null)
/*     */           {
/* 543 */             Iterator<DHTNetworkPositionListener> it = listeners.iterator();
/*     */             
/* 545 */             while (it.hasNext()) {
/*     */               try
/*     */               {
/* 548 */                 ((DHTNetworkPositionListener)it.next()).positionFound(provider, originator, np);
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 552 */                 Debug.printStackTrace(e);
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 557 */           return np;
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 561 */           Debug.printStackTrace(e);
/*     */           
/* 563 */           is.reset();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 570 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void addPositionListener(DHTNetworkPositionListener listener)
/*     */   {
/* 577 */     synchronized (DHTNetworkPositionManager.class)
/*     */     {
/* 579 */       if (position_listeners == null)
/*     */       {
/* 581 */         position_listeners = new CopyOnWriteList();
/*     */       }
/*     */       
/* 584 */       position_listeners.add(listener);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void removePositionListener(DHTNetworkPositionListener listener)
/*     */   {
/* 592 */     synchronized (DHTNetworkPositionManager.class)
/*     */     {
/* 594 */       if (position_listeners != null)
/*     */       {
/* 596 */         position_listeners.remove(listener);
/*     */         
/* 598 */         if (position_listeners.size() == 0)
/*     */         {
/* 600 */           position_listeners = null;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void addProviderListener(DHTNetworkPositionProviderListener listener)
/*     */   {
/* 610 */     provider_listeners.add(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void removeProviderListener(DHTNetworkPositionProviderListener listener)
/*     */   {
/* 617 */     provider_listeners.remove(listener);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/netcoords/DHTNetworkPositionManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */