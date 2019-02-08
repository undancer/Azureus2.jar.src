/*     */ package com.aelitis.azureus.core.dht.transport.udp.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportAlternativeContact;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.BDecoder;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DHTTransportAlternativeContactImpl
/*     */   implements DHTTransportAlternativeContact
/*     */ {
/*     */   private final byte network_type;
/*     */   private final byte version;
/*     */   private final short initial_age;
/*     */   private final byte[] encoded;
/*     */   private final int id;
/*  43 */   private final int start_time = (int)(SystemTime.getMonotonousTime() / 1000L);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DHTTransportAlternativeContactImpl(byte _network_type, byte _version, short _age, byte[] _encoded)
/*     */   {
/*  52 */     this.network_type = _network_type;
/*  53 */     this.version = _version;
/*  54 */     this.initial_age = (_age < 0 ? Short.MAX_VALUE : _age);
/*  55 */     this.encoded = _encoded;
/*     */     
/*  57 */     this.id = Arrays.hashCode(this.encoded);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNetworkType()
/*     */   {
/*  63 */     return this.network_type & 0xFF;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getVersion()
/*     */   {
/*  69 */     return this.version & 0xFF;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getID()
/*     */   {
/*  75 */     return this.id;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLastAlive()
/*     */   {
/*  81 */     return this.start_time - this.initial_age;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getAge()
/*     */   {
/*  87 */     if (this.initial_age < 0)
/*     */     {
/*  89 */       return 32767;
/*     */     }
/*     */     
/*  92 */     int elapsed = (int)(SystemTime.getMonotonousTime() / 1000L) - this.start_time;
/*     */     
/*  94 */     int rem = Short.MAX_VALUE - this.initial_age;
/*     */     
/*  96 */     if (rem < elapsed)
/*     */     {
/*  98 */       return 32767;
/*     */     }
/*     */     
/*     */ 
/* 102 */     return (short)(this.initial_age + elapsed);
/*     */   }
/*     */   
/*     */ 
/*     */   public Map<String, Object> getProperties()
/*     */   {
/*     */     try
/*     */     {
/* 110 */       return BDecoder.decode(this.encoded);
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/* 114 */     return new HashMap();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/DHTTransportAlternativeContactImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */