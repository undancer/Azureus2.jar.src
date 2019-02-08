/*     */ package com.aelitis.net.udp.uc;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
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
/*     */ 
/*     */ 
/*     */ public abstract class PRUDPPacketRequest
/*     */   extends PRUDPPacket
/*     */ {
/*     */   public static final int PR_HEADER_SIZE = 16;
/*  44 */   private static AEMonitor class_mon = new AEMonitor("PRUDPPacketRequest:class");
/*     */   
/*  46 */   private static Map packet_decoders = new HashMap();
/*     */   
/*     */   private long connection_id;
/*     */   
/*     */   private long receive_time;
/*     */   
/*     */   public static void registerDecoders(Map _decoders)
/*     */   {
/*     */     try
/*     */     {
/*  56 */       class_mon.enter();
/*     */       
/*  58 */       Map new_decoders = new HashMap(packet_decoders);
/*     */       
/*  60 */       Iterator it = _decoders.keySet().iterator();
/*     */       
/*  62 */       while (it.hasNext())
/*     */       {
/*  64 */         Integer action = (Integer)it.next();
/*     */         
/*  66 */         if (packet_decoders.containsKey(action))
/*     */         {
/*  68 */           Debug.out("Duplicate codec! " + action);
/*     */         }
/*     */       }
/*     */       
/*  72 */       new_decoders.putAll(_decoders);
/*     */       
/*  74 */       packet_decoders = new_decoders;
/*     */     }
/*     */     finally
/*     */     {
/*  78 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public PRUDPPacketRequest(int _action, long _con_id)
/*     */   {
/*  87 */     super(_action);
/*     */     
/*  89 */     this.connection_id = _con_id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected PRUDPPacketRequest(int _action, long _con_id, int _trans_id)
/*     */   {
/*  98 */     super(_action, _trans_id);
/*     */     
/* 100 */     this.connection_id = _con_id;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getConnectionId()
/*     */   {
/* 106 */     return this.connection_id;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getReceiveTime()
/*     */   {
/* 112 */     return this.receive_time;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setReceiveTime(long _rt)
/*     */   {
/* 119 */     this.receive_time = _rt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialise(DataOutputStream os)
/*     */     throws IOException
/*     */   {
/* 130 */     os.writeLong(this.connection_id);
/*     */     
/* 132 */     os.writeInt(getAction());
/*     */     
/* 134 */     os.writeInt(getTransactionId());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PRUDPPacketRequest deserialiseRequest(PRUDPPacketHandler handler, DataInputStream is)
/*     */     throws IOException
/*     */   {
/* 144 */     long connection_id = is.readLong();
/* 145 */     int action = is.readInt();
/* 146 */     int transaction_id = is.readInt();
/*     */     
/* 148 */     PRUDPPacketRequestDecoder decoder = (PRUDPPacketRequestDecoder)packet_decoders.get(new Integer(action));
/*     */     
/* 150 */     if (decoder == null)
/*     */     {
/* 152 */       throw new IOException("No decoder registered for action '" + action + "'");
/*     */     }
/*     */     
/* 155 */     return decoder.decode(handler, is, connection_id, action, transaction_id);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 161 */     return super.getString() + ":request[con=" + this.connection_id + ",trans=" + getTransactionId() + "]";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/udp/uc/PRUDPPacketRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */