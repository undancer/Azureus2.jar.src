/*     */ package com.aelitis.azureus.core.messenger.config;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.messenger.PlatformMessengerException;
/*     */ import com.aelitis.azureus.core.security.CryptoECCUtils;
/*     */ import java.io.PrintStream;
/*     */ import java.security.Signature;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.json.simple.JSONArray;
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
/*     */ public class PlatformSubscriptionsMessenger
/*     */ {
/*     */   private static final boolean MESSAGING_ENABLED = true;
/*  40 */   private static final PlatformMessengerConfig dispatcher = new PlatformMessengerConfig("subscription", false);
/*     */   
/*     */ 
/*     */   private static final String OP_CREATE_SUBS = "create-subscription";
/*     */   
/*     */ 
/*     */   private static final String OP_UPDATE_SUBS = "update-subscription";
/*     */   
/*     */ 
/*     */   private static final String OP_GET_SUBS_BY_SID = "get-subscriptions";
/*     */   
/*     */ 
/*     */   private static final String OP_GET_POP_BY_SID = "get-subscription-infos";
/*     */   
/*     */ 
/*     */   private static final String OP_SET_SELECTED = "set-selected";
/*     */   
/*     */ 
/*     */ 
/*     */   public static void updateSubscription(boolean create, String name, byte[] public_key, byte[] private_key, byte[] sid, int version, boolean is_anon, String content)
/*     */     throws PlatformMessengerException
/*     */   {
/*  62 */     String operation = create ? "create-subscription" : "update-subscription";
/*     */     
/*  64 */     checkEnabled(operation);
/*     */     
/*  66 */     Map parameters = new HashMap();
/*     */     
/*  68 */     String sid_str = Base32.encode(sid);
/*  69 */     String pk_str = Base32.encode(public_key);
/*     */     
/*  71 */     parameters.put("name", name);
/*  72 */     parameters.put("subscription_id", sid_str);
/*  73 */     parameters.put("version_number", new Long(version));
/*  74 */     parameters.put("content", content);
/*     */     
/*  76 */     if (create)
/*     */     {
/*  78 */       parameters.put("public_key", pk_str);
/*     */     }
/*     */     try
/*     */     {
/*  82 */       Signature sig = CryptoECCUtils.getSignature(CryptoECCUtils.rawdataToPrivkey(private_key));
/*     */       
/*  84 */       sig.update((name + pk_str + sid_str + version + content).getBytes("UTF-8"));
/*     */       
/*  86 */       byte[] sig_bytes = sig.sign();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  96 */       parameters.put("signature", Base32.encode(sig_bytes));
/*     */       
/*  98 */       dispatcher.syncInvoke(operation, parameters, is_anon);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 102 */       throw new PlatformMessengerException("Failed to create/update subscription", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static subscriptionDetails getSubscriptionBySID(byte[] sid, boolean is_anon)
/*     */     throws PlatformMessengerException
/*     */   {
/* 113 */     checkEnabled("get-subscriptions");
/*     */     
/* 115 */     Map parameters = new HashMap();
/*     */     
/* 117 */     List sid_list = new JSONArray();
/*     */     
/* 119 */     sid_list.add(Base32.encode(sid));
/*     */     
/* 121 */     parameters.put("subscription_ids", sid_list);
/*     */     
/* 123 */     Map reply = dispatcher.syncInvoke("get-subscriptions", parameters, is_anon);
/*     */     
/* 125 */     for (int i = 0; i < sid_list.size(); i++)
/*     */     {
/* 127 */       Map map = (Map)reply.get((String)sid_list.get(i));
/*     */       
/* 129 */       if (map != null)
/*     */       {
/* 131 */         subscriptionDetails details = new subscriptionDetails(map);
/*     */         
/* 133 */         return details;
/*     */       }
/*     */     }
/*     */     
/* 137 */     throw new PlatformMessengerException("Unknown sid '" + ByteFormatter.encodeString(sid) + "'");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static long getPopularityBySID(byte[] sid)
/*     */     throws PlatformMessengerException
/*     */   {
/* 146 */     checkEnabled("get-subscription-infos");
/*     */     
/* 148 */     Map parameters = new HashMap();
/*     */     
/* 150 */     List sid_list = new JSONArray();
/*     */     
/* 152 */     sid_list.add(Base32.encode(sid));
/*     */     
/* 154 */     parameters.put("subscription_ids", sid_list);
/*     */     
/* 156 */     Map reply = dispatcher.syncInvoke("get-subscription-infos", parameters);
/*     */     
/* 158 */     for (int i = 0; i < sid_list.size(); i++)
/*     */     {
/* 160 */       Map map = (Map)reply.get((String)sid_list.get(i));
/*     */       
/* 162 */       if (map != null)
/*     */       {
/* 164 */         subscriptionInfo info = new subscriptionInfo(map);
/*     */         
/* 166 */         return info.getPopularity();
/*     */       }
/*     */     }
/*     */     
/* 170 */     return -1L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static List[] setSelected(List sids)
/*     */     throws PlatformMessengerException
/*     */   {
/* 179 */     checkEnabled("set-selected");
/*     */     
/* 181 */     Map parameters = new HashMap();
/*     */     
/* 183 */     List sid_list = new JSONArray();
/* 184 */     for (int i = 0; i < sids.size(); i++)
/*     */     {
/* 186 */       sid_list.add(Base32.encode((byte[])sids.get(i)));
/*     */     }
/*     */     
/* 189 */     parameters.put("subscription_ids", sid_list);
/*     */     
/* 191 */     Map reply = dispatcher.syncInvoke("set-selected", parameters);
/*     */     
/* 193 */     List versions = (List)reply.get("version_numbers");
/*     */     
/* 195 */     if (versions == null)
/*     */     {
/*     */ 
/*     */ 
/* 199 */       versions = new ArrayList();
/*     */       
/* 201 */       for (int i = 0; i < sids.size(); i++)
/*     */       {
/* 203 */         versions.add(new Long(1L));
/*     */       }
/*     */     }
/*     */     
/* 207 */     List popularities = (List)reply.get("popularities");
/*     */     
/* 209 */     if (popularities == null)
/*     */     {
/*     */ 
/*     */ 
/* 213 */       popularities = new ArrayList();
/*     */       
/* 215 */       for (int i = 0; i < sids.size(); i++)
/*     */       {
/* 217 */         popularities.add(new Long(-1L));
/*     */       }
/*     */     }
/*     */     
/* 221 */     return new List[] { versions, popularities };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static void checkEnabled(String method)
/*     */     throws PlatformMessengerException
/*     */   {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static class subscriptionInfo
/*     */   {
/*     */     private Map info;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     protected subscriptionInfo(Map _info)
/*     */     {
/* 245 */       this.info = _info;
/*     */     }
/*     */     
/*     */ 
/*     */     public long getPopularity()
/*     */     {
/* 251 */       return ((Long)this.info.get("popularity")).intValue();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static class subscriptionDetails
/*     */   {
/*     */     private Map details;
/*     */     
/*     */ 
/*     */     protected subscriptionDetails(Map _details)
/*     */     {
/* 264 */       this.details = _details;
/*     */     }
/*     */     
/*     */ 
/*     */     public String getName()
/*     */     {
/* 270 */       return getString("name");
/*     */     }
/*     */     
/*     */ 
/*     */     public String getContent()
/*     */     {
/* 276 */       return getString("content");
/*     */     }
/*     */     
/*     */ 
/*     */     public int getPopularity()
/*     */     {
/* 282 */       Long l_pop = (Long)this.details.get("popularity");
/*     */       
/* 284 */       if (l_pop != null)
/*     */       {
/* 286 */         return l_pop.intValue();
/*     */       }
/*     */       
/* 289 */       return -1;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected String getString(String key)
/*     */     {
/* 296 */       Object obj = this.details.get(key);
/*     */       
/* 298 */       if ((obj instanceof String))
/*     */       {
/* 300 */         return (String)obj;
/*     */       }
/* 302 */       if ((obj instanceof byte[]))
/*     */       {
/* 304 */         byte[] bytes = (byte[])obj;
/*     */         try
/*     */         {
/* 307 */           return new String(bytes, "UTF-8");
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 311 */           return new String(bytes);
/*     */         }
/*     */       }
/*     */       
/* 315 */       return null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*     */     try
/*     */     {
/* 325 */       AzureusCoreFactory.create();
/*     */       
/* 327 */       String short_id = "";
/*     */       
/* 329 */       long res = getPopularityBySID(Base32.decode(short_id));
/*     */       
/* 331 */       System.out.println(res);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 335 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/messenger/config/PlatformSubscriptionsMessenger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */