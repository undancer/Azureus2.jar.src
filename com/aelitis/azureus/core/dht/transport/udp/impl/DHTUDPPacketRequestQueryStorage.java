/*     */ package com.aelitis.azureus.core.dht.transport.udp.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.transport.udp.impl.packethandler.DHTUDPPacketNetworkHandler;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DHTUDPPacketRequestQueryStorage
/*     */   extends DHTUDPPacketRequest
/*     */ {
/*     */   protected static final int SPACE = 1354;
/*     */   private int header_length;
/*     */   private List<Object[]> keys;
/*     */   
/*     */   public DHTUDPPacketRequestQueryStorage(DHTTransportUDPImpl _transport, long _connection_id, DHTTransportUDPContactImpl _local_contact, DHTTransportUDPContactImpl _remote_contact)
/*     */   {
/*  54 */     super(_transport, 1038, _connection_id, _local_contact, _remote_contact);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DHTUDPPacketRequestQueryStorage(DHTUDPPacketNetworkHandler network_handler, DataInputStream is, long con_id, int trans_id)
/*     */     throws IOException
/*     */   {
/*  66 */     super(network_handler, is, 1038, con_id, trans_id);
/*     */     
/*  68 */     this.header_length = (is.readByte() & 0xFF);
/*     */     
/*  70 */     int num_keys = is.readShort();
/*     */     
/*  72 */     this.keys = new ArrayList(num_keys);
/*     */     
/*  74 */     for (int i = 0; i < num_keys; i++)
/*     */     {
/*  76 */       int prefix_length = is.readByte() & 0xFF;
/*     */       
/*  78 */       byte[] prefix = new byte[prefix_length];
/*     */       
/*  80 */       is.read(prefix);
/*     */       
/*  82 */       short num_suffixes = is.readShort();
/*     */       
/*  84 */       List<byte[]> suffixes = new ArrayList(num_suffixes);
/*     */       
/*  86 */       this.keys.add(new Object[] { prefix, suffixes });
/*     */       
/*  88 */       int suffix_length = this.header_length - prefix_length;
/*     */       
/*  90 */       for (int j = 0; j < num_suffixes; j++)
/*     */       {
/*  92 */         byte[] suffix = new byte[suffix_length];
/*     */         
/*  94 */         is.read(suffix);
/*     */         
/*  96 */         suffixes.add(suffix);
/*     */       }
/*     */     }
/*     */     
/* 100 */     super.postDeserialise(is);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialise(DataOutputStream os)
/*     */     throws IOException
/*     */   {
/* 109 */     super.serialise(os);
/*     */     
/* 111 */     os.writeByte(this.header_length & 0xFF);
/*     */     
/* 113 */     os.writeShort(this.keys.size());
/*     */     
/*     */ 
/*     */ 
/* 117 */     for (Object[] entry : this.keys)
/*     */     {
/* 119 */       byte[] prefix = (byte[])entry[0];
/*     */       
/* 121 */       os.writeByte(prefix.length);
/*     */       
/* 123 */       os.write(prefix);
/*     */       
/* 125 */       List<byte[]> suffixes = (List)entry[1];
/*     */       
/* 127 */       os.writeShort(suffixes.size());
/*     */       
/* 129 */       for (byte[] suffix : suffixes)
/*     */       {
/* 131 */         os.write(suffix);
/*     */       }
/*     */     }
/*     */     
/* 135 */     super.postSerialise(os);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setDetails(int _header_length, List<Object[]> _keys)
/*     */   {
/* 143 */     this.header_length = _header_length;
/* 144 */     this.keys = _keys;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getHeaderLength()
/*     */   {
/* 150 */     return this.header_length;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected List<Object[]> getKeys()
/*     */   {
/* 157 */     return this.keys;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 163 */     return super.getString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/DHTUDPPacketRequestQueryStorage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */