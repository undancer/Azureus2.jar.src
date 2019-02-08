/*    */ package org.gudy.azureus2.core3.ipfilter.impl.tests;
/*    */ 
/*    */ import java.io.PrintStream;
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
/*    */ public class FakeTrackerResponseGenerator
/*    */ {
/*    */   public static void main(String[] args)
/*    */   {
/* 31 */     String baseRange = "195.68.236.";
/* 32 */     String basePeerId = "-AZ2104-0VR73lDzLejd";
/* 33 */     System.out.print("d8:intervali10e5:peersl");
/* 34 */     for (int i = 100; i < 200; i++) {
/* 35 */       String iStr = "" + i;
/* 36 */       int iStrLength = iStr.length();
/* 37 */       String ip = baseRange + iStr;
/* 38 */       String peerId = basePeerId.substring(0, 20 - iStrLength) + iStr;
/* 39 */       System.out.print("d2:ip" + ip.length() + ":" + ip);
/* 40 */       System.out.print("7:peer id20:" + peerId);
/* 41 */       System.out.print("4:porti3003ee");
/*    */     }
/* 43 */     System.out.print("ee");
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipfilter/impl/tests/FakeTrackerResponseGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */