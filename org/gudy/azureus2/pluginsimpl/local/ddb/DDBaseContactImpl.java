/*     */ package org.gudy.azureus2.pluginsimpl.local.ddb;
/*     */ 
/*     */ import com.aelitis.azureus.plugins.dht.DHTPluginContact;
/*     */ import com.aelitis.azureus.plugins.dht.DHTPluginOperationListener;
/*     */ import com.aelitis.azureus.plugins.dht.DHTPluginValue;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseContact;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseEvent;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseException;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseKey;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseKeyStats;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseListener;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseProgressListener;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseTransferType;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DDBaseContactImpl
/*     */   implements DistributedDatabaseContact
/*     */ {
/*     */   private DDBaseImpl ddb;
/*     */   private DHTPluginContact contact;
/*     */   
/*     */   protected DDBaseContactImpl(DDBaseImpl _ddb, DHTPluginContact _contact)
/*     */   {
/*  59 */     this.ddb = _ddb;
/*  60 */     this.contact = _contact;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getID()
/*     */   {
/*  66 */     return this.contact.getID();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  72 */     return this.contact.getName();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getVersion()
/*     */   {
/*  78 */     return this.contact.getProtocolVersion();
/*     */   }
/*     */   
/*     */ 
/*     */   public InetSocketAddress getAddress()
/*     */   {
/*  84 */     return this.contact.getAddress();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getDHT()
/*     */   {
/*  90 */     return this.contact.getNetwork() == 1 ? 2 : 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isAlive(long timeout)
/*     */   {
/*  97 */     return this.contact.isAlive(timeout);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void isAlive(long timeout, final DistributedDatabaseListener listener)
/*     */   {
/* 106 */     this.contact.isAlive(timeout, new DHTPluginOperationListener()
/*     */     {
/*     */       public void starts(byte[] key) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public boolean diversified()
/*     */       {
/* 119 */         return true;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void valueRead(DHTPluginContact originator, DHTPluginValue value) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void valueWritten(DHTPluginContact target, DHTPluginValue value) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void complete(byte[] key, final boolean timeout_occurred)
/*     */       {
/* 141 */         listener.event(new DistributedDatabaseEvent()
/*     */         {
/*     */ 
/*     */           public int getType()
/*     */           {
/*     */ 
/* 147 */             return timeout_occurred ? 5 : 4;
/*     */           }
/*     */           
/*     */ 
/*     */           public DistributedDatabaseKey getKey()
/*     */           {
/* 153 */             return null;
/*     */           }
/*     */           
/*     */ 
/*     */           public DistributedDatabaseKeyStats getKeyStats()
/*     */           {
/* 159 */             return null;
/*     */           }
/*     */           
/*     */ 
/*     */           public DistributedDatabaseValue getValue()
/*     */           {
/* 165 */             return null;
/*     */           }
/*     */           
/*     */ 
/*     */           public DistributedDatabaseContact getContact()
/*     */           {
/* 171 */             return DDBaseContactImpl.this;
/*     */           }
/*     */         });
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isOrHasBeenLocal()
/*     */   {
/* 181 */     return this.contact.isOrHasBeenLocal();
/*     */   }
/*     */   
/*     */ 
/*     */   public Map<String, Object> exportToMap()
/*     */   {
/* 187 */     return this.contact.exportToMap();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean openTunnel()
/*     */   {
/* 193 */     return this.contact.openTunnel() != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DistributedDatabaseValue call(DistributedDatabaseProgressListener listener, DistributedDatabaseTransferType type, DistributedDatabaseValue data, long timeout)
/*     */     throws DistributedDatabaseException
/*     */   {
/* 205 */     return this.ddb.call(this, listener, type, data, timeout);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void write(DistributedDatabaseProgressListener listener, DistributedDatabaseTransferType type, DistributedDatabaseKey key, DistributedDatabaseValue value, long timeout)
/*     */     throws DistributedDatabaseException
/*     */   {
/* 218 */     this.ddb.write(this, listener, type, key, value, timeout);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DistributedDatabaseValue read(DistributedDatabaseProgressListener listener, DistributedDatabaseTransferType type, DistributedDatabaseKey key, long timeout)
/*     */     throws DistributedDatabaseException
/*     */   {
/* 230 */     return this.ddb.read(this, listener, type, key, timeout);
/*     */   }
/*     */   
/*     */ 
/*     */   protected DHTPluginContact getContact()
/*     */   {
/* 236 */     return this.contact;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ddb/DDBaseContactImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */