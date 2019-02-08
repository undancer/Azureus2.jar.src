/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
/*    */ import java.util.Vector;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DEREncodableVector
/*    */ {
/* 12 */   private Vector v = new Vector();
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
/*    */   public void add(DEREncodable obj)
/*    */   {
/* 25 */     this.v.addElement(obj);
/*    */   }
/*    */   
/*    */ 
/*    */   public DEREncodable get(int i)
/*    */   {
/* 31 */     return (DEREncodable)this.v.elementAt(i);
/*    */   }
/*    */   
/*    */   public int size()
/*    */   {
/* 36 */     return this.v.size();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DEREncodableVector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */