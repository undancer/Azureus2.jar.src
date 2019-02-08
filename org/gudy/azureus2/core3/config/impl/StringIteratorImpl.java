/*    */ package org.gudy.azureus2.core3.config.impl;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import org.gudy.azureus2.core3.config.StringIterator;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StringIteratorImpl
/*    */   implements StringIterator
/*    */ {
/*    */   final Iterator iterator;
/*    */   
/*    */   public StringIteratorImpl(Iterator _iterator)
/*    */   {
/* 36 */     this.iterator = _iterator;
/*    */   }
/*    */   
/*    */   public boolean hasNext() {
/* 40 */     return this.iterator.hasNext();
/*    */   }
/*    */   
/*    */   public String next() {
/* 44 */     return (String)this.iterator.next();
/*    */   }
/*    */   
/*    */   public void remove() {
/* 48 */     this.iterator.remove();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/config/impl/StringIteratorImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */