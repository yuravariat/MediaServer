package haifa.university.info_beads.media_server.handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import haifa.university.info_beads.generic.InfoBead;
import haifa.university.info_beads.generic.InfoBeadControl;
import haifa.university.info_beads.generic.InfoItem;
import haifa.university.info_beads.generic.MetadataPart;
import haifa.university.info_beads.generic.Triplet;
import haifa.university.mediaserver.common.AppLogger;
import haifa.university.mediaserver.common.Helpers;

/**
 * Coordinator InfoBead
 * @author Yuri Variat
 * @version  1.0;
 *
 */
public class CoordinatorInfoBead extends InfoBead<ArrayList<Triplet>> implements Runnable {
	private static final String TAG="InfoCollectorInfoBead";
	private static final long serialVersionUID = 1L;
	private File applicationDir = new File(Paths.get(".").toAbsolutePath().normalize().toString(), "MediaAgent");
	private final String SETTINGS_FILE_NAME = "InfoCollector_settings.txt";
	private boolean debugMode = true;

    private volatile boolean running = false;
    private volatile boolean started = false;
    private Gson gson;

    public CoordinatorInfoBead(){
        super();
        initialize();
        gson = Helpers.CreateGsonSerializer();
    }
	public void initialize() 
	{
		// -------------- set id ------------------------
		this.setInfoBeadId("CoordinatorInfoBead");
		this.setInfoBeadVersionId(String.valueOf(serialVersionUID));

		// -------------- set control part ------------------------
		InfoBeadControl control = new InfoBeadControl();
		control.setComMode(InfoBeadControl.ConnectionType.PUSH);
		setControlPart(control);

		// -------------- set metadataPart ------------------------
		// setting up of the main components of the info-bead
		MetadataPart metadata = new MetadataPart();
		InfoItem infoItemData = new InfoItem<ArrayList<Triplet>>();

		metadata.setInfoBeadName("CoordinatorInfoBead");
		metadata.setVersion("v1.0");
		metadata.getBackwardCompatibility().add("v1.0");
		// Enter the default metadata into the info-bead
		this.setMetadata(metadata);

		// ------Set default info,  it may be (partially or fully) changed when data is updated--------
		infoItemData.setExplainInfo("Info-bead that handle data from mobile devices");

		// Enter default content into the info-bead
		this.getContent().add(infoItemData);  // keep the data within the info-bead
	}

	public void handleData(String data) 
	{
		java.lang.reflect.Type listType = new TypeToken<Triplet<ArrayList<Triplet>>>(){}.getType();
		Triplet<ArrayList<Triplet>> triplet = gson.fromJson(data, listType);	
		Helpers.WriteUserData(triplet.getInfoItem().getSupplementalData(), data);
		handleData(triplet);
	}
	public void pushData(Triplet<ArrayList<Triplet>> tripletToDeliver) {
		// Send data to appropriate Info-Beads
	}
	@Override
	public void run()
	{
        while (running) {
        }
        started = false;
	}
    public synchronized void start() {
        if(!running) {
            started = true;
            running = true;
            AppLogger.getInstance().writeLog(TAG, "Started", AppLogger.LogLevel.TRACE);
        }
    }
    public synchronized void stop() {
        started = false;
        running = false;
        AppLogger.getInstance().writeLog(TAG, "Stoped", AppLogger.LogLevel.TRACE);
    }
    public boolean isStarted() {
        return started;
    }
    public boolean isRunning() {
        return running;
    }
	@Override
	public void destruct() {
		stop();
	}
	@Override
	public void handleData(Triplet<ArrayList<Triplet>> data) {
		// TODO Auto-generated method stub
		
	}
}