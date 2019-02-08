/*    */ package org.gudy.bouncycastle.jce;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import org.gudy.bouncycastle.asn1.x9.X962NamedCurves;
/*    */ import org.gudy.bouncycastle.asn1.x9.X9ECParameters;
/*    */ import org.gudy.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
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
/*    */ public class ECNamedCurveTable
/*    */ {
/*    */   public static ECNamedCurveParameterSpec getParameterSpec(String name)
/*    */   {
/* 24 */     X9ECParameters ecP = X962NamedCurves.getByName(name);
/* 25 */     if (ecP == null)
/*    */     {
/* 27 */       return null;
/*    */     }
/*    */     
/* 30 */     return new ECNamedCurveParameterSpec(name, ecP.getCurve(), ecP.getG(), ecP.getN(), ecP.getH(), ecP.getSeed());
/*    */   }
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
/*    */   public static Enumeration getNames()
/*    */   {
/* 47 */     return X962NamedCurves.getNames();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/ECNamedCurveTable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */