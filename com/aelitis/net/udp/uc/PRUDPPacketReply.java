/*     */ package com.aelitis.net.udp.uc;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class PRUDPPacketReply
/*     */   extends PRUDPPacket
/*     */ {
/*     */   public static final int PR_HEADER_SIZE = 8;
/*  43 */   private static AEMonitor class_mon = new AEMonitor("PRUDPPacketReply:class");
/*     */   
/*  45 */   private static Map packet_decoders = new HashMap();
/*     */   
/*     */ 
/*     */   public static void registerDecoders(Map _decoders)
/*     */   {
/*     */     try
/*     */     {
/*  52 */       class_mon.enter();
/*     */       
/*  54 */       Map new_decoders = new HashMap(packet_decoders);
/*     */       
/*  56 */       Iterator it = _decoders.keySet().iterator();
/*     */       
/*  58 */       while (it.hasNext())
/*     */       {
/*  60 */         Integer action = (Integer)it.next();
/*     */         
/*  62 */         if (packet_decoders.containsKey(action))
/*     */         {
/*  64 */           Debug.out("Duplicate codec! " + action);
/*     */         }
/*     */       }
/*     */       
/*  68 */       new_decoders.putAll(_decoders);
/*     */       
/*  70 */       packet_decoders = new_decoders;
/*     */     }
/*     */     finally
/*     */     {
/*  74 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public PRUDPPacketReply(int _action, int _tran_id)
/*     */   {
/*  83 */     super(_action, _tran_id);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialise(DataOutputStream os)
/*     */     throws IOException
/*     */   {
/*  95 */     os.writeInt(getAction());
/*     */     
/*  97 */     os.writeInt(getTransactionId());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PRUDPPacketReply deserialiseReply(PRUDPPacketHandler handler, InetSocketAddress originator, DataInputStream is)
/*     */     throws IOException
/*     */   {
/* 108 */     int action = is.readInt();
/*     */     
/* 110 */     PRUDPPacketReplyDecoder decoder = (PRUDPPacketReplyDecoder)packet_decoders.get(new Integer(action));
/*     */     
/* 112 */     if (decoder == null)
/*     */     {
/* 114 */       throw new IOException("No decoder registered for action '" + action + "'");
/*     */     }
/*     */     
/* 117 */     int transaction_id = is.readInt();
/*     */     
/* 119 */     return decoder.decode(handler, originator, is, action, transaction_id);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 125 */     return super.getString() + ":reply[trans=" + getTransactionId() + "]";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/udp/uc/PRUDPPacketReply.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */