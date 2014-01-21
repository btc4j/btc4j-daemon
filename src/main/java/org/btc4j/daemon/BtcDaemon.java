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

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.btc4j.core.BtcAccount;
import org.btc4j.core.BtcAddedNode;
import org.btc4j.core.BtcAddress;
import org.btc4j.core.BtcApi;
import org.btc4j.core.BtcBlock;
import org.btc4j.core.BtcException;
import org.btc4j.core.BtcLastBlock;
import org.btc4j.core.BtcMiningInfo;
import org.btc4j.core.BtcMultiSignatureAddress;
import org.btc4j.core.BtcNode;
import org.btc4j.core.BtcPeer;
import org.btc4j.core.BtcInfo;
import org.btc4j.core.BtcRawTransaction;
import org.btc4j.core.BtcTransaction;
import org.btc4j.core.BtcTransactionOutputSet;
import org.btc4j.core.BtcUtil;

public class BtcDaemon extends BtcJsonRpcHttpClient implements BtcApi {
	private static final String BTCAPI_ADD_MULTI_SIGNATURE_ADDRESS = "addmultisigaddress";
	private static final String BTCAPI_ADD_NODE = "addnode";
	private static final String BTCAPI_BACKUP_WALLET = "backupwallet";
	private static final String BTCAPI_CREATE_MULTI_SIGNATURE_ADDRESS = "createmultisig";
	// private static final String BTCAPI_CREATE_RAW_TRANSACTION = "createrawtransaction";
	private static final String BTCAPI_DECODE_RAW_TRANSACTION = "decoderawtransaction";
	private static final String BTCAPI_DUMP_PRIVATE_KEY = "dumpprivkey";
	private static final String BTCAPI_GET_ACCOUNT = "getaccount";
	private static final String BTCAPI_GET_ACCOUNT_ADDRESS = "getaccountaddress";
	private static final String BTCAPI_GET_ADDED_NODE_INFORMATION = "getaddednodeinfo";
	private static final String BTCAPI_GET_ADDRESSES_BY_ACCOUNT = "getaddressesbyaccount";
	private static final String BTCAPI_GET_BALANCE = "getbalance";
	private static final String BTCAPI_GET_BLOCK = "getblock";
	private static final String BTCAPI_GET_BLOCK_COUNT = "getblockcount";
	private static final String BTCAPI_GET_BLOCK_HASH = "getblockhash";
	// private static final String BTCAPI_GET_BLOCK_TEMPLATE = "getblocktemplate";
	private static final String BTCAPI_GET_CONNECTION_COUNT = "getconnectioncount";
	private static final String BTCAPI_GET_DIFFICULTY = "getdifficulty";
	private static final String BTCAPI_GET_GENERATE = "getgenerate";
	private static final String BTCAPI_GET_HASHES_PER_SECOND = "gethashespersec";
	private static final String BTCAPI_GET_INFORMATION = "getinfo";
	private static final String BTCAPI_GET_MINING_INFORMATION = "getmininginfo";
	private static final String BTCAPI_GET_NEW_ADDRESS = "getnewaddress";
	private static final String BTCAPI_GET_PEER_INFORMATION = "getpeerinfo";
	private static final String BTCAPI_GET_RAW_MEMORY_POOL = "getrawmempool";
	private static final String BTCAPI_GET_RAW_TRANSACTION = "getrawtransaction";
	private static final String BTCAPI_GET_RECEIVED_BY_ACCOUNT = "getreceivedbyaccount";
	private static final String BTCAPI_GET_RECEIVED_BY_ADDRESS = "getreceivedbyaddress";
	private static final String BTCAPI_GET_TRANSACTION = "gettransaction";
	private static final String BTCAPI_GET_TRANSACTION_OUTPUT = "gettxout";
	private static final String BTCAPI_GET_TRANSACTION_OUTPUT_SET_INFORMATION = "gettxoutsetinfo";
	// private static final String BTCAPI_GET_WORK = "getwork";
	private static final String BTCAPI_HELP = "help";
	private static final String BTCAPI_IMPORT_PRIVATE_KEY = "importprivkey";
	private static final String BTCAPI_KEY_POOL_REFILL = "keypoolrefill";
	private static final String BTCAPI_LIST_ACCOUNTS = "listaccounts";
	private static final String BTCAPI_LIST_ADDRESS_GROUPINGS = "listaddressgroupings";
	private static final String BTCAPI_LIST_LOCK_UNSPENT = "listlockunspent";
	private static final String BTCAPI_LIST_RECEIVED_BY_ACCOUNT = "listreceivedbyaccount";
	private static final String BTCAPI_LIST_RECEIVED_BY_ADDRESS = "listreceivedbyaddress";
	private static final String BTCAPI_LIST_SINCE_BLOCK = "listsinceblock";
	private static final String BTCAPI_LIST_TRANSACTIONS = "listtransactions";
	// private static final String BTCAPI_LIST_UNSPENT = "listunspent";
	// private static final String BTCAPI_LOCK_UNSPENT = "lockunspent";
	private static final String BTCAPI_MOVE = "move";
	private static final String BTCAPI_SEND_FROM = "sendfrom";
	private static final String BTCAPI_SEND_MANY = "sendmany";
	// private static final String BTCAPI_SEND_RAW_TRANSACTION = "sendrawtransaction";
	private static final String BTCAPI_SEND_TO_ADDRESS = "sendtoaddress";
	private static final String BTCAPI_SET_ACCOUNT = "setaccount";
	private static final String BTCAPI_SET_GENERATE = "setgenerate";
	private static final String BTCAPI_SET_TRANSACTION_FEE = "settxfee";
	private static final String BTCAPI_SIGN_MESSAGE = "signmessage";
	// private static final String BTCAPI_SIGN_RAW_TRANSACTION = "signrawtransaction";
	private static final String BTCAPI_STOP = "stop";
	// private static final String BTCAPI_SUBMIT_BLOCK = "submitblock";
	private static final String BTCAPI_VALIDATE_ADDRESS = "validateaddress";
	private static final String BTCAPI_VERIFY_MESSAGE = "verifymessage";
	private static final String BTCAPI_WALLET_LOCK = "walletlock";
	private static final String BTCAPI_WALLET_PASSPHRASE = "walletpassphrase";
	private static final String BTCAPI_WALLET_PASSPHRASE_CHANGE = "walletpassphrasechange";
	private static final String[] BTC4J_DAEMON_VERSIONS = { "0.8.6" };
	private BtcAlertListener alertListener;
	private Thread alertThread;
	private BtcBlockListener blockListener;
	private Thread blockThread;
	private BtcWalletListener walletListener;
	private Thread walletThread;

	public BtcDaemon(URL url, String account, String password, int timeout) {
		super(url, account, password, timeout);
	}
	
	public BtcDaemon(URL url, String account, String password) {
		super(url, account, password);
	}

	public BtcDaemon(URL url, String account, String password, int timeout,
			int alertPort, int blockPort, int walletPort) {
		this(url, account, password, timeout);
		alertListener = new BtcAlertListener(alertPort);
		alertThread = new Thread(alertListener, "alertListener");
		alertThread.start();
		blockListener = new BtcBlockListener(blockPort, this);
		blockThread = new Thread(blockListener, "blockListener");
		blockThread.start();
		walletListener = new BtcWalletListener(walletPort, this);
		walletThread = new Thread(walletListener, "walletListener");
		walletThread.start();
	}

	public String[] getSupportedVersions() {
		return BTC4J_DAEMON_VERSIONS;
	}

	public BtcAlertListener getAlertListener() {
		return alertListener;
	}

	public BtcBlockListener getBlockListener() {
		return blockListener;
	}

	public BtcWalletListener getWalletListener() {
		return walletListener;
	}

	public void stopListening() {
		if ((alertListener != null) && (alertThread != null)) {
			alertThread.interrupt();
		}
		if ((blockListener != null) && (blockThread != null)) {
			blockThread.interrupt();
		}
		if ((walletListener != null) && (walletThread != null)) {
			walletThread.interrupt();
		}
	}

	public String addMultiSignatureAddress(long required, List<String> keys)
			throws BtcException {
		return addMultiSignatureAddress(required, keys, "");
	}

	@Override
	public String addMultiSignatureAddress(long required, List<String> keys,
			String account) throws BtcException {
		if (required < 1) {
			required = 1;
		}
		if (keys == null) {
			keys = new ArrayList<String>();
		}
		if (required > keys.size()) {
			required = keys.size();
		}
		JsonArrayBuilder keyParams = Json.createArrayBuilder();
		for (String key : keys) {
			keyParams.add(BtcUtil.notNull(key));
		}
		JsonArray parameters = Json.createArrayBuilder().add(required)
				.add(keyParams).add(BtcUtil.notNull(account)).build();
		JsonString results = (JsonString) invoke(
				BTCAPI_ADD_MULTI_SIGNATURE_ADDRESS, parameters);
		return results.getString();
	}

	@Override
	public void addNode(String node, BtcNode.Operation operation)
			throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(node))
				.add(String.valueOf(operation).toLowerCase()).build();
		invoke(BTCAPI_ADD_NODE, parameters);
	}

	@Override
	public void backupWallet(File destination) throws BtcException {
		if (destination == null) {
			destination = new File(".");
		}
		JsonArray parameters = Json.createArrayBuilder()
				.add(destination.toString()).build();
		invoke(BTCAPI_BACKUP_WALLET, parameters);
	}

	@Override
	public BtcMultiSignatureAddress createMultiSignatureAddress(long required,
			List<String> keys) throws BtcException {
		if (required < 1) {
			required = 1;
		}
		if (keys == null) {
			keys = new ArrayList<String>();
		}
		if (required > keys.size()) {
			required = keys.size();
		}
		JsonArrayBuilder keyParams = Json.createArrayBuilder();
		for (String key : keys) {
			keyParams.add(BtcUtil.notNull(key));
		}
		JsonArray parameters = Json.createArrayBuilder().add(required)
				.add(keyParams).build();
		JsonObject results = (JsonObject) invoke(
				BTCAPI_CREATE_MULTI_SIGNATURE_ADDRESS, parameters);
		return jsonMultiSignatureAddress(results);
	}

	@Override
	public String createRawTransaction(List<Object> transactionIds,
			List<Object> addresses) throws BtcException {
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BtcException.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public BtcRawTransaction decodeRawTransaction(String hex)
			throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(hex)).build();
		JsonObject results = (JsonObject) invoke(BTCAPI_DECODE_RAW_TRANSACTION,
				parameters);
		return jsonRawTransaction(results);
	}

	@Override
	public String dumpPrivateKey(String address) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(address)).build();
		JsonString results = (JsonString) invoke(BTCAPI_DUMP_PRIVATE_KEY,
				parameters);
		return results.getString();
	}

	@Override
	public String getAccount(String address) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(address)).build();
		JsonString results = (JsonString) invoke(BTCAPI_GET_ACCOUNT, parameters);
		return results.getString();
	}

	@Override
	public String getAccountAddress(String account) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(account)).build();
		JsonString results = (JsonString) invoke(BTCAPI_GET_ACCOUNT_ADDRESS,
				parameters);
		return results.getString();
	}

	public List<BtcAddedNode> getAddedNodeInformation(boolean dns)
			throws BtcException {
		return getAddedNodeInformation(dns, "");
	}

	@Override
	public List<BtcAddedNode> getAddedNodeInformation(boolean dns, String node)
			throws BtcException {
		JsonArrayBuilder builder = Json.createArrayBuilder().add(dns);
		if ((node != null) && (node.length() > 0)) {
			builder.add(node);
		}
		JsonArray parameters = builder.build();
		List<BtcAddedNode> addedNodes = new ArrayList<BtcAddedNode>();
		if (dns) {
			JsonArray results = (JsonArray) invoke(
					BTCAPI_GET_ADDED_NODE_INFORMATION, parameters);
			for (JsonObject result : results.getValuesAs(JsonObject.class)) {
				addedNodes.add(jsonAddedNode(result));
			}
		} else {
			JsonObject results = (JsonObject) invoke(
					BTCAPI_GET_ADDED_NODE_INFORMATION, parameters);
			addedNodes.add(jsonAddedNode(results));
		}
		return addedNodes;
	}

	@Override
	public List<String> getAddressesByAccount(String account)
			throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(account)).build();
		JsonArray results = (JsonArray) invoke(BTCAPI_GET_ADDRESSES_BY_ACCOUNT,
				parameters);
		List<String> addresses = new ArrayList<String>();
		for (JsonString result : results.getValuesAs(JsonString.class)) {
			addresses.add(result.getString());
		}
		return addresses;
	}

	public BigDecimal getBalance() throws BtcException {
		return getBalance("", 1);
	}

	public BigDecimal getBalance(long minConfirms) throws BtcException {
		return getBalance("", minConfirms);
	}

	public BigDecimal getBalance(String account) throws BtcException {
		return getBalance(account, 1);
	}

	@Override
	public BigDecimal getBalance(String account, long minConfirms)
			throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(account)).add(BtcUtil.atLeast(minConfirms, 1))
				.build();
		JsonNumber results = (JsonNumber) invoke(BTCAPI_GET_BALANCE, parameters);
		return results.bigDecimalValue();
	}

	@Override
	public BtcBlock getBlock(String hash) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(hash)).build();
		JsonObject results = (JsonObject) invoke(BTCAPI_GET_BLOCK, parameters);
		return jsonBlock(results);
	}

	@Override
	public long getBlockCount() throws BtcException {
		JsonNumber results = (JsonNumber) invoke(BTCAPI_GET_BLOCK_COUNT);
		return results.longValue();
	}

	@Override
	public String getBlockHash(long index) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder().add(BtcUtil.atLeast(index, 0))
				.build();
		JsonString results = (JsonString) invoke(BTCAPI_GET_BLOCK_HASH,
				parameters);
		return results.getString();
	}

	@Override
	public String getBlockTemplate(String params) throws BtcException {
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BtcException.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public long getConnectionCount() throws BtcException {
		JsonNumber results = (JsonNumber) invoke(BTCAPI_GET_CONNECTION_COUNT);
		return results.longValue();
	}

	@Override
	public BigDecimal getDifficulty() throws BtcException {
		JsonNumber results = (JsonNumber) invoke(BTCAPI_GET_DIFFICULTY);
		return results.bigDecimalValue();
	}

	@Override
	public boolean getGenerate() throws BtcException {
		JsonValue results = invoke(BTCAPI_GET_GENERATE);
		return Boolean.valueOf(String.valueOf(results));
	}

	@Override
	public long getHashesPerSecond() throws BtcException {
		JsonNumber results = (JsonNumber) invoke(BTCAPI_GET_HASHES_PER_SECOND);
		return results.longValue();
	}

	@Override
	public BtcInfo getInformation() throws BtcException {
		JsonObject results = (JsonObject) invoke(BTCAPI_GET_INFORMATION);
		return jsonInfo(results);
	}

	@Override
	public BtcMiningInfo getMiningInformation() throws BtcException {
		JsonObject results = (JsonObject) invoke(BTCAPI_GET_MINING_INFORMATION);
		return jsonMiningInfo(results);
	}

	public String getNewAddress() throws BtcException {
		return getNewAddress("");
	}

	@Override
	public String getNewAddress(String account) throws BtcException {
		JsonArray parameters = null;
		if ((account != null) && (account.length() > 0)) {
			parameters = Json.createArrayBuilder().add(account).build();
		}
		JsonString results = (JsonString) invoke(BTCAPI_GET_NEW_ADDRESS,
				parameters);
		return results.getString();
	}

	@Override
	public List<BtcPeer> getPeerInformation() throws BtcException {
		JsonArray results = (JsonArray) invoke(BTCAPI_GET_PEER_INFORMATION);
		List<BtcPeer> peers = new ArrayList<BtcPeer>();
		for (JsonObject result : results.getValuesAs(JsonObject.class)) {
			peers.add(jsonPeer(result));
		}
		return peers;
	}

	@Override
	public List<String> getRawMemoryPool() throws BtcException {
		JsonArray results = (JsonArray) invoke(BTCAPI_GET_RAW_MEMORY_POOL);
		List<String> rawMemPool = new ArrayList<String>();
		for (JsonString result : results.getValuesAs(JsonString.class)) {
			rawMemPool.add(result.getString());
		}
		return rawMemPool;
	}

	public BtcRawTransaction getRawTransaction(String transactionId)
			throws BtcException {
		return getRawTransaction(transactionId, false);
	}

	@Override
	public BtcRawTransaction getRawTransaction(String transactionId,
			boolean verbose) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(transactionId)).add(BtcUtil.bool(verbose)).build();
		BtcRawTransaction transaction;
		if (verbose) {
			JsonObject results = (JsonObject) invoke(
					BTCAPI_GET_RAW_TRANSACTION, parameters);
			transaction = jsonRawTransaction(results);
		} else {
			JsonString results = (JsonString) invoke(
					BTCAPI_GET_RAW_TRANSACTION, parameters);
			transaction = new BtcRawTransaction();
			transaction.setHex(results.getString());
		}
		return transaction;
	}

	public BigDecimal getReceivedByAccount() throws BtcException {
		return getReceivedByAccount("", 1);
	}
	
	public BigDecimal getReceivedByAccount(String account) throws BtcException {
		return getReceivedByAccount(account, 1);
	}

	@Override
	public BigDecimal getReceivedByAccount(String account, long minConfirms)
			throws BtcException {
		JsonArray parameters = Json.createArrayBuilder().add(BtcUtil.notNull(account)).add(BtcUtil.atLeast(minConfirms, 1)).build();
		JsonNumber results = (JsonNumber) invoke(
				BTCAPI_GET_RECEIVED_BY_ACCOUNT, parameters);
		return results.bigDecimalValue();
	}

	public BigDecimal getReceivedByAddress(String address) throws BtcException {
		return getReceivedByAddress(address, 1);
	}

	@Override
	public BigDecimal getReceivedByAddress(String address, long minConfirms)
			throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(address)).add(BtcUtil.atLeast(minConfirms, 1))
				.build();
		JsonNumber results = (JsonNumber) invoke(
				BTCAPI_GET_RECEIVED_BY_ADDRESS, parameters);
		return results.bigDecimalValue();
	}

	@Override
	public BtcTransaction getTransaction(String transactionId)
			throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(transactionId)).build();
		JsonObject results = (JsonObject) invoke(BTCAPI_GET_TRANSACTION,
				parameters);
		return jsonTransaction(results);
	}

	public String getTransactionOutput(String transactionId,
			boolean includeMemoryPool) throws BtcException {
		return getTransactionOutput(transactionId, 0, includeMemoryPool);
	}

	@Override
	// TODO
	public String getTransactionOutput(String transactionId, long index,
			boolean includeMemoryPool) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(transactionId)).add(BtcUtil.atLeast(index, 0))
				.add(includeMemoryPool).build();
		JsonValue results = invoke(BTCAPI_GET_TRANSACTION_OUTPUT, parameters);
		return String.valueOf(results);
	}

	@Override
	public BtcTransactionOutputSet getTransactionOutputSetInformation()
			throws BtcException {
		JsonObject results = (JsonObject) invoke(BTCAPI_GET_TRANSACTION_OUTPUT_SET_INFORMATION);
		return jsonTransactionOutputSet(results);
	}

	@Override
	public String getWork(String data) throws BtcException {
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BtcException.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	public String help() throws BtcException {
		return help("");
	}

	@Override
	public String help(String command) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(command)).build();
		JsonString results = (JsonString) invoke(BTCAPI_HELP, parameters);
		return results.getString();
	}

	public String importPrivateKey(String privateKey) throws BtcException {
		return importPrivateKey(privateKey, "", true);
	}

	@Override
	public String importPrivateKey(String privateKey, String label,
			boolean reScan) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(privateKey)).add(BtcUtil.notNull(label))
				.add(reScan).build();
		JsonValue results = invoke(BTCAPI_IMPORT_PRIVATE_KEY, parameters);
		return String.valueOf(results);
	}

	@Override
	public void keyPoolRefill() throws BtcException {
		invoke(BTCAPI_KEY_POOL_REFILL);
	}

	public Map<String, BtcAccount> listAccounts() throws BtcException {
		return listAccounts(1);
	}

	@Override
	public Map<String, BtcAccount> listAccounts(long minConfirms)
			throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.atLeast(minConfirms, 1)).build();
		JsonObject results = (JsonObject) invoke(BTCAPI_LIST_ACCOUNTS,
				parameters);
		Map<String, BtcAccount> accounts = new HashMap<String, BtcAccount>();
		for (String key : results.keySet()) {
			BtcAccount account = new BtcAccount();
			account.setAccount(key);
			account.setAmount(jsonDouble(results, key));
			accounts.put(key, account);
		}
		return accounts;
	}

	@Override
	public List<BtcAddress> listAddressGroupings() throws BtcException {
		JsonArray results = (JsonArray) invoke(BTCAPI_LIST_ADDRESS_GROUPINGS);
		List<BtcAddress> addresses = new ArrayList<BtcAddress>();
		for (JsonArray groupings : results.getValuesAs(JsonArray.class)) {
			for (JsonArray grouping : groupings.getValuesAs(JsonArray.class)) {
				BtcAddress address = new BtcAddress();
				address.setAddress(grouping.getString(0));
				JsonNumber amount = grouping.getJsonNumber(1);
				if (amount != null) {
					address.setAmount(amount.bigDecimalValue());
				}
				BtcAccount account = new BtcAccount();
				account.setAccount((grouping.size() > 2) ? grouping.getString(2) : "");
				address.setAccount(account);
				addresses.add(address);
			}
		}
		return addresses;
	}

	@Override
	public List<String> listLockUnspent() throws BtcException {
		JsonArray results = (JsonArray) invoke(BTCAPI_LIST_LOCK_UNSPENT);
		List<String> unspents = new ArrayList<String>();
		for (JsonObject unspent : results.getValuesAs(JsonObject.class)) {
			unspents.add(String.valueOf(unspent));
		}
		return unspents;
	}

	public List<BtcAccount> listReceivedByAccount() throws BtcException {
		return listReceivedByAccount(1, false);
	}

	@Override
	public List<BtcAccount> listReceivedByAccount(long minConfirms,
			boolean includeEmpty) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.atLeast(minConfirms, 0)).add(includeEmpty).build();
		JsonArray results = (JsonArray) invoke(BTCAPI_LIST_RECEIVED_BY_ACCOUNT,
				parameters);
		List<BtcAccount> accounts = new ArrayList<BtcAccount>();
		for (JsonObject result : results.getValuesAs(JsonObject.class)) {
			accounts.add(jsonAccount(result));
		}
		return accounts;
	}

	public List<BtcAddress> listReceivedByAddress() throws BtcException {
		return listReceivedByAddress(1, false);
	}

	@Override
	public List<BtcAddress> listReceivedByAddress(long minConfirms,
			boolean includeEmpty) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.atLeast(minConfirms, 0)).add(includeEmpty).build();
		JsonArray results = (JsonArray) invoke(BTCAPI_LIST_RECEIVED_BY_ADDRESS,
				parameters);
		List<BtcAddress> addresses = new ArrayList<BtcAddress>();
		for (JsonObject result : results.getValuesAs(JsonObject.class)) {
			addresses.add(jsonAddress(result));
		}
		return addresses;
	}

	public BtcLastBlock listSinceBlock() throws BtcException {
		return listSinceBlock("", 1);
	}

	@Override
	public BtcLastBlock listSinceBlock(String blockHash, long targetConfirms)
			throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(blockHash)).add(BtcUtil.atLeast(targetConfirms, 1))
				.build();
		JsonObject results = (JsonObject) invoke(BTCAPI_LIST_SINCE_BLOCK,
				parameters);
		return jsonLastBlock(results);
	}

	public List<BtcTransaction> listTransactions() throws BtcException {
		return listTransactions("", 10, 0);
	}

	public List<BtcTransaction> listTransactions(String account)
			throws BtcException {
		return listTransactions(account, 10, 0);
	}

	public List<BtcTransaction> listTransactions(String account, long count)
			throws BtcException {
		return listTransactions(account, count, 0);
	}

	@Override
	public List<BtcTransaction> listTransactions(String account, long count,
			long from) throws BtcException {
		if (count < 1) {
			count = 10;
		}
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(account)).add(count).add(BtcUtil.atLeast(from, 0))
				.build();
		JsonArray results = (JsonArray) invoke(BTCAPI_LIST_TRANSACTIONS,
				parameters);
		List<BtcTransaction> transactions = new ArrayList<BtcTransaction>();
		for (JsonObject result : results.getValuesAs(JsonObject.class)) {
			transactions.add(jsonTransaction(result));
		}
		return transactions;
	}

	@Override
	public List<String> listUnspent(long minConfirms, long maxConfirms)
			throws BtcException {
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BtcException.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public void lockUnspent(boolean unlock, List<Object> outputs)
			throws BtcException {
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BtcException.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	public boolean move(String fromAccount, String toAccount, BigDecimal amount)
			throws BtcException {
		return move(fromAccount, toAccount, amount, 1, "");
	}

	@Override
	public boolean move(String fromAccount, String toAccount, BigDecimal amount,
			long minConfirms, String comment) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder().add(BtcUtil.notNull(fromAccount))
				.add(BtcUtil.notNull(toAccount)).add(BtcUtil.notNull(amount))
				.add(BtcUtil.atLeast(minConfirms, 1)).add(BtcUtil.notNull(comment)).build();
		JsonValue results = invoke(BTCAPI_MOVE, parameters);
		return Boolean.valueOf(String.valueOf(results));
	}

	public String sendFrom(String account, String address,
			BigDecimal amount) throws BtcException {
		return sendFrom(account, address, amount, 1, "", "");
	}
	
	@Override
	public String sendFrom(String account, String address,
			BigDecimal amount, long minConfirms, String comment,
			String commentTo) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder().add(BtcUtil.notNull(account))
				.add(BtcUtil.notNull(address)).add(BtcUtil.notNull(amount)).add(BtcUtil.atLeast(minConfirms, 1))
				.add(BtcUtil.notNull(comment)).add(BtcUtil.notNull(commentTo)).build();
		JsonValue results = invoke(BTCAPI_SEND_FROM, parameters);
		return String.valueOf(results);
	}

	public String sendMany(String account, Map<String, BigDecimal> amounts)
			throws BtcException {
		return sendMany(account, amounts, 1, "", "");
	}
	
	@Override
	public String sendMany(String account, Map<String, BigDecimal> amounts,
			long minConfirms, String comment, String commentTo)
			throws BtcException {
		// TODO
		if (amounts == null) {
			amounts = new HashMap<String, BigDecimal>();
		}
		JsonObjectBuilder amountParams = Json.createObjectBuilder();
		for (String address : amounts.keySet()) {
			amountParams.add(BtcUtil.notNull(address), BtcUtil.notNull(amounts.get(address)));
		}
		JsonArray parameters = Json.createArrayBuilder().add(BtcUtil.notNull(account))
				.add(amountParams).add(BtcUtil.atLeast(minConfirms, 1))
				.add(BtcUtil.notNull(comment)).add(BtcUtil.notNull(commentTo)).build();
		JsonValue results = invoke(BTCAPI_SEND_MANY, parameters);
		return String.valueOf(results);
	}

	@Override
	public void sendRawTransaction(String transactionId) throws BtcException {
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BtcException.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	public String sendToAddress(String address, BigDecimal amount) throws BtcException {
		return sendToAddress(address, amount, "", "");
	}
	
	@Override
	public String sendToAddress(String address, BigDecimal amount,
			String comment, String commentTo) throws BtcException {
		// TODO
		JsonArray parameters = Json.createArrayBuilder().add(BtcUtil.notNull(address))
				.add(BtcUtil.notNull(amount)).add(BtcUtil.notNull(comment)).add(BtcUtil.notNull(commentTo)).build();
		JsonValue results = invoke(BTCAPI_SEND_TO_ADDRESS, parameters);
		return String.valueOf(results);
	}

	@Override
	public void setAccount(String address, String account) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(address)).add(BtcUtil.notNull(account))
				.build();
		invoke(BTCAPI_SET_ACCOUNT, parameters);
	}

	@Override
	public void setGenerate(boolean generate, long generateProcessorsLimit)
			throws BtcException {
		JsonArray parameters = Json.createArrayBuilder().add(generate)
				.add(BtcUtil.atLeast(generateProcessorsLimit, -1)).build();
		invoke(BTCAPI_SET_GENERATE, parameters);
	}

	public void setGenerate(boolean generate) throws BtcException {
		setGenerate(generate, -1);
	}

	@Override
	public boolean setTransactionFee(BigDecimal amount) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder().add(BtcUtil.atLeast(amount, 0))
				.build();
		JsonValue results = invoke(BTCAPI_SET_TRANSACTION_FEE, parameters);
		return Boolean.valueOf(String.valueOf(results));
	}

	@Override
	public String signMessage(String address, String message)
			throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(address)).add(BtcUtil.notNull(message))
				.build();
		JsonString results = (JsonString) invoke(BTCAPI_SIGN_MESSAGE,
				parameters);
		return results.getString();
	}

	@Override
	public void signRawTransaction(String transactionId,
			List<Object> signatures, List<String> keys) throws BtcException {
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BtcException.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public String stop() throws BtcException {
		JsonString results = (JsonString) invoke(BTCAPI_STOP);
		return results.getString();
	}

	@Override
	public void submitBlock(String data, List<Object> params)
			throws BtcException {
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BtcException.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public BtcAddress validateAddress(String address) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(address)).build();
		JsonObject results = (JsonObject) invoke(BTCAPI_VALIDATE_ADDRESS,
				parameters);
		return jsonAddress(results);
	}

	@Override
	public boolean verifyMessage(String address, String signature,
			String message) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(address)).add(BtcUtil.notNull(signature))
				.add(BtcUtil.notNull(message)).build();
		JsonValue results = invoke(BTCAPI_VERIFY_MESSAGE, parameters);
		return Boolean.valueOf(String.valueOf(results));
	}

	@Override
	public void walletLock() throws BtcException {
		invoke(BTCAPI_WALLET_LOCK);
	}

	public void walletPassphrase(String passphrase) throws BtcException {
		walletPassphrase(passphrase, 120);
	}

	@Override
	public void walletPassphrase(String passphrase, long timeout)
			throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(passphrase)).add(BtcUtil.atLeast(timeout, 120))
				.build();
		invoke(BTCAPI_WALLET_PASSPHRASE, parameters);
	}

	@Override
	public void walletPassphraseChange(String passphrase, String newPassphrase)
			throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(passphrase))
				.add(BtcUtil.notNull(newPassphrase)).build();
		invoke(BTCAPI_WALLET_PASSPHRASE_CHANGE, parameters);
	}
}