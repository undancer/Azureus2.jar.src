/*     */ package com.aelitis.azureus.ui.swt.views.skin;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor2;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class SkinViewManager
/*     */ {
/*  37 */   private static Map<Class<?>, List<SkinView>> mapSkinViews = new HashMap();
/*     */   
/*  39 */   private static AEMonitor2 mon_skinViews = new AEMonitor2("skinViews");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  44 */   private static Map<String, SkinView> skinIDs = new HashMap();
/*     */   
/*  46 */   private static Map<String, SkinView> skinViewIDs = new HashMap();
/*     */   
/*  48 */   private static List listeners = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void add(SkinView skinView)
/*     */   {
/*  55 */     mon_skinViews.enter();
/*     */     try {
/*  57 */       List<SkinView> list = (List)mapSkinViews.get(skinView.getClass());
/*  58 */       if (list == null) {
/*  59 */         list = new ArrayList(1);
/*  60 */         mapSkinViews.put(skinView.getClass(), list);
/*     */       }
/*  62 */       list.add(skinView);
/*     */     } finally {
/*  64 */       mon_skinViews.exit();
/*     */     }
/*     */     
/*  67 */     SWTSkinObject mainSkinObject = skinView.getMainSkinObject();
/*  68 */     if (mainSkinObject != null) {
/*  69 */       skinIDs.put(mainSkinObject.getSkinObjectID(), skinView);
/*  70 */       String viewID = mainSkinObject.getViewID();
/*  71 */       if ((viewID != null) && (viewID.length() > 0)) {
/*  72 */         skinViewIDs.put(viewID, skinView);
/*     */       }
/*     */     }
/*     */     
/*  76 */     triggerViewAddedListeners(skinView);
/*     */   }
/*     */   
/*     */   public static void remove(SkinView skinView) {
/*  80 */     if (skinView == null) {
/*  81 */       return;
/*     */     }
/*     */     
/*  84 */     mon_skinViews.enter();
/*     */     try {
/*  86 */       List<SkinView> list = (List)mapSkinViews.get(skinView.getClass());
/*  87 */       if (list != null) {
/*  88 */         list.remove(skinView);
/*  89 */         if (list.isEmpty()) {
/*  90 */           mapSkinViews.remove(skinView.getClass());
/*     */         }
/*     */       }
/*     */     } finally {
/*  94 */       mon_skinViews.exit();
/*     */     }
/*     */     
/*  97 */     SWTSkinObject mainSkinObject = skinView.getMainSkinObject();
/*  98 */     if (mainSkinObject != null) {
/*  99 */       skinIDs.remove(mainSkinObject.getSkinObjectID());
/* 100 */       skinViewIDs.remove(mainSkinObject.getViewID());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static SkinView getByClass(Class<?> cla)
/*     */   {
/* 111 */     List<SkinView> list = (List)mapSkinViews.get(cla);
/* 112 */     if (list == null) {
/* 113 */       return null;
/*     */     }
/*     */     
/* 116 */     Object[] skinViews = list.toArray();
/* 117 */     for (int i = 0; i < skinViews.length; i++) {
/* 118 */       SkinView sv = (SkinView)skinViews[i];
/*     */       
/* 120 */       SWTSkinObject so = sv.getMainSkinObject();
/* 121 */       if (so != null) {
/* 122 */         if (!so.isDisposed()) {
/* 123 */           return sv;
/*     */         }
/* 125 */         remove(sv);
/*     */       }
/*     */     }
/*     */     
/* 129 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static SkinView[] getMultiByClass(Class<?> cla)
/*     */   {
/* 139 */     List<SkinView> list = (List)mapSkinViews.get(cla);
/* 140 */     if (list == null) {
/* 141 */       return new SkinView[0];
/*     */     }
/* 143 */     return (SkinView[])list.toArray(new SkinView[0]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static SkinView getBySkinObjectID(String id)
/*     */   {
/* 153 */     SkinView sv = (SkinView)skinIDs.get(id);
/* 154 */     if (sv != null) {
/* 155 */       SWTSkinObject so = sv.getMainSkinObject();
/* 156 */       if ((so != null) && (so.isDisposed())) {
/* 157 */         remove(sv);
/* 158 */         return null;
/*     */       }
/*     */     }
/* 161 */     return sv;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static SkinView getByViewID(String viewID)
/*     */   {
/* 171 */     SkinView sv = (SkinView)skinViewIDs.get(viewID);
/* 172 */     if (sv != null) {
/* 173 */       SWTSkinObject so = sv.getMainSkinObject();
/* 174 */       if ((so != null) && (so.isDisposed())) {
/* 175 */         remove(sv);
/* 176 */         return null;
/*     */       }
/*     */     }
/* 179 */     return sv;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void addListener(SkinViewManagerListener l)
/*     */   {
/* 188 */     synchronized (SkinViewManager.class) {
/* 189 */       if (!listeners.contains(l)) {
/* 190 */         listeners.add(l);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void addListener(Class cla, SkinViewManagerListener l) {
/* 196 */     synchronized (SkinViewManager.class) {
/* 197 */       if (!listeners.contains(l)) {
/* 198 */         listeners.add(l);
/*     */       }
/*     */     }
/*     */     
/* 202 */     SkinView[] svs = getMultiByClass(cla);
/* 203 */     if (svs != null) {
/* 204 */       for (SkinView skinView : svs) {
/* 205 */         l.skinViewAdded(skinView);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void RemoveListener(SkinViewManagerListener l) {
/* 211 */     synchronized (SkinViewManager.class) {
/* 212 */       listeners.remove(l);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void triggerViewAddedListeners(SkinView skinView) {
/*     */     Object[] array;
/* 218 */     synchronized (SkinViewManager.class) {
/* 219 */       array = listeners.toArray();
/*     */     }
/* 221 */     for (int i = 0; i < array.length; i++) {
/* 222 */       SkinViewManagerListener l = (SkinViewManagerListener)array[i];
/*     */       try {
/* 224 */         l.skinViewAdded(skinView);
/*     */       } catch (Exception e) {
/* 226 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface SkinViewManagerListener
/*     */   {
/*     */     public abstract void skinViewAdded(SkinView paramSkinView);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/SkinViewManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */