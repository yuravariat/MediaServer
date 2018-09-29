package haifa.university.mediaserver.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Insets;
import java.io.PrintStream;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.Timer;

import haifa.university.mediaserver.common.AppSettingsManager;
import haifa.university.mediaserver.common.TextAreaOutputStream;
import haifa.university.mediaserver.server.UsersMaping;
import haifa.university.mediaserver.server.WebService;
import java.awt.FlowLayout;
import javax.swing.JLabel;

public class DashboardWindow{

	private JFrame frame;
	private JButton btnStartService;
	private JButton btnStopService;
	private JButton clearConsoleButton;
	private TextAreaFIFO outputTestArea;
	private JPanel panel_1;
	private JPanel panel_2;
	private JLabel lblVersion;
	private JLabel lblVersionNumber;
	private JLabel lblOnlineDuration;
	private JLabel lblUpDuration;
	private JLabel lblWebOnlineDuration;
	private JLabel lblWebUpDuration;
	private Timer clockTimer;

	/**
	 * Create the application.
	 */
	public DashboardWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 700, 450);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		panel_2 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.add(panel_2, BorderLayout.SOUTH);
		
		btnStartService = new JButton("Start web service");
		panel_2.add(btnStartService);
		
		btnStopService = new JButton("Stop service");
		panel_2.add(btnStopService);
		btnStopService.addActionListener((actionEvent) -> {
			boolean b = WebService.GetInstance().Stop();
			if(b){
				btnStartService.setEnabled(true);
				btnStopService.setEnabled(false);
			}
		});
		btnStopService.setEnabled(false);
		
		clearConsoleButton = new JButton("Clear");
		panel_2.add(clearConsoleButton);
		clearConsoleButton.addActionListener((actionEvent) -> {
			outputTestArea.setText("");
			UsersMaping.getUserName("wwww");
		});
		btnStartService.addActionListener((actionEvent) -> {
			boolean b = WebService.GetInstance().Start();
			if(b){
				btnStartService.setEnabled(false);
				btnStopService.setEnabled(true);
			}
		});
		
		panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.NORTH);
		panel_1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		lblVersion = new JLabel("Version:");
		panel_1.add(lblVersion);
		
		lblVersionNumber = new JLabel(AppSettingsManager.getInstance().getBuildVersion());
		panel_1.add(lblVersionNumber);
		
		lblOnlineDuration = new JLabel(" |  Up Time:");
		panel_1.add(lblOnlineDuration);
		
		lblUpDuration = new JLabel("#");
		panel_1.add(lblUpDuration);
		
		lblWebOnlineDuration = new JLabel(" |  Web Server Up Time:");
		panel_1.add(lblWebOnlineDuration);
		
		lblWebUpDuration = new JLabel("#");
		panel_1.add(lblWebUpDuration);
		
		lblWebOnlineDuration.setVisible(false);
		lblWebUpDuration.setVisible(false);
		
		outputTestArea = new TextAreaFIFO(1000);
		outputTestArea.setEditable(false);
		outputTestArea.setMargin(new Insets(5, 5, 5, 5));
		JScrollPane sp = new JScrollPane(outputTestArea);
		frame.getContentPane().add(sp);
		
		PrintStream con = new PrintStream(new TextAreaOutputStream(outputTestArea));
		System.setOut(con);
		System.setErr(con);
	}
	public void Show(){
		if(frame!=null){
			frame.setVisible(true);
			clockTimer = new Timer(1000,(actionEvent)->{
				Instant inst = Instant.ofEpochMilli(AppSettingsManager.getInstance().getAppStarted().getTime());
				Duration uptime = Duration.between(inst,Instant.now());
				lblUpDuration.setText(String.format("%d days, %02d:%02d:%02d%n", 
						uptime.toDays(),
						uptime.toHours()%24,
						uptime.toMinutes()%60,
						uptime.minusMinutes(uptime.toMinutes()).getSeconds()));
				
				Date WebServerSarted = AppSettingsManager.getInstance().getWebServerStarted();
				if(WebServerSarted!=null){
					Instant inst2 = Instant.ofEpochMilli(WebServerSarted.getTime());
					Duration uptime2 = Duration.between(inst2,Instant.now());
					lblWebUpDuration.setText(String.format("%d days, %02d:%02d:%02d%n", 
							uptime2.toDays(),
							uptime2.toHours()%24,
							uptime2.toMinutes()%60,
							uptime2.minusMinutes(uptime2.toMinutes()).getSeconds()));
					lblWebOnlineDuration.setVisible(true);
					lblWebUpDuration.setVisible(true);
				}
				else{
					lblWebOnlineDuration.setVisible(false);
					lblWebUpDuration.setText("");
					lblWebUpDuration.setVisible(false);
				}
				
			});
			clockTimer.start();
		}
	}
}
