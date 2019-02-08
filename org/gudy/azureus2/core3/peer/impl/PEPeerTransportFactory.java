/*     */ package org.gudy.azureus2.core3.peer.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnection;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.peer.impl.transport.PEPeerTransportProtocol;
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
/*     */ public class PEPeerTransportFactory
/*     */ {
/*  41 */   protected static final Map extension_handlers = new HashMap();
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
/*     */   public static PEPeerTransport createTransport(PEPeerControl control, String peer_source, String ip, int tcp_port, int udp_port, boolean use_tcp, boolean require_crypto_handshake, byte crypto_level, Map initial_user_data)
/*     */   {
/*  63 */     return new PEPeerTransportProtocol(control, peer_source, ip, tcp_port, udp_port, use_tcp, require_crypto_handshake, crypto_level, initial_user_data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PEPeerTransport createTransport(PEPeerControl control, String peer_source, NetworkConnection connection, Map initial_user_data)
/*     */   {
/*  76 */     return new PEPeerTransportProtocol(control, peer_source, connection, initial_user_data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void registerExtensionHandler(String protocol_name, PEPeerTransportExtensionHandler handler)
/*     */   {
/*  87 */     extension_handlers.put(protocol_name, handler);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static List createExtendedTransports(PEPeerControl manager, String protocol_name, Map details)
/*     */   {
/*  96 */     System.out.println("createExtendedTransports:" + protocol_name);
/*     */     
/*  98 */     PEPeerTransportExtensionHandler handler = (PEPeerTransportExtensionHandler)extension_handlers.get(protocol_name);
/*     */     
/* 100 */     if (handler == null)
/*     */     {
/* 102 */       System.out.println("\tNo handler");
/*     */       
/* 104 */       return new ArrayList();
/*     */     }
/*     */     
/* 107 */     return handler.handleExtension(manager, details);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/peer/impl/PEPeerTransportFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */