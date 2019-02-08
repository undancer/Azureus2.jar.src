/*     */ package com.aelitis.azureus.core.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ public class CaseSensitiveFileMap
/*     */ {
/*     */   final Map map;
/*     */   
/*     */   public CaseSensitiveFileMap()
/*     */   {
/*  28 */     this.map = new HashMap();
/*     */   }
/*     */   
/*     */ 
/*     */   public File get(File key)
/*     */   {
/*  34 */     return (File)this.map.get(new wrapper(key));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void put(File key, File value)
/*     */   {
/*  42 */     this.map.put(new wrapper(key), value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void remove(File key)
/*     */   {
/*  49 */     this.map.remove(new wrapper(key));
/*     */   }
/*     */   
/*     */ 
/*     */   public Iterator keySetIterator()
/*     */   {
/*  55 */     new Iterator()
/*     */     {
/*     */ 
/*  58 */       private final Iterator iterator = CaseSensitiveFileMap.this.map.keySet().iterator();
/*     */       
/*     */ 
/*     */       public boolean hasNext()
/*     */       {
/*  63 */         return this.iterator.hasNext();
/*     */       }
/*     */       
/*     */ 
/*     */       public Object next()
/*     */       {
/*  69 */         CaseSensitiveFileMap.wrapper wrap = (CaseSensitiveFileMap.wrapper)this.iterator.next();
/*     */         
/*  71 */         return wrap.getFile();
/*     */       }
/*     */       
/*     */ 
/*     */       public void remove()
/*     */       {
/*  77 */         this.iterator.remove();
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static class wrapper
/*     */   {
/*     */     private final File file;
/*     */     
/*     */     private final String file_str;
/*     */     
/*     */ 
/*     */     protected wrapper(File _file)
/*     */     {
/*  93 */       this.file = _file;
/*  94 */       this.file_str = this.file.toString();
/*     */     }
/*     */     
/*     */ 
/*     */     protected File getFile()
/*     */     {
/* 100 */       return this.file;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean equals(Object other)
/*     */     {
/* 107 */       if ((other instanceof wrapper))
/*     */       {
/* 109 */         return this.file_str.equals(((wrapper)other).file_str);
/*     */       }
/*     */       
/* 112 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 118 */       return this.file_str.hashCode();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/CaseSensitiveFileMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */