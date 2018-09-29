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
 * BrowseHistoryInfoBead InfoBead
 * @author Yuri Variat
 * @version  1.0;
 *
 */
public class BrowseHistoryInfoBead extends InfoBead<UserBrowsingVector>{
	//implements Runnable {
	
	private static final String TAG="BrowseHistoryInfoBead";
	private static final long serialVersionUID = 1L;
	private UserBrowsingVector _userVector;

    public BrowseHistoryInfoBead(){
        super();
        initialize();
    }
	public void initialize() 
	{
		// -------------- set id ------------------------
		this.setInfoBeadId("BrowseHistoryInfoBead");
		this.setInfoBeadVersionId(String.valueOf(serialVersionUID));

		// -------------- set control part ------------------------
		InfoBeadControl control = new InfoBeadControl();
		control.setComMode(InfoBeadControl.ConnectionType.PULL);
		setControlPart(control);

		// -------------- set metadataPart ------------------------
		// setting up of the main components of the info-bead
		MetadataPart metadata = new MetadataPart();
		metadata.setInfoBeadName("BrowseHistoryInfoBead");
		metadata.setDescription("Info-bead that represent user interests in web browsing");
		metadata.setVersion("v1.0");
		metadata.getBackwardCompatibility().add("v1.0");

	}
	@Override
	public void handleData(Triplet<UserBrowsingVector> triplet) {
		if(triplet!=null){
			_userVector = triplet.getInfoItem().getInfoValue();
		}
	}
	public void pushData(Triplet<UserBrowsingVector> triplet) {
		
	}
	public Triplet<UserBrowsingVector> pullData(){
		Triplet<UserBrowsingVector> triplet = new Triplet<>(this.getInfoBeadId());
        triplet.setAllIds(getInfobeadAllIds());
        InfoItem<UserBrowsingVector> infItem = new InfoItem<>();
        infItem.setInfoValue(_userVector);
        triplet.setInfoItem(infItem);
        return triplet;
    }
	@Override
	public void destruct() {
	}
}