/*     */ package com.aelitis.azureus.core.dht.transport.udp.impl.packethandler;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.transport.udp.impl.DHTTransportUDPImpl;
/*     */ import com.aelitis.azureus.core.dht.transport.udp.impl.DHTUDPPacketRequest;
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketHandler;
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketHandlerFactory;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
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
/*     */ public class DHTUDPPacketHandlerFactory
/*     */ {
/*  36 */   private static final DHTUDPPacketHandlerFactory singleton = new DHTUDPPacketHandlerFactory();
/*     */   
/*  38 */   private final Map port_map = new HashMap();
/*     */   
/*  40 */   protected final AEMonitor this_mon = new AEMonitor("DHTUDPPacketHandlerFactory");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DHTUDPPacketHandler getHandler(DHTTransportUDPImpl transport, DHTUDPRequestHandler request_handler)
/*     */     throws DHTUDPPacketHandlerException
/*     */   {
/*  51 */     return singleton.getHandlerSupport(transport, request_handler);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DHTUDPPacketHandler getHandlerSupport(DHTTransportUDPImpl transport, DHTUDPRequestHandler request_handler)
/*     */     throws DHTUDPPacketHandlerException
/*     */   {
/*     */     try
/*     */     {
/*  62 */       this.this_mon.enter();
/*     */       
/*  64 */       int port = transport.getPort();
/*  65 */       int network = transport.getNetwork();
/*     */       
/*  67 */       Object[] port_details = (Object[])this.port_map.get(new Integer(port));
/*     */       
/*  69 */       if (port_details == null)
/*     */       {
/*  71 */         PRUDPPacketHandler packet_handler = PRUDPPacketHandlerFactory.getHandler(port, new DHTUDPPacketNetworkHandler(this, port));
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  77 */         port_details = new Object[] { packet_handler, new HashMap() };
/*     */         
/*  79 */         this.port_map.put(new Integer(port), port_details);
/*     */       }
/*     */       
/*  82 */       Map network_map = (Map)port_details[1];
/*     */       
/*  84 */       Object[] network_details = (Object[])network_map.get(new Integer(network));
/*     */       
/*  86 */       if (network_details != null)
/*     */       {
/*  88 */         throw new DHTUDPPacketHandlerException("Network already added");
/*     */       }
/*     */       
/*  91 */       DHTUDPPacketHandler ph = new DHTUDPPacketHandler(this, network, (PRUDPPacketHandler)port_details[0], request_handler);
/*     */       
/*  93 */       network_map.put(new Integer(network), new Object[] { transport, ph });
/*     */       
/*  95 */       return ph;
/*     */     }
/*     */     finally
/*     */     {
/*  99 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void destroy(DHTUDPPacketHandler handler)
/*     */   {
/* 107 */     PRUDPPacketHandler packet_handler = handler.getPacketHandler();
/*     */     
/* 109 */     int port = packet_handler.getPort();
/* 110 */     int network = handler.getNetwork();
/*     */     try
/*     */     {
/* 113 */       this.this_mon.enter();
/*     */       
/* 115 */       Object[] port_details = (Object[])this.port_map.get(new Integer(port));
/*     */       
/* 117 */       if (port_details == null) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/* 122 */       Map network_map = (Map)port_details[1];
/*     */       
/* 124 */       network_map.remove(new Integer(network));
/*     */       
/* 126 */       if (network_map.size() == 0)
/*     */       {
/* 128 */         this.port_map.remove(new Integer(port));
/*     */         try
/*     */         {
/* 131 */           packet_handler.setRequestHandler(null);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 135 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 140 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void process(int port, DHTUDPPacketRequest request)
/*     */   {
/*     */     try
/*     */     {
/* 150 */       int network = request.getNetwork();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 159 */       Object[] port_details = (Object[])this.port_map.get(new Integer(port));
/*     */       
/* 161 */       if (port_details == null)
/*     */       {
/* 163 */         throw new IOException("Port '" + port + "' not registered");
/*     */       }
/*     */       
/* 166 */       Map network_map = (Map)port_details[1];
/*     */       
/* 168 */       Object[] network_details = (Object[])network_map.get(new Integer(network));
/*     */       
/* 170 */       if (network_details == null)
/*     */       {
/* 172 */         throw new IOException("Network '" + network + "' not registered");
/*     */       }
/*     */       
/* 175 */       DHTUDPPacketHandler res = (DHTUDPPacketHandler)network_details[1];
/*     */       
/* 177 */       res.receive(request);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 181 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DHTTransportUDPImpl getTransport(int port, int network)
/*     */     throws IOException
/*     */   {
/* 192 */     Object[] port_details = (Object[])this.port_map.get(new Integer(port));
/*     */     
/* 194 */     if (port_details == null)
/*     */     {
/* 196 */       throw new IOException("Port '" + port + "' not registered");
/*     */     }
/*     */     
/* 199 */     Map network_map = (Map)port_details[1];
/*     */     
/* 201 */     Object[] network_details = (Object[])network_map.get(new Integer(network));
/*     */     
/* 203 */     if (network_details == null)
/*     */     {
/* 205 */       throw new IOException("Network '" + network + "' not registered");
/*     */     }
/*     */     
/* 208 */     return (DHTTransportUDPImpl)network_details[0];
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/packethandler/DHTUDPPacketHandlerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */