package haifa.university.clustering;

import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

public class KMeansClustering implements IClusterer<SimpleKMeans>{
	SimpleKMeans _kmeans;
	private int _numberOfClusters = 10;
	private boolean _clustringApplied = false;
	
	public int[] Apply(Instances data){
		return Apply(data,_numberOfClusters);
	}
	public int[] Apply(Instances data,int numberofClusters){
		_kmeans = new SimpleKMeans();
		
		_kmeans.setDebug(false);
		_kmeans.setSeed(10);
		_kmeans.setDontReplaceMissingValues(true);
		//important parameter to set: preserver order, number of cluster.
		_kmeans.setPreserveInstancesOrder(true);
		try {
			_kmeans.setNumClusters(numberofClusters);
			_kmeans.buildClusterer(data);
			_clustringApplied = true;
			
			// This array returns the cluster number (starting with 0) for each instance
			// The array has as many elements as the number of instances
			int[] assignments = _kmeans.getAssignments();
			
			return assignments;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}	
	@Override
	public int[] getClusterSizes() {
		if(_clustringApplied){
			final int[] intArray = new int[_kmeans.getClusterSizes().length];
			for (int i=0; i<intArray.length; ++i)
			    intArray[i] = (int) _kmeans.getClusterSizes()[i];
			return intArray;
		}
		return null;
	}
	@Override
	public Instances getClusterCenters() {
		if(_clustringApplied){
			return _kmeans.getClusterCentroids();
		}
		return null;
	}
	@Override
	public int[] getAssignments() {
		if(_clustringApplied){
			try {
				return _kmeans.getAssignments();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	@Override
	public int getNumberOfClusters() {
		if(_clustringApplied){
			return _kmeans.getNumClusters();
		}
		return 0;
	}
	@Override
	public SimpleKMeans getClusterer() {
		return _kmeans;
	}
	@Override
	public boolean clustererApplied() {
		return _clustringApplied;
	}
}
