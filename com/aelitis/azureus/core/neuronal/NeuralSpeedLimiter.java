/*     */ package com.aelitis.azureus.core.neuronal;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NeuralSpeedLimiter
/*     */ {
/*     */   long maxDlSpeed;
/*     */   
/*     */ 
/*     */ 
/*     */   long maxUlSpeed;
/*     */   
/*     */ 
/*     */ 
/*     */   long ulSpeed;
/*     */   
/*     */ 
/*     */   long dlSpeed;
/*     */   
/*     */ 
/*     */   long minLatency;
/*     */   
/*     */ 
/*     */   long maxLatency;
/*     */   
/*     */ 
/*     */   long latency;
/*     */   
/*     */ 
/*     */   final NeuralNetwork neuralNetwork;
/*     */   
/*     */ 
/*     */   private boolean dirty;
/*     */   
/*     */ 
/*  37 */   private double currentULTarget = 0.6D;
/*     */   
/*  39 */   final double[][] trainingSet = { { 0.0D, 0.0D, 0.0D, 0.9D, 0.9D, 0.9D, 0.9D }, { 0.0D, 1.0D, 0.0D, 0.9D, 0.9D, 0.9D, 0.9D }, { 1.0D, 0.0D, 0.0D, 0.9D, 0.9D, 0.9D, 0.9D }, { 1.0D, 1.0D, 0.0D, 0.9D, 0.9D, 0.9D, 0.9D }, { 0.0D, 1.0D, 1.0D, 0.9D, 0.9D, 0.1D, 0.1D }, { 0.0D, 1.0D, 0.3D, 0.9D, 0.9D, 0.1D, 0.1D }, { 0.0D, 1.0D, 0.1D, 0.9D, 0.9D, 0.1D, 0.8D }, { 0.0D, 1.0D, 0.2D, 0.9D, 0.9D, 0.1D, 0.6D }, { 1.0D, 0.0D, 1.0D, 0.1D, 0.5D, 0.9D, 0.9D }, { 1.0D, 0.0D, 0.3D, 0.1D, 0.6D, 0.9D, 0.9D }, { 1.0D, 0.0D, 0.1D, 0.1D, 0.9D, 0.9D, 0.9D }, { 1.0D, 0.0D, 0.2D, 0.1D, 0.8D, 0.9D, 0.9D } };
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
/*     */   public NeuralSpeedLimiter()
/*     */   {
/*  66 */     this.neuralNetwork = new NeuralNetwork(3, 4, 4);
/*  67 */     this.neuralNetwork.setLearningRate(0.05D);
/*  68 */     this.neuralNetwork.setMomentum(true, 0.9D);
/*  69 */     this.neuralNetwork.setActivationFunction(new LogisticActivationFunction());
/*  70 */     train();
/*  71 */     this.dirty = false;
/*     */   }
/*     */   
/*     */   private void train()
/*     */   {
/*  76 */     double error = 1.0D;
/*  77 */     int c = 0;
/*  78 */     while ((error > 0.002D) && (c < 200000))
/*     */     {
/*  80 */       error = 0.0D;
/*  81 */       for (int i = 0; i < this.trainingSet.length; i++) {
/*  82 */         this.neuralNetwork.setInput(0, this.trainingSet[i][0]);
/*  83 */         this.neuralNetwork.setInput(1, this.trainingSet[i][1]);
/*  84 */         this.neuralNetwork.setInput(2, this.trainingSet[i][2]);
/*     */         
/*  86 */         this.neuralNetwork.setDesiredOutput(0, this.trainingSet[i][3]);
/*  87 */         this.neuralNetwork.setDesiredOutput(1, this.trainingSet[i][4]);
/*  88 */         this.neuralNetwork.setDesiredOutput(2, this.trainingSet[i][5]);
/*  89 */         this.neuralNetwork.setDesiredOutput(3, this.trainingSet[i][6]);
/*     */         
/*  91 */         this.neuralNetwork.feedForward();
/*  92 */         error += this.neuralNetwork.calculateError();
/*  93 */         this.neuralNetwork.backPropagate();
/*     */       }
/*     */       
/*     */ 
/*  97 */       error /= this.trainingSet.length;
/*     */       
/*  99 */       c++;
/*     */     }
/*     */   }
/*     */   
/*     */   private void resetInput() {
/*     */     try {
/* 105 */       if (this.ulSpeed > this.maxUlSpeed) this.maxUlSpeed = this.ulSpeed;
/* 106 */       if (this.dlSpeed > this.maxDlSpeed) this.maxDlSpeed = this.dlSpeed;
/* 107 */       if (this.latency > this.maxLatency) this.maxLatency = this.latency;
/* 108 */       if (this.latency < this.minLatency) { this.minLatency = this.latency;
/*     */       }
/* 110 */       double downloadFactor = this.dlSpeed / this.maxDlSpeed;
/* 111 */       double uploadFactor = this.ulSpeed / this.maxUlSpeed;
/* 112 */       double latencyFactor = (this.latency - this.minLatency) / this.maxLatency;
/*     */       
/* 114 */       this.neuralNetwork.setInput(0, downloadFactor);
/* 115 */       this.neuralNetwork.setInput(1, uploadFactor);
/* 116 */       this.neuralNetwork.setInput(2, latencyFactor);
/*     */       
/* 118 */       this.dirty = true;
/*     */     }
/*     */     catch (Throwable t) {}
/*     */   }
/*     */   
/*     */   private void retrain(double ulTarget)
/*     */   {
/* 125 */     resetInput();
/* 126 */     this.neuralNetwork.feedForward();
/* 127 */     double shouldLimitDownload = this.neuralNetwork.getOutput(0);
/* 128 */     double downloadLimit = this.neuralNetwork.getOutput(1);
/*     */     
/* 130 */     double downloadFactor = this.dlSpeed / this.maxDlSpeed;
/* 131 */     double uploadFactor = this.ulSpeed / this.maxUlSpeed;
/* 132 */     double latencyFactor = (this.latency - this.minLatency) / this.maxLatency;
/*     */     
/*     */ 
/* 135 */     double error = 1.0D;
/* 136 */     int c = 0;
/*     */     
/* 138 */     while ((error > 0.002D) && (c < 400)) {
/* 139 */       this.neuralNetwork.setInput(0, downloadFactor);
/* 140 */       this.neuralNetwork.setInput(1, uploadFactor);
/* 141 */       this.neuralNetwork.setInput(2, latencyFactor);
/*     */       
/* 143 */       this.neuralNetwork.setDesiredOutput(0, shouldLimitDownload);
/* 144 */       this.neuralNetwork.setDesiredOutput(1, downloadLimit);
/* 145 */       this.neuralNetwork.setDesiredOutput(2, 0.9D);
/* 146 */       this.neuralNetwork.setDesiredOutput(3, ulTarget);
/*     */       
/* 148 */       this.neuralNetwork.feedForward();
/* 149 */       error = this.neuralNetwork.calculateError();
/* 150 */       this.neuralNetwork.backPropagate();
/*     */       
/* 152 */       c++;
/*     */     }
/*     */   }
/*     */   
/*     */   private void feedForward() {
/* 157 */     this.neuralNetwork.feedForward();
/* 158 */     this.dirty = false;
/*     */     
/* 160 */     double latencyFactor = (this.latency - this.minLatency) / this.maxLatency;
/*     */     
/* 162 */     if (latencyFactor >= 0.15D)
/*     */     {
/* 164 */       this.currentULTarget *= 0.97D;
/* 165 */       retrain(this.currentULTarget);
/* 166 */     } else if (latencyFactor < 0.05D)
/*     */     {
/* 168 */       this.currentULTarget *= 1.02D;
/* 169 */       retrain(this.currentULTarget);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setMaxDlSpeed(long maxDlSpeed)
/*     */   {
/* 181 */     if (maxDlSpeed > 0L) {
/* 182 */       this.maxDlSpeed = maxDlSpeed;
/*     */     }
/* 184 */     resetInput();
/*     */   }
/*     */   
/*     */   public void setMaxUlSpeed(long maxUlSpeed) {
/* 188 */     if (maxUlSpeed > 0L) {
/* 189 */       this.maxUlSpeed = maxUlSpeed;
/*     */     }
/* 191 */     resetInput();
/*     */   }
/*     */   
/*     */   public void setMinLatency(long minLatency) {
/* 195 */     if (minLatency >= 0L) {
/* 196 */       this.minLatency = minLatency;
/*     */     }
/* 198 */     resetInput();
/*     */   }
/*     */   
/*     */   public void setUlSpeed(long ulSpeed) {
/* 202 */     if (ulSpeed >= 0L) {
/* 203 */       this.ulSpeed = ulSpeed;
/*     */     }
/* 205 */     resetInput();
/*     */   }
/*     */   
/*     */   public void setDlSpeed(long dlSpeed) {
/* 209 */     if (dlSpeed >= 0L) {
/* 210 */       this.dlSpeed = dlSpeed;
/*     */     }
/* 212 */     resetInput();
/*     */   }
/*     */   
/*     */   public void setLatency(long latency) {
/* 216 */     if (latency >= 0L) {
/* 217 */       this.latency = latency;
/*     */     }
/* 219 */     resetInput();
/*     */   }
/*     */   
/*     */   public void setMaxLatency(long maxLatency) {
/* 223 */     if (maxLatency > 0L) {
/* 224 */       this.maxLatency = maxLatency;
/*     */     }
/* 226 */     resetInput();
/*     */   }
/*     */   
/*     */   public boolean shouldLimitDownload() {
/* 230 */     if (this.dirty) feedForward();
/* 231 */     return this.neuralNetwork.getOutput(0) < 0.5D;
/*     */   }
/*     */   
/*     */   public long getDownloadLimit() {
/* 235 */     if (this.dirty) feedForward();
/* 236 */     return (1.2D * this.maxDlSpeed * this.neuralNetwork.getOutput(1));
/*     */   }
/*     */   
/*     */   public boolean shouldLimitUpload()
/*     */   {
/* 241 */     if (this.dirty) feedForward();
/* 242 */     return this.neuralNetwork.getOutput(2) < 0.5D;
/*     */   }
/*     */   
/*     */   public long getUploadLimit() {
/* 246 */     if (this.dirty) feedForward();
/* 247 */     return (1.2D * this.maxUlSpeed * this.neuralNetwork.getOutput(3));
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 251 */     new NeuralSpeedLimiter();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/neuronal/NeuralSpeedLimiter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */