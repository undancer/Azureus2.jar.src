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
/*     */ public class PRUDPPacketReplyAnnounce2
/*     */   extends PRUDPPacketReply
/*     */ {
/*     */   protected int interval;
/*     */   protected int leechers;
/*     */   protected int seeders;
/*     */   protected static final int BYTES_PER_ENTRY = 6;
/*     */   protected int[] addresses;
/*     */   protected short[] ports;
/*     */   
/*     */   public PRUDPPacketReplyAnnounce2(int trans_id)
/*     */   {
/*  50 */     super(1, trans_id);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected PRUDPPacketReplyAnnounce2(DataInputStream is, int trans_id)
/*     */     throws IOException
/*     */   {
/*  60 */     super(1, trans_id);
/*     */     
/*  62 */     this.interval = is.readInt();
/*  63 */     this.leechers = is.readInt();
/*  64 */     this.seeders = is.readInt();
/*     */     
/*  66 */     this.addresses = new int[is.available() / 6];
/*  67 */     this.ports = new short[this.addresses.length];
/*     */     
/*  69 */     for (int i = 0; i < this.addresses.length; i++)
/*     */     {
/*  71 */       this.addresses[i] = is.readInt();
/*  72 */       this.ports[i] = is.readShort();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setInterval(int value)
/*     */   {
/*  80 */     this.interval = value;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getInterval()
/*     */   {
/*  86 */     return this.interval;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setLeechersSeeders(int _leechers, int _seeders)
/*     */   {
/*  94 */     this.leechers = _leechers;
/*  95 */     this.seeders = _seeders;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPeers(int[] _addresses, short[] _ports)
/*     */   {
/* 103 */     this.addresses = _addresses;
/* 104 */     this.ports = _ports;
/*     */   }
/*     */   
/*     */ 
/*     */   public int[] getAddresses()
/*     */   {
/* 110 */     return this.addresses;
/*     */   }
/*     */   
/*     */ 
/*     */   public short[] getPorts()
/*     */   {
/* 116 */     return this.ports;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLeechers()
/*     */   {
/* 122 */     return this.leechers;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSeeders()
/*     */   {
/* 128 */     return this.seeders;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialise(DataOutputStream os)
/*     */     throws IOException
/*     */   {
/* 137 */     super.serialise(os);
/*     */     
/* 139 */     os.writeInt(this.interval);
/* 140 */     os.writeInt(this.leechers);
/* 141 */     os.writeInt(this.seeders);
/*     */     
/* 143 */     if (this.addresses != null)
/*     */     {
/* 145 */       for (int i = 0; i < this.addresses.length; i++)
/*     */       {
/* 147 */         os.writeInt(this.addresses[i]);
/* 148 */         os.writeShort(this.ports[i]);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 156 */     return super.getString() + "[interval=" + this.interval + ",leechers=" + this.leechers + ",seeders=" + this.seeders + ",addresses=" + this.addresses.length + "]";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/protocol/udp/PRUDPPacketReplyAnnounce2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */