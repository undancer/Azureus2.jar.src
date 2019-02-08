/*     */ package org.gudy.azureus2.core3.tracker.client.impl;
/*     */ 
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerResponsePeer;
/*     */ import org.gudy.azureus2.core3.util.StringInterner;
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
/*     */ public class TRTrackerAnnouncerResponsePeerImpl
/*     */   implements TRTrackerAnnouncerResponsePeer
/*     */ {
/*     */   private final String source;
/*     */   private final byte[] peer_id;
/*     */   private final String address;
/*     */   private final short tcp_port;
/*     */   private final short udp_port;
/*     */   private final short http_port;
/*     */   private final short crypto;
/*     */   private final byte az_version;
/*     */   private final short up_speed;
/*     */   
/*     */   public TRTrackerAnnouncerResponsePeerImpl(String _source, byte[] _peer_id, String _address, int _tcp_port, int _udp_port, int _http_port, short _crypto, byte _az_version, int _up_speed)
/*     */   {
/*  55 */     this.source = StringInterner.intern(_source);
/*  56 */     this.peer_id = _peer_id;
/*  57 */     this.address = StringInterner.intern(_address);
/*  58 */     this.tcp_port = ((short)_tcp_port);
/*  59 */     this.udp_port = ((short)_udp_port);
/*  60 */     this.http_port = ((short)_http_port);
/*  61 */     this.crypto = _crypto;
/*  62 */     this.az_version = _az_version;
/*  63 */     this.up_speed = ((short)_up_speed);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getSource()
/*     */   {
/*  69 */     return this.source;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getPeerID()
/*     */   {
/*  75 */     return this.peer_id;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getAddress()
/*     */   {
/*  81 */     return this.address;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPort()
/*     */   {
/*  87 */     return this.tcp_port & 0xFFFF;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getUDPPort()
/*     */   {
/*  93 */     return this.udp_port & 0xFFFF;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getHTTPPort()
/*     */   {
/*  99 */     return this.http_port & 0xFFFF;
/*     */   }
/*     */   
/*     */ 
/*     */   public short getProtocol()
/*     */   {
/* 105 */     return this.crypto;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getAZVersion()
/*     */   {
/* 111 */     return this.az_version;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getUploadSpeed()
/*     */   {
/* 117 */     return this.up_speed & 0xFFFF;
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getKey()
/*     */   {
/* 123 */     return this.address + ":" + this.tcp_port;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int compareTo(TRTrackerAnnouncerResponsePeer other)
/*     */   {
/* 130 */     return getString2(this).compareTo(getString2(other));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private String getString2(TRTrackerAnnouncerResponsePeer peer)
/*     */   {
/* 137 */     return peer.getAddress() + ":" + peer.getPort() + ":" + peer.getHTTPPort() + ":" + peer.getUDPPort();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 143 */     return "ip=" + this.address + (this.tcp_port == 0 ? "" : new StringBuilder().append(",tcp_port=").append(getPort()).toString()) + (this.udp_port == 0 ? "" : new StringBuilder().append(",udp_port=").append(getUDPPort()).toString()) + (this.http_port == 0 ? "" : new StringBuilder().append(",http_port=").append(getHTTPPort()).toString()) + ",prot=" + this.crypto + (this.up_speed == 0 ? "" : new StringBuilder().append(",up=").append(getUploadSpeed()).toString()) + ",ver=" + this.az_version;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/client/impl/TRTrackerAnnouncerResponsePeerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */