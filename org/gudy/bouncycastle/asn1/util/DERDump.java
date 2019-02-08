/*    */ package org.gudy.bouncycastle.asn1.util;
/*    */ 
/*    */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ /**
/*    */  * @deprecated
/*    */  */
/*    */ public class DERDump
/*    */   extends ASN1Dump
/*    */ {
/*    */   public static String dumpAsString(DERObject obj)
/*    */   {
/* 20 */     return _dumpAsString("", obj);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static String dumpAsString(DEREncodable obj)
/*    */   {
/* 31 */     return _dumpAsString("", obj.getDERObject());
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/util/DERDump.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */