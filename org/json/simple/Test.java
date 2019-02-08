/*    */ package org.json.simple;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Test
/*    */ {
/*    */   public static void main(String[] args)
/*    */     throws Exception
/*    */   {
/* 14 */     JSONArray array1 = new JSONArray();
/* 15 */     array1.add("abc\020a/");
/* 16 */     array1.add(new Integer(123));
/* 17 */     array1.add(new Double(122.22D));
/* 18 */     array1.add(Boolean.TRUE);
/* 19 */     System.out.println("======array1==========");
/* 20 */     System.out.println(array1);
/* 21 */     System.out.println();
/*    */     
/* 23 */     JSONObject obj1 = new JSONObject();
/* 24 */     obj1.put("name", "fang");
/* 25 */     obj1.put("age", new Integer(27));
/* 26 */     obj1.put("is_developer", Boolean.TRUE);
/* 27 */     obj1.put("weight", new Double(60.21D));
/* 28 */     obj1.put("array1", array1);
/* 29 */     System.out.println();
/*    */     
/* 31 */     System.out.println("======obj1 with array1===========");
/* 32 */     System.out.println(obj1);
/* 33 */     System.out.println();
/*    */     
/* 35 */     obj1.remove("array1");
/* 36 */     array1.add(obj1);
/* 37 */     System.out.println("======array1 with obj1========");
/* 38 */     System.out.println(array1);
/* 39 */     System.out.println();
/*    */     
/* 41 */     System.out.println("======parse to java========");
/*    */     
/* 43 */     String s = "[0,{\"1\":{\"2\":{\"3\":{\"4\":[5,{\"6\":7}]}}}}]";
/* 44 */     Object obj = JSONValue.parse(s);
/* 45 */     JSONArray array = (JSONArray)obj;
/* 46 */     System.out.println("======the 2nd element of array======");
/* 47 */     System.out.println(array.get(1));
/* 48 */     System.out.println();
/*    */     
/* 50 */     JSONObject obj2 = (JSONObject)array.get(1);
/* 51 */     System.out.println("======field \"1\"==========");
/* 52 */     System.out.println(obj2.get("1"));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/json/simple/Test.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */