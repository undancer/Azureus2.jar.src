/*     */ package org.gudy.azureus2.pluginsimpl.local.messaging;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.ConnectionEndpoint;
/*     */ import com.aelitis.azureus.core.networkmanager.ProtocolEndpoint;
/*     */ import com.aelitis.azureus.core.networkmanager.ProtocolEndpointFactory;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.ProtocolEndpointTCP;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.udp.ProtocolEndpointUDP;
/*     */ import java.net.InetSocketAddress;
/*     */ import org.gudy.azureus2.plugins.messaging.generic.GenericMessageEndpoint;
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
/*     */ public class GenericMessageEndpointImpl
/*     */   implements GenericMessageEndpoint
/*     */ {
/*     */   private ConnectionEndpoint ce;
/*     */   
/*     */   public GenericMessageEndpointImpl(ConnectionEndpoint _ce)
/*     */   {
/*  42 */     this.ce = _ce;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public GenericMessageEndpointImpl(InetSocketAddress _ne)
/*     */   {
/*  49 */     this.ce = new ConnectionEndpoint(_ne);
/*     */   }
/*     */   
/*     */ 
/*     */   public InetSocketAddress getNotionalAddress()
/*     */   {
/*  55 */     return this.ce.getNotionalAddress();
/*     */   }
/*     */   
/*     */ 
/*     */   protected ConnectionEndpoint getConnectionEndpoint()
/*     */   {
/*  61 */     return this.ce;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addTCP(InetSocketAddress target)
/*     */   {
/*  68 */     this.ce.addProtocol(ProtocolEndpointFactory.createEndpoint(1, target));
/*     */   }
/*     */   
/*     */ 
/*     */   public InetSocketAddress getTCP()
/*     */   {
/*  74 */     ProtocolEndpoint[] pes = this.ce.getProtocols();
/*     */     
/*  76 */     for (int i = 0; i < pes.length; i++)
/*     */     {
/*  78 */       if ((pes[i] instanceof ProtocolEndpointTCP))
/*     */       {
/*  80 */         return ((ProtocolEndpointTCP)pes[i]).getAddress();
/*     */       }
/*     */     }
/*     */     
/*  84 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addUDP(InetSocketAddress target)
/*     */   {
/*  91 */     this.ce.addProtocol(ProtocolEndpointFactory.createEndpoint(2, target));
/*     */   }
/*     */   
/*     */ 
/*     */   public InetSocketAddress getUDP()
/*     */   {
/*  97 */     ProtocolEndpoint[] pes = this.ce.getProtocols();
/*     */     
/*  99 */     for (int i = 0; i < pes.length; i++)
/*     */     {
/* 101 */       if ((pes[i] instanceof ProtocolEndpointUDP))
/*     */       {
/* 103 */         return ((ProtocolEndpointUDP)pes[i]).getAddress();
/*     */       }
/*     */     }
/*     */     
/* 107 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/messaging/GenericMessageEndpointImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */