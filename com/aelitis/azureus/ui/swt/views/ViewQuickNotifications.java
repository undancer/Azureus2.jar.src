/*     */ package com.aelitis.azureus.ui.swt.views;
/*     */ 
/*     */ import com.aelitis.azureus.activities.VuzeActivitiesEntry;
/*     */ import com.aelitis.azureus.activities.VuzeActivitiesManager;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.BufferedLabel;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
/*     */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter;
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
/*     */ public class ViewQuickNotifications
/*     */   implements UISWTViewCoreEventListener
/*     */ {
/*     */   private UISWTView swtView;
/*     */   private Composite composite;
/*     */   private Label notification_icon;
/*     */   private Label notification_text;
/*     */   private BufferedLabel more_text;
/*     */   
/*     */   private void initialize(Composite parent)
/*     */   {
/*  66 */     parent.setLayout(new GridLayout());
/*     */     
/*  68 */     this.composite = new Composite(parent, 2048);
/*     */     
/*  70 */     GridData gridData = new GridData(1808);
/*     */     
/*  72 */     Utils.setLayoutData(this.composite, gridData);
/*     */     
/*  74 */     GridLayout layout = new GridLayout(2, false);
/*  75 */     layout.marginLeft = (layout.marginRight = layout.marginTop = layout.marginBottom = 0);
/*     */     
/*  77 */     this.composite.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*  81 */     this.notification_icon = new Label(this.composite, 0);
/*  82 */     gridData = new GridData();
/*  83 */     gridData.widthHint = 20;
/*  84 */     Utils.setLayoutData(this.notification_icon, gridData);
/*     */     
/*     */ 
/*     */ 
/*  88 */     this.notification_text = new Label(this.composite, 536870912);
/*  89 */     gridData = new GridData(768);
/*  90 */     Utils.setLayoutData(this.notification_text, gridData);
/*     */     
/*  92 */     MouseAdapter listener = new MouseAdapter()
/*     */     {
/*     */ 
/*     */       public void mouseDown(MouseEvent e)
/*     */       {
/*  97 */         UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*     */         
/*  99 */         if (uif != null)
/*     */         {
/* 101 */           uif.getMDI().showEntryByID("Activity");
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/* 107 */     };
/* 108 */     this.more_text = new BufferedLabel(this.composite, 0);
/* 109 */     gridData = new GridData(768);
/* 110 */     gridData.horizontalSpan = 2;
/* 111 */     Utils.setLayoutData(this.more_text, gridData);
/* 112 */     this.notification_text.setData("");
/*     */     
/* 114 */     Control[] controls = { this.composite, this.notification_icon, this.notification_text, this.more_text.getControl() };
/*     */     
/* 116 */     for (Control c : controls)
/*     */     {
/* 118 */       c.addMouseListener(listener);
/*     */       
/* 120 */       Messages.setLanguageTooltip(c, "label.click.to.view");
/*     */     }
/*     */     
/* 123 */     this.notification_text.addPaintListener(new PaintListener()
/*     */     {
/*     */ 
/*     */       public void paintControl(PaintEvent e)
/*     */       {
/* 128 */         String text = (String)ViewQuickNotifications.this.notification_text.getData();
/*     */         
/* 130 */         int style = 16384;
/*     */         
/* 132 */         Rectangle bounds = ViewQuickNotifications.this.notification_text.getBounds();
/*     */         
/* 134 */         bounds.x = 4;
/* 135 */         bounds.y = 0;
/* 136 */         bounds.width -= 8;
/*     */         
/* 138 */         GCStringPrinter sp = new GCStringPrinter(e.gc, text, bounds, true, true, style);
/*     */         
/* 140 */         sp.calculateMetrics();
/*     */         
/* 142 */         sp.printString();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   private void delete()
/*     */   {
/* 150 */     Utils.disposeComposite(this.composite);
/*     */   }
/*     */   
/*     */ 
/*     */   private String getFullTitle()
/*     */   {
/* 156 */     return MessageText.getString("label.quick.notifications");
/*     */   }
/*     */   
/*     */ 
/*     */   private Composite getComposite()
/*     */   {
/* 162 */     return this.composite;
/*     */   }
/*     */   
/*     */   private void refresh()
/*     */   {
/* 167 */     Object[] temp = VuzeActivitiesManager.getMostRecentUnseen();
/*     */     
/* 169 */     VuzeActivitiesEntry entry = (VuzeActivitiesEntry)temp[0];
/*     */     
/* 171 */     String old_text = (String)this.notification_text.getData();
/*     */     
/* 173 */     if (entry == null)
/*     */     {
/* 175 */       this.notification_icon.setImage(null);
/*     */       
/* 177 */       if (old_text.length() > 0)
/*     */       {
/* 179 */         this.notification_text.setData("");
/*     */         
/* 181 */         this.notification_text.redraw();
/*     */       }
/*     */       
/* 184 */       this.more_text.setText("");
/*     */     }
/*     */     else
/*     */     {
/* 188 */       String cur_text = entry.getText();
/*     */       
/* 190 */       if (!old_text.equals(cur_text))
/*     */       {
/* 192 */         this.notification_text.setData(cur_text);
/*     */         
/* 194 */         this.notification_text.redraw();
/*     */       }
/*     */       
/* 197 */       String icon_id = entry.getIconID();
/*     */       
/* 199 */       if (icon_id != null)
/*     */       {
/* 201 */         String existing = (String)this.notification_icon.getData();
/*     */         
/* 203 */         if ((existing == null) || (this.notification_icon.getImage() == null) || (!existing.equals(icon_id)))
/*     */         {
/* 205 */           ImageLoader imageLoader = ImageLoader.getInstance();
/*     */           
/* 207 */           if (existing != null)
/*     */           {
/* 209 */             imageLoader.releaseImage(existing);
/*     */           }
/*     */           
/* 212 */           Image image = imageLoader.getImage(icon_id);
/*     */           
/* 214 */           this.notification_icon.setImage(image);
/*     */           
/* 216 */           this.notification_icon.setData(icon_id);
/*     */         }
/*     */       }
/*     */       else {
/* 220 */         this.notification_icon.setImage(null);
/*     */       }
/*     */       
/* 223 */       int num = ((Integer)temp[1]).intValue();
/*     */       
/* 225 */       if (num <= 1)
/*     */       {
/* 227 */         this.more_text.setText("");
/*     */       }
/*     */       else
/*     */       {
/* 231 */         this.more_text.setText(MessageText.getString("popup.more.waiting", new String[] { String.valueOf(num - 1) }));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean eventOccurred(UISWTViewEvent event)
/*     */   {
/* 240 */     switch (event.getType()) {
/*     */     case 0: 
/* 242 */       this.swtView = event.getView();
/* 243 */       this.swtView.setTitle(getFullTitle());
/* 244 */       break;
/*     */     
/*     */     case 7: 
/* 247 */       delete();
/* 248 */       break;
/*     */     
/*     */     case 2: 
/* 251 */       initialize((Composite)event.getData());
/* 252 */       break;
/*     */     
/*     */     case 6: 
/* 255 */       Messages.updateLanguageForControl(getComposite());
/* 256 */       this.swtView.setTitle(getFullTitle());
/* 257 */       break;
/*     */     
/*     */     case 5: 
/* 260 */       refresh();
/* 261 */       break;
/*     */     case 3: 
/* 263 */       this.composite.traverse(16);
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 268 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/ViewQuickNotifications.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */