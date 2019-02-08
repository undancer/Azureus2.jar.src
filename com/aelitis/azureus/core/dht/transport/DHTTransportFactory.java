/*    */ package com.aelitis.azureus.core.dht.transport;
/*    */ 
/*    */ import com.aelitis.azureus.core.dht.DHTLogger;
/*    */ import com.aelitis.azureus.core.dht.transport.loopback.DHTTransportLoopbackImpl;
/*    */ import com.aelitis.azureus.core.dht.transport.udp.DHTTransportUDP;
/*    */ import com.aelitis.azureus.core.dht.transport.udp.impl.DHTTransportUDPImpl;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DHTTransportFactory
/*    */ {
/*    */   public static DHTTransport createLoopback(int id_byte_num)
/*    */   {
/* 40 */     return new DHTTransportLoopbackImpl(id_byte_num);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static DHTTransportUDP createUDP(byte protocol_version, int network, boolean v6, String ip, String default_ip, int port, int max_fails_for_live, int max_fails_for_unknown, long timeout, int send_delay, int receive_delay, boolean bootstrap_node, boolean reachable, DHTLogger logger)
/*    */     throws DHTTransportException
/*    */   {
/* 62 */     return new DHTTransportUDPImpl(protocol_version, network, v6, ip, default_ip, port, max_fails_for_live, max_fails_for_unknown, timeout, send_delay, receive_delay, bootstrap_node, reachable, logger);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/DHTTransportFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */