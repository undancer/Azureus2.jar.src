/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ChangeSelectionActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.IntParameter;
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
/*     */ public class ConfigSectionFilePerformance
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  41 */     return "files";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String configSectionGetName()
/*     */   {
/*  48 */     return "file.perf";
/*     */   }
/*     */   
/*     */ 
/*     */   public void configSectionSave() {}
/*     */   
/*     */   public void configSectionDelete() {}
/*     */   
/*     */   public int maxUserMode()
/*     */   {
/*  58 */     return 2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/*  67 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*     */     
/*  69 */     Composite cSection = new Composite(parent, 0);
/*  70 */     GridLayout layout = new GridLayout();
/*  71 */     layout.numColumns = 3;
/*  72 */     cSection.setLayout(layout);
/*  73 */     GridData gridData = new GridData(272);
/*  74 */     gridData.horizontalSpan = 2;
/*  75 */     Utils.setLayoutData(cSection, gridData);
/*     */     
/*  77 */     Label label = new Label(cSection, 64);
/*  78 */     Messages.setLanguageText(label, "ConfigView.section.file.perf.explain");
/*  79 */     gridData = new GridData(768);
/*  80 */     gridData.horizontalSpan = 3;
/*  81 */     Utils.setLayoutData(label, gridData);
/*     */     
/*     */ 
/*  84 */     BooleanParameter friendly_hashchecking = new BooleanParameter(cSection, "diskmanager.friendly.hashchecking", "ConfigView.section.file.friendly.hashchecking");
/*  85 */     gridData = new GridData();
/*  86 */     gridData.horizontalSpan = 3;
/*  87 */     friendly_hashchecking.setLayoutData(gridData);
/*     */     
/*     */ 
/*  90 */     BooleanParameter check_smallest = new BooleanParameter(cSection, "diskmanager.hashchecking.smallestfirst", "ConfigView.section.file.hashchecking.smallestfirst");
/*  91 */     gridData = new GridData();
/*  92 */     gridData.horizontalSpan = 3;
/*  93 */     check_smallest.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 102 */     BooleanParameter disk_cache = new BooleanParameter(cSection, "diskmanager.perf.cache.enable", "ConfigView.section.file.perf.cache.enable");
/* 103 */     gridData = new GridData();
/* 104 */     gridData.horizontalSpan = 3;
/* 105 */     disk_cache.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 109 */     long max_mem_bytes = Runtime.getRuntime().maxMemory();
/* 110 */     long mb_1 = 1048576L;
/* 111 */     long mb_32 = 32L * mb_1;
/*     */     
/* 113 */     Label cache_size_label = new Label(cSection, 0);
/* 114 */     gridData = new GridData(2);
/* 115 */     cache_size_label.setLayoutData(gridData);
/*     */     
/*     */ 
/* 118 */     Messages.setLanguageText(cache_size_label, "ConfigView.section.file.perf.cache.size", new String[] { DisplayFormatters.getUnitBase10(2) });
/*     */     
/*     */ 
/*     */ 
/* 122 */     IntParameter cache_size = new IntParameter(cSection, "diskmanager.perf.cache.size", 1, COConfigurationManager.CONFIG_CACHE_SIZE_MAX_MB);
/*     */     
/*     */ 
/* 125 */     gridData = new GridData(2);
/* 126 */     cache_size.setLayoutData(gridData);
/*     */     
/*     */ 
/* 129 */     Label cache_explain_label = new Label(cSection, 64);
/* 130 */     gridData = new GridData(770);
/* 131 */     gridData.widthHint = 300;
/* 132 */     Utils.setLayoutData(cache_explain_label, gridData);
/* 133 */     Messages.setLanguageText(cache_explain_label, "ConfigView.section.file.perf.cache.size.explain", new String[] { DisplayFormatters.formatByteCountToKiBEtc(mb_32), DisplayFormatters.formatByteCountToKiBEtc(max_mem_bytes), "http://wiki.vuze.com/w/" });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 142 */     if (userMode > 0)
/*     */     {
/*     */ 
/*     */ 
/* 146 */       Label cnst_label = new Label(cSection, 0);
/* 147 */       gridData = new GridData(2);
/* 148 */       Utils.setLayoutData(cnst_label, gridData);
/*     */       
/*     */ 
/* 151 */       Messages.setLanguageText(cnst_label, "ConfigView.section.file.perf.cache.notsmallerthan", new String[] { DisplayFormatters.getUnitBase10(1) });
/*     */       
/*     */ 
/* 154 */       IntParameter cache_not_smaller_than = new IntParameter(cSection, "diskmanager.perf.cache.notsmallerthan");
/* 155 */       cache_not_smaller_than.setMinimumValue(0);
/* 156 */       gridData = new GridData(2);
/* 157 */       cache_not_smaller_than.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 162 */       BooleanParameter disk_cache_read = new BooleanParameter(cSection, "diskmanager.perf.cache.enable.read", "ConfigView.section.file.perf.cache.enable.read");
/* 163 */       gridData = new GridData();
/* 164 */       gridData.horizontalSpan = 3;
/* 165 */       disk_cache_read.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/* 169 */       BooleanParameter disk_cache_write = new BooleanParameter(cSection, "diskmanager.perf.cache.enable.write", "ConfigView.section.file.perf.cache.enable.write");
/* 170 */       gridData = new GridData();
/* 171 */       gridData.horizontalSpan = 3;
/* 172 */       disk_cache_write.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/* 176 */       BooleanParameter disk_cache_flush = new BooleanParameter(cSection, "diskmanager.perf.cache.flushpieces", "ConfigView.section.file.perf.cache.flushpieces");
/* 177 */       gridData = new GridData();
/* 178 */       gridData.horizontalSpan = 3;
/* 179 */       disk_cache_flush.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/* 183 */       BooleanParameter disk_cache_trace = new BooleanParameter(cSection, "diskmanager.perf.cache.trace", "ConfigView.section.file.perf.cache.trace");
/* 184 */       gridData = new GridData();
/* 185 */       gridData.horizontalSpan = 3;
/* 186 */       disk_cache_trace.setLayoutData(gridData);
/*     */       
/* 188 */       disk_cache.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(new Control[] { cnst_label }));
/*     */       
/* 190 */       disk_cache.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(cache_not_smaller_than.getControls()));
/*     */       
/* 192 */       disk_cache.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(disk_cache_trace.getControls()));
/*     */       
/* 194 */       disk_cache.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(disk_cache_read.getControls()));
/*     */       
/* 196 */       disk_cache.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(disk_cache_write.getControls()));
/*     */       
/* 198 */       disk_cache.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(disk_cache_flush.getControls()));
/*     */       
/* 200 */       disk_cache.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(disk_cache_trace.getControls()));
/*     */       
/*     */ 
/* 203 */       if (userMode > 1)
/*     */       {
/*     */ 
/*     */ 
/* 207 */         label = new Label(cSection, 0);
/* 208 */         gridData = new GridData(2);
/* 209 */         label.setLayoutData(gridData);
/* 210 */         Messages.setLanguageText(label, "ConfigView.section.file.max_open_files");
/* 211 */         IntParameter file_max_open = new IntParameter(cSection, "File Max Open");
/* 212 */         gridData = new GridData(2);
/* 213 */         file_max_open.setLayoutData(gridData);
/* 214 */         label = new Label(cSection, 64);
/* 215 */         gridData = new GridData(770);
/* 216 */         gridData.widthHint = 300;
/* 217 */         Utils.setLayoutData(label, gridData);
/* 218 */         Messages.setLanguageText(label, "ConfigView.section.file.max_open_files.explain");
/*     */         
/*     */ 
/*     */ 
/* 222 */         label = new Label(cSection, 0);
/* 223 */         gridData = new GridData(2);
/* 224 */         Utils.setLayoutData(label, gridData);
/* 225 */         String label_text = MessageText.getString("ConfigView.section.file.writemblimit", new String[] { DisplayFormatters.getUnitBase10(2) });
/*     */         
/*     */ 
/*     */ 
/* 229 */         label.setText(label_text);
/* 230 */         IntParameter write_block_limit = new IntParameter(cSection, "diskmanager.perf.write.maxmb");
/* 231 */         gridData = new GridData(2);
/* 232 */         write_block_limit.setLayoutData(gridData);
/* 233 */         label = new Label(cSection, 64);
/* 234 */         gridData = new GridData(770);
/* 235 */         gridData.widthHint = 300;
/* 236 */         Utils.setLayoutData(label, gridData);
/* 237 */         Messages.setLanguageText(label, "ConfigView.section.file.writemblimit.explain");
/*     */         
/*     */ 
/*     */ 
/* 241 */         label = new Label(cSection, 0);
/* 242 */         gridData = new GridData(2);
/* 243 */         Utils.setLayoutData(label, gridData);
/* 244 */         label_text = MessageText.getString("ConfigView.section.file.readmblimit", new String[] { DisplayFormatters.getUnitBase10(2) });
/*     */         
/*     */ 
/*     */ 
/* 248 */         label.setText(label_text);
/* 249 */         IntParameter check_piece_limit = new IntParameter(cSection, "diskmanager.perf.read.maxmb");
/* 250 */         gridData = new GridData(2);
/* 251 */         check_piece_limit.setLayoutData(gridData);
/* 252 */         label = new Label(cSection, 64);
/* 253 */         gridData = new GridData(770);
/* 254 */         gridData.widthHint = 300;
/* 255 */         Utils.setLayoutData(label, gridData);
/* 256 */         Messages.setLanguageText(label, "ConfigView.section.file.readmblimit.explain");
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 261 */     disk_cache.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(cache_size.getControls()));
/*     */     
/* 263 */     disk_cache.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(new Control[] { cache_size_label, cache_explain_label }));
/*     */     
/*     */ 
/* 266 */     return cSection;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionFilePerformance.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */