/*     */ package org.gudy.azureus2.core3.tracker.client.impl;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerException;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerFactory.DataProvider;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerFactoryListener;
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
/*     */ public class TRTrackerAnnouncerFactoryImpl
/*     */ {
/*  40 */   protected static final List<TRTrackerAnnouncerFactoryListener> listeners = new ArrayList();
/*  41 */   protected static final List<TRTrackerAnnouncerImpl> clients = new ArrayList();
/*     */   
/*  43 */   protected static final AEMonitor class_mon = new AEMonitor("TRTrackerClientFactory");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static TRTrackerAnnouncer create(TOTorrent torrent, TRTrackerAnnouncerFactory.DataProvider provider, boolean manual)
/*     */     throws TRTrackerAnnouncerException
/*     */   {
/*  53 */     TRTrackerAnnouncerImpl client = new TRTrackerAnnouncerMuxer(torrent, provider, manual);
/*     */     
/*  55 */     if (!manual)
/*     */     {
/*     */       List<TRTrackerAnnouncerFactoryListener> listeners_copy;
/*     */       try
/*     */       {
/*  60 */         class_mon.enter();
/*     */         
/*  62 */         clients.add(client);
/*     */         
/*  64 */         listeners_copy = new ArrayList(listeners);
/*     */       }
/*     */       finally
/*     */       {
/*  68 */         class_mon.exit();
/*     */       }
/*     */       
/*  71 */       for (int i = 0; i < listeners_copy.size(); i++) {
/*     */         try
/*     */         {
/*  74 */           ((TRTrackerAnnouncerFactoryListener)listeners_copy.get(i)).clientCreated(client);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/*  78 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*  83 */     return client;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void addListener(TRTrackerAnnouncerFactoryListener l)
/*     */   {
/*     */     List<TRTrackerAnnouncerImpl> clients_copy;
/*     */     
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/*  97 */       class_mon.enter();
/*     */       
/*  99 */       listeners.add(l);
/*     */       
/* 101 */       clients_copy = new ArrayList(clients);
/*     */     }
/*     */     finally
/*     */     {
/* 105 */       class_mon.exit();
/*     */     }
/*     */     
/* 108 */     for (int i = 0; i < clients_copy.size(); i++) {
/*     */       try
/*     */       {
/* 111 */         l.clientCreated((TRTrackerAnnouncer)clients_copy.get(i));
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 115 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static void removeListener(TRTrackerAnnouncerFactoryListener l)
/*     */   {
/*     */     try
/*     */     {
/* 125 */       class_mon.enter();
/*     */       
/* 127 */       listeners.remove(l);
/*     */     }
/*     */     finally
/*     */     {
/* 131 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void destroy(TRTrackerAnnouncer client)
/*     */   {
/* 139 */     if (!client.isManual())
/*     */     {
/*     */       List<TRTrackerAnnouncerFactoryListener> listeners_copy;
/*     */       try
/*     */       {
/* 144 */         class_mon.enter();
/*     */         
/* 146 */         clients.remove(client);
/*     */         
/* 148 */         listeners_copy = new ArrayList(listeners);
/*     */       }
/*     */       finally
/*     */       {
/* 152 */         class_mon.exit();
/*     */       }
/*     */       
/* 155 */       for (int i = 0; i < listeners_copy.size(); i++) {
/*     */         try
/*     */         {
/* 158 */           ((TRTrackerAnnouncerFactoryListener)listeners_copy.get(i)).clientDestroyed(client);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 162 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/client/impl/TRTrackerAnnouncerFactoryImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */