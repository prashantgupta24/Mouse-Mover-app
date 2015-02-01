package amm;
 
import java.awt.MouseInfo;

import javax.swing.JTextField;

class Monitor
implements Runnable
 {
	int sleepTime;
	int x;
	int y;
	boolean isFirstTime = true;
	boolean isLastTime = false;
	JTextField idleTimeField;
	int idleTime = 0;
	int count = 0;
	Thread mouseMoverThread;

	public Monitor(int sleepTime, JTextField idleTimeField) {
		this.sleepTime = sleepTime;
		this.idleTimeField = idleTimeField;
	}

	public void run() {
		this.idleTime = 0;
		MouseMover mouseMover = new MouseMover(this.sleepTime);

		boolean flag = true;
		for (;;) {
			boolean value = MainClass.value;
			if (!value) {
				break;
			}
			if (flag) {
				this.x = MouseInfo.getPointerInfo().getLocation().x;
				this.y = MouseInfo.getPointerInfo().getLocation().y;
				flag = false;
			} else {
				this.count += 1;
				int x2 = MouseInfo.getPointerInfo().getLocation().x;
				int y2 = MouseInfo.getPointerInfo().getLocation().y;
				if ((Math.abs(x2 - this.x) <= 5)
						&& (Math.abs(y2 - this.y) <= 5)) {
					if ((x2 != this.x) || (y2 != this.y)) {
						this.idleTimeField.setText(Integer.toString(0));
						this.idleTime = 0;
					}
					if (this.count % 2 == 0) {
						if (this.idleTime >= this.sleepTime / 1000) {
							this.idleTimeField.setText(Integer.toString(0));
							this.idleTime = 0;
						}
						this.idleTimeField.setText(Integer
								.toString(this.idleTime++));
						this.count = 0;
						if (this.isFirstTime) {
							this.mouseMoverThread = new Thread(mouseMover,
									"Mouse Mover Thread");
							if (!this.mouseMoverThread.isAlive()) {
								this.mouseMoverThread.start();
								this.isFirstTime = false;
								this.isLastTime = true;
							}
						}
					}
				} else {
					this.idleTimeField.setText(Integer.toString(0));
					this.idleTime = 0;
					this.count = 0;
					if (this.isLastTime) {
						mouseMover.setStop(true);
						this.mouseMoverThread.interrupt();
						this.isLastTime = false;
					}
					this.isFirstTime = true;
				}
				flag = true;
			}
			try {
				Thread.sleep(250L);
			} catch (InterruptedException localInterruptedException) {
			}
		}
		this.idleTimeField.setText(Integer.toString(0));
		mouseMover.setStop(true);
		this.mouseMoverThread.interrupt();
		try {
			this.mouseMoverThread.join();
		} catch (InterruptedException localInterruptedException1) {
		}
	}
}