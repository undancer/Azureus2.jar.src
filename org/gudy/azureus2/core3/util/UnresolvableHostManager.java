/*    */ package org.gudy.azureus2.core3.util;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.gudy.azureus2.core3.logging.LogEvent;
/*    */ import org.gudy.azureus2.core3.logging.LogIDs;
/*    */ import org.gudy.azureus2.core3.logging.Logger;
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
/*    */ public class UnresolvableHostManager
/*    */ {
/* 39 */   protected static int next_address = -268435456 + RandomUtils.nextInt(16777215);
/*    */   
/* 41 */   protected static final Map host_map = new HashMap();
/*    */   
/*    */ 
/*    */ 
/*    */   public static int getPseudoAddress(String str)
/*    */   {
/* 47 */     synchronized (host_map)
/*    */     {
/* 49 */       Integer res = (Integer)host_map.get(str);
/*    */       
/* 51 */       if (res == null)
/*    */       {
/* 53 */         res = new Integer(next_address++);
/*    */         
/* 55 */         if (Logger.isEnabled()) {
/* 56 */           Logger.log(new LogEvent(LogIDs.NET, "Allocated pseudo IP address '" + Integer.toHexString(res.intValue()) + "' for host '" + str + "'"));
/*    */         }
/*    */         
/*    */ 
/* 60 */         host_map.put(str, res);
/*    */       }
/*    */       
/* 63 */       return res.intValue();
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static boolean isPseudoAddress(String str)
/*    */   {
/* 71 */     synchronized (host_map)
/*    */     {
/* 73 */       return host_map.get(str) != null;
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/UnresolvableHostManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */