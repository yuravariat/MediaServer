package haifa.university.mediaserver;
import java.awt.EventQueue;
import haifa.university.mediaserver.gui.DashboardWindow;

public class ServerApp {

	private static boolean playGround = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		if(playGround){
			PlayGround();
		}
		else{
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						DashboardWindow window = new DashboardWindow();
						window.Show();				
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
	public static void PlayGround(){
		PlayGround.testZone();
	}

}