Bitcoin Java Daemon (btc4j-daemon)
==================================
Type-safe, open source Java - bitcoind bridge.

Development build status: [![Build Status](https://travis-ci.org/btc4j/btc4j-daemon.png?branch=master)](https://travis-ci.org/btc4j/btc4j-daemon)

Calls the bitcoind Json-Rpc service (Bitcoin-Qt v0.8.6) using:
* Apache Commons HttpClient API (org.apache.commons.httpclient)
* Java API for Json Processing and reference implementation (javax.json and org.glassfish.json)

bitcoind API development status (out of a total of 59 commands):
* Completed and verified: 34
* Completed but not verified: 1
* Work in progress: 5
* Not yet implemented: 19

Notification subsystem development status:
* Notifier: complete
* Alerts: complete
* Block events: complete
* Wallet events: complete

Using btc4j-daemon
------------------
btc4j-daemon is free software under [The MIT License (MIT)](http://opensource.org/licenses/MIT/ "The MIT License (MIT)"). It is maintained by Guillermo Gonzalez (email@ggonzalez.info).

Maven pom.xml dependency:
```xml
<dependency>
	<groupId>org.btc4j</groupId>
	<artifactId>btc4j-daemon</artifactId>
	<version>0.0.3-SNAPSHOT</version>
</dependency>
```

Connect to a bitcoind process:
```java
BtcDaemon daemon = new BtcDaemon(new URL("http://127.0.0.1:18332"),
						"user", "password", 30000);
BtcStatus info = daemon.getInfo();
String address = daemon.getAccountAddress("user");
String stop = daemon.stop(); // will stop bitcoind 
```
or with notifications enabled:
```java
BtcDaemon daemon = new BtcDaemon(new URL("http://127.0.0.1:18332"),
						"user", "password", 30000, 18334, 18335, 18336);
daemon.getWalletListener().addObserver(new Observer() {
	@Override
	public void update(Observable o, Object obj) {
		if (obj instanceof BtcTransaction) {
			BtcTransaction transaction = (BtcTransaction) obj;
			System.out.println("received wallet event: " + transaction);
		}
	}
});
double amount = daemon.getReceivedByAccount("user");
daemon.backupWallet(new File("wallet.dat"));
daemon.stopListening(); // stops the listeners if notifications enabled
String stop = daemon.stop(); // will stop bitcoind
```
For notifications to work, bitcoind has to be started with the notification args:
```bash
./bitcoind -testnet -rpcuser=user -rpcpassword=password
			-alertnotify="java -cp btc4j-daemon-0.0.3-SNAPSHOT.jar org.btc4j.daemon.BtcDaemonNotifier 127.0.0.1 18334 %s"
			-blocknotify="java -cp btc4j-daemon-0.0.3-SNAPSHOT.jar org.btc4j.daemon.BtcDaemonNotifier 127.0.0.1 18335 %s"
			-walletnotify="java -cp btc4j-daemon-0.0.3-SNAPSHOT.jar org.btc4j.daemon.BtcDaemonNotifier 127.0.0.1 18336 %s"
```
'org.btc4j.daemon.BtcDaemonNotifier' is a simple util that sends a line of text to a port on a given host.
* usage: `java BitcoinDaemonNotifier <host> <port> <message>`
* OS command: you could use an OS command insted such as `nc`

For more examples see `BtcDaemonTest.java`.

Issues
------
Report bugs, issues, or suggestions: https://github.com/btc4j/btc4j-daemon/issues.

Contributions
-------------
Project location: https://github.com/btc4j/btc4j-daemon.
* git: `git@github.com:btc4j/btc4j-daemon.git`
* svn: `https://github.com/btc4j/btc4j-daemon`

Donations
---------
If you find the software useful and would like to make a donation, please send bitcoins to `1ACz6GKw3B6vjoYwGA2hnXDnbR5koFhC4j`.

Copyright &copy; 2013, 2014 by Guillermo Gonzalez, btc4j.org.