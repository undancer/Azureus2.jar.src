/*     */ package com.aelitis.azureus.core.networkmanager;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.ProtocolEndpointTCP;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.udp.ProtocolEndpointUDP;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ public class ProtocolEndpointFactory
/*     */ {
/*  32 */   private static ProtocolEndpointHandler tcp_handler = null;
/*  33 */   private static ProtocolEndpointHandler udp_handler = null;
/*     */   
/*  35 */   private static final Map<Integer, ProtocolEndpointHandler> other_handlers = new HashMap();
/*     */   
/*     */   static {
/*  38 */     ProtocolEndpointTCP.register();
/*  39 */     ProtocolEndpointUDP.register();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void registerHandler(ProtocolEndpointHandler handler)
/*     */   {
/*  46 */     int type = handler.getType();
/*     */     
/*  48 */     if (type == 1)
/*     */     {
/*  50 */       tcp_handler = handler;
/*     */     }
/*  52 */     else if (type == 2)
/*     */     {
/*  54 */       udp_handler = handler;
/*     */     }
/*     */     else
/*     */     {
/*  58 */       other_handlers.put(Integer.valueOf(type), handler);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean isHandlerRegistered(int type)
/*     */   {
/*  66 */     if ((type == 1) || (type == 2))
/*     */     {
/*  68 */       return true;
/*     */     }
/*     */     
/*     */ 
/*  72 */     return other_handlers.containsKey(Integer.valueOf(type));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ProtocolEndpoint createEndpoint(int type, InetSocketAddress target)
/*     */   {
/*  81 */     switch (type) {
/*     */     case 1: 
/*  83 */       return tcp_handler.create(target);
/*     */     
/*     */     case 2: 
/*  86 */       return udp_handler.create(target);
/*     */     }
/*     */     
/*  89 */     ProtocolEndpointHandler handler = (ProtocolEndpointHandler)other_handlers.get(Integer.valueOf(type));
/*  90 */     if (handler != null) {
/*  91 */       return handler.create(target);
/*     */     }
/*  93 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ProtocolEndpoint createEndpoint(int type, ConnectionEndpoint connection_endpoint, InetSocketAddress target)
/*     */   {
/* 104 */     switch (type) {
/*     */     case 1: 
/* 106 */       return tcp_handler.create(connection_endpoint, target);
/*     */     
/*     */     case 2: 
/* 109 */       return udp_handler.create(connection_endpoint, target);
/*     */     }
/*     */     
/* 112 */     ProtocolEndpointHandler handler = (ProtocolEndpointHandler)other_handlers.get(Integer.valueOf(type));
/* 113 */     if (handler != null) {
/* 114 */       return handler.create(connection_endpoint, target);
/*     */     }
/* 116 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/ProtocolEndpointFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */