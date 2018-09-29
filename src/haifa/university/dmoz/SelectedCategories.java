package haifa.university.dmoz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

public class SelectedCategories {
	private static SelectedCategories _instance;
	private ArrayList<Category> _categoriesList;
	private TreeNode _categoriesTree;
	private SelectedCategories(){
		super();
	}
	public static SelectedCategories getInstance() { 
		if (_instance == null) {
			synchronized (SelectedCategories.class) { 
				if (_instance == null) {
					_instance = new SelectedCategories();
					_instance._categoriesList = _instance._readCategoriesFromCsv();
					if(_instance._categoriesList!=null){
						_instance._categoriesTree = _instance._createCategoriesTree(_instance._categoriesList);
					}
				} 
			} 
		} 
		return _instance; 
	}
	
	private ArrayList<Category> _readCategoriesFromCsv(){
		
		ArrayList<Category> categories = new ArrayList<Category>(2500);
		int count = 0;
		try {
			try(BufferedReader br = new BufferedReader(new FileReader(new File("data/interests_vector.csv")))){
				String line;
				while ((line = br.readLine()) != null) {
				    String[] entries = line.split(",\"");
				    //catid,name,title,hierarchy_level,description
				    try{
					    Category cat = new Category();
					    cat.setIndex(count++);
					    cat.setCatid(Integer.parseInt(entries[0].replace("\"", "")));
					    cat.setHierarchy_level(Integer.parseInt(entries[4].replace("\"", "")));
					    cat.setPath(entries[1].replace("\"", ""));
					    cat.setName(cat.getPath().split("/")[cat.getHierarchy_level()]);
					    cat.setTitle(entries[2].replace("\"", ""));
					    cat.setDescription(entries[6].replace("\"", ""));
					    categories.add(cat);
				    }
				    catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return categories;
	}
	public int getCategoiresCount(){
		return _categoriesList!=null ? _categoriesList.size() : 0 ;
	}
	public Category getCategory(int id){
		return (_categoriesList!=null && id>=0 && id<_categoriesList.size()) ? _categoriesList.get(id) : null;
	}
	public int findCategoryIndex(String categoryPath){
		return _categoriesTree.findPath(categoryPath);
	}
	private TreeNode _createCategoriesTree(ArrayList<Category> cats){
		TreeNode rootNode = new TreeNode("top","top",-1,0);
		for (Category category : cats) {
			rootNode.addNode(category);
		}
		return rootNode;
	}
	public class TreeNode{

	    String name;
	    String path;
	    int index;
	    int level;
	    List<TreeNode> children;

	    public TreeNode(String name,String path,int index,int level) {
	    	this.name = name;
	    	this.path = path;
	    	this.index = index;
	    	this.level = level;
	        this.children = new LinkedList<TreeNode>();
	    }
	    public void addNode(Category newNode){
	    	LinkedList<String> filledPath = new LinkedList<String>();
			filledPath.add(this.name);
			_addNode(this, newNode, newNode.getPath().toLowerCase().split("/"), filledPath);
		}
	    private void _addNode(TreeNode parent,Category newNode, String[] path,LinkedList<String> filledPath){
			int level = filledPath.size();
			String name = path[level];
			filledPath.add(name);
			Optional<TreeNode> nextN = parent.children.stream().filter(n-> n.name.equals(name)).findFirst();
			TreeNode newChild = null;
			if(nextN.isPresent()){
				newChild = nextN.get();
			}
			else{
				newChild = new TreeNode(name, String.join("/", filledPath), -1, level);
				parent.children.add(newChild);
			}
			if(level == path.length-1){
				newChild.index = newNode.getIndex();
				return;
			}
			_addNode(newChild, newNode, path, filledPath);
		}
	    public int findPath(String path){
	    	if(path!=null){
		    	Queue<String> queuePath = new LinkedList<>(Arrays.asList(path.toLowerCase().split("/")));
		    	if(!queuePath.isEmpty() && queuePath.poll().equals("top")){ // first level is always 'top'
		    		return _findPath(this, queuePath);
		    	}
	    	}
	    	return -1;
	    }
	    private int _findPath(TreeNode node, Queue<String> queuePath){
	    	String name = queuePath.poll();
	    	if(node.children==null || node.children.size()==0){
	    		return node.index;
	    	}
	    	Optional<TreeNode> nextN = node.children.stream().filter(n-> n.name.equals(name)).findFirst();
	    	if(nextN.isPresent()){
	    		if(queuePath.isEmpty()){
	    			return nextN.get().index;
	    		}
	    		return _findPath(nextN.get(), queuePath);
	    	}
	    	return -1;
	    }
	    public int size(){
	    	return size(this);
	    }
	    private int size(TreeNode node){
	    	if(node!=null){
	    		if(node.children==null || node.children.size()==0){
	    			return 1;
	    		}
	    		else{
	    			int count = 0;
	    			for (TreeNode childNode : node.children) {
						count+=size(childNode);
					}
	    			return count + 1;
	    		}
	    	}
	    	return 0;
	    }
	    public int maxDepth(){
	    	return maxDepth(this,0);
	    }
	    private int maxDepth(TreeNode node,int level){
	    	if(node!=null){
	    		++level;
	    		if(node.children==null || node.children.size()==0){
	    			return level;
	    		}
	    		else{
	    			int depth = level;
	    			for (TreeNode childNode : node.children) {
						int _level = maxDepth(childNode,level);
						if(_level>depth){
							depth = _level;
						}
					}
	    			return depth;
	    		}
	    	}
	    	return 0;
	    }
	}
}









//CREATE TABLE `browse_history` (
//`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
//`device_id` CHAR(50) NOT NULL,
//`url` VARCHAR(1000) NULL DEFAULT NULL,
//`visits` SMALLINT(5) UNSIGNED NULL DEFAULT NULL,
//`sourceApp` VARCHAR(50) NULL DEFAULT NULL,
//`lastVisited` DATETIME NULL DEFAULT NULL,
//`title` VARCHAR(300) NULL DEFAULT NULL,
//`created` DATETIME NULL DEFAULT NULL,
//PRIMARY KEY (`id`),
//INDEX `device_id` (`device_id`),
//INDEX `url` (`url`)
//)
//COMMENT='Web browsing history from mobile devices'
//COLLATE='utf8_general_ci'
//ENGINE=InnoDB
//;

//select * from users_data.browse_history
//INTO OUTFILE 'C:/ProgramData/MySQL/MySQL Server 5.7/Uploads/browse_history.csv'
//FIELDS TERMINATED BY ','
//ENCLOSED BY '"'
//LINES TERMINATED BY '\n';

//Extract categories vector
//SELECT catid,name,title,hierarchy_level,description
//FROM structure
//where (hierarchy_level=2
//  and name not like 'Top/Regional/%'
//  and name not like 'Top/Sports/%'
//  and name not like 'Top/Science/%'
//	and name not like 'Top/World/%')
//or (name like 'Top/Regional/Middle_East/Israel/%')
//or (name like 'Top/Sports/%' and hierarchy_level=3)
//or (name like 'Top/Science/%' and hierarchy_level=3)
//and is_english_cat=1
//order by name
//INTO OUTFILE 'C:/ProgramData/MySQL/MySQL Server 5.7/Uploads/interests_vector.csv'
//fields terminated by ','
//enclosed by '"'
//LINES TERMINATED BY '\n';