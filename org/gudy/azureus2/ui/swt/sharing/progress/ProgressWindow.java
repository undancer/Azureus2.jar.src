/*     */ package org.gudy.azureus2.ui.swt.sharing.progress;
/*     */ 
/*     */ import org.eclipse.swt.custom.StyledText;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FormAttachment;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Monitor;
/*     */ import org.eclipse.swt.widgets.ProgressBar;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareException;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareManager;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareManagerListener;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResource;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.shells.PopupShell;
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
/*     */ public class ProgressWindow
/*     */   implements ShareManagerListener
/*     */ {
/*     */   private static boolean window_disabled;
/*     */   private ShareManager share_manager;
/*     */   
/*     */   static
/*     */   {
/*  55 */     COConfigurationManager.addAndFireParameterListener("Suppress Sharing Dialog", new ParameterListener()
/*     */     {
/*     */ 
/*     */       public void parameterChanged(String parameterName)
/*     */       {
/*     */ 
/*  61 */         ProgressWindow.access$002(COConfigurationManager.getBooleanParameter("Suppress Sharing Dialog"));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*  67 */   private progressDialog dialog = null;
/*     */   
/*     */   private Display display;
/*     */   
/*     */   private StyledText tasks;
/*     */   
/*     */   private ProgressBar progress;
/*     */   
/*     */   private Button cancel_button;
/*     */   
/*     */   private boolean shell_opened;
/*     */   private boolean manually_hidden;
/*     */   
/*     */   public ProgressWindow(Display _display)
/*     */   {
/*     */     try
/*     */     {
/*  84 */       this.share_manager = PluginInitializer.getDefaultInterface().getShareManager();
/*     */       
/*  86 */       this.display = _display;
/*     */       
/*  88 */       this.share_manager.addListener(this);
/*     */     }
/*     */     catch (ShareException e)
/*     */     {
/*  92 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private class progressDialog
/*     */     extends PopupShell
/*     */   {
/*     */     int x0;
/*     */     int y1;
/*     */     
/*     */     protected progressDialog(Display dialog_display)
/*     */     {
/* 105 */       super();
/*     */       
/* 107 */       if (dialog_display.isDisposed())
/*     */       {
/* 109 */         return;
/*     */       }
/*     */       
/* 112 */       this.shell.setText(MessageText.getString("sharing.progress.title"));
/*     */       
/*     */ 
/* 115 */       ProgressWindow.this.tasks = new StyledText(this.shell, 2824);
/* 116 */       ProgressWindow.this.tasks.setBackground(dialog_display.getSystemColor(1));
/*     */       
/* 118 */       ProgressWindow.this.progress = new ProgressBar(this.shell, 0);
/* 119 */       ProgressWindow.this.progress.setMinimum(0);
/* 120 */       ProgressWindow.this.progress.setMaximum(100);
/*     */       
/*     */ 
/* 123 */       Button hide_button = new Button(this.shell, 8);
/* 124 */       hide_button.setText(MessageText.getString("sharing.progress.hide"));
/*     */       
/* 126 */       ProgressWindow.this.cancel_button = new Button(this.shell, 8);
/* 127 */       ProgressWindow.this.cancel_button.setText(MessageText.getString("sharing.progress.cancel"));
/* 128 */       ProgressWindow.this.cancel_button.setEnabled(false);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 135 */       FormData formData = new FormData();
/* 136 */       formData.right = new FormAttachment(100, -5);
/* 137 */       formData.bottom = new FormAttachment(100, -10);
/*     */       
/* 139 */       hide_button.setLayoutData(formData);
/*     */       
/* 141 */       formData = new FormData();
/* 142 */       formData.right = new FormAttachment(hide_button, -5);
/* 143 */       formData.bottom = new FormAttachment(100, -10);
/*     */       
/* 145 */       ProgressWindow.this.cancel_button.setLayoutData(formData);
/*     */       
/* 147 */       formData = new FormData();
/* 148 */       formData.right = new FormAttachment(ProgressWindow.this.cancel_button, -5);
/* 149 */       formData.left = new FormAttachment(0, 50);
/* 150 */       formData.bottom = new FormAttachment(100, -10);
/*     */       
/* 152 */       ProgressWindow.this.progress.setLayoutData(formData);
/*     */       
/* 154 */       formData = new FormData();
/* 155 */       formData.right = new FormAttachment(100, -5);
/* 156 */       formData.bottom = new FormAttachment(100, -50);
/* 157 */       formData.top = new FormAttachment(0, 5);
/* 158 */       formData.left = new FormAttachment(0, 5);
/*     */       
/* 160 */       ProgressWindow.this.tasks.setLayoutData(formData);
/*     */       
/*     */ 
/* 163 */       layout();
/*     */       
/* 165 */       ProgressWindow.this.cancel_button.addListener(13, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 167 */           ProgressWindow.this.cancel_button.setEnabled(false);
/*     */           
/* 169 */           ProgressWindow.this.share_manager.cancelOperation();
/*     */         }
/*     */         
/* 172 */       });
/* 173 */       hide_button.addListener(13, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 175 */           ProgressWindow.progressDialog.this.hidePanel();
/*     */         }
/*     */         
/*     */ 
/* 179 */       });
/* 180 */       this.shell.setDefaultButton(hide_button);
/*     */       
/* 182 */       this.shell.addListener(31, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 184 */           if (e.character == '\033') {
/* 185 */             ProgressWindow.progressDialog.this.hidePanel();
/*     */           }
/*     */           
/*     */         }
/*     */         
/* 190 */       });
/* 191 */       Rectangle bounds = this.shell.getMonitor().getClientArea();
/* 192 */       this.x0 = (bounds.x + bounds.width - 255);
/*     */       
/* 194 */       this.y1 = (bounds.y + bounds.height - 155);
/*     */       
/* 196 */       this.shell.setLocation(this.x0, this.y1);
/*     */     }
/*     */     
/*     */ 
/*     */     protected void hidePanel()
/*     */     {
/* 202 */       ProgressWindow.this.manually_hidden = true;
/* 203 */       this.shell.setVisible(false);
/*     */     }
/*     */     
/*     */ 
/*     */     protected void showPanel()
/*     */     {
/* 209 */       ProgressWindow.this.manually_hidden = false;
/*     */       
/* 211 */       if (!ProgressWindow.this.shell_opened)
/*     */       {
/* 213 */         ProgressWindow.this.shell_opened = true;
/*     */         
/* 215 */         this.shell.open();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 221 */       if (!this.shell.isVisible()) {
/* 222 */         this.shell.setVisible(true);
/*     */       }
/*     */       
/* 225 */       this.shell.moveAbove(null);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected boolean isShown()
/*     */     {
/* 232 */       return this.shell.isVisible();
/*     */     }
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
/*     */   public void resourceAdded(ShareResource resource)
/*     */   {
/* 248 */     if (!this.share_manager.isInitialising())
/*     */     {
/* 250 */       reportCurrentTask("Resource added: " + resource.getName());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void resourceModified(ShareResource old_resource, ShareResource new_resource)
/*     */   {
/* 259 */     reportCurrentTask("Resource modified: " + old_resource.getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void resourceDeleted(ShareResource resource)
/*     */   {
/* 266 */     reportCurrentTask("Resource deleted: " + resource.getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void reportProgress(final int percent_complete)
/*     */   {
/* 273 */     if (window_disabled)
/*     */     {
/* 275 */       return;
/*     */     }
/*     */     
/* 278 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 280 */         if ((ProgressWindow.this.progress != null) && (!ProgressWindow.this.progress.isDisposed()))
/*     */         {
/* 282 */           if (ProgressWindow.this.dialog == null) {
/* 283 */             ProgressWindow.this.dialog = new ProgressWindow.progressDialog(ProgressWindow.this, ProgressWindow.this.display);
/* 284 */             if (ProgressWindow.this.dialog == null) {
/* 285 */               return;
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 292 */           if ((!ProgressWindow.this.dialog.isShown()) && (!ProgressWindow.this.manually_hidden))
/*     */           {
/* 294 */             ProgressWindow.this.dialog.showPanel();
/*     */           }
/*     */           
/* 297 */           ProgressWindow.this.cancel_button.setEnabled(percent_complete < 100);
/*     */           
/* 299 */           ProgressWindow.this.progress.setSelection(percent_complete);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reportCurrentTask(final String task_description)
/*     */   {
/* 310 */     if (window_disabled)
/*     */     {
/* 312 */       return;
/*     */     }
/*     */     
/* 315 */     Utils.execSWTThread(new AERunnable()
/*     */     {
/*     */       public void runSupport() {
/* 318 */         if (ProgressWindow.this.dialog == null) {
/* 319 */           ProgressWindow.this.dialog = new ProgressWindow.progressDialog(ProgressWindow.this, ProgressWindow.this.display);
/* 320 */           if (ProgressWindow.this.dialog == null) {
/* 321 */             return;
/*     */           }
/*     */         }
/*     */         
/* 325 */         if ((ProgressWindow.this.tasks != null) && (!ProgressWindow.this.tasks.isDisposed())) {
/* 326 */           ProgressWindow.this.dialog.showPanel();
/*     */           
/* 328 */           ProgressWindow.this.tasks.append(task_description + Text.DELIMITER);
/*     */           
/* 330 */           int lines = ProgressWindow.this.tasks.getLineCount();
/*     */           
/*     */ 
/*     */ 
/* 334 */           ProgressWindow.this.tasks.setTopIndex(lines - 1);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/sharing/progress/ProgressWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */