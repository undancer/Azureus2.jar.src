/*      */ package com.aelitis.azureus.core.devices.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.devices.TranscodeFile;
/*      */ import java.io.DataInputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.Socket;
/*      */ import java.net.URL;
/*      */ import java.net.URLDecoder;
/*      */ import java.net.URLEncoder;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.regex.Pattern;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.xml.util.XUXmlWriter;
/*      */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageRequest;
/*      */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageResponse;
/*      */ import org.gudy.azureus2.pluginsimpl.local.utils.FormattersImpl;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DeviceTivo
/*      */   extends DeviceMediaRendererImpl
/*      */ {
/*      */   private static final boolean TRACE = false;
/*      */   private static final String NL = "\r\n";
/*   53 */   private static Map<String, Comparator<ItemInfo>> sort_comparators = new HashMap();
/*      */   private String server_name;
/*      */   
/*   56 */   static { sort_comparators.put("Type", new Comparator()
/*      */     {
/*      */ 
/*      */ 
/*      */       public int compare(DeviceTivo.ItemInfo o1, DeviceTivo.ItemInfo o2)
/*      */       {
/*      */ 
/*      */ 
/*   64 */         if (o1.isContainer() == o2.isContainer())
/*      */         {
/*   66 */           return 0;
/*      */         }
/*      */         
/*   69 */         if (o1.isContainer())
/*      */         {
/*   71 */           return -1;
/*      */         }
/*      */         
/*   74 */         return 1;
/*      */       }
/*      */       
/*   77 */     });
/*   78 */     sort_comparators.put("Title", new Comparator()
/*      */     {
/*      */ 
/*      */ 
/*   82 */       Comparator<String> c = new FormattersImpl().getAlphanumericComparator(true);
/*      */       
/*      */ 
/*      */ 
/*      */       public int compare(DeviceTivo.ItemInfo o1, DeviceTivo.ItemInfo o2)
/*      */       {
/*   88 */         return this.c.compare(o1.getName(), o2.getName());
/*      */       }
/*      */       
/*   91 */     });
/*   92 */     sort_comparators.put("CreationDate", new Comparator()
/*      */     {
/*      */ 
/*      */ 
/*      */       public int compare(DeviceTivo.ItemInfo o1, DeviceTivo.ItemInfo o2)
/*      */       {
/*      */ 
/*      */ 
/*  100 */         long res = o1.getCreationMillis() - o2.getCreationMillis();
/*      */         
/*  102 */         if (res < 0L)
/*  103 */           return -1;
/*  104 */         if (res > 0L) {
/*  105 */           return 1;
/*      */         }
/*  107 */         return 0;
/*      */       }
/*      */       
/*      */ 
/*  111 */     });
/*  112 */     sort_comparators.put("LastChangeDate", sort_comparators.get("CreationDate"));
/*  113 */     sort_comparators.put("CaptureDate", sort_comparators.get("CreationDate"));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DeviceTivo(DeviceManagerImpl _manager, String _uid, String _classification)
/*      */   {
/*  125 */     super(_manager, _uid, _classification, false);
/*      */     
/*  127 */     setName("TiVo", true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DeviceTivo(DeviceManagerImpl _manager, Map _map)
/*      */     throws IOException
/*      */   {
/*  137 */     super(_manager, _map);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean updateFrom(DeviceImpl _other, boolean _is_alive)
/*      */   {
/*  145 */     if (!super.updateFrom(_other, _is_alive))
/*      */     {
/*  147 */       return false;
/*      */     }
/*      */     
/*  150 */     if (!(_other instanceof DeviceTivo))
/*      */     {
/*  152 */       Debug.out("Inconsistent");
/*      */       
/*  154 */       return false;
/*      */     }
/*      */     
/*  157 */     DeviceTivo other = (DeviceTivo)_other;
/*      */     
/*  159 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void initialise()
/*      */   {
/*  165 */     super.initialise();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean canFilterFilesView()
/*      */   {
/*  171 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean canAssociate()
/*      */   {
/*  177 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean canRestrictAccess()
/*      */   {
/*  185 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean canShowCategories()
/*      */   {
/*  191 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean getShowCategoriesDefault()
/*      */   {
/*  197 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   protected String getMachineName()
/*      */   {
/*  203 */     return getPersistentStringProperty("tivo_machine", null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void found(DeviceTivoManager _tivo_manager, InetAddress _address, String _server_name, String _machine)
/*      */   {
/*  213 */     boolean first_time = false;
/*      */     
/*  215 */     synchronized (this)
/*      */     {
/*  217 */       if (this.server_name == null)
/*      */       {
/*  219 */         this.server_name = _server_name;
/*      */         
/*  221 */         first_time = true;
/*      */       }
/*      */     }
/*      */     
/*  225 */     if ((_machine == null) && (!this.tried_tcp_beacon)) {
/*      */       try
/*      */       {
/*  228 */         Socket socket = new Socket();
/*      */         try
/*      */         {
/*  231 */           socket.connect(new InetSocketAddress(_address, 2190), 5000);
/*      */           
/*  233 */           socket.setSoTimeout(5000);
/*      */           
/*  235 */           DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
/*      */           
/*  237 */           byte[] beacon_out = _tivo_manager.encodeBeacon(false, 0);
/*      */           
/*  239 */           dos.writeInt(beacon_out.length);
/*      */           
/*  241 */           dos.write(beacon_out);
/*      */           
/*  243 */           DataInputStream dis = new DataInputStream(socket.getInputStream());
/*      */           
/*  245 */           int len = dis.readInt();
/*      */           
/*  247 */           if (len < 65536)
/*      */           {
/*  249 */             byte[] bytes = new byte[len];
/*      */             
/*  251 */             int pos = 0;
/*      */             
/*  253 */             while (pos < len)
/*      */             {
/*  255 */               int read = dis.read(bytes, pos, len - pos);
/*      */               
/*  257 */               pos += read;
/*      */             }
/*      */             
/*  260 */             Map<String, String> beacon_in = _tivo_manager.decodeBeacon(bytes, len);
/*      */             
/*  262 */             _machine = (String)beacon_in.get("machine");
/*      */           }
/*      */         }
/*      */         finally {
/*  266 */           socket.close();
/*      */         }
/*      */         
/*      */       }
/*      */       catch (Throwable e) {}finally
/*      */       {
/*  272 */         this.tried_tcp_beacon = true;
/*      */       }
/*      */     }
/*      */     
/*  276 */     if (_machine != null)
/*      */     {
/*  278 */       String existing = getMachineName();
/*      */       
/*  280 */       if ((existing == null) || (!existing.equals(_machine)))
/*      */       {
/*  282 */         setPersistentStringProperty("tivo_machine", _machine);
/*      */       }
/*      */     }
/*      */     
/*  286 */     setAddress(_address);
/*      */     
/*  288 */     alive();
/*      */     
/*  290 */     if (first_time)
/*      */     {
/*  292 */       browseReceived();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean generate(TrackerWebPageRequest request, TrackerWebPageResponse response)
/*      */     throws IOException
/*      */   {
/*  304 */     InetSocketAddress local_address = request.getLocalAddress();
/*      */     
/*  306 */     if (local_address == null)
/*      */     {
/*  308 */       return false;
/*      */     }
/*      */     
/*  311 */     String host = local_address.getAddress().getHostAddress();
/*      */     
/*  313 */     String url = request.getURL();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  319 */     if (!url.startsWith("/TiVoConnect?"))
/*      */     {
/*  321 */       return false;
/*      */     }
/*      */     
/*  324 */     int pos = url.indexOf('?');
/*      */     
/*  326 */     if (pos == -1)
/*      */     {
/*  328 */       return false;
/*      */     }
/*      */     
/*  331 */     String[] bits = url.substring(pos + 1).split("&");
/*      */     
/*  333 */     Map<String, String> args = new HashMap();
/*      */     
/*  335 */     for (String bit : bits)
/*      */     {
/*  337 */       String[] x = bit.split("=");
/*      */       
/*  339 */       args.put(x[0], URLDecoder.decode(x[1], "UTF-8"));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  348 */     String command = (String)args.get("Command");
/*      */     
/*  350 */     if (command == null)
/*      */     {
/*  352 */       return false;
/*      */     }
/*      */     
/*  355 */     String reply = null;
/*      */     
/*  357 */     if (command.equals("QueryContainer"))
/*      */     {
/*  359 */       String container = (String)args.get("Container");
/*      */       
/*  361 */       if (container == null)
/*      */       {
/*  363 */         return false;
/*      */       }
/*      */       
/*  366 */       if (container.equals("/"))
/*      */       {
/*  368 */         reply = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n<TiVoContainer>\r\n    <Details>\r\n        <Title>" + this.server_name + "</Title>" + "\r\n" + "        <ContentType>x-container/tivo-server</ContentType>" + "\r\n" + "        <SourceFormat>x-container/folder</SourceFormat>" + "\r\n" + "        <TotalItems>1</TotalItems>" + "\r\n" + "    </Details>" + "\r\n" + "    <Item>" + "\r\n" + "        <Details>" + "\r\n" + "            <Title>" + this.server_name + "</Title>" + "\r\n" + "            <ContentType>x-container/tivo-videos</ContentType>" + "\r\n" + "            <SourceFormat>x-container/folder</SourceFormat>" + "\r\n" + "        </Details>" + "\r\n" + "        <Links>" + "\r\n" + "            <Content>" + "\r\n" + "                <Url>/TiVoConnect?Command=QueryContainer&amp;Container=" + urlencode("/Content") + "</Url>" + "\r\n" + "                <ContentType>x-container/tivo-videos</ContentType>" + "\r\n" + "            </Content>" + "\r\n" + "        </Links>" + "\r\n" + "    </Item>" + "\r\n" + "    <ItemStart>0</ItemStart>" + "\r\n" + "    <ItemCount>1</ItemCount>" + "\r\n" + "</TiVoContainer>";
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*  394 */       else if (container.startsWith("/Content"))
/*      */       {
/*  396 */         boolean show_categories = getShowCategories();
/*      */         
/*  398 */         String recurse = (String)args.get("Recurse");
/*      */         
/*  400 */         if ((recurse != null) && (recurse.equals("Yes")))
/*      */         {
/*  402 */           show_categories = false;
/*      */         }
/*      */         
/*  405 */         TranscodeFileImpl[] tfs = getFiles();
/*      */         
/*  407 */         String category_or_tag = null;
/*      */         
/*  409 */         Map<String, ContainerInfo> categories_or_tags = null;
/*      */         
/*  411 */         if (show_categories)
/*      */         {
/*  413 */           if (container.startsWith("/Content/"))
/*      */           {
/*  415 */             category_or_tag = container.substring(container.lastIndexOf('/') + 1);
/*      */           }
/*      */           else
/*      */           {
/*  419 */             categories_or_tags = new HashMap();
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  425 */         List<ItemInfo> items = new ArrayList(tfs.length);
/*      */         
/*  427 */         for (TranscodeFileImpl file : tfs)
/*      */         {
/*  429 */           if ((file.isComplete()) || 
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  434 */             (setupStreamXCode(file)))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  440 */             if (category_or_tag != null)
/*      */             {
/*  442 */               boolean hit = false;
/*      */               
/*  444 */               String[] cats = file.getCategories();
/*  445 */               String[] tags = file.getTags(true);
/*      */               
/*  447 */               for (String[] strs : new String[][] { cats, tags })
/*      */               {
/*  449 */                 for (String c : strs)
/*      */                 {
/*  451 */                   if (c.equals(category_or_tag))
/*      */                   {
/*  453 */                     hit = true;
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/*  458 */               if (!hit) {}
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/*      */ 
/*  464 */               FileInfo info = new FileInfo(file, host);
/*      */               
/*  466 */               if (info.isOK())
/*      */               {
/*  468 */                 boolean skip = false;
/*      */                 
/*  470 */                 if (categories_or_tags != null)
/*      */                 {
/*  472 */                   String[] cats = file.getCategories();
/*  473 */                   String[] tags = file.getTags(true);
/*      */                   
/*  475 */                   if ((cats.length > 0) || (tags.length > 0))
/*      */                   {
/*  477 */                     skip = true;
/*      */                     
/*  479 */                     for (String[] strs : new String[][] { cats, tags })
/*      */                     {
/*  481 */                       for (String s : strs)
/*      */                       {
/*  483 */                         ContainerInfo cont = (ContainerInfo)categories_or_tags.get(s);
/*      */                         
/*  485 */                         if (cont == null)
/*      */                         {
/*  487 */                           items.add(cont = new ContainerInfo(s));
/*      */                           
/*  489 */                           categories_or_tags.put(s, cont);
/*      */                         }
/*      */                         
/*  492 */                         cont.addChild();
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 
/*  498 */                 if (!skip)
/*      */                 {
/*  500 */                   items.add(info);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  507 */         String sort_order = (String)args.get("SortOrder");
/*      */         
/*  509 */         if (sort_order != null)
/*      */         {
/*  511 */           String[] keys = Constants.PAT_SPLIT_COMMA.split(sort_order);
/*      */           
/*  513 */           final List<Comparator<ItemInfo>> comparators = new ArrayList();
/*  514 */           final List<Boolean> reverses = new ArrayList();
/*      */           
/*  516 */           for (String key : keys)
/*      */           {
/*  518 */             boolean reverse = false;
/*      */             
/*  520 */             if (key.startsWith("!"))
/*      */             {
/*  522 */               reverse = true;
/*      */               
/*  524 */               key = key.substring(1);
/*      */             }
/*      */             
/*  527 */             Comparator<ItemInfo> comp = (Comparator)sort_comparators.get(key);
/*      */             
/*  529 */             if (comp != null)
/*      */             {
/*  531 */               comparators.add(comp);
/*  532 */               reverses.add(Boolean.valueOf(reverse));
/*      */             }
/*      */           }
/*      */           
/*  536 */           if (comparators.size() > 0)
/*      */           {
/*  538 */             Collections.sort(items, new Comparator()
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */               public int compare(DeviceTivo.ItemInfo i1, DeviceTivo.ItemInfo i2)
/*      */               {
/*      */ 
/*      */ 
/*  547 */                 for (int i = 0; i < comparators.size(); i++)
/*      */                 {
/*  549 */                   Comparator<DeviceTivo.ItemInfo> comp = (Comparator)comparators.get(i);
/*      */                   
/*  551 */                   int res = comp.compare(i1, i2);
/*      */                   
/*  553 */                   if (res != 0)
/*      */                   {
/*  555 */                     if (((Boolean)reverses.get(i)).booleanValue())
/*      */                     {
/*  557 */                       if (res < 0)
/*      */                       {
/*  559 */                         res = 1;
/*      */                       }
/*      */                       else
/*      */                       {
/*  563 */                         res = -1;
/*      */                       }
/*      */                     }
/*      */                     
/*  567 */                     return res;
/*      */                   }
/*      */                 }
/*      */                 
/*  571 */                 return 0;
/*      */               }
/*      */             });
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  579 */         String item_count = (String)args.get("ItemCount");
/*  580 */         String anchor_offset = (String)args.get("AnchorOffset");
/*  581 */         String anchor = (String)args.get("AnchorItem");
/*      */         
/*      */         int num_items;
/*      */         int num_items;
/*  585 */         if (item_count == null)
/*      */         {
/*  587 */           num_items = items.size();
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*  593 */           num_items = Integer.parseInt(item_count);
/*      */         }
/*      */         
/*      */         int anchor_index;
/*      */         int anchor_index;
/*  598 */         if (num_items < 0)
/*      */         {
/*  600 */           anchor_index = items.size();
/*      */         }
/*      */         else
/*      */         {
/*  604 */           anchor_index = -1;
/*      */         }
/*      */         
/*  607 */         if (anchor != null)
/*      */         {
/*  609 */           for (int i = 0; i < items.size(); i++)
/*      */           {
/*  611 */             ItemInfo info = (ItemInfo)items.get(i);
/*      */             
/*  613 */             if (anchor.equals(info.getLinkURL()))
/*      */             {
/*  615 */               anchor_index = i;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  620 */         if (anchor_offset != null)
/*      */         {
/*  622 */           anchor_index += Integer.parseInt(anchor_offset);
/*      */           
/*  624 */           if (anchor_index < -1)
/*      */           {
/*  626 */             anchor_index = -1;
/*      */           }
/*  628 */           else if (anchor_index > items.size())
/*      */           {
/*  630 */             anchor_index = items.size();
/*      */           }
/*      */         }
/*      */         
/*      */         int end_index;
/*      */         int start_index;
/*      */         int end_index;
/*  637 */         if (num_items > 0)
/*      */         {
/*  639 */           int start_index = anchor_index + 1;
/*      */           
/*  641 */           end_index = anchor_index + num_items;
/*      */         }
/*      */         else
/*      */         {
/*  645 */           start_index = anchor_index + num_items;
/*      */           
/*  647 */           end_index = anchor_index - 1;
/*      */         }
/*      */         
/*  650 */         if (start_index < 0)
/*      */         {
/*  652 */           start_index = 0;
/*      */         }
/*      */         
/*  655 */         if (end_index >= items.size())
/*      */         {
/*  657 */           end_index = items.size() - 1;
/*      */         }
/*      */         
/*  660 */         int num_to_return = end_index - start_index + 1;
/*      */         
/*  662 */         if (num_to_return < 0)
/*      */         {
/*  664 */           num_to_return = 0;
/*      */         }
/*      */         
/*  667 */         String machine = getMachineName();
/*      */         
/*  669 */         if (machine == null)
/*      */         {
/*      */ 
/*      */ 
/*  673 */           machine = "TivoHDDVR";
/*      */         }
/*      */         
/*  676 */         String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n<TiVoContainer>\r\n    <Tivos>\r\n      <Tivo>" + machine + "</Tivo>" + "\r\n" + "    </Tivos>" + "\r\n" + "    <ItemStart>" + start_index + "</ItemStart>" + "\r\n" + "    <ItemCount>" + num_to_return + "</ItemCount>" + "\r\n" + "    <Details>" + "\r\n" + "        <Title>" + escape(container) + "</Title>" + "\r\n" + "        <ContentType>x-container/tivo-videos</ContentType>" + "\r\n" + "        <SourceFormat>x-container/folder</SourceFormat>" + "\r\n" + "        <TotalItems>" + items.size() + "</TotalItems>" + "\r\n" + "    </Details>" + "\r\n";
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  691 */         reply = header;
/*      */         
/*  693 */         for (int i = start_index; i <= end_index; i++)
/*      */         {
/*  695 */           ItemInfo item = (ItemInfo)items.get(i);
/*      */           
/*  697 */           if ((item instanceof FileInfo))
/*      */           {
/*  699 */             FileInfo file = (FileInfo)item;
/*      */             
/*  701 */             long file_size = file.getTargetSize();
/*      */             
/*  703 */             String title = escape(file.getName());
/*  704 */             String desc = title;
/*      */             
/*  706 */             int MAX_TITLE_LENGTH = 30;
/*      */             
/*  708 */             if (title.length() > MAX_TITLE_LENGTH)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*  713 */               String temp = "";
/*      */               
/*  715 */               for (int j = 0; j < title.length(); j++)
/*      */               {
/*  717 */                 char c = title.charAt(j);
/*      */                 
/*  719 */                 if (Character.isLetterOrDigit(c))
/*      */                 {
/*  721 */                   temp = temp + c;
/*      */                 }
/*      */                 else {
/*  724 */                   temp = temp + ' ';
/*      */                 }
/*      */               }
/*      */               
/*  728 */               int space_pos = temp.indexOf(' ');
/*      */               
/*  730 */               if ((space_pos == -1) || (space_pos > MAX_TITLE_LENGTH))
/*      */               {
/*  732 */                 temp = temp.substring(0, 30) + "...";
/*      */               }
/*      */               
/*  735 */               title = temp;
/*      */             }
/*      */             
/*  738 */             reply = reply + "    <Item>\r\n        <Details>\r\n            <Title>" + title + "</Title>" + "\r\n" + "            <ContentType>video/x-tivo-mpeg</ContentType>" + "\r\n" + "            <SourceFormat>video/x-ms-wmv</SourceFormat>" + "\r\n";
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  746 */             if (file_size > 0L) {
/*  747 */               reply = reply + "            <SourceSize>" + file_size + "</SourceSize>" + "\r\n";
/*      */             }
/*      */             else {
/*  750 */               long est_size = file.getEstimatedTargetSize();
/*      */               
/*  752 */               if (est_size > 0L) {
/*  753 */                 reply = reply + "            <SourceSize>" + est_size + "</SourceSize>" + "\r\n";
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*  758 */             reply = reply + "            <Duration>" + file.getDurationMillis() + "</Duration>" + "\r\n" + "            <Description>" + desc + "</Description>" + "\r\n" + "            <SourceChannel>0</SourceChannel>" + "\r\n" + "            <SourceStation></SourceStation>" + "\r\n" + "            <SeriesId></SeriesId>" + "\r\n" + "            <CaptureDate>" + file.getCaptureDate() + "</CaptureDate>" + "\r\n" + "        </Details>" + "\r\n" + "        <Links>" + "\r\n" + "            <Content>" + "\r\n" + "                <ContentType>video/x-tivo-mpeg</ContentType>" + "\r\n" + "                    <AcceptsParams>No</AcceptsParams>" + "\r\n" + "                    <Url>" + file.getLinkURL() + "</Url>" + "\r\n" + "                </Content>" + "\r\n" + "                <CustomIcon>" + "\r\n" + "                    <ContentType>video/*</ContentType>" + "\r\n" + "                    <AcceptsParams>No</AcceptsParams>" + "\r\n" + "                    <Url>urn:tivo:image:save-until-i-delete-recording</Url>" + "\r\n" + "                </CustomIcon>" + "\r\n" + "        </Links>" + "\r\n" + "    </Item>" + "\r\n";
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           }
/*      */           else
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
/*      */ 
/*  782 */             ContainerInfo cont = (ContainerInfo)item;
/*      */             
/*  784 */             reply = reply + "    <Item>\r\n        <Details>\r\n            <Title>" + cont.getName() + "</Title>" + "\r\n" + "            <ContentType>x-container/tivo-videos</ContentType>" + "\r\n" + "            <SourceFormat>x-container/folder</SourceFormat>" + "\r\n" + "            <TotalItems>" + cont.getChildCount() + "</TotalItems>" + "\r\n" + "        </Details>" + "\r\n" + "        <Links>" + "\r\n" + "            <Content>" + "\r\n" + "                <Url>" + cont.getLinkURL() + "</Url>" + "\r\n" + "                <ContentType>x-container/tivo-videos</ContentType>" + "\r\n" + "            </Content>" + "\r\n" + "        </Links>" + "\r\n" + "    </Item>" + "\r\n";
/*      */           }
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
/*      */ 
/*  802 */         String footer = "</TiVoContainer>";
/*      */         
/*      */ 
/*  805 */         reply = reply + footer;
/*      */       }
/*      */       
/*      */     }
/*  809 */     else if (command.equals("QueryFormats"))
/*      */     {
/*  811 */       String source_format = (String)args.get("SourceFormat");
/*      */       
/*  813 */       if ((source_format != null) && (source_format.startsWith("video")))
/*      */       {
/*      */ 
/*      */ 
/*  817 */         reply = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<TiVoFormats><Format>\r\n<ContentType>video/x-tivo-mpeg</ContentType><Description/>\r\n</Format></TiVoFormats>";
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  825 */     if (reply == null)
/*      */     {
/*  827 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  834 */     response.setContentType("text/xml");
/*      */     
/*  836 */     response.getOutputStream().write(reply.getBytes("UTF-8"));
/*      */     
/*  838 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   protected static String urlencode(String str)
/*      */   {
/*      */     try
/*      */     {
/*  846 */       return URLEncoder.encode(str, "UTF-8");
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  850 */       Debug.out(e);
/*      */     }
/*  852 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static String escape(String str)
/*      */   {
/*  860 */     return XUXmlWriter.escapeXML(str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void getDisplayProperties(List<String[]> dp)
/*      */   {
/*  867 */     super.getDisplayProperties(dp);
/*      */     
/*  869 */     addDP(dp, "devices.tivo.machine", getMachineName());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void generate(IndentWriter writer)
/*      */   {
/*  876 */     super.generate(writer);
/*      */     try
/*      */     {
/*  879 */       writer.indent();
/*      */       
/*  881 */       writer.println("tico_machine=" + getMachineName());
/*      */     }
/*      */     finally
/*      */     {
/*  885 */       writer.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean tried_tcp_beacon;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static class ContainerInfo
/*      */     extends DeviceTivo.ItemInfo
/*      */   {
/*      */     private String name;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private int child_count;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected ContainerInfo(String _name)
/*      */     {
/*  918 */       this.name = _name;
/*      */     }
/*      */     
/*      */ 
/*      */     protected String getName()
/*      */     {
/*  924 */       return this.name;
/*      */     }
/*      */     
/*      */ 
/*      */     protected String getLinkURL()
/*      */     {
/*  930 */       return "/TiVoConnect?Command=QueryContainer&amp;Container=" + DeviceTivo.urlencode(new StringBuilder().append("/Content/").append(this.name).toString());
/*      */     }
/*      */     
/*      */ 
/*      */     protected void addChild()
/*      */     {
/*  936 */       this.child_count += 1;
/*      */     }
/*      */     
/*      */ 
/*      */     protected int getChildCount()
/*      */     {
/*  942 */       return this.child_count;
/*      */     }
/*      */     
/*      */ 
/*      */     public long getCreationMillis()
/*      */     {
/*  948 */       return 0L;
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean isContainer()
/*      */     {
/*  954 */       return true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected static class FileInfo
/*      */     extends DeviceTivo.ItemInfo
/*      */   {
/*      */     private TranscodeFile file;
/*      */     
/*      */     private String stream_url;
/*      */     
/*      */     private long target_size;
/*      */     
/*      */     private long creation_millis;
/*      */     
/*      */     boolean ok;
/*      */     
/*      */     protected FileInfo(TranscodeFile _file, String _host)
/*      */     {
/*  974 */       this.file = _file;
/*      */       try
/*      */       {
/*  977 */         URL url = this.file.getStreamURL(_host);
/*      */         
/*  979 */         if (url == null)
/*      */         {
/*  981 */           return;
/*      */         }
/*      */         
/*  984 */         this.stream_url = url.toExternalForm();
/*      */         try
/*      */         {
/*  987 */           if (this.file.isComplete())
/*      */           {
/*  989 */             this.target_size = this.file.getTargetFile().getLength();
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {}
/*      */         
/*  994 */         this.creation_millis = this.file.getCreationDateMillis();
/*      */         
/*  996 */         this.ok = true;
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected boolean isOK()
/*      */     {
/* 1006 */       return this.ok;
/*      */     }
/*      */     
/*      */ 
/*      */     protected String getName()
/*      */     {
/* 1012 */       return this.file.getName();
/*      */     }
/*      */     
/*      */ 
/*      */     protected String getLinkURL()
/*      */     {
/* 1018 */       return this.stream_url;
/*      */     }
/*      */     
/*      */ 
/*      */     protected long getTargetSize()
/*      */     {
/* 1024 */       return this.target_size;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected long getEstimatedTargetSize()
/*      */     {
/*      */       try
/*      */       {
/* 1033 */         long duration_secs = getDurationMillis() / 1000L;
/*      */         
/* 1035 */         if (duration_secs == 0L)
/*      */         {
/* 1037 */           long length = this.file.getSourceFile().getLength();
/*      */           
/* 1039 */           return length * 10L;
/*      */         }
/*      */         
/* 1042 */         long mb_per_sec = 3L;
/*      */         
/* 1044 */         return duration_secs * mb_per_sec * 1024L * 1024L;
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       
/*      */ 
/* 1049 */       return 0L;
/*      */     }
/*      */     
/*      */ 
/*      */     protected long getDurationMillis()
/*      */     {
/* 1055 */       return this.file.getDurationMillis();
/*      */     }
/*      */     
/*      */ 
/*      */     public long getCreationMillis()
/*      */     {
/* 1061 */       return this.creation_millis;
/*      */     }
/*      */     
/*      */ 
/*      */     protected String getCaptureDate()
/*      */     {
/* 1067 */       return "0x" + Long.toString(this.creation_millis / 1000L, 16);
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean isContainer()
/*      */     {
/* 1073 */       return false;
/*      */     }
/*      */   }
/*      */   
/*      */   protected static abstract class ItemInfo
/*      */   {
/*      */     protected abstract String getName();
/*      */     
/*      */     protected abstract String getLinkURL();
/*      */     
/*      */     protected abstract boolean isContainer();
/*      */     
/*      */     public abstract long getCreationMillis();
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/impl/DeviceTivo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */