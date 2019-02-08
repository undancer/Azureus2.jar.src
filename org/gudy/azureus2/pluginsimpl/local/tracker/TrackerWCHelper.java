/*     */ package org.gudy.azureus2.pluginsimpl.local.tracker;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostAuthenticationListener;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerListener2.ExternalRequest;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.tracker.Tracker;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebContext;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageGenerator;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl.runnableWithReturnAndException;
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
/*     */ public abstract class TrackerWCHelper
/*     */   implements TrackerWebContext, TRHostAuthenticationListener
/*     */ {
/*     */   private PluginInterface plugin_interface;
/*     */   private Tracker tracker;
/*  48 */   private List generators = new ArrayList();
/*     */   
/*  50 */   protected AEMonitor this_mon = new AEMonitor("TrackerWCHelper");
/*     */   
/*     */ 
/*     */   protected TrackerWCHelper()
/*     */   {
/*  55 */     this.plugin_interface = UtilitiesImpl.getPluginThreadContext();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setTracker(Tracker _tracker)
/*     */   {
/*  62 */     this.tracker = _tracker;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean handleExternalRequest(final TRTrackerServerListener2.ExternalRequest external_request)
/*     */     throws IOException
/*     */   {
/*  71 */     ((Boolean)UtilitiesImpl.callWithPluginThreadContext(this.plugin_interface, new UtilitiesImpl.runnableWithReturnAndException()
/*     */     {
/*     */ 
/*     */ 
/*     */       public Boolean run()
/*     */         throws IOException
/*     */       {
/*     */ 
/*     */ 
/*  80 */         TrackerWebPageRequestImpl request = new TrackerWebPageRequestImpl(TrackerWCHelper.this.tracker, TrackerWCHelper.this, external_request);
/*  81 */         TrackerWebPageResponseImpl reply = new TrackerWebPageResponseImpl(request);
/*     */         
/*  83 */         for (int i = 0; i < TrackerWCHelper.this.generators.size(); i++)
/*     */         {
/*     */           TrackerWebPageGenerator generator;
/*     */           try
/*     */           {
/*  88 */             TrackerWCHelper.this.this_mon.enter();
/*     */             
/*  90 */             if (i >= TrackerWCHelper.this.generators.size())
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  99 */               TrackerWCHelper.this.this_mon.exit(); break;
/*     */             }
/*  95 */             generator = (TrackerWebPageGenerator)TrackerWCHelper.this.generators.get(i);
/*     */           }
/*     */           finally
/*     */           {
/*  99 */             TrackerWCHelper.this.this_mon.exit();
/*     */           }
/*     */           
/* 102 */           if (generator.generate(request, reply))
/*     */           {
/* 104 */             reply.complete();
/*     */             
/* 106 */             return Boolean.valueOf(true);
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 111 */         return Boolean.valueOf(false);
/*     */       }
/*     */     })).booleanValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TrackerWebPageGenerator[] getPageGenerators()
/*     */   {
/* 120 */     TrackerWebPageGenerator[] res = new TrackerWebPageGenerator[this.generators.size()];
/*     */     
/* 122 */     this.generators.toArray(res);
/*     */     
/* 124 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public void addPageGenerator(TrackerWebPageGenerator generator)
/*     */   {
/*     */     try
/*     */     {
/* 132 */       this.this_mon.enter();
/*     */       
/* 134 */       this.generators.add(generator);
/*     */     }
/*     */     finally
/*     */     {
/* 138 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void removePageGenerator(TrackerWebPageGenerator generator)
/*     */   {
/*     */     try
/*     */     {
/* 147 */       this.this_mon.enter();
/*     */       
/* 149 */       this.generators.remove(generator);
/*     */     }
/*     */     finally
/*     */     {
/* 153 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 161 */     this.generators.clear();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/tracker/TrackerWCHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */