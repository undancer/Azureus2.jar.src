/*     */ package org.gudy.azureus2.pluginsimpl.local.utils.xml.simpleparser;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Vector;
/*     */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentAttribute;
/*     */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentNode;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.Node;
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
/*     */ public class SimpleXMLParserDocumentNodeImpl
/*     */   implements SimpleXMLParserDocumentNode
/*     */ {
/*     */   protected SimpleXMLParserDocumentImpl document;
/*     */   protected Node node;
/*     */   protected SimpleXMLParserDocumentNode[] kids;
/*     */   
/*     */   protected SimpleXMLParserDocumentNodeImpl(SimpleXMLParserDocumentImpl _doc, Node _node)
/*     */   {
/*  50 */     this.document = _doc;
/*  51 */     this.node = _node;
/*     */   }
/*     */   
/*     */ 
/*     */   protected Node getNode()
/*     */   {
/*  57 */     return this.node;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  63 */     return this.node.getLocalName();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getFullName()
/*     */   {
/*  69 */     return this.node.getNodeName();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getNameSpaceURI()
/*     */   {
/*  75 */     return this.node.getNamespaceURI();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getValue()
/*     */   {
/*  85 */     if (this.node.getNodeType() == 7)
/*     */     {
/*  87 */       return this.node.getNodeValue();
/*     */     }
/*     */     
/*  90 */     String res = "";
/*     */     
/*  92 */     for (Node child = this.node.getFirstChild(); child != null; child = child.getNextSibling())
/*     */     {
/*  94 */       int type = child.getNodeType();
/*     */       
/*  96 */       if ((type == 4) || (type == 3) || (type == 12))
/*     */       {
/*     */ 
/*     */ 
/* 100 */         String str = child.getNodeValue();
/*     */         
/* 102 */         res = res + str;
/*     */       }
/*     */     }
/*     */     
/* 106 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SimpleXMLParserDocumentAttribute getAttribute(String name)
/*     */   {
/* 113 */     SimpleXMLParserDocumentAttribute[] attributes = getAttributes();
/*     */     
/* 115 */     for (int i = 0; i < attributes.length; i++)
/*     */     {
/* 117 */       if (attributes[i].getName().equalsIgnoreCase(name))
/*     */       {
/* 119 */         return attributes[i];
/*     */       }
/*     */     }
/*     */     
/* 123 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public SimpleXMLParserDocumentAttribute[] getAttributes()
/*     */   {
/* 129 */     Vector v = new Vector();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 134 */     if (this.node.getNodeType() == 1)
/*     */     {
/* 136 */       NamedNodeMap atts = this.node.getAttributes();
/*     */       
/* 138 */       for (int i = 0; i < atts.getLength(); i++)
/*     */       {
/* 140 */         Node child = atts.item(i);
/*     */         
/* 142 */         v.addElement(new SimpleXMLParserDocumentAttributeImpl(child.getNodeName(), child.getNodeValue()));
/*     */       }
/*     */     }
/*     */     
/* 146 */     for (Node child = this.node.getFirstChild(); child != null; child = child.getNextSibling())
/*     */     {
/* 148 */       int type = child.getNodeType();
/*     */       
/* 150 */       if (type == 2)
/*     */       {
/* 152 */         v.addElement(new SimpleXMLParserDocumentAttributeImpl(child.getNodeName(), child.getNodeValue()));
/*     */       }
/*     */     }
/*     */     
/* 156 */     SimpleXMLParserDocumentAttributeImpl[] res = new SimpleXMLParserDocumentAttributeImpl[v.size()];
/*     */     
/* 158 */     v.copyInto(res);
/*     */     
/* 160 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SimpleXMLParserDocumentNode[] getChildren()
/*     */   {
/* 167 */     if (this.kids == null)
/*     */     {
/* 169 */       this.kids = this.document.parseNode(this.node, true);
/*     */     }
/*     */     
/* 172 */     return this.kids;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SimpleXMLParserDocumentNode getChild(String name)
/*     */   {
/* 179 */     SimpleXMLParserDocumentNode[] kids = getChildren();
/*     */     
/* 181 */     for (int i = 0; i < kids.length; i++)
/*     */     {
/* 183 */       if (kids[i].getName().equalsIgnoreCase(name))
/*     */       {
/* 185 */         return kids[i];
/*     */       }
/*     */     }
/*     */     
/* 189 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public void print()
/*     */   {
/* 195 */     PrintWriter pw = new PrintWriter(System.out);
/*     */     
/* 197 */     print(pw);
/*     */     
/* 199 */     pw.flush();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void print(PrintWriter pw)
/*     */   {
/* 206 */     print(pw, "");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void print(PrintWriter pw, String indent)
/*     */   {
/* 214 */     String attr_str = "";
/*     */     
/* 216 */     SimpleXMLParserDocumentAttribute[] attrs = getAttributes();
/*     */     
/* 218 */     for (int i = 0; i < attrs.length; i++) {
/* 219 */       attr_str = attr_str + (i == 0 ? "" : ",") + attrs[i].getName() + "=" + attrs[i].getValue();
/*     */     }
/*     */     
/* 222 */     pw.println(indent + getName() + ":" + attr_str + " -> " + getValue());
/*     */     
/* 224 */     SimpleXMLParserDocumentNode[] kids = getChildren();
/*     */     
/* 226 */     for (int i = 0; i < kids.length; i++)
/*     */     {
/* 228 */       ((SimpleXMLParserDocumentNodeImpl)kids[i]).print(pw, indent + "  ");
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/xml/simpleparser/SimpleXMLParserDocumentNodeImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */