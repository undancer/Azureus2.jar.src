/*     */ package org.gudy.azureus2.ui.swt.config;
/*     */ 
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.config.generic.GenericParameterAdapter;
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
/*     */ public class ConfigParameterAdapter
/*     */   extends GenericParameterAdapter
/*     */ {
/*     */   private static final int CHANGINGCOUNT_BREAKER = 5;
/*     */   private Parameter owner;
/*  37 */   private int changingCount = 0;
/*     */   
/*  39 */   private boolean changedExternally = false;
/*     */   
/*     */   protected ConfigParameterAdapter(Parameter _owner, final String configID) {
/*  42 */     this.owner = _owner;
/*     */     
/*  44 */     COConfigurationManager.addParameterListener(configID, new ParameterListener()
/*     */     {
/*     */       public void parameterChanged(String parameterName) {
/*     */         try {
/*  48 */           if (!ConfigParameterAdapter.this.owner.isInitialised()) {
/*  49 */             return;
/*     */           }
/*  51 */           if (ConfigParameterAdapter.this.owner.isDisposed()) {
/*  52 */             COConfigurationManager.removeParameterListener(parameterName, this);
/*  53 */             return;
/*     */           }
/*     */           
/*  56 */           ConfigParameterAdapter.this.informChanged(true);
/*     */           
/*  58 */           Object valueObject = ConfigParameterAdapter.this.owner.getValueObject();
/*     */           
/*  60 */           if ((valueObject instanceof Boolean)) {
/*  61 */             boolean b = COConfigurationManager.getBooleanParameter(parameterName);
/*  62 */             ConfigParameterAdapter.this.owner.setValue(Boolean.valueOf(b));
/*  63 */           } else if ((valueObject instanceof Integer)) {
/*  64 */             int i = COConfigurationManager.getIntParameter(parameterName);
/*  65 */             ConfigParameterAdapter.this.owner.setValue(new Integer(i));
/*  66 */           } else if ((valueObject instanceof String)) {
/*  67 */             String s = COConfigurationManager.getStringParameter(parameterName);
/*  68 */             ConfigParameterAdapter.this.owner.setValue(s);
/*     */           }
/*     */         } catch (Exception e) {
/*  71 */           Debug.out("parameterChanged trigger from ConfigParamAdapter " + configID, e);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public int getIntValue(String key)
/*     */   {
/*  80 */     return COConfigurationManager.getIntParameter(key);
/*     */   }
/*     */   
/*     */   public int getIntValue(String key, int def)
/*     */   {
/*  85 */     return COConfigurationManager.getIntParameter(key, def);
/*     */   }
/*     */   
/*     */   public void setIntValue(String key, int value)
/*     */   {
/*  90 */     if (this.changingCount == 0) {
/*  91 */       this.changedExternally = false;
/*     */     }
/*  93 */     this.changingCount += 1;
/*     */     try {
/*  95 */       if (getIntValue(key) == value) {
/*  96 */         this.changedExternally = true;
/*     */ 
/*     */ 
/*     */       }
/* 100 */       else if (this.changingCount > 5) {
/* 101 */         Debug.out("Preventing StackOverflow on setting " + key + " to " + value + " (was " + getIntValue(key) + ") via " + Debug.getCompressedStackTrace());
/*     */         
/*     */ 
/* 104 */         this.changingCount = 1;
/*     */       } else {
/* 106 */         informChanging(value);
/*     */         
/* 108 */         if (!this.changedExternally) {
/* 109 */           COConfigurationManager.setParameter(key, value);
/* 110 */           this.changedExternally = true;
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 115 */       this.changingCount -= 1;
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean resetIntDefault(String key)
/*     */   {
/* 121 */     if (COConfigurationManager.doesParameterDefaultExist(key)) {
/* 122 */       COConfigurationManager.removeParameter(key);
/* 123 */       return true;
/*     */     }
/*     */     
/* 126 */     return false;
/*     */   }
/*     */   
/*     */   public Boolean getBooleanValue(String key)
/*     */   {
/* 131 */     return Boolean.valueOf(COConfigurationManager.getBooleanParameter(key));
/*     */   }
/*     */   
/*     */   public Boolean getBooleanValue(String key, Boolean def)
/*     */   {
/* 136 */     return Boolean.valueOf(COConfigurationManager.getBooleanParameter(key, def.booleanValue()));
/*     */   }
/*     */   
/*     */   public void setBooleanValue(String key, boolean value)
/*     */   {
/* 141 */     if (this.changingCount == 0) {
/* 142 */       this.changedExternally = false;
/*     */     }
/*     */     
/* 145 */     this.changingCount += 1;
/*     */     try {
/* 147 */       if ((getBooleanValue(key).booleanValue() == value) && ((COConfigurationManager.doesParameterNonDefaultExist(key)) || (COConfigurationManager.doesParameterDefaultExist(key))))
/*     */       {
/*     */ 
/* 150 */         this.changedExternally = true;
/*     */ 
/*     */ 
/*     */       }
/* 154 */       else if (this.changingCount > 5) {
/* 155 */         Debug.out("Preventing StackOverflow on setting " + key + " to " + value + " (was " + getBooleanValue(key) + ") via " + Debug.getCompressedStackTrace());
/*     */         
/*     */ 
/* 158 */         this.changingCount = 1;
/*     */       } else {
/* 160 */         informChanging(value);
/*     */         
/* 162 */         if (!this.changedExternally) {
/* 163 */           COConfigurationManager.setParameter(key, value);
/* 164 */           this.changedExternally = true;
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 169 */       this.changingCount -= 1;
/*     */     }
/*     */   }
/*     */   
/*     */   public void informChanged(boolean internally) {
/* 174 */     if (this.owner.change_listeners != null) {
/* 175 */       for (int i = 0; i < this.owner.change_listeners.size(); i++) {
/*     */         try {
/* 177 */           ((ParameterChangeListener)this.owner.change_listeners.get(i)).parameterChanged(this.owner, internally);
/*     */         }
/*     */         catch (Exception e) {
/* 180 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void informChanging(int toValue) {
/* 187 */     if (this.owner.change_listeners != null) {
/* 188 */       for (int i = 0; i < this.owner.change_listeners.size(); i++) {
/*     */         try {
/* 190 */           ParameterChangeListener l = (ParameterChangeListener)this.owner.change_listeners.get(i);
/* 191 */           l.intParameterChanging(this.owner, toValue);
/*     */         } catch (Exception e) {
/* 193 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void informChanging(boolean toValue) {
/* 200 */     if (this.owner.change_listeners != null) {
/* 201 */       for (int i = 0; i < this.owner.change_listeners.size(); i++) {
/*     */         try {
/* 203 */           ParameterChangeListener l = (ParameterChangeListener)this.owner.change_listeners.get(i);
/* 204 */           l.booleanParameterChanging(this.owner, toValue);
/*     */         } catch (Exception e) {
/* 206 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void informChanging(String toValue) {
/* 213 */     if (this.owner.change_listeners != null) {
/* 214 */       for (int i = 0; i < this.owner.change_listeners.size(); i++) {
/*     */         try {
/* 216 */           ParameterChangeListener l = (ParameterChangeListener)this.owner.change_listeners.get(i);
/* 217 */           l.stringParameterChanging(this.owner, toValue);
/*     */         } catch (Exception e) {
/* 219 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void informChanging(double toValue) {
/* 226 */     if (this.owner.change_listeners != null) {
/* 227 */       for (int i = 0; i < this.owner.change_listeners.size(); i++) {
/*     */         try {
/* 229 */           ParameterChangeListener l = (ParameterChangeListener)this.owner.change_listeners.get(i);
/* 230 */           l.floatParameterChanging(this.owner, toValue);
/*     */         } catch (Exception e) {
/* 232 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/ConfigParameterAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */