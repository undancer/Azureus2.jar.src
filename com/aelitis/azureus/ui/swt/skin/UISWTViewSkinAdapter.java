/*     */ package com.aelitis.azureus.ui.swt.skin;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteMap;
/*     */ import java.util.Set;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
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
/*     */ public class UISWTViewSkinAdapter
/*     */ {
/*     */   private final String skin_folder;
/*     */   private final String skin_file;
/*     */   private final String wrapper_id;
/*     */   private final String target_id;
/*  46 */   private CopyOnWriteMap<UISWTView, ViewHolder> subviews = new CopyOnWriteMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public UISWTViewSkinAdapter(String _skin_folder, String _skin_file, String _wrapper_id, String _target_id)
/*     */   {
/*  55 */     this.skin_folder = _skin_folder;
/*  56 */     this.skin_file = _skin_file;
/*  57 */     this.wrapper_id = _wrapper_id;
/*  58 */     this.target_id = _target_id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean eventOccurred(UISWTViewEvent event)
/*     */   {
/*  65 */     UISWTView currentView = event.getView();
/*     */     
/*  67 */     switch (event.getType())
/*     */     {
/*     */     case 0: 
/*  70 */       SWTSkin skin = SWTSkinFactory.getNonPersistentInstance(getClass().getClassLoader(), this.skin_folder, this.skin_file);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*  75 */       this.subviews.put(currentView, new ViewHolder(currentView, skin, null));
/*     */       
/*  77 */       event.getView().setDestroyOnDeactivate(false);
/*     */       
/*  79 */       break;
/*     */     
/*     */ 
/*     */     case 2: 
/*  83 */       ViewHolder subview = (ViewHolder)this.subviews.get(currentView);
/*     */       
/*  85 */       if (subview != null)
/*     */       {
/*  87 */         subview.initialise((Composite)event.getData(), currentView.getDataSource());
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       break;
/*     */     case 1: 
/*  94 */       ViewHolder subview = (ViewHolder)this.subviews.get(currentView);
/*     */       
/*  96 */       if (subview != null)
/*     */       {
/*  98 */         subview.setDataSource(event.getData());
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       break;
/*     */     case 4: 
/* 106 */       ViewHolder subview = (ViewHolder)this.subviews.get(currentView);
/*     */       
/* 108 */       if (subview != null)
/*     */       {
/* 110 */         subview.focusLost();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       break;
/*     */     case 3: 
/* 117 */       ViewHolder subview = (ViewHolder)this.subviews.get(currentView);
/*     */       
/* 119 */       if (subview != null)
/*     */       {
/* 121 */         subview.focusGained();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       break;
/*     */     case 7: 
/* 128 */       ViewHolder subview = (ViewHolder)this.subviews.remove(currentView);
/*     */       
/* 130 */       if (subview != null)
/*     */       {
/* 132 */         subview.destroy();
/*     */       }
/*     */       
/*     */       break;
/*     */     }
/*     */     
/*     */     
/* 139 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   protected Set<UISWTView> getViews()
/*     */   {
/* 145 */     return this.subviews.keySet();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private class ViewHolder
/*     */   {
/*     */     private final UISWTView view;
/*     */     
/*     */     private final SWTSkin skin;
/*     */     
/*     */     private SWTSkinObject so;
/*     */     
/*     */ 
/*     */     private ViewHolder(UISWTView _view, SWTSkin _skin)
/*     */     {
/* 161 */       this.view = _view;
/* 162 */       this.skin = _skin;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     protected void initialise(Composite parent, Object data_source)
/*     */     {
/* 170 */       Composite skin_area = new Composite(parent, 0);
/*     */       
/* 172 */       skin_area.setLayout(new FormLayout());
/*     */       
/* 174 */       skin_area.setLayoutData(new GridData(1808));
/*     */       
/* 176 */       this.skin.initialize(skin_area, UISWTViewSkinAdapter.this.wrapper_id);
/*     */       
/* 178 */       this.so = this.skin.getSkinObjectByID(UISWTViewSkinAdapter.this.target_id);
/*     */       
/* 180 */       this.so.triggerListeners(7, data_source);
/*     */       
/* 182 */       this.so.setVisible(true);
/*     */       
/* 184 */       this.skin.layout();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void setDataSource(Object data_source)
/*     */     {
/* 191 */       if (this.so != null)
/*     */       {
/* 193 */         this.so.triggerListeners(7, data_source);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public void focusGained()
/*     */     {
/* 200 */       if (this.so != null)
/*     */       {
/* 202 */         this.so.setVisible(true);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public void focusLost()
/*     */     {
/* 209 */       if (this.so != null)
/*     */       {
/* 211 */         this.so.setVisible(false);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     protected void destroy()
/*     */     {
/* 218 */       if (this.so != null)
/*     */       {
/* 220 */         this.so.dispose();
/*     */         
/* 222 */         this.skin.removeSkinObject(this.so);
/*     */         
/* 224 */         this.so = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/UISWTViewSkinAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */