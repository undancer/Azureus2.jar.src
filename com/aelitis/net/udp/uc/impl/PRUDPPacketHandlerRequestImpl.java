/*     */ package com.aelitis.net.udp.uc.impl;
/*     */ 
/*     */ import com.aelitis.net.udp.uc.PRUDPPacket;
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketHandlerException;
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketHandlerRequest;
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketReceiver;
/*     */ import java.net.InetSocketAddress;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
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
/*     */ public class PRUDPPacketHandlerRequestImpl
/*     */   implements PRUDPPacketHandlerRequest
/*     */ {
/*  43 */   private AESemaphore sem = new AESemaphore("PRUDPPacketHandlerRequest");
/*     */   
/*     */   private long timeout;
/*     */   
/*     */   private PRUDPPacketReceiver receiver;
/*     */   
/*     */   private PRUDPPacketHandlerException exception;
/*     */   
/*     */   private PRUDPPacket reply;
/*     */   
/*     */   private long send_time;
/*     */   
/*     */   private long reply_time;
/*     */   
/*     */   protected PRUDPPacketHandlerRequestImpl(PRUDPPacketReceiver _receiver, long _timeout)
/*     */   {
/*  59 */     this.receiver = _receiver;
/*  60 */     this.timeout = _timeout;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void sent()
/*     */   {
/*  66 */     this.send_time = SystemTime.getCurrentTime();
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getSendTime()
/*     */   {
/*  72 */     return this.send_time;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getTimeout()
/*     */   {
/*  78 */     return this.timeout;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getElapsedTime()
/*     */   {
/*  84 */     if ((this.send_time == 0L) || (this.reply_time == 0L))
/*     */     {
/*  86 */       return -1L;
/*     */     }
/*     */     
/*  89 */     long res = this.reply_time - this.send_time;
/*     */     
/*  91 */     if (res < 0L)
/*     */     {
/*  93 */       res = 0L;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  98 */     if (res == 0L)
/*     */     {
/* 100 */       res = 12L;
/*     */     }
/*     */     
/* 103 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setReply(PRUDPPacket packet, InetSocketAddress originator, long receive_time)
/*     */   {
/* 112 */     if (this.reply == null)
/*     */     {
/* 114 */       this.reply_time = receive_time;
/*     */       
/* 116 */       this.reply = packet;
/*     */     }
/*     */     else
/*     */     {
/* 120 */       packet.setPreviousPacket(this.reply);
/*     */       
/* 122 */       this.reply = packet;
/*     */     }
/*     */     
/* 125 */     if (!packet.hasContinuation())
/*     */     {
/* 127 */       this.sem.release();
/*     */     }
/*     */     
/* 130 */     if (this.receiver != null)
/*     */     {
/* 132 */       this.receiver.packetReceived(this, packet, originator);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setException(PRUDPPacketHandlerException e)
/*     */   {
/* 143 */     if (this.reply == null)
/*     */     {
/* 145 */       this.reply_time = SystemTime.getCurrentTime();
/*     */       
/* 147 */       this.exception = e;
/*     */     }
/*     */     
/* 150 */     this.sem.release();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 158 */     if (this.receiver != null)
/*     */     {
/* 160 */       this.receiver.error(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected PRUDPPacket getReply()
/*     */     throws PRUDPPacketHandlerException
/*     */   {
/* 169 */     this.sem.reserve();
/*     */     
/* 171 */     if (this.exception != null)
/*     */     {
/* 173 */       throw this.exception;
/*     */     }
/*     */     
/* 176 */     return this.reply;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerRequestImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */