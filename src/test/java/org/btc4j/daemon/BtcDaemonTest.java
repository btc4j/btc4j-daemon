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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

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
	private static final boolean BITCOIND_STOP = false;
	private static final String BITCOIND_HOST = "127.0.0.1";
	private static final String BITCOIND_URL = "http://" + BITCOIND_HOST + ":18332";
	private static final String BITCOIND_ACCOUNT = "user";
	private static final String BITCOIND_PASSWD = "password";
	private static final long BITCOIND_TIMEOUT = 10000;
	private static final long BITCOIND_NOTIFICATION_SLEEP = 30000;
	private static final int BITCOIND_ALERT_PORT = 18334;
	private static final int BITCOIND_BLOCK_PORT = 18335;
	private static final int BITCOIND_WALLET_PORT = 18336;
	private static final String BITCOIND_DIR = "E:/bitcoin/bitcoind-0.8.6";
	private static final String BITCOIND_WALLET = "wallet.dat";
	private static final String BITCOIND_ADDRESS = "mteUu5qrZJAjybLJwVQpxxmpnyGFUhPYQD";
	private static final String BITCOIND_PRIVATE_KEY = "cQ57cLoFkYRSAZJGkYMc8cTCoJhaQVEqSYNuVuUySzuLATFQ4Vcr";
	private static final String BITCOIND_ALERT = "bitcoin daemon test alert";
	private static BtcDaemon BITCOIND_WITHOUT_LISTENER;
	private static BtcDaemon BITCOIND_WITH_LISTENER;
	private static List<String> ALERT_NOTIFICATIONS = new Vector<String>();
	private static List<BtcBlock> BLOCK_NOTIFICATIONS = new Vector<BtcBlock>();
	private static List<BtcTransaction> WALLET_NOTIFICATIONS = new Vector<BtcTransaction>();

	@BeforeClass
	public static void init() throws Exception {
		BITCOIND_WITHOUT_LISTENER = new BtcDaemon(new URL(BITCOIND_URL), BITCOIND_ACCOUNT,
				BITCOIND_PASSWD, BITCOIND_TIMEOUT);
		BITCOIND_WITH_LISTENER = new BtcDaemon(new URL(BITCOIND_URL), BITCOIND_ACCOUNT,
				BITCOIND_PASSWD, BITCOIND_TIMEOUT, BITCOIND_ALERT_PORT,
				BITCOIND_BLOCK_PORT, BITCOIND_WALLET_PORT);
		BITCOIND_WITH_LISTENER.getAlertListener().addObserver(new Observer() {
			@Override
			public void update(Observable o, Object obj) {
				ALERT_NOTIFICATIONS.add(String.valueOf(obj));
			}
		});
		BITCOIND_WITH_LISTENER.getBlockListener().addObserver(new Observer() {
			@Override
			public void update(Observable o, Object obj) {
				if (obj instanceof BtcBlock) {
					BLOCK_NOTIFICATIONS.add((BtcBlock) obj);
				}
			}
		});
		BITCOIND_WITH_LISTENER.getWalletListener().addObserver(new Observer() {
			@Override
			public void update(Observable o, Object obj) {
				if (obj instanceof BtcTransaction) {
					WALLET_NOTIFICATIONS.add((BtcTransaction) obj);
				}
			}
		});
	}

	@AfterClass
	public static void cleanUp() throws Exception {
		for (String alert : ALERT_NOTIFICATIONS) {
			System.out.println("alert: " + alert);
		}
		for (BtcBlock block : BLOCK_NOTIFICATIONS) {
			System.out.println("block: " + block);
		}
		for (BtcTransaction transaction : WALLET_NOTIFICATIONS) {
			System.out.println("transaction: " + transaction);
		}
		assertTrue(ALERT_NOTIFICATIONS.size() > 0);
		assertEquals(BITCOIND_ALERT, ALERT_NOTIFICATIONS.get(0));
		assertTrue(BLOCK_NOTIFICATIONS.size() > 0);
		assertTrue(WALLET_NOTIFICATIONS.size() > 0);
		BITCOIND_WITH_LISTENER.stopListening();
		if (BITCOIND_STOP) {
			String stop = BITCOIND_WITHOUT_LISTENER.stop();
			assertNotNull(stop);
			assertTrue(stop.length() >= 0);
		}
	}
	
	@Test
	public void daemonNotifier() throws BtcException {
		BtcDaemonNotifier.notify(BITCOIND_HOST, BITCOIND_BLOCK_PORT, "000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943");
		BtcDaemonNotifier.notify(BITCOIND_HOST, BITCOIND_WALLET_PORT, "5cfc16d9937e4ad36686b829942b0a7ab088750bc3008a71cd0a13ccf4ac1099");
		try {
			Thread.sleep(BITCOIND_NOTIFICATION_SLEEP);
		} catch (InterruptedException e) {
		}
		BtcDaemonNotifier.notify(BITCOIND_HOST, BITCOIND_ALERT_PORT, BITCOIND_ALERT);
	}

	@Test(expected = BtcException.class)
	public void addMultiSignatureAddress() throws BtcException {
		BITCOIND_WITH_LISTENER.addMultiSignatureAddress(0, new ArrayList<String>(), "");
	}

	@Test(expected = BtcException.class)
	public void addNode() throws BtcException {
		BITCOIND_WITH_LISTENER.addNode("", BtcNodeOperation.ADD);
	}

	@Test
	public void backupWallet() throws BtcException {
		File wallet = new File(BITCOIND_DIR + "/" + BITCOIND_WALLET);
		if (wallet.exists()) {
			wallet.delete();
		}
		BITCOIND_WITH_LISTENER.backupWallet(new File(BITCOIND_DIR));
		assertTrue(wallet.exists());
	}

	@Test(expected = BtcException.class)
	public void createMultiSignatureAddress() throws BtcException {
		BITCOIND_WITH_LISTENER.createMultiSignatureAddress(0, new ArrayList<String>());
	}

	@Test(expected = BtcException.class)
	public void createRawTransaction() throws BtcException {
		BITCOIND_WITH_LISTENER.createRawTransaction(new ArrayList<Object>(),
				new ArrayList<Object>());
	}

	@Test(expected = BtcException.class)
	public void decodeRawTransaction() throws BtcException {
		BITCOIND_WITH_LISTENER.decodeRawTransaction("");
	}

	@Test
	public void dumpPrivateKey() throws BtcException {
		String privateKey = BITCOIND_WITH_LISTENER.dumpPrivateKey(BITCOIND_ADDRESS);
		assertNotNull(privateKey);
		assertTrue(privateKey.length() >= 0);
		assertEquals(BITCOIND_PRIVATE_KEY, privateKey);
	}

	@Test(expected = BtcException.class)
	public void encryptWallet() throws BtcException {
		BITCOIND_WITH_LISTENER.encryptWallet("");
	}

	@Test
	public void getAccount() throws BtcException {
		String account = BITCOIND_WITH_LISTENER.getAccount(BITCOIND_ADDRESS);
		assertNotNull(account);
	}

	@Test
	public void getAccountAddress() throws BtcException {
		String address = BITCOIND_WITH_LISTENER.getAccountAddress(BITCOIND_ACCOUNT);
		assertNotNull(address);
	}

	@Test(expected = BtcException.class)
	public void getAddedNodeInformation() throws BtcException {
		BITCOIND_WITH_LISTENER.getAddedNodeInformation(false, "");
	}

	@Test
	public void getAddressesByAccount() throws BtcException {
		List<String> addresses = BITCOIND_WITH_LISTENER
				.getAddressesByAccount(BITCOIND_ACCOUNT);
		assertNotNull(addresses);
		assertTrue(addresses.size() >= 0);
	}

	@Test
	public void getBalance() throws BtcException {
		double balance = BITCOIND_WITH_LISTENER.getBalance("", -1);
		assertTrue(balance >= 0);
		balance = BITCOIND_WITH_LISTENER.getBalance(BITCOIND_ACCOUNT, 2);
		assertTrue(balance >= 0);
	}

	@Test
	public void getBlock() throws BtcException {
		BtcBlock block = BITCOIND_WITH_LISTENER
				.getBlock("000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943");
		assertNotNull(block);
		assertEquals(
				"000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943",
				block.getHash());
		assertEquals(0, block.getHeight());
		assertEquals(1, block.getVersion());
		block = BITCOIND_WITHOUT_LISTENER
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
		int blocks = BITCOIND_WITH_LISTENER.getBlockCount();
		assertTrue(blocks >= 0);
	}

	@Test
	public void getBlockHash() throws BtcException {
		String hash = BITCOIND_WITH_LISTENER.getBlockHash(-1);
		assertNotNull(hash);
		assertTrue(hash.length() >= 0);
	}

	@Test(expected = BtcException.class)
	public void getBlockTemplate() throws BtcException {
		BITCOIND_WITH_LISTENER.getBlockTemplate("");
	}

	@Test
	public void getConnectionCount() throws BtcException {
		int connections = BITCOIND_WITH_LISTENER.getConnectionCount();
		assertTrue(connections >= 0);
	}

	@Test
	public void getDifficulty() throws BtcException {
		double difficulty = BITCOIND_WITH_LISTENER.getDifficulty();
		assertTrue(difficulty >= 0);
	}

	@Test
	public void getGenerate() throws BtcException {
		boolean generate = BITCOIND_WITH_LISTENER.getGenerate();
		assertTrue(generate || true);
	}

	@Test
	public void getHashesPerSecond() throws BtcException {
		int hashesPerSec = BITCOIND_WITH_LISTENER.getHashesPerSecond();
		assertTrue(hashesPerSec >= 0);
	}

	@Test
	public void getInformation() throws BtcException {
		BtcStatus info = BITCOIND_WITH_LISTENER.getInformation();
		assertNotNull(info);
		assertTrue(info.isTestnet());
		assertEquals(80600, info.getVersion());
	}

	@Test
	public void getMiningInformation() throws BtcException {
		BtcMining info = BITCOIND_WITH_LISTENER.getMiningInformation();
		assertNotNull(info);
		assertTrue(info.isTestnet());
		assertTrue(info.getDifficulty() >= 0);
	}

	@Ignore("Generates new address")
	@Test
	public void getNewAddress() throws BtcException {
		String address = BITCOIND_WITH_LISTENER.getNewAddress(BITCOIND_ACCOUNT);
		assertNotNull(address);
		address = BITCOIND_WITHOUT_LISTENER.getNewAddress();
		assertNotNull(address);
	}

	@Test
	public void getPeerInformation() throws BtcException {
		List<BtcPeer> peers = BITCOIND_WITH_LISTENER.getPeerInformation();
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
		List<String> rawMemPool = BITCOIND_WITH_LISTENER.getRawMemoryPool();
		assertNotNull(rawMemPool);
		assertTrue(rawMemPool.size() >= 0);
	}

	@Test(expected = BtcException.class)
	public void getRawTransaction() throws BtcException {
		BITCOIND_WITH_LISTENER.getRawTransaction("", false);
	}

	@Test
	public void getReceivedByAccount() throws BtcException {
		double balance = BITCOIND_WITH_LISTENER.getReceivedByAccount("");
		assertTrue(balance >= 0);
		balance = BITCOIND_WITHOUT_LISTENER.getReceivedByAccount(BITCOIND_ACCOUNT, 2);
		assertTrue(balance >= 0);
	}

	@Test
	public void getReceivedByAddress() throws BtcException {
		double balance = BITCOIND_WITH_LISTENER.getReceivedByAddress(BITCOIND_ADDRESS);
		assertTrue(balance >= 0);
	}

	@Test
	public void getTransaction() throws BtcException {
		BtcTransaction transaction = BITCOIND_WITH_LISTENER
				.getTransaction("5cfc16d9937e4ad36686b829942b0a7ab088750bc3008a71cd0a13ccf4ac1099");
		assertNotNull(transaction);
		List<BtcTransactionDetail> details = transaction.getDetails();
		assertNotNull(details);
		transaction = BITCOIND_WITHOUT_LISTENER
				.getTransaction("98dafef582ae53e8af30e8ad09577bbd472d4bf24a173121d58a5900747fd082");
		assertNotNull(transaction);
		details = transaction.getDetails();
		assertNotNull(details);
	}

	@Test(expected = BtcException.class)
	public void getTransactionOutput() throws BtcException {
		BITCOIND_WITH_LISTENER.getTransactionOutput("", 0, false);
	}

	@Test
	public void getTransactionOutputSetInformation() throws BtcException {
		BtcTransactionOutputSet txOutputSet = BITCOIND_WITH_LISTENER
				.getTransactionOutputSetInformation();
		assertNotNull(txOutputSet);
		assertTrue(txOutputSet.getHeight() >= 0);
	}

	@Test(expected = BtcException.class)
	public void getWork() throws BtcException {
		BITCOIND_WITH_LISTENER.getWork("");
	}

	@Test
	public void help() throws BtcException {
		String help = BITCOIND_WITH_LISTENER.help();
		assertNotNull(help);
		assertTrue(help.length() >= 0);
		help = BITCOIND_WITH_LISTENER.help("fakecommand");
		assertNotNull(help);
		assertTrue(help.length() >= 0);
		help = BITCOIND_WITHOUT_LISTENER.help("walletlock");
		assertNotNull(help);
		assertTrue(help.length() >= 0);
		System.out.println(help);
	}

	@Ignore("need wallet password")
	@Test
	public void importPrivateKey() throws BtcException {
		// walletpassphrase <passphrase> <timeout>
		BITCOIND_WITH_LISTENER.importPrivateKey(BITCOIND_PRIVATE_KEY, "", false);
		// walletlock
	}

	@Test(expected = BtcException.class)
	public void keyPoolRefill() throws BtcException {
		BITCOIND_WITH_LISTENER.keyPoolRefill();
	}

	@Test
	public void listAccounts() throws BtcException {
		List<BtcAccount> accounts = BITCOIND_WITH_LISTENER.listAccounts();
		assertNotNull(accounts);
		assertTrue(accounts.size() > 0);
		BtcAccount account = accounts.get(0);
		assertNotNull(account);
		assertNotNull(account.getAccount());
	}

	@Test
	public void listAddressGroupings() throws BtcException {
		List<BtcAddress> addresses = BITCOIND_WITH_LISTENER.listAddressGroupings();
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
		List<String> unspents = BITCOIND_WITH_LISTENER.listLockUnspent();
		assertNotNull(unspents);
	}

	@Test
	public void listReceivedByAccount() throws BtcException {
		List<BtcAccount> accounts = BITCOIND_WITH_LISTENER.listReceivedByAccount();
		assertNotNull(accounts);
		accounts = BITCOIND_WITH_LISTENER.listReceivedByAccount(0, true);
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
		List<BtcAddress> addresses = BITCOIND_WITH_LISTENER.listReceivedByAddress();
		assertNotNull(addresses);
		addresses = BITCOIND_WITH_LISTENER.listReceivedByAddress(0, true);
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
		BtcLastBlock lastBlock = BITCOIND_WITH_LISTENER.listSinceBlock();
		assertNotNull(lastBlock);
		lastBlock = BITCOIND_WITH_LISTENER
				.listSinceBlock(
						"00000000b873e79784647a6c82962c70d228557d24a747ea4d1b8bbe878e1206",
						0);
		assertNotNull(lastBlock);
	}

	@Test
	public void listTransactions() throws BtcException {
		List<BtcTransaction> transactions = BITCOIND_WITH_LISTENER
				.listTransactions(BITCOIND_ACCOUNT);
		assertNotNull(transactions);
		transactions = BITCOIND_WITH_LISTENER.listTransactions();
		assertNotNull(transactions);
	}

	@Test(expected = BtcException.class)
	public void listUnspent() throws BtcException {
		BITCOIND_WITH_LISTENER.listUnspent(0, 0);
	}

	@Test(expected = BtcException.class)
	public void lockUnspent() throws BtcException {
		BITCOIND_WITH_LISTENER.lockUnspent(false, new ArrayList<Object>());
	}

	@Test(expected = BtcException.class)
	public void move() throws BtcException {
		BITCOIND_WITH_LISTENER.move("", "", 0, 0, "");
	}

	@Test(expected = BtcException.class)
	public void sendFrom() throws BtcException {
		BITCOIND_WITH_LISTENER.sendFrom("", "", 0, 0, "", "");
	}

	@Test(expected = BtcException.class)
	public void sendMany() throws BtcException {
		BITCOIND_WITH_LISTENER.sendMany("", new ArrayList<Object>(), 0, "", "");
	}

	@Test(expected = BtcException.class)
	public void sendRawTransaction() throws BtcException {
		BITCOIND_WITH_LISTENER.sendRawTransaction("");
	}

	@Test(expected = BtcException.class)
	public void sendToAddress() throws BtcException {
		BITCOIND_WITH_LISTENER.sendToAddress("", 0, "", "");
	}

	@Ignore("Generates new address")
	@Test
	public void setAccount() throws BtcException {
		BITCOIND_WITH_LISTENER.setAccount(BITCOIND_ADDRESS, BITCOIND_ACCOUNT);
		List<String> addresses = BITCOIND_WITH_LISTENER
				.getAddressesByAccount(BITCOIND_ACCOUNT);
		assertNotNull(addresses);
		assertTrue(addresses.size() >= 0);
	}

	@Test
	public void setGenerate() throws BtcException {
		boolean generate = false;
		BITCOIND_WITH_LISTENER.setGenerate(generate);
		assertFalse(BITCOIND_WITH_LISTENER.getGenerate());
		generate = true;
		BITCOIND_WITH_LISTENER.setGenerate(generate, 1);
		assertTrue(BITCOIND_WITH_LISTENER.getGenerate());
	}

	@Test
	public void setTransactionFee() throws BtcException {
		assertTrue(BITCOIND_WITH_LISTENER.setTransactionFee(0.00000001));
		assertTrue(BITCOIND_WITH_LISTENER.setTransactionFee(-1));
	}

	@Test(expected = BtcException.class)
	public void signMessage() throws BtcException {
		BITCOIND_WITH_LISTENER.signMessage("", "");
	}

	@Test(expected = BtcException.class)
	public void signRawTransaction() throws BtcException {
		BITCOIND_WITH_LISTENER.signRawTransaction("", new ArrayList<Object>(),
				new ArrayList<String>());
	}

	@Test(expected = BtcException.class)
	public void submitBlock() throws BtcException {
		BITCOIND_WITH_LISTENER.submitBlock("", new ArrayList<Object>());
	}

	@Test
	public void validateAddress() throws BtcException {
		BtcAddress address = BITCOIND_WITH_LISTENER.validateAddress(BITCOIND_ADDRESS);
		assertNotNull(address);
		assertTrue(address.isValid());
		BtcAccount account = address.getAccount();
		assertNotNull(account);
		assertNotNull(account.getAccount());
		address = BITCOIND_WITH_LISTENER.validateAddress("bad address");
		assertNotNull(address);
		assertFalse(address.isValid());
	}

	@Test(expected = BtcException.class)
	public void verifyMessage() throws BtcException {
		BITCOIND_WITH_LISTENER.verifyMessage("", "", "");
	}
}