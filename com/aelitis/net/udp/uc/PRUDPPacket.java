/*     */ package com.aelitis.net.udp.uc;
/*     */ 
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class PRUDPPacket
/*     */ {
/*     */   public static final int MAX_PACKET_SIZE = 8192;
/*     */   public static final int DEFAULT_UDP_TIMEOUT = 30000;
/*  42 */   private static int next_id = ;
/*  43 */   private static AEMonitor class_mon = new AEMonitor("PRUDPPacket");
/*     */   
/*     */ 
/*     */   private InetSocketAddress address;
/*     */   
/*     */   private int type;
/*     */   
/*     */   private int transaction_id;
/*     */   
/*     */   private PRUDPPacket previous_packet;
/*     */   
/*     */   private int serialised_size;
/*     */   
/*     */ 
/*     */   protected PRUDPPacket(int _type, int _transaction_id)
/*     */   {
/*  59 */     this.type = _type;
/*  60 */     this.transaction_id = _transaction_id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected PRUDPPacket(int _type)
/*     */   {
/*  67 */     this.type = _type;
/*     */     try
/*     */     {
/*  70 */       class_mon.enter();
/*     */       
/*  72 */       this.transaction_id = (next_id++);
/*     */     }
/*     */     finally
/*     */     {
/*  76 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setSerialisedSize(int len)
/*     */   {
/*  84 */     this.serialised_size = len;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSerialisedSize()
/*     */   {
/*  90 */     return this.serialised_size;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean hasContinuation()
/*     */   {
/*  96 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setPreviousPacket(PRUDPPacket p)
/*     */   {
/* 103 */     this.previous_packet = p;
/*     */   }
/*     */   
/*     */ 
/*     */   public PRUDPPacket getPreviousPacket()
/*     */   {
/* 109 */     return this.previous_packet;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setAddress(InetSocketAddress _address)
/*     */   {
/* 116 */     this.address = _address;
/*     */   }
/*     */   
/*     */ 
/*     */   public InetSocketAddress getAddress()
/*     */   {
/* 122 */     return this.address;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getAction()
/*     */   {
/* 128 */     return this.type;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTransactionId()
/*     */   {
/* 134 */     return this.transaction_id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract void serialise(DataOutputStream paramDataOutputStream)
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */   public String getString()
/*     */   {
/* 146 */     return "type=" + this.type + ",addr=" + this.address;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/udp/uc/PRUDPPacket.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */