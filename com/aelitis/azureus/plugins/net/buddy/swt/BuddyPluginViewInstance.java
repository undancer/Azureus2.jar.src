/*      */ package com.aelitis.azureus.plugins.net.buddy.swt;
/*      */ 
/*      */ import com.aelitis.azureus.core.security.CryptoHandler;
/*      */ import com.aelitis.azureus.core.security.CryptoManager;
/*      */ import com.aelitis.azureus.core.util.AZ3Functions;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPlugin;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPlugin.cryptoResult;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBeta;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBeta.ChatInstance;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBuddy;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBuddyMessage;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBuddyMessageHandler;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginException;
/*      */ import com.aelitis.azureus.plugins.net.buddy.tracker.BuddyPluginTracker;
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*      */ import java.net.InetAddress;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.eclipse.swt.custom.CTabFolder;
/*      */ import org.eclipse.swt.custom.CTabItem;
/*      */ import org.eclipse.swt.custom.StyleRange;
/*      */ import org.eclipse.swt.custom.StyledText;
/*      */ import org.eclipse.swt.dnd.Clipboard;
/*      */ import org.eclipse.swt.dnd.TextTransfer;
/*      */ import org.eclipse.swt.events.KeyEvent;
/*      */ import org.eclipse.swt.events.MenuEvent;
/*      */ import org.eclipse.swt.events.MenuListener;
/*      */ import org.eclipse.swt.events.MouseEvent;
/*      */ import org.eclipse.swt.events.SelectionAdapter;
/*      */ import org.eclipse.swt.events.SelectionEvent;
/*      */ import org.eclipse.swt.graphics.Color;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.layout.FormAttachment;
/*      */ import org.eclipse.swt.layout.FormData;
/*      */ import org.eclipse.swt.layout.GridData;
/*      */ import org.eclipse.swt.layout.GridLayout;
/*      */ import org.eclipse.swt.widgets.Button;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.FileDialog;
/*      */ import org.eclipse.swt.widgets.Group;
/*      */ import org.eclipse.swt.widgets.Label;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Menu;
/*      */ import org.eclipse.swt.widgets.MenuItem;
/*      */ import org.eclipse.swt.widgets.Sash;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.eclipse.swt.widgets.Table;
/*      */ import org.eclipse.swt.widgets.TableColumn;
/*      */ import org.eclipse.swt.widgets.TableItem;
/*      */ import org.eclipse.swt.widgets.Text;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.core3.util.SHA1Simple;
/*      */ import org.gudy.azureus2.plugins.PluginConfig;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.ui.UIInputReceiver;
/*      */ import org.gudy.azureus2.plugins.ui.UIInstance;
/*      */ import org.gudy.azureus2.plugins.utils.LocaleUtilities;
/*      */ import org.gudy.azureus2.ui.swt.Messages;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.config.IntParameter;
/*      */ import org.gudy.azureus2.ui.swt.config.Parameter;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.SWTThread;
/*      */ 
/*      */ public class BuddyPluginViewInstance implements com.aelitis.azureus.plugins.net.buddy.BuddyPluginListener, com.aelitis.azureus.plugins.net.buddy.BuddyPluginBuddyRequestListener
/*      */ {
/*      */   private static final int LOG_NORMAL = 1;
/*      */   private static final int LOG_SUCCESS = 2;
/*      */   private static final int LOG_ERROR = 3;
/*      */   private final BuddyPluginView view;
/*      */   private final BuddyPlugin plugin;
/*      */   private final UIInstance ui_instance;
/*      */   private final LocaleUtilities lu;
/*      */   private final BuddyPluginTracker tracker;
/*      */   private Composite composite;
/*      */   private Table buddy_table;
/*      */   private StyledText log;
/*      */   private CTabFolder tab_folder;
/*      */   private CTabItem beta_item;
/*      */   private CTabItem classic_item;
/*      */   private Text public_nickname;
/*      */   private Text anon_nickname;
/*   95 */   private List<BuddyPluginBuddy> buddies = new java.util.ArrayList();
/*      */   
/*      */ 
/*      */ 
/*      */   private Button plugin_install_button;
/*      */   
/*      */ 
/*      */   private boolean init_complete;
/*      */   
/*      */ 
/*      */ 
/*      */   protected BuddyPluginViewInstance(BuddyPluginView _view, BuddyPlugin _plugin, UIInstance _ui_instance, Composite _composite)
/*      */   {
/*  108 */     this.view = _view;
/*  109 */     this.plugin = _plugin;
/*  110 */     this.ui_instance = _ui_instance;
/*  111 */     this.composite = _composite;
/*      */     
/*  113 */     this.tracker = this.plugin.getTracker();
/*      */     
/*  115 */     this.lu = this.plugin.getPluginInterface().getUtilities().getLocaleUtilities();
/*      */     
/*  117 */     this.tab_folder = new CTabFolder(this.composite, 16384);
/*      */     
/*  119 */     this.tab_folder.setBorderVisible(true);
/*  120 */     this.tab_folder.setTabHeight(Utils.adjustPXForDPI(20));
/*  121 */     GridData grid_data = new GridData(1808);
/*  122 */     Utils.setLayoutData(this.tab_folder, grid_data);
/*      */     
/*  124 */     this.beta_item = new CTabItem(this.tab_folder, 0);
/*      */     
/*  126 */     this.beta_item.setText(this.lu.getLocalisedMessageText("azbuddy.dchat.decentralized"));
/*      */     
/*  128 */     Composite beta_area = new Composite(this.tab_folder, 0);
/*  129 */     this.beta_item.setControl(beta_area);
/*      */     
/*  131 */     createBeta(beta_area);
/*      */     
/*  133 */     this.classic_item = new CTabItem(this.tab_folder, 0);
/*      */     
/*  135 */     this.classic_item.setText(this.lu.getLocalisedMessageText("label.classic"));
/*      */     
/*  137 */     Composite classic_area = new Composite(this.tab_folder, 0);
/*  138 */     this.classic_item.setControl(classic_area);
/*      */     
/*  140 */     createClassic(classic_area);
/*      */     
/*  142 */     this.tab_folder.setSelection(this.beta_item);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void selectClassicTab()
/*      */   {
/*  148 */     Utils.execSWTThread(new Runnable()
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/*  153 */         BuddyPluginViewInstance.this.tab_folder.setSelection(BuddyPluginViewInstance.this.classic_item);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void createBeta(Composite main)
/*      */   {
/*  162 */     final BuddyPluginBeta plugin_beta = this.plugin.getBeta();
/*      */     
/*  164 */     GridLayout layout = new GridLayout();
/*  165 */     layout.numColumns = 3;
/*      */     
/*  167 */     main.setLayout(layout);
/*  168 */     GridData grid_data = new GridData(1808);
/*  169 */     Utils.setLayoutData(main, grid_data);
/*      */     
/*  171 */     if (!this.plugin.isBetaEnabled())
/*      */     {
/*  173 */       Label control_label = new Label(main, 0);
/*  174 */       control_label.setText(this.lu.getLocalisedMessageText("azbuddy.disabled"));
/*      */       
/*  176 */       return;
/*      */     }
/*      */     
/*  179 */     final BuddyPluginBeta beta = this.plugin.getBeta();
/*      */     
/*  181 */     boolean i2p_enabled = plugin_beta.isI2PAvailable();
/*      */     
/*      */ 
/*      */ 
/*  185 */     Composite info_area = new Composite(main, 0);
/*  186 */     layout = new GridLayout();
/*  187 */     layout.numColumns = 3;
/*  188 */     layout.marginHeight = 0;
/*  189 */     layout.marginWidth = 0;
/*  190 */     info_area.setLayout(layout);
/*  191 */     grid_data = new GridData(768);
/*  192 */     grid_data.horizontalSpan = 3;
/*  193 */     Utils.setLayoutData(info_area, grid_data);
/*      */     
/*  195 */     Label label = new Label(info_area, 0);
/*      */     
/*  197 */     label.setText(this.lu.getLocalisedMessageText("azbuddy.dchat.info"));
/*      */     
/*  199 */     new org.gudy.azureus2.ui.swt.components.LinkLabel(info_area, "ConfigView.label.please.visit.here", this.lu.getLocalisedMessageText("azbuddy.dchat.link.url"));
/*      */     
/*  201 */     label = new Label(info_area, 0);
/*      */     
/*      */ 
/*      */ 
/*  205 */     label = new Label(info_area, 0);
/*      */     
/*  207 */     label.setText(MessageText.getString("azmsgsync.install.text"));
/*      */     
/*  209 */     this.plugin_install_button = new Button(info_area, 0);
/*      */     
/*  211 */     this.plugin_install_button.setText(MessageText.getString("UpdateWindow.columns.install"));
/*      */     
/*  213 */     this.plugin_install_button.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */       public void widgetSelected(SelectionEvent e)
/*      */       {
/*      */ 
/*  219 */         BuddyPluginViewInstance.this.plugin_install_button.setEnabled(false);
/*      */         
/*  221 */         new AEThread2("installer")
/*      */         {
/*      */           /* Error */
/*      */           public void run()
/*      */           {
/*      */             // Byte code:
/*      */             //   0: iconst_0
/*      */             //   1: istore_1
/*      */             //   2: aload_0
/*      */             //   3: getfield 95	com/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance$2$1:this$1	Lcom/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance$2;
/*      */             //   6: getfield 94	com/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance$2:this$0	Lcom/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance;
/*      */             //   9: invokestatic 97	com/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance:access$300	(Lcom/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance;)V
/*      */             //   12: ldc 4
/*      */             //   14: invokestatic 103	org/gudy/azureus2/core3/internat/MessageText:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */             //   17: astore_2
/*      */             //   18: iconst_1
/*      */             //   19: istore_1
/*      */             //   20: aload_0
/*      */             //   21: getfield 95	com/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance$2$1:this$1	Lcom/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance$2;
/*      */             //   24: getfield 94	com/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance$2:this$0	Lcom/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance;
/*      */             //   27: invokestatic 98	com/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance:access$400	(Lcom/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance;)Z
/*      */             //   30: ifne +121 -> 151
/*      */             //   33: iload_1
/*      */             //   34: ifeq +117 -> 151
/*      */             //   37: iconst_0
/*      */             //   38: istore_1
/*      */             //   39: ldc 3
/*      */             //   41: iconst_1
/*      */             //   42: anewarray 51	java/lang/String
/*      */             //   45: dup
/*      */             //   46: iconst_0
/*      */             //   47: ldc 2
/*      */             //   49: aastore
/*      */             //   50: invokestatic 104	org/gudy/azureus2/core3/internat/MessageText:getString	(Ljava/lang/String;[Ljava/lang/String;)Ljava/lang/String;
/*      */             //   53: astore_2
/*      */             //   54: goto +97 -> 151
/*      */             //   57: astore_3
/*      */             //   58: ldc 3
/*      */             //   60: iconst_1
/*      */             //   61: anewarray 51	java/lang/String
/*      */             //   64: dup
/*      */             //   65: iconst_0
/*      */             //   66: aload_3
/*      */             //   67: invokestatic 106	org/gudy/azureus2/core3/util/Debug:getNestedExceptionMessage	(Ljava/lang/Throwable;)Ljava/lang/String;
/*      */             //   70: aastore
/*      */             //   71: invokestatic 104	org/gudy/azureus2/core3/internat/MessageText:getString	(Ljava/lang/String;[Ljava/lang/String;)Ljava/lang/String;
/*      */             //   74: astore_2
/*      */             //   75: aload_0
/*      */             //   76: getfield 95	com/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance$2$1:this$1	Lcom/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance$2;
/*      */             //   79: getfield 94	com/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance$2:this$0	Lcom/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance;
/*      */             //   82: invokestatic 98	com/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance:access$400	(Lcom/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance;)Z
/*      */             //   85: ifne +66 -> 151
/*      */             //   88: iload_1
/*      */             //   89: ifeq +62 -> 151
/*      */             //   92: iconst_0
/*      */             //   93: istore_1
/*      */             //   94: ldc 3
/*      */             //   96: iconst_1
/*      */             //   97: anewarray 51	java/lang/String
/*      */             //   100: dup
/*      */             //   101: iconst_0
/*      */             //   102: ldc 2
/*      */             //   104: aastore
/*      */             //   105: invokestatic 104	org/gudy/azureus2/core3/internat/MessageText:getString	(Ljava/lang/String;[Ljava/lang/String;)Ljava/lang/String;
/*      */             //   108: astore_2
/*      */             //   109: goto +42 -> 151
/*      */             //   112: astore 4
/*      */             //   114: aload_0
/*      */             //   115: getfield 95	com/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance$2$1:this$1	Lcom/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance$2;
/*      */             //   118: getfield 94	com/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance$2:this$0	Lcom/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance;
/*      */             //   121: invokestatic 98	com/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance:access$400	(Lcom/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance;)Z
/*      */             //   124: ifne +24 -> 148
/*      */             //   127: iload_1
/*      */             //   128: ifeq +20 -> 148
/*      */             //   131: iconst_0
/*      */             //   132: istore_1
/*      */             //   133: ldc 3
/*      */             //   135: iconst_1
/*      */             //   136: anewarray 51	java/lang/String
/*      */             //   139: dup
/*      */             //   140: iconst_0
/*      */             //   141: ldc 2
/*      */             //   143: aastore
/*      */             //   144: invokestatic 104	org/gudy/azureus2/core3/internat/MessageText:getString	(Ljava/lang/String;[Ljava/lang/String;)Ljava/lang/String;
/*      */             //   147: astore_2
/*      */             //   148: aload 4
/*      */             //   150: athrow
/*      */             //   151: aload_0
/*      */             //   152: getfield 95	com/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance$2$1:this$1	Lcom/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance$2;
/*      */             //   155: getfield 94	com/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance$2:this$0	Lcom/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance;
/*      */             //   158: invokestatic 99	com/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance:access$500	(Lcom/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance;)Lcom/aelitis/azureus/plugins/net/buddy/BuddyPlugin;
/*      */             //   161: invokevirtual 96	com/aelitis/azureus/plugins/net/buddy/BuddyPlugin:getPluginInterface	()Lorg/gudy/azureus2/plugins/PluginInterface;
/*      */             //   164: invokeinterface 107 1 0
/*      */             //   169: iload_1
/*      */             //   170: ifeq +8 -> 178
/*      */             //   173: ldc 6
/*      */             //   175: goto +5 -> 180
/*      */             //   178: ldc 5
/*      */             //   180: new 52	java/lang/StringBuilder
/*      */             //   183: dup
/*      */             //   184: invokespecial 100	java/lang/StringBuilder:<init>	()V
/*      */             //   187: ldc 1
/*      */             //   189: invokevirtual 102	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */             //   192: aload_2
/*      */             //   193: invokevirtual 102	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */             //   196: ldc 1
/*      */             //   198: invokevirtual 102	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */             //   201: invokevirtual 101	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */             //   204: lconst_1
/*      */             //   205: invokeinterface 108 5 0
/*      */             //   210: pop2
/*      */             //   211: return
/*      */             // Line number table:
/*      */             //   Java source line #226	-> byte code offset #0
/*      */             //   Java source line #231	-> byte code offset #2
/*      */             //   Java source line #233	-> byte code offset #12
/*      */             //   Java source line #235	-> byte code offset #18
/*      */             //   Java source line #245	-> byte code offset #20
/*      */             //   Java source line #247	-> byte code offset #33
/*      */             //   Java source line #251	-> byte code offset #37
/*      */             //   Java source line #253	-> byte code offset #39
/*      */             //   Java source line #237	-> byte code offset #57
/*      */             //   Java source line #239	-> byte code offset #58
/*      */             //   Java source line #245	-> byte code offset #75
/*      */             //   Java source line #247	-> byte code offset #88
/*      */             //   Java source line #251	-> byte code offset #92
/*      */             //   Java source line #253	-> byte code offset #94
/*      */             //   Java source line #245	-> byte code offset #112
/*      */             //   Java source line #247	-> byte code offset #127
/*      */             //   Java source line #251	-> byte code offset #131
/*      */             //   Java source line #253	-> byte code offset #133
/*      */             //   Java source line #260	-> byte code offset #151
/*      */             //   Java source line #264	-> byte code offset #211
/*      */             // Local variable table:
/*      */             //   start	length	slot	name	signature
/*      */             //   0	212	0	this	1
/*      */             //   1	169	1	ok	boolean
/*      */             //   17	131	2	msg	String
/*      */             //   151	42	2	msg	String
/*      */             //   57	10	3	e	Throwable
/*      */             //   112	37	4	localObject	Object
/*      */             // Exception table:
/*      */             //   from	to	target	type
/*      */             //   2	20	57	java/lang/Throwable
/*      */             //   2	20	112	finally
/*      */             //   57	75	112	finally
/*      */             //   112	114	112	finally
/*      */           }
/*      */         }.start();
/*      */       }
/*  268 */     });
/*  269 */     checkMsgSyncPlugin();
/*      */     
/*      */ 
/*      */ 
/*  273 */     Group ui_area = new Group(main, 0);
/*  274 */     layout = new GridLayout();
/*  275 */     layout.numColumns = 3;
/*  276 */     ui_area.setLayout(layout);
/*  277 */     grid_data = new GridData(768);
/*  278 */     grid_data.horizontalSpan = 3;
/*  279 */     Utils.setLayoutData(ui_area, grid_data);
/*      */     
/*  281 */     ui_area.setText(this.lu.getLocalisedMessageText("ConfigView.section.style"));
/*      */     
/*      */ 
/*      */ 
/*  285 */     label = new Label(ui_area, 0);
/*  286 */     label.setText(this.lu.getLocalisedMessageText("azbuddy.dchat.public.nick"));
/*      */     
/*  288 */     this.public_nickname = new Text(ui_area, 2048);
/*  289 */     grid_data = new GridData();
/*  290 */     grid_data.widthHint = 200;
/*  291 */     Utils.setLayoutData(this.public_nickname, grid_data);
/*      */     
/*  293 */     this.public_nickname.setText(plugin_beta.getSharedPublicNickname());
/*  294 */     this.public_nickname.addListener(16, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  296 */         plugin_beta.setSharedPublicNickname(BuddyPluginViewInstance.this.public_nickname.getText().trim());
/*      */       }
/*      */       
/*  299 */     });
/*  300 */     label = new Label(ui_area, 0);
/*      */     
/*      */ 
/*      */ 
/*  304 */     label = new Label(ui_area, 0);
/*  305 */     label.setText(this.lu.getLocalisedMessageText("azbuddy.dchat.anon.nick"));
/*      */     
/*  307 */     this.anon_nickname = new Text(ui_area, 2048);
/*  308 */     grid_data = new GridData();
/*  309 */     grid_data.widthHint = 200;
/*  310 */     Utils.setLayoutData(this.anon_nickname, grid_data);
/*      */     
/*  312 */     this.anon_nickname.setText(plugin_beta.getSharedAnonNickname());
/*  313 */     this.anon_nickname.addListener(16, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  315 */         plugin_beta.setSharedAnonNickname(BuddyPluginViewInstance.this.anon_nickname.getText().trim());
/*      */       }
/*      */       
/*  318 */     });
/*  319 */     label = new Label(ui_area, 0);
/*      */     
/*      */ 
/*      */ 
/*  323 */     label = new Label(ui_area, 0);
/*  324 */     label.setText(this.lu.getLocalisedMessageText("azbuddy.dchat.ui.max.lines"));
/*      */     
/*  326 */     final IntParameter max_lines = new IntParameter(ui_area, "azbuddy.chat.temp.ui.max.lines", 128, Integer.MAX_VALUE);
/*      */     
/*      */ 
/*  329 */     max_lines.setValue(beta.getMaxUILines());
/*      */     
/*  331 */     max_lines.addChangeListener(new org.gudy.azureus2.ui.swt.config.ParameterChangeAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter p, boolean caused_internally)
/*      */       {
/*      */ 
/*      */ 
/*  340 */         beta.setMaxUILines(max_lines.getValue());
/*      */       }
/*      */       
/*  343 */     });
/*  344 */     label = new Label(ui_area, 0);
/*      */     
/*      */ 
/*      */ 
/*  348 */     label = new Label(ui_area, 0);
/*  349 */     label.setText(this.lu.getLocalisedMessageText("azbuddy.dchat.ui.max.kb"));
/*      */     
/*  351 */     final IntParameter max_chars = new IntParameter(ui_area, "azbuddy.chat.temp.ui.max.chars", 1, 512);
/*      */     
/*      */ 
/*  354 */     max_chars.setValue(beta.getMaxUICharsKB());
/*      */     
/*  356 */     max_chars.addChangeListener(new org.gudy.azureus2.ui.swt.config.ParameterChangeAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter p, boolean caused_internally)
/*      */       {
/*      */ 
/*      */ 
/*  365 */         beta.setMaxUICharsKB(max_chars.getValue());
/*      */       }
/*      */       
/*  368 */     });
/*  369 */     label = new Label(ui_area, 0);
/*      */     
/*      */ 
/*      */ 
/*  373 */     final Button hide_ratings = new Button(ui_area, 32);
/*      */     
/*  375 */     hide_ratings.setText(this.lu.getLocalisedMessageText("azbuddy.dchat.ui.hide.ratings"));
/*      */     
/*  377 */     hide_ratings.setSelection(plugin_beta.getHideRatings());
/*      */     
/*  379 */     hide_ratings.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent ev)
/*      */       {
/*      */ 
/*  386 */         plugin_beta.setHideRatings(hide_ratings.getSelection());
/*      */       }
/*      */       
/*  389 */     });
/*  390 */     label = new Label(ui_area, 0);
/*  391 */     grid_data = new GridData();
/*  392 */     grid_data.horizontalSpan = 2;
/*  393 */     Utils.setLayoutData(label, grid_data);
/*      */     
/*      */ 
/*      */ 
/*  397 */     final Button hide_search_subs = new Button(ui_area, 32);
/*      */     
/*  399 */     hide_search_subs.setText(this.lu.getLocalisedMessageText("azbuddy.dchat.ui.hide.search_subs"));
/*      */     
/*  401 */     hide_search_subs.setSelection(plugin_beta.getHideSearchSubs());
/*      */     
/*  403 */     hide_search_subs.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent ev)
/*      */       {
/*      */ 
/*  410 */         plugin_beta.setHideSearchSubs(hide_search_subs.getSelection());
/*      */       }
/*      */       
/*  413 */     });
/*  414 */     label = new Label(ui_area, 0);
/*  415 */     grid_data = new GridData();
/*  416 */     grid_data.horizontalSpan = 2;
/*  417 */     Utils.setLayoutData(label, grid_data);
/*      */     
/*      */ 
/*      */ 
/*  421 */     final Button stand_alone = new Button(ui_area, 32);
/*      */     
/*  423 */     stand_alone.setText(this.lu.getLocalisedMessageText("azbuddy.dchat.ui.standalone.windows"));
/*      */     
/*  425 */     stand_alone.setSelection(plugin_beta.getStandAloneWindows());
/*      */     
/*  427 */     stand_alone.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent ev)
/*      */       {
/*      */ 
/*  434 */         plugin_beta.setStandAloneWindows(stand_alone.getSelection());
/*      */       }
/*      */       
/*  437 */     });
/*  438 */     label = new Label(ui_area, 0);
/*  439 */     grid_data = new GridData();
/*  440 */     grid_data.horizontalSpan = 2;
/*  441 */     Utils.setLayoutData(label, grid_data);
/*      */     
/*  443 */     if (AZ3Functions.getProvider() != null)
/*      */     {
/*      */ 
/*      */ 
/*  447 */       final Button windows_to_sidebar = new Button(ui_area, 32);
/*      */       
/*  449 */       windows_to_sidebar.setText(this.lu.getLocalisedMessageText("azbuddy.dchat.ui.windows.to.sidebar"));
/*      */       
/*  451 */       windows_to_sidebar.setSelection(plugin_beta.getWindowsToSidebar());
/*      */       
/*  453 */       windows_to_sidebar.addSelectionListener(new SelectionAdapter()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void widgetSelected(SelectionEvent ev)
/*      */         {
/*      */ 
/*  460 */           plugin_beta.setWindowsToSidebar(windows_to_sidebar.getSelection());
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  467 */     final Group noti_area = new Group(main, 0);
/*  468 */     layout = new GridLayout();
/*  469 */     layout.numColumns = 4;
/*  470 */     noti_area.setLayout(layout);
/*  471 */     grid_data = new GridData(768);
/*  472 */     grid_data.horizontalSpan = 3;
/*  473 */     Utils.setLayoutData(noti_area, grid_data);
/*      */     
/*  475 */     noti_area.setText(this.lu.getLocalisedMessageText("v3.MainWindow.tab.events"));
/*      */     
/*  477 */     final Button noti_enable = new Button(noti_area, 32);
/*      */     
/*  479 */     noti_enable.setText(this.lu.getLocalisedMessageText("azbuddy.dchat.noti.sound"));
/*      */     
/*  481 */     boolean sound_enabled = plugin_beta.getSoundEnabled();
/*      */     
/*  483 */     noti_enable.setSelection(sound_enabled);
/*      */     
/*  485 */     noti_enable.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent ev)
/*      */       {
/*      */ 
/*  492 */         plugin_beta.setSoundEnabled(noti_enable.getSelection());
/*      */       }
/*      */       
/*      */ 
/*  496 */     });
/*  497 */     final Text noti_file = new Text(noti_area, 2048);
/*  498 */     grid_data = new GridData();
/*  499 */     grid_data.widthHint = 400;
/*  500 */     Utils.setLayoutData(noti_file, grid_data);
/*      */     
/*  502 */     String sound_file = plugin_beta.getSoundFile();
/*      */     
/*  504 */     if (sound_file.length() == 0)
/*      */     {
/*  506 */       sound_file = "<default>";
/*      */     }
/*      */     
/*  509 */     noti_file.setText(sound_file);
/*      */     
/*  511 */     noti_file.addListener(16, new Listener()
/*      */     {
/*      */ 
/*      */       public void handleEvent(Event event)
/*      */       {
/*  516 */         String val = noti_file.getText().trim();
/*      */         
/*  518 */         if ((val.length() == 0) || (val.startsWith("<")))
/*      */         {
/*  520 */           noti_file.setText("<default>");
/*      */           
/*  522 */           val = "";
/*      */         }
/*      */         
/*  525 */         if (!val.equals(plugin_beta.getSoundFile()))
/*      */         {
/*  527 */           plugin_beta.setSoundFile(val);
/*      */         }
/*      */         
/*      */       }
/*  531 */     });
/*  532 */     final Button noti_browse = new Button(noti_area, 8);
/*      */     
/*  534 */     final ImageLoader imageLoader = ImageLoader.getInstance();
/*      */     
/*  536 */     Image imgOpenFolder = imageLoader.getImage("openFolderButton");
/*      */     
/*  538 */     noti_area.addDisposeListener(new org.eclipse.swt.events.DisposeListener()
/*      */     {
/*      */       public void widgetDisposed(org.eclipse.swt.events.DisposeEvent e)
/*      */       {
/*  542 */         imageLoader.releaseImage("openFolderButton");
/*      */       }
/*      */       
/*  545 */     });
/*  546 */     noti_browse.setImage(imgOpenFolder);
/*      */     
/*  548 */     imgOpenFolder.setBackground(noti_browse.getBackground());
/*      */     
/*  550 */     noti_browse.setToolTipText(MessageText.getString("ConfigView.button.browse"));
/*      */     
/*  552 */     noti_browse.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  554 */         FileDialog dialog = new FileDialog(noti_area.getShell(), 65536);
/*      */         
/*  556 */         dialog.setFilterExtensions(new String[] { "*.wav" });
/*      */         
/*      */ 
/*  559 */         dialog.setFilterNames(new String[] { "*.wav" });
/*      */         
/*      */ 
/*      */ 
/*  563 */         dialog.setText(MessageText.getString("ConfigView.section.interface.wavlocation"));
/*      */         
/*  565 */         String path = dialog.open();
/*      */         
/*  567 */         if (path != null)
/*      */         {
/*  569 */           path = path.trim();
/*      */           
/*  571 */           if (path.startsWith("<"))
/*      */           {
/*  573 */             path = "";
/*      */           }
/*      */           
/*  576 */           plugin_beta.setSoundFile(path.trim());
/*      */         }
/*      */         
/*  579 */         BuddyPluginViewInstance.this.view.playSound();
/*      */       }
/*      */       
/*  582 */     });
/*  583 */     label = new Label(noti_area, 64);
/*      */     
/*  585 */     label.setText(MessageText.getString("ConfigView.section.interface.wavlocation.info"));
/*      */     
/*  587 */     if (!sound_enabled)
/*      */     {
/*  589 */       noti_file.setEnabled(false);
/*  590 */       noti_browse.setEnabled(false);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  595 */     Group private_chat_area = new Group(main, 0);
/*  596 */     layout = new GridLayout();
/*  597 */     layout.numColumns = 3;
/*      */     
/*      */ 
/*  600 */     private_chat_area.setLayout(layout);
/*  601 */     grid_data = new GridData(768);
/*  602 */     grid_data.horizontalSpan = 3;
/*  603 */     Utils.setLayoutData(private_chat_area, grid_data);
/*      */     
/*  605 */     private_chat_area.setText(this.lu.getLocalisedMessageText("label.private.chat"));
/*      */     
/*  607 */     label = new Label(private_chat_area, 0);
/*      */     
/*  609 */     label.setText(this.lu.getLocalisedMessageText("azbuddy.dchat.pc.enable"));
/*      */     
/*  611 */     final Button private_chat_enable = new Button(private_chat_area, 32);
/*      */     
/*  613 */     label = new Label(private_chat_area, 0);
/*  614 */     grid_data = new GridData(768);
/*  615 */     Utils.setLayoutData(label, grid_data);
/*      */     
/*  617 */     private_chat_enable.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent ev)
/*      */       {
/*      */ 
/*  624 */         plugin_beta.setPrivateChatState(private_chat_enable.getSelection() ? 3 : 1);
/*      */       }
/*      */       
/*  627 */     });
/*  628 */     final Label pc_pinned_only = new Label(private_chat_area, 0);
/*      */     
/*  630 */     pc_pinned_only.setText(this.lu.getLocalisedMessageText("azbuddy.dchat.pc.pinned.only"));
/*      */     
/*  632 */     final Button private_chat_pinned = new Button(private_chat_area, 32);
/*      */     
/*  634 */     label = new Label(private_chat_area, 0);
/*  635 */     grid_data = new GridData(768);
/*  636 */     Utils.setLayoutData(label, grid_data);
/*      */     
/*  638 */     private_chat_pinned.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent ev)
/*      */       {
/*      */ 
/*  645 */         plugin_beta.setPrivateChatState(private_chat_pinned.getSelection() ? 2 : 3);
/*      */       }
/*      */       
/*  648 */     });
/*  649 */     int pc_state = plugin_beta.getPrivateChatState();
/*      */     
/*  651 */     private_chat_enable.setSelection(pc_state != 1);
/*  652 */     private_chat_pinned.setSelection(pc_state == 2);
/*      */     
/*  654 */     private_chat_pinned.setEnabled(pc_state != 1);
/*  655 */     pc_pinned_only.setEnabled(pc_state != 1);
/*      */     
/*      */ 
/*      */ 
/*  659 */     Group import_area = new Group(main, 0);
/*  660 */     layout = new GridLayout();
/*  661 */     layout.numColumns = 3;
/*  662 */     import_area.setLayout(layout);
/*  663 */     grid_data = new GridData(768);
/*  664 */     grid_data.horizontalSpan = 3;
/*  665 */     Utils.setLayoutData(import_area, grid_data);
/*      */     
/*  667 */     import_area.setText(this.lu.getLocalisedMessageText("azbuddy.dchat.cannel.import"));
/*      */     
/*  669 */     label = new Label(import_area, 0);
/*      */     
/*  671 */     label.setText(this.lu.getLocalisedMessageText("azbuddy.dchat.import.data"));
/*      */     
/*  673 */     final Text import_data = new Text(import_area, 2048);
/*  674 */     grid_data = new GridData();
/*  675 */     grid_data.widthHint = 400;
/*  676 */     Utils.setLayoutData(import_data, grid_data);
/*      */     
/*  678 */     final Button import_button = new Button(import_area, 0);
/*      */     
/*  680 */     import_button.setText(this.lu.getLocalisedMessageText("br.restore"));
/*      */     
/*  682 */     import_button.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent ev)
/*      */       {
/*      */ 
/*  689 */         import_button.setEnabled(false);
/*      */         
/*  691 */         final Display display = BuddyPluginViewInstance.this.composite.getDisplay();
/*      */         
/*  693 */         final String data = import_data.getText().trim();
/*      */         
/*  695 */         new AEThread2("async")
/*      */         {
/*      */ 
/*      */           public void run()
/*      */           {
/*  700 */             if (display.isDisposed())
/*      */             {
/*  702 */               return;
/*      */             }
/*      */             try
/*      */             {
/*  706 */               final BuddyPluginBeta.ChatInstance inst = BuddyPluginViewInstance.17.this.val$plugin_beta.importChat(data);
/*      */               
/*  708 */               display.asyncExec(new Runnable()
/*      */               {
/*      */ 
/*      */                 public void run()
/*      */                 {
/*      */ 
/*  714 */                   if (!BuddyPluginViewInstance.17.1.this.val$display.isDisposed())
/*      */                   {
/*  716 */                     BuddyPluginViewBetaChat.createChatWindow(BuddyPluginViewInstance.this.view, BuddyPluginViewInstance.this.plugin, inst);
/*      */                     
/*  718 */                     BuddyPluginViewInstance.17.this.val$import_button.setEnabled(true);
/*      */                   }
/*      */                 }
/*      */               });
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  725 */               display.asyncExec(new Runnable()
/*      */               {
/*      */ 
/*      */                 public void run()
/*      */                 {
/*      */ 
/*  731 */                   if (!BuddyPluginViewInstance.17.this.val$import_button.isDisposed())
/*      */                   {
/*  733 */                     BuddyPluginViewInstance.17.this.val$import_button.setEnabled(true);
/*      */                   }
/*      */                   
/*      */                 }
/*  737 */               });
/*  738 */               Debug.out(e);
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */ 
/*      */         }.start();
/*      */       }
/*  746 */     });
/*  747 */     Group adv_area = new Group(main, 0);
/*  748 */     adv_area.setText(this.lu.getLocalisedMessageText("MyTorrentsView.menu.advancedmenu"));
/*  749 */     layout = new GridLayout();
/*  750 */     layout.numColumns = 3;
/*  751 */     adv_area.setLayout(layout);
/*  752 */     grid_data = new GridData(768);
/*  753 */     grid_data.horizontalSpan = 3;
/*  754 */     Utils.setLayoutData(adv_area, grid_data);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  759 */     label = new Label(adv_area, 0);
/*      */     
/*  761 */     label.setText(this.lu.getLocalisedMessageText("azbuddy.dchat.anon.share.endpoint"));
/*      */     
/*  763 */     final Button shared_endpoint = new Button(adv_area, 32);
/*      */     
/*  765 */     shared_endpoint.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent ev)
/*      */       {
/*      */ 
/*  772 */         plugin_beta.setSharedAnonEndpoint(shared_endpoint.getSelection());
/*      */       }
/*      */       
/*  775 */     });
/*  776 */     shared_endpoint.setSelection(plugin_beta.getSharedAnonEndpoint());
/*      */     
/*  778 */     label = new Label(adv_area, 0);
/*  779 */     label.setText(this.lu.getLocalisedMessageText("azbuddy.dchat.anon.share.endpoint.info"));
/*      */     
/*  781 */     grid_data = new GridData(768);
/*  782 */     Utils.setLayoutData(label, grid_data);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  788 */     Group test_area = new Group(main, 0);
/*  789 */     test_area.setText(this.lu.getLocalisedMessageText("br.test"));
/*  790 */     layout = new GridLayout();
/*  791 */     layout.numColumns = 4;
/*  792 */     test_area.setLayout(layout);
/*  793 */     grid_data = new GridData(768);
/*  794 */     grid_data.horizontalSpan = 3;
/*  795 */     Utils.setLayoutData(test_area, grid_data);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  800 */     label = new Label(test_area, 0);
/*  801 */     label.setText(this.lu.getLocalisedMessageText("azbuddy.dchat.public.beta"));
/*      */     
/*  803 */     Button beta_button = new Button(test_area, 0);
/*      */     
/*  805 */     setupButton(beta_button, this.lu.getLocalisedMessageText("Button.open"), "Public", "test:beta:chat");
/*      */     
/*  807 */     label = new Label(test_area, 0);
/*  808 */     grid_data = new GridData(768);
/*  809 */     grid_data.horizontalSpan = 2;
/*  810 */     Utils.setLayoutData(label, grid_data);
/*      */     
/*      */ 
/*      */ 
/*  814 */     label = new Label(test_area, 0);
/*      */     
/*  816 */     label.setText(this.lu.getLocalisedMessageText("azbuddy.dchat.anon.beta"));
/*      */     
/*  818 */     Button beta_i2p_button = new Button(test_area, 0);
/*      */     
/*  820 */     setupButton(beta_i2p_button, this.lu.getLocalisedMessageText("Button.open"), "I2P", "test:beta:chat");
/*      */     
/*  822 */     beta_i2p_button.setEnabled(i2p_enabled);
/*      */     
/*  824 */     label = new Label(test_area, 0);
/*  825 */     grid_data = new GridData(768);
/*  826 */     grid_data.horizontalSpan = 2;
/*  827 */     Utils.setLayoutData(label, grid_data);
/*      */     
/*      */ 
/*      */ 
/*  831 */     label = new Label(test_area, 0);
/*  832 */     label.setText(this.lu.getLocalisedMessageText("azbuddy.dchat.create.join.key"));
/*      */     
/*  834 */     final Text channel_key = new Text(test_area, 2048);
/*  835 */     grid_data = new GridData();
/*  836 */     grid_data.widthHint = 200;
/*  837 */     Utils.setLayoutData(channel_key, grid_data);
/*      */     
/*  839 */     final Button create_i2p_button = new Button(test_area, 32);
/*      */     
/*  841 */     create_i2p_button.setText(this.lu.getLocalisedMessageText("label.anon.i2p"));
/*      */     
/*  843 */     create_i2p_button.setEnabled(i2p_enabled);
/*      */     
/*  845 */     final Button create_button = new Button(test_area, 0);
/*      */     
/*  847 */     create_button.setText(this.lu.getLocalisedMessageText("Button.open"));
/*      */     
/*  849 */     create_button.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent ev)
/*      */       {
/*      */ 
/*  856 */         create_button.setEnabled(false);
/*      */         
/*  858 */         final Display display = BuddyPluginViewInstance.this.composite.getDisplay();
/*      */         
/*  860 */         final String network = create_i2p_button.getSelection() ? "I2P" : "Public";
/*  861 */         final String key = channel_key.getText().trim();
/*      */         
/*  863 */         new AEThread2("async")
/*      */         {
/*      */ 
/*      */           public void run()
/*      */           {
/*  868 */             if (display.isDisposed())
/*      */             {
/*  870 */               return;
/*      */             }
/*      */             try
/*      */             {
/*  874 */               final BuddyPluginBeta.ChatInstance inst = BuddyPluginViewInstance.19.this.val$plugin_beta.getChat(network, key);
/*      */               
/*  876 */               display.asyncExec(new Runnable()
/*      */               {
/*      */ 
/*      */                 public void run()
/*      */                 {
/*      */ 
/*  882 */                   if (!BuddyPluginViewInstance.19.1.this.val$display.isDisposed())
/*      */                   {
/*  884 */                     BuddyPluginViewBetaChat.createChatWindow(BuddyPluginViewInstance.this.view, BuddyPluginViewInstance.this.plugin, inst);
/*      */                     
/*  886 */                     BuddyPluginViewInstance.19.this.val$create_button.setEnabled(true);
/*      */                   }
/*      */                 }
/*      */               });
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  893 */               display.asyncExec(new Runnable()
/*      */               {
/*      */ 
/*      */                 public void run()
/*      */                 {
/*      */ 
/*  899 */                   if (!BuddyPluginViewInstance.19.this.val$create_button.isDisposed())
/*      */                   {
/*  901 */                     BuddyPluginViewInstance.19.this.val$create_button.setEnabled(true);
/*      */                   }
/*      */                   
/*      */                 }
/*  905 */               });
/*  906 */               Debug.out(e);
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */ 
/*      */         }.start();
/*      */       }
/*      */       
/*  915 */     });
/*  916 */     List<Button> buttons = new java.util.ArrayList();
/*      */     
/*  918 */     buttons.add(create_button);
/*  919 */     buttons.add(beta_button);
/*  920 */     buttons.add(beta_i2p_button);
/*  921 */     buttons.add(import_button);
/*      */     
/*  923 */     Utils.makeButtonsEqualWidth(buttons);
/*      */     
/*  925 */     this.plugin.addListener(new com.aelitis.azureus.plugins.net.buddy.BuddyPluginAdapter()
/*      */     {
/*      */ 
/*      */       public void updated()
/*      */       {
/*  930 */         if (BuddyPluginViewInstance.this.public_nickname.isDisposed())
/*      */         {
/*  932 */           BuddyPluginViewInstance.this.plugin.removeListener(this);
/*      */         }
/*      */         else
/*      */         {
/*  936 */           BuddyPluginViewInstance.this.public_nickname.getDisplay().asyncExec(new Runnable()
/*      */           {
/*      */ 
/*      */             public void run()
/*      */             {
/*      */ 
/*  942 */               if (BuddyPluginViewInstance.this.public_nickname.isDisposed())
/*      */               {
/*  944 */                 return;
/*      */               }
/*      */               
/*  947 */               String nick = BuddyPluginViewInstance.20.this.val$plugin_beta.getSharedPublicNickname();
/*      */               
/*  949 */               if (!BuddyPluginViewInstance.this.public_nickname.getText().equals(nick))
/*      */               {
/*  951 */                 BuddyPluginViewInstance.this.public_nickname.setText(nick);
/*      */               }
/*      */               
/*  954 */               nick = BuddyPluginViewInstance.20.this.val$plugin_beta.getSharedAnonNickname();
/*      */               
/*  956 */               if (!BuddyPluginViewInstance.this.anon_nickname.getText().equals(nick))
/*      */               {
/*  958 */                 BuddyPluginViewInstance.this.anon_nickname.setText(nick);
/*      */               }
/*      */               
/*  961 */               BuddyPluginViewInstance.20.this.val$shared_endpoint.setSelection(BuddyPluginViewInstance.20.this.val$plugin_beta.getSharedAnonEndpoint());
/*      */               
/*  963 */               int pc_state = BuddyPluginViewInstance.20.this.val$plugin_beta.getPrivateChatState();
/*      */               
/*  965 */               BuddyPluginViewInstance.20.this.val$private_chat_enable.setSelection(pc_state != 1);
/*  966 */               BuddyPluginViewInstance.20.this.val$private_chat_pinned.setSelection(pc_state == 2);
/*  967 */               BuddyPluginViewInstance.20.this.val$private_chat_pinned.setEnabled(pc_state != 1);
/*  968 */               BuddyPluginViewInstance.20.this.val$pc_pinned_only.setEnabled(pc_state != 1);
/*      */               
/*  970 */               String str = BuddyPluginViewInstance.20.this.val$plugin_beta.getSoundFile();
/*      */               
/*  972 */               if (str.length() == 0)
/*      */               {
/*  974 */                 BuddyPluginViewInstance.20.this.val$noti_file.setText("<default>");
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*  979 */                 BuddyPluginViewInstance.20.this.val$noti_file.setText(str);
/*      */               }
/*      */               
/*  982 */               boolean se = BuddyPluginViewInstance.20.this.val$plugin_beta.getSoundEnabled();
/*      */               
/*  984 */               BuddyPluginViewInstance.20.this.val$noti_file.setEnabled(se);
/*  985 */               BuddyPluginViewInstance.20.this.val$noti_browse.setEnabled(se);
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   private boolean isMsgSyncPluginInstalled()
/*      */   {
/*  996 */     PluginInterface pi = this.plugin.getPluginInterface().getPluginManager().getPluginInterfaceByID("azmsgsync");
/*      */     
/*  998 */     return pi != null;
/*      */   }
/*      */   
/*      */ 
/*      */   private boolean checkMsgSyncPlugin()
/*      */   {
/* 1004 */     if (this.plugin_install_button == null)
/*      */     {
/* 1006 */       return false;
/*      */     }
/*      */     
/* 1009 */     final boolean installed = isMsgSyncPluginInstalled();
/*      */     
/* 1011 */     Utils.execSWTThread(new Runnable()
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/*      */ 
/* 1017 */         BuddyPluginViewInstance.this.plugin_install_button.setEnabled(!installed);
/*      */       }
/*      */       
/* 1020 */     });
/* 1021 */     return installed;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void installMsgSyncPlugin()
/*      */     throws Throwable
/*      */   {
/* 1029 */     UIFunctions uif = com.aelitis.azureus.ui.UIFunctionsManager.getUIFunctions();
/*      */     
/* 1031 */     if (uif == null)
/*      */     {
/* 1033 */       throw new Exception("UIFunctions unavailable - can't install plugin");
/*      */     }
/*      */     
/*      */ 
/* 1037 */     final AESemaphore sem = new AESemaphore("installer_wait");
/*      */     
/* 1039 */     final Throwable[] error = { null };
/*      */     
/* 1041 */     uif.installPlugin("azmsgsync", "azmsgsync.install", new com.aelitis.azureus.ui.UIFunctions.actionListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void actionComplete(Object result)
/*      */       {
/*      */ 
/*      */         try
/*      */         {
/*      */ 
/* 1051 */           if (!(result instanceof Boolean))
/*      */           {
/*      */ 
/*      */ 
/* 1055 */             error[0] = ((Throwable)result);
/*      */           }
/*      */         }
/*      */         finally {
/* 1059 */           sem.release();
/*      */         }
/*      */         
/*      */       }
/* 1063 */     });
/* 1064 */     sem.reserve();
/*      */     
/* 1066 */     if ((error[0] instanceof Throwable))
/*      */     {
/* 1068 */       throw error[0];
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setupButton(final Button button, String title, final String network, final String key)
/*      */   {
/* 1079 */     button.setText(title);
/*      */     
/* 1081 */     button.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent ev)
/*      */       {
/*      */ 
/* 1088 */         button.setEnabled(false);
/*      */         
/* 1090 */         final Display display = BuddyPluginViewInstance.this.composite.getDisplay();
/*      */         
/* 1092 */         new AEThread2("async")
/*      */         {
/*      */           public void run()
/*      */           {
/*      */             try
/*      */             {
/* 1098 */               final BuddyPluginBeta.ChatInstance inst = BuddyPluginViewInstance.this.plugin.getBeta().getChat(BuddyPluginViewInstance.23.this.val$network, BuddyPluginViewInstance.23.this.val$key);
/*      */               
/* 1100 */               display.asyncExec(new Runnable()
/*      */               {
/*      */ 
/*      */                 public void run()
/*      */                 {
/*      */ 
/* 1106 */                   if (!BuddyPluginViewInstance.23.1.this.val$display.isDisposed())
/*      */                   {
/* 1108 */                     BuddyPluginViewBetaChat.createChatWindow(BuddyPluginViewInstance.this.view, BuddyPluginViewInstance.this.plugin, inst);
/*      */                     
/* 1110 */                     BuddyPluginViewInstance.23.this.val$button.setEnabled(true);
/*      */                   }
/*      */                 }
/*      */               });
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 1117 */               display.asyncExec(new Runnable()
/*      */               {
/*      */ 
/*      */                 public void run()
/*      */                 {
/*      */ 
/* 1123 */                   if (!BuddyPluginViewInstance.23.this.val$button.isDisposed())
/*      */                   {
/* 1125 */                     BuddyPluginViewInstance.23.this.val$button.setEnabled(true);
/*      */                   }
/*      */                   
/*      */                 }
/* 1129 */               });
/* 1130 */               Debug.out(e);
/*      */             }
/*      */           }
/*      */         }.start();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   private void createClassic(Composite main)
/*      */   {
/* 1141 */     GridLayout layout = new GridLayout();
/* 1142 */     layout.numColumns = 1;
/*      */     
/* 1144 */     main.setLayout(layout);
/* 1145 */     GridData grid_data = new GridData(1808);
/* 1146 */     Utils.setLayoutData(main, grid_data);
/*      */     
/*      */ 
/*      */ 
/* 1150 */     Composite info_area = new Composite(main, 0);
/* 1151 */     layout = new GridLayout();
/* 1152 */     layout.numColumns = 3;
/* 1153 */     layout.marginHeight = 0;
/* 1154 */     layout.marginWidth = 0;
/* 1155 */     info_area.setLayout(layout);
/* 1156 */     grid_data = new GridData(768);
/* 1157 */     grid_data.horizontalSpan = 3;
/* 1158 */     Utils.setLayoutData(info_area, grid_data);
/*      */     
/* 1160 */     Label label = new Label(info_area, 0);
/*      */     
/* 1162 */     label.setText(this.lu.getLocalisedMessageText("azbuddy.classic.info"));
/*      */     
/* 1164 */     new org.gudy.azureus2.ui.swt.components.LinkLabel(info_area, "ConfigView.label.please.visit.here", this.lu.getLocalisedMessageText("azbuddy.classic.link.url"));
/*      */     
/* 1166 */     label = new Label(info_area, 0);
/*      */     
/* 1168 */     if (!this.plugin.isClassicEnabled())
/*      */     {
/* 1170 */       Label control_label = new Label(main, 0);
/* 1171 */       control_label.setText(this.lu.getLocalisedMessageText("azbuddy.disabled"));
/*      */       
/* 1173 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1178 */     final Composite controls = new Composite(main, 0);
/* 1179 */     layout = new GridLayout();
/* 1180 */     layout.numColumns = 6;
/* 1181 */     layout.marginHeight = 0;
/* 1182 */     layout.marginWidth = 0;
/* 1183 */     controls.setLayout(layout);
/* 1184 */     grid_data = new GridData(768);
/* 1185 */     Utils.setLayoutData(controls, grid_data);
/*      */     
/* 1187 */     Label control_label = new Label(controls, 0);
/* 1188 */     control_label.setText(this.lu.getLocalisedMessageText("azbuddy.ui.new_buddy") + " ");
/*      */     
/* 1190 */     final Text control_text = new Text(controls, 2048);
/* 1191 */     GridData gridData = new GridData(768);
/* 1192 */     Utils.setLayoutData(control_text, gridData);
/*      */     
/* 1194 */     final Button control_button = new Button(controls, 0);
/* 1195 */     control_button.setText(this.lu.getLocalisedMessageText("azbuddy.ui.add"));
/*      */     
/* 1197 */     control_button.setEnabled(false);
/*      */     
/* 1199 */     control_text.addModifyListener(new org.eclipse.swt.events.ModifyListener()
/*      */     {
/*      */ 
/*      */       public void modifyText(org.eclipse.swt.events.ModifyEvent e)
/*      */       {
/*      */ 
/* 1205 */         control_button.setEnabled(BuddyPluginViewInstance.this.plugin.verifyPublicKey(control_text.getText().trim()));
/*      */       }
/*      */       
/* 1208 */     });
/* 1209 */     control_button.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent e)
/*      */       {
/*      */ 
/* 1216 */         BuddyPluginViewInstance.this.plugin.addBuddy(control_text.getText().trim(), 1);
/*      */         
/* 1218 */         control_text.setText("");
/*      */       }
/*      */       
/* 1221 */     });
/* 1222 */     Label control_lab_pk = new Label(controls, 0);
/* 1223 */     control_lab_pk.setText(this.lu.getLocalisedMessageText("azbuddy.ui.mykey") + " ");
/*      */     
/* 1225 */     final Text control_val_pk = new Text(controls, 0);
/* 1226 */     gridData = new GridData();
/* 1227 */     gridData.widthHint = 400;
/* 1228 */     Utils.setLayoutData(control_val_pk, gridData);
/*      */     
/* 1230 */     control_val_pk.setEditable(false);
/* 1231 */     control_val_pk.setBackground(control_lab_pk.getBackground());
/*      */     
/* 1233 */     control_val_pk.addKeyListener(new org.eclipse.swt.events.KeyListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void keyPressed(KeyEvent event)
/*      */       {
/*      */ 
/* 1240 */         int key = event.character;
/*      */         
/* 1242 */         if ((key <= 26) && (key > 0))
/*      */         {
/* 1244 */           key += 96;
/*      */         }
/*      */         
/* 1247 */         if ((event.stateMask == org.eclipse.swt.SWT.MOD1) && (key == 97))
/*      */         {
/* 1249 */           control_val_pk.setSelection(0, control_val_pk.getText().length());
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void keyReleased(KeyEvent event) {}
/* 1262 */     });
/* 1263 */     final CryptoManager crypt_man = com.aelitis.azureus.core.security.CryptoManagerFactory.getSingleton();
/*      */     
/* 1265 */     byte[] public_key = crypt_man.getECCHandler().peekPublicKey();
/*      */     
/* 1267 */     if (public_key == null)
/*      */     {
/* 1269 */       Messages.setLanguageText(control_val_pk, "ConfigView.section.security.publickey.undef");
/*      */     }
/*      */     else
/*      */     {
/* 1273 */       control_val_pk.setText(Base32.encode(public_key));
/*      */     }
/*      */     
/* 1276 */     Messages.setLanguageText(control_val_pk, "ConfigView.copy.to.clipboard.tooltip", true);
/*      */     
/* 1278 */     control_val_pk.setCursor(main.getDisplay().getSystemCursor(21));
/* 1279 */     control_val_pk.setForeground(Colors.blue);
/* 1280 */     control_val_pk.addMouseListener(new org.eclipse.swt.events.MouseAdapter() {
/*      */       public void mouseDoubleClick(MouseEvent arg0) {
/* 1282 */         copyToClipboard();
/*      */       }
/*      */       
/* 1285 */       public void mouseDown(MouseEvent arg0) { copyToClipboard(); }
/*      */       
/*      */ 
/*      */       protected void copyToClipboard()
/*      */       {
/* 1290 */         new Clipboard(control_val_pk.getDisplay()).setContents(new Object[] { control_val_pk.getText() }, new org.eclipse.swt.dnd.Transfer[] { TextTransfer.getInstance() });
/*      */       }
/*      */       
/* 1293 */     });
/* 1294 */     crypt_man.addKeyListener(new com.aelitis.azureus.core.security.CryptoManagerKeyListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void keyChanged(final CryptoHandler handler)
/*      */       {
/*      */ 
/* 1301 */         if (control_val_pk.isDisposed())
/*      */         {
/* 1303 */           crypt_man.removeKeyListener(this);
/*      */         }
/* 1305 */         else if (handler.getType() == 1)
/*      */         {
/* 1307 */           control_val_pk.getDisplay().asyncExec(new Runnable()
/*      */           {
/*      */ 
/*      */             public void run()
/*      */             {
/*      */ 
/* 1313 */               byte[] public_key = handler.peekPublicKey();
/*      */               
/* 1315 */               if (public_key == null)
/*      */               {
/* 1317 */                 Messages.setLanguageText(BuddyPluginViewInstance.28.this.val$control_val_pk, "ConfigView.section.security.publickey.undef");
/*      */               }
/*      */               else
/*      */               {
/* 1321 */                 BuddyPluginViewInstance.28.this.val$control_val_pk.setText(Base32.encode(public_key));
/*      */               }
/*      */               
/* 1324 */               BuddyPluginViewInstance.28.this.val$controls.layout();
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void keyLockStatusChanged(CryptoHandler handler) {}
/* 1336 */     });
/* 1337 */     Button config_button = new Button(controls, 0);
/* 1338 */     config_button.setText(this.lu.getLocalisedMessageText("plugins.basicview.config"));
/*      */     
/* 1340 */     config_button.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent e)
/*      */       {
/*      */ 
/* 1347 */         BuddyPluginViewInstance.this.plugin.showConfig();
/*      */ 
/*      */       }
/*      */       
/*      */ 
/* 1352 */     });
/* 1353 */     final Composite form = new Composite(main, 0);
/* 1354 */     org.eclipse.swt.layout.FormLayout flayout = new org.eclipse.swt.layout.FormLayout();
/* 1355 */     flayout.marginHeight = 0;
/* 1356 */     flayout.marginWidth = 0;
/* 1357 */     form.setLayout(flayout);
/* 1358 */     gridData = new GridData(1808);
/* 1359 */     Utils.setLayoutData(form, gridData);
/*      */     
/*      */ 
/* 1362 */     final Composite child1 = new Composite(form, 0);
/* 1363 */     layout = new GridLayout();
/* 1364 */     layout.numColumns = 1;
/* 1365 */     layout.horizontalSpacing = 0;
/* 1366 */     layout.verticalSpacing = 0;
/* 1367 */     layout.marginHeight = 0;
/* 1368 */     layout.marginWidth = 0;
/* 1369 */     child1.setLayout(layout);
/*      */     
/* 1371 */     final Sash sash = new Sash(form, 256);
/*      */     
/* 1373 */     Composite child2 = new Composite(form, 0);
/* 1374 */     layout = new GridLayout();
/* 1375 */     layout.numColumns = 1;
/* 1376 */     layout.horizontalSpacing = 0;
/* 1377 */     layout.verticalSpacing = 0;
/* 1378 */     layout.marginHeight = 0;
/* 1379 */     layout.marginWidth = 0;
/* 1380 */     child2.setLayout(layout);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1386 */     FormData formData = new FormData();
/* 1387 */     formData.left = new FormAttachment(0, 0);
/* 1388 */     formData.right = new FormAttachment(100, 0);
/* 1389 */     formData.top = new FormAttachment(0, 0);
/* 1390 */     Utils.setLayoutData(child1, formData);
/*      */     
/* 1392 */     final FormData child1Data = formData;
/*      */     
/* 1394 */     int SASH_WIDTH = 4;
/*      */     
/*      */ 
/*      */ 
/* 1398 */     formData = new FormData();
/* 1399 */     formData.left = new FormAttachment(0, 0);
/* 1400 */     formData.right = new FormAttachment(100, 0);
/* 1401 */     formData.top = new FormAttachment(child1);
/* 1402 */     formData.height = 4;
/* 1403 */     Utils.setLayoutData(sash, formData);
/*      */     
/*      */ 
/*      */ 
/* 1407 */     formData = new FormData();
/* 1408 */     formData.left = new FormAttachment(0, 0);
/* 1409 */     formData.right = new FormAttachment(100, 0);
/* 1410 */     formData.bottom = new FormAttachment(100, 0);
/* 1411 */     formData.top = new FormAttachment(sash);
/* 1412 */     Utils.setLayoutData(child2, formData);
/*      */     
/* 1414 */     final PluginConfig pc = this.plugin.getPluginInterface().getPluginconfig();
/*      */     
/* 1416 */     sash.setData("PCT", new Float(pc.getPluginFloatParameter("swt.sash.position", 0.7F)));
/*      */     
/* 1418 */     sash.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent e)
/*      */       {
/*      */ 
/* 1425 */         if (e.detail == 1) {
/* 1426 */           return;
/*      */         }
/*      */         
/* 1429 */         child1Data.height = (e.y + e.height - 4);
/*      */         
/* 1431 */         form.layout();
/*      */         
/* 1433 */         Float l = new Float(child1.getBounds().height / form.getBounds().height);
/*      */         
/* 1435 */         sash.setData("PCT", l);
/*      */         
/* 1437 */         pc.setPluginParameter("swt.sash.position", l.floatValue());
/*      */       }
/*      */       
/* 1440 */     });
/* 1441 */     form.addListener(11, new Listener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void handleEvent(Event e)
/*      */       {
/*      */ 
/* 1448 */         Float l = (Float)sash.getData("PCT");
/*      */         
/* 1450 */         if (l != null)
/*      */         {
/* 1452 */           child1Data.height = ((int)(form.getBounds().height * l.doubleValue()));
/*      */           
/* 1454 */           form.layout();
/*      */         }
/*      */         
/*      */       }
/*      */       
/*      */ 
/* 1460 */     });
/* 1461 */     this.buddy_table = new Table(child1, 268503042);
/*      */     
/* 1463 */     final String[] headers = { "azbuddy.ui.table.name", "azbuddy.ui.table.online", "azbuddy.ui.table.lastseen", "azbuddy.ui.table.last_ygm", "azbuddy.ui.table.last_msg", "azbuddy.ui.table.loc_cat", "azbuddy.ui.table.rem_cat", "azbuddy.ui.table.read_cat", "azbuddy.ui.table.con", "azbuddy.ui.table.track", "azbuddy.ui.table.msg_in", "azbuddy.ui.table.msg_out", "azbuddy.ui.table.msg_queued", "MyTrackerView.bytesin", "MyTrackerView.bytesout", "azbuddy.ui.table.ss" };
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
/* 1481 */     int[] sizes = { 250, 100, 100, 100, 200, 100, 100, 100, 75, 75, 75, 75, 75, 75, 75, 40 };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1487 */     int[] aligns = { 16384, 16777216, 16777216, 16777216, 16777216, 16384, 16384, 16384, 16384, 16777216, 16777216, 16777216, 16777216, 16777216, 16777216, 16777216 };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1493 */     for (int i = 0; i < headers.length; i++)
/*      */     {
/* 1495 */       TableColumn tc = new TableColumn(this.buddy_table, aligns[i]);
/*      */       
/* 1497 */       tc.setWidth(Utils.adjustPXForDPI(sizes[i]));
/*      */       
/* 1499 */       Messages.setLanguageText(tc, headers[i]);
/*      */     }
/*      */     
/* 1502 */     this.buddy_table.setHeaderVisible(true);
/*      */     
/* 1504 */     TableColumn[] columns = this.buddy_table.getColumns();
/* 1505 */     columns[0].setData(new Integer(0));
/* 1506 */     columns[1].setData(new Integer(1));
/* 1507 */     columns[2].setData(new Integer(2));
/* 1508 */     columns[3].setData(new Integer(3));
/* 1509 */     columns[4].setData(new Integer(4));
/* 1510 */     columns[5].setData(new Integer(5));
/* 1511 */     columns[6].setData(new Integer(6));
/* 1512 */     columns[7].setData(new Integer(7));
/* 1513 */     columns[8].setData(new Integer(8));
/* 1514 */     columns[9].setData(new Integer(9));
/* 1515 */     columns[10].setData(new Integer(10));
/* 1516 */     columns[11].setData(new Integer(11));
/* 1517 */     columns[12].setData(new Integer(12));
/* 1518 */     columns[13].setData(new Integer(13));
/* 1519 */     columns[14].setData(new Integer(14));
/* 1520 */     columns[15].setData(new Integer(15));
/*      */     
/*      */ 
/* 1523 */     final FilterComparator comparator = new FilterComparator();
/*      */     
/* 1525 */     Listener sort_listener = new Listener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void handleEvent(Event e)
/*      */       {
/*      */ 
/* 1532 */         TableColumn tc = (TableColumn)e.widget;
/*      */         
/* 1534 */         int field = ((Integer)tc.getData()).intValue();
/*      */         
/* 1536 */         comparator.setField(field);
/*      */         
/* 1538 */         java.util.Collections.sort(BuddyPluginViewInstance.this.buddies, comparator);
/*      */         
/* 1540 */         BuddyPluginViewInstance.this.updateTable();
/*      */       }
/*      */     };
/*      */     
/* 1544 */     for (int i = 0; i < columns.length; i++)
/*      */     {
/* 1546 */       columns[i].addListener(13, sort_listener);
/*      */     }
/*      */     
/* 1549 */     gridData = new GridData(1808);
/* 1550 */     gridData.heightHint = (this.buddy_table.getHeaderHeight() * 3);
/* 1551 */     Utils.setLayoutData(this.buddy_table, gridData);
/*      */     
/*      */ 
/* 1554 */     this.buddy_table.addListener(36, new Listener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void handleEvent(Event event)
/*      */       {
/*      */ 
/*      */ 
/* 1562 */         TableItem item = (TableItem)event.item;
/*      */         
/* 1564 */         int index = BuddyPluginViewInstance.this.buddy_table.indexOf(item);
/*      */         
/* 1566 */         if ((index < 0) || (index >= BuddyPluginViewInstance.this.buddies.size()))
/*      */         {
/* 1568 */           return;
/*      */         }
/*      */         
/* 1571 */         BuddyPluginBuddy buddy = (BuddyPluginBuddy)BuddyPluginViewInstance.this.buddies.get(index);
/*      */         
/* 1573 */         item.setText(0, buddy.getName());
/*      */         
/*      */         int os;
/*      */         int os;
/* 1577 */         if (buddy.isOnline(false))
/*      */         {
/* 1579 */           os = buddy.getOnlineStatus();
/*      */         }
/*      */         else
/*      */         {
/* 1583 */           os = 4;
/*      */         }
/*      */         
/* 1586 */         if (os == 4)
/*      */         {
/* 1588 */           item.setText(1, "");
/*      */         }
/*      */         else
/*      */         {
/* 1592 */           item.setText(1, BuddyPluginViewInstance.this.plugin.getOnlineStatus(os));
/*      */         }
/*      */         
/* 1595 */         long lo = buddy.getLastTimeOnline();
/*      */         
/* 1597 */         item.setText(2, lo == 0L ? "" : new SimpleDateFormat().format(new java.util.Date(lo)));
/*      */         
/* 1599 */         long last_ygm = buddy.getLastMessagePending();
/*      */         
/* 1601 */         item.setText(3, last_ygm == 0L ? "" : new SimpleDateFormat().format(new java.util.Date(last_ygm)));
/*      */         
/* 1603 */         String lm = buddy.getLastMessageReceived();
/*      */         
/* 1605 */         item.setText(4, lm == null ? "" : lm);
/*      */         
/* 1607 */         String loc_cat = buddy.getLocalAuthorisedRSSTagsOrCategoriesAsString();
/* 1608 */         if (loc_cat == null) {
/* 1609 */           loc_cat = "";
/*      */         }
/* 1611 */         item.setText(5, "" + loc_cat);
/*      */         
/* 1613 */         String rem_cat = buddy.getRemoteAuthorisedRSSTagsOrCategoriesAsString();
/* 1614 */         if (rem_cat == null) {
/* 1615 */           rem_cat = "";
/*      */         }
/* 1617 */         item.setText(6, "" + rem_cat);
/*      */         
/* 1619 */         String read_cat = buddy.getLocalReadTagsOrCategoriesAsString();
/* 1620 */         if (read_cat == null) {
/* 1621 */           read_cat = "";
/*      */         }
/* 1623 */         item.setText(7, "" + read_cat);
/*      */         
/* 1625 */         item.setText(8, "" + buddy.getConnectionsString());
/*      */         
/* 1627 */         item.setText(9, "" + BuddyPluginViewInstance.this.tracker.getTrackingStatus(buddy));
/*      */         
/* 1629 */         String in_frag = buddy.getMessageInFragmentDetails();
/*      */         
/* 1631 */         item.setText(10, "" + buddy.getMessageInCount() + (in_frag.length() == 0 ? "" : new StringBuilder().append("+").append(in_frag).toString()));
/* 1632 */         item.setText(11, "" + buddy.getMessageOutCount());
/* 1633 */         item.setText(12, "" + buddy.getMessageHandler().getMessageCount());
/* 1634 */         item.setText(13, "" + DisplayFormatters.formatByteCountToKiBEtc(buddy.getBytesInCount()));
/* 1635 */         item.setText(14, "" + DisplayFormatters.formatByteCountToKiBEtc(buddy.getBytesOutCount()));
/*      */         
/* 1637 */         item.setText(15, "" + buddy.getSubsystem() + " v" + buddy.getVersion());
/*      */         
/* 1639 */         item.setData(buddy);
/*      */       }
/*      */       
/* 1642 */     });
/* 1643 */     final Listener tt_label_listener = new Listener()
/*      */     {
/*      */       public void handleEvent(Event event)
/*      */       {
/* 1647 */         Label label = (Label)event.widget;
/* 1648 */         Shell shell = label.getShell();
/* 1649 */         switch (event.type) {
/*      */         case 3: 
/* 1651 */           Event e = new Event();
/* 1652 */           e.item = ((TableItem)label.getData("_TABLEITEM"));
/* 1653 */           BuddyPluginViewInstance.this.buddy_table.setSelection(new TableItem[] { (TableItem)e.item });
/* 1654 */           BuddyPluginViewInstance.this.buddy_table.notifyListeners(13, e);
/*      */         
/*      */         case 7: 
/* 1657 */           shell.dispose();
/*      */         
/*      */ 
/*      */         }
/*      */         
/*      */       }
/* 1663 */     };
/* 1664 */     Listener tt_table_listener = new Listener()
/*      */     {
/*      */ 
/* 1667 */       private Shell tip = null;
/*      */       
/* 1669 */       private Label label = null;
/*      */       
/*      */ 
/*      */ 
/*      */       public void handleEvent(Event event)
/*      */       {
/* 1675 */         switch (event.type) {
/*      */         case 1: 
/*      */         case 5: 
/*      */         case 12: 
/* 1679 */           if (this.tip != null)
/*      */           {
/* 1681 */             this.tip.dispose();
/* 1682 */             this.tip = null;
/* 1683 */             this.label = null; }
/* 1684 */           break;
/*      */         
/*      */ 
/*      */         case 32: 
/* 1688 */           Point mouse_position = new Point(event.x, event.y);
/*      */           
/* 1690 */           TableItem item = BuddyPluginViewInstance.this.buddy_table.getItem(mouse_position);
/*      */           
/* 1692 */           if (item != null)
/*      */           {
/* 1694 */             if ((this.tip != null) && (!this.tip.isDisposed()))
/*      */             {
/* 1696 */               this.tip.dispose();
/*      */               
/* 1698 */               this.tip = null;
/*      */             }
/*      */             
/* 1701 */             int index = BuddyPluginViewInstance.this.buddy_table.indexOf(item);
/*      */             
/* 1703 */             if ((index < 0) || (index >= BuddyPluginViewInstance.this.buddies.size()))
/*      */             {
/* 1705 */               return;
/*      */             }
/*      */             
/* 1708 */             BuddyPluginBuddy buddy = (BuddyPluginBuddy)BuddyPluginViewInstance.this.buddies.get(index);
/*      */             
/* 1710 */             int item_index = 0;
/*      */             
/* 1712 */             for (int i = 0; i < headers.length; i++)
/*      */             {
/* 1714 */               Rectangle bounds = item.getBounds(i);
/*      */               
/* 1716 */               if (bounds.contains(mouse_position))
/*      */               {
/* 1718 */                 item_index = i;
/*      */                 
/* 1720 */                 break;
/*      */               }
/*      */             }
/*      */             
/* 1724 */             if (item_index != 0)
/*      */             {
/* 1726 */               return;
/*      */             }
/*      */             
/* 1729 */             this.tip = new Shell(BuddyPluginViewInstance.this.buddy_table.getShell(), 16388);
/* 1730 */             this.tip.setLayout(new org.eclipse.swt.layout.FillLayout());
/* 1731 */             this.label = new Label(this.tip, 0);
/* 1732 */             this.label.setForeground(BuddyPluginViewInstance.this.buddy_table.getDisplay().getSystemColor(28));
/*      */             
/* 1734 */             this.label.setBackground(BuddyPluginViewInstance.this.buddy_table.getDisplay().getSystemColor(29));
/*      */             
/* 1736 */             this.label.setData("_TABLEITEM", item);
/*      */             
/* 1738 */             this.label.setText(getToolTip(buddy));
/*      */             
/* 1740 */             this.label.addListener(7, tt_label_listener);
/* 1741 */             this.label.addListener(3, tt_label_listener);
/* 1742 */             Point size = this.tip.computeSize(-1, -1);
/* 1743 */             Rectangle rect = item.getBounds(item_index);
/* 1744 */             Point pt = BuddyPluginViewInstance.this.buddy_table.toDisplay(rect.x, rect.y);
/* 1745 */             this.tip.setBounds(pt.x, pt.y, size.x, size.y);
/* 1746 */             this.tip.setVisible(true);
/*      */           }
/*      */           
/*      */           break;
/*      */         }
/*      */         
/*      */       }
/*      */       
/*      */       protected String getToolTip(BuddyPluginBuddy buddy)
/*      */       {
/* 1756 */         List<InetAddress> addresses = buddy.getAdjustedIPs();
/*      */         
/* 1758 */         InetAddress ip = buddy.getIP();
/*      */         
/* 1760 */         InetAddress adj = buddy.getAdjustedIP();
/*      */         
/* 1762 */         String str = "";
/*      */         
/* 1764 */         if (ip == null)
/*      */         {
/* 1766 */           str = "<none>";
/*      */         }
/* 1768 */         else if (ip == adj)
/*      */         {
/* 1770 */           str = ip.getHostAddress();
/*      */         }
/*      */         else
/*      */         {
/* 1774 */           str = ip.getHostAddress() + "{";
/*      */           
/* 1776 */           for (int i = 0; i < addresses.size(); i++)
/*      */           {
/* 1778 */             str = str + (i == 0 ? "" : "/") + ((InetAddress)addresses.get(i)).getHostAddress();
/*      */           }
/*      */           
/* 1781 */           str = str + "}";
/*      */         }
/*      */         
/* 1784 */         return "ip=" + str + ",tcp=" + buddy.getTCPPort() + ",udp=" + buddy.getUDPPort();
/*      */       }
/*      */       
/* 1787 */     };
/* 1788 */     this.buddy_table.addListener(12, tt_table_listener);
/* 1789 */     this.buddy_table.addListener(1, tt_table_listener);
/* 1790 */     this.buddy_table.addListener(5, tt_table_listener);
/* 1791 */     this.buddy_table.addListener(32, tt_table_listener);
/*      */     
/*      */ 
/*      */ 
/* 1795 */     Menu menu = new Menu(this.buddy_table);
/*      */     
/* 1797 */     final MenuItem remove_item = new MenuItem(menu, 8);
/*      */     
/* 1799 */     remove_item.setText(this.lu.getLocalisedMessageText("azbuddy.ui.menu.remove"));
/*      */     
/* 1801 */     remove_item.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent e)
/*      */       {
/*      */ 
/* 1808 */         TableItem[] selection = BuddyPluginViewInstance.this.buddy_table.getSelection();
/*      */         
/* 1810 */         for (int i = 0; i < selection.length; i++)
/*      */         {
/* 1812 */           BuddyPluginBuddy buddy = (BuddyPluginBuddy)selection[i].getData();
/*      */           
/* 1814 */           buddy.remove();
/*      */         }
/*      */         
/*      */       }
/*      */       
/*      */ 
/* 1820 */     });
/* 1821 */     final MenuItem get_pk_item = new MenuItem(menu, 8);
/*      */     
/* 1823 */     get_pk_item.setText(this.lu.getLocalisedMessageText("azbuddy.ui.menu.copypk"));
/*      */     
/* 1825 */     get_pk_item.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent event)
/*      */       {
/*      */ 
/* 1832 */         TableItem[] selection = BuddyPluginViewInstance.this.buddy_table.getSelection();
/*      */         
/* 1834 */         StringBuilder sb = new StringBuilder();
/*      */         
/* 1836 */         for (int i = 0; i < selection.length; i++)
/*      */         {
/* 1838 */           BuddyPluginBuddy buddy = (BuddyPluginBuddy)selection[i].getData();
/*      */           
/* 1840 */           sb.append(buddy.getPublicKey()).append("\r\n");
/*      */         }
/*      */         
/* 1843 */         if (sb.length() > 0)
/*      */         {
/* 1845 */           BuddyPluginViewInstance.this.writeToClipboard(sb.toString());
/*      */         }
/*      */       }
/*      */     });
/*      */     
/*      */ 
/*      */ 
/* 1852 */     if (org.gudy.azureus2.core3.util.Constants.isCVSVersion()) {
/* 1853 */       MenuItem send_msg_item = new MenuItem(menu, 8);
/*      */       
/* 1855 */       send_msg_item.setText(this.lu.getLocalisedMessageText("azbuddy.ui.menu.disconnect"));
/*      */       
/* 1857 */       send_msg_item.addSelectionListener(new SelectionAdapter()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void widgetSelected(SelectionEvent event)
/*      */         {
/*      */ 
/* 1864 */           TableItem[] selection = BuddyPluginViewInstance.this.buddy_table.getSelection();
/*      */           
/* 1866 */           for (int i = 0; i < selection.length; i++)
/*      */           {
/* 1868 */             BuddyPluginBuddy buddy = (BuddyPluginBuddy)selection[i].getData();
/*      */             
/* 1870 */             buddy.disconnect();
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1878 */     final MenuItem send_msg_item = new MenuItem(menu, 8);
/*      */     
/* 1880 */     send_msg_item.setText(this.lu.getLocalisedMessageText("azbuddy.ui.menu.send"));
/*      */     
/* 1882 */     send_msg_item.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent event)
/*      */       {
/*      */ 
/* 1889 */         final TableItem[] selection = BuddyPluginViewInstance.this.buddy_table.getSelection();
/*      */         
/* 1891 */         UIInputReceiver prompter = BuddyPluginViewInstance.this.ui_instance.getInputReceiver();
/*      */         
/* 1893 */         prompter.setLocalisedTitle(BuddyPluginViewInstance.this.lu.getLocalisedMessageText("azbuddy.ui.menu.send"));
/* 1894 */         prompter.setLocalisedMessage(BuddyPluginViewInstance.this.lu.getLocalisedMessageText("azbuddy.ui.menu.send_msg"));
/*      */         try
/*      */         {
/* 1897 */           prompter.prompt(new org.gudy.azureus2.plugins.ui.UIInputReceiverListener() {
/*      */             public void UIInputReceiverClosed(UIInputReceiver prompter) {
/* 1899 */               String text = prompter.getSubmittedInput();
/*      */               
/* 1901 */               if (text != null)
/*      */               {
/* 1903 */                 for (int i = 0; i < selection.length; i++)
/*      */                 {
/* 1905 */                   BuddyPluginBuddy buddy = (BuddyPluginBuddy)selection[i].getData();
/*      */                   
/* 1907 */                   BuddyPluginViewInstance.this.plugin.getAZ2Handler().sendAZ2Message(buddy, text);
/*      */                 }
/*      */                 
/*      */               }
/*      */               
/*      */             }
/*      */             
/*      */ 
/*      */           });
/*      */ 
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/* 1920 */     });
/* 1921 */     final MenuItem chat_item = new MenuItem(menu, 8);
/*      */     
/* 1923 */     chat_item.setText(this.lu.getLocalisedMessageText("azbuddy.ui.menu.chat"));
/*      */     
/* 1925 */     chat_item.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent event)
/*      */       {
/*      */ 
/* 1932 */         TableItem[] selection = BuddyPluginViewInstance.this.buddy_table.getSelection();
/*      */         
/* 1934 */         BuddyPluginBuddy[] buddies = new BuddyPluginBuddy[selection.length];
/*      */         
/* 1936 */         for (int i = 0; i < selection.length; i++)
/*      */         {
/* 1938 */           BuddyPluginBuddy buddy = (BuddyPluginBuddy)selection[i].getData();
/*      */           
/* 1940 */           buddies[i] = buddy;
/*      */         }
/*      */         
/* 1943 */         BuddyPluginViewInstance.this.plugin.getAZ2Handler().createChat(buddies);
/*      */ 
/*      */       }
/*      */       
/*      */ 
/* 1948 */     });
/* 1949 */     final MenuItem ping_item = new MenuItem(menu, 8);
/*      */     
/* 1951 */     ping_item.setText(this.lu.getLocalisedMessageText("azbuddy.ui.menu.ping"));
/*      */     
/* 1953 */     ping_item.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent event)
/*      */       {
/*      */ 
/* 1960 */         TableItem[] selection = BuddyPluginViewInstance.this.buddy_table.getSelection();
/*      */         
/* 1962 */         for (int i = 0; i < selection.length; i++)
/*      */         {
/* 1964 */           BuddyPluginBuddy buddy = (BuddyPluginBuddy)selection[i].getData();
/*      */           try
/*      */           {
/* 1967 */             buddy.ping();
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1971 */             BuddyPluginViewInstance.this.print("Ping failed", e);
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */       }
/*      */       
/* 1978 */     });
/* 1979 */     final MenuItem ygm_item = new MenuItem(menu, 8);
/*      */     
/* 1981 */     ygm_item.setText(this.lu.getLocalisedMessageText("azbuddy.ui.menu.ygm"));
/*      */     
/* 1983 */     ygm_item.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent event)
/*      */       {
/*      */ 
/* 1990 */         TableItem[] selection = BuddyPluginViewInstance.this.buddy_table.getSelection();
/*      */         
/* 1992 */         for (int i = 0; i < selection.length; i++)
/*      */         {
/* 1994 */           BuddyPluginBuddy buddy = (BuddyPluginBuddy)selection[i].getData();
/*      */           try
/*      */           {
/* 1997 */             buddy.setMessagePending();
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 2001 */             BuddyPluginViewInstance.this.print("YGM failed", e);
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */       }
/*      */       
/*      */ 
/* 2009 */     });
/* 2010 */     final MenuItem encrypt_item = new MenuItem(menu, 8);
/*      */     
/* 2012 */     encrypt_item.setText(this.lu.getLocalisedMessageText("azbuddy.ui.menu.enc"));
/*      */     
/* 2014 */     encrypt_item.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent event)
/*      */       {
/*      */ 
/* 2021 */         TableItem[] selection = BuddyPluginViewInstance.this.buddy_table.getSelection();
/*      */         
/* 2023 */         String str = BuddyPluginViewInstance.this.readFromClipboard();
/*      */         
/* 2025 */         if (str != null)
/*      */         {
/* 2027 */           StringBuilder sb = new StringBuilder();
/*      */           
/* 2029 */           for (int i = 0; i < selection.length; i++)
/*      */           {
/* 2031 */             BuddyPluginBuddy buddy = (BuddyPluginBuddy)selection[i].getData();
/*      */             try
/*      */             {
/* 2034 */               byte[] contents = str.getBytes("UTF-8");
/*      */               
/* 2036 */               BuddyPlugin.cryptoResult result = buddy.encrypt(contents);
/*      */               
/* 2038 */               sb.append("key: ");
/* 2039 */               sb.append(BuddyPluginViewInstance.this.plugin.getPublicKey());
/* 2040 */               sb.append("\r\n");
/*      */               
/* 2042 */               sb.append("hash: ");
/* 2043 */               sb.append(Base32.encode(result.getChallenge()));
/* 2044 */               sb.append("\r\n");
/*      */               
/* 2046 */               sb.append("payload: ");
/* 2047 */               sb.append(Base32.encode(result.getPayload()));
/* 2048 */               sb.append("\r\n\r\n");
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 2052 */               BuddyPluginViewInstance.this.print("YGM failed", e);
/*      */             }
/*      */           }
/*      */           
/* 2056 */           BuddyPluginViewInstance.this.writeToClipboard(sb.toString());
/*      */         }
/*      */         
/*      */       }
/*      */       
/*      */ 
/* 2062 */     });
/* 2063 */     final MenuItem decrypt_item = new MenuItem(menu, 8);
/*      */     
/* 2065 */     decrypt_item.setText(this.lu.getLocalisedMessageText("azbuddy.ui.menu.dec"));
/*      */     
/* 2067 */     decrypt_item.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent event)
/*      */       {
/*      */ 
/* 2074 */         String str = BuddyPluginViewInstance.this.readFromClipboard();
/*      */         
/* 2076 */         if (str != null)
/*      */         {
/* 2078 */           String[] bits = str.split("\n");
/*      */           
/* 2080 */           StringBuilder sb = new StringBuilder();
/*      */           
/* 2082 */           BuddyPluginBuddy buddy = null;
/* 2083 */           byte[] hash = null;
/*      */           
/* 2085 */           for (int i = 0; i < bits.length; i++)
/*      */           {
/* 2087 */             String bit = bits[i].trim();
/*      */             
/* 2089 */             if (bit.length() > 0)
/*      */             {
/* 2091 */               int pos = bit.indexOf(':');
/*      */               
/* 2093 */               if (pos != -1)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/* 2098 */                 String lhs = bit.substring(0, pos).trim();
/* 2099 */                 String rhs = bit.substring(pos + 1).trim();
/*      */                 
/* 2101 */                 if (lhs.equals("key"))
/*      */                 {
/* 2103 */                   buddy = BuddyPluginViewInstance.this.plugin.getBuddyFromPublicKey(rhs);
/*      */                 }
/* 2105 */                 else if (lhs.equals("hash"))
/*      */                 {
/* 2107 */                   hash = Base32.decode(rhs);
/*      */                 }
/* 2109 */                 else if (lhs.equals("payload"))
/*      */                 {
/* 2111 */                   byte[] payload = Base32.decode(rhs);
/*      */                   
/* 2113 */                   if (buddy != null) {
/*      */                     try
/*      */                     {
/* 2116 */                       BuddyPlugin.cryptoResult result = buddy.decrypt(payload);
/*      */                       
/* 2118 */                       byte[] sha1 = new SHA1Simple().calculateHash(result.getChallenge());
/*      */                       
/* 2120 */                       sb.append("key: ");
/* 2121 */                       sb.append(buddy.getPublicKey());
/* 2122 */                       sb.append("\r\n");
/*      */                       
/* 2124 */                       sb.append("hash_ok: ").append(java.util.Arrays.equals(hash, sha1));
/* 2125 */                       sb.append("\r\n");
/*      */                       
/* 2127 */                       sb.append("payload: ");
/* 2128 */                       sb.append(new String(result.getPayload(), "UTF-8"));
/* 2129 */                       sb.append("\r\n\r\n");
/*      */                     }
/*      */                     catch (Throwable e)
/*      */                     {
/* 2133 */                       BuddyPluginViewInstance.this.print("decrypt failed", e);
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/* 2140 */           if (sb.length() > 0)
/*      */           {
/* 2142 */             BuddyPluginViewInstance.this.writeToClipboard(sb.toString());
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */       }
/*      */       
/* 2149 */     });
/* 2150 */     final MenuItem sign_item = new MenuItem(menu, 8);
/*      */     
/* 2152 */     sign_item.setText(this.lu.getLocalisedMessageText("azbuddy.ui.menu.sign"));
/*      */     
/* 2154 */     sign_item.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent event)
/*      */       {
/*      */ 
/* 2161 */         String str = BuddyPluginViewInstance.this.readFromClipboard();
/*      */         
/* 2163 */         if (str != null)
/*      */         {
/* 2165 */           StringBuilder sb = new StringBuilder();
/*      */           try
/*      */           {
/* 2168 */             sb.append("key: ");
/* 2169 */             sb.append(BuddyPluginViewInstance.this.plugin.getPublicKey());
/* 2170 */             sb.append("\r\n");
/*      */             
/* 2172 */             byte[] payload = str.getBytes("UTF-8");
/*      */             
/* 2174 */             sb.append("data: ");
/* 2175 */             sb.append(Base32.encode(payload));
/* 2176 */             sb.append("\r\n");
/*      */             
/* 2178 */             byte[] sig = BuddyPluginViewInstance.this.plugin.sign(payload);
/*      */             
/* 2180 */             sb.append("sig: ");
/* 2181 */             sb.append(Base32.encode(sig));
/* 2182 */             sb.append("\r\n");
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 2186 */             BuddyPluginViewInstance.this.print("sign failed", e);
/*      */           }
/*      */           
/* 2189 */           if (sb.length() > 0)
/*      */           {
/* 2191 */             BuddyPluginViewInstance.this.writeToClipboard(sb.toString());
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */       }
/*      */       
/* 2198 */     });
/* 2199 */     final MenuItem verify_item = new MenuItem(menu, 8);
/*      */     
/* 2201 */     verify_item.setText(this.lu.getLocalisedMessageText("azbuddy.ui.menu.verify"));
/*      */     
/* 2203 */     verify_item.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent event)
/*      */       {
/*      */ 
/* 2210 */         String str = BuddyPluginViewInstance.this.readFromClipboard();
/*      */         
/* 2212 */         if (str != null)
/*      */         {
/* 2214 */           String[] bits = str.split("\n");
/*      */           
/* 2216 */           StringBuilder sb = new StringBuilder();
/*      */           
/* 2218 */           String pk = null;
/* 2219 */           byte[] data = null;
/*      */           
/* 2221 */           for (int i = 0; i < bits.length; i++)
/*      */           {
/* 2223 */             String bit = bits[i].trim();
/*      */             
/* 2225 */             if (bit.length() > 0)
/*      */             {
/* 2227 */               int pos = bit.indexOf(':');
/*      */               
/* 2229 */               if (pos != -1)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/* 2234 */                 String lhs = bit.substring(0, pos).trim();
/* 2235 */                 String rhs = bit.substring(pos + 1).trim();
/*      */                 
/* 2237 */                 if (lhs.equals("key"))
/*      */                 {
/* 2239 */                   pk = rhs;
/*      */                 }
/* 2241 */                 else if (lhs.equals("data"))
/*      */                 {
/* 2243 */                   data = Base32.decode(rhs);
/*      */                 }
/* 2245 */                 else if (lhs.equals("sig"))
/*      */                 {
/* 2247 */                   byte[] sig = Base32.decode(rhs);
/*      */                   
/* 2249 */                   if ((pk != null) && (data != null))
/*      */                   {
/*      */                     try
/*      */                     {
/* 2253 */                       sb.append("key: ");
/* 2254 */                       sb.append(pk);
/* 2255 */                       sb.append("\r\n");
/*      */                       
/* 2257 */                       boolean ok = BuddyPluginViewInstance.this.plugin.verify(pk, data, sig);
/*      */                       
/* 2259 */                       sb.append("sig_ok: ").append(ok);
/* 2260 */                       sb.append("\r\n");
/*      */                       
/* 2262 */                       sb.append("data: ");
/* 2263 */                       sb.append(new String(data, "UTF-8"));
/* 2264 */                       sb.append("\r\n\r\n");
/*      */                     }
/*      */                     catch (Throwable e)
/*      */                     {
/* 2268 */                       BuddyPluginViewInstance.this.print("decrypt failed", e);
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/* 2275 */           if (sb.length() > 0)
/*      */           {
/* 2277 */             BuddyPluginViewInstance.this.writeToClipboard(sb.toString());
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */       }
/*      */       
/*      */ 
/* 2285 */     });
/* 2286 */     Menu cat_menu = new Menu(menu.getShell(), 4);
/* 2287 */     MenuItem cat_item = new MenuItem(menu, 64);
/* 2288 */     Messages.setLanguageText(cat_item, "azbuddy.ui.menu.cat");
/* 2289 */     cat_item.setMenu(cat_menu);
/*      */     
/*      */ 
/*      */ 
/* 2293 */     MenuItem cat_share_item = new MenuItem(cat_menu, 8);
/*      */     
/* 2295 */     cat_share_item.setText(this.lu.getLocalisedMessageText("azbuddy.ui.menu.cat.share"));
/*      */     
/* 2297 */     cat_share_item.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void widgetSelected(SelectionEvent event)
/*      */       {
/*      */ 
/* 2304 */         UIInputReceiver prompter = BuddyPluginViewInstance.this.ui_instance.getInputReceiver();
/*      */         
/* 2306 */         prompter.setLocalisedTitle(BuddyPluginViewInstance.this.lu.getLocalisedMessageText("azbuddy.ui.menu.cat.set"));
/* 2307 */         prompter.setLocalisedMessage(BuddyPluginViewInstance.this.lu.getLocalisedMessageText("azbuddy.ui.menu.cat.set_msg"));
/*      */         
/* 2309 */         prompter.prompt(new org.gudy.azureus2.plugins.ui.UIInputReceiverListener() {
/*      */           public void UIInputReceiverClosed(UIInputReceiver prompter) {
/* 2311 */             String cats = prompter.getSubmittedInput();
/*      */             
/* 2313 */             if (cats != null)
/*      */             {
/* 2315 */               cats = cats.trim();
/*      */               
/* 2317 */               if (cats.equalsIgnoreCase("None"))
/*      */               {
/* 2319 */                 cats = "";
/*      */               }
/*      */               
/* 2322 */               TableItem[] selection = BuddyPluginViewInstance.this.buddy_table.getSelection();
/*      */               
/* 2324 */               for (int i = 0; i < selection.length; i++)
/*      */               {
/* 2326 */                 BuddyPluginBuddy buddy = (BuddyPluginBuddy)selection[i].getData();
/*      */                 
/* 2328 */                 buddy.setLocalAuthorisedRSSTagsOrCategories(cats);
/*      */               }
/*      */               
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */ 
/*      */         });
/*      */       }
/* 2338 */     });
/* 2339 */     final Menu cat_subs_menu = new Menu(cat_menu.getShell(), 4);
/* 2340 */     MenuItem cat_subs_item = new MenuItem(cat_menu, 64);
/* 2341 */     Messages.setLanguageText(cat_subs_item, "azbuddy.ui.menu.cat_subs");
/* 2342 */     cat_subs_item.setMenu(cat_subs_menu);
/*      */     
/* 2344 */     cat_subs_menu.addMenuListener(new MenuListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void menuShown(MenuEvent arg0)
/*      */       {
/*      */ 
/* 2351 */         MenuItem[] items = cat_subs_menu.getItems();
/*      */         
/* 2353 */         for (int i = 0; i < items.length; i++)
/*      */         {
/* 2355 */           items[i].dispose();
/*      */         }
/*      */         
/* 2358 */         com.aelitis.azureus.core.util.AZ3Functions.provider az3 = AZ3Functions.getProvider();
/*      */         final TableItem[] selection;
/* 2360 */         if (az3 != null)
/*      */         {
/* 2362 */           selection = BuddyPluginViewInstance.this.buddy_table.getSelection();
/*      */           
/* 2364 */           Set<String> avail_cats = new java.util.TreeSet();
/*      */           
/* 2366 */           for (int i = 0; i < selection.length; i++)
/*      */           {
/* 2368 */             BuddyPluginBuddy buddy = (BuddyPluginBuddy)selection[i].getData();
/*      */             
/* 2370 */             Set<String> cats = buddy.getRemoteAuthorisedRSSTagsOrCategories();
/*      */             
/* 2372 */             if (cats != null)
/*      */             {
/* 2374 */               avail_cats.addAll(cats);
/*      */             }
/*      */           }
/*      */           
/* 2378 */           for (final String cat : avail_cats)
/*      */           {
/* 2380 */             MenuItem subs_item = new MenuItem(cat_subs_menu, 8);
/*      */             
/* 2382 */             subs_item.setText(cat);
/*      */             
/* 2384 */             subs_item.addSelectionListener(new SelectionAdapter()
/*      */             {
/*      */ 
/*      */ 
/*      */               public void widgetSelected(SelectionEvent event)
/*      */               {
/*      */ 
/* 2391 */                 for (int i = 0; i < selection.length; i++)
/*      */                 {
/* 2393 */                   BuddyPluginBuddy buddy = (BuddyPluginBuddy)selection[i].getData();
/*      */                   
/* 2395 */                   if (buddy.isRemoteRSSTagOrCategoryAuthorised(cat)) {
/*      */                     try
/*      */                     {
/* 2398 */                       buddy.subscribeToCategory(cat);
/*      */                     }
/*      */                     catch (Throwable e)
/*      */                     {
/* 2402 */                       BuddyPluginViewInstance.this.print("Failed", e);
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             });
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
/*      */ 
/*      */ 
/*      */       public void menuHidden(MenuEvent arg0) {}
/* 2421 */     });
/* 2422 */     this.buddy_table.setMenu(menu);
/*      */     
/* 2424 */     menu.addMenuListener(new MenuListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void menuShown(MenuEvent arg0)
/*      */       {
/*      */ 
/* 2431 */         boolean available = BuddyPluginViewInstance.this.plugin.isAvailable();
/*      */         
/* 2433 */         TableItem[] selection = BuddyPluginViewInstance.this.buddy_table.getSelection();
/*      */         
/* 2435 */         remove_item.setEnabled(selection.length > 0);
/* 2436 */         get_pk_item.setEnabled((available) && (selection.length > 0));
/* 2437 */         send_msg_item.setEnabled((available) && (selection.length > 0));
/* 2438 */         chat_item.setEnabled((available) && (selection.length > 0));
/* 2439 */         ping_item.setEnabled((available) && (selection.length > 0));
/* 2440 */         ygm_item.setEnabled((available) && (selection.length > 0));
/* 2441 */         encrypt_item.setEnabled(selection.length > 0);
/* 2442 */         decrypt_item.setEnabled(true);
/* 2443 */         sign_item.setEnabled(true);
/* 2444 */         verify_item.setEnabled(true);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void menuHidden(MenuEvent arg0) {}
/* 2455 */     });
/* 2456 */     this.log = new StyledText(child2, 2824);
/* 2457 */     grid_data = new GridData(1808);
/* 2458 */     grid_data.horizontalSpan = 1;
/* 2459 */     grid_data.horizontalIndent = 4;
/* 2460 */     Utils.setLayoutData(this.log, grid_data);
/* 2461 */     this.log.setIndent(4);
/*      */     
/* 2463 */     this.buddies = this.plugin.getBuddies();
/*      */     
/* 2465 */     for (int i = 0; i < this.buddies.size(); i++)
/*      */     {
/* 2467 */       buddyAdded((BuddyPluginBuddy)this.buddies.get(i));
/*      */     }
/*      */     
/* 2470 */     java.util.Collections.sort(this.buddies, comparator);
/*      */     
/* 2472 */     this.plugin.addListener(this);
/*      */     
/* 2474 */     this.plugin.addRequestListener(this);
/*      */     
/* 2476 */     this.init_complete = true;
/*      */     
/* 2478 */     updateTable();
/*      */   }
/*      */   
/*      */ 
/*      */   protected String readFromClipboard()
/*      */   {
/* 2484 */     Object o = new Clipboard(SWTThread.getInstance().getDisplay()).getContents(TextTransfer.getInstance());
/*      */     
/*      */ 
/*      */ 
/* 2488 */     if ((o instanceof String))
/*      */     {
/* 2490 */       return (String)o;
/*      */     }
/*      */     
/* 2493 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void writeToClipboard(String str)
/*      */   {
/* 2500 */     new Clipboard(SWTThread.getInstance().getDisplay()).setContents(new Object[] { str }, new org.eclipse.swt.dnd.Transfer[] { TextTransfer.getInstance() });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void updateTable()
/*      */   {
/* 2508 */     if (this.init_complete)
/*      */     {
/* 2510 */       this.buddy_table.setItemCount(this.buddies.size());
/* 2511 */       this.buddy_table.clearAll();
/* 2512 */       this.buddy_table.redraw();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void initialised(boolean available)
/*      */   {
/* 2520 */     print("Initialisation complete: available=" + available);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void buddyAdded(final BuddyPluginBuddy buddy)
/*      */   {
/* 2527 */     if (this.buddy_table.isDisposed())
/*      */     {
/* 2529 */       return;
/*      */     }
/*      */     
/* 2532 */     buddy.getMessageHandler().addListener(new com.aelitis.azureus.plugins.net.buddy.BuddyPluginBuddyMessageListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void messageQueued(BuddyPluginBuddyMessage message)
/*      */       {
/*      */ 
/* 2539 */         BuddyPluginViewInstance.this.print(message.getBuddy().getName() + ": message queued, id=" + message.getID());
/*      */         
/* 2541 */         update();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void messageDeleted(BuddyPluginBuddyMessage message)
/*      */       {
/* 2548 */         BuddyPluginViewInstance.this.print(message.getBuddy().getName() + ": message deleted, id=" + message.getID());
/*      */         
/* 2550 */         update();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public boolean deliverySucceeded(BuddyPluginBuddyMessage message, Map reply)
/*      */       {
/* 2558 */         BuddyPluginViewInstance.this.print(message.getBuddy().getName() + ": message delivered, id=" + message.getID() + ", reply=" + reply);
/*      */         
/* 2560 */         update();
/*      */         
/* 2562 */         return true;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void deliveryFailed(BuddyPluginBuddyMessage message, BuddyPluginException cause)
/*      */       {
/* 2570 */         BuddyPluginViewInstance.this.print(message.getBuddy().getName() + ": message failed, id=" + message.getID(), cause);
/*      */         
/* 2572 */         update();
/*      */       }
/*      */       
/*      */ 
/*      */       protected void update()
/*      */       {
/* 2578 */         if (!BuddyPluginViewInstance.this.buddy_table.isDisposed())
/*      */         {
/* 2580 */           BuddyPluginViewInstance.this.buddy_table.getDisplay().asyncExec(new Runnable()
/*      */           {
/*      */ 
/*      */             public void run()
/*      */             {
/*      */ 
/* 2586 */               if (!BuddyPluginViewInstance.this.buddy_table.isDisposed())
/*      */               {
/* 2588 */                 BuddyPluginViewInstance.this.updateTable();
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */     });
/*      */     
/* 2596 */     if (!this.buddies.contains(buddy))
/*      */     {
/* 2598 */       this.buddy_table.getDisplay().asyncExec(new Runnable()
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/*      */ 
/* 2604 */           if (!BuddyPluginViewInstance.this.buddy_table.isDisposed())
/*      */           {
/* 2606 */             if (!BuddyPluginViewInstance.this.buddies.contains(buddy))
/*      */             {
/* 2608 */               BuddyPluginViewInstance.this.buddies.add(buddy);
/*      */               
/* 2610 */               BuddyPluginViewInstance.this.updateTable();
/*      */             }
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void buddyRemoved(final BuddyPluginBuddy buddy)
/*      */   {
/* 2622 */     if (!this.buddy_table.isDisposed())
/*      */     {
/* 2624 */       this.buddy_table.getDisplay().asyncExec(new Runnable()
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/*      */ 
/* 2630 */           if (!BuddyPluginViewInstance.this.buddy_table.isDisposed())
/*      */           {
/* 2632 */             if (BuddyPluginViewInstance.this.buddies.remove(buddy))
/*      */             {
/* 2634 */               BuddyPluginViewInstance.this.updateTable();
/*      */             }
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void buddyChanged(BuddyPluginBuddy buddy)
/*      */   {
/* 2646 */     if (!this.buddy_table.isDisposed())
/*      */     {
/* 2648 */       this.buddy_table.getDisplay().asyncExec(new Runnable()
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/*      */ 
/* 2654 */           if (!BuddyPluginViewInstance.this.buddy_table.isDisposed())
/*      */           {
/* 2656 */             BuddyPluginViewInstance.this.updateTable();
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void messageLogged(String str, boolean error)
/*      */   {
/* 2668 */     print(str, error ? 3 : 1, false, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void enabledStateChanged(boolean enabled) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void updated() {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Map requestReceived(BuddyPluginBuddy from_buddy, int subsystem, Map request)
/*      */     throws BuddyPluginException
/*      */   {
/* 2690 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void pendingMessages(BuddyPluginBuddy[] from_buddies)
/*      */   {
/* 2697 */     String str = "";
/*      */     
/* 2699 */     for (int i = 0; i < from_buddies.length; i++)
/*      */     {
/* 2701 */       str = str + (str.length() == 0 ? "" : ",") + from_buddies[i].getName();
/*      */     }
/*      */     
/* 2704 */     print("YGM received: " + str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void print(String str, Throwable e)
/*      */   {
/* 2712 */     print(str + ": " + Debug.getNestedExceptionMessage(e));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void print(String str)
/*      */   {
/* 2719 */     print(str, 1, false, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void print(final String str, int log_type, final boolean clear_first, boolean log_to_plugin)
/*      */   {
/* 2729 */     if (log_to_plugin)
/*      */     {
/* 2731 */       this.plugin.log(str);
/*      */     }
/*      */     
/* 2734 */     if (!this.log.isDisposed())
/*      */     {
/* 2736 */       final int f_log_type = log_type;
/*      */       
/* 2738 */       this.log.getDisplay().asyncExec(new Runnable()
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/*      */ 
/* 2744 */           if (BuddyPluginViewInstance.this.log.isDisposed()) {
/*      */             return;
/*      */           }
/*      */           
/*      */ 
/*      */           int start;
/*      */           
/* 2751 */           if (clear_first)
/*      */           {
/* 2753 */             int start = 0;
/*      */             
/* 2755 */             BuddyPluginViewInstance.this.log.setText(str + "\n");
/*      */           }
/*      */           else
/*      */           {
/* 2759 */             String text = BuddyPluginViewInstance.this.log.getText();
/*      */             
/* 2761 */             start = text.length();
/*      */             
/* 2763 */             if (start > 32000)
/*      */             {
/* 2765 */               BuddyPluginViewInstance.this.log.replaceTextRange(0, 1024, "");
/*      */               
/* 2767 */               start = BuddyPluginViewInstance.this.log.getText().length();
/*      */             }
/*      */             
/* 2770 */             BuddyPluginViewInstance.this.log.append(str + "\n");
/*      */           }
/*      */           
/*      */           Color color;
/*      */           Color color;
/* 2775 */           if (f_log_type == 1)
/*      */           {
/* 2777 */             color = Colors.black;
/*      */           } else { Color color;
/* 2779 */             if (f_log_type == 2)
/*      */             {
/* 2781 */               color = Colors.green;
/*      */             }
/*      */             else
/*      */             {
/* 2785 */               color = Colors.red;
/*      */             }
/*      */           }
/* 2788 */           if (color != Colors.black)
/*      */           {
/* 2790 */             StyleRange styleRange = new StyleRange();
/* 2791 */             styleRange.start = start;
/* 2792 */             styleRange.length = str.length();
/* 2793 */             styleRange.foreground = color;
/* 2794 */             BuddyPluginViewInstance.this.log.setStyleRange(styleRange);
/*      */           }
/*      */           
/* 2797 */           BuddyPluginViewInstance.this.log.setSelection(BuddyPluginViewInstance.this.log.getText().length());
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void destroy()
/*      */   {
/* 2806 */     this.composite = null;
/*      */     
/* 2808 */     this.plugin.removeListener(this);
/*      */     
/* 2810 */     this.plugin.removeRequestListener(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected class FilterComparator
/*      */     implements java.util.Comparator<BuddyPluginBuddy>
/*      */   {
/* 2818 */     boolean ascending = false;
/*      */     
/*      */     static final int FIELD_NAME = 0;
/*      */     
/*      */     static final int FIELD_ONLINE = 1;
/*      */     static final int FIELD_LAST_SEEN = 2;
/*      */     static final int FIELD_YGM = 3;
/*      */     static final int FIELD_LAST_MSG = 4;
/*      */     static final int FIELD_LOC_CAT = 5;
/*      */     static final int FIELD_REM_CAT = 6;
/*      */     static final int FIELD_READ_CAT = 7;
/*      */     static final int FIELD_CON = 8;
/*      */     static final int FIELD_TRACK = 9;
/*      */     static final int FIELD_MSG_IN = 10;
/*      */     static final int FIELD_MSG_OUT = 11;
/*      */     static final int FIELD_QUEUED = 12;
/*      */     static final int FIELD_BYTES_IN = 13;
/*      */     static final int FIELD_BYTES_OUT = 14;
/*      */     static final int FIELD_SS = 15;
/* 2837 */     int field = 0;
/*      */     
/*      */ 
/*      */     protected FilterComparator() {}
/*      */     
/*      */     public int compare(BuddyPluginBuddy b1, BuddyPluginBuddy b2)
/*      */     {
/* 2844 */       int res = 0;
/*      */       
/* 2846 */       if (this.field == 0) {
/* 2847 */         res = b1.getName().compareTo(b2.getName());
/* 2848 */       } else if (this.field == 1) {
/* 2849 */         res = (b1.isOnline(false) ? 1 : 0) - (b2.isOnline(false) ? 1 : 0);
/* 2850 */       } else if (this.field == 2) {
/* 2851 */         res = sortInt(b1.getLastTimeOnline() - b2.getLastTimeOnline());
/* 2852 */       } else if (this.field == 3) {
/* 2853 */         res = sortInt(b1.getLastMessagePending() - b2.getLastMessagePending());
/* 2854 */       } else if (this.field == 4) {
/* 2855 */         res = b1.getLastMessageReceived().compareTo(b2.getLastMessageReceived());
/* 2856 */       } else if (this.field == 5) {
/* 2857 */         res = compareStrings(b1.getLocalAuthorisedRSSTagsOrCategoriesAsString(), b2.getLocalAuthorisedRSSTagsOrCategoriesAsString());
/* 2858 */       } else if (this.field == 6) {
/* 2859 */         res = compareStrings(b1.getRemoteAuthorisedRSSTagsOrCategoriesAsString(), b2.getRemoteAuthorisedRSSTagsOrCategoriesAsString());
/* 2860 */       } else if (this.field == 7) {
/* 2861 */         res = compareStrings(b1.getLocalReadTagsOrCategoriesAsString(), b2.getLocalReadTagsOrCategoriesAsString());
/* 2862 */       } else if (this.field == 8) {
/* 2863 */         res = b1.getConnectionsString().compareTo(b2.getConnectionsString());
/* 2864 */       } else if (this.field == 9) {
/* 2865 */         res = BuddyPluginViewInstance.this.tracker.getTrackingStatus(b1).compareTo(BuddyPluginViewInstance.this.tracker.getTrackingStatus(b2));
/* 2866 */       } else if (this.field == 10) {
/* 2867 */         res = b1.getMessageInCount() - b2.getMessageInCount();
/* 2868 */       } else if (this.field == 11) {
/* 2869 */         res = b1.getMessageOutCount() - b2.getMessageOutCount();
/* 2870 */       } else if (this.field == 12) {
/* 2871 */         res = b1.getMessageHandler().getMessageCount() - b2.getMessageHandler().getMessageCount();
/* 2872 */       } else if (this.field == 13) {
/* 2873 */         res = b1.getBytesInCount() - b2.getBytesInCount();
/* 2874 */       } else if (this.field == 14) {
/* 2875 */         res = b1.getBytesOutCount() - b2.getBytesOutCount();
/* 2876 */       } else if (this.field == 15) {
/* 2877 */         res = b1.getSubsystem() - b2.getSubsystem();
/*      */       }
/*      */       
/* 2880 */       return (this.ascending ? 1 : -1) * res;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected int compareStrings(String s1, String s2)
/*      */     {
/* 2888 */       if ((s1 == null) && (s2 == null))
/* 2889 */         return 0;
/* 2890 */       if (s1 == null)
/* 2891 */         return -1;
/* 2892 */       if (s2 == null) {
/* 2893 */         return 1;
/*      */       }
/* 2895 */       return s1.compareTo(s2);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected int sortInt(long l)
/*      */     {
/* 2903 */       if (l < 0L)
/* 2904 */         return -1;
/* 2905 */       if (l > 0L) {
/* 2906 */         return 1;
/*      */       }
/* 2908 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setField(int newField)
/*      */     {
/* 2915 */       if (this.field == newField) { this.ascending = (!this.ascending);
/*      */       }
/* 2917 */       this.field = newField;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewInstance.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */