/*     */ package com.aelitis.azureus.activities;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.AZ3Functions.provider.LocalActivityCallback;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.ui.swt.UserAlerts;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LocalActivityManager
/*     */ {
/*  35 */   private static List<VuzeActivitiesEntry> pending = new ArrayList(1);
/*     */   
/*     */   static {
/*  38 */     VuzeActivitiesManager.addListener(new VuzeActivitiesLoadedListener()
/*     */     {
/*     */ 
/*     */       public void vuzeActivitiesLoaded()
/*     */       {
/*     */ 
/*  44 */         synchronized (LocalActivityManager.class)
/*     */         {
/*  46 */           for (VuzeActivitiesEntry entry : LocalActivityManager.pending)
/*     */           {
/*  48 */             VuzeActivitiesManager.addEntries(new VuzeActivitiesEntry[] { entry });
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*  53 */           LocalActivityManager.access$002(null);
/*     */         }
/*     */         
/*     */       }
/*  57 */     });
/*  58 */     VuzeActivitiesManager.addListener(new VuzeActivitiesListener()
/*     */     {
/*     */       public void vuzeNewsEntryChanged(VuzeActivitiesEntry entry) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void vuzeNewsEntriesRemoved(VuzeActivitiesEntry[] entries) {}
/*     */       
/*     */ 
/*     */ 
/*     */       public void vuzeNewsEntriesAdded(VuzeActivitiesEntry[] entries)
/*     */       {
/*  71 */         boolean local_added = false;
/*  72 */         for (VuzeActivitiesEntry entry : entries) {
/*  73 */           if (entry.getTypeID().equals("LOCAL_NEWS_ITEM")) {
/*  74 */             local_added = true;
/*     */           }
/*     */         }
/*  77 */         if (local_added) {
/*  78 */           UserAlerts ua = UserAlerts.getSingleton();
/*     */           
/*  80 */           if (ua != null) {
/*  81 */             ua.notificationAdded();
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void addLocalActivity(String uid, String icon_id, String name, String[] actions, Class<? extends AZ3Functions.provider.LocalActivityCallback> callback, Map<String, String> callback_data)
/*     */   {
/*  97 */     VuzeActivitiesEntry entry = new VuzeActivitiesEntry(SystemTime.getCurrentTime(), name, "LOCAL_NEWS_ITEM");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 103 */     entry.setID(uid);
/*     */     
/* 105 */     entry.setIconIDRaw(icon_id);
/*     */     
/* 107 */     entry.setActions(actions);
/*     */     
/* 109 */     entry.setCallback(callback, callback_data);
/*     */     
/* 111 */     synchronized (LocalActivityManager.class)
/*     */     {
/* 113 */       if (pending != null)
/*     */       {
/* 115 */         pending.add(entry);
/*     */       }
/*     */       else
/*     */       {
/* 119 */         VuzeActivitiesManager.addEntries(new VuzeActivitiesEntry[] { entry });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface LocalActivityCallback
/*     */     extends AZ3Functions.provider.LocalActivityCallback
/*     */   {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/activities/LocalActivityManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */