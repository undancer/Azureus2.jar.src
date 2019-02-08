/*    */ package com.aelitis.azureus.core.subs;
/*    */ 
/*    */ import com.aelitis.azureus.core.metasearch.Engine;
/*    */ import com.aelitis.azureus.core.vuzefile.VuzeFile;
/*    */ import org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl.PluginSubscription;
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
/*    */ public abstract interface Subscription
/*    */   extends UtilitiesImpl.PluginSubscription
/*    */ {
/*    */   public static final int AZ_VERSION = 1;
/* 34 */   public static final Object VUZE_FILE_COMPONENT_SUBSCRIPTION_KEY = new Object();
/*    */   public static final int ADD_TYPE_CREATE = 1;
/*    */   public static final int ADD_TYPE_IMPORT = 2;
/*    */   public static final int ADD_TYPE_LOOKUP = 3;
/*    */   
/*    */   public abstract String getName();
/*    */   
/*    */   public abstract String getName(boolean paramBoolean);
/*    */   
/*    */   public abstract void setLocalName(String paramString);
/*    */   
/*    */   public abstract void setName(String paramString)
/*    */     throws SubscriptionException;
/*    */   
/*    */   public abstract String getNameEx();
/*    */   
/*    */   public abstract String getQueryKey();
/*    */   
/*    */   public abstract String getID();
/*    */   
/*    */   public abstract byte[] getPublicKey();
/*    */   
/*    */   public abstract int getVersion();
/*    */   
/*    */   public abstract long getAddTime();
/*    */   
/*    */   public abstract int getAddType();
/*    */   
/*    */   public abstract int getHighestVersion();
/*    */   
/*    */   public abstract void resetHighestVersion();
/*    */   
/*    */   public abstract int getAZVersion();
/*    */   
/*    */   public abstract boolean isMine();
/*    */   
/*    */   public abstract boolean isPublic();
/*    */   
/*    */   public abstract void setPublic(boolean paramBoolean)
/*    */     throws SubscriptionException;
/*    */   
/*    */   public abstract boolean isAnonymous();
/*    */   
/*    */   public abstract boolean isUpdateable();
/*    */   
/*    */   public abstract boolean isShareable();
/*    */   
/*    */   public abstract boolean isSearchTemplate();
/*    */   
/*    */   public abstract boolean isSearchTemplateImportable();
/*    */   
/*    */   public abstract VuzeFile getSearchTemplateVuzeFile();
/*    */   
/*    */   public abstract String getJSON()
/*    */     throws SubscriptionException;
/*    */   
/*    */   public abstract boolean setJSON(String paramString)
/*    */     throws SubscriptionException;
/*    */   
/*    */   public abstract boolean isSubscribed();
/*    */   
/*    */   public abstract void setSubscribed(boolean paramBoolean);
/*    */   
/*    */   public abstract void getPopularity(SubscriptionPopularityListener paramSubscriptionPopularityListener)
/*    */     throws SubscriptionException;
/*    */   
/*    */   public abstract boolean setDetails(String paramString1, boolean paramBoolean, String paramString2)
/*    */     throws SubscriptionException;
/*    */   
/*    */   public abstract String getReferer();
/*    */   
/*    */   public abstract long getCachedPopularity();
/*    */   
/*    */   public abstract void addAssociation(byte[] paramArrayOfByte);
/*    */   
/*    */   public abstract void addPotentialAssociation(String paramString1, String paramString2);
/*    */   
/*    */   public abstract int getAssociationCount();
/*    */   
/*    */   public abstract boolean hasAssociation(byte[] paramArrayOfByte);
/*    */   
/*    */   public abstract String getCategory();
/*    */   
/*    */   public abstract void setCategory(String paramString);
/*    */   
/*    */   public abstract long getTagID();
/*    */   
/*    */   public abstract void setTagID(long paramLong);
/*    */   
/*    */   public abstract String getParent();
/*    */   
/*    */   public abstract void setParent(String paramString);
/*    */   
/*    */   public abstract Engine getEngine()
/*    */     throws SubscriptionException;
/*    */   
/*    */   public abstract Subscription cloneWithNewEngine(Engine paramEngine)
/*    */     throws SubscriptionException;
/*    */   
/*    */   public abstract boolean isAutoDownloadSupported();
/*    */   
/*    */   public abstract VuzeFile getVuzeFile()
/*    */     throws SubscriptionException;
/*    */   
/*    */   public abstract void setCreatorRef(String paramString);
/*    */   
/*    */   public abstract String getCreatorRef();
/*    */   
/*    */   public abstract void reset();
/*    */   
/*    */   public abstract void remove();
/*    */   
/*    */   public abstract SubscriptionManager getManager();
/*    */   
/*    */   public abstract SubscriptionHistory getHistory();
/*    */   
/*    */   public abstract SubscriptionResult[] getResults(boolean paramBoolean);
/*    */   
/*    */   public abstract String getURI();
/*    */   
/*    */   public abstract SubscriptionResultFilter getFilters()
/*    */     throws SubscriptionException;
/*    */   
/*    */   public abstract void requestAttention();
/*    */   
/*    */   public abstract void addListener(SubscriptionListener paramSubscriptionListener);
/*    */   
/*    */   public abstract void removeListener(SubscriptionListener paramSubscriptionListener);
/*    */   
/*    */   public abstract void setUserData(Object paramObject1, Object paramObject2);
/*    */   
/*    */   public abstract Object getUserData(Object paramObject);
/*    */   
/*    */   public abstract String getString();
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/subs/Subscription.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */