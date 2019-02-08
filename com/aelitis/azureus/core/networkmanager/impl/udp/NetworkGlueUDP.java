/*     */ package com.aelitis.azureus.core.networkmanager.impl.udp;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.AEPriorityMixin;
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketHandler;
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketHandlerFactory;
/*     */ import com.aelitis.net.udp.uc.PRUDPPrimordialHandler;
/*     */ import java.io.IOException;
/*     */ import java.net.DatagramPacket;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.LinkedList;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
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
/*     */ public class NetworkGlueUDP
/*     */   implements NetworkGlue, PRUDPPrimordialHandler, AEPriorityMixin
/*     */ {
/*  47 */   private static final LogIDs LOGID = LogIDs.NET;
/*     */   
/*     */   private final NetworkGlueListener listener;
/*     */   
/*     */   private PRUDPPacketHandler handler;
/*     */   
/*  53 */   final LinkedList msg_queue = new LinkedList();
/*  54 */   final AESemaphore msg_queue_sem = new AESemaphore("NetworkGlueUDP");
/*  55 */   final AESemaphore msg_queue_slot_sem = new AESemaphore("NetworkGlueUDP", 128);
/*     */   
/*     */   private long total_packets_received;
/*     */   
/*     */   private long total_bytes_received;
/*     */   
/*     */   private long total_packets_sent;
/*     */   private long total_bytes_sent;
/*     */   
/*     */   protected NetworkGlueUDP(NetworkGlueListener _listener)
/*     */   {
/*  66 */     this.listener = _listener;
/*     */     
/*  68 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "UDP.Listen.Port", "UDP.Listen.Port.Enable" }, new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String name)
/*     */       {
/*     */ 
/*     */ 
/*  76 */         boolean enabled = COConfigurationManager.getBooleanParameter("UDP.Listen.Port.Enable");
/*     */         
/*  78 */         if (enabled)
/*     */         {
/*  80 */           int port = COConfigurationManager.getIntParameter("UDP.Listen.Port");
/*     */           
/*  82 */           if ((NetworkGlueUDP.this.handler == null) || (port != NetworkGlueUDP.this.handler.getPort()))
/*     */           {
/*  84 */             if (NetworkGlueUDP.this.handler != null)
/*     */             {
/*  86 */               Logger.log(new LogEvent(NetworkGlueUDP.LOGID, "Deactivating UDP listener on port " + NetworkGlueUDP.this.handler.getPort()));
/*     */               
/*  88 */               NetworkGlueUDP.this.handler.removePrimordialHandler(NetworkGlueUDP.this);
/*     */             }
/*     */             
/*  91 */             Logger.log(new LogEvent(NetworkGlueUDP.LOGID, "Activating UDP listener on port " + port));
/*     */             
/*  93 */             NetworkGlueUDP.this.handler = PRUDPPacketHandlerFactory.getHandler(port);
/*     */             
/*  95 */             NetworkGlueUDP.this.handler.addPrimordialHandler(NetworkGlueUDP.this);
/*     */           }
/*     */           
/*     */         }
/*  99 */         else if (NetworkGlueUDP.this.handler != null)
/*     */         {
/* 101 */           Logger.log(new LogEvent(NetworkGlueUDP.LOGID, "Deactivating UDP listener on port " + NetworkGlueUDP.this.handler.getPort()));
/*     */           
/* 103 */           NetworkGlueUDP.this.handler.removePrimordialHandler(NetworkGlueUDP.this);
/*     */         }
/*     */         
/*     */       }
/*     */       
/* 108 */     });
/* 109 */     new AEThread("NetworkGlueUDP", true)
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/*     */         for (;;)
/*     */         {
/* 117 */           InetSocketAddress target_address = null;
/* 118 */           byte[] data = null;
/*     */           
/* 120 */           NetworkGlueUDP.this.msg_queue_sem.reserve();
/*     */           
/* 122 */           synchronized (NetworkGlueUDP.this.msg_queue)
/*     */           {
/* 124 */             Object[] entry = (Object[])NetworkGlueUDP.this.msg_queue.removeFirst();
/*     */             
/* 126 */             target_address = (InetSocketAddress)entry[0];
/* 127 */             data = (byte[])entry[1];
/*     */           }
/*     */           
/* 130 */           NetworkGlueUDP.this.msg_queue_slot_sem.release();
/*     */           
/* 132 */           NetworkGlueUDP.access$208(NetworkGlueUDP.this);
/* 133 */           NetworkGlueUDP.access$314(NetworkGlueUDP.this, data.length);
/*     */           try
/*     */           {
/* 136 */             NetworkGlueUDP.this.handler.primordialSend(data, target_address);
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             try
/*     */             {
/* 145 */               Thread.sleep(3L);
/*     */             }
/*     */             catch (Throwable e) {}
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 140 */             Logger.log(new LogEvent(NetworkGlueUDP.LOGID, "Primordial UDP send failed: " + Debug.getNestedExceptionMessage(e)));
/*     */           }
/*     */           finally
/*     */           {
/*     */             try {
/* 145 */               Thread.sleep(3L);
/*     */             }
/*     */             catch (Throwable e) {}
/*     */           }
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getPriority()
/*     */   {
/* 159 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean packetReceived(DatagramPacket packet)
/*     */   {
/* 166 */     if (packet.getLength() >= 12)
/*     */     {
/* 168 */       byte[] data = packet.getData();
/*     */       
/*     */ 
/*     */ 
/* 172 */       if ((((data[0] & 0xFF) != 0) || ((data[1] & 0xFF) != 0) || ((data[2] & 0xF8) != 0)) && (((data[8] & 0xFF) != 0) || ((data[9] & 0xFF) != 0) || ((data[10] & 0xF8) != 0)))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 180 */         this.total_packets_received += 1L;
/* 181 */         this.total_bytes_received += packet.getLength();
/*     */         
/* 183 */         this.listener.receive(this.handler.getPort(), new InetSocketAddress(packet.getAddress(), packet.getPort()), packet.getData(), packet.getLength());
/*     */         
/*     */ 
/*     */ 
/* 187 */         return true;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 193 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int send(int local_port, InetSocketAddress target, byte[] data)
/*     */     throws IOException
/*     */   {
/* 204 */     this.msg_queue_slot_sem.reserve();
/*     */     
/* 206 */     synchronized (this.msg_queue)
/*     */     {
/* 208 */       this.msg_queue.add(new Object[] { target, data });
/*     */     }
/*     */     
/* 211 */     this.msg_queue_sem.release();
/*     */     
/* 213 */     return data.length;
/*     */   }
/*     */   
/*     */ 
/*     */   public long[] getStats()
/*     */   {
/* 219 */     return new long[] { this.total_packets_sent, this.total_bytes_sent, this.total_packets_received, this.total_bytes_received };
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/udp/NetworkGlueUDP.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */