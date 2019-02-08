/*     */ package com.aelitis.azureus.core.networkmanager.impl.udp;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportHelper;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportHelper.selectListener;
/*     */ import java.io.IOException;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*     */ public class UDPSelector
/*     */ {
/*  36 */   private static final int POLL_FREQUENCY = COConfigurationManager.getIntParameter("network.udp.poll.time", 100);
/*     */   
/*  38 */   final List ready_set = new LinkedList();
/*  39 */   final AESemaphore ready_sem = new AESemaphore("UDPSelector");
/*     */   
/*     */ 
/*     */   private volatile boolean destroyed;
/*     */   
/*     */ 
/*     */   protected UDPSelector(final UDPConnectionManager manager)
/*     */   {
/*  47 */     new AEThread2("UDPSelector", true)
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*  52 */         boolean quit = false;
/*  53 */         long last_poll = 0L;
/*     */         
/*  55 */         while (!quit)
/*     */         {
/*  57 */           if (UDPSelector.this.destroyed)
/*     */           {
/*     */ 
/*     */ 
/*  61 */             quit = true;
/*     */           }
/*     */           
/*  64 */           long now = SystemTime.getCurrentTime();
/*     */           
/*  66 */           if ((now < last_poll) || (now - last_poll >= UDPSelector.POLL_FREQUENCY))
/*     */           {
/*  68 */             manager.poll();
/*     */             
/*  70 */             last_poll = now;
/*     */           }
/*     */           
/*  73 */           if (UDPSelector.this.ready_sem.reserve(UDPSelector.POLL_FREQUENCY / 2))
/*     */           {
/*     */             Object[] entry;
/*     */             
/*  77 */             synchronized (UDPSelector.this.ready_set)
/*     */             {
/*  79 */               if (UDPSelector.this.ready_set.size() == 0) {
/*     */                 continue;
/*     */               }
/*     */               
/*     */ 
/*  84 */               entry = (Object[])UDPSelector.this.ready_set.remove(0);
/*     */             }
/*     */             
/*     */ 
/*  88 */             TransportHelper transport = (TransportHelper)entry[0];
/*     */             
/*  90 */             TransportHelper.selectListener listener = (TransportHelper.selectListener)entry[1];
/*     */             
/*  92 */             if (listener == null)
/*     */             {
/*  94 */               Debug.out("Null listener");
/*     */             }
/*     */             else
/*     */             {
/*  98 */               Object attachment = entry[2];
/*     */               try
/*     */               {
/* 101 */                 if (entry.length == 3)
/*     */                 {
/* 103 */                   listener.selectSuccess(transport, attachment);
/*     */                 }
/*     */                 else
/*     */                 {
/* 107 */                   listener.selectFailure(transport, attachment, (Throwable)entry[3]);
/*     */                 }
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 112 */                 Debug.printStackTrace(e);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void destroy()
/*     */   {
/* 124 */     synchronized (this.ready_set)
/*     */     {
/* 126 */       this.destroyed = true;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void ready(TransportHelper transport, TransportHelper.selectListener listener, Object attachment)
/*     */     throws IOException
/*     */   {
/* 138 */     boolean removed = false;
/*     */     
/* 140 */     synchronized (this.ready_set)
/*     */     {
/* 142 */       if (this.destroyed)
/*     */       {
/* 144 */         throw new IOException("Selector has been destroyed");
/*     */       }
/*     */       
/* 147 */       Iterator it = this.ready_set.iterator();
/*     */       
/* 149 */       while (it.hasNext())
/*     */       {
/* 151 */         Object[] entry = (Object[])it.next();
/*     */         
/* 153 */         if (entry[1] == listener)
/*     */         {
/* 155 */           it.remove();
/*     */           
/* 157 */           removed = true;
/*     */           
/* 159 */           break;
/*     */         }
/*     */       }
/*     */       
/* 163 */       this.ready_set.add(new Object[] { transport, listener, attachment });
/*     */     }
/*     */     
/* 166 */     if (!removed)
/*     */     {
/* 168 */       this.ready_sem.release();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void ready(TransportHelper transport, TransportHelper.selectListener listener, Object attachment, Throwable error)
/*     */     throws IOException
/*     */   {
/* 181 */     boolean removed = false;
/*     */     
/* 183 */     synchronized (this.ready_set)
/*     */     {
/* 185 */       if (this.destroyed)
/*     */       {
/* 187 */         throw new IOException("Selector has been destroyed");
/*     */       }
/*     */       
/* 190 */       Iterator it = this.ready_set.iterator();
/*     */       
/* 192 */       while (it.hasNext())
/*     */       {
/* 194 */         Object[] entry = (Object[])it.next();
/*     */         
/* 196 */         if (entry[1] == listener)
/*     */         {
/* 198 */           it.remove();
/*     */           
/* 200 */           removed = true;
/*     */           
/* 202 */           break;
/*     */         }
/*     */       }
/*     */       
/* 206 */       this.ready_set.add(new Object[] { transport, listener, attachment, error });
/*     */     }
/*     */     
/* 209 */     if (!removed)
/*     */     {
/* 211 */       this.ready_sem.release();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void cancel(TransportHelper transport, TransportHelper.selectListener listener)
/*     */   {
/* 220 */     synchronized (this.ready_set)
/*     */     {
/* 222 */       Iterator it = this.ready_set.iterator();
/*     */       
/* 224 */       while (it.hasNext())
/*     */       {
/* 226 */         Object[] entry = (Object[])it.next();
/*     */         
/* 228 */         if ((entry[0] == transport) && (entry[1] == listener))
/*     */         {
/* 230 */           it.remove();
/*     */           
/* 232 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/udp/UDPSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */