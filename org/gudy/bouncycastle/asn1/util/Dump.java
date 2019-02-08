/*    */ package org.gudy.bouncycastle.asn1.util;
/*    */ 
/*    */ import java.io.FileInputStream;
/*    */ import java.io.PrintStream;
/*    */ import org.gudy.bouncycastle.asn1.ASN1InputStream;
/*    */ 
/*    */ 
/*    */ public class Dump
/*    */ {
/*    */   public static void main(String[] args)
/*    */     throws Exception
/*    */   {
/* 13 */     FileInputStream fIn = new FileInputStream(args[0]);
/* 14 */     ASN1InputStream bIn = new ASN1InputStream(fIn);
/* 15 */     Object obj = null;
/*    */     
/* 17 */     while ((obj = bIn.readObject()) != null)
/*    */     {
/* 19 */       System.out.println(ASN1Dump.dumpAsString(obj));
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/util/Dump.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */