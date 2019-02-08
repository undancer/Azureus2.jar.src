/*     */ package org.gudy.azureus2.ui.swt.views;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Layout;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.views.table.utils.TableColumnCreator;
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
/*     */ public class MyTorrentsSubView
/*     */   extends MyTorrentsView
/*     */ {
/*     */   private Button btnAnyTags;
/*     */   private boolean anyTorrentTags;
/*     */   
/*     */   public MyTorrentsSubView()
/*     */   {
/*  56 */     super("MyTorrentsSubView", false);
/*  57 */     this.neverShowCatOrTagButtons = true;
/*  58 */     this.isEmptyListOnNullDS = true;
/*  59 */     AzureusCore _azureus_core = AzureusCoreFactory.getSingleton();
/*  60 */     init(_azureus_core, "MyTorrentsSubView", Download.class, TableColumnCreator.createCompleteDM("MyTorrentsSubView"));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Composite initComposite(Composite composite)
/*     */   {
/*  69 */     Composite parent = new Composite(composite, 0);
/*  70 */     GridLayout layout = new GridLayout();
/*  71 */     layout.marginHeight = (layout.marginWidth = 0);
/*  72 */     layout.horizontalSpacing = (layout.verticalSpacing = 0);
/*  73 */     parent.setLayout(layout);
/*     */     
/*  75 */     Layout compositeLayout = composite.getLayout();
/*  76 */     if ((compositeLayout instanceof GridLayout)) {
/*  77 */       parent.setLayoutData(new GridData(4, 4, true, true));
/*  78 */     } else if ((compositeLayout instanceof FormLayout)) {
/*  79 */       parent.setLayoutData(Utils.getFilledFormData());
/*     */     }
/*     */     
/*  82 */     Composite cTop = new Composite(parent, 0);
/*     */     
/*  84 */     GridData gd = new GridData(4, 1, true, false);
/*  85 */     cTop.setLayoutData(gd);
/*  86 */     cTop.setLayout(new FormLayout());
/*     */     
/*  88 */     this.btnAnyTags = new Button(cTop, 32);
/*  89 */     Messages.setLanguageText(this.btnAnyTags, "TorrentTags.Button.Any");
/*  90 */     this.btnAnyTags.addSelectionListener(new SelectionListener() {
/*     */       public void widgetSelected(SelectionEvent e) {
/*  92 */         COConfigurationManager.setParameter("TorrentTags.Any", !MyTorrentsSubView.this.anyTorrentTags);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void widgetDefaultSelected(SelectionEvent e) {}
/*  98 */     });
/*  99 */     this.anyTorrentTags = COConfigurationManager.getBooleanParameter("TorrentTags.Any");
/*     */     
/* 101 */     this.btnAnyTags.setSelection(this.anyTorrentTags);
/* 102 */     setCurrentTagsAny(this.anyTorrentTags);
/* 103 */     updateButtonVisibility(getCurrentTags());
/* 104 */     Composite tableParent = new Composite(parent, 0);
/*     */     
/* 106 */     tableParent.setLayoutData(new GridData(4, 4, true, true));
/* 107 */     GridLayout gridLayout = new GridLayout();
/* 108 */     gridLayout.horizontalSpacing = (gridLayout.verticalSpacing = 0);
/* 109 */     gridLayout.marginHeight = (gridLayout.marginWidth = 0);
/* 110 */     tableParent.setLayout(gridLayout);
/*     */     
/* 112 */     parent.setTabList(new Control[] { tableParent, cTop });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 117 */     return tableParent;
/*     */   }
/*     */   
/*     */   public void tableViewInitialized()
/*     */   {
/* 122 */     this.anyTorrentTags = COConfigurationManager.getBooleanParameter("TorrentTags.Any");
/* 123 */     COConfigurationManager.addParameterListener("TorrentTags.Any", this);
/* 124 */     super.tableViewInitialized();
/*     */   }
/*     */   
/*     */   public void tableViewDestroyed() {
/* 128 */     COConfigurationManager.removeParameterListener("TorrentTags.Any", this);
/* 129 */     super.tableViewDestroyed();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void parameterChanged(String parameterName)
/*     */   {
/* 137 */     if ("TorrentTags.Any".equals(parameterName)) {
/* 138 */       this.anyTorrentTags = COConfigurationManager.getBooleanParameter(parameterName);
/* 139 */       if ((this.btnAnyTags != null) && (!this.btnAnyTags.isDisposed())) {
/* 140 */         Utils.execSWTThread(new AERunnable() {
/*     */           public void runSupport() {
/* 142 */             if ((MyTorrentsSubView.this.btnAnyTags != null) && (!MyTorrentsSubView.this.btnAnyTags.isDisposed())) {
/* 143 */               MyTorrentsSubView.this.btnAnyTags.setSelection(MyTorrentsSubView.this.anyTorrentTags);
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/* 148 */       setCurrentTagsAny(this.anyTorrentTags);
/*     */     }
/* 150 */     super.parameterChanged(parameterName);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setCurrentTags(Tag[] tags)
/*     */   {
/* 158 */     super.setCurrentTags(tags);
/* 159 */     updateButtonVisibility(tags);
/*     */   }
/*     */   
/*     */   private void updateButtonVisibility(final Tag[] tags) {
/* 163 */     Utils.execSWTThread(new AERunnable()
/*     */     {
/*     */       public void runSupport()
/*     */       {
/* 167 */         if ((MyTorrentsSubView.this.btnAnyTags == null) || (MyTorrentsSubView.this.btnAnyTags.isDisposed())) {
/* 168 */           return;
/*     */         }
/* 170 */         boolean show = (tags != null) && (tags.length > 1);
/* 171 */         MyTorrentsSubView.this.btnAnyTags.setVisible(show);
/* 172 */         FormData fd = Utils.getFilledFormData();
/* 173 */         fd.height = (show ? -1 : 0);
/* 174 */         MyTorrentsSubView.this.btnAnyTags.setLayoutData(fd);
/* 175 */         Composite cTop = MyTorrentsSubView.this.btnAnyTags.getParent();
/* 176 */         cTop.getParent().layout(true, true);
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/MyTorrentsSubView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */