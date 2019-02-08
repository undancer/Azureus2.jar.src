/*    */ package org.gudy.bouncycastle.asn1.misc;
/*    */ 
/*    */ import org.gudy.bouncycastle.asn1.DERIA5String;
/*    */ 
/*    */ 
/*    */ public class VerisignCzagExtension
/*    */   extends DERIA5String
/*    */ {
/*    */   public VerisignCzagExtension(DERIA5String str)
/*    */   {
/* 11 */     super(str.getString());
/*    */   }
/*    */   
/*    */   public String toString()
/*    */   {
/* 16 */     return "VerisignCzagExtension: " + getString();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/misc/VerisignCzagExtension.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */