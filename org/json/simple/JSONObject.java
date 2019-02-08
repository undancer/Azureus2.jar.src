/*     */ package org.json.simple;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.util.LightHashMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JSONObject
/*     */   extends LightHashMap<String, Object>
/*     */ {
/*     */   public JSONObject() {}
/*     */   
/*     */   public JSONObject(int initialCapacity, float loadFactor)
/*     */   {
/*  22 */     super(initialCapacity, loadFactor);
/*     */   }
/*     */   
/*     */   public JSONObject(int initialCapacity) {
/*  26 */     super(initialCapacity);
/*     */   }
/*     */   
/*     */   public JSONObject(Map<String, Object> arg0) {
/*  30 */     super(arg0);
/*     */   }
/*     */   
/*     */   public String toString() {
/*  34 */     ItemList list = new ItemList();
/*  35 */     Iterator<Map.Entry<String, Object>> iter = entrySet().iterator();
/*     */     
/*  37 */     while (iter.hasNext()) {
/*  38 */       Map.Entry<String, Object> entry = (Map.Entry)iter.next();
/*  39 */       list.add(toString(((String)entry.getKey()).toString(), entry.getValue()));
/*     */     }
/*  41 */     return "{" + list.toString() + "}";
/*     */   }
/*     */   
/*     */   public void toString(StringBuilder sb)
/*     */   {
/*  46 */     sb.append("{");
/*     */     
/*  48 */     Iterator iter = entrySet().iterator();
/*     */     
/*  50 */     boolean first = true;
/*     */     
/*  52 */     while (iter.hasNext()) {
/*  53 */       if (first) {
/*  54 */         first = false;
/*     */       } else {
/*  56 */         sb.append(",");
/*     */       }
/*  58 */       Map.Entry entry = (Map.Entry)iter.next();
/*  59 */       toString(sb, entry.getKey().toString(), entry.getValue());
/*     */     }
/*     */     
/*  62 */     sb.append("}");
/*     */   }
/*     */   
/*     */   public static String toString(String key, Object value) {
/*  66 */     StringBuilder sb = new StringBuilder();
/*     */     
/*  68 */     sb.append("\"");
/*  69 */     sb.append(escape(key));
/*  70 */     sb.append("\":");
/*  71 */     if (value == null) {
/*  72 */       sb.append("null");
/*  73 */       return sb.toString();
/*     */     }
/*     */     
/*  76 */     if ((value instanceof String)) {
/*  77 */       sb.append("\"");
/*  78 */       sb.append(escape((String)value));
/*  79 */       sb.append("\"");
/*     */     }
/*     */     else {
/*  82 */       sb.append(value); }
/*  83 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public static void toString(StringBuilder sb, String key, Object value) {
/*  87 */     sb.append("\"");
/*  88 */     escape(sb, key);
/*  89 */     sb.append("\":");
/*  90 */     if (value == null) {
/*  91 */       sb.append("null");
/*  92 */       return;
/*     */     }
/*     */     
/*  95 */     if ((value instanceof String)) {
/*  96 */       sb.append("\"");
/*  97 */       escape(sb, (String)value);
/*  98 */       sb.append("\"");
/*  99 */     } else if ((value instanceof JSONObject)) {
/* 100 */       ((JSONObject)value).toString(sb);
/* 101 */     } else if ((value instanceof JSONArray)) {
/* 102 */       ((JSONArray)value).toString(sb);
/*     */     } else {
/* 104 */       sb.append(String.valueOf(value));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String escape(String s)
/*     */   {
/* 114 */     if (s == null)
/* 115 */       return null;
/* 116 */     StringBuilder sb = new StringBuilder();
/* 117 */     for (int i = 0; i < s.length(); i++) {
/* 118 */       char ch = s.charAt(i);
/* 119 */       switch (ch) {
/*     */       case '"': 
/* 121 */         sb.append("\\\"");
/* 122 */         break;
/*     */       case '\\': 
/* 124 */         sb.append("\\\\");
/* 125 */         break;
/*     */       case '\b': 
/* 127 */         sb.append("\\b");
/* 128 */         break;
/*     */       case '\f': 
/* 130 */         sb.append("\\f");
/* 131 */         break;
/*     */       case '\n': 
/* 133 */         sb.append("\\n");
/* 134 */         break;
/*     */       case '\r': 
/* 136 */         sb.append("\\r");
/* 137 */         break;
/*     */       case '\t': 
/* 139 */         sb.append("\\t");
/* 140 */         break;
/*     */       case '/': 
/* 142 */         sb.append("\\/");
/* 143 */         break;
/*     */       default: 
/* 145 */         if ((ch >= 0) && (ch <= '\037')) {
/* 146 */           String ss = Integer.toHexString(ch);
/* 147 */           sb.append("\\u");
/* 148 */           for (int k = 0; k < 4 - ss.length(); k++) {
/* 149 */             sb.append('0');
/*     */           }
/* 151 */           sb.append(ss.toUpperCase());
/*     */         }
/*     */         else {
/* 154 */           sb.append(ch);
/*     */         }
/*     */         break; }
/*     */     }
/* 158 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public static void escape(StringBuilder sb, String s) {
/* 162 */     if (s == null) {
/* 163 */       sb.append((String)null);
/*     */     } else {
/* 165 */       for (int i = 0; i < s.length(); i++) {
/* 166 */         char ch = s.charAt(i);
/* 167 */         switch (ch) {
/*     */         case '"': 
/* 169 */           sb.append("\\\"");
/* 170 */           break;
/*     */         case '\\': 
/* 172 */           sb.append("\\\\");
/* 173 */           break;
/*     */         case '\b': 
/* 175 */           sb.append("\\b");
/* 176 */           break;
/*     */         case '\f': 
/* 178 */           sb.append("\\f");
/* 179 */           break;
/*     */         case '\n': 
/* 181 */           sb.append("\\n");
/* 182 */           break;
/*     */         case '\r': 
/* 184 */           sb.append("\\r");
/* 185 */           break;
/*     */         case '\t': 
/* 187 */           sb.append("\\t");
/* 188 */           break;
/*     */         case '/': 
/* 190 */           sb.append("\\/");
/* 191 */           break;
/*     */         default: 
/* 193 */           if ((ch >= 0) && (ch <= '\037')) {
/* 194 */             String ss = Integer.toHexString(ch);
/* 195 */             sb.append("\\u");
/* 196 */             for (int k = 0; k < 4 - ss.length(); k++) {
/* 197 */               sb.append('0');
/*     */             }
/* 199 */             sb.append(ss.toUpperCase());
/*     */           }
/*     */           else {
/* 202 */             sb.append(ch);
/*     */           }
/*     */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/json/simple/JSONObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */