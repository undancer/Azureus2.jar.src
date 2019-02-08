/*     */ package org.gudy.azureus2.pluginsimpl.local.utils.xml.simpleparser;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.UncloseableInputStream;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.Locale;
/*     */ import java.util.Vector;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import org.apache.commons.lang.Entities;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocument;
/*     */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentAttribute;
/*     */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentException;
/*     */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentNode;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Node;
/*     */ import org.xml.sax.EntityResolver;
/*     */ import org.xml.sax.ErrorHandler;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.SAXParseException;
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
/*     */ public class SimpleXMLParserDocumentImpl
/*     */   implements SimpleXMLParserDocument
/*     */ {
/*     */   private static DocumentBuilderFactory dbf_singleton;
/*     */   private URL source_url;
/*     */   private Document document;
/*     */   private SimpleXMLParserDocumentNodeImpl root_node;
/*     */   
/*     */   public SimpleXMLParserDocumentImpl(File file)
/*     */     throws SimpleXMLParserDocumentException
/*     */   {
/*     */     try
/*     */     {
/*  68 */       create(new FileInputStream(file));
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  72 */       throw new SimpleXMLParserDocumentException(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SimpleXMLParserDocumentImpl(String data)
/*     */     throws SimpleXMLParserDocumentException
/*     */   {
/*     */     try
/*     */     {
/*  83 */       create(new ByteArrayInputStream(data.getBytes("UTF8")));
/*     */     }
/*     */     catch (UnsupportedEncodingException e) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public SimpleXMLParserDocumentImpl(InputStream _input_stream)
/*     */     throws SimpleXMLParserDocumentException
/*     */   {
/* 101 */     this(null, _input_stream);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SimpleXMLParserDocumentImpl(URL _source_url, InputStream _input_stream)
/*     */     throws SimpleXMLParserDocumentException
/*     */   {
/* 111 */     this.source_url = _source_url;
/*     */     
/* 113 */     create(_input_stream);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static synchronized DocumentBuilderFactory getDBF()
/*     */   {
/* 121 */     if (dbf_singleton == null)
/*     */     {
/* 123 */       dbf_singleton = DocumentBuilderFactory.newInstance();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 129 */       dbf_singleton.setNamespaceAware(true);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 134 */       dbf_singleton.setValidating(false);
/*     */       
/*     */ 
/*     */ 
/* 138 */       dbf_singleton.setIgnoringComments(true);
/* 139 */       dbf_singleton.setIgnoringElementContentWhitespace(true);
/* 140 */       dbf_singleton.setCoalescing(true);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 148 */       dbf_singleton.setExpandEntityReferences(true);
/*     */     }
/*     */     
/* 151 */     return dbf_singleton;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void create(InputStream _input_stream)
/*     */     throws SimpleXMLParserDocumentException
/*     */   {
/* 162 */     if (!_input_stream.markSupported())
/*     */     {
/* 164 */       _input_stream = new BufferedInputStream(_input_stream);
/*     */     }
/*     */     
/* 167 */     _input_stream.mark(102400);
/*     */     
/*     */ 
/*     */ 
/* 171 */     UncloseableInputStream uc_is = new UncloseableInputStream(_input_stream);
/*     */     
/* 173 */     SimpleXMLParserDocumentException error = null;
/*     */     try
/*     */     {
/* 176 */       createSupport(uc_is);
/*     */       String stuff;
/*     */       String msg;
/*     */       String stuff;
/*     */       String stuff; return; } catch (SimpleXMLParserDocumentException e) { msg = Debug.getNestedExceptionMessage(e);
/*     */       
/* 182 */       if (((msg.contains("entity")) && (msg.contains("was referenced"))) || (msg.contains("entity reference")))
/*     */       {
/*     */ 
/*     */         try
/*     */         {
/*     */ 
/* 188 */           _input_stream.reset();
/*     */           
/* 190 */           createSupport(new EntityFudger(_input_stream));
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
/* 212 */           if ((Constants.isCVSVersion()) && (error != null)) {
/*     */             try
/*     */             {
/* 215 */               _input_stream.reset();
/*     */               
/* 217 */               stuff = FileUtil.readInputStreamAsStringWithTruncation(_input_stream, 2014);
/*     */               
/* 219 */               Debug.out("RSS parsing failed for '" + stuff + "': " + Debug.getExceptionMessage(error));
/*     */             }
/*     */             catch (Throwable e) {}
/*     */           }
/*     */           try
/*     */           {
/* 225 */             _input_stream.close();
/*     */           }
/*     */           catch (Throwable e) {}
/*     */           return;
/*     */         }
/*     */         catch (Throwable f)
/*     */         {
/* 196 */           if ((f instanceof SimpleXMLParserDocumentException))
/*     */           {
/* 198 */             error = (SimpleXMLParserDocumentException)f;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 203 */       if (error == null)
/*     */       {
/* 205 */         error = e;
/*     */       }
/*     */       
/* 208 */       throw error;
/*     */     }
/*     */     finally
/*     */     {
/* 212 */       if ((Constants.isCVSVersion()) && (error != null)) {
/*     */         try
/*     */         {
/* 215 */           _input_stream.reset();
/*     */           
/* 217 */           stuff = FileUtil.readInputStreamAsStringWithTruncation(_input_stream, 2014);
/*     */           
/* 219 */           Debug.out("RSS parsing failed for '" + stuff + "': " + Debug.getExceptionMessage(error));
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */       try
/*     */       {
/* 225 */         _input_stream.close();
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void createSupport(InputStream input_stream)
/*     */     throws SimpleXMLParserDocumentException
/*     */   {
/*     */     try
/*     */     {
/* 239 */       DocumentBuilderFactory dbf = getDBF();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 244 */       DocumentBuilder db = dbf.newDocumentBuilder();
/*     */       
/*     */ 
/*     */ 
/* 248 */       OutputStreamWriter errorWriter = new OutputStreamWriter(System.err);
/*     */       
/* 250 */       MyErrorHandler error_handler = new MyErrorHandler(new PrintWriter(errorWriter, true));
/*     */       
/* 252 */       db.setErrorHandler(error_handler);
/*     */       
/* 254 */       db.setEntityResolver(new EntityResolver()
/*     */       {
/*     */ 
/*     */ 
/*     */         public InputSource resolveEntity(String publicId, String systemId)
/*     */         {
/*     */ 
/*     */ 
/*     */           try
/*     */           {
/*     */ 
/*     */ 
/* 266 */             URL url = new URL(systemId);
/*     */             
/* 268 */             if (SimpleXMLParserDocumentImpl.this.source_url != null)
/*     */             {
/* 270 */               String net = AENetworkClassifier.categoriseAddress(SimpleXMLParserDocumentImpl.this.source_url.getHost());
/*     */               
/* 272 */               if (net != "Public")
/*     */               {
/* 274 */                 if (AENetworkClassifier.categoriseAddress(url.getHost()) != net)
/*     */                 {
/* 276 */                   return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/* 281 */             String host = url.getHost();
/*     */             
/* 283 */             InetAddress.getByName(host);
/*     */             
/*     */ 
/*     */ 
/* 287 */             InputStream is = null;
/*     */             try
/*     */             {
/* 290 */               URLConnection con = url.openConnection();
/*     */               
/* 292 */               con.setConnectTimeout(15000);
/* 293 */               con.setReadTimeout(15000);
/*     */               
/* 295 */               is = con.getInputStream();
/*     */               
/* 297 */               byte[] buffer = new byte[32];
/*     */               
/* 299 */               int pos = 0;
/*     */               
/* 301 */               while (pos < buffer.length)
/*     */               {
/* 303 */                 int len = is.read(buffer, pos, buffer.length - pos);
/*     */                 
/* 305 */                 if (len <= 0) {
/*     */                   break;
/*     */                 }
/*     */                 
/*     */ 
/* 310 */                 pos += len;
/*     */               }
/*     */               
/* 313 */               String str = new String(buffer, "UTF-8").trim().toLowerCase(Locale.US);
/*     */               
/* 315 */               if (!str.contains("<?xml"))
/*     */               {
/*     */ 
/*     */ 
/*     */ 
/* 320 */                 buffer = new byte['紀'];
/*     */                 
/* 322 */                 pos = 0;
/*     */                 
/* 324 */                 while (pos < buffer.length)
/*     */                 {
/* 326 */                   int len = is.read(buffer, pos, buffer.length - pos);
/*     */                   
/* 328 */                   if (len <= 0) {
/*     */                     break;
/*     */                   }
/*     */                   
/*     */ 
/* 333 */                   pos += len;
/*     */                 }
/*     */                 
/* 336 */                 str = str + new String(buffer, "UTF-8").trim().toLowerCase(Locale.US);
/*     */                 
/* 338 */                 if ((str.contains("<html")) && (str.contains("<head")))
/*     */                 {
/* 340 */                   throw new Exception("Bad DTD");
/*     */                 }
/*     */               }
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
/* 359 */               return null;
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 345 */               return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
/*     */             }
/*     */             finally
/*     */             {
/* 349 */               if (is != null) {
/*     */                 try
/*     */                 {
/* 352 */                   is.close();
/*     */                 }
/*     */                 catch (Throwable e) {}
/*     */               }
/*     */             }
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
/* 367 */             return null;
/*     */           }
/*     */           catch (UnknownHostException e)
/*     */           {
/* 363 */             return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
/*     */ 
/*     */ 
/*     */           }
/*     */           catch (Throwable e) {}
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 373 */       });
/* 374 */       this.document = db.parse(input_stream);
/*     */       
/* 376 */       SimpleXMLParserDocumentNodeImpl[] root_nodes = parseNode(this.document, false);
/*     */       
/* 378 */       int root_node_count = 0;
/*     */       
/*     */ 
/*     */ 
/* 382 */       for (int i = 0; i < root_nodes.length; i++)
/*     */       {
/* 384 */         SimpleXMLParserDocumentNodeImpl node = root_nodes[i];
/*     */         
/* 386 */         if (node.getNode().getNodeType() != 7)
/*     */         {
/* 388 */           this.root_node = node;
/*     */           
/* 390 */           root_node_count++;
/*     */         }
/*     */       }
/*     */       
/* 394 */       if (root_node_count != 1)
/*     */       {
/* 396 */         throw new SimpleXMLParserDocumentException("invalid document - " + root_nodes.length + " root elements");
/*     */       }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 401 */       throw new SimpleXMLParserDocumentException(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 408 */     return this.root_node.getName();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getFullName()
/*     */   {
/* 414 */     return this.root_node.getFullName();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getNameSpaceURI()
/*     */   {
/* 420 */     return this.root_node.getNameSpaceURI();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getValue()
/*     */   {
/* 426 */     return this.root_node.getValue();
/*     */   }
/*     */   
/*     */ 
/*     */   public SimpleXMLParserDocumentNode[] getChildren()
/*     */   {
/* 432 */     return this.root_node.getChildren();
/*     */   }
/*     */   
/*     */ 
/*     */   public SimpleXMLParserDocumentNode getChild(String name)
/*     */   {
/* 438 */     return this.root_node.getChild(name);
/*     */   }
/*     */   
/*     */ 
/*     */   public SimpleXMLParserDocumentAttribute[] getAttributes()
/*     */   {
/* 444 */     return this.root_node.getAttributes();
/*     */   }
/*     */   
/*     */ 
/*     */   public SimpleXMLParserDocumentAttribute getAttribute(String name)
/*     */   {
/* 450 */     return this.root_node.getAttribute(name);
/*     */   }
/*     */   
/*     */ 
/*     */   public void print()
/*     */   {
/* 456 */     PrintWriter pw = new PrintWriter(System.out);
/*     */     
/* 458 */     print(pw);
/*     */     
/* 460 */     pw.flush();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void print(PrintWriter pw)
/*     */   {
/* 467 */     this.root_node.print(pw, "");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected SimpleXMLParserDocumentNodeImpl[] parseNode(Node node, boolean skip_this_node)
/*     */   {
/* 478 */     int type = node.getNodeType();
/*     */     
/* 480 */     if (((type == 1) || (type == 7)) && (!skip_this_node))
/*     */     {
/*     */ 
/* 483 */       return new SimpleXMLParserDocumentNodeImpl[] { new SimpleXMLParserDocumentNodeImpl(this, node) };
/*     */     }
/*     */     
/* 486 */     Vector v = new Vector();
/*     */     
/* 488 */     for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
/*     */     {
/* 490 */       SimpleXMLParserDocumentNodeImpl[] kids = parseNode(child, false);
/*     */       
/* 492 */       for (int i = 0; i < kids.length; i++)
/*     */       {
/* 494 */         v.addElement(kids[i]);
/*     */       }
/*     */     }
/*     */     
/* 498 */     SimpleXMLParserDocumentNodeImpl[] res = new SimpleXMLParserDocumentNodeImpl[v.size()];
/*     */     
/* 500 */     v.copyInto(res);
/*     */     
/* 502 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class MyErrorHandler
/*     */     implements ErrorHandler
/*     */   {
/*     */     MyErrorHandler(PrintWriter out) {}
/*     */     
/*     */ 
/*     */ 
/*     */     private String getParseExceptionInfo(SAXParseException spe)
/*     */     {
/* 517 */       String systemId = spe.getSystemId();
/* 518 */       if (systemId == null) {
/* 519 */         systemId = "null";
/*     */       }
/* 521 */       String info = "URI=" + systemId + " Line=" + spe.getLineNumber() + ": " + spe.getMessage();
/*     */       
/*     */ 
/* 524 */       return info;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void warning(SAXParseException spe)
/*     */       throws SAXException
/*     */     {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void error(SAXParseException spe)
/*     */       throws SAXException
/*     */     {
/* 545 */       String message = "Error: " + getParseExceptionInfo(spe);
/*     */       
/* 547 */       throw new SAXException(message);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void fatalError(SAXParseException spe)
/*     */       throws SAXException
/*     */     {
/* 556 */       String message = "Fatal Error: " + getParseExceptionInfo(spe);
/*     */       
/* 558 */       throw new SAXException(message, spe);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static class EntityFudger
/*     */     extends InputStream
/*     */   {
/*     */     private InputStream is;
/*     */     
/* 568 */     char[] buffer = new char[16];
/* 569 */     int buffer_pos = 0;
/*     */     
/* 571 */     char[] insertion = new char[16];
/* 572 */     int insertion_pos = 0;
/* 573 */     int insertion_len = 0;
/*     */     
/*     */ 
/*     */ 
/*     */     public EntityFudger(InputStream _is)
/*     */     {
/* 579 */       this.is = _is;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public int read()
/*     */       throws IOException
/*     */     {
/* 587 */       if (this.insertion_len > 0)
/*     */       {
/* 589 */         int result = this.insertion[(this.insertion_pos++)] & 0xFF;
/*     */         
/* 591 */         if (this.insertion_pos == this.insertion_len)
/*     */         {
/* 593 */           this.insertion_pos = 0;
/* 594 */           this.insertion_len = 0;
/*     */         }
/*     */         
/* 597 */         return result;
/*     */       }
/*     */       
/*     */       for (;;)
/*     */       {
/* 602 */         int b = this.is.read();
/*     */         
/* 604 */         if (b < 0)
/*     */         {
/*     */ 
/*     */ 
/* 608 */           if (this.buffer_pos == 0)
/*     */           {
/* 610 */             return b;
/*     */           }
/* 612 */           if (this.buffer_pos == 1)
/*     */           {
/* 614 */             this.buffer_pos = 0;
/*     */             
/* 616 */             return this.buffer[0] & 0xFF;
/*     */           }
/*     */           
/*     */ 
/* 620 */           System.arraycopy(this.buffer, 1, this.insertion, 0, this.buffer_pos - 1);
/*     */           
/* 622 */           this.insertion_len = (this.buffer_pos - 1);
/* 623 */           this.insertion_pos = 0;
/*     */           
/* 625 */           this.buffer_pos = 0;
/*     */           
/* 627 */           return this.buffer[0] & 0xFF;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 633 */         if (this.buffer_pos == 0)
/*     */         {
/* 635 */           if (b == 38)
/*     */           {
/* 637 */             this.buffer[(this.buffer_pos++)] = ((char)b);
/*     */           }
/*     */           else
/*     */           {
/* 641 */             return b;
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 646 */           if (this.buffer_pos == this.buffer.length - 1)
/*     */           {
/*     */ 
/*     */ 
/* 650 */             this.buffer[(this.buffer_pos++)] = ((char)b);
/*     */             
/* 652 */             System.arraycopy(this.buffer, 0, this.insertion, 0, this.buffer_pos);
/*     */             
/* 654 */             this.buffer_pos = 0;
/* 655 */             this.insertion_pos = 0;
/* 656 */             this.insertion_len = this.buffer_pos;
/*     */             
/* 658 */             return this.insertion[(this.insertion_pos++)];
/*     */           }
/*     */           
/*     */ 
/* 662 */           if (b == 59)
/*     */           {
/*     */ 
/*     */ 
/* 666 */             this.buffer[(this.buffer_pos++)] = ((char)b);
/*     */             
/* 668 */             String ref = new String(this.buffer, 1, this.buffer_pos - 2).toLowerCase(Locale.US);
/*     */             
/*     */             String replacement;
/*     */             String replacement;
/* 672 */             if ((ref.equals("amp")) || (ref.equals("lt")) || (ref.equals("gt")) || (ref.equals("quot")) || (ref.equals("apos")) || (ref.startsWith("#")))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 679 */               replacement = new String(this.buffer, 0, this.buffer_pos);
/*     */             }
/*     */             else
/*     */             {
/* 683 */               int num = Entities.HTML40.entityValue(ref);
/*     */               String replacement;
/* 685 */               if (num != -1)
/*     */               {
/* 687 */                 replacement = "&#" + num + ";";
/*     */               }
/*     */               else
/*     */               {
/* 691 */                 replacement = new String(this.buffer, 0, this.buffer_pos);
/*     */               }
/*     */             }
/*     */             
/* 695 */             char[] chars = replacement.toCharArray();
/*     */             
/* 697 */             System.arraycopy(chars, 0, this.insertion, 0, chars.length);
/*     */             
/* 699 */             this.buffer_pos = 0;
/* 700 */             this.insertion_pos = 0;
/* 701 */             this.insertion_len = chars.length;
/*     */             
/* 703 */             return this.insertion[(this.insertion_pos++)];
/*     */           }
/*     */           
/*     */ 
/* 707 */           this.buffer[(this.buffer_pos++)] = ((char)b);
/*     */           
/* 709 */           char c = (char)b;
/*     */           
/* 711 */           if (!Character.isLetterOrDigit(c))
/*     */           {
/*     */ 
/*     */ 
/* 715 */             if ((this.buffer_pos == 2) && (this.buffer[0] == '&'))
/*     */             {
/* 717 */               char[] chars = "&amp;".toCharArray();
/*     */               
/* 719 */               System.arraycopy(chars, 0, this.insertion, 0, chars.length);
/*     */               
/* 721 */               this.buffer_pos = 0;
/* 722 */               this.insertion_pos = 0;
/* 723 */               this.insertion_len = chars.length;
/*     */               
/*     */ 
/*     */ 
/* 727 */               this.insertion[(this.insertion_len++)] = ((char)b);
/*     */               
/* 729 */               return this.insertion[(this.insertion_pos++)];
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 735 */             System.arraycopy(this.buffer, 0, this.insertion, 0, this.buffer_pos);
/*     */             
/* 737 */             this.buffer_pos = 0;
/* 738 */             this.insertion_pos = 0;
/* 739 */             this.insertion_len = this.buffer_pos;
/*     */             
/* 741 */             return this.insertion[(this.insertion_pos++)];
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void close()
/*     */       throws IOException
/*     */     {
/* 755 */       this.is.close();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public long skip(long n)
/*     */       throws IOException
/*     */     {
/* 766 */       if (this.insertion_len > 0)
/*     */       {
/*     */ 
/*     */ 
/* 770 */         int rem = this.insertion_len - this.insertion_pos;
/*     */         
/* 772 */         System.arraycopy(this.insertion, this.insertion_pos, this.buffer, 0, rem);
/*     */         
/* 774 */         this.insertion_pos = 0;
/* 775 */         this.insertion_len = 0;
/*     */         
/* 777 */         this.buffer_pos = rem;
/*     */       }
/*     */       
/* 780 */       if (n <= this.buffer_pos)
/*     */       {
/*     */ 
/*     */ 
/* 784 */         int rem = this.buffer_pos - (int)n;
/*     */         
/* 786 */         System.arraycopy(this.buffer, (int)n, this.insertion, 0, rem);
/*     */         
/* 788 */         this.insertion_pos = 0;
/* 789 */         this.insertion_len = rem;
/*     */         
/* 791 */         return n;
/*     */       }
/*     */       
/* 794 */       int to_skip = this.buffer_pos;
/*     */       
/* 796 */       this.buffer_pos = 0;
/*     */       
/* 798 */       return this.is.skip(n - to_skip) + to_skip;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public int available()
/*     */       throws IOException
/*     */     {
/* 806 */       return this.buffer_pos + this.is.available();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/xml/simpleparser/SimpleXMLParserDocumentImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */