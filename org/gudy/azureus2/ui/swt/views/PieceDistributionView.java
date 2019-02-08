/*     */ package org.gudy.azureus2.ui.swt.views;
/*     */ 
/*     */ import com.aelitis.azureus.core.peermanager.piecepicker.PiecePicker;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.FontMetrics;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.disk.DiskManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.core3.peer.PEPiece;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
/*     */ import org.gudy.azureus2.ui.swt.views.utils.CoordinateTransform;
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
/*     */ public abstract class PieceDistributionView
/*     */   implements UISWTViewCoreEventListener
/*     */ {
/*     */   private Composite comp;
/*     */   private Canvas pieceDistCanvas;
/*     */   protected PEPeerManager pem;
/*     */   protected boolean[] hasPieces;
/*  55 */   protected boolean isMe = false;
/*  56 */   private boolean initialized = false;
/*  57 */   private Image imgToPaint = null;
/*     */   
/*     */   private UISWTView swtView;
/*     */   
/*     */ 
/*     */   public abstract void dataSourceChanged(Object paramObject);
/*     */   
/*     */ 
/*     */   private String getFullTitle()
/*     */   {
/*  67 */     return MessageText.getString("PiecesView.DistributionView.title");
/*     */   }
/*     */   
/*     */   private void initialize(Composite parent) {
/*  71 */     this.comp = new Composite(parent, 0);
/*  72 */     createPieceDistPanel();
/*  73 */     this.initialized = true;
/*  74 */     refresh();
/*     */   }
/*     */   
/*     */   private void createPieceDistPanel() {
/*  78 */     this.comp.setLayout(new FillLayout());
/*     */     
/*  80 */     this.pieceDistCanvas = new Canvas(this.comp, 262144);
/*  81 */     this.pieceDistCanvas.addListener(9, new Listener() {
/*     */       public void handleEvent(Event event) {
/*  83 */         if ((PieceDistributionView.this.pem == null) || (PieceDistributionView.this.pem.isDestroyed())) {
/*  84 */           event.gc.fillRectangle(event.x, event.y, event.width, event.height);
/*     */         }
/*  86 */         else if ((PieceDistributionView.this.imgToPaint != null) && (!PieceDistributionView.this.imgToPaint.isDisposed())) {
/*  87 */           event.gc.drawImage(PieceDistributionView.this.imgToPaint, 0, 0);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   private final void updateDistribution()
/*     */   {
/*  96 */     if ((!this.initialized) || (this.pem == null) || (this.comp == null) || (this.pem.getPiecePicker() == null) || (this.pem.getDiskManager() == null) || (!this.comp.isVisible()))
/*     */     {
/*     */ 
/*  99 */       return; }
/* 100 */     Rectangle rect = this.pieceDistCanvas.getBounds();
/* 101 */     if ((rect.height <= 0) || (rect.width <= 0)) {
/* 102 */       return;
/*     */     }
/* 104 */     PiecePicker picker = this.pem.getPiecePicker();
/*     */     
/* 106 */     int seeds = this.pem.getNbSeeds() + (this.pem.isSeeding() ? 1 : 0);
/* 107 */     int connected = this.pem.getNbPeers() + seeds + (this.pem.isSeeding() ? 0 : 1);
/* 108 */     int upperBound = 1 + (1 << (int)Math.ceil(Math.log(connected + 0.0D) / Math.log(2.0D)));
/*     */     
/* 110 */     int minAvail = (int)picker.getMinAvailability();
/*     */     
/* 112 */     int nbPieces = picker.getNumberOfPieces();
/* 113 */     int[] availabilties = picker.getAvailability();
/* 114 */     DiskManagerPiece[] dmPieces = this.pem.getDiskManager().getPieces();
/* 115 */     PEPiece[] pePieces = this.pem.getPieces();
/* 116 */     int[] globalPiecesPerAvailability = new int[upperBound];
/* 117 */     int[] datasourcePiecesPerAvailability = new int[upperBound];
/*     */     
/*     */ 
/* 120 */     boolean[] downloading = new boolean[upperBound];
/*     */     
/* 122 */     int avlPeak = 0;
/*     */     
/*     */ 
/* 125 */     for (int i = 0; i < nbPieces; i++)
/*     */     {
/* 127 */       if (availabilties[i] >= upperBound)
/*     */         return;
/*     */       int newPeak;
/* 130 */       if (avlPeak < (newPeak = globalPiecesPerAvailability[availabilties[i]] += 1))
/*     */       {
/* 132 */         avlPeak = newPeak;
/*     */       }
/*     */       
/* 135 */       if (((this.isMe) && (dmPieces[i].isDone())) || ((!this.isMe) && (this.hasPieces != null) && (this.hasPieces[i] != 0)))
/* 136 */         datasourcePiecesPerAvailability[availabilties[i]] += 1;
/* 137 */       if ((this.isMe) && (pePieces[i] != null)) {
/* 138 */         downloading[availabilties[i]] = true;
/*     */       }
/*     */     }
/* 141 */     Image img = new Image(this.comp.getDisplay(), this.pieceDistCanvas.getBounds());
/*     */     
/* 143 */     GC gc = new GC(img);
/*     */     
/*     */     try
/*     */     {
/* 147 */       int stepWidthX = rect.width / upperBound;
/* 148 */       int barGap = 1;
/* 149 */       int barWidth = stepWidthX - barGap - 1;
/* 150 */       int barFillingWidth = barWidth - 1;
/* 151 */       double stepWidthY = 1.0D * (rect.height - 1) / avlPeak;
/* 152 */       int offsetY = rect.height;
/*     */       
/* 154 */       gc.setForeground(Colors.green);
/* 155 */       for (int i = 0; i <= connected; i++) {
/*     */         Color curColor;
/*     */         Color curColor;
/* 158 */         if (i == 0) {
/* 159 */           curColor = Colors.colorError; } else { Color curColor;
/* 160 */           if (i <= seeds) {
/* 161 */             curColor = Colors.green;
/*     */           } else {
/* 163 */             curColor = Colors.blues[9];
/*     */           }
/*     */         }
/* 166 */         gc.setBackground(curColor);
/* 167 */         gc.setForeground(curColor);
/*     */         
/* 169 */         if (globalPiecesPerAvailability[i] == 0)
/*     */         {
/* 171 */           gc.setLineWidth(2);
/* 172 */           gc.drawLine(stepWidthX * i, offsetY - 1, stepWidthX * (i + 1) - barGap, offsetY - 1);
/*     */         }
/*     */         else {
/* 175 */           gc.setLineWidth(1);
/* 176 */           if (downloading[i] != 0)
/* 177 */             gc.setLineStyle(2);
/* 178 */           gc.fillRectangle(stepWidthX * i + 1, offsetY - 1, barFillingWidth, (int)(Math.ceil(stepWidthY * datasourcePiecesPerAvailability[i] - 1.0D) * -1.0D));
/* 179 */           gc.drawRectangle(stepWidthX * i, offsetY, barWidth, (int)(Math.ceil(stepWidthY * globalPiecesPerAvailability[i]) + 1.0D) * -1);
/*     */         }
/*     */         
/* 182 */         if (i == minAvail)
/*     */         {
/* 184 */           gc.setForeground(Colors.blue);
/* 185 */           gc.drawRectangle(stepWidthX * i + 1, offsetY - 1, barWidth - 2, (int)Math.ceil(stepWidthY * globalPiecesPerAvailability[i] - 1.0D) * -1);
/*     */         }
/*     */         
/*     */ 
/* 189 */         gc.setLineStyle(1);
/*     */       }
/* 191 */       gc.setLineWidth(1);
/*     */       
/*     */ 
/* 194 */       CoordinateTransform t = new CoordinateTransform(rect);
/* 195 */       t.shiftExternal(rect.width, 0);
/* 196 */       t.scale(-1.0D, 1.0D);
/*     */       
/* 198 */       String[] boxContent = { MessageText.getString("PiecesView.DistributionView.NoAvl"), MessageText.getString("PiecesView.DistributionView.SeedAvl"), MessageText.getString("PiecesView.DistributionView.PeerAvl"), MessageText.getString("PiecesView.DistributionView.RarestAvl", new String[] { globalPiecesPerAvailability[minAvail] + "", minAvail + "" }), MessageText.getString("PiecesView.DistributionView." + (this.isMe ? "weHave" : "theyHave")), MessageText.getString("PiecesView.DistributionView.weDownload") };
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 207 */       int charWidth = gc.getFontMetrics().getAverageCharWidth();
/* 208 */       int charHeight = gc.getFontMetrics().getHeight();
/* 209 */       int maxBoxOffsetY = charHeight + 2;
/* 210 */       int maxBoxWidth = 0;
/* 211 */       int maxBoxOffsetX = 0;
/* 212 */       for (int i = 0; i < boxContent.length; i++) {
/* 213 */         maxBoxWidth = Math.max(maxBoxWidth, boxContent[i].length());
/*     */       }
/* 215 */       maxBoxOffsetX = (maxBoxWidth + 5) * charWidth;
/* 216 */       maxBoxWidth++;maxBoxWidth *= charWidth;
/*     */       
/*     */ 
/* 219 */       int boxNum = 1;
/* 220 */       gc.setForeground(Colors.colorError);
/* 221 */       gc.setBackground(Colors.background);
/* 222 */       gc.drawRectangle(t.x(maxBoxOffsetX), t.y(maxBoxOffsetY * boxNum), maxBoxWidth, charHeight);
/* 223 */       gc.drawString(boxContent[(boxNum - 1)], t.x(maxBoxOffsetX - 5), t.y(maxBoxOffsetY * boxNum), true);
/*     */       
/* 225 */       boxNum++;
/* 226 */       gc.setForeground(Colors.green);
/* 227 */       gc.setBackground(Colors.background);
/* 228 */       gc.drawRectangle(t.x(maxBoxOffsetX), t.y(maxBoxOffsetY * boxNum), maxBoxWidth, charHeight);
/* 229 */       gc.drawString(boxContent[(boxNum - 1)], t.x(maxBoxOffsetX - 5), t.y(maxBoxOffsetY * boxNum), true);
/*     */       
/* 231 */       boxNum++;
/* 232 */       gc.setForeground(Colors.blues[9]);
/* 233 */       gc.drawRectangle(t.x(maxBoxOffsetX), t.y(maxBoxOffsetY * boxNum), maxBoxWidth, charHeight);
/* 234 */       gc.drawString(boxContent[(boxNum - 1)], t.x(maxBoxOffsetX - 5), t.y(maxBoxOffsetY * boxNum), true);
/*     */       
/* 236 */       boxNum++;
/* 237 */       gc.setForeground(Colors.blue);
/* 238 */       gc.drawRectangle(t.x(maxBoxOffsetX), t.y(maxBoxOffsetY * boxNum), maxBoxWidth, charHeight);
/* 239 */       gc.drawString(boxContent[(boxNum - 1)], t.x(maxBoxOffsetX - 5), t.y(maxBoxOffsetY * boxNum), true);
/*     */       
/* 241 */       boxNum++;
/* 242 */       gc.setForeground(Colors.black);
/* 243 */       gc.setBackground(Colors.black);
/* 244 */       gc.drawRectangle(t.x(maxBoxOffsetX), t.y(maxBoxOffsetY * boxNum), maxBoxWidth, charHeight);
/* 245 */       gc.fillRectangle(t.x(maxBoxOffsetX), t.y(maxBoxOffsetY * boxNum), maxBoxWidth / 2, charHeight);
/* 246 */       gc.setForeground(Colors.grey);
/* 247 */       gc.setBackground(Colors.background);
/* 248 */       gc.drawString(boxContent[(boxNum - 1)], t.x(maxBoxOffsetX - 5), t.y(maxBoxOffsetY * boxNum), true);
/*     */       
/* 250 */       if (this.isMe)
/*     */       {
/* 252 */         boxNum++;
/* 253 */         gc.setForeground(Colors.black);
/* 254 */         gc.setLineStyle(2);
/* 255 */         gc.drawRectangle(t.x(maxBoxOffsetX), t.y(maxBoxOffsetY * boxNum), maxBoxWidth, charHeight);
/* 256 */         gc.drawString(boxContent[(boxNum - 1)], t.x(maxBoxOffsetX - 5), t.y(maxBoxOffsetY * boxNum), true);
/*     */       }
/*     */       
/* 259 */       gc.setLineStyle(1);
/*     */     }
/*     */     finally
/*     */     {
/* 263 */       gc.dispose();
/*     */     }
/*     */     
/* 266 */     if (this.imgToPaint != null) {
/* 267 */       this.imgToPaint.dispose();
/*     */     }
/* 269 */     this.imgToPaint = img;
/* 270 */     this.pieceDistCanvas.redraw();
/*     */   }
/*     */   
/*     */   public void refresh() {
/* 274 */     if ((!this.initialized) || (this.pem == null))
/* 275 */       return;
/* 276 */     updateDistribution();
/*     */   }
/*     */   
/*     */   private Composite getComposite() {
/* 280 */     return this.comp;
/*     */   }
/*     */   
/*     */   private void delete() {
/* 284 */     if (!this.initialized)
/* 285 */       return;
/* 286 */     this.initialized = false;
/* 287 */     Utils.disposeSWTObjects(new Object[] { this.pieceDistCanvas, this.comp, this.imgToPaint });
/*     */   }
/*     */   
/*     */   private void viewActivated() {
/* 291 */     updateDistribution();
/*     */   }
/*     */   
/*     */   private void viewDeactivated() {
/* 295 */     Utils.disposeSWTObjects(new Object[] { this.imgToPaint });
/*     */   }
/*     */   
/*     */   public boolean eventOccurred(UISWTViewEvent event) {
/* 299 */     switch (event.getType()) {
/*     */     case 0: 
/* 301 */       this.swtView = ((UISWTView)event.getData());
/* 302 */       this.swtView.setTitle(getFullTitle());
/* 303 */       break;
/*     */     
/*     */     case 7: 
/* 306 */       delete();
/* 307 */       break;
/*     */     
/*     */     case 2: 
/* 310 */       initialize((Composite)event.getData());
/* 311 */       break;
/*     */     
/*     */     case 6: 
/* 314 */       Messages.updateLanguageForControl(getComposite());
/* 315 */       this.swtView.setTitle(getFullTitle());
/* 316 */       break;
/*     */     
/*     */     case 1: 
/* 319 */       dataSourceChanged(event.getData());
/* 320 */       break;
/*     */     
/*     */     case 3: 
/* 323 */       viewActivated();
/* 324 */       break;
/*     */     
/*     */     case 4: 
/* 327 */       viewDeactivated();
/* 328 */       break;
/*     */     
/*     */     case 5: 
/* 331 */       refresh();
/*     */     }
/*     */     
/*     */     
/* 335 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/PieceDistributionView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */