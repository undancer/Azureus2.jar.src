/*     */ package org.gudy.azureus2.pluginsimpl.local.ddb;
/*     */ 
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseException;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseKey;
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
/*     */ public class DDBaseKeyImpl
/*     */   implements DistributedDatabaseKey
/*     */ {
/*     */   private Object key;
/*     */   private byte[] key_bytes;
/*     */   private String description;
/*     */   private int flags;
/*     */   
/*     */   protected DDBaseKeyImpl(Object _key)
/*     */     throws DistributedDatabaseException
/*     */   {
/*  47 */     this(_key, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DDBaseKeyImpl(Object _key, String _description)
/*     */     throws DistributedDatabaseException
/*     */   {
/*  57 */     this.key = _key;
/*  58 */     this.description = _description;
/*     */     
/*  60 */     this.key_bytes = DDBaseHelpers.encode(this.key);
/*     */     
/*  62 */     if (this.description == null)
/*     */     {
/*  64 */       if ((this.key instanceof String))
/*     */       {
/*  66 */         this.description = ((String)this.key);
/*     */       }
/*     */       else
/*     */       {
/*  70 */         this.description = ("[" + ByteFormatter.nicePrint(this.key_bytes) + "]");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getKey()
/*     */   {
/*  78 */     return this.key;
/*     */   }
/*     */   
/*     */ 
/*     */   protected byte[] getBytes()
/*     */   {
/*  84 */     return this.key_bytes;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDescription()
/*     */   {
/*  90 */     return this.description;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getFlags()
/*     */   {
/*  96 */     return this.flags;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setFlags(int _flags)
/*     */   {
/* 103 */     this.flags = _flags;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ddb/DDBaseKeyImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */