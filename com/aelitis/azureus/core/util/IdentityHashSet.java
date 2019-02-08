/*    */ package com.aelitis.azureus.core.util;
/*    */ 
/*    */ import java.util.AbstractSet;
/*    */ import java.util.Collection;
/*    */ import java.util.IdentityHashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.Set;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class IdentityHashSet<E>
/*    */   extends AbstractSet<E>
/*    */ {
/*    */   private final IdentityHashMap<E, Object> identity_map;
/*    */   
/*    */   public IdentityHashSet()
/*    */   {
/* 36 */     this.identity_map = new IdentityHashMap();
/*    */   }
/*    */   
/*    */ 
/*    */   public IdentityHashSet(Collection<? extends E> set)
/*    */   {
/* 42 */     this.identity_map = new IdentityHashMap(Math.max((int)(set.size() / 0.75F) + 1, 16));
/*    */     
/* 44 */     addAll(set);
/*    */   }
/*    */   
/*    */ 
/*    */   public int size()
/*    */   {
/* 50 */     return this.identity_map.size();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public boolean contains(Object entry)
/*    */   {
/* 57 */     return this.identity_map.containsKey(entry);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public boolean add(E entry)
/*    */   {
/* 65 */     return this.identity_map.put(entry, "") == null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public boolean remove(Object entry)
/*    */   {
/* 72 */     return this.identity_map.remove(entry) != null;
/*    */   }
/*    */   
/*    */ 
/*    */   public void clear()
/*    */   {
/* 78 */     this.identity_map.clear();
/*    */   }
/*    */   
/*    */ 
/*    */   public Iterator<E> iterator()
/*    */   {
/* 84 */     return this.identity_map.keySet().iterator();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/IdentityHashSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */