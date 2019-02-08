/*     */ package com.aelitis.azureus.core.util;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
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
/*     */ public class CopyOnWriteSet<T>
/*     */   implements Iterable<T>
/*     */ {
/*     */   private final boolean is_identify;
/*     */   private volatile Set<T> set;
/*  35 */   private boolean visible = false;
/*     */   
/*     */ 
/*     */ 
/*     */   public CopyOnWriteSet(boolean identity_hash_set)
/*     */   {
/*  41 */     this.is_identify = identity_hash_set;
/*     */     
/*  43 */     if (this.is_identify)
/*     */     {
/*  45 */       this.set = new IdentityHashSet();
/*     */     }
/*     */     else
/*     */     {
/*  49 */       this.set = new HashSet();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean add(T o)
/*     */   {
/*     */     boolean result;
/*     */     
/*  59 */     synchronized (this)
/*     */     {
/*  61 */       if (this.visible)
/*     */       {
/*     */         Set<T> new_set;
/*     */         Set<T> new_set;
/*  65 */         if (this.is_identify)
/*     */         {
/*  67 */           new_set = new IdentityHashSet(this.set);
/*     */         }
/*     */         else
/*     */         {
/*  71 */           new_set = new HashSet(this.set);
/*     */         }
/*     */         
/*  74 */         boolean result = new_set.add(o);
/*     */         
/*  76 */         this.set = new_set;
/*     */         
/*  78 */         this.visible = false;
/*     */       }
/*     */       else
/*     */       {
/*  82 */         result = this.set.add(o);
/*     */       }
/*     */     }
/*     */     
/*  86 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean remove(T o)
/*     */   {
/*  93 */     synchronized (this)
/*     */     {
/*  95 */       if (this.visible)
/*     */       {
/*     */         Set<T> new_set;
/*     */         Set<T> new_set;
/*  99 */         if (this.is_identify)
/*     */         {
/* 101 */           new_set = new IdentityHashSet(this.set);
/*     */         }
/*     */         else
/*     */         {
/* 105 */           new_set = new HashSet(this.set);
/*     */         }
/*     */         
/* 108 */         boolean res = new_set.remove(o);
/*     */         
/* 110 */         this.set = new_set;
/*     */         
/* 112 */         this.visible = false;
/*     */         
/* 114 */         return res;
/*     */       }
/*     */       
/*     */ 
/* 118 */       return this.set.remove(o);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean contains(T o)
/*     */   {
/* 127 */     return this.set.contains(o);
/*     */   }
/*     */   
/*     */ 
/*     */   public void clear()
/*     */   {
/* 133 */     synchronized (this)
/*     */     {
/* 135 */       if (this.visible)
/*     */       {
/*     */         Set<T> new_set;
/*     */         Set<T> new_set;
/* 139 */         if (this.is_identify)
/*     */         {
/* 141 */           new_set = new IdentityHashSet(this.set);
/*     */         }
/*     */         else
/*     */         {
/* 145 */           new_set = new HashSet(this.set);
/*     */         }
/*     */         
/* 148 */         this.set = new_set;
/*     */         
/* 150 */         this.visible = false;
/*     */       }
/*     */       else
/*     */       {
/* 154 */         this.set.clear();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 162 */     return this.set.isEmpty();
/*     */   }
/*     */   
/*     */ 
/*     */   public int size()
/*     */   {
/* 168 */     return this.set.size();
/*     */   }
/*     */   
/*     */ 
/*     */   public Set<T> getSet()
/*     */   {
/* 174 */     synchronized (this)
/*     */     {
/* 176 */       this.visible = true;
/*     */       
/* 178 */       return this.set;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Iterator<T> iterator()
/*     */   {
/* 185 */     synchronized (this)
/*     */     {
/* 187 */       this.visible = true;
/*     */       
/* 189 */       return new CopyOnWriteSetIterator(this.set.iterator());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private class CopyOnWriteSetIterator
/*     */     implements Iterator<T>
/*     */   {
/*     */     private final Iterator<T> it;
/*     */     
/*     */     private T last;
/*     */     
/*     */ 
/*     */     protected CopyOnWriteSetIterator()
/*     */     {
/* 204 */       this.it = _it;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean hasNext()
/*     */     {
/* 210 */       return this.it.hasNext();
/*     */     }
/*     */     
/*     */ 
/*     */     public T next()
/*     */     {
/* 216 */       this.last = this.it.next();
/*     */       
/* 218 */       return (T)this.last;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void remove()
/*     */     {
/* 227 */       if (this.last == null)
/*     */       {
/* 229 */         throw new IllegalStateException("next has not been called!");
/*     */       }
/*     */       
/* 232 */       CopyOnWriteSet.this.remove(this.last);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/CopyOnWriteSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */