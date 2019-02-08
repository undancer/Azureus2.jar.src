/*      */ package com.aelitis.azureus.plugins.net.buddy.swt;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.util.AZ3Functions;
/*      */ import com.aelitis.azureus.core.util.AZ3Functions.provider;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPlugin;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBeta;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBeta.ChatInstance;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBeta.ChatListener;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBeta.ChatMessage;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBeta.ChatParticipant;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBeta.FTUXStateChangeListener;
/*      */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*      */ import java.io.File;
/*      */ import java.io.PrintStream;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.URL;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import org.eclipse.swt.SWT;
/*      */ import org.eclipse.swt.custom.SashForm;
/*      */ import org.eclipse.swt.custom.StackLayout;
/*      */ import org.eclipse.swt.custom.StyleRange;
/*      */ import org.eclipse.swt.custom.StyledText;
/*      */ import org.eclipse.swt.dnd.DropTarget;
/*      */ import org.eclipse.swt.dnd.DropTargetAdapter;
/*      */ import org.eclipse.swt.dnd.DropTargetEvent;
/*      */ import org.eclipse.swt.dnd.FileTransfer;
/*      */ import org.eclipse.swt.dnd.TextTransfer;
/*      */ import org.eclipse.swt.dnd.Transfer;
/*      */ import org.eclipse.swt.events.ControlEvent;
/*      */ import org.eclipse.swt.events.ControlListener;
/*      */ import org.eclipse.swt.events.DisposeEvent;
/*      */ import org.eclipse.swt.events.DisposeListener;
/*      */ import org.eclipse.swt.events.KeyAdapter;
/*      */ import org.eclipse.swt.events.KeyEvent;
/*      */ import org.eclipse.swt.events.KeyListener;
/*      */ import org.eclipse.swt.events.MenuAdapter;
/*      */ import org.eclipse.swt.events.MenuDetectEvent;
/*      */ import org.eclipse.swt.events.MenuDetectListener;
/*      */ import org.eclipse.swt.events.MenuEvent;
/*      */ import org.eclipse.swt.events.MenuListener;
/*      */ import org.eclipse.swt.events.MouseAdapter;
/*      */ import org.eclipse.swt.events.MouseEvent;
/*      */ import org.eclipse.swt.events.MouseTrackListener;
/*      */ import org.eclipse.swt.events.SelectionAdapter;
/*      */ import org.eclipse.swt.events.SelectionEvent;
/*      */ import org.eclipse.swt.graphics.Color;
/*      */ import org.eclipse.swt.graphics.Font;
/*      */ import org.eclipse.swt.graphics.FontData;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.layout.GridData;
/*      */ import org.eclipse.swt.layout.GridLayout;
/*      */ import org.eclipse.swt.widgets.Button;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Label;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Menu;
/*      */ import org.eclipse.swt.widgets.MenuItem;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.eclipse.swt.widgets.Table;
/*      */ import org.eclipse.swt.widgets.TableColumn;
/*      */ import org.eclipse.swt.widgets.TableItem;
/*      */ import org.eclipse.swt.widgets.Text;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.AddressUtils;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FrequencyLimitedDispatcher;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.UrlUtils;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareItem;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareManager;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareResourceDir;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareResourceFile;
/*      */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.utils.LocaleUtilities;
/*      */ import org.gudy.azureus2.plugins.utils.ShortCuts;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities;
/*      */ import org.gudy.azureus2.plugins.utils.subscriptions.SubscriptionManager;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*      */ import org.gudy.azureus2.pluginsimpl.local.utils.FormattersImpl;
/*      */ import org.gudy.azureus2.ui.swt.Messages;
/*      */ import org.gudy.azureus2.ui.swt.URLTransfer;
/*      */ import org.gudy.azureus2.ui.swt.URLTransfer.URLType;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.components.BufferedLabel;
/*      */ import org.gudy.azureus2.ui.swt.components.LinkLabel;
/*      */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.TorrentOpener;
/*      */ 
/*      */ public class BuddyPluginViewBetaChat
/*      */   implements BuddyPluginBeta.ChatListener
/*      */ {
/*  130 */   private static final boolean TEST_LOOPBACK_CHAT = System.getProperty("az.chat.loopback.enable", "0").equals("1");
/*  131 */   private static final boolean DEBUG_ENABLED = BuddyPluginBeta.DEBUG_ENABLED;
/*      */   
/*      */   private static final int MAX_MSG_LENGTH = 400;
/*      */   
/*  135 */   private static final Set<BuddyPluginViewBetaChat> active_windows = new HashSet();
/*      */   
/*  137 */   private static boolean auto_ftux_popout_done = false;
/*      */   
/*      */   private final BuddyPluginView view;
/*      */   private final BuddyPlugin plugin;
/*      */   private final BuddyPluginBeta beta;
/*      */   
/*      */   protected static void createChatWindow(BuddyPluginView view, BuddyPlugin plugin, BuddyPluginBeta.ChatInstance chat)
/*      */   {
/*  145 */     createChatWindow(view, plugin, chat, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void createChatWindow(BuddyPluginView view, BuddyPlugin plugin, BuddyPluginBeta.ChatInstance chat, boolean force_popout)
/*      */   {
/*  155 */     for (BuddyPluginViewBetaChat win : active_windows)
/*      */     {
/*  157 */       if (win.getChat() == chat)
/*      */       {
/*  159 */         Shell existing = win.getShell();
/*      */         
/*  161 */         if (existing.isVisible())
/*      */         {
/*  163 */           existing.setActive();
/*      */         }
/*      */         
/*  166 */         chat.destroy();
/*      */         
/*  168 */         return;
/*      */       }
/*      */     }
/*      */     
/*  172 */     if (!force_popout)
/*      */     {
/*  174 */       if (plugin.getBeta().getWindowsToSidebar())
/*      */       {
/*  176 */         AZ3Functions.provider az3 = AZ3Functions.getProvider();
/*      */         
/*  178 */         if (az3 != null)
/*      */         {
/*  180 */           if (az3.openChat(chat.getNetwork(), chat.getKey()))
/*      */           {
/*  182 */             chat.destroy();
/*      */             
/*  184 */             return;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  191 */     new BuddyPluginViewBetaChat(view, plugin, chat);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private final BuddyPluginBeta.ChatInstance chat;
/*      */   
/*      */   private final LocaleUtilities lu;
/*      */   
/*      */   private Shell shell;
/*      */   
/*      */   private StyledText log;
/*      */   
/*  204 */   private StyleRange[] log_styles = new StyleRange[0];
/*      */   
/*      */   private BufferedLabel table_header;
/*      */   
/*      */   private Table buddy_table;
/*      */   
/*      */   private BufferedLabel status;
/*      */   
/*      */   private Button shared_nick_button;
/*      */   
/*      */   private Text nickname;
/*      */   private Text input_area;
/*      */   private Button rss_button;
/*      */   private DropTarget[] drop_targets;
/*  218 */   private LinkedHashMap<BuddyPluginBeta.ChatMessage, Integer> messages = new LinkedHashMap();
/*  219 */   private List<BuddyPluginBeta.ChatParticipant> participants = new ArrayList();
/*      */   
/*  221 */   private Map<BuddyPluginBeta.ChatParticipant, BuddyPluginBeta.ChatMessage> participant_last_message_map = new HashMap();
/*      */   
/*      */   private boolean table_resort_required;
/*      */   
/*      */   private Font italic_font;
/*      */   
/*      */   private Font bold_font;
/*      */   
/*      */   private Font big_font;
/*      */   
/*      */   private Font small_font;
/*      */   
/*      */   private Color ftux_dark_bg;
/*      */   
/*      */   private Color ftux_dark_fg;
/*      */   
/*      */   private Color ftux_light_bg;
/*      */   private boolean ftux_ok;
/*      */   private boolean build_complete;
/*      */   
/*      */   private BuddyPluginViewBetaChat(BuddyPluginView _view, BuddyPlugin _plugin, BuddyPluginBeta.ChatInstance _chat)
/*      */   {
/*  243 */     this.view = _view;
/*  244 */     this.plugin = _plugin;
/*  245 */     this.chat = _chat;
/*  246 */     this.beta = this.plugin.getBeta();
/*      */     
/*  248 */     this.lu = this.plugin.getPluginInterface().getUtilities().getLocaleUtilities();
/*      */     
/*  250 */     if (this.beta.getStandAloneWindows())
/*      */     {
/*  252 */       this.shell = ShellFactory.createShell((Shell)null, 3312);
/*      */     }
/*      */     else
/*      */     {
/*  256 */       this.shell = ShellFactory.createMainShell(3312);
/*      */     }
/*      */     
/*  259 */     this.shell.addListener(22, new Listener()
/*      */     {
/*      */       public void handleEvent(Event event)
/*      */       {
/*  263 */         BuddyPluginViewBetaChat.this.activate();
/*      */       }
/*      */       
/*  266 */     });
/*  267 */     this.shell.setText(this.lu.getLocalisedMessageText("label.chat") + ": " + this.chat.getName());
/*      */     
/*  269 */     Utils.setShellIcon(this.shell);
/*      */     
/*  271 */     build(this.shell);
/*      */     
/*  273 */     this.shell.addListener(31, new Listener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void handleEvent(Event e)
/*      */       {
/*      */ 
/*      */ 
/*  281 */         if (e.character == '\033')
/*      */         {
/*  283 */           BuddyPluginViewBetaChat.this.close();
/*      */         }
/*      */         
/*      */       }
/*  287 */     });
/*  288 */     this.shell.addControlListener(new ControlListener()
/*      */     {
/*      */       private volatile Rectangle last_position;
/*      */       
/*      */ 
/*  293 */       private FrequencyLimitedDispatcher disp = new FrequencyLimitedDispatcher(new AERunnable()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */ 
/*      */ 
/*  301 */           Rectangle pos = BuddyPluginViewBetaChat.3.this.last_position;
/*      */           
/*  303 */           String str = pos.x + "," + pos.y + "," + pos.width + "," + pos.height;
/*      */           
/*  305 */           COConfigurationManager.setParameter("azbuddy.dchat.ui.last.win.pos", str);
/*      */         }
/*  293 */       }, 1000);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void controlResized(ControlEvent e)
/*      */       {
/*  314 */         handleChange();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void controlMoved(ControlEvent e)
/*      */       {
/*  321 */         handleChange();
/*      */       }
/*      */       
/*      */ 
/*      */       private void handleChange()
/*      */       {
/*  327 */         this.last_position = BuddyPluginViewBetaChat.this.shell.getBounds();
/*      */         
/*  329 */         this.disp.dispatch();
/*      */       }
/*      */       
/*      */ 
/*  333 */     });
/*  334 */     int DEFAULT_WIDTH = 500;
/*  335 */     int DEFAULT_HEIGHT = 500;
/*  336 */     int MIN_WIDTH = 300;
/*  337 */     int MIN_HEIGHT = 150;
/*      */     
/*      */ 
/*  340 */     String str_pos = COConfigurationManager.getStringParameter("azbuddy.dchat.ui.last.win.pos", "");
/*      */     
/*  342 */     Rectangle last_bounds = null;
/*      */     try
/*      */     {
/*  345 */       if ((str_pos != null) && (str_pos.length() > 0))
/*      */       {
/*  347 */         String[] bits = str_pos.split(",");
/*      */         
/*  349 */         if (bits.length == 4)
/*      */         {
/*  351 */           int[] i_bits = new int[4];
/*      */           
/*  353 */           for (int i = 0; i < bits.length; i++)
/*      */           {
/*  355 */             i_bits[i] = Integer.parseInt(bits[i]);
/*      */           }
/*      */           
/*  358 */           last_bounds = new Rectangle(i_bits[0], i_bits[1], Math.max(MIN_WIDTH, i_bits[2]), Math.max(MIN_HEIGHT, i_bits[3]));
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  371 */     if (active_windows.size() > 0)
/*      */     {
/*  373 */       int max_x = 0;
/*  374 */       int max_y = 0;
/*      */       
/*  376 */       for (BuddyPluginViewBetaChat window : active_windows)
/*      */       {
/*  378 */         if (!window.shell.isDisposed())
/*      */         {
/*  380 */           Rectangle rect = window.shell.getBounds();
/*      */           
/*  382 */           max_x = Math.max(max_x, rect.x);
/*  383 */           max_y = Math.max(max_y, rect.y);
/*      */         }
/*      */       }
/*      */       
/*  387 */       Rectangle rect = new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
/*      */       
/*  389 */       rect.x = (max_x + 16);
/*  390 */       rect.y = (max_y + 16);
/*      */       
/*  392 */       if (last_bounds != null)
/*      */       {
/*  394 */         rect.width = last_bounds.width;
/*  395 */         rect.height = last_bounds.height;
/*      */       }
/*      */       
/*  398 */       this.shell.setBounds(rect);
/*      */       
/*  400 */       Utils.verifyShellRect(this.shell, true);
/*      */ 
/*      */ 
/*      */     }
/*  404 */     else if (last_bounds != null)
/*      */     {
/*  406 */       this.shell.setBounds(last_bounds);
/*      */       
/*  408 */       Utils.verifyShellRect(this.shell, true);
/*      */     }
/*      */     else
/*      */     {
/*  412 */       this.shell.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
/*      */       
/*  414 */       Utils.centreWindow(this.shell);
/*      */     }
/*      */     
/*      */ 
/*  418 */     active_windows.add(this);
/*      */     
/*  420 */     this.shell.addDisposeListener(new DisposeListener()
/*      */     {
/*      */ 
/*      */       public void widgetDisposed(DisposeEvent e)
/*      */       {
/*  425 */         BuddyPluginViewBetaChat.active_windows.remove(BuddyPluginViewBetaChat.this);
/*      */       }
/*      */       
/*  428 */     });
/*  429 */     this.shell.open();
/*      */     
/*  431 */     this.shell.forceActive();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected BuddyPluginViewBetaChat(BuddyPluginView _view, BuddyPlugin _plugin, BuddyPluginBeta.ChatInstance _chat, Composite _parent)
/*      */   {
/*  441 */     this.view = _view;
/*  442 */     this.plugin = _plugin;
/*  443 */     this.chat = _chat;
/*  444 */     this.beta = this.plugin.getBeta();
/*      */     
/*  446 */     this.lu = this.plugin.getPluginInterface().getUtilities().getLocaleUtilities();
/*      */     
/*  448 */     build(_parent);
/*      */   }
/*      */   
/*      */ 
/*      */   private Shell getShell()
/*      */   {
/*  454 */     return this.shell;
/*      */   }
/*      */   
/*      */ 
/*      */   private BuddyPluginBeta.ChatInstance getChat()
/*      */   {
/*  460 */     return this.chat;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void build(Composite parent)
/*      */   {
/*  467 */     this.view.registerUI(this.chat);
/*      */     
/*  469 */     boolean public_chat = !this.chat.isPrivateChat();
/*      */     
/*  471 */     GridLayout layout = new GridLayout();
/*  472 */     layout.numColumns = 2;
/*  473 */     layout.marginHeight = 0;
/*  474 */     layout.marginWidth = 0;
/*  475 */     parent.setLayout(layout);
/*  476 */     GridData grid_data = new GridData(1808);
/*  477 */     Utils.setLayoutData(parent, grid_data);
/*      */     
/*  479 */     Composite sash_area = new Composite(parent, 0);
/*  480 */     layout = new GridLayout();
/*  481 */     layout.numColumns = 1;
/*  482 */     layout.marginHeight = 0;
/*  483 */     layout.marginWidth = 0;
/*  484 */     sash_area.setLayout(layout);
/*      */     
/*  486 */     grid_data = new GridData(1808);
/*  487 */     grid_data.horizontalSpan = 2;
/*  488 */     Utils.setLayoutData(sash_area, grid_data);
/*      */     
/*  490 */     SashForm sash = new SashForm(sash_area, 256);
/*  491 */     grid_data = new GridData(1808);
/*  492 */     Utils.setLayoutData(sash, grid_data);
/*      */     
/*  494 */     Composite lhs = new Composite(sash, 0);
/*      */     
/*  496 */     lhs.addDisposeListener(new DisposeListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetDisposed(DisposeEvent arg0)
/*      */       {
/*      */ 
/*  503 */         Font[] fonts = { BuddyPluginViewBetaChat.this.italic_font, BuddyPluginViewBetaChat.this.bold_font, BuddyPluginViewBetaChat.this.big_font, BuddyPluginViewBetaChat.this.small_font };
/*      */         
/*  505 */         for (Font f : fonts)
/*      */         {
/*  507 */           if (f != null)
/*      */           {
/*  509 */             f.dispose();
/*      */           }
/*      */         }
/*      */         
/*  513 */         Color[] colours = { BuddyPluginViewBetaChat.this.ftux_dark_bg, BuddyPluginViewBetaChat.this.ftux_dark_fg, BuddyPluginViewBetaChat.this.ftux_light_bg };
/*      */         
/*  515 */         for (Color c : colours)
/*      */         {
/*  517 */           if (c != null)
/*      */           {
/*  519 */             c.dispose();
/*      */           }
/*      */         }
/*      */         
/*  523 */         if (BuddyPluginViewBetaChat.this.drop_targets != null)
/*      */         {
/*  525 */           for (DropTarget dt : BuddyPluginViewBetaChat.this.drop_targets)
/*      */           {
/*  527 */             dt.dispose();
/*      */           }
/*      */         }
/*      */         
/*  531 */         BuddyPluginViewBetaChat.this.closed();
/*      */       }
/*      */       
/*  534 */     });
/*  535 */     layout = new GridLayout();
/*  536 */     layout.numColumns = 2;
/*  537 */     layout.marginHeight = 0;
/*  538 */     layout.marginWidth = 0;
/*  539 */     layout.marginTop = 4;
/*  540 */     layout.marginLeft = 4;
/*  541 */     lhs.setLayout(layout);
/*  542 */     grid_data = new GridData(1808);
/*  543 */     grid_data.widthHint = 300;
/*  544 */     Utils.setLayoutData(lhs, grid_data);
/*      */     
/*  546 */     final Label menu_drop = new Label(lhs, 0);
/*      */     
/*  548 */     FontData fontData = menu_drop.getFont().getFontData()[0];
/*      */     
/*  550 */     Display display = menu_drop.getDisplay();
/*      */     
/*  552 */     this.italic_font = new Font(display, new FontData(fontData.getName(), fontData.getHeight(), 2));
/*  553 */     this.bold_font = new Font(display, new FontData(fontData.getName(), fontData.getHeight(), 1));
/*  554 */     this.big_font = new Font(display, new FontData(fontData.getName(), (int)(fontData.getHeight() * 1.5D), 1));
/*  555 */     this.small_font = new Font(display, new FontData(fontData.getName(), (int)(fontData.getHeight() * 0.5D), 1));
/*      */     
/*  557 */     this.ftux_dark_bg = new Color(display, 183, 200, 212);
/*  558 */     this.ftux_dark_fg = new Color(display, 0, 81, 134);
/*  559 */     this.ftux_light_bg = new Color(display, 236, 242, 246);
/*      */     
/*  561 */     this.status = new BufferedLabel(lhs, 536887296);
/*  562 */     grid_data = new GridData(768);
/*      */     
/*  564 */     Utils.setLayoutData(this.status, grid_data);
/*  565 */     this.status.setText(MessageText.getString("PeersView.state.pending"));
/*      */     
/*  567 */     Image image = ImageLoader.getInstance().getImage("menu_down");
/*  568 */     menu_drop.setImage(image);
/*  569 */     grid_data = new GridData();
/*  570 */     grid_data.widthHint = image.getBounds().width;
/*  571 */     grid_data.heightHint = image.getBounds().height;
/*  572 */     Utils.setLayoutData(menu_drop, grid_data);
/*      */     
/*  574 */     menu_drop.setCursor(menu_drop.getDisplay().getSystemCursor(21));
/*      */     
/*  576 */     Control status_control = this.status.getControl();
/*      */     
/*  578 */     final Menu status_menu = new Menu(status_control);
/*      */     
/*  580 */     this.status.getControl().setMenu(status_menu);
/*  581 */     menu_drop.setMenu(status_menu);
/*      */     
/*  583 */     menu_drop.addMouseListener(new MouseAdapter() {
/*      */       public void mouseDown(MouseEvent event) {
/*      */         try {
/*  586 */           Point p = status_menu.getDisplay().map(menu_drop, null, event.x, event.y);
/*      */           
/*  588 */           status_menu.setLocation(p);
/*      */           
/*  590 */           status_menu.setVisible(true);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  594 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */     });
/*      */     
/*  599 */     if (public_chat)
/*      */     {
/*  601 */       Menu status_clip_menu = new Menu(lhs.getShell(), 4);
/*  602 */       MenuItem status_clip_item = new MenuItem(status_menu, 64);
/*  603 */       status_clip_item.setMenu(status_clip_menu);
/*  604 */       status_clip_item.setText(MessageText.getString("label.copy.to.clipboard"));
/*      */       
/*  606 */       MenuItem status_mi = new MenuItem(status_clip_menu, 8);
/*  607 */       status_mi.setText(MessageText.getString("azbuddy.dchat.copy.channel.key"));
/*      */       
/*  609 */       status_mi.addSelectionListener(new SelectionAdapter()
/*      */       {
/*      */ 
/*      */         public void widgetSelected(SelectionEvent e)
/*      */         {
/*      */ 
/*  615 */           ClipboardCopy.copyToClipBoard(BuddyPluginViewBetaChat.this.chat.getKey());
/*      */         }
/*      */         
/*  618 */       });
/*  619 */       status_mi = new MenuItem(status_clip_menu, 8);
/*  620 */       status_mi.setText(MessageText.getString("azbuddy.dchat.copy.channel.url"));
/*      */       
/*  622 */       status_mi.addSelectionListener(new SelectionAdapter()
/*      */       {
/*      */ 
/*      */         public void widgetSelected(SelectionEvent e)
/*      */         {
/*      */ 
/*  628 */           ClipboardCopy.copyToClipBoard(BuddyPluginViewBetaChat.this.chat.getURL());
/*      */         }
/*      */         
/*  631 */       });
/*  632 */       status_mi = new MenuItem(status_clip_menu, 8);
/*  633 */       status_mi.setText(MessageText.getString("azbuddy.dchat.copy.rss.url"));
/*      */       
/*  635 */       status_mi.addSelectionListener(new SelectionAdapter()
/*      */       {
/*      */ 
/*      */         public void widgetSelected(SelectionEvent e)
/*      */         {
/*      */ 
/*  641 */           ClipboardCopy.copyToClipBoard("azplug:?id=azbuddy&arg=" + UrlUtils.encode(new StringBuilder().append(BuddyPluginViewBetaChat.this.chat.getURL()).append("&format=rss").toString()));
/*      */         }
/*      */         
/*  644 */       });
/*  645 */       status_mi = new MenuItem(status_clip_menu, 8);
/*  646 */       status_mi.setText(MessageText.getString("azbuddy.dchat.copy.channel.pk"));
/*      */       
/*  648 */       status_mi.addSelectionListener(new SelectionAdapter()
/*      */       {
/*      */ 
/*      */         public void widgetSelected(SelectionEvent e)
/*      */         {
/*      */ 
/*  654 */           ClipboardCopy.copyToClipBoard(Base32.encode(BuddyPluginViewBetaChat.this.chat.getPublicKey()));
/*      */         }
/*      */         
/*  657 */       });
/*  658 */       status_mi = new MenuItem(status_clip_menu, 8);
/*  659 */       status_mi.setText(MessageText.getString("azbuddy.dchat.copy.channel.export"));
/*      */       
/*  661 */       status_mi.addSelectionListener(new SelectionAdapter()
/*      */       {
/*      */ 
/*      */         public void widgetSelected(SelectionEvent e)
/*      */         {
/*      */ 
/*  667 */           ClipboardCopy.copyToClipBoard(BuddyPluginViewBetaChat.this.chat.export());
/*      */         }
/*      */       });
/*      */       
/*  671 */       if (!this.chat.isManaged())
/*      */       {
/*  673 */         Menu status_channel_menu = new Menu(lhs.getShell(), 4);
/*  674 */         MenuItem status_channel_item = new MenuItem(status_menu, 64);
/*  675 */         status_channel_item.setMenu(status_channel_menu);
/*  676 */         status_channel_item.setText(MessageText.getString("azbuddy.dchat.rchans"));
/*      */         
/*      */ 
/*      */ 
/*  680 */         status_mi = new MenuItem(status_channel_menu, 8);
/*  681 */         status_mi.setText(MessageText.getString("azbuddy.dchat.rchans.managed"));
/*      */         
/*  683 */         status_mi.addSelectionListener(new SelectionAdapter()
/*      */         {
/*      */ 
/*      */           public void widgetSelected(SelectionEvent event)
/*      */           {
/*      */             try
/*      */             {
/*  690 */               BuddyPluginBeta.ChatInstance inst = BuddyPluginViewBetaChat.this.chat.getManagedChannel();
/*      */               
/*  692 */               BuddyPluginViewBetaChat.createChatWindow(BuddyPluginViewBetaChat.this.view, BuddyPluginViewBetaChat.this.plugin, inst);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  696 */               Debug.out(e);
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */ 
/*  702 */         });
/*  703 */         status_mi = new MenuItem(status_channel_menu, 8);
/*  704 */         status_mi.setText(MessageText.getString("azbuddy.dchat.rchans.ro"));
/*      */         
/*  706 */         status_mi.addSelectionListener(new SelectionAdapter()
/*      */         {
/*      */ 
/*      */           public void widgetSelected(SelectionEvent event)
/*      */           {
/*      */             try
/*      */             {
/*  713 */               BuddyPluginBeta.ChatInstance inst = BuddyPluginViewBetaChat.this.chat.getReadOnlyChannel();
/*      */               
/*  715 */               BuddyPluginViewBetaChat.createChatWindow(BuddyPluginViewBetaChat.this.view, BuddyPluginViewBetaChat.this.plugin, inst);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  719 */               Debug.out(e);
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */ 
/*  725 */         });
/*  726 */         status_mi = new MenuItem(status_channel_menu, 8);
/*  727 */         status_mi.setText(MessageText.getString("azbuddy.dchat.rchans.rand"));
/*      */         
/*  729 */         status_mi.addSelectionListener(new SelectionAdapter()
/*      */         {
/*      */ 
/*      */           public void widgetSelected(SelectionEvent event)
/*      */           {
/*      */             try
/*      */             {
/*  736 */               byte[] rand = new byte[20];
/*      */               
/*  738 */               RandomUtils.nextSecureBytes(rand);
/*      */               
/*  740 */               BuddyPluginBeta.ChatInstance inst = BuddyPluginViewBetaChat.this.beta.getChat(BuddyPluginViewBetaChat.this.chat.getNetwork(), BuddyPluginViewBetaChat.this.chat.getKey() + " {" + Base32.encode(rand) + "}");
/*      */               
/*  742 */               BuddyPluginViewBetaChat.createChatWindow(BuddyPluginViewBetaChat.this.view, BuddyPluginViewBetaChat.this.plugin, inst);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  746 */               Debug.out(e);
/*      */             }
/*      */           }
/*      */         });
/*  750 */         if (this.beta.isI2PAvailable())
/*      */         {
/*  752 */           status_mi = new MenuItem(status_channel_menu, 8);
/*  753 */           status_mi.setText(MessageText.getString(this.chat.getNetwork() == "I2P" ? "azbuddy.dchat.rchans.pub" : "azbuddy.dchat.rchans.anon"));
/*      */           
/*  755 */           status_mi.addSelectionListener(new SelectionAdapter()
/*      */           {
/*      */ 
/*      */             public void widgetSelected(SelectionEvent event)
/*      */             {
/*      */               try
/*      */               {
/*  762 */                 BuddyPluginBeta.ChatInstance inst = BuddyPluginViewBetaChat.this.beta.getChat(BuddyPluginViewBetaChat.this.chat.getNetwork() == "I2P" ? "Public" : "I2P", BuddyPluginViewBetaChat.this.chat.getKey());
/*      */                 
/*  764 */                 BuddyPluginViewBetaChat.createChatWindow(BuddyPluginViewBetaChat.this.view, BuddyPluginViewBetaChat.this.plugin, inst);
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/*  768 */                 Debug.out(e);
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */       
/*  775 */       final MenuItem fave_mi = new MenuItem(status_menu, 32);
/*  776 */       fave_mi.setText(MessageText.getString("label.fave"));
/*      */       
/*  778 */       fave_mi.addSelectionListener(new SelectionAdapter()
/*      */       {
/*      */ 
/*      */         public void widgetSelected(SelectionEvent e)
/*      */         {
/*      */ 
/*  784 */           BuddyPluginViewBetaChat.this.chat.setFavourite(fave_mi.getSelection());
/*      */         }
/*      */         
/*  787 */       });
/*  788 */       final AZ3Functions.provider az3 = AZ3Functions.getProvider();
/*      */       
/*  790 */       if (az3 != null)
/*      */       {
/*  792 */         MenuItem sis_mi = new MenuItem(status_menu, 8);
/*  793 */         sis_mi.setText(MessageText.getString(Utils.isAZ2UI() ? "label.show.in.tab" : "label.show.in.sidebar"));
/*      */         
/*  795 */         sis_mi.addSelectionListener(new SelectionAdapter()
/*      */         {
/*      */ 
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*      */ 
/*  801 */             az3.openChat(BuddyPluginViewBetaChat.this.chat.getNetwork(), BuddyPluginViewBetaChat.this.chat.getKey());
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*  806 */       addFriendsMenu(status_menu);
/*      */       
/*      */ 
/*      */ 
/*  810 */       Menu advanced_menu = new Menu(status_menu.getShell(), 4);
/*  811 */       MenuItem advanced_menu_item = new MenuItem(status_menu, 64);
/*  812 */       advanced_menu_item.setMenu(advanced_menu);
/*  813 */       advanced_menu_item.setText(MessageText.getString("MyTorrentsView.menu.advancedmenu"));
/*      */       
/*  815 */       final MenuItem persist_mi = new MenuItem(advanced_menu, 32);
/*  816 */       persist_mi.setText(MessageText.getString("azbuddy.dchat.save.messages"));
/*      */       
/*  818 */       persist_mi.addSelectionListener(new SelectionAdapter()
/*      */       {
/*      */ 
/*      */         public void widgetSelected(SelectionEvent e)
/*      */         {
/*      */ 
/*  824 */           BuddyPluginViewBetaChat.this.chat.setSaveMessages(persist_mi.getSelection());
/*      */         }
/*      */         
/*  827 */       });
/*  828 */       final MenuItem log_mi = new MenuItem(advanced_menu, 32);
/*  829 */       log_mi.setText(MessageText.getString("azbuddy.dchat.log.messages"));
/*      */       
/*  831 */       log_mi.addSelectionListener(new SelectionAdapter()
/*      */       {
/*      */ 
/*      */         public void widgetSelected(SelectionEvent e)
/*      */         {
/*      */ 
/*  837 */           BuddyPluginViewBetaChat.this.chat.setLogMessages(log_mi.getSelection());
/*      */         }
/*      */         
/*  840 */       });
/*  841 */       final MenuItem automute_mi = new MenuItem(advanced_menu, 32);
/*  842 */       automute_mi.setText(MessageText.getString("azbuddy.dchat.auto.mute"));
/*      */       
/*  844 */       automute_mi.addSelectionListener(new SelectionAdapter()
/*      */       {
/*      */ 
/*      */         public void widgetSelected(SelectionEvent e)
/*      */         {
/*      */ 
/*  850 */           BuddyPluginViewBetaChat.this.chat.setAutoMute(automute_mi.getSelection());
/*      */         }
/*      */         
/*  853 */       });
/*  854 */       final MenuItem postnotifications_mi = new MenuItem(advanced_menu, 32);
/*  855 */       postnotifications_mi.setText(MessageText.getString("azbuddy.dchat.post.to.notifcations"));
/*      */       
/*  857 */       postnotifications_mi.addSelectionListener(new SelectionAdapter()
/*      */       {
/*      */ 
/*      */         public void widgetSelected(SelectionEvent e)
/*      */         {
/*      */ 
/*  863 */           BuddyPluginViewBetaChat.this.chat.setEnableNotificationsPost(postnotifications_mi.getSelection());
/*      */         }
/*      */         
/*  866 */       });
/*  867 */       final MenuItem disableindicators_mi = new MenuItem(advanced_menu, 32);
/*  868 */       disableindicators_mi.setText(MessageText.getString("azbuddy.dchat.disable.msg.indicators"));
/*      */       
/*  870 */       disableindicators_mi.addSelectionListener(new SelectionAdapter()
/*      */       {
/*      */ 
/*      */         public void widgetSelected(SelectionEvent e)
/*      */         {
/*      */ 
/*  876 */           BuddyPluginViewBetaChat.this.chat.setDisableNewMsgIndications(disableindicators_mi.getSelection());
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*  881 */       });
/*  882 */       status_menu.addMenuListener(new MenuAdapter()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void menuShown(MenuEvent e)
/*      */         {
/*      */ 
/*  889 */           fave_mi.setSelection(BuddyPluginViewBetaChat.this.chat.isFavourite());
/*  890 */           persist_mi.setSelection(BuddyPluginViewBetaChat.this.chat.getSaveMessages());
/*  891 */           log_mi.setSelection(BuddyPluginViewBetaChat.this.chat.getLogMessages());
/*  892 */           automute_mi.setSelection(BuddyPluginViewBetaChat.this.chat.getAutoMute());
/*  893 */           postnotifications_mi.setSelection(BuddyPluginViewBetaChat.this.chat.getEnableNotificationsPost());
/*  894 */           boolean disable_indications = BuddyPluginViewBetaChat.this.chat.getDisableNewMsgIndications();
/*  895 */           disableindicators_mi.setSelection(disable_indications);
/*  896 */           postnotifications_mi.setEnabled(!disable_indications);
/*      */         }
/*      */       });
/*      */     }
/*      */     else {
/*  901 */       final Menu status_priv_menu = new Menu(lhs.getShell(), 4);
/*  902 */       MenuItem status_priv_item = new MenuItem(status_menu, 64);
/*  903 */       status_priv_item.setMenu(status_priv_menu);
/*  904 */       status_priv_item.setText(MessageText.getString("label.private.chat"));
/*      */       
/*  906 */       SelectionAdapter listener = new SelectionAdapter()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void widgetSelected(SelectionEvent e)
/*      */         {
/*      */ 
/*  913 */           BuddyPluginViewBetaChat.this.beta.setPrivateChatState(((Integer)((MenuItem)e.widget).getData()).intValue());
/*      */         }
/*      */         
/*  916 */       };
/*  917 */       MenuItem status_mi = new MenuItem(status_priv_menu, 16);
/*  918 */       status_mi.setText(MessageText.getString("devices.contextmenu.od.enabled"));
/*  919 */       status_mi.setData(Integer.valueOf(3));
/*      */       
/*  921 */       status_mi.addSelectionListener(listener);
/*      */       
/*  923 */       status_mi = new MenuItem(status_priv_menu, 16);
/*  924 */       status_mi.setText(MessageText.getString("label.pinned.only"));
/*  925 */       status_mi.setData(Integer.valueOf(2));
/*      */       
/*  927 */       status_mi.addSelectionListener(listener);
/*      */       
/*  929 */       status_mi = new MenuItem(status_priv_menu, 16);
/*  930 */       status_mi.setText(MessageText.getString("pairing.status.disabled"));
/*  931 */       status_mi.setData(Integer.valueOf(1));
/*      */       
/*  933 */       status_mi.addSelectionListener(listener);
/*      */       
/*      */ 
/*  936 */       status_priv_menu.addMenuListener(new MenuAdapter()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void menuShown(MenuEvent e)
/*      */         {
/*      */ 
/*  943 */           int pc_state = BuddyPluginViewBetaChat.this.beta.getPrivateChatState();
/*      */           
/*  945 */           for (MenuItem mi : status_priv_menu.getItems())
/*      */           {
/*  947 */             mi.setSelection(pc_state == ((Integer)mi.getData()).intValue());
/*      */           }
/*      */           
/*      */         }
/*  951 */       });
/*  952 */       addFriendsMenu(status_menu);
/*      */       
/*  954 */       final MenuItem keep_alive_mi = new MenuItem(status_menu, 32);
/*  955 */       keep_alive_mi.setText(MessageText.getString("label.keep.alive"));
/*      */       
/*  957 */       status_menu.addMenuListener(new MenuAdapter()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void menuShown(MenuEvent e)
/*      */         {
/*      */ 
/*  964 */           keep_alive_mi.setSelection(BuddyPluginViewBetaChat.this.chat.getUserData("AC:KeepAlive") != null);
/*      */         }
/*      */         
/*  967 */       });
/*  968 */       keep_alive_mi.addSelectionListener(new SelectionAdapter()
/*      */       {
/*      */ 
/*      */         public void widgetSelected(SelectionEvent e)
/*      */         {
/*      */ 
/*  974 */           BuddyPluginBeta.ChatInstance clone = (BuddyPluginBeta.ChatInstance)BuddyPluginViewBetaChat.this.chat.getUserData("AC:KeepAlive");
/*      */           
/*  976 */           if (clone != null)
/*      */           {
/*  978 */             clone.destroy();
/*      */             
/*  980 */             clone = null;
/*      */           }
/*      */           else
/*      */           {
/*      */             try {
/*  985 */               clone = BuddyPluginViewBetaChat.this.chat.getClone();
/*      */             }
/*      */             catch (Throwable f) {}
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*  992 */           BuddyPluginViewBetaChat.this.chat.setUserData("AC:KeepAlive", clone);
/*      */         }
/*      */         
/*  995 */       });
/*  996 */       final AZ3Functions.provider az3 = AZ3Functions.getProvider();
/*      */       
/*  998 */       if (az3 != null)
/*      */       {
/* 1000 */         MenuItem sis_mi = new MenuItem(status_menu, 8);
/* 1001 */         sis_mi.setText(MessageText.getString("label.show.in.sidebar"));
/*      */         
/* 1003 */         sis_mi.addSelectionListener(new SelectionAdapter()
/*      */         {
/*      */ 
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*      */ 
/* 1009 */             az3.openChat(BuddyPluginViewBetaChat.this.chat.getNetwork(), BuddyPluginViewBetaChat.this.chat.getKey());
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     
/* 1015 */     final Composite ftux_stack = new Composite(lhs, 0);
/* 1016 */     grid_data = new GridData(1808);
/* 1017 */     grid_data.horizontalSpan = 2;
/* 1018 */     Utils.setLayoutData(ftux_stack, grid_data);
/*      */     
/* 1020 */     final StackLayout stack_layout = new StackLayout();
/* 1021 */     ftux_stack.setLayout(stack_layout);
/*      */     
/* 1023 */     final Composite log_holder = new Composite(ftux_stack, 2048);
/*      */     
/* 1025 */     final Composite ftux_holder = new Composite(ftux_stack, 2048);
/*      */     
/*      */ 
/*      */ 
/* 1029 */     layout = new GridLayout();
/* 1030 */     layout.numColumns = 2;
/* 1031 */     layout.marginHeight = 0;
/* 1032 */     layout.marginWidth = 0;
/* 1033 */     layout.horizontalSpacing = 0;
/* 1034 */     layout.verticalSpacing = 0;
/* 1035 */     ftux_holder.setLayout(layout);
/*      */     
/* 1037 */     ftux_holder.setBackground(this.ftux_light_bg);
/*      */     
/*      */ 
/*      */ 
/* 1041 */     Composite ftux_top_area = new Composite(ftux_holder, 0);
/* 1042 */     layout = new GridLayout();
/* 1043 */     layout.numColumns = 1;
/* 1044 */     layout.marginHeight = 0;
/* 1045 */     layout.marginWidth = 0;
/* 1046 */     layout.verticalSpacing = 0;
/* 1047 */     ftux_top_area.setLayout(layout);
/*      */     
/* 1049 */     grid_data = new GridData(768);
/* 1050 */     grid_data.horizontalSpan = 2;
/* 1051 */     grid_data.heightHint = 30;
/* 1052 */     Utils.setLayoutData(ftux_top_area, grid_data);
/* 1053 */     ftux_top_area.setBackground(this.ftux_dark_bg);
/*      */     
/*      */ 
/* 1056 */     Label ftux_top = new Label(ftux_top_area, 64);
/* 1057 */     grid_data = new GridData(16384, 16777216, true, true);
/* 1058 */     grid_data.horizontalIndent = 8;
/* 1059 */     Utils.setLayoutData(ftux_top, grid_data);
/*      */     
/* 1061 */     ftux_top.setAlignment(16384);
/* 1062 */     ftux_top.setBackground(this.ftux_dark_bg);
/* 1063 */     ftux_top.setForeground(this.ftux_dark_fg);
/* 1064 */     ftux_top.setFont(this.big_font);
/* 1065 */     ftux_top.setText(MessageText.getString("azbuddy.dchat.ftux.welcome"));
/*      */     
/*      */ 
/*      */ 
/* 1069 */     Label ftux_hack = new Label(ftux_holder, 0);
/* 1070 */     grid_data = new GridData();
/* 1071 */     grid_data.heightHint = 40;
/* 1072 */     grid_data.widthHint = 0;
/* 1073 */     Utils.setLayoutData(ftux_hack, grid_data);
/*      */     
/* 1075 */     StyledText ftux_middle = new StyledText(ftux_holder, 524872);
/*      */     
/* 1077 */     grid_data = new GridData(1808);
/* 1078 */     grid_data.horizontalSpan = 1;
/* 1079 */     grid_data.verticalIndent = 4;
/* 1080 */     grid_data.horizontalIndent = 16;
/* 1081 */     Utils.setLayoutData(ftux_middle, grid_data);
/*      */     
/* 1083 */     ftux_middle.setBackground(this.ftux_light_bg);
/*      */     
/* 1085 */     String info1_text = "Vuze chat allows you to communicate with other Vuze users directly by sending and receiving messages.\nIt is a decentralized chat system - there are no central servers involved, all messages are passed directly between Vuze users.\nConsequently Vuze has absolutely no control over message content. In particular no mechanism exists (nor is possible) for Vuze to moderate or otherwise control either messages or the users that send messages.";
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1090 */     String info2_text = "I UNDERSTAND AND AGREE that Vuze has no responsibility whatsoever with my enabling this function and using chat.";
/*      */     
/*      */ 
/* 1093 */     String[] info_lines = info1_text.split("\n");
/*      */     
/* 1095 */     for (String line : info_lines)
/*      */     {
/* 1097 */       ftux_middle.append(line);
/*      */       
/* 1099 */       if (line != info_lines[(info_lines.length - 1)])
/*      */       {
/* 1101 */         ftux_middle.append("\n");
/*      */         
/* 1103 */         int pos = ftux_middle.getText().length();
/*      */         
/*      */ 
/*      */ 
/* 1107 */         ftux_middle.append("​");
/*      */         
/* 1109 */         StyleRange styleRange = new StyleRange();
/* 1110 */         styleRange.start = pos;
/* 1111 */         styleRange.length = 1;
/* 1112 */         styleRange.font = this.big_font;
/*      */         
/* 1114 */         ftux_middle.setStyleRange(styleRange);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1120 */     Composite ftux_check_area = new Composite(ftux_holder, 0);
/* 1121 */     layout = new GridLayout();
/* 1122 */     layout.marginLeft = 0;
/* 1123 */     layout.marginWidth = 0;
/* 1124 */     layout.numColumns = 2;
/* 1125 */     ftux_check_area.setLayout(layout);
/*      */     
/* 1127 */     grid_data = new GridData(768);
/* 1128 */     grid_data.horizontalSpan = 2;
/* 1129 */     Utils.setLayoutData(ftux_check_area, grid_data);
/* 1130 */     ftux_check_area.setBackground(this.ftux_light_bg);
/*      */     
/* 1132 */     final Button ftux_check = new Button(ftux_check_area, 32);
/* 1133 */     grid_data = new GridData();
/* 1134 */     grid_data.horizontalIndent = 16;
/* 1135 */     Utils.setLayoutData(ftux_check, grid_data);
/* 1136 */     ftux_check.setBackground(this.ftux_light_bg);
/*      */     
/* 1138 */     Label ftux_check_test = new Label(ftux_check_area, 64);
/* 1139 */     grid_data = new GridData(768);
/* 1140 */     Utils.setLayoutData(ftux_check_test, grid_data);
/*      */     
/* 1142 */     ftux_check_test.setBackground(this.ftux_light_bg);
/* 1143 */     ftux_check_test.setText(info2_text);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1148 */     final StyledText ftux_bottom = new StyledText(ftux_holder, 524360);
/* 1149 */     grid_data = new GridData(768);
/* 1150 */     grid_data.horizontalSpan = 2;
/* 1151 */     grid_data.horizontalIndent = 16;
/* 1152 */     Utils.setLayoutData(ftux_bottom, grid_data);
/*      */     
/* 1154 */     ftux_bottom.setBackground(this.ftux_light_bg);
/* 1155 */     ftux_bottom.setFont(this.bold_font);
/* 1156 */     ftux_bottom.setText(MessageText.getString("azbuddy.dchat.ftux.footer") + " ");
/*      */     
/*      */ 
/* 1159 */     int start = ftux_bottom.getText().length();
/*      */     
/* 1161 */     String url = MessageText.getString("faq.legal.url");
/* 1162 */     String url_text = MessageText.getString("label.more.dot");
/*      */     
/* 1164 */     ftux_bottom.append(url_text);
/*      */     
/* 1166 */     StyleRange styleRange = new StyleRange();
/* 1167 */     styleRange.start = start;
/* 1168 */     styleRange.length = url_text.length();
/* 1169 */     styleRange.foreground = Colors.blue;
/* 1170 */     styleRange.underline = true;
/*      */     
/* 1172 */     styleRange.data = url;
/*      */     
/* 1174 */     ftux_bottom.setStyleRange(styleRange);
/*      */     
/*      */ 
/* 1177 */     ftux_bottom.addListener(4, new Listener()
/*      */     {
/*      */ 
/*      */       public void handleEvent(Event event)
/*      */       {
/* 1182 */         int offset = ftux_bottom.getOffsetAtLocation(new Point(event.x, event.y));
/* 1183 */         StyleRange style = ftux_bottom.getStyleRangeAtOffset(offset);
/*      */         
/* 1185 */         if (style != null)
/*      */         {
/* 1187 */           String url = (String)style.data;
/*      */           try
/*      */           {
/* 1190 */             Utils.launch(new URL(url));
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1194 */             Debug.out(e);
/*      */           }
/*      */           
/*      */         }
/*      */       }
/* 1199 */     });
/* 1200 */     Label ftux_line = new Label(ftux_holder, 258);
/* 1201 */     grid_data = new GridData(768);
/* 1202 */     grid_data.horizontalSpan = 2;
/* 1203 */     grid_data.verticalIndent = 4;
/* 1204 */     Utils.setLayoutData(ftux_line, grid_data);
/*      */     
/* 1206 */     Composite ftux_button_area = new Composite(ftux_holder, 0);
/* 1207 */     layout = new GridLayout();
/* 1208 */     layout.numColumns = 2;
/* 1209 */     ftux_button_area.setLayout(layout);
/*      */     
/* 1211 */     grid_data = new GridData(768);
/* 1212 */     grid_data.horizontalSpan = 2;
/* 1213 */     Utils.setLayoutData(ftux_button_area, grid_data);
/* 1214 */     ftux_button_area.setBackground(Colors.white);
/*      */     
/* 1216 */     Label filler = new Label(ftux_button_area, 0);
/* 1217 */     grid_data = new GridData(768);
/* 1218 */     Utils.setLayoutData(filler, grid_data);
/* 1219 */     filler.setBackground(Colors.white);
/*      */     
/* 1221 */     final Button ftux_accept = new Button(ftux_button_area, 8);
/* 1222 */     grid_data = new GridData();
/* 1223 */     grid_data.horizontalAlignment = 131072;
/* 1224 */     grid_data.widthHint = 60;
/* 1225 */     Utils.setLayoutData(ftux_accept, grid_data);
/*      */     
/* 1227 */     ftux_accept.setText(MessageText.getString("label.accept"));
/*      */     
/* 1229 */     ftux_accept.setEnabled(false);
/*      */     
/* 1231 */     ftux_accept.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */       public void widgetSelected(SelectionEvent e)
/*      */       {
/* 1235 */         BuddyPluginViewBetaChat.this.beta.setFTUXAccepted(true);
/*      */       }
/*      */       
/* 1238 */     });
/* 1239 */     ftux_check.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */       public void widgetSelected(SelectionEvent e)
/*      */       {
/* 1243 */         ftux_accept.setEnabled(ftux_check.getSelection());
/*      */       }
/*      */       
/*      */ 
/* 1247 */     });
/* 1248 */     layout = new GridLayout();
/* 1249 */     layout.numColumns = 1;
/* 1250 */     layout.marginHeight = 0;
/* 1251 */     layout.marginWidth = 0;
/* 1252 */     layout.marginLeft = 4;
/* 1253 */     log_holder.setLayout(layout);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1258 */     this.log = new StyledText(log_holder, 524872);
/* 1259 */     grid_data = new GridData(1808);
/* 1260 */     grid_data.horizontalSpan = 1;
/*      */     
/* 1262 */     Utils.setLayoutData(this.log, grid_data);
/*      */     
/*      */ 
/* 1265 */     this.log.setEditable(false);
/*      */     
/* 1267 */     log_holder.setBackground(this.log.getBackground());
/*      */     
/* 1269 */     final Menu log_menu = new Menu(this.log);
/*      */     
/* 1271 */     this.log.setMenu(log_menu);
/*      */     
/* 1273 */     this.log.addMenuDetectListener(new MenuDetectListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void menuDetected(MenuDetectEvent e)
/*      */       {
/*      */ 
/* 1280 */         e.doit = false;
/*      */         
/* 1282 */         boolean handled = false;
/*      */         
/* 1284 */         for (MenuItem mi : log_menu.getItems())
/*      */         {
/* 1286 */           mi.dispose();
/*      */         }
/*      */         try
/*      */         {
/* 1290 */           Point mapped = BuddyPluginViewBetaChat.this.log.getDisplay().map(null, BuddyPluginViewBetaChat.this.log, new Point(e.x, e.y));
/*      */           
/* 1292 */           int offset = BuddyPluginViewBetaChat.this.log.getOffsetAtLocation(mapped);
/*      */           
/* 1294 */           StyleRange sr = BuddyPluginViewBetaChat.this.log.getStyleRangeAtOffset(offset);
/*      */           
/* 1296 */           if (sr != null)
/*      */           {
/* 1298 */             Object data = sr.data;
/*      */             
/* 1300 */             if ((data instanceof BuddyPluginBeta.ChatParticipant))
/*      */             {
/* 1302 */               BuddyPluginBeta.ChatParticipant cp = (BuddyPluginBeta.ChatParticipant)data;
/*      */               
/* 1304 */               List<BuddyPluginBeta.ChatParticipant> cps = new ArrayList();
/*      */               
/* 1306 */               cps.add(cp);
/*      */               
/* 1308 */               BuddyPluginViewBetaChat.this.buildParticipantMenu(log_menu, cps);
/*      */               
/* 1310 */               handled = true;
/*      */             }
/* 1312 */             else if ((data instanceof String))
/*      */             {
/* 1314 */               String url_str = (String)sr.data;
/*      */               
/* 1316 */               String str = url_str;
/*      */               
/* 1318 */               if (str.length() > 50)
/*      */               {
/* 1320 */                 str = str.substring(0, 50) + "...";
/*      */               }
/*      */               
/*      */ 
/*      */ 
/* 1325 */               if ((BuddyPluginViewBetaChat.this.chat.isAnonymous()) && (url_str.toLowerCase(Locale.US).startsWith("magnet:")))
/*      */               {
/* 1327 */                 String[] magnet_uri = { url_str };
/*      */                 
/* 1329 */                 Set<String> networks = UrlUtils.extractNetworks(magnet_uri);
/*      */                 
/* 1331 */                 String i2p_only_uri = magnet_uri[0] + "&net=" + UrlUtils.encode("I2P");
/*      */                 
/* 1333 */                 String i2p_only_str = i2p_only_uri;
/*      */                 
/* 1335 */                 if (i2p_only_str.length() > 50)
/*      */                 {
/* 1337 */                   i2p_only_str = i2p_only_str.substring(0, 50) + "...";
/*      */                 }
/*      */                 
/* 1340 */                 i2p_only_str = BuddyPluginViewBetaChat.this.lu.getLocalisedMessageText("azbuddy.dchat.open.i2p.magnet") + ": " + i2p_only_str;
/*      */                 
/* 1342 */                 final MenuItem mi_open_i2p_vuze = new MenuItem(log_menu, 8);
/*      */                 
/* 1344 */                 mi_open_i2p_vuze.setText(i2p_only_str);
/* 1345 */                 mi_open_i2p_vuze.setData(i2p_only_uri);
/*      */                 
/* 1347 */                 mi_open_i2p_vuze.addSelectionListener(new SelectionAdapter()
/*      */                 {
/*      */ 
/*      */ 
/*      */                   public void widgetSelected(SelectionEvent e)
/*      */                   {
/*      */ 
/* 1354 */                     String url_str = (String)mi_open_i2p_vuze.getData();
/*      */                     
/* 1356 */                     if (url_str != null)
/*      */                     {
/* 1358 */                       TorrentOpener.openTorrent(url_str);
/*      */                     }
/*      */                   }
/*      */                 });
/*      */                 
/* 1363 */                 if ((networks.size() != 1) || (networks.iterator().next() != "I2P"))
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1369 */                   str = BuddyPluginViewBetaChat.this.lu.getLocalisedMessageText("azbuddy.dchat.open.magnet") + ": " + str;
/*      */                   
/* 1371 */                   final MenuItem mi_open_vuze = new MenuItem(log_menu, 8);
/*      */                   
/* 1373 */                   mi_open_vuze.setText(str);
/* 1374 */                   mi_open_vuze.setData(url_str);
/*      */                   
/* 1376 */                   mi_open_vuze.addSelectionListener(new SelectionAdapter()
/*      */                   {
/*      */ 
/*      */ 
/*      */                     public void widgetSelected(SelectionEvent e)
/*      */                     {
/*      */ 
/* 1383 */                       String url_str = (String)mi_open_vuze.getData();
/*      */                       
/* 1385 */                       if (url_str != null)
/*      */                       {
/* 1387 */                         TorrentOpener.openTorrent(url_str);
/*      */                       }
/*      */                     }
/*      */                   });
/*      */                 }
/*      */               }
/*      */               else
/*      */               {
/* 1395 */                 str = BuddyPluginViewBetaChat.this.lu.getLocalisedMessageText("azbuddy.dchat.open.in.vuze") + ": " + str;
/*      */                 
/* 1397 */                 final MenuItem mi_open_vuze = new MenuItem(log_menu, 8);
/*      */                 
/* 1399 */                 mi_open_vuze.setText(str);
/* 1400 */                 mi_open_vuze.setData(url_str);
/*      */                 
/* 1402 */                 mi_open_vuze.addSelectionListener(new SelectionAdapter()
/*      */                 {
/*      */ 
/*      */ 
/*      */                   public void widgetSelected(SelectionEvent e)
/*      */                   {
/*      */ 
/* 1409 */                     String url_str = (String)mi_open_vuze.getData();
/*      */                     
/* 1411 */                     if (url_str != null)
/*      */                     {
/* 1413 */                       String lc_url_str = url_str.toLowerCase(Locale.US);
/*      */                       
/* 1415 */                       if (lc_url_str.startsWith("chat:")) {
/*      */                         try
/*      */                         {
/* 1418 */                           BuddyPluginViewBetaChat.this.beta.handleURI(url_str, true);
/*      */                         }
/*      */                         catch (Throwable f)
/*      */                         {
/* 1422 */                           Debug.out(f);
/*      */                         }
/*      */                         
/*      */                       }
/*      */                       else {
/* 1427 */                         TorrentOpener.openTorrent(url_str);
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 });
/*      */               }
/*      */               
/* 1434 */               final MenuItem mi_open_ext = new MenuItem(log_menu, 8);
/*      */               
/* 1436 */               mi_open_ext.setText(BuddyPluginViewBetaChat.this.lu.getLocalisedMessageText("azbuddy.dchat.open.in.browser"));
/*      */               
/* 1438 */               mi_open_ext.addSelectionListener(new SelectionAdapter()
/*      */               {
/*      */ 
/*      */ 
/*      */                 public void widgetSelected(SelectionEvent e)
/*      */                 {
/*      */ 
/* 1445 */                   String url_str = (String)mi_open_ext.getData();
/*      */                   
/* 1447 */                   Utils.launch(url_str);
/*      */                 }
/*      */                 
/* 1450 */               });
/* 1451 */               new MenuItem(log_menu, 2);
/*      */               
/* 1453 */               if ((BuddyPluginViewBetaChat.this.chat.isAnonymous()) && (url_str.toLowerCase(Locale.US).startsWith("magnet:")))
/*      */               {
/* 1455 */                 String[] magnet_uri = { url_str };
/*      */                 
/* 1457 */                 Set<String> networks = UrlUtils.extractNetworks(magnet_uri);
/*      */                 
/* 1459 */                 String i2p_only_uri = magnet_uri[0] + "&net=" + UrlUtils.encode("I2P");
/*      */                 
/* 1461 */                 final MenuItem mi_copy_i2p_clip = new MenuItem(log_menu, 8);
/*      */                 
/* 1463 */                 mi_copy_i2p_clip.setText(BuddyPluginViewBetaChat.this.lu.getLocalisedMessageText("azbuddy.dchat.copy.i2p.magnet"));
/* 1464 */                 mi_copy_i2p_clip.setData(i2p_only_uri);
/*      */                 
/* 1466 */                 mi_copy_i2p_clip.addSelectionListener(new SelectionAdapter()
/*      */                 {
/*      */ 
/*      */ 
/*      */                   public void widgetSelected(SelectionEvent e)
/*      */                   {
/*      */ 
/* 1473 */                     String url_str = (String)mi_copy_i2p_clip.getData();
/*      */                     
/* 1475 */                     if (url_str != null)
/*      */                     {
/* 1477 */                       ClipboardCopy.copyToClipBoard(url_str);
/*      */                     }
/*      */                   }
/*      */                 });
/*      */                 
/* 1482 */                 if ((networks.size() != 1) || (networks.iterator().next() != "I2P"))
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1488 */                   final MenuItem mi_copy_clip = new MenuItem(log_menu, 8);
/*      */                   
/* 1490 */                   mi_copy_clip.setText(BuddyPluginViewBetaChat.this.lu.getLocalisedMessageText("azbuddy.dchat.copy.magnet"));
/* 1491 */                   mi_copy_clip.setData(url_str);
/*      */                   
/* 1493 */                   mi_copy_clip.addSelectionListener(new SelectionAdapter()
/*      */                   {
/*      */ 
/*      */ 
/*      */                     public void widgetSelected(SelectionEvent e)
/*      */                     {
/*      */ 
/* 1500 */                       String url_str = (String)mi_copy_clip.getData();
/*      */                       
/* 1502 */                       if (url_str != null)
/*      */                       {
/* 1504 */                         ClipboardCopy.copyToClipBoard(url_str);
/*      */                       }
/*      */                     }
/*      */                   });
/*      */                 }
/*      */               }
/*      */               else
/*      */               {
/* 1512 */                 final MenuItem mi_copy_clip = new MenuItem(log_menu, 8);
/*      */                 
/* 1514 */                 mi_copy_clip.setText(BuddyPluginViewBetaChat.this.lu.getLocalisedMessageText("label.copy.to.clipboard"));
/* 1515 */                 mi_copy_clip.setData(url_str);
/*      */                 
/* 1517 */                 mi_copy_clip.addSelectionListener(new SelectionAdapter()
/*      */                 {
/*      */ 
/*      */ 
/*      */                   public void widgetSelected(SelectionEvent e)
/*      */                   {
/*      */ 
/* 1524 */                     String url_str = (String)mi_copy_clip.getData();
/*      */                     
/* 1526 */                     if (url_str != null)
/*      */                     {
/* 1528 */                       ClipboardCopy.copyToClipBoard(url_str);
/*      */                     }
/*      */                   }
/*      */                 });
/*      */               }
/*      */               
/* 1534 */               if (url_str.toLowerCase().startsWith("http"))
/*      */               {
/* 1536 */                 mi_open_ext.setData(url_str);
/*      */                 
/* 1538 */                 mi_open_ext.setEnabled(true);
/*      */               }
/*      */               else
/*      */               {
/* 1542 */                 mi_open_ext.setEnabled(false);
/*      */               }
/*      */               
/* 1545 */               handled = true;
/*      */ 
/*      */             }
/* 1548 */             else if (Constants.isCVSVersion())
/*      */             {
/* 1550 */               if ((sr instanceof BuddyPluginViewBetaChat.MyStyleRange))
/*      */               {
/* 1552 */                 final BuddyPluginViewBetaChat.MyStyleRange msr = (BuddyPluginViewBetaChat.MyStyleRange)sr;
/*      */                 
/* 1554 */                 MenuItem item = new MenuItem(log_menu, 0);
/*      */                 
/* 1556 */                 item.setText(MessageText.getString("label.copy.to.clipboard"));
/*      */                 
/* 1558 */                 item.addSelectionListener(new SelectionAdapter()
/*      */                 {
/*      */ 
/*      */ 
/*      */                   public void widgetSelected(SelectionEvent e)
/*      */                   {
/*      */ 
/*      */ 
/* 1566 */                     ClipboardCopy.copyToClipBoard(BuddyPluginViewBetaChat.MyStyleRange.access$1800(msr).getMessage());
/*      */                   }
/*      */                   
/* 1569 */                 });
/* 1570 */                 handled = true;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (Throwable f) {}
/*      */         
/*      */ 
/* 1578 */         if (!handled)
/*      */         {
/* 1580 */           final String text = BuddyPluginViewBetaChat.this.log.getSelectionText();
/*      */           
/* 1582 */           if ((text != null) && (text.length() > 0))
/*      */           {
/* 1584 */             MenuItem item = new MenuItem(log_menu, 0);
/*      */             
/* 1586 */             item.setText(MessageText.getString("label.copy.to.clipboard"));
/*      */             
/* 1588 */             item.addSelectionListener(new SelectionAdapter()
/*      */             {
/*      */ 
/*      */ 
/*      */               public void widgetSelected(SelectionEvent e)
/*      */               {
/*      */ 
/*      */ 
/* 1596 */                 ClipboardCopy.copyToClipBoard(text);
/*      */               }
/*      */               
/* 1599 */             });
/* 1600 */             handled = true;
/*      */           }
/*      */         }
/*      */         
/* 1604 */         if (handled)
/*      */         {
/* 1606 */           e.doit = true;
/*      */         }
/*      */         
/*      */       }
/* 1610 */     });
/* 1611 */     this.log.addListener(8, new Listener()
/*      */     {
/*      */ 
/*      */       public void handleEvent(Event e)
/*      */       {
/*      */ 
/*      */         try
/*      */         {
/*      */ 
/* 1620 */           int offset = BuddyPluginViewBetaChat.this.log.getOffsetAtLocation(new Point(e.x, e.y));
/*      */           
/* 1622 */           for (int i = 0; i < BuddyPluginViewBetaChat.this.log_styles.length; i++)
/*      */           {
/* 1624 */             StyleRange sr = BuddyPluginViewBetaChat.this.log_styles[i];
/*      */             
/* 1626 */             Object data = sr.data;
/*      */             
/* 1628 */             if ((data != null) && (offset >= sr.start) && (offset < sr.start + sr.length))
/*      */             {
/* 1630 */               boolean anon_chat = BuddyPluginViewBetaChat.this.chat.isAnonymous();
/*      */               
/* 1632 */               if ((data instanceof String))
/*      */               {
/* 1634 */                 final String url_str = (String)data;
/*      */                 
/* 1636 */                 String lc_url_str = url_str.toLowerCase(Locale.US);
/*      */                 
/* 1638 */                 if (lc_url_str.startsWith("chat:"))
/*      */                 {
/*      */ 
/*      */ 
/* 1642 */                   if ((anon_chat) && (!lc_url_str.startsWith("chat:anon:")))
/*      */                   {
/* 1644 */                     return;
/*      */                   }
/*      */                   try
/*      */                   {
/* 1648 */                     BuddyPluginViewBetaChat.this.beta.handleURI(url_str, true);
/*      */                   }
/*      */                   catch (Throwable f)
/*      */                   {
/* 1652 */                     Debug.out(f);
/*      */                   }
/*      */                   
/*      */                 }
/*      */                 else
/*      */                 {
/* 1658 */                   if (anon_chat) {
/*      */                     try
/*      */                     {
/* 1661 */                       String host = new URL(lc_url_str).getHost();
/*      */                       
/*      */ 
/*      */ 
/* 1665 */                       if (AENetworkClassifier.categoriseAddress(host) == "Public")
/*      */                       {
/* 1667 */                         return;
/*      */                       }
/*      */                     }
/*      */                     catch (Throwable f)
/*      */                     {
/* 1672 */                       return;
/*      */                     }
/*      */                   }
/*      */                   
/* 1676 */                   if ((lc_url_str.contains(".torrent")) || (UrlUtils.parseTextForMagnets(url_str) != null))
/*      */                   {
/*      */ 
/* 1679 */                     TorrentOpener.openTorrent(url_str);
/*      */ 
/*      */ 
/*      */                   }
/* 1683 */                   else if (url_str.toLowerCase(Locale.US).startsWith("http"))
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/* 1688 */                     Utils.execSWTThreadLater(100, new Runnable()
/*      */                     {
/*      */ 
/*      */ 
/*      */                       public void run()
/*      */                       {
/*      */ 
/* 1695 */                         Utils.launch(url_str);
/*      */                       }
/*      */                       
/*      */                     });
/*      */                   } else {
/* 1700 */                     TorrentOpener.openTorrent(url_str);
/*      */                   }
/*      */                 }
/*      */                 
/*      */ 
/* 1705 */                 BuddyPluginViewBetaChat.this.log.setSelection(offset);
/*      */                 
/* 1707 */                 e.doit = false;
/*      */               }
/* 1709 */               else if ((data instanceof BuddyPluginBeta.ChatParticipant))
/*      */               {
/* 1711 */                 BuddyPluginBeta.ChatParticipant participant = (BuddyPluginBeta.ChatParticipant)data;
/*      */                 
/* 1713 */                 String name = participant.getName(true);
/*      */                 
/* 1715 */                 String existing = BuddyPluginViewBetaChat.this.input_area.getText();
/*      */                 
/* 1717 */                 if ((existing.length() > 0) && (!existing.endsWith(" ")))
/*      */                 {
/* 1719 */                   name = " " + name;
/*      */                 }
/*      */                 
/* 1722 */                 BuddyPluginViewBetaChat.this.input_area.append(name);
/*      */               }
/*      */               
/*      */             }
/*      */             
/*      */           }
/*      */         }
/*      */         catch (Throwable f) {}
/*      */       }
/* 1731 */     });
/* 1732 */     this.log.addMouseTrackListener(new MouseTrackListener()
/*      */     {
/*      */       private StyleRange old_range;
/*      */       
/*      */       private StyleRange temp_range;
/*      */       private int temp_index;
/*      */       
/*      */       public void mouseHover(MouseEvent e)
/*      */       {
/* 1741 */         boolean active = false;
/*      */         try
/*      */         {
/* 1744 */           int offset = BuddyPluginViewBetaChat.this.log.getOffsetAtLocation(new Point(e.x, e.y));
/*      */           
/* 1746 */           for (int i = 0; i < BuddyPluginViewBetaChat.this.log_styles.length; i++)
/*      */           {
/* 1748 */             StyleRange sr = BuddyPluginViewBetaChat.this.log_styles[i];
/*      */             
/* 1750 */             Object data = sr.data;
/*      */             
/* 1752 */             if ((data != null) && (offset >= sr.start) && (offset < sr.start + sr.length))
/*      */             {
/* 1754 */               if (this.old_range != null)
/*      */               {
/* 1756 */                 if ((this.temp_index < BuddyPluginViewBetaChat.this.log_styles.length) && (BuddyPluginViewBetaChat.this.log_styles[this.temp_index] == this.temp_range))
/*      */                 {
/*      */ 
/* 1759 */                   BuddyPluginViewBetaChat.this.log_styles[this.temp_index] = this.old_range;
/*      */                   
/* 1761 */                   this.old_range = null;
/*      */                 }
/*      */               }
/*      */               
/* 1765 */               sr = BuddyPluginViewBetaChat.this.log_styles[i];
/*      */               
/* 1767 */               String tt_extra = "";
/*      */               
/* 1769 */               if ((data instanceof String)) {
/*      */                 try
/*      */                 {
/* 1772 */                   URL url = new URL((String)data);
/*      */                   
/* 1774 */                   String query = url.getQuery();
/*      */                   
/* 1776 */                   if (query != null)
/*      */                   {
/* 1778 */                     String[] bits = query.split("&");
/*      */                     
/* 1780 */                     int seeds = -1;
/* 1781 */                     int leechers = -1;
/*      */                     
/* 1783 */                     for (String bit : bits)
/*      */                     {
/* 1785 */                       String[] temp = bit.split("=");
/*      */                       
/* 1787 */                       String lhs = temp[0];
/*      */                       
/* 1789 */                       if (lhs.equals("_s"))
/*      */                       {
/* 1791 */                         seeds = Integer.parseInt(temp[1]);
/*      */                       }
/* 1793 */                       else if (lhs.equals("_l"))
/*      */                       {
/* 1795 */                         leechers = Integer.parseInt(temp[1]);
/*      */                       }
/*      */                     }
/*      */                     
/* 1799 */                     if ((seeds != -1) && (leechers != -1))
/*      */                     {
/* 1801 */                       tt_extra = ": seeds=" + seeds + ", leechers=" + leechers;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 catch (Throwable f) {}
/*      */               }
/*      */               
/* 1808 */               BuddyPluginViewBetaChat.this.log.setToolTipText(MessageText.getString("label.right.click.for.options") + tt_extra);
/*      */               
/*      */               StyleRange derp;
/*      */               
/*      */               StyleRange derp;
/* 1813 */               if ((sr instanceof BuddyPluginViewBetaChat.MyStyleRange))
/*      */               {
/* 1815 */                 derp = new BuddyPluginViewBetaChat.MyStyleRange((BuddyPluginViewBetaChat.MyStyleRange)sr);
/*      */               }
/*      */               else {
/* 1818 */                 derp = new StyleRange(sr);
/*      */               }
/*      */               
/* 1821 */               derp.start = sr.start;
/* 1822 */               derp.length = sr.length;
/*      */               
/* 1824 */               derp.borderStyle = 2;
/*      */               
/* 1826 */               this.old_range = sr;
/* 1827 */               this.temp_range = derp;
/* 1828 */               this.temp_index = i;
/*      */               
/* 1830 */               BuddyPluginViewBetaChat.this.log_styles[i] = derp;
/*      */               
/* 1832 */               BuddyPluginViewBetaChat.this.log.setStyleRanges(BuddyPluginViewBetaChat.this.log_styles);
/*      */               
/* 1834 */               active = true;
/*      */               
/* 1836 */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (Throwable f) {}
/*      */         
/*      */ 
/* 1843 */         if (!active)
/*      */         {
/* 1845 */           BuddyPluginViewBetaChat.this.log.setToolTipText("");
/*      */           
/* 1847 */           if (this.old_range != null)
/*      */           {
/* 1849 */             if ((this.temp_index < BuddyPluginViewBetaChat.this.log_styles.length) && (BuddyPluginViewBetaChat.this.log_styles[this.temp_index] == this.temp_range))
/*      */             {
/*      */ 
/* 1852 */               BuddyPluginViewBetaChat.this.log_styles[this.temp_index] = this.old_range;
/*      */               
/* 1854 */               this.old_range = null;
/*      */               
/* 1856 */               BuddyPluginViewBetaChat.this.log.setStyleRanges(BuddyPluginViewBetaChat.this.log_styles);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void mouseExit(MouseEvent e) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void mouseEnter(MouseEvent e) {}
/* 1873 */     });
/* 1874 */     this.log.addKeyListener(new KeyAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void keyPressed(KeyEvent event)
/*      */       {
/*      */ 
/* 1881 */         int key = event.character;
/*      */         
/* 1883 */         if ((key <= 26) && (key > 0))
/*      */         {
/* 1885 */           key += 96;
/*      */         }
/*      */         
/* 1888 */         if ((key == 97) && (event.stateMask == SWT.MOD1))
/*      */         {
/* 1890 */           event.doit = false;
/*      */           
/* 1892 */           BuddyPluginViewBetaChat.this.log.selectAll();
/*      */         }
/*      */         
/*      */       }
/* 1896 */     });
/* 1897 */     Composite rhs = new Composite(sash, 0);
/* 1898 */     layout = new GridLayout();
/* 1899 */     layout.numColumns = 1;
/* 1900 */     layout.marginHeight = 0;
/* 1901 */     layout.marginWidth = 0;
/* 1902 */     layout.marginTop = 4;
/* 1903 */     layout.marginRight = 4;
/* 1904 */     rhs.setLayout(layout);
/* 1905 */     grid_data = new GridData(1040);
/* 1906 */     int rhs_width = Constants.isWindows ? 150 : 160;
/* 1907 */     grid_data.widthHint = rhs_width;
/* 1908 */     Utils.setLayoutData(rhs, grid_data);
/*      */     
/*      */ 
/*      */ 
/* 1912 */     Composite top_right = new Composite(rhs, 0);
/* 1913 */     layout = new GridLayout();
/* 1914 */     layout.numColumns = 3;
/* 1915 */     layout.marginHeight = 0;
/* 1916 */     layout.marginWidth = 0;
/*      */     
/* 1918 */     top_right.setLayout(layout);
/* 1919 */     grid_data = new GridData(768);
/*      */     
/* 1921 */     Utils.setLayoutData(top_right, grid_data);
/*      */     
/* 1923 */     boolean can_popout = (this.shell == null) && (public_chat);
/*      */     
/* 1925 */     Label label = new Label(top_right, 0);
/* 1926 */     grid_data = new GridData(768);
/* 1927 */     grid_data.horizontalSpan = (can_popout ? 1 : 2);
/* 1928 */     Utils.setLayoutData(label, grid_data);
/*      */     
/* 1930 */     LinkLabel link = new LinkLabel(top_right, "label.help", this.lu.getLocalisedMessageText("azbuddy.dchat.link.url"));
/*      */     
/*      */ 
/*      */ 
/* 1934 */     if (can_popout)
/*      */     {
/* 1936 */       Label pop_out = new Label(top_right, 0);
/* 1937 */       image = ImageLoader.getInstance().getImage("popout_window");
/* 1938 */       pop_out.setImage(image);
/* 1939 */       grid_data = new GridData();
/* 1940 */       grid_data.widthHint = image.getBounds().width;
/* 1941 */       grid_data.heightHint = image.getBounds().height;
/* 1942 */       Utils.setLayoutData(pop_out, grid_data);
/*      */       
/* 1944 */       pop_out.setCursor(label.getDisplay().getSystemCursor(21));
/*      */       
/* 1946 */       pop_out.setToolTipText(MessageText.getString("label.pop.out"));
/*      */       
/* 1948 */       pop_out.addMouseListener(new MouseAdapter() {
/*      */         public void mouseUp(MouseEvent arg0) {
/*      */           try {
/* 1951 */             BuddyPluginViewBetaChat.createChatWindow(BuddyPluginViewBetaChat.this.view, BuddyPluginViewBetaChat.this.plugin, BuddyPluginViewBetaChat.this.chat.getClone(), true);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1955 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1964 */     Composite nick_area = new Composite(top_right, 0);
/* 1965 */     layout = new GridLayout();
/* 1966 */     layout.numColumns = 4;
/* 1967 */     layout.marginHeight = 0;
/* 1968 */     layout.marginWidth = 0;
/* 1969 */     if (!Constants.isWindows) {
/* 1970 */       layout.horizontalSpacing = 2;
/* 1971 */       layout.verticalSpacing = 2;
/*      */     }
/* 1973 */     nick_area.setLayout(layout);
/* 1974 */     grid_data = new GridData(768);
/* 1975 */     grid_data.horizontalSpan = 3;
/* 1976 */     Utils.setLayoutData(nick_area, grid_data);
/*      */     
/* 1978 */     label = new Label(nick_area, 0);
/* 1979 */     label.setText(this.lu.getLocalisedMessageText("azbuddy.dchat.nick"));
/* 1980 */     grid_data = new GridData();
/*      */     
/* 1982 */     Utils.setLayoutData(label, grid_data);
/*      */     
/* 1984 */     this.nickname = new Text(nick_area, 2048);
/* 1985 */     grid_data = new GridData(768);
/* 1986 */     grid_data.horizontalSpan = 1;
/* 1987 */     Utils.setLayoutData(this.nickname, grid_data);
/*      */     
/* 1989 */     this.nickname.setText(this.chat.getNickname(false));
/* 1990 */     this.nickname.setMessage(this.chat.getDefaultNickname());
/*      */     
/* 1992 */     label = new Label(nick_area, 0);
/* 1993 */     label.setText(this.lu.getLocalisedMessageText("label.shared"));
/* 1994 */     label.setToolTipText(this.lu.getLocalisedMessageText("azbuddy.dchat.shared.tooltip"));
/*      */     
/* 1996 */     this.shared_nick_button = new Button(nick_area, 32);
/*      */     
/* 1998 */     this.shared_nick_button.setSelection(this.chat.isSharedNickname());
/*      */     
/* 2000 */     this.shared_nick_button.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */       public void widgetSelected(SelectionEvent arg0)
/*      */       {
/* 2005 */         boolean shared = BuddyPluginViewBetaChat.this.shared_nick_button.getSelection();
/*      */         
/* 2007 */         BuddyPluginViewBetaChat.this.chat.setSharedNickname(shared);
/*      */       }
/*      */       
/* 2010 */     });
/* 2011 */     this.nickname.addListener(16, new Listener() {
/*      */       public void handleEvent(Event event) {
/* 2013 */         String nick = BuddyPluginViewBetaChat.this.nickname.getText().trim();
/*      */         
/* 2015 */         if (BuddyPluginViewBetaChat.this.chat.isSharedNickname())
/*      */         {
/* 2017 */           if (BuddyPluginViewBetaChat.this.chat.getNetwork() == "Public")
/*      */           {
/* 2019 */             BuddyPluginViewBetaChat.this.beta.setSharedPublicNickname(nick);
/*      */           }
/*      */           else
/*      */           {
/* 2023 */             BuddyPluginViewBetaChat.this.beta.setSharedAnonNickname(nick);
/*      */           }
/*      */         }
/*      */         else {
/* 2027 */           BuddyPluginViewBetaChat.this.chat.setInstanceNickname(nick);
/*      */         }
/*      */         
/*      */       }
/*      */       
/* 2032 */     });
/* 2033 */     this.table_header = new BufferedLabel(top_right, 536870912);
/* 2034 */     grid_data = new GridData(768);
/* 2035 */     grid_data.horizontalSpan = 3;
/* 2036 */     if (!Constants.isWindows) {
/* 2037 */       grid_data.horizontalIndent = 2;
/*      */     }
/* 2039 */     Utils.setLayoutData(this.table_header, grid_data);
/* 2040 */     this.table_header.setText(MessageText.getString("PeersView.state.pending"));
/*      */     
/*      */ 
/*      */ 
/* 2044 */     this.buddy_table = new Table(rhs, 268503042);
/*      */     
/* 2046 */     String[] headers = { "azbuddy.ui.table.name" };
/*      */     
/*      */ 
/* 2049 */     int[] sizes = { rhs_width - 10 };
/*      */     
/* 2051 */     int[] aligns = { 16384 };
/*      */     
/* 2053 */     for (int i = 0; i < headers.length; i++)
/*      */     {
/* 2055 */       TableColumn tc = new TableColumn(this.buddy_table, aligns[i]);
/*      */       
/* 2057 */       tc.setWidth(Utils.adjustPXForDPI(sizes[i]));
/*      */       
/* 2059 */       Messages.setLanguageText(tc, headers[i]);
/*      */     }
/*      */     
/* 2062 */     this.buddy_table.setHeaderVisible(true);
/*      */     
/* 2064 */     grid_data = new GridData(1808);
/*      */     
/* 2066 */     Utils.setLayoutData(this.buddy_table, grid_data);
/*      */     
/*      */ 
/* 2069 */     this.buddy_table.addListener(36, new Listener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void handleEvent(Event event)
/*      */       {
/*      */ 
/*      */ 
/* 2077 */         TableItem item = (TableItem)event.item;
/*      */         
/* 2079 */         BuddyPluginViewBetaChat.this.setItemData(item);
/*      */       }
/*      */       
/* 2082 */     });
/* 2083 */     final Menu menu = new Menu(this.buddy_table);
/*      */     
/* 2085 */     this.buddy_table.setMenu(menu);
/*      */     
/* 2087 */     menu.addMenuListener(new MenuListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void menuShown(MenuEvent e)
/*      */       {
/*      */ 
/* 2094 */         MenuItem[] items = menu.getItems();
/*      */         
/* 2096 */         for (int i = 0; i < items.length; i++)
/*      */         {
/* 2098 */           items[i].dispose();
/*      */         }
/*      */         
/* 2101 */         TableItem[] selection = BuddyPluginViewBetaChat.this.buddy_table.getSelection();
/*      */         
/* 2103 */         List<BuddyPluginBeta.ChatParticipant> participants = new ArrayList(selection.length);
/*      */         
/* 2105 */         for (int i = 0; i < selection.length; i++)
/*      */         {
/* 2107 */           TableItem item = selection[i];
/*      */           
/* 2109 */           BuddyPluginBeta.ChatParticipant participant = (BuddyPluginBeta.ChatParticipant)item.getData();
/*      */           
/* 2111 */           if (participant == null)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 2116 */             participant = BuddyPluginViewBetaChat.this.setItemData(item);
/*      */           }
/*      */           
/* 2119 */           if (participant != null)
/*      */           {
/* 2121 */             participants.add(participant);
/*      */           }
/*      */         }
/*      */         
/* 2125 */         BuddyPluginViewBetaChat.this.buildParticipantMenu(menu, participants);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void menuHidden(MenuEvent e) {}
/* 2131 */     });
/* 2132 */     this.buddy_table.addKeyListener(new KeyAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void keyPressed(KeyEvent event)
/*      */       {
/*      */ 
/* 2139 */         int key = event.character;
/*      */         
/* 2141 */         if ((key <= 26) && (key > 0))
/*      */         {
/* 2143 */           key += 96;
/*      */         }
/*      */         
/* 2146 */         if ((key == 97) && (event.stateMask == SWT.MOD1))
/*      */         {
/* 2148 */           event.doit = false;
/*      */           
/* 2150 */           BuddyPluginViewBetaChat.this.buddy_table.selectAll();
/*      */         }
/*      */         
/*      */       }
/*      */       
/* 2155 */     });
/* 2156 */     this.buddy_table.addMouseListener(new MouseAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void mouseDoubleClick(MouseEvent e)
/*      */       {
/*      */ 
/* 2163 */         TableItem[] selection = BuddyPluginViewBetaChat.this.buddy_table.getSelection();
/*      */         
/* 2165 */         if (selection.length != 1)
/*      */         {
/* 2167 */           return;
/*      */         }
/*      */         
/* 2170 */         TableItem item = selection[0];
/*      */         
/* 2172 */         BuddyPluginBeta.ChatParticipant participant = (BuddyPluginBeta.ChatParticipant)item.getData();
/*      */         
/* 2174 */         String name = participant.getName(true);
/*      */         
/* 2176 */         String existing = BuddyPluginViewBetaChat.this.input_area.getText();
/*      */         
/* 2178 */         if ((existing.length() > 0) && (!existing.endsWith(" ")))
/*      */         {
/* 2180 */           name = " " + name;
/*      */         }
/*      */         
/* 2183 */         BuddyPluginViewBetaChat.this.input_area.append(name);
/*      */       }
/*      */       
/*      */ 
/* 2187 */     });
/* 2188 */     Utils.maintainSashPanelWidth(sash, rhs, new int[] { 700, 300 }, "azbuddy.dchat.ui.sash.pos");
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
/* 2240 */     Composite bottom_area = new Composite(parent, 0);
/* 2241 */     layout = new GridLayout();
/* 2242 */     layout.numColumns = 2;
/* 2243 */     layout.marginHeight = 0;
/* 2244 */     layout.marginWidth = 0;
/* 2245 */     bottom_area.setLayout(layout);
/*      */     
/* 2247 */     grid_data = new GridData(768);
/* 2248 */     grid_data.horizontalSpan = 2;
/* 2249 */     bottom_area.setLayoutData(grid_data);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2254 */     this.input_area = new Text(bottom_area, 2626);
/* 2255 */     grid_data = new GridData(768);
/* 2256 */     grid_data.horizontalSpan = 1;
/* 2257 */     grid_data.heightHint = 30;
/* 2258 */     grid_data.horizontalIndent = 4;
/* 2259 */     Utils.setLayoutData(this.input_area, grid_data);
/*      */     
/* 2261 */     this.input_area.setTextLimit(400);
/*      */     
/* 2263 */     this.input_area.addKeyListener(new KeyListener()
/*      */     {
/*      */ 
/* 2266 */       private LinkedList<String> history = new LinkedList();
/* 2267 */       private int history_pos = -1;
/*      */       
/* 2269 */       private String buffered_message = "";
/*      */       
/*      */ 
/*      */ 
/*      */       public void keyPressed(KeyEvent e)
/*      */       {
/* 2275 */         if (e.keyCode == 13)
/*      */         {
/* 2277 */           e.doit = false;
/*      */           
/* 2279 */           String message = BuddyPluginViewBetaChat.this.input_area.getText().trim();
/*      */           
/* 2281 */           if (message.length() > 0)
/*      */           {
/* 2283 */             BuddyPluginViewBetaChat.this.sendMessage(message);
/*      */             
/* 2285 */             this.history.addFirst(message);
/*      */             
/* 2287 */             if (this.history.size() > 32)
/*      */             {
/* 2289 */               this.history.removeLast();
/*      */             }
/*      */             
/* 2292 */             this.history_pos = -1;
/*      */             
/* 2294 */             this.buffered_message = "";
/*      */             
/* 2296 */             BuddyPluginViewBetaChat.this.input_area.setText("");
/*      */           }
/* 2298 */         } else if (e.keyCode == 16777217)
/*      */         {
/* 2300 */           this.history_pos += 1;
/*      */           
/* 2302 */           if (this.history_pos < this.history.size())
/*      */           {
/* 2304 */             if (this.history_pos == 0)
/*      */             {
/* 2306 */               this.buffered_message = BuddyPluginViewBetaChat.this.input_area.getText().trim();
/*      */             }
/*      */             
/* 2309 */             String msg = (String)this.history.get(this.history_pos);
/*      */             
/* 2311 */             BuddyPluginViewBetaChat.this.input_area.setText(msg);
/*      */             
/* 2313 */             BuddyPluginViewBetaChat.this.input_area.setSelection(msg.length());
/*      */           }
/*      */           else
/*      */           {
/* 2317 */             this.history_pos = (this.history.size() - 1);
/*      */           }
/*      */           
/* 2320 */           e.doit = false;
/*      */         }
/* 2322 */         else if (e.keyCode == 16777218)
/*      */         {
/* 2324 */           this.history_pos -= 1;
/*      */           
/* 2326 */           if (this.history_pos >= 0)
/*      */           {
/* 2328 */             String msg = (String)this.history.get(this.history_pos);
/*      */             
/* 2330 */             BuddyPluginViewBetaChat.this.input_area.setText(msg);
/*      */             
/* 2332 */             BuddyPluginViewBetaChat.this.input_area.setSelection(msg.length());
/*      */ 
/*      */ 
/*      */           }
/* 2336 */           else if (this.history_pos == -1)
/*      */           {
/* 2338 */             BuddyPluginViewBetaChat.this.input_area.setText(this.buffered_message);
/*      */             
/* 2340 */             if (this.buffered_message.length() > 0)
/*      */             {
/* 2342 */               BuddyPluginViewBetaChat.this.input_area.setSelection(this.buffered_message.length());
/*      */               
/* 2344 */               this.buffered_message = "";
/*      */             }
/*      */           }
/*      */           else {
/* 2348 */             this.history_pos = -1;
/*      */           }
/*      */           
/*      */ 
/* 2352 */           e.doit = false;
/*      */ 
/*      */ 
/*      */         }
/* 2356 */         else if (e.stateMask == SWT.MOD1)
/*      */         {
/* 2358 */           int key = e.character;
/*      */           
/* 2360 */           if ((key <= 26) && (key > 0))
/*      */           {
/* 2362 */             key += 96;
/*      */           }
/*      */           
/* 2365 */           if (key == 97)
/*      */           {
/* 2367 */             BuddyPluginViewBetaChat.this.input_area.selectAll();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void keyReleased(KeyEvent e) {}
/* 2379 */     });
/* 2380 */     Composite button_area = new Composite(bottom_area, 0);
/*      */     
/* 2382 */     layout = new GridLayout();
/* 2383 */     layout.numColumns = 1;
/* 2384 */     layout.marginHeight = 0;
/* 2385 */     layout.marginWidth = 0;
/* 2386 */     layout.marginRight = 4;
/* 2387 */     button_area.setLayout(layout);
/*      */     
/* 2389 */     this.rss_button = new Button(button_area, 8);
/* 2390 */     Image rss_image = ImageLoader.getInstance().getImage("image.sidebar.subscriptions");
/* 2391 */     this.rss_button.setImage(rss_image);
/* 2392 */     grid_data = new GridData(768);
/* 2393 */     grid_data.widthHint = rss_image.getBounds().width;
/* 2394 */     grid_data.heightHint = rss_image.getBounds().height;
/* 2395 */     this.rss_button.setLayoutData(grid_data);
/*      */     
/*      */ 
/* 2398 */     this.rss_button.setToolTipText(MessageText.getString("azbuddy.dchat.rss.subscribe.info"));
/*      */     
/* 2400 */     this.rss_button.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */       public void widgetSelected(SelectionEvent ev)
/*      */       {
/*      */ 
/*      */         try
/*      */         {
/* 2408 */           String url = "azplug:?id=azbuddy&arg=" + UrlUtils.encode(new StringBuilder().append(BuddyPluginViewBetaChat.this.chat.getURL()).append("&format=rss").toString());
/*      */           
/* 2410 */           SubscriptionManager sm = PluginInitializer.getDefaultInterface().getUtilities().getSubscriptionManager();
/*      */           
/* 2412 */           Map<String, Object> options = new HashMap();
/*      */           
/* 2414 */           if (BuddyPluginViewBetaChat.this.chat.isAnonymous())
/*      */           {
/* 2416 */             options.put("_anonymous_", Boolean.valueOf(true));
/*      */           }
/*      */           
/* 2419 */           options.put("t", BuddyPluginViewBetaChat.this.chat.getName());
/*      */           
/* 2421 */           sm.requestSubscription(new URL(url), options);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 2425 */           Debug.out(e);
/*      */         }
/*      */         
/*      */       }
/*      */       
/* 2430 */     });
/* 2431 */     this.ftux_ok = this.beta.getFTUXAccepted();
/*      */     
/* 2433 */     if (this.chat.isReadOnly())
/*      */     {
/* 2435 */       this.input_area.setText(MessageText.getString("azbuddy.dchat.ro"));
/*      */       
/* 2437 */       this.input_area.setEnabled(false);
/*      */     }
/* 2439 */     else if (!this.ftux_ok)
/*      */     {
/* 2441 */       this.input_area.setEnabled(false);
/*      */     }
/*      */     else
/*      */     {
/* 2445 */       this.input_area.setFocus();
/*      */     }
/*      */     
/* 2448 */     final boolean[] ftux_init_done = { false };
/*      */     
/* 2450 */     this.beta.addFTUXStateChangeListener(new BuddyPluginBeta.FTUXStateChangeListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void stateChanged(final boolean _ftux_ok)
/*      */       {
/*      */ 
/* 2457 */         if (ftux_stack.isDisposed())
/*      */         {
/* 2459 */           BuddyPluginViewBetaChat.this.beta.removeFTUXStateChangeListener(this);
/*      */         }
/*      */         else
/*      */         {
/* 2463 */           Utils.execSWTThread(new Runnable()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void run()
/*      */             {
/*      */ 
/* 2470 */               BuddyPluginViewBetaChat.this.ftux_ok = _ftux_ok;
/*      */               
/* 2472 */               BuddyPluginViewBetaChat.45.this.val$stack_layout.topControl = (BuddyPluginViewBetaChat.this.ftux_ok ? BuddyPluginViewBetaChat.45.this.val$log_holder : BuddyPluginViewBetaChat.45.this.val$ftux_holder);
/*      */               
/* 2474 */               if (BuddyPluginViewBetaChat.45.this.val$ftux_init_done[0] != 0)
/*      */               {
/* 2476 */                 BuddyPluginViewBetaChat.45.this.val$ftux_stack.layout(true, true);
/*      */               }
/*      */               
/* 2479 */               if (!BuddyPluginViewBetaChat.this.chat.isReadOnly())
/*      */               {
/* 2481 */                 BuddyPluginViewBetaChat.this.input_area.setEnabled(BuddyPluginViewBetaChat.this.ftux_ok);
/*      */               }
/*      */               
/* 2484 */               BuddyPluginViewBetaChat.this.table_resort_required = true;
/*      */               
/* 2486 */               BuddyPluginViewBetaChat.this.updateTable(false);
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */     });
/*      */     
/* 2493 */     if (!this.chat.isReadOnly())
/*      */     {
/* 2495 */       this.drop_targets = new DropTarget[] { new DropTarget(this.log, 1), new DropTarget(this.input_area, 1) };
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2500 */       for (DropTarget drop_target : this.drop_targets)
/*      */       {
/* 2502 */         drop_target.setTransfer(new Transfer[] { URLTransfer.getInstance(), FileTransfer.getInstance(), TextTransfer.getInstance() });
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2508 */         drop_target.addDropListener(new DropTargetAdapter() {
/*      */           public void dropAccept(DropTargetEvent event) {
/* 2510 */             event.currentDataType = URLTransfer.pickBestType(event.dataTypes, event.currentDataType);
/*      */           }
/*      */           
/*      */ 
/*      */           public void dragEnter(DropTargetEvent event) {}
/*      */           
/*      */ 
/*      */           public void dragOperationChanged(DropTargetEvent event) {}
/*      */           
/*      */ 
/*      */           public void dragOver(DropTargetEvent event)
/*      */           {
/* 2522 */             if ((event.operations & 0x4) > 0) {
/* 2523 */               event.detail = 4;
/* 2524 */             } else if ((event.operations & 0x1) > 0) {
/* 2525 */               event.detail = 1;
/* 2526 */             } else if ((event.operations & 0x10) > 0) {
/* 2527 */               event.detail = 1;
/*      */             }
/*      */             
/* 2530 */             event.feedback = 25;
/*      */           }
/*      */           
/*      */           public void dragLeave(DropTargetEvent event) {}
/*      */           
/*      */           public void drop(DropTargetEvent event)
/*      */           {
/* 2537 */             BuddyPluginViewBetaChat.this.handleDrop(event.data);
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     
/* 2543 */     ftux_init_done[0] = true;
/*      */     
/* 2545 */     Control[] focus_controls = { this.log, this.input_area, this.buddy_table, this.nickname, this.shared_nick_button };
/*      */     
/* 2547 */     Listener focus_listener = new Listener()
/*      */     {
/*      */       public void handleEvent(Event event) {
/* 2550 */         BuddyPluginViewBetaChat.this.activate();
/*      */       }
/*      */     };
/*      */     
/* 2554 */     for (Control c : focus_controls)
/*      */     {
/* 2556 */       c.addListener(15, focus_listener);
/*      */     }
/*      */     
/* 2559 */     BuddyPluginBeta.ChatParticipant[] existing_participants = this.chat.getParticipants();
/*      */     
/* 2561 */     synchronized (this.participants)
/*      */     {
/* 2563 */       this.participants.addAll(Arrays.asList(existing_participants));
/*      */     }
/*      */     
/* 2566 */     this.table_resort_required = true;
/*      */     
/* 2568 */     updateTable(false);
/*      */     
/* 2570 */     BuddyPluginBeta.ChatMessage[] history = this.chat.getHistory();
/*      */     
/* 2572 */     logChatMessages(history);
/*      */     
/* 2574 */     this.chat.addListener(this);
/*      */     
/* 2576 */     this.build_complete = true;
/*      */     
/* 2578 */     if ((can_popout) && (!this.ftux_ok) && (!auto_ftux_popout_done))
/*      */     {
/* 2580 */       auto_ftux_popout_done = true;
/*      */       try
/*      */       {
/* 2583 */         createChatWindow(this.view, this.plugin, this.chat.getClone(), true);
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void addFriendsMenu(Menu menu)
/*      */   {
/* 2595 */     if (this.chat.isAnonymous())
/*      */     {
/* 2597 */       return;
/*      */     }
/*      */     
/* 2600 */     final Menu friends_menu = new Menu(menu.getShell(), 4);
/* 2601 */     MenuItem friends_menu_item = new MenuItem(menu, 64);
/* 2602 */     friends_menu_item.setMenu(friends_menu);
/* 2603 */     friends_menu_item.setText(MessageText.getString("Views.plugins.azbuddy.title"));
/*      */     
/* 2605 */     friends_menu.addMenuListener(new MenuAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void menuShown(MenuEvent e)
/*      */       {
/*      */ 
/* 2612 */         MenuItem[] items = friends_menu.getItems();
/*      */         
/* 2614 */         for (int i = 0; i < items.length; i++)
/*      */         {
/* 2616 */           items[i].dispose();
/*      */         }
/*      */         
/* 2619 */         boolean enabled = BuddyPluginViewBetaChat.this.plugin.isClassicEnabled();
/*      */         
/* 2621 */         if (enabled)
/*      */         {
/* 2623 */           MenuItem mi = new MenuItem(friends_menu, 8);
/* 2624 */           mi.setText(MessageText.getString("azbuddy.insert.friend.key"));
/*      */           
/* 2626 */           mi.addSelectionListener(new SelectionAdapter()
/*      */           {
/*      */ 
/*      */             public void widgetSelected(SelectionEvent event)
/*      */             {
/*      */ 
/* 2632 */               String key = BuddyPluginViewBetaChat.this.plugin.getPublicKey();
/*      */               
/* 2634 */               String uri = "chat:friend:?key=" + key;
/*      */               
/* 2636 */               String my_nick = BuddyPluginViewBetaChat.this.chat.getNickname(false);
/*      */               
/* 2638 */               if (my_nick.length() > 0)
/*      */               {
/* 2640 */                 uri = uri + "[[" + UrlUtils.encode(new StringBuilder().append("Friend Key for ").append(my_nick).toString()) + "]]";
/*      */               }
/*      */               
/* 2643 */               BuddyPluginViewBetaChat.this.input_area.append(uri);
/*      */             }
/*      */             
/* 2646 */           });
/* 2647 */           new MenuItem(friends_menu, 2);
/*      */           
/* 2649 */           mi = new MenuItem(friends_menu, 8);
/* 2650 */           mi.setText(MessageText.getString("azbuddy.view.friends"));
/*      */           
/* 2652 */           mi.addSelectionListener(new SelectionAdapter()
/*      */           {
/*      */ 
/*      */             public void widgetSelected(SelectionEvent event)
/*      */             {
/*      */ 
/* 2658 */               BuddyPluginViewBetaChat.this.view.selectClassicTab();
/*      */             }
/*      */           });
/*      */         }
/*      */         else
/*      */         {
/* 2664 */           MenuItem mi = new MenuItem(friends_menu, 8);
/* 2665 */           mi.setText(MessageText.getString("devices.contextmenu.od.enable"));
/*      */           
/* 2667 */           mi.addSelectionListener(new SelectionAdapter()
/*      */           {
/*      */ 
/*      */             public void widgetSelected(SelectionEvent event)
/*      */             {
/*      */ 
/* 2673 */               BuddyPluginViewBetaChat.this.plugin.setClassicEnabled(true);
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void buildParticipantMenu(Menu menu, final List<BuddyPluginBeta.ChatParticipant> participants)
/*      */   {
/* 2687 */     boolean can_ignore = false;
/* 2688 */     boolean can_listen = false;
/* 2689 */     boolean can_pin = false;
/* 2690 */     boolean can_unpin = false;
/*      */     
/* 2692 */     boolean can_spam = false;
/* 2693 */     boolean can_unspam = false;
/*      */     
/* 2695 */     for (BuddyPluginBeta.ChatParticipant participant : participants)
/*      */     {
/* 2697 */       if (DEBUG_ENABLED)
/*      */       {
/* 2699 */         System.out.println(participant.getName() + "/" + participant.getAddress());
/*      */         
/* 2701 */         List<BuddyPluginBeta.ChatMessage> messages = participant.getMessages();
/*      */         
/* 2703 */         for (BuddyPluginBeta.ChatMessage msg : messages)
/*      */         {
/* 2705 */           System.out.println("    " + msg.getTimeStamp() + ", " + msg.getAddress() + " - " + msg.getMessage());
/*      */         }
/*      */       }
/*      */       
/* 2709 */       if (participant.isIgnored())
/*      */       {
/* 2711 */         can_listen = true;
/*      */       }
/*      */       else
/*      */       {
/* 2715 */         can_ignore = true;
/*      */       }
/*      */       
/* 2718 */       if (participant.isPinned())
/*      */       {
/* 2720 */         can_unpin = true;
/*      */ 
/*      */ 
/*      */       }
/* 2724 */       else if (!participant.isMe())
/*      */       {
/* 2726 */         can_pin = true;
/*      */       }
/*      */       
/*      */ 
/* 2730 */       if (participant.isSpammer())
/*      */       {
/* 2732 */         can_unspam = true;
/*      */       }
/*      */       else
/*      */       {
/* 2736 */         can_spam |= participant.canSpammer();
/*      */       }
/*      */     }
/*      */     
/* 2740 */     MenuItem ignore_item = new MenuItem(menu, 8);
/*      */     
/* 2742 */     ignore_item.setText(this.lu.getLocalisedMessageText("label.mute"));
/*      */     
/* 2744 */     ignore_item.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent e)
/*      */       {
/*      */ 
/* 2751 */         boolean changed = false;
/*      */         
/* 2753 */         for (BuddyPluginBeta.ChatParticipant participant : participants)
/*      */         {
/* 2755 */           if (!participant.isIgnored())
/*      */           {
/* 2757 */             participant.setIgnored(true);
/*      */             
/* 2759 */             BuddyPluginViewBetaChat.this.setProperties(participant);
/*      */             
/* 2761 */             changed = true;
/*      */           }
/*      */         }
/*      */         
/* 2765 */         if (changed)
/*      */         {
/* 2767 */           BuddyPluginViewBetaChat.this.messagesChanged();
/*      */         }
/*      */         
/*      */       }
/* 2771 */     });
/* 2772 */     ignore_item.setEnabled(can_ignore);
/*      */     
/* 2774 */     MenuItem listen_item = new MenuItem(menu, 8);
/*      */     
/* 2776 */     listen_item.setText(this.lu.getLocalisedMessageText("label.listen"));
/*      */     
/* 2778 */     listen_item.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent e)
/*      */       {
/*      */ 
/* 2785 */         boolean changed = false;
/*      */         
/* 2787 */         for (BuddyPluginBeta.ChatParticipant participant : participants)
/*      */         {
/* 2789 */           if (participant.isIgnored())
/*      */           {
/* 2791 */             participant.setIgnored(false);
/*      */             
/* 2793 */             BuddyPluginViewBetaChat.this.setProperties(participant);
/*      */             
/* 2795 */             changed = true;
/*      */           }
/*      */         }
/*      */         
/* 2799 */         if (changed)
/*      */         {
/* 2801 */           BuddyPluginViewBetaChat.this.messagesChanged();
/*      */         }
/*      */         
/*      */       }
/* 2805 */     });
/* 2806 */     listen_item.setEnabled(can_listen);
/*      */     
/*      */ 
/*      */ 
/* 2810 */     MenuItem spam_item = new MenuItem(menu, 8);
/*      */     
/* 2812 */     spam_item.setText(this.lu.getLocalisedMessageText("label.spam"));
/*      */     
/* 2814 */     spam_item.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent e)
/*      */       {
/*      */ 
/* 2821 */         boolean changed = false;
/*      */         
/* 2823 */         for (BuddyPluginBeta.ChatParticipant participant : participants)
/*      */         {
/* 2825 */           if (participant.canSpammer())
/*      */           {
/* 2827 */             participant.setSpammer(true);
/*      */             
/* 2829 */             BuddyPluginViewBetaChat.this.setProperties(participant);
/*      */             
/* 2831 */             changed = true;
/*      */           }
/*      */         }
/*      */         
/* 2835 */         if (changed)
/*      */         {
/* 2837 */           BuddyPluginViewBetaChat.this.messagesChanged();
/*      */         }
/*      */         
/*      */       }
/* 2841 */     });
/* 2842 */     spam_item.setEnabled(can_spam);
/*      */     
/* 2844 */     MenuItem unspam_item = new MenuItem(menu, 8);
/*      */     
/* 2846 */     unspam_item.setText(this.lu.getLocalisedMessageText("label.not.spam"));
/*      */     
/* 2848 */     unspam_item.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent e)
/*      */       {
/*      */ 
/* 2855 */         boolean changed = false;
/*      */         
/* 2857 */         for (BuddyPluginBeta.ChatParticipant participant : participants)
/*      */         {
/* 2859 */           if (participant.isSpammer())
/*      */           {
/* 2861 */             participant.setSpammer(false);
/*      */             
/* 2863 */             BuddyPluginViewBetaChat.this.setProperties(participant);
/*      */             
/* 2865 */             changed = true;
/*      */           }
/*      */         }
/*      */         
/* 2869 */         if (changed)
/*      */         {
/* 2871 */           BuddyPluginViewBetaChat.this.messagesChanged();
/*      */         }
/*      */         
/*      */       }
/* 2875 */     });
/* 2876 */     unspam_item.setEnabled(can_unspam);
/*      */     
/*      */ 
/*      */ 
/* 2880 */     new MenuItem(menu, 2);
/*      */     
/* 2882 */     MenuItem pin_item = new MenuItem(menu, 8);
/*      */     
/* 2884 */     pin_item.setText(this.lu.getLocalisedMessageText("label.pin"));
/*      */     
/* 2886 */     pin_item.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent e)
/*      */       {
/*      */ 
/* 2893 */         for (BuddyPluginBeta.ChatParticipant participant : participants)
/*      */         {
/* 2895 */           if (!participant.isPinned())
/*      */           {
/* 2897 */             if (!participant.isMe())
/*      */             {
/* 2899 */               participant.setPinned(true);
/*      */               
/* 2901 */               BuddyPluginViewBetaChat.this.setProperties(participant);
/*      */             }
/*      */             
/*      */           }
/*      */         }
/*      */       }
/* 2907 */     });
/* 2908 */     pin_item.setEnabled(can_pin);
/*      */     
/* 2910 */     MenuItem unpin_item = new MenuItem(menu, 8);
/*      */     
/* 2912 */     unpin_item.setText(this.lu.getLocalisedMessageText("label.unpin"));
/*      */     
/* 2914 */     unpin_item.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent e)
/*      */       {
/*      */ 
/* 2921 */         for (BuddyPluginBeta.ChatParticipant participant : participants)
/*      */         {
/* 2923 */           if (participant.isPinned())
/*      */           {
/* 2925 */             participant.setPinned(false);
/*      */             
/* 2927 */             BuddyPluginViewBetaChat.this.setProperties(participant);
/*      */           }
/*      */           
/*      */         }
/*      */       }
/* 2932 */     });
/* 2933 */     unpin_item.setEnabled(can_unpin);
/*      */     
/* 2935 */     if (!this.chat.isPrivateChat())
/*      */     {
/* 2937 */       new MenuItem(menu, 2);
/*      */       
/* 2939 */       MenuItem private_chat_item = new MenuItem(menu, 8);
/*      */       
/* 2941 */       private_chat_item.setText(this.lu.getLocalisedMessageText("label.private.chat"));
/*      */       
/* 2943 */       final byte[] chat_pk = this.chat.getPublicKey();
/*      */       
/* 2945 */       private_chat_item.addSelectionListener(new SelectionAdapter()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void widgetSelected(SelectionEvent e)
/*      */         {
/*      */ 
/* 2952 */           for (BuddyPluginBeta.ChatParticipant participant : participants)
/*      */           {
/* 2954 */             if ((BuddyPluginViewBetaChat.TEST_LOOPBACK_CHAT) || (!Arrays.equals(participant.getPublicKey(), chat_pk))) {
/*      */               try
/*      */               {
/* 2957 */                 BuddyPluginBeta.ChatInstance chat = participant.createPrivateChat();
/*      */                 
/* 2959 */                 BuddyPluginViewBetaChat.createChatWindow(BuddyPluginViewBetaChat.this.view, BuddyPluginViewBetaChat.this.plugin, chat);
/*      */               }
/*      */               catch (Throwable f)
/*      */               {
/* 2963 */                 Debug.out(f);
/*      */               }
/*      */               
/*      */             }
/*      */           }
/*      */         }
/* 2969 */       });
/* 2970 */       boolean pc_enable = false;
/*      */       
/* 2972 */       if (chat_pk != null)
/*      */       {
/* 2974 */         for (BuddyPluginBeta.ChatParticipant participant : participants)
/*      */         {
/* 2976 */           if (!Arrays.equals(participant.getPublicKey(), chat_pk))
/*      */           {
/* 2978 */             pc_enable = true;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 2983 */       private_chat_item.setEnabled((pc_enable) || (TEST_LOOPBACK_CHAT));
/*      */     }
/*      */     
/* 2986 */     if (participants.size() == 1)
/*      */     {
/* 2988 */       new MenuItem(menu, 2);
/*      */       
/* 2990 */       MenuItem mi_copy_clip = new MenuItem(menu, 8);
/*      */       
/* 2992 */       mi_copy_clip.setText(this.lu.getLocalisedMessageText("label.copy.to.clipboard"));
/*      */       
/* 2994 */       mi_copy_clip.addSelectionListener(new SelectionAdapter()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void widgetSelected(SelectionEvent e)
/*      */         {
/*      */ 
/* 3001 */           StringBuffer sb = new StringBuffer();
/*      */           
/* 3003 */           sb.append(((BuddyPluginBeta.ChatParticipant)participants.get(0)).getName(true));
/*      */           
/* 3005 */           if (Constants.isCVSVersion())
/*      */           {
/* 3007 */             List<BuddyPluginBeta.ChatMessage> messages = ((BuddyPluginBeta.ChatParticipant)participants.get(0)).getMessages();
/*      */             
/* 3009 */             for (BuddyPluginBeta.ChatMessage msg : messages)
/*      */             {
/* 3011 */               sb.append("\r\n" + msg.getMessage());
/*      */             }
/*      */           }
/*      */           
/*      */ 
/* 3016 */           ClipboardCopy.copyToClipBoard(sb.toString());
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private BuddyPluginBeta.ChatParticipant setItemData(TableItem item)
/*      */   {
/* 3026 */     int index = this.buddy_table.indexOf(item);
/*      */     
/* 3028 */     if ((index < 0) || (index >= this.participants.size()))
/*      */     {
/* 3030 */       return null;
/*      */     }
/*      */     
/* 3033 */     BuddyPluginBeta.ChatParticipant participant = (BuddyPluginBeta.ChatParticipant)this.participants.get(index);
/*      */     
/* 3035 */     item.setData(participant);
/*      */     
/* 3037 */     item.setText(0, participant.getName(this.ftux_ok));
/*      */     
/* 3039 */     setProperties(item, participant);
/*      */     
/* 3041 */     return participant;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void setProperties(BuddyPluginBeta.ChatParticipant p)
/*      */   {
/* 3048 */     for (TableItem ti : this.buddy_table.getItems())
/*      */     {
/* 3050 */       if (ti.getData() == p)
/*      */       {
/* 3052 */         setProperties(ti, p);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setProperties(TableItem item, BuddyPluginBeta.ChatParticipant p)
/*      */   {
/* 3062 */     if ((p.isIgnored()) || (p.isSpammer()))
/*      */     {
/* 3064 */       item.setForeground(0, Colors.grey);
/*      */ 
/*      */ 
/*      */     }
/* 3068 */     else if (p.isPinned())
/*      */     {
/* 3070 */       item.setForeground(0, Colors.fadedGreen);
/*      */ 
/*      */ 
/*      */     }
/* 3074 */     else if (p.isMe())
/*      */     {
/* 3076 */       item.setForeground(0, Colors.fadedGreen);
/*      */       
/* 3078 */       item.setFont(0, this.italic_font);
/*      */     }
/* 3080 */     else if (p.isNickClash())
/*      */     {
/* 3082 */       item.setForeground(0, Colors.red);
/*      */ 
/*      */ 
/*      */     }
/* 3086 */     else if (p.hasNickname())
/*      */     {
/* 3088 */       item.setForeground(0, Colors.blues[9]);
/*      */     }
/*      */     else
/*      */     {
/* 3092 */       item.setForeground(0, Colors.black);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void addDisposeListener(DisposeListener listener)
/*      */   {
/* 3103 */     if (this.shell != null)
/*      */     {
/* 3105 */       if (this.shell.isDisposed())
/*      */       {
/* 3107 */         listener.widgetDisposed(null);
/*      */       }
/*      */       else
/*      */       {
/* 3111 */         this.shell.addDisposeListener(listener);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void updateTableHeader()
/*      */   {
/* 3119 */     int active = this.buddy_table.getItemCount();
/* 3120 */     int online = this.chat.getEstimatedNodes();
/*      */     
/* 3122 */     String msg = this.lu.getLocalisedMessageText("azbuddy.dchat.user.status", new String[] { online >= 100 ? "100+" : String.valueOf(online), String.valueOf(active) });
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3130 */     this.table_header.setText(msg);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void updateTable(boolean async)
/*      */   {
/* 3137 */     if (async)
/*      */     {
/* 3139 */       if (!this.buddy_table.isDisposed())
/*      */       {
/* 3141 */         Utils.execSWTThread(new Runnable()
/*      */         {
/*      */ 
/*      */           public void run()
/*      */           {
/*      */ 
/* 3147 */             if (BuddyPluginViewBetaChat.this.buddy_table.isDisposed())
/*      */             {
/* 3149 */               return;
/*      */             }
/*      */             
/* 3152 */             BuddyPluginViewBetaChat.this.updateTable(false);
/*      */             
/* 3154 */             BuddyPluginViewBetaChat.this.updateTableHeader();
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     else {
/* 3160 */       if (this.table_resort_required)
/*      */       {
/* 3162 */         this.table_resort_required = false;
/*      */         
/* 3164 */         sortParticipants();
/*      */       }
/*      */       
/* 3167 */       this.buddy_table.setItemCount(this.participants.size());
/* 3168 */       this.buddy_table.clearAll();
/* 3169 */       this.buddy_table.redraw();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void handleExternalDrop(String payload)
/*      */   {
/* 3177 */     handleDrop(payload);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void handleDrop(Object payload)
/*      */   {
/* 3184 */     if ((payload instanceof String[]))
/*      */     {
/* 3186 */       String[] files = (String[])payload;
/*      */       
/* 3188 */       if (files.length == 0)
/*      */       {
/* 3190 */         Debug.out("Nothing to drop");
/*      */       }
/*      */       else {
/* 3193 */         int hits = 0;
/*      */         
/* 3195 */         for (String file : files)
/*      */         {
/* 3197 */           File f = new File(file);
/*      */           
/* 3199 */           if (f.exists())
/*      */           {
/* 3201 */             dropFile(f);
/*      */             
/* 3203 */             hits++;
/*      */           }
/*      */         }
/*      */         
/* 3207 */         if (hits == 0)
/*      */         {
/* 3209 */           Debug.out("Nothing files found to drop");
/*      */         }
/*      */       }
/* 3212 */     } else if ((payload instanceof String))
/*      */     {
/* 3214 */       String stuff = (String)payload;
/*      */       
/* 3216 */       if ((stuff.startsWith("DownloadManager\n")) || (stuff.startsWith("DiskManagerFileInfo\n")))
/*      */       {
/* 3218 */         String[] bits = Constants.PAT_SPLIT_SLASH_N.split(stuff);
/*      */         
/* 3220 */         for (int i = 1; i < bits.length; i++)
/*      */         {
/* 3222 */           String hash_str = bits[i];
/*      */           
/* 3224 */           int pos = hash_str.indexOf(';');
/*      */           
/*      */           try
/*      */           {
/* 3228 */             if (pos == -1)
/*      */             {
/* 3230 */               byte[] hash = Base32.decode(bits[i]);
/*      */               
/* 3232 */               Download download = AzureusCoreFactory.getSingleton().getPluginManager().getDefaultPluginInterface().getShortCuts().getDownload(hash);
/*      */               
/* 3234 */               dropDownload(download);
/*      */             }
/*      */             else
/*      */             {
/* 3238 */               String[] files = hash_str.split(";");
/*      */               
/* 3240 */               byte[] hash = Base32.decode(files[0].trim());
/*      */               
/* 3242 */               DiskManagerFileInfo[] dm_files = AzureusCoreFactory.getSingleton().getPluginManager().getDefaultPluginInterface().getShortCuts().getDownload(hash).getDiskManagerFileInfo();
/*      */               
/* 3244 */               for (int j = 1; j < files.length; j++)
/*      */               {
/* 3246 */                 DiskManagerFileInfo dm_file = dm_files[Integer.parseInt(files[j].trim())];
/*      */                 
/* 3248 */                 dropDownloadFile(dm_file);
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/* 3253 */             Debug.out("Failed to get download for hash " + bits[1]);
/*      */           }
/*      */         }
/* 3256 */       } else if (stuff.startsWith("TranscodeFile\n"))
/*      */       {
/* 3258 */         String[] bits = Constants.PAT_SPLIT_SLASH_N.split(stuff);
/*      */         
/* 3260 */         for (int i = 1; i < bits.length; i++)
/*      */         {
/* 3262 */           File f = new File(bits[i]);
/*      */           
/* 3264 */           if (f.isFile())
/*      */           {
/* 3266 */             dropFile(f);
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/* 3271 */         File f = new File(stuff);
/*      */         
/* 3273 */         if (f.exists())
/*      */         {
/* 3275 */           dropFile(f);
/*      */         }
/*      */         else {
/* 3278 */           String lc_stuff = stuff.toLowerCase(Locale.US);
/*      */           
/* 3280 */           if ((lc_stuff.startsWith("http:")) || (lc_stuff.startsWith("https:")) || (lc_stuff.startsWith("magnet: ")))
/*      */           {
/*      */ 
/*      */ 
/* 3284 */             dropURL(stuff);
/*      */           }
/*      */           else
/*      */           {
/* 3288 */             Debug.out("Failed to handle drop for '" + stuff + "'");
/*      */           }
/*      */         }
/*      */       }
/* 3292 */     } else if ((payload instanceof URLTransfer.URLType))
/*      */     {
/* 3294 */       String url = ((URLTransfer.URLType)payload).linkURL;
/*      */       
/* 3296 */       if (url != null)
/*      */       {
/* 3298 */         dropURL(url);
/*      */       }
/*      */       else
/*      */       {
/* 3302 */         Debug.out("Failed to handle drop for '" + payload + "'");
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void dropURL(String str)
/*      */   {
/* 3311 */     this.input_area.setText(this.input_area.getText() + str);
/*      */   }
/*      */   
/*      */ 
/*      */   private void dropFile(final File file)
/*      */   {
/*      */     try
/*      */     {
/* 3319 */       if ((file.exists()) && (file.canRead()))
/*      */       {
/* 3321 */         new AEThread2("share async")
/*      */         {
/*      */ 
/*      */           public void run()
/*      */           {
/* 3326 */             PluginInterface pi = BuddyPluginViewBetaChat.this.plugin.getPluginInterface();
/*      */             
/* 3328 */             Map<String, String> properties = new HashMap();
/*      */             
/*      */             String[] networks;
/*      */             String[] networks;
/* 3332 */             if (BuddyPluginViewBetaChat.this.chat.isAnonymous())
/*      */             {
/* 3334 */               networks = AENetworkClassifier.AT_NON_PUBLIC;
/*      */             }
/*      */             else
/*      */             {
/* 3338 */               networks = AENetworkClassifier.AT_NETWORKS;
/*      */             }
/*      */             
/* 3341 */             String networks_str = "";
/*      */             
/* 3343 */             for (String net : networks)
/*      */             {
/* 3345 */               networks_str = networks_str + (networks_str.length() == 0 ? "" : ",") + net;
/*      */             }
/*      */             
/* 3348 */             properties.put("personal", "true");
/* 3349 */             properties.put("networks", networks_str);
/* 3350 */             properties.put("user_data", "buddyplugin:share");
/*      */             try
/*      */             {
/*      */               Torrent torrent;
/*      */               Torrent torrent;
/* 3355 */               if (file.isFile())
/*      */               {
/* 3357 */                 ShareResourceFile srf = pi.getShareManager().addFile(file, properties);
/*      */                 
/* 3359 */                 torrent = srf.getItem().getTorrent();
/*      */               }
/*      */               else
/*      */               {
/* 3363 */                 ShareResourceDir srd = pi.getShareManager().addDir(file, properties);
/*      */                 
/* 3365 */                 torrent = srd.getItem().getTorrent();
/*      */               }
/*      */               
/* 3368 */               final Download download = pi.getPluginManager().getDefaultPluginInterface().getShortCuts().getDownload(torrent.getHash());
/*      */               
/* 3370 */               if (download == null)
/*      */               {
/* 3372 */                 throw new Exception("Download no longer exists");
/*      */               }
/*      */               
/* 3375 */               Utils.execSWTThread(new Runnable()
/*      */               {
/*      */ 
/*      */                 public void run()
/*      */                 {
/*      */ 
/* 3381 */                   BuddyPluginViewBetaChat.this.dropDownload(download);
/*      */                 }
/*      */                 
/*      */               });
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 3388 */               BuddyPluginViewBetaChat.this.dropFailed(file.getName(), e);
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */         }.start();
/*      */       } else {
/* 3395 */         throw new Exception("File '" + file + "' does not exist or is not accessible");
/*      */       }
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 3400 */       dropFailed(file.getName(), e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void dropDownload(Download download)
/*      */   {
/* 3408 */     String magnet = UrlUtils.getMagnetURI(download, 80);
/*      */     
/* 3410 */     InetSocketAddress address = this.chat.getMyAddress();
/*      */     
/* 3412 */     if (address != null)
/*      */     {
/* 3414 */       String address_str = AddressUtils.getHostAddress(address) + ":" + address.getPort();
/*      */       
/* 3416 */       String arg = "&xsource=" + UrlUtils.encode(address_str);
/*      */       
/* 3418 */       if (magnet.length() + arg.length() < 400)
/*      */       {
/* 3420 */         magnet = magnet + arg;
/*      */       }
/*      */     }
/*      */     
/* 3424 */     if (magnet.length() < 390)
/*      */     {
/* 3426 */       magnet = magnet + "[[$dn]]";
/*      */     }
/*      */     
/* 3429 */     this.plugin.getBeta().tagDownload(download);
/*      */     
/* 3431 */     download.setForceStart(true);
/*      */     
/* 3433 */     this.input_area.setText(this.input_area.getText() + magnet);
/*      */   }
/*      */   
/*      */ 
/*      */   private void dropDownloadFile(DiskManagerFileInfo file)
/*      */   {
/*      */     try
/*      */     {
/* 3441 */       Download download = file.getDownload();
/*      */       
/* 3443 */       if (download.getTorrent().isSimpleTorrent())
/*      */       {
/* 3445 */         dropDownload(download);
/*      */         
/* 3447 */         return;
/*      */       }
/*      */       
/* 3450 */       File target = file.getFile(true);
/*      */       
/* 3452 */       if ((target.exists()) && ((file.getDownloaded() == file.getLength()) || ((download.isComplete()) && (!file.isSkipped()))))
/*      */       {
/*      */ 
/*      */ 
/* 3456 */         dropFile(target);
/*      */       }
/*      */       else
/*      */       {
/* 3460 */         throw new Exception("File is incomplete or missing");
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 3464 */       dropFailed(file.getFile(true).getName(), e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void dropFailed(String content, Throwable e)
/*      */   {
/* 3473 */     UIManager ui_manager = this.plugin.getPluginInterface().getUIManager();
/*      */     
/* 3475 */     String details = MessageText.getString("azbuddy.dchat.share.fail.msg", new String[] { content, Debug.getNestedExceptionMessage(e) });
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 3480 */     ui_manager.showMessageBox("azbuddy.dchat.share.fail.title", "!" + details + "!", 1L);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void close()
/*      */   {
/* 3489 */     if (this.shell != null)
/*      */     {
/* 3491 */       this.shell.dispose();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void closed()
/*      */   {
/* 3498 */     this.chat.removeListener(this);
/*      */     
/* 3500 */     this.chat.destroy();
/*      */     
/* 3502 */     this.view.unregisterUI(this.chat);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void stateChanged(final boolean avail)
/*      */   {
/* 3509 */     if (this.buddy_table.isDisposed())
/*      */     {
/* 3511 */       return;
/*      */     }
/*      */     
/* 3514 */     Utils.execSWTThread(new Runnable()
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/*      */ 
/* 3520 */         if (BuddyPluginViewBetaChat.this.buddy_table.isDisposed())
/*      */         {
/* 3522 */           return;
/*      */         }
/*      */         
/* 3525 */         BuddyPluginViewBetaChat.this.input_area.setEnabled(avail);
/*      */         
/*      */ 
/*      */ 
/* 3529 */         BuddyPluginViewBetaChat.this.nickname.setMessage(BuddyPluginViewBetaChat.this.chat.getDefaultNickname());
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   public void updated()
/*      */   {
/* 3537 */     if (this.status.isDisposed())
/*      */     {
/* 3539 */       return;
/*      */     }
/*      */     
/* 3542 */     Utils.execSWTThread(new Runnable()
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/*      */ 
/* 3548 */         if (BuddyPluginViewBetaChat.this.status.isDisposed())
/*      */         {
/* 3550 */           return;
/*      */         }
/*      */         
/* 3553 */         BuddyPluginViewBetaChat.this.status.setText(BuddyPluginViewBetaChat.this.chat.getStatus());
/*      */         
/* 3555 */         boolean is_shared = BuddyPluginViewBetaChat.this.chat.isSharedNickname();
/*      */         
/* 3557 */         if (is_shared != BuddyPluginViewBetaChat.this.shared_nick_button.getSelection())
/*      */         {
/* 3559 */           BuddyPluginViewBetaChat.this.shared_nick_button.setSelection(is_shared);
/*      */         }
/*      */         
/* 3562 */         if (!BuddyPluginViewBetaChat.this.nickname.isFocusControl())
/*      */         {
/* 3564 */           String old_nick = BuddyPluginViewBetaChat.this.nickname.getText().trim();
/*      */           
/* 3566 */           String new_nick = BuddyPluginViewBetaChat.this.chat.getNickname(false);
/*      */           
/* 3568 */           if (!new_nick.equals(old_nick))
/*      */           {
/* 3570 */             BuddyPluginViewBetaChat.this.nickname.setText(new_nick);
/*      */           }
/*      */         }
/*      */         
/* 3574 */         if (BuddyPluginViewBetaChat.this.table_resort_required)
/*      */         {
/* 3576 */           BuddyPluginViewBetaChat.this.updateTable(false);
/*      */         }
/*      */         
/* 3579 */         BuddyPluginViewBetaChat.this.updateTableHeader();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   private void sortParticipants()
/*      */   {
/* 3587 */     synchronized (this.participants) {
/* 3588 */       Collections.sort(this.participants, new Comparator()
/*      */       {
/*      */ 
/*      */ 
/* 3592 */         private Comparator<String> comp = new FormattersImpl().getAlphanumericComparator(true);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public int compare(BuddyPluginBeta.ChatParticipant p1, BuddyPluginBeta.ChatParticipant p2)
/*      */         {
/* 3599 */           boolean b_p1 = p1.hasNickname();
/* 3600 */           boolean b_p2 = p2.hasNickname();
/*      */           
/* 3602 */           if (b_p1 == b_p2)
/*      */           {
/* 3604 */             return this.comp.compare(p1.getName(BuddyPluginViewBetaChat.this.ftux_ok), p2.getName(BuddyPluginViewBetaChat.this.ftux_ok));
/*      */           }
/* 3606 */           if (b_p1)
/*      */           {
/* 3608 */             return -1;
/*      */           }
/*      */           
/*      */ 
/* 3612 */           return 1;
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void participantAdded(BuddyPluginBeta.ChatParticipant participant)
/*      */   {
/* 3623 */     synchronized (this.participants)
/*      */     {
/* 3625 */       this.participants.add(participant);
/*      */       
/* 3627 */       this.table_resort_required = true;
/*      */     }
/*      */     
/* 3630 */     updateTable(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void participantChanged(final BuddyPluginBeta.ChatParticipant participant)
/*      */   {
/* 3637 */     if (!this.buddy_table.isDisposed())
/*      */     {
/* 3639 */       Utils.execSWTThread(new Runnable()
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/*      */ 
/* 3645 */           if (BuddyPluginViewBetaChat.this.buddy_table.isDisposed())
/*      */           {
/* 3647 */             return;
/*      */           }
/*      */           
/* 3650 */           TableItem[] items = BuddyPluginViewBetaChat.this.buddy_table.getItems();
/*      */           
/* 3652 */           String name = participant.getName(BuddyPluginViewBetaChat.this.ftux_ok);
/*      */           
/* 3654 */           for (TableItem item : items)
/*      */           {
/* 3656 */             if (item.getData() == participant)
/*      */             {
/* 3658 */               BuddyPluginViewBetaChat.this.setProperties(item, participant);
/*      */               
/* 3660 */               String old_name = item.getText(0);
/*      */               
/* 3662 */               if (!old_name.equals(name))
/*      */               {
/* 3664 */                 item.setText(0, name);
/*      */                 
/* 3666 */                 BuddyPluginViewBetaChat.this.table_resort_required = true;
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
/*      */   public void participantRemoved(BuddyPluginBeta.ChatParticipant participant)
/*      */   {
/* 3679 */     synchronized (this.participants)
/*      */     {
/* 3681 */       this.participants.remove(participant);
/*      */       
/* 3683 */       this.participant_last_message_map.remove(participant);
/*      */     }
/*      */     
/* 3686 */     updateTable(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void sendMessage(String text)
/*      */   {
/*      */     try
/*      */     {
/* 3698 */       Pattern p = Pattern.compile("(?i)\\\\u([\\dabcdef]{4})");
/*      */       
/* 3700 */       Matcher m = p.matcher(text);
/*      */       
/* 3702 */       boolean result = m.find();
/*      */       
/* 3704 */       if (result)
/*      */       {
/* 3706 */         StringBuffer sb = new StringBuffer();
/*      */         
/* 3708 */         while (result)
/*      */         {
/* 3710 */           String str = m.group(1);
/*      */           
/* 3712 */           int unicode = Integer.parseInt(str, 16);
/*      */           
/* 3714 */           m.appendReplacement(sb, Matcher.quoteReplacement(String.valueOf((char)unicode)));
/*      */           
/* 3716 */           result = m.find();
/*      */         }
/*      */         
/* 3719 */         m.appendTail(sb);
/*      */         
/* 3721 */         text = sb.toString();
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/* 3726 */     this.chat.sendMessage(text, new HashMap());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static String expand(Map<String, String> params, String str, boolean url_decode)
/*      */   {
/* 3735 */     int pos = 0;
/*      */     
/* 3737 */     String result = "";
/*      */     
/*      */     for (;;)
/*      */     {
/* 3741 */       int new_pos = str.indexOf('$', pos);
/*      */       
/* 3743 */       if (new_pos == -1)
/*      */       {
/* 3745 */         result = result + str.substring(pos);
/*      */         
/* 3747 */         break;
/*      */       }
/*      */       
/* 3750 */       result = result + str.substring(pos, new_pos);
/*      */       
/* 3752 */       int end_pos = str.length();
/*      */       
/* 3754 */       for (int i = new_pos + 1; i < end_pos; i++)
/*      */       {
/* 3756 */         char c = str.charAt(i);
/*      */         
/* 3758 */         if ((!Character.isLetterOrDigit(c)) && (c != '_'))
/*      */         {
/* 3760 */           end_pos = i;
/*      */           
/* 3762 */           break;
/*      */         }
/*      */       }
/*      */       
/* 3766 */       String param = str.substring(new_pos + 1, end_pos);
/*      */       
/* 3768 */       String value = (String)params.get(param);
/*      */       
/* 3770 */       if (value == null)
/*      */       {
/* 3772 */         pos = new_pos + 1;
/*      */         
/* 3774 */         result = result + "$";
/*      */       }
/*      */       else
/*      */       {
/* 3778 */         if (url_decode)
/*      */         {
/* 3780 */           result = result + UrlUtils.decode(value);
/*      */         }
/*      */         else
/*      */         {
/* 3784 */           result = result + value;
/*      */         }
/*      */         
/* 3787 */         pos = end_pos;
/*      */       }
/*      */     }
/*      */     
/* 3791 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void messageReceived(final BuddyPluginBeta.ChatMessage message, boolean sort_outstanding)
/*      */   {
/* 3799 */     if (sort_outstanding)
/*      */     {
/*      */ 
/*      */ 
/* 3803 */       return;
/*      */     }
/*      */     
/* 3806 */     if (!this.log.isDisposed())
/*      */     {
/* 3808 */       Utils.execSWTThread(new Runnable()
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/*      */ 
/* 3814 */           if (BuddyPluginViewBetaChat.this.log.isDisposed())
/*      */           {
/* 3816 */             return;
/*      */           }
/*      */           
/* 3819 */           BuddyPluginViewBetaChat.this.logChatMessage(message);
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void messagesChanged()
/*      */   {
/* 3828 */     if (!this.log.isDisposed())
/*      */     {
/* 3830 */       Utils.execSWTThread(new Runnable()
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/*      */ 
/* 3836 */           if (BuddyPluginViewBetaChat.this.log.isDisposed())
/*      */           {
/* 3838 */             return;
/*      */           }
/*      */           try
/*      */           {
/* 3842 */             BuddyPluginViewBetaChat.this.resetChatMessages();
/*      */             
/* 3844 */             BuddyPluginBeta.ChatMessage[] history = BuddyPluginViewBetaChat.this.chat.getHistory();
/*      */             
/* 3846 */             BuddyPluginViewBetaChat.this.logChatMessages(history);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 3850 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/* 3857 */   private String previous_says = null;
/* 3858 */   private int previous_says_mt = -1;
/* 3859 */   private long last_seen_message = -1L;
/* 3860 */   private long last_seen_message_pending = -1L;
/*      */   
/*      */ 
/*      */   private void resetChatMessages()
/*      */   {
/* 3865 */     this.log.setText("");
/*      */     
/* 3867 */     this.messages.clear();
/*      */     
/* 3869 */     this.previous_says = null;
/*      */     
/* 3871 */     synchronized (this.participants)
/*      */     {
/* 3873 */       this.participant_last_message_map.clear();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void logChatMessage(BuddyPluginBeta.ChatMessage message)
/*      */   {
/* 3881 */     logChatMessages(new BuddyPluginBeta.ChatMessage[] { message });
/*      */   }
/*      */   
/* 3884 */   private final SimpleDateFormat time_format1 = new SimpleDateFormat("HH:mm");
/* 3885 */   private final SimpleDateFormat time_format2a = new SimpleDateFormat("EE h");
/* 3886 */   private final SimpleDateFormat time_format2b = new SimpleDateFormat("a");
/* 3887 */   private final SimpleDateFormat time_format3 = new SimpleDateFormat("dd/MM");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private String getChatTimestamp(long now, long time)
/*      */   {
/* 3894 */     long age = now - time;
/* 3895 */     Date date = new Date(time);
/*      */     
/* 3897 */     if (age < 86400000L)
/*      */     {
/* 3899 */       return this.time_format1.format(date);
/*      */     }
/* 3901 */     if (age < 604800000L)
/*      */     {
/* 3903 */       return this.time_format2a.format(date) + this.time_format2b.format(date).toLowerCase();
/*      */     }
/*      */     
/*      */ 
/* 3907 */     return this.time_format3.format(date);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void logChatMessages(BuddyPluginBeta.ChatMessage[] all_messages)
/*      */   {
/* 3915 */     long now = SystemTime.getCurrentTime();
/*      */     
/* 3917 */     int initial_log_length = this.log.getText().length();
/*      */     
/* 3919 */     StringBuilder appended = new StringBuilder(2048);
/*      */     
/* 3921 */     List<StyleRange> new_ranges = new ArrayList();
/*      */     
/* 3923 */     BuddyPluginBeta.ChatMessage last_message_not_ours = null;
/*      */     
/* 3925 */     boolean ignore_ratings = this.beta.getHideRatings();
/* 3926 */     boolean ignore_search_subs = this.beta.getHideSearchSubs();
/*      */     
/* 3928 */     for (BuddyPluginBeta.ChatMessage message : all_messages)
/*      */     {
/* 3930 */       if (!this.messages.containsKey(message))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 3935 */         String original_msg = message.getMessage();
/*      */         
/* 3937 */         if ((!message.isIgnored()) && (original_msg.length() > 0))
/*      */         {
/* 3939 */           if ((ignore_ratings) || (ignore_search_subs))
/*      */           {
/* 3941 */             int origin = message.getFlagOrigin();
/*      */             
/* 3943 */             if ((origin != 1) || (!ignore_ratings))
/*      */             {
/*      */ 
/*      */ 
/* 3947 */               if ((origin == 3) && (ignore_search_subs)) {}
/*      */             }
/*      */             
/*      */           }
/*      */           else
/*      */           {
/* 3953 */             long time = message.getTimeStamp();
/*      */             
/* 3955 */             BuddyPluginBeta.ChatParticipant participant = message.getParticipant();
/*      */             
/* 3957 */             boolean is_me = participant.isMe();
/*      */             
/* 3959 */             if (!is_me)
/*      */             {
/* 3961 */               last_message_not_ours = message;
/*      */             }
/*      */             
/* 3964 */             int message_start_appended_length = appended.length();
/* 3965 */             int message_start_style_index = new_ranges.size();
/*      */             
/* 3967 */             String nick = message.getNickName();
/*      */             
/* 3969 */             int message_type = message.getMessageType();
/*      */             
/* 3971 */             Font default_font = null;
/* 3972 */             Color default_colour = null;
/*      */             
/* 3974 */             Font info_font = null;
/* 3975 */             Color info_colour = Colors.grey;
/*      */             
/* 3977 */             Color colour = Colors.blues[9];
/*      */             
/* 3979 */             if (message_type == 2)
/*      */             {
/* 3981 */               if ((original_msg.startsWith("*")) && (original_msg.endsWith("*")))
/*      */               {
/* 3983 */                 original_msg = original_msg.substring(1, original_msg.length() - 1);
/*      */                 
/* 3985 */                 info_colour = Colors.black;
/* 3986 */                 info_font = this.bold_font;
/*      */               }
/*      */               else
/*      */               {
/* 3990 */                 colour = Colors.grey;
/*      */               }
/* 3992 */             } else if (message_type == 3)
/*      */             {
/* 3994 */               colour = Colors.red;
/*      */             }
/* 3996 */             else if ((participant.isPinned()) || (is_me))
/*      */             {
/* 3998 */               colour = Colors.fadedGreen;
/*      */             }
/* 4000 */             else if (message.isNickClash())
/*      */             {
/* 4002 */               colour = Colors.red;
/*      */             }
/*      */             
/* 4005 */             String stamp = getChatTimestamp(now, time);
/*      */             
/*      */             BuddyPluginBeta.ChatMessage last_message;
/*      */             
/* 4009 */             synchronized (this.participants)
/*      */             {
/* 4011 */               last_message = (BuddyPluginBeta.ChatMessage)this.participant_last_message_map.get(participant);
/*      */               
/* 4013 */               this.participant_last_message_map.put(participant, message);
/*      */             }
/*      */             
/* 4016 */             boolean is_me_msg = message.getFlagType() == 1;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 4021 */             int was_len = 0;
/*      */             int stamp_len;
/* 4023 */             String says; int stamp_len; if (message_type != 1)
/*      */             {
/* 4025 */               String says = "[" + stamp + "]";
/*      */               
/* 4027 */               stamp_len = says.length();
/*      */             }
/*      */             else
/*      */             {
/* 4031 */               says = "[" + stamp + "] " + (nick.length() > 20 ? nick.substring(0, 16) + "..." : nick);
/*      */               
/* 4033 */               stamp_len = stamp.length() + 3;
/*      */               
/* 4035 */               if ((last_message != null) && (!is_me))
/*      */               {
/* 4037 */                 String last_nick = last_message.getNickName();
/*      */                 
/* 4039 */                 if (!nick.equals(last_nick))
/*      */                 {
/* 4041 */                   String was = " (was " + (last_nick.length() > 20 ? last_nick.substring(0, 16) + "..." : last_nick) + ")";
/*      */                   
/* 4043 */                   says = says + was;
/*      */                   
/* 4045 */                   was_len = was.length();
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/* 4050 */             if (message_type == 1)
/*      */             {
/* 4052 */               if (is_me_msg)
/*      */               {
/* 4054 */                 says = says + " ";
/*      */                 
/* 4056 */                 default_colour = colour;
/*      */                 
/* 4058 */                 if (is_me)
/*      */                 {
/* 4060 */                   default_font = this.italic_font;
/*      */                 }
/*      */               }
/*      */               else
/*      */               {
/* 4065 */                 says = says + "\n";
/*      */               }
/*      */             }
/*      */             else {
/* 4069 */               says = says + " ";
/*      */               
/* 4071 */               if (message_type == 3)
/*      */               {
/* 4073 */                 default_colour = colour;
/*      */               }
/*      */             }
/*      */             
/*      */ 
/* 4078 */             if ((this.previous_says == null) || (this.previous_says_mt != message_type) || (is_me_msg) || (!this.previous_says.equals(says)))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 4083 */               this.previous_says = says;
/* 4084 */               this.previous_says_mt = message_type;
/*      */               
/* 4086 */               int start = initial_log_length + appended.length();
/*      */               
/* 4088 */               appended.append(says);
/*      */               
/*      */ 
/* 4091 */               StyleRange styleRange = new MyStyleRange(message);
/* 4092 */               styleRange.start = start;
/* 4093 */               styleRange.length = stamp_len;
/* 4094 */               styleRange.foreground = Colors.grey;
/*      */               
/* 4096 */               if (is_me)
/*      */               {
/* 4098 */                 styleRange.font = this.italic_font;
/*      */               }
/*      */               
/* 4101 */               new_ranges.add(styleRange);
/*      */               
/*      */ 
/* 4104 */               if (colour != Colors.black)
/*      */               {
/* 4106 */                 int rem = says.length() - stamp_len;
/*      */                 
/* 4108 */                 if (rem > 0)
/*      */                 {
/* 4110 */                   StyleRange styleRange = new MyStyleRange(message);
/* 4111 */                   styleRange.start = (start + stamp_len);
/* 4112 */                   styleRange.length = (rem - was_len);
/* 4113 */                   styleRange.foreground = colour;
/* 4114 */                   styleRange.data = participant;
/*      */                   
/* 4116 */                   if (is_me)
/*      */                   {
/* 4118 */                     styleRange.font = this.italic_font;
/*      */                   }
/*      */                   
/* 4121 */                   new_ranges.add(styleRange);
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/* 4126 */             int start = initial_log_length + appended.length();
/*      */             
/* 4128 */             String rendered_msg = renderMessage(this.beta, this.chat, message, original_msg, message_type, start, new_ranges, info_font, info_colour, this.bold_font);
/*      */             
/* 4130 */             appended.append(rendered_msg);
/*      */             
/*      */ 
/*      */ 
/* 4134 */             if ((default_font != null) || (default_colour != null))
/*      */             {
/* 4136 */               int message_start_log_length = initial_log_length + message_start_appended_length;
/*      */               
/* 4138 */               int pos = message_start_log_length;
/*      */               
/* 4140 */               for (int i = message_start_style_index; i < new_ranges.size(); i++)
/*      */               {
/* 4142 */                 StyleRange style = (StyleRange)new_ranges.get(i);
/*      */                 
/* 4144 */                 int style_start = style.start;
/* 4145 */                 int style_length = style.length;
/*      */                 
/* 4147 */                 if (style_start > pos)
/*      */                 {
/*      */ 
/*      */ 
/* 4151 */                   StyleRange styleRange = new MyStyleRange(message);
/* 4152 */                   styleRange.start = pos;
/* 4153 */                   styleRange.length = (style_start - pos);
/*      */                   
/* 4155 */                   if (default_colour != null) {
/* 4156 */                     styleRange.foreground = default_colour;
/*      */                   }
/* 4158 */                   if (default_font != null) {
/* 4159 */                     styleRange.font = default_font;
/*      */                   }
/*      */                   
/* 4162 */                   new_ranges.add(i, styleRange);
/*      */                   
/* 4164 */                   i++;
/*      */                 }
/*      */                 
/* 4167 */                 pos = style_start + style_length;
/*      */               }
/*      */               
/* 4170 */               int message_end_log_length = initial_log_length + appended.length();
/*      */               
/* 4172 */               if (pos < message_end_log_length)
/*      */               {
/*      */ 
/*      */ 
/* 4176 */                 StyleRange styleRange = new MyStyleRange(message);
/* 4177 */                 styleRange.start = pos;
/* 4178 */                 styleRange.length = (message_end_log_length - pos);
/*      */                 
/* 4180 */                 if (default_colour != null) {
/* 4181 */                   styleRange.foreground = default_colour;
/*      */                 }
/* 4183 */                 if (default_font != null) {
/* 4184 */                   styleRange.font = default_font;
/*      */                 }
/*      */                 
/* 4187 */                 new_ranges.add(styleRange);
/*      */               }
/*      */             }
/*      */             
/* 4191 */             appended.append("\n");
/*      */             
/* 4193 */             int actual_length = appended.length() - message_start_appended_length;
/*      */             
/* 4195 */             this.messages.put(message, Integer.valueOf(actual_length));
/*      */           } }
/*      */       }
/*      */     }
/* 4199 */     if (appended.length() > 0)
/*      */     {
/*      */       try {
/* 4202 */         this.log.setVisible(false);
/*      */         
/* 4204 */         this.log.append(appended.toString());
/*      */         
/* 4206 */         if (new_ranges.size() > 0)
/*      */         {
/* 4208 */           List<StyleRange> existing_ranges = Arrays.asList(this.log.getStyleRanges());
/*      */           
/* 4210 */           List<StyleRange> all_ranges = new ArrayList(existing_ranges.size() + new_ranges.size());
/*      */           
/* 4212 */           all_ranges.addAll(existing_ranges);
/*      */           
/* 4214 */           all_ranges.addAll(new_ranges);
/*      */           
/* 4216 */           StyleRange[] ranges = (StyleRange[])all_ranges.toArray(new StyleRange[all_ranges.size()]);
/*      */           
/* 4218 */           for (StyleRange sr : ranges)
/*      */           {
/* 4220 */             sr.borderStyle = 0;
/*      */           }
/*      */           
/* 4223 */           this.log.setStyleRanges(ranges);
/*      */           
/* 4225 */           this.log_styles = ranges;
/*      */         }
/*      */         
/*      */ 
/* 4229 */         Iterator<Integer> it = null;
/*      */         
/* 4231 */         int max_lines = this.beta.getMaxUILines();
/* 4232 */         int max_chars = this.beta.getMaxUICharsKB() * 1024;
/*      */         
/* 4234 */         while ((this.messages.size() > max_lines) || (this.log.getText().length() > max_chars))
/*      */         {
/* 4236 */           if (it == null)
/*      */           {
/* 4238 */             it = this.messages.values().iterator();
/*      */           }
/*      */           
/* 4241 */           if (!it.hasNext()) {
/*      */             break;
/*      */           }
/*      */           
/*      */ 
/* 4246 */           int to_remove = ((Integer)it.next()).intValue();
/*      */           
/* 4248 */           it.remove();
/*      */           
/* 4250 */           this.log.replaceTextRange(0, to_remove, "");
/*      */           
/* 4252 */           this.log_styles = this.log.getStyleRanges();
/*      */         }
/*      */         
/* 4255 */         this.log.setSelection(this.log.getText().length());
/*      */       }
/*      */       finally
/*      */       {
/* 4259 */         this.log.setVisible(true);
/*      */       }
/*      */       
/* 4262 */       if (last_message_not_ours != null)
/*      */       {
/* 4264 */         long last_message_not_ours_time = last_message_not_ours.getTimeStamp();
/*      */         
/* 4266 */         boolean mesages_seen = true;
/*      */         
/* 4268 */         if (this.build_complete)
/*      */         {
/* 4270 */           if ((!this.log.isVisible()) || ((this.shell != null) && (this.shell.getMinimized())) || (this.log.getDisplay().getFocusControl() == null))
/*      */           {
/*      */ 
/*      */ 
/* 4274 */             if (last_message_not_ours_time > this.last_seen_message)
/*      */             {
/* 4276 */               this.last_seen_message_pending = last_message_not_ours_time;
/*      */               
/* 4278 */               this.view.betaMessagePending(this.chat, this.log, last_message_not_ours);
/*      */               
/* 4280 */               mesages_seen = false;
/*      */             }
/*      */           }
/*      */           else {
/* 4284 */             this.last_seen_message = last_message_not_ours_time;
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */         }
/* 4290 */         else if (last_message_not_ours_time > this.last_seen_message)
/*      */         {
/* 4292 */           this.last_seen_message = last_message_not_ours_time;
/*      */         }
/*      */         
/*      */ 
/* 4296 */         if (mesages_seen)
/*      */         {
/* 4298 */           for (BuddyPluginBeta.ChatMessage msg : all_messages)
/*      */           {
/* 4300 */             msg.setSeen(true);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
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
/*      */   protected static String renderMessage(BuddyPluginBeta beta, BuddyPluginBeta.ChatInstance chat, BuddyPluginBeta.ChatMessage message, String original_msg, int message_type, int start, List<StyleRange> new_ranges, Font info_font, Color info_colour, Font bold_font)
/*      */   {
/* 4320 */     String msg = original_msg;
/*      */     
/*      */     try
/*      */     {
/* 4324 */       List<Object> segments = new ArrayList();
/*      */       
/* 4326 */       int pos = 0;
/*      */       
/*      */       for (;;)
/*      */       {
/* 4330 */         int old_pos = pos;
/*      */         
/* 4332 */         pos = original_msg.indexOf(':', old_pos);
/*      */         
/* 4334 */         if (pos == -1)
/*      */         {
/* 4336 */           String tail = original_msg.substring(old_pos);
/*      */           
/* 4338 */           if (tail.length() <= 0)
/*      */             break;
/* 4340 */           segments.add(tail); break;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 4346 */         boolean was_url = false;
/*      */         
/* 4348 */         String protocol = "";
/*      */         
/* 4350 */         for (int i = pos - 1; i >= 0; i--)
/*      */         {
/* 4352 */           char c = original_msg.charAt(i);
/*      */           
/* 4354 */           if (!Character.isLetterOrDigit(c))
/*      */           {
/* 4356 */             if (c != '"')
/*      */               break;
/* 4358 */             protocol = c + protocol; break;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 4364 */           protocol = c + protocol;
/*      */         }
/*      */         
/* 4367 */         if (protocol.length() > 0)
/*      */         {
/* 4369 */           char term_char = ' ';
/*      */           
/* 4371 */           if (protocol.startsWith("\""))
/*      */           {
/* 4373 */             term_char = '"';
/*      */           }
/*      */           
/* 4376 */           int url_start = pos - protocol.length();
/* 4377 */           int url_end = original_msg.length();
/*      */           
/* 4379 */           for (int i = pos + 1; i < url_end; i++)
/*      */           {
/* 4381 */             char c = original_msg.charAt(i);
/*      */             
/* 4383 */             if ((c == term_char) || ((term_char == ' ') && (Character.isWhitespace(c))))
/*      */             {
/* 4385 */               url_end = term_char == ' ' ? i : i + 1;
/*      */               
/* 4387 */               break;
/*      */             }
/*      */           }
/*      */           
/* 4391 */           if ((url_end > pos + 1) && (!Character.isDigit(protocol.charAt(0)))) {
/*      */             try
/*      */             {
/* 4394 */               String url_str = protocol + original_msg.substring(pos, url_end);
/*      */               
/* 4396 */               if ((url_str.startsWith("\"")) && (url_str.endsWith("\"")))
/*      */               {
/* 4398 */                 url_str = url_str.substring(1, url_str.length() - 1);
/*      */                 
/* 4400 */                 protocol = protocol.substring(1);
/*      */               }
/*      */               
/* 4403 */               URL url = new URL(url_str);
/*      */               
/* 4405 */               if (url_start > old_pos)
/*      */               {
/* 4407 */                 segments.add(original_msg.substring(old_pos, url_start));
/*      */               }
/*      */               
/* 4410 */               segments.add(url);
/*      */               
/* 4412 */               was_url = true;
/*      */               
/* 4414 */               pos = url_end;
/*      */             }
/*      */             catch (Throwable e) {}
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 4422 */         if (!was_url)
/*      */         {
/* 4424 */           pos++;
/*      */           
/* 4426 */           segments.add(original_msg.substring(old_pos, pos));
/*      */         }
/*      */       }
/*      */       
/* 4430 */       if (segments.size() > 1)
/*      */       {
/* 4432 */         List<Object> temp = new ArrayList(segments.size());
/*      */         
/* 4434 */         String str = "";
/*      */         
/* 4436 */         for (Object obj : segments)
/*      */         {
/* 4438 */           if ((obj instanceof String))
/*      */           {
/* 4440 */             str = str + obj;
/*      */           }
/*      */           else
/*      */           {
/* 4444 */             if (str.length() > 0)
/*      */             {
/* 4446 */               temp.add(str);
/*      */             }
/*      */             
/* 4449 */             str = "";
/*      */             
/* 4451 */             temp.add(obj);
/*      */           }
/*      */         }
/*      */         
/* 4455 */         if (str.length() > 0)
/*      */         {
/* 4457 */           temp.add(str);
/*      */         }
/*      */         
/* 4460 */         segments = temp;
/*      */       }
/*      */       
/* 4463 */       Map<String, String> params = new HashMap();
/*      */       
/* 4465 */       for (int i = 0; i < segments.size(); i++)
/*      */       {
/* 4467 */         Object obj = segments.get(i);
/*      */         
/* 4469 */         if ((obj instanceof URL))
/*      */         {
/* 4471 */           params.clear();
/*      */           
/* 4473 */           String str = ((URL)obj).toExternalForm();
/*      */           
/* 4475 */           int qpos = str.indexOf('?');
/*      */           
/* 4477 */           if (qpos > 0)
/*      */           {
/* 4479 */             int hpos = str.lastIndexOf("[[");
/*      */             
/* 4481 */             String[] bits = str.substring(qpos + 1, hpos == -1 ? str.length() : hpos).split("&");
/*      */             
/* 4483 */             for (String bit : bits)
/*      */             {
/* 4485 */               String[] temp = bit.split("=", 2);
/*      */               
/* 4487 */               if (temp.length == 2)
/*      */               {
/* 4489 */                 params.put(temp[0], temp[1]);
/*      */               }
/*      */             }
/*      */             
/* 4493 */             if ((hpos > 0) && (str.endsWith("]]")))
/*      */             {
/* 4495 */               str = str.substring(0, hpos) + "[[" + expand(params, str.substring(hpos + 2, str.length() - 2), false) + "]]";
/*      */               
/*      */ 
/*      */ 
/*      */               try
/*      */               {
/* 4501 */                 segments.set(i, new URL(str));
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/* 4505 */                 Debug.out(e);
/*      */               }
/*      */             }
/*      */           }
/*      */           else {
/* 4510 */             int hpos = str.lastIndexOf("[[");
/*      */             
/* 4512 */             if ((hpos > 0) && (str.endsWith("]]")))
/*      */             {
/* 4514 */               str = str.substring(0, hpos) + "[[" + str.substring(hpos + 2, str.length() - 2) + "]]";
/*      */               
/*      */ 
/*      */ 
/*      */               try
/*      */               {
/* 4520 */                 segments.set(i, new URL(str));
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/* 4524 */                 Debug.out(e);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         else {
/* 4530 */           String str = (String)obj;
/*      */           
/* 4532 */           if (params.size() > 0)
/*      */           {
/* 4534 */             segments.set(i, expand(params, str, true));
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 4539 */       StringBuilder sb = new StringBuilder(1024);
/*      */       
/* 4541 */       for (Object obj : segments)
/*      */       {
/* 4543 */         if ((obj instanceof URL))
/*      */         {
/* 4545 */           sb.append("\"").append(((URL)obj).toExternalForm()).append("\"");
/*      */         }
/*      */         else
/*      */         {
/* 4549 */           String segment_str = (String)obj;
/*      */           try
/*      */           {
/* 4552 */             String my_nick = chat.getNickname(true);
/*      */             
/* 4554 */             if ((my_nick.length() > 0) && (segment_str.contains(my_nick)) && (message_type == 1))
/*      */             {
/*      */ 
/*      */ 
/* 4558 */               StringBuilder temp = new StringBuilder(segment_str.length() + 1024);
/*      */               
/* 4560 */               int nick_len = my_nick.length();
/*      */               
/* 4562 */               int segment_len = segment_str.length();
/*      */               
/* 4564 */               int segment_pos = 0;
/*      */               
/* 4566 */               while (segment_pos < segment_len)
/*      */               {
/* 4568 */                 int next_pos = segment_str.indexOf(my_nick, segment_pos);
/*      */                 
/* 4570 */                 if (next_pos >= 0)
/*      */                 {
/* 4572 */                   temp.append(segment_str.substring(segment_pos, next_pos));
/*      */                   
/* 4574 */                   boolean match = true;
/*      */                   
/* 4576 */                   if (next_pos > 0)
/*      */                   {
/* 4578 */                     if (Character.isLetterOrDigit(segment_str.charAt(next_pos - 1)))
/*      */                     {
/* 4580 */                       match = false;
/*      */                     }
/*      */                   }
/*      */                   
/* 4584 */                   int nick_end = next_pos + nick_len;
/*      */                   
/* 4586 */                   if (nick_end < segment_len)
/*      */                   {
/* 4588 */                     if (Character.isLetterOrDigit(segment_str.charAt(nick_end)))
/*      */                     {
/* 4590 */                       match = false;
/*      */                     }
/*      */                   }
/*      */                   
/* 4594 */                   if (match)
/*      */                   {
/* 4596 */                     temp.append("\"chat:nick[[").append(UrlUtils.encode(my_nick)).append("]]\"");
/*      */                   }
/*      */                   else
/*      */                   {
/* 4600 */                     temp.append(my_nick);
/*      */                   }
/*      */                   
/* 4603 */                   segment_pos = next_pos + nick_len;
/*      */                 }
/*      */                 else
/*      */                 {
/* 4607 */                   temp.append(segment_str.substring(segment_pos));
/*      */                   
/* 4609 */                   break;
/*      */                 }
/*      */               }
/*      */               
/* 4613 */               segment_str = temp.toString();
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/* 4617 */             Debug.out(e);
/*      */           }
/*      */           
/* 4620 */           sb.append(segment_str);
/*      */         }
/*      */       }
/*      */       
/* 4624 */       msg = sb.toString();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4630 */       int next_style_start = start;
/*      */       
/* 4632 */       int pos = 0;
/*      */       
/* 4634 */       while (pos < msg.length())
/*      */       {
/* 4636 */         pos = msg.indexOf(':', pos);
/*      */         
/* 4638 */         if (pos == -1) {
/*      */           break;
/*      */         }
/*      */         
/*      */ 
/* 4643 */         String protocol = "";
/*      */         
/* 4645 */         for (int i = pos - 1; i >= 0; i--)
/*      */         {
/* 4647 */           char c = msg.charAt(i);
/*      */           
/* 4649 */           if (!Character.isLetterOrDigit(c))
/*      */           {
/* 4651 */             if (c != '"')
/*      */               break;
/* 4653 */             protocol = c + protocol; break;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 4659 */           protocol = c + protocol;
/*      */         }
/*      */         
/* 4662 */         if (protocol.length() > 0)
/*      */         {
/* 4664 */           char term_char = ' ';
/*      */           
/* 4666 */           if (protocol.startsWith("\""))
/*      */           {
/* 4668 */             term_char = '"';
/*      */           }
/*      */           
/* 4671 */           int url_start = pos - protocol.length();
/* 4672 */           int url_end = msg.length();
/*      */           
/* 4674 */           for (int i = pos + 1; i < url_end; i++)
/*      */           {
/* 4676 */             char c = msg.charAt(i);
/*      */             
/* 4678 */             if ((c == term_char) || ((term_char == ' ') && (Character.isWhitespace(c))))
/*      */             {
/* 4680 */               url_end = term_char == ' ' ? i : i + 1;
/*      */               
/* 4682 */               break;
/*      */             }
/*      */           }
/*      */           
/* 4686 */           if ((url_end > pos + 1) && (!Character.isDigit(protocol.charAt(0)))) {
/*      */             try
/*      */             {
/* 4689 */               String url_str = protocol + msg.substring(pos, url_end);
/*      */               
/* 4691 */               if ((url_str.startsWith("\"")) && (url_str.endsWith("\"")))
/*      */               {
/* 4693 */                 url_str = url_str.substring(1, url_str.length() - 1);
/*      */                 
/* 4695 */                 protocol = protocol.substring(1);
/*      */               }
/*      */               URL url;
/* 4698 */               if (protocol.equalsIgnoreCase("chat"))
/*      */               {
/* 4700 */                 if (url_str.toLowerCase(Locale.US).startsWith("chat:anon"))
/*      */                 {
/* 4702 */                   if ((beta != null) && (!beta.isI2PAvailable()))
/*      */                   {
/* 4704 */                     throw new Exception("Anonymous chat unavailable");
/*      */                   }
/*      */                   
/*      */                 }
/*      */                 
/*      */               }
/*      */               else {
/* 4711 */                 url = new URL(url_str);
/*      */               }
/*      */               
/* 4714 */               String original_url_str = url_str;
/*      */               
/* 4716 */               String display_url = UrlUtils.decode(url_str);
/*      */               
/*      */ 
/*      */ 
/* 4720 */               int hack_pos = url_str.lastIndexOf("[[");
/*      */               
/* 4722 */               if ((hack_pos > 0) && (url_str.endsWith("]]")))
/*      */               {
/* 4724 */                 String substitution = url_str.substring(hack_pos + 2, url_str.length() - 2).trim();
/*      */                 
/* 4726 */                 url_str = url_str.substring(0, hack_pos);
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/* 4731 */                 boolean safe = (protocol.equals("azplug")) || (protocol.equals("chat"));
/*      */                 
/* 4733 */                 if ((safe) || (UrlUtils.parseTextForURL(substitution, true) == null))
/*      */                 {
/* 4735 */                   display_url = UrlUtils.decode(substitution);
/*      */                 }
/*      */                 else
/*      */                 {
/* 4739 */                   display_url = UrlUtils.decode(url_str);
/*      */                 }
/*      */               }
/*      */               
/* 4743 */               if ((term_char != ' ') || (!display_url.equals(original_url_str)))
/*      */               {
/* 4745 */                 int old_len = msg.length();
/*      */                 
/* 4747 */                 msg = msg.substring(0, url_start) + display_url + msg.substring(url_end);
/*      */                 
/*      */ 
/*      */ 
/* 4751 */                 url_end += msg.length() - old_len;
/*      */               }
/*      */               
/* 4754 */               int this_style_start = start + url_start;
/* 4755 */               int this_style_length = display_url.length();
/*      */               
/* 4757 */               if (this_style_start > next_style_start)
/*      */               {
/* 4759 */                 if (message_type == 2)
/*      */                 {
/* 4761 */                   StyleRange styleRange = new MyStyleRange(message);
/* 4762 */                   styleRange.start = next_style_start;
/* 4763 */                   styleRange.length = (this_style_start - next_style_start);
/* 4764 */                   styleRange.foreground = info_colour;
/* 4765 */                   styleRange.font = info_font;
/*      */                   
/* 4767 */                   new_ranges.add(styleRange);
/*      */                   
/* 4769 */                   next_style_start = this_style_start + this_style_length;
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4777 */               boolean will_work = true;
/*      */               
/*      */               try
/*      */               {
/* 4781 */                 String lc_url = url_str.toLowerCase(Locale.US);
/*      */                 
/* 4783 */                 if (lc_url.startsWith("magnet"))
/*      */                 {
/* 4785 */                   if ((!lc_url.contains("btih:")) || (lc_url.contains("btih:&")) || (lc_url.endsWith("btih:")))
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4791 */                     if (!lc_url.contains("&fl="))
/*      */                     {
/*      */ 
/*      */ 
/* 4795 */                       will_work = false;
/*      */                     }
/*      */                   }
/* 4798 */                 } else if (lc_url.startsWith("chat:nick"))
/*      */                 {
/* 4800 */                   will_work = false;
/*      */                 }
/*      */               }
/*      */               catch (Throwable e) {}
/*      */               
/*      */ 
/* 4806 */               if (will_work)
/*      */               {
/* 4808 */                 StyleRange styleRange = new MyStyleRange(message);
/* 4809 */                 styleRange.start = this_style_start;
/* 4810 */                 styleRange.length = this_style_length;
/* 4811 */                 styleRange.foreground = Colors.blue;
/* 4812 */                 styleRange.underline = true;
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4818 */                 styleRange.data = url_str;
/*      */                 
/* 4820 */                 new_ranges.add(styleRange);
/*      */               }
/*      */               else
/*      */               {
/* 4824 */                 StyleRange styleRange = new MyStyleRange(message);
/* 4825 */                 styleRange.start = this_style_start;
/* 4826 */                 styleRange.length = this_style_length;
/* 4827 */                 styleRange.font = bold_font;
/*      */                 
/* 4829 */                 new_ranges.add(styleRange);
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {}
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 4837 */           pos = url_end;
/*      */         }
/*      */         else
/*      */         {
/* 4841 */           pos += 1;
/*      */         }
/*      */       }
/*      */       
/* 4845 */       if (next_style_start < start + msg.length())
/*      */       {
/* 4847 */         if (message_type == 2)
/*      */         {
/* 4849 */           StyleRange styleRange = new MyStyleRange(message);
/* 4850 */           styleRange.start = next_style_start;
/* 4851 */           styleRange.length = (start + msg.length() - next_style_start);
/* 4852 */           styleRange.foreground = info_colour;
/* 4853 */           styleRange.font = info_font;
/*      */           
/* 4855 */           new_ranges.add(styleRange);
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 4861 */       Debug.out(e);
/*      */     }
/*      */     
/* 4864 */     return msg;
/*      */   }
/*      */   
/*      */ 
/*      */   public void activate()
/*      */   {
/* 4870 */     if (this.last_seen_message_pending > this.last_seen_message)
/*      */     {
/* 4872 */       this.last_seen_message = this.last_seen_message_pending;
/*      */     }
/*      */     
/* 4875 */     this.view.betaMessagePending(this.chat, this.log, null);
/*      */     
/* 4877 */     List<BuddyPluginBeta.ChatMessage> unseen = this.chat.getUnseenMessages();
/*      */     
/* 4879 */     for (BuddyPluginBeta.ChatMessage msg : unseen)
/*      */     {
/* 4881 */       msg.setSeen(true);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static class MyStyleRange
/*      */     extends StyleRange
/*      */   {
/*      */     private BuddyPluginBeta.ChatMessage message;
/*      */     
/*      */ 
/*      */     MyStyleRange(BuddyPluginBeta.ChatMessage _msg)
/*      */     {
/* 4894 */       this.message = _msg;
/*      */       
/* 4896 */       this.data = this.message;
/*      */     }
/*      */     
/*      */ 
/*      */     MyStyleRange(MyStyleRange other)
/*      */     {
/* 4902 */       super();
/*      */       
/* 4904 */       this.message = other.message;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewBetaChat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */