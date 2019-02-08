/*     */ package com.aelitis.azureus.util;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.plugins.magnet.MagnetPlugin;
/*     */ import com.aelitis.azureus.plugins.magnet.MagnetPluginListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ExternalStimulusHandler
/*     */ {
/*     */   private static MagnetPlugin magnet_plugin;
/*     */   private static List pending_listeners;
/*     */   
/*     */   protected static void initialise(AzureusCore core)
/*     */   {
/*  45 */     PluginInterface pi = core.getPluginManager().getPluginInterfaceByClass(MagnetPlugin.class);
/*     */     
/*  47 */     if (pi != null)
/*     */     {
/*  49 */       MagnetPlugin temp = (MagnetPlugin)pi.getPlugin();
/*     */       
/*     */       List to_add;
/*     */       
/*  53 */       synchronized (ExternalStimulusHandler.class)
/*     */       {
/*  55 */         magnet_plugin = temp;
/*     */         
/*  57 */         to_add = pending_listeners;
/*     */         
/*  59 */         pending_listeners = null;
/*     */       }
/*     */       
/*  62 */       if (to_add != null)
/*     */       {
/*  64 */         for (int i = 0; i < to_add.size(); i++)
/*     */         {
/*  66 */           addListener((ExternalStimulusListener)to_add.get(i));
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/*  71 */       Debug.out("Failed to resolve magnet plugin");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  76 */     addListener(new ExternalStimulusListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public boolean receive(String name, Map values)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*  87 */         return name.equals("ExternalStimulus.test");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public int query(String name, Map values)
/*     */       {
/*  95 */         return Integer.MIN_VALUE;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void addListener(ExternalStimulusListener listener)
/*     */   {
/* 104 */     synchronized (ExternalStimulusHandler.class)
/*     */     {
/* 106 */       if (magnet_plugin == null)
/*     */       {
/* 108 */         if (pending_listeners == null)
/*     */         {
/* 110 */           pending_listeners = new ArrayList();
/*     */         }
/*     */         
/* 113 */         pending_listeners.add(listener);
/*     */         
/* 115 */         return;
/*     */       }
/*     */     }
/*     */     
/* 119 */     if (magnet_plugin != null)
/*     */     {
/* 121 */       magnet_plugin.addListener(new MagnetPluginListener()
/*     */       {
/*     */ 
/*     */         public boolean set(String name, Map values)
/*     */         {
/*     */ 
/*     */           try
/*     */           {
/*     */ 
/* 130 */             return this.val$listener.receive(name, values);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 134 */             Debug.out(e);
/*     */           }
/* 136 */           return false;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public int get(String name, Map values)
/*     */         {
/*     */           try
/*     */           {
/* 146 */             return this.val$listener.query(name, values);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 150 */             Debug.out(e);
/*     */           }
/* 152 */           return Integer.MIN_VALUE;
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/util/ExternalStimulusHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */