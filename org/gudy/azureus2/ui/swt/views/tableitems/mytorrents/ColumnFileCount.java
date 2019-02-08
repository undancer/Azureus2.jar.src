/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.table.TableCellCore;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdatable;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdater;
/*     */ import com.aelitis.azureus.ui.swt.uiupdater.UIUpdaterSWT;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*     */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter;
/*     */ import org.gudy.azureus2.ui.swt.views.FilesView;
/*     */ import org.gudy.azureus2.ui.swt.views.table.CoreTableColumnSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWTPaintListener;
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
/*     */ public class ColumnFileCount
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellMouseListener, TableCellSWTPaintListener, TableCellAddedListener
/*     */ {
/*  55 */   public static final Class DATASOURCE_TYPE = Download.class;
/*     */   public static final String COLUMN_ID = "filecount";
/*     */   
/*     */   public ColumnFileCount(String sTableID)
/*     */   {
/*  60 */     super(DATASOURCE_TYPE, "filecount", 2, 60, sTableID);
/*  61 */     setRefreshInterval(-3);
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  65 */     info.addCategories(new String[] { "content" });
/*     */     
/*     */ 
/*  68 */     info.setProficiency((byte)0);
/*     */   }
/*     */   
/*     */   public void cellAdded(TableCell cell)
/*     */   {
/*  73 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/*  74 */     int sortVal = dm.getNumFileInfos();
/*  75 */     cell.setSortValue(sortVal);
/*     */   }
/*     */   
/*     */   public void cellMouseTrigger(final TableCellMouseEvent event)
/*     */   {
/*  80 */     if (Utils.getUserMode() < 2) {
/*  81 */       return;
/*     */     }
/*  83 */     final DownloadManager dm = (DownloadManager)event.cell.getDataSource();
/*     */     
/*  85 */     if (event.eventType == 4) {
/*  86 */       ((TableCellCore)event.cell).setCursorID(21);
/*  87 */     } else if (event.eventType == 4) {
/*  88 */       ((TableCellCore)event.cell).setCursorID(0);
/*  89 */     } else if ((event.eventType == 1) && (event.button == 1))
/*     */     {
/*  91 */       Utils.execSWTThreadLater(0, new AERunnable()
/*     */       {
/*     */         public void runSupport() {
/*  94 */           ColumnFileCount.this.openFilesMiniView(dm, event.cell);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */   public void cellPaint(GC gc, TableCellSWT cell)
/*     */   {
/* 102 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 103 */     if (dm == null) {
/* 104 */       return;
/*     */     }
/*     */     
/* 107 */     int sortVal = dm.getNumFileInfos();
/* 108 */     Rectangle bounds = cell.getBounds();
/* 109 */     Rectangle printArea = new Rectangle(bounds.x, bounds.y, bounds.width - 6, bounds.height);
/*     */     
/* 111 */     GCStringPrinter.printString(gc, "" + sortVal, printArea, true, true, 131072);
/*     */   }
/*     */   
/*     */   private void openFilesMiniView(DownloadManager dm, TableCell cell)
/*     */   {
/* 116 */     Shell shell = ShellFactory.createShell(Utils.findAnyShell(), 1264);
/*     */     
/* 118 */     FillLayout fillLayout = new FillLayout();
/* 119 */     fillLayout.marginHeight = 2;
/* 120 */     fillLayout.marginWidth = 2;
/* 121 */     shell.setLayout(fillLayout);
/*     */     
/* 123 */     Rectangle bounds = ((TableCellSWT)cell).getBoundsOnDisplay();
/* 124 */     bounds.y += bounds.height;
/* 125 */     bounds.width = 630;
/* 126 */     bounds.height = (16 * dm.getNumFileInfos() + 60);
/* 127 */     Rectangle realBounds = shell.computeTrim(0, 0, bounds.width, bounds.height);
/* 128 */     realBounds.width -= realBounds.x;
/* 129 */     realBounds.height -= realBounds.y;
/* 130 */     realBounds.x = bounds.x;
/* 131 */     realBounds.y = bounds.y;
/* 132 */     if (bounds.height > 500) {
/* 133 */       bounds.height = 500;
/*     */     }
/* 135 */     shell.setBounds(realBounds);
/* 136 */     shell.setAlpha(230);
/*     */     
/* 138 */     Utils.verifyShellRect(shell, true);
/*     */     
/*     */ 
/* 141 */     final FilesView view = new FilesView(false);
/* 142 */     view.dataSourceChanged(dm);
/*     */     
/* 144 */     view.initialize(shell);
/*     */     
/* 146 */     Composite composite = view.getComposite();
/*     */     
/*     */ 
/*     */ 
/* 150 */     view.viewActivated();
/* 151 */     view.refresh();
/*     */     
/* 153 */     final UIUpdatable viewUpdater = new UIUpdatable() {
/*     */       public void updateUI() {
/* 155 */         view.refresh();
/*     */       }
/*     */       
/*     */       public String getUpdateUIName() {
/* 159 */         return view.getFullTitle();
/*     */       }
/* 161 */     };
/* 162 */     UIUpdaterSWT.getInstance().addUpdater(viewUpdater);
/*     */     
/* 164 */     shell.addDisposeListener(new DisposeListener() {
/*     */       public void widgetDisposed(DisposeEvent e) {
/* 166 */         UIUpdaterSWT.getInstance().removeUpdater(viewUpdater);
/* 167 */         view.delete();
/*     */       }
/*     */       
/* 170 */     });
/* 171 */     shell.layout(true, true);
/*     */     
/*     */ 
/* 174 */     shell.setText(dm.getDisplayName());
/*     */     
/* 176 */     shell.open();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/ColumnFileCount.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */