package com.aelitis.azureus.core.tag;

import java.util.List;
import org.gudy.azureus2.core3.peer.PEPeer;

public abstract interface TagPeer
  extends Tag, TagFeatureRateLimit
{
  public static final int FEATURES = 65;
  
  public abstract List<PEPeer> getTaggedPeers();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/TagPeer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */