Bitcoin Java Daemon (btc4j-daemon)
==================================
Open source Java - bitcoind bridge.

The bridge calls the bitcoind Json-Rpc service (Bitcoin-Qt v0.8.6) using:
* Apache Commons HttpClient API (org.apache.commons.httpclient)
* Java API for Json Processing and reference implementation (javax.json and org.glassfish.json)

There are two connection modes:
* Connect to an already running bitcoind process
* Start a bitcoind process and then connect to it 

Using btc4j-daemon
------------------
Snapshot builds are available from GitHub btc4j-repo:

		<dependency>
			<groupId>org.btc4j</groupId>
			<artifactId>btc4j-daemon</artifactId>
			<version>0.0.3-SNAPSHOT</version>
		</dependency>
		
Add the following to your pom.xml:

		<repositories>
			<repository>
				<id>btc4j-repo-snapshots</id>
				<url>https://github.com/btc4j/btc4j-repo/snapshots</url>
			</repository>
		</repositories>

Development build status: [![Build Status](https://travis-ci.org/btc4j/btc4j-daemon.png?branch=master)](https://travis-ci.org/btc4j/btc4j-daemon)

btc4j-daemon is free software under [The MIT License (MIT)](http://opensource.org/licenses/MIT/ "The MIT License (MIT)"). It is maintained by Guillermo Gonzalez (email@ggonzalez.info).

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