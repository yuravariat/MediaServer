package haifa.university.info_beads.media_server;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import haifa.university.info_beads.generic.InfoBead;
import haifa.university.info_beads.generic.InfoBeadControl;
import haifa.university.info_beads.generic.InfoItem;
import haifa.university.info_beads.generic.MetadataPart;
import haifa.university.info_beads.generic.Triplet;
import haifa.university.mediaserver.common.Helpers;
import haifa.university.mysql.MySqlHelper;

/**
 * SetTopBoxDeviceInfoBead InfoBead
 * @author Yuri Variat
 * @version  1.0;
 *
 */
public class SetTopBoxDeviceInfoBead extends InfoBead<GenresVector>{
	//implements Runnable {
	
	private static final String TAG="SetTopBoxDeviceInfoBead";
	private static final long serialVersionUID = 1L;
	private String deviceID;
	private GenresVector _genresVector;

    public SetTopBoxDeviceInfoBead(){
        super();
        initialize();
    }
	public void initialize() 
	{
		// -------------- set id ------------------------
		this.setInfoBeadId(TAG);
		this.setInfoBeadVersionId(String.valueOf(serialVersionUID));

		// -------------- set control part ------------------------
		InfoBeadControl control = new InfoBeadControl();
		control.setComMode(InfoBeadControl.ConnectionType.PULL);
		setControlPart(control);

		// -------------- set metadataPart ------------------------
		// setting up of the main components of the info-bead
		MetadataPart metadata = new MetadataPart();
		metadata.setInfoBeadName(TAG);
		metadata.setDescription("Info-bead that represent user set-top box device preferences");
		metadata.setVersion("v1.0");
		metadata.getBackwardCompatibility().add("v1.0");

	}
	@Override
	public void handleData(Triplet<GenresVector> triplet) {
		if(triplet!=null){
			_genresVector = triplet.getInfoItem().getInfoValue();
		}
	}
	public void pushData(Triplet<GenresVector> triplet) {
		
	}
	public Triplet<GenresVector> pullData(){
		Triplet<GenresVector> triplet = new Triplet<>(this.getInfoBeadId());
        triplet.setAllIds(getInfobeadAllIds());
        InfoItem<GenresVector> infItem = new InfoItem<>();
        infItem.setInfoValue(_genresVector);
        triplet.setInfoItem(infItem);
        return triplet;
    }
	@Override
	public void destruct() {
	}
	public String getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
}