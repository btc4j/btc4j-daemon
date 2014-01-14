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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.btc4j.core.BtcAccount;
import org.btc4j.core.BtcAddress;
import org.btc4j.core.BtcApi;
import org.btc4j.core.BtcBlock;
import org.btc4j.core.BtcException;
import org.btc4j.core.BtcLastBlock;
import org.btc4j.core.BtcMining;
import org.btc4j.core.BtcNodeOperation;
import org.btc4j.core.BtcPeer;
import org.btc4j.core.BtcStatus;
import org.btc4j.core.BtcTransaction;
import org.btc4j.core.BtcTransactionOutputSet;

public class BtcDaemon extends BtcJsonRpcHttpClient implements BtcApi {
	// private static final String BTCAPI_ADD_MULTI_SIGNATURE_ADDRESS =
	// "addmultisigaddress";
	// private static final String BTCAPI_ADD_NODE = "addnode";
	private static final String BTCAPI_BACKUP_WALLET = "backupwallet";
	// private static final String BTCAPI_CREATE_MULTI_SIGNATURE_ADDRESS =
	// "createmultisig";
	// private static final String BTCAPI_CREATE_RAW_TRANSACTION =
	// "createrawtransaction";
	// private static final String BTCAPI_DECODE_RAW_TRANSACTION =
	// "decoderawtransaction";
	private static final String BTCAPI_DUMP_PRIVATE_KEY = "dumpprivkey";
	// private static final String BTCAPI_ENCRYPT_WALLET = "encryptwallet";
	private static final String BTCAPI_GET_ACCOUNT = "getaccount";
	private static final String BTCAPI_GET_ACCOUNT_ADDRESS = "getaccountaddress";
	// private static final String BTCAPI_GET_ADDED_NODE_INFORMATION =
	// "getaddednodeinfo";
	private static final String BTCAPI_GET_ADDRESSES_BY_ACCOUNT = "getaddressesbyaccount";
	private static final String BTCAPI_GET_BALANCE = "getbalance";
	private static final String BTCAPI_GET_BLOCK = "getblock";
	private static final String BTCAPI_GET_BLOCK_COUNT = "getblockcount";
	private static final String BTCAPI_GET_BLOCK_HASH = "getblockhash";
	// private static final String BTCAPI_GET_BLOCK_TEMPLATE =
	// "getblocktemplate";
	private static final String BTCAPI_GET_CONNECTION_COUNT = "getconnectioncount";
	private static final String BTCAPI_GET_DIFFICULTY = "getdifficulty";
	private static final String BTCAPI_GET_GENERATE = "getgenerate";
	private static final String BTCAPI_GET_HASHES_PER_SECOND = "gethashespersec";
	private static final String BTCAPI_GET_INFORMATION = "getinfo";
	private static final String BTCAPI_GET_MINING_INFORMATION = "getmininginfo";
	private static final String BTCAPI_GET_NEW_ADDRESS = "getnewaddress";
	private static final String BTCAPI_GET_PEER_INFORMATION = "getpeerinfo";
	private static final String BTCAPI_GET_RAW_MEMORY_POOL = "getrawmempool";
	// private static final String BTCAPI_GET_RAW_TRANSACTION =
	// "getrawtransaction";
	private static final String BTCAPI_GET_RECEIVED_BY_ACCOUNT = "getreceivedbyaccount";
	private static final String BTCAPI_GET_RECEIVED_BY_ADDRESS = "getreceivedbyaddress";
	private static final String BTCAPI_GET_TRANSACTION = "gettransaction";
	// private static final String BTCAPI_GET_TRANSACTION_OUTPUT = "gettxout";
	private static final String BTCAPI_GET_TRANSACTION_OUTPUT_SET_INFORMATION = "gettxoutsetinfo";
	// private static final String BTCAPI_GET_WORK = "getwork";
	private static final String BTCAPI_HELP = "help";
	private static final String BTCAPI_IMPORT_PRIVATE_KEY = "importprivkey";
	// private static final String BTCAPI_KEY_POOL_REFILL = "keypoolrefill";
	private static final String BTCAPI_LIST_ACCOUNTS = "listaccounts";
	private static final String BTCAPI_LIST_ADDRESS_GROUPINGS = "listaddressgroupings";
	private static final String BTCAPI_LIST_LOCK_UNSPENT = "listlockunspent";
	private static final String BTCAPI_LIST_RECEIVED_BY_ACCOUNT = "listreceivedbyaccount";
	private static final String BTCAPI_LIST_RECEIVED_BY_ADDRESS = "listreceivedbyaddress";
	private static final String BTCAPI_LIST_SINCE_BLOCK = "listsinceblock";
	private static final String BTCAPI_LIST_TRANSACTIONS = "listtransactions";
	// private static final String BTCAPI_LIST_UNSPENT = "listunspent";
	// private static final String BTCAPI_LOCK_UNSPENT = "lockunspent";
	// private static final String BTCAPI_MOVE = "move";
	// private static final String BTCAPI_SEND_FROM = "sendfrom";
	// private static final String BTCAPI_SEND_MANY = "sendmany";
	// private static final String BTCAPI_SEND_RAW_TRANSACTION =
	// "sendrawtransaction";
	// private static final String BTCAPI_SEND_TO_ADDRESS = "sendtoaddress";
	private static final String BTCAPI_SET_ACCOUNT = "setaccount";
	private static final String BTCAPI_SET_GENERATE = "setgenerate";
	private static final String BTCAPI_SET_TRANSACTION_FEE = "settxfee";
	// private static final String BTCAPI_SIGN_MESSAGE = "signmessage";
	// private static final String BTCAPI_SIGN_RAW_TRANSACTION =
	// "signrawtransaction";
	private static final String BTCAPI_STOP = "stop";
	// private static final String BTCAPI_SUBMIT_BLOCK = "submitblock";
	private static final String BTCAPI_VALIDATE_ADDRESS = "validateaddress";
	// private static final String BTCAPI_VERIFY_MESSAGE = "verifymessage";
	private static final String[] BTC4J_DAEMON_VERSIONS = { "0.8.6" };

	public BtcDaemon(URL url, String account, String password, long timeout) {
		super(url, account, password, timeout);
	}

	public String[] getSupportedVersions() {
		return BTC4J_DAEMON_VERSIONS;
	}

	public void addMultiSignatureAddress(int required, List<String> keys)
			throws BtcException {
		addMultiSignatureAddress(required, keys, "");
	}

	@Override
	public void addMultiSignatureAddress(int required, List<String> keys,
			String account) throws BtcException {
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BtcException.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public void addNode(String node, BtcNodeOperation operation)
			throws BtcException {
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BtcException.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
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
	public String createMultiSignatureAddress(int required, List<String> keys)
			throws BtcException {
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BtcException.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public String createRawTransaction(List<Object> transactionIds,
			List<Object> addresses) throws BtcException {
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BtcException.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public String decodeRawTransaction(String transactionId)
			throws BtcException {
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BtcException.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public String dumpPrivateKey(String address) throws BtcException {
		if (address == null) {
			address = "";
		}
		JsonArray parameters = Json.createArrayBuilder().add(address).build();
		JsonString resultss = (JsonString) invoke(BTCAPI_DUMP_PRIVATE_KEY,
				parameters);
		return resultss.getString();
	}

	@Override
	public void encryptWallet(String passPhrase) throws BtcException {
		// TODO
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BtcException.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public String getAccount(String address) throws BtcException {
		if (address == null) {
			address = "";
		}
		JsonArray parameters = Json.createArrayBuilder().add(address).build();
		JsonString resultss = (JsonString) invoke(BTCAPI_GET_ACCOUNT,
				parameters);
		return resultss.getString();
	}

	@Override
	public String getAccountAddress(String account) throws BtcException {
		if (account == null) {
			account = "";
		}
		JsonArray parameters = Json.createArrayBuilder().add(account).build();
		JsonString results = (JsonString) invoke(BTCAPI_GET_ACCOUNT_ADDRESS,
				parameters);
		return results.getString();
	}

	@Override
	public String getAddedNodeInformation(boolean dns, String node)
			throws BtcException {
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BtcException.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public List<String> getAddressesByAccount(String account)
			throws BtcException {
		if (account == null) {
			account = "";
		}
		JsonArray parameters = Json.createArrayBuilder().add(account).build();
		JsonArray results = (JsonArray) invoke(BTCAPI_GET_ADDRESSES_BY_ACCOUNT,
				parameters);

		List<String> addresses = new ArrayList<String>();
		for (JsonString result : results.getValuesAs(JsonString.class)) {
			addresses.add(result.getString());
		}
		return addresses;
	}

	public double getBalance() throws BtcException {
		return getBalance("", 1);
	}

	public double getBalance(int minConfirms) throws BtcException {
		return getBalance("", minConfirms);
	}

	public double getBalance(String account) throws BtcException {
		return getBalance(account, 1);
	}

	@Override
	public double getBalance(String account, int minConfirms)
			throws BtcException {
		if (account == null) {
			account = "";
		}
		if (minConfirms < 1) {
			minConfirms = 1;
		}
		JsonArray parameters = Json.createArrayBuilder().add(account)
				.add(minConfirms).build();
		JsonNumber results = (JsonNumber) invoke(BTCAPI_GET_BALANCE, parameters);
		return results.doubleValue();
	}

	@Override
	public BtcBlock getBlock(String hash) throws BtcException {
		if (hash == null) {
			hash = "";
		}
		JsonArray parameters = Json.createArrayBuilder().add(hash).build();
		JsonObject results = (JsonObject) invoke(BTCAPI_GET_BLOCK, parameters);
		return jsonBlock(results);
	}

	@Override
	public int getBlockCount() throws BtcException {
		JsonNumber results = (JsonNumber) invoke(BTCAPI_GET_BLOCK_COUNT);
		return results.intValue();
	}

	@Override
	public String getBlockHash(int index) throws BtcException {
		if (index < 0) {
			index = 0;
		}
		JsonArray parameters = Json.createArrayBuilder().add(index).build();
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
	public int getConnectionCount() throws BtcException {
		JsonNumber results = (JsonNumber) invoke(BTCAPI_GET_CONNECTION_COUNT);
		return results.intValue();
	}

	@Override
	public double getDifficulty() throws BtcException {
		JsonNumber results = (JsonNumber) invoke(BTCAPI_GET_DIFFICULTY);
		return results.doubleValue();
	}

	@Override
	public boolean getGenerate() throws BtcException {
		JsonValue results = invoke(BTCAPI_GET_GENERATE);
		return Boolean.valueOf(String.valueOf(results));
	}

	@Override
	public int getHashesPerSecond() throws BtcException {
		JsonNumber results = (JsonNumber) invoke(BTCAPI_GET_HASHES_PER_SECOND);
		return results.intValue();
	}

	@Override
	public BtcStatus getInformation() throws BtcException {
		JsonObject results = (JsonObject) invoke(BTCAPI_GET_INFORMATION);
		return jsonStatus(results);
	}

	@Override
	public BtcMining getMiningInformation() throws BtcException {
		JsonObject results = (JsonObject) invoke(BTCAPI_GET_MINING_INFORMATION);
		return jsonMining(results);
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

	@Override
	public String getRawTransaction(String transactionId, boolean verbose)
			throws BtcException {
		// TODO
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BtcException.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	public double getReceivedByAccount(String account) throws BtcException {
		return getReceivedByAccount(account, 1);
	}

	@Override
	public double getReceivedByAccount(String account, int minConfirms)
			throws BtcException {
		if (account == null) {
			account = "";
		}
		if (minConfirms < 1) {
			minConfirms = 1;
		}
		JsonArray parameters = Json.createArrayBuilder().add(account)
				.add(minConfirms).build();
		JsonNumber results = (JsonNumber) invoke(
				BTCAPI_GET_RECEIVED_BY_ACCOUNT, parameters);
		return results.doubleValue();
	}

	public double getReceivedByAddress(String address) throws BtcException {
		return getReceivedByAddress(address, 1);
	}

	@Override
	public double getReceivedByAddress(String address, int minConfirms)
			throws BtcException {
		if (address == null) {
			address = "";
		}
		if (minConfirms < 1) {
			minConfirms = 1;
		}
		JsonArray parameters = Json.createArrayBuilder().add(address)
				.add(minConfirms).build();
		JsonNumber results = (JsonNumber) invoke(
				BTCAPI_GET_RECEIVED_BY_ADDRESS, parameters);
		return results.doubleValue();
	}

	@Override
	public BtcTransaction getTransaction(String transactionId)
			throws BtcException {
		if (transactionId == null) {
			transactionId = "";
		}
		JsonArray parameters = Json.createArrayBuilder().add(transactionId)
				.build();
		JsonObject results = (JsonObject) invoke(BTCAPI_GET_TRANSACTION,
				parameters);
		return jsonTransaction(results);
	}

	@Override
	// TODO
	public String getTransactionOutput(String transactionId, int n,
			boolean includeMemoryPool) throws BtcException {
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BtcException.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
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
		JsonArray parameters = null;
		if ((command != null) && (command.length() > 0)) {
			parameters = Json.createArrayBuilder().add(command).build();
		}
		JsonString results = (JsonString) invoke(BTCAPI_HELP, parameters);
		return results.getString();
	}

	public String importPrivateKey(String privateKey) throws BtcException {
		return importPrivateKey(privateKey, "", true);
	}

	@Override
	// TODO
	public String importPrivateKey(String privateKey, String label,
			boolean reScan) throws BtcException {
		if (privateKey == null) {
			privateKey = "";
		}
		if (label == null) {
			label = "";
		}
		JsonArray parameters = Json.createArrayBuilder().add(privateKey)
				.add(label).add(reScan).build();
		JsonValue results = invoke(BTCAPI_IMPORT_PRIVATE_KEY, parameters);
		return String.valueOf(results);
	}

	@Override
	public void keyPoolRefill() throws BtcException {
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BtcException.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	public List<BtcAccount> listAccounts() throws BtcException {
		return listAccounts(1);
	}

	@Override
	public List<BtcAccount> listAccounts(int minConfirms) throws BtcException {
		if (minConfirms < 1) {
			minConfirms = 1;
		}
		JsonArray parameters = Json.createArrayBuilder().add(minConfirms)
				.build();
		JsonObject results = (JsonObject) invoke(BTCAPI_LIST_ACCOUNTS,
				parameters);
		List<BtcAccount> accounts = new ArrayList<BtcAccount>();
		for (String result : results.keySet()) {
			BtcAccount account = new BtcAccount();
			account.setAccount(result);
			JsonNumber amount = results.getJsonNumber(result);
			if (amount != null) {
				account.setAmount(amount.doubleValue());
			}
			accounts.add(account);
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
					address.setAmount(amount.doubleValue());
				}
				BtcAccount account = new BtcAccount();
				account.setAccount(grouping.getString(2));
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
	public List<BtcAccount> listReceivedByAccount(int minConfirms,
			boolean includeEmpty) throws BtcException {
		if (minConfirms < 0) {
			minConfirms = 1;
		}
		JsonArray parameters = Json.createArrayBuilder().add(minConfirms)
				.add(includeEmpty).build();
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
	public List<BtcAddress> listReceivedByAddress(int minConfirms,
			boolean includeEmpty) throws BtcException {
		if (minConfirms < 0) {
			minConfirms = 1;
		}
		JsonArray parameters = Json.createArrayBuilder().add(minConfirms)
				.add(includeEmpty).build();
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
	public BtcLastBlock listSinceBlock(String blockHash, int targetConfirms)
			throws BtcException {
		if (blockHash == null) {
			blockHash = "";
		}
		if (targetConfirms < 1) {
			targetConfirms = 1;
		}
		JsonArray parameters = Json.createArrayBuilder().add(blockHash)
				.add(targetConfirms).build();
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

	public List<BtcTransaction> listTransactions(String account, int count)
			throws BtcException {
		return listTransactions(account, count, 0);
	}

	@Override
	public List<BtcTransaction> listTransactions(String account, int count,
			int from) throws BtcException {
		if (account == null) {
			account = "";
		}
		if (count < 1) {
			count = 10;
		}
		if (from < 0) {
			from = 0;
		}
		JsonArray parameters = Json.createArrayBuilder().add(account)
				.add(count).add(from).build();
		JsonArray results = (JsonArray) invoke(BTCAPI_LIST_TRANSACTIONS,
				parameters);
		List<BtcTransaction> transactions = new ArrayList<BtcTransaction>();
		for (JsonObject result : results.getValuesAs(JsonObject.class)) {
			transactions.add(jsonTransaction(result));
		}
		return transactions;
	}

	@Override
	public List<String> listUnspent(int minConfirms, int maxConfirms)
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

	@Override
	public void move(String fromAccount, String toAccount, double amount,
			int minConfirms, String comment) throws BtcException {
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BtcException.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public String sendFrom(String fromAccount, String toAddress, double amount,
			int minConfirms, String commentFrom, String commentTo)
			throws BtcException {
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BtcException.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public String sendMany(String fromAccount, List<Object> addresses,
			int minConfirms, String commentFrom, String commentTo)
			throws BtcException {
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BtcException.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public void sendRawTransaction(String transactionId) throws BtcException {
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BtcException.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public String sendToAddress(String toAddress, double amount,
			String commentFrom, String commentTo) throws BtcException {
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BtcException.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public void setAccount(String address, String account) throws BtcException {
		if (address == null) {
			address = "";
		}
		if (account == null) {
			account = "";
		}
		JsonArray parameters = Json.createArrayBuilder().add(address)
				.add(account).build();
		invoke(BTCAPI_SET_ACCOUNT, parameters);
	}

	@Override
	public void setGenerate(boolean generate, int generateProcessorsLimit)
			throws BtcException {
		if (generateProcessorsLimit < 1) {
			generateProcessorsLimit = -1;
		}
		JsonArray parameters = Json.createArrayBuilder().add(generate)
				.add(generateProcessorsLimit).build();
		invoke(BTCAPI_SET_GENERATE, parameters);
	}

	public void setGenerate(boolean generate) throws BtcException {
		setGenerate(generate, -1);
	}

	@Override
	public boolean setTransactionFee(double amount) throws BtcException {
		if (amount < 0) {
			amount = 0;
		}
		JsonArray parameters = Json.createArrayBuilder().add(amount).build();
		JsonValue results = invoke(BTCAPI_SET_TRANSACTION_FEE, parameters);
		return Boolean.valueOf(String.valueOf(results));
	}

	@Override
	public void signMessage(String address, String message) throws BtcException {
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BtcException.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
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
		if (address == null) {
			address = "";
		}
		JsonArray parameters = Json.createArrayBuilder().add(address).build();
		JsonObject results = (JsonObject) invoke(BTCAPI_VALIDATE_ADDRESS,
				parameters);
		return jsonAddress(results);
	}

	@Override
	public String verifyMessage(String address, String signature, String message)
			throws BtcException {
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BtcException.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}
}