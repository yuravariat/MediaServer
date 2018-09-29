package haifa.university.clustering;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import haifa.university.info_beads.media_server.UserModelInfoBead;
import haifa.university.info_beads.media_server.*;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.matrix.Matrix;

public class DataHandler {
	public static Matrix generateSparseMatrix(int rows ,int cols, double trashold){
		
		int multiply = (int) Math.ceil(1.5/(1-trashold));
		Matrix matr = Matrix.random(rows,cols);
		for(int row=0;row<rows;row++){
			for(int col=0;col<cols;col++){
				double value = matr.get(row, col);
				if(value>trashold){
					double newval = (value-trashold) * multiply;
					matr.set(row, col, Math.round((Math.exp(newval))));
				}
				else{
					matr.set(row, col, 0);
				}
			}
		}
		return matr;
	}
	public static Matrix generateSparseMatrix(int rows ,int cols){
		double trashold = 0.93;
		return generateSparseMatrix(rows, cols,trashold);
	}
	public static Instances generateInstances(int rows ,int cols){
		Matrix mtrx = generateSparseMatrix(rows, cols);
		
		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		List<Instance> instances = new ArrayList<Instance>();
		
		for(int dim = 0; dim < cols; dim++)
		{
		    Attribute current = new Attribute("Attribute" + dim, dim);
		    if(dim == 0)
		    {
		        for(int obj = 0; obj < rows; obj++)
		        {
		            instances.add(new SparseInstance(cols));
		        }
		    }

		    for(int obj = 0; obj < rows; obj++)
		    {
		        instances.get(obj).setValue(current, mtrx.get(obj,dim));
		    }

		    atts.add(current);
		}

		Instances newDataset = new Instances("Dataset", atts, instances.size());

		for(Instance inst : instances)
		    newDataset.add(inst);
		
		return newDataset;
	}
	private static void saveToFile(String text,String filePath){
		try (PrintStream out = new PrintStream(new FileOutputStream(filePath))) {
		    out.print(text);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static Instances readArffFile(String filename) {
		Instances data = null;
		try(BufferedReader inputReader = new BufferedReader(new FileReader(filename))) {
			data = new Instances(inputReader);
		} catch (FileNotFoundException ex) {
			System.err.println("File not found: " + filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
	public static List<UserModelInfoBead> generateUsersModels(int numberOfusers){
		double trashold = 0.93;
		int numOffeatures = UserBrowsingVector.getVectorLength();
		List<UserModelInfoBead> users = new ArrayList<>();
		
		// Create random sparse matrix
		Matrix matr = generateSparseMatrix(numberOfusers, numOffeatures,trashold);
		
		// Create user info beads
		for(int i=0;i<numberOfusers;i++){
			UserModelInfoBead user = new UserModelInfoBead();
			user.setUserID(java.util.UUID.randomUUID().toString());
			UserBrowsingVector uv = new UserBrowsingVector();
			uv.vector = new int[UserBrowsingVector.getVectorLength()];
			for(int j= 0 ;j<numOffeatures;j++){
				uv.vector[j] = (int)matr.get(i, j);
			}
			user.setBrowsingPreferncesVector(uv);
			users.add(user);
		}
		return users;
	}
	public static Instances PrepareInstancesBrowseHistory(List<UserModelInfoBead> users){
		
		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		List<Instance> instances = new ArrayList<Instance>();
		int cols = UserBrowsingVector.getVectorLength();
		int rows = users.size();
				
		for(int col = 0; col < cols; col++)
		{
		    Attribute current = new Attribute("Attribute" + col, col);
		    if(col == 0)
		    {
		        for(int obj = 0; obj < rows; obj++)
		        {
		            instances.add(new SparseInstance(cols));
		        }
		    }
		    for(int obj = 0; obj < rows; obj++)
		    {
		        instances.get(obj).setValue(current, users.get(obj).getBrowsingPreferncesVector().vector[col]);
		    }
		    atts.add(current);
		}

		Instances newDataset = new Instances("Dataset", atts, instances.size());

		for(Instance inst : instances)
		    newDataset.add(inst);
		
		return newDataset;
	}
	public static List<UserModelInfoBead> loadSetTopBoxData(String filePath){
		List<UserModelInfoBead> items = new ArrayList<>();
		try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			int count = 0;
		    for(String line; (line = br.readLine()) != null; ) {
		        if(count==0){
		        	// This is headers
		        	count++;
		        	continue;
		        }
		        String[] segm = line.split(",");
		        UserModelInfoBead user = new UserModelInfoBead();
				user.setUserID(segm[0]);
				GenresVector v = new GenresVector();
				v.vector = new int[GenresVector.getVectorLength()];
				for(int j= 0 ;j<GenresVector.getVectorLength();j++){
					v.vector[j] = Integer.parseInt(segm[j+1]);
				}
				user.setSetTopBoxDeviceVector(v);
				items.add(user);
		    }
		    // line is not visible here.
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return items;
	}
	public static Instances PrepareInstancesSetTopBoxes(List<UserModelInfoBead> users){
		
		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		List<Instance> instances = new ArrayList<Instance>();
		int cols = GenresVector.getVectorLength();
		int rows = users.size();
				
		for(int col = 0; col < cols; col++)
		{
		    Attribute current = new Attribute("Attribute" + col, col);
		    if(col == 0)
		    {
		        for(int obj = 0; obj < rows; obj++)
		        {
		            instances.add(new SparseInstance(cols));
		        }
		    }
		    for(int obj = 0; obj < rows; obj++)
		    {
		        instances.get(obj).setValue(current, users.get(obj).getSetTopBoxGenresVector().vector[col]);
		    }
		    atts.add(current);
		}

		Instances newDataset = new Instances("Dataset", atts, instances.size());

		for(Instance inst : instances)
		    newDataset.add(inst);
		
		return newDataset;
	}
}
