/*     */ package com.aelitis.azureus.ui.swt.skin;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.eclipse.swt.widgets.Composite;
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
/*     */ public class SWTSkinObjectTab
/*     */   extends SWTSkinObjectContainer
/*     */ {
/*  29 */   SWTSkinObject[] activeWidgets = null;
/*     */   
/*     */   SWTSkinObject activeWidgetsParent;
/*     */   
/*     */   SWTSkinTabSet tabset;
/*     */   
/*     */   public SWTSkinObjectTab(SWTSkin skin, SWTSkinProperties properties, String sID, String sConfigID, SWTSkinObject parent)
/*     */   {
/*  37 */     super(skin, properties, sID, sConfigID, parent);
/*  38 */     this.type = "tab";
/*     */   }
/*     */   
/*     */   public String[] getActiveWidgetIDs() {
/*  42 */     String[] sIDs = this.properties.getStringArray(getConfigID() + ".active-widgets");
/*  43 */     return sIDs;
/*     */   }
/*     */   
/*     */   public SWTSkinObject[] getActiveWidgets(boolean create) {
/*  47 */     if (this.activeWidgets == null)
/*     */     {
/*  49 */       String[] sIDs = getActiveWidgetIDs();
/*  50 */       ArrayList skinObjectArray = new ArrayList();
/*     */       
/*  52 */       if (sIDs != null) {
/*  53 */         for (int i = 0; i < sIDs.length; i++)
/*     */         {
/*     */ 
/*  56 */           SWTSkinObject skinObject = getSkin().getSkinObjectByID(sIDs[i], this.activeWidgetsParent);
/*     */           
/*     */ 
/*  59 */           if ((skinObject == null) && (create)) {
/*  60 */             SWTSkinObject soParent = this.skin.getSkinObjectByID(this.properties.getStringValue(getConfigID() + ".contentarea", (String)null), this.activeWidgetsParent);
/*     */             
/*     */ 
/*  63 */             if (soParent != null) {
/*  64 */               skinObject = this.skin.createSkinObject(sIDs[i], sIDs[i], soParent);
/*  65 */               this.skin.layout();
/*  66 */               this.skin.getShell().layout(true, true);
/*     */             }
/*     */           }
/*     */           
/*  70 */           if (skinObject != null) {
/*  71 */             skinObjectArray.add(skinObject);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*  76 */       if (skinObjectArray.size() == 0) {
/*  77 */         return new SWTSkinObject[0];
/*     */       }
/*     */       
/*  80 */       this.activeWidgets = new SWTSkinObject[skinObjectArray.size()];
/*  81 */       this.activeWidgets = ((SWTSkinObject[])skinObjectArray.toArray(this.activeWidgets));
/*     */     }
/*     */     
/*  84 */     return this.activeWidgets;
/*     */   }
/*     */   
/*     */   public void setActiveWidgets(SWTSkinObject[] skinObjects) {
/*  88 */     this.activeWidgets = skinObjects;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SWTSkinObject getActiveWidgetsParent()
/*     */   {
/*  97 */     return this.activeWidgetsParent;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setActiveWidgetsParent(SWTSkinObject activeWidgetsParent)
/*     */   {
/* 108 */     this.activeWidgetsParent = activeWidgetsParent;
/* 109 */     this.activeWidgets = null;
/*     */   }
/*     */   
/*     */   public SWTSkinTabSet getTabset() {
/* 113 */     return this.tabset;
/*     */   }
/*     */   
/*     */   public void setTabset(SWTSkinTabSet tabset) {
/* 117 */     this.tabset = tabset;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinObjectTab.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */