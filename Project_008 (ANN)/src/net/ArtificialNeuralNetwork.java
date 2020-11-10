package net;
import java.util.Random;

public class ArtificialNeuralNetwork {
	
	//TODO:
	// 1: Save and Load all data
	// 2: Convert Weights to 1d array rather than 2d
	//    [w + (w2 * width) + (d * width * width)] gives weight from the node [w2, d-1] to [w, d]	
	// 3: Momentum (as in Demo.ANN)
	
	Random r = new Random();
	
	private int width;
	private int depth;
	
	private int input_width;
	private int output_width;
	
	private float mod;
	private float learning_rate = 0.01f;
	
	private float[][] weights; //[w + (d * width)][w2] gives weight from the node [w2, d-1] to [w, d]	
	private float[] bias; //[w + d * width] gives bias for the node [w, d]
	
	public ArtificialNeuralNetwork(int input_width, int output_width, int width, int depth) {
		this.width = width;
		this.depth = depth;
		this.input_width = input_width;
		this.output_width = output_width;
		
		mod = (float) (1.0 / Math.sqrt(width));
		weights = new float[(width * depth) + output_width][];
		bias = new float[(width * depth) + output_width];
				
		for(int d = 0; d < depth + 1; d++) {
			for(int w = 0; w < ((d == depth)? output_width : width); w++) {
				int wi = (d == 0)? input_width : width;
				int node = w + d * width;	
				weights[node] = new float[wi];	
				for(int w2 = 0; w2 < wi; w2++) {
					weights[node][w2] = r.nextFloat() * (r.nextBoolean()? mod : -mod);
				}		
			}
		}
	}
	public float[] forwardPass (float[] input) {	
		float[] output = new float[output_width];
		float[] nodes = new float[(width * depth) + output_width];	
		for(int d = 0; d < depth + 1; d++) {
			for(int w = 0; w < ((d == depth)? output_width : width); w++) {
				int wi = (d == 0)? input_width : width;
				int node = w + d * width;		
				for(int w2 = 0; w2 < wi; w2++) {
					int target_node = w2 + (d-1) * width;
					nodes[node] += ((d == 0) ? input[w2] : nodes[target_node]) * weights[node][w2];
				}
				nodes[node] = ActivationFunction(nodes[node] + bias[node], false);
				if(d == depth) output[w] = nodes[node];
			}
		}	
		return output;
	}
	public void train(float[] input, float[] ExpectedOutput) {
		float[] output = new float[output_width];
		float[] nodes = new float[(width * depth) + output_width];
		for(int d = 0; d < depth + 1; d++) {
			for(int w = 0; w < ((d == depth)? output_width : width); w++) {
				int wi = (d == 0)? input_width : width;
				int node = w + d * width;		
				for(int w2 = 0; w2 < wi; w2++) {
					int target_node = w2 + (d-1) * width;
					nodes[node] += ((d == 0) ? input[w2] : nodes[target_node]) * weights[node][w2];
				}
				nodes[node] = ActivationFunction(nodes[node] + bias[node], false);
				if(d == depth) output[w] = nodes[node];
			}
		}
		float[] OutputSignalError = new float[output_width];
		float[] SignalError = new float[width * depth];		
		for (int out = 0; out < output_width; out++) {
			OutputSignalError[out] = (ExpectedOutput[out] - output[out]) * ActivationFunction(output[out], true);
		}
		for (int d = depth - 1; d >= 0; d--) {
			for (int w = 0; w < width; w++) {
				int node = w + d * width;
				float Sum = 0;
				for (int w2 = 0; w2 < ((d == depth - 1)? output_width : width); w2++) {
					int target_node = w2 + (d + 1) * width;
					Sum += weights[target_node][w] * ((d == depth-1)? OutputSignalError[w2] : SignalError[target_node]);
				}
				SignalError[node] = ActivationFunction(nodes[node], true) * Sum;
			}
		}
		for (int d = depth; d >= 0; d--) {
			for (int w = 0; w < ((d == depth)? output_width : width); w++) {
				int node = w + d * width;
				bias[node] += learning_rate * ((d == depth)? OutputSignalError[w] : SignalError[node]);
				if(d == 0) {
					for (int w2 = 0; w2 < input_width; w2++) {
						weights[node][w2] += learning_rate * SignalError[node] * input[w2];
					}
				}else {
					for (int w2 = 0; w2 < width; w2++) {
						int target_node = w2 + (d - 1) * width;
						weights[node][w2] += learning_rate * ((d == depth)? OutputSignalError[w] : SignalError[node]) * nodes[target_node];
					}
				}
			}
		}
	}
	private float ActivationFunction(float x, boolean derivative) {
		return ReLU(x, derivative);
	}
	private float ReLU(float x, boolean derivative) {
		if(derivative)
			return (x > 0)? 1 : 0;
		else
			return (x > 0)? x : 0;
	}
	public void save() {
		
	}
}
