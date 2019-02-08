/*     */ package com.aelitis.azureus.core.networkmanager.impl.udp;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UDPPacket
/*     */ {
/*     */   public static final byte PROTOCOL_VERSION = 1;
/*     */   
/*     */ 
/*     */   public static final byte COMMAND_CRYPTO = 0;
/*     */   
/*     */ 
/*     */   public static final byte COMMAND_DATA = 1;
/*     */   
/*     */ 
/*     */   public static final byte COMMAND_ACK = 2;
/*     */   
/*     */ 
/*     */   public static final byte COMMAND_CLOSE = 3;
/*     */   
/*     */ 
/*     */   public static final byte COMMAND_STAT_REQUEST = 4;
/*     */   
/*     */ 
/*     */   public static final byte COMMAND_STAT_REPLY = 5;
/*     */   
/*     */ 
/*     */   public static final byte FLAG_NONE = 0;
/*     */   
/*     */ 
/*     */   public static final byte FLAG_LAZY_ACK = 1;
/*     */   
/*     */ 
/*     */   private final UDPConnection connection;
/*     */   
/*     */ 
/*     */   private final int sequence;
/*     */   
/*     */   private final int alt_sequence;
/*     */   
/*     */   private final byte command;
/*     */   
/*     */   private final byte[] buffer;
/*     */   
/*     */   private final long unack_in_sequence_count;
/*     */   
/*  47 */   private boolean auto_retransmit = true;
/*     */   
/*     */ 
/*     */   private short sent_count;
/*     */   
/*     */   private short resend_count;
/*     */   
/*     */   private boolean received;
/*     */   
/*     */   private long send_tick_count;
/*     */   
/*     */ 
/*     */   protected UDPPacket(UDPConnection _connection, int[] _sequences, byte _command, byte[] _buffer, long _unack_in_sequence_count)
/*     */   {
/*  61 */     this.connection = _connection;
/*  62 */     this.sequence = _sequences[1];
/*  63 */     this.alt_sequence = _sequences[3];
/*  64 */     this.command = _command;
/*  65 */     this.buffer = _buffer;
/*     */     
/*  67 */     this.unack_in_sequence_count = _unack_in_sequence_count;
/*     */   }
/*     */   
/*     */ 
/*     */   protected UDPConnection getConnection()
/*     */   {
/*  73 */     return this.connection;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getSequence()
/*     */   {
/*  79 */     return this.sequence;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getAlternativeSequence()
/*     */   {
/*  85 */     return this.alt_sequence;
/*     */   }
/*     */   
/*     */ 
/*     */   protected byte getCommand()
/*     */   {
/*  91 */     return this.command;
/*     */   }
/*     */   
/*     */ 
/*     */   protected byte[] getBuffer()
/*     */   {
/*  97 */     return this.buffer;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getUnAckInSequenceCount()
/*     */   {
/* 103 */     return this.unack_in_sequence_count;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isAutoRetransmit()
/*     */   {
/* 109 */     return this.auto_retransmit;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setAutoRetransmit(boolean b)
/*     */   {
/* 116 */     this.auto_retransmit = b;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected short sent(long tick_count)
/*     */   {
/* 123 */     this.sent_count = ((short)(this.sent_count + 1));
/*     */     
/* 125 */     this.send_tick_count = tick_count;
/*     */     
/* 127 */     return this.sent_count;
/*     */   }
/*     */   
/*     */ 
/*     */   protected short getResendCount()
/*     */   {
/* 133 */     return this.resend_count;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void resent()
/*     */   {
/* 139 */     this.resend_count = ((short)(this.resend_count + 1));
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getSendTickCount()
/*     */   {
/* 145 */     return this.send_tick_count;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void setHasBeenReceived()
/*     */   {
/* 151 */     this.received = true;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean hasBeenReceived()
/*     */   {
/* 157 */     return this.received;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getSentCount()
/*     */   {
/* 163 */     return this.sent_count;
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getString()
/*     */   {
/* 169 */     return "seq=" + this.sequence + ",type=" + this.command + ",retrans=" + this.auto_retransmit + ",sent=" + this.sent_count + ",len=" + this.buffer.length;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/udp/UDPPacket.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */