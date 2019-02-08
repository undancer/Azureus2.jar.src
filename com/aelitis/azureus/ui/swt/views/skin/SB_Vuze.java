/*     */ package com.aelitis.azureus.ui.swt.views.skin;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfo;
/*     */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfoListener;
/*     */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfoManager;
/*     */ import com.aelitis.azureus.ui.mdi.MdiChildCloseListener;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryLoadedListener;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.swt.views.ViewTitleInfoBetaP;
/*     */ import java.util.ArrayList;
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
/*     */ public class SB_Vuze
/*     */ {
/*  31 */   private ArrayList<MdiEntry> children = new ArrayList(4);
/*     */   private ViewTitleInfo titleInfo;
/*     */   
/*     */   public SB_Vuze(MultipleDocumentInterface mdi)
/*     */   {
/*  36 */     setup(mdi);
/*     */   }
/*     */   
/*     */   private void setup(MultipleDocumentInterface mdi)
/*     */   {
/*  41 */     ViewTitleInfoBetaP.setupSidebarEntry(mdi);
/*     */     
/*  43 */     WelcomeView.setupSidebarEntry(mdi);
/*     */     
/*  45 */     SBC_ActivityTableView.setupSidebarEntry(mdi);
/*     */     
/*     */ 
/*  48 */     ViewTitleInfoManager.addListener(new ViewTitleInfoListener() {
/*     */       public void viewTitleInfoRefresh(ViewTitleInfo titleInfo) {
/*  50 */         if (SB_Vuze.this.titleInfo == null) {
/*  51 */           return;
/*     */         }
/*  53 */         MdiEntry[] childrenArray = (MdiEntry[])SB_Vuze.this.children.toArray(new MdiEntry[0]);
/*  54 */         for (MdiEntry entry : childrenArray) {
/*  55 */           if (entry.getViewTitleInfo() == titleInfo) {
/*  56 */             ViewTitleInfoManager.refreshTitleInfo(SB_Vuze.this.titleInfo);
/*  57 */             break;
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */       }
/*  63 */     });
/*  64 */     mdi.addListener(new MdiEntryLoadedListener() {
/*     */       public void mdiEntryLoaded(MdiEntry entry) {
/*  66 */         if ("header.vuze".equals(entry.getParentID()))
/*     */         {
/*  68 */           SB_Vuze.this.children.add(entry);
/*  69 */           entry.addListener(new MdiChildCloseListener()
/*     */           {
/*     */             public void mdiChildEntryClosed(MdiEntry parent, MdiEntry child, boolean user) {
/*  72 */               SB_Vuze.this.children.remove(child);
/*     */             }
/*     */           });
/*     */         }
/*  76 */         if (!entry.getId().equals("header.vuze"))
/*     */         {
/*  78 */           return;
/*     */         }
/*  80 */         SB_Vuze.this.titleInfo = new SB_Vuze.ViewTitleInfo_Vuze(entry);
/*  81 */         entry.setViewTitleInfo(SB_Vuze.this.titleInfo);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private static class ViewTitleInfo_Vuze implements ViewTitleInfo
/*     */   {
/*     */     private MdiEntry entry;
/*     */     
/*     */     public ViewTitleInfo_Vuze(MdiEntry entry)
/*     */     {
/*  92 */       this.entry = entry;
/*     */     }
/*     */     
/*     */     public Object getTitleInfoProperty(int propertyID) {
/*  96 */       if (propertyID == 0) {
/*  97 */         if (this.entry.isExpanded()) {
/*  98 */           return null;
/*     */         }
/* 100 */         StringBuilder sb = new StringBuilder();
/* 101 */         MdiEntry[] entries = this.entry.getMDI().getEntries();
/* 102 */         for (MdiEntry subEntry : entries) {
/* 103 */           if (this.entry.getId().equals(subEntry.getParentID())) {
/* 104 */             ViewTitleInfo titleInfo = subEntry.getViewTitleInfo();
/* 105 */             if (titleInfo != null) {
/* 106 */               Object text = titleInfo.getTitleInfoProperty(0);
/*     */               
/* 108 */               if ((text instanceof String)) {
/* 109 */                 if (sb.length() > 0) {
/* 110 */                   sb.append(" | ");
/*     */                 }
/* 112 */                 sb.append(text);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/* 117 */         if (sb.length() > 0) {
/* 118 */           return sb.toString();
/*     */         }
/* 120 */       } else if (propertyID == 1) {
/* 121 */         if (this.entry.isExpanded()) {
/* 122 */           return null;
/*     */         }
/* 124 */         StringBuilder sb = new StringBuilder();
/* 125 */         MdiEntry[] entries = this.entry.getMDI().getEntries();
/* 126 */         for (MdiEntry subEntry : entries) {
/* 127 */           if (this.entry.getId().equals(subEntry.getParentID())) {
/* 128 */             ViewTitleInfo titleInfo = subEntry.getViewTitleInfo();
/* 129 */             if (titleInfo != null) {
/* 130 */               Object text = titleInfo.getTitleInfoProperty(0);
/*     */               
/* 132 */               if ((text instanceof String)) {
/* 133 */                 if (sb.length() > 0) {
/* 134 */                   sb.append("\n");
/*     */                 }
/* 136 */                 sb.append(subEntry.getTitle()).append(": ").append(text);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/* 141 */         if (sb.length() > 0) {
/* 142 */           return sb.toString();
/*     */         }
/* 144 */       } else if (propertyID == 8) {
/* 145 */         if (this.entry.isExpanded()) {
/* 146 */           return null;
/*     */         }
/* 148 */         MdiEntry[] entries = this.entry.getMDI().getEntries();
/* 149 */         for (MdiEntry subEntry : entries) {
/* 150 */           if (this.entry.getId().equals(subEntry.getParentID())) {
/* 151 */             ViewTitleInfo titleInfo = subEntry.getViewTitleInfo();
/* 152 */             if (titleInfo != null) {
/* 153 */               Object color = titleInfo.getTitleInfoProperty(8);
/*     */               
/* 155 */               if ((color instanceof int[])) {
/* 156 */                 return color;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/* 161 */         return null;
/*     */       }
/* 163 */       return null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/SB_Vuze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */