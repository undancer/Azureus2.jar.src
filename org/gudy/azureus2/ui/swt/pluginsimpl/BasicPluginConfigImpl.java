/*     */ package org.gudy.azureus2.ui.swt.pluginsimpl;
/*     */ 
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.custom.CTabFolder;
/*     */ import org.eclipse.swt.custom.CTabItem;
/*     */ import org.eclipse.swt.graphics.RGB;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.ui.config.EnablerParameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.ParameterListener;
/*     */ import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.ActionParameterImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.BooleanParameterImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.ColorParameterImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.DirectoryParameterImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.HyperlinkParameterImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.InfoParameterImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.IntParameterImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.LabelParameterImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.ParameterGroupImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.ParameterImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.ParameterImplListener;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.ParameterTabFolderImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.PasswordParameterImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.StringListParameterImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.StringParameterImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.UIParameterImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.UITextAreaImpl;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.LinkLabel;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ButtonParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ColorParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.DualChangeSelectionActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.IAdditionalActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.InfoParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.IntParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.LinkParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ParameterChangeAdapter;
/*     */ import org.gudy.azureus2.ui.swt.config.PasswordParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.StringAreaParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.StringParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.TextAreaParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.UISWTParameter;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy.copyToClipProvider;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTConfigSection;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTParameterContext;
/*     */ 
/*     */ public class BasicPluginConfigImpl implements UISWTConfigSection
/*     */ {
/*     */   protected WeakReference<BasicPluginConfigModel> model_ref;
/*     */   protected String parent_section;
/*     */   protected String section;
/*     */   
/*     */   public BasicPluginConfigImpl(WeakReference<BasicPluginConfigModel> _model_ref)
/*     */   {
/*  74 */     this.model_ref = _model_ref;
/*     */     
/*  76 */     BasicPluginConfigModel model = (BasicPluginConfigModel)this.model_ref.get();
/*     */     
/*  78 */     this.parent_section = model.getParentSection();
/*  79 */     this.section = model.getSection();
/*     */   }
/*     */   
/*     */ 
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  85 */     if ((this.parent_section == null) || (this.parent_section.length() == 0))
/*     */     {
/*  87 */       return "root";
/*     */     }
/*     */     
/*  90 */     return this.parent_section;
/*     */   }
/*     */   
/*     */ 
/*     */   public String configSectionGetName()
/*     */   {
/*  96 */     return this.section;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void configSectionSave() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void configSectionDelete() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int maxUserMode()
/*     */   {
/* 116 */     BasicPluginConfigModel model = (BasicPluginConfigModel)this.model_ref.get();
/*     */     
/* 118 */     int max_mode = 0;
/*     */     
/* 120 */     if (model != null)
/*     */     {
/* 122 */       org.gudy.azureus2.plugins.ui.config.Parameter[] parameters = model.getParameters();
/*     */       
/* 124 */       for (int i = 0; i < parameters.length; i++)
/*     */       {
/* 126 */         ParameterImpl param = (ParameterImpl)parameters[i];
/*     */         
/* 128 */         if (param.getMinimumRequiredUserMode() > max_mode)
/*     */         {
/* 130 */           max_mode = param.getMinimumRequiredUserMode();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 135 */     return max_mode;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/* 143 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*     */     
/*     */ 
/*     */ 
/* 147 */     Composite main_tab = new Composite(parent, 0);
/*     */     
/* 149 */     GridData main_gridData = new GridData(272);
/*     */     
/* 151 */     Utils.setLayoutData(main_tab, main_gridData);
/*     */     
/* 153 */     GridLayout layout = new GridLayout();
/*     */     
/* 155 */     layout.numColumns = 2;
/*     */     
/* 157 */     layout.marginHeight = 0;
/*     */     
/* 159 */     main_tab.setLayout(layout);
/*     */     
/* 161 */     final Map comp_map = new HashMap();
/*     */     
/* 163 */     Composite current_composite = main_tab;
/*     */     
/* 165 */     Map<ParameterGroupImpl, Composite> group_map = new HashMap();
/*     */     
/* 167 */     Map<ParameterTabFolderImpl, CTabFolder> tab_folder_map = new HashMap();
/* 168 */     Map<ParameterGroupImpl, Composite> tab_map = new HashMap();
/*     */     
/*     */ 
/* 171 */     BasicPluginConfigModel model = (BasicPluginConfigModel)this.model_ref.get();
/*     */     
/* 173 */     if (model == null)
/*     */     {
/* 175 */       return main_tab;
/*     */     }
/*     */     
/* 178 */     org.gudy.azureus2.plugins.ui.config.Parameter[] parameters = model.getParameters();
/*     */     
/* 180 */     List<Button> buttons = new ArrayList();
/*     */     
/* 182 */     for (int i = 0; i < parameters.length; i++)
/*     */     {
/* 184 */       final ParameterImpl param = (ParameterImpl)parameters[i];
/*     */       
/* 186 */       if (param.getMinimumRequiredUserMode() <= userMode)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 191 */         ParameterGroupImpl pg = param.getGroup();
/*     */         
/* 193 */         if (pg == null)
/*     */         {
/* 195 */           current_composite = main_tab;
/*     */         }
/*     */         else
/*     */         {
/* 199 */           ParameterTabFolderImpl tab_folder = pg.getTabFolder();
/*     */           
/* 201 */           if (tab_folder != null)
/*     */           {
/* 203 */             ParameterGroupImpl tab_group = tab_folder.getGroup();
/*     */             
/* 205 */             CTabFolder tf = (CTabFolder)tab_folder_map.get(tab_folder);
/*     */             
/* 207 */             if (tf == null)
/*     */             {
/* 209 */               Composite tab_parent = current_composite;
/*     */               
/* 211 */               if (tab_group != null)
/*     */               {
/* 213 */                 String tg_resource = tab_group.getResourceName();
/*     */                 
/* 215 */                 if (tg_resource != null)
/*     */                 {
/* 217 */                   tab_parent = (Composite)group_map.get(tab_group);
/*     */                   
/* 219 */                   if (tab_parent == null)
/*     */                   {
/* 221 */                     tab_parent = new Group(current_composite, 0);
/*     */                     
/* 223 */                     Messages.setLanguageText(tab_parent, tg_resource);
/*     */                     
/* 225 */                     GridData gridData = new GridData(768);
/*     */                     
/* 227 */                     gridData.horizontalSpan = 2;
/*     */                     
/* 229 */                     if (tab_group.getMinimumRequiredUserMode() > userMode)
/*     */                     {
/* 231 */                       tab_parent.setVisible(false);
/*     */                       
/* 233 */                       gridData.widthHint = 0;
/* 234 */                       gridData.heightHint = 0;
/*     */                     }
/*     */                     
/* 237 */                     Utils.setLayoutData(tab_parent, gridData);
/*     */                     
/* 239 */                     layout = new GridLayout();
/*     */                     
/* 241 */                     layout.numColumns = (tab_group.getNumberColumns() * 2);
/*     */                     
/* 243 */                     tab_parent.setLayout(layout);
/*     */                     
/* 245 */                     group_map.put(tab_group, tab_parent);
/*     */                   }
/*     */                 }
/*     */               }
/*     */               
/* 250 */               tf = new CTabFolder(tab_parent, 16384);
/*     */               
/* 252 */               tf.setBorderVisible(tab_group == null);
/*     */               
/* 254 */               tf.setTabHeight(20);
/*     */               
/* 256 */               GridData grid_data = new GridData(768);
/*     */               
/* 258 */               grid_data.horizontalSpan = 2;
/*     */               
/* 260 */               if (tab_folder.getMinimumRequiredUserMode() > userMode)
/*     */               {
/* 262 */                 tf.setVisible(false);
/*     */                 
/* 264 */                 grid_data.widthHint = 0;
/* 265 */                 grid_data.heightHint = 0;
/*     */               }
/*     */               
/* 268 */               Utils.setLayoutData(tf, grid_data);
/*     */               
/* 270 */               tab_folder_map.put(tab_folder, tf);
/*     */             }
/*     */             
/* 273 */             Composite tab_composite = (Composite)tab_map.get(pg);
/*     */             
/* 275 */             if (tab_composite == null)
/*     */             {
/* 277 */               CTabItem tab_item = new CTabItem(tf, 0);
/*     */               
/* 279 */               String tab_name = pg.getResourceName();
/*     */               
/* 281 */               if (tab_name != null)
/*     */               {
/* 283 */                 Messages.setLanguageText(tab_item, tab_name);
/*     */               }
/*     */               
/* 286 */               tab_composite = new Composite(tf, 0);
/* 287 */               tab_item.setControl(tab_composite);
/*     */               
/* 289 */               layout = new GridLayout();
/* 290 */               layout.numColumns = 2;
/*     */               
/* 292 */               tab_composite.setLayout(layout);
/*     */               
/* 294 */               GridData grid_data = new GridData(1808);
/*     */               
/* 296 */               if (pg.getMinimumRequiredUserMode() > userMode)
/*     */               {
/* 298 */                 tab_composite.setVisible(false);
/*     */                 
/* 300 */                 grid_data.widthHint = 0;
/* 301 */                 grid_data.heightHint = 0;
/*     */               }
/*     */               
/* 304 */               Utils.setLayoutData(tab_composite, grid_data);
/*     */               
/* 306 */               if (tf.getItemCount() == 1)
/*     */               {
/* 308 */                 tf.setSelection(tab_item);
/*     */               }
/*     */               
/* 311 */               tab_map.put(pg, tab_composite);
/*     */             }
/*     */             
/* 314 */             current_composite = tab_composite;
/*     */           }
/*     */           
/* 317 */           Composite comp = (Composite)group_map.get(pg);
/*     */           
/* 319 */           if (comp == null)
/*     */           {
/* 321 */             boolean nested = (pg.getGroup() != null) || (tab_folder != null);
/*     */             
/* 323 */             Composite group_parent = nested ? current_composite : main_tab;
/*     */             
/* 325 */             String resource_name = pg.getResourceName();
/*     */             
/* 327 */             boolean use_composite = (resource_name == null) || (tab_folder != null);
/*     */             
/* 329 */             current_composite = use_composite ? new Composite(group_parent, 0) : new Group(group_parent, 0);
/*     */             
/* 331 */             if (!use_composite)
/*     */             {
/* 333 */               Messages.setLanguageText(current_composite, resource_name);
/*     */             }
/*     */             
/* 336 */             GridData grid_data = new GridData(272);
/*     */             
/* 338 */             grid_data.grabExcessHorizontalSpace = true;
/* 339 */             grid_data.horizontalSpan = 2;
/*     */             
/* 341 */             if (pg.getMinimumRequiredUserMode() > userMode)
/*     */             {
/* 343 */               current_composite.setVisible(false);
/*     */               
/* 345 */               grid_data.widthHint = 0;
/* 346 */               grid_data.heightHint = 0;
/*     */             }
/*     */             
/* 349 */             Utils.setLayoutData(current_composite, grid_data);
/*     */             
/* 351 */             layout = new GridLayout();
/*     */             
/* 353 */             layout.numColumns = (pg.getNumberColumns() * 2);
/*     */             
/* 355 */             current_composite.setLayout(layout);
/*     */             
/* 357 */             group_map.put(pg, current_composite);
/*     */           }
/*     */           else
/*     */           {
/* 361 */             current_composite = comp;
/*     */           }
/*     */         }
/*     */         
/* 365 */         Label label = null;
/*     */         
/* 367 */         String label_key = param.getLabelKey();
/*     */         
/* 369 */         String label_text = label_key == null ? param.getLabelText() : MessageText.getString(label_key);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 374 */         if ((label_text.indexOf('\n') != -1) || (label_text.indexOf('\t') != -1) || (!(param instanceof BooleanParameterImpl)))
/*     */         {
/*     */ 
/*     */ 
/* 378 */           String hyperlink = null;
/* 379 */           if ((param instanceof HyperlinkParameterImpl)) {
/* 380 */             hyperlink = ((HyperlinkParameterImpl)param).getHyperlink();
/*     */           }
/*     */           
/* 383 */           label = new Label(current_composite, (param instanceof LabelParameterImpl) ? 64 : 0);
/*     */           
/*     */           boolean add_copy;
/*     */           boolean add_copy;
/* 387 */           if (label_key == null) {
/* 388 */             label.setText(param.getLabelText());
/* 389 */             add_copy = true;
/*     */           } else {
/* 391 */             Messages.setLanguageText(label, label_key);
/* 392 */             add_copy = label_key.startsWith("!");
/*     */           }
/*     */           
/* 395 */           if (add_copy) {
/* 396 */             final Label f_label = label;
/*     */             
/* 398 */             org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy.addCopyToClipMenu(label, new ClipboardCopy.copyToClipProvider()
/*     */             {
/*     */ 
/*     */ 
/*     */               public String getText()
/*     */               {
/*     */ 
/* 405 */                 return f_label.getText().trim();
/*     */               }
/*     */             });
/*     */           }
/*     */           
/* 410 */           if (hyperlink != null) {
/* 411 */             LinkLabel.makeLinkedLabel(label, hyperlink);
/*     */           }
/*     */           
/* 414 */           if ((param instanceof HyperlinkParameterImpl))
/*     */           {
/* 416 */             final Label f_label = label;
/*     */             
/* 418 */             param.addListener(new ParameterListener()
/*     */             {
/*     */ 
/*     */ 
/*     */               public void parameterChanged(org.gudy.azureus2.plugins.ui.config.Parameter p)
/*     */               {
/*     */ 
/* 425 */                 if (f_label.isDisposed())
/*     */                 {
/* 427 */                   param.removeListener(this);
/*     */                 }
/*     */                 else {
/* 430 */                   final String hyperlink = ((HyperlinkParameterImpl)param).getHyperlink();
/*     */                   
/* 432 */                   if (hyperlink != null)
/*     */                   {
/* 434 */                     Utils.execSWTThread(new Runnable()
/*     */                     {
/*     */ 
/*     */                       public void run()
/*     */                       {
/*     */ 
/* 440 */                         LinkLabel.updateLinkedLabel(BasicPluginConfigImpl.2.this.val$f_label, hyperlink);
/*     */                       }
/*     */                     });
/*     */                   }
/*     */                 }
/*     */               }
/*     */             });
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 451 */         String key = param.getKey();
/*     */         
/*     */ 
/*     */         org.gudy.azureus2.ui.swt.config.Parameter swt_param;
/*     */         
/*     */ 
/* 457 */         if ((param instanceof BooleanParameterImpl)) { org.gudy.azureus2.ui.swt.config.Parameter swt_param;
/*     */           final org.gudy.azureus2.ui.swt.config.Parameter swt_param;
/* 459 */           if (label == null)
/*     */           {
/* 461 */             swt_param = new BooleanParameter(current_composite, key, ((BooleanParameterImpl)param).getDefaultValue(), param.getLabelKey());
/*     */           }
/*     */           else
/*     */           {
/* 465 */             swt_param = new BooleanParameter(current_composite, key, ((BooleanParameterImpl)param).getDefaultValue());
/*     */           }
/*     */           
/* 468 */           GridData data = new GridData();
/* 469 */           data.horizontalSpan = (label == null ? 2 : 1);
/* 470 */           swt_param.setLayoutData(data);
/*     */           
/* 472 */           param.addListener(new ParameterListener()
/*     */           {
/*     */ 
/*     */ 
/*     */             public void parameterChanged(org.gudy.azureus2.plugins.ui.config.Parameter p)
/*     */             {
/*     */ 
/* 479 */               if (swt_param.getControls()[0].isDisposed())
/*     */               {
/* 481 */                 param.removeListener(this);
/*     */               }
/*     */               else
/*     */               {
/* 485 */                 ((BooleanParameter)swt_param).setSelected(((BooleanParameterImpl)param).getValue());
/*     */               }
/*     */             }
/*     */           });
/*     */         }
/* 490 */         else if ((param instanceof IntParameterImpl))
/*     */         {
/* 492 */           IntParameterImpl int_param = (IntParameterImpl)param;
/* 493 */           final org.gudy.azureus2.ui.swt.config.Parameter swt_param = new IntParameter(current_composite, key, int_param.getDefaultValue());
/*     */           
/*     */ 
/* 496 */           if (int_param.isLimited()) {
/* 497 */             ((IntParameter)swt_param).setMinimumValue(int_param.getMinValue());
/* 498 */             ((IntParameter)swt_param).setMaximumValue(int_param.getMaxValue());
/*     */           }
/*     */           
/* 501 */           param.addListener(new ParameterListener()
/*     */           {
/*     */ 
/*     */ 
/*     */             public void parameterChanged(org.gudy.azureus2.plugins.ui.config.Parameter p)
/*     */             {
/*     */ 
/* 508 */               if (swt_param.getControls()[0].isDisposed())
/*     */               {
/* 510 */                 param.removeListener(this);
/*     */               }
/*     */               else
/*     */               {
/* 514 */                 ((IntParameter)swt_param).setValue(((IntParameterImpl)param).getValue());
/*     */               }
/*     */               
/*     */             }
/*     */             
/* 519 */           });
/* 520 */           GridData gridData = new GridData();
/* 521 */           gridData.widthHint = 100;
/*     */           
/* 523 */           swt_param.setLayoutData(gridData);
/*     */         }
/* 525 */         else if ((param instanceof ColorParameterImpl)) {
/* 526 */           Composite area = new Composite(current_composite, 0);
/*     */           
/* 528 */           GridData gridData = new GridData();
/* 529 */           Utils.setLayoutData(area, gridData);
/* 530 */           layout = new GridLayout();
/* 531 */           layout.numColumns = 2;
/* 532 */           layout.marginHeight = 0;
/* 533 */           layout.marginWidth = 0;
/* 534 */           area.setLayout(layout);
/*     */           
/* 536 */           final ButtonParameter[] reset_button_holder = new ButtonParameter[1];
/* 537 */           final ColorParameterImpl color_param = (ColorParameterImpl)param;
/* 538 */           org.gudy.azureus2.ui.swt.config.Parameter swt_param = new ColorParameter(area, key, color_param.getRedValue(), color_param.getGreenValue(), color_param.getBlueValue())
/*     */           {
/*     */ 
/*     */             public void newColorSet(RGB newColor)
/*     */             {
/* 543 */               color_param.reloadParamDataFromConfig(true);
/* 544 */               if (reset_button_holder[0] == null) return;
/* 545 */               reset_button_holder[0].getControl().setEnabled(true);
/*     */             }
/*     */             
/*     */ 
/* 549 */           };
/* 550 */           reset_button_holder[0] = new ButtonParameter(area, "ConfigView.section.style.colorOverrides.reset");
/* 551 */           reset_button_holder[0].getControl().setEnabled(color_param.isOverridden());
/* 552 */           reset_button_holder[0].getControl().addListener(13, new org.eclipse.swt.widgets.Listener() {
/*     */             public void handleEvent(Event event) {
/* 554 */               reset_button_holder[0].getControl().setEnabled(false);
/* 555 */               color_param.resetToDefault();
/* 556 */               color_param.reloadParamDataFromConfig(false);
/*     */             }
/*     */             
/* 559 */           });
/* 560 */           gridData = new GridData();
/* 561 */           gridData.widthHint = 50;
/*     */           
/* 563 */           swt_param.setLayoutData(gridData);
/* 564 */         } else if ((param instanceof StringParameterImpl))
/*     */         {
/* 566 */           GridData gridData = new GridData(768);
/*     */           
/* 568 */           gridData.widthHint = 150;
/*     */           
/* 570 */           StringParameterImpl s_param = (StringParameterImpl)param;
/*     */           
/* 572 */           int num_lines = s_param.getMultiLine();
/*     */           
/* 574 */           if (num_lines <= 1)
/*     */           {
/* 576 */             org.gudy.azureus2.ui.swt.config.Parameter swt_param = new StringParameter(current_composite, key, s_param.getDefaultValue(), s_param.getGenerateIntermediateEvents());
/*     */             
/*     */ 
/* 579 */             swt_param.setLayoutData(gridData);
/*     */           }
/*     */           else
/*     */           {
/* 583 */             StringAreaParameter sa_param = new StringAreaParameter(current_composite, key, s_param.getDefaultValue());
/*     */             
/* 585 */             org.gudy.azureus2.ui.swt.config.Parameter swt_param = sa_param;
/*     */             
/* 587 */             gridData.heightHint = sa_param.getPreferredHeight(num_lines);
/*     */             
/* 589 */             swt_param.setLayoutData(gridData);
/*     */           }
/*     */         }
/* 592 */         else if ((param instanceof InfoParameterImpl))
/*     */         {
/* 594 */           GridData gridData = new GridData(768);
/*     */           
/* 596 */           gridData.widthHint = 150;
/*     */           
/* 598 */           org.gudy.azureus2.ui.swt.config.Parameter swt_param = new InfoParameter(current_composite, key, "");
/*     */           
/* 600 */           swt_param.setLayoutData(gridData);
/*     */         }
/* 602 */         else if ((param instanceof StringListParameterImpl))
/*     */         {
/* 604 */           StringListParameterImpl sl_param = (StringListParameterImpl)param;
/*     */           
/* 606 */           GridData gridData = new GridData();
/*     */           
/* 608 */           gridData.widthHint = 150;
/*     */           
/* 610 */           org.gudy.azureus2.ui.swt.config.Parameter swt_param = new org.gudy.azureus2.ui.swt.config.StringListParameter(current_composite, key, sl_param.getDefaultValue(), sl_param.getLabels(), sl_param.getValues());
/*     */           
/* 612 */           swt_param.setLayoutData(gridData);
/*     */         }
/* 614 */         else if ((param instanceof PasswordParameterImpl))
/*     */         {
/* 616 */           GridData gridData = new GridData();
/*     */           
/* 618 */           gridData.widthHint = 150;
/*     */           
/* 620 */           org.gudy.azureus2.ui.swt.config.Parameter swt_param = new PasswordParameter(current_composite, key, ((PasswordParameterImpl)param).getEncodingType());
/*     */           
/* 622 */           swt_param.setLayoutData(gridData);
/*     */         } else { org.gudy.azureus2.ui.swt.config.Parameter swt_param;
/* 624 */           if (((param instanceof DirectoryParameterImpl)) || ((param instanceof org.gudy.azureus2.pluginsimpl.local.ui.config.FileParameter)))
/*     */           {
/* 626 */             Composite area = new Composite(current_composite, 0);
/*     */             
/* 628 */             GridData gridData = new GridData(768);
/*     */             
/* 630 */             Utils.setLayoutData(area, gridData);
/*     */             
/* 632 */             layout = new GridLayout();
/*     */             
/* 634 */             layout.numColumns = 2;
/* 635 */             layout.marginHeight = 0;
/* 636 */             layout.marginWidth = 0;
/*     */             
/* 638 */             area.setLayout(layout);
/*     */             org.gudy.azureus2.ui.swt.config.Parameter swt_param;
/* 640 */             if ((param instanceof DirectoryParameterImpl)) {
/* 641 */               swt_param = new org.gudy.azureus2.ui.swt.config.DirectoryParameter(area, key, ((DirectoryParameterImpl)param).getDefaultValue());
/*     */             }
/*     */             else {
/* 644 */               org.gudy.azureus2.pluginsimpl.local.ui.config.FileParameter fp = (org.gudy.azureus2.pluginsimpl.local.ui.config.FileParameter)param;
/* 645 */               swt_param = new org.gudy.azureus2.ui.swt.config.FileParameter(area, key, fp.getDefaultValue(), fp.getFileExtensions());
/*     */             }
/*     */           }
/* 648 */           else if ((param instanceof ActionParameterImpl))
/*     */           {
/* 650 */             ActionParameterImpl _param = (ActionParameterImpl)param;
/*     */             org.gudy.azureus2.ui.swt.config.Parameter swt_param;
/* 652 */             if (_param.getStyle() == 1)
/*     */             {
/* 654 */               ButtonParameter bp = new ButtonParameter(current_composite, _param.getActionResource());
/*     */               
/* 656 */               org.gudy.azureus2.ui.swt.config.Parameter swt_param = bp;
/*     */               
/* 658 */               buttons.add(bp.getButton());
/*     */             }
/*     */             else {
/* 661 */               swt_param = new LinkParameter(current_composite, _param.getActionResource());
/*     */             }
/*     */             
/* 664 */             swt_param.addChangeListener(new ParameterChangeAdapter()
/*     */             {
/*     */ 
/*     */               public void parameterChanged(org.gudy.azureus2.ui.swt.config.Parameter p, boolean caused_internally)
/*     */               {
/*     */ 
/*     */                 try
/*     */                 {
/*     */ 
/* 673 */                   param.parameterChanged("");
/*     */                 } catch (Throwable t) {
/* 675 */                   Debug.out(t);
/*     */                 }
/*     */               }
/*     */             }); } else { org.gudy.azureus2.ui.swt.config.Parameter swt_param;
/* 679 */             if ((param instanceof UIParameterImpl)) {
/* 680 */               if ((((UIParameterImpl)param).getContext() instanceof UISWTParameterContext)) {
/* 681 */                 UISWTParameterContext context = (UISWTParameterContext)((UIParameterImpl)param).getContext();
/* 682 */                 Composite internal_composite = new Composite(current_composite, 0);
/* 683 */                 GridData gridData = new GridData(768);
/* 684 */                 Utils.setLayoutData(internal_composite, gridData);
/* 685 */                 boolean initialised_component = true;
/* 686 */                 try { context.create(internal_composite);
/* 687 */                 } catch (Exception e) { Debug.printStackTrace(e);initialised_component = false; }
/*     */                 org.gudy.azureus2.ui.swt.config.Parameter swt_param;
/* 689 */                 if (initialised_component) {
/* 690 */                   swt_param = new UISWTParameter(internal_composite, param.getKey());
/*     */                 }
/*     */                 else {
/* 693 */                   org.gudy.azureus2.ui.swt.config.Parameter swt_param = null;
/*     */                   
/*     */ 
/*     */ 
/* 697 */                   if (label != null) label.setText("Error while generating UI component.");
/*     */                 }
/*     */               }
/*     */               else {
/* 701 */                 swt_param = null;
/*     */               }
/* 703 */             } else if ((param instanceof UITextAreaImpl))
/*     */             {
/* 705 */               org.gudy.azureus2.ui.swt.config.Parameter swt_param = new TextAreaParameter(current_composite, (UITextAreaImpl)param);
/*     */               
/* 707 */               GridData gridData = new GridData(768);
/* 708 */               gridData.horizontalSpan = 2;
/* 709 */               gridData.heightHint = 100;
/* 710 */               swt_param.setLayoutData(gridData);
/*     */ 
/*     */             }
/*     */             else
/*     */             {
/*     */ 
/* 716 */               GridData gridData = new GridData(768);
/* 717 */               gridData.horizontalSpan = 2;
/*     */               
/*     */ 
/* 720 */               gridData.widthHint = 300;
/*     */               
/* 722 */               Utils.setLayoutData(label, gridData);
/*     */               
/* 724 */               swt_param = null;
/*     */             }
/*     */           } }
/* 727 */         if (swt_param == null)
/*     */         {
/* 729 */           if (label == null) {
/* 730 */             comp_map.put(param, new Object[] { null });
/*     */           } else {
/* 732 */             comp_map.put(param, new Object[] { null, label });
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 737 */           Control[] c = swt_param.getControls();
/*     */           
/* 739 */           Object[] moo = new Object[c.length + (label == null ? 1 : 2)];
/*     */           
/* 741 */           int pos = 1;
/*     */           
/* 743 */           moo[0] = swt_param;
/*     */           
/* 745 */           if (label != null) {
/* 746 */             moo[(pos++)] = label;
/*     */           }
/*     */           
/* 749 */           System.arraycopy(c, 0, moo, 0 + pos, c.length);
/*     */           
/* 751 */           comp_map.put(param, moo);
/*     */         }
/*     */       }
/*     */     }
/* 755 */     if (buttons.size() > 1)
/*     */     {
/* 757 */       Utils.makeButtonsEqualWidth(buttons);
/*     */     }
/*     */     
/*     */ 
/* 761 */     ParameterImplListener parameterImplListener = new ParameterImplListener()
/*     */     {
/*     */       public void enabledChanged(final ParameterImpl p) {
/* 764 */         final Object[] stuff = (Object[])comp_map.get(p);
/*     */         
/* 766 */         if (stuff != null)
/*     */         {
/* 768 */           if ((stuff[1] != null) && (((Control)stuff[1]).isDisposed()))
/*     */           {
/* 770 */             p.removeImplListener(this);
/*     */           }
/*     */           else
/*     */           {
/* 774 */             Utils.execSWTThread(new AERunnable()
/*     */             {
/*     */               public void runSupport() {
/* 777 */                 for (int k = 1; k < stuff.length; k++) {
/* 778 */                   if ((stuff[k] instanceof Control)) {
/* 779 */                     ((Control)stuff[k]).setEnabled(p.isEnabled());
/*     */                   }
/*     */                 }
/*     */               }
/*     */             });
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void labelChanged(ParameterImpl p, final String text, final boolean bIsKey)
/*     */       {
/* 792 */         Object[] stuff = (Object[])comp_map.get(p);
/*     */         
/* 794 */         if (stuff == null) {
/*     */           return;
/*     */         }
/*     */         Label lbl;
/* 798 */         if ((stuff[1] instanceof Label)) {
/* 799 */           lbl = (Label)stuff[1]; } else { Label lbl;
/* 800 */           if ((stuff[0] instanceof Label))
/* 801 */             lbl = (Label)stuff[0]; else {
/*     */             return;
/*     */           }
/*     */         }
/*     */         Label lbl;
/* 806 */         if (lbl.isDisposed())
/*     */         {
/* 808 */           p.removeImplListener(this);
/*     */         }
/*     */         else {
/* 811 */           final Label finalLabel = lbl;
/*     */           
/* 813 */           Utils.execSWTThread(new AERunnable() {
/*     */             public void runSupport() {
/* 815 */               if (bIsKey) {
/* 816 */                 Messages.setLanguageText(finalLabel, text);
/*     */               } else {
/* 818 */                 finalLabel.setData("");
/* 819 */                 finalLabel.setText(text);
/*     */               }
/* 821 */               finalLabel.getParent().layout(true);
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/*     */     };
/*     */     
/* 828 */     for (int i = 0; i < parameters.length; i++)
/*     */     {
/* 830 */       ParameterImpl param = (ParameterImpl)parameters[i];
/*     */       
/* 832 */       param.addImplListener(parameterImplListener);
/*     */       
/* 834 */       if (!param.isEnabled())
/*     */       {
/* 836 */         Object[] stuff = (Object[])comp_map.get(param);
/*     */         
/* 838 */         if (stuff != null)
/*     */         {
/* 840 */           for (int k = 1; k < stuff.length; k++)
/*     */           {
/* 842 */             ((Control)stuff[k]).setEnabled(false);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 847 */       if (!param.isVisible())
/*     */       {
/* 849 */         Object[] stuff = (Object[])comp_map.get(param);
/*     */         
/* 851 */         if (stuff != null)
/*     */         {
/* 853 */           for (int k = 1; k < stuff.length; k++)
/*     */           {
/* 855 */             Control con = (Control)stuff[k];
/*     */             
/* 857 */             con.setVisible(false);
/*     */             
/* 859 */             con.setSize(0, 0);
/*     */             
/* 861 */             GridData gridData = new GridData();
/*     */             
/* 863 */             gridData.heightHint = 0;
/* 864 */             gridData.verticalSpan = 0;
/* 865 */             gridData.grabExcessVerticalSpace = false;
/*     */             
/* 867 */             con.setLayoutData(gridData);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 872 */       if ((param instanceof EnablerParameter))
/*     */       {
/* 874 */         List controlsToEnable = new ArrayList();
/*     */         
/* 876 */         Iterator iter = param.getEnabledOnSelectionParameters().iterator();
/*     */         
/* 878 */         while (iter.hasNext())
/*     */         {
/* 880 */           ParameterImpl enable_param = (ParameterImpl)iter.next();
/*     */           
/* 882 */           Object[] stuff = (Object[])comp_map.get(enable_param);
/*     */           
/* 884 */           if (stuff != null)
/*     */           {
/* 886 */             controlsToEnable.addAll(Arrays.asList(stuff).subList(1, stuff.length));
/*     */           }
/*     */         }
/*     */         
/* 890 */         List controlsToDisable = new ArrayList();
/*     */         
/* 892 */         iter = param.getDisabledOnSelectionParameters().iterator();
/*     */         
/* 894 */         while (iter.hasNext())
/*     */         {
/* 896 */           ParameterImpl disable_param = (ParameterImpl)iter.next();
/*     */           
/* 898 */           Object[] stuff = (Object[])comp_map.get(disable_param);
/*     */           
/* 900 */           if (stuff != null)
/*     */           {
/* 902 */             controlsToDisable.addAll(Arrays.asList(stuff).subList(1, stuff.length));
/*     */           }
/*     */         }
/*     */         
/* 906 */         Control[] ce = new Control[controlsToEnable.size()];
/* 907 */         Control[] cd = new Control[controlsToDisable.size()];
/*     */         
/* 909 */         if (ce.length + cd.length > 0)
/*     */         {
/* 911 */           IAdditionalActionPerformer ap = new DualChangeSelectionActionPerformer((Control[])controlsToEnable.toArray(ce), (Control[])controlsToDisable.toArray(cd));
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 917 */           Object[] data = (Object[])comp_map.get(param);
/*     */           
/*     */ 
/*     */ 
/* 921 */           if (data != null)
/*     */           {
/* 923 */             BooleanParameter target = (BooleanParameter)data[0];
/*     */             
/* 925 */             target.setAdditionalActionPerformer(ap);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 931 */     return main_tab;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/pluginsimpl/BasicPluginConfigImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */