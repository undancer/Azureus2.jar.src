/*    */ package com.aelitis.azureus.plugins.net.buddy;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ import org.gudy.azureus2.core3.util.Debug;
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
/*    */ public class BuddyPluginUI
/*    */ {
/*    */   private static Class<?> impl_class;
/*    */   
/*    */   static
/*    */   {
/*    */     try
/*    */     {
/* 34 */       impl_class = BuddyPluginUI.class.getClassLoader().loadClass("com.aelitis.azureus.plugins.net.buddy.swt.SBC_ChatOverview");
/*    */     }
/*    */     catch (Throwable e) {}
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static void preInitialize()
/*    */   {
/* 43 */     if (impl_class != null) {
/*    */       try
/*    */       {
/* 46 */         impl_class.getMethod("preInitialize", new Class[0]).invoke(null, new Object[0]);
/*    */       }
/*    */       catch (Throwable e)
/*    */       {
/* 50 */         Debug.out(e);
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static boolean openChat(String network, String key)
/*    */   {
/* 60 */     if (impl_class != null) {
/*    */       try
/*    */       {
/* 63 */         impl_class.getMethod("openChat", new Class[] { String.class, String.class }).invoke(null, new Object[] { network, key });
/*    */         
/* 65 */         return true;
/*    */       }
/*    */       catch (Throwable e)
/*    */       {
/* 69 */         Debug.out(e);
/*    */       }
/*    */       
/*    */     } else {
/* 73 */       Debug.out("Not supported");
/*    */     }
/*    */     
/* 76 */     return false;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/BuddyPluginUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */