/*      */ package org.gudy.azureus2.ui.swt.views.table.painted;
/*      */ 
/*      */ import com.aelitis.azureus.ui.common.table.TableCellCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableStructureEventDispatcher;
/*      */ import com.aelitis.azureus.ui.common.table.TableViewFilterCheck;
/*      */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*      */ import com.aelitis.azureus.ui.common.table.impl.TableViewImpl;
/*      */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
/*      */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*      */ import com.aelitis.azureus.ui.swt.mdi.MdiEntrySWT;
/*      */ import com.aelitis.azureus.ui.swt.utils.FontUtils;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.atomic.AtomicBoolean;
/*      */ import org.eclipse.swt.SWT;
/*      */ import org.eclipse.swt.dnd.DragSource;
/*      */ import org.eclipse.swt.dnd.DragSourceEvent;
/*      */ import org.eclipse.swt.dnd.DropTarget;
/*      */ import org.eclipse.swt.dnd.DropTargetEvent;
/*      */ import org.eclipse.swt.dnd.Transfer;
/*      */ import org.eclipse.swt.events.ControlEvent;
/*      */ import org.eclipse.swt.events.DisposeEvent;
/*      */ import org.eclipse.swt.events.DisposeListener;
/*      */ import org.eclipse.swt.events.FocusEvent;
/*      */ import org.eclipse.swt.events.KeyEvent;
/*      */ import org.eclipse.swt.events.KeyListener;
/*      */ import org.eclipse.swt.events.ModifyEvent;
/*      */ import org.eclipse.swt.events.PaintEvent;
/*      */ import org.eclipse.swt.events.SelectionEvent;
/*      */ import org.eclipse.swt.graphics.Color;
/*      */ import org.eclipse.swt.graphics.Cursor;
/*      */ import org.eclipse.swt.graphics.Font;
/*      */ import org.eclipse.swt.graphics.GC;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Pattern;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.layout.FormData;
/*      */ import org.eclipse.swt.layout.GridLayout;
/*      */ import org.eclipse.swt.widgets.Canvas;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Menu;
/*      */ import org.eclipse.swt.widgets.ScrollBar;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.eclipse.swt.widgets.Text;
/*      */ import org.gudy.azureus2.core3.config.impl.ConfigurationManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.FrequencyLimitedDispatcher;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableRowMouseListener;
/*      */ import org.gudy.azureus2.ui.swt.SimpleTextEntryWindow;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.HSLColor;
/*      */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWT;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableRowSWT;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableRowSWTPaintListener;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWTFilter;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWTPanelCreator;
/*      */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewSWT_Common;
/*      */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewSWT_TabsCommon;
/*      */ 
/*      */ public class TableViewPainted extends TableViewImpl<Object> implements org.gudy.azureus2.core3.config.ParameterListener, TableViewSWT<Object>, org.gudy.azureus2.ui.swt.debug.ObfusticateImage, org.gudy.azureus2.core3.internat.MessageText.MessageTextListener
/*      */ {
/*   80 */   private static final boolean hasGetScrollBarMode = SWT.getVersion() >= 3821;
/*      */   
/*      */   private static final boolean DEBUG_ROWCHANGE = false;
/*      */   
/*      */   private static final boolean DEBUG_WITH_SHELL = false;
/*      */   
/*   86 */   public static final boolean DIRECT_DRAW = (org.gudy.azureus2.core3.util.Constants.isOSX) && (org.gudy.azureus2.ui.swt.mainwindow.SWTThread.getInstance().isRetinaDisplay());
/*      */   
/*      */ 
/*      */   private static final int DEFAULT_HEADER_HEIGHT = 27;
/*      */   
/*      */ 
/*      */   private static final boolean DEBUG_REDRAW_CLIP = false;
/*      */   
/*      */   private Composite cTable;
/*      */   
/*      */   private int loopFactor;
/*      */   
/*   98 */   protected int graphicsUpdate = configMan.getIntParameter("Graphics Update");
/*      */   
/*  100 */   protected int reOrderDelay = configMan.getIntParameter("ReOrder Delay");
/*      */   
/*  102 */   protected boolean extendedErase = configMan.getBooleanParameter("Table.extendedErase");
/*      */   
/*  104 */   private int defaultRowHeight = 17;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  109 */   LinkedHashSet<TableRowPainted> visibleRows = new LinkedHashSet();
/*      */   
/*  111 */   Object visibleRows_sync = new Object();
/*      */   
/*  113 */   Object lock = new Object();
/*      */   
/*      */ 
/*      */   protected Rectangle clientArea;
/*      */   
/*      */ 
/*      */   private boolean isVisible;
/*      */   
/*      */ 
/*      */   private Shell shell;
/*      */   
/*      */   private Color colorLine;
/*      */   
/*      */   private int headerHeight;
/*      */   
/*      */   private Canvas cHeaderArea;
/*      */   
/*      */   private Image canvasImage;
/*      */   
/*      */   private final String sDefaultSortOn;
/*      */   
/*      */   private TableViewSWT_Common tvSWTCommon;
/*      */   
/*      */   private TableViewSWT_TabsCommon tvTabsCommon;
/*      */   
/*      */   private TableViewSWTPanelCreator mainPanelCreator;
/*      */   
/*      */   private boolean isMultiSelect;
/*      */   
/*      */   private int columnsWidth;
/*      */   
/*      */   private Menu menu;
/*      */   
/*      */   protected boolean isHeaderDragging;
/*      */   
/*      */   private TableRowPainted focusedRow;
/*      */   
/*      */   private boolean enableTabViews;
/*      */   
/*      */   private String[] tabViewRestriction;
/*      */   
/*  154 */   private boolean tabViewsExpandedByDefault = true;
/*      */   
/*      */   protected boolean isDragging;
/*      */   
/*      */   private Composite mainComposite;
/*      */   
/*  160 */   private Object heightChangeSync = new Object();
/*  161 */   private int totalHeight = 0;
/*      */   
/*      */   private boolean redrawTableScheduled;
/*      */   
/*      */   private Font fontHeaderSmall;
/*      */   
/*      */   private Font fontHeader;
/*      */   
/*      */   private ScrollBar hBar;
/*      */   private ScrollBar vBar;
/*      */   private Canvas sCanvasImage;
/*      */   
/*      */   private class RefreshTableRunnable
/*      */     extends AERunnable
/*      */   {
/*      */     private RefreshTableRunnable() {}
/*      */     
/*  178 */     private AtomicBoolean forceSortPending = new AtomicBoolean();
/*      */     
/*      */ 
/*  181 */     public void runSupport() { TableViewPainted.this.__refreshTable(this.forceSortPending.getAndSet(false)); }
/*      */     
/*      */     public void setForceSort(boolean fs) {
/*  184 */       if (fs) {
/*  185 */         this.forceSortPending.set(true);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*  190 */   private RefreshTableRunnable refreshTableRunnable = new RefreshTableRunnable(null);
/*      */   
/*  192 */   private FrequencyLimitedDispatcher refresh_dispatcher = new FrequencyLimitedDispatcher(this.refreshTableRunnable, 250);
/*      */   private FrequencyLimitedDispatcher redraw_dispatcher;
/*      */   protected boolean isFocused;
/*      */   protected float iHeightEM;
/*      */   private boolean in_swt_updateCanvasImage;
/*      */   boolean qdRowHeightChanged;
/*      */   private List<TableRowPainted> pending_rows;
/*      */   
/*      */   private class RedrawTableRunnable
/*      */     extends AERunnable
/*      */   {
/*  203 */     private AERunnable target = new AERunnable()
/*      */     {
/*      */       public void runSupport() {
/*  206 */         synchronized (TableViewPainted.this) {
/*  207 */           TableViewPainted.this.redrawTableScheduled = false;
/*      */         }
/*      */         
/*  210 */         TableViewPainted.this.visibleRowsChanged();
/*      */         
/*  212 */         if (TableViewPainted.DIRECT_DRAW) {
/*  213 */           if ((TableViewPainted.this.cTable != null) && (!TableViewPainted.this.cTable.isDisposed())) {
/*  214 */             TableViewPainted.this.cTable.redraw();
/*      */           }
/*      */         } else {
/*  217 */           if ((TableViewPainted.this.canvasImage != null) && (!TableViewPainted.this.canvasImage.isDisposed())) {
/*  218 */             TableViewPainted.this.canvasImage.dispose();
/*  219 */             TableViewPainted.this.canvasImage = null;
/*      */           }
/*  221 */           TableViewPainted.this.swt_calculateClientArea();
/*      */         }
/*      */       }
/*      */     };
/*      */     
/*      */     private RedrawTableRunnable() {}
/*      */     
/*  228 */     public void runSupport() { Utils.execSWTThread(this.target); }
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public TableViewPainted(Class<?> pluginDataSourceType, String _sTableID, String _sPropertiesPrefix, TableColumnCore[] _basicItems, String _sDefaultSortOn, int _iTableStyle)
/*      */   {
/*  260 */     super(pluginDataSourceType, _sTableID, _sPropertiesPrefix, _basicItems);this.refresh_dispatcher.setSingleThreaded();this.redraw_dispatcher = new FrequencyLimitedDispatcher(new RedrawTableRunnable(null), 100);this.redraw_dispatcher.setSingleThreaded();this.iHeightEM = -1.0F;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2574 */     this.in_swt_updateCanvasImage = false;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3075 */     this.qdRowHeightChanged = false;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3120 */     this.pending_rows = new ArrayList();setRowsSync(this.lock);this.sDefaultSortOn = _sDefaultSortOn;this.isMultiSelect = ((_iTableStyle & 0x2) != 0);this.tvSWTCommon = new TableViewSWT_Common(this)
/*      */     {
/*      */       public void widgetSelected(SelectionEvent event) {}
/*      */       
/*      */ 
/*      */ 
/*      */       public void mouseUp(TableRowCore clickedRow, TableCellCore cell, int button, int stateMask)
/*      */       {
/*  283 */         super.mouseUp(clickedRow, cell, button, stateMask);
/*      */         
/*  285 */         if (clickedRow == null) {
/*  286 */           return;
/*      */         }
/*  288 */         if (button == 1) {
/*  289 */           int keyboardModifier = stateMask & SWT.MODIFIER_MASK;
/*  290 */           if ((keyboardModifier & 0x20000) != 0)
/*      */           {
/*  292 */             TableViewPainted.this.selectRowsTo(clickedRow);
/*  293 */             return; }
/*  294 */           if (keyboardModifier == 0) {
/*  295 */             TableViewPainted.this.setSelectedRows(new TableRowCore[] { clickedRow });
/*      */             
/*      */ 
/*  298 */             return;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */       public void mouseDown(TableRowSWT clickedRow, TableCellCore cell, int button, int stateMask)
/*      */       {
/*  306 */         if (clickedRow == null) {
/*  307 */           return;
/*      */         }
/*  309 */         int keyboardModifier = stateMask & SWT.MODIFIER_MASK;
/*  310 */         if (button == 1) {
/*  311 */           if ((keyboardModifier & SWT.MOD1) != 0)
/*      */           {
/*  313 */             TableViewPainted.this.setRowSelected(clickedRow, !clickedRow.isSelected(), true);
/*      */           }
/*      */         }
/*  316 */         else if ((button == 3) && 
/*  317 */           (!TableViewPainted.this.isSelected(clickedRow)) && (keyboardModifier == 0)) {
/*  318 */           TableViewPainted.this.setSelectedRows(new TableRowCore[] { clickedRow });
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  323 */         if (TableViewPainted.this.getSelectedRowsSize() == 0) {
/*  324 */           TableViewPainted.this.setSelectedRows(new TableRowCore[] { clickedRow });
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void keyPressed(KeyEvent event)
/*      */       {
/*  332 */         if (event.keyCode == 27) {
/*  333 */           TableViewSWTFilter<?> filter = TableViewPainted.this.getSWTFilter();
/*  334 */           if (filter != null) {
/*  335 */             filter.widget.setText("");
/*      */           }
/*      */         }
/*  338 */         if (TableViewPainted.this.getComposite() != event.widget) {
/*  339 */           super.keyPressed(event);
/*  340 */           return;
/*      */         }
/*  342 */         boolean updateTable = false;
/*  343 */         if (event.keyCode == 16777217) {
/*  344 */           TableRowCore rowToSelect = TableViewPainted.this.getPreviousRow(TableViewPainted.this.focusedRow);
/*  345 */           if ((event.stateMask & 0x20000) != 0) {
/*  346 */             if ((rowToSelect != null) && (TableViewPainted.this.focusedRow != null)) {
/*  347 */               TableRowCore[] selectedRows = TableViewPainted.this.getSelectedRows();
/*  348 */               Arrays.sort(selectedRows, new com.aelitis.azureus.ui.common.table.impl.TableRowCoreSorter());
/*  349 */               boolean select = (selectedRows.length == 0) || (selectedRows[0] == TableViewPainted.this.focusedRow);
/*      */               
/*      */ 
/*      */ 
/*  353 */               if (select) {
/*  354 */                 rowToSelect.setSelected(select);
/*      */               } else {
/*  356 */                 TableRowPainted rowToUnSelect = TableViewPainted.this.focusedRow;
/*  357 */                 TableViewPainted.this.setFocusedRow(rowToSelect);
/*  358 */                 rowToUnSelect.setSelected(false);
/*      */               }
/*  360 */               updateTable = true;
/*      */             }
/*  362 */           } else if ((event.stateMask & 0x40000) != 0)
/*      */           {
/*  364 */             TableRowPainted firstRow = (TableRowPainted)TableViewPainted.this.visibleRows.iterator().next();
/*  365 */             if (firstRow != null) {
/*  366 */               int hChange = 0;
/*  367 */               if (TableViewPainted.this.isRowPartiallyVisible(firstRow)) {
/*  368 */                 hChange = firstRow.getDrawOffset().y - TableViewPainted.this.clientArea.y;
/*      */               } else {
/*  370 */                 TableRowCore prevRow = TableViewPainted.this.getPreviousRow(firstRow);
/*  371 */                 if ((prevRow != firstRow) && (prevRow != null)) {
/*  372 */                   hChange = -prevRow.getHeight();
/*      */                 }
/*      */               }
/*  375 */               TableViewPainted.this.vBar.setSelection(TableViewPainted.this.vBar.getSelection() + hChange);
/*  376 */               TableViewPainted.this.swt_vBarChanged();
/*      */             }
/*      */           } else {
/*  379 */             TableViewPainted.this.setSelectedRows(new TableRowCore[] { rowToSelect });
/*      */             
/*      */ 
/*  382 */             updateTable = true;
/*      */           }
/*  384 */         } else if (event.keyCode == 16777221) {
/*  385 */           TableRowCore row = TableViewPainted.this.focusedRow;
/*  386 */           TableRowPainted lastRow = TableViewPainted.this.getLastVisibleRow();
/*  387 */           int y = lastRow == null ? 0 : TableViewPainted.this.clientArea.y + TableViewPainted.this.clientArea.height - lastRow.getDrawOffset().y;
/*  388 */           while ((row != null) && (y < TableViewPainted.this.clientArea.height)) {
/*  389 */             y += row.getHeight();
/*  390 */             row = TableViewPainted.this.getPreviousRow(row);
/*      */           }
/*  392 */           if (row == null) {
/*  393 */             row = TableViewPainted.this.getRow(0);
/*      */           }
/*  395 */           if ((event.stateMask & 0x20000) != 0) {
/*  396 */             if (row != null) {
/*  397 */               TableViewPainted.this.selectRowsTo(row);
/*      */             }
/*  399 */           } else if (event.stateMask == 0) {
/*  400 */             TableViewPainted.this.setSelectedRows(new TableRowCore[] { row });
/*      */           }
/*      */           
/*      */ 
/*  404 */           updateTable = true;
/*  405 */         } else if (event.keyCode == 16777223) {
/*  406 */           if ((event.stateMask & 0x20000) != 0) {
/*  407 */             TableViewPainted.this.selectRowsTo(TableViewPainted.this.getRow(0));
/*  408 */           } else if (event.stateMask == 0) {
/*  409 */             TableViewPainted.this.setSelectedRows(new TableRowCore[] { TableViewPainted.this.getRow(0) });
/*      */           }
/*      */           
/*      */ 
/*  413 */           updateTable = true;
/*  414 */         } else if (event.keyCode == 16777218) {
/*  415 */           if ((event.stateMask & 0x40000) != 0)
/*      */           {
/*  417 */             TableRowPainted firstRow = (TableRowPainted)TableViewPainted.this.visibleRows.iterator().next();
/*  418 */             if (firstRow != null) {
/*  419 */               int hChange = 0;
/*  420 */               if (TableViewPainted.this.isRowPartiallyVisible(firstRow)) {
/*  421 */                 hChange = firstRow.getHeight() + (firstRow.getDrawOffset().y - TableViewPainted.this.clientArea.y);
/*      */               } else {
/*  423 */                 hChange = firstRow.getHeight();
/*      */               }
/*  425 */               TableViewPainted.this.vBar.setSelection(TableViewPainted.this.vBar.getSelection() + hChange);
/*  426 */               TableViewPainted.this.swt_vBarChanged();
/*      */             }
/*      */           } else {
/*  429 */             TableRowCore rowToSelect = TableViewPainted.this.getNextRow(TableViewPainted.this.focusedRow);
/*  430 */             if (rowToSelect != null) {
/*  431 */               if ((event.stateMask & 0x20000) != 0) {
/*  432 */                 TableRowCore[] selectedRows = TableViewPainted.this.getSelectedRows();
/*  433 */                 Arrays.sort(selectedRows, new com.aelitis.azureus.ui.common.table.impl.TableRowCoreSorter());
/*  434 */                 boolean select = (selectedRows.length == 0) || (selectedRows[(selectedRows.length - 1)] == TableViewPainted.this.focusedRow);
/*      */                 
/*  436 */                 if (select) {
/*  437 */                   rowToSelect.setSelected(select);
/*      */                 } else {
/*  439 */                   TableRowPainted rowToUnSelect = TableViewPainted.this.focusedRow;
/*  440 */                   TableViewPainted.this.setFocusedRow(rowToSelect);
/*  441 */                   rowToUnSelect.setSelected(false);
/*      */                 }
/*      */               } else {
/*  444 */                 TableViewPainted.this.setSelectedRows(new TableRowCore[] { rowToSelect });
/*      */               }
/*      */               
/*      */ 
/*  448 */               updateTable = true;
/*      */             }
/*      */           }
/*  451 */         } else if (event.keyCode == 16777222) {
/*  452 */           TableRowCore row = TableViewPainted.this.focusedRow;
/*  453 */           TableRowPainted firstRow = TableViewPainted.this.visibleRows.size() == 0 ? null : (TableRowPainted)TableViewPainted.this.visibleRows.iterator().next();
/*      */           
/*  455 */           int y = firstRow == null ? 0 : firstRow.getHeight() - (TableViewPainted.this.clientArea.y - firstRow.getDrawOffset().y);
/*  456 */           while ((row != null) && (y < TableViewPainted.this.clientArea.height)) {
/*  457 */             y += row.getHeight();
/*  458 */             TableRowCore nextRow = TableViewPainted.this.getNextRow(row);
/*  459 */             if (nextRow == null) {
/*      */               break;
/*      */             }
/*  462 */             row = nextRow;
/*      */           }
/*  464 */           if ((event.stateMask & 0x20000) != 0) {
/*  465 */             TableViewPainted.this.selectRowsTo(row);
/*  466 */           } else if (event.stateMask == 0) {
/*  467 */             TableViewPainted.this.setSelectedRows(new TableRowCore[] { row });
/*      */           }
/*      */           
/*      */ 
/*  471 */           updateTable = true;
/*  472 */         } else if (event.keyCode == 16777224) {
/*  473 */           TableRowCore lastRow = TableViewPainted.this.getRow(TableViewPainted.this.getRowCount() - 1);
/*  474 */           if ((event.stateMask & 0x20000) != 0) {
/*  475 */             TableViewPainted.this.selectRowsTo(lastRow);
/*  476 */           } else if (event.stateMask == 0) {
/*  477 */             TableViewPainted.this.setSelectedRows(new TableRowCore[] { lastRow });
/*      */           }
/*      */           
/*      */ 
/*  481 */           updateTable = true;
/*  482 */         } else if ((event.keyCode == 16777220) && (event.stateMask == 0)) {
/*  483 */           if ((event.stateMask == 0) && (TableViewPainted.this.focusedRow != null) && (!TableViewPainted.this.focusedRow.isExpanded()) && (TableViewPainted.this.canHaveSubItems())) {
/*  484 */             TableViewPainted.this.focusedRow.setExpanded(true);
/*      */           }
/*  486 */           else if (TableViewPainted.this.hBar.isEnabled()) {
/*  487 */             TableViewPainted.this.hBar.setSelection(TableViewPainted.this.hBar.getSelection() + 50);
/*  488 */             TableViewPainted.this.cTable.redraw();
/*  489 */             updateTable = true;
/*      */           }
/*      */         }
/*  492 */         else if ((event.keyCode == 16777219) && (event.stateMask == 0)) {
/*  493 */           if ((event.stateMask == 0) && (TableViewPainted.this.focusedRow != null) && (TableViewPainted.this.focusedRow.isExpanded()) && (TableViewPainted.this.canHaveSubItems())) {
/*  494 */             TableViewPainted.this.focusedRow.setExpanded(false);
/*      */           }
/*  496 */           else if (TableViewPainted.this.hBar.isEnabled()) {
/*  497 */             TableViewPainted.this.hBar.setSelection(TableViewPainted.this.hBar.getSelection() - 50);
/*  498 */             TableViewPainted.this.cTable.redraw();
/*  499 */             updateTable = true;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  504 */         if (updateTable) {
/*  505 */           TableViewPainted.this.cTable.update();
/*      */         }
/*  507 */         super.keyPressed(event);
/*      */       }
/*      */       
/*      */       public void keyReleased(KeyEvent e)
/*      */       {
/*  512 */         TableViewPainted.this.swt_calculateClientArea();
/*  513 */         TableViewPainted.this.visibleRowsChanged();
/*      */         
/*  515 */         super.keyReleased(e);
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */   protected boolean isRowPartiallyVisible(TableRowPainted row) {
/*  521 */     if (row == null) {
/*  522 */       return false;
/*      */     }
/*  524 */     Point drawOffset = row.getDrawOffset();
/*  525 */     int height = row.getHeight();
/*  526 */     return ((drawOffset.y < this.clientArea.y) && (drawOffset.y + height > this.clientArea.y)) || ((drawOffset.y < this.clientArea.y + this.clientArea.height) && (drawOffset.y + height > this.clientArea.y + this.clientArea.height));
/*      */   }
/*      */   
/*      */ 
/*      */   protected void selectRowsTo(TableRowCore clickedRow)
/*      */   {
/*  532 */     if (!this.isMultiSelect) {
/*  533 */       setSelectedRows(new TableRowCore[] { clickedRow });
/*      */       
/*      */ 
/*  536 */       return;
/*      */     }
/*  538 */     TableRowCore[] selectedRows = getSelectedRows();
/*  539 */     TableRowCore firstRow = selectedRows.length > 0 ? selectedRows[0] : getRow(0);
/*      */     
/*  541 */     TableRowCore parentFirstRow = firstRow;
/*  542 */     while (parentFirstRow.getParentRowCore() != null) {
/*  543 */       parentFirstRow = parentFirstRow.getParentRowCore();
/*      */     }
/*  545 */     TableRowCore parentClickedRow = clickedRow;
/*  546 */     while (parentClickedRow.getParentRowCore() != null)
/*  547 */       parentClickedRow = parentClickedRow.getParentRowCore();
/*      */     int endPos;
/*      */     int startPos;
/*      */     int endPos;
/*  551 */     if (parentFirstRow == parentClickedRow) {
/*  552 */       int startPos = parentFirstRow == firstRow ? -1 : firstRow.getIndex();
/*  553 */       endPos = parentClickedRow == clickedRow ? -1 : clickedRow.getIndex();
/*      */     } else {
/*  555 */       startPos = indexOf(parentFirstRow);
/*  556 */       endPos = indexOf(parentClickedRow);
/*  557 */       if ((endPos == -1) || (startPos == -1)) {
/*  558 */         return;
/*      */       }
/*      */     }
/*  561 */     ArrayList<TableRowCore> rowsToSelect = new ArrayList(Arrays.asList(selectedRows));
/*  562 */     TableRowCore curRow = firstRow;
/*      */     do {
/*  564 */       if (!rowsToSelect.contains(curRow)) {
/*  565 */         rowsToSelect.add(curRow);
/*      */       }
/*  567 */       TableRowCore newRow = startPos < endPos ? getNextRow(curRow) : getPreviousRow(curRow);
/*      */       
/*      */ 
/*  570 */       if (newRow == curRow) {
/*      */         break;
/*      */       }
/*  573 */       curRow = newRow;
/*      */ 
/*      */     }
/*  576 */     while ((curRow != clickedRow) && (curRow != null));
/*  577 */     if ((curRow != null) && (!rowsToSelect.contains(curRow))) {
/*  578 */       rowsToSelect.add(curRow);
/*      */     }
/*  580 */     setSelectedRows((TableRowCore[])rowsToSelect.toArray(new TableRowCore[0]));
/*  581 */     setFocusedRow(clickedRow);
/*      */   }
/*      */   
/*      */   protected TableRowCore getPreviousRow(TableRowCore relativeToRow) {
/*  585 */     TableRowCore rowToSelect = null;
/*  586 */     if (relativeToRow != null) {
/*  587 */       TableRowCore parentRow = relativeToRow.getParentRowCore();
/*  588 */       if (parentRow == null) {
/*  589 */         TableRowCore row = getRow(indexOf(relativeToRow) - 1);
/*  590 */         if ((row != null) && (row.isExpanded()) && (row.getSubItemCount() > 0)) {
/*  591 */           rowToSelect = row.getSubRow(row.getSubItemCount() - 1);
/*      */         } else {
/*  593 */           rowToSelect = row;
/*      */         }
/*      */       } else {
/*  596 */         int index = relativeToRow.getIndex();
/*  597 */         if (index > 0) {
/*  598 */           rowToSelect = parentRow.getSubRow(index - 1);
/*      */         } else {
/*  600 */           rowToSelect = parentRow;
/*      */         }
/*      */       }
/*      */     }
/*  604 */     if (rowToSelect == null) {
/*  605 */       rowToSelect = getRow(0);
/*      */     }
/*  607 */     return rowToSelect;
/*      */   }
/*      */   
/*      */   protected TableRowCore getNextRow(TableRowCore relativeToRow) {
/*  611 */     TableRowCore rowToSelect = null;
/*  612 */     if (relativeToRow == null) {
/*  613 */       rowToSelect = getRow(0);
/*      */     }
/*  615 */     else if ((relativeToRow.isExpanded()) && (relativeToRow.getSubItemCount() > 0)) {
/*  616 */       TableRowCore[] subRowsWithNull = relativeToRow.getSubRowsWithNull();
/*  617 */       for (TableRowCore row : subRowsWithNull) {
/*  618 */         if (row != null) {
/*  619 */           rowToSelect = row;
/*  620 */           break;
/*      */         }
/*      */       }
/*  623 */       if (rowToSelect == null) {
/*  624 */         rowToSelect = getRow(relativeToRow.getIndex() + 1);
/*      */       }
/*      */     } else {
/*  627 */       TableRowCore parentRow = relativeToRow.getParentRowCore();
/*  628 */       if (parentRow != null) {
/*  629 */         rowToSelect = parentRow.getSubRow(relativeToRow.getIndex() + 1);
/*      */         
/*  631 */         if (rowToSelect == null) {
/*  632 */           rowToSelect = getRow(parentRow.getIndex() + 1);
/*      */         }
/*      */       } else {
/*  635 */         rowToSelect = getRow(relativeToRow.getIndex() + 1);
/*      */       }
/*      */     }
/*      */     
/*  639 */     return rowToSelect;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void clipboardSelected()
/*      */   {
/*  646 */     String sToClipboard = "";
/*  647 */     TableColumnCore[] visibleColumns = getVisibleColumns();
/*  648 */     for (int j = 0; j < visibleColumns.length; j++) {
/*  649 */       if (j != 0) {
/*  650 */         sToClipboard = sToClipboard + "\t";
/*      */       }
/*  652 */       String title = MessageText.getString(visibleColumns[j].getTitleLanguageKey());
/*  653 */       sToClipboard = sToClipboard + title;
/*      */     }
/*      */     
/*  656 */     TableRowCore[] rows = getSelectedRows();
/*  657 */     for (TableRowCore row : rows) {
/*  658 */       sToClipboard = sToClipboard + "\n";
/*  659 */       for (int j = 0; j < visibleColumns.length; j++) {
/*  660 */         TableColumnCore column = visibleColumns[j];
/*  661 */         if (j != 0) {
/*  662 */           sToClipboard = sToClipboard + "\t";
/*      */         }
/*  664 */         TableCellCore cell = row.getTableCellCore(column.getName());
/*  665 */         if (cell != null) {
/*  666 */           sToClipboard = sToClipboard + cell.getClipboardText();
/*      */         }
/*      */       }
/*      */     }
/*  670 */     new org.eclipse.swt.dnd.Clipboard(getComposite().getDisplay()).setContents(new Object[] { sToClipboard }, new Transfer[] { org.eclipse.swt.dnd.TextTransfer.getInstance() });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isDisposed()
/*      */   {
/*  681 */     return (this.cTable == null) || (this.cTable.isDisposed());
/*      */   }
/*      */   
/*      */   public void refreshTable(boolean bForceSort)
/*      */   {
/*  686 */     this.refreshTableRunnable.setForceSort(bForceSort);
/*  687 */     this.refresh_dispatcher.dispatch();
/*      */   }
/*      */   
/*      */   private void __refreshTable(boolean bForceSort) {
/*  691 */     long lStart = SystemTime.getCurrentTime();
/*  692 */     super.refreshTable(bForceSort);
/*      */     
/*  694 */     Utils.execSWTThread(new AERunnable()
/*      */     {
/*      */       public void runSupport() {
/*  697 */         TableViewPainted.this.isVisible();
/*      */       }
/*  699 */     });
/*  700 */     final boolean bDoGraphics = this.loopFactor % this.graphicsUpdate == 0;
/*  701 */     boolean bWillSort = (bForceSort) || ((this.reOrderDelay != 0) && (this.loopFactor % this.reOrderDelay == 0));
/*      */     
/*      */ 
/*      */ 
/*  705 */     if (bWillSort) {
/*  706 */       TableColumnCore sortColumn = getSortColumn();
/*  707 */       if ((bForceSort) && (sortColumn != null)) {
/*  708 */         resetLastSortedOn();
/*  709 */         sortColumn.setLastSortValueChange(SystemTime.getCurrentTime());
/*      */       }
/*  711 */       _sortColumn(true, false, false);
/*      */     }
/*      */     
/*  714 */     runForAllRows(new com.aelitis.azureus.ui.common.table.TableGroupRowVisibilityRunner() {
/*      */       public void run(TableRowCore row, boolean bVisible) {
/*  716 */         row.refresh(bDoGraphics, bVisible);
/*      */       }
/*  718 */     });
/*  719 */     this.loopFactor += 1;
/*      */     
/*  721 */     long diff = SystemTime.getCurrentTime() - lStart;
/*  722 */     if ((diff <= 0L) || 
/*      */     
/*      */ 
/*      */ 
/*  726 */       (this.tvTabsCommon != null)) {
/*  727 */       Utils.execSWTThread(new AERunnable() {
/*      */         public void runSupport() {
/*  729 */           if (TableViewPainted.this.tvTabsCommon != null) {
/*  730 */             TableViewPainted.this.tvTabsCommon.swt_refresh();
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setEnableTabViews(boolean enableTabViews, boolean expandByDefault, String[] restrictedToIDs)
/*      */   {
/*  741 */     this.enableTabViews = enableTabViews;
/*  742 */     this.tabViewRestriction = restrictedToIDs;
/*  743 */     this.tabViewsExpandedByDefault = expandByDefault;
/*      */   }
/*      */   
/*      */   public boolean isTabViewsEnabled() {
/*  747 */     return this.enableTabViews;
/*      */   }
/*      */   
/*      */   public String[] getTabViewsRestrictedTo() {
/*  751 */     return this.tabViewRestriction;
/*      */   }
/*      */   
/*      */   public boolean getTabViewsExpandedByDefault()
/*      */   {
/*  756 */     return this.tabViewsExpandedByDefault;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setFocus()
/*      */   {
/*  763 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  765 */         if (TableViewPainted.this.isDisposed()) {
/*  766 */           return;
/*      */         }
/*  768 */         TableViewPainted.this.cTable.setFocus();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public void setRowDefaultHeightEM(final float lineHeight) {
/*  774 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  776 */         if ((TableViewPainted.this.cTable == null) || (TableViewPainted.this.cTable.isDisposed())) {
/*  777 */           TableViewPainted.this.iHeightEM = lineHeight;
/*      */           
/*  779 */           return;
/*      */         }
/*  781 */         int fontHeightInPX = FontUtils.getFontHeightInPX(TableViewPainted.this.cTable.getFont());
/*  782 */         int height = (int)(fontHeightInPX * lineHeight + lineHeight);
/*  783 */         TableViewPainted.this.setRowDefaultHeightPX(height);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setRowDefaultHeight(int iHeight)
/*      */   {
/*  792 */     iHeight = Utils.adjustPXForDPI(iHeight);
/*  793 */     setRowDefaultHeightPX(iHeight);
/*      */   }
/*      */   
/*      */   public void setRowDefaultHeightPX(int iHeight) {
/*  797 */     if (iHeight > this.defaultRowHeight) {
/*  798 */       this.defaultRowHeight = iHeight;
/*      */       
/*  800 */       Utils.execSWTThread(new AERunnable() {
/*      */         public void runSupport() {
/*  802 */           if ((TableViewPainted.this.vBar != null) && (!TableViewPainted.this.vBar.isDisposed())) {
/*  803 */             TableViewPainted.this.vBar.setIncrement(TableViewPainted.this.defaultRowHeight);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public TableRowCore getRow(int x, int y)
/*      */   {
/*  814 */     Set<TableRowPainted> visibleRows = this.visibleRows;
/*  815 */     if (visibleRows.size() == 0) {
/*  816 */       return null;
/*      */     }
/*  818 */     boolean firstRow = true;
/*  819 */     int curY = 0;
/*  820 */     for (TableRowPainted row : visibleRows) {
/*  821 */       if (firstRow) {
/*  822 */         curY = row.getDrawOffset().y;
/*      */       }
/*  824 */       int h = row.getHeight();
/*  825 */       if ((y >= curY) && (y < curY + h)) {
/*  826 */         return row;
/*      */       }
/*  828 */       curY += h;
/*      */     }
/*  830 */     return null;
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public boolean isRowVisible(TableRowCore row)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_1
/*      */     //   1: ifnonnull +5 -> 6
/*      */     //   4: iconst_0
/*      */     //   5: ireturn
/*      */     //   6: aload_0
/*      */     //   7: getfield 1660	org/gudy/azureus2/ui/swt/views/table/painted/TableViewPainted:visibleRows_sync	Ljava/lang/Object;
/*      */     //   10: dup
/*      */     //   11: astore_2
/*      */     //   12: monitorenter
/*      */     //   13: aload_0
/*      */     //   14: getfield 1664	org/gudy/azureus2/ui/swt/views/table/painted/TableViewPainted:visibleRows	Ljava/util/LinkedHashSet;
/*      */     //   17: aload_1
/*      */     //   18: invokevirtual 1740	java/util/LinkedHashSet:contains	(Ljava/lang/Object;)Z
/*      */     //   21: aload_2
/*      */     //   22: monitorexit
/*      */     //   23: ireturn
/*      */     //   24: astore_3
/*      */     //   25: aload_2
/*      */     //   26: monitorexit
/*      */     //   27: aload_3
/*      */     //   28: athrow
/*      */     // Line number table:
/*      */     //   Java source line #837	-> byte code offset #0
/*      */     //   Java source line #838	-> byte code offset #4
/*      */     //   Java source line #840	-> byte code offset #6
/*      */     //   Java source line #841	-> byte code offset #13
/*      */     //   Java source line #842	-> byte code offset #24
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	29	0	this	TableViewPainted
/*      */     //   0	29	1	row	TableRowCore
/*      */     //   11	15	2	Ljava/lang/Object;	Object
/*      */     //   24	4	3	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   13	23	24	finally
/*      */     //   24	27	24	finally
/*      */   }
/*      */   
/*      */   public TableCellCore getTableCellWithCursor()
/*      */   {
/*  850 */     Point pt = this.cTable.getDisplay().getCursorLocation();
/*  851 */     pt = this.cTable.toControl(pt);
/*  852 */     return getTableCell(pt.x, this.clientArea.y + pt.y);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public TableRowCore getTableRowWithCursor()
/*      */   {
/*  860 */     Point pt = this.cTable.getDisplay().getCursorLocation();
/*  861 */     pt = this.cTable.toControl(pt);
/*  862 */     return getTableRow(pt.x, pt.y, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getRowDefaultHeight()
/*      */   {
/*  869 */     return this.defaultRowHeight;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setEnabled(final boolean enable)
/*      */   {
/*  876 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  878 */         if (!TableViewPainted.this.isDisposed()) {
/*  879 */           TableViewPainted.this.cTable.setEnabled(enable);
/*  880 */           TableViewPainted.this.cHeaderArea.setEnabled(enable);
/*  881 */           TableViewPainted.this.cHeaderArea.redraw();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean canHaveSubItems()
/*      */   {
/*  891 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setHeaderVisible(final boolean visible)
/*      */   {
/*  898 */     super.setHeaderVisible(visible);
/*      */     
/*  900 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  902 */         if ((TableViewPainted.this.cHeaderArea != null) && (!TableViewPainted.this.cHeaderArea.isDisposed())) {
/*  903 */           TableViewPainted.this.cHeaderArea.setVisible(visible);
/*  904 */           FormData fd = Utils.getFilledFormData();
/*  905 */           fd.height = (visible ? TableViewPainted.this.headerHeight : 1);
/*  906 */           fd.bottom = null;
/*  907 */           TableViewPainted.this.cHeaderArea.setLayoutData(fd);
/*  908 */           TableViewPainted.this.cHeaderArea.getParent().layout(true);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaxItemShown()
/*      */   {
/*  919 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setMaxItemShown(int newIndex) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void localeChanged(Locale old_locale, Locale new_locale)
/*      */   {
/*  933 */     Utils.execSWTThreadLater(0, new AERunnable() {
/*      */       public void runSupport() {
/*  935 */         if (TableViewPainted.this.tvTabsCommon != null) {
/*  936 */           TableViewPainted.this.tvTabsCommon.localeChanged();
/*      */         }
/*      */         
/*  939 */         TableViewPainted.this.tableInvalidate();
/*  940 */         TableViewPainted.this.refreshTable(true);
/*  941 */         TableViewPainted.this.cHeaderArea.redraw();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void columnOrderChanged(int[] iPositions) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void columnSizeChanged(TableColumnCore tableColumn, int diff)
/*      */   {
/*  957 */     this.columnsWidth += diff;
/*  958 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  960 */         if ((TableViewPainted.this.cHeaderArea != null) && (!TableViewPainted.this.cHeaderArea.isDisposed())) {
/*  961 */           TableViewPainted.this.cHeaderArea.redraw();
/*      */         }
/*  963 */         TableViewPainted.this.swt_fixupSize();
/*  964 */         TableViewPainted.this.redrawTable();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addKeyListener(KeyListener listener)
/*      */   {
/*  973 */     if (this.tvSWTCommon == null) {
/*  974 */       return;
/*      */     }
/*  976 */     this.tvSWTCommon.addKeyListener(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeKeyListener(KeyListener listener)
/*      */   {
/*  983 */     if (this.tvSWTCommon == null) {
/*  984 */       return;
/*      */     }
/*  986 */     this.tvSWTCommon.removeKeyListener(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public KeyListener[] getKeyListeners()
/*      */   {
/*  993 */     if (this.tvSWTCommon == null) {
/*  994 */       return new KeyListener[0];
/*      */     }
/*  996 */     return this.tvSWTCommon.getKeyListeners();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addMenuFillListener(org.gudy.azureus2.ui.swt.views.table.TableViewSWTMenuFillListener l)
/*      */   {
/* 1003 */     if (this.tvSWTCommon == null) {
/* 1004 */       return;
/*      */     }
/* 1006 */     this.tvSWTCommon.addMenuFillListener(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DragSource createDragSource(int style)
/*      */   {
/* 1013 */     final DragSource dragSource = new DragSource(this.cTable, style);
/* 1014 */     dragSource.addDragListener(new org.eclipse.swt.dnd.DragSourceAdapter() {
/*      */       public void dragStart(DragSourceEvent event) {
/* 1016 */         TableViewPainted.this.cTable.setCursor(null);
/* 1017 */         TableRowCore row = TableViewPainted.this.getTableRow(event.x, event.y, true);
/* 1018 */         if ((row != null) && (!row.isSelected())) {
/* 1019 */           TableViewPainted.this.setSelectedRows(new TableRowCore[] { row });
/*      */         }
/* 1021 */         TableViewPainted.this.isDragging = true;
/*      */       }
/*      */       
/*      */       public void dragFinished(DragSourceEvent event) {
/* 1025 */         TableViewPainted.this.isDragging = false;
/*      */       }
/* 1027 */     });
/* 1028 */     this.cTable.addDisposeListener(new DisposeListener()
/*      */     {
/*      */       public void widgetDisposed(DisposeEvent e) {
/* 1031 */         if (!dragSource.isDisposed()) {
/* 1032 */           dragSource.dispose();
/*      */         }
/*      */       }
/* 1035 */     });
/* 1036 */     return dragSource;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DropTarget createDropTarget(int style)
/*      */   {
/* 1043 */     final DropTarget dropTarget = new DropTarget(this.cTable, style);
/* 1044 */     this.cTable.addDisposeListener(new DisposeListener()
/*      */     {
/*      */       public void widgetDisposed(DisposeEvent e) {
/* 1047 */         if (!dropTarget.isDisposed()) {
/* 1048 */           dropTarget.dispose();
/*      */         }
/*      */       }
/* 1051 */     });
/* 1052 */     return dropTarget;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Composite getComposite()
/*      */   {
/* 1059 */     return this.cTable;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public TableRowCore getRow(DropTargetEvent event)
/*      */   {
/* 1068 */     Point pt = this.cTable.toControl(event.x, event.y);
/* 1069 */     return getRow(pt.x, this.clientArea.y + pt.y);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public TableRowSWT getRowSWT(Object dataSource)
/*      */   {
/* 1076 */     return (TableRowSWT)getRow(dataSource);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Composite getTableComposite()
/*      */   {
/* 1083 */     return this.cTable;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Composite createMainPanel(Composite composite)
/*      */   {
/* 1093 */     TableViewSWTPanelCreator mainPanelCreator = getMainPanelCreator();
/* 1094 */     if (mainPanelCreator != null) {
/* 1095 */       return mainPanelCreator.createTableViewPanel(composite);
/*      */     }
/* 1097 */     Composite panel = new Composite(composite, 524288);
/* 1098 */     composite.getLayout();
/* 1099 */     GridLayout layout = new GridLayout();
/* 1100 */     layout.marginHeight = 0;
/* 1101 */     layout.marginWidth = 0;
/* 1102 */     panel.setLayout(layout);
/*      */     
/* 1104 */     Object parentLayout = composite.getLayout();
/* 1105 */     if ((parentLayout == null) || ((parentLayout instanceof GridLayout))) {
/* 1106 */       panel.setLayoutData(new org.eclipse.swt.layout.GridData(1808));
/*      */     }
/*      */     
/* 1109 */     return panel;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1116 */   public void initialize(Composite parent) { initialize(null, parent); }
/*      */   
/*      */   public void initialize(org.gudy.azureus2.ui.swt.plugins.UISWTView parentView, Composite parent) {
/* 1119 */     this.tvTabsCommon = new TableViewSWT_TabsCommon(parentView, this);
/*      */     
/* 1121 */     this.shell = parent.getShell();
/* 1122 */     this.mainComposite = this.tvTabsCommon.createSashForm(parent);
/* 1123 */     this.mainComposite.setData("Name", this.tableID);
/* 1124 */     this.mainComposite.setData("ObfusticateImage", this);
/* 1125 */     Composite cTableComposite = this.tvTabsCommon.tableComposite;
/*      */     
/* 1127 */     cTableComposite.setLayout(new org.eclipse.swt.layout.FormLayout());
/* 1128 */     org.eclipse.swt.widgets.Layout layout = parent.getLayout();
/* 1129 */     if ((layout instanceof org.eclipse.swt.layout.FormLayout)) {
/* 1130 */       FormData fd = Utils.getFilledFormData();
/* 1131 */       cTableComposite.setLayoutData(fd);
/*      */     }
/*      */     
/* 1134 */     this.cHeaderArea = new Canvas(cTableComposite, 536870912);
/*      */     
/* 1136 */     this.fontHeader = FontUtils.getFontWithHeight(this.cHeaderArea.getFont(), null, Utils.adjustPXForDPI(12));
/*      */     
/* 1138 */     this.fontHeaderSmall = FontUtils.getFontPercentOf(this.fontHeader, 0.8F);
/* 1139 */     this.cHeaderArea.setFont(this.fontHeader);
/*      */     
/* 1141 */     this.cTable = new Canvas(cTableComposite, 262912);
/*      */     
/* 1143 */     int minRowHeight = FontUtils.getFontHeightInPX(this.cTable.getFont());
/* 1144 */     if (this.iHeightEM > 0.0F) {
/* 1145 */       this.defaultRowHeight = ((int)(minRowHeight * this.iHeightEM + this.iHeightEM));
/* 1146 */       this.iHeightEM = -1.0F;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1151 */     minRowHeight = (int)(minRowHeight + Math.ceil(minRowHeight * 2.0D / 16.0D));
/* 1152 */     if (this.defaultRowHeight < minRowHeight) {
/* 1153 */       this.defaultRowHeight = minRowHeight;
/*      */     }
/*      */     
/* 1156 */     this.cTable.setBackground(parent.getDisplay().getSystemColor(25));
/*      */     
/*      */ 
/* 1159 */     this.headerHeight = configMan.getIntParameter("Table.headerHeight");
/* 1160 */     if (this.headerHeight <= 0) {
/* 1161 */       this.headerHeight = Utils.adjustPXForDPI(27);
/*      */     }
/*      */     
/* 1164 */     FormData fd = Utils.getFilledFormData();
/* 1165 */     fd.height = this.headerHeight;
/* 1166 */     fd.bottom = null;
/* 1167 */     this.cHeaderArea.setLayoutData(fd);
/* 1168 */     fd = Utils.getFilledFormData();
/* 1169 */     fd.top = new org.eclipse.swt.layout.FormAttachment(this.cHeaderArea);
/* 1170 */     this.cTable.setLayoutData(fd);
/*      */     
/* 1172 */     this.clientArea = this.cTable.getClientArea();
/*      */     
/* 1174 */     TableColumnCore[] tableColumns = getAllColumns();
/* 1175 */     TableColumnCore[] tmpColumnsOrdered = new TableColumnCore[tableColumns.length];
/*      */     
/* 1177 */     int columnOrderPos = 0;
/* 1178 */     Arrays.sort(tableColumns, TableColumnManager.getTableColumnOrderComparator());
/*      */     
/* 1180 */     for (int i = 0; i < tableColumns.length; i++) {
/* 1181 */       int position = tableColumns[i].getPosition();
/* 1182 */       if ((position != -1) && (tableColumns[i].isVisible()))
/*      */       {
/*      */ 
/* 1185 */         tmpColumnsOrdered[(columnOrderPos++)] = tableColumns[i];
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1190 */     TableColumnCore[] columnsOrdered = new TableColumnCore[columnOrderPos];
/* 1191 */     System.arraycopy(tmpColumnsOrdered, 0, columnsOrdered, 0, columnOrderPos);
/* 1192 */     setColumnsOrdered(columnsOrdered);
/*      */     
/* 1194 */     this.cTable.addPaintListener(new org.eclipse.swt.events.PaintListener() {
/*      */       public void paintControl(PaintEvent e) {
/* 1196 */         TableViewPainted.this.swt_paintComposite(e);
/*      */       }
/*      */       
/* 1199 */     });
/* 1200 */     this.menu = createMenu();
/* 1201 */     this.cTable.setMenu(this.menu);
/* 1202 */     this.cHeaderArea.setMenu(this.menu);
/*      */     
/* 1204 */     setupHeaderArea(this.cHeaderArea);
/*      */     
/* 1206 */     this.cTable.addControlListener(new org.eclipse.swt.events.ControlListener()
/*      */     {
/*      */       public void controlResized(ControlEvent e) {
/* 1209 */         TableViewPainted.this.swt_calculateClientArea();
/* 1210 */         TableViewPainted.this.swt_fixupSize();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void controlMoved(ControlEvent e) {}
/* 1216 */     });
/* 1217 */     this.hBar = this.cTable.getHorizontalBar();
/* 1218 */     if (this.hBar != null) {
/* 1219 */       this.hBar.setValues(0, 0, 0, 10, 10, 100);
/* 1220 */       this.hBar.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
/*      */       {
/*      */         public void widgetSelected(SelectionEvent e)
/*      */         {
/* 1224 */           if (TableViewPainted.DIRECT_DRAW) {
/* 1225 */             TableViewPainted.this.swt_calculateClientArea();
/* 1226 */             TableViewPainted.this.redrawTable();
/*      */           } else {
/* 1228 */             TableViewPainted.this.cTable.redraw();
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */         public void widgetDefaultSelected(SelectionEvent e) {}
/*      */       });
/*      */     }
/* 1236 */     this.vBar = this.cTable.getVerticalBar();
/* 1237 */     if (this.vBar != null) {
/* 1238 */       this.vBar.setValues(0, 0, 0, 50, getRowDefaultHeight(), 50);
/* 1239 */       this.vBar.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
/*      */         public void widgetSelected(SelectionEvent e) {
/* 1241 */           TableViewPainted.this.swt_vBarChanged();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void widgetDefaultSelected(SelectionEvent e) {}
/*      */       });
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
/* 1273 */     this.cTable.addMouseListener(this.tvSWTCommon);
/* 1274 */     this.cTable.addMouseMoveListener(this.tvSWTCommon);
/* 1275 */     this.cTable.addKeyListener(this.tvSWTCommon);
/*      */     
/*      */ 
/* 1278 */     this.cTable.addTraverseListener(new org.eclipse.swt.events.TraverseListener() {
/*      */       public void keyTraversed(org.eclipse.swt.events.TraverseEvent e) {
/* 1280 */         e.doit = true;
/*      */       }
/*      */       
/*      */ 
/* 1284 */     });
/* 1285 */     SelectedContentManager.addCurrentlySelectedContentListener(new com.aelitis.azureus.ui.selectedcontent.SelectedContentListener()
/*      */     {
/*      */       public void currentlySelectedContentChanged(com.aelitis.azureus.ui.selectedcontent.ISelectedContent[] currentContent, String viewID) {
/* 1288 */         if ((TableViewPainted.this.cTable == null) || (TableViewPainted.this.cTable.isDisposed())) {
/* 1289 */           SelectedContentManager.removeCurrentlySelectedContentListener(this);
/*      */         } else {
/* 1291 */           TableViewPainted.this.redrawTable();
/*      */         }
/*      */         
/*      */       }
/* 1295 */     });
/* 1296 */     this.cTable.addFocusListener(new org.eclipse.swt.events.FocusListener() {
/*      */       public void focusLost(FocusEvent e) {
/* 1298 */         TableViewPainted.this.isFocused = false;
/* 1299 */         TableViewPainted.this.redrawTable();
/*      */       }
/*      */       
/*      */       public void focusGained(FocusEvent e) {
/* 1303 */         TableViewPainted.this.isFocused = true;
/* 1304 */         TableViewPainted.this.redrawTable();
/*      */       }
/* 1306 */     });
/* 1307 */     this.isFocused = this.cTable.isFocusControl();
/*      */     
/* 1309 */     new org.gudy.azureus2.ui.swt.views.table.impl.TableTooltips(this, this.cTable);
/*      */     
/* 1311 */     TableColumnManager tcManager = TableColumnManager.getInstance();
/*      */     
/* 1313 */     String sSortColumn = tcManager.getDefaultSortColumnName(this.tableID);
/* 1314 */     if ((sSortColumn == null) || (sSortColumn.length() == 0)) {
/* 1315 */       sSortColumn = this.sDefaultSortOn;
/*      */     }
/*      */     
/* 1318 */     TableColumnCore tc = tcManager.getTableColumnCore(this.tableID, sSortColumn);
/* 1319 */     if ((tc == null) && (tableColumns.length > 0)) {
/* 1320 */       tc = tableColumns[0];
/*      */     }
/* 1322 */     setSortColumn(tc, false);
/*      */     
/* 1324 */     triggerLifeCycleListener(0);
/*      */     
/* 1326 */     configMan.addParameterListener("Graphics Update", this);
/* 1327 */     configMan.addParameterListener("ReOrder Delay", this);
/* 1328 */     configMan.addParameterListener("Table.extendedErase", this);
/* 1329 */     configMan.addParameterListener("Table.headerHeight", this);
/* 1330 */     Colors.getInstance().addColorsChangedListener(this);
/*      */     
/*      */ 
/*      */ 
/* 1334 */     TableStructureEventDispatcher.getInstance(this.tableID).addListener(this);
/*      */     
/* 1336 */     MessageText.addListener(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void swt_vBarChanged()
/*      */   {
/* 1343 */     swt_calculateClientArea();
/* 1344 */     this.cTable.update();
/*      */   }
/*      */   
/*      */   private void setupHeaderArea(final Canvas cHeaderArea)
/*      */   {
/* 1349 */     cHeaderArea.addPaintListener(new org.eclipse.swt.events.PaintListener() {
/*      */       public void paintControl(PaintEvent e) {
/* 1351 */         TableViewPainted.this.paintHeader(e);
/*      */       }
/*      */       
/* 1354 */     });
/* 1355 */     Listener l = new Listener() {
/* 1356 */       boolean mouseDown = false;
/*      */       
/*      */       TableColumnCore columnSizing;
/*      */       
/* 1360 */       int columnSizingStart = 0;
/*      */       
/*      */       public void handleEvent(Event e) {
/* 1363 */         switch (e.type) {
/*      */         case 3: 
/* 1365 */           if (e.button != 1) {
/* 1366 */             return;
/*      */           }
/* 1368 */           this.mouseDown = true;
/*      */           
/* 1370 */           this.columnSizing = null;
/* 1371 */           int x = -TableViewPainted.this.clientArea.x;
/* 1372 */           TableColumnCore[] visibleColumns = TableViewPainted.this.getVisibleColumns();
/* 1373 */           for (TableColumnCore column : visibleColumns) {
/* 1374 */             int w = column.getWidth();
/* 1375 */             x += w;
/*      */             
/* 1377 */             if ((e.x >= x - 3) && (e.x <= x + 3)) {
/* 1378 */               this.columnSizing = column;
/* 1379 */               this.columnSizingStart = e.x;
/* 1380 */               break;
/*      */             }
/*      */           }
/*      */           
/* 1384 */           break;
/*      */         
/*      */ 
/*      */         case 4: 
/* 1388 */           if (e.button != 1) {
/* 1389 */             return;
/*      */           }
/* 1391 */           if ((this.mouseDown) && (this.columnSizing == null)) {
/* 1392 */             TableColumnCore column = TableViewPainted.this.getTableColumnByOffset(e.x);
/* 1393 */             if (column != null) {
/* 1394 */               TableViewPainted.this.setSortColumn(column, true);
/*      */             }
/*      */           }
/* 1397 */           this.columnSizing = null;
/* 1398 */           this.mouseDown = false;
/* 1399 */           break;
/*      */         
/*      */ 
/*      */         case 5: 
/* 1403 */           if (this.columnSizing != null) {
/* 1404 */             int diff = e.x - this.columnSizingStart;
/* 1405 */             this.columnSizing.setWidthPX(this.columnSizing.getWidth() + diff);
/* 1406 */             this.columnSizingStart = e.x;
/*      */           } else {
/* 1408 */             int cursorID = 21;
/* 1409 */             int x = -TableViewPainted.this.clientArea.x;
/* 1410 */             TableColumnCore[] visibleColumns = TableViewPainted.this.getVisibleColumns();
/* 1411 */             for (TableColumnCore column : visibleColumns) {
/* 1412 */               int w = column.getWidth();
/* 1413 */               x += w;
/*      */               
/* 1415 */               if ((e.x >= x - 3) && (e.x <= x + 3)) {
/* 1416 */                 cursorID = 9;
/* 1417 */                 break;
/*      */               }
/*      */             }
/* 1420 */             cHeaderArea.setCursor(e.display.getSystemCursor(cursorID));
/* 1421 */             TableColumnCore column = TableViewPainted.this.getTableColumnByOffset(e.x);
/*      */             
/* 1423 */             if (column == null) {
/* 1424 */               cHeaderArea.setToolTipText(null);
/*      */             } else {
/* 1426 */               String info = MessageText.getString(column.getTitleLanguageKey() + ".info", (String)null);
/*      */               
/* 1428 */               if (column.showOnlyImage()) {
/* 1429 */                 String tt = MessageText.getString(column.getTitleLanguageKey());
/*      */                 
/* 1431 */                 if (info != null) {
/* 1432 */                   tt = tt + "\n" + info;
/*      */                 }
/* 1434 */                 cHeaderArea.setToolTipText(tt);
/*      */               } else {
/* 1436 */                 cHeaderArea.setToolTipText(info);
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */           break;
/*      */         }
/*      */         
/*      */       }
/* 1445 */     };
/* 1446 */     cHeaderArea.addListener(3, l);
/* 1447 */     cHeaderArea.addListener(4, l);
/* 1448 */     cHeaderArea.addListener(5, l);
/*      */     
/* 1450 */     Transfer[] types = { org.eclipse.swt.dnd.TextTransfer.getInstance() };
/*      */     
/*      */ 
/*      */ 
/* 1454 */     final DragSource ds = new DragSource(cHeaderArea, 2);
/* 1455 */     ds.setTransfer(types);
/* 1456 */     ds.addDragListener(new org.eclipse.swt.dnd.DragSourceListener() {
/*      */       private String eventData;
/*      */       
/*      */       public void dragStart(DragSourceEvent event) {
/* 1460 */         Cursor cursor = cHeaderArea.getCursor();
/* 1461 */         if ((cursor != null) && (cursor.equals(event.display.getSystemCursor(9))))
/*      */         {
/* 1463 */           event.doit = false;
/* 1464 */           return;
/*      */         }
/*      */         
/* 1467 */         cHeaderArea.setCursor(null);
/* 1468 */         TableColumnCore tc = TableViewPainted.this.getTableColumnByOffset(event.x);
/* 1469 */         TableViewPainted.this.isHeaderDragging = (tc != null);
/* 1470 */         if (TableViewPainted.this.isHeaderDragging) {
/* 1471 */           this.eventData = tc.getName();
/*      */         }
/*      */       }
/*      */       
/*      */       public void dragSetData(DragSourceEvent event)
/*      */       {
/* 1477 */         event.data = this.eventData;
/*      */       }
/*      */       
/*      */       public void dragFinished(DragSourceEvent event) {
/* 1481 */         TableViewPainted.this.isHeaderDragging = false;
/* 1482 */         this.eventData = null;
/*      */       }
/*      */       
/* 1485 */     });
/* 1486 */     final DropTarget dt = new DropTarget(cHeaderArea, 2);
/* 1487 */     dt.setTransfer(types);
/* 1488 */     dt.addDropListener(new org.eclipse.swt.dnd.DropTargetListener()
/*      */     {
/*      */       public void dropAccept(DropTargetEvent event) {}
/*      */       
/*      */       public void drop(final DropTargetEvent event)
/*      */       {
/* 1494 */         if ((event.data instanceof String)) {
/* 1495 */           TableColumn tcOrig = TableViewPainted.this.getTableColumn((String)event.data);
/* 1496 */           Point pt = TableViewPainted.this.cTable.toControl(event.x, event.y);
/* 1497 */           TableColumn tcDest = TableViewPainted.this.getTableColumnByOffset(pt.x);
/* 1498 */           if (tcDest == null) {
/* 1499 */             TableColumnCore[] visibleColumns = TableViewPainted.this.getVisibleColumns();
/* 1500 */             if ((visibleColumns != null) && (visibleColumns.length > 0)) {
/* 1501 */               tcDest = visibleColumns[(visibleColumns.length - 1)];
/*      */             }
/*      */           }
/* 1504 */           if ((tcOrig != null) && (tcDest != null)) {
/* 1505 */             int destPos = tcDest.getPosition();
/* 1506 */             int origPos = tcOrig.getPosition();
/* 1507 */             final boolean moveRight = destPos > origPos;
/* 1508 */             TableColumnCore[] visibleColumns = TableViewPainted.this.getVisibleColumns();
/* 1509 */             ((TableColumnCore)tcOrig).setPositionNoShift(destPos);
/*      */             
/*      */ 
/* 1512 */             Arrays.sort(visibleColumns, new java.util.Comparator() {
/*      */               public int compare(TableColumnCore o1, TableColumnCore o2) {
/* 1514 */                 if (o1 == o2) {
/* 1515 */                   return 0;
/*      */                 }
/* 1517 */                 int diff = o1.getPosition() - o2.getPosition();
/* 1518 */                 if (diff == 0) {
/* 1519 */                   int i = o1.getName().equals(event.data) ? -1 : 1;
/* 1520 */                   if (moveRight) {
/* 1521 */                     i *= -1;
/*      */                   }
/* 1523 */                   return i;
/*      */                 }
/* 1525 */                 return diff;
/*      */               }
/*      */             });
/*      */             
/* 1529 */             for (int i = 0; i < visibleColumns.length; i++) {
/* 1530 */               TableColumnCore tc = visibleColumns[i];
/* 1531 */               tc.setPositionNoShift(i);
/*      */             }
/* 1533 */             TableViewPainted.this.setColumnsOrdered(visibleColumns);
/*      */             
/* 1535 */             TableStructureEventDispatcher.getInstance(TableViewPainted.this.tableID).tableStructureChanged(false, TableViewPainted.this.getDataSourceType());
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void dragOver(DropTargetEvent event) {}
/*      */       
/*      */ 
/*      */       public void dragOperationChanged(DropTargetEvent event) {}
/*      */       
/*      */ 
/*      */       public void dragLeave(DropTargetEvent event) {}
/*      */       
/*      */ 
/*      */       public void dragEnter(DropTargetEvent event) {}
/* 1552 */     });
/* 1553 */     cHeaderArea.addDisposeListener(new DisposeListener() {
/*      */       public void widgetDisposed(DisposeEvent e) {
/* 1555 */         Utils.disposeSWTObjects(new Object[] { ds, dt, TableViewPainted.this.fontHeader, TableViewPainted.this.fontHeaderSmall });
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void tableStructureChanged(final boolean columnAddedOrRemoved, final Class forPluginDataSourceType)
/*      */   {
/* 1569 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/* 1571 */         TableViewPainted.this.tableStructureChanged(columnAddedOrRemoved, forPluginDataSourceType);
/* 1572 */         if ((TableViewPainted.this.cHeaderArea != null) && (!TableViewPainted.this.cHeaderArea.isDisposed())) {
/* 1573 */           TableViewPainted.this.cHeaderArea.redraw();
/*      */         }
/*      */         
/* 1576 */         TableViewPainted.this.redrawTable();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   protected void swt_paintComposite(PaintEvent e) {
/* 1582 */     swt_calculateClientArea();
/* 1583 */     if (this.canvasImage == null) {
/* 1584 */       swt_paintCanvasImage(e.gc, e.gc.getClipping());
/* 1585 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1590 */     e.gc.drawImage(this.canvasImage, -this.clientArea.x, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void swt_paintCanvasImage(GC gc, Rectangle drawBounds)
/*      */   {
/* 1597 */     if ((this.cTable == null) || (this.cTable.isDisposed())) {
/* 1598 */       return;
/*      */     }
/*      */     
/* 1601 */     int end = drawBounds.y + drawBounds.height;
/*      */     
/* 1603 */     gc.setFont(this.cTable.getFont());
/* 1604 */     Utils.setClipping(gc, drawBounds);
/* 1605 */     TableRowCore oldRow = null;
/* 1606 */     int pos = -1;
/* 1607 */     Set<TableRowPainted> visibleRows = this.visibleRows;
/*      */     
/* 1609 */     boolean isTableSelected = isTableSelected();
/* 1610 */     boolean isTableEnabled = this.cTable.isEnabled();
/* 1611 */     for (TableRowPainted row : visibleRows) {
/* 1612 */       TableRowPainted paintedRow = row;
/* 1613 */       if (pos == -1) {
/* 1614 */         pos = row.getIndex();
/*      */       } else {
/* 1616 */         pos++;
/*      */       }
/* 1618 */       Point drawOffset = paintedRow.getDrawOffset();
/* 1619 */       int rowStartX = 0;
/* 1620 */       if (DIRECT_DRAW) {
/* 1621 */         rowStartX = -drawOffset.x;
/*      */       }
/* 1623 */       int rowStartY = drawOffset.y - this.clientArea.y;
/* 1624 */       int rowHeight = paintedRow.getHeight();
/*      */       
/* 1626 */       if (drawBounds.intersects(rowStartX, rowStartY, 9999, rowHeight))
/*      */       {
/* 1628 */         int diffY2 = rowStartY + rowHeight - (drawBounds.y + drawBounds.height);
/* 1629 */         if (diffY2 > 0) {
/* 1630 */           drawBounds.height += diffY2;
/* 1631 */           Utils.setClipping(gc, drawBounds);
/*      */         }
/* 1633 */         paintedRow.swt_paintGC(gc, drawBounds, rowStartX, rowStartY, pos, isTableSelected, isTableEnabled);
/*      */       }
/*      */       
/* 1636 */       oldRow = row;
/*      */     }
/*      */     int h;
/*      */     int yDirty;
/*      */     int h;
/* 1641 */     if (oldRow == null) {
/* 1642 */       int yDirty = drawBounds.y;
/* 1643 */       h = drawBounds.height;
/*      */     } else {
/* 1645 */       yDirty = ((TableRowPainted)oldRow).getDrawOffset().y + ((TableRowPainted)oldRow).getFullHeight();
/*      */       
/* 1647 */       h = drawBounds.y + drawBounds.height - yDirty;
/*      */     }
/* 1649 */     if (h > 0) {
/* 1650 */       int rowHeight = getRowDefaultHeight();
/* 1651 */       if ((this.extendedErase) && (this.cTable.isEnabled())) {}
/* 1652 */       while (yDirty < end) {
/* 1653 */         pos++;
/* 1654 */         Color color = TableRowPainted.alternatingColors[(pos % 2)];
/* 1655 */         if (color != null) {
/* 1656 */           gc.setBackground(color);
/*      */         }
/* 1658 */         if (color == null) {
/* 1659 */           gc.setBackground(gc.getDevice().getSystemColor(25));
/*      */         }
/*      */         
/* 1662 */         gc.fillRectangle(drawBounds.x, yDirty, drawBounds.width, rowHeight);
/* 1663 */         yDirty += rowHeight;
/* 1664 */         continue;
/*      */         
/* 1666 */         gc.setBackground(gc.getDevice().getSystemColor(this.cTable.isEnabled() ? 25 : 22));
/*      */         
/* 1668 */         gc.fillRectangle(drawBounds.x, yDirty, drawBounds.width, h);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1673 */     Utils.setClipping(gc, drawBounds);
/* 1674 */     TableColumnCore[] visibleColumns = getVisibleColumns();
/* 1675 */     int x = DIRECT_DRAW ? -this.clientArea.x : 0;
/* 1676 */     gc.setAlpha(20);
/* 1677 */     for (TableColumnCore column : visibleColumns) {
/* 1678 */       x += column.getWidth();
/*      */       
/*      */ 
/* 1681 */       gc.drawLine(x - 1, drawBounds.y, x - 1, drawBounds.y + drawBounds.height);
/*      */     }
/* 1683 */     gc.setAlpha(255);
/*      */   }
/*      */   
/*      */   private Color getColorLine() {
/* 1687 */     if (this.colorLine == null) {
/* 1688 */       this.colorLine = this.cTable.getDisplay().getSystemColor(25);
/* 1689 */       HSLColor hslColor = new HSLColor();
/* 1690 */       hslColor.initHSLbyRGB(this.colorLine.getRed(), this.colorLine.getGreen(), this.colorLine.getBlue());
/*      */       
/*      */ 
/* 1693 */       int lum = hslColor.getLuminence();
/* 1694 */       if (lum > 127) {
/* 1695 */         lum -= 25;
/*      */       } else
/* 1697 */         lum += 40;
/* 1698 */       hslColor.setLuminence(lum);
/*      */       
/* 1700 */       this.colorLine = new Color(this.cTable.getDisplay(), hslColor.getRed(), hslColor.getGreen(), hslColor.getBlue());
/*      */     }
/*      */     
/*      */ 
/* 1704 */     return this.colorLine;
/*      */   }
/*      */   
/*      */   private void paintHeader(PaintEvent e)
/*      */   {
/* 1709 */     Rectangle ca = this.cHeaderArea.getClientArea();
/*      */     Color fg;
/*      */     Color c1;
/* 1712 */     Color c2; Color fg; if (this.cTable.isEnabled()) {
/* 1713 */       Color c1 = e.display.getSystemColor(25);
/* 1714 */       Color c2 = e.display.getSystemColor(22);
/* 1715 */       fg = e.display.getSystemColor(24);
/*      */     } else {
/* 1717 */       c1 = e.display.getSystemColor(22);
/* 1718 */       c2 = e.display.getSystemColor(19);
/* 1719 */       fg = e.display.getSystemColor(18);
/*      */     }
/*      */     
/* 1722 */     Color line = c2;
/*      */     
/* 1724 */     Pattern patternUp = new Pattern(e.display, 0.0F, 0.0F, 0.0F, ca.height, c1, c2);
/* 1725 */     Pattern patternDown = new Pattern(e.display, 0.0F, -ca.height, 0.0F, 0.0F, c2, c1);
/*      */     
/*      */ 
/*      */ 
/* 1729 */     e.gc.setForeground(line);
/*      */     
/* 1731 */     e.gc.drawLine(0, this.headerHeight - 1, this.clientArea.width, this.headerHeight - 1);
/*      */     
/* 1733 */     TableColumnCore[] visibleColumns = getVisibleColumns();
/*      */     
/* 1735 */     TableColumnCore sortColumn = getSortColumn();
/* 1736 */     int x = -this.clientArea.x;
/* 1737 */     for (TableColumnCore column : visibleColumns) {
/* 1738 */       int w = column.getWidth();
/*      */       
/*      */ 
/* 1741 */       if (x + w > ca.width) {
/* 1742 */         w = ca.width - x;
/* 1743 */         if (w <= 16) {
/*      */           break;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1749 */       boolean isSortColumn = column.equals(sortColumn);
/*      */       
/* 1751 */       e.gc.setBackgroundPattern(isSortColumn ? patternDown : patternUp);
/* 1752 */       e.gc.fillRectangle(x, 1, w, this.headerHeight - 2);
/* 1753 */       e.gc.setForeground(line);
/* 1754 */       e.gc.drawLine(x + w - 1, 0, x + w - 1, this.headerHeight - 1);
/*      */       
/* 1756 */       e.gc.setForeground(fg);
/* 1757 */       int yOfs = 0;
/* 1758 */       int wText = w;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1787 */       if (isSortColumn)
/*      */       {
/* 1789 */         int arrowHeight = Utils.adjustPXForDPI(6);
/* 1790 */         int arrowY = this.headerHeight / 2 - arrowHeight / 2;
/* 1791 */         int arrowHalfW = Utils.adjustPXForDPI(4);
/* 1792 */         int middle = w - arrowHalfW - 4;
/* 1793 */         wText = w - arrowHalfW * 2 - 5;
/*      */         int y1;
/* 1795 */         int y1; int y2; if (column.isSortAscending()) {
/* 1796 */           int y2 = arrowY;
/* 1797 */           y1 = y2 + arrowHeight;
/*      */         } else {
/* 1799 */           y1 = arrowY;
/* 1800 */           y2 = y1 + arrowHeight;
/*      */         }
/* 1802 */         e.gc.setAntialias(1);
/* 1803 */         e.gc.setBackground(fg);
/* 1804 */         e.gc.fillPolygon(new int[] { x + middle - arrowHalfW, y1, x + middle + arrowHalfW, y1, x + middle, y2 });
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1814 */       int xOfs = x + 2;
/*      */       
/* 1816 */       boolean onlyShowImage = column.showOnlyImage();
/* 1817 */       String text = "";
/* 1818 */       if (!onlyShowImage) {
/* 1819 */         text = MessageText.getString(column.getTitleLanguageKey());
/*      */       }
/*      */       
/* 1822 */       int style = 16777280;
/* 1823 */       Image image = null;
/* 1824 */       String imageID = column.getIconReference();
/* 1825 */       if (imageID != null) {
/* 1826 */         image = ImageLoader.getInstance().getImage(imageID);
/* 1827 */         if (ImageLoader.isRealImage(image)) {
/* 1828 */           if (onlyShowImage) {
/* 1829 */             text = null;
/* 1830 */             Rectangle imageBounds = image.getBounds();
/* 1831 */             e.gc.drawImage(image, (int)(x + w / 2.0D - imageBounds.width / 2.0D + 0.5D), this.headerHeight / 2 - imageBounds.height / 2);
/*      */           }
/*      */           else {
/* 1834 */             text = "%0 " + text;
/*      */           }
/*      */         } else {
/* 1837 */           image = null;
/*      */         }
/*      */       }
/*      */       
/* 1841 */       if (text != null) {
/* 1842 */         GCStringPrinter sp = new GCStringPrinter(e.gc, text, new Rectangle(xOfs, yOfs - 1, wText - 4, this.headerHeight - yOfs + 2), true, false, style);
/*      */         
/* 1844 */         if (image != null) {
/* 1845 */           sp.setImages(new Image[] { image });
/*      */         }
/* 1847 */         sp.calculateMetrics();
/* 1848 */         if ((sp.isWordCut()) || (sp.isCutoff())) {
/* 1849 */           Font font = e.gc.getFont();
/* 1850 */           e.gc.setFont(this.fontHeaderSmall);
/* 1851 */           sp.printString();
/* 1852 */           e.gc.setFont(font);
/*      */         } else {
/* 1854 */           sp.printString();
/*      */         }
/*      */       }
/*      */       
/* 1858 */       if (imageID != null) {
/* 1859 */         ImageLoader.getInstance().releaseImage(imageID);
/*      */       }
/*      */       
/* 1862 */       x += w;
/*      */     }
/*      */     
/* 1865 */     e.gc.setBackgroundPattern(patternUp);
/* 1866 */     e.gc.fillRectangle(x, 1, this.clientArea.width - x, this.headerHeight - 2);
/*      */     
/* 1868 */     patternUp.dispose();
/* 1869 */     patternDown.dispose();
/* 1870 */     e.gc.setBackgroundPattern(null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Image obfusticatedImage(Image image)
/*      */   {
/* 1877 */     TableColumnCore[] visibleColumns = getVisibleColumns();
/* 1878 */     TableRowPainted[] visibleRows = (TableRowPainted[])this.visibleRows.toArray(new TableRowPainted[0]);
/*      */     
/* 1880 */     for (TableRowPainted row : visibleRows) {
/* 1881 */       if ((row != null) && (!row.isRowDisposed()))
/*      */       {
/*      */ 
/*      */ 
/* 1885 */         for (TableColumnCore tc : visibleColumns)
/* 1886 */           if ((tc != null) && (tc.isObfusticated()))
/*      */           {
/*      */ 
/*      */ 
/* 1890 */             TableCellPainted cell = (TableCellPainted)row.getTableCell(tc.getName());
/* 1891 */             if (cell != null)
/*      */             {
/*      */ 
/*      */ 
/* 1895 */               String text = cell.getObfusticatedText();
/*      */               
/* 1897 */               if (text != null)
/*      */               {
/* 1899 */                 Rectangle cellBounds = cell.getBoundsOnDisplay();
/* 1900 */                 Point ptDisplay = this.cTable.getShell().getLocation();
/* 1901 */                 cellBounds.x -= ptDisplay.x;
/* 1902 */                 cellBounds.y -= ptDisplay.y;
/* 1903 */                 Rectangle boundsRaw = cell.getBoundsRaw();
/* 1904 */                 if (boundsRaw.y + cellBounds.height > this.clientArea.y + this.clientArea.height)
/*      */                 {
/* 1906 */                   cellBounds.height -= boundsRaw.y + cellBounds.height - (this.clientArea.y + this.clientArea.height);
/*      */                 }
/*      */                 
/* 1909 */                 int tableWidth = this.cTable.getClientArea().width;
/* 1910 */                 if (boundsRaw.x + cellBounds.width > this.clientArea.x + tableWidth)
/*      */                 {
/* 1912 */                   cellBounds.width -= boundsRaw.x + cellBounds.width - (this.clientArea.x + tableWidth);
/*      */                 }
/*      */                 
/*      */ 
/* 1916 */                 org.gudy.azureus2.ui.swt.debug.UIDebugGenerator.obfusticateArea(image, cellBounds, text);
/*      */               }
/*      */             }
/*      */           }
/*      */       }
/*      */     }
/* 1922 */     if (this.tvTabsCommon != null) {
/* 1923 */       this.tvTabsCommon.obfusticatedImage(image);
/*      */     }
/* 1925 */     return image;
/*      */   }
/*      */   
/*      */   protected TableViewSWTPanelCreator getMainPanelCreator() {
/* 1929 */     return this.mainPanelCreator;
/*      */   }
/*      */   
/*      */ 
/*      */   public com.aelitis.azureus.ui.common.table.TableViewCreator getTableViewCreator()
/*      */   {
/* 1935 */     return this.mainPanelCreator;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setMainPanelCreator(TableViewSWTPanelCreator mainPanelCreator)
/*      */   {
/* 1942 */     this.mainPanelCreator = mainPanelCreator;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public TableCellCore getTableCell(int x, int y)
/*      */   {
/* 1949 */     TableRowSWT row = getTableRow(x, y, true);
/* 1950 */     if (row == null) {
/* 1951 */       return null;
/*      */     }
/*      */     
/* 1954 */     TableColumnCore column = getTableColumnByOffset(x);
/* 1955 */     if (column == null) {
/* 1956 */       return null;
/*      */     }
/*      */     
/* 1959 */     return row.getTableCellCore(column.getName());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Point getTableCellMouseOffset(TableCellSWT tableCell)
/*      */   {
/* 1966 */     if (tableCell == null) {
/* 1967 */       return null;
/*      */     }
/* 1969 */     Point pt = this.cTable.getDisplay().getCursorLocation();
/* 1970 */     pt = this.cTable.toControl(pt);
/*      */     
/* 1972 */     Rectangle bounds = tableCell.getBounds();
/* 1973 */     int x = pt.x - bounds.x;
/* 1974 */     if ((x < 0) || (x > bounds.width)) {
/* 1975 */       return null;
/*      */     }
/* 1977 */     int y = pt.y - bounds.y;
/* 1978 */     if ((y < 0) || (y > bounds.height)) {
/* 1979 */       return null;
/*      */     }
/* 1981 */     return new Point(x, y);
/*      */   }
/*      */   
/*      */ 
/*      */   public void enableFilterCheck(Text txtFilter, TableViewFilterCheck<Object> filterCheck)
/*      */   {
/* 1987 */     TableViewSWTFilter<?> filter = getSWTFilter();
/* 1988 */     if (filter != null) {
/* 1989 */       if ((filter.widget != null) && (!filter.widget.isDisposed())) {
/* 1990 */         filter.widget.removeKeyListener(this.tvSWTCommon);
/* 1991 */         filter.widget.removeModifyListener(filter.widgetModifyListener);
/*      */       }
/*      */     } else {
/* 1994 */       this.filter = (filter = new TableViewSWTFilter());
/*      */     }
/* 1996 */     filter.widget = txtFilter;
/* 1997 */     if (txtFilter != null) {
/* 1998 */       txtFilter.addKeyListener(this.tvSWTCommon);
/*      */       
/* 2000 */       filter.widgetModifyListener = new org.eclipse.swt.events.ModifyListener() {
/*      */         public void modifyText(ModifyEvent e) {
/* 2002 */           TableViewPainted.this.setFilterText(((Text)e.widget).getText());
/*      */         }
/* 2004 */       };
/* 2005 */       txtFilter.addModifyListener(filter.widgetModifyListener);
/*      */       
/* 2007 */       if (txtFilter.getText().length() == 0) {
/* 2008 */         txtFilter.setText(filter.text);
/*      */       } else {
/* 2010 */         filter.text = (filter.nextText = txtFilter.getText());
/*      */       }
/*      */     } else {
/* 2013 */       filter.text = (filter.nextText = "");
/*      */     }
/*      */     
/* 2016 */     filter.checker = filterCheck;
/*      */     
/* 2018 */     filter.checker.filterSet(filter.text);
/* 2019 */     refilter();
/*      */   }
/*      */   
/*      */ 
/*      */   public Text getFilterControl()
/*      */   {
/* 2025 */     TableViewSWTFilter<?> filter = getSWTFilter();
/*      */     
/* 2027 */     return filter == null ? null : filter.widget;
/*      */   }
/*      */   
/*      */   public void disableFilterCheck() {
/* 2031 */     TableViewSWTFilter<?> filter = getSWTFilter();
/* 2032 */     if (filter == null) {
/* 2033 */       return;
/*      */     }
/*      */     
/* 2036 */     if ((filter.widget != null) && (!filter.widget.isDisposed())) {
/* 2037 */       filter.widget.removeKeyListener(this.tvSWTCommon);
/* 2038 */       filter.widget.removeModifyListener(filter.widgetModifyListener);
/*      */     }
/* 2040 */     filter = null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setFilterText(String s)
/*      */   {
/* 2047 */     if (this.tvSWTCommon != null) {
/* 2048 */       this.tvSWTCommon.setFilterText(s);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean enableSizeSlider(Composite composite, int min, int max)
/*      */   {
/* 2057 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void disableSizeSlider() {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addRowPaintListener(TableRowSWTPaintListener listener)
/*      */   {
/* 2071 */     if (this.tvSWTCommon != null) {
/* 2072 */       this.tvSWTCommon.addRowPaintListener(listener);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeRowPaintListener(TableRowSWTPaintListener listener)
/*      */   {
/* 2080 */     if (this.tvSWTCommon != null) {
/* 2081 */       this.tvSWTCommon.removeRowPaintListener(listener);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void invokePaintListeners(GC gc, TableRowCore row, TableColumnCore column, Rectangle cellArea)
/*      */   {
/* 2090 */     if (this.tvSWTCommon != null) {
/* 2091 */       this.tvSWTCommon.invokePaintListeners(gc, row, column, cellArea);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addRowMouseListener(TableRowMouseListener listener)
/*      */   {
/* 2099 */     if (this.tvSWTCommon != null) {
/* 2100 */       this.tvSWTCommon.addRowMouseListener(listener);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeRowMouseListener(TableRowMouseListener listener)
/*      */   {
/* 2108 */     if (this.tvSWTCommon != null) {
/* 2109 */       this.tvSWTCommon.removeRowMouseListener(listener);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void invokeRowMouseListener(org.gudy.azureus2.plugins.ui.tables.TableRowMouseEvent event)
/*      */   {
/* 2117 */     if (this.tvSWTCommon != null) {
/* 2118 */       this.tvSWTCommon.invokeRowMouseListener(event);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void packColumns() {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void parameterChanged(String parameterName)
/*      */   {
/* 2133 */     boolean invalidate = parameterName == null;
/* 2134 */     if ((parameterName == null) || (parameterName.equals("Graphics Update"))) {
/* 2135 */       this.graphicsUpdate = configMan.getIntParameter("Graphics Update");
/*      */     }
/* 2137 */     if ((parameterName == null) || (parameterName.equals("ReOrder Delay"))) {
/* 2138 */       this.reOrderDelay = configMan.getIntParameter("ReOrder Delay");
/*      */     }
/* 2140 */     if ((parameterName == null) || (parameterName.equals("Table.extendedErase"))) {
/* 2141 */       this.extendedErase = configMan.getBooleanParameter("Table.extendedErase");
/* 2142 */       invalidate = true;
/*      */     }
/* 2144 */     if ((parameterName == null) || (parameterName.equals("Table.headerHeight"))) {
/* 2145 */       this.headerHeight = configMan.getIntParameter("Table.headerHeight");
/* 2146 */       if (this.headerHeight == 0) {
/* 2147 */         this.headerHeight = Utils.adjustPXForDPI(27);
/*      */       }
/* 2149 */       setHeaderVisible(getHeaderVisible());
/*      */     }
/*      */     
/* 2152 */     if ((parameterName == null) || (parameterName.startsWith("Color"))) {
/* 2153 */       tableInvalidate();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public TableRowCore createNewRow(Object object)
/*      */   {
/* 2162 */     return new TableRowPainted(null, this, object, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void visibleRowsChanged()
/*      */   {
/* 2170 */     swt_visibleRowsChanged();
/*      */   }
/*      */   
/*      */   private void swt_visibleRowsChanged() {
/* 2174 */     final List<TableRowSWT> newlyVisibleRows = new ArrayList();
/*      */     
/* 2176 */     final ArrayList<TableRowSWT> rowsStayedVisibleButMoved = new ArrayList();
/*      */     List<TableRowSWT> newVisibleRows;
/* 2178 */     if (isVisible())
/*      */     {
/* 2180 */       TableRowCore[] rows = getRows();
/* 2181 */       List<TableRowSWT> newVisibleRows = new ArrayList();
/* 2182 */       recalculateVisibleRows(rows, 0, newVisibleRows, rowsStayedVisibleButMoved);
/*      */     }
/*      */     else
/*      */     {
/* 2186 */       newVisibleRows = java.util.Collections.emptyList();
/*      */     }
/* 2188 */     final List<TableRowSWT> nowInVisibleRows = new ArrayList(0);
/* 2189 */     synchronized (this.visibleRows_sync) {
/* 2190 */       if (this.visibleRows != null) {
/* 2191 */         nowInVisibleRows.addAll(this.visibleRows);
/*      */       }
/*      */     }
/*      */     
/* 2195 */     LinkedHashSet<TableRowPainted> rows = new LinkedHashSet(newVisibleRows.size());
/* 2196 */     for (TableRowSWT row : newVisibleRows) {
/* 2197 */       rows.add((TableRowPainted)row);
/* 2198 */       boolean removed = nowInVisibleRows.remove(row);
/* 2199 */       if (!removed) {
/* 2200 */         newlyVisibleRows.add(row);
/*      */       }
/*      */     }
/*      */     
/* 2204 */     synchronized (this.visibleRows_sync) {
/* 2205 */       this.visibleRows = rows;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2214 */     Utils.getOffOfSWTThread(new AERunnable()
/*      */     {
/*      */       public void runSupport() {
/* 2217 */         boolean bTableUpdate = false;
/*      */         
/* 2219 */         for (TableRowSWT row : newlyVisibleRows)
/*      */         {
/*      */ 
/* 2222 */           row.setShown(true, false);
/* 2223 */           rowsStayedVisibleButMoved.remove(row);
/* 2224 */           if (org.gudy.azureus2.core3.util.Constants.isOSX) {
/* 2225 */             bTableUpdate = true;
/*      */           }
/*      */         }
/*      */         
/* 2229 */         for (TableRowSWT row : rowsStayedVisibleButMoved) {
/* 2230 */           row.invalidate();
/* 2231 */           TableViewPainted.this.redrawRow((TableRowPainted)row, false);
/*      */         }
/*      */         
/* 2234 */         for (TableRowSWT row : nowInVisibleRows) {
/* 2235 */           row.setShown(false, false);
/*      */         }
/*      */         
/* 2238 */         if (bTableUpdate) {
/* 2239 */           Utils.execSWTThread(new AERunnable() {
/*      */             public void runSupport() {
/* 2241 */               if ((TableViewPainted.this.cTable != null) && (!TableViewPainted.this.cTable.isDisposed())) {
/* 2242 */                 TableViewPainted.this.cTable.update();
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void recalculateVisibleRows(TableRowCore[] rows, int yStart, List<TableRowSWT> newVisibleRows, List<TableRowSWT> rowsStayedVisibleButMoved)
/*      */   {
/* 2255 */     Rectangle bounds = this.clientArea;
/*      */     
/* 2257 */     int y = yStart;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2262 */     for (TableRowCore row : rows) {
/* 2263 */       if (row != null)
/*      */       {
/*      */ 
/* 2266 */         TableRowPainted rowSWT = (TableRowPainted)row;
/* 2267 */         int rowHeight = rowSWT.getHeight();
/* 2268 */         int rowFullHeight = rowSWT.getFullHeight();
/*      */         
/* 2270 */         if ((y < bounds.y + bounds.height) && (y + rowFullHeight > bounds.y))
/*      */         {
/*      */ 
/* 2273 */           boolean offsetChanged = rowSWT.setDrawOffset(new Point(bounds.x, y));
/*      */           
/*      */ 
/* 2276 */           if (y + rowHeight > bounds.y)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2287 */             if (offsetChanged) {
/* 2288 */               rowsStayedVisibleButMoved.add(rowSWT);
/*      */             }
/* 2290 */             newVisibleRows.add(rowSWT);
/*      */           }
/*      */           
/*      */ 
/* 2294 */           if (row.isExpanded()) {
/* 2295 */             TableRowCore[] subRowsWithNull = row.getSubRowsWithNull();
/* 2296 */             if (subRowsWithNull.length > 0) {
/* 2297 */               recalculateVisibleRows(subRowsWithNull, y + rowHeight, newVisibleRows, rowsStayedVisibleButMoved);
/*      */             }
/*      */           }
/*      */         } else {
/* 2301 */           if (newVisibleRows.size() > 0) {
/*      */             break;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/* 2307 */         y += rowFullHeight;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int uiGuessMaxVisibleRows()
/*      */   {
/* 2318 */     return this.clientArea.height / this.defaultRowHeight + 1;
/*      */   }
/*      */   
/*      */   public void uiRemoveRows(TableRowCore[] rows, Integer[] rowIndexes)
/*      */   {
/* 2323 */     if (this.focusedRow != null) {
/* 2324 */       for (TableRowCore row : rows) {
/* 2325 */         if (row == this.focusedRow) {
/* 2326 */           setFocusedRow(null);
/* 2327 */           break;
/*      */         }
/*      */       }
/*      */     }
/* 2331 */     int bottomIndex = getRowCount() - 1;
/* 2332 */     if (bottomIndex < 0) {
/* 2333 */       redrawTable();
/*      */     } else {
/* 2335 */       TableRowCore rowBottom = getLastVisibleRow();
/* 2336 */       if (rowBottom != null) {
/* 2337 */         while (rowBottom.getParentRowCore() != null) {
/* 2338 */           rowBottom = rowBottom.getParentRowCore();
/*      */         }
/*      */         
/* 2341 */         if (indexOf(rowBottom) < 0) {
/* 2342 */           redrawTable();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private TableRowPainted getLastVisibleRow() {
/* 2349 */     synchronized (this.visibleRows_sync) {
/* 2350 */       if ((this.visibleRows == null) || (this.visibleRows.size() == 0)) {
/* 2351 */         return null;
/*      */       }
/* 2353 */       TableRowPainted rowBottom = null;
/* 2354 */       for (TableRowPainted row : this.visibleRows) {
/* 2355 */         rowBottom = row;
/*      */       }
/* 2357 */       return rowBottom;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void getOffUIThread(AERunnable runnable)
/*      */   {
/* 2364 */     Utils.getOffOfSWTThread(runnable);
/*      */   }
/*      */   
/*      */   protected void swt_calculateClientArea() {
/* 2368 */     if ((this.cTable == null) || (this.cTable.isDisposed())) {
/* 2369 */       return;
/*      */     }
/* 2371 */     Rectangle oldClientArea = this.clientArea;
/* 2372 */     Rectangle newClientArea = this.cTable.getClientArea();
/* 2373 */     newClientArea.x = this.hBar.getSelection();
/* 2374 */     newClientArea.y = this.vBar.getSelection();
/*      */     
/* 2376 */     int w = 0;
/* 2377 */     TableColumnCore[] visibleColumns = getVisibleColumns();
/* 2378 */     for (TableColumnCore column : visibleColumns) {
/* 2379 */       w += column.getWidth();
/*      */     }
/* 2381 */     this.columnsWidth = w;
/* 2382 */     w = newClientArea.width = Math.max(newClientArea.width, w);
/*      */     
/* 2384 */     boolean refreshTable = false;
/*      */     boolean changedH;
/*      */     boolean changedH;
/*      */     boolean changedY;
/*      */     boolean changedX;
/* 2389 */     if (oldClientArea != null) {
/* 2390 */       boolean changedX = oldClientArea.x != newClientArea.x;
/* 2391 */       boolean changedY = oldClientArea.y != newClientArea.y;
/*      */       
/* 2393 */       changedH = oldClientArea.height != newClientArea.height;
/*      */     } else {
/* 2395 */       changedX = changedY = changedH = 1;
/*      */     }
/*      */     
/*      */ 
/* 2399 */     this.clientArea = newClientArea;
/* 2400 */     if (this.tvSWTCommon != null) {
/* 2401 */       this.tvSWTCommon.xAdj = (-this.clientArea.x);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2406 */     boolean needRedraw = false;
/* 2407 */     if ((changedY) || (changedH)) {
/* 2408 */       visibleRowsChanged();
/* 2409 */       if ((changedY) && (oldClientArea != null)) {
/* 2410 */         Set<TableRowPainted> visibleRows = this.visibleRows;
/* 2411 */         if (visibleRows.size() > 0) {
/* 2412 */           if ((this.canvasImage != null) && (!this.canvasImage.isDisposed()) && (!changedH))
/*      */           {
/* 2414 */             int yDiff = oldClientArea.y - newClientArea.y;
/* 2415 */             if (Math.abs(yDiff) < this.clientArea.height) {
/* 2416 */               boolean wasIn = this.in_swt_updateCanvasImage;
/* 2417 */               this.in_swt_updateCanvasImage = true;
/*      */               try {
/* 2419 */                 GC gc = new GC(this.canvasImage);
/*      */                 try {
/* 2421 */                   Rectangle bounds = this.canvasImage.getBounds();
/*      */                   
/* 2423 */                   if (yDiff > 0) {
/* 2424 */                     if (Utils.isGTK3)
/*      */                     {
/* 2426 */                       gc.drawImage(this.canvasImage, 0, yDiff);
/*      */                     } else {
/* 2428 */                       gc.copyArea(0, 0, bounds.width, bounds.height, 0, yDiff, false);
/*      */                     }
/* 2430 */                     swt_paintCanvasImage(gc, new Rectangle(0, 0, 9999, yDiff));
/* 2431 */                     Utils.setClipping(gc, (Rectangle)null);
/*      */                   } else {
/* 2433 */                     if (Utils.isGTK3)
/*      */                     {
/* 2435 */                       gc.drawImage(this.canvasImage, 0, yDiff);
/*      */                     } else {
/* 2437 */                       gc.copyArea(0, -yDiff, bounds.width, bounds.height, 0, 0, false);
/*      */                     }
/* 2439 */                     int h = -yDiff;
/* 2440 */                     TableRowPainted row = getLastVisibleRow();
/* 2441 */                     if (row != null)
/*      */                     {
/* 2443 */                       h += row.getHeight();
/*      */                     }
/* 2445 */                     swt_paintCanvasImage(gc, new Rectangle(0, bounds.height - h, 9999, h));
/* 2446 */                     Utils.setClipping(gc, (Rectangle)null);
/*      */                   }
/*      */                 } finally {
/* 2449 */                   gc.dispose();
/*      */ 
/*      */ 
/*      */ 
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/* 2465 */                 refreshTable = true;
/*      */               }
/*      */               finally
/*      */               {
/* 2469 */                 this.in_swt_updateCanvasImage = wasIn;
/*      */               }
/*      */               
/* 2472 */               needRedraw = true;
/*      */             } else {
/* 2474 */               refreshTable = true;
/*      */             }
/* 2476 */           } else if (this.canvasImage == null) {
/* 2477 */             needRedraw = true;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 2484 */     if (changedX) {
/* 2485 */       this.cHeaderArea.redraw();
/*      */     }
/*      */     
/* 2488 */     if (!DIRECT_DRAW) {
/* 2489 */       Image newImage = this.canvasImage;
/*      */       
/*      */ 
/* 2492 */       int h = 0;
/* 2493 */       synchronized (this.visibleRows_sync) {
/* 2494 */         TableRowPainted lastRow = getLastVisibleRow();
/* 2495 */         if (lastRow != null) {
/* 2496 */           h = lastRow.getDrawOffset().y - this.clientArea.y + lastRow.getHeight();
/* 2497 */           if ((h < this.clientArea.height) && (lastRow.isExpanded())) {
/* 2498 */             TableRowCore[] subRows = lastRow.getSubRowsWithNull();
/* 2499 */             for (TableRowCore subRow : subRows) {
/* 2500 */               if (subRow != null)
/*      */               {
/*      */ 
/* 2503 */                 TableRowPainted subRowP = (TableRowPainted)subRow;
/*      */                 
/* 2505 */                 h += subRowP.getFullHeight();
/* 2506 */                 if (h >= this.clientArea.height)
/*      */                   break;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 2513 */       if (h < this.clientArea.height) {
/* 2514 */         h = this.clientArea.height;
/*      */       }
/*      */       
/* 2517 */       int oldH = (this.canvasImage == null) || (this.canvasImage.isDisposed()) ? 0 : this.canvasImage.getBounds().height;
/*      */       
/* 2519 */       int oldW = (this.canvasImage == null) || (this.canvasImage.isDisposed()) ? 0 : this.canvasImage.getBounds().width;
/*      */       
/*      */ 
/* 2522 */       if ((this.canvasImage == null) || (oldW != w) || (h > oldH))
/*      */       {
/* 2524 */         if ((h <= 0) || (this.clientArea.width <= 0)) {
/* 2525 */           newImage = null;
/*      */         } else {
/* 2527 */           newImage = new Image(this.shell.getDisplay(), w, h);
/*      */         }
/*      */       }
/* 2530 */       boolean canvasChanged = this.canvasImage != newImage;
/* 2531 */       if (canvasChanged) {
/* 2532 */         Image oldImage = this.canvasImage;
/* 2533 */         this.canvasImage = newImage;
/*      */         
/* 2535 */         if ((oldImage != null) && (!oldImage.isDisposed())) {
/* 2536 */           oldImage.dispose();
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2543 */       if ((changedH) || (canvasChanged) || (refreshTable))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2549 */         __refreshTable(false);
/*      */         
/* 2551 */         if (canvasChanged) {
/* 2552 */           swt_updateCanvasImage(false);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2561 */     if (needRedraw) {
/* 2562 */       this.cTable.redraw();
/*      */     }
/*      */   }
/*      */   
/*      */   public void swt_updateCanvasImage(boolean immediateRedraw) {
/* 2567 */     if ((this.canvasImage != null) && (!this.canvasImage.isDisposed())) {
/* 2568 */       swt_updateCanvasImage(this.canvasImage.getBounds(), immediateRedraw);
/*      */     } else {
/* 2570 */       this.cTable.redraw();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void swt_updateCanvasImage(final Rectangle bounds, final boolean immediateRedraw)
/*      */   {
/* 2578 */     if (this.in_swt_updateCanvasImage) {
/* 2579 */       Utils.execSWTThreadLater(0, new AERunnable() {
/*      */         public void runSupport() {
/* 2581 */           TableViewPainted.this.swt_updateCanvasImage(bounds, immediateRedraw);
/*      */         }
/* 2583 */       });
/* 2584 */       return;
/*      */     }
/* 2586 */     this.in_swt_updateCanvasImage = true;
/*      */     try { int x;
/*      */       int x;
/* 2589 */       if (!DIRECT_DRAW) {
/* 2590 */         if ((this.canvasImage == null) || (this.canvasImage.isDisposed()) || (bounds == null)) {
/*      */           return;
/*      */         }
/*      */         
/* 2594 */         GC gc = new GC(this.canvasImage);
/* 2595 */         swt_paintCanvasImage(gc, bounds);
/* 2596 */         gc.dispose();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2608 */         x = bounds.x - this.clientArea.x;
/*      */       } else {
/* 2610 */         x = bounds.x;
/*      */       }
/*      */       
/* 2613 */       if ((this.cTable != null) && (!this.cTable.isDisposed()))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2620 */         this.cTable.redraw(x, bounds.y, bounds.width, bounds.height, false);
/* 2621 */         if (immediateRedraw) {
/* 2622 */           this.cTable.update();
/*      */         }
/*      */       }
/*      */     } finally {
/* 2626 */       this.in_swt_updateCanvasImage = false;
/*      */     }
/*      */   }
/*      */   
/*      */   public Rectangle getClientArea() {
/* 2631 */     return this.clientArea;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isVisible()
/*      */   {
/* 2638 */     if (!Utils.isThisThreadSWT()) {
/* 2639 */       return this.isVisible;
/*      */     }
/* 2641 */     boolean wasVisible = this.isVisible;
/* 2642 */     this.isVisible = ((this.cTable != null) && (!this.cTable.isDisposed()) && (this.cTable.isVisible()) && (!this.shell.getMinimized()));
/*      */     
/* 2644 */     if (this.isVisible != wasVisible) {
/* 2645 */       visibleRowsChanged();
/* 2646 */       MdiEntrySWT view = this.tvTabsCommon == null ? null : this.tvTabsCommon.getActiveSubView();
/*      */       
/* 2648 */       if (this.isVisible) {
/* 2649 */         this.loopFactor = 0;
/*      */         
/* 2651 */         if (view != null) {
/* 2652 */           view.getMDI().showEntry(view);
/*      */         }
/*      */       }
/* 2655 */       else if (view != null) {
/* 2656 */         view.triggerEvent(4, null);
/*      */       }
/*      */     }
/*      */     
/* 2660 */     return this.isVisible;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeAllTableRows()
/*      */   {
/* 2667 */     super.removeAllTableRows();
/* 2668 */     synchronized (this.visibleRows_sync) {
/* 2669 */       this.visibleRows = new LinkedHashSet();
/*      */     }
/* 2671 */     setFocusedRow(null);
/* 2672 */     this.totalHeight = 0;
/* 2673 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/* 2675 */         if ((TableViewPainted.this.cTable == null) || (TableViewPainted.this.cTable.isDisposed())) {
/* 2676 */           return;
/*      */         }
/* 2678 */         TableViewPainted.this.swt_fixupSize();
/* 2679 */         TableViewPainted.this.swt_updateCanvasImage(false);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void swt_fixupSize()
/*      */   {
/* 2689 */     boolean vBarValid = (this.vBar != null) && (!this.vBar.isDisposed());
/* 2690 */     if (vBarValid) {
/* 2691 */       int tableSize = this.clientArea.height;
/* 2692 */       int max = this.totalHeight;
/* 2693 */       if (max < tableSize) {
/* 2694 */         this.vBar.setSelection(0);
/* 2695 */         this.vBar.setEnabled(false);
/* 2696 */         this.vBar.setVisible(false);
/*      */       } else {
/* 2698 */         if (!this.vBar.isVisible()) {
/* 2699 */           this.vBar.setVisible(true);
/* 2700 */           this.vBar.setEnabled(true);
/*      */         }
/* 2702 */         if (this.vBar.getMaximum() != max) {
/* 2703 */           this.vBar.setMaximum(max);
/* 2704 */           swt_vBarChanged();
/*      */         }
/* 2706 */         this.vBar.setThumb(tableSize);
/* 2707 */         this.vBar.setPageIncrement(tableSize);
/*      */       }
/*      */     }
/* 2710 */     if ((this.hBar != null) && (!this.hBar.isDisposed())) {
/* 2711 */       int tableSize = this.cTable.getSize().x;
/* 2712 */       int max = this.columnsWidth;
/* 2713 */       if ((vBarValid) && (this.vBar.isVisible()) && (getScrollbarsMode() == 0)) {
/* 2714 */         int vBarW = this.vBar.getSize().x;
/*      */         
/* 2716 */         max += vBarW;
/*      */       }
/* 2718 */       if (max < tableSize) {
/* 2719 */         this.hBar.setSelection(0);
/* 2720 */         this.hBar.setEnabled(false);
/* 2721 */         this.hBar.setVisible(false);
/*      */       } else {
/* 2723 */         if (!this.hBar.isVisible()) {
/* 2724 */           this.hBar.setVisible(true);
/* 2725 */           this.hBar.setEnabled(true);
/*      */         }
/* 2727 */         this.hBar.setValues(this.hBar.getSelection(), 0, max, tableSize, 50, tableSize);
/*      */       }
/* 2729 */       if ((vBarValid) && (this.hBar.isVisible())) {
/* 2730 */         int hBarW = getScrollbarsMode() == 0 ? this.hBar.getSize().y : 0;
/*      */         
/* 2732 */         this.vBar.setThumb(this.clientArea.height - hBarW);
/* 2733 */         this.vBar.setMaximum(this.totalHeight - hBarW);
/* 2734 */         this.vBar.setPageIncrement(this.vBar.getPageIncrement() - hBarW);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private int getScrollbarsMode()
/*      */   {
/* 2741 */     if (hasGetScrollBarMode) {
/* 2742 */       return this.cTable.getScrollbarsMode();
/*      */     }
/* 2744 */     return 0;
/*      */   }
/*      */   
/*      */   protected void uiChangeColumnIndicator()
/*      */   {
/* 2749 */     Utils.execSWTThread(new AERunnable()
/*      */     {
/*      */       public void runSupport()
/*      */       {
/* 2753 */         if ((TableViewPainted.this.cHeaderArea != null) && (!TableViewPainted.this.cHeaderArea.isDisposed())) {
/* 2754 */           TableViewPainted.this.cHeaderArea.redraw();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public TableColumnCore getTableColumnByOffset(int mouseX) {
/* 2761 */     int x = -this.clientArea.x;
/* 2762 */     TableColumnCore[] visibleColumns = getVisibleColumns();
/* 2763 */     for (TableColumnCore column : visibleColumns) {
/* 2764 */       int w = column.getWidth();
/*      */       
/* 2766 */       if ((mouseX >= x) && (mouseX < x + w)) {
/* 2767 */         return column;
/*      */       }
/*      */       
/* 2770 */       x += w;
/*      */     }
/* 2772 */     return null;
/*      */   }
/*      */   
/*      */   public TableRowSWT getTableRow(int x, int y, boolean anyX)
/*      */   {
/* 2777 */     return (TableRowSWT)getRow(anyX ? 2 : x, this.clientArea.y + y);
/*      */   }
/*      */   
/*      */   public void setSelectedRows(TableRowCore[] newSelectionArray, boolean trigger)
/*      */   {
/* 2782 */     super.setSelectedRows(newSelectionArray, trigger);
/*      */     
/* 2784 */     boolean focusInSelection = false;
/* 2785 */     for (TableRowCore row : newSelectionArray) {
/* 2786 */       if (row != null)
/*      */       {
/*      */ 
/* 2789 */         if (row.equals(this.focusedRow)) {
/* 2790 */           focusInSelection = true;
/* 2791 */           break;
/*      */         } }
/*      */     }
/* 2794 */     if (!focusInSelection) {
/* 2795 */       setFocusedRow(newSelectionArray.length == 0 ? null : newSelectionArray[0]);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setRowSelected(TableRowCore row, boolean selected, boolean trigger)
/*      */   {
/* 2801 */     if ((selected) && (!isSelected(row))) {
/* 2802 */       setFocusedRow(row);
/*      */     }
/* 2804 */     super.setRowSelected(row, selected, trigger);
/*      */     
/* 2806 */     if ((row instanceof TableRowSWT)) {
/* 2807 */       ((TableRowSWT)row).setWidgetSelected(selected);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void editCell(TableColumnCore column, int row) {}
/*      */   
/*      */   public boolean isDragging()
/*      */   {
/* 2816 */     return this.isDragging;
/*      */   }
/*      */   
/*      */   public TableViewSWTFilter<?> getSWTFilter() {
/* 2820 */     return (TableViewSWTFilter)this.filter;
/*      */   }
/*      */   
/*      */   public void openFilterDialog() {
/* 2824 */     if (this.filter == null) {
/* 2825 */       return;
/*      */     }
/* 2827 */     SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow();
/* 2828 */     entryWindow.initTexts("MyTorrentsView.dialog.setFilter.title", null, "MyTorrentsView.dialog.setFilter.text", new String[] { MessageText.getString(getTableID() + "View" + ".header") });
/*      */     
/*      */ 
/*      */ 
/* 2832 */     entryWindow.setPreenteredText(this.filter.text, false);
/* 2833 */     entryWindow.prompt();
/* 2834 */     if (!entryWindow.hasSubmittedInput()) {
/* 2835 */       return;
/*      */     }
/* 2837 */     String message = entryWindow.getSubmittedInput();
/*      */     
/* 2839 */     if (message == null) {
/* 2840 */       message = "";
/*      */     }
/*      */     
/* 2843 */     setFilterText(message);
/*      */   }
/*      */   
/*      */   public boolean isSingleSelection() {
/* 2847 */     return !this.isMultiSelect;
/*      */   }
/*      */   
/*      */ 
/*      */   public void expandColumns() {}
/*      */   
/*      */ 
/*      */   public void triggerTabViewsDataSourceChanged()
/*      */   {
/* 2856 */     if (this.tvTabsCommon != null) {
/* 2857 */       this.tvTabsCommon.triggerTabViewsDataSourceChanged(this);
/*      */     }
/*      */   }
/*      */   
/*      */   public TableViewSWT_TabsCommon getTabsCommon() {
/* 2862 */     return this.tvTabsCommon;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void uiSelectionChanged(final TableRowCore[] newlySelectedRows, final TableRowCore[] deselectedRows)
/*      */   {
/* 2869 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/* 2871 */         for (TableRowCore row : deselectedRows) {
/* 2872 */           row.invalidate();
/* 2873 */           TableViewPainted.this.redrawRow((TableRowPainted)row, false);
/*      */         }
/* 2875 */         for (TableRowCore row : newlySelectedRows) {
/* 2876 */           row.invalidate();
/* 2877 */           TableViewPainted.this.redrawRow((TableRowPainted)row, false);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public void delete() {
/* 2884 */     triggerLifeCycleListener(1);
/*      */     
/* 2886 */     if (this.tvTabsCommon != null) {
/* 2887 */       this.tvTabsCommon.delete();
/* 2888 */       this.tvTabsCommon = null;
/*      */     }
/*      */     
/* 2891 */     TableStructureEventDispatcher.getInstance(this.tableID).removeListener(this);
/* 2892 */     TableColumnManager tcManager = TableColumnManager.getInstance();
/* 2893 */     if (tcManager != null) {
/* 2894 */       tcManager.saveTableColumns(getDataSourceType(), this.tableID);
/*      */     }
/*      */     
/* 2897 */     Utils.disposeSWTObjects(new Object[] { this.cTable });
/*      */     
/*      */ 
/* 2900 */     this.cTable = null;
/*      */     
/* 2902 */     if (this.filter != null) {
/* 2903 */       disableFilterCheck();
/*      */     }
/*      */     
/* 2906 */     removeAllTableRows();
/* 2907 */     configMan.removeParameterListener("ReOrder Delay", this);
/* 2908 */     configMan.removeParameterListener("Graphics Update", this);
/* 2909 */     configMan.removeParameterListener("Table.extendedErase", this);
/* 2910 */     configMan.removeParameterListener("Table.headerHeight", this);
/* 2911 */     Colors.getInstance().removeColorsChangedListener(this);
/*      */     
/* 2913 */     super.delete();
/*      */     
/* 2915 */     MessageText.removeListener(this);
/*      */   }
/*      */   
/*      */   public void generate(IndentWriter writer)
/*      */   {
/* 2920 */     super.generate(writer);
/*      */     
/* 2922 */     if (this.tvTabsCommon != null) {
/* 2923 */       this.tvTabsCommon.generate(writer);
/*      */     }
/*      */   }
/*      */   
/*      */   private Menu createMenu() {
/* 2928 */     if (!isMenuEnabled()) {
/* 2929 */       return null;
/*      */     }
/*      */     
/* 2932 */     final Menu menu = new Menu(this.shell, 8);
/* 2933 */     this.cTable.addListener(35, new Listener() {
/*      */       public void handleEvent(Event event) {
/* 2935 */         if (event.widget == TableViewPainted.this.cHeaderArea) {
/* 2936 */           menu.setData("inBlankArea", Boolean.valueOf(false));
/* 2937 */           menu.setData("isHeader", Boolean.valueOf(true));
/*      */         }
/*      */         else {
/* 2940 */           TableRowCore row = TableViewPainted.this.getTableRowWithCursor();
/* 2941 */           boolean noRow = row == null;
/*      */           
/*      */ 
/*      */ 
/* 2945 */           if (!TableViewPainted.this.isSelected(row)) {
/* 2946 */             TableViewPainted.this.setSelectedRows(new TableRowCore[] { row });
/*      */           }
/*      */           
/* 2949 */           menu.setData("inBlankArea", Boolean.valueOf(noRow));
/* 2950 */           menu.setData("isHeader", Boolean.valueOf(false));
/*      */         }
/* 2952 */         Point pt = TableViewPainted.this.cHeaderArea.toControl(event.x, event.y);
/* 2953 */         menu.setData("column", TableViewPainted.this.getTableColumnByOffset(pt.x));
/*      */       }
/* 2955 */     });
/* 2956 */     this.cHeaderArea.addListener(35, new Listener() {
/*      */       public void handleEvent(Event event) {
/* 2958 */         menu.setData("inBlankArea", Boolean.valueOf(false));
/* 2959 */         menu.setData("isHeader", Boolean.valueOf(true));
/* 2960 */         Point pt = TableViewPainted.this.cHeaderArea.toControl(event.x, event.y);
/* 2961 */         menu.setData("column", TableViewPainted.this.getTableColumnByOffset(pt.x));
/*      */       }
/* 2963 */     });
/* 2964 */     org.gudy.azureus2.ui.swt.MenuBuildUtils.addMaintenanceListenerForMenu(menu, new org.gudy.azureus2.ui.swt.MenuBuildUtils.MenuBuilder()
/*      */     {
/*      */       public void buildMenu(Menu menu, org.eclipse.swt.events.MenuEvent menuEvent) {
/* 2967 */         Object oIsHeader = menu.getData("isHeader");
/* 2968 */         boolean isHeader = (oIsHeader instanceof Boolean) ? ((Boolean)oIsHeader).booleanValue() : false;
/*      */         
/*      */ 
/* 2971 */         Object oInBlankArea = menu.getData("inBlankArea");
/* 2972 */         boolean inBlankArea = (oInBlankArea instanceof Boolean) ? ((Boolean)oInBlankArea).booleanValue() : false;
/*      */         
/*      */ 
/* 2975 */         TableColumnCore column = (TableColumnCore)menu.getData("column");
/*      */         
/* 2977 */         if (isHeader) {
/* 2978 */           TableViewPainted.this.tvSWTCommon.fillColumnMenu(menu, column, false);
/* 2979 */         } else if (inBlankArea) {
/* 2980 */           TableViewPainted.this.tvSWTCommon.fillColumnMenu(menu, column, true);
/*      */         } else {
/* 2982 */           TableViewPainted.this.tvSWTCommon.fillMenu(menu, column);
/*      */         }
/*      */         
/*      */       }
/*      */       
/* 2987 */     });
/* 2988 */     return menu;
/*      */   }
/*      */   
/*      */   public void showColumnEditor() {
/* 2992 */     if (this.tvSWTCommon != null) {
/* 2993 */       this.tvSWTCommon.showColumnEditor();
/*      */     }
/*      */   }
/*      */   
/*      */   public TableRowCore getFocusedRow()
/*      */   {
/* 2999 */     return this.focusedRow;
/*      */   }
/*      */   
/*      */   public void setFocusedRow(TableRowCore row) {
/* 3003 */     TableRowPainted oldFocusedRow = this.focusedRow;
/* 3004 */     if (!(row instanceof TableRowPainted)) {
/* 3005 */       row = null;
/*      */     }
/* 3007 */     this.focusedRow = ((TableRowPainted)row);
/* 3008 */     if (this.focusedRow != null) {
/* 3009 */       if ((this.focusedRow.isVisible()) && (this.focusedRow.getDrawOffset().y + this.focusedRow.getHeight() <= this.clientArea.y + this.clientArea.height) && (this.focusedRow.getDrawOffset().y >= this.clientArea.y))
/*      */       {
/*      */ 
/*      */ 
/* 3013 */         redrawRow(this.focusedRow, false);
/*      */       }
/*      */       else {
/* 3016 */         showRow(this.focusedRow);
/*      */       }
/*      */     }
/* 3019 */     if (oldFocusedRow != null) {
/* 3020 */       redrawRow(oldFocusedRow, false);
/*      */     }
/*      */   }
/*      */   
/*      */   public void showRow(final TableRowCore rowToShow)
/*      */   {
/* 3026 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/* 3028 */         if (TableViewPainted.this.isDisposed()) {
/* 3029 */           return;
/*      */         }
/*      */         
/* 3032 */         if (rowToShow.isVisible())
/*      */         {
/* 3034 */           int y = ((TableRowPainted)rowToShow).getDrawOffset().y;
/* 3035 */           if (y + rowToShow.getHeight() > TableViewPainted.this.clientArea.y + TableViewPainted.this.clientArea.height) {
/* 3036 */             y -= TableViewPainted.this.clientArea.height - rowToShow.getHeight();
/*      */           }
/* 3038 */           TableViewPainted.this.vBar.setSelection(y);
/* 3039 */           TableViewPainted.this.swt_vBarChanged();
/*      */         } else {
/* 3041 */           TableRowCore parentFocusedRow = rowToShow;
/* 3042 */           while (parentFocusedRow.getParentRowCore() != null) {
/* 3043 */             parentFocusedRow = parentFocusedRow.getParentRowCore();
/*      */           }
/* 3045 */           TableRowCore[] rows = TableViewPainted.this.getRows();
/* 3046 */           int y = 0;
/* 3047 */           for (TableRowCore row : rows) {
/* 3048 */             if (row == parentFocusedRow) {
/* 3049 */               if (parentFocusedRow == rowToShow) break;
/* 3050 */               y += row.getHeight();
/* 3051 */               TableRowCore[] subRowsWithNull = parentFocusedRow.getSubRowsWithNull();
/* 3052 */               for (TableRowCore subrow : subRowsWithNull) {
/* 3053 */                 if (subrow == rowToShow) {
/*      */                   break;
/*      */                 }
/* 3056 */                 y += ((TableRowPainted)subrow).getFullHeight();
/*      */               }
/* 3058 */               break;
/*      */             }
/*      */             
/* 3061 */             y += ((TableRowPainted)row).getFullHeight();
/*      */           }
/*      */           
/* 3064 */           if (y + rowToShow.getHeight() > TableViewPainted.this.clientArea.y + TableViewPainted.this.clientArea.height) {
/* 3065 */             y -= TableViewPainted.this.clientArea.height - rowToShow.getHeight();
/*      */           }
/*      */           
/* 3068 */           TableViewPainted.this.vBar.setSelection(y);
/* 3069 */           TableViewPainted.this.swt_vBarChanged();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void rowHeightChanged(TableRowCore row, int oldHeight, int newHeight)
/*      */   {
/* 3079 */     synchronized (this.heightChangeSync) {
/* 3080 */       this.totalHeight += newHeight - oldHeight;
/*      */       
/*      */ 
/* 3083 */       if (this.qdRowHeightChanged) {
/* 3084 */         return;
/*      */       }
/* 3086 */       this.qdRowHeightChanged = true;
/*      */     }
/* 3088 */     Utils.execSWTThreadLater(0, new AERunnable() {
/*      */       public void runSupport() {
/* 3090 */         synchronized (TableViewPainted.this.heightChangeSync) {
/* 3091 */           TableViewPainted.this.qdRowHeightChanged = false;
/*      */         }
/*      */         
/*      */ 
/* 3095 */         TableViewPainted.this.visibleRowsChanged();
/* 3096 */         TableViewPainted.this.swt_fixupSize();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public void redrawTable() {
/* 3102 */     synchronized (this) {
/* 3103 */       if (this.redrawTableScheduled) {
/* 3104 */         return;
/*      */       }
/* 3106 */       this.redrawTableScheduled = true;
/*      */     }
/*      */     
/* 3109 */     this.redraw_dispatcher.dispatch();
/*      */   }
/*      */   
/*      */   private String prettyIndex(TableRowCore row) {
/* 3113 */     String s = "" + row.getIndex();
/* 3114 */     if (row.getParentRowCore() != null) {
/* 3115 */       s = row.getParentRowCore().getIndex() + "." + s;
/*      */     }
/* 3117 */     return s;
/*      */   }
/*      */   
/*      */ 
/*      */   public void redrawRow(TableRowPainted row, final boolean immediateRedraw)
/*      */   {
/* 3123 */     if (row == null) {
/* 3124 */       return;
/*      */     }
/* 3126 */     if (TableRowPainted.DEBUG_ROW_PAINT) {
/* 3127 */       System.out.println(SystemTime.getCurrentTime() + "} redraw " + prettyIndex(row) + " scheduled via " + org.gudy.azureus2.core3.util.Debug.getCompressedStackTrace());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3134 */     synchronized (this.pending_rows)
/*      */     {
/* 3136 */       this.pending_rows.add(row);
/*      */     }
/*      */     
/* 3139 */     Utils.execSWTThread(new AERunnable()
/*      */     {
/*      */       public void runSupport()
/*      */       {
/*      */         List<TableRowPainted> rows;
/*      */         
/* 3145 */         synchronized (TableViewPainted.this.pending_rows)
/*      */         {
/* 3147 */           if (TableViewPainted.this.pending_rows.size() == 0)
/*      */           {
/* 3149 */             return;
/*      */           }
/*      */           
/* 3152 */           rows = new ArrayList(TableViewPainted.this.pending_rows.size());
/*      */           
/* 3154 */           for (TableRowPainted row : TableViewPainted.this.pending_rows)
/*      */           {
/* 3156 */             if (row.isVisible())
/*      */             {
/* 3158 */               rows.add(row);
/*      */             }
/*      */           }
/*      */           
/* 3162 */           TableViewPainted.this.pending_rows.clear();
/*      */           
/* 3164 */           if ((!TableViewPainted.this.isVisible) || (rows.size() == 0))
/*      */           {
/* 3166 */             return;
/*      */           }
/*      */         }
/*      */         
/* 3170 */         Rectangle bounds = null;
/*      */         
/* 3172 */         boolean has_last = false;
/*      */         
/* 3174 */         for (TableRowPainted row : rows)
/*      */         {
/* 3176 */           Rectangle b = row.getDrawBounds();
/*      */           
/* 3178 */           if (b != null)
/*      */           {
/* 3180 */             if (bounds == null)
/*      */             {
/* 3182 */               bounds = b;
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/*      */ 
/* 3188 */               bounds = bounds.union(b);
/*      */             }
/*      */           }
/*      */           
/* 3192 */           if ((!has_last) && (TableViewPainted.this.isLastRow(row)))
/*      */           {
/* 3194 */             has_last = true;
/*      */           }
/*      */         }
/*      */         
/* 3198 */         if (bounds != null) {
/* 3199 */           Composite composite = TableViewPainted.this.getComposite();
/* 3200 */           if ((composite != null) && (!composite.isDisposed())) {
/* 3201 */             int h = has_last ? composite.getSize().y - bounds.y : bounds.height;
/*      */             
/*      */ 
/* 3204 */             TableViewPainted.this.swt_updateCanvasImage(new Rectangle(bounds.x, bounds.y, bounds.width, h), immediateRedraw);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
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
/*      */   public Object getSyncObject()
/*      */   {
/* 3233 */     return this.lock;
/*      */   }
/*      */   
/*      */   public boolean isTableSelected()
/*      */   {
/* 3238 */     com.aelitis.azureus.ui.common.table.TableView tv = SelectedContentManager.getCurrentlySelectedTableView();
/* 3239 */     return (tv == this) || ((tv == null) && (this.isFocused)) || ((tv != this) && (tv != null) && (tv.getSelectedRowsSize() == 0));
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/table/painted/TableViewPainted.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */