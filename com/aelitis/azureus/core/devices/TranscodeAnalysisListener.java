package com.aelitis.azureus.core.devices;

public abstract interface TranscodeAnalysisListener
{
  public abstract void analysisComplete(TranscodeJob paramTranscodeJob, TranscodeProviderAnalysis paramTranscodeProviderAnalysis);
  
  public abstract void analysisFailed(TranscodeJob paramTranscodeJob, TranscodeException paramTranscodeException);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/TranscodeAnalysisListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */