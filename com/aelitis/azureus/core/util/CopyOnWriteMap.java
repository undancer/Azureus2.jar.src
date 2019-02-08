/*    */ package com.aelitis.azureus.core.util;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
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
/*    */ public class CopyOnWriteMap<K, V>
/*    */ {
/*    */   private volatile Map<K, V> map;
/*    */   
/*    */   public CopyOnWriteMap()
/*    */   {
/* 32 */     this.map = new HashMap(4);
/*    */   }
/*    */   
/*    */   public V put(K key, V val) {
/* 36 */     synchronized (this) {
/* 37 */       HashMap<K, V> new_map = new HashMap(this.map);
/* 38 */       V result = new_map.put(key, val);
/* 39 */       this.map = new_map;
/* 40 */       return result;
/*    */     }
/*    */   }
/*    */   
/*    */   public void putAll(Map<K, V> m) {
/* 45 */     synchronized (this) {
/* 46 */       HashMap<K, V> new_map = new HashMap(this.map);
/* 47 */       new_map.putAll(m);
/* 48 */       this.map = new_map;
/*    */     }
/*    */   }
/*    */   
/*    */   public void putAll(CopyOnWriteMap<K, V> m) {
/* 53 */     putAll(m.map);
/*    */   }
/*    */   
/*    */   public V remove(Object key) {
/* 57 */     synchronized (this) {
/* 58 */       HashMap<K, V> new_map = new HashMap(this.map);
/* 59 */       V res = new_map.remove(key);
/* 60 */       this.map = new_map;
/* 61 */       return res;
/*    */     }
/*    */   }
/*    */   
/*    */   public V get(K key) {
/* 66 */     return (V)this.map.get(key);
/*    */   }
/*    */   
/*    */   public int size() {
/* 70 */     return this.map.size();
/*    */   }
/*    */   
/*    */   public boolean isEmpty() {
/* 74 */     return this.map.isEmpty();
/*    */   }
/*    */   
/*    */ 
/*    */   public Set<K> keySet()
/*    */   {
/* 80 */     return this.map.keySet();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/CopyOnWriteMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */