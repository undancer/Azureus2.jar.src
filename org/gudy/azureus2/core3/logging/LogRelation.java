/*    */ package org.gudy.azureus2.core3.logging;
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
/*    */ public class LogRelation
/*    */ {
/*    */   public String getRelationText()
/*    */   {
/* 36 */     return toString();
/*    */   }
/*    */   
/*    */   protected final String propogatedRelationText(Object o) {
/* 40 */     if ((o instanceof LogRelation)) {
/* 41 */       return ((LogRelation)o).getRelationText();
/*    */     }
/* 43 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public Object[] getQueryableInterfaces()
/*    */   {
/* 53 */     return null;
/*    */   }
/*    */   
/*    */   public final Object queryForClass(Class c) {
/* 57 */     return queryForClass(c, getQueryableInterfaces());
/*    */   }
/*    */   
/* 60 */   private boolean running = false;
/*    */   
/*    */   protected final Object queryForClass(Class c, Object[] queryObjects) {
/* 63 */     if ((this.running) || (queryObjects == null)) {
/* 64 */       return null;
/*    */     }
/*    */     try {
/* 67 */       this.running = true;
/*    */       
/* 69 */       if (c.isInstance(this)) {
/* 70 */         return this;
/*    */       }
/*    */       
/* 73 */       for (int i = 0; i < queryObjects.length; i++) {
/* 74 */         if (c.isInstance(queryObjects[i])) {
/* 75 */           return queryObjects[i];
/*    */         }
/*    */       }
/*    */       
/* 79 */       for (int i = 0; i < queryObjects.length; i++) {
/* 80 */         if ((queryObjects[i] instanceof LogRelation)) {
/* 81 */           Object obj = ((LogRelation)queryObjects[i]).queryForClass(c, ((LogRelation)queryObjects[i]).getQueryableInterfaces());
/*    */           
/* 83 */           if (obj != null) {
/* 84 */             return obj;
/*    */           }
/*    */         }
/*    */       }
/* 88 */       return null;
/*    */     } finally {
/* 90 */       this.running = false;
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/logging/LogRelation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */