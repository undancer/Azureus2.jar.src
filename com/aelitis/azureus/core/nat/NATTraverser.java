/*     */ package com.aelitis.azureus.core.nat;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.dht.DHT;
/*     */ import com.aelitis.azureus.core.dht.nat.DHTNATPuncher;
/*     */ import com.aelitis.azureus.core.dht.nat.DHTNATPuncherAdapter;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*     */ import com.aelitis.azureus.plugins.dht.DHTPlugin;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.ThreadPool;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
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
/*     */ public class NATTraverser
/*     */   implements DHTNATPuncherAdapter
/*     */ {
/*     */   public static final int TRAVERSE_REASON_PEER_DATA = 1;
/*     */   public static final int TRAVERSE_REASON_GENERIC_MESSAGING = 2;
/*     */   public static final int TRAVERSE_REASON_PAIR_TUNNEL = 3;
/*     */   private static final int MAX_QUEUE_SIZE = 128;
/*     */   private final AzureusCore core;
/*     */   private DHTNATPuncher puncher;
/*  52 */   private final ThreadPool thread_pool = new ThreadPool("NATTraverser", 16, true);
/*     */   
/*  54 */   private final Map handlers = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */   public NATTraverser(AzureusCore _core)
/*     */   {
/*  60 */     this.core = _core;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void registerHandler(NATTraversalHandler handler)
/*     */   {
/*  67 */     synchronized (this.handlers)
/*     */     {
/*  69 */       this.handlers.put(new Integer(handler.getType()), handler);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public NATTraversal attemptTraversal(final NATTraversalHandler handler, final InetSocketAddress target, final Map request, boolean sync, final NATTraversalObserver listener)
/*     */   {
/*  81 */     final NATTraversal traversal = new NATTraversal()
/*     */     {
/*     */       private boolean cancelled;
/*     */       
/*     */ 
/*     */ 
/*     */       public void cancel()
/*     */       {
/*  89 */         this.cancelled = true;
/*     */       }
/*     */       
/*     */ 
/*     */       public boolean isCancelled()
/*     */       {
/*  95 */         return this.cancelled;
/*     */       }
/*     */     };
/*     */     
/*  99 */     if (sync)
/*     */     {
/* 101 */       syncTraverse(handler, target, request, listener);
/*     */ 
/*     */ 
/*     */     }
/* 105 */     else if (this.thread_pool.getQueueSize() >= 128)
/*     */     {
/* 107 */       Debug.out("NATTraversal queue full");
/*     */       
/* 109 */       listener.failed(2);
/*     */     }
/*     */     else
/*     */     {
/* 113 */       this.thread_pool.run(new AERunnable()
/*     */       {
/*     */ 
/*     */         public void runSupport()
/*     */         {
/*     */ 
/* 119 */           if (traversal.isCancelled())
/*     */           {
/* 121 */             listener.failed(3);
/*     */           }
/*     */           else
/*     */           {
/* 125 */             NATTraverser.this.syncTraverse(handler, target, request, listener);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */ 
/* 132 */     return traversal;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void syncTraverse(NATTraversalHandler handler, InetSocketAddress target, Map request, NATTraversalObserver listener)
/*     */   {
/*     */     try
/*     */     {
/* 143 */       int type = handler.getType();
/*     */       
/* 145 */       synchronized (this)
/*     */       {
/* 147 */         if (this.puncher == null)
/*     */         {
/* 149 */           if (!PluginCoreUtils.isInitialisationComplete())
/*     */           {
/* 151 */             listener.failed(new Exception("NAT traversal failed, initialisation not complete"));
/*     */             
/* 153 */             return;
/*     */           }
/*     */           
/* 156 */           PluginInterface dht_pi = this.core.getPluginManager().getPluginInterfaceByClass(DHTPlugin.class);
/*     */           
/*     */ 
/* 159 */           if (dht_pi != null)
/*     */           {
/* 161 */             DHTPlugin dht_plugin = (DHTPlugin)dht_pi.getPlugin();
/*     */             
/* 163 */             if (dht_plugin.isEnabled())
/*     */             {
/* 165 */               DHT dht = dht_plugin.getDHT(0);
/*     */               
/* 167 */               if (dht == null)
/*     */               {
/* 169 */                 dht = dht_plugin.getDHT(1);
/*     */               }
/*     */               
/* 172 */               if (dht != null)
/*     */               {
/* 174 */                 this.puncher = dht.getNATPuncher();
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 180 */         if (this.puncher == null)
/*     */         {
/* 182 */           listener.disabled();
/*     */           
/* 184 */           return;
/*     */         }
/*     */       }
/*     */       
/* 188 */       if (request == null)
/*     */       {
/* 190 */         request = new HashMap();
/*     */       }
/*     */       
/* 193 */       request.put("_travreas", new Long(type));
/*     */       
/* 195 */       InetSocketAddress[] target_a = { target };
/*     */       
/* 197 */       DHTTransportContact[] rendezvous_used = { null };
/*     */       
/* 199 */       Map reply = this.puncher.punch(handler.getName(), target_a, rendezvous_used, request);
/*     */       
/* 201 */       if (reply == null)
/*     */       {
/* 203 */         if (rendezvous_used[0] == null)
/*     */         {
/* 205 */           listener.failed(1);
/*     */         }
/*     */         else
/*     */         {
/* 209 */           listener.failed(new Exception("NAT traversal failed"));
/*     */         }
/*     */         
/*     */       }
/*     */       else {
/* 214 */         listener.succeeded(rendezvous_used[0].getAddress(), target_a[0], reply);
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 218 */       listener.failed(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map sendMessage(NATTraversalHandler handler, InetSocketAddress rendezvous, InetSocketAddress target, Map message)
/*     */     throws NATTraversalException
/*     */   {
/* 231 */     if (this.puncher == null)
/*     */     {
/* 233 */       throw new NATTraversalException("Puncher unavailable");
/*     */     }
/*     */     
/* 236 */     message.put("_travreas", new Long(handler.getType()));
/*     */     
/* 238 */     Map reply = this.puncher.sendMessage(rendezvous, target, message);
/*     */     
/* 240 */     if (reply == null)
/*     */     {
/* 242 */       throw new NATTraversalException("Send message failed");
/*     */     }
/*     */     
/* 245 */     return reply;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map getClientData(InetSocketAddress originator, Map originator_data)
/*     */   {
/* 253 */     Long type = (Long)originator_data.get("_travreas");
/*     */     
/* 255 */     if (type != null)
/*     */     {
/*     */       NATTraversalHandler handler;
/*     */       
/* 259 */       synchronized (this.handlers)
/*     */       {
/* 261 */         handler = (NATTraversalHandler)this.handlers.get(new Integer(type.intValue()));
/*     */       }
/*     */       
/*     */ 
/* 265 */       if (handler != null)
/*     */       {
/* 267 */         return handler.process(originator, originator_data);
/*     */       }
/*     */     }
/*     */     
/* 271 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/nat/NATTraverser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */