/*     */ package org.gudy.azureus2.ui.swt.update;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.eclipse.swt.custom.StyledText;
/*     */ import org.eclipse.swt.layout.FormAttachment;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.plugins.update.UpdatableComponent;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckInstance;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckInstanceListener;
/*     */ import org.gudy.azureus2.plugins.update.UpdateChecker;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckerListener;
/*     */ import org.gudy.azureus2.plugins.update.UpdateManager;
/*     */ import org.gudy.azureus2.plugins.update.UpdateManagerListener;
/*     */ import org.gudy.azureus2.plugins.update.UpdateProgressListener;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
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
/*     */ /**
/*     */  * @deprecated
/*     */  */
/*     */ public class UpdateProgressWindow
/*     */   implements UpdateManagerListener
/*     */ {
/*     */   protected Display display;
/*     */   protected Shell window;
/*     */   protected StyledText text_area;
/*     */   protected UpdateManager manager;
/*     */   
/*     */   public static void show(UpdateCheckInstance[] instances, Shell shell)
/*     */   {
/*  51 */     if (instances.length == 0)
/*     */     {
/*  53 */       return;
/*     */     }
/*     */     
/*  56 */     new UpdateProgressWindow().showSupport(instances, shell);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  65 */   protected ArrayList current_instances = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void showSupport(UpdateCheckInstance[] instances, Shell shell)
/*     */   {
/*  72 */     this.manager = instances[0].getManager();
/*     */     
/*  74 */     this.display = shell.getDisplay();
/*     */     
/*  76 */     this.window = ShellFactory.createShell(this.display, 67696);
/*  77 */     Messages.setLanguageText(this.window, "updater.progress.window.title");
/*  78 */     Utils.setShellIcon(shell);
/*  79 */     FormLayout layout = new FormLayout();
/*     */     try {
/*  81 */       layout.spacing = 5;
/*     */     }
/*     */     catch (NoSuchFieldError e) {}
/*     */     
/*  85 */     layout.marginHeight = 5;
/*  86 */     layout.marginWidth = 5;
/*  87 */     this.window.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  92 */     this.text_area = new StyledText(this.window, 2816);
/*     */     
/*  94 */     this.text_area.setEditable(false);
/*     */     
/*  96 */     Button btnOk = new Button(this.window, 8);
/*  97 */     Button btnAbort = new Button(this.window, 8);
/*     */     
/*     */ 
/* 100 */     FormData formData = new FormData();
/* 101 */     formData.left = new FormAttachment(0, 0);
/* 102 */     formData.right = new FormAttachment(100, 0);
/* 103 */     formData.top = new FormAttachment(0, 0);
/* 104 */     formData.bottom = new FormAttachment(90, 0);
/* 105 */     Utils.setLayoutData(this.text_area, formData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 110 */     Label info_label = new Label(this.window, 0);
/* 111 */     Messages.setLanguageText(info_label, "updater.progress.window.info");
/* 112 */     formData = new FormData();
/* 113 */     formData.top = new FormAttachment(this.text_area);
/* 114 */     formData.right = new FormAttachment(btnAbort);
/* 115 */     formData.left = new FormAttachment(0, 0);
/* 116 */     Utils.setLayoutData(info_label, formData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 121 */     Messages.setLanguageText(btnAbort, "Button.abort");
/* 122 */     formData = new FormData();
/* 123 */     formData.right = new FormAttachment(btnOk);
/* 124 */     formData.bottom = new FormAttachment(100, 0);
/* 125 */     formData.width = 70;
/* 126 */     Utils.setLayoutData(btnAbort, formData);
/* 127 */     btnAbort.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void handleEvent(Event e)
/*     */       {
/*     */ 
/*     */ 
/* 135 */         UpdateProgressWindow.this.manager.removeListener(UpdateProgressWindow.this);
/*     */         
/* 137 */         for (int i = 0; i < UpdateProgressWindow.this.current_instances.size(); i++)
/*     */         {
/* 139 */           ((UpdateCheckInstance)UpdateProgressWindow.this.current_instances.get(i)).cancel();
/*     */         }
/*     */         
/* 142 */         UpdateProgressWindow.this.window.dispose();
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 147 */     });
/* 148 */     Messages.setLanguageText(btnOk, "Button.ok");
/* 149 */     formData = new FormData();
/* 150 */     formData.right = new FormAttachment(95, 0);
/* 151 */     formData.bottom = new FormAttachment(100, 0);
/* 152 */     formData.width = 70;
/* 153 */     Utils.setLayoutData(btnOk, formData);
/* 154 */     btnOk.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void handleEvent(Event e)
/*     */       {
/*     */ 
/*     */ 
/* 162 */         UpdateProgressWindow.this.manager.removeListener(UpdateProgressWindow.this);
/*     */         
/* 164 */         UpdateProgressWindow.this.window.dispose();
/*     */       }
/*     */       
/* 167 */     });
/* 168 */     this.window.setDefaultButton(btnOk);
/*     */     
/* 170 */     this.window.addListener(31, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 172 */         if (e.character == '\033')
/*     */         {
/* 174 */           UpdateProgressWindow.this.manager.removeListener(UpdateProgressWindow.this);
/*     */           
/* 176 */           UpdateProgressWindow.this.window.dispose();
/*     */         }
/*     */         
/*     */       }
/* 180 */     });
/* 181 */     this.manager.addListener(this);
/*     */     
/* 183 */     this.window.setSize(620, 450);
/* 184 */     this.window.layout();
/*     */     
/* 186 */     Utils.centreWindow(this.window);
/*     */     
/* 188 */     this.window.open();
/*     */     
/* 190 */     for (int i = 0; i < instances.length; i++)
/*     */     {
/* 192 */       addInstance(instances[i]);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void log(UpdateCheckInstance instance, String str)
/*     */   {
/* 201 */     String name = instance.getName();
/*     */     
/* 203 */     if (MessageText.keyExists(name))
/*     */     {
/* 205 */       name = MessageText.getString(name);
/*     */     }
/*     */     
/* 208 */     log(name + " - " + str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void log(UpdateChecker checker, String str)
/*     */   {
/* 216 */     log("    " + checker.getComponent().getName() + " - " + str);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void log(final String str)
/*     */   {
/*     */     try
/*     */     {
/* 224 */       if (!this.display.isDisposed())
/*     */       {
/* 226 */         this.display.asyncExec(new AERunnable()
/*     */         {
/*     */ 
/*     */           public void runSupport()
/*     */           {
/*     */ 
/* 232 */             if (!UpdateProgressWindow.this.text_area.isDisposed())
/*     */             {
/* 234 */               UpdateProgressWindow.this.text_area.append(str + "\n");
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void checkInstanceCreated(UpdateCheckInstance instance)
/*     */   {
/* 247 */     addInstance(instance);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void addInstance(final UpdateCheckInstance instance)
/*     */   {
/* 254 */     if (!this.display.isDisposed())
/*     */     {
/* 256 */       this.display.asyncExec(new AERunnable()
/*     */       {
/*     */ 
/*     */         public void runSupport()
/*     */         {
/*     */ 
/* 262 */           if ((UpdateProgressWindow.this.display.isDisposed()) || (UpdateProgressWindow.this.window.isDisposed()))
/*     */           {
/* 264 */             return;
/*     */           }
/*     */           
/* 267 */           if (!UpdateProgressWindow.this.current_instances.contains(instance))
/*     */           {
/* 269 */             UpdateProgressWindow.this.current_instances.add(instance);
/*     */             
/* 271 */             UpdateProgressWindow.this.log(instance, "added");
/*     */             
/* 273 */             instance.addListener(new UpdateCheckInstanceListener()
/*     */             {
/*     */ 
/*     */ 
/*     */               public void cancelled(UpdateCheckInstance instance)
/*     */               {
/*     */ 
/* 280 */                 UpdateProgressWindow.this.log(instance, "cancelled");
/*     */               }
/*     */               
/*     */ 
/*     */ 
/*     */               public void complete(UpdateCheckInstance instance)
/*     */               {
/* 287 */                 UpdateProgressWindow.this.log(instance, "complete");
/*     */               }
/*     */               
/* 290 */             });
/* 291 */             UpdateChecker[] checkers = instance.getCheckers();
/*     */             
/* 293 */             for (int i = 0; i < checkers.length; i++)
/*     */             {
/* 295 */               final UpdateChecker checker = checkers[i];
/*     */               
/* 297 */               UpdateProgressWindow.this.log(checker, "added");
/*     */               
/* 299 */               checker.addListener(new UpdateCheckerListener()
/*     */               {
/*     */ 
/*     */ 
/*     */                 public void completed(UpdateChecker checker)
/*     */                 {
/*     */ 
/* 306 */                   UpdateProgressWindow.this.log(checker, "completed");
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/*     */                 public void failed(UpdateChecker checker)
/*     */                 {
/* 313 */                   UpdateProgressWindow.this.log(checker, "failed");
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/*     */                 public void cancelled(UpdateChecker checker)
/*     */                 {
/* 321 */                   UpdateProgressWindow.this.log(checker, "cancelled");
/*     */                 }
/*     */                 
/* 324 */               });
/* 325 */               checker.addProgressListener(new UpdateProgressListener()
/*     */               {
/*     */ 
/*     */ 
/*     */                 public void reportProgress(String str)
/*     */                 {
/*     */ 
/* 332 */                   UpdateProgressWindow.this.log(checker, "    " + str);
/*     */                 }
/*     */               });
/*     */             }
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/update/UpdateProgressWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */