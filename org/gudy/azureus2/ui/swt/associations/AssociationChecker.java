/*     */ package org.gudy.azureus2.ui.swt.associations;
/*     */ 
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.platform.PlatformManager;
/*     */ import org.gudy.azureus2.platform.PlatformManagerCapabilities;
/*     */ import org.gudy.azureus2.platform.PlatformManagerFactory;
/*     */ import org.gudy.azureus2.plugins.platform.PlatformManagerException;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.SWTThread;
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
/*     */ public class AssociationChecker
/*     */ {
/*     */   protected PlatformManager platform;
/*     */   protected Display display;
/*     */   protected Shell shell;
/*     */   
/*     */   public static void checkAssociations()
/*     */   {
/*     */     try
/*     */     {
/*  54 */       PlatformManager platform = PlatformManagerFactory.getPlatformManager();
/*     */       
/*  56 */       if (platform.hasCapability(PlatformManagerCapabilities.RegisterFileAssociations))
/*     */       {
/*  58 */         if (COConfigurationManager.getBooleanParameter("config.interface.checkassoc"))
/*     */         {
/*  60 */           if (!platform.isApplicationRegistered())
/*     */           {
/*  62 */             new AssociationChecker(platform);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */   }
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
/*     */   protected AssociationChecker(PlatformManager _platform)
/*     */   {
/*  81 */     this.platform = _platform;
/*     */     
/*  83 */     this.display = SWTThread.getInstance().getDisplay();
/*     */     
/*  85 */     if (this.display.isDisposed())
/*     */     {
/*  87 */       return;
/*     */     }
/*     */     
/*  90 */     Utils.execSWTThread(new AERunnable()
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/*  96 */         AssociationChecker.this.check();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   protected void check()
/*     */   {
/* 104 */     if (this.display.isDisposed()) {
/* 105 */       return;
/*     */     }
/* 107 */     this.shell = ShellFactory.createMainShell(2144);
/*     */     
/* 109 */     Utils.setShellIcon(this.shell);
/* 110 */     this.shell.setText(MessageText.getString("dialog.associations.title"));
/*     */     
/* 112 */     GridLayout layout = new GridLayout();
/* 113 */     layout.numColumns = 3;
/*     */     
/* 115 */     this.shell.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 121 */     Label user_label = new Label(this.shell, 0);
/* 122 */     Messages.setLanguageText(user_label, "dialog.associations.prompt");
/* 123 */     GridData gridData = new GridData(1808);
/* 124 */     gridData.horizontalSpan = 3;
/* 125 */     Utils.setLayoutData(user_label, gridData);
/*     */     
/*     */ 
/* 128 */     final Button checkBox = new Button(this.shell, 32);
/* 129 */     checkBox.setSelection(true);
/* 130 */     gridData = new GridData(1808);
/* 131 */     gridData.horizontalSpan = 3;
/* 132 */     Utils.setLayoutData(checkBox, gridData);
/* 133 */     Messages.setLanguageText(checkBox, "dialog.associations.askagain");
/*     */     
/*     */ 
/*     */ 
/* 137 */     Label labelSeparator = new Label(this.shell, 258);
/* 138 */     gridData = new GridData(768);
/* 139 */     gridData.horizontalSpan = 3;
/* 140 */     Utils.setLayoutData(labelSeparator, gridData);
/*     */     
/*     */ 
/*     */ 
/* 144 */     new Label(this.shell, 0);
/*     */     
/* 146 */     Button bYes = new Button(this.shell, 8);
/* 147 */     bYes.setText(MessageText.getString("Button.yes"));
/* 148 */     gridData = new GridData(896);
/* 149 */     gridData.grabExcessHorizontalSpace = true;
/* 150 */     gridData.widthHint = 70;
/* 151 */     Utils.setLayoutData(bYes, gridData);
/* 152 */     bYes.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 154 */         AssociationChecker.this.close(true, checkBox.getSelection());
/*     */       }
/*     */       
/* 157 */     });
/* 158 */     Button bNo = new Button(this.shell, 8);
/* 159 */     bNo.setText(MessageText.getString("Button.no"));
/* 160 */     gridData = new GridData(128);
/* 161 */     gridData.grabExcessHorizontalSpace = false;
/* 162 */     gridData.widthHint = 70;
/* 163 */     Utils.setLayoutData(bNo, gridData);
/* 164 */     bNo.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 166 */         AssociationChecker.this.close(false, checkBox.getSelection());
/*     */       }
/*     */       
/* 169 */     });
/* 170 */     bYes.setFocus();
/* 171 */     this.shell.setDefaultButton(bYes);
/*     */     
/* 173 */     this.shell.addListener(31, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 175 */         if (e.character == '\033') {
/* 176 */           AssociationChecker.this.close(false, true);
/*     */         }
/*     */         
/*     */       }
/*     */       
/* 181 */     });
/* 182 */     this.shell.pack();
/*     */     
/* 184 */     Utils.centreWindow(this.shell);
/*     */     
/* 186 */     this.shell.open();
/*     */   }
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
/*     */   protected void close(boolean ok, boolean check_on_startup)
/*     */   {
/* 204 */     if (check_on_startup != COConfigurationManager.getBooleanParameter("config.interface.checkassoc"))
/*     */     {
/* 206 */       COConfigurationManager.setParameter("config.interface.checkassoc", check_on_startup);
/*     */       
/* 208 */       COConfigurationManager.save();
/*     */     }
/*     */     
/* 211 */     if (ok) {
/*     */       try
/*     */       {
/* 214 */         this.platform.registerApplication();
/*     */       }
/*     */       catch (PlatformManagerException e)
/*     */       {
/* 218 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     
/* 222 */     this.shell.dispose();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/associations/AssociationChecker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */