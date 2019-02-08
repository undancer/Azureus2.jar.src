/*     */ package com.aelitis.azureus.core.neuronal;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NeuralNetwork
/*     */ {
/*     */   final NeuralNetworkLayer inputLayer;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   final NeuralNetworkLayer hiddenLayer;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   final NeuralNetworkLayer outputLayer;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public NeuralNetwork(int nbInputNodes, int nbHiddenNodes, int nbOutputNodes)
/*     */   {
/*  27 */     this.inputLayer = new NeuralNetworkLayer(nbInputNodes);
/*  28 */     this.hiddenLayer = new NeuralNetworkLayer(nbHiddenNodes);
/*  29 */     this.outputLayer = new NeuralNetworkLayer(nbOutputNodes);
/*     */     
/*  31 */     this.inputLayer.initialize(null, this.hiddenLayer);
/*  32 */     this.inputLayer.randomizeWeights();
/*     */     
/*  34 */     this.hiddenLayer.initialize(this.inputLayer, this.outputLayer);
/*  35 */     this.hiddenLayer.randomizeWeights();
/*     */     
/*  37 */     this.outputLayer.initialize(this.hiddenLayer, null);
/*     */   }
/*     */   
/*     */ 
/*     */   public void setActivationFunction(ActivationFunction activationFunction)
/*     */   {
/*  43 */     this.inputLayer.setActivationFunction(activationFunction);
/*  44 */     this.hiddenLayer.setActivationFunction(activationFunction);
/*  45 */     this.outputLayer.setActivationFunction(activationFunction);
/*     */   }
/*     */   
/*     */   public void setInput(int i, double value)
/*     */   {
/*  50 */     if ((i >= 0) && (i < this.inputLayer.getNumberOfNodes())) {
/*  51 */       this.inputLayer.neuronValues[i] = value;
/*     */     }
/*     */   }
/*     */   
/*     */   public double getOutput(int i) {
/*  56 */     if ((i >= 0) && (i < this.outputLayer.getNumberOfNodes())) {
/*  57 */       return this.outputLayer.neuronValues[i];
/*     */     }
/*     */     
/*  60 */     return NaN.0D;
/*     */   }
/*     */   
/*     */   public void setDesiredOutput(int i, double value) {
/*  64 */     if ((i >= 0) && (i < this.outputLayer.getNumberOfNodes())) {
/*  65 */       this.outputLayer.desiredValues[i] = value;
/*     */     }
/*     */   }
/*     */   
/*     */   public void setMomentum(boolean useMomentum, double factor) {
/*  70 */     this.inputLayer.setMomentum(useMomentum, factor);
/*  71 */     this.hiddenLayer.setMomentum(useMomentum, factor);
/*  72 */     this.outputLayer.setMomentum(useMomentum, factor);
/*     */   }
/*     */   
/*     */   public void setLearningRate(double rate) {
/*  76 */     this.inputLayer.setLearningRate(rate);
/*  77 */     this.hiddenLayer.setLearningRate(rate);
/*  78 */     this.outputLayer.setLearningRate(rate);
/*     */   }
/*     */   
/*     */   public void feedForward() {
/*  82 */     this.inputLayer.calculateNeuronValues();
/*  83 */     this.hiddenLayer.calculateNeuronValues();
/*  84 */     this.outputLayer.calculateNeuronValues();
/*     */   }
/*     */   
/*     */   public void backPropagate() {
/*  88 */     this.outputLayer.calculateErrors();
/*  89 */     this.hiddenLayer.calculateErrors();
/*     */     
/*  91 */     this.hiddenLayer.adjustWeights();
/*  92 */     this.inputLayer.adjustWeights();
/*     */   }
/*     */   
/*     */   public double calculateError() {
/*  96 */     double error = 0.0D;
/*     */     
/*  98 */     for (int i = 0; i < this.outputLayer.numberOfNodes; i++) {
/*  99 */       error += Math.pow(this.outputLayer.neuronValues[i] - this.outputLayer.desiredValues[i], 2.0D);
/*     */     }
/*     */     
/* 102 */     error /= this.outputLayer.numberOfNodes;
/*     */     
/* 104 */     return error;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 108 */     StringBuilder sb = new StringBuilder();
/*     */     
/* 110 */     sb.append("Input Layer :\n");
/* 111 */     sb.append(this.inputLayer.toString());
/* 112 */     sb.append("\n\n");
/*     */     
/* 114 */     sb.append("Hidden Layer :\n");
/* 115 */     sb.append(this.hiddenLayer.toString());
/* 116 */     sb.append("\n\n");
/*     */     
/* 118 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/neuronal/NeuralNetwork.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */