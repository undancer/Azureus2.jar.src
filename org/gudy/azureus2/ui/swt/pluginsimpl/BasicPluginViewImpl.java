/*     */ package org.gudy.azureus2.ui.swt.pluginsimpl;
/*     */ 
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import java.util.regex.PatternSyntaxException;
/*     */ import org.eclipse.swt.SWT;
/*     */ import org.eclipse.swt.custom.StyledText;
/*     */ import org.eclipse.swt.events.KeyAdapter;
/*     */ import org.eclipse.swt.events.KeyEvent;
/*     */ import org.eclipse.swt.events.ModifyEvent;
/*     */ import org.eclipse.swt.events.ModifyListener;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.ProgressBar;
/*     */ import org.eclipse.swt.widgets.ScrollBar;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.plugins.ui.components.UIProgressBar;
/*     */ import org.gudy.azureus2.plugins.ui.components.UIPropertyChangeEvent;
/*     */ import org.gudy.azureus2.plugins.ui.components.UIPropertyChangeListener;
/*     */ import org.gudy.azureus2.plugins.ui.components.UITextArea;
/*     */ import org.gudy.azureus2.plugins.ui.components.UITextField;
/*     */ import org.gudy.azureus2.plugins.ui.model.BasicPluginViewModel;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.BufferedLabel;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy.copyToClipProvider;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
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
/*     */ public class BasicPluginViewImpl
/*     */   implements UISWTViewCoreEventListenerEx, UIPropertyChangeListener
/*     */ {
/*     */   BasicPluginViewModel model;
/*     */   Display display;
/*     */   Composite panel;
/*     */   ProgressBar progress;
/*     */   BufferedLabel status;
/*     */   BufferedLabel task;
/*     */   StyledText log;
/*     */   Pattern inclusionFilter;
/*     */   Pattern exclusionFilter;
/*     */   boolean paused;
/*     */   boolean isCreated;
/*     */   
/*     */   public BasicPluginViewImpl(BasicPluginViewModel model)
/*     */   {
/*  81 */     this.model = model;
/*  82 */     this.isCreated = false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isCloneable()
/*     */   {
/*  88 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public UISWTViewCoreEventListener getClone()
/*     */   {
/*  94 */     return new BasicPluginViewImpl(this.model);
/*     */   }
/*     */   
/*     */ 
/*     */   public BasicPluginViewModel getModel()
/*     */   {
/* 100 */     return this.model;
/*     */   }
/*     */   
/*     */   public boolean eventOccurred(UISWTViewEvent event) {
/* 104 */     switch (event.getType()) {
/*     */     case 0: 
/* 106 */       if (this.isCreated)
/* 107 */         return false;
/* 108 */       this.isCreated = true;
/* 109 */       UISWTView swtView = event.getView();
/* 110 */       if (swtView != null) {
/* 111 */         swtView.setTitle(this.model.getName());
/*     */       }
/*     */       
/*     */ 
/*     */       break;
/*     */     case 2: 
/* 117 */       initialize((Composite)event.getData());
/* 118 */       UISWTView swtView = event.getView();
/* 119 */       if (swtView != null) {
/* 120 */         swtView.setTitle(this.model.getName());
/*     */       }
/*     */       
/*     */ 
/*     */       break;
/*     */     case 5: 
/* 126 */       refresh();
/* 127 */       break;
/*     */     
/*     */     case 7: 
/* 130 */       delete();
/* 131 */       this.isCreated = false;
/* 132 */       break;
/*     */     
/*     */     case 3: 
/* 135 */       String text = this.model.getLogArea().getText().trim();
/*     */       
/* 137 */       if ((this.log != null) && (!this.log.isDisposed())) {
/* 138 */         this.log.setText(text);
/*     */         
/* 140 */         this.log.setTopIndex(this.log.getLineCount());
/*     */       }
/*     */       
/*     */       break;
/*     */     case 4: 
/* 145 */       if ((this.log != null) && (!this.log.isDisposed())) {
/* 146 */         this.log.setText("");
/*     */       }
/*     */       break;
/*     */     }
/*     */     
/* 151 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   private void initialize(Composite composite)
/*     */   {
/* 157 */     String sConfigSectionID = this.model.getConfigSectionID();
/*     */     
/* 159 */     this.display = composite.getDisplay();
/* 160 */     this.panel = new Composite(composite, 0);
/* 161 */     GridLayout gridLayout = new GridLayout();
/* 162 */     gridLayout.numColumns = 2;
/* 163 */     this.panel.setLayout(gridLayout);
/* 164 */     GridData gridData = new GridData(1808);
/* 165 */     Utils.setLayoutData(this.panel, gridData);
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
/* 178 */     Composite topSection = new Composite(this.panel, 0);
/* 179 */     gridLayout = new GridLayout();
/* 180 */     gridLayout.numColumns = 2;
/* 181 */     gridLayout.marginHeight = 0;
/* 182 */     gridLayout.marginWidth = 0;
/* 183 */     topSection.setLayout(gridLayout);
/* 184 */     gridData = new GridData(768);
/* 185 */     if (sConfigSectionID == null) {
/* 186 */       gridData.horizontalSpan = 2;
/*     */     }
/* 188 */     Utils.setLayoutData(topSection, gridData);
/*     */     
/* 190 */     if (this.model.getStatus().getVisible()) {
/* 191 */       Label statusTitle = new Label(topSection, 0);
/* 192 */       Messages.setLanguageText(statusTitle, "plugins.basicview.status");
/*     */       
/* 194 */       this.status = new BufferedLabel(topSection, 0);
/* 195 */       gridData = new GridData(768);
/* 196 */       Utils.setLayoutData(this.status, gridData);
/*     */     }
/*     */     
/* 199 */     if (this.model.getActivity().getVisible()) {
/* 200 */       Label activityTitle = new Label(topSection, 0);
/* 201 */       Messages.setLanguageText(activityTitle, "plugins.basicview.activity");
/*     */       
/* 203 */       this.task = new BufferedLabel(topSection, 0);
/* 204 */       gridData = new GridData(768);
/* 205 */       Utils.setLayoutData(this.task, gridData);
/*     */     }
/*     */     
/* 208 */     if (this.model.getProgress().getVisible()) {
/* 209 */       Label progressTitle = new Label(topSection, 0);
/* 210 */       Messages.setLanguageText(progressTitle, "plugins.basicview.progress");
/*     */       
/* 212 */       this.progress = new ProgressBar(topSection, 0);
/* 213 */       this.progress.setMaximum(100);
/* 214 */       this.progress.setMinimum(0);
/* 215 */       gridData = new GridData(768);
/* 216 */       Utils.setLayoutData(this.progress, gridData);
/*     */     }
/*     */     
/* 219 */     if (sConfigSectionID != null) {
/* 220 */       Composite configSection = new Composite(this.panel, 0);
/* 221 */       gridLayout = new GridLayout();
/* 222 */       gridLayout.numColumns = 1;
/* 223 */       gridLayout.marginHeight = 0;
/* 224 */       gridLayout.marginWidth = 2;
/* 225 */       configSection.setLayout(gridLayout);
/* 226 */       gridData = new GridData(11);
/* 227 */       Utils.setLayoutData(configSection, gridData);
/*     */       
/*     */ 
/*     */ 
/* 231 */       Button btnConfig = new Button(configSection, 8);
/* 232 */       Messages.setLanguageText(btnConfig, "plugins.basicview.config");
/* 233 */       btnConfig.addSelectionListener(new SelectionAdapter() {
/*     */         public void widgetSelected(SelectionEvent e) {
/* 235 */           UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 236 */           if (uiFunctions != null) {
/* 237 */             uiFunctions.getMDI().showEntryByID("ConfigView", BasicPluginViewImpl.this.model.getConfigSectionID());
/*     */           }
/*     */           
/*     */         }
/*     */         
/* 242 */       });
/* 243 */       Utils.setLayoutData(btnConfig, new GridData());
/*     */     }
/*     */     
/* 246 */     if (this.model.getLogArea().getVisible()) {
/* 247 */       Label logTitle = new Label(topSection, 0);
/* 248 */       Messages.setLanguageText(logTitle, "plugins.basicview.log");
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 253 */       Button button = new Button(topSection, 8);
/* 254 */       Messages.setLanguageText(button, "plugins.basicview.clear");
/*     */       
/* 256 */       button.addListener(13, new Listener()
/*     */       {
/*     */         public void handleEvent(Event event) {
/* 259 */           BasicPluginViewImpl.this.model.getLogArea().setText("");
/*     */         }
/* 261 */       });
/* 262 */       this.log = new StyledText(this.panel, 2824);
/* 263 */       gridData = new GridData(1808);
/* 264 */       gridData.horizontalSpan = 2;
/* 265 */       Utils.setLayoutData(this.log, gridData);
/*     */       
/* 267 */       ClipboardCopy.addCopyToClipMenu(this.log, new ClipboardCopy.copyToClipProvider()
/*     */       {
/*     */ 
/*     */ 
/*     */         public String getText()
/*     */         {
/*     */ 
/* 274 */           return BasicPluginViewImpl.this.log.getText().trim();
/*     */         }
/*     */         
/* 277 */       });
/* 278 */       this.log.addKeyListener(new KeyAdapter()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void keyPressed(KeyEvent event)
/*     */         {
/*     */ 
/* 285 */           int key = event.character;
/*     */           
/* 287 */           if ((key <= 26) && (key > 0))
/*     */           {
/* 289 */             key += 96;
/*     */           }
/*     */           
/* 292 */           if ((key == 97) && (event.stateMask == SWT.MOD1))
/*     */           {
/* 294 */             event.doit = false;
/*     */             
/* 296 */             BasicPluginViewImpl.this.log.selectAll();
/*     */ 
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */ 
/* 303 */       });
/* 304 */       this.model.getLogArea().addPropertyChangeListener(this);
/*     */       
/* 306 */       Composite bottomSection = new Composite(this.panel, 0);
/* 307 */       gridLayout = new GridLayout();
/* 308 */       gridLayout.numColumns = 3;
/* 309 */       gridLayout.marginHeight = 0;
/* 310 */       gridLayout.marginWidth = 0;
/* 311 */       bottomSection.setLayout(gridLayout);
/* 312 */       gridData = new GridData(768);
/* 313 */       gridData.horizontalSpan = 2;
/* 314 */       Utils.setLayoutData(bottomSection, gridData);
/*     */       
/*     */ 
/*     */ 
/* 318 */       Label label = new Label(bottomSection, 0);
/* 319 */       Utils.setLayoutData(label, new GridData());
/* 320 */       Messages.setLanguageText(label, "LoggerView.includeOnly");
/*     */       
/* 322 */       final Text inclText = new Text(bottomSection, 2048);
/* 323 */       gridData = new GridData();
/* 324 */       gridData.widthHint = 200;
/* 325 */       Utils.setLayoutData(inclText, gridData);
/* 326 */       inclText.addModifyListener(new ModifyListener()
/*     */       {
/*     */         public void modifyText(ModifyEvent e) {
/* 329 */           String newExpression = inclText.getText();
/* 330 */           if (newExpression.length() == 0) {
/* 331 */             BasicPluginViewImpl.this.inclusionFilter = null;
/*     */           }
/*     */           else {
/*     */             try
/*     */             {
/* 336 */               BasicPluginViewImpl.this.inclusionFilter = Pattern.compile(newExpression, 2);
/* 337 */               inclText.setBackground(null);
/*     */             }
/*     */             catch (PatternSyntaxException e1) {
/* 340 */               inclText.setBackground(Colors.colorErrorBG);
/*     */             }
/*     */             
/*     */           }
/*     */         }
/* 345 */       });
/* 346 */       label = new Label(bottomSection, 0);
/*     */       
/*     */ 
/*     */ 
/* 350 */       label = new Label(bottomSection, 0);
/* 351 */       Utils.setLayoutData(label, new GridData());
/* 352 */       Messages.setLanguageText(label, "LoggerView.excludeAll");
/*     */       
/* 354 */       final Text exclText = new Text(bottomSection, 2048);
/* 355 */       gridData = new GridData();
/* 356 */       gridData.widthHint = 200;
/* 357 */       Utils.setLayoutData(exclText, gridData);
/* 358 */       exclText.addModifyListener(new ModifyListener()
/*     */       {
/*     */         public void modifyText(ModifyEvent e) {
/* 361 */           String newExpression = exclText.getText();
/* 362 */           if (newExpression.length() == 0) {
/* 363 */             BasicPluginViewImpl.this.exclusionFilter = null;
/*     */           }
/*     */           else {
/*     */             try
/*     */             {
/* 368 */               BasicPluginViewImpl.this.exclusionFilter = Pattern.compile(newExpression, 2);
/* 369 */               exclText.setBackground(null);
/*     */             }
/*     */             catch (PatternSyntaxException e1) {
/* 372 */               exclText.setBackground(Colors.colorErrorBG);
/*     */             }
/*     */             
/*     */           }
/*     */         }
/* 377 */       });
/* 378 */       label = new Label(bottomSection, 0);
/*     */       
/*     */ 
/*     */ 
/* 382 */       Button buttonPause = new Button(bottomSection, 32);
/* 383 */       Messages.setLanguageText(buttonPause, "LoggerView.pause");
/* 384 */       gridData = new GridData();
/* 385 */       Utils.setLayoutData(buttonPause, gridData);
/* 386 */       buttonPause.addSelectionListener(new SelectionAdapter() {
/*     */         public void widgetSelected(SelectionEvent e) {
/* 388 */           if ((e.widget == null) || (!(e.widget instanceof Button)))
/* 389 */             return;
/* 390 */           Button btn = (Button)e.widget;
/* 391 */           BasicPluginViewImpl.this.paused = btn.getSelection();
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void refresh()
/*     */   {
/* 400 */     if (this.status != null) {
/* 401 */       this.status.setText(this.model.getStatus().getText());
/*     */     }
/* 403 */     if (this.task != null) {
/* 404 */       this.task.setText(this.model.getActivity().getText());
/*     */     }
/* 406 */     if (this.progress != null) {
/* 407 */       this.progress.setSelection(this.model.getProgress().getPercentageComplete());
/*     */     }
/*     */   }
/*     */   
/*     */   public void propertyChanged(final UIPropertyChangeEvent ev) {
/* 412 */     if (ev.getSource() != this.model.getLogArea())
/* 413 */       return;
/* 414 */     if ((this.display == null) || (this.display.isDisposed()) || (this.log == null) || (this.paused)) {
/* 415 */       return;
/*     */     }
/*     */     
/* 418 */     this.display.asyncExec(new AERunnable() {
/*     */       public void runSupport() {
/* 420 */         if (BasicPluginViewImpl.this.log.isDisposed())
/* 421 */           return;
/* 422 */         if (!BasicPluginViewImpl.this.log.isVisible()) {
/* 423 */           return;
/*     */         }
/* 425 */         String old_value = (String)ev.getOldPropertyValue();
/* 426 */         String new_value = (String)ev.getNewPropertyValue();
/*     */         
/* 428 */         ScrollBar bar = BasicPluginViewImpl.this.log.getVerticalBar();
/*     */         
/* 430 */         boolean max = bar.getSelection() == bar.getMaximum() - bar.getThumb();
/* 431 */         int lineOffset = BasicPluginViewImpl.this.log.getLineCount() - BasicPluginViewImpl.this.log.getTopIndex();
/*     */         
/* 433 */         if (new_value.startsWith(old_value))
/*     */         {
/* 435 */           String toAppend = new_value.substring(old_value.length());
/* 436 */           if (toAppend.length() == 0) {
/* 437 */             return;
/*     */           }
/* 439 */           StringBuilder builder = new StringBuilder(toAppend.length());
/*     */           
/* 441 */           String[] lines = toAppend.split("\n");
/*     */           
/*     */ 
/* 444 */           for (int i = 0; i < lines.length; i++) {
/* 445 */             String line = lines[i];
/*     */             
/* 447 */             if (((BasicPluginViewImpl.this.inclusionFilter == null) || (BasicPluginViewImpl.this.inclusionFilter.matcher(line).find())) && ((BasicPluginViewImpl.this.exclusionFilter == null) || (!BasicPluginViewImpl.this.exclusionFilter.matcher(line).find())))
/*     */             {
/* 449 */               builder.append("\n");
/* 450 */               builder.append(line);
/*     */             }
/*     */           }
/*     */           
/* 454 */           BasicPluginViewImpl.this.log.append(builder.toString());
/*     */         }
/*     */         else
/*     */         {
/* 458 */           StringBuilder builder = new StringBuilder(new_value.length());
/*     */           
/* 460 */           String[] lines = new_value.split("\n");
/*     */           
/* 462 */           for (int i = 0; i < lines.length; i++) {
/* 463 */             String line = lines[i];
/* 464 */             if (((BasicPluginViewImpl.this.inclusionFilter == null) || (BasicPluginViewImpl.this.inclusionFilter.matcher(line).find())) && ((BasicPluginViewImpl.this.exclusionFilter == null) || (!BasicPluginViewImpl.this.exclusionFilter.matcher(line).find())))
/*     */             {
/* 466 */               if (line != lines[0]) {
/* 467 */                 builder.append("\n");
/*     */               }
/* 469 */               builder.append(line);
/*     */             }
/*     */           }
/* 472 */           BasicPluginViewImpl.this.log.setText(builder.toString());
/*     */         }
/*     */         
/* 475 */         if (max)
/*     */         {
/* 477 */           bar.setSelection(bar.getMaximum() - bar.getThumb());
/* 478 */           BasicPluginViewImpl.this.log.setTopIndex(BasicPluginViewImpl.this.log.getLineCount() - lineOffset);
/* 479 */           BasicPluginViewImpl.this.log.redraw();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void delete()
/*     */   {
/* 489 */     this.model.getLogArea().removePropertyChangeListener(this);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/pluginsimpl/BasicPluginViewImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */