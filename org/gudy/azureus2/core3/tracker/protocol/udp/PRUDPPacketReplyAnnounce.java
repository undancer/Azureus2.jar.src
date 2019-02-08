/*     */ package org.gudy.azureus2.core3.tracker.protocol.udp;
/*     */ 
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketReply;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
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
/*     */ public class PRUDPPacketReplyAnnounce
/*     */   extends PRUDPPacketReply
/*     */ {
/*     */   protected int interval;
/*     */   protected static final int BYTES_PER_ENTRY = 6;
/*     */   protected int[] addresses;
/*     */   protected short[] ports;
/*     */   
/*     */   public PRUDPPacketReplyAnnounce(int trans_id)
/*     */   {
/*  48 */     super(1, trans_id);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected PRUDPPacketReplyAnnounce(DataInputStream is, int trans_id)
/*     */     throws IOException
/*     */   {
/*  58 */     super(1, trans_id);
/*     */     
/*  60 */     this.interval = is.readInt();
/*     */     
/*  62 */     this.addresses = new int[is.available() / 6];
/*  63 */     this.ports = new short[this.addresses.length];
/*     */     
/*  65 */     for (int i = 0; i < this.addresses.length; i++)
/*     */     {
/*  67 */       this.addresses[i] = is.readInt();
/*  68 */       this.ports[i] = is.readShort();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setInterval(int value)
/*     */   {
/*  76 */     this.interval = value;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getInterval()
/*     */   {
/*  82 */     return this.interval;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPeers(int[] _addresses, short[] _ports)
/*     */   {
/*  90 */     this.addresses = _addresses;
/*  91 */     this.ports = _ports;
/*     */   }
/*     */   
/*     */ 
/*     */   public int[] getAddresses()
/*     */   {
/*  97 */     return this.addresses;
/*     */   }
/*     */   
/*     */ 
/*     */   public short[] getPorts()
/*     */   {
/* 103 */     return this.ports;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialise(DataOutputStream os)
/*     */     throws IOException
/*     */   {
/* 112 */     super.serialise(os);
/*     */     
/* 114 */     os.writeInt(this.interval);
/*     */     
/* 116 */     if (this.addresses != null)
/*     */     {
/* 118 */       for (int i = 0; i < this.addresses.length; i++)
/*     */       {
/* 120 */         os.writeInt(this.addresses[i]);
/* 121 */         os.writeShort(this.ports[i]);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 129 */     return super.getString().concat("[interval=").concat(String.valueOf(this.interval)).concat(", addresses=").concat(String.valueOf(this.addresses.length)).concat("]");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/protocol/udp/PRUDPPacketReplyAnnounce.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */