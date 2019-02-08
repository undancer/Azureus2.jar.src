/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.RGB;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.config.ColorParameter;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTConfigSection;
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
/*     */ public class ConfigSectionInterfaceColor
/*     */   implements UISWTConfigSection
/*     */ {
/*  42 */   private static final String[] sColorsToOverride = { "progressBar", "error", "warning", "altRow" };
/*     */   
/*     */ 
/*  45 */   private Color[] colorsToOverride = { Colors.colorProgressBar, Colors.colorError, Colors.colorWarning, Colors.colorAltRow };
/*     */   
/*     */ 
/*  48 */   private Button[] btnColorReset = new Button[sColorsToOverride.length];
/*     */   
/*     */   public String configSectionGetParentSection() {
/*  51 */     return "style";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  55 */     return "color";
/*     */   }
/*     */   
/*     */ 
/*     */   public void configSectionSave() {}
/*     */   
/*     */   public void configSectionDelete() {}
/*     */   
/*     */   public int maxUserMode()
/*     */   {
/*  65 */     return 0;
/*     */   }
/*     */   
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/*  70 */     boolean isAZ3 = COConfigurationManager.getStringParameter("ui").equals("az3");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  75 */     Composite cSection = new Composite(parent, 0);
/*  76 */     cSection.setLayoutData(new GridData(1808));
/*  77 */     GridLayout layout = new GridLayout();
/*  78 */     layout.numColumns = 1;
/*  79 */     cSection.setLayout(layout);
/*     */     
/*  81 */     Composite cArea = new Composite(cSection, 0);
/*  82 */     layout = new GridLayout();
/*  83 */     layout.marginHeight = 0;
/*  84 */     layout.marginWidth = 0;
/*  85 */     layout.numColumns = 3;
/*  86 */     cArea.setLayout(layout);
/*  87 */     cArea.setLayoutData(new GridData());
/*     */     
/*  89 */     Label label = new Label(cArea, 0);
/*  90 */     Messages.setLanguageText(label, "ConfigView.section.color");
/*  91 */     ColorParameter colorScheme = new ColorParameter(cArea, "Color Scheme", 0, 128, 255);
/*     */     
/*  93 */     GridData gridData = new GridData();
/*  94 */     gridData.widthHint = 50;
/*  95 */     colorScheme.setLayoutData(gridData);
/*     */     
/*  97 */     label = new Label(cArea, 0);
/*     */     
/*  99 */     if (isAZ3) {
/* 100 */       Messages.setLanguageText(label, "restart.required.for.some");
/*     */     }
/*     */     
/* 103 */     Group cColorOverride = new Group(cArea, 0);
/* 104 */     Messages.setLanguageText(cColorOverride, "ConfigView.section.style.colorOverrides");
/*     */     
/* 106 */     layout = new GridLayout();
/* 107 */     layout.numColumns = 3;
/* 108 */     cColorOverride.setLayout(layout);
/* 109 */     gridData = new GridData(256);
/* 110 */     gridData.horizontalSpan = 3;
/* 111 */     cColorOverride.setLayoutData(gridData);
/*     */     
/* 113 */     for (int i = 0; i < sColorsToOverride.length; i++) {
/* 114 */       if ((!Utils.TABLE_GRIDLINE_IS_ALTERNATING_COLOR) || (!sColorsToOverride[i].equals("altRow")))
/*     */       {
/*     */ 
/* 117 */         String sConfigID = "Colors." + sColorsToOverride[i];
/* 118 */         label = new Label(cColorOverride, 0);
/* 119 */         Messages.setLanguageText(label, "ConfigView.section.style.colorOverride." + sColorsToOverride[i]);
/*     */         
/* 121 */         ColorParameter colorParm = new ColorParameter(cColorOverride, sConfigID, this.colorsToOverride[i].getRed(), this.colorsToOverride[i].getGreen(), this.colorsToOverride[i].getBlue())
/*     */         {
/*     */           public void newColorChosen(RGB newColor)
/*     */           {
/* 125 */             COConfigurationManager.setParameter(this.sParamName + ".override", true);
/* 126 */             for (int i = 0; i < ConfigSectionInterfaceColor.sColorsToOverride.length; i++) {
/* 127 */               if (this.sParamName.equals("Colors." + ConfigSectionInterfaceColor.sColorsToOverride[i])) {
/* 128 */                 ConfigSectionInterfaceColor.this.btnColorReset[i].setEnabled(true);
/* 129 */                 break;
/*     */               }
/*     */             }
/*     */           }
/* 133 */         };
/* 134 */         gridData = new GridData();
/* 135 */         gridData.widthHint = 50;
/* 136 */         colorParm.setLayoutData(gridData);
/* 137 */         this.btnColorReset[i] = new Button(cColorOverride, 8);
/* 138 */         Messages.setLanguageText(this.btnColorReset[i], "ConfigView.section.style.colorOverrides.reset");
/*     */         
/* 140 */         this.btnColorReset[i].setEnabled(COConfigurationManager.getBooleanParameter(sConfigID + ".override", false));
/*     */         
/* 142 */         this.btnColorReset[i].setData("ColorName", sConfigID);
/* 143 */         this.btnColorReset[i].addListener(13, new Listener() {
/*     */           public void handleEvent(Event event) {
/* 145 */             Button btn = (Button)event.widget;
/* 146 */             String sName = (String)btn.getData("ColorName");
/* 147 */             if (sName != null) {
/* 148 */               COConfigurationManager.setParameter(sName + ".override", false);
/* 149 */               btn.setEnabled(false);
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/* 155 */     if (isAZ3)
/*     */     {
/* 157 */       String[][] override_keys_blocks = { { "config.skin.color.sidebar.bg" }, { "config.skin.color.library.header" } };
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 162 */       for (int i = 0; i < override_keys_blocks.length; i++)
/*     */       {
/* 164 */         if (i == 1)
/*     */         {
/* 166 */           label = new Label(cColorOverride, 0);
/* 167 */           gridData = new GridData(768);
/* 168 */           gridData.horizontalSpan = 3;
/* 169 */           label.setLayoutData(gridData);
/*     */           
/* 171 */           label = new Label(cColorOverride, 0);
/* 172 */           Messages.setLanguageText(label, "restart.required.for.following");
/* 173 */           gridData = new GridData(768);
/* 174 */           gridData.horizontalSpan = 3;
/* 175 */           label.setLayoutData(gridData);
/*     */         }
/*     */         
/* 178 */         String[] override_keys = override_keys_blocks[i];
/*     */         
/* 180 */         for (final String key : override_keys)
/*     */         {
/* 182 */           label = new Label(cColorOverride, 0);
/* 183 */           Messages.setLanguageText(label, key);
/* 184 */           gridData = new GridData(768);
/* 185 */           label.setLayoutData(gridData);
/*     */           
/* 187 */           Color existing = null;
/*     */           
/* 189 */           boolean is_override = COConfigurationManager.getStringParameter(key, "").length() > 0;
/*     */           
/* 191 */           if (is_override)
/*     */           {
/* 193 */             existing = ColorCache.getSchemedColor(parent.getDisplay(), key);
/*     */           }
/*     */           
/* 196 */           final Button[] f_reset = { null };
/*     */           
/* 198 */           final ColorParameter colorParm = new ColorParameter(cColorOverride, null, existing == null ? -1 : existing.getRed(), existing == null ? -1 : existing.getGreen(), existing == null ? -1 : existing.getBlue())
/*     */           {
/*     */ 
/*     */             public void newColorChosen(RGB newColor)
/*     */             {
/* 203 */               COConfigurationManager.setParameter(key, newColor.red + "," + newColor.green + "," + newColor.blue);
/* 204 */               f_reset[0].setEnabled(true);
/*     */             }
/*     */             
/* 207 */           };
/* 208 */           gridData = new GridData();
/* 209 */           gridData.widthHint = 50;
/* 210 */           colorParm.setLayoutData(gridData);
/*     */           
/* 212 */           final Button reset = f_reset[0] = new Button(cColorOverride, 8);
/* 213 */           Messages.setLanguageText(reset, "ConfigView.section.style.colorOverrides.reset");
/* 214 */           reset.setEnabled(is_override);
/* 215 */           reset.addListener(13, new Listener() {
/*     */             public void handleEvent(Event event) {
/* 217 */               reset.setEnabled(false);
/* 218 */               colorParm.setColor(-1, -1, -1);
/* 219 */               COConfigurationManager.removeParameter(key);
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 226 */     return cSection;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionInterfaceColor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */