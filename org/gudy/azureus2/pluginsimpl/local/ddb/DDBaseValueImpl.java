/*     */ package org.gudy.azureus2.pluginsimpl.local.ddb;
/*     */ 
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseContact;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseException;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseValue;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DDBaseValueImpl
/*     */   implements DistributedDatabaseValue
/*     */ {
/*     */   private DDBaseContactImpl contact;
/*     */   private Object value;
/*     */   private byte[] value_bytes;
/*     */   private long creation_time;
/*     */   private long version;
/*  45 */   protected static int MAX_VALUE_SIZE = 509;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DDBaseValueImpl(DDBaseContactImpl _contact, Object _value, long _creation_time, long _version)
/*     */     throws DistributedDatabaseException
/*     */   {
/*  56 */     this.contact = _contact;
/*  57 */     this.value = _value;
/*  58 */     this.creation_time = _creation_time;
/*  59 */     this.version = _version;
/*     */     
/*  61 */     this.value_bytes = DDBaseHelpers.encode(this.value);
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
/*     */   protected DDBaseValueImpl(DDBaseContactImpl _contact, byte[] _value_bytes, long _creation_time, long _version)
/*     */   {
/*  74 */     this.contact = _contact;
/*  75 */     this.value_bytes = _value_bytes;
/*  76 */     this.creation_time = _creation_time;
/*  77 */     this.version = _version;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object getValue(Class c)
/*     */     throws DistributedDatabaseException
/*     */   {
/*  86 */     if (this.value == null)
/*     */     {
/*  88 */       this.value = DDBaseHelpers.decode(c, this.value_bytes);
/*     */     }
/*     */     
/*  91 */     return this.value;
/*     */   }
/*     */   
/*     */ 
/*     */   protected byte[] getBytes()
/*     */   {
/*  97 */     return this.value_bytes;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getCreationTime()
/*     */   {
/* 103 */     return this.creation_time;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getVersion()
/*     */   {
/* 109 */     return this.version;
/*     */   }
/*     */   
/*     */ 
/*     */   public DistributedDatabaseContact getContact()
/*     */   {
/* 115 */     return this.contact;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ddb/DDBaseValueImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */