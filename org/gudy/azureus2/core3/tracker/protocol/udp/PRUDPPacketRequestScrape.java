/*     */ package org.gudy.azureus2.core3.tracker.protocol.udp;
/*     */ 
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketRequest;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PRUDPPacketRequestScrape
/*     */   extends PRUDPPacketRequest
/*     */ {
/*     */   protected final List hashes;
/*     */   
/*     */   public PRUDPPacketRequestScrape(long con_id, byte[] _hash)
/*     */   {
/*  50 */     super(2, con_id);
/*     */     
/*  52 */     this.hashes = new ArrayList();
/*  53 */     this.hashes.add(_hash);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public PRUDPPacketRequestScrape(long con_id, List hashwrappers)
/*     */   {
/*  61 */     super(2, con_id);
/*  62 */     this.hashes = new ArrayList();
/*  63 */     for (Iterator it = hashwrappers.iterator(); it.hasNext();) {
/*  64 */       this.hashes.add(((HashWrapper)it.next()).getBytes());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected PRUDPPacketRequestScrape(DataInputStream is, long con_id, int trans_id)
/*     */     throws IOException
/*     */   {
/*  75 */     super(2, con_id, trans_id);
/*  76 */     this.hashes = new ArrayList();
/*     */     byte[] hash;
/*  78 */     while (is.read(hash = new byte[20]) == 20) {
/*  79 */       this.hashes.add(hash);
/*     */     }
/*     */   }
/*     */   
/*     */   public List getHashes()
/*     */   {
/*  85 */     return this.hashes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialise(DataOutputStream os)
/*     */     throws IOException
/*     */   {
/*  94 */     super.serialise(os);
/*     */     
/*  96 */     for (Iterator it = this.hashes.iterator(); it.hasNext();) {
/*  97 */       os.write((byte[])it.next());
/*     */     }
/*     */   }
/*     */   
/*     */   public String getString()
/*     */   {
/* 103 */     StringBuilder buf = new StringBuilder();
/* 104 */     buf.append(super.getString());
/* 105 */     buf.append("[");
/* 106 */     for (Iterator it = this.hashes.iterator(); it.hasNext();)
/*     */     {
/* 108 */       buf.append(ByteFormatter.nicePrint((byte[])it.next(), true));
/* 109 */       if (it.hasNext())
/* 110 */         buf.append(";");
/*     */     }
/* 112 */     buf.append("]");
/* 113 */     return buf.toString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/protocol/udp/PRUDPPacketRequestScrape.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */