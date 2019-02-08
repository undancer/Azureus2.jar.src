/*     */ package com.aelitis.azureus.core.util;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.util.Constants;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CopyOnWriteList<T>
/*     */   implements Iterable<T>
/*     */ {
/*     */   private static final boolean LOG_STATS = false;
/*  39 */   private List<T> list = Collections.EMPTY_LIST;
/*     */   
/*     */   private final boolean use_linked_list;
/*     */   
/*  43 */   private boolean visible = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int initialCapacity;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static CopyOnWriteList stats;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int mutation_count;
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
/*     */   public CopyOnWriteList(int initialCapacity)
/*     */   {
/*  86 */     this.initialCapacity = initialCapacity;
/*  87 */     this.use_linked_list = false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CopyOnWriteList()
/*     */   {
/*  99 */     this.initialCapacity = 1;
/* 100 */     this.use_linked_list = false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public CopyOnWriteList(boolean _use_linked_list)
/*     */   {
/* 108 */     this.initialCapacity = 1;
/* 109 */     this.use_linked_list = _use_linked_list;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public int getMutationCount()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_1
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: getfield 147	com/aelitis/azureus/core/util/CopyOnWriteList:mutation_count	I
/*     */     //   8: aload_1
/*     */     //   9: monitorexit
/*     */     //   10: ireturn
/*     */     //   11: astore_2
/*     */     //   12: aload_1
/*     */     //   13: monitorexit
/*     */     //   14: aload_2
/*     */     //   15: athrow
/*     */     // Line number table:
/*     */     //   Java source line #118	-> byte code offset #0
/*     */     //   Java source line #120	-> byte code offset #4
/*     */     //   Java source line #121	-> byte code offset #11
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	16	0	this	CopyOnWriteList<T>
/*     */     //   2	11	1	Ljava/lang/Object;	Object
/*     */     //   11	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	10	11	finally
/*     */     //   11	14	11	finally
/*     */   }
/*     */   
/*     */   public void add(T obj)
/*     */   {
/* 128 */     synchronized (this)
/*     */     {
/* 130 */       this.mutation_count += 1;
/*     */       
/* 132 */       if (this.visible)
/*     */       {
/* 134 */         List<T> new_list = this.use_linked_list ? new LinkedList(this.list) : new ArrayList(this.list);
/*     */         
/*     */ 
/*     */ 
/* 138 */         new_list.add(obj);
/*     */         
/* 140 */         this.list = new_list;
/*     */         
/* 142 */         this.visible = false;
/*     */       }
/*     */       else {
/* 145 */         if (this.list == Collections.EMPTY_LIST) {
/* 146 */           this.list = (this.use_linked_list ? new LinkedList() : new ArrayList(this.initialCapacity));
/*     */         }
/*     */         
/* 149 */         this.list.add(obj);
/*     */       }
/*     */     }
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
/*     */   public boolean addIfNotPresent(T obj)
/*     */   {
/* 164 */     synchronized (this)
/*     */     {
/* 166 */       if (this.list.contains(obj))
/*     */       {
/* 168 */         return false;
/*     */       }
/*     */       
/* 171 */       add(obj);
/*     */       
/* 173 */       return true;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public T get(T entry)
/*     */   {
/* 182 */     synchronized (this)
/*     */     {
/* 184 */       for (T e : this.list)
/*     */       {
/* 186 */         if (e.equals(entry))
/*     */         {
/* 188 */           return e;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 193 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void add(int index, T obj)
/*     */   {
/* 201 */     if ((Constants.IS_CVS_VERSION) && (this.use_linked_list)) {
/* 202 */       Debug.out("hmm");
/*     */     }
/* 204 */     synchronized (this)
/*     */     {
/* 206 */       this.mutation_count += 1;
/*     */       
/* 208 */       if (this.visible)
/*     */       {
/* 210 */         List<T> new_list = this.use_linked_list ? new LinkedList(this.list) : new ArrayList(this.list);
/*     */         
/*     */ 
/*     */ 
/* 214 */         new_list.add(index, obj);
/*     */         
/* 216 */         this.list = new_list;
/*     */         
/* 218 */         this.visible = false;
/*     */       }
/*     */       else {
/* 221 */         if (this.list == Collections.EMPTY_LIST) {
/* 222 */           this.list = (this.use_linked_list ? new LinkedList() : new ArrayList(this.initialCapacity));
/*     */         }
/*     */         
/* 225 */         this.list.add(index, obj);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addAll(Collection<T> c)
/*     */   {
/* 234 */     synchronized (this)
/*     */     {
/* 236 */       this.mutation_count += 1;
/*     */       
/* 238 */       if (this.visible)
/*     */       {
/* 240 */         List<T> new_list = this.use_linked_list ? new LinkedList(this.list) : new ArrayList(this.list);
/*     */         
/*     */ 
/*     */ 
/* 244 */         new_list.addAll(c);
/*     */         
/* 246 */         this.list = new_list;
/*     */         
/* 248 */         this.visible = false;
/*     */       }
/*     */       else {
/* 251 */         if (this.list == Collections.EMPTY_LIST) {
/* 252 */           this.list = (this.use_linked_list ? new LinkedList() : new ArrayList(this.initialCapacity));
/*     */         }
/*     */         
/* 255 */         this.list.addAll(c);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public T get(int index)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: getstatic 152	org/gudy/azureus2/core3/util/Constants:IS_CVS_VERSION	Z
/*     */     //   3: ifeq +15 -> 18
/*     */     //   6: aload_0
/*     */     //   7: getfield 148	com/aelitis/azureus/core/util/CopyOnWriteList:use_linked_list	Z
/*     */     //   10: ifeq +8 -> 18
/*     */     //   13: ldc 1
/*     */     //   15: invokestatic 162	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/String;)V
/*     */     //   18: aload_0
/*     */     //   19: dup
/*     */     //   20: astore_2
/*     */     //   21: monitorenter
/*     */     //   22: aload_0
/*     */     //   23: getfield 150	com/aelitis/azureus/core/util/CopyOnWriteList:list	Ljava/util/List;
/*     */     //   26: iload_1
/*     */     //   27: invokeinterface 168 2 0
/*     */     //   32: aload_2
/*     */     //   33: monitorexit
/*     */     //   34: areturn
/*     */     //   35: astore_3
/*     */     //   36: aload_2
/*     */     //   37: monitorexit
/*     */     //   38: aload_3
/*     */     //   39: athrow
/*     */     // Line number table:
/*     */     //   Java source line #264	-> byte code offset #0
/*     */     //   Java source line #265	-> byte code offset #13
/*     */     //   Java source line #268	-> byte code offset #18
/*     */     //   Java source line #270	-> byte code offset #22
/*     */     //   Java source line #271	-> byte code offset #35
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	40	0	this	CopyOnWriteList<T>
/*     */     //   0	40	1	index	int
/*     */     //   20	17	2	Ljava/lang/Object;	Object
/*     */     //   35	4	3	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   22	34	35	finally
/*     */     //   35	38	35	finally
/*     */   }
/*     */   
/*     */   public boolean remove(T obj)
/*     */   {
/* 278 */     synchronized (this)
/*     */     {
/* 280 */       this.mutation_count += 1;
/*     */       
/* 282 */       if (this.visible)
/*     */       {
/* 284 */         List<T> new_list = this.use_linked_list ? new LinkedList(this.list) : new ArrayList(this.list);
/*     */         
/*     */ 
/*     */ 
/* 288 */         boolean result = new_list.remove(obj);
/*     */         
/* 290 */         this.list = new_list;
/*     */         
/* 292 */         this.visible = false;
/*     */         
/* 294 */         return result;
/*     */       }
/*     */       
/*     */ 
/* 298 */       return this.list.remove(obj);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void clear()
/*     */   {
/* 306 */     synchronized (this)
/*     */     {
/* 308 */       this.mutation_count += 1;
/*     */       
/* 310 */       this.list = Collections.EMPTY_LIST;
/*     */       
/* 312 */       this.visible = false;
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public boolean contains(T obj)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_2
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: getfield 150	com/aelitis/azureus/core/util/CopyOnWriteList:list	Ljava/util/List;
/*     */     //   8: aload_1
/*     */     //   9: invokeinterface 171 2 0
/*     */     //   14: aload_2
/*     */     //   15: monitorexit
/*     */     //   16: ireturn
/*     */     //   17: astore_3
/*     */     //   18: aload_2
/*     */     //   19: monitorexit
/*     */     //   20: aload_3
/*     */     //   21: athrow
/*     */     // Line number table:
/*     */     //   Java source line #320	-> byte code offset #0
/*     */     //   Java source line #322	-> byte code offset #4
/*     */     //   Java source line #323	-> byte code offset #17
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	22	0	this	CopyOnWriteList<T>
/*     */     //   0	22	1	obj	T
/*     */     //   2	17	2	Ljava/lang/Object;	Object
/*     */     //   17	4	3	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	16	17	finally
/*     */     //   17	20	17	finally
/*     */   }
/*     */   
/*     */   public Iterator<T> iterator()
/*     */   {
/* 329 */     synchronized (this)
/*     */     {
/* 331 */       this.visible = true;
/*     */       
/* 333 */       return new CopyOnWriteListIterator(this.list.iterator());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<T> getList()
/*     */   {
/* 342 */     synchronized (this)
/*     */     {
/* 344 */       this.visible = true;
/*     */       
/* 346 */       if (Constants.IS_CVS_VERSION)
/*     */       {
/* 348 */         return Collections.unmodifiableList(this.list);
/*     */       }
/*     */       
/*     */ 
/* 352 */       return this.list;
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public int size()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_1
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: getfield 150	com/aelitis/azureus/core/util/CopyOnWriteList:list	Ljava/util/List;
/*     */     //   8: invokeinterface 165 1 0
/*     */     //   13: aload_1
/*     */     //   14: monitorexit
/*     */     //   15: ireturn
/*     */     //   16: astore_2
/*     */     //   17: aload_1
/*     */     //   18: monitorexit
/*     */     //   19: aload_2
/*     */     //   20: athrow
/*     */     // Line number table:
/*     */     //   Java source line #360	-> byte code offset #0
/*     */     //   Java source line #362	-> byte code offset #4
/*     */     //   Java source line #363	-> byte code offset #16
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	21	0	this	CopyOnWriteList<T>
/*     */     //   2	16	1	Ljava/lang/Object;	Object
/*     */     //   16	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	15	16	finally
/*     */     //   16	19	16	finally
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public boolean isEmpty()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_1
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: getfield 150	com/aelitis/azureus/core/util/CopyOnWriteList:list	Ljava/util/List;
/*     */     //   8: invokeinterface 166 1 0
/*     */     //   13: aload_1
/*     */     //   14: monitorexit
/*     */     //   15: ireturn
/*     */     //   16: astore_2
/*     */     //   17: aload_1
/*     */     //   18: monitorexit
/*     */     //   19: aload_2
/*     */     //   20: athrow
/*     */     // Line number table:
/*     */     //   Java source line #369	-> byte code offset #0
/*     */     //   Java source line #371	-> byte code offset #4
/*     */     //   Java source line #372	-> byte code offset #16
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	21	0	this	CopyOnWriteList<T>
/*     */     //   2	16	1	Ljava/lang/Object;	Object
/*     */     //   16	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	15	16	finally
/*     */     //   16	19	16	finally
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public Object[] toArray()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_1
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: getfield 150	com/aelitis/azureus/core/util/CopyOnWriteList:list	Ljava/util/List;
/*     */     //   8: invokeinterface 167 1 0
/*     */     //   13: aload_1
/*     */     //   14: monitorexit
/*     */     //   15: areturn
/*     */     //   16: astore_2
/*     */     //   17: aload_1
/*     */     //   18: monitorexit
/*     */     //   19: aload_2
/*     */     //   20: athrow
/*     */     // Line number table:
/*     */     //   Java source line #378	-> byte code offset #0
/*     */     //   Java source line #380	-> byte code offset #4
/*     */     //   Java source line #381	-> byte code offset #16
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	21	0	this	CopyOnWriteList<T>
/*     */     //   2	16	1	Ljava/lang/Object;	Object
/*     */     //   16	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	15	16	finally
/*     */     //   16	19	16	finally
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public T[] toArray(T[] x)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_2
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: getfield 150	com/aelitis/azureus/core/util/CopyOnWriteList:list	Ljava/util/List;
/*     */     //   8: aload_1
/*     */     //   9: invokeinterface 175 2 0
/*     */     //   14: aload_2
/*     */     //   15: monitorexit
/*     */     //   16: areturn
/*     */     //   17: astore_3
/*     */     //   18: aload_2
/*     */     //   19: monitorexit
/*     */     //   20: aload_3
/*     */     //   21: athrow
/*     */     // Line number table:
/*     */     //   Java source line #388	-> byte code offset #0
/*     */     //   Java source line #390	-> byte code offset #4
/*     */     //   Java source line #391	-> byte code offset #17
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	22	0	this	CopyOnWriteList<T>
/*     */     //   0	22	1	x	T[]
/*     */     //   2	17	2	Ljava/lang/Object;	Object
/*     */     //   17	4	3	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	16	17	finally
/*     */     //   17	20	17	finally
/*     */   }
/*     */   
/*     */   private class CopyOnWriteListIterator
/*     */     implements Iterator<T>
/*     */   {
/*     */     private final Iterator<T> it;
/*     */     private T last;
/*     */     
/*     */     protected CopyOnWriteListIterator()
/*     */     {
/* 418 */       this.it = _it;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean hasNext()
/*     */     {
/* 424 */       return this.it.hasNext();
/*     */     }
/*     */     
/*     */ 
/*     */     public T next()
/*     */     {
/* 430 */       this.last = this.it.next();
/*     */       
/* 432 */       return (T)this.last;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void remove()
/*     */     {
/* 441 */       if (this.last == null)
/*     */       {
/* 443 */         throw new IllegalStateException("next has not been called!");
/*     */       }
/*     */       
/* 446 */       CopyOnWriteList.this.remove(this.last);
/*     */     }
/*     */   }
/*     */   
/*     */   public int getInitialCapacity() {
/* 451 */     return this.initialCapacity;
/*     */   }
/*     */   
/*     */   public void setInitialCapacity(int initialCapacity) {
/* 455 */     this.initialCapacity = initialCapacity;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/CopyOnWriteList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */