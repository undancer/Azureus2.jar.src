/*    */ package com.aelitis.azureus.core.metasearch.impl.web;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.StringTokenizer;
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
/*    */ public class CookieParser
/*    */ {
/*    */   public static boolean cookiesContain(String[] requiredCookies, String cookies)
/*    */   {
/* 27 */     if (cookies == null) return false;
/* 28 */     boolean[] cookieFound = new boolean[requiredCookies.length];
/*    */     
/* 30 */     String[] names = getCookiesNames(cookies);
/*    */     
/* 32 */     for (int j = 0; j < names.length; j++) {
/* 33 */       String cookieName = names[j];
/* 34 */       for (int i = 0; i < requiredCookies.length; i++) {
/* 35 */         if (requiredCookies[i].equals(cookieName)) {
/* 36 */           cookieFound[i] = true;
/*    */         }
/*    */       }
/*    */     }
/*    */     
/* 41 */     for (int i = 0; i < cookieFound.length; i++) {
/* 42 */       if (cookieFound[i] == 0) { return false;
/*    */       }
/*    */     }
/* 45 */     return true;
/*    */   }
/*    */   
/*    */   public static String[] getCookiesNames(String cookies) {
/* 49 */     if (cookies == null) { return new String[0];
/*    */     }
/* 51 */     StringTokenizer st = new StringTokenizer(cookies, "; ");
/* 52 */     List names = new ArrayList();
/*    */     
/* 54 */     while (st.hasMoreTokens()) {
/* 55 */       String cookie = st.nextToken();
/* 56 */       int separator = cookie.indexOf("=");
/* 57 */       if (separator > -1) {
/* 58 */         names.add(cookie.substring(0, separator));
/*    */       }
/*    */     }
/*    */     
/* 62 */     String[] result = (String[])names.toArray(new String[names.size()]);
/*    */     
/* 64 */     return result;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/metasearch/impl/web/CookieParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */