/*     */ package org.gudy.azureus2.pluginsimpl;
/*     */ 
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctions.actionListener;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.UIFunctionsUserPrompter;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Constants;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PluginUtils
/*     */ {
/*     */   private static boolean js_plugin_installing;
/*     */   
/*     */   public static int comparePluginVersions(String version_1, String version_2)
/*     */   {
/*  49 */     return Constants.compareVersions(version_1, version_2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void installJavaScriptPlugin()
/*     */   {
/*  57 */     synchronized (PluginUtils.class)
/*     */     {
/*  59 */       if (js_plugin_installing)
/*     */       {
/*  61 */         return;
/*     */       }
/*     */       
/*  64 */       js_plugin_installing = true;
/*     */     }
/*     */     
/*  67 */     boolean installing = false;
/*     */     try
/*     */     {
/*  70 */       UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*     */       
/*  72 */       if (uif == null) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/*  77 */       if (!Constants.isJava8OrHigher)
/*     */       {
/*  79 */         String title = MessageText.getString("azjscripter.install.fail.jver");
/*     */         
/*  81 */         String text = MessageText.getString("azjscripter.install.fail.jver.text");
/*     */         
/*  83 */         UIFunctionsUserPrompter prompter = uif.getUserPrompter(title, text, new String[] { MessageText.getString("Button.ok") }, 0);
/*     */         
/*     */ 
/*     */ 
/*  87 */         prompter.setAutoCloseInMS(0);
/*     */         
/*  89 */         prompter.open(null);
/*     */       }
/*     */       
/*  92 */       String title = MessageText.getString("azjscripter.install");
/*     */       
/*  94 */       String text = MessageText.getString("azjscripter.install.text");
/*     */       
/*  96 */       UIFunctionsUserPrompter prompter = uif.getUserPrompter(title, text, new String[] { MessageText.getString("Button.yes"), MessageText.getString("Button.no") }, 0);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 101 */       String remember_id = "azjscripter.install.remember.id";
/*     */       
/* 103 */       if (remember_id != null)
/*     */       {
/* 105 */         prompter.setRemember(remember_id, false, MessageText.getString("MessageBoxWindow.nomoreprompting"));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 111 */       prompter.setAutoCloseInMS(0);
/*     */       
/* 113 */       prompter.open(null);
/*     */       
/* 115 */       boolean install = prompter.waitUntilClosed() == 0;
/*     */       
/* 117 */       if (install)
/*     */       {
/* 119 */         uif.installPlugin("azjscripter", "azjscripter.install", new UIFunctions.actionListener()
/*     */         {
/*     */           /* Error */
/*     */           public void actionComplete(Object result)
/*     */           {
/*     */             // Byte code:
/*     */             //   0: ldc_w 19
/*     */             //   3: dup
/*     */             //   4: astore_2
/*     */             //   5: monitorenter
/*     */             //   6: iconst_0
/*     */             //   7: invokestatic 28	org/gudy/azureus2/pluginsimpl/PluginUtils:access$002	(Z)Z
/*     */             //   10: pop
/*     */             //   11: aload_2
/*     */             //   12: monitorexit
/*     */             //   13: goto +8 -> 21
/*     */             //   16: astore_3
/*     */             //   17: aload_2
/*     */             //   18: monitorexit
/*     */             //   19: aload_3
/*     */             //   20: athrow
/*     */             //   21: goto +34 -> 55
/*     */             //   24: astore 4
/*     */             //   26: ldc_w 19
/*     */             //   29: dup
/*     */             //   30: astore 5
/*     */             //   32: monitorenter
/*     */             //   33: iconst_0
/*     */             //   34: invokestatic 28	org/gudy/azureus2/pluginsimpl/PluginUtils:access$002	(Z)Z
/*     */             //   37: pop
/*     */             //   38: aload 5
/*     */             //   40: monitorexit
/*     */             //   41: goto +11 -> 52
/*     */             //   44: astore 6
/*     */             //   46: aload 5
/*     */             //   48: monitorexit
/*     */             //   49: aload 6
/*     */             //   51: athrow
/*     */             //   52: aload 4
/*     */             //   54: athrow
/*     */             //   55: return
/*     */             // Line number table:
/*     */             //   Java source line #132	-> byte code offset #0
/*     */             //   Java source line #134	-> byte code offset #6
/*     */             //   Java source line #135	-> byte code offset #11
/*     */             //   Java source line #136	-> byte code offset #21
/*     */             //   Java source line #132	-> byte code offset #24
/*     */             //   Java source line #134	-> byte code offset #33
/*     */             //   Java source line #135	-> byte code offset #38
/*     */             //   Java source line #137	-> byte code offset #55
/*     */             // Local variable table:
/*     */             //   start	length	slot	name	signature
/*     */             //   0	56	0	this	1
/*     */             //   0	56	1	result	Object
/*     */             //   4	14	2	Ljava/lang/Object;	Object
/*     */             //   16	4	3	localObject1	Object
/*     */             //   24	29	4	localObject2	Object
/*     */             //   44	6	6	localObject3	Object
/*     */             // Exception table:
/*     */             //   from	to	target	type
/*     */             //   6	13	16	finally
/*     */             //   16	19	16	finally
/*     */             //   24	26	24	finally
/*     */             //   33	41	44	finally
/*     */             //   44	49	44	finally
/*     */           }
/* 139 */         });
/* 140 */         installing = true;
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/*     */ 
/* 150 */       if (!installing)
/*     */       {
/* 152 */         synchronized (PluginUtils.class)
/*     */         {
/* 154 */           js_plugin_installing = false;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/PluginUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */