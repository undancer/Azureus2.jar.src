package org.gudy.azureus2.plugins.utils.xml.simpleparser;

import java.io.PrintWriter;

public abstract interface SimpleXMLParserDocumentNode
{
  public abstract String getName();
  
  public abstract String getFullName();
  
  public abstract String getNameSpaceURI();
  
  public abstract String getValue();
  
  public abstract SimpleXMLParserDocumentAttribute[] getAttributes();
  
  public abstract SimpleXMLParserDocumentAttribute getAttribute(String paramString);
  
  public abstract SimpleXMLParserDocumentNode[] getChildren();
  
  public abstract SimpleXMLParserDocumentNode getChild(String paramString);
  
  public abstract void print();
  
  public abstract void print(PrintWriter paramPrintWriter);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/xml/simpleparser/SimpleXMLParserDocumentNode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */