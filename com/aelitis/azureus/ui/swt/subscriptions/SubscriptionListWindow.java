/*     */ package com.aelitis.azureus.ui.swt.subscriptions;
/*     */ 
/*     */ import com.aelitis.azureus.core.subs.Subscription;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionAssociationLookup;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionException;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionLookupListener;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionManager;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionManagerFactory;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionPopularityListener;
/*     */ import com.aelitis.azureus.ui.swt.widgets.AnimatedImage;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import org.eclipse.swt.custom.StackLayout;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.layout.FormAttachment;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.ProgressBar;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Table;
/*     */ import org.eclipse.swt.widgets.TableColumn;
/*     */ import org.eclipse.swt.widgets.TableItem;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
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
/*     */ public class SubscriptionListWindow
/*     */   implements SubscriptionLookupListener
/*     */ {
/*     */   private final byte[] torrent_hash;
/*     */   private final String[] networks;
/*     */   private final boolean useCachedSubs;
/*     */   private Display display;
/*     */   private Shell shell;
/*     */   AnimatedImage animatedImage;
/*     */   Button action;
/*     */   Label loadingText;
/*     */   ProgressBar loadingProgress;
/*  70 */   boolean loadingDone = false;
/*     */   
/*  72 */   SubscriptionAssociationLookup lookup = null;
/*     */   
/*     */ 
/*     */ 
/*     */   Composite mainComposite;
/*     */   
/*     */ 
/*     */ 
/*     */   Composite loadingPanel;
/*     */   
/*     */ 
/*     */ 
/*     */   Composite listPanel;
/*     */   
/*     */ 
/*     */ 
/*     */   Table subscriptionsList;
/*     */   
/*     */ 
/*     */   StackLayout mainLayout;
/*     */   
/*     */ 
/*     */   SubscriptionItemModel[] subscriptionItems;
/*     */   
/*     */ 
/*     */ 
/*     */   public SubscriptionListWindow(Shell parent, String display_name, byte[] torrent_hash, String[] networks, boolean useCachedSubs)
/*     */   {
/* 100 */     this.torrent_hash = torrent_hash;
/* 101 */     this.networks = networks;
/* 102 */     this.useCachedSubs = useCachedSubs;
/*     */     
/* 104 */     this.shell = ShellFactory.createShell(parent, 2160);
/* 105 */     Utils.setShellIcon(this.shell);
/* 106 */     this.shell.setSize(400, 300);
/* 107 */     Utils.centerWindowRelativeTo(this.shell, parent);
/*     */     
/* 109 */     String networks_str = "";
/*     */     
/* 111 */     for (String net : networks)
/*     */     {
/* 113 */       networks_str = networks_str + (networks_str.length() == 0 ? "" : ", ") + MessageText.getString(new StringBuilder().append("ConfigView.section.connection.networks.").append(net).toString());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 118 */     if (networks_str.length() == 0)
/*     */     {
/* 120 */       networks_str = MessageText.getString("PeersView.uniquepiece.none");
/*     */     }
/*     */     
/* 123 */     this.display = this.shell.getDisplay();
/* 124 */     this.shell.setText(MessageText.getString("subscriptions.listwindow.title") + " [" + networks_str + "]");
/*     */     
/* 126 */     this.shell.setLayout(new FormLayout());
/*     */     
/* 128 */     this.mainComposite = new Composite(this.shell, 0);
/* 129 */     Label separator = new Label(this.shell, 258);
/* 130 */     Button cancel = new Button(this.shell, 8);
/* 131 */     this.action = new Button(this.shell, 8);
/* 132 */     cancel.setText(MessageText.getString("Button.cancel"));
/*     */     
/*     */ 
/*     */ 
/* 136 */     FormData data = new FormData();
/* 137 */     data.left = new FormAttachment(0, 0);
/* 138 */     data.right = new FormAttachment(100, 0);
/* 139 */     data.top = new FormAttachment(0, 0);
/* 140 */     data.bottom = new FormAttachment(separator, 0);
/* 141 */     Utils.setLayoutData(this.mainComposite, data);
/*     */     
/* 143 */     data = new FormData();
/* 144 */     data.left = new FormAttachment(0, 0);
/* 145 */     data.right = new FormAttachment(100, 0);
/* 146 */     data.bottom = new FormAttachment(cancel, -2);
/* 147 */     Utils.setLayoutData(separator, data);
/*     */     
/* 149 */     data = new FormData();
/* 150 */     data.right = new FormAttachment(this.action);
/* 151 */     data.width = 100;
/* 152 */     data.bottom = new FormAttachment(100, -5);
/* 153 */     Utils.setLayoutData(cancel, data);
/*     */     
/* 155 */     data = new FormData();
/* 156 */     data.right = new FormAttachment(100, -5);
/* 157 */     data.width = 100;
/* 158 */     data.bottom = new FormAttachment(100, -5);
/* 159 */     Utils.setLayoutData(this.action, data);
/*     */     
/* 161 */     cancel.addListener(13, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/* 163 */         if (SubscriptionListWindow.this.lookup != null) {
/* 164 */           SubscriptionListWindow.this.lookup.cancel();
/*     */         }
/* 166 */         if (!SubscriptionListWindow.this.shell.isDisposed()) {
/* 167 */           SubscriptionListWindow.this.shell.dispose();
/*     */         }
/*     */         
/*     */       }
/* 171 */     });
/* 172 */     this.mainLayout = new StackLayout();
/* 173 */     this.mainComposite.setLayout(this.mainLayout);
/*     */     
/* 175 */     this.loadingPanel = new Composite(this.mainComposite, 0);
/* 176 */     this.loadingPanel.setLayout(new FormLayout());
/*     */     
/* 178 */     this.listPanel = new Composite(this.mainComposite, 0);
/* 179 */     this.listPanel.setLayout(new FillLayout());
/*     */     
/* 181 */     this.subscriptionsList = new Table(this.listPanel, 268501508);
/* 182 */     this.subscriptionsList.setHeaderVisible(true);
/*     */     
/* 184 */     TableColumn name = new TableColumn(this.subscriptionsList, 0);
/* 185 */     name.setText(MessageText.getString("subscriptions.listwindow.name"));
/* 186 */     name.setWidth(Utils.adjustPXForDPI(310));
/* 187 */     name.setResizable(false);
/*     */     
/* 189 */     TableColumn popularity = new TableColumn(this.subscriptionsList, 0);
/* 190 */     popularity.setText(MessageText.getString("subscriptions.listwindow.popularity"));
/* 191 */     popularity.setWidth(Utils.adjustPXForDPI(70));
/* 192 */     popularity.setResizable(false);
/*     */     
/* 194 */     this.subscriptionsList.addListener(36, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 196 */         TableItem item = (TableItem)e.item;
/* 197 */         int index = SubscriptionListWindow.this.subscriptionsList.indexOf(item);
/* 198 */         if ((index >= 0) && (index < SubscriptionListWindow.this.subscriptionItems.length)) {
/* 199 */           SubscriptionListWindow.SubscriptionItemModel subscriptionItem = SubscriptionListWindow.this.subscriptionItems[index];
/* 200 */           item.setText(0, subscriptionItem.name);
/* 201 */           item.setText(1, subscriptionItem.popularityDisplay);
/*     */         }
/*     */         
/*     */       }
/* 205 */     });
/* 206 */     this.subscriptionsList.setSortColumn(popularity);
/* 207 */     this.subscriptionsList.setSortDirection(1024);
/*     */     
/* 209 */     this.subscriptionsList.addListener(13, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/* 211 */         SubscriptionListWindow.this.action.setEnabled(SubscriptionListWindow.this.subscriptionsList.getSelectionIndex() != -1);
/*     */       }
/*     */       
/* 214 */     });
/* 215 */     Listener sortListener = new Listener()
/*     */     {
/*     */       public void handleEvent(Event e) {
/* 218 */         TableColumn sortColumn = SubscriptionListWindow.this.subscriptionsList.getSortColumn();
/* 219 */         TableColumn currentColumn = (TableColumn)e.widget;
/* 220 */         int dir = SubscriptionListWindow.this.subscriptionsList.getSortDirection();
/* 221 */         if (sortColumn == currentColumn) {
/* 222 */           dir = dir == 128 ? 1024 : 128;
/*     */         } else {
/* 224 */           SubscriptionListWindow.this.subscriptionsList.setSortColumn(currentColumn);
/* 225 */           dir = 1024;
/*     */         }
/* 227 */         SubscriptionListWindow.this.subscriptionsList.setSortDirection(dir);
/* 228 */         SubscriptionListWindow.this.sortAndRefresh();
/*     */       }
/* 230 */     };
/* 231 */     name.addListener(13, sortListener);
/* 232 */     popularity.addListener(13, sortListener);
/*     */     
/* 234 */     this.animatedImage = new AnimatedImage(this.loadingPanel);
/* 235 */     this.loadingText = new Label(this.loadingPanel, 16777280);
/* 236 */     this.loadingProgress = new ProgressBar(this.loadingPanel, 256);
/*     */     
/* 238 */     this.animatedImage.setImageFromName("spinner_big");
/*     */     
/* 240 */     this.loadingText.setText(MessageText.getString("subscriptions.listwindow.loadingtext", new String[] { display_name }));
/*     */     
/* 242 */     this.loadingProgress.setMinimum(0);
/* 243 */     this.loadingProgress.setMaximum(300);
/* 244 */     this.loadingProgress.setSelection(0);
/*     */     
/* 246 */     data = new FormData();
/* 247 */     data.left = new FormAttachment(1, 2, -16);
/* 248 */     data.top = new FormAttachment(1, 2, -32);
/* 249 */     data.width = 32;
/* 250 */     data.height = 32;
/* 251 */     this.animatedImage.setLayoutData(data);
/*     */     
/* 253 */     data = new FormData();
/* 254 */     data.left = new FormAttachment(0, 5);
/* 255 */     data.right = new FormAttachment(100, -5);
/* 256 */     data.top = new FormAttachment(this.animatedImage.getControl(), 10);
/* 257 */     data.height = 50;
/* 258 */     Utils.setLayoutData(this.loadingText, data);
/*     */     
/* 260 */     data = new FormData();
/* 261 */     data.left = new FormAttachment(0, 5);
/* 262 */     data.right = new FormAttachment(100, -5);
/* 263 */     data.top = new FormAttachment(this.loadingText, 5);
/* 264 */     Utils.setLayoutData(this.loadingProgress, data);
/*     */     
/* 266 */     boolean autoCheck = COConfigurationManager.getBooleanParameter("subscriptions.autocheck");
/*     */     
/* 268 */     if (autoCheck) {
/* 269 */       startChecking();
/*     */     } else {
/* 271 */       this.action.setText(MessageText.getString("Button.yes"));
/* 272 */       Composite acceptPanel = new Composite(this.mainComposite, 0);
/* 273 */       acceptPanel.setLayout(new FormLayout());
/*     */       
/* 275 */       Label acceptLabel = new Label(acceptPanel, 16777280);
/*     */       
/* 277 */       acceptLabel.setText(MessageText.getString("subscriptions.listwindow.autochecktext"));
/*     */       
/* 279 */       data = new FormData();
/* 280 */       data.left = new FormAttachment(0, 5);
/* 281 */       data.right = new FormAttachment(100, -5);
/* 282 */       data.top = new FormAttachment(1, 3, 0);
/* 283 */       Utils.setLayoutData(acceptLabel, data);
/*     */       
/* 285 */       this.action.addListener(13, new Listener() {
/*     */         public void handleEvent(Event event) {
/* 287 */           SubscriptionListWindow.this.action.removeListener(13, this);
/* 288 */           COConfigurationManager.setParameter("subscriptions.autocheck", true);
/* 289 */           SubscriptionListWindow.this.startChecking();
/* 290 */           SubscriptionListWindow.this.mainComposite.layout();
/*     */         }
/* 292 */       });
/* 293 */       this.mainLayout.topControl = acceptPanel;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 298 */     this.shell.open();
/*     */   }
/*     */   
/*     */   private void startChecking()
/*     */   {
/* 303 */     this.action.setText(MessageText.getString("subscriptions.listwindow.subscribe"));
/* 304 */     this.action.setEnabled(false);
/*     */     
/*     */     try
/*     */     {
/* 308 */       SubscriptionManager subs_man = SubscriptionManagerFactory.getSingleton();
/* 309 */       if (this.useCachedSubs) {
/* 310 */         Subscription[] subs = subs_man.getKnownSubscriptions(this.torrent_hash);
/* 311 */         complete(this.torrent_hash, subs);
/*     */       } else {
/* 313 */         this.lookup = subs_man.lookupAssociations(this.torrent_hash, this.networks, this);
/*     */         
/* 315 */         this.lookup.setTimeout(60000L);
/*     */       }
/*     */       
/*     */ 
/* 319 */       this.loadingDone = false;
/* 320 */       AEThread2 progressMover = new AEThread2("progressMover", true) {
/*     */         public void run() {
/* 322 */           final int[] waitTime = new int[1];
/* 323 */           waitTime[0] = 100;
/* 324 */           while (!SubscriptionListWindow.this.loadingDone) {
/* 325 */             if ((SubscriptionListWindow.this.display != null) && (!SubscriptionListWindow.this.display.isDisposed())) {
/* 326 */               SubscriptionListWindow.this.display.asyncExec(new Runnable() {
/*     */                 public void run() {
/* 328 */                   if ((SubscriptionListWindow.this.loadingProgress != null) && (!SubscriptionListWindow.this.loadingProgress.isDisposed())) {
/* 329 */                     int currentSelection = SubscriptionListWindow.this.loadingProgress.getSelection() + 1;
/* 330 */                     SubscriptionListWindow.this.loadingProgress.setSelection(currentSelection);
/* 331 */                     if (currentSelection > SubscriptionListWindow.this.loadingProgress.getMaximum() * 80 / 100) {
/* 332 */                       waitTime[0] = 300;
/*     */                     }
/* 334 */                     if (currentSelection > SubscriptionListWindow.this.loadingProgress.getMaximum() * 90 / 100) {
/* 335 */                       waitTime[0] = 1000;
/*     */                     }
/*     */                   } else {
/* 338 */                     SubscriptionListWindow.this.loadingDone = true;
/*     */                   }
/*     */                 }
/*     */               });
/*     */             }
/*     */             try {
/* 344 */               Thread.sleep(waitTime[0]);
/*     */             }
/*     */             catch (Exception e) {
/* 347 */               SubscriptionListWindow.this.loadingDone = true;
/*     */             }
/*     */           }
/*     */         }
/* 351 */       };
/* 352 */       progressMover.start();
/*     */     }
/*     */     catch (Exception e) {
/* 355 */       failed(null, null);
/*     */     }
/* 357 */     this.animatedImage.start();
/* 358 */     this.mainLayout.topControl = this.loadingPanel;
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
/*     */   public void found(byte[] hash, Subscription subscription) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void complete(byte[] hash, final Subscription[] subscriptions)
/*     */   {
/* 381 */     if (subscriptions.length <= 0) {
/* 382 */       failed(hash, null);
/*     */ 
/*     */     }
/* 385 */     else if ((this.display != null) && (!this.display.isDisposed())) {
/* 386 */       this.display.asyncExec(new Runnable() {
/*     */         public void run() {
/* 388 */           SubscriptionListWindow.this.subscriptionItems = new SubscriptionListWindow.SubscriptionItemModel[subscriptions.length];
/* 389 */           for (int i = 0; i < subscriptions.length; i++) {
/* 390 */             final SubscriptionListWindow.SubscriptionItemModel subscriptionItem = new SubscriptionListWindow.SubscriptionItemModel(null);
/* 391 */             SubscriptionListWindow.this.subscriptionItems[i] = subscriptionItem;
/* 392 */             subscriptionItem.name = subscriptions[i].getName();
/* 393 */             subscriptionItem.popularity = -1L;
/* 394 */             subscriptionItem.popularityDisplay = MessageText.getString("subscriptions.listwindow.popularity.reading");
/* 395 */             subscriptionItem.subscription = subscriptions[i];
/*     */             try
/*     */             {
/* 398 */               subscriptions[i].getPopularity(new SubscriptionPopularityListener()
/*     */               {
/*     */ 
/*     */ 
/*     */                 public void gotPopularity(long popularity)
/*     */                 {
/*     */ 
/* 405 */                   SubscriptionListWindow.this.update(subscriptionItem, popularity, popularity + "");
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/*     */                 public void failed(SubscriptionException error)
/*     */                 {
/* 412 */                   SubscriptionListWindow.this.update(subscriptionItem, -2L, MessageText.getString("subscriptions.listwindow.popularity.unknown"));
/*     */                 }
/*     */                 
/*     */               });
/*     */             }
/*     */             catch (SubscriptionException e)
/*     */             {
/* 419 */               SubscriptionListWindow.this.update(subscriptionItem, -2L, MessageText.getString("subscriptions.listwindow.popularity.unknown"));
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 425 */           SubscriptionListWindow.this.animatedImage.stop();
/*     */           
/* 427 */           SubscriptionListWindow.this.mainLayout.topControl = SubscriptionListWindow.this.listPanel;
/* 428 */           SubscriptionListWindow.this.mainComposite.layout();
/*     */           
/* 430 */           SubscriptionListWindow.this.sortAndRefresh();
/* 431 */           SubscriptionListWindow.this.subscriptionsList.setSelection(0);
/*     */           
/* 433 */           SubscriptionListWindow.this.action.addListener(13, new Listener() {
/*     */             public void handleEvent(Event arg0) {
/* 435 */               if ((SubscriptionListWindow.this.subscriptionsList != null) && (!SubscriptionListWindow.this.subscriptionsList.isDisposed())) {
/* 436 */                 int selectedIndex = SubscriptionListWindow.this.subscriptionsList.getSelectionIndex();
/* 437 */                 if ((selectedIndex >= 0) && (selectedIndex < SubscriptionListWindow.this.subscriptionItems.length)) {
/* 438 */                   Subscription subscription = SubscriptionListWindow.this.subscriptionItems[selectedIndex].subscription;
/* 439 */                   if (subscription != null) {
/* 440 */                     subscription.setSubscribed(true);
/* 441 */                     subscription.requestAttention();
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */           });
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void update(final SubscriptionItemModel subscriptionItem, final long popularity, String text)
/*     */   {
/* 460 */     this.display.asyncExec(new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */ 
/* 466 */         subscriptionItem.popularity = popularity;
/* 467 */         subscriptionItem.popularityDisplay = this.val$text;
/*     */         
/* 469 */         SubscriptionListWindow.this.sortAndRefresh();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void sortAndRefresh()
/*     */   {
/* 476 */     if (this.subscriptionsList.isDisposed())
/*     */     {
/* 478 */       return;
/*     */     }
/*     */     
/* 481 */     for (int i = 0; i < this.subscriptionItems.length; i++) {
/* 482 */       this.subscriptionItems[i].selected = false;
/*     */     }
/*     */     
/* 485 */     int currentSelection = this.subscriptionsList.getSelectionIndex();
/* 486 */     if ((currentSelection >= 0) && (currentSelection < this.subscriptionItems.length)) {
/* 487 */       this.subscriptionItems[currentSelection].selected = true;
/*     */     }
/*     */     
/* 490 */     final int dir = this.subscriptionsList.getSortDirection() == 1024 ? 1 : -1;
/* 491 */     final boolean nameSort = this.subscriptionsList.getColumn(0) == this.subscriptionsList.getSortColumn();
/* 492 */     Arrays.sort(this.subscriptionItems, new Comparator() {
/*     */       public int compare(Object arg0, Object arg1) {
/* 494 */         SubscriptionListWindow.SubscriptionItemModel item0 = (SubscriptionListWindow.SubscriptionItemModel)arg0;
/* 495 */         SubscriptionListWindow.SubscriptionItemModel item1 = (SubscriptionListWindow.SubscriptionItemModel)arg1;
/* 496 */         if (nameSort) {
/* 497 */           return dir * item0.name.compareTo(item1.name);
/*     */         }
/* 499 */         return dir * (int)(item1.popularity - item0.popularity);
/*     */       }
/*     */       
/* 502 */     });
/* 503 */     this.subscriptionsList.setItemCount(this.subscriptionItems.length);
/* 504 */     this.subscriptionsList.clearAll();
/* 505 */     if ((currentSelection >= 0) && (currentSelection < this.subscriptionItems.length)) {
/* 506 */       for (int i = 0; i < this.subscriptionItems.length; i++) {
/* 507 */         if (this.subscriptionItems[i].selected) {
/* 508 */           this.subscriptionsList.setSelection(i);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void failed(byte[] hash, SubscriptionException error) {
/* 515 */     if ((this.display != null) && (!this.display.isDisposed())) {
/* 516 */       this.display.asyncExec(new Runnable() {
/*     */         public void run() {
/* 518 */           SubscriptionListWindow.this.animatedImage.stop();
/* 519 */           SubscriptionListWindow.this.animatedImage.dispose();
/* 520 */           SubscriptionListWindow.this.loadingProgress.dispose();
/* 521 */           if (!SubscriptionListWindow.this.loadingText.isDisposed()) {
/* 522 */             SubscriptionListWindow.this.loadingText.setText(MessageText.getString("subscriptions.listwindow.failed"));
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */   private static class SubscriptionItemModel
/*     */   {
/*     */     String name;
/*     */     long popularity;
/*     */     String popularityDisplay;
/*     */     Subscription subscription;
/*     */     boolean selected;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/subscriptions/SubscriptionListWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */