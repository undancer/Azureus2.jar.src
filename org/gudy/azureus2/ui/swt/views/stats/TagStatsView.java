/*     */ package org.gudy.azureus2.ui.swt.views.stats;
/*     */ 
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagFeatureRateLimit;
/*     */ import com.aelitis.azureus.core.tag.TagManager;
/*     */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*     */ import com.aelitis.azureus.core.tag.TagManagerListener;
/*     */ import com.aelitis.azureus.core.tag.TagType;
/*     */ import com.aelitis.azureus.core.tag.TagTypeAdapter;
/*     */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.custom.ScrolledComposite;
/*     */ import org.eclipse.swt.events.ControlAdapter;
/*     */ import org.eclipse.swt.events.ControlEvent;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.Legend;
/*     */ import org.gudy.azureus2.ui.swt.components.Legend.LegendListener;
/*     */ import org.gudy.azureus2.ui.swt.components.graphics.MultiPlotGraphic;
/*     */ import org.gudy.azureus2.ui.swt.components.graphics.ValueFormater;
/*     */ import org.gudy.azureus2.ui.swt.components.graphics.ValueSource;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
/*     */ import org.gudy.azureus2.ui.swt.views.utils.TagUIUtils;
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
/*     */ 
/*     */ 
/*     */ public class TagStatsView
/*     */   extends TagTypeAdapter
/*     */   implements UISWTViewCoreEventListener, TagManagerListener
/*     */ {
/*     */   public static final String MSGID_PREFIX = "TagStatsView";
/*     */   private Composite panel;
/*     */   private Group legend_panel;
/*     */   private ScrolledComposite legend_panel_sc;
/*     */   private Composite speed_panel;
/*     */   private UISWTView swtView;
/*     */   private MultiPlotGraphic mpg;
/*     */   
/*     */   public void periodicUpdate() {}
/*     */   
/*     */   private void initialize(Composite composite)
/*     */   {
/*  91 */     this.panel = new Composite(composite, 0);
/*  92 */     this.panel.setLayout(new GridLayout(2, false));
/*     */     
/*  94 */     this.legend_panel_sc = new ScrolledComposite(this.panel, 512);
/*  95 */     this.legend_panel_sc.setExpandHorizontal(true);
/*  96 */     this.legend_panel_sc.setExpandVertical(true);
/*  97 */     GridLayout layout = new GridLayout();
/*  98 */     layout.horizontalSpacing = 0;
/*  99 */     layout.verticalSpacing = 0;
/* 100 */     layout.marginHeight = 0;
/* 101 */     layout.marginWidth = 0;
/* 102 */     this.legend_panel_sc.setLayout(layout);
/* 103 */     GridData gridData = new GridData(1040);
/* 104 */     this.legend_panel_sc.setLayoutData(gridData);
/*     */     
/* 106 */     this.legend_panel = new Group(this.legend_panel_sc, 0);
/* 107 */     this.legend_panel.setText(MessageText.getString("label.tags"));
/*     */     
/* 109 */     this.legend_panel.setLayout(new GridLayout());
/*     */     
/* 111 */     this.legend_panel_sc.setContent(this.legend_panel);
/* 112 */     this.legend_panel_sc.addControlListener(new ControlAdapter() {
/*     */       public void controlResized(ControlEvent e) {
/* 114 */         TagStatsView.this.legend_panel_sc.setMinSize(TagStatsView.this.legend_panel.computeSize(-1, -1));
/*     */       }
/*     */       
/* 117 */     });
/* 118 */     this.speed_panel = new Composite(this.panel, 0);
/* 119 */     this.speed_panel.setLayout(new GridLayout());
/* 120 */     gridData = new GridData(1808);
/* 121 */     this.speed_panel.setLayoutData(gridData);
/*     */     
/* 123 */     build();
/*     */     
/* 125 */     TagManager tm = TagManagerFactory.getTagManager();
/*     */     
/* 127 */     tm.addTagManagerListener(this, false);
/*     */     
/* 129 */     for (TagType tt : tm.getTagTypes())
/*     */     {
/* 131 */       tt.addTagTypeListener(this, false);
/*     */     }
/*     */     
/* 134 */     this.panel.addListener(26, new Listener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/*     */ 
/*     */ 
/* 142 */         TagStatsView.this.refresh(true);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   private void build()
/*     */   {
/* 150 */     if ((this.legend_panel == null) || (this.legend_panel.isDisposed()))
/*     */     {
/* 152 */       return;
/*     */     }
/*     */     
/* 155 */     for (Control c : this.legend_panel.getChildren())
/*     */     {
/* 157 */       c.dispose();
/*     */     }
/*     */     
/* 160 */     List<String> configs = new ArrayList();
/* 161 */     List<String> texts = new ArrayList();
/* 162 */     List<Color> colors = new ArrayList();
/*     */     
/* 164 */     TagManager tm = TagManagerFactory.getTagManager();
/*     */     
/* 166 */     List<TagType> tag_types = tm.getTagTypes();
/*     */     
/* 168 */     tag_types = TagUIUtils.sortTagTypes(tag_types);
/*     */     
/* 170 */     List<TagFeatureRateLimit> visible_tags = new ArrayList();
/*     */     
/* 172 */     for (Iterator i$ = tag_types.iterator(); i$.hasNext();) { tag_type = (TagType)i$.next();
/*     */       
/* 174 */       if (tag_type.hasTagTypeFeature(1L))
/*     */       {
/* 176 */         List<Tag> tags = tag_type.getTags();
/*     */         
/* 178 */         tags = TagUIUtils.sortTags(tags);
/*     */         
/* 180 */         for (Tag tag : tags)
/*     */         {
/* 182 */           if (tag.isVisible())
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 187 */             TagFeatureRateLimit rl = (TagFeatureRateLimit)tag;
/*     */             
/* 189 */             if (rl.supportsTagRates())
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/* 194 */               String config_key = "TagStatsView.cc." + tag_type.getTagType() + "." + tag.getTagID();
/*     */               
/* 196 */               configs.add(config_key);
/*     */               
/* 198 */               texts.add(tag.getTagName(true));
/*     */               
/*     */ 
/*     */ 
/* 202 */               int[] rgb = tag.getColor();
/*     */               Color tt_colour;
/* 204 */               Color tt_colour; if (rgb == null)
/*     */               {
/* 206 */                 tt_colour = org.gudy.azureus2.ui.swt.mainwindow.Colors.blues[9];
/*     */               }
/*     */               else
/*     */               {
/* 210 */                 tt_colour = ColorCache.getColor(this.legend_panel.getDisplay(), rgb);
/*     */               }
/*     */               
/* 213 */               colors.add(tt_colour);
/*     */               
/* 215 */               visible_tags.add(rl);
/*     */             }
/*     */           } }
/*     */       }
/*     */     }
/*     */     TagType tag_type;
/* 221 */     Color[] color_array = (Color[])colors.toArray(new Color[colors.size()]);
/* 222 */     String[] text_array = (String[])texts.toArray(new String[texts.size()]);
/*     */     
/* 224 */     final List<ValueSourceImpl> sources = new ArrayList();
/*     */     
/* 226 */     List<int[]> history_records = new ArrayList();
/* 227 */     int history_record_max = 0;
/*     */     
/* 229 */     for (int i = 0; i < visible_tags.size(); i++)
/*     */     {
/* 231 */       TagFeatureRateLimit tag = (TagFeatureRateLimit)visible_tags.get(i);
/*     */       
/* 233 */       tag.setRecentHistoryRetention(true);
/*     */       
/* 235 */       int[][] history = tag.getRecentHistory();
/*     */       
/* 237 */       history_record_max = Math.max(history[0].length, history_record_max);
/*     */       
/* 239 */       history_records.add(history[0]);
/* 240 */       history_records.add(history[1]);
/*     */       
/* 242 */       sources.add(new ValueSourceImpl(tag, text_array[i], i, color_array, true, null));
/* 243 */       sources.add(new ValueSourceImpl(tag, text_array[i], i, color_array, false, null));
/*     */     }
/*     */     
/* 246 */     ValueFormater formatter = new ValueFormater()
/*     */     {
/*     */ 
/*     */ 
/*     */       public String format(int value)
/*     */       {
/*     */ 
/* 253 */         return DisplayFormatters.formatByteCountToKiBEtcPerSec(value);
/*     */       }
/*     */     };
/*     */     
/*     */ 
/*     */ 
/* 259 */     if (this.mpg != null)
/*     */     {
/* 261 */       this.mpg.dispose();
/*     */     }
/*     */     
/* 264 */     final MultiPlotGraphic f_mpg = this.mpg = MultiPlotGraphic.getInstance((ValueSource[])sources.toArray(new ValueSource[sources.size()]), formatter);
/*     */     
/* 266 */     int[][] history = new int[history_records.size()][];
/*     */     
/* 268 */     for (int i = 0; i < history.length; i++) {
/* 269 */       int[] hist = (int[])history_records.get(i);
/* 270 */       int hist_len = hist.length;
/*     */       
/* 272 */       if (hist_len == history_record_max) {
/* 273 */         history[i] = hist;
/*     */       } else {
/* 275 */         int[] temp = new int[history_record_max];
/* 276 */         System.arraycopy(hist, 0, temp, history_record_max - hist_len, hist_len);
/* 277 */         history[i] = temp;
/*     */       }
/*     */     }
/*     */     
/* 281 */     this.mpg.reset(history);
/*     */     
/*     */ 
/*     */ 
/* 285 */     if (color_array.length > 0)
/*     */     {
/* 287 */       GridData gridData = new GridData(1040);
/* 288 */       gridData.verticalAlignment = 16777216;
/*     */       
/* 290 */       Legend.createLegendComposite(this.legend_panel, color_array, (String[])configs.toArray(new String[configs.size()]), text_array, gridData, false, new Legend.LegendListener()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 299 */         private int hover_index = -1;
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public void hoverChange(boolean entry, int index)
/*     */         {
/* 306 */           if (this.hover_index != -1)
/*     */           {
/* 308 */             for (int i = this.hover_index * 2; i < this.hover_index * 2 + 2; i++)
/*     */             {
/* 310 */               TagStatsView.ValueSourceImpl.access$400((TagStatsView.ValueSourceImpl)sources.get(i), false);
/*     */             }
/*     */           }
/*     */           
/* 314 */           if (entry)
/*     */           {
/* 316 */             this.hover_index = index;
/*     */             
/* 318 */             for (int i = this.hover_index * 2; i < this.hover_index * 2 + 2; i++)
/*     */             {
/* 320 */               TagStatsView.ValueSourceImpl.access$400((TagStatsView.ValueSourceImpl)sources.get(i), true);
/*     */             }
/*     */           }
/*     */           
/* 324 */           f_mpg.refresh(true);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public void visibilityChange(boolean visible, int index)
/*     */         {
/* 332 */           for (int i = index * 2; i < index * 2 + 2; i++)
/*     */           {
/* 334 */             TagStatsView.ValueSourceImpl.access$500((TagStatsView.ValueSourceImpl)sources.get(i), visible);
/*     */           }
/*     */           
/* 337 */           f_mpg.refresh(true);
/*     */         }
/*     */       });
/*     */     }
/*     */     else {
/* 342 */       gridData = new GridData(768);
/* 343 */       gridData.verticalAlignment = 128;
/*     */       
/* 345 */       Label lab = new Label(this.legend_panel, 0);
/* 346 */       lab.setText(MessageText.getString("tag.stats.none.defined"));
/*     */       
/* 348 */       lab.setLayoutData(gridData);
/*     */     }
/*     */     
/* 351 */     this.legend_panel_sc.setMinSize(this.legend_panel.computeSize(-1, -1));
/*     */     
/*     */ 
/*     */ 
/* 355 */     for (Control c : this.speed_panel.getChildren())
/*     */     {
/* 357 */       c.dispose();
/*     */     }
/*     */     
/* 360 */     Canvas speed_canvas = new Canvas(this.speed_panel, 262144);
/* 361 */     GridData gridData = new GridData(1808);
/* 362 */     speed_canvas.setLayoutData(gridData);
/*     */     
/*     */ 
/* 365 */     this.mpg.initialize(speed_canvas, true);
/*     */     
/* 367 */     this.panel.layout(true, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void rebuild()
/*     */   {
/* 374 */     Utils.execSWTThread(new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */ 
/* 380 */         TagStatsView.this.build();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void rebuild(TagType tag_type, boolean added)
/*     */   {
/* 390 */     if ((this.panel == null) || (this.panel.isDisposed()))
/*     */     {
/* 392 */       tag_type.getTagManager().removeTagManagerListener(this);
/*     */       
/* 394 */       return;
/*     */     }
/*     */     
/* 397 */     if (added)
/*     */     {
/* 399 */       tag_type.addTagTypeListener(this, false);
/*     */     }
/*     */     
/* 402 */     rebuild();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void tagTypeAdded(TagManager manager, TagType tag_type)
/*     */   {
/* 410 */     rebuild(tag_type, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void tagTypeRemoved(TagManager manager, TagType tag_type)
/*     */   {
/* 418 */     rebuild(tag_type, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void rebuild(Tag tag)
/*     */   {
/* 425 */     if ((this.panel == null) || (this.panel.isDisposed()))
/*     */     {
/* 427 */       TagType tt = tag.getTagType();
/*     */       
/* 429 */       tt.removeTagTypeListener(this);
/*     */       
/* 431 */       tt.getTagManager().removeTagManagerListener(this);
/*     */       
/* 433 */       return;
/*     */     }
/*     */     
/* 436 */     rebuild();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void tagAdded(Tag tag)
/*     */   {
/* 443 */     rebuild(tag);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void tagRemoved(Tag tag)
/*     */   {
/* 450 */     rebuild(tag);
/*     */   }
/*     */   
/*     */ 
/*     */   private void delete()
/*     */   {
/* 456 */     Utils.disposeComposite(this.panel);
/*     */     
/* 458 */     TagManager tm = TagManagerFactory.getTagManager();
/*     */     
/* 460 */     tm.removeTagManagerListener(this);
/*     */     
/* 462 */     for (TagType tt : tm.getTagTypes())
/*     */     {
/* 464 */       tt.removeTagTypeListener(this);
/*     */     }
/*     */     
/* 467 */     if (this.mpg != null)
/*     */     {
/* 469 */       this.mpg.dispose();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void refresh(boolean force)
/*     */   {
/* 479 */     if (this.mpg != null)
/*     */     {
/* 481 */       this.mpg.refresh(force);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean eventOccurred(UISWTViewEvent event)
/*     */   {
/* 489 */     switch (event.getType()) {
/*     */     case 0: 
/* 491 */       this.swtView = event.getView();
/* 492 */       this.swtView.setTitle(MessageText.getString("TagStatsView.title.full"));
/* 493 */       break;
/*     */     
/*     */     case 7: 
/* 496 */       delete();
/* 497 */       break;
/*     */     
/*     */     case 2: 
/* 500 */       initialize((Composite)event.getData());
/* 501 */       break;
/*     */     
/*     */     case 6: 
/* 504 */       Messages.updateLanguageForControl(this.panel);
/* 505 */       break;
/*     */     
/*     */     case 1: 
/*     */       break;
/*     */     
/*     */     case 3: 
/* 511 */       refresh(true);
/* 512 */       break;
/*     */     
/*     */     case 5: 
/* 515 */       refresh(false);
/* 516 */       break;
/*     */     
/*     */     case 256: 
/* 519 */       periodicUpdate();
/*     */     }
/*     */     
/*     */     
/* 523 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   private static class ValueSourceImpl
/*     */     implements ValueSource
/*     */   {
/*     */     TagFeatureRateLimit tag;
/*     */     
/*     */     String name;
/*     */     
/*     */     int index;
/*     */     
/*     */     Color[] colours;
/*     */     
/*     */     boolean is_up;
/*     */     
/*     */     private boolean is_hover;
/*     */     
/*     */     private boolean is_invisible;
/*     */     
/*     */ 
/*     */     private ValueSourceImpl(TagFeatureRateLimit _tag, String _name, int _index, Color[] _colours, boolean _is_up)
/*     */     {
/* 547 */       this.tag = _tag;
/* 548 */       this.name = _name;
/* 549 */       this.index = _index;
/* 550 */       this.colours = _colours;
/* 551 */       this.is_up = _is_up;
/*     */     }
/*     */     
/*     */ 
/*     */     public String getName()
/*     */     {
/* 557 */       return this.name;
/*     */     }
/*     */     
/*     */ 
/*     */     public Color getLineColor()
/*     */     {
/* 563 */       return this.colours[this.index];
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isTrimmable()
/*     */     {
/* 569 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private void setHover(boolean h)
/*     */     {
/* 576 */       this.is_hover = h;
/*     */     }
/*     */     
/*     */ 
/*     */     public int getAlpha()
/*     */     {
/* 582 */       return 255;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private void setVisible(boolean visible)
/*     */     {
/* 589 */       this.is_invisible = (!visible);
/*     */     }
/*     */     
/*     */ 
/*     */     public int getStyle()
/*     */     {
/* 595 */       if (this.is_invisible)
/*     */       {
/* 597 */         return 16;
/*     */       }
/*     */       
/* 600 */       int style = this.is_up ? 1 : 2;
/*     */       
/* 602 */       if (this.is_hover)
/*     */       {
/* 604 */         style |= 0x8;
/*     */       }
/*     */       
/* 607 */       return style;
/*     */     }
/*     */     
/*     */ 
/*     */     public int getValue()
/*     */     {
/* 613 */       int rate = this.is_up ? this.tag.getTagCurrentUploadRate() : this.tag.getTagCurrentDownloadRate();
/*     */       
/* 615 */       if (rate < 0)
/*     */       {
/* 617 */         rate = 0;
/*     */       }
/*     */       
/* 620 */       return rate;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/stats/TagStatsView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */