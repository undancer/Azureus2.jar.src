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
/*     */ 
/*     */ public class PRUDPPacketReplyScrape
/*     */   extends PRUDPPacketReply
/*     */ {
/*     */   protected static final int BYTES_PER_ENTRY = 32;
/*     */   protected byte[][] hashes;
/*     */   protected int[] complete;
/*     */   protected int[] incomplete;
/*     */   protected int[] downloaded;
/*     */   
/*     */   public PRUDPPacketReplyScrape(int trans_id)
/*     */   {
/*  50 */     super(2, trans_id);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected PRUDPPacketReplyScrape(DataInputStream is, int trans_id)
/*     */     throws IOException
/*     */   {
/*  60 */     super(2, trans_id);
/*     */     
/*     */ 
/*     */ 
/*  64 */     this.hashes = new byte[is.available() / 32][];
/*  65 */     this.complete = new int[this.hashes.length];
/*  66 */     this.incomplete = new int[this.hashes.length];
/*  67 */     this.downloaded = new int[this.hashes.length];
/*     */     
/*  69 */     for (int i = 0; i < this.hashes.length; i++)
/*     */     {
/*  71 */       this.hashes[i] = new byte[20];
/*  72 */       is.read(this.hashes[i]);
/*  73 */       this.complete[i] = is.readInt();
/*  74 */       this.downloaded[i] = is.readInt();
/*  75 */       this.incomplete[i] = is.readInt();
/*     */     }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setDetails(byte[][] _hashes, int[] _complete, int[] _downloaded, int[] _incomplete)
/*     */   {
/* 101 */     this.hashes = _hashes;
/* 102 */     this.complete = _complete;
/* 103 */     this.downloaded = _downloaded;
/* 104 */     this.incomplete = _incomplete;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[][] getHashes()
/*     */   {
/* 110 */     return this.hashes;
/*     */   }
/*     */   
/*     */ 
/*     */   public int[] getComplete()
/*     */   {
/* 116 */     return this.complete;
/*     */   }
/*     */   
/*     */ 
/*     */   public int[] getDownloaded()
/*     */   {
/* 122 */     return this.downloaded;
/*     */   }
/*     */   
/*     */ 
/*     */   public int[] getIncomplete()
/*     */   {
/* 128 */     return this.incomplete;
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
/*     */ 
/*     */ 
/* 141 */     if (this.hashes != null)
/*     */     {
/* 143 */       for (int i = 0; i < this.hashes.length; i++)
/*     */       {
/* 145 */         os.write(this.hashes[i]);
/* 146 */         os.writeInt(this.complete[i]);
/* 147 */         os.writeInt(this.downloaded[i]);
/* 148 */         os.writeInt(this.incomplete[i]);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 156 */     return super.getString().concat("[hashes=").concat(String.valueOf(this.hashes.length)).concat("]");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/protocol/udp/PRUDPPacketReplyScrape.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */