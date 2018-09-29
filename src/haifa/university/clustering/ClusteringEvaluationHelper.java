package haifa.university.clustering;

import haifa.university.clustering.distanceFunctions.DistanceFunctionForSSF;
import weka.clusterers.ClusterEvaluation;
import weka.core.Instances;
import weka.core.Utils;

public class ClusteringEvaluationHelper {
	public static double SilhouetteScore(IClusterer cls, Instances data, DistanceFunctionForSSF df) {
		double[] silh = new double[data.numInstances()];
		try {

		    double a, b;

		    //hold the distance between the instance and the centroids
		    double[] tmp = new double[cls.getNumberOfClusters()];
		    //cluster that each instance is assigned
		    int[] instanceAssignment = cls.getAssignments();

		    int[] clustersSize = cls.getClusterSizes();
		    Instances centroids = cls.getClusterCenters();
		    for (int i = 0; i < data.numInstances(); ++i) {
			try {
			    //singleton case
			    if (clustersSize[instanceAssignment[i]] == 1) {
				silh[i] = 0;
				continue;
			    }
			} catch (Exception e) {
			    e.printStackTrace();

			}

			for (int k = 0; k < cls.getNumberOfClusters(); ++k) {
			    if (k == instanceAssignment[i]) {
				tmp[k] = Double.POSITIVE_INFINITY;
				continue;
			    }
			    tmp[k] = df.distance(data.instance(i), centroids.instance(k));
			}
			//intern distance
			a = df.distance(data.instance(i), centroids.instance(instanceAssignment[i]));

			//nearest neighbor distance (extern distance)
			b = tmp[Utils.minIndex(tmp)];
			double max = Math.max(a, b);
			silh[i] = max > 0 ? (b - a) / (Math.max(a, b)) : 1;
		    
		    }
		    
		} catch (Exception e) {
		    e.printStackTrace();
		    System.out.println("Problem calculating simplified silhouette."
			    + "\n" + e.getMessage());
		}

		return Utils.mean(silh);
	}
	public static void Evaluate(IClusterer<?> cls, Instances data){
		if(cls.clustererApplied()){
			ClusterEvaluation eval = new ClusterEvaluation();
			eval.setClusterer(cls.getClusterer()); // the cluster to evaluate
			try {
				eval.evaluateClusterer(data);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}                                // data to evaluate the clusterer on
			//System.out.println("# of clusters: " + eval.getNumClusters());  // output # of clusters
			System.out.println(eval.clusterResultsToString());  // output # of clusters
		}
	}
}
