/*    */ package com.aelitis.azureus.util;
/*    */ 
/*    */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*    */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManager;
/*    */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManagerFactory;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ContentNetworkUtils
/*    */ {
/*    */   public static String getUrl(ContentNetwork cn, int serviceID)
/*    */   {
/*    */     try
/*    */     {
/* 44 */       if (!cn.isServiceSupported(serviceID)) {
/* 45 */         return null;
/*    */       }
/* 47 */       return cn.getServiceURL(serviceID);
/*    */     } catch (Throwable t) {}
/* 49 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */   public static ContentNetwork getContentNetworkFromTarget(String target)
/*    */   {
/* 55 */     ContentNetwork cn = null;
/* 56 */     if ((target != null) && (target.startsWith("ContentNetwork."))) {
/* 57 */       long networkID = Long.parseLong(target.substring(15));
/* 58 */       cn = ContentNetworkManagerFactory.getSingleton().getContentNetwork(networkID);
/*    */     }
/*    */     
/*    */ 
/* 62 */     if (cn == null) {
/* 63 */       cn = ConstantsVuze.getDefaultContentNetwork();
/*    */     }
/* 65 */     return cn;
/*    */   }
/*    */   
/*    */   public static String getTarget(ContentNetwork cn) {
/* 69 */     return "ContentNetwork." + (cn == null ? ConstantsVuze.getDefaultContentNetwork().getID() : cn.getID());
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/util/ContentNetworkUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */