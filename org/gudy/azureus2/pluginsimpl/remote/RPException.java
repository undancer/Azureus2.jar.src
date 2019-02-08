/*    */ package org.gudy.azureus2.pluginsimpl.remote;
/*    */ 
/*    */ import org.gudy.azureus2.core3.util.Debug;
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
/*    */ public class RPException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static void checkErrorType(Throwable e)
/*    */   {
/* 34 */     if ((e instanceof RPException)) {
/* 35 */       Debug.outNoStack("RPExceptions chained together - stack trace, followed by other RPException stack trace.");
/* 36 */       Debug.outStackTrace();
/* 37 */       Debug.printStackTrace(e);
/* 38 */       throw new RuntimeException("cannot chain RPException instances together");
/*    */     }
/*    */   }
/*    */   
/*    */   public RPException(String str) {
/* 43 */     super(str);
/*    */   }
/*    */   
/*    */   public RPException(String str, Throwable e) {
/* 47 */     super(str, e);
/* 48 */     checkErrorType(e);
/*    */   }
/*    */   
/*    */   public RPException(Throwable e) {
/* 52 */     super(e);
/* 53 */     checkErrorType(e);
/*    */   }
/*    */   
/*    */   public String getRPType() {
/* 57 */     return null;
/*    */   }
/*    */   
/*    */   public Throwable getSerialisableObject() {
/* 61 */     Throwable t = getCause();
/* 62 */     if (t == null) {
/* 63 */       return this;
/*    */     }
/*    */     
/* 66 */     return t;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public Class getErrorClass()
/*    */   {
/* 73 */     Throwable t = getCause();
/* 74 */     if (t == null) return null;
/* 75 */     return t.getClass();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/RPException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */