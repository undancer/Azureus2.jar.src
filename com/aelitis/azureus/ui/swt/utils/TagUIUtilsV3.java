/*     */ package com.aelitis.azureus.ui.swt.utils;
/*     */ 
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagException;
/*     */ import com.aelitis.azureus.core.tag.TagManager;
/*     */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*     */ import com.aelitis.azureus.core.tag.TagType;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctions.TagReturner;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinCheckboxListener;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectCheckbox;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectCombo;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectContainer;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectTextbox;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SkinnedDialog;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.StandardButtonsArea;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
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
/*     */ public class TagUIUtilsV3
/*     */ {
/*     */   public static void showCreateTagDialog(final UIFunctions.TagReturner tagReturner)
/*     */   {
/*  47 */     final SkinnedDialog dialog = new SkinnedDialog("skin3_dlg_addtag", "shell", 2144);
/*     */     
/*  49 */     SWTSkin skin = dialog.getSkin();
/*     */     
/*  51 */     SWTSkinObjectTextbox tb = (SWTSkinObjectTextbox)skin.getSkinObject("tag-name");
/*     */     
/*  53 */     final SWTSkinObjectCheckbox cb = (SWTSkinObjectCheckbox)skin.getSkinObject("tag-share");
/*     */     
/*     */ 
/*  56 */     final SWTSkinObjectCheckbox ss = (SWTSkinObjectCheckbox)skin.getSkinObject("tag-customize");
/*     */     
/*     */ 
/*  59 */     if ((tb == null) || (cb == null)) {
/*  60 */       return;
/*     */     }
/*     */     
/*  63 */     SWTSkinObjectContainer soGroupBox = (SWTSkinObjectContainer)skin.getSkinObject("tag-group-area");
/*     */     
/*     */ 
/*  66 */     final SWTSkinObjectCombo soGroup = (SWTSkinObjectCombo)skin.getSkinObject("tag-group");
/*     */     
/*     */ 
/*  69 */     if ((soGroupBox != null) && (soGroup != null)) {
/*  70 */       List<String> listGroups = new ArrayList();
/*  71 */       TagManager tagManager = TagManagerFactory.getTagManager();
/*  72 */       TagType tt = tagManager.getTagType(3);
/*  73 */       List<Tag> tags = tt.getTags();
/*  74 */       for (Tag tag : tags) {
/*  75 */         String group = tag.getGroup();
/*  76 */         if ((group != null) && (group.length() > 0) && (!listGroups.contains(group))) {
/*  77 */           listGroups.add(group);
/*     */         }
/*     */       }
/*     */       
/*  81 */       soGroupBox.setVisible(listGroups.size() > 0);
/*  82 */       soGroup.setList((String[])listGroups.toArray(new String[0]));
/*     */     }
/*     */     
/*  85 */     cb.setChecked(COConfigurationManager.getBooleanParameter("tag.sharing.default.checked"));
/*     */     
/*     */ 
/*  88 */     if (ss != null)
/*     */     {
/*  90 */       ss.setChecked(COConfigurationManager.getBooleanParameter("tag.add.customize.default.checked"));
/*     */       
/*     */ 
/*  93 */       ss.addSelectionListener(new SWTSkinCheckboxListener()
/*     */       {
/*     */ 
/*     */         public void checkboxChanged(SWTSkinObjectCheckbox so, boolean checked)
/*     */         {
/*  98 */           COConfigurationManager.setParameter("tag.add.customize.default.checked", checked);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */ 
/* 104 */     SWTSkinObject soButtonArea = skin.getSkinObject("bottom-area");
/* 105 */     if ((soButtonArea instanceof SWTSkinObjectContainer)) {
/* 106 */       StandardButtonsArea buttonsArea = new StandardButtonsArea()
/*     */       {
/*     */         protected void clicked(int buttonValue) {
/* 109 */           if (buttonValue == 32)
/*     */           {
/* 111 */             String tag_name = this.val$tb.getText().trim();
/* 112 */             TagType tt = TagManagerFactory.getTagManager().getTagType(3);
/*     */             
/*     */ 
/* 115 */             Tag tag = tt.getTag(tag_name, true);
/*     */             
/* 117 */             if (tag == null)
/*     */             {
/*     */               try
/*     */               {
/* 121 */                 tag = tt.createTag(tag_name, true);
/*     */                 
/* 123 */                 tag.setPublic(cb.isChecked());
/*     */                 
/* 125 */                 if (soGroup != null) {
/* 126 */                   String group = soGroup.getText();
/* 127 */                   if ((group != null) && (group.length() > 0)) {
/* 128 */                     tag.setGroup(group);
/*     */                   }
/*     */                 }
/*     */               }
/*     */               catch (TagException e)
/*     */               {
/* 134 */                 Debug.out(e);
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 141 */             if ((tagReturner != null) && (tag != null)) {
/* 142 */               tagReturner.returnedTags(new Tag[] { tag });
/*     */             }
/*     */             
/*     */ 
/*     */ 
/* 147 */             if (ss.isChecked()) {
/* 148 */               tag.setTransientProperty("Settings Requested", Boolean.valueOf(true));
/*     */               
/* 150 */               UIFunctionsManager.getUIFunctions().getMDI().showEntryByID("TagsOverview");
/*     */             }
/*     */           }
/*     */           
/*     */ 
/* 155 */           dialog.close();
/*     */         }
/* 157 */       };
/* 158 */       buttonsArea.setButtonIDs(new String[] { MessageText.getString("Button.add"), MessageText.getString("Button.cancel") });
/*     */       
/*     */ 
/*     */ 
/* 162 */       buttonsArea.setButtonVals(new Integer[] { Integer.valueOf(32), Integer.valueOf(256) });
/*     */       
/*     */ 
/*     */ 
/* 166 */       buttonsArea.swt_createButtons(((SWTSkinObjectContainer)soButtonArea).getComposite());
/*     */     }
/*     */     
/*     */ 
/* 170 */     dialog.open();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/utils/TagUIUtilsV3.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */