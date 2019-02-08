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
/*    */ public class LogRelationUtils
/*    */ {
/*    */   public static Object queryForClass(Object[] objects, Class cla)
/*    */   {
/* 28 */     if (objects == null) {
/* 29 */       return null;
/*    */     }
/*    */     
/*    */ 
/* 33 */     for (int i = 0; i < objects.length; i++) {
/* 34 */       Object object = objects[i];
/* 35 */       if (cla.isInstance(object)) {
/* 36 */         return object;
/*    */       }
/*    */     }
/*    */     
/*    */ 
/* 41 */     for (int i = 0; i < objects.length; i++) {
/* 42 */       Object object = objects[i];
/* 43 */       if ((object instanceof LogRelation)) {
/* 44 */         LogRelation logRelation = (LogRelation)object;
/* 45 */         Object answer = logRelation.queryForClass(cla);
/* 46 */         if (answer != null) {
/* 47 */           return answer;
/*    */         }
/*    */       }
/*    */     }
/*    */     
/* 52 */     return null;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/logging/LogRelationUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */