/*     */ package com.aelitis.azureus.plugins;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctions.actionListener;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.UIFunctionsUserPrompter;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
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
/*     */ public class I2PHelpers
/*     */ {
/*  40 */   private static final Object i2p_install_lock = new Object();
/*     */   
/*  42 */   private static boolean i2p_installing = false;
/*     */   
/*     */ 
/*     */   public static boolean isI2PInstalled()
/*     */   {
/*  47 */     if (isInstallingI2PHelper())
/*     */     {
/*  49 */       return true;
/*     */     }
/*     */     
/*  52 */     PluginManager pm = AzureusCoreFactory.getSingleton().getPluginManager();
/*     */     
/*  54 */     return pm.getPluginInterfaceByID("azneti2phelper") != null;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public static boolean isInstallingI2PHelper()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: getstatic 177	com/aelitis/azureus/plugins/I2PHelpers:i2p_install_lock	Ljava/lang/Object;
/*     */     //   3: dup
/*     */     //   4: astore_0
/*     */     //   5: monitorenter
/*     */     //   6: getstatic 176	com/aelitis/azureus/plugins/I2PHelpers:i2p_installing	Z
/*     */     //   9: aload_0
/*     */     //   10: monitorexit
/*     */     //   11: ireturn
/*     */     //   12: astore_1
/*     */     //   13: aload_0
/*     */     //   14: monitorexit
/*     */     //   15: aload_1
/*     */     //   16: athrow
/*     */     // Line number table:
/*     */     //   Java source line #60	-> byte code offset #0
/*     */     //   Java source line #62	-> byte code offset #6
/*     */     //   Java source line #63	-> byte code offset #12
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   4	10	0	Ljava/lang/Object;	Object
/*     */     //   12	4	1	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   6	11	12	finally
/*     */     //   12	15	12	finally
/*     */   }
/*     */   
/*     */   public static boolean installI2PHelper(String remember_id, boolean[] install_outcome, Runnable callback)
/*     */   {
/*  72 */     return installI2PHelper(null, remember_id, install_outcome, callback);
/*     */   }
/*     */   
/*  75 */   private static Map<String, Long> declines = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean installI2PHelper(String extra_text, String remember_id, final boolean[] install_outcome, Runnable callback)
/*     */   {
/*  84 */     String decline_key = remember_id;
/*     */     
/*  86 */     if (decline_key == null)
/*     */     {
/*  88 */       decline_key = extra_text;
/*     */     }
/*     */     
/*  91 */     if (decline_key == null)
/*     */     {
/*  93 */       decline_key = "generic";
/*     */     }
/*     */     
/*  96 */     synchronized (i2p_install_lock)
/*     */     {
/*  98 */       Long decline = (Long)declines.get(decline_key);
/*     */       
/* 100 */       if ((decline != null) && (SystemTime.getMonotonousTime() - decline.longValue() < 60000L))
/*     */       {
/* 102 */         return false;
/*     */       }
/*     */       
/* 105 */       if (i2p_installing)
/*     */       {
/* 107 */         Debug.out("I2P Helper already installing");
/*     */         
/* 109 */         return false;
/*     */       }
/*     */       
/* 112 */       i2p_installing = true;
/*     */     }
/*     */     
/* 115 */     boolean installing = false;
/*     */     
/* 117 */     boolean declined = false;
/*     */     try
/*     */     {
/* 120 */       UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*     */       
/* 122 */       if (uif == null)
/*     */       {
/* 124 */         Debug.out("UIFunctions unavailable - can't install plugin");
/*     */         
/* 126 */         return false;
/*     */       }
/*     */       
/* 129 */       String title = MessageText.getString("azneti2phelper.install");
/*     */       
/* 131 */       String text = "";
/*     */       
/* 133 */       if (extra_text != null)
/*     */       {
/* 135 */         text = extra_text + "\n\n";
/*     */       }
/*     */       
/* 138 */       text = text + MessageText.getString("azneti2phelper.install.text");
/*     */       
/* 140 */       UIFunctionsUserPrompter prompter = uif.getUserPrompter(title, text, new String[] { MessageText.getString("Button.yes"), MessageText.getString("Button.no") }, 0);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 145 */       if (remember_id != null)
/*     */       {
/* 147 */         prompter.setRemember(remember_id, false, MessageText.getString("MessageBoxWindow.nomoreprompting"));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 153 */       prompter.setAutoCloseInMS(0);
/*     */       
/* 155 */       prompter.open(null);
/*     */       
/* 157 */       boolean install = prompter.waitUntilClosed() == 0;
/*     */       
/* 159 */       if (install)
/*     */       {
/* 161 */         installing = true;
/*     */         
/* 163 */         uif.installPlugin("azneti2phelper", "azneti2phelper.install", new UIFunctions.actionListener()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void actionComplete(Object result)
/*     */           {
/*     */ 
/*     */             try
/*     */             {
/*     */ 
/* 173 */               if (this.val$callback != null)
/*     */               {
/* 175 */                 if ((result instanceof Boolean))
/*     */                 {
/* 177 */                   install_outcome[0] = ((Boolean)result).booleanValue();
/*     */                 }
/*     */                 
/* 180 */                 this.val$callback.run();
/*     */               }
/*     */             }
/*     */             finally {
/* 184 */               synchronized (I2PHelpers.i2p_install_lock)
/*     */               {
/* 186 */                 I2PHelpers.access$102(false);
/*     */               }
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */       else
/*     */       {
/* 194 */         declined = true;
/*     */         
/* 196 */         Debug.out("I2P Helper install declined (either user reply or auto-remembered)");
/*     */       }
/*     */       
/* 199 */       return install;
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/* 204 */       synchronized (i2p_install_lock)
/*     */       {
/* 206 */         if (!installing)
/*     */         {
/* 208 */           i2p_installing = false;
/*     */         }
/*     */         
/* 211 */         if (declined)
/*     */         {
/* 213 */           declines.put(decline_key, Long.valueOf(SystemTime.getMonotonousTime()));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/I2PHelpers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */