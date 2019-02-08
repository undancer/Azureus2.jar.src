package com.aelitis.net.upnp;

import java.util.Comparator;
import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocument;
import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentException;

public abstract interface UPnPAdapter
  extends UPnPSSDPAdapter
{
  public abstract SimpleXMLParserDocument parseXML(String paramString)
    throws SimpleXMLParserDocumentException;
  
  public abstract ResourceDownloaderFactory getResourceDownloaderFactory();
  
  public abstract Comparator getAlphanumericComparator();
  
  public abstract String getTraceDir();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/UPnPAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */