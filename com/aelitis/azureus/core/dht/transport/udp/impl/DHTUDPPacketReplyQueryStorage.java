/*     */ package com.aelitis.azureus.core.dht.transport.udp.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*     */ import com.aelitis.azureus.core.dht.transport.udp.impl.packethandler.DHTUDPPacketNetworkHandler;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
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
/*     */ public class DHTUDPPacketReplyQueryStorage
/*     */   extends DHTUDPPacketReply
/*     */ {
/*     */   private int random_id;
/*     */   private int header_length;
/*     */   private List<byte[]> response;
/*     */   
/*     */   public DHTUDPPacketReplyQueryStorage(DHTTransportUDPImpl transport, DHTUDPPacketRequestQueryStorage request, DHTTransportContact local_contact, DHTTransportContact remote_contact)
/*     */   {
/*  53 */     super(transport, 1039, request, local_contact, remote_contact);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DHTUDPPacketReplyQueryStorage(DHTUDPPacketNetworkHandler network_handler, InetSocketAddress originator, DataInputStream is, int trans_id)
/*     */     throws IOException
/*     */   {
/*  65 */     super(network_handler, originator, is, 1039, trans_id);
/*     */     
/*  67 */     short size = is.readShort();
/*     */     
/*  69 */     this.response = new ArrayList(size);
/*     */     
/*  71 */     if (size > 0)
/*     */     {
/*  73 */       this.header_length = (is.readByte() & 0xFF);
/*     */       
/*  75 */       byte[] bitmap = new byte[size + 0];
/*     */       
/*  77 */       is.read(bitmap);
/*     */       
/*  79 */       int pos = 0;
/*     */       
/*  81 */       int current = 0;
/*     */       
/*  83 */       for (int i = 0; i < size; i++)
/*     */       {
/*  85 */         if (i % 8 == 0)
/*     */         {
/*  87 */           current = bitmap[(pos++)] & 0xFF;
/*     */         }
/*     */         
/*  90 */         if ((current & 0x80) != 0)
/*     */         {
/*  92 */           byte[] x = new byte[this.header_length];
/*     */           
/*  94 */           is.read(x);
/*     */           
/*  96 */           this.response.add(x);
/*     */         }
/*     */         else
/*     */         {
/* 100 */           this.response.add(null);
/*     */         }
/*     */         
/* 103 */         current <<= 1;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialise(DataOutputStream os)
/*     */     throws IOException
/*     */   {
/* 114 */     super.serialise(os);
/*     */     
/* 116 */     int size = this.response.size();
/*     */     
/* 118 */     os.writeShort(size);
/*     */     
/* 120 */     if (size > 0)
/*     */     {
/* 122 */       os.writeByte(this.header_length);
/*     */       
/* 124 */       byte[] bitmap = new byte[size + 0];
/*     */       
/* 126 */       int bitmap_pos = 0;
/* 127 */       int current_byte = 0;
/* 128 */       int pos = 0;
/*     */       
/* 130 */       for (byte[] x : this.response)
/*     */       {
/* 132 */         current_byte <<= 1;
/*     */         
/* 134 */         if (x != null)
/*     */         {
/* 136 */           current_byte++;
/*     */         }
/*     */         
/* 139 */         if (pos % 8 == 7)
/*     */         {
/* 141 */           bitmap[(bitmap_pos++)] = ((byte)current_byte);
/*     */           
/* 143 */           current_byte = 0;
/*     */         }
/*     */         
/* 146 */         pos++;
/*     */       }
/*     */       
/* 149 */       if (pos % 8 != 0)
/*     */       {
/* 151 */         bitmap[(bitmap_pos++)] = ((byte)(current_byte << 8 - pos % 8));
/*     */       }
/*     */       
/* 154 */       os.write(bitmap);
/*     */       
/* 156 */       for (byte[] x : this.response)
/*     */       {
/* 158 */         if (x != null)
/*     */         {
/* 160 */           os.write(x);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setRandomID(int id)
/*     */   {
/* 170 */     this.random_id = id;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getRandomID()
/*     */   {
/* 176 */     return this.random_id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setResponse(int _header_length, List<byte[]> _response)
/*     */   {
/* 184 */     this.header_length = _header_length;
/* 185 */     this.response = _response;
/*     */   }
/*     */   
/*     */ 
/*     */   protected List<byte[]> getResponse()
/*     */   {
/* 191 */     return this.response;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/DHTUDPPacketReplyQueryStorage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */