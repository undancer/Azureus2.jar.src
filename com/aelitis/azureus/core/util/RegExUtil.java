/*     */ package com.aelitis.azureus.core.util;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Stack;
/*     */ import java.util.regex.Pattern;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class RegExUtil
/*     */ {
/*  31 */   private static final ThreadLocal<Map<String, Object[]>> tls = new ThreadLocal()
/*     */   {
/*     */ 
/*     */     public Map<String, Object[]> initialValue()
/*     */     {
/*     */ 
/*  37 */       return new HashMap();
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Pattern getCachedPattern(String namespace, String pattern)
/*     */   {
/*  46 */     return getCachedPattern(namespace, pattern, 0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Pattern getCachedPattern(String namespace, String pattern, int flags)
/*     */   {
/*  55 */     Map<String, Object[]> map = (Map)tls.get();
/*     */     
/*  57 */     Object[] entry = (Object[])map.get(namespace);
/*     */     
/*  59 */     if ((entry == null) || (!pattern.equals((String)entry[0])))
/*     */     {
/*  61 */       Pattern result = Pattern.compile(pattern, flags);
/*     */       
/*  63 */       map.put(namespace, new Object[] { pattern, result });
/*     */       
/*  65 */       return result;
/*     */     }
/*     */     
/*     */ 
/*  69 */     return (Pattern)entry[1];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean mightBeEvil(String str)
/*     */   {
/*  79 */     if (!str.contains(")"))
/*     */     {
/*  81 */       return false;
/*     */     }
/*     */     
/*  84 */     char[] chars = str.toCharArray();
/*     */     
/*  86 */     Stack<Integer> stack = new Stack();
/*     */     
/*  88 */     for (int i = 0; i < chars.length; i++)
/*     */     {
/*  90 */       char c = chars[i];
/*     */       
/*  92 */       if (c == '(')
/*     */       {
/*  94 */         stack.push(Integer.valueOf(i + 1));
/*     */       }
/*  96 */       else if (c == ')')
/*     */       {
/*  98 */         if (stack.isEmpty())
/*     */         {
/* 100 */           Debug.out("bracket un-matched in " + str + " - treating as evil");
/*     */           
/* 102 */           return true;
/*     */         }
/*     */         
/*     */ 
/* 106 */         int start = ((Integer)stack.pop()).intValue();
/*     */         
/* 108 */         if (i < chars.length - 1)
/*     */         {
/* 110 */           char next = chars[(i + 1)];
/*     */           
/* 112 */           if ((next == '*') || (next == '+') || (next == '{'))
/*     */           {
/* 114 */             for (int j = start; j < i; j++)
/*     */             {
/* 116 */               c = chars[j];
/*     */               
/* 118 */               if ("+*{|".indexOf(c) != -1)
/*     */               {
/* 120 */                 Debug.out("regular expression " + str + " might be evil due to '" + str.substring(start - 1, i + 2) + "'");
/*     */                 
/* 122 */                 return true;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 131 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/RegExUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */