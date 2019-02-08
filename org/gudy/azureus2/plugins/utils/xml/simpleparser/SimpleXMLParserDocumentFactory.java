package org.gudy.azureus2.plugins.utils.xml.simpleparser;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public abstract interface SimpleXMLParserDocumentFactory
{
  public abstract SimpleXMLParserDocument create(File paramFile)
    throws SimpleXMLParserDocumentException;
  
  /**
   * @deprecated
   */
  public abstract SimpleXMLParserDocument create(InputStream paramInputStream)
    throws SimpleXMLParserDocumentException;
  
  public abstract SimpleXMLParserDocument create(URL paramURL, InputStream paramInputStream)
    throws SimpleXMLParserDocumentException;
  
  public abstract SimpleXMLParserDocument create(String paramString)
    throws SimpleXMLParserDocumentException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/xml/simpleparser/SimpleXMLParserDocumentFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */