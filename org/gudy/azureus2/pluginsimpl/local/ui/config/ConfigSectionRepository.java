/*    */ package org.gudy.azureus2.pluginsimpl.local.ui.config;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.Map;
/*    */ import org.gudy.azureus2.core3.util.AEMonitor;
/*    */ import org.gudy.azureus2.plugins.PluginInterface;
/*    */ import org.gudy.azureus2.plugins.ui.config.ConfigSection;
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
/*    */ public class ConfigSectionRepository
/*    */ {
/*    */   private static ConfigSectionRepository instance;
/* 34 */   private static AEMonitor class_mon = new AEMonitor("ConfigSectionRepository:class");
/*    */   private Map<ConfigSection, ConfigSectionHolder> items;
/*    */   
/*    */   private ConfigSectionRepository()
/*    */   {
/* 39 */     this.items = new LinkedHashMap();
/*    */   }
/*    */   
/*    */   public static ConfigSectionRepository getInstance() {
/*    */     try {
/* 44 */       class_mon.enter();
/*    */       
/* 46 */       if (instance == null)
/* 47 */         instance = new ConfigSectionRepository();
/* 48 */       return instance;
/*    */     }
/*    */     finally {
/* 51 */       class_mon.exit();
/*    */     }
/*    */   }
/*    */   
/*    */   public void addConfigSection(ConfigSection item, PluginInterface pi) {
/*    */     try {
/* 57 */       class_mon.enter();
/*    */       
/* 59 */       this.items.put(item, new ConfigSectionHolder(item, pi));
/*    */     }
/*    */     finally
/*    */     {
/* 63 */       class_mon.exit();
/*    */     }
/*    */   }
/*    */   
/*    */   public void removeConfigSection(ConfigSection item) {
/*    */     try {
/* 69 */       class_mon.enter();
/*    */       
/* 71 */       this.items.remove(item);
/*    */     }
/*    */     finally
/*    */     {
/* 75 */       class_mon.exit();
/*    */     }
/*    */   }
/*    */   
/*    */   public ArrayList<ConfigSection> getList() {
/*    */     try {
/* 81 */       class_mon.enter();
/*    */       
/* 83 */       return new ArrayList(this.items.keySet());
/*    */     }
/*    */     finally
/*    */     {
/* 87 */       class_mon.exit();
/*    */     }
/*    */   }
/*    */   
/*    */   public ArrayList<ConfigSectionHolder> getHolderList() {
/*    */     try {
/* 93 */       class_mon.enter();
/*    */       
/* 95 */       return new ArrayList(this.items.values());
/*    */     }
/*    */     finally
/*    */     {
/* 99 */       class_mon.exit();
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/config/ConfigSectionRepository.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */