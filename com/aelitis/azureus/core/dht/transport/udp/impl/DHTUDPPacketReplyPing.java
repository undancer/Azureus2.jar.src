/*     */ package com.aelitis.azureus.core.dht.transport.udp.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportAlternativeContact;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*     */ import com.aelitis.azureus.core.dht.transport.udp.impl.packethandler.DHTUDPPacketNetworkHandler;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.List;
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
/*     */ public class DHTUDPPacketReplyPing
/*     */   extends DHTUDPPacketReply
/*     */ {
/*  43 */   private static final DHTTransportAlternativeContact[] EMPTY_CONTACTS = new DHTTransportAlternativeContact[0];
/*     */   
/*  45 */   private DHTTransportAlternativeContact[] alt_contacts = EMPTY_CONTACTS;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DHTUDPPacketReplyPing(DHTTransportUDPImpl transport, DHTUDPPacketRequestPing request, DHTTransportContact local_contact, DHTTransportContact remote_contact)
/*     */   {
/*  54 */     super(transport, 1025, request, local_contact, remote_contact);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DHTUDPPacketReplyPing(DHTUDPPacketNetworkHandler network_handler, InetSocketAddress originator, DataInputStream is, int trans_id)
/*     */     throws IOException
/*     */   {
/*  66 */     super(network_handler, originator, is, 1025, trans_id);
/*     */     
/*  68 */     if (getProtocolVersion() >= 10)
/*     */     {
/*  70 */       DHTUDPUtils.deserialiseVivaldi(this, is);
/*     */     }
/*     */     
/*  73 */     if (getProtocolVersion() >= 52)
/*     */     {
/*  75 */       this.alt_contacts = DHTUDPUtils.deserialiseAltContacts(is);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialise(DataOutputStream os)
/*     */     throws IOException
/*     */   {
/*  85 */     super.serialise(os);
/*     */     
/*  87 */     if (getProtocolVersion() >= 10)
/*     */     {
/*  89 */       DHTUDPUtils.serialiseVivaldi(this, os);
/*     */     }
/*     */     
/*  92 */     if (getProtocolVersion() >= 52)
/*     */     {
/*  94 */       DHTUDPUtils.serialiseAltContacts(os, this.alt_contacts);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setAltContacts(List<DHTTransportAlternativeContact> _contacts)
/*     */   {
/* 102 */     int MAX_CONTACTS = 16;
/*     */     
/* 104 */     if (_contacts.size() < 16)
/*     */     {
/* 106 */       this.alt_contacts = ((DHTTransportAlternativeContact[])_contacts.toArray(new DHTTransportAlternativeContact[_contacts.size()]));
/*     */     }
/*     */     else
/*     */     {
/* 110 */       this.alt_contacts = new DHTTransportAlternativeContact[16];
/*     */       
/* 112 */       for (int i = 0; i < this.alt_contacts.length; i++)
/*     */       {
/* 114 */         this.alt_contacts[i] = ((DHTTransportAlternativeContact)_contacts.get(i));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected DHTTransportAlternativeContact[] getAltContacts()
/*     */   {
/* 122 */     return this.alt_contacts;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/DHTUDPPacketReplyPing.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */