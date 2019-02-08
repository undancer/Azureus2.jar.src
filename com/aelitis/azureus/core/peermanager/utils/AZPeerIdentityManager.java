/*    */ package com.aelitis.azureus.core.peermanager.utils;
/*    */ 
/*    */ import org.gudy.azureus2.core3.config.COConfigurationManager;
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
/*    */ public class AZPeerIdentityManager
/*    */ {
/* 30 */   private static byte[] identity = COConfigurationManager.getByteParameter("az_identity", null);
/*    */   
/* 32 */   static { if ((identity == null) || (identity.length != 20)) {
/* 33 */       identity = RandomUtils.generateRandomBytes(20);
/* 34 */       COConfigurationManager.setParameter("az_identity", identity);
/*    */     }
/*    */   }
/*    */   
/*    */   public static byte[] getAZPeerIdentity()
/*    */   {
/* 40 */     return identity;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/utils/AZPeerIdentityManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */