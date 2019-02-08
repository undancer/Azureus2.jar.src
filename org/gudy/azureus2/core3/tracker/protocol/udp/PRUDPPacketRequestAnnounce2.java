/*     */ package org.gudy.azureus2.core3.tracker.protocol.udp;
/*     */ 
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketRequest;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
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
/*     */ public class PRUDPPacketRequestAnnounce2
/*     */   extends PRUDPPacketRequest
/*     */ {
/*     */   public static final int EV_STARTED = 2;
/*     */   public static final int EV_STOPPED = 3;
/*     */   public static final int EV_COMPLETED = 1;
/*     */   public static final int EV_UPDATE = 0;
/*     */   protected byte[] hash;
/*     */   protected byte[] peer_id;
/*     */   protected long downloaded;
/*     */   protected int event;
/*     */   protected int key;
/*     */   protected int num_want;
/*     */   protected long left;
/*     */   protected short port;
/*     */   protected long uploaded;
/*     */   protected int ip_address;
/*     */   
/*     */   public PRUDPPacketRequestAnnounce2(long con_id)
/*     */   {
/*  85 */     super(1, con_id);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected PRUDPPacketRequestAnnounce2(DataInputStream is, long con_id, int trans_id)
/*     */     throws IOException
/*     */   {
/*  96 */     super(1, con_id, trans_id);
/*     */     
/*  98 */     this.hash = new byte[20];
/*  99 */     this.peer_id = new byte[20];
/*     */     
/* 101 */     is.read(this.hash);
/* 102 */     is.read(this.peer_id);
/*     */     
/* 104 */     this.downloaded = is.readLong();
/* 105 */     this.left = is.readLong();
/* 106 */     this.uploaded = is.readLong();
/* 107 */     this.event = is.readInt();
/* 108 */     this.ip_address = is.readInt();
/* 109 */     this.key = is.readInt();
/* 110 */     this.num_want = is.readInt();
/* 111 */     this.port = is.readShort();
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getHash()
/*     */   {
/* 117 */     return this.hash;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getPeerId()
/*     */   {
/* 123 */     return this.peer_id;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDownloaded()
/*     */   {
/* 129 */     return this.downloaded;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getEvent()
/*     */   {
/* 135 */     return this.event;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNumWant()
/*     */   {
/* 141 */     return this.num_want;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLeft()
/*     */   {
/* 147 */     return this.left;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 153 */     return this.port & 0xFFFF;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getUploaded()
/*     */   {
/* 159 */     return this.uploaded;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getIPAddress()
/*     */   {
/* 165 */     return this.ip_address;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getKey()
/*     */   {
/* 171 */     return this.key;
/*     */   }
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
/*     */   public void setDetails(byte[] _hash, byte[] _peer_id, long _downloaded, int _event, int _ip_address, int _key, int _num_want, long _left, short _port, long _uploaded)
/*     */   {
/* 187 */     this.hash = _hash;
/* 188 */     this.peer_id = _peer_id;
/* 189 */     this.downloaded = _downloaded;
/* 190 */     this.event = _event;
/* 191 */     this.ip_address = _ip_address;
/* 192 */     this.key = _key;
/* 193 */     this.num_want = _num_want;
/* 194 */     this.left = _left;
/* 195 */     this.port = _port;
/* 196 */     this.uploaded = _uploaded;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialise(DataOutputStream os)
/*     */     throws IOException
/*     */   {
/* 205 */     super.serialise(os);
/*     */     
/* 207 */     os.write(this.hash);
/* 208 */     os.write(this.peer_id);
/* 209 */     os.writeLong(this.downloaded);
/* 210 */     os.writeLong(this.left);
/* 211 */     os.writeLong(this.uploaded);
/* 212 */     os.writeInt(this.event);
/* 213 */     os.writeInt(this.ip_address);
/* 214 */     os.writeInt(this.key);
/* 215 */     os.writeInt(this.num_want);
/* 216 */     os.writeShort(this.port);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 222 */     return super.getString().concat("[").concat("hash=" + ByteFormatter.nicePrint(this.hash, true) + "peer=" + ByteFormatter.nicePrint(this.peer_id, true) + "dl=" + this.downloaded + "ev=" + this.event + "ip=" + this.ip_address + "key=" + this.key + "nw=" + this.num_want + "left=" + this.left + "port=" + this.port + "ul=" + this.uploaded + "]");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/protocol/udp/PRUDPPacketRequestAnnounce2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */