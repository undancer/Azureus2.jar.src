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
/*     */ public class PRUDPPacketRequestAnnounce
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
/*     */   protected int num_want;
/*     */   protected long left;
/*     */   protected short port;
/*     */   protected long uploaded;
/*     */   protected int ip_address;
/*     */   
/*     */   public PRUDPPacketRequestAnnounce(long con_id)
/*     */   {
/*  83 */     super(1, con_id);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected PRUDPPacketRequestAnnounce(DataInputStream is, long con_id, int trans_id)
/*     */     throws IOException
/*     */   {
/*  94 */     super(1, con_id, trans_id);
/*     */     
/*  96 */     this.hash = new byte[20];
/*  97 */     this.peer_id = new byte[20];
/*     */     
/*  99 */     is.read(this.hash);
/* 100 */     is.read(this.peer_id);
/*     */     
/* 102 */     this.downloaded = is.readLong();
/* 103 */     this.left = is.readLong();
/* 104 */     this.uploaded = is.readLong();
/* 105 */     this.event = is.readInt();
/* 106 */     this.ip_address = is.readInt();
/* 107 */     this.num_want = is.readInt();
/* 108 */     this.port = is.readShort();
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getHash()
/*     */   {
/* 114 */     return this.hash;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getPeerId()
/*     */   {
/* 120 */     return this.peer_id;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDownloaded()
/*     */   {
/* 126 */     return this.downloaded;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getEvent()
/*     */   {
/* 132 */     return this.event;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNumWant()
/*     */   {
/* 138 */     return this.num_want;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLeft()
/*     */   {
/* 144 */     return this.left;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 150 */     return this.port & 0xFFFF;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getUploaded()
/*     */   {
/* 156 */     return this.uploaded;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getIPAddress()
/*     */   {
/* 162 */     return this.ip_address;
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
/*     */   public void setDetails(byte[] _hash, byte[] _peer_id, long _downloaded, int _event, int _ip_address, int _num_want, long _left, short _port, long _uploaded)
/*     */   {
/* 178 */     this.hash = _hash;
/* 179 */     this.peer_id = _peer_id;
/* 180 */     this.downloaded = _downloaded;
/* 181 */     this.event = _event;
/* 182 */     this.ip_address = _ip_address;
/* 183 */     this.num_want = _num_want;
/* 184 */     this.left = _left;
/* 185 */     this.port = _port;
/* 186 */     this.uploaded = _uploaded;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialise(DataOutputStream os)
/*     */     throws IOException
/*     */   {
/* 195 */     super.serialise(os);
/*     */     
/* 197 */     os.write(this.hash);
/* 198 */     os.write(this.peer_id);
/* 199 */     os.writeLong(this.downloaded);
/* 200 */     os.writeLong(this.left);
/* 201 */     os.writeLong(this.uploaded);
/* 202 */     os.writeInt(this.event);
/* 203 */     os.writeInt(this.ip_address);
/* 204 */     os.writeInt(this.num_want);
/* 205 */     os.writeShort(this.port);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 211 */     return super.getString().concat("[").concat("hash=").concat(ByteFormatter.nicePrint(this.hash, true)).concat("peer=").concat(ByteFormatter.nicePrint(this.peer_id, true)).concat("dl=").concat(String.valueOf(this.downloaded)).concat("ev=").concat(String.valueOf(this.event)).concat("ip=").concat(String.valueOf(this.ip_address)).concat("nw=").concat(String.valueOf(this.num_want)).concat("left=").concat(String.valueOf(this.left)).concat("port=").concat(String.valueOf(this.port)).concat("ul=").concat(String.valueOf(this.uploaded)).concat("]");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/protocol/udp/PRUDPPacketRequestAnnounce.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */