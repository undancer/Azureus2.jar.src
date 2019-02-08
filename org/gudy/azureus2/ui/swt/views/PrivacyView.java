/*      */ package org.gudy.azureus2.ui.swt.views;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*      */ import com.aelitis.azureus.core.proxy.AEProxySelector;
/*      */ import com.aelitis.azureus.core.proxy.AEProxySelectorFactory;
/*      */ import com.aelitis.azureus.plugins.I2PHelpers;
/*      */ import com.aelitis.azureus.plugins.extseed.ExternalSeedPlugin;
/*      */ import com.aelitis.azureus.plugins.extseed.ExternalSeedReader;
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.Proxy;
/*      */ import java.net.URL;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.eclipse.swt.custom.ScrolledComposite;
/*      */ import org.eclipse.swt.events.ControlAdapter;
/*      */ import org.eclipse.swt.events.ControlEvent;
/*      */ import org.eclipse.swt.events.DisposeEvent;
/*      */ import org.eclipse.swt.events.DisposeListener;
/*      */ import org.eclipse.swt.events.MouseAdapter;
/*      */ import org.eclipse.swt.events.MouseEvent;
/*      */ import org.eclipse.swt.events.PaintEvent;
/*      */ import org.eclipse.swt.events.PaintListener;
/*      */ import org.eclipse.swt.events.SelectionAdapter;
/*      */ import org.eclipse.swt.events.SelectionEvent;
/*      */ import org.eclipse.swt.graphics.Font;
/*      */ import org.eclipse.swt.graphics.FontData;
/*      */ import org.eclipse.swt.graphics.GC;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.layout.FormLayout;
/*      */ import org.eclipse.swt.layout.GridData;
/*      */ import org.eclipse.swt.layout.GridLayout;
/*      */ import org.eclipse.swt.widgets.Button;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Group;
/*      */ import org.eclipse.swt.widgets.Label;
/*      */ import org.eclipse.swt.widgets.Layout;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Scale;
/*      */ import org.eclipse.swt.widgets.ScrollBar;
/*      */ import org.eclipse.swt.widgets.Text;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStateAttributeListener;
/*      */ import org.gudy.azureus2.core3.download.impl.DownloadManagerController;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.peer.PEPeer;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerSource;
/*      */ import org.gudy.azureus2.core3.peer.util.PeerUtils;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*      */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.core3.util.HostNameToIPResolver;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.ipc.IPCException;
/*      */ import org.gudy.azureus2.plugins.ipc.IPCInterface;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*      */ import org.gudy.azureus2.ui.swt.Messages;
/*      */ import org.gudy.azureus2.ui.swt.TextViewerWindow;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.components.BufferedLabel;
/*      */ import org.gudy.azureus2.ui.swt.components.LinkLabel;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class PrivacyView
/*      */   implements UISWTViewCoreEventListener, DownloadManagerStateAttributeListener
/*      */ {
/*      */   public static final String MSGID_PREFIX = "PrivacyView";
/*      */   private UISWTView swtView;
/*      */   private Composite cMainComposite;
/*      */   private ScrolledComposite sc;
/*      */   private Composite parent;
/*      */   private static final int PL_PUBLIC = 0;
/*      */   private static final int PL_MIX = 1;
/*      */   private static final int PL_ANONYMOUS = 2;
/*      */   private static final int PL_INVALID = 3;
/*      */   private int privacy_level;
/*      */   private Scale privacy_scale;
/*      */   private boolean i2p_install_prompted;
/*      */   private Composite i2p_lookup_comp;
/*      */   private Button i2p_install_button;
/*      */   private Button i2p_lookup_button;
/*      */   private Label i2p_options_link;
/*      */   private BufferedLabel i2p_result_summary;
/*      */   private Text i2p_result_list;
/*      */   private Button[] network_buttons;
/*      */   private Button[] source_buttons;
/*      */   private Button ipfilter_enabled;
/*      */   private BufferedLabel peer_info;
/*      */   private BufferedLabel torrent_info;
/*      */   private BufferedLabel tracker_info;
/*      */   private BufferedLabel webseed_info;
/*      */   private BufferedLabel vpn_info;
/*      */   private BufferedLabel socks_state;
/*      */   private BufferedLabel socks_current;
/*      */   private BufferedLabel socks_fails;
/*      */   private Label socks_more;
/*      */   private DownloadManager current_dm;
/*  134 */   private Set<String> enabled_networks = new HashSet();
/*  135 */   private Set<String> enabled_sources = new HashSet();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private String getFullTitle()
/*      */   {
/*  145 */     return MessageText.getString("label.privacy");
/*      */   }
/*      */   
/*      */   public boolean eventOccurred(UISWTViewEvent event) {
/*  149 */     switch (event.getType()) {
/*      */     case 0: 
/*  151 */       this.swtView = ((UISWTView)event.getData());
/*  152 */       this.swtView.setTitle(getFullTitle());
/*  153 */       break;
/*      */     
/*      */     case 7: 
/*  156 */       delete();
/*  157 */       break;
/*      */     
/*      */     case 2: 
/*  160 */       this.parent = ((Composite)event.getData());
/*  161 */       break;
/*      */     
/*      */     case 6: 
/*  164 */       Messages.updateLanguageForControl(this.cMainComposite);
/*  165 */       this.swtView.setTitle(getFullTitle());
/*  166 */       break;
/*      */     
/*      */     case 1: 
/*  169 */       Object ds = event.getData();
/*  170 */       dataSourceChanged(ds);
/*  171 */       break;
/*      */     
/*      */     case 3: 
/*  174 */       initialize();
/*  175 */       if (this.current_dm == null) {
/*  176 */         dataSourceChanged(this.swtView.getDataSource());
/*      */       }
/*      */       
/*      */       break;
/*      */     case 4: 
/*  181 */       delete();
/*  182 */       break;
/*      */     
/*      */     case 5: 
/*  185 */       refresh();
/*      */     }
/*      */     
/*      */     
/*  189 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   private void delete()
/*      */   {
/*  195 */     Utils.disposeComposite(this.sc);
/*      */     
/*  197 */     dataSourceChanged(null);
/*      */   }
/*      */   
/*      */ 
/*      */   private void refresh()
/*      */   {
/*  203 */     updatePeersEtc(this.current_dm);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void dataSourceChanged(Object newDataSource)
/*      */   {
/*  210 */     synchronized (this)
/*      */     {
/*  212 */       DownloadManager new_dm = ViewUtils.getDownloadManagerFromDataSource(newDataSource);
/*      */       
/*  214 */       if (new_dm == this.current_dm)
/*      */       {
/*  216 */         return;
/*      */       }
/*      */       
/*  219 */       final DownloadManager f_old_dm = this.current_dm;
/*      */       
/*  221 */       this.current_dm = new_dm;
/*      */       
/*  223 */       final DownloadManager f_new_dm = this.current_dm;
/*      */       
/*  225 */       Utils.execSWTThread(new AERunnable() {
/*      */         public void runSupport() {
/*  227 */           PrivacyView.this.swt_updateFields(f_old_dm, f_new_dm);
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void initialize()
/*      */   {
/*  236 */     if ((this.cMainComposite == null) || (this.cMainComposite.isDisposed()))
/*      */     {
/*  238 */       if ((this.parent == null) || (this.parent.isDisposed())) {
/*  239 */         return;
/*      */       }
/*      */       
/*  242 */       this.sc = new ScrolledComposite(this.parent, 512);
/*  243 */       this.sc.setExpandHorizontal(true);
/*  244 */       this.sc.setExpandVertical(true);
/*  245 */       this.sc.getVerticalBar().setIncrement(16);
/*      */       
/*  247 */       Layout parentLayout = this.parent.getLayout();
/*      */       
/*  249 */       if ((parentLayout instanceof GridLayout))
/*      */       {
/*  251 */         GridData gd = new GridData(4, 4, true, true);
/*      */         
/*  253 */         Utils.setLayoutData(this.sc, gd);
/*      */       }
/*  255 */       else if ((parentLayout instanceof FormLayout))
/*      */       {
/*  257 */         Utils.setLayoutData(this.sc, Utils.getFilledFormData());
/*      */       }
/*      */       
/*  260 */       this.cMainComposite = new Composite(this.sc, 0);
/*      */       
/*  262 */       this.sc.setContent(this.cMainComposite);
/*      */     }
/*      */     else
/*      */     {
/*  266 */       Utils.disposeComposite(this.cMainComposite, false);
/*      */     }
/*      */     
/*  269 */     GridLayout layout = new GridLayout(1, false);
/*  270 */     layout.horizontalSpacing = 0;
/*  271 */     layout.verticalSpacing = 0;
/*  272 */     layout.marginHeight = 0;
/*  273 */     layout.marginWidth = 0;
/*  274 */     this.cMainComposite.setLayout(layout);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  280 */     Composite overview_comp = new Composite(this.cMainComposite, 0);
/*  281 */     overview_comp.setLayout(new GridLayout(3, false));
/*      */     
/*  283 */     GridData gd = new GridData(768);
/*  284 */     Utils.setLayoutData(overview_comp, gd);
/*      */     
/*  286 */     Label label = new Label(overview_comp, 0);
/*  287 */     label.setText(MessageText.getString("privacy.view.intro"));
/*      */     
/*  289 */     LinkLabel link = new LinkLabel(overview_comp, "label.read.more", MessageText.getString("privacy.view.wiki.url"));
/*      */     
/*      */ 
/*      */ 
/*  293 */     Composite slider_comp = new Composite(this.cMainComposite, 0);
/*  294 */     layout = new GridLayout(3, false);
/*  295 */     layout.horizontalSpacing = 0;
/*  296 */     layout.verticalSpacing = 0;
/*  297 */     layout.marginHeight = 0;
/*      */     
/*  299 */     slider_comp.setLayout(layout);
/*      */     
/*  301 */     gd = new GridData(768);
/*  302 */     Utils.setLayoutData(slider_comp, gd);
/*      */     
/*  304 */     label = new Label(slider_comp, 0);
/*  305 */     label.setText(MessageText.getString("privacy.view.level") + ":");
/*      */     
/*  307 */     Composite slider2_comp = new Composite(slider_comp, 0);
/*  308 */     slider2_comp.setLayout(new GridLayout(6, true));
/*  309 */     gd = new GridData(768);
/*  310 */     Utils.setLayoutData(slider2_comp, gd);
/*      */     
/*  312 */     label = new Label(slider2_comp, 0);
/*  313 */     label.setText(MessageText.getString("privacy.view.public.only"));
/*      */     
/*  315 */     label = new Label(slider2_comp, 0);
/*  316 */     label.setText(MessageText.getString("privacy.view.public.anon"));
/*  317 */     label.setAlignment(16777216);
/*  318 */     gd = new GridData(768);
/*  319 */     gd.horizontalSpan = 2;
/*  320 */     Utils.setLayoutData(label, gd);
/*      */     
/*  322 */     label = new Label(slider2_comp, 0);
/*  323 */     label.setText(MessageText.getString("privacy.view.anon.only"));
/*  324 */     label.setAlignment(16777216);
/*  325 */     gd = new GridData(768);
/*  326 */     gd.horizontalSpan = 2;
/*  327 */     Utils.setLayoutData(label, gd);
/*      */     
/*  329 */     label = new Label(slider2_comp, 0);
/*  330 */     label.setText(MessageText.getString("label.invalid"));
/*  331 */     gd = new GridData(768);
/*  332 */     gd.horizontalAlignment = 16777224;
/*  333 */     Utils.setLayoutData(label, gd);
/*      */     
/*  335 */     this.privacy_scale = new Scale(slider2_comp, 256);
/*      */     
/*  337 */     gd = new GridData(768);
/*  338 */     gd.horizontalSpan = 6;
/*  339 */     Utils.setLayoutData(this.privacy_scale, gd);
/*      */     
/*  341 */     this.privacy_scale.setMinimum(0);
/*  342 */     this.privacy_scale.setMaximum(30);
/*      */     
/*      */ 
/*      */ 
/*  346 */     final boolean[] slider_mouse_down = { false };
/*      */     
/*  348 */     this.privacy_scale.addMouseListener(new MouseAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void mouseUp(MouseEvent e)
/*      */       {
/*      */ 
/*  355 */         int pos = PrivacyView.this.privacy_scale.getSelection();
/*      */         
/*  357 */         int level = (pos + 5) / 10;
/*      */         
/*  359 */         if (level * 10 != pos)
/*      */         {
/*  361 */           PrivacyView.this.privacy_scale.setSelection(level * 10);
/*      */         }
/*      */         
/*  364 */         PrivacyView.this.setPrivacyLevel(level);
/*      */         
/*  366 */         slider_mouse_down[0] = false;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void mouseDown(MouseEvent e)
/*      */       {
/*  373 */         slider_mouse_down[0] = true;
/*      */       }
/*      */       
/*  376 */     });
/*  377 */     this.privacy_scale.addListener(13, new Listener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void handleEvent(Event event)
/*      */       {
/*      */ 
/*  384 */         if (slider_mouse_down[0] == 0)
/*      */         {
/*  386 */           int pos = PrivacyView.this.privacy_scale.getSelection();
/*      */           
/*  388 */           int level = (pos + 5) / 10;
/*      */           
/*  390 */           PrivacyView.this.setPrivacyLevel(level);
/*      */         }
/*      */         
/*      */       }
/*      */       
/*      */ 
/*  396 */     });
/*  397 */     Composite network_comp = new Composite(slider_comp, 0);
/*      */     
/*  399 */     gd = new GridData();
/*  400 */     Utils.setLayoutData(network_comp, gd);
/*      */     
/*  402 */     this.network_buttons = new Button[AENetworkClassifier.AT_NETWORKS.length];
/*      */     
/*  404 */     network_comp.setLayout(new GridLayout(1, false));
/*      */     
/*  406 */     label = new Label(network_comp, 0);
/*  407 */     label.setText(MessageText.getString("ConfigView.section.connection.group.networks") + ":");
/*      */     
/*  409 */     for (int i = 0; i < this.network_buttons.length; i++)
/*      */     {
/*  411 */       final String nn = AENetworkClassifier.AT_NETWORKS[i];
/*      */       
/*  413 */       String msg_text = "ConfigView.section.connection.networks." + nn;
/*      */       
/*  415 */       Button button = new Button(network_comp, 32);
/*  416 */       Messages.setLanguageText(button, msg_text);
/*      */       
/*  418 */       this.network_buttons[i] = button;
/*      */       
/*  420 */       button.addSelectionListener(new SelectionAdapter() {
/*      */         public void widgetSelected(SelectionEvent e) {
/*  422 */           boolean selected = ((Button)e.widget).getSelection();
/*      */           
/*  424 */           if (PrivacyView.this.current_dm != null) {
/*  425 */             PrivacyView.this.current_dm.getDownloadState().setNetworkEnabled(nn, selected);
/*      */           }
/*      */           
/*      */         }
/*  429 */       });
/*  430 */       GridData gridData = new GridData();
/*  431 */       Utils.setLayoutData(button, gridData);
/*      */     }
/*      */     
/*      */ 
/*  435 */     label = new Label(slider_comp, 0);
/*      */     
/*  437 */     final Composite tracker_webseed_comp = new Composite(slider_comp, 0);
/*      */     
/*  439 */     layout = new GridLayout(2, true);
/*  440 */     layout.marginTop = (layout.marginBottom = layout.marginLeft = layout.marginRight = 1);
/*  441 */     tracker_webseed_comp.setLayout(layout);
/*  442 */     gd = new GridData(768);
/*  443 */     gd.horizontalSpan = 2;
/*  444 */     Utils.setLayoutData(tracker_webseed_comp, gd);
/*      */     
/*  446 */     tracker_webseed_comp.addPaintListener(new PaintListener()
/*      */     {
/*      */ 
/*      */       public void paintControl(PaintEvent e)
/*      */       {
/*  451 */         Rectangle client_area = tracker_webseed_comp.getClientArea();
/*      */         
/*  453 */         Rectangle rect = new Rectangle(0, 0, client_area.width - 1, client_area.height - 1);
/*      */         
/*  455 */         e.gc.setAlpha(50);
/*      */         
/*  457 */         e.gc.drawRectangle(rect);
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  462 */     });
/*  463 */     Composite tracker_comp = new Composite(tracker_webseed_comp, 0);
/*      */     
/*  465 */     gd = new GridData(768);
/*  466 */     Utils.setLayoutData(tracker_comp, gd);
/*  467 */     tracker_comp.setLayout(new GridLayout(2, false));
/*      */     
/*  469 */     label = new Label(tracker_comp, 0);
/*  470 */     label.setText(MessageText.getString("label.trackers") + ":");
/*      */     
/*  472 */     this.tracker_info = new BufferedLabel(tracker_comp, 536870912);
/*  473 */     gd = new GridData(768);
/*  474 */     Utils.setLayoutData(this.tracker_info, gd);
/*      */     
/*      */ 
/*      */ 
/*  478 */     Composite webseed_comp = new Composite(tracker_webseed_comp, 0);
/*      */     
/*  480 */     gd = new GridData(768);
/*  481 */     Utils.setLayoutData(webseed_comp, gd);
/*      */     
/*  483 */     webseed_comp.setLayout(new GridLayout(2, false));
/*      */     
/*  485 */     label = new Label(webseed_comp, 0);
/*  486 */     label.setText(MessageText.getString("label.webseeds") + ":");
/*      */     
/*  488 */     this.webseed_info = new BufferedLabel(webseed_comp, 536870912);
/*  489 */     gd = new GridData(768);
/*  490 */     Utils.setLayoutData(this.webseed_info, gd);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  496 */     Composite peer_comp = new Composite(tracker_webseed_comp, 0);
/*      */     
/*  498 */     gd = new GridData(768);
/*  499 */     Utils.setLayoutData(peer_comp, gd);
/*  500 */     peer_comp.setLayout(new GridLayout(2, false));
/*      */     
/*  502 */     label = new Label(peer_comp, 0);
/*  503 */     label.setText(MessageText.getString("TableColumn.header.peers") + ":");
/*      */     
/*  505 */     this.peer_info = new BufferedLabel(peer_comp, 536870912);
/*  506 */     gd = new GridData(768);
/*  507 */     Utils.setLayoutData(this.peer_info, gd);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  513 */     Group i2p_group = new Group(this.cMainComposite, 0);
/*  514 */     i2p_group.setText("I2P");
/*      */     
/*      */ 
/*      */ 
/*  518 */     gd = new GridData(768);
/*  519 */     Utils.setLayoutData(i2p_group, gd);
/*      */     
/*  521 */     i2p_group.setLayout(new GridLayout(4, false));
/*      */     
/*  523 */     label = new Label(i2p_group, 0);
/*  524 */     label.setText(MessageText.getString("privacy.view.lookup.info"));
/*  525 */     gd = new GridData();
/*  526 */     gd.horizontalSpan = 2;
/*  527 */     Utils.setLayoutData(label, gd);
/*      */     
/*  529 */     label = new Label(i2p_group, 0);
/*  530 */     label.setText(MessageText.getString("label.lookup.status") + ":");
/*      */     
/*      */ 
/*  533 */     this.i2p_result_summary = new BufferedLabel(i2p_group, 536870912);
/*  534 */     gd = new GridData(768);
/*      */     
/*  536 */     Utils.setLayoutData(this.i2p_result_summary, gd);
/*      */     
/*  538 */     Composite i2p_button_comp = new Composite(i2p_group, 0);
/*  539 */     i2p_button_comp.setLayout(new GridLayout(2, false));
/*      */     
/*  541 */     gd = new GridData(1040);
/*  542 */     Utils.setLayoutData(i2p_button_comp, gd);
/*      */     
/*  544 */     label = new Label(i2p_button_comp, 0);
/*  545 */     label.setText(MessageText.getString("GeneralView.section.availability"));
/*      */     
/*  547 */     this.i2p_install_button = new Button(i2p_button_comp, 8);
/*      */     
/*  549 */     this.i2p_install_button.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent event)
/*      */       {
/*      */ 
/*  556 */         boolean[] result = { false };
/*      */         
/*  558 */         I2PHelpers.installI2PHelper(null, result, new Runnable()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void run()
/*      */           {
/*      */ 
/*  565 */             Utils.execSWTThread(new Runnable()
/*      */             {
/*      */ 
/*      */               public void run()
/*      */               {
/*      */ 
/*  571 */                 PrivacyView.this.updateI2PState();
/*      */               }
/*      */               
/*      */ 
/*      */             });
/*      */           }
/*      */           
/*      */         });
/*      */       }
/*  580 */     });
/*  581 */     this.i2p_lookup_comp = new Composite(i2p_group, 2048);
/*      */     
/*  583 */     gd = new GridData();
/*  584 */     gd.widthHint = 300;
/*  585 */     gd.heightHint = 150;
/*  586 */     Utils.setLayoutData(this.i2p_lookup_comp, gd);
/*      */     
/*  588 */     this.i2p_lookup_comp.setBackground(Colors.white);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  593 */     this.i2p_result_list = new Text(i2p_group, 526920);
/*  594 */     gd = new GridData(1808);
/*  595 */     gd.horizontalSpan = 2;
/*  596 */     Utils.setLayoutData(this.i2p_result_list, gd);
/*      */     
/*  598 */     this.i2p_result_list.setEditable(false);
/*      */     
/*      */ 
/*      */ 
/*  602 */     label = new Label(i2p_button_comp, 0);
/*  603 */     label.setText(MessageText.getString("button.lookup.peers"));
/*      */     
/*  605 */     this.i2p_lookup_button = new Button(i2p_button_comp, 8);
/*      */     
/*  607 */     this.i2p_lookup_button.setText(MessageText.getString("button.search.dht"));
/*      */     
/*  609 */     this.i2p_lookup_button.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */       private int search_count;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent event)
/*      */       {
/*  618 */         Utils.disposeComposite(PrivacyView.this.i2p_lookup_comp, false);
/*      */         
/*  620 */         PrivacyView.this.i2p_result_summary.setText("");
/*      */         
/*  622 */         PrivacyView.this.i2p_result_list.setText("");
/*      */         
/*  624 */         PluginInterface i2p_pi = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByID("azneti2phelper", true);
/*      */         
/*  626 */         if (i2p_pi != null)
/*      */         {
/*  628 */           IPCInterface ipc = i2p_pi.getIPC();
/*      */           
/*  630 */           Map<String, Object> options = new HashMap();
/*      */           
/*  632 */           options.put("server_id", "Scraper");
/*  633 */           options.put("server_id_transient", Boolean.valueOf(true));
/*  634 */           options.put("ui_composite", PrivacyView.this.i2p_lookup_comp);
/*      */           
/*  636 */           final byte[] hash = (byte[])PrivacyView.this.i2p_lookup_button.getData("hash");
/*      */           
/*  638 */           this.search_count += 1;
/*      */           
/*  640 */           final int search_id = this.search_count;
/*      */           
/*  642 */           IPCInterface callback = new IPCInterface()
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */             public Object invoke(String methodName, final Object[] params)
/*      */               throws IPCException
/*      */             {
/*      */ 
/*      */ 
/*  652 */               if (search_id != PrivacyView.7.this.search_count)
/*      */               {
/*  654 */                 return null;
/*      */               }
/*      */               
/*  657 */               if (methodName.equals("statusUpdate"))
/*      */               {
/*  659 */                 final int status = ((Integer)params[0]).intValue();
/*      */                 
/*  661 */                 if ((status != 9) && (status != 4))
/*      */                 {
/*      */ 
/*  664 */                   Utils.execSWTThread(new Runnable()
/*      */                   {
/*      */ 
/*      */                     public void run()
/*      */                     {
/*      */ 
/*  670 */                       if ((PrivacyView.this.i2p_lookup_button.isDisposed()) || (PrivacyView.7.1.this.val$hash != PrivacyView.this.i2p_lookup_button.getData("hash")))
/*      */                       {
/*  672 */                         return;
/*      */                       }
/*      */                       
/*  675 */                       PrivacyView.this.i2p_lookup_button.setEnabled(true);
/*      */                       
/*  677 */                       if ((PrivacyView.this.i2p_result_list.getText().length() == 0) && (status != 8))
/*      */                       {
/*      */ 
/*  680 */                         PrivacyView.this.i2p_result_summary.setText(MessageText.getString("label.no.peers.found"));
/*      */                       }
/*      */                     }
/*      */                   });
/*      */                 }
/*      */                 
/*  686 */                 if (params.length == 4)
/*      */                 {
/*  688 */                   Utils.execSWTThread(new Runnable()
/*      */                   {
/*      */ 
/*      */                     public void run()
/*      */                     {
/*      */ 
/*  694 */                       if ((PrivacyView.this.i2p_result_summary.isDisposed()) || (PrivacyView.7.1.this.val$hash != PrivacyView.this.i2p_lookup_button.getData("hash")))
/*      */                       {
/*  696 */                         return;
/*      */                       }
/*      */                       
/*  699 */                       int seeds = ((Integer)params[1]).intValue();
/*  700 */                       int leechers = ((Integer)params[2]).intValue();
/*  701 */                       int peers = ((Integer)params[3]).intValue();
/*      */                       
/*  703 */                       PrivacyView.this.i2p_result_summary.setText(MessageText.getString("privacy.view.lookup.msg", new String[] { String.valueOf(seeds), String.valueOf(leechers), String.valueOf(peers) }));
/*      */ 
/*      */                     }
/*      */                     
/*      */ 
/*      */ 
/*      */                   });
/*      */                 }
/*      */                 
/*      */ 
/*      */               }
/*  714 */               else if (methodName.equals("msgUpdate"))
/*      */               {
/*  716 */                 Utils.execSWTThread(new Runnable()
/*      */                 {
/*      */ 
/*      */                   public void run()
/*      */                   {
/*      */ 
/*  722 */                     if ((PrivacyView.this.i2p_result_summary.isDisposed()) || (PrivacyView.7.1.this.val$hash != PrivacyView.this.i2p_lookup_button.getData("hash")))
/*      */                     {
/*  724 */                       return;
/*      */                     }
/*      */                     
/*  727 */                     String msg = (String)params[0];
/*      */                     
/*  729 */                     PrivacyView.this.i2p_result_summary.setText(msg);
/*      */                   }
/*      */                 });
/*      */               }
/*  733 */               else if (methodName.equals("peerFound"))
/*      */               {
/*  735 */                 Utils.execSWTThread(new Runnable()
/*      */                 {
/*      */ 
/*      */                   public void run()
/*      */                   {
/*      */ 
/*  741 */                     if ((PrivacyView.this.i2p_result_list.isDisposed()) || (PrivacyView.7.1.this.val$hash != PrivacyView.this.i2p_lookup_button.getData("hash")))
/*      */                     {
/*  743 */                       return;
/*      */                     }
/*      */                     
/*  746 */                     String host = (String)params[0];
/*  747 */                     int peer_type = ((Integer)params[1]).intValue();
/*      */                     
/*  749 */                     PrivacyView.this.i2p_result_list.append(host + "\r\n");
/*      */                   }
/*      */                 });
/*      */               }
/*      */               
/*      */ 
/*  755 */               return null;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             public boolean canInvoke(String methodName, Object[] params)
/*      */             {
/*  763 */               return true;
/*      */             }
/*      */             
/*      */ 
/*  767 */           };
/*  768 */           PrivacyView.this.i2p_lookup_button.setEnabled(false);
/*      */           
/*  770 */           PrivacyView.this.i2p_result_summary.setText(MessageText.getString("label.searching"));
/*      */           try
/*      */           {
/*  773 */             ipc.invoke("lookupTorrent", new Object[] { "", hash, options, callback });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*  784 */             PrivacyView.this.i2p_lookup_button.setEnabled(true);
/*      */             
/*  786 */             e.printStackTrace();
/*      */           }
/*      */           
/*      */         }
/*      */       }
/*  791 */     });
/*  792 */     Label i2p_options_info = new Label(i2p_button_comp, 64);
/*  793 */     gd = new GridData(768);
/*  794 */     gd.horizontalSpan = 2;
/*  795 */     gd.widthHint = 150;
/*  796 */     Utils.setLayoutData(i2p_options_info, gd);
/*      */     
/*  798 */     i2p_options_info.setText(MessageText.getString("privacy.view.check.bw.info"));
/*      */     
/*  800 */     if (!COConfigurationManager.getBooleanParameter("privacy.view.check.bw.clicked", false))
/*      */     {
/*  802 */       FontData fontData = i2p_options_info.getFont().getFontData()[0];
/*  803 */       final Font bold_font = new Font(i2p_options_info.getDisplay(), new FontData(fontData.getName(), fontData.getHeight(), 1));
/*  804 */       i2p_options_info.setFont(bold_font);
/*      */       
/*  806 */       i2p_options_info.addDisposeListener(new DisposeListener()
/*      */       {
/*      */         public void widgetDisposed(DisposeEvent e) {
/*  809 */           bold_font.dispose();
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*  814 */     this.i2p_options_link = new Label(i2p_button_comp, 0);
/*  815 */     gd = new GridData(768);
/*  816 */     gd.horizontalSpan = 2;
/*  817 */     Utils.setLayoutData(this.i2p_options_link, gd);
/*  818 */     this.i2p_options_link.setText(MessageText.getString("privacy.view.check.bw"));
/*      */     
/*  820 */     this.i2p_options_link.setCursor(this.i2p_options_link.getDisplay().getSystemCursor(21));
/*  821 */     this.i2p_options_link.setForeground(Colors.blue);
/*  822 */     this.i2p_options_link.addMouseListener(new MouseAdapter() {
/*      */       public void mouseDoubleClick(MouseEvent arg0) {
/*  824 */         openOptions();
/*      */       }
/*      */       
/*  827 */       public void mouseUp(MouseEvent arg0) { openOptions(); }
/*      */       
/*      */ 
/*      */ 
/*      */       private void openOptions()
/*      */       {
/*  833 */         COConfigurationManager.setParameter("privacy.view.check.bw.clicked", true);
/*      */         
/*  835 */         UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*      */         
/*  837 */         if (uif != null)
/*      */         {
/*  839 */           uif.openView(4, "azi2phelper.name");
/*      */         }
/*      */         
/*      */       }
/*  843 */     });
/*  844 */     updateI2PState();
/*      */     
/*  846 */     Utils.makeButtonsEqualWidth(Arrays.asList(new Button[] { this.i2p_install_button, this.i2p_lookup_button }));
/*      */     
/*  848 */     label = new Label(i2p_button_comp, 0);
/*  849 */     gd = new GridData(1808);
/*  850 */     gd.horizontalSpan = 2;
/*  851 */     Utils.setLayoutData(label, gd);
/*      */     
/*      */ 
/*  854 */     Group bottom_comp = new Group(this.cMainComposite, 0);
/*      */     
/*  856 */     gd = new GridData(768);
/*  857 */     Utils.setLayoutData(bottom_comp, gd);
/*      */     
/*  859 */     bottom_comp.setLayout(new GridLayout(2, false));
/*      */     
/*      */ 
/*      */ 
/*  863 */     label = new Label(bottom_comp, 0);
/*  864 */     label.setText(MessageText.getString("authenticator.torrent") + ":");
/*      */     
/*  866 */     Composite torrent_comp = new Composite(bottom_comp, 0);
/*      */     
/*  868 */     gd = new GridData(768);
/*  869 */     Utils.setLayoutData(torrent_comp, gd);
/*  870 */     torrent_comp.setLayout(removeMarginsAndSpacing(new GridLayout(2, false)));
/*      */     
/*  872 */     this.torrent_info = new BufferedLabel(torrent_comp, 536870912);
/*  873 */     gd = new GridData(768);
/*  874 */     Utils.setLayoutData(this.torrent_info, gd);
/*      */     
/*      */ 
/*      */ 
/*  878 */     label = new Label(bottom_comp, 0);
/*  879 */     label.setText(MessageText.getString("ConfigView.section.connection.group.peersources") + ":");
/*      */     
/*  881 */     Composite sources_comp = new Composite(bottom_comp, 0);
/*      */     
/*  883 */     gd = new GridData(768);
/*  884 */     Utils.setLayoutData(sources_comp, gd);
/*      */     
/*  886 */     this.source_buttons = new Button[PEPeerSource.PS_SOURCES.length];
/*      */     
/*  888 */     sources_comp.setLayout(removeMargins(new GridLayout(this.source_buttons.length + 1, false)));
/*      */     
/*      */ 
/*  891 */     for (int i = 0; i < this.source_buttons.length; i++)
/*      */     {
/*  893 */       final String src = PEPeerSource.PS_SOURCES[i];
/*      */       
/*  895 */       String msg_text = "ConfigView.section.connection.peersource." + src;
/*      */       
/*  897 */       Button button = new Button(sources_comp, 32);
/*  898 */       Messages.setLanguageText(button, msg_text);
/*      */       
/*  900 */       this.source_buttons[i] = button;
/*      */       
/*  902 */       button.addSelectionListener(new SelectionAdapter() {
/*      */         public void widgetSelected(SelectionEvent e) {
/*  904 */           boolean selected = ((Button)e.widget).getSelection();
/*      */           
/*  906 */           if (PrivacyView.this.current_dm != null) {
/*  907 */             PrivacyView.this.current_dm.getDownloadState().setPeerSourceEnabled(src, selected);
/*      */           }
/*      */           
/*      */         }
/*  911 */       });
/*  912 */       GridData gridData = new GridData();
/*  913 */       Utils.setLayoutData(button, gridData);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  918 */     label = new Label(bottom_comp, 0);
/*  919 */     label.setText(MessageText.getString("label.ip.filter") + ":");
/*      */     
/*  921 */     Composite ipfilter_comp = new Composite(bottom_comp, 0);
/*      */     
/*  923 */     gd = new GridData(768);
/*  924 */     Utils.setLayoutData(ipfilter_comp, gd);
/*  925 */     ipfilter_comp.setLayout(removeMargins(new GridLayout(2, false)));
/*      */     
/*      */ 
/*  928 */     this.ipfilter_enabled = new Button(ipfilter_comp, 32);
/*  929 */     this.ipfilter_enabled.setText(MessageText.getString("devices.contextmenu.od.enabled"));
/*      */     
/*  931 */     this.ipfilter_enabled.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */       public void widgetSelected(SelectionEvent e)
/*      */       {
/*  935 */         PrivacyView.this.current_dm.getDownloadState().setFlag(256L, !PrivacyView.this.ipfilter_enabled.getSelection());
/*      */       }
/*      */       
/*  938 */     });
/*  939 */     gd = new GridData(768);
/*  940 */     Utils.setLayoutData(this.ipfilter_enabled, gd);
/*      */     
/*      */ 
/*      */ 
/*  944 */     label = new Label(bottom_comp, 0);
/*  945 */     label.setText(MessageText.getString("label.vpn.status") + ":");
/*      */     
/*  947 */     Composite vpn_comp = new Composite(bottom_comp, 0);
/*      */     
/*  949 */     gd = new GridData(768);
/*  950 */     Utils.setLayoutData(vpn_comp, gd);
/*  951 */     vpn_comp.setLayout(removeMargins(new GridLayout(2, false)));
/*      */     
/*  953 */     this.vpn_info = new BufferedLabel(vpn_comp, 536870912);
/*  954 */     gd = new GridData(768);
/*  955 */     Utils.setLayoutData(this.vpn_info, gd);
/*      */     
/*      */ 
/*      */ 
/*  959 */     label = new Label(bottom_comp, 0);
/*  960 */     label.setText(MessageText.getString("label.socks.status") + ":");
/*      */     
/*  962 */     Composite socks_comp = new Composite(bottom_comp, 0);
/*      */     
/*  964 */     gd = new GridData(768);
/*  965 */     Utils.setLayoutData(socks_comp, gd);
/*  966 */     socks_comp.setLayout(removeMargins(new GridLayout(10, false)));
/*      */     
/*  968 */     label = new Label(socks_comp, 0);
/*  969 */     label.setText(MessageText.getString("label.proxy") + ":");
/*      */     
/*  971 */     this.socks_state = new BufferedLabel(socks_comp, 536870912);
/*  972 */     gd = new GridData();
/*  973 */     gd.widthHint = 120;
/*  974 */     Utils.setLayoutData(this.socks_state, gd);
/*      */     
/*      */ 
/*      */ 
/*  978 */     label = new Label(socks_comp, 0);
/*  979 */     label.setText(MessageText.getString("PeersView.state") + ":");
/*      */     
/*  981 */     this.socks_current = new BufferedLabel(socks_comp, 536870912);
/*  982 */     gd = new GridData();
/*  983 */     gd.widthHint = 120;
/*  984 */     Utils.setLayoutData(this.socks_current, gd);
/*      */     
/*      */ 
/*      */ 
/*  988 */     label = new Label(socks_comp, 0);
/*  989 */     label.setText(MessageText.getString("label.fails") + ":");
/*      */     
/*  991 */     this.socks_fails = new BufferedLabel(socks_comp, 536870912);
/*  992 */     gd = new GridData();
/*  993 */     gd.widthHint = 120;
/*  994 */     Utils.setLayoutData(this.socks_fails, gd);
/*      */     
/*      */ 
/*      */ 
/*  998 */     label = new Label(socks_comp, 0);
/*      */     
/* 1000 */     gd = new GridData(768);
/* 1001 */     gd.horizontalAlignment = 32;
/* 1002 */     this.socks_more = new Label(socks_comp, 0);
/* 1003 */     this.socks_more.setText(MessageText.getString("label.more") + "...");
/* 1004 */     Utils.setLayoutData(this.socks_more, gd);
/* 1005 */     this.socks_more.setCursor(this.socks_more.getDisplay().getSystemCursor(21));
/* 1006 */     this.socks_more.setForeground(Colors.blue);
/* 1007 */     this.socks_more.addMouseListener(new MouseAdapter() {
/*      */       public void mouseDoubleClick(MouseEvent arg0) {
/* 1009 */         PrivacyView.this.showSOCKSInfo();
/*      */       }
/*      */       
/* 1012 */       public void mouseUp(MouseEvent arg0) { PrivacyView.this.showSOCKSInfo();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1017 */     });
/* 1018 */     this.sc.addControlListener(new ControlAdapter() {
/*      */       public void controlResized(ControlEvent e) {
/* 1020 */         Rectangle r = PrivacyView.this.sc.getClientArea();
/* 1021 */         Point size = PrivacyView.this.cMainComposite.computeSize(r.width, -1);
/* 1022 */         PrivacyView.this.sc.setMinSize(size);
/*      */       }
/*      */       
/* 1025 */     });
/* 1026 */     swt_updateFields(null, this.current_dm);
/*      */     
/* 1028 */     updatePeersEtc(this.current_dm);
/*      */     
/* 1030 */     updateVPNSocks();
/*      */     
/* 1032 */     Rectangle r = this.sc.getClientArea();
/* 1033 */     Point size = this.cMainComposite.computeSize(r.width, -1);
/* 1034 */     this.sc.setMinSize(size);
/*      */     
/* 1036 */     Utils.relayout(this.cMainComposite);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void setPrivacyLevel(final int level)
/*      */   {
/* 1043 */     if (level != this.privacy_level)
/*      */     {
/* 1045 */       Utils.execSWTThread(new AERunnable()
/*      */       {
/*      */         public void runSupport()
/*      */         {
/* 1049 */           if (level == PrivacyView.this.privacy_level)
/*      */           {
/* 1051 */             return;
/*      */           }
/*      */           
/* 1054 */           PrivacyView.this.privacy_level = level;
/*      */           
/* 1056 */           DownloadManager dm = PrivacyView.this.current_dm;
/*      */           
/* 1058 */           if (dm == null)
/*      */           {
/* 1060 */             return;
/*      */           }
/*      */           
/* 1063 */           DownloadManagerState state = dm.getDownloadState();
/*      */           
/*      */           String[] new_nets;
/*      */           String[] new_nets;
/* 1067 */           if (level == 0)
/*      */           {
/* 1069 */             new_nets = new String[] { "Public" };
/*      */           } else { String[] new_nets;
/* 1071 */             if (level == 1)
/*      */             {
/* 1073 */               new_nets = AENetworkClassifier.AT_NETWORKS;
/*      */             } else { String[] new_nets;
/* 1075 */               if (level == 2)
/*      */               {
/* 1077 */                 new_nets = AENetworkClassifier.AT_NON_PUBLIC;
/*      */               }
/*      */               else
/*      */               {
/* 1081 */                 new_nets = new String[0];
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 1086 */           state.setNetworks(new_nets);
/*      */           
/* 1088 */           if (level != 0)
/*      */           {
/* 1090 */             if (!I2PHelpers.isI2PInstalled())
/*      */             {
/* 1092 */               if (!PrivacyView.this.i2p_install_prompted)
/*      */               {
/* 1094 */                 PrivacyView.this.i2p_install_prompted = true;
/*      */                 
/* 1096 */                 I2PHelpers.installI2PHelper(null, null, new Runnable()
/*      */                 {
/*      */ 
/*      */ 
/*      */                   public void run()
/*      */                   {
/*      */ 
/* 1103 */                     PrivacyView.this.updateI2PState();
/*      */                   }
/*      */                 });
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void swt_updateFields(DownloadManager old_dm, DownloadManager new_dm)
/*      */   {
/* 1119 */     if ((this.cMainComposite == null) || (this.cMainComposite.isDisposed()))
/*      */     {
/* 1121 */       return;
/*      */     }
/*      */     
/* 1124 */     byte[] hash = null;
/*      */     
/* 1126 */     if (new_dm != null)
/*      */     {
/* 1128 */       TOTorrent torrent = new_dm.getTorrent();
/*      */       
/* 1130 */       if (torrent != null) {
/*      */         try
/*      */         {
/* 1133 */           hash = torrent.getHash();
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1141 */     this.i2p_lookup_button.setData("hash", hash);
/*      */     
/* 1143 */     updateI2PState();
/*      */     
/* 1145 */     Utils.disposeComposite(this.i2p_lookup_comp, false);
/*      */     
/* 1147 */     this.i2p_result_summary.setText("");
/* 1148 */     this.i2p_result_list.setText("");
/*      */     
/* 1150 */     if (old_dm != null)
/*      */     {
/* 1152 */       DownloadManagerState state = old_dm.getDownloadState();
/*      */       
/* 1154 */       state.removeListener(this, "networks", 1);
/* 1155 */       state.removeListener(this, "peersources", 1);
/* 1156 */       state.removeListener(this, "flags", 1);
/*      */     }
/*      */     
/* 1159 */     if (new_dm != null)
/*      */     {
/* 1161 */       DownloadManagerState state = new_dm.getDownloadState();
/*      */       
/* 1163 */       state.addListener(this, "networks", 1);
/* 1164 */       state.addListener(this, "peersources", 1);
/* 1165 */       state.addListener(this, "flags", 1);
/*      */       
/* 1167 */       setupNetworksAndSources(new_dm);
/*      */       
/* 1169 */       setupTorrentTracker(new_dm);
/*      */     }
/*      */     else
/*      */     {
/* 1173 */       setupNetworksAndSources(null);
/*      */       
/* 1175 */       setupTorrentTracker(null);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void setupNetworksAndSources(final DownloadManager dm)
/*      */   {
/* 1183 */     Utils.execSWTThread(new AERunnable()
/*      */     {
/*      */       public void runSupport() {
/* 1186 */         PrivacyView.this.enabled_networks.clear();
/* 1187 */         PrivacyView.this.enabled_sources.clear();
/*      */         
/* 1189 */         if ((PrivacyView.this.network_buttons == null) || (PrivacyView.this.network_buttons[0].isDisposed()))
/*      */         {
/* 1191 */           return;
/*      */         }
/*      */         
/* 1194 */         DownloadManagerState state = null;
/*      */         
/* 1196 */         String[] networks = null;
/* 1197 */         String[] sources = null;
/*      */         
/* 1199 */         if (dm != null)
/*      */         {
/* 1201 */           state = dm.getDownloadState();
/*      */           
/* 1203 */           networks = state.getNetworks();
/* 1204 */           sources = state.getPeerSources();
/*      */         }
/*      */         
/* 1207 */         PrivacyView.this.privacy_scale.setEnabled(networks != null);
/*      */         
/* 1209 */         if (networks != null)
/*      */         {
/* 1211 */           PrivacyView.this.enabled_networks.addAll(Arrays.asList(networks));
/*      */           
/*      */           int pl;
/*      */           int pl;
/* 1215 */           if (PrivacyView.this.enabled_networks.contains("Public")) {
/*      */             int pl;
/* 1217 */             if (PrivacyView.this.enabled_networks.size() == 1)
/*      */             {
/* 1219 */               pl = 0;
/*      */             }
/*      */             else {
/* 1222 */               pl = 1;
/*      */             }
/*      */           } else {
/*      */             int pl;
/* 1226 */             if (PrivacyView.this.enabled_networks.size() == 0)
/*      */             {
/* 1228 */               pl = 3;
/*      */             }
/*      */             else
/*      */             {
/* 1232 */               pl = 2;
/*      */             }
/*      */           }
/*      */           
/* 1236 */           PrivacyView.this.privacy_level = pl;
/*      */           
/* 1238 */           PrivacyView.this.privacy_scale.setSelection(pl * 10);
/*      */         }
/*      */         
/* 1241 */         for (int i = 0; i < AENetworkClassifier.AT_NETWORKS.length; i++)
/*      */         {
/* 1243 */           String net = AENetworkClassifier.AT_NETWORKS[i];
/*      */           
/* 1245 */           PrivacyView.this.network_buttons[i].setEnabled(networks != null);
/*      */           
/* 1247 */           PrivacyView.this.network_buttons[i].setSelection(PrivacyView.this.enabled_networks.contains(net));
/*      */         }
/*      */         
/*      */ 
/* 1251 */         if (sources != null)
/*      */         {
/* 1253 */           PrivacyView.this.enabled_sources.addAll(Arrays.asList(sources));
/*      */         }
/*      */         
/* 1256 */         for (int i = 0; i < PEPeerSource.PS_SOURCES.length; i++)
/*      */         {
/* 1258 */           String source = PEPeerSource.PS_SOURCES[i];
/*      */           
/* 1260 */           PrivacyView.this.source_buttons[i].setEnabled((sources != null) && (state.isPeerSourcePermitted(source)));
/*      */           
/* 1262 */           PrivacyView.this.source_buttons[i].setSelection(PrivacyView.this.enabled_sources.contains(source));
/*      */         }
/*      */         
/* 1265 */         if (state != null)
/*      */         {
/* 1267 */           PrivacyView.this.ipfilter_enabled.setEnabled(true);
/*      */           
/* 1269 */           PrivacyView.this.ipfilter_enabled.setSelection(!state.getFlag(256L));
/*      */         }
/*      */         else
/*      */         {
/* 1273 */           PrivacyView.this.ipfilter_enabled.setEnabled(false);
/*      */         }
/*      */         
/*      */ 
/* 1277 */         PrivacyView.this.setupTorrentTracker(dm);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void setupTorrentTracker(final DownloadManager dm)
/*      */   {
/* 1286 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/* 1288 */         if ((PrivacyView.this.torrent_info == null) || (PrivacyView.this.torrent_info.isDisposed()))
/*      */         {
/* 1290 */           return;
/*      */         }
/*      */         
/* 1293 */         TOTorrent torrent = dm == null ? null : dm.getTorrent();
/*      */         
/* 1295 */         if (torrent == null)
/*      */         {
/* 1297 */           PrivacyView.this.torrent_info.setText("");
/* 1298 */           PrivacyView.this.tracker_info.setText("");
/* 1299 */           PrivacyView.this.webseed_info.setText("");
/*      */           
/* 1301 */           return;
/*      */         }
/*      */         
/* 1304 */         boolean private_torrent = torrent.getPrivate();
/*      */         
/* 1306 */         PrivacyView.this.torrent_info.setText(MessageText.getString(private_torrent ? "label.private" : "subs.prop.is_public"));
/*      */         
/* 1308 */         boolean decentralised = false;
/*      */         
/* 1310 */         Set<String> tracker_nets = new HashSet();
/*      */         
/* 1312 */         URL announce_url = torrent.getAnnounceURL();
/*      */         
/* 1314 */         if (announce_url != null)
/*      */         {
/* 1316 */           if (TorrentUtils.isDecentralised(announce_url))
/*      */           {
/* 1318 */             decentralised = true;
/*      */           }
/*      */           else
/*      */           {
/* 1322 */             String net = AENetworkClassifier.categoriseAddress(announce_url.getHost());
/*      */             
/* 1324 */             tracker_nets.add(net);
/*      */           }
/*      */         }
/*      */         
/* 1328 */         TOTorrentAnnounceURLGroup group = torrent.getAnnounceURLGroup();
/*      */         
/* 1330 */         TOTorrentAnnounceURLSet[] sets = group.getAnnounceURLSets();
/*      */         
/* 1332 */         for (TOTorrentAnnounceURLSet set : sets)
/*      */         {
/* 1334 */           URL[] urls = set.getAnnounceURLs();
/*      */           
/* 1336 */           for (URL u : urls)
/*      */           {
/* 1338 */             if (TorrentUtils.isDecentralised(u))
/*      */             {
/* 1340 */               decentralised = true;
/*      */             }
/*      */             else
/*      */             {
/* 1344 */               String net = AENetworkClassifier.categoriseAddress(u.getHost());
/*      */               
/* 1346 */               tracker_nets.add(net);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1351 */         boolean tracker_source_enabled = PrivacyView.this.enabled_sources.contains("Tracker");
/* 1352 */         boolean dht_source_enabled = PrivacyView.this.enabled_sources.contains("DHT");
/*      */         
/* 1354 */         String tracker_str = "";
/*      */         
/* 1356 */         tracker_str = MessageText.getString("label.decentralised");
/*      */         
/* 1358 */         String disabled_str = MessageText.getString("MyTorrentsView.menu.setSpeed.disabled");
/*      */         
/* 1360 */         String net_string = "";
/*      */         
/* 1362 */         if ((dht_source_enabled) && (!private_torrent))
/*      */         {
/*      */ 
/*      */ 
/* 1366 */           for (String net : new String[] { "Public", "I2P" })
/*      */           {
/* 1368 */             if (PrivacyView.this.enabled_networks.contains(net))
/*      */             {
/* 1370 */               net_string = net_string + (net_string.length() == 0 ? "" : ", ") + net;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1375 */         if (net_string.length() == 0)
/*      */         {
/* 1377 */           tracker_str = tracker_str + " (" + disabled_str + ")";
/*      */         }
/*      */         else
/*      */         {
/* 1381 */           tracker_str = tracker_str + " [" + net_string + "]";
/*      */         }
/*      */         
/* 1384 */         for (String net : tracker_nets)
/*      */         {
/* 1386 */           if ((!tracker_source_enabled) || (!PrivacyView.this.enabled_networks.contains(net)))
/*      */           {
/* 1388 */             net = net + " (" + disabled_str + ")";
/*      */           }
/*      */           
/* 1391 */           tracker_str = tracker_str + (tracker_str.length() == 0 ? "" : ", ") + net;
/*      */         }
/*      */         
/* 1394 */         PrivacyView.this.tracker_info.setText(tracker_str);
/*      */         
/*      */ 
/*      */ 
/* 1398 */         Set<String> webseed_nets = new HashSet();
/*      */         
/* 1400 */         ExternalSeedPlugin esp = DownloadManagerController.getExternalSeedPlugin();
/*      */         
/* 1402 */         if (esp != null)
/*      */         {
/* 1404 */           ExternalSeedReader[] seeds = esp.getManualWebSeeds(PluginCoreUtils.wrap(torrent));
/*      */           
/* 1406 */           if (seeds != null)
/*      */           {
/* 1408 */             for (ExternalSeedReader seed : seeds)
/*      */             {
/* 1410 */               URL u = seed.getURL();
/*      */               
/* 1412 */               String net = AENetworkClassifier.categoriseAddress(u.getHost());
/*      */               
/* 1414 */               webseed_nets.add(net);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1419 */         String webseeds_str = "";
/*      */         
/* 1421 */         if (webseed_nets.isEmpty())
/*      */         {
/* 1423 */           webseeds_str = MessageText.getString("PeersView.uniquepiece.none");
/*      */         }
/*      */         else
/*      */         {
/* 1427 */           for (String net : webseed_nets)
/*      */           {
/* 1429 */             if (!PrivacyView.this.enabled_networks.contains(net))
/*      */             {
/* 1431 */               net = net + " (" + disabled_str + ")";
/*      */             }
/*      */             
/* 1434 */             webseeds_str = webseeds_str + (webseeds_str.length() == 0 ? "" : ", ") + net;
/*      */           }
/*      */         }
/*      */         
/* 1438 */         PrivacyView.this.webseed_info.setText(webseeds_str);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   private void updatePeersEtc(final DownloadManager dm)
/*      */   {
/*      */     PEPeerManager pm;
/*      */     
/*      */     final PEPeerManager pm;
/* 1449 */     if (dm != null)
/*      */     {
/* 1451 */       pm = dm.getPeerManager();
/*      */     }
/*      */     else
/*      */     {
/* 1455 */       pm = null;
/*      */     }
/*      */     
/* 1458 */     Utils.execSWTThread(new AERunnable()
/*      */     {
/*      */       public void runSupport() {
/* 1461 */         if ((PrivacyView.this.peer_info == null) || (PrivacyView.this.peer_info.isDisposed()))
/*      */         {
/* 1463 */           return;
/*      */         }
/*      */         
/* 1466 */         if (pm == null)
/*      */         {
/* 1468 */           PrivacyView.this.peer_info.setText(dm == null ? "" : MessageText.getString("privacy.view.dl.not.running"));
/*      */         }
/*      */         else
/*      */         {
/* 1472 */           AEProxySelector proxy_selector = AEProxySelectorFactory.getSelector();
/*      */           
/* 1474 */           Proxy proxy = proxy_selector.getActiveProxy();
/*      */           
/* 1476 */           boolean socks_bad_incoming = false;
/*      */           
/* 1478 */           List<PEPeer> peers = pm.getPeers();
/*      */           
/* 1480 */           String[] all_nets = AENetworkClassifier.AT_NETWORKS;
/*      */           
/* 1482 */           int[] counts = new int[all_nets.length];
/*      */           
/* 1484 */           int incoming = 0;
/* 1485 */           int outgoing = 0;
/* 1486 */           int outgoing_connected = 0;
/*      */           
/* 1488 */           for (PEPeer peer : peers)
/*      */           {
/* 1490 */             String net = PeerUtils.getNetwork(peer);
/*      */             
/* 1492 */             for (int i = 0; i < all_nets.length; i++)
/*      */             {
/* 1494 */               if (all_nets[i] == net)
/*      */               {
/* 1496 */                 counts[i] += 1;
/*      */                 
/* 1498 */                 break;
/*      */               }
/*      */             }
/*      */             
/* 1502 */             boolean is_incoming = peer.isIncoming();
/*      */             
/* 1504 */             if (is_incoming)
/*      */             {
/* 1506 */               incoming++;
/*      */             }
/*      */             else
/*      */             {
/* 1510 */               outgoing++;
/*      */               
/* 1512 */               if (peer.getPeerState() == 30)
/*      */               {
/* 1514 */                 outgoing_connected++;
/*      */               }
/*      */             }
/*      */             
/* 1518 */             if (proxy != null)
/*      */             {
/* 1520 */               if (is_incoming)
/*      */               {
/* 1522 */                 if (!peer.isLANLocal())
/*      */                 {
/*      */                   try {
/* 1525 */                     if (InetAddress.getByAddress(HostNameToIPResolver.hostAddressToBytes(peer.getIp())).isLoopbackAddress()) {
/*      */                       continue;
/*      */                     }
/*      */                   }
/*      */                   catch (Throwable e) {}
/*      */                   
/*      */ 
/* 1532 */                   socks_bad_incoming = true;
/*      */                   
/* 1534 */                   break;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 1540 */           String str = "";
/*      */           
/* 1542 */           for (int i = 0; i < all_nets.length; i++)
/*      */           {
/* 1544 */             int num = counts[i];
/*      */             
/* 1546 */             if (num > 0)
/*      */             {
/* 1548 */               str = str + (str.length() == 0 ? "" : ", ") + all_nets[i] + "=" + num;
/*      */             }
/*      */           }
/*      */           
/* 1552 */           if (str.length() == 0)
/*      */           {
/* 1554 */             str = MessageText.getString("privacy.view.no.peers");
/*      */           }
/*      */           else
/*      */           {
/* 1558 */             str = str + ", " + MessageText.getString("label.incoming") + "=" + incoming + ", " + MessageText.getString("label.outgoing") + "=" + outgoing_connected + "/" + outgoing;
/*      */           }
/*      */           
/*      */ 
/* 1562 */           if (socks_bad_incoming)
/*      */           {
/* 1564 */             str = str + " (" + MessageText.getString("privacy.view.non.local.peer") + ")";
/*      */           }
/*      */           
/* 1567 */           PrivacyView.this.peer_info.setText(str);
/*      */         }
/*      */         
/* 1570 */         PrivacyView.this.updateVPNSocks();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   private void updateVPNSocks()
/*      */   {
/* 1578 */     AEProxySelector proxy_selector = AEProxySelectorFactory.getSelector();
/*      */     
/* 1580 */     Proxy proxy = proxy_selector.getActiveProxy();
/*      */     
/* 1582 */     this.socks_more.setEnabled(proxy != null);
/*      */     
/* 1584 */     if (Constants.isOSX)
/*      */     {
/* 1586 */       this.socks_more.setForeground(proxy == null ? Colors.light_grey : Colors.blue);
/*      */     }
/*      */     
/* 1589 */     this.socks_state.setText(proxy == null ? MessageText.getString("label.inactive") : ((InetSocketAddress)proxy.address()).getHostName());
/*      */     
/* 1591 */     if (proxy == null)
/*      */     {
/* 1593 */       this.socks_current.setText("");
/*      */       
/* 1595 */       this.socks_fails.setText("");
/*      */     }
/*      */     else
/*      */     {
/* 1599 */       long last_con = proxy_selector.getLastConnectionTime();
/* 1600 */       long last_fail = proxy_selector.getLastFailTime();
/* 1601 */       int total_cons = proxy_selector.getConnectionCount();
/* 1602 */       int total_fails = proxy_selector.getFailCount();
/*      */       
/* 1604 */       long now = SystemTime.getMonotonousTime();
/*      */       
/* 1606 */       long con_ago = now - last_con;
/* 1607 */       long fail_ago = now - last_fail;
/*      */       
/*      */       String state_str;
/*      */       String state_str;
/* 1611 */       if (last_fail < 0L)
/*      */       {
/* 1613 */         state_str = "PeerManager.status.ok";
/*      */       }
/*      */       else {
/*      */         String state_str;
/* 1617 */         if (fail_ago > 60000L) {
/*      */           String state_str;
/* 1619 */           if (con_ago < fail_ago)
/*      */           {
/* 1621 */             state_str = "PeerManager.status.ok";
/*      */           }
/*      */           else
/*      */           {
/* 1625 */             state_str = "SpeedView.stats.unknown";
/*      */           }
/*      */         }
/*      */         else {
/* 1629 */           state_str = "ManagerItem.error";
/*      */         }
/*      */       }
/*      */       
/* 1633 */       this.socks_current.setText(MessageText.getString(state_str) + ", con=" + total_cons);
/*      */       
/* 1635 */       long fail_ago_secs = fail_ago / 1000L;
/*      */       
/* 1637 */       if (fail_ago_secs == 0L)
/*      */       {
/* 1639 */         fail_ago_secs = 1L;
/*      */       }
/*      */       
/* 1642 */       this.socks_fails.setText(DisplayFormatters.formatETA(fail_ago_secs, false) + " " + MessageText.getString("label.ago") + ", tot=" + total_fails);
/*      */     }
/*      */     
/* 1645 */     this.vpn_info.setText(NetworkAdmin.getSingleton().getBindStatus());
/*      */   }
/*      */   
/*      */ 
/*      */   private void updateI2PState()
/*      */   {
/* 1651 */     Utils.execSWTThread(new AERunnable()
/*      */     {
/*      */       public void runSupport() {
/* 1654 */         boolean i2p_installed = I2PHelpers.isI2PInstalled();
/*      */         
/* 1656 */         PrivacyView.this.i2p_install_button.setText(MessageText.getString(i2p_installed ? "devices.installed" : "privacy.view.install.i2p"));
/*      */         
/* 1658 */         PrivacyView.this.i2p_install_button.setEnabled(!i2p_installed);
/*      */         
/* 1660 */         PrivacyView.this.i2p_lookup_button.setEnabled((i2p_installed) && (PrivacyView.this.i2p_lookup_button.getData("hash") != null));
/*      */         
/* 1662 */         PrivacyView.this.i2p_options_link.setEnabled(i2p_installed);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void attributeEventOccurred(DownloadManager download, String attribute, int event_type)
/*      */   {
/* 1673 */     setupNetworksAndSources(download);
/*      */   }
/*      */   
/*      */ 
/*      */   private void showSOCKSInfo()
/*      */   {
/* 1679 */     AEProxySelector proxy_selector = AEProxySelectorFactory.getSelector();
/*      */     
/* 1681 */     String info = proxy_selector.getInfo();
/*      */     
/* 1683 */     TextViewerWindow viewer = new TextViewerWindow(MessageText.getString("proxy.info.title"), null, info, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private GridLayout removeMarginsAndSpacing(GridLayout layout)
/*      */   {
/* 1694 */     layout.horizontalSpacing = 0;
/* 1695 */     layout.verticalSpacing = 0;
/* 1696 */     layout.marginHeight = 0;
/* 1697 */     layout.marginWidth = 0;
/*      */     
/* 1699 */     return layout;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private GridLayout removeMargins(GridLayout layout)
/*      */   {
/* 1706 */     layout.marginHeight = 0;
/* 1707 */     layout.marginWidth = 0;
/*      */     
/* 1709 */     return layout;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/PrivacyView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */