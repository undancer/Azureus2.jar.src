/*     */ package com.aelitis.azureus.core.dht.transport.udp.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*     */ import com.aelitis.azureus.core.dht.transport.udp.impl.packethandler.DHTUDPPacketNetworkHandler;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
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
/*     */ public class DHTUDPPacketReplyFindNode
/*     */   extends DHTUDPPacketReply
/*     */ {
/*     */   private DHTTransportContact[] contacts;
/*     */   private int random_id;
/*  43 */   private int node_status = -1;
/*     */   
/*     */ 
/*     */ 
/*     */   private int estimated_dht_size;
/*     */   
/*     */ 
/*     */ 
/*     */   public DHTUDPPacketReplyFindNode(DHTTransportUDPImpl transport, DHTUDPPacketRequestFindNode request, DHTTransportContact local_contact, DHTTransportContact remote_contact)
/*     */   {
/*  53 */     super(transport, 1029, request, local_contact, remote_contact);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DHTUDPPacketReplyFindNode(DHTUDPPacketNetworkHandler network_handler, InetSocketAddress originator, DataInputStream is, int trans_id)
/*     */     throws IOException
/*     */   {
/*  65 */     super(network_handler, originator, is, 1029, trans_id);
/*     */     
/*  67 */     if (getProtocolVersion() >= 7)
/*     */     {
/*  69 */       this.random_id = is.readInt();
/*     */     }
/*     */     
/*  72 */     if (getProtocolVersion() >= 12)
/*     */     {
/*  74 */       this.node_status = is.readInt();
/*     */     }
/*     */     
/*  77 */     if (getProtocolVersion() >= 13)
/*     */     {
/*  79 */       this.estimated_dht_size = is.readInt();
/*     */     }
/*     */     
/*  82 */     if (getProtocolVersion() >= 10)
/*     */     {
/*  84 */       DHTUDPUtils.deserialiseVivaldi(this, is);
/*     */     }
/*     */     
/*  87 */     this.contacts = DHTUDPUtils.deserialiseContacts(getTransport(), is);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialise(DataOutputStream os)
/*     */     throws IOException
/*     */   {
/*  96 */     super.serialise(os);
/*     */     
/*  98 */     if (getProtocolVersion() >= 7)
/*     */     {
/* 100 */       os.writeInt(this.random_id);
/*     */     }
/*     */     
/* 103 */     if (getProtocolVersion() >= 12)
/*     */     {
/* 105 */       os.writeInt(this.node_status);
/*     */     }
/*     */     
/* 108 */     if (getProtocolVersion() >= 13)
/*     */     {
/* 110 */       os.writeInt(this.estimated_dht_size);
/*     */     }
/*     */     
/* 113 */     if (getProtocolVersion() >= 10)
/*     */     {
/* 115 */       DHTUDPUtils.serialiseVivaldi(this, os);
/*     */     }
/*     */     
/* 118 */     DHTUDPUtils.serialiseContacts(os, this.contacts);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setContacts(DHTTransportContact[] _contacts)
/*     */   {
/* 125 */     this.contacts = _contacts;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setRandomID(int _random_id)
/*     */   {
/* 132 */     this.random_id = _random_id;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getRandomID()
/*     */   {
/* 138 */     return this.random_id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setNodeStatus(int ns)
/*     */   {
/* 145 */     this.node_status = ns;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getNodeStatus()
/*     */   {
/* 151 */     return this.node_status;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setEstimatedDHTSize(int s)
/*     */   {
/* 158 */     this.estimated_dht_size = s;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getEstimatedDHTSize()
/*     */   {
/* 164 */     return this.estimated_dht_size;
/*     */   }
/*     */   
/*     */ 
/*     */   protected DHTTransportContact[] getContacts()
/*     */   {
/* 170 */     return this.contacts;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 176 */     return super.getString() + ",contacts=" + (this.contacts == null ? "null" : new StringBuilder().append("").append(this.contacts.length).toString());
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/DHTUDPPacketReplyFindNode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */