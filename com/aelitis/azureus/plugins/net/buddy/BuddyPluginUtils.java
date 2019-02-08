/*     */ package com.aelitis.azureus.plugins.net.buddy;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BuddyPluginUtils
/*     */ {
/*     */   private static BuddyPlugin getPlugin()
/*     */   {
/*  50 */     PluginInterface pi = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByID("azbuddy", true);
/*     */     
/*  52 */     if (pi != null)
/*     */     {
/*  54 */       return (BuddyPlugin)pi.getPlugin();
/*     */     }
/*     */     
/*  57 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public static BuddyPluginBeta getBetaPlugin()
/*     */   {
/*  63 */     BuddyPlugin bp = getPlugin();
/*     */     
/*  65 */     if ((bp != null) && (bp.isBetaEnabled()))
/*     */     {
/*  67 */       BuddyPluginBeta beta = bp.getBeta();
/*     */       
/*  69 */       if (beta.isAvailable())
/*     */       {
/*  71 */         return beta;
/*     */       }
/*     */     }
/*     */     
/*  75 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean isBetaChatAvailable()
/*     */   {
/*  81 */     BuddyPlugin bp = getPlugin();
/*     */     
/*  83 */     if ((bp != null) && (bp.isBetaEnabled()))
/*     */     {
/*  85 */       return bp.getBeta().isAvailable();
/*     */     }
/*     */     
/*  88 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean isBetaChatAnonAvailable()
/*     */   {
/*  94 */     BuddyPlugin bp = getPlugin();
/*     */     
/*  96 */     if ((bp != null) && (bp.isBetaEnabled()))
/*     */     {
/*  98 */       return (bp.getBeta().isAvailable()) && (bp.getBeta().isI2PAvailable());
/*     */     }
/*     */     
/* 101 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void createBetaChat(final String network, final String key, final CreateChatCallback callback)
/*     */   {
/* 110 */     new AEThread2("Chat create async")
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/* 115 */         BuddyPluginBeta.ChatInstance result = null;
/*     */         try
/*     */         {
/* 118 */           BuddyPlugin bp = BuddyPluginUtils.access$000();
/*     */           
/* 120 */           result = bp.getBeta().getAndShowChat(network, key);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 124 */           Debug.out(e);
/*     */         }
/*     */         finally
/*     */         {
/* 128 */           if (callback != null)
/*     */           {
/* 130 */             callback.complete(result);
/*     */           }
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
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
/*     */   public static Map<String, Object> peekChat(String net, String key)
/*     */   {
/* 150 */     BuddyPlugin bp = getPlugin();
/*     */     
/* 152 */     if ((bp != null) && (bp.isBetaEnabled()))
/*     */     {
/* 154 */       return bp.getBeta().peekChat(net, key);
/*     */     }
/*     */     
/* 157 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static Map<String, Object> peekChat(Download download)
/*     */   {
/* 164 */     BuddyPlugin bp = getPlugin();
/*     */     
/* 166 */     if ((bp != null) && (bp.isBetaEnabled()))
/*     */     {
/* 168 */       return bp.getBeta().peekChat(download, false);
/*     */     }
/*     */     
/* 171 */     return null;
/*     */   }
/*     */   
/* 174 */   private static AsyncDispatcher peek_dispatcher = new AsyncDispatcher("peeker");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void peekChatAsync(String net, final String key, final Runnable done)
/*     */   {
/* 182 */     boolean async = false;
/*     */     try
/*     */     {
/* 185 */       if (isBetaChatAvailable())
/*     */       {
/* 187 */         if ((net != "Public") && (!isBetaChatAnonAvailable())) {
/*     */           return;
/*     */         }
/*     */         
/*     */ 
/* 192 */         if (peek_dispatcher.getQueueSize() > 200) {
/*     */           return;
/*     */         }
/*     */         
/*     */ 
/* 197 */         peek_dispatcher.dispatch(new AERunnable()
/*     */         {
/*     */ 
/*     */           public void runSupport()
/*     */           {
/*     */ 
/*     */             try
/*     */             {
/* 205 */               Map<String, Object> peek_data = BuddyPluginUtils.peekChat(this.val$net, key);
/*     */               
/* 207 */               if (peek_data != null)
/*     */               {
/* 209 */                 Number message_count = (Number)peek_data.get("m");
/* 210 */                 Number node_count = (Number)peek_data.get("n");
/*     */                 
/* 212 */                 if ((message_count != null) && (node_count != null))
/*     */                 {
/* 214 */                   if (message_count.intValue() > 0)
/*     */                   {
/* 216 */                     BuddyPluginBeta.ChatInstance chat = BuddyPluginUtils.getChat(this.val$net, key);
/*     */                     
/* 218 */                     if (chat != null)
/*     */                     {
/* 220 */                       chat.setAutoNotify(true);
/*     */                     }
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */             finally {
/* 227 */               done.run();
/*     */             }
/*     */             
/*     */           }
/* 231 */         });
/* 232 */         async = true;
/*     */       }
/*     */     }
/*     */     finally {
/* 236 */       if (!async)
/*     */       {
/* 238 */         done.run();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static BuddyPluginBeta.ChatInstance getChat(String net, String key)
/*     */   {
/* 248 */     BuddyPlugin bp = getPlugin();
/*     */     
/* 250 */     if ((bp != null) && (bp.isBetaEnabled())) {
/*     */       try
/*     */       {
/* 253 */         return bp.getBeta().getChat(net, key);
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 260 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static BuddyPluginBeta.ChatInstance getChat(String net, String key, Map<String, Object> options)
/*     */   {
/* 269 */     BuddyPlugin bp = getPlugin();
/*     */     
/* 271 */     if ((bp != null) && (bp.isBetaEnabled())) {
/*     */       try
/*     */       {
/* 274 */         return bp.getBeta().getChat(net, key, options);
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 281 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static BuddyPluginBeta.ChatInstance getChat(Download download)
/*     */   {
/* 288 */     BuddyPlugin bp = getPlugin();
/*     */     
/* 290 */     if ((bp != null) && (bp.isBetaEnabled()))
/*     */     {
/* 292 */       return bp.getBeta().getChat(download);
/*     */     }
/*     */     
/* 295 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static BuddyPluginViewInterface.View buildChatView(Map<String, Object> properties, BuddyPluginViewInterface.ViewListener listener)
/*     */   {
/* 303 */     BuddyPlugin bp = getPlugin();
/*     */     
/* 305 */     if ((bp != null) && (bp.isBetaEnabled()) && (bp.getBeta().isAvailable()))
/*     */     {
/* 307 */       BuddyPluginViewInterface ui = bp.getSWTUI();
/*     */       
/* 309 */       if (ui != null)
/*     */       {
/* 311 */         return ui.buildView(properties, listener);
/*     */       }
/*     */     }
/*     */     
/* 315 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String getChatKey(TOTorrent torrent)
/*     */   {
/* 322 */     if (torrent == null)
/*     */     {
/* 324 */       return null;
/*     */     }
/*     */     
/* 327 */     return getChatKey(PluginCoreUtils.wrap(torrent));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String getChatKey(Download download)
/*     */   {
/* 334 */     return getChatKey(download.getTorrent());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String getChatKey(Torrent torrent)
/*     */   {
/* 341 */     if (torrent == null)
/*     */     {
/* 343 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 349 */     String torrent_name = null;
/*     */     try
/*     */     {
/* 352 */       TOTorrent to_torrent = PluginCoreUtils.unwrap(torrent);
/*     */       
/* 354 */       torrent_name = to_torrent.getUTF8Name();
/*     */       
/* 356 */       if (torrent_name == null)
/*     */       {
/* 358 */         torrent_name = new String(to_torrent.getName(), "UTF-8");
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/* 364 */     if (torrent_name == null)
/*     */     {
/* 366 */       torrent_name = torrent.getName();
/*     */     }
/*     */     
/* 369 */     String key = "Download: " + torrent_name + " {" + ByteFormatter.encodeString(torrent.getHash()) + "}";
/*     */     
/* 371 */     return key;
/*     */   }
/*     */   
/*     */   public static abstract interface CreateChatCallback
/*     */   {
/*     */     public abstract void complete(BuddyPluginBeta.ChatInstance paramChatInstance);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/BuddyPluginUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */