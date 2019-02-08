/*     */ package com.aelitis.azureus.ui.swt.views.skin;
/*     */ 
/*     */ import com.aelitis.azureus.activities.VuzeActivitiesEntry;
/*     */ import com.aelitis.azureus.activities.VuzeActivitiesManager;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility.ButtonListenerAdapter;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
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
/*     */ public class SBC_ActivityView
/*     */   extends SkinView
/*     */ {
/*     */   public static final String ID = "activity-list";
/*     */   public static final int MODE_BIGTABLE = -1;
/*     */   public static final int MODE_SMALLTABLE = 0;
/*     */   public static final int MODE_DEFAULT = 0;
/*  54 */   private static final String[] modeViewIDs = { "activity-small-area" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  59 */   private static final String[] modeIDs = { "activity.table.small" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  64 */   private int viewMode = -1;
/*     */   
/*     */   private SWTSkinButtonUtility btnSmallTable;
/*     */   
/*     */   private SWTSkinButtonUtility btnBigTable;
/*     */   
/*     */   private SWTSkinObject soListArea;
/*     */   
/*     */ 
/*     */   public Object skinObjectInitialShow(SWTSkinObject skinObject, Object params)
/*     */   {
/*  75 */     this.soListArea = getSkinObject("activity-list-area");
/*     */     
/*     */ 
/*  78 */     SWTSkinObject so = getSkinObject("activity-list-button-smalltable");
/*  79 */     if (so != null) {
/*  80 */       this.btnSmallTable = new SWTSkinButtonUtility(so);
/*  81 */       this.btnSmallTable.addSelectionListener(new SWTSkinButtonUtility.ButtonListenerAdapter()
/*     */       {
/*     */         public void pressed(SWTSkinButtonUtility buttonUtility, SWTSkinObject skinObject, int stateMask) {
/*  84 */           SBC_ActivityView.this.setViewMode(0, true);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*  89 */     so = getSkinObject("activity-list-button-bigtable");
/*  90 */     if (so != null) {
/*  91 */       this.btnBigTable = new SWTSkinButtonUtility(so);
/*  92 */       this.btnBigTable.addSelectionListener(new SWTSkinButtonUtility.ButtonListenerAdapter()
/*     */       {
/*     */         public void pressed(SWTSkinButtonUtility buttonUtility, SWTSkinObject skinObject, int stateMask) {
/*  95 */           SBC_ActivityView.this.setViewMode(-1, true);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 100 */     so = getSkinObject("activity-list-button-right");
/* 101 */     if (so != null) {
/* 102 */       so.setVisible(true);
/* 103 */       SWTSkinButtonUtility btnReadAll = new SWTSkinButtonUtility(so);
/* 104 */       btnReadAll.setTextID("v3.activity.button.readall");
/* 105 */       btnReadAll.addSelectionListener(new SWTSkinButtonUtility.ButtonListenerAdapter()
/*     */       {
/*     */         public void pressed(SWTSkinButtonUtility buttonUtility, SWTSkinObject skinObject, int stateMask) {
/* 108 */           List<VuzeActivitiesEntry> allEntries = VuzeActivitiesManager.getAllEntries();
/* 109 */           for (VuzeActivitiesEntry entry : allEntries) {
/* 110 */             entry.setRead(true);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 116 */     setViewMode(COConfigurationManager.getIntParameter("activity-list.viewmode", 0), false);
/*     */     
/*     */ 
/* 119 */     return null;
/*     */   }
/*     */   
/*     */   public Object skinObjectShown(SWTSkinObject skinObject, Object params) {
/* 123 */     VuzeActivitiesManager.pullActivitiesNow(0L, "shown", true);
/* 124 */     return super.skinObjectShown(skinObject, params);
/*     */   }
/*     */   
/*     */   public int getViewMode() {
/* 128 */     return this.viewMode;
/*     */   }
/*     */   
/*     */   public void setViewMode(int viewMode, boolean save) {
/* 132 */     if ((viewMode >= modeViewIDs.length) || (viewMode < 0)) {
/* 133 */       viewMode = 0;
/*     */     }
/*     */     
/* 136 */     if (viewMode == this.viewMode) {
/* 137 */       return;
/*     */     }
/*     */     
/* 140 */     int oldViewMode = this.viewMode;
/*     */     
/* 142 */     this.viewMode = viewMode;
/*     */     
/* 144 */     this.soListArea = getSkinObject("activity-list-area");
/*     */     
/* 146 */     this.soListArea.getControl().setData("ViewMode", new Long(viewMode));
/*     */     
/* 148 */     if ((oldViewMode >= 0) && (oldViewMode < modeViewIDs.length)) {
/* 149 */       SWTSkinObject soOldViewArea = getSkinObject(modeViewIDs[oldViewMode]);
/* 150 */       if (soOldViewArea != null) {
/* 151 */         soOldViewArea.setVisible(false);
/*     */       }
/*     */     }
/*     */     
/* 155 */     SWTSkinObject soViewArea = getSkinObject(modeViewIDs[viewMode]);
/* 156 */     if (soViewArea == null) {
/* 157 */       soViewArea = this.skin.createSkinObject(modeIDs[viewMode], modeIDs[viewMode], this.soListArea);
/*     */       
/* 159 */       this.skin.layout();
/* 160 */       soViewArea.setVisible(true);
/* 161 */       soViewArea.getControl().setLayoutData(Utils.getFilledFormData());
/*     */     } else {
/* 163 */       soViewArea.setVisible(true);
/*     */     }
/*     */     
/* 166 */     if (this.btnSmallTable != null) {
/* 167 */       this.btnSmallTable.getSkinObject().switchSuffix(viewMode == 0 ? "-selected" : "");
/*     */     }
/*     */     
/* 170 */     if (this.btnBigTable != null) {
/* 171 */       this.btnBigTable.getSkinObject().switchSuffix(viewMode == -1 ? "-selected" : "");
/*     */     }
/*     */     
/*     */ 
/* 175 */     if (save) {
/* 176 */       COConfigurationManager.setParameter("activity-list.viewmode", viewMode);
/*     */     }
/*     */     
/*     */ 
/* 180 */     MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 181 */     MdiEntry entry = mdi.getEntry("Activity");
/* 182 */     if (entry != null) {
/* 183 */       entry.setLogID("Activity-" + viewMode);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void removeSelected() {
/* 188 */     SBC_ActivityTableView tv = (SBC_ActivityTableView)SkinViewManager.getBySkinObjectID(modeIDs[this.viewMode]);
/* 189 */     if (tv != null) {
/* 190 */       tv.removeSelected();
/*     */     }
/*     */   }
/*     */   
/*     */   public int getNumSelected() {
/* 195 */     SBC_ActivityTableView tv = (SBC_ActivityTableView)SkinViewManager.getBySkinObjectID(modeIDs[this.viewMode]);
/* 196 */     if (tv != null) {
/* 197 */       return tv.getView().getSelectedRowsSize();
/*     */     }
/* 199 */     return 0;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/SBC_ActivityView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */