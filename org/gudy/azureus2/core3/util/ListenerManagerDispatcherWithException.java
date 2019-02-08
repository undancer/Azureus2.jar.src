/*    */ package org.gudy.azureus2.core3.util;
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
/*    */ 
/*    */ 
/*    */ public abstract class ListenerManagerDispatcherWithException
/*    */   extends ListenerManagerDispatcher
/*    */ {
/*    */   public void dispatch(Object _listener, int type, Object value)
/*    */   {
/* 39 */     throw new RuntimeException("ListenerManagerDispatcherWithException: you must invoke dispatchWithException");
/*    */   }
/*    */   
/*    */   public abstract void dispatchWithException(Object paramObject1, int paramInt, Object paramObject2)
/*    */     throws Throwable;
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/ListenerManagerDispatcherWithException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */