 package amm;
 
 import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.Keymap;
 
 class MainClass
 {
	static boolean value = false;
	private JButton startButton;
	private JButton stopButton;
	private JTextField textField;
	private JTextField idleTimeField;
	private MenuItem startMenuItem;
	private MenuItem stopMenuItem;
	private MenuItem exitMenuItem;
	private Thread monitorThread;

	public JTextField getIdleTimeField() {
		return this.idleTimeField;
	}

	public void setIdleTimeField(JTextField idleTimeField) {
		this.idleTimeField = idleTimeField;
	}

	public void createDialog() {
		final JFrame frame = new JFrame("AMM v2.1 - Prashant Gupta");
		frame.setSize(400, 100);
		frame.setDefaultCloseOperation(3);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		frame.add(panel, "Center");
		JLabel label = new JLabel("Please enter the time interval (seconds) : ");
		panel.add(label);
		this.textField = new JTextField(10);
		this.textField.setText("60");
		this.textField.selectAll();
		panel.add(this.textField);

		JLabel idleLabel = new JLabel("Idle Time : ");
		panel.add(idleLabel);
		this.idleTimeField = new JTextField(5);
		this.idleTimeField.setEditable(false);
		panel.add(this.idleTimeField);

		this.startButton = new JButton("Start");
		panel.add(this.startButton);
		this.startButton.addActionListener(new StartButton());

		this.stopButton = new JButton("Stop");
		this.stopButton.setEnabled(false);
		panel.add(this.stopButton);
		this.stopButton.addActionListener(new StopButton());

		Keymap keymap = this.textField.getKeymap();
		KeyStroke keystroke = KeyStroke.getKeyStroke(10, 0, false);
		keymap.removeKeyStrokeBinding(keystroke);

		frame.getRootPane().setDefaultButton(this.startButton);
		frame.setVisible(true);

		URL imageURL = ClassLoader.getSystemResource("amm/image.gif");
		Image image = Toolkit.getDefaultToolkit().getImage(imageURL);

		ActionListener exitListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		};
		if (SystemTray.isSupported()) {
			PopupMenu popup = new PopupMenu();

			this.startMenuItem = new MenuItem("Start");
			this.startMenuItem.addActionListener(new StartButton());
			popup.add(this.startMenuItem);

			this.stopMenuItem = new MenuItem("Stop");
			this.stopMenuItem.addActionListener(new StopButton());
			this.stopMenuItem.setEnabled(false);
			popup.add(this.stopMenuItem);

			popup.addSeparator();

			this.exitMenuItem = new MenuItem("Exit");
			this.exitMenuItem.addActionListener(exitListener);
			popup.add(this.exitMenuItem);

			final TrayIcon icon = new TrayIcon(image, "Prashant Gupta", popup);

			icon.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					frame.setVisible(true);
					frame.setExtendedState(0);
					SystemTray.getSystemTray().remove(icon);
				}
			});
			frame.addWindowListener(new WindowAdapter() {
				public void windowIconified(WindowEvent e) {
					frame.setVisible(false);
					try {
						SystemTray.getSystemTray().add(icon);
					} catch (AWTException e1) {
						System.out
								.println("Sorry dude your system does not support system tray!");
					}
				}
			});
		}
	}

	class StartButton implements ActionListener {
		StartButton() {
		}

		public void actionPerformed(ActionEvent e) {
			MainClass.value = true;

			String text = MainClass.this.textField.getText();
			int parseInt;
			try {
				parseInt = Integer.parseInt(text);
				if (parseInt <= 5) {
					throw new RuntimeException();
				}
				parseInt *= 1000;
			} catch (Exception e1) {
				parseInt = 5000;
				MainClass.this.textField.setText(Integer.toString(5));
			}
			Monitor m = new Monitor(parseInt, MainClass.this.idleTimeField);

			MainClass.this.monitorThread = new Thread(m);
			MainClass.this.monitorThread.start();
			MainClass.this.startButton.setEnabled(false);
			MainClass.this.stopButton.setEnabled(true);
			MainClass.this.startMenuItem.setEnabled(false);
			MainClass.this.stopMenuItem.setEnabled(true);
			MainClass.this.stopButton.requestFocus();
			MainClass.this.textField.setEditable(false);
		}
	}

	class StopButton implements ActionListener {
		StopButton() {
		}

		public void actionPerformed(ActionEvent e) {
			MainClass.this.monitorThread.interrupt();
			MainClass.value = false;
			try {
				MainClass.this.monitorThread.join();
			} catch (InterruptedException localInterruptedException) {
			}
			MainClass.this.startButton.setEnabled(true);
			MainClass.this.stopButton.setEnabled(false);
			MainClass.this.startMenuItem.setEnabled(true);
			MainClass.this.stopMenuItem.setEnabled(false);
			MainClass.this.startButton.requestFocus();
			MainClass.this.textField.setEditable(true);
		}
	}
}