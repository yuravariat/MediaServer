package haifa.university.clustering;

import weka.clusterers.Clusterer;
import weka.core.Instances;

public interface IClusterer<T extends Clusterer>{
	public int[] getClusterSizes();
	public Instances getClusterCenters();
	public int[] getAssignments();
	public int getNumberOfClusters();
	public T getClusterer();
	public boolean clustererApplied();
}
