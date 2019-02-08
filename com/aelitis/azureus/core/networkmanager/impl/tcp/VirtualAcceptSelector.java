/*     */ package com.aelitis.azureus.core.networkmanager.impl.tcp;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualChannelSelector;
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualChannelSelector.VirtualAcceptSelectorListener;
/*     */ import java.io.IOException;
/*     */ import java.nio.channels.ServerSocketChannel;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
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
/*     */ public class VirtualAcceptSelector
/*     */ {
/*  39 */   private static final VirtualAcceptSelector singleton = new VirtualAcceptSelector();
/*     */   
/*     */ 
/*     */   public static VirtualAcceptSelector getSingleton()
/*     */   {
/*  44 */     return singleton;
/*     */   }
/*     */   
/*  47 */   private static final LogIDs LOGID = LogIDs.NWMAN;
/*     */   
/*  49 */   private final VirtualChannelSelector accept_selector = new VirtualChannelSelector("Accepter", 16, false);
/*     */   
/*     */ 
/*     */ 
/*     */   protected VirtualAcceptSelector()
/*     */   {
/*  55 */     AEThread select_thread = new AEThread("Accept Selector")
/*     */     {
/*     */       public void runSupport()
/*     */       {
/*     */         try
/*     */         {
/*     */           for (;;) {
/*  62 */             VirtualAcceptSelector.this.accept_selector.select(50L);
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {
/*  66 */           Debug.printStackTrace(e);
/*     */         }
/*     */         
/*     */       }
/*     */       
/*  71 */     };
/*  72 */     select_thread.setDaemon(true);
/*  73 */     select_thread.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void register(ServerSocketChannel channel, final AcceptListener listener)
/*     */   {
/*  81 */     this.accept_selector.register(channel, new VirtualChannelSelector.VirtualAcceptSelectorListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public boolean selectSuccess(VirtualChannelSelector selector, ServerSocketChannel sc, Object attachment)
/*     */       {
/*     */ 
/*     */ 
/*     */         try
/*     */         {
/*     */ 
/*  92 */           SocketChannel new_channel = sc.accept();
/*     */           
/*  94 */           if (new_channel == null)
/*     */           {
/*  96 */             return false;
/*     */           }
/*     */           try
/*     */           {
/* 100 */             new_channel.configureBlocking(false);
/*     */           }
/*     */           catch (IOException e)
/*     */           {
/* 104 */             new_channel.close();
/*     */             
/* 106 */             throw e;
/*     */           }
/*     */           
/* 109 */           listener.newConnectionAccepted(new_channel);
/*     */           
/* 111 */           return true;
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/* 115 */           Debug.printStackTrace(e);
/*     */         }
/* 117 */         return true;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 128 */       public void selectFailure(VirtualChannelSelector selector, ServerSocketChannel sc, Object attachment, Throwable msg) { Debug.printStackTrace(msg); } }, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void cancel(ServerSocketChannel channel)
/*     */   {
/* 138 */     this.accept_selector.cancel(channel);
/*     */   }
/*     */   
/*     */   public static abstract interface AcceptListener
/*     */   {
/*     */     public abstract void newConnectionAccepted(SocketChannel paramSocketChannel);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/tcp/VirtualAcceptSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */