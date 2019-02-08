/*    */ package com.aelitis.azureus.core.networkmanager.impl.udp;
/*    */ 
/*    */ import com.aelitis.azureus.core.networkmanager.ProtocolEndpoint;
/*    */ import com.aelitis.azureus.core.networkmanager.TransportEndpoint;
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
/*    */ public class TransportEndpointUDP
/*    */   implements TransportEndpoint
/*    */ {
/*    */   private final ProtocolEndpoint pe;
/*    */   
/*    */   public TransportEndpointUDP(ProtocolEndpoint _pe)
/*    */   {
/* 35 */     this.pe = _pe;
/*    */   }
/*    */   
/*    */ 
/*    */   public ProtocolEndpoint getProtocolEndpoint()
/*    */   {
/* 41 */     return this.pe;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/udp/TransportEndpointUDP.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */