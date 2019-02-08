/*     */ package org.gudy.azureus2.pluginsimpl.remote;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RPRequest
/*     */   implements Serializable
/*     */ {
/*     */   public RPObject object;
/*     */   public String method;
/*     */   public Object[] params;
/*     */   protected transient PluginInterface plugin_interface;
/*     */   protected transient LoggerChannel channel;
/*     */   public long connection_id;
/*     */   public long request_id;
/*     */   protected transient String client_ip;
/*     */   
/*     */   public RPRequest() {}
/*     */   
/*     */   public RPRequest(RPObject _object, String _method, Object[] _params)
/*     */   {
/*  63 */     this.object = _object;
/*  64 */     this.method = _method;
/*  65 */     this.params = _params;
/*     */     
/*  67 */     if (this.object != null)
/*     */     {
/*  69 */       RPPluginInterface pi = this.object.getDispatcher().getPlugin();
/*     */       
/*  71 */       this.connection_id = pi._getConectionId();
/*  72 */       this.request_id = pi._getNextRequestId();
/*  73 */       this.plugin_interface = ((PluginInterface)pi._getDelegate());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setClientIP(String str)
/*     */   {
/*  81 */     this.client_ip = str;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getClientIP()
/*     */   {
/*  87 */     return this.client_ip;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getConnectionId()
/*     */   {
/*  93 */     return this.connection_id;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getRequestId()
/*     */   {
/*  99 */     return this.request_id;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 105 */     return "object=" + this.object + ", method=" + this.method + ", params=" + this.params;
/*     */   }
/*     */   
/*     */ 
/*     */   public RPObject getObject()
/*     */   {
/* 111 */     return this.object;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getMethod()
/*     */   {
/* 117 */     return this.method;
/*     */   }
/*     */   
/*     */ 
/*     */   public Object[] getParams()
/*     */   {
/* 123 */     return this.params;
/*     */   }
/*     */   
/*     */   public PluginInterface getPluginInterface() {
/* 127 */     return this.plugin_interface;
/*     */   }
/*     */   
/*     */   public void setPluginInterface(PluginInterface pi) {
/* 131 */     this.plugin_interface = pi;
/*     */   }
/*     */   
/*     */   public LoggerChannel getRPLoggerChannel() {
/* 135 */     return this.channel;
/*     */   }
/*     */   
/*     */   public void setRPLoggerChannel(LoggerChannel channel) {
/* 139 */     this.channel = channel;
/*     */   }
/*     */   
/*     */   public RPPluginInterface createRemotePluginInterface(PluginInterface pi)
/*     */   {
/* 144 */     return RPPluginInterface.create(pi);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/RPRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */