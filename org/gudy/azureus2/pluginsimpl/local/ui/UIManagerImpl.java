/*     */ package org.gudy.azureus2.pluginsimpl.local.ui;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import com.aelitis.azureus.ui.IUIIntializer;
/*     */ import com.aelitis.azureus.ui.selectedcontent.ISelectedContent;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentListener;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
/*     */ import java.io.File;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.ui.UIDataSourceListener;
/*     */ import org.gudy.azureus2.plugins.ui.UIException;
/*     */ import org.gudy.azureus2.plugins.ui.UIInputReceiver;
/*     */ import org.gudy.azureus2.plugins.ui.UIInstance;
/*     */ import org.gudy.azureus2.plugins.ui.UIInstanceFactory;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.UIManagerEvent;
/*     */ import org.gudy.azureus2.plugins.ui.UIManagerEventListener;
/*     */ import org.gudy.azureus2.plugins.ui.UIManagerListener;
/*     */ import org.gudy.azureus2.plugins.ui.UIManagerListener2;
/*     */ import org.gudy.azureus2.plugins.ui.UIMessage;
/*     */ import org.gudy.azureus2.plugins.ui.components.UIProgressBar;
/*     */ import org.gudy.azureus2.plugins.ui.components.UITextField;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuManager;
/*     */ import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;
/*     */ import org.gudy.azureus2.plugins.ui.model.BasicPluginViewModel;
/*     */ import org.gudy.azureus2.plugins.ui.model.PluginConfigModel;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableManager;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.menus.MenuManagerImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.model.BasicPluginConfigModelImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.model.BasicPluginViewModelImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.tables.TableManagerImpl;
/*     */ import org.gudy.azureus2.ui.common.UIInstanceBase;
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
/*     */ public class UIManagerImpl
/*     */   implements UIManager
/*     */ {
/*  66 */   protected static AEMonitor class_mon = new AEMonitor("UIManager:class");
/*     */   
/*     */   protected static boolean initialisation_complete;
/*     */   
/*  70 */   protected static CopyOnWriteList<Object[]> ui_listeners = new CopyOnWriteList();
/*  71 */   protected static CopyOnWriteList<UIManagerEventListener> ui_event_listeners = new CopyOnWriteList();
/*     */   
/*  73 */   protected static List<UIInstanceFactory> ui_factories = new ArrayList();
/*  74 */   protected static List<UIManagerEventAdapter> ui_event_history = new ArrayList();
/*  75 */   protected static List<BasicPluginConfigModel> configModels = new ArrayList();
/*     */   
/*     */ 
/*     */   protected PluginInterface pi;
/*     */   
/*     */   protected TableManager table_manager;
/*     */   
/*     */   protected MenuManager menu_manager;
/*     */   
/*     */   private static ArrayList<UIDataSourceListener> listDSListeners;
/*     */   
/*     */ 
/*     */   public UIManagerImpl(PluginInterface _pi)
/*     */   {
/*  89 */     this.pi = _pi;
/*     */     
/*  91 */     this.table_manager = new TableManagerImpl(this);
/*  92 */     this.menu_manager = new MenuManagerImpl(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public PluginInterface getPluginInterface()
/*     */   {
/*  98 */     return this.pi;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public BasicPluginViewModel createBasicPluginViewModel(String name)
/*     */   {
/* 105 */     BasicPluginViewModel model = new BasicPluginViewModelImpl(this, name);
/*     */     
/* 107 */     fireEvent(this.pi, 4, model);
/*     */     
/* 109 */     return model;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void destroy(BasicPluginViewModel model)
/*     */   {
/* 116 */     fireEvent(this.pi, 7, model);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public BasicPluginConfigModel createBasicPluginConfigModel(String section_name)
/*     */   {
/* 123 */     return createBasicPluginConfigModel("plugins", section_name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BasicPluginConfigModel createBasicPluginConfigModel(String parent_section, String section_name)
/*     */   {
/* 132 */     BasicPluginConfigModel model = new BasicPluginConfigModelImpl(this, parent_section, section_name);
/*     */     try
/*     */     {
/* 135 */       class_mon.enter();
/*     */       
/* 137 */       configModels.add(model);
/*     */     }
/*     */     finally
/*     */     {
/* 141 */       class_mon.exit();
/*     */     }
/*     */     
/* 144 */     fireEvent(this.pi, 5, model);
/*     */     
/* 146 */     return model;
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy(BasicPluginConfigModel model)
/*     */   {
/*     */     try
/*     */     {
/* 154 */       class_mon.enter();
/*     */       
/* 156 */       configModels.remove(model);
/*     */     }
/*     */     finally
/*     */     {
/* 160 */       class_mon.exit();
/*     */     }
/*     */     
/* 163 */     fireEvent(this.pi, 8, model);
/*     */   }
/*     */   
/*     */   public PluginConfigModel[] getPluginConfigModels()
/*     */   {
/*     */     try
/*     */     {
/* 170 */       class_mon.enter();
/*     */       
/* 172 */       return (PluginConfigModel[])configModels.toArray(new PluginConfigModel[0]);
/*     */     }
/*     */     finally
/*     */     {
/* 176 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void copyToClipBoard(String data)
/*     */     throws UIException
/*     */   {
/* 186 */     boolean ok = fireEvent(this.pi, 6, data);
/*     */     
/* 188 */     if (!ok)
/*     */     {
/* 190 */       throw new UIException("Failed to deliver request to UI");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void openURL(URL url)
/*     */     throws UIException
/*     */   {
/* 200 */     boolean ok = fireEvent(this.pi, 9, url);
/*     */     
/* 202 */     if (!ok)
/*     */     {
/* 204 */       throw new UIException("Failed to deliver request to UI");
/*     */     }
/*     */   }
/*     */   
/*     */   public TableManager getTableManager() {
/* 209 */     return this.table_manager;
/*     */   }
/*     */   
/*     */   public MenuManager getMenuManager() {
/* 213 */     return this.menu_manager;
/*     */   }
/*     */   
/*     */ 
/*     */   public static void initialisationComplete()
/*     */   {
/* 219 */     SelectedContentManager.addCurrentlySelectedContentListener(new SelectedContentListener()
/*     */     {
/*     */       public void currentlySelectedContentChanged(ISelectedContent[] currentContent, String viewID) {
/* 222 */         UIManagerImpl.triggerDataSourceListeners(SelectedContentManager.convertSelectedContentToObject(null));
/*     */       }
/*     */       
/* 225 */     });
/* 226 */     List<Object[]> to_fire = new ArrayList();
/*     */     try
/*     */     {
/* 229 */       class_mon.enter();
/*     */       
/* 231 */       initialisation_complete = true;
/*     */       
/* 233 */       for (int j = 0; j < ui_factories.size(); j++)
/*     */       {
/* 235 */         UIInstanceFactory factory = (UIInstanceFactory)ui_factories.get(j);
/*     */         
/* 237 */         Iterator<Object[]> it = ui_listeners.iterator();
/*     */         
/* 239 */         while (it.hasNext())
/*     */         {
/* 241 */           Object[] entry = (Object[])it.next();
/*     */           
/* 243 */           List<UIInstanceFactory> fired = (List)entry[2];
/*     */           
/* 245 */           if (!fired.contains(factory))
/*     */           {
/* 247 */             fired.add(factory);
/*     */             
/* 249 */             to_fire.add(new Object[] { entry[0], factory.getInstance((PluginInterface)entry[1]) });
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 255 */       class_mon.exit();
/*     */     }
/*     */     
/* 258 */     for (Object[] entry : to_fire) {
/*     */       try
/*     */       {
/* 261 */         ((UIManagerListener)entry[0]).UIAttached((UIInstance)entry[1]);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 265 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 270 */     for (Object[] entry : to_fire) {
/*     */       try
/*     */       {
/* 273 */         if ((entry[0] instanceof UIManagerListener2)) {
/* 274 */           ((UIManagerListener2)entry[0]).UIAttachedComplete((UIInstance)entry[1]);
/*     */         }
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 279 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void attachUI(UIInstanceFactory factory) throws UIException {
/* 285 */     attachUI(factory, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void attachUI(UIInstanceFactory factory, IUIIntializer init)
/*     */   {
/* 293 */     List<Object[]> to_fire = new ArrayList();
/*     */     try
/*     */     {
/* 296 */       class_mon.enter();
/*     */       
/* 298 */       ui_factories.add(factory);
/*     */       
/* 300 */       if (initialisation_complete)
/*     */       {
/* 302 */         Iterator<Object[]> it = ui_listeners.iterator();
/*     */         
/* 304 */         while (it.hasNext())
/*     */         {
/* 306 */           Object[] entry = (Object[])it.next();
/*     */           
/* 308 */           List<UIInstanceFactory> fired = (List)entry[2];
/*     */           
/* 310 */           fired.add(factory);
/*     */           
/* 312 */           to_fire.add(new Object[] { entry[0], entry[1], factory.getInstance((PluginInterface)entry[1]) });
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 317 */       class_mon.exit();
/*     */     }
/*     */     
/* 320 */     for (Object[] entry : to_fire)
/*     */     {
/* 322 */       PluginInterface pi = (PluginInterface)entry[1];
/*     */       
/* 324 */       String name = pi.getPluginName();
/*     */       
/* 326 */       if (init != null)
/*     */       {
/* 328 */         init.reportCurrentTask(MessageText.getString("splash.plugin.UIinit", new String[] { name }));
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 333 */         init.increaseProgress();
/*     */       }
/*     */       try
/*     */       {
/* 337 */         ((UIManagerListener)entry[0]).UIAttached((UIInstance)entry[2]);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 341 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     
/* 345 */     for (Object[] entry : to_fire) {
/*     */       try
/*     */       {
/* 348 */         if ((entry[0] instanceof UIManagerListener2)) {
/* 349 */           ((UIManagerListener2)entry[0]).UIAttachedComplete((UIInstance)entry[2]);
/*     */         }
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 354 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void detachUI(UIInstanceFactory factory)
/*     */     throws UIException
/*     */   {
/* 365 */     factory.detach();
/*     */     
/* 367 */     List<Object[]> to_fire = new ArrayList();
/*     */     try
/*     */     {
/* 370 */       class_mon.enter();
/*     */       
/* 372 */       ui_factories.remove(factory);
/*     */       
/* 374 */       if (initialisation_complete)
/*     */       {
/* 376 */         Iterator<Object[]> it = ui_listeners.iterator();
/*     */         
/* 378 */         while (it.hasNext())
/*     */         {
/* 380 */           Object[] entry = (Object[])it.next();
/*     */           
/* 382 */           List<UIInstanceFactory> fired = (List)entry[2];
/*     */           
/* 384 */           fired.remove(factory);
/*     */           
/* 386 */           to_fire.add(new Object[] { entry[0], factory.getInstance((PluginInterface)entry[1]) });
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 391 */       class_mon.exit();
/*     */     }
/*     */     
/* 394 */     for (Object[] entry : to_fire) {
/*     */       try
/*     */       {
/* 397 */         ((UIManagerListener)entry[0]).UIDetached((UIInstance)entry[1]);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 401 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addUIListener(UIManagerListener listener)
/*     */   {
/* 410 */     List<UIInstance> to_fire = new ArrayList();
/*     */     try
/*     */     {
/* 413 */       class_mon.enter();
/*     */       
/* 415 */       List<UIInstanceFactory> fired = new ArrayList();
/*     */       
/* 417 */       ui_listeners.add(new Object[] { listener, this.pi, fired });
/*     */       
/* 419 */       if (initialisation_complete)
/*     */       {
/* 421 */         for (int i = 0; i < ui_factories.size(); i++)
/*     */         {
/* 423 */           UIInstanceFactory factory = (UIInstanceFactory)ui_factories.get(i);
/*     */           
/* 425 */           to_fire.add(factory.getInstance(this.pi));
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 430 */       class_mon.exit();
/*     */     }
/*     */     
/* 433 */     for (UIInstance instance : to_fire) {
/*     */       try
/*     */       {
/* 436 */         listener.UIAttached(instance);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 440 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     
/* 444 */     if ((listener instanceof UIManagerListener2)) {
/* 445 */       for (UIInstance instance : to_fire) {
/*     */         try
/*     */         {
/* 448 */           ((UIManagerListener2)listener).UIAttachedComplete(instance);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 452 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeUIListener(UIManagerListener listener)
/*     */   {
/*     */     try
/*     */     {
/* 464 */       class_mon.enter();
/*     */       
/* 466 */       Iterator<Object[]> it = ui_listeners.iterator();
/*     */       
/* 468 */       while (it.hasNext())
/*     */       {
/* 470 */         Object[] entry = (Object[])it.next();
/*     */         
/* 472 */         if (entry[0] == listener)
/*     */         {
/* 474 */           it.remove();
/*     */         }
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 480 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void addUIEventListener(UIManagerEventListener listener)
/*     */   {
/*     */     List<UIManagerEventAdapter> ui_event_history_copy;
/*     */     
/*     */     try
/*     */     {
/* 491 */       class_mon.enter();
/*     */       
/* 493 */       ui_event_listeners.add(listener);
/*     */       
/* 495 */       ui_event_history_copy = new ArrayList(ui_event_history);
/*     */     }
/*     */     finally
/*     */     {
/* 499 */       class_mon.exit();
/*     */     }
/*     */     
/* 502 */     for (int i = 0; i < ui_event_history_copy.size(); i++) {
/*     */       try
/*     */       {
/* 505 */         listener.eventOccurred((UIManagerEvent)ui_event_history_copy.get(i));
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 509 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeUIEventListener(UIManagerEventListener listener)
/*     */   {
/*     */     try
/*     */     {
/* 519 */       class_mon.enter();
/*     */       
/* 521 */       ui_event_listeners.remove(listener);
/*     */     }
/*     */     finally
/*     */     {
/* 525 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/* 529 */   public boolean hasUIInstances() { return !ui_factories.isEmpty(); }
/*     */   
/*     */   public UIInstance[] getUIInstances() {
/*     */     try {
/* 533 */       class_mon.enter();
/* 534 */       ArrayList<UIInstance> result = new ArrayList(ui_factories.size());
/* 535 */       for (int i = 0; i < ui_factories.size(); i++) {
/* 536 */         UIInstanceFactory instance = (UIInstanceFactory)ui_factories.get(i);
/* 537 */         result.add(instance.getInstance(this.pi));
/*     */       }
/* 539 */       return (UIInstance[])result.toArray(new UIInstance[result.size()]);
/*     */     }
/*     */     finally {
/* 542 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean fireEvent(PluginInterface pi, int type, Object data)
/*     */   {
/* 552 */     return fireEvent(new UIManagerEventAdapter(pi, type, data));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean fireEvent(UIManagerEventAdapter event)
/*     */   {
/* 559 */     boolean delivered = false;
/*     */     
/* 561 */     Iterator<UIManagerEventListener> event_it = ui_event_listeners.iterator();
/*     */     
/* 563 */     while (event_it.hasNext()) {
/*     */       try
/*     */       {
/* 566 */         if (((UIManagerEventListener)event_it.next()).eventOccurred(event))
/*     */         {
/* 568 */           delivered = true;
/*     */           
/* 570 */           break;
/*     */         }
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 575 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */     
/* 579 */     int type = event.getType();
/*     */     
/*     */ 
/*     */ 
/* 583 */     if ((type == 4) || (type == 5) || (type == 12) || (type == 15) || (type == 17) || (type == 19))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 590 */       delivered = true;
/*     */       try
/*     */       {
/* 593 */         class_mon.enter();
/*     */         
/* 595 */         ui_event_history.add(event);
/*     */       }
/*     */       finally
/*     */       {
/* 599 */         class_mon.exit();
/*     */       }
/*     */     }
/* 602 */     else if ((type == 7) || (type == 8))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 607 */       delivered = true;
/*     */       try
/*     */       {
/* 610 */         class_mon.enter();
/*     */         
/* 612 */         Object history_it = ui_event_history.iterator();
/*     */         
/* 614 */         while (((Iterator)history_it).hasNext())
/*     */         {
/* 616 */           UIManagerEvent e = (UIManagerEvent)((Iterator)history_it).next();
/*     */           
/* 618 */           int e_type = e.getType();
/*     */           
/* 620 */           if ((e_type == 4) || (e_type == 5))
/*     */           {
/*     */ 
/* 623 */             if (e.getData() == event.getData())
/*     */             {
/* 625 */               ((Iterator)history_it).remove();
/*     */               
/* 627 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       finally {
/* 633 */         class_mon.exit();
/*     */       }
/*     */     }
/*     */     
/* 637 */     return delivered;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void showTextMessage(String title_resource, String message_resource, String contents)
/*     */   {
/* 646 */     fireEvent(this.pi, 1, new String[] { title_resource, message_resource, contents });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long showMessageBox(String title_resource, String message_resource, long message_map)
/*     */   {
/* 655 */     return showMessageBox(title_resource, message_resource, message_map, new Object[0]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long showMessageBox(String title_resource, String message_resource, long message_map, Object[] params)
/*     */   {
/* 665 */     Object[] all_params = new Object[3 + params.length];
/*     */     
/* 667 */     all_params[0] = title_resource;
/* 668 */     all_params[1] = message_resource;
/* 669 */     all_params[2] = new Long(message_map);
/*     */     
/* 671 */     System.arraycopy(params, 0, all_params, 3, params.length);
/*     */     
/* 673 */     UIManagerEventAdapter event = new UIManagerEventAdapter(this.pi, 21, all_params);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 679 */     if (!fireEvent(event))
/*     */     {
/* 681 */       return 0L;
/*     */     }
/*     */     
/* 684 */     return ((Long)event.getResult()).longValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long showMessageBox(String title_resource, String message_resource, long message_map, Map<String, Object> params)
/*     */   {
/* 694 */     Object[] all_params = new Object[4];
/*     */     
/* 696 */     all_params[0] = title_resource;
/* 697 */     all_params[1] = message_resource;
/* 698 */     all_params[2] = new Long(message_map);
/* 699 */     all_params[3] = params;
/*     */     
/* 701 */     UIManagerEventAdapter event = new UIManagerEventAdapter(this.pi, 21, all_params);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 707 */     if (!fireEvent(event))
/*     */     {
/* 709 */       return 0L;
/*     */     }
/*     */     
/* 712 */     return ((Long)event.getResult()).longValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void openTorrent(Torrent torrent)
/*     */   {
/* 719 */     fireEvent(this.pi, 22, torrent);
/*     */   }
/*     */   
/* 722 */   public void openFile(File file) { fireEvent(this.pi, 24, file); }
/* 723 */   public void showFile(File file) { fireEvent(this.pi, 23, file); }
/*     */   
/*     */   public boolean showConfigSection(String sectionID) {
/* 726 */     UIManagerEventAdapter event = new UIManagerEventAdapter(this.pi, 13, sectionID);
/*     */     
/* 728 */     if (!fireEvent(event)) {
/* 729 */       return false;
/*     */     }
/* 731 */     if ((event.getResult() instanceof Boolean)) {
/* 732 */       return false;
/*     */     }
/* 734 */     return ((Boolean)event.getResult()).booleanValue();
/*     */   }
/*     */   
/*     */   public UIInputReceiver getInputReceiver() {
/* 738 */     UIInstance[] instances = getUIInstances();
/* 739 */     UIInputReceiver r = null;
/* 740 */     for (int i = 0; i < instances.length; i++) {
/* 741 */       r = instances[i].getInputReceiver();
/* 742 */       if (r != null) return r;
/*     */     }
/* 744 */     return null;
/*     */   }
/*     */   
/*     */   public UIMessage createMessage() {
/* 748 */     UIInstance[] instances = getUIInstances();
/* 749 */     UIMessage r = null;
/* 750 */     for (int i = 0; i < instances.length; i++) {
/* 751 */       r = instances[i].createMessage();
/* 752 */       if (r != null) return r;
/*     */     }
/* 754 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public BasicPluginViewModel createLoggingViewModel(LoggerChannel channel, boolean use_plugin_name)
/*     */   {
/* 760 */     String log_view_name = use_plugin_name ? this.pi.getPluginName() : channel.getName();
/* 761 */     BasicPluginViewModel model = createBasicPluginViewModel(log_view_name);
/* 762 */     model.getActivity().setVisible(false);
/* 763 */     model.getProgress().setVisible(false);
/* 764 */     model.getStatus().setVisible(false);
/* 765 */     model.attachLoggerChannel(channel);
/* 766 */     return model;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setEverythingHidden(boolean hidden)
/*     */   {
/* 773 */     fireEvent(this.pi, 27, Boolean.valueOf(hidden));
/*     */   }
/*     */   
/*     */ 
/*     */   public static void unload(PluginInterface pi)
/*     */   {
/*     */     try
/*     */     {
/* 781 */       class_mon.enter();
/*     */       
/* 783 */       Iterator<Object[]> it = ui_listeners.iterator();
/*     */       
/* 785 */       while (it.hasNext())
/*     */       {
/* 787 */         Object[] entry = (Object[])it.next();
/*     */         
/* 789 */         if (pi == (PluginInterface)entry[1])
/*     */         {
/* 791 */           it.remove();
/*     */         }
/*     */       }
/*     */       
/* 795 */       Iterator<UIManagerEventAdapter> ev_it = ui_event_history.iterator();
/*     */       
/* 797 */       while (ev_it.hasNext())
/*     */       {
/* 799 */         UIManagerEventAdapter event = (UIManagerEventAdapter)ev_it.next();
/*     */         
/* 801 */         if (event.getPluginInterface() == pi)
/*     */         {
/* 803 */           ev_it.remove();
/*     */         }
/*     */       }
/*     */       
/* 807 */       for (UIInstanceFactory uif : ui_factories) {
/* 808 */         UIInstance instance = uif.getInstance(pi);
/* 809 */         if ((instance instanceof UIInstanceBase)) {
/* 810 */           UIInstanceBase instanceBase = (UIInstanceBase)instance;
/* 811 */           instanceBase.unload(pi);
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 816 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void addDataSourceListener(UIDataSourceListener l, boolean triggerNow) {
/* 821 */     class_mon.enter();
/*     */     try {
/* 823 */       if (listDSListeners == null) {
/* 824 */         listDSListeners = new ArrayList();
/*     */       }
/* 826 */       listDSListeners.add(l);
/*     */     } finally {
/* 828 */       class_mon.exit();
/*     */     }
/* 830 */     if (triggerNow) {
/*     */       try {
/* 832 */         l.dataSourceChanged(SelectedContentManager.convertSelectedContentToObject(null));
/*     */       } catch (Throwable t) {
/* 834 */         Debug.out(t);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeDataSourceListener(UIDataSourceListener l) {
/* 840 */     class_mon.enter();
/*     */     try {
/* 842 */       if (listDSListeners == null) {
/*     */         return;
/*     */       }
/* 845 */       listDSListeners.remove(l);
/*     */     } finally {
/* 847 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public Object getDataSource()
/*     */   {
/* 853 */     return SelectedContentManager.convertSelectedContentToObject(null);
/*     */   }
/*     */   
/*     */   private static void triggerDataSourceListeners(Object ds)
/*     */   {
/* 858 */     class_mon.enter();
/*     */     UIDataSourceListener[] listeners;
/* 860 */     try { if (listDSListeners == null) {
/*     */         return;
/*     */       }
/* 863 */       listeners = (UIDataSourceListener[])listDSListeners.toArray(new UIDataSourceListener[0]);
/*     */     } finally {
/* 865 */       class_mon.exit();
/*     */     }
/* 867 */     for (UIDataSourceListener l : listeners) {
/*     */       try {
/* 869 */         l.dataSourceChanged(ds);
/*     */       } catch (Throwable t) {
/* 871 */         Debug.out(t);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/UIManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */