package org.btc4j.daemon;

import java.net.URL;

public class BtcDaemonMain {

	public static void main(String[] args) {
		try {
			BtcDaemon daemon = new BtcDaemon(new URL("http://127.0.0.1:18332"),
					"user", "password", 10000);

			System.out.println("help: " + daemon.help("getrawtransaction"));
			System.out
					.println("get raw transaction: "
							+ daemon.getRawTransaction(
									"98dafef582ae53e8af30e8ad09577bbd472d4bf24a173121d58a5900747fd082",
									true));
			System.out
					.println("get raw transaction: "
							+ daemon.getRawTransaction("5cfc16d9937e4ad36686b829942b0a7ab088750bc3008a71cd0a13ccf4ac1099"));

			System.out.println("help: " + daemon.help("decoderawtransaction"));
			System.out
					.println("decode raw transaction: "
							+ daemon.decodeRawTransaction("010000000110f7af4e331b02cb2d0300bc879c65803274969da9aa305bade614058b32152d010000006b4830450220744d68a227a390e170e0f7d23ecb41ef883d6b1059d0e8b04cd6b00db2b0fd12022100d61a88715e6f0b192c4d09b8a302e93c7eb202c4346386d918a0668caa57df6c012102d94d41c62b1d0cd455772bcca8be0ffbbcee3c7bc87e6ee79689715137b96abeffffffff0240787d01000000001976a914ca8ac94e5f147ed553a0d8e6497e0bfa7e53a92588ac00e1f505000000001976a9149006180537784880b99fc57efdf25eee152cdd4588ac00000000"));

			daemon.stop();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
