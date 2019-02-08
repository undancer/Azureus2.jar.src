/*     */ package com.aelitis.azureus.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.bouncycastle.util.encoders.Base64;
/*     */ import org.json.simple.JSONArray;
/*     */ import org.json.simple.JSONObject;
/*     */ import org.json.simple.JSONValue;
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
/*     */ public class JSONUtils
/*     */ {
/*     */   public static Map decodeJSON(String json)
/*     */   {
/*     */     try
/*     */     {
/*  49 */       Object object = JSONValue.parse(json);
/*  50 */       if ((object instanceof Map)) {
/*  51 */         return (Map)object;
/*     */       }
/*     */       
/*  54 */       Map map = new HashMap();
/*  55 */       map.put("value", object);
/*  56 */       return map;
/*     */     } catch (Throwable t) {
/*  58 */       Debug.out("Warning: Bad JSON String: " + json, t); }
/*  59 */     return null;
/*     */   }
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
/*     */   public static JSONObject encodeToJSONObject(Map map)
/*     */   {
/*  74 */     JSONObject newMap = new JSONObject((int)(map.size() * 1.5D));
/*     */     
/*  76 */     for (Map.Entry<String, Object> entry : map.entrySet()) {
/*  77 */       String key = (String)entry.getKey();
/*  78 */       Object value = entry.getValue();
/*     */       
/*  80 */       if ((value instanceof byte[])) {
/*  81 */         key = key + ".B64";
/*  82 */         value = Base64.encode((byte[])value);
/*     */       }
/*     */       
/*  85 */       value = coerce(value);
/*     */       
/*  87 */       newMap.put(key, value);
/*     */     }
/*  89 */     return newMap;
/*     */   }
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
/*     */   public static String encodeToJSON(Map map)
/*     */   {
/* 104 */     JSONObject jobj = encodeToJSONObject(map);
/* 105 */     StringBuilder sb = new StringBuilder(8192);
/* 106 */     jobj.toString(sb);
/* 107 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public static String encodeToJSON(Collection list) {
/* 111 */     return encodeToJSONArray(list).toString();
/*     */   }
/*     */   
/*     */   private static Object coerce(Object value) {
/* 115 */     if ((value instanceof Map)) {
/* 116 */       value = encodeToJSONObject((Map)value);
/* 117 */     } else if ((value instanceof List)) {
/* 118 */       value = encodeToJSONArray((List)value);
/* 119 */     } else if ((value instanceof Object[])) {
/* 120 */       Object[] array = (Object[])value;
/* 121 */       value = encodeToJSONArray(Arrays.asList(array));
/* 122 */     } else if ((value instanceof byte[])) {
/*     */       try {
/* 124 */         value = new String((byte[])value, "utf-8");
/*     */       }
/*     */       catch (UnsupportedEncodingException e) {}
/* 127 */     } else if ((value instanceof boolean[])) {
/* 128 */       boolean[] array = (boolean[])value;
/* 129 */       ArrayList<Object> list = new ArrayList();
/* 130 */       for (boolean b : array) {
/* 131 */         list.add(Boolean.valueOf(b));
/*     */       }
/* 133 */       value = encodeToJSONArray(list);
/* 134 */     } else if ((value instanceof long[])) {
/* 135 */       long[] array = (long[])value;
/* 136 */       ArrayList<Object> list = new ArrayList();
/* 137 */       for (long b : array) {
/* 138 */         list.add(Long.valueOf(b));
/*     */       }
/* 140 */       value = encodeToJSONArray(list);
/* 141 */     } else if ((value instanceof int[])) {
/* 142 */       int[] array = (int[])value;
/* 143 */       ArrayList<Object> list = new ArrayList();
/* 144 */       for (int b : array) {
/* 145 */         list.add(Integer.valueOf(b));
/*     */       }
/* 147 */       value = encodeToJSONArray(list);
/*     */     }
/* 149 */     return value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static JSONArray encodeToJSONArray(Collection list)
/*     */   {
/* 159 */     JSONArray newList = new JSONArray(list.size());
/*     */     
/* 161 */     for (Object value : list)
/*     */     {
/* 163 */       newList.add(coerce(value));
/*     */     }
/*     */     
/* 166 */     return newList;
/*     */   }
/*     */   
/*     */   public static void main(String[] args)
/*     */   {
/* 171 */     Map mapBefore = new HashMap();
/* 172 */     byte[] b = { 0, 1, 2 };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 177 */     mapBefore.put("Hi", b);
/* 178 */     String jsonByteArray = encodeToJSON(mapBefore);
/* 179 */     System.out.println(jsonByteArray);
/* 180 */     Map mapAfter = decodeJSON(jsonByteArray);
/* 181 */     b = MapUtils.getMapByteArray(mapAfter, "Hi", null);
/* 182 */     System.out.println(b.length);
/* 183 */     for (int i = 0; i < b.length; i++) {
/* 184 */       byte c = b[i];
/* 185 */       System.out.println("--" + c);
/*     */     }
/*     */     
/* 188 */     Map map = new HashMap();
/* 189 */     map.put("Test", "TestValue");
/* 190 */     Map map2 = new HashMap();
/* 191 */     map2.put("Test2", "test2value");
/* 192 */     map.put("TestMap", map2);
/*     */     
/* 194 */     List list = new ArrayList();
/* 195 */     list.add(new Long(5L));
/* 196 */     list.add("five");
/* 197 */     map2.put("ListTest", list);
/*     */     
/* 199 */     Map map3 = new HashMap();
/* 200 */     map3.put("Test3", "test3value");
/* 201 */     list.add(map3);
/*     */     
/* 203 */     System.out.println(encodeToJSON(map));
/* 204 */     System.out.println(encodeToJSON(list));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/util/JSONUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */