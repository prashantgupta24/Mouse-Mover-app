package amm;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;

import javax.swing.JTextField;

class MouseMover
  implements Runnable
 {
	int sleepTime;
	boolean stop = false;
	JTextField idleTimeField;

	public boolean isStop() {
		return this.stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	public MouseMover(int sleepTime) {
		this.sleepTime = sleepTime;
	}

	public void run() {
		Robot robot = null;
		try {
			robot = new Robot();
		} catch (AWTException localAWTException) {
		}
		boolean flag = true;
		synchronized (this) {
			if (this.stop) {
				this.stop = false;
			} else {
				try {
					wait(this.sleepTime);
				} catch (InterruptedException localInterruptedException) {
				}
				if ((flag) && (!this.stop)) {
					robot.mouseMove(
							MouseInfo.getPointerInfo().getLocation().x + 5,
							MouseInfo.getPointerInfo().getLocation().y);
					flag = false;
				} else if ((!flag) && (!this.stop)) {
					robot.mouseMove(
							MouseInfo.getPointerInfo().getLocation().x - 5,
							MouseInfo.getPointerInfo().getLocation().y);
					flag = true;
				}
			}
		}
	}
}