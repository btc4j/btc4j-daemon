package org.btc4j.daemon;

import java.net.URL;

public class BtcDaemonMain {

	public static void main(String[] args) {
		try {
			BtcDaemon daemon = new BtcDaemon(new URL("http://127.0.0.1:18332"),
					"user", "password", 10000);
			//System.out.println("help: " + daemon.help("getrawtransaction"));
			//System.out.println("raw transaction: " + daemon.getRawTransaction("98dafef582ae53e8af30e8ad09577bbd472d4bf24a173121d58a5900747fd082", true));
			//System.out.println("raw transaction: " + daemon.getRawTransaction("5cfc16d9937e4ad36686b829942b0a7ab088750bc3008a71cd0a13ccf4ac1099"));
			 daemon.stop();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
