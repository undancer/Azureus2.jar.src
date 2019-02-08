/*      */ package org.gudy.azureus2.ui.swt;
/*      */ 
/*      */ import com.aelitis.azureus.core.util.LaunchManager;
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIFunctionsUserPrompter;
/*      */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.Field;
/*      */ import java.net.URL;
/*      */ import java.net.URLConnection;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.WeakHashMap;
/*      */ import org.eclipse.swt.SWTException;
/*      */ import org.eclipse.swt.custom.CLabel;
/*      */ import org.eclipse.swt.custom.SashForm;
/*      */ import org.eclipse.swt.custom.ScrolledComposite;
/*      */ import org.eclipse.swt.dnd.Clipboard;
/*      */ import org.eclipse.swt.dnd.DropTarget;
/*      */ import org.eclipse.swt.dnd.DropTargetEvent;
/*      */ import org.eclipse.swt.events.DisposeEvent;
/*      */ import org.eclipse.swt.events.DisposeListener;
/*      */ import org.eclipse.swt.graphics.Color;
/*      */ import org.eclipse.swt.graphics.Device;
/*      */ import org.eclipse.swt.graphics.GC;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.ImageData;
/*      */ import org.eclipse.swt.graphics.PaletteData;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.graphics.RGB;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.graphics.Resource;
/*      */ import org.eclipse.swt.layout.FormAttachment;
/*      */ import org.eclipse.swt.layout.FormData;
/*      */ import org.eclipse.swt.layout.GridData;
/*      */ import org.eclipse.swt.layout.GridLayout;
/*      */ import org.eclipse.swt.layout.RowData;
/*      */ import org.eclipse.swt.layout.RowLayout;
/*      */ import org.eclipse.swt.program.Program;
/*      */ import org.eclipse.swt.widgets.Button;
/*      */ import org.eclipse.swt.widgets.ColorDialog;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Monitor;
/*      */ import org.eclipse.swt.widgets.Sash;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.eclipse.swt.widgets.Table;
/*      */ import org.eclipse.swt.widgets.TableItem;
/*      */ import org.eclipse.swt.widgets.Text;
/*      */ import org.eclipse.swt.widgets.Widget;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnosticsLogger;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AERunnableBoolean;
/*      */ import org.gudy.azureus2.core3.util.AERunnableObject;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.RARTOCDecoder;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.ThreadPool;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.UrlUtils;
/*      */ import org.gudy.azureus2.platform.PlatformManager;
/*      */ import org.gudy.azureus2.platform.PlatformManagerFactory;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.disk.DiskManagerEvent;
/*      */ import org.gudy.azureus2.plugins.utils.PooledByteBuffer;
/*      */ import org.gudy.azureus2.ui.swt.components.BufferedLabel;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.SWTThread;
/*      */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*      */ 
/*      */ public class Utils
/*      */ {
/*   93 */   private static final int DEFAULT_DPI = Constants.isOSX ? 72 : 96;
/*      */   
/*      */   public static final String GOOD_STRING = "(/|,jI~`gy";
/*      */   
/*   97 */   public static final boolean isGTK = org.eclipse.swt.SWT.getPlatform().equals("gtk");
/*      */   
/*   99 */   public static final boolean isGTK3 = (isGTK) && (System.getProperty("org.eclipse.swt.internal.gtk.version", "2").startsWith("3"));
/*      */   
/*      */ 
/*  102 */   public static final boolean isCarbon = org.eclipse.swt.SWT.getPlatform().equals("carbon");
/*      */   
/*  104 */   public static final boolean isCocoa = org.eclipse.swt.SWT.getPlatform().equals("cocoa");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  109 */   public static final boolean LAST_TABLECOLUMN_EXPANDS = isGTK;
/*      */   
/*      */ 
/*  112 */   public static final boolean TABLE_GRIDLINE_IS_ALTERNATING_COLOR = (isGTK) || (isCocoa);
/*      */   
/*      */   public static int BUTTON_MARGIN;
/*      */   
/*  116 */   public static int BUTTON_MINWIDTH = Constants.isOSX ? 90 : 70;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  123 */   private static final boolean DEBUG_SWTEXEC = System.getProperty("debug.swtexec", "0").equals("1");
/*      */   
/*      */ 
/*      */   private static ArrayList<Runnable> queue;
/*      */   
/*      */   private static AEDiagnosticsLogger diag_logger;
/*      */   
/*  130 */   private static Image[] shellIcons = null;
/*      */   
/*      */   private static Image icon128;
/*      */   
/*  134 */   private static final String[] shellIconNames = { "azureus", "azureus32", "azureus64", "azureus128" };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  141 */   public static final Rectangle EMPTY_RECT = new Rectangle(0, 0, 0, 0);
/*      */   
/*      */ 
/*      */ 
/*      */   private static int userMode;
/*      */   
/*      */ 
/*      */ 
/*      */   private static boolean isAZ2;
/*      */   
/*      */ 
/*      */   private static Set<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> quick_view_active;
/*      */   
/*      */ 
/*      */   private static org.gudy.azureus2.core3.util.TimerEventPeriodic quick_view_event;
/*      */   
/*      */ 
/*      */   private static Point dpi;
/*      */   
/*      */ 
/*      */   private static Set<String> pending_ext_urls;
/*      */   
/*      */ 
/*      */   private static AsyncDispatcher ext_url_dispatcher;
/*      */   
/*      */ 
/*      */   private static boolean i2p_install_active_for_url;
/*      */   
/*      */ 
/*      */   private static boolean browser_install_active_for_url;
/*      */   
/*      */ 
/*      */   private static boolean tb_installing;
/*      */   
/*      */ 
/*      */   private static boolean gotBrowserStyle;
/*      */   
/*      */ 
/*      */   private static int browserStyle;
/*      */   
/*      */ 
/*      */   private static Map truncatedTextCache;
/*      */   
/*      */ 
/*      */   private static ThreadPool tp;
/*      */   
/*      */ 
/*      */   private static Set<String> qv_exts;
/*      */   
/*      */ 
/*      */   private static int qv_max_bytes;
/*      */   
/*      */ 
/*      */ 
/*      */   public static void initialize(Display display)
/*      */   {
/*  197 */     getDPI();
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
/*      */   public static boolean isAZ2UI()
/*      */   {
/*  213 */     return isAZ2;
/*      */   }
/*      */   
/*      */   public static void disposeComposite(Composite composite, boolean disposeSelf) {
/*  217 */     if ((composite == null) || (composite.isDisposed()))
/*  218 */       return;
/*  219 */     Control[] controls = composite.getChildren();
/*  220 */     for (int i = 0; i < controls.length; i++) {
/*  221 */       Control control = controls[i];
/*  222 */       if ((control != null) && (!control.isDisposed())) {
/*  223 */         if ((control instanceof Composite)) {
/*  224 */           disposeComposite((Composite)control, true);
/*      */         }
/*      */         try {
/*  227 */           control.dispose();
/*      */         } catch (SWTException e) {
/*  229 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  234 */     if ((!composite.isDisposed()) && (disposeSelf))
/*      */       try {
/*  236 */         composite.dispose();
/*      */       } catch (SWTException e) {
/*  238 */         Debug.printStackTrace(e);
/*      */       }
/*      */   }
/*      */   
/*      */   public static void disposeComposite(Composite composite) {
/*  243 */     disposeComposite(composite, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void disposeSWTObjects(List disposeList)
/*      */   {
/*  252 */     disposeSWTObjects(disposeList.toArray());
/*  253 */     disposeList.clear();
/*      */   }
/*      */   
/*      */   public static void disposeSWTObjects(Object[] disposeList) {
/*  257 */     if (disposeList == null) {
/*  258 */       return;
/*      */     }
/*  260 */     for (int i = 0; i < disposeList.length; i++) {
/*      */       try {
/*  262 */         Object o = disposeList[i];
/*  263 */         if (((o instanceof Widget)) && (!((Widget)o).isDisposed())) {
/*  264 */           ((Widget)o).dispose();
/*  265 */         } else if (((o instanceof Resource)) && (!((Resource)o).isDisposed())) {
/*  266 */           ((Resource)o).dispose();
/*      */         }
/*      */       } catch (Exception e) {
/*  269 */         Debug.out("Warning: Disposal failed " + Debug.getCompressedStackTrace(e, 0, -1, true));
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
/*      */   public static void setTextLinkFromClipboard(Shell shell, Text url, boolean accept_magnets, boolean default_magnet)
/*      */   {
/*  287 */     String link = getLinkFromClipboard(shell.getDisplay(), accept_magnets, default_magnet);
/*  288 */     if (link != null) {
/*  289 */       url.setText(link);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String getLinkFromClipboard(Display display, boolean accept_magnets, boolean default_magnet)
/*      */   {
/*  301 */     Clipboard cb = new Clipboard(display);
/*  302 */     org.eclipse.swt.dnd.TextTransfer transfer = org.eclipse.swt.dnd.TextTransfer.getInstance();
/*      */     
/*  304 */     String data = (String)cb.getContents(transfer);
/*      */     
/*  306 */     String text = UrlUtils.parseTextForURL(data, accept_magnets);
/*  307 */     if (text == null) {
/*  308 */       return default_magnet ? "magnet:" : "http://";
/*      */     }
/*      */     
/*  311 */     return text;
/*      */   }
/*      */   
/*      */   public static void centreWindow(Shell shell) {
/*  315 */     centreWindow(shell, true);
/*      */   }
/*      */   
/*      */   public static void centreWindow(Shell shell, boolean shrink_if_needed)
/*      */   {
/*      */     Rectangle displayArea;
/*      */     try {
/*  322 */       displayArea = shell.getMonitor().getClientArea();
/*      */     } catch (NoSuchMethodError e) {
/*  324 */       displayArea = shell.getDisplay().getClientArea(); }
/*      */     Rectangle centerInArea;
/*      */     Rectangle centerInArea;
/*  327 */     if (shell.getParent() != null) {
/*  328 */       centerInArea = shell.getParent().getBounds();
/*      */     } else {
/*  330 */       centerInArea = displayArea;
/*      */     }
/*      */     
/*  333 */     Rectangle shellRect = shell.getBounds();
/*      */     
/*  335 */     if (shrink_if_needed) {
/*  336 */       if (shellRect.height > displayArea.height) {
/*  337 */         shellRect.height = displayArea.height;
/*      */       }
/*  339 */       if (shellRect.width > displayArea.width - 50) {
/*  340 */         shellRect.width = displayArea.width;
/*      */       }
/*      */     }
/*      */     
/*  344 */     centerInArea.x += (centerInArea.width - shellRect.width) / 2;
/*  345 */     centerInArea.y += (centerInArea.height - shellRect.height) / 2;
/*      */     
/*  347 */     shell.setBounds(shellRect);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void centerWindowRelativeTo(Shell window, Control control)
/*      */   {
/*  357 */     if ((control == null) || (control.isDisposed()) || (window == null) || (window.isDisposed())) {
/*  358 */       return;
/*      */     }
/*  360 */     Rectangle bounds = control.getBounds();
/*  361 */     Point shellSize = window.getSize();
/*  362 */     window.setLocation(bounds.x + bounds.width / 2 - shellSize.x / 2, bounds.y + bounds.height / 2 - shellSize.y / 2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static List<RGB> getCustomColors()
/*      */   {
/*  369 */     String custom_colours_str = COConfigurationManager.getStringParameter("color.parameter.custom.colors", "");
/*      */     
/*  371 */     String[] bits = custom_colours_str.split(";");
/*      */     
/*  373 */     List<RGB> custom_colours = new ArrayList();
/*      */     
/*  375 */     for (String bit : bits)
/*      */     {
/*  377 */       String[] x = bit.split(",");
/*      */       
/*  379 */       if (x.length == 3) {
/*      */         try
/*      */         {
/*  382 */           custom_colours.add(new RGB(Integer.parseInt(x[0]), Integer.parseInt(x[1]), Integer.parseInt(x[2])));
/*      */         }
/*      */         catch (Throwable f) {}
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  390 */     return custom_colours;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void updateCustomColors(RGB[] new_cc)
/*      */   {
/*  397 */     if (new_cc != null)
/*      */     {
/*  399 */       String custom_colours_str = "";
/*      */       
/*  401 */       for (RGB colour : new_cc)
/*      */       {
/*  403 */         custom_colours_str = custom_colours_str + (custom_colours_str.isEmpty() ? "" : ";") + colour.red + "," + colour.green + "," + colour.blue;
/*      */       }
/*      */       
/*  406 */       COConfigurationManager.setParameter("color.parameter.custom.colors", custom_colours_str);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static RGB showColorDialog(Composite parent, RGB existing)
/*      */   {
/*  415 */     Shell parent_shell = parent.getShell();
/*      */     
/*  417 */     return showColorDialog(parent_shell, existing);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static RGB showColorDialog(Shell parent_shell, RGB existing)
/*      */   {
/*  425 */     Shell centerShell = new Shell(parent_shell, 8);
/*      */     try
/*      */     {
/*      */       Rectangle displayArea;
/*      */       try
/*      */       {
/*  431 */         displayArea = parent_shell.getMonitor().getClientArea();
/*      */       }
/*      */       catch (NoSuchMethodError e)
/*      */       {
/*  435 */         displayArea = parent_shell.getDisplay().getClientArea();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  440 */       int x = displayArea.x + displayArea.width / 2 - 120;
/*  441 */       int y = displayArea.y + displayArea.height / 2 - 170;
/*      */       
/*  443 */       centerShell.setLocation(x, y);
/*      */       
/*  445 */       ColorDialog cd = new ColorDialog(centerShell);
/*      */       
/*  447 */       cd.setRGB(existing);
/*      */       
/*  449 */       List<RGB> custom_colours = getCustomColors();
/*      */       
/*  451 */       if (existing != null)
/*      */       {
/*  453 */         custom_colours.remove(existing);
/*      */       }
/*      */       
/*  456 */       cd.setRGBs((RGB[])custom_colours.toArray(new RGB[0]));
/*      */       
/*  458 */       RGB rgb = cd.open();
/*      */       
/*  460 */       if (rgb != null)
/*      */       {
/*  462 */         updateCustomColors(cd.getRGBs());
/*      */       }
/*      */       
/*  465 */       return rgb;
/*      */     }
/*      */     finally
/*      */     {
/*  469 */       centerShell.dispose();
/*      */     }
/*      */   }
/*      */   
/*      */   public static void createTorrentDropTarget(Composite composite, boolean bAllowShareAdd)
/*      */   {
/*      */     try {
/*  476 */       createDropTarget(composite, bAllowShareAdd, null);
/*      */     } catch (Exception e) {
/*  478 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void createURLDropTarget(Composite composite, Text url)
/*      */   {
/*      */     try
/*      */     {
/*  490 */       createDropTarget(composite, false, url);
/*      */     } catch (Exception e) {
/*  492 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static void createDropTarget(Composite composite, boolean bAllowShareAdd, Text url, org.eclipse.swt.dnd.DropTargetListener dropTargetListener)
/*      */   {
/*  500 */     org.eclipse.swt.dnd.Transfer[] transferList = { org.eclipse.swt.dnd.HTMLTransfer.getInstance(), URLTransfer.getInstance(), org.eclipse.swt.dnd.FileTransfer.getInstance(), org.eclipse.swt.dnd.TextTransfer.getInstance() };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  507 */     DropTarget dropTarget = new DropTarget(composite, 31);
/*      */     
/*  509 */     dropTarget.setTransfer(transferList);
/*  510 */     dropTarget.addDropListener(dropTargetListener);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  515 */     if (Constants.isWindows) {
/*  516 */       return;
/*      */     }
/*  518 */     Control[] children = composite.getChildren();
/*  519 */     for (int i = 0; i < children.length; i++) {
/*  520 */       Control control = children[i];
/*  521 */       if (!control.isDisposed()) {
/*  522 */         if ((control instanceof Composite)) {
/*  523 */           createDropTarget((Composite)control, bAllowShareAdd, url, dropTargetListener);
/*      */         }
/*      */         else {
/*  526 */           DropTarget dropTarget2 = new DropTarget(control, 31);
/*      */           
/*      */ 
/*  529 */           dropTarget2.setTransfer(transferList);
/*  530 */           dropTarget2.addDropListener(dropTargetListener);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static void createDropTarget(Composite composite, boolean bAllowShareAdd, Text url)
/*      */   {
/*  539 */     URLDropTarget target = new URLDropTarget(url, bAllowShareAdd);
/*  540 */     createDropTarget(composite, bAllowShareAdd, url, target);
/*      */   }
/*      */   
/*      */   private static class URLDropTarget
/*      */     extends org.eclipse.swt.dnd.DropTargetAdapter
/*      */   {
/*      */     private final Text url;
/*      */     private final boolean bAllowShareAdd;
/*      */     
/*      */     public URLDropTarget(Text url, boolean bAllowShareAdd)
/*      */     {
/*  551 */       this.url = url;
/*  552 */       this.bAllowShareAdd = bAllowShareAdd;
/*      */     }
/*      */     
/*      */     public void dropAccept(DropTargetEvent event) {
/*  556 */       event.currentDataType = URLTransfer.pickBestType(event.dataTypes, event.currentDataType);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void dragOver(DropTargetEvent event)
/*      */     {
/*  563 */       if ((event.detail != 16) && ((event.operations & event.detail) > 0))
/*      */       {
/*  565 */         return;
/*      */       }
/*  567 */       if ((event.operations & 0x4) > 0) {
/*  568 */         event.detail = 4;
/*  569 */       } else if ((event.operations & 0x10) > 0) {
/*  570 */         event.detail = 16;
/*  571 */       } else if ((event.operations & 0x1) > 0)
/*  572 */         event.detail = 1;
/*      */     }
/*      */     
/*      */     public void drop(DropTargetEvent event) {
/*  576 */       if ((this.url == null) || (this.url.isDisposed())) {
/*  577 */         org.gudy.azureus2.ui.swt.mainwindow.TorrentOpener.openDroppedTorrents(event, this.bAllowShareAdd);
/*      */       }
/*  579 */       else if ((event.data instanceof URLTransfer.URLType)) {
/*  580 */         if (((URLTransfer.URLType)event.data).linkURL != null)
/*  581 */           this.url.setText(((URLTransfer.URLType)event.data).linkURL);
/*  582 */       } else if ((event.data instanceof String)) {
/*  583 */         String sURL = UrlUtils.parseTextForURL((String)event.data, true);
/*  584 */         if (sURL != null) {
/*  585 */           this.url.setText(sURL);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static void alternateRowBackground(TableItem item)
/*      */   {
/*  593 */     if (TABLE_GRIDLINE_IS_ALTERNATING_COLOR) {
/*  594 */       if (!item.getParent().getLinesVisible())
/*  595 */         item.getParent().setLinesVisible(true);
/*  596 */       return;
/*      */     }
/*      */     
/*  599 */     if ((item == null) || (item.isDisposed()))
/*  600 */       return;
/*  601 */     Color[] colors = { item.getDisplay().getSystemColor(25), org.gudy.azureus2.ui.swt.mainwindow.Colors.colorAltRow };
/*      */     
/*      */ 
/*      */ 
/*  605 */     Color newColor = colors[(item.getParent().indexOf(item) % colors.length)];
/*  606 */     if (!item.getBackground().equals(newColor)) {
/*  607 */       item.setBackground(newColor);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void alternateTableBackground(Table table)
/*      */   {
/*  615 */     if ((table == null) || (table.isDisposed())) {
/*  616 */       return;
/*      */     }
/*  618 */     if (TABLE_GRIDLINE_IS_ALTERNATING_COLOR) {
/*  619 */       if (!table.getLinesVisible())
/*  620 */         table.setLinesVisible(true);
/*  621 */       return;
/*      */     }
/*      */     
/*  624 */     int iTopIndex = table.getTopIndex();
/*  625 */     if ((iTopIndex < 0) || ((iTopIndex == 0) && (table.getItemCount() == 0))) {
/*  626 */       return;
/*      */     }
/*  628 */     int iBottomIndex = getTableBottomIndex(table, iTopIndex);
/*      */     
/*  630 */     Color[] colors = { table.getDisplay().getSystemColor(25), org.gudy.azureus2.ui.swt.mainwindow.Colors.colorAltRow };
/*      */     
/*      */ 
/*      */ 
/*  634 */     int iFixedIndex = iTopIndex;
/*  635 */     for (int i = iTopIndex; i <= iBottomIndex; i++) {
/*  636 */       TableItem row = table.getItem(i);
/*      */       
/*  638 */       if (!row.isDisposed()) {
/*  639 */         Color newColor = colors[(iFixedIndex % colors.length)];
/*  640 */         iFixedIndex++;
/*  641 */         if (!row.getBackground().equals(newColor))
/*      */         {
/*  643 */           row.setBackground(newColor);
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
/*      */   public static void setMenuItemImage(org.eclipse.swt.widgets.MenuItem item, String repoKey)
/*      */   {
/*  659 */     if ((Constants.isOSX) || (repoKey == null)) {
/*  660 */       return;
/*      */     }
/*  662 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*  663 */     item.setImage(imageLoader.getImage(repoKey));
/*  664 */     item.addDisposeListener(new DisposeListener() {
/*      */       public void widgetDisposed(DisposeEvent e) {
/*  666 */         ImageLoader imageLoader = ImageLoader.getInstance();
/*  667 */         imageLoader.releaseImage(this.val$repoKey);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static void setMenuItemImage(final org.gudy.azureus2.plugins.ui.menus.MenuItem item, String repoKey) {
/*  673 */     if ((Constants.isOSX) || (repoKey == null)) {
/*  674 */       return;
/*      */     }
/*  676 */     if (isSWTThread()) {
/*  677 */       ImageLoader imageLoader = ImageLoader.getInstance();
/*  678 */       org.gudy.azureus2.plugins.ui.Graphic graphic = new org.gudy.azureus2.ui.swt.pluginsimpl.UISWTGraphicImpl(imageLoader.getImage(repoKey));
/*      */       
/*  680 */       item.setGraphic(graphic);
/*      */     } else {
/*  682 */       execSWTThread(new Runnable()
/*      */       {
/*      */         public void run()
/*      */         {
/*  686 */           ImageLoader imageLoader = ImageLoader.getInstance();
/*  687 */           org.gudy.azureus2.plugins.ui.Graphic graphic = new org.gudy.azureus2.ui.swt.pluginsimpl.UISWTGraphicImpl(imageLoader.getImage(this.val$repoKey));
/*      */           
/*  689 */           item.setGraphic(graphic);
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */   public static void setMenuItemImage(CLabel item, String repoKey) {
/*  696 */     if ((Constants.isOSX) || (repoKey == null)) {
/*  697 */       return;
/*      */     }
/*  699 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*  700 */     item.setImage(imageLoader.getImage(repoKey));
/*  701 */     item.addDisposeListener(new DisposeListener() {
/*      */       public void widgetDisposed(DisposeEvent e) {
/*  703 */         ImageLoader imageLoader = ImageLoader.getInstance();
/*  704 */         imageLoader.releaseImage(this.val$repoKey);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static void setMenuItemImage(org.eclipse.swt.widgets.MenuItem item, Image image) {
/*  710 */     if (!Constants.isOSX) {
/*  711 */       item.setImage(image);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setShellIcon(Shell shell)
/*      */   {
/*  721 */     if (Constants.isOSX)
/*      */     {
/*  723 */       return;
/*      */     }
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
/*      */     try
/*      */     {
/*  753 */       if (shellIcons == null)
/*      */       {
/*  755 */         ArrayList<Image> listShellIcons = new ArrayList(shellIconNames.length);
/*      */         
/*      */ 
/*  758 */         ImageLoader imageLoader = ImageLoader.getInstance();
/*  759 */         for (int i = 0; i < shellIconNames.length; i++)
/*      */         {
/*      */ 
/*  762 */           Image image = imageLoader.getImage(shellIconNames[i]);
/*  763 */           if (ImageLoader.isRealImage(image)) {
/*  764 */             listShellIcons.add(image);
/*      */           }
/*      */         }
/*  767 */         shellIcons = (Image[])listShellIcons.toArray(new Image[listShellIcons.size()]);
/*      */       }
/*      */       
/*  770 */       shell.setImages(shellIcons);
/*      */     }
/*      */     catch (NoSuchMethodError e) {}
/*      */   }
/*      */   
/*      */   public static Display getDisplay()
/*      */   {
/*  777 */     SWTThread swt = SWTThread.getInstance();
/*      */     
/*      */ 
/*  780 */     if (swt == null) {
/*      */       try {
/*  782 */         display = Display.getDefault();
/*  783 */         if (display == null) {
/*  784 */           System.err.println("SWT Thread not started yet!");
/*  785 */           return null;
/*      */         }
/*      */       }
/*      */       catch (Throwable t) {
/*  789 */         return null;
/*      */       }
/*      */     }
/*  792 */     if (swt.isTerminated()) {
/*  793 */       return null;
/*      */     }
/*  795 */     Display display = swt.getDisplay();
/*      */     
/*      */ 
/*  798 */     if ((display == null) || (display.isDisposed())) {
/*  799 */       return null;
/*      */     }
/*  801 */     return display;
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
/*      */   public static boolean execSWTThread(Runnable code, boolean async)
/*      */   {
/*  817 */     return execSWTThread(code, async ? -1 : -2);
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
/*      */ 
/*      */ 
/*      */   public static boolean execSWTThreadLater(int msLater, Runnable code)
/*      */   {
/*  835 */     return execSWTThread(code, msLater);
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
/*      */ 
/*      */ 
/*      */   public static boolean isSWTThread()
/*      */   {
/*  853 */     Display display = getDisplay();
/*  854 */     if (display == null) {
/*  855 */       return false;
/*      */     }
/*      */     
/*  858 */     return display.getThread() == Thread.currentThread();
/*      */   }
/*      */   
/*      */   private static boolean execSWTThread(final Runnable code, int msLater) {
/*  862 */     Display display = getDisplay();
/*  863 */     if ((display == null) || (code == null)) {
/*  864 */       return false;
/*      */     }
/*      */     
/*  867 */     boolean isSWTThread = display.getThread() == Thread.currentThread();
/*  868 */     if ((msLater < 0) && (isSWTThread)) {
/*  869 */       if (queue == null) {
/*  870 */         code.run();
/*      */       } else {
/*  872 */         long lStartTimeRun = SystemTime.getCurrentTime();
/*      */         
/*  874 */         code.run();
/*      */         
/*  876 */         long wait = SystemTime.getCurrentTime() - lStartTimeRun;
/*  877 */         if (wait > 700L) {
/*  878 */           diag_logger.log(SystemTime.getCurrentTime() + "] took " + wait + "ms to run " + Debug.getCompressedStackTrace(-5));
/*      */         }
/*      */       }
/*      */     }
/*  882 */     else if (msLater >= -1) {
/*      */       try {
/*  884 */         if (queue == null) {
/*  885 */           if (msLater <= 0) {
/*  886 */             display.asyncExec(code);
/*      */           }
/*  888 */           else if (isSWTThread) {
/*  889 */             display.timerExec(msLater, code);
/*      */           } else {
/*  891 */             SimpleTimer.addEvent("execSWTThreadLater", SystemTime.getOffsetTime(msLater), new TimerEventPerformer()
/*      */             {
/*      */               public void perform(TimerEvent event) {
/*  894 */                 if (!this.val$display.isDisposed()) {
/*  895 */                   this.val$display.asyncExec(code);
/*      */                 }
/*      */               }
/*      */             });
/*      */           }
/*      */         }
/*      */         else {
/*  902 */           queue.add(code);
/*      */           
/*  904 */           diag_logger.log(SystemTime.getCurrentTime() + "] + Q. size= " + queue.size() + ";in=" + msLater + "; add " + code + " via " + Debug.getCompressedStackTrace(-5));
/*      */           
/*      */ 
/*  907 */           long lStart = SystemTime.getCurrentTime();
/*      */           
/*  909 */           final Display fDisplay = display;
/*  910 */           final AERunnable runnableWrapper = new AERunnable() {
/*      */             public void runSupport() {
/*  912 */               long wait = SystemTime.getCurrentTime() - this.val$lStart - code;
/*  913 */               if (wait > 700L) {
/*  914 */                 Utils.diag_logger.log(SystemTime.getCurrentTime() + "] took " + wait + "ms before SWT ran async code " + fDisplay);
/*      */               }
/*      */               
/*  917 */               long lStartTimeRun = SystemTime.getCurrentTime();
/*      */               try
/*      */               {
/*  920 */                 if (this.val$fDisplay.isDisposed()) {
/*  921 */                   Debug.out("Display disposed while trying to execSWTThread " + fDisplay);
/*      */                   
/*      */                   try
/*      */                   {
/*  925 */                     fDisplay.run();
/*      */                   } catch (SWTException e) {
/*  927 */                     Debug.out("Error while execSWTThread w/disposed Display", e);
/*      */                   }
/*      */                 } else {
/*  930 */                   fDisplay.run();
/*      */                 }
/*      */               } finally { long runTIme;
/*  933 */                 long runTIme = SystemTime.getCurrentTime() - lStartTimeRun;
/*  934 */                 if (runTIme > 500L) {
/*  935 */                   Utils.diag_logger.log(SystemTime.getCurrentTime() + "] took " + runTIme + "ms to run " + fDisplay);
/*      */                 }
/*      */                 
/*      */ 
/*  939 */                 Utils.queue.remove(fDisplay);
/*      */                 
/*  941 */                 if (runTIme > 10L) {
/*  942 */                   Utils.diag_logger.log(SystemTime.getCurrentTime() + "] - Q. size=" + Utils.queue.size() + ";wait:" + wait + "ms;run:" + runTIme + "ms " + fDisplay);
/*      */                 }
/*      */                 else
/*      */                 {
/*  946 */                   Utils.diag_logger.log(SystemTime.getCurrentTime() + "] - Q. size=" + Utils.queue.size() + ";wait:" + wait + "ms;run:" + runTIme + "ms");
/*      */                 }
/*      */               }
/*      */             }
/*      */           };
/*      */           
/*      */ 
/*  953 */           if (msLater <= 0) {
/*  954 */             display.asyncExec(runnableWrapper);
/*      */           }
/*  956 */           else if (isSWTThread) {
/*  957 */             display.timerExec(msLater, runnableWrapper);
/*      */           } else {
/*  959 */             SimpleTimer.addEvent("execSWTThreadLater", SystemTime.getOffsetTime(msLater), new TimerEventPerformer()
/*      */             {
/*      */               public void perform(TimerEvent event) {
/*  962 */                 if (!this.val$display.isDisposed()) {
/*  963 */                   this.val$display.asyncExec(runnableWrapper);
/*      */                 }
/*      */                 
/*      */               }
/*      */               
/*      */             });
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (NullPointerException e)
/*      */       {
/*  974 */         if (Constants.isCVSVersion()) {
/*  975 */           Debug.out(e);
/*      */         }
/*      */         
/*  978 */         return false;
/*      */       }
/*      */     } else {
/*  981 */       display.syncExec(code);
/*      */     }
/*      */     
/*  984 */     return true;
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
/*      */   public static boolean execSWTThread(Runnable code)
/*      */   {
/*  999 */     return execSWTThread(code, -1);
/*      */   }
/*      */   
/*      */   public static boolean isThisThreadSWT() {
/* 1003 */     SWTThread swt = SWTThread.getInstance();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1009 */     Display display = (swt != null) || (swt == null) ? Display.getCurrent() : swt.getDisplay();
/*      */     
/* 1011 */     if (display == null) {
/* 1012 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 1019 */       display.getWarnings();
/*      */     } catch (SWTException e) {
/* 1021 */       return false;
/*      */     }
/*      */     
/* 1024 */     return display.getThread() == Thread.currentThread();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int getTableBottomIndex(Table table, int iTopIndex)
/*      */   {
/* 1035 */     Object lastBottomIndex = table.getData("lastBottomIndex");
/* 1036 */     if ((lastBottomIndex instanceof Number)) {
/* 1037 */       return ((Number)lastBottomIndex).intValue();
/*      */     }
/*      */     
/* 1040 */     int columnCount = table.getColumnCount();
/* 1041 */     if (columnCount == 0) {
/* 1042 */       return -1;
/*      */     }
/* 1044 */     int xPos = table.getColumn(0).getWidth() - 1;
/* 1045 */     if (columnCount > 1) {
/* 1046 */       xPos += table.getColumn(1).getWidth();
/*      */     }
/*      */     
/* 1049 */     Rectangle clientArea = table.getClientArea();
/* 1050 */     TableItem bottomItem = table.getItem(new Point(xPos, clientArea.y + clientArea.height - 2));
/*      */     
/* 1052 */     if (bottomItem != null) {
/* 1053 */       return table.indexOf(bottomItem);
/*      */     }
/* 1055 */     return table.getItemCount() - 1;
/*      */   }
/*      */   
/*      */   public static void launch(org.gudy.azureus2.core3.disk.DiskManagerFileInfo fileInfo)
/*      */   {
/* 1060 */     LaunchManager launch_manager = LaunchManager.getManager();
/*      */     
/* 1062 */     com.aelitis.azureus.core.util.LaunchManager.LaunchTarget target = launch_manager.createTarget(fileInfo);
/*      */     
/* 1064 */     launch_manager.launchRequest(target, new com.aelitis.azureus.core.util.LaunchManager.LaunchAction()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void actionAllowed()
/*      */       {
/*      */ 
/* 1071 */         Utils.execSWTThread(new Runnable()
/*      */         {
/*      */ 
/*      */           public void run()
/*      */           {
/*      */ 
/* 1077 */             Utils.launch(Utils.10.this.val$fileInfo.getFile(true).toString());
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void actionDenied(Throwable reason)
/*      */       {
/* 1086 */         Debug.out("Launch request denied", reason);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static void launch(URL url)
/*      */   {
/* 1093 */     launch(url.toExternalForm());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void launch(String sFile)
/*      */   {
/* 1100 */     launch(sFile, false);
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
/*      */   public static void launch(String sFileOriginal, boolean sync)
/*      */   {
/* 1114 */     launch(sFileOriginal, sync, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void launch(String sFileOriginal, boolean sync, boolean force_url)
/*      */   {
/* 1123 */     launch(sFileOriginal, sync, force_url, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void launch(String sFileOriginal, boolean sync, boolean force_url, boolean force_anon)
/*      */   {
/* 1133 */     String sFileModified = sFileOriginal;
/*      */     
/* 1135 */     if ((sFileModified == null) || (sFileModified.trim().length() == 0)) {
/* 1136 */       return;
/*      */     }
/*      */     
/* 1139 */     if (!force_url) {
/* 1140 */       if ((!Constants.isWindows) && (new File(sFileModified).isDirectory())) {
/* 1141 */         PlatformManager mgr = PlatformManagerFactory.getPlatformManager();
/* 1142 */         if (mgr.hasCapability(org.gudy.azureus2.platform.PlatformManagerCapabilities.ShowFileInBrowser)) {
/*      */           try {
/* 1144 */             PlatformManagerFactory.getPlatformManager().showFile(sFileModified);
/* 1145 */             return;
/*      */           }
/*      */           catch (org.gudy.azureus2.plugins.platform.PlatformManagerException e) {}
/*      */         }
/*      */       }
/*      */       
/* 1151 */       sFileModified = sFileModified.replaceAll("&vzemb=1", "");
/*      */       
/* 1153 */       String exe = getExplicitLauncher(sFileModified);
/*      */       
/* 1155 */       if (exe != null)
/*      */       {
/* 1157 */         File file = new File(sFileModified);
/*      */         try
/*      */         {
/* 1160 */           System.out.println("Launching " + sFileModified + " with " + exe);
/*      */           
/* 1162 */           if (Constants.isWindows)
/*      */           {
/*      */             try
/*      */             {
/*      */ 
/* 1167 */               PlatformManagerFactory.getPlatformManager().createProcess(exe + " \"" + sFileModified + "\"", false);
/*      */               
/* 1169 */               return;
/*      */             }
/*      */             catch (Throwable e) {}
/*      */           }
/*      */           
/*      */ 
/* 1175 */           ProcessBuilder pb = com.aelitis.azureus.core.util.GeneralUtils.createProcessBuilder(file.getParentFile(), new String[] { exe, file.getName() }, null);
/*      */           
/* 1177 */           pb.start();
/*      */           
/* 1179 */           return;
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1183 */           Debug.out("Launch failed", e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1188 */     String lc_sFile = sFileModified.toLowerCase(java.util.Locale.US);
/*      */     
/* 1190 */     if (lc_sFile.startsWith("tor:"))
/*      */     {
/* 1192 */       force_anon = true;
/*      */       
/* 1194 */       lc_sFile = lc_sFile.substring(4);
/*      */       
/* 1196 */       sFileModified = lc_sFile;
/*      */     }
/*      */     
/* 1199 */     if ((lc_sFile.startsWith("http:")) || (lc_sFile.startsWith("https:")))
/*      */     {
/*      */       boolean use_plugins;
/*      */       String net_type;
/*      */       String eb_choice;
/*      */       boolean use_plugins;
/* 1205 */       if (force_anon)
/*      */       {
/* 1207 */         String net_type = "Tor";
/* 1208 */         String eb_choice = "plugin";
/* 1209 */         use_plugins = true;
/*      */       }
/*      */       else
/*      */       {
/* 1213 */         net_type = "Public";
/*      */         try
/*      */         {
/* 1216 */           net_type = org.gudy.azureus2.core3.util.AENetworkClassifier.categoriseAddress(new URL(sFileModified).getHost());
/*      */         }
/*      */         catch (Throwable e) {}
/*      */         
/*      */ 
/*      */ 
/* 1222 */         eb_choice = COConfigurationManager.getStringParameter("browser.external.id", "system");
/*      */         
/* 1224 */         use_plugins = COConfigurationManager.getBooleanParameter("browser.external.non.pub", true);
/*      */         
/* 1226 */         if ((net_type != "Public") && (use_plugins))
/*      */         {
/* 1228 */           eb_choice = "plugin";
/*      */         }
/*      */       }
/*      */       
/* 1232 */       if (!eb_choice.equals("system"))
/*      */       {
/* 1234 */         if (eb_choice.equals("manual"))
/*      */         {
/* 1236 */           String browser_exe = COConfigurationManager.getStringParameter("browser.external.prog", "");
/*      */           
/* 1238 */           File bf = new File(browser_exe);
/*      */           
/* 1240 */           if (bf.exists()) {
/*      */             try
/*      */             {
/* 1243 */               proc = Runtime.getRuntime().exec(new String[] { bf.getAbsolutePath(), sFileModified });
/*      */             }
/*      */             catch (Throwable e) {
/*      */               Process proc;
/* 1247 */               Debug.out(e);
/*      */             }
/*      */             
/*      */           } else {
/* 1251 */             Debug.out("Can't launch '" + sFileModified + "' as manual browser '" + bf + " ' doesn't exist");
/*      */           }
/*      */           
/* 1254 */           return;
/*      */         }
/*      */         
/*      */ 
/* 1258 */         handlePluginLaunch(eb_choice, net_type, use_plugins, sFileOriginal, sFileModified, sync, force_url, force_anon);
/*      */         
/* 1260 */         return;
/*      */       }
/* 1262 */     } else if (lc_sFile.startsWith("chat:"))
/*      */     {
/* 1264 */       String plug_uri = "azplug:?id=azbuddy&arg=" + UrlUtils.encode(sFileModified);
/*      */       try
/*      */       {
/* 1267 */         URLConnection connection = new URL(plug_uri).openConnection();
/*      */         
/* 1269 */         connection.connect();
/*      */         
/* 1271 */         String res = org.gudy.azureus2.core3.util.FileUtil.readInputStreamAsString(connection.getInputStream(), 2048);
/*      */         
/* 1273 */         return;
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*      */ 
/* 1279 */     boolean launched = Program.launch(sFileModified);
/*      */     
/* 1281 */     if ((!launched) && (Constants.isUnix))
/*      */     {
/* 1283 */       sFileModified = sFileModified.replaceAll(" ", "\\ ");
/*      */       
/* 1285 */       if (!Program.launch("xdg-open " + sFileModified))
/*      */       {
/* 1287 */         if (!Program.launch("htmlview " + sFileModified))
/*      */         {
/* 1289 */           Debug.out("Failed to launch '" + sFileModified + "'");
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
/*      */   private static void handlePluginLaunch(String eb_choice, String net_type, boolean use_plugins, String sFileOriginal, final String sFileModified, final boolean sync, final boolean force_url, final boolean force_anon)
/*      */   {
/* 1306 */     org.gudy.azureus2.plugins.PluginManager pm = com.aelitis.azureus.core.AzureusCoreFactory.getSingleton().getPluginManager();
/*      */     
/* 1308 */     if (net_type == "I2P")
/*      */     {
/* 1310 */       if (pm.getPluginInterfaceByID("azneti2phelper") == null)
/*      */       {
/*      */         boolean try_it;
/*      */         
/* 1314 */         synchronized (pending_ext_urls)
/*      */         {
/* 1316 */           try_it = !i2p_install_active_for_url;
/*      */           
/* 1318 */           i2p_install_active_for_url = true;
/*      */         }
/*      */         
/* 1321 */         if (try_it)
/*      */         {
/* 1323 */           ext_url_dispatcher.dispatch(new AERunnable()
/*      */           {
/*      */ 
/*      */             public void runSupport()
/*      */             {
/*      */ 
/* 1329 */               boolean installing = false;
/*      */               try
/*      */               {
/* 1332 */                 final boolean[] install_outcome = { false };
/*      */                 
/* 1334 */                 installing = com.aelitis.azureus.plugins.I2PHelpers.installI2PHelper("azneti2phelper.install", install_outcome, new Runnable()
/*      */                 {
/*      */ 
/*      */ 
/*      */                   public void run()
/*      */                   {
/*      */ 
/*      */                     try
/*      */                     {
/*      */ 
/* 1344 */                       if (install_outcome[0] != 0)
/*      */                       {
/* 1346 */                         Utils.launch(Utils.11.this.val$sFileOriginal, Utils.11.this.val$sync, Utils.11.this.val$force_url, Utils.11.this.val$force_anon);
/*      */                       }
/*      */                     }
/*      */                     finally {
/* 1350 */                       synchronized (Utils.pending_ext_urls)
/*      */                       {
/* 1352 */                         Utils.access$502(false);
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 });
/*      */               }
/*      */               finally
/*      */               {
/* 1360 */                 if (!installing)
/*      */                 {
/* 1362 */                   synchronized (Utils.pending_ext_urls)
/*      */                   {
/* 1364 */                     Utils.access$502(false);
/*      */                   }
/*      */                   
/*      */                 }
/*      */                 
/*      */               }
/*      */             }
/*      */           });
/*      */         } else {
/* 1373 */           Debug.out("I2P installation already active");
/*      */         }
/*      */         
/* 1376 */         return;
/*      */       }
/*      */     }
/*      */     
/* 1380 */     List<PluginInterface> pis = pm.getPluginsWithMethod("launchURL", new Class[] { URL.class, Boolean.TYPE, Runnable.class });
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1385 */     boolean found = false;
/*      */     
/* 1387 */     for (PluginInterface pi : pis)
/*      */     {
/* 1389 */       String id = "plugin:" + pi.getPluginID();
/*      */       
/* 1391 */       if ((eb_choice.equals("plugin")) || (id.equals(eb_choice)))
/*      */       {
/* 1393 */         found = true;
/*      */         
/* 1395 */         synchronized (pending_ext_urls)
/*      */         {
/* 1397 */           if (pending_ext_urls.contains(sFileModified))
/*      */           {
/* 1399 */             Debug.outNoStack("Already queued browser request for '" + sFileModified + "' - ignoring");
/*      */             
/* 1401 */             return;
/*      */           }
/*      */           
/* 1404 */           pending_ext_urls.add(sFileModified);
/*      */         }
/*      */         
/* 1407 */         AERunnable launch = new AERunnable()
/*      */         {
/*      */ 
/*      */           public void runSupport()
/*      */           {
/*      */             try
/*      */             {
/* 1414 */               final AESemaphore sem = new AESemaphore("wait");
/*      */               
/* 1416 */               this.val$pi.getIPC().invoke("launchURL", new Object[] { new URL(sFileModified), Boolean.valueOf(false), new Runnable()
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */                 public void run()
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/* 1426 */                   sem.release();
/*      */                 }
/*      */               } });
/*      */               
/* 1430 */               if (sem.reserve(30000L)) {}
/*      */ 
/*      */ 
/*      */ 
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*      */ 
/*      */ 
/* 1439 */               Debug.out(e);
/*      */             }
/*      */             finally
/*      */             {
/* 1443 */               synchronized (Utils.pending_ext_urls)
/*      */               {
/* 1445 */                 Utils.pending_ext_urls.remove(sFileModified);
/*      */               }
/*      */             }
/*      */           }
/*      */         };
/*      */         
/* 1451 */         if (sync)
/*      */         {
/* 1453 */           launch.runSupport();
/*      */         }
/*      */         else
/*      */         {
/* 1457 */           ext_url_dispatcher.dispatch(launch);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1462 */     if (!found)
/*      */     {
/* 1464 */       if ((net_type != "Public") && (use_plugins))
/*      */       {
/*      */         boolean try_it;
/*      */         
/* 1468 */         synchronized (pending_ext_urls)
/*      */         {
/* 1470 */           try_it = !browser_install_active_for_url;
/*      */           
/* 1472 */           browser_install_active_for_url = true;
/*      */         }
/*      */         
/* 1475 */         if (try_it)
/*      */         {
/* 1477 */           ext_url_dispatcher.dispatch(new AERunnable()
/*      */           {
/*      */ 
/*      */             public void runSupport()
/*      */             {
/*      */ 
/* 1483 */               boolean installing = false;
/*      */               try
/*      */               {
/* 1486 */                 final boolean[] install_outcome = { false };
/*      */                 
/* 1488 */                 installing = Utils.installTorBrowser("aznettorbrowser.install", install_outcome, new Runnable()
/*      */                 {
/*      */ 
/*      */ 
/*      */                   public void run()
/*      */                   {
/*      */ 
/*      */                     try
/*      */                     {
/*      */ 
/* 1498 */                       if (install_outcome[0] != 0)
/*      */                       {
/* 1500 */                         Utils.launch(Utils.13.this.val$sFileOriginal, Utils.13.this.val$sync, Utils.13.this.val$force_url, Utils.13.this.val$force_anon);
/*      */                       }
/*      */                     }
/*      */                     finally {
/* 1504 */                       synchronized (Utils.pending_ext_urls)
/*      */                       {
/* 1506 */                         Utils.access$602(false);
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 });
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/* 1514 */                 Debug.out(e);
/*      */               }
/*      */               finally
/*      */               {
/* 1518 */                 if (!installing)
/*      */                 {
/* 1520 */                   synchronized (Utils.pending_ext_urls)
/*      */                   {
/* 1522 */                     Utils.access$602(false);
/*      */                   }
/*      */                   
/*      */                 }
/*      */               }
/*      */             }
/*      */           });
/*      */         } else {
/* 1530 */           Debug.out("Browser installation already active");
/*      */         }
/*      */         
/* 1533 */         return;
/*      */       }
/*      */     }
/*      */     
/* 1537 */     if ((!found) && (!eb_choice.equals("plugin")))
/*      */     {
/* 1539 */       Debug.out("Failed to find external URL launcher plugin with id '" + eb_choice + "'");
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static boolean installTorBrowser(String remember_id, final boolean[] install_outcome, Runnable callback)
/*      */   {
/* 1564 */     synchronized (pending_ext_urls)
/*      */     {
/* 1566 */       if (tb_installing)
/*      */       {
/* 1568 */         Debug.out("Tor Browser already installing");
/*      */         
/* 1570 */         return false;
/*      */       }
/*      */       
/* 1573 */       tb_installing = true;
/*      */     }
/*      */     
/* 1576 */     boolean installing = false;
/*      */     try
/*      */     {
/* 1579 */       UIFunctions uif = com.aelitis.azureus.ui.UIFunctionsManager.getUIFunctions();
/*      */       
/* 1581 */       if (uif == null)
/*      */       {
/* 1583 */         Debug.out("UIFunctions unavailable - can't install plugin");
/*      */         
/* 1585 */         return false;
/*      */       }
/*      */       
/* 1588 */       String title = MessageText.getString("aznettorbrowser.install");
/*      */       
/* 1590 */       String text = MessageText.getString("aznettorbrowser.install.text");
/*      */       
/* 1592 */       UIFunctionsUserPrompter prompter = uif.getUserPrompter(title, text, new String[] { MessageText.getString("Button.yes"), MessageText.getString("Button.no") }, 0);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1597 */       if (remember_id != null)
/*      */       {
/* 1599 */         prompter.setRemember(remember_id, false, MessageText.getString("MessageBoxWindow.nomoreprompting"));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1605 */       prompter.setAutoCloseInMS(0);
/*      */       
/* 1607 */       prompter.open(null);
/*      */       
/* 1609 */       boolean install = prompter.waitUntilClosed() == 0;
/*      */       
/* 1611 */       if (install)
/*      */       {
/* 1613 */         uif.installPlugin("aznettorbrowser", "aznettorbrowser.install", new com.aelitis.azureus.ui.UIFunctions.actionListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void actionComplete(Object result)
/*      */           {
/*      */ 
/*      */             try
/*      */             {
/*      */ 
/* 1623 */               if (this.val$callback != null)
/*      */               {
/* 1625 */                 if ((result instanceof Boolean))
/*      */                 {
/* 1627 */                   install_outcome[0] = ((Boolean)result).booleanValue();
/*      */                 }
/*      */                 
/* 1630 */                 this.val$callback.run();
/*      */               }
/*      */             }
/*      */             finally {
/* 1634 */               synchronized (Utils.pending_ext_urls)
/*      */               {
/* 1636 */                 Utils.access$802(false);
/*      */               }
/*      */               
/*      */             }
/*      */           }
/* 1641 */         });
/* 1642 */         installing = true;
/*      */       }
/*      */       else
/*      */       {
/* 1646 */         Debug.out("Tor Browser install declined (either user reply or auto-remembered)");
/*      */       }
/*      */       
/* 1649 */       return install;
/*      */     }
/*      */     finally
/*      */     {
/* 1653 */       if (!installing)
/*      */       {
/* 1655 */         synchronized (pending_ext_urls)
/*      */         {
/* 1657 */           tb_installing = false;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static String getExplicitLauncher(String file)
/*      */   {
/* 1667 */     int pos = file.lastIndexOf(".");
/*      */     
/* 1669 */     if (pos >= 0)
/*      */     {
/* 1671 */       String ext = file.substring(pos + 1).toLowerCase().trim();
/*      */       
/*      */ 
/*      */ 
/* 1675 */       int q_pos = ext.indexOf("?");
/*      */       
/* 1677 */       if (q_pos > 0)
/*      */       {
/* 1679 */         ext = ext.substring(0, q_pos);
/*      */       }
/*      */       
/* 1682 */       for (int i = 0; i < 10; i++)
/*      */       {
/* 1684 */         String exts = COConfigurationManager.getStringParameter("Table.lh" + i + ".exts", "").trim();
/* 1685 */         String exe = COConfigurationManager.getStringParameter("Table.lh" + i + ".prog", "").trim();
/*      */         
/* 1687 */         if ((exts.length() > 0) && (exe.length() > 0) && (new File(exe).exists()))
/*      */         {
/* 1689 */           exts = "," + exts.toLowerCase();
/*      */           
/* 1691 */           exts = exts.replaceAll("\\.", ",");
/* 1692 */           exts = exts.replaceAll(";", ",");
/* 1693 */           exts = exts.replaceAll(" ", ",");
/*      */           
/* 1695 */           exts = exts.replaceAll("[,]+", ",");
/*      */           
/* 1697 */           if (exts.contains("," + ext))
/*      */           {
/* 1699 */             return exe;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1705 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setCheckedInSetData(TableItem item, boolean checked)
/*      */   {
/* 1717 */     item.setChecked(checked);
/*      */     
/* 1719 */     if ((Constants.isWindowsXP) || (isGTK)) {
/* 1720 */       Rectangle r = item.getBounds(0);
/* 1721 */       Table table = item.getParent();
/* 1722 */       Rectangle rTable = table.getClientArea();
/*      */       
/* 1724 */       table.redraw(0, r.y, rTable.width, r.height, true);
/*      */     }
/*      */   }
/*      */   
/*      */   public static boolean linkShellMetricsToConfig(Shell shell, String sConfigPrefix)
/*      */   {
/* 1730 */     boolean isMaximized = COConfigurationManager.getBooleanParameter(sConfigPrefix + ".maximized");
/*      */     
/*      */ 
/* 1733 */     if (!isMaximized) {
/* 1734 */       shell.setMaximized(false);
/*      */     }
/*      */     
/* 1737 */     String windowRectangle = COConfigurationManager.getStringParameter(sConfigPrefix + ".rectangle", null);
/*      */     
/* 1739 */     boolean bDidResize = false;
/* 1740 */     if (null != windowRectangle) {
/* 1741 */       int i = 0;
/* 1742 */       int[] values = new int[4];
/* 1743 */       StringTokenizer st = new StringTokenizer(windowRectangle, ",");
/*      */       try {
/* 1745 */         while ((st.hasMoreTokens()) && (i < 4)) {
/* 1746 */           values[(i++)] = Integer.valueOf(st.nextToken()).intValue();
/*      */         }
/* 1748 */         if (i == 4) {
/* 1749 */           Rectangle shellBounds = new Rectangle(values[0], values[1], values[2], values[3]);
/*      */           
/* 1751 */           if ((shellBounds.width > 100) && (shellBounds.height > 50)) {
/* 1752 */             shell.setBounds(shellBounds);
/* 1753 */             verifyShellRect(shell, true);
/* 1754 */             bDidResize = true;
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Exception e) {}
/*      */     }
/*      */     
/* 1761 */     if (isMaximized) {
/* 1762 */       shell.setMaximized(isMaximized);
/*      */     }
/*      */     
/* 1765 */     new ShellMetricsResizeListener(shell, sConfigPrefix);
/*      */     
/* 1767 */     return bDidResize;
/*      */   }
/*      */   
/*      */   private static class TruncatedTextResult { String text;
/*      */     int maxWidth; }
/*      */   
/* 1773 */   private static class ShellMetricsResizeListener implements Listener { private int state = -1;
/*      */     
/*      */     private String sConfigPrefix;
/*      */     
/* 1777 */     private Rectangle bounds = null;
/*      */     
/*      */     ShellMetricsResizeListener(Shell shell, String sConfigPrefix) {
/* 1780 */       this.sConfigPrefix = sConfigPrefix;
/* 1781 */       this.state = calcState(shell);
/* 1782 */       if (this.state == 0) {
/* 1783 */         this.bounds = shell.getBounds();
/*      */       }
/* 1785 */       shell.addListener(11, this);
/* 1786 */       shell.addListener(10, this);
/* 1787 */       shell.addListener(12, this);
/*      */     }
/*      */     
/*      */     private int calcState(Shell shell) {
/* 1791 */       return (shell.getMaximized()) && (!Utils.isCarbon) ? 1024 : shell.getMinimized() ? 128 : 0;
/*      */     }
/*      */     
/*      */     private void saveMetrics()
/*      */     {
/* 1796 */       COConfigurationManager.setParameter(this.sConfigPrefix + ".maximized", this.state == 1024);
/*      */       
/*      */ 
/* 1799 */       if (this.bounds == null) {
/* 1800 */         return;
/*      */       }
/* 1802 */       COConfigurationManager.setParameter(this.sConfigPrefix + ".rectangle", this.bounds.x + "," + this.bounds.y + "," + this.bounds.width + "," + this.bounds.height);
/*      */       
/*      */ 
/* 1805 */       COConfigurationManager.save();
/*      */     }
/*      */     
/*      */     public void handleEvent(Event event) {
/* 1809 */       Shell shell = (Shell)event.widget;
/* 1810 */       this.state = calcState(shell);
/*      */       
/* 1812 */       if ((event.type != 12) && (this.state == 0)) {
/* 1813 */         this.bounds = shell.getBounds();
/*      */       }
/* 1815 */       if (event.type == 12) {
/* 1816 */         saveMetrics();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static GridData setGridData(Composite composite, int gridStyle, Control ctrlBestSize, int maxHeight) {
/* 1822 */     GridData gridData = new GridData(gridStyle);
/* 1823 */     gridData.heightHint = ctrlBestSize.computeSize(-1, -1).y;
/* 1824 */     if ((gridData.heightHint > maxHeight) && (maxHeight > 0))
/* 1825 */       gridData.heightHint = maxHeight;
/* 1826 */     composite.setLayoutData(gridData);
/*      */     
/* 1828 */     return gridData;
/*      */   }
/*      */   
/*      */   public static FormData getFilledFormData() {
/* 1832 */     FormData formData = new FormData();
/* 1833 */     formData.top = new FormAttachment(0, 0);
/* 1834 */     formData.left = new FormAttachment(0, 0);
/* 1835 */     formData.right = new FormAttachment(100, 0);
/* 1836 */     formData.bottom = new FormAttachment(100, 0);
/*      */     
/* 1838 */     return formData;
/*      */   }
/*      */   
/*      */   public static int pixelsToPoint(int pixels, int dpi) {
/* 1842 */     int ret = (int)Math.round(pixels * 72.0D / dpi);
/* 1843 */     return ret;
/*      */   }
/*      */   
/*      */   private static int pixelsToPoint(double pixels, int dpi) {
/* 1847 */     int ret = (int)Math.round(pixels * 72.0D / dpi);
/* 1848 */     return ret;
/*      */   }
/*      */   
/*      */   private static boolean drawImage(GC gc, Image image, Rectangle dstRect, Rectangle clipping, int hOffset, int vOffset, boolean clearArea)
/*      */   {
/* 1853 */     return drawImage(gc, image, new Point(0, 0), dstRect, clipping, hOffset, vOffset, clearArea);
/*      */   }
/*      */   
/*      */ 
/*      */   private static boolean drawImage(GC gc, Image image, Rectangle dstRect, Rectangle clipping, int hOffset, int vOffset)
/*      */   {
/* 1859 */     return drawImage(gc, image, new Point(0, 0), dstRect, clipping, hOffset, vOffset, false);
/*      */   }
/*      */   
/*      */ 
/*      */   public static boolean drawImage(GC gc, Image image, Point srcStart, Rectangle dstRect, Rectangle clipping, int hOffset, int vOffset, boolean clearArea)
/*      */   {
/*      */     Rectangle srcRect;
/*      */     
/*      */     Point dstAdj;
/*      */     Rectangle srcRect;
/* 1869 */     if (clipping == null) {
/* 1870 */       Point dstAdj = new Point(0, 0);
/* 1871 */       srcRect = new Rectangle(srcStart.x, srcStart.y, dstRect.width, dstRect.height);
/*      */     }
/*      */     else {
/* 1874 */       if (!dstRect.intersects(clipping)) {
/* 1875 */         return false;
/*      */       }
/*      */       
/* 1878 */       dstAdj = new Point(Math.max(0, clipping.x - dstRect.x), Math.max(0, clipping.y - dstRect.y));
/*      */       
/*      */ 
/* 1881 */       srcRect = new Rectangle(0, 0, 0, 0);
/* 1882 */       srcRect.x = (srcStart.x + dstAdj.x);
/* 1883 */       srcRect.y = (srcStart.y + dstAdj.y);
/* 1884 */       srcRect.width = Math.min(dstRect.width - dstAdj.x, clipping.x + clipping.width - dstRect.x);
/*      */       
/* 1886 */       srcRect.height = Math.min(dstRect.height - dstAdj.y, clipping.y + clipping.height - dstRect.y);
/*      */     }
/*      */     
/*      */ 
/* 1890 */     if (!srcRect.isEmpty()) {
/*      */       try {
/* 1892 */         if (clearArea) {
/* 1893 */           gc.fillRectangle(dstRect.x + dstAdj.x + hOffset, dstRect.y + dstAdj.y + vOffset, srcRect.width, srcRect.height);
/*      */         }
/*      */         
/* 1896 */         gc.drawImage(image, srcRect.x, srcRect.y, srcRect.width, srcRect.height, dstRect.x + dstAdj.x + hOffset, dstRect.y + dstAdj.y + vOffset, srcRect.width, srcRect.height);
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/* 1900 */         System.out.println("drawImage: " + e.getMessage() + ": " + image + ", " + srcRect + ", " + (dstRect.x + dstAdj.y + hOffset) + "," + (dstRect.y + dstAdj.y + vOffset) + "," + srcRect.width + "," + srcRect.height + "; imageBounds = " + image.getBounds());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1907 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Control findChild(Composite comp, int x, int y)
/*      */   {
/* 1916 */     Rectangle comp_bounds = comp.getBounds();
/*      */     
/* 1918 */     if ((comp.isVisible()) && (comp_bounds.contains(x, y)))
/*      */     {
/* 1920 */       x -= comp_bounds.x;
/* 1921 */       y -= comp_bounds.y;
/*      */       
/* 1923 */       Control[] children = comp.getChildren();
/*      */       
/* 1925 */       for (int i = 0; i < children.length; i++)
/*      */       {
/* 1927 */         Control child = children[i];
/*      */         
/* 1929 */         if (child.isVisible())
/*      */         {
/* 1931 */           if ((child instanceof Composite))
/*      */           {
/* 1933 */             Control res = findChild((Composite)child, x, y);
/*      */             
/* 1935 */             if (res != null)
/*      */             {
/* 1937 */               return res;
/*      */             }
/*      */           }
/*      */           else {
/* 1941 */             return child;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1946 */       return comp;
/*      */     }
/*      */     
/* 1949 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void dump(Control comp)
/*      */   {
/* 1956 */     java.io.PrintWriter pw = new java.io.PrintWriter(System.out);
/*      */     
/* 1958 */     IndentWriter iw = new IndentWriter(pw);
/*      */     
/* 1960 */     dump(iw, comp, new HashSet());
/*      */     
/* 1962 */     pw.flush();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void dump(IndentWriter iw, Control comp, Set<Object> done)
/*      */   {
/* 1971 */     if (done.contains(comp))
/*      */     {
/* 1973 */       iw.println("<RECURSIVE!>");
/*      */       
/* 1975 */       return;
/*      */     }
/*      */     
/* 1978 */     done.add(comp);
/*      */     
/* 1980 */     String str = comp.getClass().getName();
/*      */     
/* 1982 */     int pos = str.lastIndexOf(".");
/*      */     
/* 1984 */     if (pos != -1)
/*      */     {
/* 1986 */       str = str.substring(pos + 1);
/*      */     }
/*      */     try
/*      */     {
/* 1990 */       Field f = Widget.class.getDeclaredField("data");
/*      */       
/* 1992 */       f.setAccessible(true);
/*      */       
/* 1994 */       Object data = f.get(comp);
/*      */       
/* 1996 */       if ((data instanceof Object[])) {
/* 1997 */         Object[] temp = (Object[])data;
/* 1998 */         String s = "";
/* 1999 */         for (Object t : temp) {
/* 2000 */           s = s + (s == "" ? "" : ",") + t;
/*      */         }
/* 2002 */         data = s;
/*      */       }
/*      */       
/* 2005 */       String lay = "" + comp.getLayoutData();
/*      */       
/* 2007 */       if ((comp instanceof Composite))
/*      */       {
/* 2009 */         lay = lay + "/" + ((Composite)comp).getLayoutData();
/*      */       }
/*      */       
/* 2012 */       iw.println(str + ",vis=" + comp.isVisible() + ",data=" + data + ",layout=" + lay + ",size=" + comp.getBounds());
/*      */       
/* 2014 */       if ((comp instanceof Composite)) {
/*      */         try
/*      */         {
/* 2017 */           iw.indent();
/*      */           
/* 2019 */           Control[] children = ((Composite)comp).getChildren();
/*      */           
/* 2021 */           for (Control kid : children)
/*      */           {
/*      */ 
/*      */ 
/* 2025 */             dump(iw, kid, done);
/*      */           }
/*      */         }
/*      */         finally {
/* 2029 */           iw.exdent();
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 2034 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void addListenerAndChildren(Composite area, int event, Listener listener)
/*      */   {
/* 2045 */     area.addListener(event, listener);
/* 2046 */     Control[] children = area.getChildren();
/* 2047 */     for (int i = 0; i < children.length; i++) {
/* 2048 */       Control child = children[i];
/* 2049 */       if ((child instanceof Composite)) {
/* 2050 */         addListenerAndChildren((Composite)child, event, listener);
/*      */       } else {
/* 2052 */         child.addListener(event, listener);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static Shell findAnyShell()
/*      */   {
/* 2059 */     com.aelitis.azureus.ui.swt.UIFunctionsSWT uiFunctions = com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 2060 */     if (uiFunctions != null) {
/* 2061 */       Shell shell = uiFunctions.getMainShell();
/* 2062 */       if ((shell != null) && (!shell.isDisposed())) {
/* 2063 */         return shell;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 2068 */     Display current = Display.getCurrent();
/* 2069 */     if (current == null) {
/* 2070 */       return null;
/*      */     }
/* 2072 */     Shell shell = current.getActiveShell();
/* 2073 */     if ((shell != null) && (!shell.isDisposed())) {
/* 2074 */       return shell;
/*      */     }
/*      */     
/*      */ 
/* 2078 */     Shell[] shells = current.getShells();
/* 2079 */     if (shells.length == 0) {
/* 2080 */       return null;
/*      */     }
/*      */     
/* 2083 */     if ((shells[0] != null) && (!shells[0].isDisposed())) {
/* 2084 */       return shells[0];
/*      */     }
/*      */     
/* 2087 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static boolean verifyShellRect(Shell shell, boolean bAdjustIfInvalid)
/*      */   {
/* 2094 */     return verifyShellRect(shell, bAdjustIfInvalid, true);
/*      */   }
/*      */   
/*      */   private static boolean verifyShellRect(Shell shell, boolean bAdjustIfInvalid, boolean reverifyOnChange)
/*      */   {
/*      */     boolean bMetricsOk;
/*      */     try {
/* 2101 */       if (shell.getMaximized()) {
/* 2102 */         return true;
/*      */       }
/* 2104 */       bMetricsOk = false;
/* 2105 */       Point ptTopLeft = shell.getLocation();
/* 2106 */       Point size = shell.getSize();
/* 2107 */       Point ptBottomRight = shell.getLocation();
/* 2108 */       ptBottomRight.x += size.x - 1;
/* 2109 */       ptBottomRight.y += size.y - 1;
/*      */       
/* 2111 */       Rectangle boundsMonitorTopLeft = null;
/* 2112 */       Rectangle boundsMonitorBottomRight = null;
/* 2113 */       Rectangle boundsMonitorContained = null;
/*      */       
/* 2115 */       Monitor[] monitors = shell.getDisplay().getMonitors();
/* 2116 */       for (int j = 0; (j < monitors.length) && (!bMetricsOk); j++) {
/* 2117 */         Rectangle bounds = monitors[j].getClientArea();
/* 2118 */         boolean hasTopLeft = bounds.contains(ptTopLeft);
/* 2119 */         boolean hasBottomRight = bounds.contains(ptBottomRight);
/* 2120 */         bMetricsOk = (hasTopLeft) && (hasBottomRight);
/* 2121 */         if (hasTopLeft) {
/* 2122 */           boundsMonitorTopLeft = bounds;
/*      */         }
/* 2124 */         if (hasBottomRight) {
/* 2125 */           boundsMonitorBottomRight = bounds;
/*      */         }
/* 2127 */         if ((boundsMonitorContained == null) && (bounds.intersects(ptTopLeft.x, ptTopLeft.y, ptBottomRight.x - ptTopLeft.x + 1, ptBottomRight.y - ptTopLeft.y + 1)))
/*      */         {
/*      */ 
/* 2130 */           boundsMonitorContained = bounds;
/*      */         }
/*      */       }
/* 2133 */       Rectangle bounds = boundsMonitorBottomRight != null ? boundsMonitorBottomRight : boundsMonitorTopLeft != null ? boundsMonitorTopLeft : boundsMonitorContained;
/*      */       
/*      */ 
/*      */ 
/* 2137 */       if ((!bMetricsOk) && (bAdjustIfInvalid) && (bounds != null))
/*      */       {
/* 2139 */         int xDiff = ptBottomRight.x - (bounds.x + bounds.width);
/* 2140 */         int yDiff = ptBottomRight.y - (bounds.y + bounds.height);
/* 2141 */         boolean needsResize = false;
/* 2142 */         boolean needsMove = false;
/*      */         
/* 2144 */         if (xDiff > 0) {
/* 2145 */           ptTopLeft.x -= xDiff;
/* 2146 */           needsMove = true;
/*      */         }
/* 2148 */         if (yDiff > 0) {
/* 2149 */           ptTopLeft.y -= yDiff;
/* 2150 */           needsMove = true;
/*      */         }
/*      */         
/*      */ 
/* 2154 */         if (ptTopLeft.x < bounds.x) {
/* 2155 */           ptTopLeft.x = bounds.x;
/* 2156 */           needsMove = true;
/*      */         }
/* 2158 */         if (ptTopLeft.y < bounds.y) {
/* 2159 */           ptTopLeft.y = bounds.y;
/* 2160 */           needsMove = true;
/*      */         }
/*      */         
/* 2163 */         if (ptTopLeft.y < bounds.y) {
/* 2164 */           ptBottomRight.y -= bounds.y - ptTopLeft.y;
/* 2165 */           ptTopLeft.y = bounds.y;
/* 2166 */           needsResize = true;
/*      */         }
/* 2168 */         if (ptTopLeft.x < bounds.x) {
/* 2169 */           ptBottomRight.x -= bounds.x - ptTopLeft.x;
/* 2170 */           ptTopLeft.x = bounds.x;
/* 2171 */           needsResize = true;
/*      */         }
/* 2173 */         if (ptBottomRight.y >= bounds.y + bounds.height) {
/* 2174 */           ptBottomRight.y = (bounds.y + bounds.height - 1);
/* 2175 */           needsResize = true;
/*      */         }
/* 2177 */         if (ptBottomRight.x >= bounds.x + bounds.width) {
/* 2178 */           ptBottomRight.x = (bounds.x + bounds.width - 1);
/* 2179 */           needsResize = true;
/*      */         }
/* 2181 */         if (needsMove) {
/* 2182 */           shell.setLocation(ptTopLeft);
/*      */         }
/*      */         
/* 2185 */         if (needsResize) {
/* 2186 */           shell.setSize(ptBottomRight.x - ptTopLeft.x + 1, ptBottomRight.y - ptTopLeft.y + 1);
/*      */         }
/*      */         
/* 2189 */         if ((reverifyOnChange) && ((needsMove) || (needsResize)))
/*      */         {
/* 2191 */           return verifyShellRect(shell, bAdjustIfInvalid, false);
/*      */         }
/*      */         
/*      */ 
/* 2195 */         return true;
/*      */       }
/*      */     }
/*      */     catch (NoSuchMethodError e) {
/* 2199 */       Rectangle bounds = shell.getDisplay().getClientArea();
/* 2200 */       bMetricsOk = shell.getBounds().intersects(bounds);
/*      */     } catch (Throwable t) {
/* 2202 */       bMetricsOk = true;
/*      */     }
/*      */     
/*      */ 
/* 2206 */     if ((!bMetricsOk) && (bAdjustIfInvalid)) {
/* 2207 */       centreWindow(shell);
/*      */     }
/* 2209 */     return bMetricsOk;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void relayout(Control control)
/*      */   {
/* 2219 */     long startOn = DEBUG_SWTEXEC ? System.currentTimeMillis() : 0L;
/* 2220 */     relayout(control, false);
/* 2221 */     if (DEBUG_SWTEXEC) {
/* 2222 */       long diff = System.currentTimeMillis() - startOn;
/* 2223 */       if (diff > 100L) {
/* 2224 */         String s = "Long relayout of " + diff + "ms " + Debug.getCompressedStackTrace();
/*      */         
/* 2226 */         if (diag_logger != null) {
/* 2227 */           diag_logger.log(s);
/*      */         }
/* 2229 */         System.out.println(s);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void relayout(Control control, boolean expandOnly)
/*      */   {
/* 2241 */     if ((control == null) || (control.isDisposed()) || (!control.isVisible())) {
/* 2242 */       return;
/*      */     }
/*      */     
/* 2245 */     if ((control instanceof ScrolledComposite)) {
/* 2246 */       ScrolledComposite sc = (ScrolledComposite)control;
/* 2247 */       Control content = sc.getContent();
/* 2248 */       if ((content != null) && (!content.isDisposed())) {
/* 2249 */         Rectangle r = sc.getClientArea();
/* 2250 */         sc.setMinSize(content.computeSize(r.width, -1));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 2255 */     Composite parent = control.getParent();
/* 2256 */     Point targetSize = control.computeSize(-1, -1, true);
/* 2257 */     Point size = control.getSize();
/* 2258 */     if ((size.y == targetSize.y) && (size.x == targetSize.x)) {
/* 2259 */       return;
/*      */     }
/*      */     
/* 2262 */     int fixedWidth = -1;
/* 2263 */     int fixedHeight = -1;
/* 2264 */     Object layoutData = control.getLayoutData();
/* 2265 */     if ((layoutData instanceof FormData)) {
/* 2266 */       FormData fd = (FormData)layoutData;
/* 2267 */       fixedHeight = fd.height;
/* 2268 */       fixedWidth = fd.width;
/* 2269 */       if ((fd.width != -1) && (fd.height != -1)) {
/* 2270 */         parent.layout();
/* 2271 */         return;
/*      */       }
/*      */     }
/*      */     
/* 2275 */     if ((expandOnly) && (size.y >= targetSize.y) && (size.x >= targetSize.x)) {
/* 2276 */       parent.layout();
/* 2277 */       return;
/*      */     }
/*      */     
/*      */ 
/* 2281 */     Control previous = control;
/* 2282 */     while (parent != null) {
/* 2283 */       parent.layout(new Control[] { previous }, 5);
/* 2284 */       if ((parent instanceof ScrolledComposite)) {
/* 2285 */         ScrolledComposite sc = (ScrolledComposite)parent;
/* 2286 */         Control content = sc.getContent();
/* 2287 */         if ((content != null) && (!content.isDisposed())) {
/* 2288 */           Rectangle r = sc.getClientArea();
/* 2289 */           sc.setMinSize(content.computeSize(r.width, -1));
/*      */         }
/*      */       }
/*      */       
/* 2293 */       Point newSize = control.getSize();
/*      */       
/*      */ 
/*      */ 
/* 2297 */       if (((fixedHeight > -1) || (newSize.y >= targetSize.y)) && ((fixedWidth > -1) || (newSize.x >= targetSize.x))) {
/*      */         break;
/*      */       }
/*      */       
/*      */ 
/* 2302 */       previous = parent;
/* 2303 */       parent = parent.getParent();
/*      */     }
/*      */     
/* 2306 */     if (parent != null) {
/* 2307 */       parent.layout();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void beep()
/*      */   {
/* 2315 */     execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/* 2317 */         Display display = Display.getDefault();
/* 2318 */         if (display != null) {
/* 2319 */           display.beep();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   public static Boolean execSWTThreadWithBool(String ID, AERunnableBoolean code)
/*      */   {
/* 2330 */     return execSWTThreadWithBool(ID, code, 0L);
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
/*      */   public static Boolean execSWTThreadWithBool(String ID, AERunnableBoolean code, long millis)
/*      */   {
/* 2356 */     if (code == null) {
/* 2357 */       org.gudy.azureus2.core3.logging.Logger.log(new org.gudy.azureus2.core3.logging.LogEvent(org.gudy.azureus2.core3.logging.LogIDs.CORE, "code null"));
/*      */       
/* 2359 */       return null;
/*      */     }
/*      */     
/* 2362 */     Boolean[] returnValueObject = { null };
/*      */     
/*      */ 
/*      */ 
/* 2366 */     Display display = getDisplay();
/*      */     
/* 2368 */     AESemaphore sem = null;
/* 2369 */     if ((display == null) || (display.getThread() != Thread.currentThread())) {
/* 2370 */       sem = new AESemaphore(ID);
/*      */     }
/*      */     try
/*      */     {
/* 2374 */       code.setupReturn(ID, returnValueObject, sem);
/*      */       
/* 2376 */       if (!execSWTThread(code))
/*      */       {
/*      */ 
/* 2379 */         return null;
/*      */       }
/*      */     } catch (Throwable e) {
/* 2382 */       if (sem != null) {
/* 2383 */         sem.release();
/*      */       }
/* 2385 */       Debug.out(ID, e);
/*      */     }
/* 2387 */     if (sem != null) {
/* 2388 */       sem.reserve(millis);
/*      */     }
/*      */     
/* 2391 */     return returnValueObject[0];
/*      */   }
/*      */   
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   public static Object execSWTThreadWithObject(String ID, AERunnableObject code)
/*      */   {
/* 2399 */     return execSWTThreadWithObject(ID, code, 0L);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Object execSWTThreadWithObject(String ID, AERunnableObject code, long millis)
/*      */   {
/* 2424 */     if (code == null) {
/* 2425 */       return null;
/*      */     }
/*      */     
/* 2428 */     Object[] returnValueObject = { null };
/*      */     
/*      */ 
/*      */ 
/* 2432 */     Display display = getDisplay();
/*      */     
/* 2434 */     AESemaphore sem = null;
/* 2435 */     if ((display == null) || (display.getThread() != Thread.currentThread())) {
/* 2436 */       sem = new AESemaphore(ID);
/*      */     }
/*      */     try
/*      */     {
/* 2440 */       code.setupReturn(ID, returnValueObject, sem);
/*      */       
/* 2442 */       if (!execSWTThread(code))
/*      */       {
/* 2444 */         return null;
/*      */       }
/*      */     } catch (Throwable e) {
/* 2447 */       if (sem != null) {
/* 2448 */         sem.releaseForever();
/*      */       }
/* 2450 */       Debug.out(ID, e);
/*      */     }
/* 2452 */     if ((sem != null) && 
/* 2453 */       (!sem.reserve(millis)) && (DEBUG_SWTEXEC)) {
/* 2454 */       System.out.println("Timeout in execSWTThreadWithObject(" + ID + ", " + code + ", " + millis + ") via " + Debug.getCompressedStackTrace());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2459 */     return returnValueObject[0];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void waitForModals()
/*      */   {
/* 2468 */     SWTThread swt = SWTThread.getInstance();
/*      */     
/*      */     Display display;
/* 2471 */     if (swt == null) {
/* 2472 */       Display display = Display.getDefault();
/* 2473 */       if (display == null) {
/* 2474 */         System.err.println("SWT Thread not started yet!");
/*      */       }
/*      */     }
/*      */     else {
/* 2478 */       if (swt.isTerminated()) {
/* 2479 */         return;
/*      */       }
/* 2481 */       display = swt.getDisplay();
/*      */     }
/*      */     
/* 2484 */     if ((display == null) || (display.isDisposed())) {
/* 2485 */       return;
/*      */     }
/*      */     
/* 2488 */     Shell[] shells = display.getShells();
/* 2489 */     Shell modalShell = null;
/* 2490 */     for (int i = 0; i < shells.length; i++) {
/* 2491 */       Shell shell = shells[i];
/* 2492 */       if ((shell.getStyle() & 0x10000) != 0) {
/* 2493 */         modalShell = shell;
/* 2494 */         break;
/*      */       }
/*      */     }
/*      */     
/* 2498 */     if (modalShell != null) {
/* 2499 */       while (!modalShell.isDisposed()) {
/* 2500 */         if (!display.readAndDispatch()) {
/* 2501 */           display.sleep();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static GridData getWrappableLabelGridData(int hspan, int styles) {
/* 2508 */     GridData gridData = new GridData(0x100 | styles);
/* 2509 */     gridData.horizontalSpan = hspan;
/* 2510 */     gridData.widthHint = 0;
/* 2511 */     return gridData;
/*      */   }
/*      */   
/*      */   public static Image createAlphaImage(Device device, int width, int height) {
/* 2515 */     return createAlphaImage(device, width, height, (byte)0);
/*      */   }
/*      */   
/*      */   public static Image createAlphaImage(Device device, int width, int height, byte defaultAlpha)
/*      */   {
/* 2520 */     byte[] alphaData = new byte[width * height];
/* 2521 */     Arrays.fill(alphaData, 0, alphaData.length, defaultAlpha);
/*      */     
/* 2523 */     ImageData imageData = new ImageData(width, height, 24, new PaletteData(255, 65280, 16711680));
/*      */     
/* 2525 */     Arrays.fill(imageData.data, 0, imageData.data.length, (byte)0);
/* 2526 */     imageData.alphaData = alphaData;
/* 2527 */     if (device == null) {
/* 2528 */       device = Display.getDefault();
/*      */     }
/* 2530 */     Image image = new Image(device, imageData);
/* 2531 */     return image;
/*      */   }
/*      */   
/*      */   public static Image blitImage(Device device, Image srcImage, Rectangle srcArea, Image dstImage, Point dstPos)
/*      */   {
/* 2536 */     if (srcArea == null) {
/* 2537 */       srcArea = srcImage.getBounds();
/*      */     }
/* 2539 */     Rectangle dstBounds = dstImage.getBounds();
/* 2540 */     if (dstPos == null) {
/* 2541 */       dstPos = new Point(dstBounds.x, dstBounds.y);
/*      */     } else {
/* 2543 */       dstBounds.x = dstPos.x;
/* 2544 */       dstBounds.y = dstPos.y;
/*      */     }
/*      */     
/* 2547 */     ImageData dstImageData = dstImage.getImageData();
/* 2548 */     ImageData srcImageData = srcImage.getImageData();
/* 2549 */     int yPos = dstPos.y;
/* 2550 */     int[] pixels = new int[srcArea.width];
/* 2551 */     byte[] alphas = new byte[srcArea.width];
/* 2552 */     for (int y = 0; y < srcArea.height; y++) {
/* 2553 */       srcImageData.getPixels(srcArea.x, y + srcArea.y, srcArea.width, pixels, 0);
/* 2554 */       dstImageData.setPixels(dstPos.x, yPos, srcArea.width, pixels, 0);
/* 2555 */       srcImageData.getAlphas(srcArea.x, y + srcArea.y, srcArea.width, alphas, 0);
/* 2556 */       dstImageData.setAlphas(dstPos.x, yPos, srcArea.width, alphas, 0);
/* 2557 */       yPos++;
/*      */     }
/*      */     
/* 2560 */     return new Image(device, dstImageData);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void drawStriped(GC gcImg, int x, int y, int width, int height, int lineDist, int leftshift, boolean fallingLines)
/*      */   {
/* 2571 */     lineDist += 2;
/* 2572 */     int xm = x + width;
/* 2573 */     int ym = y + height;
/* 2574 */     for (int i = x; i < xm; i++) {
/* 2575 */       for (int j = y; j < ym; j++) {
/* 2576 */         if ((i + leftshift + (fallingLines ? -j : j)) % lineDist == 0) {
/* 2577 */           gcImg.drawPoint(i, j);
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
/*      */   public static Image renderTransparency(Display display, Image background, Image foreground, Point foregroundOffsetOnBg, int modifyForegroundAlpha)
/*      */   {
/* 2594 */     if ((display == null) || (display.isDisposed()) || (background == null) || (background.isDisposed()) || (foreground == null) || (foreground.isDisposed()))
/*      */     {
/*      */ 
/* 2597 */       return null; }
/* 2598 */     Rectangle backgroundArea = background.getBounds();
/* 2599 */     Rectangle foregroundDrawArea = foreground.getBounds();
/*      */     
/* 2601 */     foregroundDrawArea.x += foregroundOffsetOnBg.x;
/* 2602 */     foregroundDrawArea.y += foregroundOffsetOnBg.y;
/*      */     
/* 2604 */     foregroundDrawArea.intersect(backgroundArea);
/*      */     
/* 2606 */     if (foregroundDrawArea.isEmpty()) {
/* 2607 */       return null;
/*      */     }
/* 2609 */     Image image = new Image(display, backgroundArea);
/*      */     
/* 2611 */     ImageData backData = background.getImageData();
/* 2612 */     ImageData foreData = foreground.getImageData();
/* 2613 */     ImageData imgData = image.getImageData();
/*      */     
/* 2615 */     PaletteData backPalette = backData.palette;
/* 2616 */     ImageData backMask = backData.getTransparencyType() != 1 ? backData.getTransparencyMask() : null;
/*      */     
/* 2618 */     PaletteData forePalette = foreData.palette;
/* 2619 */     ImageData foreMask = foreData.getTransparencyType() != 1 ? foreData.getTransparencyMask() : null;
/*      */     
/* 2621 */     PaletteData imgPalette = imgData.palette;
/* 2622 */     image.dispose();
/*      */     
/* 2624 */     for (int x = 0; x < backgroundArea.width; x++) {
/* 2625 */       for (int y = 0; y < backgroundArea.height; y++) {
/* 2626 */         RGB cBack = backPalette.getRGB(backData.getPixel(x, y));
/* 2627 */         int aBack = backData.getAlpha(x, y);
/* 2628 */         if ((backMask != null) && (backMask.getPixel(x, y) == 0)) {
/* 2629 */           aBack = 0;
/*      */         }
/* 2631 */         int aFore = 0;
/*      */         
/* 2633 */         if (foregroundDrawArea.contains(x, y)) {
/* 2634 */           int fx = x - foregroundDrawArea.x;
/* 2635 */           int fy = y - foregroundDrawArea.y;
/* 2636 */           RGB cFore = forePalette.getRGB(foreData.getPixel(fx, fy));
/* 2637 */           aFore = foreData.getAlpha(fx, fy);
/* 2638 */           if ((foreMask != null) && (foreMask.getPixel(fx, fy) == 0))
/* 2639 */             aFore = 0;
/* 2640 */           aFore = aFore * modifyForegroundAlpha / 255;
/* 2641 */           cBack.red *= aBack * (255 - aFore);
/* 2642 */           cBack.red /= 255;
/* 2643 */           cBack.red += aFore * cFore.red;
/* 2644 */           cBack.red /= 255;
/* 2645 */           cBack.green *= aBack * (255 - aFore);
/* 2646 */           cBack.green /= 255;
/* 2647 */           cBack.green += aFore * cFore.green;
/* 2648 */           cBack.green /= 255;
/* 2649 */           cBack.blue *= aBack * (255 - aFore);
/* 2650 */           cBack.blue /= 255;
/* 2651 */           cBack.blue += aFore * cFore.blue;
/* 2652 */           cBack.blue /= 255;
/*      */         }
/* 2654 */         imgData.setAlpha(x, y, aFore + aBack * (255 - aFore) / 255);
/* 2655 */         imgData.setPixel(x, y, imgPalette.getPixel(cBack));
/*      */       }
/*      */     }
/* 2658 */     return new Image(display, imgData);
/*      */   }
/*      */   
/*      */   public static Control findBackgroundImageControl(Control control) {
/* 2662 */     Image image = control.getBackgroundImage();
/* 2663 */     if (image == null) {
/* 2664 */       return control;
/*      */     }
/*      */     
/* 2667 */     Composite parent = control.getParent();
/* 2668 */     Composite lastParent = parent;
/* 2669 */     while (parent != null) {
/* 2670 */       Image parentImage = parent.getBackgroundImage();
/* 2671 */       if (!image.equals(parentImage)) {
/* 2672 */         return lastParent;
/*      */       }
/* 2674 */       lastParent = parent;
/* 2675 */       parent = parent.getParent();
/*      */     }
/*      */     
/* 2678 */     return control;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean anyShellHaveStyle(int styles)
/*      */   {
/* 2687 */     Display display = Display.getCurrent();
/* 2688 */     if (display != null) {
/* 2689 */       Shell[] shells = display.getShells();
/* 2690 */       for (int i = 0; i < shells.length; i++) {
/* 2691 */         Shell shell = shells[i];
/* 2692 */         int style = shell.getStyle();
/* 2693 */         if ((style & styles) == styles) {
/* 2694 */           return true;
/*      */         }
/*      */       }
/*      */     }
/* 2698 */     return false;
/*      */   }
/*      */   
/*      */   public static Shell findFirstShellWithStyle(int styles) {
/* 2702 */     Display display = Display.getCurrent();
/* 2703 */     if (display != null) {
/* 2704 */       Shell[] shells = display.getShells();
/* 2705 */       for (int i = 0; i < shells.length; i++) {
/* 2706 */         Shell shell = shells[i];
/* 2707 */         int style = shell.getStyle();
/* 2708 */         if (((style & styles) == styles) && (!shell.isDisposed())) {
/* 2709 */           return shell;
/*      */         }
/*      */       }
/*      */     }
/* 2713 */     return null;
/*      */   }
/*      */   
/*      */   public static int[] colorToIntArray(Color color) {
/* 2717 */     if ((color == null) || (color.isDisposed())) {
/* 2718 */       return null;
/*      */     }
/* 2720 */     return new int[] { color.getRed(), color.getGreen(), color.getBlue() };
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
/*      */   public static void centerRelativeTo(Rectangle target, Rectangle reference)
/*      */   {
/* 2733 */     target.x = (reference.x + reference.width / 2 - target.width / 2);
/* 2734 */     target.y = (reference.y + reference.height / 2 - target.height / 2);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void makeVisibleOnCursor(Rectangle rect)
/*      */   {
/* 2756 */     if (null == rect) {
/* 2757 */       return;
/*      */     }
/*      */     
/* 2760 */     Display display = Display.getCurrent();
/* 2761 */     if (null == display) {
/* 2762 */       Debug.out("No current display detected.  This method [Utils.makeVisibleOnCursor()] must be called from a display thread.");
/* 2763 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 2771 */       Point cursorLocation = display.getCursorLocation();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2776 */       makeVisibleOnMonitor(rect, getMonitor(cursorLocation));
/*      */     }
/*      */     catch (Throwable t) {}
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void makeVisibleOnMonitor(Rectangle rect, Monitor monitor)
/*      */   {
/* 2801 */     if ((null == rect) || (null == monitor)) {
/* 2802 */       return;
/*      */     }
/*      */     
/*      */     try
/*      */     {
/* 2807 */       Rectangle monitorBounds = monitor.getClientArea();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2813 */       int bottomDiff = monitorBounds.y + monitorBounds.height - (rect.y + rect.height);
/*      */       
/* 2815 */       if (bottomDiff < 0) {
/* 2816 */         rect.y += bottomDiff;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2823 */       int rightDiff = monitorBounds.x + monitorBounds.width - (rect.x + rect.width);
/*      */       
/* 2825 */       if (rightDiff < 0) {
/* 2826 */         rect.x += rightDiff;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2832 */       if (rect.x < monitorBounds.x) {
/* 2833 */         rect.x = monitorBounds.x;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2839 */       if (rect.y < monitorBounds.y) {
/* 2840 */         rect.y = monitorBounds.y;
/*      */       }
/*      */     }
/*      */     catch (Throwable t) {}
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
/*      */   private static Monitor getMonitor(int x, int y)
/*      */   {
/* 2856 */     return getMonitor(new Point(x, y));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Monitor getMonitor(Point location)
/*      */   {
/* 2865 */     Display display = Display.getCurrent();
/*      */     
/* 2867 */     if (null == display) {
/* 2868 */       Debug.out("No current display detected.  This method [Utils.makeVisibleOnCursor()] must be called from a display thread.");
/* 2869 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 2877 */       Monitor[] monitors = display.getMonitors();
/* 2878 */       Rectangle monitorBounds = null;
/* 2879 */       for (int i = 0; i < monitors.length; i++) {
/* 2880 */         monitorBounds = monitors[i].getClientArea();
/* 2881 */         if (monitorBounds.contains(location)) {
/* 2882 */           return monitors[i];
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable t) {}
/*      */     
/*      */ 
/* 2889 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void makeButtonsEqualWidth(List<Button> buttons)
/*      */   {
/* 2896 */     int width = 75;
/*      */     
/* 2898 */     for (Button button : buttons)
/*      */     {
/* 2900 */       width = Math.max(width, button.computeSize(-1, -1).x);
/*      */     }
/*      */     
/* 2903 */     for (Button button : buttons) {
/* 2904 */       Object data = button.getLayoutData();
/* 2905 */       if (data != null) {
/* 2906 */         if ((data instanceof GridData)) {
/* 2907 */           ((GridData)data).widthHint = width;
/* 2908 */         } else if ((data instanceof FormData)) {
/* 2909 */           ((FormData)data).width = width;
/*      */         } else {
/* 2911 */           Debug.out("Expected GridData/FormData");
/*      */         }
/*      */       } else {
/* 2914 */         data = new GridData();
/* 2915 */         ((GridData)data).widthHint = width;
/* 2916 */         button.setLayoutData(data);
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
/*      */   public static int getInitialBrowserStyle(int style)
/*      */   {
/* 2931 */     if (!gotBrowserStyle) {
/* 2932 */       browserStyle = COConfigurationManager.getBooleanParameter("swt.forceMozilla") ? 32768 : 0;
/*      */       
/* 2934 */       gotBrowserStyle = true;
/*      */     }
/* 2936 */     return style | browserStyle;
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
/*      */   public static synchronized String truncateText(GC gc, String text, int maxWidth, boolean cache)
/*      */   {
/* 2952 */     if (cache) {
/* 2953 */       TruncatedTextResult result = (TruncatedTextResult)truncatedTextCache.get(text);
/* 2954 */       if ((result != null) && (result.maxWidth == maxWidth)) {
/* 2955 */         return result.text;
/*      */       }
/*      */     }
/* 2958 */     StringBuilder sb = new StringBuilder(text);
/* 2959 */     String append = "...";
/* 2960 */     int appendWidth = gc.textExtent(append).x;
/* 2961 */     boolean needsAppend = false;
/* 2962 */     while (gc.textExtent(sb.toString()).x > maxWidth)
/*      */     {
/* 2964 */       sb.deleteCharAt(sb.length() - 1);
/* 2965 */       needsAppend = true;
/* 2966 */       if (sb.length() == 1) {
/*      */         break;
/*      */       }
/*      */     }
/*      */     
/* 2971 */     if (needsAppend) {
/* 2972 */       while (gc.textExtent(sb.toString()).x + appendWidth > maxWidth)
/*      */       {
/* 2974 */         sb.deleteCharAt(sb.length() - 1);
/* 2975 */         needsAppend = true;
/* 2976 */         if (sb.length() == 1) {
/*      */           break;
/*      */         }
/*      */       }
/* 2980 */       sb.append(append);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2985 */     if (cache) {
/* 2986 */       TruncatedTextResult ttR = new TruncatedTextResult();
/* 2987 */       ttR.text = sb.toString();
/* 2988 */       ttR.maxWidth = maxWidth;
/*      */       
/* 2990 */       truncatedTextCache.put(text, ttR);
/*      */     }
/*      */     
/* 2993 */     return sb.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String toColorHexString(Color bg)
/*      */   {
/* 3003 */     StringBuffer sb = new StringBuffer();
/* 3004 */     twoHex(sb, bg.getRed());
/* 3005 */     twoHex(sb, bg.getGreen());
/* 3006 */     twoHex(sb, bg.getBlue());
/* 3007 */     return sb.toString();
/*      */   }
/*      */   
/*      */   private static void twoHex(StringBuffer sb, int h) {
/* 3011 */     if (h <= 15) {
/* 3012 */       sb.append('0');
/*      */     }
/* 3014 */     sb.append(Integer.toHexString(h));
/*      */   }
/*      */   
/*      */ 
/*      */   public static String getWidgetBGColorURLParam()
/*      */   {
/* 3020 */     Color bg = findAnyShell().getDisplay().getSystemColor(22);
/*      */     
/* 3022 */     byte[] color = new byte[3];
/*      */     
/* 3024 */     color[0] = ((byte)bg.getRed());
/* 3025 */     color[1] = ((byte)bg.getGreen());
/* 3026 */     color[2] = ((byte)bg.getBlue());
/*      */     
/* 3028 */     return "bg_color=" + org.gudy.azureus2.core3.util.ByteFormatter.nicePrint(color);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void reportError(Throwable e)
/*      */   {
/* 3035 */     MessageBoxShell mb = new MessageBoxShell(MessageText.getString("ConfigView.section.security.op.error.title"), MessageText.getString("ConfigView.section.security.op.error", new String[] { Debug.getNestedExceptionMessage(e) }), new String[] { MessageText.getString("Button.ok") }, 0);
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
/* 3047 */     mb.open(null);
/*      */   }
/*      */   
/*      */   public static void getOffOfSWTThread(AERunnable runnable) {
/* 3051 */     tp.run(runnable);
/*      */   }
/*      */   
/*      */ 
/*      */   public static BrowserWrapper createSafeBrowser(Composite parent, int style)
/*      */   {
/*      */     try
/*      */     {
/* 3059 */       BrowserWrapper browser = BrowserWrapper.createBrowser(parent, getInitialBrowserStyle(style));
/* 3060 */       browser.addDisposeListener(new DisposeListener()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void widgetDisposed(DisposeEvent e)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3074 */           this.val$browser.setUrl("about:blank");
/*      */           
/* 3076 */           this.val$browser.setVisible(false);
/*      */           
/* 3078 */           final boolean[] done = { false };
/*      */           
/* 3080 */           long start = SystemTime.getMonotonousTime();
/*      */           
/* 3082 */           Utils.execSWTThreadLater(250, new Runnable()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void run()
/*      */             {
/*      */ 
/* 3089 */               synchronized (done)
/*      */               {
/* 3091 */                 done[0] = true;
/*      */               }
/*      */             }
/*      */           });
/*      */           
/* 3096 */           while ((!e.display.isDisposed()) && (e.display.readAndDispatch()))
/*      */           {
/* 3098 */             synchronized (done)
/*      */             {
/* 3100 */               if ((done[0] != 0) || (SystemTime.getMonotonousTime() - start > 500L)) {
/*      */                 break;
/*      */               }
/*      */               
/*      */             }
/*      */           }
/*      */         }
/* 3107 */       });
/* 3108 */       return browser;
/*      */     }
/*      */     catch (Throwable e) {}
/* 3111 */     return null;
/*      */   }
/*      */   
/*      */   public static int getUserMode() {
/* 3115 */     return userMode;
/*      */   }
/*      */   
/*      */   public static Point getLocationRelativeToShell(Control control) {
/* 3119 */     Point controlLocation = control.toDisplay(0, 0);
/* 3120 */     Point shellLocation = control.getShell().getLocation();
/* 3121 */     return new Point(controlLocation.x - shellLocation.x, controlLocation.y - shellLocation.y);
/*      */   }
/*      */   
/*      */   static
/*      */   {
/*  148 */     if (DEBUG_SWTEXEC) {
/*  149 */       System.out.println("==== debug.swtexec=1, performance may be affected ====");
/*  150 */       queue = new ArrayList();
/*  151 */       diag_logger = org.gudy.azureus2.core3.util.AEDiagnostics.getLogger("swt");
/*  152 */       diag_logger.log("\n\nSWT Logging Starts");
/*      */       
/*  154 */       org.gudy.azureus2.core3.util.AEDiagnostics.addEvidenceGenerator(new org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator() {
/*      */         public void generate(IndentWriter writer) {
/*  156 */           writer.println("SWT Queue:");
/*  157 */           writer.indent();
/*  158 */           for (Runnable r : Utils.queue) {
/*  159 */             if (r == null) {
/*  160 */               writer.println("NULL");
/*      */             } else {
/*  162 */               writer.println(r.toString());
/*      */             }
/*      */           }
/*  165 */           writer.exdent();
/*      */         }
/*      */       });
/*      */     } else {
/*  169 */       queue = null;
/*  170 */       diag_logger = null;
/*      */     }
/*      */     
/*  173 */     COConfigurationManager.addAndFireParameterListener("User Mode", new ParameterListener() {
/*      */       public void parameterChanged(String parameterName) {
/*  175 */         Utils.access$102(COConfigurationManager.getIntParameter("User Mode"));
/*      */       }
/*  177 */     });
/*  178 */     COConfigurationManager.addAndFireParameterListener("ui", new ParameterListener() {
/*      */       public void parameterChanged(String parameterName) {
/*  180 */         Utils.access$202("az2".equals(COConfigurationManager.getStringParameter("ui")));
/*      */       }
/*      */       
/*  183 */     });
/*  184 */     boolean smallOSXControl = COConfigurationManager.getBooleanParameter("enable_small_osx_fonts");
/*  185 */     BUTTON_MARGIN = Constants.isOSX ? 12 : smallOSXControl ? 10 : 6;
/*      */     
/*      */ 
/*  188 */     quick_view_active = new HashSet();
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
/* 1103 */     pending_ext_urls = new HashSet();
/* 1104 */     ext_url_dispatcher = new AsyncDispatcher("Ext Urls");
/*      */     
/* 1106 */     i2p_install_active_for_url = false;
/* 1107 */     browser_install_active_for_url = false;
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
/* 1547 */     tb_installing = false;
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
/* 2921 */     gotBrowserStyle = false;
/*      */     
/* 2923 */     browserStyle = 0;
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
/* 2939 */     truncatedTextCache = new java.util.HashMap();
/*      */     
/* 2941 */     tp = new ThreadPool("GetOffSWT", 3, true);
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
/* 3124 */     qv_exts = new HashSet();
/*      */     
/*      */ 
/*      */ 
/* 3128 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "quick.view.exts", "quick.view.maxkb" }, new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String name)
/*      */       {
/*      */ 
/*      */ 
/* 3136 */         String exts_str = COConfigurationManager.getStringParameter("quick.view.exts");
/* 3137 */         int max_bytes = COConfigurationManager.getIntParameter("quick.view.maxkb") * 1024;
/*      */         
/* 3139 */         String[] bits = exts_str.split("[;, ]");
/*      */         
/* 3141 */         Set<String> exts = new HashSet();
/*      */         
/* 3143 */         for (String bit : bits)
/*      */         {
/* 3145 */           bit = bit.trim();
/*      */           
/* 3147 */           if (bit.startsWith("."))
/*      */           {
/* 3149 */             bit = bit.substring(1);
/*      */           }
/*      */           
/* 3152 */           if (bit.length() > 0)
/*      */           {
/* 3154 */             exts.add(bit.toLowerCase());
/*      */           }
/*      */         }
/*      */         
/* 3158 */         Utils.access$902(exts);
/* 3159 */         Utils.access$1002(max_bytes);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static boolean isQuickViewSupported(org.gudy.azureus2.core3.disk.DiskManagerFileInfo file)
/*      */   {
/* 3168 */     String ext = file.getExtension().toLowerCase();
/*      */     
/* 3170 */     if (ext.startsWith("."))
/*      */     {
/* 3172 */       ext = ext.substring(1);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 3177 */     if (ext.equals("rar"))
/*      */     {
/* 3179 */       return true;
/*      */     }
/*      */     
/* 3182 */     if (qv_exts.contains(ext))
/*      */     {
/* 3184 */       if (file.getLength() <= qv_max_bytes)
/*      */       {
/* 3186 */         return true;
/*      */       }
/*      */     }
/*      */     
/* 3190 */     return false;
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
/*      */ 
/*      */ 
/*      */   public static void setQuickViewActive(org.gudy.azureus2.core3.disk.DiskManagerFileInfo file, boolean active)
/*      */   {
/* 3208 */     synchronized (quick_view_active)
/*      */     {
/* 3210 */       if (!active)
/*      */       {
/* 3212 */         quick_view_active.remove(file);
/*      */         
/* 3214 */         return;
/*      */       }
/*      */       
/* 3217 */       if (quick_view_active.contains(file))
/*      */       {
/* 3219 */         return;
/*      */       }
/*      */       
/* 3222 */       String ext = file.getExtension().toLowerCase();
/*      */       
/* 3224 */       boolean file_complete = file.getDownloaded() == file.getLength();
/*      */       
/* 3226 */       if (ext.equals(".rar"))
/*      */       {
/* 3228 */         quick_view_active.add(file);
/*      */         
/* 3230 */         quickViewRAR(file);
/*      */ 
/*      */ 
/*      */       }
/* 3234 */       else if (file_complete)
/*      */       {
/* 3236 */         quickView(file);
/*      */       }
/*      */       else
/*      */       {
/* 3240 */         quick_view_active.add(file);
/*      */         
/* 3242 */         if (file.isSkipped())
/*      */         {
/* 3244 */           file.setSkipped(false);
/*      */         }
/*      */         
/* 3247 */         file.setPriority(1);
/*      */         
/* 3249 */         org.gudy.azureus2.core3.disk.DiskManagerFileInfo[] all_files = file.getDownloadManager().getDiskManagerFileInfoSet().getFiles();
/*      */         
/* 3251 */         for (org.gudy.azureus2.core3.disk.DiskManagerFileInfo f : all_files)
/*      */         {
/* 3253 */           if (!quick_view_active.contains(f))
/*      */           {
/* 3255 */             f.setPriority(0);
/*      */           }
/*      */         }
/*      */         
/* 3259 */         if (quick_view_event == null)
/*      */         {
/* 3261 */           quick_view_event = SimpleTimer.addPeriodicEvent("qv_checker", 5000L, new TimerEventPerformer()
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */             public void perform(TimerEvent event)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 3271 */               synchronized (Utils.quick_view_active)
/*      */               {
/* 3273 */                 Iterator<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> it = Utils.quick_view_active.iterator();
/*      */                 
/* 3275 */                 while (it.hasNext())
/*      */                 {
/* 3277 */                   org.gudy.azureus2.core3.disk.DiskManagerFileInfo file = (org.gudy.azureus2.core3.disk.DiskManagerFileInfo)it.next();
/*      */                   
/* 3279 */                   if (file.getDownloadManager().isDestroyed())
/*      */                   {
/* 3281 */                     it.remove();
/*      */ 
/*      */ 
/*      */                   }
/* 3285 */                   else if (file.getDownloaded() == file.getLength())
/*      */                   {
/* 3287 */                     Utils.quickView(file);
/*      */                     
/* 3289 */                     it.remove();
/*      */                   }
/*      */                 }
/*      */                 
/*      */ 
/* 3294 */                 if (Utils.quick_view_active.isEmpty())
/*      */                 {
/* 3296 */                   Utils.quick_view_event.cancel();
/*      */                   
/* 3298 */                   Utils.access$1302(null);
/*      */                 }
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 3307 */       if (!file_complete)
/*      */       {
/* 3309 */         execSWTThreadLater(10, new Runnable()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void run()
/*      */           {
/*      */ 
/* 3316 */             MessageBoxShell mb = new MessageBoxShell(32, MessageText.getString("quick.view.scheduled.title"), MessageText.getString("quick.view.scheduled.text"));
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3322 */             mb.setDefaultButtonUsingStyle(32);
/* 3323 */             mb.setRemember("quick.view.inform.activated.id", false, MessageText.getString("label.dont.show.again"));
/* 3324 */             mb.setLeftImage(2);
/* 3325 */             mb.open(null);
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static void quickView(final org.gudy.azureus2.core3.disk.DiskManagerFileInfo file)
/*      */   {
/*      */     try
/*      */     {
/* 3337 */       File target_file = file.getFile(true);
/*      */       
/* 3339 */       final String contents = org.gudy.azureus2.core3.util.FileUtil.readFileAsString(target_file, qv_max_bytes);
/*      */       
/* 3341 */       execSWTThread(new Runnable()
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/*      */ 
/* 3347 */           if (Utils.getExplicitLauncher(this.val$target_file.getName()) != null)
/*      */           {
/* 3349 */             Utils.launch(this.val$target_file.getAbsolutePath());
/*      */           }
/*      */           else
/*      */           {
/* 3353 */             DownloadManager dm = file.getDownloadManager();
/*      */             
/*      */             Image image;
/*      */             try
/*      */             {
/* 3358 */               java.io.InputStream is = null;
/*      */               try
/*      */               {
/* 3361 */                 is = new java.io.FileInputStream(this.val$target_file);
/*      */                 
/* 3363 */                 image = new Image(Utils.getDisplay(), is);
/*      */               }
/*      */               finally
/*      */               {
/* 3367 */                 if (is != null)
/*      */                 {
/* 3369 */                   is.close();
/*      */                 }
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {
/* 3374 */               image = null;
/*      */             }
/*      */             
/* 3377 */             if (image != null)
/*      */             {
/* 3379 */               new ImageViewerWindow(MessageText.getString("MainWindow.menu.quick_view") + ": " + this.val$target_file.getName(), MessageText.getString("MainWindow.menu.quick_view.msg", new String[] { this.val$target_file.getName(), dm.getDisplayName() }), image);
/*      */ 
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/*      */ 
/*      */ 
/* 3387 */               new TextViewerWindow(MessageText.getString("MainWindow.menu.quick_view") + ": " + this.val$target_file.getName(), MessageText.getString("MainWindow.menu.quick_view.msg", new String[] { this.val$target_file.getName(), dm.getDisplayName() }), contents, false);
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */ 
/*      */       });
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 3399 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static void quickViewRAR(final org.gudy.azureus2.core3.disk.DiskManagerFileInfo file)
/*      */   {
/* 3407 */     boolean went_async = false;
/*      */     try
/*      */     {
/* 3410 */       final org.gudy.azureus2.plugins.disk.DiskManagerFileInfo plugin_file = org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils.wrap(file);
/*      */       
/* 3412 */       final RARTOCDecoder decoder = new RARTOCDecoder(new org.gudy.azureus2.core3.util.RARTOCDecoder.DataProvider()
/*      */       {
/*      */         private long file_position;
/*      */         
/*      */ 
/* 3417 */         private long file_size = this.val$file.getLength();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public int read(final byte[] buffer)
/*      */           throws IOException
/*      */         {
/* 3425 */           long read_from = this.file_position;
/* 3426 */           int read_length = buffer.length;
/*      */           
/* 3428 */           long read_to = Math.min(this.file_size, read_from + read_length);
/*      */           
/* 3430 */           read_length = (int)(read_to - read_from);
/*      */           
/* 3432 */           if (read_length <= 0)
/*      */           {
/* 3434 */             return -1;
/*      */           }
/*      */           
/* 3437 */           final int f_read_length = read_length;
/*      */           try
/*      */           {
/* 3440 */             final AESemaphore sem = new AESemaphore("rarwait");
/*      */             
/* 3442 */             final Object[] result = { null };
/*      */             
/* 3444 */             plugin_file.createRandomReadRequest(read_from, read_length, false, new org.gudy.azureus2.plugins.disk.DiskManagerListener()
/*      */             {
/*      */               private int buffer_pos;
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               public void eventOccurred(DiskManagerEvent event)
/*      */               {
/* 3454 */                 int event_type = event.getType();
/*      */                 
/* 3456 */                 if (event_type == 1)
/*      */                 {
/* 3458 */                   PooledByteBuffer pooled_buffer = event.getBuffer();
/*      */                   try
/*      */                   {
/* 3461 */                     byte[] data = pooled_buffer.toByteArray();
/*      */                     
/* 3463 */                     System.arraycopy(data, 0, buffer, this.buffer_pos, data.length);
/*      */                     
/* 3465 */                     this.buffer_pos += data.length;
/*      */                     
/* 3467 */                     if (this.buffer_pos == f_read_length)
/*      */                     {
/* 3469 */                       sem.release();
/*      */                     }
/*      */                   }
/*      */                   finally
/*      */                   {
/* 3474 */                     pooled_buffer.returnToPool();
/*      */                   }
/* 3476 */                 } else if (event_type == 2)
/*      */                 {
/* 3478 */                   result[0] = event.getFailure();
/*      */                   
/* 3480 */                   sem.release();
/*      */                 }
/*      */                 
/*      */               }
/* 3484 */             });
/* 3485 */             sem.reserve();
/*      */             
/* 3487 */             if ((result[0] instanceof Throwable))
/*      */             {
/* 3489 */               throw ((Throwable)result[0]);
/*      */             }
/*      */             
/* 3492 */             this.file_position += read_length;
/*      */             
/* 3494 */             return read_length;
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 3498 */             throw new IOException("read failed: " + Debug.getNestedExceptionMessage(e));
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void skip(long bytes)
/*      */           throws IOException
/*      */         {
/* 3508 */           this.file_position += bytes;
/*      */         }
/*      */         
/* 3511 */       });
/* 3512 */       new org.gudy.azureus2.core3.util.AEThread2("rardecoder")
/*      */       {
/*      */         public void run()
/*      */         {
/*      */           try
/*      */           {
/* 3518 */             decoder.analyse(new org.gudy.azureus2.core3.util.RARTOCDecoder.TOCResultHandler()
/*      */             {
/*      */               private TextViewerWindow viewer;
/*      */               
/* 3522 */               private List<String> lines = new ArrayList();
/*      */               
/* 3524 */               private int pw_entries = 0;
/* 3525 */               private int pw_text = 0;
/*      */               
/* 3527 */               private volatile boolean abandon = false;
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               public void entryRead(String name, long size, boolean password)
/*      */                 throws IOException
/*      */               {
/* 3537 */                 if (this.abandon)
/*      */                 {
/* 3539 */                   throw new IOException("Operation abandoned");
/*      */                 }
/*      */                 
/* 3542 */                 String line = name + ":    " + org.gudy.azureus2.core3.util.DisplayFormatters.formatByteCountToKiBEtc(size);
/*      */                 
/* 3544 */                 if (password)
/*      */                 {
/* 3546 */                   line = line + "    **** password protected ****";
/*      */                   
/* 3548 */                   this.pw_entries += 1;
/*      */                 }
/*      */                 
/* 3551 */                 if ((password) || (name.toLowerCase().contains("password")))
/*      */                 {
/* 3553 */                   line = "*\t" + line;
/*      */                   
/* 3555 */                   this.pw_text += 1;
/*      */                 }
/*      */                 else
/*      */                 {
/* 3559 */                   line = " \t" + line;
/*      */                 }
/*      */                 
/* 3562 */                 appendLine(line, false);
/*      */               }
/*      */               
/*      */ 
/*      */               public void complete()
/*      */               {
/* 3568 */                 appendLine("Done", true);
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */               public void failed(IOException error)
/*      */               {
/* 3575 */                 appendLine("Failed: " + Debug.getNestedExceptionMessage(error), true);
/*      */               }
/*      */               
/*      */ 
/*      */               private String getInfo()
/*      */               {
/* 3581 */                 if (this.pw_entries > 0)
/*      */                 {
/* 3583 */                   return this.pw_entries + " password protected file(s) found";
/*      */                 }
/* 3585 */                 if (this.pw_text > 0)
/*      */                 {
/* 3587 */                   return this.pw_text + " file(s) mentioning 'password' found";
/*      */                 }
/*      */                 
/* 3590 */                 return "";
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */               private void appendLine(final String line, final boolean complete)
/*      */               {
/* 3598 */                 Utils.execSWTThread(new Runnable()
/*      */                 {
/*      */ 
/*      */                   public void run()
/*      */                   {
/*      */ 
/* 3604 */                     Utils.22.1.this.lines.add(line);
/*      */                     
/* 3606 */                     StringBuilder content = new StringBuilder();
/*      */                     
/* 3608 */                     for (String l : Utils.22.1.this.lines)
/*      */                     {
/* 3610 */                       content.append(l).append("\r\n");
/*      */                     }
/*      */                     
/* 3613 */                     if (!complete)
/*      */                     {
/* 3615 */                       content.append("processing...");
/*      */                     }
/*      */                     else
/*      */                     {
/* 3619 */                       String info = Utils.22.1.this.getInfo();
/*      */                       
/* 3621 */                       if (info.length() > 0)
/*      */                       {
/* 3623 */                         content.append(info).append("\r\n");
/*      */                       }
/*      */                     }
/*      */                     
/* 3627 */                     if (Utils.22.1.this.viewer == null)
/*      */                     {
/* 3629 */                       File target_file = Utils.22.this.val$file.getFile(true);
/*      */                       
/* 3631 */                       DownloadManager dm = Utils.22.this.val$file.getDownloadManager();
/*      */                       
/* 3633 */                       Utils.22.1.this.viewer = new TextViewerWindow(MessageText.getString("MainWindow.menu.quick_view") + ": " + target_file.getName(), MessageText.getString("MainWindow.menu.quick_view.msg", new String[] { target_file.getName(), dm.getDisplayName() }), content.toString(), false);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                     }
/* 3642 */                     else if (Utils.22.1.this.viewer.isDisposed())
/*      */                     {
/* 3644 */                       Utils.22.1.this.abandon = true;
/*      */                     }
/*      */                     else
/*      */                     {
/* 3648 */                       Utils.22.1.this.viewer.setText(content.toString());
/*      */                     }
/*      */                     
/*      */                   }
/*      */                 });
/*      */               }
/*      */             });
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 3658 */             Debug.out(e);
/*      */           }
/*      */           finally
/*      */           {
/* 3662 */             synchronized (Utils.quick_view_active)
/*      */             {
/* 3664 */               Utils.quick_view_active.remove(file);
/*      */             }
/*      */             
/*      */           }
/*      */         }
/* 3669 */       }.start();
/* 3670 */       went_async = true;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 3674 */       Debug.out(e);
/*      */     }
/*      */     finally
/*      */     {
/* 3678 */       if (!went_async)
/*      */       {
/* 3680 */         synchronized (quick_view_active)
/*      */         {
/* 3682 */           quick_view_active.remove(file);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Sash createSash(Composite form, int SASH_WIDTH)
/*      */   {
/* 3693 */     Sash sash = new Sash(form, 256);
/* 3694 */     Image image = new Image(sash.getDisplay(), 9, SASH_WIDTH);
/* 3695 */     ImageData imageData = image.getImageData();
/* 3696 */     int[] row = new int[imageData.width];
/* 3697 */     for (int i = 0; i < row.length; i++) {
/* 3698 */       if (imageData.depth == 16) {
/* 3699 */         row[i] = (i % 3 != 0 ? 2078209981 : -554189333);
/*      */       } else {
/* 3701 */         row[i] = (i % 3 != 0 ? 14737632 : 8421504);
/* 3702 */         if (imageData.depth == 32) {
/* 3703 */           row[i] = ((row[i] & 0xFF) + (row[i] << 8));
/*      */         }
/*      */       }
/*      */     }
/* 3707 */     for (int y = 1; y < imageData.height - 1; y++) {
/* 3708 */       imageData.setPixels(0, y, row.length, row, 0);
/*      */     }
/* 3710 */     Arrays.fill(row, imageData.depth == 16 ? 2078209981 : -522133280);
/* 3711 */     imageData.setPixels(0, 0, row.length, row, 0);
/* 3712 */     imageData.setPixels(0, imageData.height - 1, row.length, row, 0);
/* 3713 */     image.dispose();
/* 3714 */     image = new Image(sash.getDisplay(), imageData);
/* 3715 */     sash.setBackgroundImage(image);
/* 3716 */     sash.addDisposeListener(new DisposeListener() {
/*      */       public void widgetDisposed(DisposeEvent e) {
/* 3718 */         this.val$sash.getBackgroundImage().dispose();
/*      */       }
/*      */       
/* 3721 */     });
/* 3722 */     return sash;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static Control getCursorControl()
/*      */   {
/* 3729 */     Display d = getDisplay();
/* 3730 */     Point cursorLocation = d.getCursorLocation();
/* 3731 */     Control cursorControl = d.getCursorControl();
/*      */     
/* 3733 */     if ((cursorControl instanceof Composite)) {
/* 3734 */       return getCursorControl((Composite)cursorControl, cursorLocation);
/*      */     }
/*      */     
/* 3737 */     return cursorControl;
/*      */   }
/*      */   
/*      */   public static Control getCursorControl(Composite parent, Point cursorLocation) {
/* 3741 */     for (Control con : parent.getChildren()) {
/* 3742 */       Rectangle bounds = con.getBounds();
/* 3743 */       Point displayLoc = con.toDisplay(0, 0);
/* 3744 */       bounds.x = displayLoc.x;
/* 3745 */       bounds.y = displayLoc.y;
/* 3746 */       boolean found = bounds.contains(cursorLocation);
/* 3747 */       if (found) {
/* 3748 */         if ((con instanceof Composite)) {
/* 3749 */           return getCursorControl((Composite)con, cursorLocation);
/*      */         }
/* 3751 */         return con;
/*      */       }
/*      */     }
/* 3754 */     return parent;
/*      */   }
/*      */   
/*      */   public static void relayoutUp(Composite c) {
/* 3758 */     while ((c != null) && (!c.isDisposed())) {
/* 3759 */       Composite newParent = c.getParent();
/* 3760 */       if (newParent == null) {
/*      */         break;
/*      */       }
/* 3763 */       newParent.layout(new Control[] { c });
/* 3764 */       c = newParent;
/*      */     }
/*      */   }
/*      */   
/*      */   public static void updateScrolledComposite(ScrolledComposite sc) {
/* 3769 */     Control content = sc.getContent();
/* 3770 */     if ((content != null) && (!content.isDisposed())) {
/* 3771 */       Rectangle r = sc.getClientArea();
/* 3772 */       sc.setMinSize(content.computeSize(r.width, -1));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void maintainSashPanelWidth(final SashForm sash, Composite comp, int[] default_weights, final String config_key)
/*      */   {
/* 3783 */     final boolean is_lhs = comp == sash.getChildren()[0];
/*      */     
/* 3785 */     String str = COConfigurationManager.getStringParameter(config_key, default_weights[0] + "," + default_weights[1]);
/*      */     try
/*      */     {
/* 3788 */       String[] bits = str.split(",");
/*      */       
/* 3790 */       sash.setWeights(new int[] { Integer.parseInt(bits[0]), Integer.parseInt(bits[1]) });
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 3794 */       sash.setWeights(default_weights);
/*      */     }
/*      */     
/* 3797 */     Listener sash_listener = new Listener()
/*      */     {
/*      */       private int comp_weight;
/*      */       
/*      */ 
/*      */       private int comp_width;
/*      */       
/*      */ 
/*      */       public void handleEvent(Event ev)
/*      */       {
/* 3807 */         if (ev.widget == this.val$comp)
/*      */         {
/* 3809 */           int[] weights = sash.getWeights();
/*      */           
/* 3811 */           int current_weight = weights[1];
/*      */           
/* 3813 */           if (this.comp_weight != current_weight)
/*      */           {
/* 3815 */             COConfigurationManager.setParameter(config_key, weights[0] + "," + weights[1]);
/*      */             
/*      */ 
/*      */ 
/* 3819 */             this.comp_weight = current_weight;
/*      */             
/*      */ 
/*      */ 
/* 3823 */             this.comp_width = this.val$comp.getBounds().width;
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */         }
/* 3829 */         else if (this.comp_width > 0)
/*      */         {
/* 3831 */           int width = sash.getClientArea().width;
/*      */           
/* 3833 */           if (width < 20)
/*      */           {
/* 3835 */             width = 20;
/*      */           }
/*      */           
/* 3838 */           double ratio = this.comp_width / width;
/*      */           
/* 3840 */           this.comp_weight = ((int)(ratio * 1000.0D));
/*      */           
/* 3842 */           if (this.comp_weight < 20)
/*      */           {
/* 3844 */             this.comp_weight = 20;
/*      */           }
/* 3846 */           else if (this.comp_weight > 980)
/*      */           {
/* 3848 */             this.comp_weight = 980;
/*      */           }
/*      */           
/* 3851 */           if (is_lhs)
/*      */           {
/* 3853 */             sash.setWeights(new int[] { this.comp_weight, 1000 - this.comp_weight });
/*      */           }
/*      */           else
/*      */           {
/* 3857 */             sash.setWeights(new int[] { 1000 - this.comp_weight, this.comp_weight });
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */       }
/* 3863 */     };
/* 3864 */     comp.addListener(11, sash_listener);
/* 3865 */     sash.addListener(11, sash_listener);
/*      */   }
/*      */   
/*      */   private static Point getDPI() {
/* 3869 */     if (dpi == null) {
/* 3870 */       boolean enableForceDPI = COConfigurationManager.getBooleanParameter("enable.ui.forceDPI");
/* 3871 */       if (enableForceDPI) {
/* 3872 */         int forceDPI = COConfigurationManager.getIntParameter("Force DPI");
/* 3873 */         if (forceDPI > 0) {
/* 3874 */           dpi = new Point(forceDPI, forceDPI);
/* 3875 */           return dpi;
/*      */         }
/*      */       }
/* 3878 */       Display display = getDisplay();
/* 3879 */       if (display == null) {
/* 3880 */         return new Point(0, 0);
/*      */       }
/* 3882 */       dpi = getDPIRaw(display);
/* 3883 */       COConfigurationManager.setIntDefault("Force DPI", dpi.x);
/* 3884 */       if ((dpi.x <= 96) || (dpi.y <= 96)) {
/* 3885 */         dpi = new Point(0, 0);
/*      */       }
/*      */     }
/* 3888 */     return dpi;
/*      */   }
/*      */   
/* 3891 */   private static boolean logged_invalid_dpi = false;
/*      */   
/*      */ 
/*      */ 
/*      */   public static Point getDPIRaw(Device device)
/*      */   {
/* 3897 */     Point p = device.getDPI();
/*      */     
/* 3899 */     if ((p.x < 0) || (p.y < 0) || (p.x > 8192) || (p.y > 8192))
/*      */     {
/* 3901 */       if (!logged_invalid_dpi)
/*      */       {
/* 3903 */         logged_invalid_dpi = true;
/*      */         
/* 3905 */         Debug.outNoStack("Invalid DPI: " + p);
/*      */       }
/*      */       
/* 3908 */       return new Point(96, 96);
/*      */     }
/*      */     
/* 3911 */     return p;
/*      */   }
/*      */   
/*      */   public static int adjustPXForDPI(int unadjustedPX)
/*      */   {
/* 3916 */     if (unadjustedPX == 0) {
/* 3917 */       return unadjustedPX;
/*      */     }
/* 3919 */     int xDPI = getDPI().x;
/* 3920 */     if (xDPI == 0) {
/* 3921 */       return unadjustedPX;
/*      */     }
/* 3923 */     return unadjustedPX * xDPI / DEFAULT_DPI;
/*      */   }
/*      */   
/*      */   public static Rectangle adjustPXForDPI(Rectangle bounds) {
/* 3927 */     Point dpi = getDPI();
/* 3928 */     if (dpi.x == 0) {
/* 3929 */       return bounds;
/*      */     }
/* 3931 */     return new Rectangle(bounds.x * dpi.x / DEFAULT_DPI, bounds.y * dpi.y / DEFAULT_DPI, bounds.width * dpi.x / DEFAULT_DPI, bounds.height * dpi.y / DEFAULT_DPI);
/*      */   }
/*      */   
/*      */ 
/*      */   public static Point adjustPXForDPI(Point size)
/*      */   {
/* 3937 */     Point dpi = getDPI();
/* 3938 */     if (dpi.x == 0) {
/* 3939 */       return size;
/*      */     }
/* 3941 */     return new Point(size.x * dpi.x / DEFAULT_DPI, size.y * dpi.y / DEFAULT_DPI);
/*      */   }
/*      */   
/*      */   public static void adjustPXForDPI(FormData fd)
/*      */   {
/* 3946 */     Point dpi = getDPI();
/* 3947 */     if (dpi.x == 0) {
/* 3948 */       return;
/*      */     }
/* 3950 */     adjustPXForDPI(fd.left);
/* 3951 */     adjustPXForDPI(fd.right);
/* 3952 */     adjustPXForDPI(fd.top);
/* 3953 */     adjustPXForDPI(fd.bottom);
/* 3954 */     if (fd.width > 0) {
/* 3955 */       fd.width = adjustPXForDPI(fd.width);
/*      */     }
/* 3957 */     if (fd.height > 0) {
/* 3958 */       fd.height = adjustPXForDPI(fd.height);
/*      */     }
/*      */   }
/*      */   
/*      */   public static void adjustPXForDPI(FormAttachment fa) {
/* 3963 */     if (fa == null) {
/* 3964 */       return;
/*      */     }
/* 3966 */     if (fa.offset != 0) {
/* 3967 */       fa.offset = adjustPXForDPI(fa.offset);
/*      */     }
/*      */   }
/*      */   
/*      */   public static void setLayoutData(Control widget, GridData layoutData) {
/* 3972 */     adjustPXForDPI(layoutData);
/* 3973 */     widget.setLayoutData(layoutData);
/*      */   }
/*      */   
/*      */   private static void adjustPXForDPI(GridData layoutData) {
/* 3977 */     Point dpi = getDPI();
/* 3978 */     if (dpi.x == 0) {
/* 3979 */       return;
/*      */     }
/* 3981 */     if (layoutData.heightHint > 0) {
/* 3982 */       layoutData.heightHint = adjustPXForDPI(layoutData.heightHint);
/*      */     }
/* 3984 */     if (layoutData.horizontalIndent > 0) {
/* 3985 */       layoutData.horizontalIndent = adjustPXForDPI(layoutData.horizontalIndent);
/*      */     }
/* 3987 */     if (layoutData.minimumHeight > 0) {
/* 3988 */       layoutData.minimumHeight = adjustPXForDPI(layoutData.minimumHeight);
/*      */     }
/* 3990 */     if (layoutData.verticalIndent > 0) {
/* 3991 */       layoutData.verticalIndent = adjustPXForDPI(layoutData.verticalIndent);
/*      */     }
/* 3993 */     if (layoutData.minimumWidth > 0) {
/* 3994 */       layoutData.minimumWidth = adjustPXForDPI(layoutData.minimumWidth);
/*      */     }
/* 3996 */     if (layoutData.widthHint > 0) {
/* 3997 */       layoutData.widthHint = adjustPXForDPI(layoutData.widthHint);
/*      */     }
/*      */   }
/*      */   
/*      */   public static void setLayoutData(Control widget, FormData layoutData) {
/* 4002 */     adjustPXForDPI(layoutData);
/* 4003 */     widget.setLayoutData(layoutData);
/*      */   }
/*      */   
/*      */   public static void setLayoutData(Control item, RowData rowData) {
/* 4007 */     if (rowData.height > 0) {
/* 4008 */       rowData.height = adjustPXForDPI(rowData.height);
/*      */     }
/* 4010 */     if (rowData.width > 0) {
/* 4011 */       rowData.width = adjustPXForDPI(rowData.width);
/*      */     }
/* 4013 */     item.setLayoutData(rowData);
/*      */   }
/*      */   
/*      */   public static void setLayoutData(BufferedLabel label, GridData gridData) {
/* 4017 */     adjustPXForDPI(gridData);
/* 4018 */     label.setLayoutData(gridData);
/*      */   }
/*      */   
/*      */   public static void adjustPXForDPI(Object layoutData) {
/* 4022 */     if ((layoutData instanceof GridData)) {
/* 4023 */       GridData gd = (GridData)layoutData;
/* 4024 */       adjustPXForDPI(gd);
/* 4025 */     } else if ((layoutData instanceof FormData)) {
/* 4026 */       FormData fd = (FormData)layoutData;
/* 4027 */       adjustPXForDPI(fd);
/* 4028 */     } else if ((layoutData instanceof RowData)) {
/* 4029 */       RowData fd = (RowData)layoutData;
/* 4030 */       adjustPXForDPI(fd);
/*      */     }
/*      */   }
/*      */   
/* 4034 */   private static final WeakHashMap<Image, String> scaled_images = new WeakHashMap();
/* 4035 */   private static int scaled_imaged_check_count = 0;
/*      */   
/*      */ 
/*      */ 
/*      */   public static boolean adjustPXForDPIRequired(Image image)
/*      */   {
/* 4041 */     Point dpi = getDPI();
/* 4042 */     if (dpi.x > 0) {
/* 4043 */       return !scaled_images.containsKey(image);
/*      */     }
/* 4045 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Image adjustPXForDPI(Display display, Image image)
/*      */   {
/* 4054 */     Point dpi = getDPI();
/*      */     
/* 4056 */     if (dpi.x > 0) {
/*      */       try
/*      */       {
/* 4059 */         Rectangle bounds = image.getBounds();
/* 4060 */         Rectangle newBounds = adjustPXForDPI(bounds);
/*      */         
/* 4062 */         ImageData scaledTo = image.getImageData().scaledTo(newBounds.width, newBounds.height);
/*      */         
/* 4064 */         Image newImage = new Image(display, scaledTo);
/*      */         
/* 4066 */         if (scaled_imaged_check_count++ % 100 == 0) {
/* 4067 */           Iterator<Image> it = scaled_images.keySet().iterator();
/* 4068 */           while (it.hasNext()) {
/* 4069 */             if (((Image)it.next()).isDisposed()) {
/* 4070 */               it.remove();
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 4075 */         scaled_images.put(newImage, "");
/*      */         
/* 4077 */         image.dispose();
/*      */         
/* 4079 */         return newImage;
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 4083 */         Debug.out("Image DPI adjustment failed: " + Debug.getNestedExceptionMessage(e));
/*      */       }
/*      */     }
/*      */     
/* 4087 */     return image;
/*      */   }
/*      */   
/*      */   public static void setLayout(Composite composite, GridLayout layout) {
/* 4091 */     Point dpi = getDPI();
/* 4092 */     if (dpi.x == 0) {
/* 4093 */       composite.setLayout(layout);
/* 4094 */       return;
/*      */     }
/*      */     
/* 4097 */     layout.marginBottom = adjustPXForDPI(layout.marginBottom);
/* 4098 */     layout.marginHeight = adjustPXForDPI(layout.marginHeight);
/* 4099 */     layout.marginLeft = adjustPXForDPI(layout.marginLeft);
/* 4100 */     layout.marginRight = adjustPXForDPI(layout.marginRight);
/* 4101 */     layout.marginTop = adjustPXForDPI(layout.marginTop);
/* 4102 */     layout.marginWidth = adjustPXForDPI(layout.marginWidth);
/* 4103 */     layout.horizontalSpacing = adjustPXForDPI(layout.horizontalSpacing);
/* 4104 */     layout.verticalSpacing = adjustPXForDPI(layout.verticalSpacing);
/*      */     
/* 4106 */     composite.setLayout(layout);
/*      */   }
/*      */   
/*      */   public static void setLayout(Composite composite, RowLayout layout) {
/* 4110 */     Point dpi = getDPI();
/* 4111 */     if (dpi.x == 0) {
/* 4112 */       composite.setLayout(layout);
/* 4113 */       return;
/*      */     }
/*      */     
/* 4116 */     layout.marginBottom = adjustPXForDPI(layout.marginBottom);
/* 4117 */     layout.marginHeight = adjustPXForDPI(layout.marginHeight);
/* 4118 */     layout.marginLeft = adjustPXForDPI(layout.marginLeft);
/* 4119 */     layout.marginRight = adjustPXForDPI(layout.marginRight);
/* 4120 */     layout.marginTop = adjustPXForDPI(layout.marginTop);
/* 4121 */     layout.marginWidth = adjustPXForDPI(layout.marginWidth);
/* 4122 */     layout.spacing = adjustPXForDPI(layout.spacing);
/*      */     
/*      */ 
/* 4125 */     composite.setLayout(layout);
/*      */   }
/*      */   
/*      */   public static void setClipping(GC gc, Rectangle r) {
/* 4129 */     if (r == null) {
/* 4130 */       if (isGTK3)
/*      */       {
/* 4132 */         gc.setClipping((org.eclipse.swt.graphics.Path)null);
/*      */       }
/*      */       else {
/* 4135 */         gc.setClipping((Rectangle)null);
/*      */       }
/* 4137 */       return;
/*      */     }
/* 4139 */     gc.setClipping(r.x, r.y, r.width, r.height);
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public static boolean isInstallingTorBrowser()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: getstatic 2237	org/gudy/azureus2/ui/swt/Utils:pending_ext_urls	Ljava/util/Set;
/*      */     //   3: dup
/*      */     //   4: astore_0
/*      */     //   5: monitorenter
/*      */     //   6: getstatic 2233	org/gudy/azureus2/ui/swt/Utils:tb_installing	Z
/*      */     //   9: aload_0
/*      */     //   10: monitorexit
/*      */     //   11: ireturn
/*      */     //   12: astore_1
/*      */     //   13: aload_0
/*      */     //   14: monitorexit
/*      */     //   15: aload_1
/*      */     //   16: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1552	-> byte code offset #0
/*      */     //   Java source line #1554	-> byte code offset #6
/*      */     //   Java source line #1555	-> byte code offset #12
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   4	10	0	Ljava/lang/Object;	Object
/*      */     //   12	4	1	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   6	11	12	finally
/*      */     //   12	15	12	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public static boolean isQuickViewActive(org.gudy.azureus2.core3.disk.DiskManagerFileInfo file)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: getstatic 2238	org/gudy/azureus2/ui/swt/Utils:quick_view_active	Ljava/util/Set;
/*      */     //   3: dup
/*      */     //   4: astore_1
/*      */     //   5: monitorenter
/*      */     //   6: getstatic 2238	org/gudy/azureus2/ui/swt/Utils:quick_view_active	Ljava/util/Set;
/*      */     //   9: aload_0
/*      */     //   10: invokeinterface 2677 2 0
/*      */     //   15: aload_1
/*      */     //   16: monitorexit
/*      */     //   17: ireturn
/*      */     //   18: astore_2
/*      */     //   19: aload_1
/*      */     //   20: monitorexit
/*      */     //   21: aload_2
/*      */     //   22: athrow
/*      */     // Line number table:
/*      */     //   Java source line #3197	-> byte code offset #0
/*      */     //   Java source line #3199	-> byte code offset #6
/*      */     //   Java source line #3200	-> byte code offset #18
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	23	0	file	org.gudy.azureus2.core3.disk.DiskManagerFileInfo
/*      */     //   4	16	1	Ljava/lang/Object;	Object
/*      */     //   18	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   6	17	18	finally
/*      */     //   18	21	18	finally
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/Utils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */