package haifa.university.mediaserver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

import haifa.university.mediaserver.common.CSVUtils;
import haifa.university.mediaserver.common.GenericResponse;
import haifa.university.clustering.ClusteringEvaluationHelper;
import haifa.university.clustering.DataHandler;
import haifa.university.clustering.FuzzyClustering;
import haifa.university.clustering.HierarchicalClustering;
import haifa.university.clustering.KMeansClustering;
import haifa.university.clustering.distanceFunctions.DistanceFunctionForSSF;
import haifa.university.clustering.distanceFunctions.PearsonCorrelation;
import haifa.university.dmoz.Category;
import haifa.university.dmoz.DmozExtractor;
import haifa.university.dmoz.DmozLink;
import haifa.university.dmoz.SelectedCategories;
import haifa.university.info_beads.inputs.browsed_history.HistoryBookmark;
import haifa.university.info_beads.media_server.UserModelInfoBead;
import haifa.university.info_beads.media_server.handlers.BrowserHistoryInfoBeadHandler;
import haifa.university.info_beads.media_server.handlers.CoordinatorInfoBead;
import weka.clusterers.ClusterEvaluation;
import weka.core.Instances;
import weka.core.matrix.Matrix;

public class PlayGround {
	public static void testZone() {
		testKMeansSetTopBoxes();
	}

	public static void loadBookimarksFromCSV(String filename, String source) {
		List<HistoryBookmark> user_marks = DmozExtractor.loadBookmarksFromScv(source);
		int total_count = 0;
		int total_valid = 0;
		int found_in_dmoz = 0;
		int found_exact_dmoz = 0;
		String csvFile = Paths.get("data") + "/" + filename + ".csv";
		File file = new File(csvFile);

		try (FileWriter writer = new FileWriter(file, file.exists())) {
			CSVUtils.writeLine(writer, Arrays.asList("url", "found_url"));
			for (HistoryBookmark mark : user_marks) {
				total_count++;
				if (isValidUrlforAnalyze(mark.url)) {
					total_valid++;
					GenericResponse<DmozLink> cat = DmozExtractor.FindDmozCat(mark.url);
					if (cat.isOK() && cat.data != null) {
						found_in_dmoz++;
						if (mark.url.equals(cat.data.getResource())) {
							found_exact_dmoz++;
						}
						CSVUtils.writeLine(writer, Arrays.asList(mark.url, cat.data.getResource()));
					} else {
						// CSVUtils.writeLine(writer,Arrays.asList(mark.url,
						// "not found"));
					}
				}
			}
			CSVUtils.writeLine(writer, Arrays.asList("urls:" + total_count, "Valid urls:" + total_valid,
					"Found in dmoz urls:" + found_in_dmoz, "Found exact in dmoz urls:" + found_exact_dmoz));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done!");
		System.out.println("urls:" + total_count);
		System.out.println("Valid urls:" + total_valid);
		System.out.println("Found in dmoz urls:" + found_in_dmoz);
		System.out.println("Found exact in dmoz urls:" + found_exact_dmoz);
	}

	public static boolean isValidUrlforAnalyze(String url) {
		String _url = url.replace("http://", "").replace("https://", "");
		if (_url.startsWith("www.google") || _url.startsWith("google") || _url.startsWith("maps.google")) {
			return false;
		}
		return true;
	}

	public static void loadBookimarksFromDBtoCSV(String DeviceID, String name) {
		// loadBookimarksFromCSV("tsvika_dmoz_extract","tsvika_history");
		// loadBookimarksFromCSV("yura_dmoz_extract","yura_history");
		// loadBookimarksFromDBtoCSV("866695024182701","yura_history");

		List<HistoryBookmark> user_marks = DmozExtractor.loadMarksFromDB(DeviceID, 0);
		DmozExtractor.saveBookmarksToScv(user_marks, name);
	}

	public static void testTripletRecieve() {
		// Created Info-Beads
		CoordinatorInfoBead coordinatorInfoBead = new CoordinatorInfoBead();
		BrowserHistoryInfoBeadHandler historyHandlerInfoBead = new BrowserHistoryInfoBeadHandler();
		coordinatorInfoBead.addObserver(historyHandlerInfoBead);

		// Load sent triplet
		Path tripletFile = Paths.get("data/urls/data-03-11-2016_00_19.txt");
		String tripletTxt = "";
		try {
			tripletTxt = new String(Files.readAllBytes(tripletFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		coordinatorInfoBead.handleData(tripletTxt);

		int test = 1;
		System.out.println(test);
	}

	public static void test() {
		DmozExtractor.urlsFromFilesIntoScv();

		// int test = SelectedCategories.getInstance().getCategoiresCount();

		List<HistoryBookmark> marks = DmozExtractor.loadMarksFromDB("ffffffff-dfdb-b2ad-ffff-ffffef1e7ce3", 15);

		HashMap<String, String> hmap = new HashMap<String, String>();

		String csvFile = Paths.get("data") + "/translated-urls.csv";

		try {

			File file = new File(csvFile);
			if (!file.exists()) {
				file.createNewFile();
			}

			try (FileWriter writer = new FileWriter(file, true)) {

				CSVUtils.writeLine(writer, Arrays.asList("url", "found path in dmoz", "matched url in dmoz",
						"user vector category name", "user vector category id", "errors 1", "error 2"));

				for (HistoryBookmark mark : marks) {
					GenericResponse<DmozLink> cat = DmozExtractor.FindDmozCat(mark.url);
					String erros = cat.errors != null
							? String.join(",", cat.errors.stream().map(c -> c.message).collect(Collectors.toList()))
							: "none";
					String erros2 = "";
					int uscat = -1;
					Category uscateg = null;
					boolean catFound = cat.isOK() && cat.data != null && cat.data.getTopic().length() > 0;
					if (catFound) {
						uscat = SelectedCategories.getInstance().findCategoryIndex(cat.data.getTopic());
						if (uscat > -1) {
							uscateg = SelectedCategories.getInstance().getCategory(uscat);
						}
					}
					CSVUtils.writeLine(writer,
							Arrays.asList(mark.url, catFound ? cat.data.getTopic() : "",
									catFound ? cat.data.getResource() : "", (uscateg != null ? uscateg.getPath() : ""),
									uscat + "", erros, erros2));
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int wrerwerw = 7;
	}

	public static void testGenerateData() {
		Instances data = DataHandler.generateInstances(10, 20);
	}

	public static void testKMeans(int numOfUsers) {

		KMeansClustering kMeans = new KMeansClustering();
		List<UserModelInfoBead> users = DataHandler.generateUsersModels(numOfUsers);
		Instances data = DataHandler.PrepareInstancesBrowseHistory(users);
		// Instances data = DataHandler.generateInstances(numOfUsers, 30);

		int[] assignments = kMeans.Apply(data);

		DistanceFunctionForSSF df = new PearsonCorrelation();
		df.setInstances(data);
		System.out.println("Silhouette Score = " + ClusteringEvaluationHelper.SilhouetteScore(kMeans, data, df));
		ClusteringEvaluationHelper.Evaluate(kMeans, data);
	}

	public static void testHierarchical(int numOfUsers) {
		HierarchicalClustering h = new HierarchicalClustering();
		List<UserModelInfoBead> users = DataHandler.generateUsersModels(numOfUsers);
		Instances data = DataHandler.PrepareInstancesBrowseHistory(users);
		// Instances data = DataHandler.generateInstances(numOfUsers, 30);

		int[] assignments = h.Apply(data);

		DistanceFunctionForSSF df = new PearsonCorrelation();
		df.setInstances(data);
		System.out.println("Silhouette Score = " + ClusteringEvaluationHelper.SilhouetteScore(h, data, df));
		ClusteringEvaluationHelper.Evaluate(h, data);
	}

	public static void testFuzzy(int numOfUsers) {
		FuzzyClustering h = new FuzzyClustering();
		List<UserModelInfoBead> users = DataHandler.generateUsersModels(numOfUsers);
		Instances data = DataHandler.PrepareInstancesBrowseHistory(users);
		// Instances data = DataHandler.generateInstances(numOfUsers, 30);

		int[] assignments = h.Apply(data);

		DistanceFunctionForSSF df = new PearsonCorrelation();
		df.setInstances(data);
		System.out.println("Silhouette Score = " + ClusteringEvaluationHelper.SilhouetteScore(h, data, df));
		ClusteringEvaluationHelper.Evaluate(h, data);
	}

	public static void testloadSetTopBoxDataFromCSV(String filePath) {
		List<UserModelInfoBead> users = DataHandler.loadSetTopBoxData(filePath);
		System.out.println("user loaded " + users.size());
	}

	public static void testKMeansSetTopBoxes() {

		KMeansClustering kMeans = new KMeansClustering();
		List<UserModelInfoBead> users = DataHandler
				.loadSetTopBoxData("D:\\SintecMediaData\\playground\\ViewHistory14Days3670Matix1Parts.csv");
		Instances data = DataHandler.PrepareInstancesSetTopBoxes(users);
		// Instances data = DataHandler.generateInstances(numOfUsers, 30);

		for (int i = 3; i <= 15; i++) {
			int[] assignments = kMeans.Apply(data, i);

			DistanceFunctionForSSF df = new PearsonCorrelation();
			df.setInstances(data);
			System.out.println("k=" + i + " Silhouette Score = " + ClusteringEvaluationHelper.SilhouetteScore(kMeans, data, df));
		}
		Scanner s = new Scanner(System.in);
		System.out.println("Press enter to continue.....");
		s.nextLine();
		// ClusteringEvaluationHelper.Evaluate(kMeans,data);
	}
}
