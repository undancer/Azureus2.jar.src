/*     */ package org.json.simple;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
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
/*     */ public class ItemList
/*     */ {
/*     */   private static final String sp = ",";
/*  21 */   List<String> items = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */   public ItemList() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public ItemList(String s)
/*     */   {
/*  31 */     split(s, ",", this.items);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ItemList(String s, String sp, boolean isMultiToken)
/*     */   {
/*  50 */     split(s, sp, this.items, isMultiToken);
/*     */   }
/*     */   
/*     */   public List<String> getItems() {
/*  54 */     return this.items;
/*     */   }
/*     */   
/*     */   public String[] getArray() {
/*  58 */     return (String[])this.items.toArray(new String[this.items.size()]);
/*     */   }
/*     */   
/*     */   public void split(String s, String sp, List<String> append, boolean isMultiToken) {
/*  62 */     if ((s == null) || (sp == null))
/*  63 */       return;
/*  64 */     if (isMultiToken) {
/*  65 */       StringTokenizer tokens = new StringTokenizer(s, sp);
/*  66 */       while (tokens.hasMoreTokens()) {
/*  67 */         append.add(tokens.nextToken().trim());
/*     */       }
/*     */     }
/*     */     else {
/*  71 */       split(s, sp, append);
/*     */     }
/*     */   }
/*     */   
/*     */   public void split(String s, String sp, List<String> append) {
/*  76 */     if ((s == null) || (sp == null))
/*  77 */       return;
/*  78 */     int pos = 0;
/*  79 */     int prevPos = 0;
/*     */     do {
/*  81 */       prevPos = pos;
/*  82 */       pos = s.indexOf(sp, pos);
/*  83 */       if (pos == -1)
/*     */         break;
/*  85 */       append.add(s.substring(prevPos, pos).trim());
/*  86 */       pos += sp.length();
/*  87 */     } while (pos != -1);
/*  88 */     append.add(s.substring(prevPos).trim());
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
/*     */ 
/*     */ 
/*     */   public void add(int i, String item)
/*     */   {
/* 105 */     if (item == null)
/* 106 */       return;
/* 107 */     this.items.add(i, item.trim());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void add(String item)
/*     */   {
/* 114 */     if (item == null)
/* 115 */       return;
/* 116 */     this.items.add(item.trim());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addAll(ItemList list)
/*     */   {
/* 124 */     this.items.addAll(list.items);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addAll(String s)
/*     */   {
/* 132 */     split(s, ",", this.items);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addAll(String s, String sp)
/*     */   {
/* 141 */     split(s, sp, this.items);
/*     */   }
/*     */   
/*     */   public void addAll(String s, String sp, boolean isMultiToken) {
/* 145 */     split(s, sp, this.items, isMultiToken);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String get(int i)
/*     */   {
/* 154 */     return (String)this.items.get(i);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int size()
/*     */   {
/* 162 */     return this.items.size();
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 168 */     return toString(",");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString(String sp)
/*     */   {
/* 177 */     StringBuilder sb = new StringBuilder();
/*     */     
/* 179 */     for (int i = 0; i < this.items.size(); i++) {
/* 180 */       if (i == 0) {
/* 181 */         sb.append((String)this.items.get(i));
/*     */       } else {
/* 183 */         sb.append(sp);
/* 184 */         sb.append((String)this.items.get(i));
/*     */       }
/*     */     }
/* 187 */     return sb.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void clear()
/*     */   {
/* 195 */     this.items.clear();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/* 203 */     this.items.clear();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/json/simple/ItemList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */