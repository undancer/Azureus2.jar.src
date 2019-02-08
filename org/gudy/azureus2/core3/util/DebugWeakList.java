/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.NoSuchElementException;
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
/*     */ public class DebugWeakList
/*     */ {
/*  29 */   static final boolean DEBUG = ;
/*     */   
/*     */   private final String name;
/*     */   
/*     */   final List list;
/*     */   
/*     */ 
/*     */   public DebugWeakList(String _name)
/*     */   {
/*  38 */     this.name = _name;
/*  39 */     this.list = new ArrayList();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DebugWeakList(String _name, DebugWeakList l)
/*     */   {
/*  47 */     this.name = _name;
/*  48 */     this.list = new ArrayList(l.list);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void add(Object obj)
/*     */   {
/*  55 */     if (DEBUG)
/*     */     {
/*  57 */       this.list.add(new Object[] { obj.getClass(), new WeakReference(obj) });
/*     */     }
/*     */     else
/*     */     {
/*  61 */       this.list.add(obj);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void remove(Object obj)
/*     */   {
/*  69 */     if (DEBUG)
/*     */     {
/*  71 */       Iterator it = this.list.iterator();
/*     */       
/*  73 */       while (it.hasNext())
/*     */       {
/*  75 */         Object[] entry = (Object[])it.next();
/*     */         
/*  77 */         WeakReference wr = (WeakReference)entry[1];
/*     */         
/*  79 */         Object target = wr.get();
/*     */         
/*  81 */         if (target == null)
/*     */         {
/*  83 */           it.remove();
/*     */           
/*  85 */           logRemoved((Class)entry[0]);
/*     */         }
/*  87 */         else if (target == obj)
/*     */         {
/*  89 */           it.remove();
/*     */           
/*  91 */           return;
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/*  96 */       this.list.remove(obj);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean contains(Object obj)
/*     */   {
/* 104 */     if (DEBUG)
/*     */     {
/* 106 */       Iterator it = this.list.iterator();
/*     */       
/* 108 */       while (it.hasNext())
/*     */       {
/* 110 */         Object[] entry = (Object[])it.next();
/*     */         
/* 112 */         WeakReference wr = (WeakReference)entry[1];
/*     */         
/* 114 */         Object target = wr.get();
/*     */         
/* 116 */         if (target == null)
/*     */         {
/* 118 */           it.remove();
/*     */           
/* 120 */           logRemoved((Class)entry[0]);
/*     */         }
/* 122 */         else if (target == obj)
/*     */         {
/* 124 */           return true;
/*     */         }
/*     */       }
/*     */       
/* 128 */       return false;
/*     */     }
/*     */     
/* 131 */     return this.list.contains(obj);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void logRemoved(Class cla)
/*     */   {
/* 139 */     Debug.out("Object '" + cla + "' was not removed correctly from " + this.name);
/*     */   }
/*     */   
/*     */ 
/*     */   public Iterator iterator()
/*     */   {
/* 145 */     if (DEBUG)
/*     */     {
/* 147 */       return new WeakListIterator();
/*     */     }
/*     */     
/*     */ 
/* 151 */     return this.list.iterator();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int estimatedSize()
/*     */   {
/* 158 */     return this.list.size();
/*     */   }
/*     */   
/*     */ 
/*     */   protected class WeakListIterator
/*     */     implements Iterator
/*     */   {
/* 165 */     private Iterator it = DebugWeakList.this.list.iterator();
/*     */     private Object pending_result;
/*     */     private Object last_result;
/*     */     
/*     */     protected WeakListIterator() {}
/*     */     
/*     */     public boolean hasNext()
/*     */     {
/* 173 */       if (this.pending_result != null)
/*     */       {
/* 175 */         return true;
/*     */       }
/*     */       
/* 178 */       while (this.it.hasNext())
/*     */       {
/* 180 */         Object[] entry = (Object[])this.it.next();
/*     */         
/* 182 */         WeakReference wr = (WeakReference)entry[1];
/*     */         
/* 184 */         Object target = wr.get();
/*     */         
/* 186 */         if (target == null)
/*     */         {
/* 188 */           this.it.remove();
/*     */           
/* 190 */           DebugWeakList.this.logRemoved((Class)entry[0]);
/*     */         }
/*     */         else
/*     */         {
/* 194 */           this.pending_result = target;
/*     */           
/* 196 */           return true;
/*     */         }
/*     */       }
/*     */       
/* 200 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public Object next()
/*     */       throws NoSuchElementException
/*     */     {
/* 208 */       if (this.pending_result == null)
/*     */       {
/* 210 */         hasNext();
/*     */       }
/*     */       
/* 213 */       if (this.pending_result == null)
/*     */       {
/* 215 */         throw new NoSuchElementException();
/*     */       }
/*     */       
/* 218 */       this.last_result = this.pending_result;
/*     */       
/* 220 */       this.pending_result = null;
/*     */       
/* 222 */       return this.last_result;
/*     */     }
/*     */     
/*     */ 
/*     */     public void remove()
/*     */     {
/* 228 */       Object lr = this.last_result;
/*     */       
/* 230 */       if (lr == null)
/*     */       {
/* 232 */         throw new NoSuchElementException();
/*     */       }
/*     */       
/* 235 */       this.last_result = null;
/*     */       
/* 237 */       if (this.pending_result == null)
/*     */       {
/* 239 */         this.it.remove();
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 245 */         Iterator temp_it = DebugWeakList.this.list.iterator();
/*     */         
/* 247 */         while (temp_it.hasNext())
/*     */         {
/* 249 */           Object[] entry = (Object[])temp_it.next();
/*     */           
/* 251 */           WeakReference wr = (WeakReference)entry[1];
/*     */           
/* 253 */           Object target = wr.get();
/*     */           
/* 255 */           if (target == lr)
/*     */           {
/* 257 */             this.it = temp_it;
/*     */             
/* 259 */             this.it.remove();
/*     */             
/* 261 */             return;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/DebugWeakList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */