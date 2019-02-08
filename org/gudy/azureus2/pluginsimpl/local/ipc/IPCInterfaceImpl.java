/*     */ package org.gudy.azureus2.pluginsimpl.local.ipc;
/*     */ 
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import org.gudy.azureus2.plugins.Plugin;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ipc.IPCException;
/*     */ import org.gudy.azureus2.plugins.ipc.IPCInterface;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IPCInterfaceImpl
/*     */   implements IPCInterface
/*     */ {
/*     */   private Object target_use_accessor;
/*     */   private String plugin_class;
/*     */   private PluginInitializer plugin_initializer;
/*     */   
/*     */   public IPCInterfaceImpl(PluginInitializer _plugin_initializer, Plugin _target)
/*     */   {
/*  56 */     this.plugin_initializer = _plugin_initializer;
/*     */     
/*  58 */     this.target_use_accessor = _target;
/*     */     
/*  60 */     this.plugin_class = _target.getClass().getName();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public IPCInterfaceImpl(Object _target)
/*     */   {
/*  72 */     this.target_use_accessor = _target;
/*     */     
/*  74 */     this.plugin_class = _target.getClass().getName();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean canInvoke(String methodName, Object[] params)
/*     */   {
/*     */     try
/*     */     {
/*  83 */       Object target = getTarget();
/*     */       
/*  85 */       Method mtd = getMethod(target, methodName, params);
/*     */       
/*  87 */       mtd.setAccessible(true);
/*     */       
/*  89 */       if (mtd != null)
/*     */       {
/*  91 */         return true;
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*  96 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean canInvoke(String methodName, Class<?>[] params)
/*     */   {
/*     */     try
/*     */     {
/* 105 */       Object target = getTarget();
/*     */       
/* 107 */       Method mtd = getMethod(target, methodName, params);
/*     */       
/* 109 */       mtd.setAccessible(true);
/*     */       
/* 111 */       if (mtd != null)
/*     */       {
/* 113 */         return true;
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/* 118 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object invoke(String methodName, Object[] params)
/*     */     throws IPCException
/*     */   {
/* 128 */     Object target = getTarget();
/*     */     try
/*     */     {
/* 131 */       Method mtd = getMethod(target, methodName, params);
/*     */       
/* 133 */       mtd.setAccessible(true);
/*     */       
/* 135 */       return mtd.invoke(target, params);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 139 */       if ((e instanceof InvocationTargetException))
/*     */       {
/* 141 */         if (e.getCause() != null)
/*     */         {
/* 143 */           e = e.getCause();
/*     */         }
/*     */       }
/*     */       
/* 147 */       throw new IPCException(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Method getMethod(Object target, String methodName, Object[] params)
/*     */     throws Throwable
/*     */   {
/* 159 */     if (params == null)
/*     */     {
/* 161 */       params = new Object[0];
/*     */     }
/*     */     
/* 164 */     Class[] paramTypes = new Class[params.length];
/*     */     
/* 166 */     for (int i = 0; i < params.length; i++) {
/* 167 */       if ((params[i] instanceof Boolean)) {
/* 168 */         paramTypes[i] = Boolean.TYPE;
/* 169 */       } else if ((params[i] instanceof Integer)) {
/* 170 */         paramTypes[i] = Integer.TYPE;
/* 171 */       } else if ((params[i] instanceof Long)) {
/* 172 */         paramTypes[i] = Long.TYPE;
/* 173 */       } else if ((params[i] instanceof Float)) {
/* 174 */         paramTypes[i] = Float.TYPE;
/* 175 */       } else if ((params[i] instanceof Double)) {
/* 176 */         paramTypes[i] = Double.TYPE;
/* 177 */       } else if ((params[i] instanceof Byte)) {
/* 178 */         paramTypes[i] = Byte.TYPE;
/* 179 */       } else if ((params[i] instanceof Character)) {
/* 180 */         paramTypes[i] = Character.TYPE;
/* 181 */       } else if ((params[i] instanceof Short)) {
/* 182 */         paramTypes[i] = Short.TYPE;
/*     */       } else {
/* 184 */         paramTypes[i] = params[i].getClass();
/*     */       }
/*     */     }
/* 187 */     Method mtd = null;
/*     */     try
/*     */     {
/* 190 */       mtd = target.getClass().getDeclaredMethod(methodName, paramTypes);
/*     */     }
/*     */     catch (NoSuchMethodException e)
/*     */     {
/* 194 */       Method[] methods = target.getClass().getMethods();
/*     */       
/* 196 */       for (int i = 0; i < methods.length; i++)
/*     */       {
/* 198 */         Method method = methods[i];
/*     */         
/* 200 */         Class[] method_params = method.getParameterTypes();
/*     */         
/* 202 */         if ((method.getName().equals(methodName)) && (method_params.length == paramTypes.length))
/*     */         {
/* 204 */           boolean ok = true;
/*     */           
/* 206 */           for (int j = 0; j < method_params.length; j++)
/*     */           {
/* 208 */             Class declared = method_params[j];
/* 209 */             Class supplied = paramTypes[j];
/*     */             
/* 211 */             if (!declared.isAssignableFrom(supplied))
/*     */             {
/* 213 */               ok = false;
/*     */               
/* 215 */               break;
/*     */             }
/*     */           }
/*     */           
/* 219 */           if (ok)
/*     */           {
/* 221 */             mtd = method;
/*     */             
/* 223 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 228 */       if (mtd == null)
/*     */       {
/* 230 */         throw e;
/*     */       }
/*     */     }
/*     */     
/* 234 */     return mtd;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Method getMethod(Object target, String methodName, Class<?>[] paramTypes)
/*     */     throws Throwable
/*     */   {
/* 245 */     if (paramTypes == null)
/*     */     {
/* 247 */       paramTypes = new Class[0];
/*     */     }
/*     */     
/* 250 */     Method mtd = null;
/*     */     try
/*     */     {
/* 253 */       mtd = target.getClass().getDeclaredMethod(methodName, paramTypes);
/*     */     }
/*     */     catch (NoSuchMethodException e)
/*     */     {
/* 257 */       Method[] methods = target.getClass().getMethods();
/*     */       
/* 259 */       for (int i = 0; i < methods.length; i++)
/*     */       {
/* 261 */         Method method = methods[i];
/*     */         
/* 263 */         Class[] method_params = method.getParameterTypes();
/*     */         
/* 265 */         if ((method.getName().equals(methodName)) && (method_params.length == paramTypes.length))
/*     */         {
/* 267 */           boolean ok = true;
/*     */           
/* 269 */           for (int j = 0; j < method_params.length; j++)
/*     */           {
/* 271 */             Class declared = method_params[j];
/* 272 */             Class supplied = paramTypes[j];
/*     */             
/* 274 */             if (!declared.isAssignableFrom(supplied))
/*     */             {
/* 276 */               ok = false;
/*     */               
/* 278 */               break;
/*     */             }
/*     */           }
/*     */           
/* 282 */           if (ok)
/*     */           {
/* 284 */             mtd = method;
/*     */             
/* 286 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 291 */       if (mtd == null)
/*     */       {
/* 293 */         throw e;
/*     */       }
/*     */     }
/*     */     
/* 297 */     return mtd;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Object getTarget()
/*     */     throws IPCException
/*     */   {
/* 306 */     synchronized (this)
/*     */     {
/* 308 */       if (this.target_use_accessor == null)
/*     */       {
/* 310 */         PluginInterface[] pis = this.plugin_initializer.getPlugins();
/*     */         
/* 312 */         for (int i = 0; i < pis.length; i++)
/*     */         {
/* 314 */           PluginInterface pi = pis[i];
/*     */           
/* 316 */           if (pi.getPlugin().getClass().getName().equals(this.plugin_class))
/*     */           {
/* 318 */             this.target_use_accessor = pi.getPlugin();
/*     */             
/* 320 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 325 */       if (this.target_use_accessor == null)
/*     */       {
/* 327 */         throw new IPCException("Plugin has been unloaded");
/*     */       }
/*     */       
/* 330 */       return this.target_use_accessor;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void unload()
/*     */   {
/* 337 */     synchronized (this)
/*     */     {
/* 339 */       this.target_use_accessor = null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ipc/IPCInterfaceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */