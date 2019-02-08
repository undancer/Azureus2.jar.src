/*    */ package com.aelitis.azureus.core.util;
/*    */ 
/*    */ import java.net.URL;
/*    */ import java.util.Map;
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
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
/*    */ public class AZ3Functions
/*    */ {
/*    */   private static volatile provider provider;
/*    */   
/*    */   public static void setProvider(provider _p)
/*    */   {
/* 37 */     provider = _p;
/*    */   }
/*    */   
/*    */ 
/*    */   public static provider getProvider()
/*    */   {
/* 43 */     return provider;
/*    */   }
/*    */   
/*    */   public static abstract interface provider
/*    */   {
/*    */     public static final int SERVICE_SITE_RELATIVE = 27;
/*    */     
/*    */     public abstract void subscribeToRSS(String paramString1, URL paramURL, int paramInt, boolean paramBoolean, String paramString2)
/*    */       throws Exception;
/*    */     
/*    */     public abstract void subscribeToSubscription(String paramString)
/*    */       throws Exception;
/*    */     
/*    */     public abstract void openRemotePairingWindow();
/*    */     
/*    */     public abstract boolean canPlay(DownloadManager paramDownloadManager, int paramInt);
/*    */     
/*    */     public abstract void play(DownloadManager paramDownloadManager, int paramInt);
/*    */     
/*    */     public abstract boolean openChat(String paramString1, String paramString2);
/*    */     
/*    */     public abstract void setOpened(DownloadManager paramDownloadManager, boolean paramBoolean);
/*    */     
/*    */     public abstract TranscodeTarget[] getTranscodeTargets();
/*    */     
/*    */     public abstract String getDefaultContentNetworkURL(int paramInt, Object[] paramArrayOfObject);
/*    */     
/*    */     public abstract void addLocalActivity(String paramString1, String paramString2, String paramString3, String[] paramArrayOfString, Class<? extends LocalActivityCallback> paramClass, Map<String, String> paramMap);
/*    */     
/*    */     public static abstract interface LocalActivityCallback
/*    */     {
/*    */       public abstract void actionSelected(String paramString, Map<String, String> paramMap);
/*    */     }
/*    */     
/*    */     public static abstract interface TranscodeProfile
/*    */     {
/*    */       public abstract String getUID();
/*    */       
/*    */       public abstract String getName();
/*    */     }
/*    */     
/*    */     public static abstract interface TranscodeTarget
/*    */     {
/*    */       public abstract String getID();
/*    */       
/*    */       public abstract String getName();
/*    */       
/*    */       public abstract AZ3Functions.provider.TranscodeProfile[] getProfiles();
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/AZ3Functions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */