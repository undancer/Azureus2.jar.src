/*     */ package com.aelitis.azureus.core.neuronal;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NeuralNetworkLayer
/*     */ {
/*     */   final int numberOfNodes;
/*     */   
/*     */ 
/*     */   double[][] weights;
/*     */   
/*     */ 
/*     */   double[][] weightChanges;
/*     */   
/*     */ 
/*     */   double[] neuronValues;
/*     */   
/*     */ 
/*     */   double[] desiredValues;
/*     */   
/*     */ 
/*     */   double[] errors;
/*     */   
/*     */   double[] biasWeights;
/*     */   
/*     */   double[] biasValues;
/*     */   
/*     */   double learningRate;
/*     */   
/*     */   final boolean linearOutput;
/*     */   
/*     */   boolean useMomentum;
/*     */   
/*     */   double momentumFactor;
/*     */   
/*     */   NeuralNetworkLayer parentLayer;
/*     */   
/*     */   NeuralNetworkLayer childLayer;
/*     */   
/*     */   ActivationFunction activationFunction;
/*     */   
/*     */ 
/*     */   public NeuralNetworkLayer(int numberOfNodes)
/*     */   {
/*  45 */     this.numberOfNodes = numberOfNodes;
/*     */     
/*  47 */     this.linearOutput = false;
/*  48 */     this.useMomentum = false;
/*  49 */     this.momentumFactor = 0.9D;
/*     */   }
/*     */   
/*     */   public void initialize(NeuralNetworkLayer parentLayer, NeuralNetworkLayer childLayer)
/*     */   {
/*  54 */     this.neuronValues = new double[this.numberOfNodes];
/*  55 */     this.desiredValues = new double[this.numberOfNodes];
/*  56 */     this.errors = new double[this.numberOfNodes];
/*  57 */     this.parentLayer = parentLayer;
/*     */     
/*  59 */     if (childLayer != null) {
/*  60 */       this.childLayer = childLayer;
/*  61 */       this.weights = new double[this.numberOfNodes][childLayer.getNumberOfNodes()];
/*  62 */       this.weightChanges = new double[this.numberOfNodes][childLayer.getNumberOfNodes()];
/*  63 */       this.biasValues = new double[childLayer.getNumberOfNodes()];
/*  64 */       this.biasWeights = new double[childLayer.getNumberOfNodes()];
/*     */       
/*  66 */       for (int j = 0; j < childLayer.getNumberOfNodes(); j++) {
/*  67 */         this.biasValues[j] = -1.0D;
/*  68 */         this.biasWeights[j] = 0.0D;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void randomizeWeights() {
/*  74 */     for (int i = 0; i < this.numberOfNodes; i++) {
/*  75 */       for (int j = 0; j < this.childLayer.getNumberOfNodes(); j++) {
/*  76 */         this.weights[i][j] = (Math.random() * 2.0D - 1.0D);
/*     */       }
/*     */     }
/*     */     
/*  80 */     for (int j = 0; j < this.childLayer.getNumberOfNodes(); j++) {
/*  81 */       this.biasWeights[j] = (Math.random() * 2.0D - 1.0D);
/*     */     }
/*     */   }
/*     */   
/*     */   public void calculateNeuronValues() {
/*  86 */     if (this.parentLayer != null) {
/*  87 */       for (int j = 0; j < this.numberOfNodes; j++) {
/*  88 */         double x = 0.0D;
/*     */         
/*  90 */         for (int i = 0; i < this.parentLayer.getNumberOfNodes(); i++) {
/*  91 */           x += this.parentLayer.neuronValues[i] * this.parentLayer.weights[i][j];
/*     */         }
/*     */         
/*  94 */         x += this.parentLayer.biasValues[j] * this.parentLayer.biasWeights[j];
/*     */         
/*  96 */         if ((this.childLayer == null) && (this.linearOutput)) {
/*  97 */           this.neuronValues[j] = x;
/*     */         } else {
/*  99 */           this.neuronValues[j] = this.activationFunction.getValueFor(x);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void calculateErrors()
/*     */   {
/* 107 */     if (this.childLayer == null)
/*     */     {
/* 109 */       for (int i = 0; i < this.numberOfNodes; i++) {
/* 110 */         this.errors[i] = ((this.desiredValues[i] - this.neuronValues[i]) * this.activationFunction.getDerivedFunctionValueFor(this.neuronValues[i]));
/*     */       }
/* 112 */     } else if (this.parentLayer == null)
/*     */     {
/* 114 */       for (int i = 0; i < this.numberOfNodes; i++) {
/* 115 */         this.errors[i] = 0.0D;
/*     */       }
/*     */       
/*     */     } else {
/* 119 */       for (int i = 0; i < this.numberOfNodes; i++) {
/* 120 */         double sum = 0.0D;
/* 121 */         for (int j = 0; j < this.childLayer.getNumberOfNodes(); j++) {
/* 122 */           sum += this.childLayer.errors[j] * this.weights[i][j];
/*     */         }
/* 124 */         this.errors[i] = (sum * this.activationFunction.getDerivedFunctionValueFor(this.neuronValues[i]));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void adjustWeights()
/*     */   {
/* 131 */     if (this.childLayer != null) {
/* 132 */       for (int i = 0; i < this.numberOfNodes; i++) {
/* 133 */         for (int j = 0; j < this.childLayer.getNumberOfNodes(); j++) {
/* 134 */           double dw = this.learningRate * this.childLayer.errors[j] * this.neuronValues[i];
/* 135 */           if (this.useMomentum) {
/* 136 */             this.weights[i][j] += dw + this.momentumFactor * this.weightChanges[i][j];
/* 137 */             this.weightChanges[i][j] = dw;
/*     */           } else {
/* 139 */             this.weights[i][j] += dw;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 144 */       for (int j = 0; j < this.childLayer.getNumberOfNodes(); j++) {
/* 145 */         this.biasWeights[j] += this.learningRate * this.childLayer.errors[j] * this.biasValues[j];
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public int getNumberOfNodes()
/*     */   {
/* 152 */     return this.numberOfNodes;
/*     */   }
/*     */   
/*     */   public void setActivationFunction(ActivationFunction activationFunction) {
/* 156 */     this.activationFunction = activationFunction;
/*     */   }
/*     */   
/*     */   public void setMomentum(boolean useMomentum, double factor) {
/* 160 */     this.useMomentum = useMomentum;
/* 161 */     this.momentumFactor = factor;
/*     */   }
/*     */   
/*     */   public void setLearningRate(double rate) {
/* 165 */     this.learningRate = rate;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 169 */     StringBuilder sb = new StringBuilder();
/* 170 */     if (this.childLayer != null) {
/* 171 */       for (int j = 0; j < this.childLayer.getNumberOfNodes(); j++) {
/* 172 */         sb.append(j);
/* 173 */         sb.append("\t> ");
/* 174 */         for (int i = 0; i < this.numberOfNodes; i++) {
/* 175 */           sb.append(i);
/* 176 */           sb.append(":");
/* 177 */           sb.append(this.weights[i][j]);
/* 178 */           sb.append("\t");
/*     */         }
/* 180 */         sb.append("\n");
/*     */       }
/*     */     }
/*     */     
/* 184 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/neuronal/NeuralNetworkLayer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */