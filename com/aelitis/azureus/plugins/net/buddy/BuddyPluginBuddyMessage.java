/*     */ package com.aelitis.azureus.plugins.net.buddy;
/*     */ 
/*     */ import java.util.Map;
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
/*     */ public class BuddyPluginBuddyMessage
/*     */ {
/*     */   private BuddyPluginBuddyMessageHandler handler;
/*     */   private int id;
/*     */   private int subsystem;
/*     */   private int timeout;
/*     */   private long create_time;
/*     */   private boolean deleted;
/*     */   
/*     */   protected BuddyPluginBuddyMessage(BuddyPluginBuddyMessageHandler _handler, int _id, int _subsystem, Map _request, int _timeout, long _create_time)
/*     */     throws BuddyPluginException
/*     */   {
/*  46 */     this.handler = _handler;
/*  47 */     this.id = _id;
/*  48 */     this.subsystem = _subsystem;
/*  49 */     this.timeout = _timeout;
/*  50 */     this.create_time = _create_time;
/*     */     
/*  52 */     if (_request != null)
/*     */     {
/*  54 */       this.handler.writeRequest(this, _request);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public BuddyPluginBuddy getBuddy()
/*     */   {
/*  61 */     return this.handler.getBuddy();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getID()
/*     */   {
/*  67 */     return this.id;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSubsystem()
/*     */   {
/*  73 */     return this.subsystem;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Map getRequest()
/*     */     throws BuddyPluginException
/*     */   {
/*  81 */     return this.handler.readRequest(this);
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
/*     */   protected Map getReply()
/*     */     throws BuddyPluginException
/*     */   {
/*  95 */     return this.handler.readReply(this);
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getTimeout()
/*     */   {
/* 101 */     return this.timeout;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getCreateTime()
/*     */   {
/* 107 */     return this.create_time;
/*     */   }
/*     */   
/*     */ 
/*     */   public void delete()
/*     */   {
/* 113 */     this.deleted = true;
/*     */     
/* 115 */     this.handler.deleteMessage(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDeleted()
/*     */   {
/* 121 */     return this.deleted;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/BuddyPluginBuddyMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */