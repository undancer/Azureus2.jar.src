/*     */ package org.gudy.azureus2.pluginsimpl.local.sharing;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareException;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResource;
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
/*     */ public class ShareConfigImpl
/*     */ {
/*     */   protected ShareManagerImpl manager;
/*     */   protected int suspend_level;
/*     */   protected boolean save_outstanding;
/*  44 */   protected AEMonitor this_mon = new AEMonitor("ShareConfig");
/*     */   
/*     */ 
/*     */ 
/*     */   protected void loadConfig(ShareManagerImpl _manager)
/*     */   {
/*  50 */     this.manager = _manager;
/*     */     
/*     */     try
/*     */     {
/*  54 */       Map map = FileUtil.readResilientConfigFile("sharing.config");
/*     */       
/*  56 */       List resources = (List)map.get("resources");
/*     */       
/*  58 */       if (resources == null)
/*     */       {
/*  60 */         return;
/*     */       }
/*     */       
/*  63 */       Iterator iter = resources.iterator();
/*     */       
/*  65 */       while (iter.hasNext())
/*     */       {
/*  67 */         Map r_map = (Map)iter.next();
/*     */         
/*  69 */         this.manager.deserialiseResource(r_map);
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  74 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void saveConfig()
/*     */     throws ShareException
/*     */   {
/*     */     try
/*     */     {
/*  84 */       this.this_mon.enter();
/*     */       
/*  86 */       if (this.suspend_level > 0)
/*     */       {
/*  88 */         this.save_outstanding = true;
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*  93 */         Map map = new HashMap();
/*     */         
/*  95 */         List list = new ArrayList();
/*     */         
/*  97 */         map.put("resources", list);
/*     */         
/*  99 */         ShareResource[] shares = this.manager.getShares();
/*     */         
/* 101 */         for (int i = 0; i < shares.length; i++)
/*     */         {
/* 103 */           Map m = new HashMap();
/*     */           
/* 105 */           ((ShareResourceImpl)shares[i]).serialiseResource(m);
/*     */           
/* 107 */           list.add(m);
/*     */         }
/*     */         
/* 110 */         FileUtil.writeResilientConfigFile("sharing.config", map);
/*     */       }
/*     */     }
/*     */     finally {
/* 114 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   protected void suspendSaving()
/*     */   {
/*     */     try
/*     */     {
/* 122 */       this.this_mon.enter();
/*     */       
/* 124 */       this.suspend_level += 1;
/*     */     }
/*     */     finally
/*     */     {
/* 128 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   protected void resumeSaving()
/*     */     throws ShareException
/*     */   {
/*     */     try
/*     */     {
/* 137 */       this.this_mon.enter();
/*     */       
/* 139 */       this.suspend_level -= 1;
/*     */       
/* 141 */       if ((this.suspend_level == 0) && (this.save_outstanding))
/*     */       {
/* 143 */         this.save_outstanding = false;
/*     */         
/* 145 */         saveConfig();
/*     */       }
/*     */     }
/*     */     finally {
/* 149 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/sharing/ShareConfigImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */