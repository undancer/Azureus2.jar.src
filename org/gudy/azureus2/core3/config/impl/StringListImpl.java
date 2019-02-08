/*     */ package org.gudy.azureus2.core3.config.impl;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.config.StringIterator;
/*     */ import org.gudy.azureus2.core3.config.StringList;
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
/*     */ public class StringListImpl
/*     */   implements StringList
/*     */ {
/*     */   final List list;
/*     */   
/*     */   public StringListImpl()
/*     */   {
/*  40 */     this.list = new ArrayList();
/*     */   }
/*     */   
/*     */   public StringListImpl(StringListImpl _list) {
/*  44 */     this.list = new ArrayList(_list.getList());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public StringListImpl(Collection _list)
/*     */   {
/*  52 */     this();
/*  53 */     Iterator iter = _list.iterator();
/*  54 */     while (iter.hasNext()) {
/*  55 */       Object obj = iter.next();
/*  56 */       if ((obj instanceof String)) {
/*  57 */         this.list.add(obj);
/*  58 */       } else if ((obj instanceof byte[])) {
/*  59 */         this.list.add(ConfigurationManager.bytesToString((byte[])obj));
/*  60 */       } else if (obj != null) {
/*  61 */         this.list.add(obj.toString());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   List getList() {
/*  67 */     return this.list;
/*     */   }
/*     */   
/*     */ 
/*     */   public int size()
/*     */   {
/*  73 */     return this.list.size();
/*     */   }
/*     */   
/*     */   public String get(int i) {
/*  77 */     return (String)this.list.get(i);
/*     */   }
/*     */   
/*     */   public void add(String str) {
/*  81 */     this.list.add(str);
/*     */   }
/*     */   
/*     */   public void add(int index, String str) {
/*  85 */     this.list.add(index, str);
/*     */   }
/*     */   
/*     */   public StringIterator iterator() {
/*  89 */     return new StringIteratorImpl(this.list.iterator());
/*     */   }
/*     */   
/*     */   public int indexOf(String str) {
/*  93 */     return this.list.indexOf(str);
/*     */   }
/*     */   
/*     */   public boolean contains(String str) {
/*  97 */     return this.list.contains(str);
/*     */   }
/*     */   
/*     */   public String remove(int index) {
/* 101 */     return (String)this.list.remove(index);
/*     */   }
/*     */   
/*     */   public String[] toArray() {
/* 105 */     return (String[])this.list.toArray(new String[this.list.size()]);
/*     */   }
/*     */   
/*     */   public void clear()
/*     */   {
/* 110 */     this.list.clear();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/config/impl/StringListImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */