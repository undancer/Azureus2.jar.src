/*    */ package org.json.simple;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.Iterator;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JSONArray
/*    */   extends ArrayList<Object>
/*    */ {
/*    */   public JSONArray() {}
/*    */   
/*    */   public JSONArray(Collection<Object> arg0)
/*    */   {
/* 21 */     super(arg0);
/*    */   }
/*    */   
/*    */   public JSONArray(int initialCapacity) {
/* 25 */     super(initialCapacity);
/*    */   }
/*    */   
/*    */   public String toString() {
/* 29 */     ItemList list = new ItemList();
/*    */     
/* 31 */     Iterator<Object> iter = iterator();
/*    */     
/* 33 */     while (iter.hasNext()) {
/* 34 */       Object value = iter.next();
/* 35 */       if ((value instanceof String)) {
/* 36 */         list.add("\"" + JSONObject.escape((String)value) + "\"");
/*    */       }
/*    */       else
/* 39 */         list.add(String.valueOf(value));
/*    */     }
/* 41 */     return "[" + list.toString() + "]";
/*    */   }
/*    */   
/*    */   public void toString(StringBuilder sb) {
/* 45 */     sb.append("[");
/*    */     
/* 47 */     Iterator<Object> iter = iterator();
/*    */     
/* 49 */     boolean first = true;
/* 50 */     while (iter.hasNext()) {
/* 51 */       if (first) {
/* 52 */         first = false;
/*    */       } else {
/* 54 */         sb.append(",");
/*    */       }
/* 56 */       Object value = iter.next();
/* 57 */       if ((value instanceof String)) {
/* 58 */         sb.append("\"");
/* 59 */         JSONObject.escape(sb, (String)value);
/* 60 */         sb.append("\"");
/* 61 */       } else if ((value instanceof JSONObject)) {
/* 62 */         ((JSONObject)value).toString(sb);
/* 63 */       } else if ((value instanceof JSONArray)) {
/* 64 */         ((JSONArray)value).toString(sb);
/*    */       } else {
/* 66 */         sb.append(String.valueOf(value));
/*    */       }
/*    */     }
/*    */     
/* 70 */     sb.append("]");
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/json/simple/JSONArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */