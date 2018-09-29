package haifa.university.clustering;

import java.awt.Container;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.JFrame;

import weka.clusterers.HierarchicalClusterer;
import weka.clusterers.SimpleKMeans;
import weka.core.Capabilities;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;
import weka.gui.hierarchyvisualizer.HierarchyVisualizer;

public class HierarchicalClustering implements IClusterer<HierarchicalClusterer>{
	private HierarchicalClusterer _clusterer;
	private int _numberOfClusters = 5;
	private boolean _clustringApplied = false;
	private int[] _assignments;
	
	public int[] Apply(Instances data){
		// Instantiate clusterer
		_clusterer = new HierarchicalClusterer();
		try {
			_clusterer.setOptions(new String[] {"-L", "SINGLE"});
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		_clusterer.setDebug(true);
		_clusterer.setNumClusters(_numberOfClusters);
		_clusterer.setDistanceFunction(new EuclideanDistance());
		_clusterer.setDistanceIsBranchLength(true);
		
		try {
			_clusterer.buildClusterer(data);
			_clustringApplied = true;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
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
		
	}
	private void Visualize(){
		if(_clusterer!=null){
			// Cluster network
			try {
				// Print normal
				_clusterer.setPrintNewick(false);
				System.out.println(_clusterer.graph());
				// Print Newick
				_clusterer.setPrintNewick(true);
				System.out.println(_clusterer.graph());
				
				// Let's try to show this clustered data!
				JFrame mainFrame = new JFrame("Weka Test");
				mainFrame.setSize(600, 400);
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				Container content = mainFrame.getContentPane();
				content.setLayout(new GridLayout(1, 1));
				
				HierarchyVisualizer visualizer = new HierarchyVisualizer(_clusterer.graph());
				content.add(visualizer);
				mainFrame.setVisible(true);
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
	public HierarchicalClusterer getClusterer() {
		return _clusterer;
	}
	@Override
	public boolean clustererApplied() {
		return _clustringApplied;
	}
	/*
	 * 
	 * -N
	  number of clusters
	 
	 -L
	  Link type (Single, Complete, Average, Mean, Centroid, Ward, Adjusted complete, Neighbor Joining)
	  [SINGLE|COMPLETE|AVERAGE|MEAN|CENTROID|WARD|ADJCOMLPETE|NEIGHBOR_JOINING]
	 
	 -A
	 Distance function to use. (default: weka.core.EuclideanDistance)
	 
	 -P
	 Print hierarchy in Newick format, which can be used for display in other programs.
	 
	 -D
	 If set, classifier is run in debug mode and may output additional info to the console.
	 
	 -B
	 \If set, distance is interpreted as branch length, otherwise it is node height.
	 * 
	 * */
}
