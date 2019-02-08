/*     */ package org.gudy.azureus2.ui.swt.update;
/*     */ 
/*     */ import org.eclipse.swt.layout.FormAttachment;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.ProgressBar;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.update.Update;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckInstance;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderAdapter;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
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
/*     */ public class SimpleInstallUI
/*     */ {
/*     */   private UpdateMonitor monitor;
/*     */   private UpdateCheckInstance instance;
/*     */   private boolean cancelled;
/*     */   private ResourceDownloader current_downloader;
/*     */   
/*     */   protected SimpleInstallUI(UpdateMonitor _monitor, UpdateCheckInstance _instance)
/*     */   {
/*  56 */     this.monitor = _monitor;
/*  57 */     this.instance = _instance;
/*     */     try
/*     */     {
/*  60 */       this.monitor.addDecisionHandler(_instance);
/*     */       
/*  62 */       Utils.execSWTThread(new Runnable()
/*     */       {
/*     */ 
/*     */         public void run()
/*     */         {
/*     */           try
/*     */           {
/*  69 */             SimpleInstallUI.this.build();
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/*  73 */             Debug.out(e);
/*     */             
/*  75 */             SimpleInstallUI.this.instance.cancel();
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     catch (Throwable e) {
/*  81 */       Debug.out(e);
/*     */       
/*  83 */       this.instance.cancel();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void build()
/*     */   {
/*  90 */     Composite parent = (Composite)this.instance.getProperty(2);
/*     */     
/*  92 */     if (parent != null)
/*     */     {
/*  94 */       if (parent.isDisposed()) {
/*  95 */         throw new RuntimeException("cancelled");
/*     */       }
/*     */       
/*  98 */       build(parent);
/*     */     }
/*     */     else
/*     */     {
/* 102 */       throw new RuntimeException("borkeroo");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void build(Composite parent)
/*     */   {
/* 110 */     parent.setLayout(new FormLayout());
/*     */     
/* 112 */     Button cancel_button = new Button(parent, 0);
/*     */     
/* 114 */     cancel_button.setText("Cancel");
/*     */     
/* 116 */     cancel_button.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void handleEvent(Event arg0)
/*     */       {
/*     */ 
/*     */ 
/* 124 */         synchronized (SimpleInstallUI.this)
/*     */         {
/* 126 */           SimpleInstallUI.this.cancelled = true;
/*     */           
/* 128 */           if (SimpleInstallUI.this.current_downloader != null)
/*     */           {
/* 130 */             SimpleInstallUI.this.current_downloader.cancel();
/*     */           }
/*     */         }
/*     */         
/* 134 */         SimpleInstallUI.this.instance.cancel();
/*     */       }
/*     */       
/* 137 */     });
/* 138 */     FormData data = new FormData();
/* 139 */     data.right = new FormAttachment(100, 0);
/* 140 */     data.top = new FormAttachment(0, 0);
/* 141 */     data.bottom = new FormAttachment(100, 0);
/*     */     
/* 143 */     cancel_button.setLayoutData(data);
/*     */     
/* 145 */     final Label label = new Label(parent, 0);
/*     */     
/* 147 */     label.setText("blah blah ");
/*     */     
/* 149 */     data = new FormData();
/* 150 */     data.left = new FormAttachment(0, 0);
/* 151 */     data.top = new FormAttachment(cancel_button, 0, 16777216);
/*     */     
/* 153 */     label.setLayoutData(data);
/*     */     
/* 155 */     final ProgressBar progress = new ProgressBar(parent, 0);
/*     */     
/* 157 */     progress.setMinimum(0);
/* 158 */     progress.setMaximum(100);
/* 159 */     progress.setSelection(0);
/*     */     
/*     */ 
/* 162 */     data = new FormData();
/* 163 */     data.left = new FormAttachment(label, 4);
/* 164 */     data.top = new FormAttachment(cancel_button, 0, 16777216);
/* 165 */     data.right = new FormAttachment(cancel_button, -4);
/*     */     
/* 167 */     progress.setLayoutData(data);
/*     */     
/* 169 */     parent.layout(true, true);
/*     */     
/* 171 */     new AEThread2("SimpleInstallerUI", true)
/*     */     {
/*     */       public void run()
/*     */       {
/*     */         try
/*     */         {
/* 177 */           Update[] updates = SimpleInstallUI.this.instance.getUpdates();
/*     */           
/* 179 */           for (Update update : updates)
/*     */           {
/* 181 */             String name = update.getName();
/*     */             
/* 183 */             int pos = name.indexOf('/');
/*     */             
/* 185 */             if (pos >= 0)
/*     */             {
/* 187 */               name = name.substring(pos + 1);
/*     */             }
/*     */             
/* 190 */             setLabel(name);
/*     */             
/* 192 */             ResourceDownloader[] downloaders = update.getDownloaders();
/*     */             
/* 194 */             for (ResourceDownloader downloader : downloaders)
/*     */             {
/* 196 */               synchronized (SimpleInstallUI.this)
/*     */               {
/* 198 */                 if (SimpleInstallUI.this.cancelled)
/*     */                 {
/* 200 */                   return;
/*     */                 }
/*     */                 
/* 203 */                 SimpleInstallUI.this.current_downloader = downloader;
/*     */               }
/*     */               
/* 206 */               setProgress(0);
/*     */               
/* 208 */               downloader.addListener(new ResourceDownloaderAdapter()
/*     */               {
/*     */ 
/*     */ 
/*     */                 public void reportPercentComplete(ResourceDownloader downloader, int percentage)
/*     */                 {
/*     */ 
/*     */ 
/* 216 */                   SimpleInstallUI.3.this.setProgress(percentage);
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */                 public void reportAmountComplete(ResourceDownloader downloader, long amount) {}
/* 227 */               });
/* 228 */               downloader.download();
/*     */             }
/*     */           }
/*     */           
/* 232 */           boolean restart_required = false;
/*     */           
/* 234 */           for (int i = 0; i < updates.length; i++)
/*     */           {
/* 236 */             if (updates[i].getRestartRequired() == 2)
/*     */             {
/* 238 */               restart_required = true;
/*     */             }
/*     */           }
/*     */           
/* 242 */           if (restart_required)
/*     */           {
/* 244 */             SimpleInstallUI.this.monitor.handleRestart();
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {
/* 248 */           Debug.out("Install failed", e);
/*     */           
/* 250 */           SimpleInstallUI.this.instance.cancel();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       protected void setLabel(final String str)
/*     */       {
/* 258 */         Utils.execSWTThread(new Runnable()
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/*     */ 
/* 264 */             if ((SimpleInstallUI.3.this.val$label != null) && (!SimpleInstallUI.3.this.val$label.isDisposed())) {
/* 265 */               SimpleInstallUI.3.this.val$label.setText(str);
/* 266 */               SimpleInstallUI.3.this.val$label.getParent().layout();
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       protected void setProgress(final int percent)
/*     */       {
/* 276 */         Utils.execSWTThread(new Runnable()
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/*     */ 
/* 282 */             if ((SimpleInstallUI.3.this.val$progress != null) && (!SimpleInstallUI.3.this.val$progress.isDisposed())) {
/* 283 */               SimpleInstallUI.3.this.val$progress.setSelection(percent);
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }.start();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/update/SimpleInstallUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */