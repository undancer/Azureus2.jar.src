/*     */ package org.gudy.azureus2.ui.swt.views;
/*     */ 
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagManager;
/*     */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*     */ import com.aelitis.azureus.core.tag.TagType;
/*     */ import com.aelitis.azureus.core.tag.TagTypeListener;
/*     */ import com.aelitis.azureus.core.tag.TagTypeListener.TagEvent;
/*     */ import com.aelitis.azureus.core.tag.Taggable;
/*     */ import com.aelitis.azureus.ui.UIFunctions.TagReturner;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.custom.ScrolledComposite;
/*     */ import org.eclipse.swt.events.ControlAdapter;
/*     */ import org.eclipse.swt.events.ControlEvent;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Layout;
/*     */ import org.eclipse.swt.widgets.ScrollBar;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
/*     */ import org.gudy.azureus2.ui.swt.views.utils.TagButtonsUI;
/*     */ import org.gudy.azureus2.ui.swt.views.utils.TagButtonsUI.TagButtonTrigger;
/*     */ import org.gudy.azureus2.ui.swt.views.utils.TagUIUtils;
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
/*     */ public class TaggingView
/*     */   implements UISWTViewCoreEventListener, TagTypeListener
/*     */ {
/*     */   public static final String MSGID_PREFIX = "TaggingView";
/*     */   private UISWTView swtView;
/*     */   private Composite cMainComposite;
/*     */   private ScrolledComposite sc;
/*     */   private List<Taggable> taggables;
/*     */   private Composite parent;
/*     */   private TagButtonsUI tagButtonsUI;
/*     */   
/*     */   public boolean eventOccurred(UISWTViewEvent event)
/*     */   {
/*  83 */     switch (event.getType()) {
/*     */     case 0: 
/*  85 */       this.swtView = ((UISWTView)event.getData());
/*  86 */       this.swtView.setTitle(getFullTitle());
/*  87 */       break;
/*     */     
/*     */     case 7: 
/*  90 */       delete();
/*  91 */       break;
/*     */     
/*     */     case 2: 
/*  94 */       this.parent = ((Composite)event.getData());
/*  95 */       break;
/*     */     
/*     */     case 6: 
/*  98 */       Messages.updateLanguageForControl(this.cMainComposite);
/*  99 */       this.swtView.setTitle(getFullTitle());
/* 100 */       break;
/*     */     
/*     */     case 1: 
/* 103 */       Object ds = event.getData();
/* 104 */       dataSourceChanged(ds);
/* 105 */       break;
/*     */     
/*     */     case 3: 
/* 108 */       initialize();
/* 109 */       if (this.taggables == null) {
/* 110 */         dataSourceChanged(this.swtView.getDataSource());
/*     */       }
/*     */       
/*     */       break;
/*     */     case 4: 
/* 115 */       delete();
/* 116 */       break;
/*     */     
/*     */     case 5: 
/* 119 */       refresh();
/*     */     }
/*     */     
/*     */     
/* 123 */     return true;
/*     */   }
/*     */   
/*     */   private void delete() {
/* 127 */     Utils.disposeComposite(this.sc);
/* 128 */     dataSourceChanged(null);
/*     */   }
/*     */   
/*     */   private void refresh() {}
/*     */   
/*     */   private void dataSourceChanged(Object ds)
/*     */   {
/* 135 */     boolean wasNull = this.taggables == null;
/*     */     
/* 137 */     if ((ds instanceof Taggable)) {
/* 138 */       this.taggables = new ArrayList();
/* 139 */       this.taggables.add((Taggable)ds);
/* 140 */     } else if ((ds instanceof Taggable[])) {
/* 141 */       this.taggables = new ArrayList();
/* 142 */       this.taggables.addAll(Arrays.asList((Taggable[])ds));
/* 143 */     } else if ((ds instanceof Object[])) {
/* 144 */       this.taggables = new ArrayList();
/* 145 */       Object[] objects = (Object[])ds;
/* 146 */       for (Object o : objects) {
/* 147 */         if ((o instanceof Taggable)) {
/* 148 */           Taggable taggable = (Taggable)o;
/* 149 */           if (!this.taggables.contains(taggable)) {
/* 150 */             this.taggables.add(taggable);
/*     */           }
/* 152 */         } else if ((o instanceof DiskManagerFileInfo)) {
/* 153 */           DownloadManager temp = ((DiskManagerFileInfo)o).getDownloadManager();
/* 154 */           if ((temp != null) && 
/* 155 */             (!this.taggables.contains(temp))) {
/* 156 */             this.taggables.add(temp);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 161 */       if (this.taggables.size() == 0) {
/* 162 */         this.taggables = null;
/*     */       }
/*     */     } else {
/* 165 */       this.taggables = null;
/*     */     }
/*     */     
/* 168 */     boolean isNull = this.taggables == null;
/* 169 */     if (isNull != wasNull) {
/* 170 */       TagManager tm = TagManagerFactory.getTagManager();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 180 */       TagType tagType = tm.getTagType(3);
/* 181 */       if (isNull) {
/* 182 */         tagType.removeTagTypeListener(this);
/*     */       } else {
/* 184 */         tagType.addTagTypeListener(this, false);
/*     */       }
/*     */     }
/*     */     
/* 188 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 190 */         TaggingView.this.swt_updateFields();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void initialize() {
/* 196 */     if ((this.cMainComposite == null) || (this.cMainComposite.isDisposed())) {
/* 197 */       if ((this.parent == null) || (this.parent.isDisposed())) {
/* 198 */         return;
/*     */       }
/* 200 */       this.sc = new ScrolledComposite(this.parent, 512);
/* 201 */       this.sc.setExpandHorizontal(true);
/* 202 */       this.sc.setExpandVertical(true);
/* 203 */       this.sc.getVerticalBar().setIncrement(16);
/* 204 */       Layout parentLayout = this.parent.getLayout();
/* 205 */       if ((parentLayout instanceof GridLayout)) {
/* 206 */         GridData gd = new GridData(4, 4, true, true);
/* 207 */         this.sc.setLayoutData(gd);
/* 208 */       } else if ((parentLayout instanceof FormLayout)) {
/* 209 */         this.sc.setLayoutData(Utils.getFilledFormData());
/*     */       }
/*     */       
/* 212 */       this.cMainComposite = new Composite(this.sc, 0);
/*     */       
/* 214 */       this.sc.setContent(this.cMainComposite);
/*     */     } else {
/* 216 */       Utils.disposeComposite(this.cMainComposite, false);
/*     */     }
/*     */     
/* 219 */     this.cMainComposite.setLayout(new GridLayout(1, false));
/*     */     
/* 221 */     TagManager tm = TagManagerFactory.getTagManager();
/* 222 */     int[] tagTypesWanted = { 3 };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 227 */     this.tagButtonsUI = new TagButtonsUI();
/*     */     
/* 229 */     List<Tag> listAllTags = new ArrayList();
/*     */     
/* 231 */     for (int tagType : tagTypesWanted)
/*     */     {
/* 233 */       TagType tt = tm.getTagType(tagType);
/* 234 */       List<Tag> tags = tt.getTags();
/* 235 */       listAllTags.addAll(tags);
/*     */     }
/* 237 */     this.tagButtonsUI.buildTagGroup(listAllTags, this.cMainComposite, new TagButtonsUI.TagButtonTrigger()
/*     */     {
/*     */       public void tagButtonTriggered(Tag tag, boolean doTag) {
/* 240 */         for (Taggable taggable : TaggingView.this.taggables) {
/* 241 */           if (doTag) {
/* 242 */             tag.addTaggable(taggable);
/*     */           } else {
/* 244 */             tag.removeTaggable(taggable);
/*     */           }
/* 246 */           TaggingView.this.swt_updateFields();
/*     */         }
/*     */         
/*     */       }
/* 250 */     });
/* 251 */     Button buttonAdd = new Button(this.cMainComposite, 8);
/* 252 */     buttonAdd.setLayoutData(new GridData(16777216, 4, false, false));
/* 253 */     Messages.setLanguageText(buttonAdd, "label.add.tag");
/* 254 */     buttonAdd.addSelectionListener(new SelectionListener() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 256 */         TagUIUtils.createManualTag(new UIFunctions.TagReturner() {
/*     */           public void returnedTags(Tag[] tags) {
/* 258 */             if (TaggingView.this.taggables == null)
/*     */               return;
/*     */             Tag tag;
/* 261 */             for (tag : tags) {
/* 262 */               for (Taggable taggable : TaggingView.this.taggables) {
/* 263 */                 tag.addTaggable(taggable);
/*     */               }
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void widgetDefaultSelected(SelectionEvent e) {}
/* 273 */     });
/* 274 */     this.sc.addControlListener(new ControlAdapter() {
/*     */       public void controlResized(ControlEvent e) {
/* 276 */         Rectangle r = TaggingView.this.sc.getClientArea();
/* 277 */         Point size = TaggingView.this.cMainComposite.computeSize(r.width, -1);
/* 278 */         TaggingView.this.sc.setMinSize(size);
/*     */       }
/*     */       
/* 281 */     });
/* 282 */     swt_updateFields();
/*     */     
/* 284 */     Rectangle r = this.sc.getClientArea();
/* 285 */     Point size = this.cMainComposite.computeSize(r.width, -1);
/* 286 */     this.sc.setMinSize(size);
/*     */   }
/*     */   
/*     */   private String getFullTitle() {
/* 290 */     return MessageText.getString("label.tags");
/*     */   }
/*     */   
/*     */   private void swt_updateFields()
/*     */   {
/* 295 */     if ((this.cMainComposite == null) || (this.cMainComposite.isDisposed())) {
/* 296 */       return;
/*     */     }
/*     */     
/* 299 */     if (this.tagButtonsUI.updateFields(this.taggables)) {
/* 300 */       this.parent.layout();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void tagTypeChanged(TagType tag_type) {}
/*     */   
/*     */ 
/*     */   public void tagEventOccurred(TagTypeListener.TagEvent event)
/*     */   {
/* 311 */     int type = event.getEventType();
/* 312 */     Tag tag = event.getTag();
/* 313 */     if (type == 0) {
/* 314 */       tagAdded(tag);
/* 315 */     } else if (type == 1) {
/* 316 */       tagChanged(tag);
/* 317 */     } else if (type == 2) {
/* 318 */       tagRemoved(tag);
/*     */     }
/*     */   }
/*     */   
/*     */   public void tagAdded(Tag tag) {
/* 323 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 325 */         TaggingView.this.initialize();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void tagChanged(Tag changedTag) {
/* 331 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 333 */         TaggingView.this.swt_updateFields();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void tagRemoved(Tag tag) {
/* 339 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 341 */         TaggingView.this.initialize();
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/TaggingView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */