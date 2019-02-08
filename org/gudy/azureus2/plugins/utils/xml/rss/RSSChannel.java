package org.gudy.azureus2.plugins.utils.xml.rss;

import java.net.URL;
import java.util.Date;
import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentNode;

public abstract interface RSSChannel
{
  public abstract String getTitle();
  
  public abstract String getDescription();
  
  public abstract URL getLink();
  
  public abstract Date getPublicationDate();
  
  public abstract RSSItem[] getItems();
  
  public abstract SimpleXMLParserDocumentNode getNode();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/xml/rss/RSSChannel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */