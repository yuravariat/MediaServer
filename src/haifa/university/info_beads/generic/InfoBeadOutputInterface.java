package haifa.university.info_beads.generic;
import java.sql.Time;

/** 
* Sends a single Triplet of evidence to an external consumer, which may be another info-bead or a service
* @author Eyal Dim
* @version 1.0
*/
public interface InfoBeadOutputInterface {

	/**
	 * Sends a Triplet of evidence generated by the current info bead to an external consumer, which may be another info-bead or a service
	 * @param infoBeadID the ID of the sending info-bead
	 * @param sentTime the time the data was sent
	 * @param outPutData the Triplet of evidence sent
	 */
	public void sendToConsumer(String infoBeadID, Time sentTime, Triplet outputData);
}
