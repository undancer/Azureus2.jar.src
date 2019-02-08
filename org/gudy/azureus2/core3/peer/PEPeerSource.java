/*    */ package org.gudy.azureus2.core3.peer;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.gudy.azureus2.core3.config.COConfigurationManager;
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
/*    */ public class PEPeerSource
/*    */ {
/*    */   public static final String PS_BT_TRACKER = "Tracker";
/*    */   public static final String PS_DHT = "DHT";
/*    */   public static final String PS_OTHER_PEER = "PeerExchange";
/*    */   public static final String PS_PLUGIN = "Plugin";
/*    */   public static final String PS_INCOMING = "Incoming";
/* 56 */   public static final String[] PS_SOURCES = { "Tracker", "DHT", "PeerExchange", "Plugin", "Incoming" };
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static boolean isPeerSourceEnabledByDefault(String ps)
/*    */   {
/* 68 */     return COConfigurationManager.getBooleanParameter("Peer Source Selection Default." + ps);
/*    */   }
/*    */   
/*    */ 
/*    */   public static String[] getDefaultEnabledPeerSources()
/*    */   {
/* 74 */     List res = new ArrayList();
/*    */     
/* 76 */     for (int i = 0; i < PS_SOURCES.length; i++)
/*    */     {
/* 78 */       if (COConfigurationManager.getBooleanParameter("Peer Source Selection Default." + PS_SOURCES[i]))
/*    */       {
/* 80 */         res.add(PS_SOURCES[i]);
/*    */       }
/*    */     }
/*    */     
/* 84 */     String[] x = new String[res.size()];
/*    */     
/* 86 */     res.toArray(x);
/*    */     
/* 88 */     return x;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/peer/PEPeerSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */