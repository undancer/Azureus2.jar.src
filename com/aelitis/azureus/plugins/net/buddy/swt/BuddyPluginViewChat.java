/*     */ package com.aelitis.azureus.plugins.net.buddy.swt;
/*     */ 
/*     */ import com.aelitis.azureus.plugins.net.buddy.BuddyPlugin;
/*     */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginAZ2.chatInstance;
/*     */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginAZ2.chatMessage;
/*     */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginAZ2.chatParticipant;
/*     */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginAZ2ChatListener;
/*     */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBuddy;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.custom.StyleRange;
/*     */ import org.eclipse.swt.custom.StyledText;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.KeyEvent;
/*     */ import org.eclipse.swt.events.KeyListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Table;
/*     */ import org.eclipse.swt.widgets.TableColumn;
/*     */ import org.eclipse.swt.widgets.TableItem;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.utils.LocaleUtilities;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
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
/*     */ public class BuddyPluginViewChat
/*     */   implements BuddyPluginAZ2ChatListener
/*     */ {
/*     */   private BuddyPlugin plugin;
/*     */   private BuddyPluginAZ2.chatInstance chat;
/*     */   private LocaleUtilities lu;
/*     */   private Shell shell;
/*     */   private StyledText log;
/*     */   private Table buddy_table;
/*  69 */   private List participants = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected BuddyPluginViewChat(BuddyPlugin _plugin, Display _display, BuddyPluginAZ2.chatInstance _chat)
/*     */   {
/*  77 */     this.plugin = _plugin;
/*  78 */     this.chat = _chat;
/*     */     
/*  80 */     this.lu = this.plugin.getPluginInterface().getUtilities().getLocaleUtilities();
/*     */     
/*  82 */     this.shell = ShellFactory.createMainShell(3312);
/*     */     
/*  84 */     this.shell.addDisposeListener(new DisposeListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void widgetDisposed(DisposeEvent arg0)
/*     */       {
/*     */ 
/*  91 */         BuddyPluginViewChat.this.closed();
/*     */       }
/*     */       
/*  94 */     });
/*  95 */     this.shell.setText(this.lu.getLocalisedMessageText("azbuddy.chat.title"));
/*     */     
/*  97 */     Utils.setShellIcon(this.shell);
/*     */     
/*  99 */     GridLayout layout = new GridLayout();
/* 100 */     layout.numColumns = 2;
/* 101 */     layout.marginHeight = 0;
/* 102 */     layout.marginWidth = 0;
/* 103 */     this.shell.setLayout(layout);
/* 104 */     GridData grid_data = new GridData(1808);
/* 105 */     Utils.setLayoutData(this.shell, grid_data);
/*     */     
/*     */ 
/* 108 */     this.log = new StyledText(this.shell, 526920);
/* 109 */     grid_data = new GridData(1808);
/* 110 */     grid_data.horizontalSpan = 1;
/* 111 */     grid_data.horizontalIndent = 4;
/* 112 */     grid_data.widthHint = 300;
/* 113 */     grid_data.heightHint = 400;
/* 114 */     Utils.setLayoutData(this.log, grid_data);
/* 115 */     this.log.setIndent(4);
/*     */     
/* 117 */     this.log.setEditable(false);
/*     */     
/* 119 */     Composite rhs = new Composite(this.shell, 0);
/* 120 */     layout = new GridLayout();
/* 121 */     layout.numColumns = 1;
/* 122 */     layout.marginHeight = 0;
/* 123 */     layout.marginWidth = 0;
/* 124 */     rhs.setLayout(layout);
/* 125 */     grid_data = new GridData(1808);
/* 126 */     grid_data.widthHint = 150;
/* 127 */     Utils.setLayoutData(rhs, grid_data);
/*     */     
/*     */ 
/*     */ 
/* 131 */     this.buddy_table = new Table(rhs, 268503042);
/*     */     
/* 133 */     String[] headers = { "azbuddy.ui.table.name" };
/*     */     
/*     */ 
/* 136 */     int[] sizes = { 150 };
/*     */     
/* 138 */     int[] aligns = { 16384 };
/*     */     
/* 140 */     for (int i = 0; i < headers.length; i++)
/*     */     {
/* 142 */       TableColumn tc = new TableColumn(this.buddy_table, aligns[i]);
/*     */       
/* 144 */       tc.setWidth(Utils.adjustPXForDPI(sizes[i]));
/*     */       
/* 146 */       Messages.setLanguageText(tc, headers[i]);
/*     */     }
/*     */     
/* 149 */     this.buddy_table.setHeaderVisible(true);
/*     */     
/* 151 */     grid_data = new GridData(1808);
/* 152 */     grid_data.heightHint = (this.buddy_table.getHeaderHeight() * 3);
/* 153 */     Utils.setLayoutData(this.buddy_table, grid_data);
/*     */     
/*     */ 
/* 156 */     this.buddy_table.addListener(36, new Listener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/*     */ 
/*     */ 
/* 164 */         TableItem item = (TableItem)event.item;
/*     */         
/* 166 */         int index = BuddyPluginViewChat.this.buddy_table.indexOf(item);
/*     */         
/* 168 */         if ((index < 0) || (index >= BuddyPluginViewChat.this.participants.size()))
/*     */         {
/* 170 */           return;
/*     */         }
/*     */         
/* 173 */         BuddyPluginAZ2.chatParticipant participant = (BuddyPluginAZ2.chatParticipant)BuddyPluginViewChat.this.participants.get(index);
/*     */         
/* 175 */         BuddyPluginBuddy buddy = participant.getBuddy();
/*     */         
/* 177 */         if (buddy == null)
/*     */         {
/* 179 */           item.setForeground(0, Colors.red);
/*     */         }
/* 181 */         else if (buddy.isOnline(false))
/*     */         {
/* 183 */           item.setForeground(0, Colors.black);
/*     */         }
/*     */         else
/*     */         {
/* 187 */           item.setForeground(0, Colors.grey);
/*     */         }
/*     */         
/* 190 */         item.setText(0, participant.getName());
/*     */ 
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 198 */     });
/* 199 */     final Text text = new Text(this.shell, 2626);
/* 200 */     grid_data = new GridData(768);
/* 201 */     grid_data.horizontalSpan = 2;
/* 202 */     grid_data.heightHint = 50;
/* 203 */     Utils.setLayoutData(text, grid_data);
/*     */     
/* 205 */     text.addKeyListener(new KeyListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void keyPressed(KeyEvent e)
/*     */       {
/*     */ 
/* 212 */         if (e.keyCode == 13)
/*     */         {
/* 214 */           e.doit = false;
/*     */           
/* 216 */           BuddyPluginViewChat.this.sendMessage(text.getText());
/*     */           
/* 218 */           text.setText("");
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void keyReleased(KeyEvent e) {}
/* 228 */     });
/* 229 */     text.setFocus();
/*     */     
/* 231 */     this.shell.addListener(31, new Listener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void handleEvent(Event e)
/*     */       {
/*     */ 
/*     */ 
/* 239 */         if (e.character == '\033')
/*     */         {
/* 241 */           BuddyPluginViewChat.this.close();
/*     */         }
/*     */         
/*     */       }
/* 245 */     });
/* 246 */     BuddyPluginAZ2.chatParticipant[] existing_participants = this.chat.getParticipants();
/*     */     
/* 248 */     synchronized (this.participants)
/*     */     {
/* 250 */       this.participants.addAll(Arrays.asList(existing_participants));
/*     */     }
/*     */     
/* 253 */     updateTable(false);
/*     */     
/* 255 */     BuddyPluginAZ2.chatMessage[] history = this.chat.getHistory();
/*     */     
/* 257 */     for (int i = 0; i < history.length; i++)
/*     */     {
/* 259 */       logChatMessage(history[i].getNickName(), Colors.blue, history[i].getMessage());
/*     */     }
/*     */     
/* 262 */     this.chat.addListener(this);
/*     */     
/* 264 */     this.shell.pack();
/* 265 */     Utils.createURLDropTarget(this.shell, text);
/* 266 */     Utils.centreWindow(this.shell);
/* 267 */     this.shell.open();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void updateTable(boolean async)
/*     */   {
/* 274 */     if (async)
/*     */     {
/* 276 */       if (!this.buddy_table.isDisposed())
/*     */       {
/* 278 */         this.buddy_table.getDisplay().asyncExec(new Runnable()
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/*     */ 
/* 284 */             if (BuddyPluginViewChat.this.buddy_table.isDisposed())
/*     */             {
/* 286 */               return;
/*     */             }
/*     */             
/* 289 */             BuddyPluginViewChat.this.updateTable(false);
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */     else {
/* 295 */       this.buddy_table.setItemCount(this.participants.size());
/* 296 */       this.buddy_table.clearAll();
/* 297 */       this.buddy_table.redraw();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void close()
/*     */   {
/* 304 */     this.shell.dispose();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void closed()
/*     */   {
/* 310 */     this.chat.removeListener(this);
/*     */     
/* 312 */     this.chat.destroy();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void participantAdded(BuddyPluginAZ2.chatParticipant participant)
/*     */   {
/* 319 */     synchronized (this.participants)
/*     */     {
/* 321 */       this.participants.add(participant);
/*     */     }
/*     */     
/* 324 */     updateTable(true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void participantChanged(BuddyPluginAZ2.chatParticipant participant)
/*     */   {
/* 331 */     updateTable(true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void participantRemoved(BuddyPluginAZ2.chatParticipant participant)
/*     */   {
/* 338 */     synchronized (this.participants)
/*     */     {
/* 340 */       this.participants.remove(participant);
/*     */     }
/*     */     
/* 343 */     updateTable(true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void sendMessage(String text)
/*     */   {
/* 350 */     Map msg = new HashMap();
/*     */     try
/*     */     {
/* 353 */       msg.put("line", text.getBytes("UTF-8"));
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 357 */       msg.put("line", text.getBytes());
/*     */     }
/*     */     
/* 360 */     logChatMessage(this.plugin.getNickname(), Colors.green, msg);
/*     */     
/* 362 */     this.chat.sendMessage(msg);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void messageReceived(final BuddyPluginAZ2.chatParticipant participant, final Map msg)
/*     */   {
/* 370 */     if (!this.log.isDisposed())
/*     */     {
/* 372 */       this.log.getDisplay().asyncExec(new Runnable()
/*     */       {
/*     */ 
/*     */         public void run()
/*     */         {
/*     */ 
/* 378 */           if (BuddyPluginViewChat.this.log.isDisposed())
/*     */           {
/* 380 */             return;
/*     */           }
/*     */           try
/*     */           {
/* 384 */             BuddyPluginViewChat.this.logChatMessage(participant.getName(), Colors.blue, msg);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 388 */             Debug.printStackTrace(e);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void logChatMessage(String buddy_name, Color colour, Map map)
/*     */   {
/* 401 */     byte[] line = (byte[])map.get("line");
/*     */     
/*     */     String msg;
/*     */     try
/*     */     {
/* 406 */       msg = new String(line, "UTF-8");
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 410 */       msg = new String(line);
/*     */     }
/*     */     
/* 413 */     if (buddy_name.length() > 32)
/*     */     {
/* 415 */       buddy_name = buddy_name.substring(0, 16) + "...";
/*     */     }
/*     */     
/* 418 */     int start = this.log.getText().length();
/*     */     
/* 420 */     if (msg.startsWith("/me"))
/*     */     {
/* 422 */       msg = msg.substring(3).trim();
/*     */       
/* 424 */       String me = "* " + buddy_name + " " + msg;
/*     */       
/* 426 */       this.log.append(me);
/*     */       
/* 428 */       if (colour != Colors.black)
/*     */       {
/* 430 */         StyleRange styleRange = new StyleRange();
/* 431 */         styleRange.start = start;
/* 432 */         styleRange.length = me.length();
/* 433 */         styleRange.foreground = colour;
/* 434 */         this.log.setStyleRange(styleRange);
/*     */       }
/*     */       
/* 437 */       this.log.append("\n");
/*     */     }
/*     */     else {
/* 440 */       String says = this.lu.getLocalisedMessageText("azbuddy.chat.says", new String[] { buddy_name }) + "\n";
/*     */       
/* 442 */       this.log.append(says);
/*     */       
/* 444 */       if (colour != Colors.black)
/*     */       {
/* 446 */         StyleRange styleRange = new StyleRange();
/* 447 */         styleRange.start = start;
/* 448 */         styleRange.length = says.length();
/* 449 */         styleRange.foreground = colour;
/* 450 */         this.log.setStyleRange(styleRange);
/*     */       }
/*     */       
/* 453 */       this.log.append(msg + "\n");
/*     */     }
/*     */     
/* 456 */     this.log.setSelection(this.log.getText().length());
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginViewChat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */