/*
 The MIT License (MIT)
 
 Copyright (c) 2013, 2014 by Guillermo Gonzalez, btc4j.org

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

package org.btc4j.daemon;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.btc4j.core.BtcAccount;
import org.btc4j.core.BtcAddress;
import org.btc4j.core.BtcBlock;
import org.btc4j.core.BtcException;
import org.btc4j.core.BtcLastBlock;
import org.btc4j.core.BtcMining;
import org.btc4j.core.BtcNodeOperation;
import org.btc4j.core.BtcPeer;
import org.btc4j.core.BtcStatus;
import org.btc4j.core.BtcTransaction;
import org.btc4j.core.BtcTransactionDetail;
import org.btc4j.core.BtcTransactionOutputSet;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class BtcDaemonTest {
	private static BtcDaemon BITCOIND;
	private static final boolean BITCOIND_RUN = false;
	private static final String BITCOIND_ACCOUNT = "user";
	private static final String BITCOIND_CMD = "bitcoind.exe";
	private static final String BITCOIND_NOTIFICATION = "java -cp E:\\eclipse\\workspace\\btc4j-daemon\\target\\btc4j-daemon-0.0.3-SNAPSHOT.jar org.btc4j.daemon.BtcDaemonNotifier";
	private static int BITCOIND_DELAY_MILLIS = 5000;
	private static final String BITCOIND_DIR = "E:/bitcoin/bitcoind-0.8.6";
	private static final String BITCOIND_PASSWD = "password";
	private static final String BITCOIND_WALLET = "wallet.dat";
	private static final String BITCOIND_ADDRESS = "mteUu5qrZJAjybLJwVQpxxmpnyGFUhPYQD";
	private static final String BITCOIND_PRIVATE_KEY = "cQ57cLoFkYRSAZJGkYMc8cTCoJhaQVEqSYNuVuUySzuLATFQ4Vcr";

	@BeforeClass
	public static void testSetup() throws Exception {
		if (BITCOIND_RUN) {
			BITCOIND = BtcDaemon.runDaemon(new File(BITCOIND_DIR + "/"
					+ BITCOIND_CMD), BITCOIND_NOTIFICATION, true,
					BITCOIND_ACCOUNT, BITCOIND_PASSWD, BITCOIND_DELAY_MILLIS);
		} else {
			BITCOIND = BtcDaemon.connectDaemon(
					BtcDaemonConstant.BTC4J_DAEMON_HOST,
					BtcDaemonConstant.BTC4J_DAEMON_PORT,
					BtcDaemonConstant.BTC4J_DAEMON_NOTIFIER_PORT,
					BITCOIND_ACCOUNT, BITCOIND_PASSWD, BITCOIND_DELAY_MILLIS);
		}
	}

	@AfterClass
	public static void testCleanup() throws Exception {
		if (BITCOIND_RUN) {
			String stop = BITCOIND.stop();
			assertNotNull(stop);
			assertTrue(stop.length() >= 0);
		}
	}

	@Test
	public void notification() throws BtcException, InterruptedException {
		BITCOIND.getListener().addObserver(new Observer() {
			@Override
			public void update(Observable o, Object obj) {
				System.out.println(String.valueOf(obj));
			}
		});
		Thread.sleep(60000);
	}

	@Test(expected = BtcException.class)
	public void addMultiSignatureAddress() throws BtcException {
		BITCOIND.addMultiSignatureAddress(0, new ArrayList<String>(), "");
	}

	@Test(expected = BtcException.class)
	public void addNode() throws BtcException {
		BITCOIND.addNode("", BtcNodeOperation.ADD);
	}

	@Test
	public void backupWallet() throws BtcException {
		File wallet = new File(BITCOIND_DIR + "/" + BITCOIND_WALLET);
		if (wallet.exists()) {
			wallet.delete();
		}
		BITCOIND.backupWallet(new File(BITCOIND_DIR));
		assertTrue(wallet.exists());
	}

	@Test(expected = BtcException.class)
	public void createMultiSignatureAddress() throws BtcException {
		BITCOIND.createMultiSignatureAddress(0, new ArrayList<String>());
	}

	@Test(expected = BtcException.class)
	public void createRawTransaction() throws BtcException {
		BITCOIND.createRawTransaction(new ArrayList<Object>(),
				new ArrayList<Object>());
	}

	@Test(expected = BtcException.class)
	public void decodeRawTransaction() throws BtcException {
		BITCOIND.decodeRawTransaction("");
	}

	@Test
	public void dumpPrivateKey() throws BtcException {
		String privateKey = BITCOIND.dumpPrivateKey(BITCOIND_ADDRESS);
		assertNotNull(privateKey);
		assertTrue(privateKey.length() >= 0);
		assertEquals(BITCOIND_PRIVATE_KEY, privateKey);
	}

	@Test(expected = BtcException.class)
	public void encryptWallet() throws BtcException {
		BITCOIND.encryptWallet("");
	}

	@Test
	public void getAccount() throws BtcException {
		String account = BITCOIND.getAccount(BITCOIND_ADDRESS);
		assertNotNull(account);
	}

	@Test
	public void getAccountAddress() throws BtcException {
		String address = BITCOIND.getAccountAddress(BITCOIND_ACCOUNT);
		assertNotNull(address);
	}

	@Test(expected = BtcException.class)
	public void getAddedNodeInformation() throws BtcException {
		BITCOIND.getAddedNodeInformation(false, "");
	}

	@Test
	public void getAddressesByAccount() throws BtcException {
		List<String> addresses = BITCOIND
				.getAddressesByAccount(BITCOIND_ACCOUNT);
		assertNotNull(addresses);
		assertTrue(addresses.size() >= 0);
	}

	@Test
	public void getBalance() throws BtcException {
		double balance = BITCOIND.getBalance("", -1);
		assertTrue(balance >= 0);
		balance = BITCOIND.getBalance(BITCOIND_ACCOUNT, 2);
		assertTrue(balance >= 0);
	}

	@Test
	public void getBlock() throws BtcException {
		BtcBlock block = BITCOIND
				.getBlock("000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943");
		assertNotNull(block);
		assertEquals(
				"000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943",
				block.getHash());
		assertEquals(0, block.getHeight());
		assertEquals(1, block.getVersion());
		block = BITCOIND
				.getBlock("00000000b873e79784647a6c82962c70d228557d24a747ea4d1b8bbe878e1206");
		assertNotNull(block);
		assertEquals(
				"00000000b873e79784647a6c82962c70d228557d24a747ea4d1b8bbe878e1206",
				block.getHash());
		List<BtcTransaction> transactions = block.getTransactions();
		assertNotNull(transactions);
		int size = transactions.size();
		assertTrue(size > 0);
		if (size > 0) {
			BtcTransaction transaction = transactions.get(0);
			assertNotNull(transaction);
		}
	}

	@Test
	public void getBlockCount() throws BtcException {
		int blocks = BITCOIND.getBlockCount();
		assertTrue(blocks >= 0);
	}

	@Test
	public void getBlockHash() throws BtcException {
		String hash = BITCOIND.getBlockHash(-1);
		assertNotNull(hash);
		assertTrue(hash.length() >= 0);
	}

	@Test(expected = BtcException.class)
	public void getBlockTemplate() throws BtcException {
		BITCOIND.getBlockTemplate("");
	}

	@Test
	public void getConnectionCount() throws BtcException {
		int connections = BITCOIND.getConnectionCount();
		assertTrue(connections >= 0);
	}

	@Test
	public void getDifficulty() throws BtcException {
		double difficulty = BITCOIND.getDifficulty();
		assertTrue(difficulty >= 0);
	}

	@Test
	public void getGenerate() throws BtcException {
		boolean generate = BITCOIND.getGenerate();
		assertTrue(generate || true);
	}

	@Test
	public void getHashesPerSecond() throws BtcException {
		int hashesPerSec = BITCOIND.getHashesPerSecond();
		assertTrue(hashesPerSec >= 0);
	}

	@Test
	public void getInformation() throws BtcException {
		BtcStatus info = BITCOIND.getInformation();
		assertNotNull(info);
		assertTrue(info.isTestnet());
		assertEquals(80600, info.getVersion());
	}

	@Test
	public void getMiningInformation() throws BtcException {
		BtcMining info = BITCOIND.getMiningInformation();
		assertNotNull(info);
		assertTrue(info.isTestnet());
		assertTrue(info.getDifficulty() >= 0);
	}

	@Ignore("Generates new address")
	@Test
	public void getNewAddress() throws BtcException {
		String address = BITCOIND.getNewAddress(BITCOIND_ACCOUNT);
		assertNotNull(address);
		address = BITCOIND.getNewAddress();
		assertNotNull(address);
	}

	@Test
	public void getPeerInformation() throws BtcException {
		List<BtcPeer> peers = BITCOIND.getPeerInformation();
		assertNotNull(peers);
		int size = peers.size();
		assertTrue(size >= 0);
		if (size > 0) {
			BtcPeer peer = peers.get(0);
			assertTrue(peer.isSyncNode() || true);
			assertTrue(peer.getBanScore() >= 0);
		}
	}

	@Test
	public void getRawMemoryPool() throws BtcException {
		List<String> rawMemPool = BITCOIND.getRawMemoryPool();
		assertNotNull(rawMemPool);
		assertTrue(rawMemPool.size() >= 0);
	}

	@Test(expected = BtcException.class)
	public void getRawTransaction() throws BtcException {
		BITCOIND.getRawTransaction("", false);
	}

	@Test
	public void getReceivedByAccount() throws BtcException {
		double balance = BITCOIND.getReceivedByAccount("");
		assertTrue(balance >= 0);
		balance = BITCOIND.getReceivedByAccount(BITCOIND_ACCOUNT, 2);
		assertTrue(balance >= 0);
	}

	@Test
	public void getReceivedByAddress() throws BtcException {
		double balance = BITCOIND.getReceivedByAddress(BITCOIND_ADDRESS);
		assertTrue(balance >= 0);
	}

	@Test
	public void getTransaction() throws BtcException {
		BtcTransaction transaction = BITCOIND
				.getTransaction("5cfc16d9937e4ad36686b829942b0a7ab088750bc3008a71cd0a13ccf4ac1099");
		assertNotNull(transaction);
		List<BtcTransactionDetail> details = transaction.getDetails();
		assertNotNull(details);
		transaction = BITCOIND
				.getTransaction("98dafef582ae53e8af30e8ad09577bbd472d4bf24a173121d58a5900747fd082");
		assertNotNull(transaction);
		details = transaction.getDetails();
		assertNotNull(details);
	}

	@Test(expected = BtcException.class)
	public void getTransactionOutput() throws BtcException {
		BITCOIND.getTransactionOutput("", 0, false);
	}

	@Test
	public void getTransactionOutputSetInformation() throws BtcException {
		BtcTransactionOutputSet txOutputSet = BITCOIND
				.getTransactionOutputSetInformation();
		assertNotNull(txOutputSet);
		assertTrue(txOutputSet.getHeight() >= 0);
	}

	@Test(expected = BtcException.class)
	public void getWork() throws BtcException {
		BITCOIND.getWork("");
	}

	@Test
	public void help() throws BtcException {
		String help = BITCOIND.help();
		assertNotNull(help);
		assertTrue(help.length() >= 0);
		help = BITCOIND.help("fakecommand");
		assertNotNull(help);
		assertTrue(help.length() >= 0);
		help = BITCOIND.help("walletlock");
		assertNotNull(help);
		assertTrue(help.length() >= 0);
		System.out.println(help);
	}

	@Ignore("need wallet password")
	@Test
	public void importPrivateKey() throws BtcException {
		// walletpassphrase <passphrase> <timeout>
		BITCOIND.importPrivateKey(BITCOIND_PRIVATE_KEY, "", false);
		// walletlock
	}

	@Test(expected = BtcException.class)
	public void keyPoolRefill() throws BtcException {
		BITCOIND.keyPoolRefill();
	}

	@Test
	public void listAccounts() throws BtcException {
		List<BtcAccount> accounts = BITCOIND.listAccounts();
		assertNotNull(accounts);
		assertTrue(accounts.size() > 0);
		BtcAccount account = accounts.get(0);
		assertNotNull(account);
		assertNotNull(account.getAccount());
	}

	@Test
	public void listAddressGroupings() throws BtcException {
		List<BtcAddress> addresses = BITCOIND.listAddressGroupings();
		assertNotNull(addresses);
		assertTrue(addresses.size() > 0);
		BtcAddress address = addresses.get(0);
		assertNotNull(address);
		BtcAccount account = address.getAccount();
		assertNotNull(account);
		assertNotNull(account.getAccount());
	}

	@Test
	public void listLockUnspent() throws BtcException {
		List<String> unspents = BITCOIND.listLockUnspent();
		assertNotNull(unspents);
	}

	@Test
	public void listReceivedByAccount() throws BtcException {
		List<BtcAccount> accounts = BITCOIND.listReceivedByAccount();
		assertNotNull(accounts);
		accounts = BITCOIND.listReceivedByAccount(0, true);
		assertNotNull(accounts);
		int size = accounts.size();
		assertTrue(size >= 0);
		if (size > 0) {
			BtcAccount account = accounts.get(0);
			assertTrue(account.getAmount() >= 0);
		}
	}

	@Test
	public void listReceivedByAddress() throws BtcException {
		List<BtcAddress> addresses = BITCOIND.listReceivedByAddress();
		assertNotNull(addresses);
		addresses = BITCOIND.listReceivedByAddress(0, true);
		assertNotNull(addresses);
		int size = addresses.size();
		assertTrue(size >= 0);
		if (size > 0) {
			BtcAddress address = addresses.get(0);
			assertTrue(address.getAmount() >= 0);
		}
	}

	@Test
	public void listSinceBlock() throws BtcException {
		BtcLastBlock lastBlock = BITCOIND.listSinceBlock();
		assertNotNull(lastBlock);
		lastBlock = BITCOIND
				.listSinceBlock(
						"00000000b873e79784647a6c82962c70d228557d24a747ea4d1b8bbe878e1206",
						0);
		assertNotNull(lastBlock);
	}

	@Test
	public void listTransactions() throws BtcException {
		List<BtcTransaction> transactions = BITCOIND
				.listTransactions(BITCOIND_ACCOUNT);
		assertNotNull(transactions);
		transactions = BITCOIND.listTransactions();
		assertNotNull(transactions);
	}

	@Test(expected = BtcException.class)
	public void listUnspent() throws BtcException {
		BITCOIND.listUnspent(0, 0);
	}

	@Test(expected = BtcException.class)
	public void lockUnspent() throws BtcException {
		BITCOIND.lockUnspent(false, new ArrayList<Object>());
	}

	@Test(expected = BtcException.class)
	public void move() throws BtcException {
		BITCOIND.move("", "", 0, 0, "");
	}

	@Test(expected = BtcException.class)
	public void sendFrom() throws BtcException {
		BITCOIND.sendFrom("", "", 0, 0, "", "");
	}

	@Test(expected = BtcException.class)
	public void sendMany() throws BtcException {
		BITCOIND.sendMany("", new ArrayList<Object>(), 0, "", "");
	}

	@Test(expected = BtcException.class)
	public void sendRawTransaction() throws BtcException {
		BITCOIND.sendRawTransaction("");
	}

	@Test(expected = BtcException.class)
	public void sendToAddress() throws BtcException {
		BITCOIND.sendToAddress("", 0, "", "");
	}

	@Ignore("Generates new address")
	@Test
	public void setAccount() throws BtcException {
		BITCOIND.setAccount(BITCOIND_ADDRESS, BITCOIND_ACCOUNT);
		List<String> addresses = BITCOIND
				.getAddressesByAccount(BITCOIND_ACCOUNT);
		assertNotNull(addresses);
		assertTrue(addresses.size() >= 0);
	}

	@Test
	public void setGenerate() throws BtcException {
		boolean generate = false;
		BITCOIND.setGenerate(generate);
		assertFalse(BITCOIND.getGenerate());
		generate = true;
		BITCOIND.setGenerate(generate, 1);
		assertTrue(BITCOIND.getGenerate());
	}

	@Test
	public void setTransactionFee() throws BtcException {
		assertTrue(BITCOIND.setTransactionFee(0.00000001));
		assertTrue(BITCOIND.setTransactionFee(-1));
	}

	@Test(expected = BtcException.class)
	public void signMessage() throws BtcException {
		BITCOIND.signMessage("", "");
	}

	@Test(expected = BtcException.class)
	public void signRawTransaction() throws BtcException {
		BITCOIND.signRawTransaction("", new ArrayList<Object>(),
				new ArrayList<String>());
	}

	@Test(expected = BtcException.class)
	public void submitBlock() throws BtcException {
		BITCOIND.submitBlock("", new ArrayList<Object>());
	}

	@Test
	public void validateAddress() throws BtcException {
		BtcAddress address = BITCOIND.validateAddress(BITCOIND_ADDRESS);
		assertNotNull(address);
		assertTrue(address.isValid());
		BtcAccount account = address.getAccount();
		assertNotNull(account);
		assertNotNull(account.getAccount());
		address = BITCOIND.validateAddress("bad address");
		assertNotNull(address);
		assertFalse(address.isValid());
	}

	@Test(expected = BtcException.class)
	public void verifyMessage() throws BtcException {
		BITCOIND.verifyMessage("", "", "");
	}
}