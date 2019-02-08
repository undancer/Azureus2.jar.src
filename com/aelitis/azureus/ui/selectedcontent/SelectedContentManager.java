/*     */ package com.aelitis.azureus.ui.selectedcontent;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import com.aelitis.azureus.ui.common.table.TableView;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
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
/*     */ public class SelectedContentManager
/*     */ {
/*  40 */   private static CopyOnWriteList<SelectedContentListener> listeners = new CopyOnWriteList();
/*     */   
/*  42 */   private static volatile ISelectedContent[] currentlySelectedContent = new ISelectedContent[0];
/*     */   
/*  44 */   private static volatile String viewID = null;
/*     */   
/*  46 */   private static volatile TableView tv = null;
/*     */   
/*     */   public static String getCurrentySelectedViewID() {
/*  49 */     return viewID;
/*     */   }
/*     */   
/*     */   public static void addCurrentlySelectedContentListener(SelectedContentListener l)
/*     */   {
/*  54 */     if (listeners.contains(l)) {
/*  55 */       return;
/*     */     }
/*  57 */     listeners.add(l);
/*  58 */     l.currentlySelectedContentChanged(currentlySelectedContent, viewID);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*  63 */   public static void removeCurrentlySelectedContentListener(SelectedContentListener l) { listeners.remove(l); }
/*     */   
/*     */   public static void clearCurrentlySelectedContent() {
/*  66 */     changeCurrentlySelectedContentNoTrigger(null, null, null);
/*     */     
/*     */ 
/*     */ 
/*  70 */     triggerSelectedContentListeners();
/*     */   }
/*     */   
/*     */   public static void changeCurrentlySelectedContent(String viewID, ISelectedContent[] currentlySelectedContent)
/*     */   {
/*  75 */     changeCurrentlySelectedContent(viewID, currentlySelectedContent, null);
/*     */   }
/*     */   
/*     */   public static void changeCurrentlySelectedContent(String viewID, ISelectedContent[] currentlySelectedContent, TableView tv)
/*     */   {
/*  80 */     changeCurrentlySelectedContentNoTrigger(viewID, currentlySelectedContent, tv);
/*  81 */     triggerSelectedContentListeners();
/*     */   }
/*     */   
/*     */   private static void changeCurrentlySelectedContentNoTrigger(String viewID, ISelectedContent[] currentlySelectedContent, TableView tv)
/*     */   {
/*  86 */     if (currentlySelectedContent == null) {
/*  87 */       currentlySelectedContent = new ISelectedContent[0];
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  98 */     if ((currentlySelectedContent.length == 0) && (viewID != null) && (viewID != null) && (!viewID.equals(viewID)))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 103 */       return;
/*     */     }
/*     */     
/* 106 */     synchronized (SelectedContentManager.class) {
/* 107 */       boolean same = tv == tv;
/*     */       
/* 109 */       if (same)
/*     */       {
/* 111 */         same = (viewID == viewID) || ((viewID != null) && (viewID != null) && (viewID.equals(viewID)));
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 117 */         if (same)
/*     */         {
/* 119 */           if (currentlySelectedContent.length == currentlySelectedContent.length)
/*     */           {
/* 121 */             for (int i = 0; (i < currentlySelectedContent.length) && (same); i++)
/*     */             {
/* 123 */               same = currentlySelectedContent[i].sameAs(currentlySelectedContent[i]);
/*     */             }
/*     */             
/* 126 */             if (same)
/*     */             {
/* 128 */               return;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 134 */       tv = tv;
/* 135 */       currentlySelectedContent = currentlySelectedContent;
/* 136 */       viewID = viewID;
/*     */     }
/*     */   }
/*     */   
/*     */   public static void triggerSelectedContentListeners() {
/* 141 */     for (SelectedContentListener l : listeners) {
/*     */       try
/*     */       {
/* 144 */         l.currentlySelectedContentChanged(currentlySelectedContent, viewID);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 148 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static ISelectedContent[] getCurrentlySelectedContent() {
/* 154 */     return currentlySelectedContent;
/*     */   }
/*     */   
/*     */   public static DownloadManager[] getDMSFromSelectedContent() {
/* 158 */     ISelectedContent[] sc = getCurrentlySelectedContent();
/* 159 */     if (sc.length > 0) {
/* 160 */       int x = 0;
/* 161 */       DownloadManager[] dms = new DownloadManager[sc.length];
/* 162 */       for (int i = 0; i < sc.length; i++) {
/* 163 */         ISelectedContent selectedContent = sc[i];
/* 164 */         if (selectedContent != null)
/*     */         {
/*     */ 
/* 167 */           dms[x] = selectedContent.getDownloadManager();
/* 168 */           if (dms[x] != null)
/* 169 */             x++;
/*     */         }
/*     */       }
/* 172 */       if (x > 0) {
/* 173 */         System.arraycopy(dms, 0, dms, 0, x);
/* 174 */         return dms;
/*     */       }
/*     */     }
/* 177 */     return null;
/*     */   }
/*     */   
/*     */   public static TableView getCurrentlySelectedTableView() {
/* 181 */     return tv;
/*     */   }
/*     */   
/*     */   public static Object convertSelectedContentToObject(ISelectedContent[] contents) {
/* 185 */     if (contents == null) {
/* 186 */       contents = getCurrentlySelectedContent();
/*     */     }
/* 188 */     if (contents.length == 0) {
/* 189 */       TableView tv = getCurrentlySelectedTableView();
/* 190 */       if (tv != null) {
/* 191 */         return tv.getSelectedDataSources(false);
/*     */       }
/* 193 */       return null;
/*     */     }
/* 195 */     if (contents.length == 1) {
/* 196 */       return selectedContentToObject(contents[0]);
/*     */     }
/* 198 */     Object[] objects = new Object[contents.length];
/* 199 */     for (int i = 0; i < contents.length; i++) {
/* 200 */       ISelectedContent content = contents[i];
/* 201 */       objects[i] = selectedContentToObject(content);
/*     */     }
/* 203 */     return objects;
/*     */   }
/*     */   
/*     */   private static Object selectedContentToObject(ISelectedContent content) {
/* 207 */     DownloadManager dm = content.getDownloadManager();
/* 208 */     if (dm == null) {
/* 209 */       return null;
/*     */     }
/* 211 */     Download dl = PluginCoreUtils.wrap(dm);
/* 212 */     if (dl == null) {
/* 213 */       return null;
/*     */     }
/* 215 */     int i = content.getFileIndex();
/* 216 */     if (i < 0) {
/* 217 */       return dl;
/*     */     }
/* 219 */     return dl.getDiskManagerFileInfo(i);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/selectedcontent/SelectedContentManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */