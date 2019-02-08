/*     */ package org.gudy.azureus2.ui.swt.config;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.DirectoryDialog;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DirectoryParameter
/*     */   extends Parameter
/*     */ {
/*     */   Control[] controls;
/*     */   StringParameter sp;
/*     */   
/*     */   public DirectoryParameter(final Composite pluginGroup, String name, String defaultValue)
/*     */   {
/*  54 */     super(name);
/*  55 */     this.controls = new Control[2];
/*     */     
/*  57 */     this.sp = new StringParameter(pluginGroup, name, defaultValue);
/*     */     
/*  59 */     this.controls[0] = this.sp.getControl();
/*  60 */     GridData gridData = new GridData(768);
/*  61 */     this.controls[0].setLayoutData(gridData);
/*     */     
/*  63 */     Button browse = new Button(pluginGroup, 8);
/*  64 */     ImageLoader.getInstance().setButtonImage(browse, getBrowseImageResource());
/*  65 */     browse.setToolTipText(MessageText.getString("ConfigView.button.browse"));
/*     */     
/*  67 */     browse.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/*  69 */         String path = DirectoryParameter.this.openDialog(pluginGroup.getShell(), DirectoryParameter.this.sp.getValue());
/*  70 */         if (path != null) {
/*  71 */           DirectoryParameter.this.sp.setValue(path);
/*     */         }
/*     */       }
/*  74 */     });
/*  75 */     this.controls[1] = browse;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setLayoutData(Object layoutData) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public Control getControl()
/*     */   {
/*  87 */     return this.controls[0];
/*     */   }
/*     */   
/*     */ 
/*     */   public Control[] getControls()
/*     */   {
/*  93 */     return this.controls;
/*     */   }
/*     */   
/*     */   protected String getBrowseImageResource() {
/*  97 */     return "openFolderButton";
/*     */   }
/*     */   
/*     */   protected String openDialog(Shell shell, String old_value) {
/* 101 */     DirectoryDialog dialog = new DirectoryDialog(shell, 65536);
/* 102 */     dialog.setFilterPath(old_value);
/* 103 */     return dialog.open();
/*     */   }
/*     */   
/*     */   public void setValue(Object value) {
/* 107 */     if ((value instanceof String)) {
/* 108 */       this.sp.setValue((String)value);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/DirectoryParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */