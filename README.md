Bitcoin Java Daemon Wrapper
===========================
Type-safe, open source Java - bitcoind wrapper.

Development build status: [![Build Status](https://travis-ci.org/btc4j/btc4j-daemon.png?branch=master)](https://travis-ci.org/btc4j/btc4j-daemon)

btc4j-daemon calls the bitcoind Json-Rpc service (Bitcoin-Qt v0.8.6) using:
* [Bitcoin Java Core Components (org.btc4j.core)] (http://btc4j.github.io/btc4j-core)
* [Apache HTTP Components (org.apache.http)] (http://hc.apache.org)
* [Java API for Json Processing (javax.json)] (http://www.oracle.com/technetwork/articles/java/json-1973242.html)

btc4j-daemon implements all 61 commands in bitcoind API as defined in org.btc4j.core.BtcApi. It also contains a notification component to process alerts, and block and wallet events received by bitcoind.

Using btc4j-daemon
------------------
btc4j-daemon is free software under [The MIT License (MIT)](http://opensource.org/licenses/MIT/ "The MIT License (MIT)"). It is maintained by Guillermo Gonzalez (ggonzalez@btc4j.org).

Maven pom.xml dependency:
```xml
<dependency>
	<groupId>org.btc4j</groupId>
	<artifactId>btc4j-daemon</artifactId>
	<version>0.0.3-SNAPSHOT</version>
</dependency>
```
or, download artifacts directly from https://github.com/btc4j/btc4j-repo/tree/master/btc4j-daemon.

Connect to a bitcoind process:
```java
// bitcoind URL, rpc user/account, rpc password
BtcDaemon daemon = new BtcDaemon(new URL("https://127.0.0.1:18332"),
					"rpcuser", "GBxDyFeDMYEHucz6XFRpXDDB2woCU4wi96KD9widEmsj");
BtcInfo info = daemon.getInfo();
String address = daemon.getAccountAddress("rpcuser");
daemon.walletPassphrase("GBxDyFeDMYEHucz6XFRpXDDB2woCU4wi96KD9widEmsj");
daemon.sendToAddress("mm48fadf1wJVF341ArWmtwZZGV8s34UGWD", 2.5); 
daemon.walletLock();
String stop = daemon.stop(); // will stop bitcoind, not required
```
or, with notifications enabled:
```java
// bitcoind URL, rpc user/account, rpc password, notification ports 
BtcDaemon daemon = new BtcDaemon(new URL("http://127.0.0.1:18332"),
					"user", "password", 18334, 18335, 18336);
daemon.getWalletListener().addObserver(new Observer() {
	@Override
	public void update(Observable o, Object obj) {
		if (obj instanceof BtcTransaction) {
			BtcTransaction transaction = (BtcTransaction) obj;
			System.out.println("received wallet event: " + transaction);
		}
	}
});
BigDecimal amount = daemon.getReceivedByAccount("user");
Map<String, BtcAccount> accounts = daemon.listAccounts();
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
`org.btc4j.daemon.BtcDaemonNotifier` is a simple utility that sends a line of text to a port on a given host.
* usage: `java BitcoinDaemonNotifier <host> <port> <message>`
* OS command: you could use an OS command instead such as `netcat` or `nc`

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

Copyright &copy; 2013, 2014 by Guillermo Gonzalez, [btc4j.org](http://www.btc4j.org "btc4j.org").