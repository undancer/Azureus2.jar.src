/*     */ package com.aelitis.azureus.core.dht.db.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.db.DHTDBValue;
/*     */ import com.aelitis.azureus.core.dht.impl.DHTLog;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportValue;
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
/*     */ 
/*     */ 
/*     */ public class DHTDBValueImpl
/*     */   implements DHTDBValue
/*     */ {
/*  39 */   private static final byte[] ZERO_LENGTH_BYTE_ARRAY = new byte[0];
/*     */   
/*     */ 
/*     */   private long creation_time;
/*     */   
/*     */ 
/*     */   private byte[] value;
/*     */   
/*     */ 
/*     */   private DHTTransportContact originator;
/*     */   
/*     */ 
/*     */   private DHTTransportContact sender;
/*     */   
/*     */ 
/*     */   private final boolean local;
/*     */   
/*     */ 
/*     */   private byte flags;
/*     */   
/*     */ 
/*     */   private final byte life_hours;
/*     */   
/*     */ 
/*     */   private final byte rep_control;
/*     */   
/*     */ 
/*     */   private int version;
/*     */   
/*     */ 
/*     */   private long store_time;
/*     */   
/*     */ 
/*     */ 
/*     */   protected DHTDBValueImpl(long _creation_time, byte[] _value, int _version, DHTTransportContact _originator, DHTTransportContact _sender, boolean _local, int _flags, int _life_hours, byte _rep_control)
/*     */   {
/*  75 */     this.creation_time = _creation_time;
/*  76 */     this.value = _value;
/*  77 */     this.version = _version;
/*  78 */     this.originator = _originator;
/*  79 */     this.sender = _sender;
/*  80 */     this.local = _local;
/*  81 */     this.flags = ((byte)_flags);
/*  82 */     this.life_hours = ((byte)_life_hours);
/*  83 */     this.rep_control = _rep_control;
/*     */     
/*     */ 
/*     */ 
/*  87 */     if ((this.value != null) && (this.value.length == 0))
/*     */     {
/*  89 */       this.value = ZERO_LENGTH_BYTE_ARRAY;
/*     */     }
/*     */     
/*  92 */     reset();
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
/*     */   protected DHTDBValueImpl(DHTTransportContact _sender, DHTTransportValue _other, boolean _local)
/*     */   {
/* 109 */     this(_other.getCreationTime(), _other.getValue(), _other.getVersion(), _other.getOriginator(), _sender, _local, _other.getFlags(), _other.getLifeTimeHours(), _other.getReplicationControl());
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
/*     */   protected void reset()
/*     */   {
/* 123 */     this.store_time = SystemTime.getCurrentTime();
/*     */     
/*     */ 
/*     */ 
/* 127 */     if (this.creation_time > this.store_time)
/*     */     {
/* 129 */       this.creation_time = this.store_time;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public long getCreationTime()
/*     */   {
/* 136 */     return this.creation_time;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void setCreationTime()
/*     */   {
/* 142 */     this.creation_time = SystemTime.getCurrentTime();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setStoreTime(long l)
/*     */   {
/* 149 */     this.store_time = l;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getStoreTime()
/*     */   {
/* 155 */     return this.store_time;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isLocal()
/*     */   {
/* 161 */     return this.local;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getValue()
/*     */   {
/* 167 */     return this.value;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getVersion()
/*     */   {
/* 173 */     return this.version;
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTTransportContact getOriginator()
/*     */   {
/* 179 */     return this.originator;
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTTransportContact getSender()
/*     */   {
/* 185 */     return this.sender;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getFlags()
/*     */   {
/* 191 */     return this.flags & 0xFF;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setFlags(byte _flags)
/*     */   {
/* 198 */     this.flags = _flags;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLifeTimeHours()
/*     */   {
/* 204 */     return this.life_hours & 0xFF;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getReplicationControl()
/*     */   {
/* 210 */     return this.rep_control;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getReplicationFactor()
/*     */   {
/* 216 */     return this.rep_control == -1 ? -1 : (byte)(this.rep_control & 0xF);
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getReplicationFrequencyHours()
/*     */   {
/* 222 */     return this.rep_control == -1 ? -1 : (byte)((this.rep_control & 0xF0) >> 4);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setOriginatorAndSender(DHTTransportContact _originator)
/*     */   {
/* 229 */     this.originator = _originator;
/* 230 */     this.sender = _originator;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DHTDBValue getValueForRelay(DHTTransportContact _sender)
/*     */   {
/* 237 */     return new DHTDBValueImpl(_sender, this, this.local);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DHTDBValue getValueForDeletion(int _version)
/*     */   {
/* 244 */     DHTDBValueImpl res = new DHTDBValueImpl(this.originator, this, this.local);
/*     */     
/* 246 */     res.value = ZERO_LENGTH_BYTE_ARRAY;
/*     */     
/* 248 */     res.setCreationTime();
/*     */     
/* 250 */     res.version = _version;
/*     */     
/* 252 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 258 */     long now = SystemTime.getCurrentTime();
/*     */     
/* 260 */     return DHTLog.getString(this.value) + " - " + new String(this.value) + "{v=" + this.version + ",f=" + Integer.toHexString(this.flags) + ",l=" + this.life_hours + ",r=" + Integer.toHexString(this.rep_control) + ",ca=" + (now - this.creation_time) + ",sa=" + (now - this.store_time) + ",se=" + this.sender.getString() + ",or=" + this.originator.getString() + "}";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/db/impl/DHTDBValueImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */