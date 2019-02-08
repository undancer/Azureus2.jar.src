/*    */ package org.gudy.azureus2.core3.util;
/*    */ 
/*    */ import java.net.UnknownHostException;
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
/*    */ public class HostNameToIPResolverException
/*    */   extends UnknownHostException
/*    */ {
/* 33 */   protected boolean is_anonymous = false;
/*    */   
/*    */ 
/*    */ 
/*    */   protected HostNameToIPResolverException(String msg)
/*    */   {
/* 39 */     super(msg);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   protected HostNameToIPResolverException(String msg, boolean anonymous)
/*    */   {
/* 47 */     super(msg);
/*    */     
/* 49 */     this.is_anonymous = anonymous;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isAnonymous()
/*    */   {
/* 55 */     return this.is_anonymous;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/HostNameToIPResolverException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */