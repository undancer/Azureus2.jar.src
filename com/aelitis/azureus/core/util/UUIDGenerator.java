/*    */ package com.aelitis.azureus.core.util;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.security.SecureRandom;
/*    */ import org.gudy.azureus2.core3.internat.MessageText;
/*    */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*    */ import org.gudy.azureus2.core3.util.RandomUtils;
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
/*    */ 
/*    */ public class UUIDGenerator
/*    */ {
/*    */   public static synchronized byte[] generateUUID()
/*    */   {
/* 37 */     byte[] bytes = new byte[16];
/*    */     
/* 39 */     RandomUtils.SECURE_RANDOM.nextBytes(bytes);
/*    */     
/* 41 */     return bytes;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static String generateUUIDString()
/*    */   {
/* 52 */     byte[] bytes = generateUUID();
/*    */     
/* 54 */     String res = ByteFormatter.encodeString(bytes).toLowerCase(MessageText.LOCALE_ENGLISH);
/*    */     
/* 56 */     return res.substring(0, 8) + "-" + res.substring(8, 12) + "-" + res.substring(12, 16) + "-" + res.substring(16, 20) + "-" + res.substring(20);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static void main(String[] args)
/*    */   {
/* 64 */     for (int i = 0; i < 100; i++) {
/* 65 */       System.out.println(generateUUIDString());
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/UUIDGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */