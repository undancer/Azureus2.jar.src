/*    */ package com.aelitis.azureus.util;
/*    */ 
/*    */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*    */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManager;
/*    */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManagerFactory;
/*    */ import com.aelitis.azureus.core.crypto.VuzeCryptoManager;
/*    */ import org.gudy.azureus2.core3.util.Base32;
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
/*    */ 
/*    */ 
/*    */ public class ConstantsVuze
/*    */ {
/* 37 */   public static final String AZID = Base32.encode(VuzeCryptoManager.getSingleton().getPlatformAZID());
/*    */   
/*    */   public static final long DEFAULT_CONTENT_NETWORK_ID = 1L;
/*    */   
/* 41 */   public static final boolean DIAG_TO_STDOUT = System.getProperty("DIAG_TO_STDOUT", "0").equals("1");
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static ContentNetwork getDefaultContentNetwork()
/*    */   {
/* 48 */     return ContentNetworkManagerFactory.getSingleton().getContentNetwork(1L);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/util/ConstantsVuze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */