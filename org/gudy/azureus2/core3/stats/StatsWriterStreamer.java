package org.gudy.azureus2.core3.stats;

import java.io.IOException;
import java.io.OutputStream;

public abstract interface StatsWriterStreamer
{
  public abstract void write(OutputStream paramOutputStream)
    throws IOException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/stats/StatsWriterStreamer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */