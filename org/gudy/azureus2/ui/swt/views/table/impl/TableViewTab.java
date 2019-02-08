/*     */ package org.gudy.azureus2.ui.swt.views.table.impl;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.table.TableViewFilterCheck.TableViewFilterCheckEx;
/*     */ import com.aelitis.azureus.util.MapUtils;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*     */ import org.gudy.azureus2.core3.util.IndentWriter;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.debug.ObfusticateImage;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
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
/*     */ public abstract class TableViewTab<DATASOURCETYPE>
/*     */   implements UISWTViewCoreEventListener, AEDiagnosticsEvidenceGenerator, ObfusticateImage
/*     */ {
/*     */   private TableViewSWT<DATASOURCETYPE> tv;
/*     */   private Object parentDataSource;
/*     */   private final String propertiesPrefix;
/*     */   private Composite composite;
/*     */   private UISWTView swtView;
/*     */   private Text filterTextControl;
/*     */   private TableViewFilterCheck.TableViewFilterCheckEx<DATASOURCETYPE> filterCheck;
/*     */   
/*     */   public TableViewTab(String propertiesPrefix)
/*     */   {
/*  61 */     this.propertiesPrefix = propertiesPrefix;
/*     */   }
/*     */   
/*     */   public TableViewSWT<DATASOURCETYPE> getTableView() {
/*  65 */     return this.tv;
/*     */   }
/*     */   
/*     */   public final void initialize(Composite composite) {
/*  69 */     this.tv = initYourTableView();
/*  70 */     if (this.parentDataSource != null) {
/*  71 */       this.tv.setParentDataSource(this.parentDataSource);
/*     */     }
/*  73 */     Composite parent = initComposite(composite);
/*  74 */     this.tv.initialize(this.swtView, parent);
/*  75 */     if (parent != composite) {
/*  76 */       this.composite = composite;
/*     */     } else {
/*  78 */       this.composite = this.tv.getComposite();
/*     */     }
/*     */     
/*  81 */     if (this.filterCheck != null) {
/*  82 */       this.tv.enableFilterCheck(this.filterTextControl, this.filterCheck);
/*     */     }
/*     */     
/*  85 */     tableViewTabInitComplete();
/*     */   }
/*     */   
/*     */   public void tableViewTabInitComplete() {}
/*     */   
/*     */   public Composite initComposite(Composite composite)
/*     */   {
/*  92 */     return composite;
/*     */   }
/*     */   
/*     */   public abstract TableViewSWT<DATASOURCETYPE> initYourTableView();
/*     */   
/*     */   public final void dataSourceChanged(Object newDataSource) {
/*  98 */     this.parentDataSource = newDataSource;
/*  99 */     if (this.tv != null) {
/* 100 */       this.tv.setParentDataSource(newDataSource);
/*     */     }
/*     */   }
/*     */   
/*     */   public final void refresh() {
/* 105 */     if (this.tv != null) {
/* 106 */       this.tv.refreshTable(false);
/*     */     }
/*     */   }
/*     */   
/*     */   public final void delete() {
/* 111 */     if (this.tv != null) {
/* 112 */       this.tv.delete();
/*     */     }
/* 114 */     this.tv = null;
/*     */   }
/*     */   
/*     */   public String getFullTitle() {
/* 118 */     return MessageText.getString(getPropertiesPrefix() + ".title.full");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void generate(IndentWriter writer)
/*     */   {
/* 125 */     if (this.tv != null) {
/* 126 */       this.tv.generate(writer);
/*     */     }
/*     */   }
/*     */   
/*     */   public Composite getComposite() {
/* 131 */     return this.composite;
/*     */   }
/*     */   
/*     */   public String getPropertiesPrefix() {
/* 135 */     return this.propertiesPrefix;
/*     */   }
/*     */   
/*     */   public Menu getPrivateMenu() {
/* 139 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public void viewActivated()
/*     */   {
/* 145 */     if ((this.tv instanceof TableViewSWT)) {
/* 146 */       this.tv.isVisible();
/*     */     }
/*     */   }
/*     */   
/*     */   private void viewDeactivated() {
/* 151 */     if ((this.tv instanceof TableViewSWT)) {
/* 152 */       this.tv.isVisible();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean eventOccurred(UISWTViewEvent event) {
/* 157 */     switch (event.getType()) {
/*     */     case 0: 
/* 159 */       this.swtView = ((UISWTView)event.getData());
/* 160 */       this.swtView.setTitle(getFullTitle());
/* 161 */       break;
/*     */     
/*     */     case 7: 
/* 164 */       delete();
/* 165 */       break;
/*     */     
/*     */     case 2: 
/* 168 */       initialize((Composite)event.getData());
/* 169 */       break;
/*     */     
/*     */     case 6: 
/* 172 */       this.swtView.setTitle(getFullTitle());
/* 173 */       updateLanguage();
/* 174 */       Messages.updateLanguageForControl(this.composite);
/* 175 */       break;
/*     */     
/*     */     case 1: 
/* 178 */       dataSourceChanged(event.getData());
/* 179 */       break;
/*     */     
/*     */     case 3: 
/* 182 */       viewActivated();
/* 183 */       break;
/*     */     
/*     */     case 4: 
/* 186 */       viewDeactivated();
/* 187 */       break;
/*     */     
/*     */     case 5: 
/* 190 */       refresh();
/* 191 */       break;
/*     */     
/*     */     case 9: 
/* 194 */       Object data = event.getData();
/* 195 */       if ((data instanceof Map)) {
/* 196 */         obfusticatedImage((Image)MapUtils.getMapObject((Map)data, "image", null, Image.class));
/*     */       }
/*     */       
/*     */       break;
/*     */     }
/*     */     
/* 202 */     return true;
/*     */   }
/*     */   
/*     */   public void updateLanguage() {}
/*     */   
/*     */   public UISWTView getSWTView()
/*     */   {
/* 209 */     return this.swtView;
/*     */   }
/*     */   
/*     */   public void enableFilterCheck(Text textControl, TableViewFilterCheck.TableViewFilterCheckEx<DATASOURCETYPE> filter_check_handler)
/*     */   {
/* 214 */     if (this.tv != null) {
/* 215 */       this.tv.enableFilterCheck(textControl, filter_check_handler);
/*     */     } else {
/* 217 */       this.filterTextControl = textControl;
/* 218 */       this.filterCheck = filter_check_handler;
/*     */     }
/*     */   }
/*     */   
/*     */   public Image obfusticatedImage(Image image)
/*     */   {
/* 224 */     if (this.tv != null) {
/* 225 */       return this.tv.obfusticatedImage(image);
/*     */     }
/* 227 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/table/impl/TableViewTab.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */