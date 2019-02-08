/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
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
/*     */ public class UISwitcherUtil
/*     */ {
/*     */   private static final long UPTIME_NEWUSER = 3600L;
/*  37 */   private static ArrayList listeners = new ArrayList();
/*     */   
/*  39 */   private static String switchedToUI = null;
/*     */   
/*     */   public static void addListener(UISwitcherListener l) {
/*  42 */     listeners.add(l);
/*  43 */     if (switchedToUI != null) {
/*  44 */       triggerListeners(switchedToUI);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void removeListener(UISwitcherListener l) {
/*  49 */     listeners.remove(l);
/*     */   }
/*     */   
/*     */ 
/*     */   public static void openSwitcherWindow() {}
/*     */   
/*     */   public static void triggerListeners(String ui)
/*     */   {
/*  57 */     Object[] array = listeners.toArray();
/*  58 */     for (int i = 0; i < array.length; i++) {
/*  59 */       UISwitcherListener l = (UISwitcherListener)array[i];
/*  60 */       l.uiSwitched(ui);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static String calcUIMode()
/*     */   {
/*  67 */     if ("1".equals(System.getProperty("azureus.safemode")))
/*     */     {
/*  69 */       return "az2";
/*     */     }
/*     */     
/*  72 */     String lastUI = COConfigurationManager.getStringParameter("ui", "az2");
/*  73 */     COConfigurationManager.setParameter("lastUI", lastUI);
/*     */     
/*  75 */     String forceUI = System.getProperty("force.ui");
/*  76 */     if (forceUI != null) {
/*  77 */       COConfigurationManager.setParameter("ui", forceUI);
/*  78 */       return forceUI;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  84 */     boolean installLogExists = FileUtil.getApplicationFile("installer.log").exists();
/*  85 */     boolean alreadySwitched = COConfigurationManager.getBooleanParameter("installer.ui.alreadySwitched", false);
/*     */     
/*  87 */     if ((!alreadySwitched) && (installLogExists)) {
/*  88 */       COConfigurationManager.setParameter("installer.ui.alreadySwitched", true);
/*  89 */       COConfigurationManager.setParameter("ui", "az3");
/*  90 */       COConfigurationManager.setParameter("az3.virgin.switch", true);
/*     */       
/*  92 */       return "az3";
/*     */     }
/*     */     
/*  95 */     boolean asked = COConfigurationManager.getBooleanParameter("ui.asked", false);
/*     */     
/*     */ 
/*  98 */     if ((asked) || (COConfigurationManager.hasParameter("ui", true))) {
/*  99 */       return COConfigurationManager.getStringParameter("ui", "az3");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 104 */     String sFirstVersion = COConfigurationManager.getStringParameter("First Recorded Version");
/* 105 */     if (Constants.compareVersions(sFirstVersion, "3.0.0.0") >= 0) {
/* 106 */       COConfigurationManager.setParameter("ui", "az3");
/* 107 */       return "az3";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 117 */       Map map = FileUtil.readResilientConfigFile("azureus.statistics");
/* 118 */       if (map != null) {
/* 119 */         Map overallMap = (Map)map.get("all");
/* 120 */         if (overallMap != null) {
/* 121 */           long uptime = 0L;
/* 122 */           Object uptimeObject = overallMap.get("uptime");
/* 123 */           if ((uptimeObject instanceof Number)) {
/* 124 */             uptime = ((Number)uptimeObject).longValue();
/*     */           }
/*     */           
/*     */ 
/* 128 */           if ((uptime < 3600L) && (uptime >= 0L)) {
/* 129 */             COConfigurationManager.setParameter("ui", "az3");
/* 130 */             COConfigurationManager.setParameter("az3.virgin.switch", true);
/* 131 */             COConfigurationManager.setParameter("az3.switch.immediate", true);
/* 132 */             return "az3";
/*     */           }
/*     */         }
/*     */       }
/*     */     } catch (Exception e) {
/* 137 */       Debug.out(e);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 142 */     COConfigurationManager.setParameter("ui", "az2");
/* 143 */     return "az2";
/*     */   }
/*     */   
/*     */   public static void _openSwitcherWindow() {
/* 147 */     Class uiswClass = null;
/*     */     try {
/* 149 */       uiswClass = Class.forName("com.aelitis.azureus.ui.swt.shells.uiswitcher.UISwitcherWindow");
/*     */     }
/*     */     catch (ClassNotFoundException e1) {}
/* 152 */     if (uiswClass == null) {
/* 153 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 160 */       Constructor constructor = uiswClass.getConstructor(new Class[0]);
/*     */       
/* 162 */       Object object = constructor.newInstance(new Object[0]);
/*     */       
/* 164 */       Method method = uiswClass.getMethod("open", new Class[0]);
/*     */       
/* 166 */       method.invoke(object, new Object[0]);
/*     */     }
/*     */     catch (Exception e) {
/* 169 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/UISwitcherUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */