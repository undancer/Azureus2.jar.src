package com.aelitis.azureus.core.tag;

import java.util.Set;
import org.gudy.azureus2.core3.download.DownloadManager;

public abstract interface TagDownload
  extends Tag, TagFeatureRateLimit, TagFeatureRSSFeed, TagFeatureRunState, TagFeatureTranscode, TagFeatureFileLocation, TagFeatureProperties, TagFeatureExecOnAssign, TagFeatureLimits, TagFeatureNotifications
{
  public static final int FEATURES = 511;
  
  public abstract Set<DownloadManager> getTaggedDownloads();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/TagDownload.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */