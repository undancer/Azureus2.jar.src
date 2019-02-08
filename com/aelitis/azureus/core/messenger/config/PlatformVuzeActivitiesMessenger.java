/*    */ package com.aelitis.azureus.core.messenger.config;
/*    */ 
/*    */ import com.aelitis.azureus.activities.VuzeActivitiesEntry;
/*    */ import com.aelitis.azureus.activities.VuzeActivitiesManager;
/*    */ import com.aelitis.azureus.core.messenger.PlatformMessage;
/*    */ import com.aelitis.azureus.core.messenger.PlatformMessenger;
/*    */ import com.aelitis.azureus.core.messenger.PlatformMessengerListener;
/*    */ import com.aelitis.azureus.util.MapUtils;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.Map;
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
/*    */ public class PlatformVuzeActivitiesMessenger
/*    */ {
/*    */   public static final String LISTENER_ID = "vuzenews";
/*    */   public static final String OP_GET = "get-entries";
/*    */   public static final long DEFAULT_RETRY_MS = 86400000L;
/*    */   
/*    */   public static void getEntries(long agoMS, long maxDelayMS, String reason, GetEntriesReplyListener replyListener)
/*    */   {
/* 49 */     PlatformMessage message = new PlatformMessage("AZMSG", reason.equals("shown") ? "vznews" : "vuzenews", "get-entries", new Object[] { "ago-ms", new Long(agoMS), "reason", reason }, maxDelayMS);
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 57 */     PlatformMessengerListener listener = null;
/* 58 */     if (replyListener != null) {
/* 59 */       listener = new PlatformMessengerListener()
/*    */       {
/*    */         public void messageSent(PlatformMessage message) {}
/*    */         
/*    */         public void replyReceived(PlatformMessage message, String replyType, Map reply)
/*    */         {
/* 65 */           VuzeActivitiesEntry[] entries = new VuzeActivitiesEntry[0];
/* 66 */           List entriesList = (List)MapUtils.getMapObject(reply, "entries", null, List.class);
/*    */           int i;
/* 68 */           Iterator iter; if ((entriesList != null) && (entriesList.size() > 0)) {
/* 69 */             entries = new VuzeActivitiesEntry[entriesList.size()];
/* 70 */             i = 0;
/* 71 */             for (iter = entriesList.iterator(); iter.hasNext();) {
/* 72 */               Map platformEntry = (Map)iter.next();
/* 73 */               if (platformEntry != null)
/*    */               {
/*    */ 
/*    */ 
/* 77 */                 entries[i] = VuzeActivitiesManager.createEntryFromMap(platformEntry, false);
/*    */                 
/* 79 */                 if (entries[i] != null)
/* 80 */                   i++;
/*    */               }
/*    */             }
/*    */           }
/* 84 */           long refreshInMS = MapUtils.getMapLong(reply, "refresh-in-ms", 86400000L);
/*    */           
/* 86 */           this.val$replyListener.gotVuzeNewsEntries(entries, refreshInMS);
/*    */         }
/*    */       };
/*    */     }
/*    */     
/* 91 */     PlatformMessenger.queueMessage(message, listener);
/*    */   }
/*    */   
/*    */   public static abstract interface GetEntriesReplyListener
/*    */   {
/*    */     public abstract void gotVuzeNewsEntries(VuzeActivitiesEntry[] paramArrayOfVuzeActivitiesEntry, long paramLong);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/messenger/config/PlatformVuzeActivitiesMessenger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */