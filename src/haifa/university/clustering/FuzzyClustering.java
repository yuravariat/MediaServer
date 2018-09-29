package haifa.university.clustering;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import weka.clusterers.EM;
import weka.clusterers.HierarchicalClusterer;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;

public class FuzzyClustering implements IClusterer<EM>{
	EM _clusterer;
	private int numberOfClusters = 5;
	private boolean _clustringApplied = false;
	private int[] _assignments;
	
	public int[] Apply(Instances data){
		_clusterer = new EM();
		_clusterer.setDebug(true);
		_clusterer.setDisplayModelInOldFormat(false);
		_clusterer.setMinStdDev(0.000001);
		_clusterer.setSeed(10);
		try {
			_clusterer.setMaxIterations(100);
			_clusterer.setNumClusters(numberOfClusters);
			_clusterer.buildClusterer(data);
			
			_assignments = new int[data.size()];
			
			Instance temp;
			for(int i = 0; i<data.size(); i++){
				try{
					temp = data.instance(i);
					_assignments[i] = _clusterer.clusterInstance(temp);
					// println("Debug:"+temp.toString());
				}catch(Exception e){
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			return _assignments;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public int[] getClusterSizes() {
		if(_clustringApplied){
			try {
				List<Integer> list = Arrays.stream(_assignments).boxed().collect(Collectors.toList());
				Map<Integer, Long> counted = list.stream()
			            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
				int[] sizes = new int[counted.size()];
				for (Entry<Integer, Long> entry : counted.entrySet()) {
					sizes[entry.getKey()]=(int)(long)entry.getValue();
				}
				return sizes;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	@Override
	public Instances getClusterCenters() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int[] getAssignments() {
		if(_clustringApplied){
			return _assignments;
		}
		return null;
	}
	@Override
	public int getNumberOfClusters() {
		if(_clustringApplied){
			try {
				return _clusterer.numberOfClusters();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
	@Override
	public EM getClusterer() {
		return _clusterer;
	}
	@Override
	public boolean clustererApplied() {
		return _clustringApplied;
	}
}
