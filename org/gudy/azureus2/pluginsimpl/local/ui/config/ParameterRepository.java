/*     */ package org.gudy.azureus2.pluginsimpl.local.ui.config;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.impl.ConfigurationDefaults;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.plugins.ui.config.Parameter;
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
/*     */ public class ParameterRepository
/*     */ {
/*     */   private static ParameterRepository instance;
/*  38 */   private static AEMonitor class_mon = new AEMonitor("ParameterRepository:class");
/*     */   
/*     */   private HashMap params;
/*     */   
/*     */   private ParameterRepository()
/*     */   {
/*  44 */     this.params = new HashMap();
/*     */   }
/*     */   
/*     */   public static ParameterRepository getInstance()
/*     */   {
/*     */     try {
/*  50 */       class_mon.enter();
/*     */       
/*  52 */       if (instance == null)
/*  53 */         instance = new ParameterRepository();
/*  54 */       return instance;
/*     */     }
/*     */     finally
/*     */     {
/*  58 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void addPlugin(Parameter[] parameters, String displayName)
/*     */   {
/*  64 */     this.params.put(displayName, parameters);
/*     */     
/*     */ 
/*  67 */     ConfigurationDefaults def = ConfigurationDefaults.getInstance();
/*  68 */     if (def == null) {
/*  69 */       return;
/*     */     }
/*  71 */     for (int i = 0; i < parameters.length; i++) {
/*  72 */       Parameter parameter = parameters[i];
/*  73 */       if ((parameter instanceof ParameterImpl))
/*     */       {
/*  75 */         String sKey = ((ParameterImpl)parameter).getKey();
/*     */         
/*  77 */         if ((parameter instanceof StringParameterImpl)) {
/*  78 */           def.addParameter(sKey, ((StringParameterImpl)parameter).getDefaultValue());
/*     */         }
/*  80 */         else if ((parameter instanceof IntParameterImpl)) {
/*  81 */           def.addParameter(sKey, ((IntParameterImpl)parameter).getDefaultValue());
/*     */         }
/*  83 */         else if ((parameter instanceof BooleanParameterImpl)) {
/*  84 */           def.addParameter(sKey, ((BooleanParameterImpl)parameter).getDefaultValue());
/*     */         }
/*  86 */         else if ((parameter instanceof FileParameter)) {
/*  87 */           def.addParameter(sKey, ((FileParameter)parameter).getDefaultValue());
/*     */         }
/*  89 */         else if ((parameter instanceof DirectoryParameterImpl)) {
/*  90 */           def.addParameter(sKey, ((DirectoryParameterImpl)parameter).getDefaultValue());
/*     */         }
/*  92 */         else if ((parameter instanceof IntsParameter)) {
/*  93 */           def.addParameter(sKey, ((IntsParameter)parameter).getDefaultValue());
/*     */         }
/*  95 */         else if ((parameter instanceof StringListParameterImpl)) {
/*  96 */           def.addParameter(sKey, ((StringListParameterImpl)parameter).getDefaultValue());
/*     */         }
/*  98 */         else if ((parameter instanceof ColorParameter)) {
/*  99 */           def.addParameter(sKey + ".red", ((ColorParameter)parameter).getDefaultRed());
/*     */           
/* 101 */           def.addParameter(sKey + ".green", ((ColorParameter)parameter).getDefaultGreen());
/*     */           
/* 103 */           def.addParameter(sKey + ".blue", ((ColorParameter)parameter).getDefaultBlue());
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public String[] getNames()
/*     */   {
/* 111 */     Set keys = this.params.keySet();
/* 112 */     return (String[])keys.toArray(new String[keys.size()]);
/*     */   }
/*     */   
/*     */   public Parameter[] getParameterBlock(String key)
/*     */   {
/* 117 */     return (Parameter[])this.params.get(key);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/config/ParameterRepository.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */