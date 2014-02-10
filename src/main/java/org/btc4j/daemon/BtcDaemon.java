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
import org.btc4j.core.BtcBlockSubmission;
import org.btc4j.core.BtcBlockTemplate;
import org.btc4j.core.BtcException;
import org.btc4j.core.BtcLastBlock;
import org.btc4j.core.BtcMiningInfo;
import org.btc4j.core.BtcMultiSignatureAddress;
import org.btc4j.core.BtcNode;
import org.btc4j.core.BtcOutput;
import org.btc4j.core.BtcOutputPart;
import org.btc4j.core.BtcOutputSet;
import org.btc4j.core.BtcPeer;
import org.btc4j.core.BtcInfo;
import org.btc4j.core.BtcRawTransaction;
import org.btc4j.core.BtcTransaction;
import org.btc4j.core.BtcUtil;
import org.btc4j.core.BtcWork;

public class BtcDaemon extends BtcJsonRpcHttpClient implements BtcApi {
	private static final String BTCAPI_ADD_MULTISIGNATURE_ADDRESS = "addmultisigaddress";
	private static final String BTCAPI_ADD_NODE = "addnode";
	private static final String BTCAPI_BACKUP_WALLET = "backupwallet";
	private static final String BTCAPI_CREATE_MULTISIGNATURE_ADDRESS = "createmultisig";
	private static final String BTCAPI_CREATE_RAW_TRANSACTION = "createrawtransaction";
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
	private static final String BTCAPI_GET_BLOCK_TEMPLATE = "getblocktemplate";
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
	private static final String BTCAPI_GET_TRANSACTION_OUTPUTSET_INFORMATION = "gettxoutsetinfo";
	private static final String BTCAPI_GET_WORK = "getwork";
	private static final String BTCAPI_GET_HELP = "help";
	private static final String BTCAPI_IMPORT_PRIVATE_KEY = "importprivkey";
	private static final String BTCAPI_KEYPOOL_REFILL = "keypoolrefill";
	private static final String BTCAPI_LIST_ACCOUNTS = "listaccounts";
	private static final String BTCAPI_LIST_ADDRESS_GROUPINGS = "listaddressgroupings";
	private static final String BTCAPI_LIST_LOCK_UNSPENT = "listlockunspent";
	private static final String BTCAPI_LIST_RECEIVED_BY_ACCOUNT = "listreceivedbyaccount";
	private static final String BTCAPI_LIST_RECEIVED_BY_ADDRESS = "listreceivedbyaddress";
	private static final String BTCAPI_LIST_SINCE_BLOCK = "listsinceblock";
	private static final String BTCAPI_LIST_TRANSACTIONS = "listtransactions";
	private static final String BTCAPI_LIST_UNSPENT = "listunspent";
	private static final String BTCAPI_LOCK_UNSPENT = "lockunspent";
	private static final String BTCAPI_MOVE_FUNDS = "move";
	private static final String BTCAPI_SEND_FROM = "sendfrom";
	private static final String BTCAPI_SEND_MANY = "sendmany";
	private static final String BTCAPI_SEND_RAW_TRANSACTION = "sendrawtransaction";
	private static final String BTCAPI_SEND_TO_ADDRESS = "sendtoaddress";
	private static final String BTCAPI_SET_ACCOUNT = "setaccount";
	private static final String BTCAPI_SET_GENERATE = "setgenerate";
	private static final String BTCAPI_SET_TRANSACTION_FEE = "settxfee";
	private static final String BTCAPI_SIGN_MESSAGE = "signmessage";
	private static final String BTCAPI_SIGN_RAW_TRANSACTION = "signrawtransaction";
	private static final String BTCAPI_STOP_DAEMON = "stop";
	private static final String BTCAPI_SUBMIT_BLOCK = "submitblock";
	private static final String BTCAPI_VALIDATE_ADDRESS = "validateaddress";
	private static final String BTCAPI_VERIFY_MESSAGE = "verifymessage";
	private static final String BTCAPI_WALLET_LOCK = "walletlock";
	private static final String BTCAPI_WALLET_PASSPHRASE = "walletpassphrase";
	private static final String BTCAPI_WALLET_PASSPHRASE_CHANGE = "walletpassphrasechange";
	private static final String[] BTC4J_DAEMON_VERSIONS = { "0.8.6" };
	private static final long BTCAPI_WALLET_TIMEOUT = 120;
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

	public BtcDaemon(URL url, int timeout) {
		super(url, timeout);
	}

	public BtcDaemon(URL url) {
		super(url);
	}

	public BtcDaemon(URL url, String account, String password, int timeout,
			int alertPort, int blockPort, int walletPort) {
		super(url, account, password, timeout);
		startListening(alertPort, blockPort, walletPort);
	}

	public BtcDaemon(URL url, String account, String password, int alertPort,
			int blockPort, int walletPort) {
		super(url, account, password);
		startListening(alertPort, blockPort, walletPort);
	}

	public BtcDaemon(URL url, int timeout, int alertPort, int blockPort,
			int walletPort) {
		super(url, timeout);
		startListening(alertPort, blockPort, walletPort);
	}

	public BtcDaemon(URL url, int alertPort, int blockPort, int walletPort) {
		super(url);
		startListening(alertPort, blockPort, walletPort);
	}

	private void startListening(int alertPort, int blockPort, int walletPort) {
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
		JsonArrayBuilder keysParam = Json.createArrayBuilder();
		for (String key : keys) {
			keysParam.add(BtcUtil.notNull(key));
		}
		JsonArray parameters = Json.createArrayBuilder().add(required)
				.add(keysParam).add(BtcUtil.notNull(account)).build();
		return jsonString(invoke(
				BTCAPI_ADD_MULTISIGNATURE_ADDRESS, parameters));
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
		JsonArrayBuilder keysParam = Json.createArrayBuilder();
		for (String key : keys) {
			keysParam.add(BtcUtil.notNull(key));
		}
		JsonArray parameters = Json.createArrayBuilder().add(required)
				.add(keysParam).build();
		return jsonMultiSignatureAddress(invoke(
				BTCAPI_CREATE_MULTISIGNATURE_ADDRESS, parameters));
	}

	public String createRawTransaction(Map<String, BigDecimal> amounts) throws BtcException {
		return createRawTransaction(null, amounts);
	}
			
	@Override
	public String createRawTransaction(List<BtcOutputPart> outputs,
			Map<String, BigDecimal> amounts) throws BtcException {
		if (outputs == null) {
			outputs = new ArrayList<BtcOutputPart>();
		}
		JsonArrayBuilder outputsParam = Json.createArrayBuilder();
		for (BtcOutputPart output : outputs) {
			JsonObjectBuilder outputPart = Json.createObjectBuilder();
			outputPart.add(BtcOutputPart.PARAM_TRANSACTION, output.getTransaction());
			outputPart.add(BtcOutputPart.PARAM_OUTPUT, output.getOutput());
			outputsParam.add(outputPart);
		}
		if (amounts == null) {
			amounts = new HashMap<String, BigDecimal>();
		}
		JsonObjectBuilder amountsParam = Json.createObjectBuilder();
		for (String address : amounts.keySet()) {
			amountsParam.add(BtcUtil.notNull(address),
					BtcUtil.notNull(amounts.get(address)));
		}
		JsonArray parameters = Json.createArrayBuilder().add(outputsParam).add(amountsParam).build();
		return jsonString(invoke(BTCAPI_CREATE_RAW_TRANSACTION, parameters));
	}

	@Override
	public BtcRawTransaction decodeRawTransaction(String encoded)
			throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(encoded)).build();
		return jsonRawTransaction(invoke(BTCAPI_DECODE_RAW_TRANSACTION,
				parameters));
	}

	@Override
	public String dumpPrivateKey(String address) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(address)).build();
		return jsonString(invoke(BTCAPI_DUMP_PRIVATE_KEY,
				parameters));
	}

	@Override
	public String getAccount(String address) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(address)).build();
		return jsonString(invoke(BTCAPI_GET_ACCOUNT, parameters));
	}

	@Override
	public String getAccountAddress(String account) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(account)).build();
		return jsonString(invoke(BTCAPI_GET_ACCOUNT_ADDRESS,
				parameters));
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
		JsonValue results = invoke(
				BTCAPI_GET_ADDED_NODE_INFORMATION, parameters);
		if (results != null) {
			if (dns) {
				JsonArray resultsArray = (JsonArray) results;
				for (JsonValue result : resultsArray.getValuesAs(JsonValue.class)) {
					addedNodes.add(jsonAddedNode(result));
				}
			} else {
				addedNodes.add(jsonAddedNode(results));
			}
		}
		return addedNodes;
	}

	@Override
	public List<String> getAddressesByAccount(String account)
			throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(account)).build();
		List<String> addresses = new ArrayList<String>();
		JsonValue results = invoke(BTCAPI_GET_ADDRESSES_BY_ACCOUNT,
				parameters);
		if (results != null) {
			JsonArray resultsArray = (JsonArray) results;
			for (JsonValue result : resultsArray.getValuesAs(JsonValue.class)) {
				addresses.add(jsonString(result));
			}
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
				.add(BtcUtil.notNull(account))
				.add(BtcUtil.atLeast(minConfirms, 1)).build();
		return jsonDouble(invoke(BTCAPI_GET_BALANCE, parameters));
	}

	@Override
	public BtcBlock getBlock(String hash) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(hash)).build();
		return jsonBlock(invoke(BTCAPI_GET_BLOCK, parameters));
	}

	@Override
	public long getBlockCount() throws BtcException {
		return jsonLong(invoke(BTCAPI_GET_BLOCK_COUNT));
	}

	@Override
	public String getBlockHash(long index) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.atLeast(index, 0)).build();
		return jsonString(invoke(BTCAPI_GET_BLOCK_HASH,
				parameters));
	}

	public BtcBlockTemplate getBlockTemplate() throws BtcException {
		return getBlockTemplate(null, BtcBlockTemplate.Mode.NULL);
	}
	
	public BtcBlockTemplate getBlockTemplate(
			List<BtcBlockTemplate.Capability> capabilities) throws BtcException {
		return getBlockTemplate(capabilities, BtcBlockTemplate.Mode.NULL);
	}

	@Override
	public BtcBlockTemplate getBlockTemplate(
			List<BtcBlockTemplate.Capability> capabilities,
			BtcBlockTemplate.Mode mode) throws BtcException {
		if (capabilities == null) {
			capabilities = new ArrayList<BtcBlockTemplate.Capability>();
		}
		JsonArrayBuilder capabilitiesParam = Json.createArrayBuilder();
		for (BtcBlockTemplate.Capability capability : capabilities) {
			if (BtcUtil.notNull(capability) != BtcBlockTemplate.Capability.NULL) {
				capabilitiesParam.add(String.valueOf(capability).toLowerCase());
			}
		}
		JsonObjectBuilder request = Json.createObjectBuilder()
				.add(BtcBlockTemplate.PARAM_CAPABILITIES, capabilitiesParam);
		if (BtcUtil.notNull(mode) != BtcBlockTemplate.Mode.NULL) {
			request.add(BtcBlockTemplate.PARAM_MODE, String.valueOf(mode).toLowerCase());
		}
		JsonArray parameters = Json.createArrayBuilder().add(request).build();
		return jsonBlockTemplate(invoke(BTCAPI_GET_BLOCK_TEMPLATE,
				parameters));
	}

	@Override
	public long getConnectionCount() throws BtcException {
		return jsonLong(invoke(BTCAPI_GET_CONNECTION_COUNT));
	}

	@Override
	public BigDecimal getDifficulty() throws BtcException {
		return jsonDouble(invoke(BTCAPI_GET_DIFFICULTY));
	}

	@Override
	public boolean getGenerate() throws BtcException {
		return Boolean.valueOf(String.valueOf(invoke(BTCAPI_GET_GENERATE)));
	}

	@Override
	public long getHashesPerSecond() throws BtcException {
		return jsonLong(invoke(BTCAPI_GET_HASHES_PER_SECOND));
	}

	@Override
	public BtcInfo getInformation() throws BtcException {
		return jsonInfo(invoke(BTCAPI_GET_INFORMATION));
	}

	@Override
	public BtcMiningInfo getMiningInformation() throws BtcException {
		return jsonMiningInfo(invoke(BTCAPI_GET_MINING_INFORMATION));
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
		return jsonString(invoke(BTCAPI_GET_NEW_ADDRESS,
				parameters));
	}

	@Override
	public List<BtcPeer> getPeerInformation() throws BtcException {
		List<BtcPeer> peers = new ArrayList<BtcPeer>();
		JsonValue results = invoke(BTCAPI_GET_PEER_INFORMATION);
		if (results != null) {
			JsonArray resultsArray = (JsonArray) results;
			for (JsonValue result : resultsArray.getValuesAs(JsonValue.class)) {
				peers.add(jsonPeer(result));
			}
		}
		return peers;
	}

	@Override
	public List<String> getRawMemoryPool() throws BtcException {
		List<String> rawMemPool = new ArrayList<String>();
		JsonValue results = invoke(BTCAPI_GET_RAW_MEMORY_POOL);
		if (results != null) {
			JsonArray resultsArray = (JsonArray) results;
			for (JsonValue result : resultsArray.getValuesAs(JsonValue.class)) {
				rawMemPool.add(jsonString(result));
			}
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
				.add(BtcUtil.notNull(transactionId)).add(BtcUtil.bool(verbose))
				.build();
		JsonValue results = invoke(
				BTCAPI_GET_RAW_TRANSACTION, parameters);
		if (verbose) {
			return jsonRawTransaction(results);
		}
		BtcRawTransaction transaction = new BtcRawTransaction();
		transaction.setHex(jsonString(results));
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
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(account))
				.add(BtcUtil.atLeast(minConfirms, 1)).build();
		return jsonDouble(invoke(
				BTCAPI_GET_RECEIVED_BY_ACCOUNT, parameters));
	}

	public BigDecimal getReceivedByAddress(String address) throws BtcException {
		return getReceivedByAddress(address, 1);
	}

	@Override
	public BigDecimal getReceivedByAddress(String address, long minConfirms)
			throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(address))
				.add(BtcUtil.atLeast(minConfirms, 1)).build();
		return jsonDouble(invoke(
				BTCAPI_GET_RECEIVED_BY_ADDRESS, parameters));
	}

	@Override
	public BtcTransaction getTransaction(String transactionId)
			throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(transactionId)).build();
		return jsonTransaction(invoke(BTCAPI_GET_TRANSACTION,
				parameters));
	}

	public BtcOutput getTransactionOutput(String transactionId) throws BtcException {
		return getTransactionOutput(transactionId, 0, false);
	}
	
	public BtcOutput getTransactionOutput(String transactionId,
			boolean includeMemoryPool) throws BtcException {
		return getTransactionOutput(transactionId, 0, includeMemoryPool);
	}
	
	public BtcOutput getTransactionOutput(String transactionId, long index) throws BtcException {
		return getTransactionOutput(transactionId, index, false);
	}

	@Override
	public BtcOutput getTransactionOutput(String transactionId, long index,
			boolean includeMemoryPool) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder().add(BtcUtil.notNull(transactionId))
				.add(BtcUtil.atLeast(index, 0)).add(includeMemoryPool).build();
		return jsonOutput(invoke(BTCAPI_GET_TRANSACTION_OUTPUT, parameters));
	}

	@Override
	public BtcOutputSet getTransactionOutputSetInformation()
			throws BtcException {
		return jsonOutputSet(invoke(BTCAPI_GET_TRANSACTION_OUTPUTSET_INFORMATION));
	}

	public BtcWork getWork() throws BtcException {
		return getWork("");
	}

	@Override
	public BtcWork getWork(String data) throws BtcException {
		if (BtcUtil.nil(data) != null) {
			JsonArray parameters = Json.createArrayBuilder()
					.add(BtcUtil.notNull(data)).build();
			JsonValue results = invoke(BTCAPI_GET_WORK, parameters);
			if (results == null) {
				return null;
			}
			BtcWork work = new BtcWork();
			work.setSuccess(Boolean.valueOf(String.valueOf(results)));
			return work;
		} else {
			return jsonWork(invoke(BTCAPI_GET_WORK));
		}
	}

	public String help() throws BtcException {
		return help("");
	}

	@Override
	public String help(String command) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(command)).build();
		return jsonString(invoke(BTCAPI_GET_HELP, parameters));
	}

	public void importPrivateKey(String privateKey) throws BtcException {
		importPrivateKey(privateKey, "", true);
	}
	
	public void importPrivateKey(String privateKey, String label) throws BtcException {
		importPrivateKey(privateKey, label, true);
	}
	
	public void importPrivateKey(String privateKey, boolean rescan) throws BtcException {
		importPrivateKey(privateKey, "", rescan);
	}

	@Override
	public void importPrivateKey(String privateKey, String label,
			boolean rescan) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(privateKey)).add(BtcUtil.notNull(label))
				.add(rescan).build();
		invoke(BTCAPI_IMPORT_PRIVATE_KEY, parameters);
	}

	@Override
	public void keyPoolRefill() throws BtcException {
		invoke(BTCAPI_KEYPOOL_REFILL);
	}

	public Map<String, BtcAccount> listAccounts() throws BtcException {
		return listAccounts(1);
	}

	@Override
	public Map<String, BtcAccount> listAccounts(long minConfirms)
			throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.atLeast(minConfirms, 1)).build();
		Map<String, BtcAccount> accounts = new HashMap<String, BtcAccount>();
		JsonObject results = jsonObject(invoke(BTCAPI_LIST_ACCOUNTS,
				parameters));
		if (results != null) {
			for (String key : results.keySet()) {
				BtcAccount account = new BtcAccount();
				account.setAccount(key);
				account.setAmount(jsonDouble(results, key));
				accounts.put(key, account);
			}
		}
		return accounts;
	}

	@Override
	public List<BtcAddress> listAddressGroupings() throws BtcException {
		List<BtcAddress> addresses = new ArrayList<BtcAddress>();
		JsonValue results = invoke(BTCAPI_LIST_ADDRESS_GROUPINGS);
		if (results != null) {
			JsonArray resultsArray = (JsonArray) results;
			for (JsonArray groupings : resultsArray.getValuesAs(JsonArray.class)) {
				for (JsonArray grouping : groupings.getValuesAs(JsonArray.class)) {
					BtcAddress address = new BtcAddress();
					address.setAddress(grouping.getString(0));
					JsonNumber amount = grouping.getJsonNumber(1);
					if (amount != null) {
						address.setAmount(amount.bigDecimalValue());
					}
					BtcAccount account = new BtcAccount();
					account.setAccount((grouping.size() > 2) ? grouping
							.getString(2) : "");
					address.setAccount(account);
					addresses.add(address);
				}
			}
		}
		return addresses;
	}

	@Override
	public List<BtcOutputPart> listLockUnspent() throws BtcException {
		List<BtcOutputPart> unspent = new ArrayList<BtcOutputPart>();
		JsonValue results = invoke(BTCAPI_LIST_LOCK_UNSPENT);
		if (results != null) {
			JsonArray resultsArray = (JsonArray) results;
			for (JsonValue result : resultsArray.getValuesAs(JsonValue.class)) {
				unspent.add(jsonOutputPart(result));
			}
		}
		return unspent;
	}

	public List<BtcAccount> listReceivedByAccount() throws BtcException {
		return listReceivedByAccount(1, false);
	}

	@Override
	public List<BtcAccount> listReceivedByAccount(long minConfirms,
			boolean includeEmpty) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.atLeast(minConfirms, 0)).add(includeEmpty).build();
		List<BtcAccount> accounts = new ArrayList<BtcAccount>();
		JsonValue results = invoke(BTCAPI_LIST_RECEIVED_BY_ACCOUNT,
				parameters);
		JsonArray resultsArray = (JsonArray) results;
		for (JsonValue result : resultsArray.getValuesAs(JsonValue.class)) {
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
		List<BtcAddress> addresses = new ArrayList<BtcAddress>();
		JsonValue results = invoke(BTCAPI_LIST_RECEIVED_BY_ADDRESS,
				parameters);
		if (results != null) {
		JsonArray resultsArray = (JsonArray) results;
			for (JsonValue result : resultsArray.getValuesAs(JsonValue.class)) {
				addresses.add(jsonAddress(result));
			}
		}
		return addresses;
	}

	public BtcLastBlock listSinceBlock() throws BtcException {
		return listSinceBlock("", 1);
	}

	@Override
	public BtcLastBlock listSinceBlock(String hash, long targetConfirms)
			throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(hash))
				.add(BtcUtil.atLeast(targetConfirms, 1)).build();
		return jsonLastBlock(invoke(BTCAPI_LIST_SINCE_BLOCK,
				parameters));
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
				.add(BtcUtil.notNull(account)).add(count)
				.add(BtcUtil.atLeast(from, 0)).build();
		List<BtcTransaction> transactions = new ArrayList<BtcTransaction>();
		JsonValue results = invoke(BTCAPI_LIST_TRANSACTIONS,
				parameters);
		if (results != null) {
			JsonArray resultsArray = (JsonArray) results;
			for (JsonValue result : resultsArray.getValuesAs(JsonValue.class)) {
				transactions.add(jsonTransaction(result));
			}
		}
		return transactions;
	}

	public List<BtcOutput> listUnspent() throws BtcException {
		return listUnspent(1, 999999);
	}
	
	public List<BtcOutput> listUnspent(long minConfirms) throws BtcException {
		return listUnspent(minConfirms, 999999);
	}
	
	@Override
	public List<BtcOutput> listUnspent(long minConfirms, long maxConfirms)
			throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.atLeast(minConfirms, 1))
				.add(BtcUtil.atLeast(maxConfirms, 1)).build();
		List<BtcOutput> unspents = new ArrayList<BtcOutput>();
		JsonValue results = invoke(BTCAPI_LIST_UNSPENT, parameters);
		if (results != null) {
			JsonArray resultsArray = (JsonArray) results;
			for (JsonValue result : resultsArray.getValuesAs(JsonValue.class)) {
				unspents.add(jsonOutput(result));
			}
		}
		return unspents;
	}
	
	public boolean lockUnspent() throws BtcException {
		return lockUnspent(false, null);
	}
	
	public boolean lockUnspent(List<BtcOutputPart> outputs) throws BtcException {
		return lockUnspent(false, outputs);
	}
	
	public boolean unlockUnspent() throws BtcException {
		return lockUnspent(true, null);
	}
	
	public boolean unlockUnspent(List<BtcOutputPart> outputs) throws BtcException {
		return lockUnspent(true, outputs);
	}

	@Override
	public boolean lockUnspent(boolean unlock, List<BtcOutputPart> outputs)
			throws BtcException {
		JsonArrayBuilder parameters = Json.createArrayBuilder().add(unlock);
		if ((outputs != null) && (outputs.size() > 0)) {
			JsonArrayBuilder outputsParam = Json.createArrayBuilder();
			for (BtcOutputPart output : outputs) {
				JsonObjectBuilder outputPart = Json.createObjectBuilder();
				outputPart.add(BtcOutputPart.PARAM_TRANSACTION, output.getTransaction());
				outputPart.add(BtcOutputPart.PARAM_OUTPUT, output.getOutput());
				outputsParam.add(outputPart);
			}
			parameters.add(outputsParam);
		}
		return Boolean.valueOf(String.valueOf(invoke(BTCAPI_LOCK_UNSPENT, parameters.build())));
	}

	public boolean move(String fromAccount, String toAccount, double amount)
			throws BtcException {
		return move(fromAccount, toAccount, BigDecimal.valueOf(amount), 1, "");
	}

	@Override
	public boolean move(String fromAccount, String toAccount,
			BigDecimal amount, long minConfirms, String comment)
			throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(fromAccount))
				.add(BtcUtil.notNull(toAccount)).add(BtcUtil.notNull(amount))
				.add(BtcUtil.atLeast(minConfirms, 1))
				.add(BtcUtil.notNull(comment)).build();
		return Boolean.valueOf(String.valueOf(invoke(BTCAPI_MOVE_FUNDS, parameters)));
	}

	public String sendFrom(String account, String address, double amount)
			throws BtcException {
		return sendFrom(account, address, BigDecimal.valueOf(amount), 1, "", "");
	}

	@Override
	public String sendFrom(String account, String address, BigDecimal amount,
			long minConfirms, String comment, String commentTo)
			throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(account)).add(BtcUtil.notNull(address))
				.add(BtcUtil.notNull(amount))
				.add(BtcUtil.atLeast(minConfirms, 1))
				.add(BtcUtil.notNull(comment)).add(BtcUtil.notNull(commentTo))
				.build();
		return jsonString(invoke(BTCAPI_SEND_FROM, parameters));
	}

	public String sendMany(String account, Map<String, BigDecimal> amounts)
			throws BtcException {
		return sendMany(account, amounts, 1, "");
	}

	@Override
	public String sendMany(String account, Map<String, BigDecimal> amounts,
			long minConfirms, String comment) throws BtcException {
		if (amounts == null) {
			amounts = new HashMap<String, BigDecimal>();
		}
		JsonObjectBuilder amountsParam = Json.createObjectBuilder();
		for (String address : amounts.keySet()) {
			amountsParam.add(BtcUtil.notNull(address),
					BtcUtil.notNull(amounts.get(address)));
		}
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(account)).add(amountsParam)
				.add(BtcUtil.atLeast(minConfirms, 1))
				.add(BtcUtil.notNull(comment)).build();
		return jsonString(invoke(BTCAPI_SEND_MANY, parameters));
	}

	public BtcTransaction sendRawTransaction(String encoded, boolean verbose) throws BtcException {
		BtcTransaction transaction = sendRawTransaction(encoded);
		return verbose ? getTransaction(transaction.getTransaction()) : transaction;
	}
	
	@Override
	public BtcTransaction sendRawTransaction(String encoded) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(encoded)).build();
		BtcTransaction transaction = new BtcTransaction();
		transaction.setTransaction(jsonString(invoke(BTCAPI_SEND_RAW_TRANSACTION, parameters)));
		return transaction;
	}

	public String sendToAddress(String address, double amount)
			throws BtcException {
		return sendToAddress(address, BigDecimal.valueOf(amount), "", "");
	}

	@Override
	public String sendToAddress(String address, BigDecimal amount,
			String comment, String commentTo) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(address)).add(BtcUtil.notNull(amount))
				.add(BtcUtil.notNull(comment)).add(BtcUtil.notNull(commentTo))
				.build();
		return jsonString(invoke(BTCAPI_SEND_TO_ADDRESS,
				parameters));
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

	public boolean setTransactionFee(double amount) throws BtcException {
		return setTransactionFee(BigDecimal.valueOf(amount));
	}

	@Override
	public boolean setTransactionFee(BigDecimal amount) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.atLeast(amount, 0)).build();
		return Boolean.valueOf(String.valueOf(invoke(BTCAPI_SET_TRANSACTION_FEE, parameters)));
	}

	@Override
	public String signMessage(String address, String message)
			throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(address)).add(BtcUtil.notNull(message))
				.build();
		return jsonString(invoke(BTCAPI_SIGN_MESSAGE,
				parameters));
	}
	
	public BtcRawTransaction signRawTransaction(String encoded) throws BtcException {
		return signRawTransaction(encoded, null, null, null);
	}
	
	public BtcRawTransaction signRawTransaction(String encoded, boolean verbose) throws BtcException {
		return signRawTransaction(encoded, null, null, null, verbose);
	}
	
	public BtcRawTransaction signRawTransaction(String encoded,
			List<BtcOutputPart> outputs, boolean verbose) throws BtcException {
		return signRawTransaction(encoded, outputs, null, null, verbose);
	}
	
	public BtcRawTransaction signRawTransaction(String encoded,
			List<BtcOutputPart> outputs, List<String> keys, boolean verbose) throws BtcException {
		return signRawTransaction(encoded, outputs, keys, null, verbose);
	}
	
	public BtcRawTransaction signRawTransaction(String encoded,
			List<BtcOutputPart> outputs, List<String> keys,
			BtcRawTransaction.SignatureHash signatureHash, boolean verbose) throws BtcException {
		BtcRawTransaction transaction = signRawTransaction(encoded, outputs, keys, signatureHash);
		if (verbose) {
			boolean complete = transaction.isComplete();
			transaction = decodeRawTransaction(transaction.getHex());
			transaction.setComplete(complete);
		}
		return transaction;
	}

	@Override
	public BtcRawTransaction signRawTransaction(String encoded,
			List<BtcOutputPart> outputs, List<String> keys,
			BtcRawTransaction.SignatureHash signatureHash) throws BtcException {
		if (outputs == null) {
			outputs = new ArrayList<BtcOutputPart>();
		}
		JsonArrayBuilder outputsParam = Json.createArrayBuilder();
		for (BtcOutputPart output : outputs) {
			JsonObjectBuilder outputPart = Json.createObjectBuilder();
			outputPart.add(BtcOutputPart.PARAM_TRANSACTION, output.getTransaction());
			outputPart.add(BtcOutputPart.PARAM_OUTPUT, output.getOutput());
			outputPart.add(BtcOutputPart.PARAM_SCRIPT, output.getScript().getPublicKey());
			outputsParam.add(outputPart);
		}
		JsonArrayBuilder parameters = Json.createArrayBuilder().add(BtcUtil.notNull(encoded)).add(outputsParam);
		if (keys != null) {
			JsonArrayBuilder keysParam = Json.createArrayBuilder();
			for (String key : keys) {
				keysParam.add(key);
			}
			parameters.add(keysParam);
		}
		if (BtcUtil.notNull(signatureHash) != BtcRawTransaction.SignatureHash.NULL) {
			parameters.add(String.valueOf(signatureHash));
		}
		return jsonRawTransaction(invoke(BTCAPI_SIGN_RAW_TRANSACTION, parameters.build()));
	}

	@Override
	public String stop() throws BtcException {
		return jsonString(invoke(BTCAPI_STOP_DAEMON));
	}
	
	public BtcBlockSubmission submitBlock(String data)
			throws BtcException {
		return submitBlock(data, "", null);
	}

	public BtcBlockSubmission submitBlock(String data, String workId)
			throws BtcException {
		return submitBlock(data, workId, null);
	}
	
	@Override
	public BtcBlockSubmission submitBlock(String data, String workId, Map<String, String> params)
			throws BtcException {
		JsonObjectBuilder request = Json.createObjectBuilder()
				.add(BtcBlockSubmission.PARAM_WORK_ID, BtcUtil.notNull(workId));
		if (params != null) {
			for (String key : params.keySet()) {
				request.add(key, params.get(key));
			}
		}
		JsonArray parameters = Json.createArrayBuilder().add(BtcUtil.notNull(data)).add(request).build();
		JsonValue results = invoke(BTCAPI_SUBMIT_BLOCK,
				parameters);
		if (results == null) {
			BtcBlockSubmission submission = new BtcBlockSubmission();
			submission.setAccepted(true);
			return submission;
		} else {
			if ((results.getValueType().equals(JsonValue.ValueType.STRING)) && (results instanceof JsonString)) {
				JsonString reason = (JsonString) results;
				BtcBlockSubmission submission = new BtcBlockSubmission();
				List<String> reasons = new ArrayList<String>();
				reasons.add(reason.getString());
				submission.setReasons(reasons);
				return submission;
			}
		}
		return jsonBlockSubmission(results);		
	}

	@Override
	public BtcAddress validateAddress(String address) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(address)).build();
		return jsonAddress(invoke(BTCAPI_VALIDATE_ADDRESS,
				parameters));
	}

	@Override
	public boolean verifyMessage(String address, String signature,
			String message) throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(address)).add(BtcUtil.notNull(signature))
				.add(BtcUtil.notNull(message)).build();
		return Boolean.valueOf(String.valueOf(invoke(BTCAPI_VERIFY_MESSAGE, parameters)));
	}

	@Override
	public void walletLock() throws BtcException {
		invoke(BTCAPI_WALLET_LOCK);
	}
	
	public void walletUnlock(String passphrase) throws BtcException {
		walletPassphrase(passphrase, BTCAPI_WALLET_TIMEOUT);
	}
	
	public void walletUnlock(String passphrase, long timeout) throws BtcException {
		walletPassphrase(passphrase, timeout);
	}

	public void walletPassphrase(String passphrase) throws BtcException {
		walletPassphrase(passphrase, BTCAPI_WALLET_TIMEOUT);
	}

	@Override
	public void walletPassphrase(String passphrase, long timeout)
			throws BtcException {
		JsonArray parameters = Json.createArrayBuilder()
				.add(BtcUtil.notNull(passphrase))
				.add(BtcUtil.atLeast(timeout, BTCAPI_WALLET_TIMEOUT)).build();
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