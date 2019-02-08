/*    */ package org.gudy.azureus2.pluginsimpl.remote;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.gudy.azureus2.pluginsimpl.remote.rpexceptions.RPThrowableAsReplyException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RPReply
/*    */   implements Serializable
/*    */ {
/*    */   public Object response;
/* 41 */   protected transient Map properties = new HashMap();
/*    */   
/*    */ 
/*    */ 
/*    */   public RPReply(Object _response)
/*    */   {
/* 47 */     this.response = _response;
/*    */   }
/*    */   
/*    */   public Object getResponse() throws RPException {
/* 51 */     if ((this.response instanceof RPException)) {
/* 52 */       throw ((RPException)this.response);
/*    */     }
/* 54 */     if ((this.response instanceof Throwable)) {
/* 55 */       throw new RPThrowableAsReplyException((Throwable)this.response);
/*    */     }
/* 57 */     return this.response;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void setProperty(String name, String value)
/*    */   {
/* 65 */     this.properties.put(name, value);
/*    */   }
/*    */   
/*    */ 
/*    */   public Map getProperties()
/*    */   {
/* 71 */     return this.properties;
/*    */   }
/*    */   
/* 74 */   private Class response_class = null;
/* 75 */   public Class getResponseClass() { return this.response_class; }
/* 76 */   public void setResponseClass(Class c) { this.response_class = c; }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/RPReply.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */