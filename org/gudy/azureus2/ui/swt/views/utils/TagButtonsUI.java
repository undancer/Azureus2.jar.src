/*     */ package org.gudy.azureus2.ui.swt.views.utils;
/*     */ 
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.Taggable;
/*     */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.events.MenuEvent;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.layout.RowLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.ui.swt.MenuBuildUtils;
/*     */ import org.gudy.azureus2.ui.swt.MenuBuildUtils.MenuBuilder;
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
/*     */ public class TagButtonsUI
/*     */   implements PaintListener
/*     */ {
/*     */   private ArrayList<Button> buttons;
/*     */   private Composite cMainComposite;
/*     */   
/*     */   public void paintControl(PaintEvent e)
/*     */   {
/*  56 */     Composite c = null;
/*  57 */     Button button; Button button; if ((e.widget instanceof Composite)) {
/*  58 */       c = (Composite)e.widget;
/*  59 */       button = (Button)c.getChildren()[0];
/*     */     } else {
/*  61 */       button = (Button)e.widget;
/*     */     }
/*  63 */     Tag tag = (Tag)button.getData("Tag");
/*  64 */     if (tag == null) {
/*  65 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  70 */     if (c != null) {
/*  71 */       boolean checked = button.getSelection();
/*  72 */       Point size = c.getSize();
/*  73 */       Point sizeButton = button.getSize();
/*  74 */       e.gc.setAntialias(1);
/*  75 */       e.gc.setForeground(ColorCache.getColor(e.display, tag.getColor()));
/*  76 */       int lineWidth = button.getSelection() ? 2 : 1;
/*  77 */       e.gc.setLineWidth(lineWidth);
/*     */       
/*  79 */       int curve = 20;
/*  80 */       int width = sizeButton.x + lineWidth + 1;
/*  81 */       width += (Constants.isOSX ? 5 : curve / 2);
/*  82 */       if (checked) {
/*  83 */         e.gc.setAlpha(32);
/*  84 */         e.gc.setBackground(ColorCache.getColor(e.display, tag.getColor()));
/*  85 */         e.gc.fillRoundRectangle(-curve, lineWidth - 1, width + curve, size.y - lineWidth, curve, curve);
/*  86 */         e.gc.setAlpha(255);
/*     */       }
/*  88 */       if (!checked) {
/*  89 */         e.gc.setAlpha(128);
/*     */       }
/*  91 */       e.gc.drawRoundRectangle(-curve, lineWidth - 1, width + curve, size.y - lineWidth, curve, curve);
/*  92 */       e.gc.drawLine(lineWidth - 1, lineWidth, lineWidth - 1, size.y - lineWidth);
/*     */     }
/*  94 */     else if ((!Constants.isOSX) && (button.getSelection())) {
/*  95 */       Point size = button.getSize();
/*  96 */       e.gc.setBackground(ColorCache.getColor(e.display, tag.getColor()));
/*  97 */       e.gc.setAlpha(20);
/*  98 */       e.gc.fillRectangle(0, 0, size.x, size.y);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void buildTagGroup(List<Tag> tags, Composite cMainComposite, final TagButtonTrigger trigger)
/*     */   {
/* 106 */     this.cMainComposite = cMainComposite;
/*     */     
/* 108 */     cMainComposite.setLayout(new GridLayout(1, false));
/*     */     
/* 110 */     this.buttons = new ArrayList();
/*     */     
/* 112 */     SelectionListener selectionListener = new SelectionListener()
/*     */     {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 115 */         Button button = (Button)e.widget;
/* 116 */         Tag tag = (Tag)button.getData("Tag");
/* 117 */         if (button.getGrayed()) {
/* 118 */           button.setGrayed(false);
/* 119 */           button.setSelection(!button.getSelection());
/* 120 */           button.getParent().redraw();
/*     */         }
/* 122 */         boolean doTag = button.getSelection();
/* 123 */         trigger.tagButtonTriggered(tag, doTag);
/*     */         
/* 125 */         button.getParent().redraw();
/* 126 */         button.getParent().update();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void widgetDefaultSelected(SelectionEvent e) {}
/* 132 */     };
/* 133 */     Listener menuDetectListener = new Listener()
/*     */     {
/*     */       public void handleEvent(Event event) {
/* 136 */         final Button button = (Button)event.widget;
/* 137 */         Menu menu = new Menu(button);
/* 138 */         button.setMenu(menu);
/*     */         
/* 140 */         MenuBuildUtils.addMaintenanceListenerForMenu(menu, new MenuBuildUtils.MenuBuilder() {
/*     */           public void buildMenu(Menu menu, MenuEvent menuEvent) {
/* 142 */             Tag tag = (Tag)button.getData("Tag");
/* 143 */             TagUIUtils.createSideBarMenuItems(menu, tag);
/*     */           }
/*     */           
/*     */ 
/*     */         });
/*     */       }
/* 149 */     };
/* 150 */     tags = TagUIUtils.sortTags(tags);
/* 151 */     Composite g = null;
/* 152 */     String group = null;
/* 153 */     for (Tag tag : tags) {
/* 154 */       String newGroup = tag.getGroup();
/* 155 */       if ((g == null) || ((group != null) && (!group.equals(newGroup))) || ((group == null) && (newGroup != null)))
/*     */       {
/* 157 */         group = newGroup;
/*     */         
/* 159 */         g = group == null ? new Composite(cMainComposite, 536870912) : new Group(cMainComposite, 536870912);
/*     */         
/* 161 */         if (group != null) {
/* 162 */           ((Group)g).setText(group);
/*     */         }
/* 164 */         g.setLayoutData(new GridData(4, 4, true, true));
/* 165 */         RowLayout rowLayout = new RowLayout();
/* 166 */         rowLayout.pack = true;
/* 167 */         rowLayout.spacing = 5;
/* 168 */         Utils.setLayout(g, rowLayout);
/*     */       }
/*     */       
/* 171 */       Composite p = new Composite(g, 536870912);
/* 172 */       GridLayout layout = new GridLayout(1, false);
/* 173 */       layout.marginHeight = 3;
/* 174 */       if (Constants.isWindows) {
/* 175 */         layout.marginWidth = 6;
/* 176 */         layout.marginLeft = 2;
/* 177 */         layout.marginTop = 1;
/*     */       } else {
/* 179 */         layout.marginWidth = 0;
/* 180 */         layout.marginLeft = 3;
/* 181 */         layout.marginRight = 11;
/*     */       }
/* 183 */       p.setLayout(layout);
/* 184 */       p.addPaintListener(this);
/*     */       
/* 186 */       Button button = new Button(p, 32);
/* 187 */       this.buttons.add(button);
/* 188 */       boolean[] auto = tag.isTagAuto();
/*     */       
/* 190 */       if ((auto[0] != 0) && (auto[1] != 0)) {
/* 191 */         button.setEnabled(false);
/*     */       } else {
/* 193 */         button.addSelectionListener(selectionListener);
/*     */       }
/* 195 */       button.setData("Tag", tag);
/*     */       
/* 197 */       button.addListener(35, menuDetectListener);
/* 198 */       button.addPaintListener(this);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean updateFields(List<Taggable> taggables)
/*     */   {
/* 204 */     List<Control> layoutChanges = new ArrayList();
/* 205 */     for (Button button : this.buttons)
/*     */     {
/* 207 */       Tag tag = (Tag)button.getData("Tag");
/* 208 */       if (tag != null)
/*     */       {
/*     */ 
/* 211 */         String name = tag.getTagName(true);
/* 212 */         if (!button.getText().equals(name)) {
/* 213 */           button.setText(name);
/* 214 */           layoutChanges.add(button);
/*     */         }
/*     */         
/* 217 */         updateButtonState(tag, button, taggables);
/*     */         
/* 219 */         button.getParent().redraw();
/*     */       }
/*     */     }
/* 222 */     if (layoutChanges.size() > 0) {
/* 223 */       this.cMainComposite.layout((Control[])layoutChanges.toArray(new Control[0]));
/* 224 */       return true;
/*     */     }
/* 226 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void updateButtonState(Tag tag, Button button, List<Taggable> taggables)
/*     */   {
/* 236 */     if (taggables == null) {
/* 237 */       button.setSelection(false);
/* 238 */       button.setEnabled(false);
/* 239 */       button.getParent().redraw();
/* 240 */       return;
/*     */     }
/*     */     
/* 243 */     boolean hasTag = false;
/* 244 */     boolean hasNoTag = false;
/*     */     
/* 246 */     for (Taggable taggable : taggables) {
/* 247 */       boolean curHasTag = tag.hasTaggable(taggable);
/* 248 */       if ((!hasTag) && (curHasTag)) {
/* 249 */         hasTag = true;
/* 250 */         if (hasNoTag) {
/*     */           break;
/*     */         }
/* 253 */       } else if ((!hasNoTag) && (!curHasTag)) {
/* 254 */         hasNoTag = true;
/* 255 */         if (hasTag) {
/*     */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 261 */     boolean[] auto = tag.isTagAuto();
/*     */     
/* 263 */     boolean auto_add = auto[0];
/* 264 */     boolean auto_rem = auto[1];
/*     */     
/* 266 */     if ((hasTag) && (hasNoTag)) {
/* 267 */       button.setEnabled(!auto_add);
/*     */       
/* 269 */       button.setGrayed(true);
/* 270 */       button.setSelection(true);
/*     */     }
/*     */     else {
/* 273 */       if ((auto_add) && (auto_rem))
/*     */       {
/* 275 */         button.setEnabled(false);
/*     */       } else {
/* 277 */         button.setEnabled((hasTag) || ((!hasTag) && (!auto_add)));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 282 */       button.setGrayed(false);
/* 283 */       button.setSelection(hasTag);
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface TagButtonTrigger
/*     */   {
/*     */     public abstract void tagButtonTriggered(Tag paramTag, boolean paramBoolean);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/utils/TagButtonsUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */