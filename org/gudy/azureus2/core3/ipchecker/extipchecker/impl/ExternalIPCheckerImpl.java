/*    */ package org.gudy.azureus2.core3.ipchecker.extipchecker.impl;
/*    */ 
/*    */ import org.gudy.azureus2.core3.ipchecker.extipchecker.ExternalIPChecker;
/*    */ import org.gudy.azureus2.core3.ipchecker.extipchecker.ExternalIPCheckerService;
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
/*    */ public class ExternalIPCheckerImpl
/*    */   implements ExternalIPChecker
/*    */ {
/* 39 */   static final ExternalIPCheckerService[] services = { new ExternalIPCheckerServiceDynDNS(), new ExternalIPCheckerServiceDiscoveryVIP(), new ExternalIPCheckerServiceNoLookup("IPChecker.external.service.no-ip") };
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public ExternalIPCheckerService[] getServices()
/*    */   {
/* 48 */     return services;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipchecker/extipchecker/impl/ExternalIPCheckerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */