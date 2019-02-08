/*     */ package com.aelitis.azureus.core.dht.router.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.impl.DHTLog;
/*     */ import com.aelitis.azureus.core.dht.router.DHTRouterContact;
/*     */ import com.aelitis.azureus.core.dht.router.DHTRouterContactAttachment;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DHTRouterContactImpl
/*     */   implements DHTRouterContact
/*     */ {
/*     */   private final byte[] node_id;
/*     */   private DHTRouterContactAttachment attachment;
/*     */   private boolean has_been_alive;
/*     */   private boolean ping_outstanding;
/*     */   private int fail_count;
/*     */   private long first_alive_time;
/*     */   private long first_fail_or_last_alive_time;
/*     */   private long last_added_time;
/*     */   private boolean is_bucket_entry;
/*     */   
/*     */   protected DHTRouterContactImpl(byte[] _node_id, DHTRouterContactAttachment _attachment, boolean _has_been_alive)
/*     */   {
/*  55 */     this.node_id = _node_id;
/*  56 */     this.attachment = _attachment;
/*  57 */     this.has_been_alive = _has_been_alive;
/*     */     
/*  59 */     if (this.attachment != null)
/*     */     {
/*  61 */       this.attachment.setRouterContact(this);
/*     */     }
/*     */     
/*  64 */     this.is_bucket_entry = false;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getID()
/*     */   {
/*  70 */     return this.node_id;
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTRouterContactAttachment getAttachment()
/*     */   {
/*  76 */     return this.attachment;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setAttachment(DHTRouterContactAttachment _attachment)
/*     */   {
/*  83 */     this.attachment = _attachment;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setAlive()
/*     */   {
/*  89 */     this.fail_count = 0;
/*  90 */     this.first_fail_or_last_alive_time = SystemTime.getCurrentTime();
/*  91 */     this.has_been_alive = true;
/*     */     
/*  93 */     if (this.first_alive_time == 0L)
/*     */     {
/*  95 */       this.first_alive_time = this.first_fail_or_last_alive_time;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean hasBeenAlive()
/*     */   {
/* 102 */     return this.has_been_alive;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isAlive()
/*     */   {
/* 108 */     return (this.has_been_alive) && (this.fail_count == 0);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isFailing()
/*     */   {
/* 114 */     return this.fail_count > 0;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getFailCount()
/*     */   {
/* 120 */     return this.fail_count;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTimeAlive()
/*     */   {
/* 126 */     if ((this.fail_count > 0) || (this.first_alive_time == 0L))
/*     */     {
/* 128 */       return 0L;
/*     */     }
/*     */     
/* 131 */     return SystemTime.getCurrentTime() - this.first_alive_time;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean setFailed()
/*     */   {
/* 137 */     this.fail_count += 1;
/*     */     
/* 139 */     if (this.fail_count == 1)
/*     */     {
/* 141 */       this.first_fail_or_last_alive_time = SystemTime.getCurrentTime();
/*     */     }
/*     */     
/* 144 */     return hasFailed();
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean hasFailed()
/*     */   {
/* 150 */     if (this.has_been_alive)
/*     */     {
/* 152 */       return this.fail_count >= this.attachment.getMaxFailForLiveCount();
/*     */     }
/*     */     
/*     */ 
/* 156 */     return this.fail_count >= this.attachment.getMaxFailForUnknownCount();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected long getFirstFailTime()
/*     */   {
/* 163 */     return this.fail_count == 0 ? 0L : this.first_fail_or_last_alive_time;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getLastAliveTime()
/*     */   {
/* 169 */     return this.fail_count == 0 ? this.first_fail_or_last_alive_time : 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getFirstFailOrLastAliveTime()
/*     */   {
/* 175 */     return this.first_fail_or_last_alive_time;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getFirstAliveTime()
/*     */   {
/* 181 */     return this.first_alive_time;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getLastAddedTime()
/*     */   {
/* 187 */     return this.last_added_time;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setLastAddedTime(long l)
/*     */   {
/* 194 */     this.last_added_time = l;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setPingOutstanding(boolean b)
/*     */   {
/* 201 */     this.ping_outstanding = b;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean getPingOutstanding()
/*     */   {
/* 207 */     return this.ping_outstanding;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 213 */     return DHTLog.getString2(this.node_id) + "[hba=" + (this.has_been_alive ? "Y" : "N") + ",bad=" + this.fail_count + ",OK=" + getTimeAlive() + "]";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void getString(StringBuilder sb)
/*     */   {
/* 222 */     sb.append(DHTLog.getString2(this.node_id));
/* 223 */     sb.append("[hba=");
/* 224 */     sb.append(this.has_been_alive ? "Y" : "N");
/* 225 */     sb.append(",bad=");
/* 226 */     sb.append(this.fail_count);
/* 227 */     sb.append(",OK=");
/* 228 */     sb.append(getTimeAlive());
/* 229 */     sb.append("]");
/*     */   }
/*     */   
/*     */   public boolean isBucketEntry() {
/* 233 */     return this.is_bucket_entry;
/*     */   }
/*     */   
/*     */   public void setBucketEntry() {
/* 237 */     this.is_bucket_entry = true;
/*     */   }
/*     */   
/*     */   public boolean isReplacement() {
/* 241 */     return !this.is_bucket_entry;
/*     */   }
/*     */   
/*     */   public void setReplacement() {
/* 245 */     this.is_bucket_entry = false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/router/impl/DHTRouterContactImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */