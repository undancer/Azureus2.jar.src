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
/*     */ public class PRUDPPacketReplyScrape2
/*     */   extends PRUDPPacketReply
/*     */ {
/*     */   protected static final int BYTES_PER_ENTRY = 12;
/*     */   protected int[] complete;
/*     */   protected int[] incomplete;
/*     */   protected int[] downloaded;
/*     */   
/*     */   public PRUDPPacketReplyScrape2(int trans_id)
/*     */   {
/*  49 */     super(2, trans_id);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected PRUDPPacketReplyScrape2(DataInputStream is, int trans_id)
/*     */     throws IOException
/*     */   {
/*  59 */     super(2, trans_id);
/*     */     
/*     */ 
/*     */ 
/*  63 */     this.complete = new int[is.available() / 12];
/*  64 */     this.incomplete = new int[this.complete.length];
/*  65 */     this.downloaded = new int[this.complete.length];
/*     */     
/*  67 */     for (int i = 0; i < this.complete.length; i++)
/*     */     {
/*  69 */       this.complete[i] = is.readInt();
/*  70 */       this.downloaded[i] = is.readInt();
/*  71 */       this.incomplete[i] = is.readInt();
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
/*     */   public void setDetails(int[] _complete, int[] _downloaded, int[] _incomplete)
/*     */   {
/*  96 */     this.complete = _complete;
/*  97 */     this.downloaded = _downloaded;
/*  98 */     this.incomplete = _incomplete;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int[] getComplete()
/*     */   {
/* 105 */     return this.complete;
/*     */   }
/*     */   
/*     */ 
/*     */   public int[] getDownloaded()
/*     */   {
/* 111 */     return this.downloaded;
/*     */   }
/*     */   
/*     */ 
/*     */   public int[] getIncomplete()
/*     */   {
/* 117 */     return this.incomplete;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialise(DataOutputStream os)
/*     */     throws IOException
/*     */   {
/* 126 */     super.serialise(os);
/*     */     
/*     */ 
/*     */ 
/* 130 */     if (this.complete != null)
/*     */     {
/* 132 */       for (int i = 0; i < this.complete.length; i++)
/*     */       {
/* 134 */         os.writeInt(this.complete[i]);
/* 135 */         os.writeInt(this.downloaded[i]);
/* 136 */         os.writeInt(this.incomplete[i]);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 144 */     String data = "";
/*     */     
/* 146 */     for (int i = 0; i < this.complete.length; i++) {
/* 147 */       data = data + (i == 0 ? "" : ",") + this.complete[i] + "/" + this.incomplete[i] + "/" + this.downloaded[i];
/*     */     }
/* 149 */     return super.getString() + "[entries=" + this.complete.length + "=" + data + "]";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/protocol/udp/PRUDPPacketReplyScrape2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */