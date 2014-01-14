package org.btc4j.daemon;

public class BtcBlockListener extends BtcAlertListener {
	
	public BtcBlockListener(int port) {
		super(port);
	}
	
	@Override
	public void notifyObservers(Object arg) {
		super.notifyObservers(arg);
	}
}
