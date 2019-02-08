/*     */ package com.aelitis.azureus.ui.swt.views.skin;
/*     */ 
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManager;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManagerFactory;
/*     */ import com.aelitis.azureus.core.util.FeatureAvailability;
/*     */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfo;
/*     */ import com.aelitis.azureus.ui.mdi.MdiChildCloseListener;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryCreationListener;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryLoadedListener;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import com.aelitis.azureus.ui.swt.mdi.BaseMdiEntry;
/*     */ import com.aelitis.azureus.util.ConstantsVuze;
/*     */ import com.aelitis.azureus.util.ContentNetworkUtils;
/*     */ import java.util.ArrayList;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.gudy.azureus2.core3.util.Constants;
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
/*     */ public class SB_Discovery
/*     */ {
/*  48 */   private ArrayList<MdiEntry> children = new ArrayList();
/*     */   private ViewTitleInfo titleInfo;
/*     */   
/*     */   public SB_Discovery(MultipleDocumentInterface mdi)
/*     */   {
/*  53 */     setup(mdi);
/*     */   }
/*     */   
/*     */   private void setup(final MultipleDocumentInterface mdi) {
/*  57 */     MdiEntry entry = mdi.createEntryFromSkinRef("header.discovery", ContentNetworkUtils.getTarget(ConstantsVuze.getDefaultContentNetwork()), "main.area.browsetab", "{sidebar.VuzeHDNetwork}", null, null, false, null);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  62 */     entry.setImageLeftID("image.sidebar.vuze");
/*     */     
/*  64 */     if ((Constants.isWindows) && (FeatureAvailability.isGamesEnabled())) {
/*  65 */       mdi.registerEntry("Games", new MdiEntryCreationListener()
/*     */       {
/*     */         public MdiEntry createMDiEntry(String id) {
/*  68 */           MdiEntry entry = mdi.createEntryFromSkinRef("header.discovery", "Games", "main.generic.browse", "{mdi.entry.games}", null, null, true, null);
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  74 */           ((BaseMdiEntry)entry).setPreferredAfterID(ContentNetworkUtils.getTarget(ConstantsVuze.getDefaultContentNetwork()));
/*  75 */           String url = ConstantsVuze.getDefaultContentNetwork().getSiteRelativeURL("starts/games.start", false);
/*     */           
/*  77 */           entry.setDatasource(url);
/*  78 */           entry.setImageLeftID("image.sidebar.games");
/*  79 */           return entry;
/*     */         }
/*  81 */       });
/*  82 */       mdi.loadEntryByID("Games", false, true, null);
/*     */     }
/*     */     
/*     */ 
/*  86 */     mdi.registerEntry("ContentNetwork\\..*", new MdiEntryCreationListener() {
/*     */       public MdiEntry createMDiEntry(String id) {
/*  88 */         long networkID = Long.parseLong(id.substring(15));
/*  89 */         return SB_Discovery.this.handleContentNetworkSwitch(mdi, id, networkID);
/*     */       }
/*     */       
/*  92 */     });
/*  93 */     mdi.addListener(new MdiEntryLoadedListener() {
/*     */       public void mdiEntryLoaded(MdiEntry entry) {
/*  95 */         if ("header.discovery".equals(entry.getParentID())) {
/*  96 */           SB_Discovery.this.children.add(entry);
/*  97 */           entry.addListener(new MdiChildCloseListener()
/*     */           {
/*     */             public void mdiChildEntryClosed(MdiEntry parent, MdiEntry child, boolean user) {
/* 100 */               SB_Discovery.this.children.remove(child);
/*     */             }
/*     */           });
/*     */         }
/* 104 */         if (!entry.getId().equals("header.discovery")) {
/* 105 */           return;
/*     */         }
/* 107 */         SB_Discovery.this.setupHeader(entry);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void setupHeader(final MdiEntry entry)
/*     */   {
/* 114 */     this.titleInfo = new ViewTitleInfo() {
/*     */       public Object getTitleInfoProperty(int propertyID) {
/* 116 */         if (propertyID == 0) {
/* 117 */           if (entry.isExpanded()) {
/* 118 */             return null;
/*     */           }
/* 120 */           StringBuilder sb = new StringBuilder();
/* 121 */           MdiEntry[] entries = entry.getMDI().getEntries();
/* 122 */           for (MdiEntry subEntry : entries)
/*     */           {
/* 124 */             if (!subEntry.getId().startsWith("Subscription_"))
/*     */             {
/*     */ 
/* 127 */               if (entry.getId().equals(subEntry.getParentID())) {
/* 128 */                 ViewTitleInfo titleInfo = subEntry.getViewTitleInfo();
/* 129 */                 if (titleInfo != null) {
/* 130 */                   Object text = titleInfo.getTitleInfoProperty(0);
/* 131 */                   if ((text instanceof String)) {
/* 132 */                     if (sb.length() > 0) {
/* 133 */                       sb.append(" | ");
/*     */                     }
/* 135 */                     sb.append(text);
/*     */                   }
/*     */                 }
/*     */               } }
/*     */           }
/* 140 */           if (sb.length() > 0) {
/* 141 */             return sb.toString();
/*     */           }
/* 143 */         } else if (propertyID == 1) {
/* 144 */           if (entry.isExpanded()) {
/* 145 */             return null;
/*     */           }
/* 147 */           StringBuilder sb = new StringBuilder();
/* 148 */           MdiEntry[] entries = entry.getMDI().getEntries();
/* 149 */           for (MdiEntry subEntry : entries) {
/* 150 */             if (entry.getId().equals(subEntry.getParentID())) {
/* 151 */               ViewTitleInfo titleInfo = subEntry.getViewTitleInfo();
/* 152 */               if (titleInfo != null) {
/* 153 */                 Object text = titleInfo.getTitleInfoProperty(0);
/* 154 */                 if ((text instanceof String)) {
/* 155 */                   if (sb.length() > 0) {
/* 156 */                     sb.append("\n");
/*     */                   }
/* 158 */                   sb.append(subEntry.getTitle()).append(": ").append(text);
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/* 163 */           if (sb.length() > 0) {
/* 164 */             return sb.toString();
/*     */           }
/*     */         }
/* 167 */         return null;
/*     */       }
/* 169 */     };
/* 170 */     entry.setViewTitleInfo(this.titleInfo);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected BaseMdiEntry handleContentNetworkSwitch(MultipleDocumentInterface mdi, String tabID, long networkID)
/*     */   {
/* 180 */     String defaultID = ContentNetworkUtils.getTarget(ConstantsVuze.getDefaultContentNetwork());
/*     */     try {
/* 182 */       ContentNetworkManager cnManager = ContentNetworkManagerFactory.getSingleton();
/* 183 */       if (cnManager == null) {
/* 184 */         mdi.showEntryByID(defaultID);
/* 185 */         return null;
/*     */       }
/*     */       
/* 188 */       ContentNetwork cn = cnManager.getContentNetwork(networkID);
/* 189 */       if (cn == null) {
/* 190 */         mdi.showEntryByID(defaultID);
/* 191 */         return null;
/*     */       }
/*     */       
/* 194 */       if (networkID == 1L) {
/* 195 */         mdi.showEntryByID(defaultID);
/* 196 */         cn.setPersistentProperty("active", Boolean.TRUE);
/* 197 */         return null;
/*     */       }
/*     */       
/* 200 */       return createContentNetworkSideBarEntry(mdi, cn);
/*     */     } catch (Exception e) {
/* 202 */       Debug.out(e);
/*     */       
/* 204 */       mdi.showEntryByID(defaultID);
/*     */     }
/* 206 */     return null;
/*     */   }
/*     */   
/*     */   private BaseMdiEntry createContentNetworkSideBarEntry(MultipleDocumentInterface mdi, ContentNetwork cn) {
/* 210 */     String entryID = ContentNetworkUtils.getTarget(cn);
/*     */     
/* 212 */     if (mdi.entryExists(entryID)) {
/* 213 */       return null;
/*     */     }
/*     */     
/* 216 */     String name = cn.getName();
/*     */     
/* 218 */     Object prop = cn.getProperty(2);
/* 219 */     boolean closeable = (prop instanceof Boolean) ? ((Boolean)prop).booleanValue() : false;
/*     */     
/* 221 */     BaseMdiEntry entry = (BaseMdiEntry)mdi.createEntryFromSkinRef("header.discovery", entryID, "main.area.browsetab", name, null, cn, closeable, null);
/*     */     
/*     */ 
/*     */ 
/* 225 */     Image image = ImageLoader.getInstance().getImage("image.sidebar.vuze");
/* 226 */     entry.setImageLeft(image);
/*     */     
/* 228 */     cn.setPersistentProperty("active", Boolean.TRUE);
/* 229 */     cn.setPersistentProperty("in_menu", Boolean.TRUE);
/*     */     
/* 231 */     return entry;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/SB_Discovery.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */