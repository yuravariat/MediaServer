package haifa.university.info_beads.media_server;

import haifa.university.info_beads.generic.InfoBead;
import haifa.university.info_beads.generic.InfoBeadControl;
import haifa.university.info_beads.generic.InfoItem;
import haifa.university.info_beads.generic.MetadataPart;
import haifa.university.info_beads.generic.Triplet;

/**
 * HistoryHandlerInfoBead InfoBead
 * @author Yuri Variat
 * @version  1.0;
 *
 */
public class UserModelInfoBead extends InfoBead{
	//implements Runnable {
	
	private static final String TAG="UserModelInfoBead";
	private static final long serialVersionUID = 1L;
	
	private String userID;
	private String name;
	private BrowseHistoryInfoBead browseHistoryInfoBead;
	private SetTopBoxDeviceInfoBead setTopBoxDeviceInfoBead;

    public UserModelInfoBead(){
        super();
        initialize();
    }
	public void initialize() 
	{
		// -------------- set id ------------------------
		this.setInfoBeadId("UserModelInfoBead");
		this.setInfoBeadVersionId(String.valueOf(serialVersionUID));

		// -------------- set control part ------------------------
		InfoBeadControl control = new InfoBeadControl();
		control.setComMode(InfoBeadControl.ConnectionType.PULL);
		setControlPart(control);

		// -------------- set metadataPart ------------------------
		// setting up of the main components of the info-bead
		MetadataPart metadata = new MetadataPart();

		metadata.setInfoBeadName("UserModelInfoBead");
		metadata.setVersion("v1.0");
		metadata.getBackwardCompatibility().add("v1.0");
		metadata.setDescription("Info-bead that represent user model, Info-Bead pendant");
		// Enter the default meta data into the info-bead
		this.setMetadata(metadata);
		
		browseHistoryInfoBead = new BrowseHistoryInfoBead();
		setTopBoxDeviceInfoBead = new SetTopBoxDeviceInfoBead();

	}
	@Override
	public void handleData(Triplet triplet) {
		
	}
	public void pushData(Triplet triplet) {
	}
	@Override
	public void destruct() {
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public UserBrowsingVector getBrowsingPreferncesVector(){
		InfoItem<UserBrowsingVector> item = browseHistoryInfoBead.pullData().getInfoItem();
		return item!=null ? item.getInfoValue() : null;
	}
	public GenresVector getSetTopBoxGenresVector(){
		InfoItem<GenresVector> item = setTopBoxDeviceInfoBead.pullData().getInfoItem();
		return item!=null ? item.getInfoValue() : null;
	}
	public void setBrowsingPreferncesVector(UserBrowsingVector vector){
		Triplet<UserBrowsingVector> triplet = new Triplet<>(getInfoBeadId());
		InfoItem<UserBrowsingVector> item = new InfoItem<>();
		item.setInfoValue(vector);
		triplet.setInfoItem(item);
		browseHistoryInfoBead.handleData(triplet);
	}
	public void setSetTopBoxDeviceVector(GenresVector vector){
		Triplet<GenresVector> triplet = new Triplet<>(getInfoBeadId());
		InfoItem<GenresVector> item = new InfoItem<>();
		item.setInfoValue(vector);
		triplet.setInfoItem(item);
		setTopBoxDeviceInfoBead.handleData(triplet);
	}
}