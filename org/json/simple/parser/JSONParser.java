/*     */ package org.json.simple.parser;
/*     */ 
/*     */ import java.io.Reader;
/*     */ import java.util.Stack;
/*     */ import org.json.simple.JSONArray;
/*     */ import org.json.simple.JSONObject;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JSONParser
/*     */ {
/*     */   public static final int S_INIT = 0;
/*     */   public static final int S_IN_FINISHED_VALUE = 1;
/*     */   public static final int S_IN_OBJECT = 2;
/*     */   public static final int S_IN_ARRAY = 3;
/*     */   public static final int S_PASSED_PAIR_KEY = 4;
/*     */   public static final int S_IN_ERROR = -1;
/*     */   
/*     */   private int peekStatus(Stack statusStack)
/*     */   {
/*  27 */     if (statusStack.size() == 0)
/*  28 */       return -1;
/*  29 */     Integer status = (Integer)statusStack.peek();
/*  30 */     return status.intValue();
/*     */   }
/*     */   
/*     */   public Object parse(Reader in) throws Exception {
/*  34 */     Stack statusStack = new Stack();
/*  35 */     Stack valueStack = new Stack();
/*  36 */     Yylex lexer = new Yylex(in);
/*  37 */     Yytoken token = null;
/*  38 */     int status = 0;
/*     */     try
/*     */     {
/*     */       do {
/*  42 */         token = lexer.yylex();
/*  43 */         if (token == null)
/*  44 */           token = new Yytoken(-1, null);
/*  45 */         switch (status) {
/*     */         case 0: 
/*  47 */           switch (token.type) {
/*     */           case 0: 
/*  49 */             status = 1;
/*  50 */             statusStack.push(new Integer(status));
/*  51 */             valueStack.push(token.value);
/*  52 */             break;
/*     */           case 1: 
/*  54 */             status = 2;
/*  55 */             statusStack.push(new Integer(status));
/*  56 */             valueStack.push(new JSONObject());
/*  57 */             break;
/*     */           case 3: 
/*  59 */             status = 3;
/*  60 */             statusStack.push(new Integer(status));
/*  61 */             valueStack.push(new JSONArray());
/*  62 */             break;
/*     */           case 2: default: 
/*  64 */             status = -1;
/*     */           }
/*  66 */           break;
/*     */         
/*     */         case 1: 
/*  69 */           if (token.type == -1) {
/*  70 */             return valueStack.pop();
/*     */           }
/*  72 */           return null;
/*     */         
/*     */         case 2: 
/*  75 */           switch (token.type) {
/*     */           case 5: 
/*     */             break;
/*     */           case 0: 
/*  79 */             if ((token.value instanceof String)) {
/*  80 */               String key = (String)token.value;
/*  81 */               valueStack.push(key);
/*  82 */               status = 4;
/*  83 */               statusStack.push(new Integer(status));
/*     */             }
/*     */             else {
/*  86 */               status = -1;
/*     */             }
/*  88 */             break;
/*     */           case 2: 
/*  90 */             if (valueStack.size() > 1) {
/*  91 */               statusStack.pop();
/*  92 */               JSONObject map = (JSONObject)valueStack.pop();
/*  93 */               map.compactify(-0.9F);
/*  94 */               status = peekStatus(statusStack);
/*     */             }
/*     */             else {
/*  97 */               status = 1;
/*     */             }
/*  99 */             break;
/*     */           default: 
/* 101 */             status = -1; }
/* 102 */           break;
/*     */         case 4: 
/*     */           String key;
/*     */           
/*     */           JSONObject parent;
/* 107 */           switch (token.type) {
/*     */           case 6: 
/*     */             break;
/*     */           case 0: 
/* 111 */             statusStack.pop();
/* 112 */             key = (String)valueStack.pop();
/* 113 */             parent = (JSONObject)valueStack.peek();
/* 114 */             parent.put(key, token.value);
/* 115 */             status = peekStatus(statusStack);
/* 116 */             break;
/*     */           case 3: 
/* 118 */             statusStack.pop();
/* 119 */             key = (String)valueStack.pop();
/* 120 */             parent = (JSONObject)valueStack.peek();
/* 121 */             JSONArray newArray = new JSONArray();
/* 122 */             parent.put(key, newArray);
/* 123 */             status = 3;
/* 124 */             statusStack.push(new Integer(status));
/* 125 */             valueStack.push(newArray);
/* 126 */             break;
/*     */           case 1: 
/* 128 */             statusStack.pop();
/* 129 */             key = (String)valueStack.pop();
/* 130 */             parent = (JSONObject)valueStack.peek();
/* 131 */             JSONObject newObject = new JSONObject();
/* 132 */             parent.put(key, newObject);
/* 133 */             status = 2;
/* 134 */             statusStack.push(new Integer(status));
/* 135 */             valueStack.push(newObject);
/* 136 */             break;
/*     */           case 2: case 4: case 5: default: 
/* 138 */             status = -1;
/*     */           }
/* 140 */           break;
/*     */         case 3: 
/*     */           JSONArray val;
/* 143 */           switch (token.type) {
/*     */           case 5: 
/*     */             break;
/*     */           case 0: 
/* 147 */             val = (JSONArray)valueStack.peek();
/* 148 */             val.add(token.value);
/* 149 */             break;
/*     */           case 4: 
/* 151 */             if (valueStack.size() > 1) {
/* 152 */               statusStack.pop();
/* 153 */               valueStack.pop();
/* 154 */               status = peekStatus(statusStack);
/*     */             }
/*     */             else {
/* 157 */               status = 1;
/*     */             }
/* 159 */             break;
/*     */           case 1: 
/* 161 */             val = (JSONArray)valueStack.peek();
/* 162 */             JSONObject newObject = new JSONObject();
/* 163 */             val.add(newObject);
/* 164 */             status = 2;
/* 165 */             statusStack.push(new Integer(status));
/* 166 */             valueStack.push(newObject);
/* 167 */             break;
/*     */           case 3: 
/* 169 */             val = (JSONArray)valueStack.peek();
/* 170 */             JSONArray newArray = new JSONArray();
/* 171 */             val.add(newArray);
/* 172 */             status = 3;
/* 173 */             statusStack.push(new Integer(status));
/* 174 */             valueStack.push(newArray);
/* 175 */             break;
/*     */           case 2: default: 
/* 177 */             status = -1;
/*     */           }
/* 179 */           break;
/*     */         case -1: 
/* 181 */           return null;
/*     */         }
/* 183 */         if (status == -1)
/* 184 */           return null;
/* 185 */       } while (token.type != -1);
/*     */     }
/*     */     catch (Exception e) {
/* 188 */       throw e;
/*     */     }
/* 190 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/json/simple/parser/JSONParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */