/*     */ package org.gudy.azureus2.ui.swt.pluginsimpl;
/*     */ 
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ui.model.BasicPluginViewModel;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance.UISWTViewEventListenerWrapper;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;
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
/*     */ public class UISWTViewEventListenerHolder
/*     */   implements UISWTInstance.UISWTViewEventListenerWrapper
/*     */ {
/*     */   private final UISWTViewEventListener listener;
/*     */   private final Reference<PluginInterface> pi;
/*     */   private Object datasource;
/*     */   private final String viewID;
/*     */   Map<UISWTView, UISWTViewEventListener> mapSWTViewToEventListener;
/*     */   private Class<? extends UISWTViewEventListener> cla;
/*     */   
/*     */   public UISWTViewEventListenerHolder(String viewID, Class<? extends UISWTViewEventListener> _cla, Object datasource, PluginInterface _pi)
/*     */   {
/*  62 */     this(viewID, (UISWTViewEventListener)null, _pi);
/*  63 */     this.cla = _cla;
/*  64 */     this.datasource = datasource;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected UISWTViewEventListenerHolder(String viewID, UISWTViewEventListener _listener, PluginInterface _pi)
/*     */   {
/*  74 */     this.viewID = viewID;
/*  75 */     this.listener = _listener;
/*     */     
/*  77 */     if (_pi == null)
/*     */     {
/*  79 */       if ((this.listener instanceof BasicPluginViewImpl))
/*     */       {
/*  81 */         _pi = ((BasicPluginViewImpl)this.listener).getModel().getPluginInterface();
/*     */       }
/*     */     }
/*     */     
/*  85 */     if (_pi != null)
/*     */     {
/*  87 */       this.pi = new WeakReference(_pi);
/*     */     }
/*     */     else
/*     */     {
/*  91 */       this.pi = null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isLogView()
/*     */   {
/*  98 */     return this.listener instanceof BasicPluginViewImpl;
/*     */   }
/*     */   
/*     */ 
/*     */   public PluginInterface getPluginInterface()
/*     */   {
/* 104 */     return this.pi == null ? null : (PluginInterface)this.pi.get();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean eventOccurred(UISWTViewEvent event)
/*     */   {
/* 111 */     if (this.listener == null) {
/* 112 */       UISWTViewEventListener eventListener = null;
/*     */       
/* 114 */       synchronized (this) {
/* 115 */         int type = event.getType();
/* 116 */         if (type == 0) {
/*     */           try {
/* 118 */             eventListener = (UISWTViewEventListener)this.cla.newInstance();
/* 119 */             UISWTView view = event.getView();
/* 120 */             if (((eventListener instanceof UISWTViewCoreEventListener)) && 
/* 121 */               ((view instanceof UISWTViewCore))) {
/* 122 */               UISWTViewCore coreView = (UISWTViewCore)view;
/* 123 */               coreView.setUseCoreDataSource(true);
/*     */             }
/*     */             
/* 126 */             if (this.mapSWTViewToEventListener == null) {
/* 127 */               this.mapSWTViewToEventListener = new HashMap();
/*     */             }
/* 129 */             this.mapSWTViewToEventListener.put(view, eventListener);
/*     */             
/* 131 */             if (this.datasource != null) {
/* 132 */               if ((view instanceof UISWTViewImpl)) {
/* 133 */                 UISWTViewImpl swtView = (UISWTViewImpl)view;
/* 134 */                 swtView.triggerEventRaw(1, PluginCoreUtils.convert(this.datasource, ((UISWTViewImpl)view).useCoreDataSource()));
/*     */ 
/*     */               }
/*     */               else
/*     */               {
/* 139 */                 view.triggerEvent(1, this.datasource);
/*     */               }
/*     */             }
/*     */           }
/*     */           catch (Exception e) {
/* 144 */             Debug.out(e);
/* 145 */             return false;
/*     */           }
/* 147 */         } else if (type == 1) {
/* 148 */           this.datasource = event.getData();
/*     */         }
/*     */         
/* 151 */         if (this.mapSWTViewToEventListener != null) {
/* 152 */           if (type == 7) {
/* 153 */             eventListener = (UISWTViewEventListener)this.mapSWTViewToEventListener.remove(event.getView());
/*     */           } else {
/* 155 */             eventListener = (UISWTViewEventListener)this.mapSWTViewToEventListener.get(event.getView());
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 160 */       if (eventListener == null) {
/* 161 */         return false;
/*     */       }
/*     */       
/* 164 */       return eventListener.eventOccurred(event); }
/* 165 */     if ((event.getType() == 0) && ((this.listener instanceof UISWTViewCoreEventListener)) && 
/* 166 */       ((event.getView() instanceof UISWTViewCore))) {
/* 167 */       UISWTViewCore coreView = (UISWTViewCore)event.getView();
/* 168 */       coreView.setUseCoreDataSource(true);
/*     */     }
/*     */     
/*     */ 
/* 172 */     return this.listener.eventOccurred(event);
/*     */   }
/*     */   
/*     */   public UISWTViewEventListener getDelegatedEventListener(UISWTView view) {
/* 176 */     if (this.listener != null) {
/* 177 */       return this.listener;
/*     */     }
/* 179 */     synchronized (this) {
/* 180 */       if (this.mapSWTViewToEventListener == null) {
/* 181 */         return null;
/*     */       }
/* 183 */       return (UISWTViewEventListener)this.mapSWTViewToEventListener.get(view);
/*     */     }
/*     */   }
/*     */   
/*     */   public String getViewID()
/*     */   {
/* 189 */     return this.viewID;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/pluginsimpl/UISWTViewEventListenerHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */