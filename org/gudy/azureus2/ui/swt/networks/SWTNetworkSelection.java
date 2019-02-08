/*     */ package org.gudy.azureus2.ui.swt.networks;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifierListener;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SWTNetworkSelection
/*     */   implements AENetworkClassifierListener
/*     */ {
/*     */   public SWTNetworkSelection()
/*     */   {
/*  58 */     AENetworkClassifier.addListener(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String[] selectNetworks(final String description, final String[] tracker_networks)
/*     */   {
/*  66 */     final Display display = SWTThread.getInstance().getDisplay();
/*     */     
/*  68 */     if (display.isDisposed())
/*     */     {
/*  70 */       return null;
/*     */     }
/*     */     
/*  73 */     final AESemaphore sem = new AESemaphore("NetworkClassifier");
/*     */     
/*  75 */     final classifierDialog[] dialog = new classifierDialog[1];
/*     */     try
/*     */     {
/*  78 */       Utils.execSWTThread(new AERunnable()
/*     */       {
/*     */ 
/*     */         public void runSupport()
/*     */         {
/*     */ 
/*  84 */           dialog[0] = new SWTNetworkSelection.classifierDialog(sem, display, description, tracker_networks);
/*     */         }
/*     */       });
/*     */     }
/*     */     catch (Throwable e) {
/*  89 */       Debug.printStackTrace(e);
/*     */       
/*  91 */       return null;
/*     */     }
/*     */     
/*  94 */     sem.reserve();
/*     */     
/*  96 */     return dialog[0].getSelection();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static class classifierDialog
/*     */   {
/*     */     protected Shell shell;
/*     */     
/*     */ 
/*     */     protected AESemaphore sem;
/*     */     
/*     */ 
/*     */     protected String[] selection;
/*     */     
/*     */ 
/*     */     private Button[] checkboxes;
/*     */     
/*     */ 
/*     */     protected classifierDialog(AESemaphore _sem, Display display, String description, String[] tracker_networks)
/*     */     {
/* 117 */       this.sem = _sem;
/*     */       
/* 119 */       if (display.isDisposed())
/*     */       {
/* 121 */         this.sem.releaseForever();
/*     */         
/* 123 */         return;
/*     */       }
/*     */       
/* 126 */       this.shell = ShellFactory.createMainShell(67680);
/*     */       
/* 128 */       Utils.setShellIcon(this.shell);
/*     */       
/* 130 */       this.shell.setText(MessageText.getString("window.networkselection.title"));
/*     */       
/* 132 */       GridLayout layout = new GridLayout();
/* 133 */       layout.numColumns = 3;
/*     */       
/* 135 */       this.shell.setLayout(layout);
/*     */       
/*     */ 
/*     */ 
/* 139 */       Label info_label = new Label(this.shell, 0);
/* 140 */       info_label.setText(MessageText.getString("window.networkselection.info"));
/* 141 */       GridData gridData = new GridData(1808);
/* 142 */       gridData.horizontalSpan = 3;
/* 143 */       Utils.setLayoutData(info_label, gridData);
/*     */       
/*     */ 
/*     */ 
/* 147 */       Label labelSeparator = new Label(this.shell, 258);
/* 148 */       gridData = new GridData(768);
/* 149 */       gridData.horizontalSpan = 3;
/* 150 */       Utils.setLayoutData(labelSeparator, gridData);
/*     */       
/*     */ 
/*     */ 
/* 154 */       Label desc_label = new Label(this.shell, 0);
/* 155 */       desc_label.setText(MessageText.getString("window.networkselection.description"));
/* 156 */       gridData = new GridData(1808);
/* 157 */       gridData.horizontalSpan = 1;
/* 158 */       Utils.setLayoutData(desc_label, gridData);
/*     */       
/* 160 */       Label desc_value = new Label(this.shell, 0);
/* 161 */       desc_value.setText(description);
/* 162 */       gridData = new GridData(1808);
/* 163 */       gridData.horizontalSpan = 2;
/* 164 */       Utils.setLayoutData(desc_value, gridData);
/*     */       
/*     */ 
/*     */ 
/* 168 */       this.checkboxes = new Button[AENetworkClassifier.AT_NETWORKS.length];
/*     */       
/* 170 */       for (int i = 0; i < AENetworkClassifier.AT_NETWORKS.length; i++)
/*     */       {
/* 172 */         String network = AENetworkClassifier.AT_NETWORKS[i];
/*     */         
/* 174 */         String msg_text = "ConfigView.section.connection.networks." + network;
/*     */         
/* 176 */         Label label = new Label(this.shell, 0);
/* 177 */         gridData = new GridData(1808);
/* 178 */         gridData.horizontalSpan = 1;
/* 179 */         Utils.setLayoutData(label, gridData);
/* 180 */         Messages.setLanguageText(label, msg_text);
/*     */         
/* 182 */         Button checkBox = new Button(this.shell, 32);
/* 183 */         checkBox.setSelection(false);
/* 184 */         gridData = new GridData(1808);
/* 185 */         gridData.horizontalSpan = 2;
/* 186 */         Utils.setLayoutData(checkBox, gridData);
/*     */         
/* 188 */         this.checkboxes[i] = checkBox;
/*     */         
/* 190 */         for (int j = 0; j < tracker_networks.length; j++)
/*     */         {
/* 192 */           if (tracker_networks[j] == network)
/*     */           {
/* 194 */             checkBox.setSelection(true);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 201 */       labelSeparator = new Label(this.shell, 258);
/* 202 */       gridData = new GridData(768);
/* 203 */       gridData.horizontalSpan = 3;
/* 204 */       Utils.setLayoutData(labelSeparator, gridData);
/*     */       
/*     */ 
/*     */ 
/* 208 */       new Label(this.shell, 0);
/*     */       
/* 210 */       Button bOk = new Button(this.shell, 8);
/* 211 */       bOk.setText(MessageText.getString("Button.ok"));
/* 212 */       gridData = new GridData(896);
/* 213 */       gridData.grabExcessHorizontalSpace = true;
/* 214 */       gridData.widthHint = 70;
/* 215 */       Utils.setLayoutData(bOk, gridData);
/* 216 */       bOk.addListener(13, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 218 */           SWTNetworkSelection.classifierDialog.this.close(true);
/*     */         }
/*     */         
/* 221 */       });
/* 222 */       Button bCancel = new Button(this.shell, 8);
/* 223 */       bCancel.setText(MessageText.getString("Button.cancel"));
/* 224 */       gridData = new GridData(128);
/* 225 */       gridData.grabExcessHorizontalSpace = false;
/* 226 */       gridData.widthHint = 70;
/* 227 */       Utils.setLayoutData(bCancel, gridData);
/* 228 */       bCancel.addListener(13, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 230 */           SWTNetworkSelection.classifierDialog.this.close(false);
/*     */         }
/*     */         
/* 233 */       });
/* 234 */       this.shell.setDefaultButton(bOk);
/*     */       
/* 236 */       this.shell.addListener(31, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 238 */           if (e.character == '\033') {
/* 239 */             SWTNetworkSelection.classifierDialog.this.close(false);
/*     */           }
/*     */           
/*     */         }
/*     */         
/* 244 */       });
/* 245 */       this.shell.pack();
/*     */       
/* 247 */       Utils.centreWindow(this.shell);
/*     */       
/* 249 */       this.shell.open();
/*     */       
/* 251 */       while (!this.shell.isDisposed()) {
/* 252 */         if ((!display.isDisposed()) && (!display.readAndDispatch())) {
/* 253 */           display.sleep();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void close(boolean ok)
/*     */     {
/* 262 */       if (!ok)
/*     */       {
/* 264 */         this.selection = null;
/*     */       }
/*     */       else
/*     */       {
/* 268 */         List l = new ArrayList();
/*     */         
/* 270 */         for (int i = 0; i < AENetworkClassifier.AT_NETWORKS.length; i++)
/*     */         {
/* 272 */           if (this.checkboxes[i].getSelection())
/*     */           {
/* 274 */             l.add(AENetworkClassifier.AT_NETWORKS[i]);
/*     */           }
/*     */         }
/*     */         
/* 278 */         this.selection = new String[l.size()];
/*     */         
/* 280 */         l.toArray(this.selection);
/*     */       }
/*     */       
/* 283 */       this.shell.dispose();
/* 284 */       this.sem.releaseForever();
/*     */     }
/*     */     
/*     */ 
/*     */     protected String[] getSelection()
/*     */     {
/* 290 */       return this.selection;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/networks/SWTNetworkSelection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */