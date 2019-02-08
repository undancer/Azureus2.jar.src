/*    */ package org.json.simple;
/*    */ 
/*    */ import com.aelitis.azureus.util.JSONUtils;
/*    */ import java.io.Reader;
/*    */ import java.io.StringReader;
/*    */ import java.util.Map;
/*    */ import org.json.simple.parser.JSONParser;
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
/*    */ public class JSONValue
/*    */ {
/*    */   public static Object parse(Reader in)
/*    */   {
/*    */     try
/*    */     {
/* 27 */       JSONParser parser = new JSONParser();
/* 28 */       return parser.parse(in);
/*    */     }
/*    */     catch (Exception e) {}
/* 31 */     return null;
/*    */   }
/*    */   
/*    */   public static Object parse(String s)
/*    */   {
/* 36 */     StringReader in = new StringReader(s);
/* 37 */     return parse(in);
/*    */   }
/*    */   
/*    */   public static String toJSONString(Object value) {
/* 41 */     if ((value instanceof Map)) {
/* 42 */       return JSONUtils.encodeToJSON((Map)value);
/*    */     }
/* 44 */     return "";
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/json/simple/JSONValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */