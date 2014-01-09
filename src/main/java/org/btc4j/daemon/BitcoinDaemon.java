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
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.btc4j.core.BitcoinAccount;
import org.btc4j.core.BitcoinAddress;
import org.btc4j.core.BitcoinApi;
import org.btc4j.core.BitcoinBlock;
import org.btc4j.core.BitcoinConstant;
import org.btc4j.core.BitcoinException;
import org.btc4j.core.BitcoinMining;
import org.btc4j.core.BitcoinNodeOperationEnum;
import org.btc4j.core.BitcoinPeer;
import org.btc4j.core.BitcoinStatus;
import org.btc4j.core.BitcoinTransaction;
import org.btc4j.core.BitcoinTransactionOutputSet;

public class BitcoinDaemon implements BitcoinApi {

	private final static Logger LOGGER = Logger.getLogger(BitcoinDaemon.class
			.getName());
	private final HttpState state;
	private final HttpClientParams params;
	private final URL url;

	private BitcoinDaemon(URL url, String account, String password,
			int timeoutInMillis) {
		this.url = url;
		state = new HttpState();
		state.setCredentials(new AuthScope(url.getHost(), url.getPort()),
				new UsernamePasswordCredentials(account, password));
		params = new HttpClientParams();
		params.setConnectionManagerTimeout(timeoutInMillis);
	}

	private static BitcoinDaemon makeDaemon(String host, int port,
			String account, String password, int timeoutInMillis,
			Process bitcoind) throws BitcoinException {
		URL url;
		try {
			url = new URL(BitcoinDaemonConstant.BTC4J_DAEMON_HTTP + "://" + host + ":"
					+ port);
		} catch (MalformedURLException e) {
			LOGGER.severe(String.valueOf(e));
			throw new BitcoinException(
					BitcoinConstant.BTC4J_ERROR_CODE,
					BitcoinConstant.BTC4J_ERROR_MESSAGE + ": " + e.getMessage(),
					e);
		}
		BitcoinDaemon daemon = new BitcoinDaemon(url, account, password,
				timeoutInMillis);
		int attempts = 0;
		boolean ping = false;
		String message = "";
		do {
			attempts++;
			try {
				LOGGER.info("attempt " + attempts + " of "
						+ BitcoinDaemonConstant.BTC4J_DAEMON_CONNECT_ATTEMPTS
						+ " to ping " + url);
				Thread.sleep(attempts * timeoutInMillis);
				BitcoinStatus info = daemon.getInformation();
				if (info != null) {
					ping = true;
					message = "connected bitcoind " + info.getVersion()
							+ " on " + url + " as " + account;
				}
			} catch (InterruptedException | BitcoinException e) {
				message = e.getMessage();
			}
		} while (!ping
				&& (attempts < BitcoinDaemonConstant.BTC4J_DAEMON_CONNECT_ATTEMPTS));
		if (!ping) {
			daemon = null;
			if (bitcoind != null) {
				bitcoind.destroy();
			}
			LOGGER.severe(message);
			throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
					BitcoinConstant.BTC4J_ERROR_MESSAGE + ": " + message);
		}
		LOGGER.info(message);
		return daemon;
	}

	public static BitcoinDaemon connectDaemon(String host, int port,
			final String account, final String password, int timeoutInMillis)
			throws BitcoinException {
		return makeDaemon(host, port, account, password, timeoutInMillis, null);
	}

	public static BitcoinDaemon runDaemon(File bitcoind, boolean testnet,
			String account, String password, int timeoutInMillis)
			throws BitcoinException {
		try {
			List<String> args = new ArrayList<String>();
			args.add(bitcoind.getCanonicalPath());
			if (testnet) {
				args.add(BitcoinDaemonConstant.BTC4J_DAEMON_ARG_TESTNET);
			}
			args.add(BitcoinDaemonConstant.BTC4J_DAEMON_ARG_ACCOUNT + account);
			args.add(BitcoinDaemonConstant.BTC4J_DAEMON_ARG_PASSWORD + password);
			LOGGER.info("args: " + args);
			return makeDaemon(BitcoinDaemonConstant.BTC4J_DAEMON_HOST,
					BitcoinDaemonConstant.BTC4J_DAEMON_PORT, account, password,
					timeoutInMillis, new ProcessBuilder(args).start());
		} catch (IOException e) {
			LOGGER.severe(String.valueOf(e));
			throw new BitcoinException(
					BitcoinConstant.BTC4J_ERROR_CODE,
					BitcoinConstant.BTC4J_ERROR_MESSAGE + ": " + e.getMessage(),
					e);
		}
	}

	public String[] getSupportedVersions() {
		return BitcoinDaemonConstant.BTC4J_DAEMON_VERSIONS;
	}

	protected JsonValue invoke(String method) throws BitcoinException {
		return invoke(method, null);
	}

	protected JsonValue invoke(String method, JsonValue parameters)
			throws BitcoinException {
		if (url == null) {
			LOGGER.severe(BitcoinDaemonConstant.BTC4J_DAEMON_DATA_NULL_URL);
			throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
					BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
							+ BitcoinDaemonConstant.BTC4J_DAEMON_DATA_NULL_URL);
		}
		PostMethod post = new PostMethod(url.toString());
		try {
			post.setRequestHeader(BitcoinDaemonConstant.BTC4J_DAEMON_HTTP_HEADER,
					BitcoinDaemonConstant.BTC4J_DAEMON_JSONRPC_CONTENT_TYPE);
			String guid = UUID.randomUUID().toString();
			JsonObjectBuilder builder = Json.createObjectBuilder();
			builder.add(BitcoinDaemonConstant.JSONRPC_JSONRPC,
					BitcoinDaemonConstant.JSONRPC_VERSION).add(
							BitcoinDaemonConstant.JSONRPC_METHOD, method);
			if (parameters != null) {
				builder.add(BitcoinDaemonConstant.JSONRPC_PARAMS, parameters);
			} else {
				builder.addNull(BitcoinDaemonConstant.JSONRPC_PARAMS);
			}
			builder.add(BitcoinDaemonConstant.JSONRPC_ID, guid);
			JsonObject request = builder.build();
			LOGGER.info("request: " + request);
			post.setRequestEntity(new StringRequestEntity(request.toString(),
					BitcoinDaemonConstant.BTC4J_DAEMON_JSON_CONTENT_TYPE, null));
			HttpClient client = new HttpClient();
			client.setState(state);
			client.setParams(params);
			int status = client.executeMethod(post);
			if (status != HttpStatus.SC_OK) {
				LOGGER.severe(status + " " + HttpStatus.getStatusText(status));
				throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
						BitcoinConstant.BTC4J_ERROR_MESSAGE + ": " + status
								+ " " + HttpStatus.getStatusText(status));
			}
			JsonObject response = (JsonObject) Json.createReader(
					new StringReader(post.getResponseBodyAsString())).read();
			if (response == null) {
				LOGGER.severe(BitcoinDaemonConstant.BTC4J_DAEMON_DATA_NULL_RESPONSE);
				throw new BitcoinException(
						BitcoinConstant.BTC4J_ERROR_CODE,
						BitcoinConstant.BTC4J_ERROR_MESSAGE
								+ ": "
								+ BitcoinDaemonConstant.BTC4J_DAEMON_DATA_NULL_RESPONSE);
			}
			LOGGER.info("response: " + response);
			JsonString id = response.getJsonString(BitcoinDaemonConstant.JSONRPC_ID);
			if (id == null) {
				JsonObject error = response
						.getJsonObject(BitcoinDaemonConstant.JSONRPC_ERROR);
				if (error != null) {
					JsonObject data = error
							.getJsonObject(BitcoinDaemonConstant.JSONRPC_DATA);
					LOGGER.severe(String.valueOf(data));
					throw new BitcoinException(
							error.getInt(BitcoinDaemonConstant.JSONRPC_CODE),
							error.get(BitcoinDaemonConstant.JSONRPC_MESSAGE) + ": "
									+ data);
				} else {
					LOGGER.severe(BitcoinDaemonConstant.BTC4J_DAEMON_DATA_INVALID_ERROR);
					throw new BitcoinException(
							BitcoinConstant.BTC4J_ERROR_CODE,
							BitcoinConstant.BTC4J_ERROR_MESSAGE
									+ ": "
									+ BitcoinDaemonConstant.BTC4J_DAEMON_DATA_INVALID_ERROR);
				}
			}
			if (!guid.equals(id.getString())) {
				LOGGER.severe(BitcoinDaemonConstant.BTC4J_DAEMON_DATA_INVALID_ID);
				throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
						BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
								+ BitcoinDaemonConstant.BTC4J_DAEMON_DATA_INVALID_ID);
			}
			return response.get(BitcoinDaemonConstant.JSONRPC_RESULT);
		} catch (NullPointerException | ClassCastException | IOException e) {
			LOGGER.severe(String.valueOf(e));
			throw new BitcoinException(
					BitcoinConstant.BTC4J_ERROR_CODE,
					BitcoinConstant.BTC4J_ERROR_MESSAGE + ": " + e.getMessage(),
					e);
		} finally {
			post.releaseConnection();
		}
	}
	
	protected BitcoinAccount jsonBitcoinAccount(JsonObject value) {
		BitcoinAccount account = new BitcoinAccount();
		account.setAccount(value.getString(
				BitcoinDaemonConstant.BTCOBJ_ACCOUNT_ACCOUNT, ""));
		JsonNumber amount = value
				.getJsonNumber(BitcoinDaemonConstant.BTCOBJ_ACCOUNT_AMOUNT);
		if (amount != null) {
			account.setAmount(amount.doubleValue());
		}
		account.setConfirmations(value.getInt(
				BitcoinDaemonConstant.BTCOBJ_ACCOUNT_CONFIRMATIONS, 0));
		return account;
	}
	
	protected BitcoinAddress jsonBitcoinAddress(JsonObject value) {
		BitcoinAddress address = new BitcoinAddress();
		address.setValid(value.getBoolean(BitcoinDaemonConstant.BTCOBJ_ADDRESS_VALID,
				false));
		address.setAddress(value.getString(
				BitcoinDaemonConstant.BTCOBJ_ADDRESS_ADDRESS, ""));
		address.setMine(value.getBoolean(BitcoinDaemonConstant.BTCOBJ_ADDRESS_MINE,
				false));
		address.setScript(value.getBoolean(
				BitcoinDaemonConstant.BTCOBJ_ADDRESS_SCRIPT, false));
		address.setPublicKey(value.getString(
				BitcoinDaemonConstant.BTCOBJ_ADDRESS_PUBLIC_KEY, ""));
		address.setCompressed(value.getBoolean(
				BitcoinDaemonConstant.BTCOBJ_ADDRESS_COMPRESSED, false));
		BitcoinAccount account = new BitcoinAccount();
		account.setAccount(value.getString(
				BitcoinDaemonConstant.BTCOBJ_ADDRESS_ACCOUNT, ""));
		address.setAccount(account);
		JsonNumber amount = value
				.getJsonNumber(BitcoinDaemonConstant.BTCOBJ_ADDRESS_AMOUNT);
		if (amount != null) {
			address.setAmount(amount.doubleValue());
		}
		address.setConfirmations(value.getInt(
				BitcoinDaemonConstant.BTCOBJ_ADDRESS_CONFIRMATIONS, 0));
		return address;
	}

	protected BitcoinBlock jsonBitcoinBlock(JsonObject value)
			throws BitcoinException {
		BitcoinBlock block = new BitcoinBlock();
		block.setHash(value.getString(BitcoinDaemonConstant.BTCOBJ_BLOCK_HASH, ""));
		block.setConfirmations(value.getInt(
				BitcoinDaemonConstant.BTCOBJ_BLOCK_CONFIRMATIONS, 0));
		block.setSize(value.getInt(BitcoinDaemonConstant.BTCOBJ_BLOCK_SIZE, 0));
		block.setHeight(value.getInt(BitcoinDaemonConstant.BTCOBJ_BLOCK_HEIGHT, 0));
		block.setVersion(value.getInt(BitcoinDaemonConstant.BTCOBJ_BLOCK_VERSION, 0));
		block.setMerkleRoot(value.getString(
				BitcoinDaemonConstant.BTCOBJ_BLOCK_MERKLE_ROOT, ""));
		List<BitcoinTransaction> transactions = new ArrayList<BitcoinTransaction>();
		JsonArray transactionIds = value
				.getJsonArray(BitcoinDaemonConstant.BTCOBJ_BLOCK_TRANSACTIONS);
		if (transactionIds != null) {
			for (JsonString transactionId : transactionIds
					.getValuesAs(JsonString.class)) {
				BitcoinTransaction transaction = new BitcoinTransaction();
				transaction.setTransactionId(transactionId
						.getString());
				transactions.add(transaction);
			}
		}
		block.setTransactions(transactions);
		block.setTime(value.getInt(BitcoinDaemonConstant.BTCOBJ_BLOCK_TIME, 0));
		block.setNonce(value.getInt(BitcoinDaemonConstant.BTCOBJ_BLOCK_NONCE, 0));
		block.setBits(value.getString(BitcoinDaemonConstant.BTCOBJ_BLOCK_BITS, ""));
		JsonNumber difficulty = value
				.getJsonNumber(BitcoinDaemonConstant.BTCOBJ_BLOCK_DIFFICULTY);
		if (difficulty != null) {
			block.setDifficulty(difficulty.doubleValue());
		}
		block.setPreviousBlockHash(value.getString(
				BitcoinDaemonConstant.BTCOBJ_BLOCK_PREVIOUS_BLOCK_HASH, ""));
		block.setNextBlockHash(value.getString(
				BitcoinDaemonConstant.BTCOBJ_BLOCK_NEXT_BLOCK_HASH, ""));
		return block;
	}
	
	protected BitcoinMining jsonBitcoinMining(JsonObject value)
			throws BitcoinException {
		BitcoinMining info = new BitcoinMining();
		info.setBlocks(value.getInt(BitcoinDaemonConstant.BTCOBJ_INFO_BLOCKS, 0));
		info.setCurrentBlockSize(value.getInt(
				BitcoinDaemonConstant.BTCOBJ_INFO_CURRENT_BLOCK_SIZE, 0));
		info.setCurrentBlockTransactions(value.getInt(
				BitcoinDaemonConstant.BTCOBJ_INFO_CURRENT_BLOCK_TRANSACTIONS, 0));
		JsonNumber difficulty = value
				.getJsonNumber(BitcoinDaemonConstant.BTCOBJ_INFO_DIFFICULTY);
		if (difficulty != null) {
			info.setDifficulty(difficulty.doubleValue());
		}
		info.setErrors(value.getString(BitcoinDaemonConstant.BTCOBJ_INFO_ERRORS, ""));
		info.setGenerate(value.getBoolean(BitcoinDaemonConstant.BTCOBJ_INFO_GENERATE,
				false));
		info.setGenProcessorLimit(value.getInt(
				BitcoinDaemonConstant.BTCOBJ_INFO_PROCESSOR_LIMIT, -1));
		info.setHashesPerSecond(value.getInt(
				BitcoinDaemonConstant.BTCOBJ_INFO_HASHES_PER_SECOND, 0));
		info.setPooledTransactions(value.getInt(
				BitcoinDaemonConstant.BTCOBJ_INFO_POOLED_TRANSACTIONS, 0));
		info.setTestnet(value.getBoolean(BitcoinDaemonConstant.BTCOBJ_INFO_TESTNET,
				false));
		return info;
	}
	
	protected BitcoinPeer jsonBitcoinPeer(JsonObject value)
			throws BitcoinException {
		BitcoinPeer peer = new BitcoinPeer();
		peer.setNetworkAddress(value
				.getString(BitcoinDaemonConstant.BTCOBJ_PEER_ADDRESS, ""));
		peer.setServices(value.getString(BitcoinDaemonConstant.BTCOBJ_PEER_SERVICES,
				""));
		peer.setLastSend(value.getInt(BitcoinDaemonConstant.BTCOBJ_PEER_LAST_SEND, 0));
		peer.setLastReceived(value.getInt(
				BitcoinDaemonConstant.BTCOBJ_PEER_LAST_RECEIVED, 0));
		peer.setBytesSent(value.getInt(BitcoinDaemonConstant.BTCOBJ_PEER_BYTES_SENT,
				0));
		peer.setBytesReceived(value.getInt(
				BitcoinDaemonConstant.BTCOBJ_PEER_BYTES_RECEIVED, 0));
		peer.setConnectionTime(value.getInt(
				BitcoinDaemonConstant.BTCOBJ_PEER_CONNECTION_TIME, 0));
		peer.setVersion(value.getInt(BitcoinDaemonConstant.BTCOBJ_PEER_VERSION, 0));
		peer.setSubVersion(value.getString(
				BitcoinDaemonConstant.BTCOBJ_PEER_SUBVERSION, ""));
		peer.setInbound(value.getBoolean(BitcoinDaemonConstant.BTCOBJ_PEER_INBOUND,
				false));
		peer.setStartingHeight(value.getInt(
				BitcoinDaemonConstant.BTCOBJ_PEER_START_HEIGHT, 0));
		peer.setBanScore(value.getInt(BitcoinDaemonConstant.BTCOBJ_PEER_BAN_SCORE, 0));
		peer.setSyncNode(value.getBoolean(
				BitcoinDaemonConstant.BTCOBJ_PEER_SYNC_NODE, false));
		return peer;
	}
	
	protected BitcoinTransactionOutputSet jsonBitcoinTransactionOutputSet(JsonObject value)
			throws BitcoinException {
		BitcoinTransactionOutputSet output = new BitcoinTransactionOutputSet();
		output.setHeight(value.getInt(
				BitcoinDaemonConstant.BTCOBJ_TXOUTPUTSET_HEIGHT, 0));
		output.setBestBlock(value.getString(
				BitcoinDaemonConstant.BTCOBJ_TXOUTPUTSET_BEST_BLOCK, ""));
		output.setTransactions(value.getInt(
				BitcoinDaemonConstant.BTCOBJ_TXOUTPUTSET_TRANSACTIONS, 0));
		output.setOutputTransactions(value.getInt(
				BitcoinDaemonConstant.BTCOBJ_TXOUTPUTSET_OUTPUT_TRANSACTIONS, 0));
		output.setBytesSerialized(value.getInt(
				BitcoinDaemonConstant.BTCOBJ_TXOUTPUTSET_BYTES_SERIALIZED, 0));
		output.setHashSerialized(value.getString(
				BitcoinDaemonConstant.BTCOBJ_TXOUTPUTSET_HASH_SERIALIZED, ""));
		JsonNumber amount = value
				.getJsonNumber(BitcoinDaemonConstant.BTCOBJ_TXOUTPUTSET_TOTAL_AMOUT);
		if (amount != null) {
			output.setTotalAmount(amount.doubleValue());
		}
		return output;
	}
	
	protected BitcoinStatus jsonBitcoinStatus(JsonObject value)
			throws BitcoinException {
		BitcoinStatus info = new BitcoinStatus();
		info.setVersion(value.getInt(BitcoinDaemonConstant.BTCOBJ_INFO_VERSION, 0));
		info.setProtocolVersion(value.getInt(
				BitcoinDaemonConstant.BTCOBJ_INFO_PROTOCOL_VERSION, 0));
		info.setWalletVersion(value.getInt(
				BitcoinDaemonConstant.BTCOBJ_INFO_WALLET_VERSION, 0));
		JsonNumber balance = value
				.getJsonNumber(BitcoinDaemonConstant.BTCOBJ_INFO_BALANCE);
		if (balance != null) {
			info.setBalance(balance.doubleValue());
		}
		info.setBlocks(value.getInt(BitcoinDaemonConstant.BTCOBJ_INFO_BLOCKS, 0));
		info.setTimeOffset(value.getInt(
				BitcoinDaemonConstant.BTCOBJ_INFO_TIME_OFFSET, 0));
		info.setConnections(value.getInt(
				BitcoinDaemonConstant.BTCOBJ_INFO_CONNECTIONS, 0));
		info.setProxy(value.getString(BitcoinDaemonConstant.BTCOBJ_INFO_PROXY, ""));
		JsonNumber difficulty = value
				.getJsonNumber(BitcoinDaemonConstant.BTCOBJ_INFO_DIFFICULTY);
		if (difficulty != null) {
			info.setDifficulty(difficulty.doubleValue());
		}
		info.setTestnet(value.getBoolean(BitcoinDaemonConstant.BTCOBJ_INFO_TESTNET,
				false));
		info.setKeyPoolOldest(value.getInt(
				BitcoinDaemonConstant.BTCOBJ_INFO_KEYPOOL_OLDEST, 0));
		info.setKeyPoolSize(value.getInt(
				BitcoinDaemonConstant.BTCOBJ_INFO_KEYPOOL_SIZE, 0));
		JsonNumber transactionFee = value
				.getJsonNumber(BitcoinDaemonConstant.BTCOBJ_INFO_TRANSACTION_FEE);
		if (transactionFee != null) {
			info.setTransactionFee(transactionFee.doubleValue());
		}
		info.setErrors(value.getString(BitcoinDaemonConstant.BTCOBJ_INFO_ERRORS, ""));
		return info;
	}
	
	public void addMultiSignatureAddress(int required, List<String> keys)
			throws BitcoinException {
		addMultiSignatureAddress(required, keys, "");
	}

	@Override
	public void addMultiSignatureAddress(int required, List<String> keys,
			String account) throws BitcoinException {
		throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
				BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
						+ BitcoinConstant.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public void addNode(String node, BitcoinNodeOperationEnum operation)
			throws BitcoinException {
		throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
				BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
						+ BitcoinConstant.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public void backupWallet(File destination) throws BitcoinException {
		if (destination == null) {
			destination = new File(".");
		}
		JsonArray parameters = Json.createArrayBuilder()
				.add(destination.toString()).build();
		invoke(BitcoinDaemonConstant.BTCAPI_BACKUP_WALLET, parameters);
	}

	@Override
	public String createMultiSignatureAddress(int required, List<String> keys)
			throws BitcoinException {
		throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
				BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
						+ BitcoinConstant.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public String createRawTransaction(List<Object> transactionIds,
			List<Object> addresses) throws BitcoinException {
		throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
				BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
						+ BitcoinConstant.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public String decodeRawTransaction(String transactionId)
			throws BitcoinException {
		throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
				BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
						+ BitcoinConstant.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public String dumpPrivateKey(String address) throws BitcoinException {
		// TODO
		throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
				BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
						+ BitcoinConstant.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public void encryptWallet(String passPhrase) throws BitcoinException {
		// TODO
		throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
				BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
						+ BitcoinConstant.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public String getAccount(String address) throws BitcoinException {
		if (address == null) {
			address = "";
		}
		JsonArray parameters = Json.createArrayBuilder().add(address).build();
		JsonString resultss = (JsonString) invoke(
				BitcoinDaemonConstant.BTCAPI_GET_ACCOUNT, parameters);
		return resultss.getString();
	}

	@Override
	public String getAccountAddress(String account) throws BitcoinException {
		if (account == null) {
			account = "";
		}
		JsonArray parameters = Json.createArrayBuilder().add(account).build();
		JsonString results = (JsonString) invoke(
				BitcoinDaemonConstant.BTCAPI_GET_ACCOUNT_ADDRESS, parameters);
		return results.getString();
	}

	@Override
	public String getAddedNodeInformation(boolean dns, String node)
			throws BitcoinException {
		throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
				BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
						+ BitcoinConstant.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public List<String> getAddressesByAccount(String account)
			throws BitcoinException {
		if (account == null) {
			account = "";
		}
		JsonArray parameters = Json.createArrayBuilder().add(account).build();
		JsonArray results = (JsonArray) invoke(
				BitcoinDaemonConstant.BTCAPI_GET_ADDRESSES_BY_ACCOUNT, parameters);

		List<String> addresses = new ArrayList<String>();
		for (JsonString result : results.getValuesAs(JsonString.class)) {
			addresses.add(result.getString());
		}
		return addresses;
	}

	public double getBalance() throws BitcoinException {
		return getBalance("", 1);
	}

	public double getBalance(int minConfirms) throws BitcoinException {
		return getBalance("", minConfirms);
	}

	public double getBalance(String account) throws BitcoinException {
		return getBalance(account, 1);
	}

	@Override
	public double getBalance(String account, int minConfirms)
			throws BitcoinException {
		if (account == null) {
			account = "";
		}
		if (minConfirms < 1) {
			minConfirms = 1;
		}
		JsonArray parameters = Json.createArrayBuilder().add(account)
				.add(minConfirms).build();
		JsonNumber results = (JsonNumber) invoke(
				BitcoinDaemonConstant.BTCAPI_GET_BALANCE, parameters);
		return results.doubleValue();
	}

	@Override
	public BitcoinBlock getBlock(String hash) throws BitcoinException {
		if (hash == null) {
			hash = "";
		}
		JsonArray parameters = Json.createArrayBuilder().add(hash).build();
		JsonObject results = (JsonObject) invoke(
				BitcoinDaemonConstant.BTCAPI_GET_BLOCK, parameters);
		return jsonBitcoinBlock(results);
	}

	@Override
	public int getBlockCount() throws BitcoinException {
		JsonNumber results = (JsonNumber) invoke(BitcoinDaemonConstant.BTCAPI_GET_BLOCK_COUNT);
		return results.intValue();
	}

	@Override
	public String getBlockHash(int index) throws BitcoinException {
		if (index < 0) {
			index = 0;
		}
		JsonArray parameters = Json.createArrayBuilder().add(index).build();
		JsonString results = (JsonString) invoke(
				BitcoinDaemonConstant.BTCAPI_GET_BLOCK_HASH, parameters);
		return results.getString();
	}

	@Override
	public String getBlockTemplate(String params) throws BitcoinException {
		throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
				BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
						+ BitcoinConstant.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public int getConnectionCount() throws BitcoinException {
		JsonNumber results = (JsonNumber) invoke(BitcoinDaemonConstant.BTCAPI_GET_CONNECTION_COUNT);
		return results.intValue();
	}

	@Override
	public double getDifficulty() throws BitcoinException {
		JsonNumber results = (JsonNumber) invoke(BitcoinDaemonConstant.BTCAPI_GET_DIFFICULTY);
		return results.doubleValue();
	}

	@Override
	public boolean getGenerate() throws BitcoinException {
		JsonValue results = invoke(BitcoinDaemonConstant.BTCAPI_GET_GENERATE);
		return Boolean.valueOf(String.valueOf(results));
	}

	@Override
	public int getHashesPerSecond() throws BitcoinException {
		JsonNumber results = (JsonNumber) invoke(BitcoinDaemonConstant.BTCAPI_GET_HASHES_PER_SECOND);
		return results.intValue();
	}

	@Override
	public BitcoinStatus getInformation() throws BitcoinException {
		JsonObject results = (JsonObject) invoke(BitcoinDaemonConstant.BTCAPI_GET_INFORMATION);
		return jsonBitcoinStatus(results);
	}

	@Override
	public BitcoinMining getMiningInformation() throws BitcoinException {
		JsonObject results = (JsonObject) invoke(BitcoinDaemonConstant.BTCAPI_GET_MINING_INFORMATION);
		return jsonBitcoinMining(results);
	}

	public String getNewAddress() throws BitcoinException {
		return getNewAddress("");
	}

	@Override
	public String getNewAddress(String account) throws BitcoinException {
		JsonArray parameters = null;
		if ((account != null) && (account.length() > 0)) {
			parameters = Json.createArrayBuilder().add(account).build();
		}
		JsonString results = (JsonString) invoke(
				BitcoinDaemonConstant.BTCAPI_GET_NEW_ADDRESS, parameters);
		return results.getString();
	}

	@Override
	public List<BitcoinPeer> getPeerInformation() throws BitcoinException {
		JsonArray results = (JsonArray) invoke(BitcoinDaemonConstant.BTCAPI_GET_PEER_INFORMATION);
		List<BitcoinPeer> peers = new ArrayList<BitcoinPeer>();
		for (JsonObject result : results.getValuesAs(JsonObject.class)) {
			peers.add(jsonBitcoinPeer(result));
		}
		return peers;
	}

	@Override
	public List<String> getRawMemoryPool() throws BitcoinException {
		JsonArray results = (JsonArray) invoke(BitcoinDaemonConstant.BTCAPI_GET_RAW_MEMORY_POOL);
		List<String> rawMemPool = new ArrayList<String>();
		for (JsonString result : results.getValuesAs(JsonString.class)) {
			rawMemPool.add(result.getString());
		}
		return rawMemPool;
	}

	@Override
	public String getRawTransaction(String transactionId, boolean verbose)
			throws BitcoinException {
		// TODO
		throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
				BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
						+ BitcoinConstant.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	public double getReceivedByAccount(String account) throws BitcoinException {
		return getReceivedByAccount(account, 1);
	}

	@Override
	public double getReceivedByAccount(String account, int minConfirms)
			throws BitcoinException {
		if (account == null) {
			account = "";
		}
		if (minConfirms < 1) {
			minConfirms = 1;
		}
		JsonArray parameters = Json.createArrayBuilder().add(account)
				.add(minConfirms).build();
		JsonNumber results = (JsonNumber) invoke(
				BitcoinDaemonConstant.BTCAPI_GET_RECEIVED_BY_ACCOUNT, parameters);
		return results.doubleValue();
	}

	public double getReceivedByAddress(String address) throws BitcoinException {
		return getReceivedByAddress(address, 1);
	}

	@Override
	public double getReceivedByAddress(String address, int minConfirms)
			throws BitcoinException {
		if (address == null) {
			address = "";
		}
		if (minConfirms < 1) {
			minConfirms = 1;
		}
		JsonArray parameters = Json.createArrayBuilder().add(address)
				.add(minConfirms).build();
		JsonNumber results = (JsonNumber) invoke(
				BitcoinDaemonConstant.BTCAPI_GET_RECEIVED_BY_ADDRESS, parameters);
		return results.doubleValue();
	}

	@Override
	public String getTransaction(String transactionId) throws BitcoinException {
		// TODO
		if (transactionId == null) {
			transactionId = "";
		}
		JsonArray parameters = Json.createArrayBuilder().add(transactionId)
				.build();
		JsonValue results = invoke(BitcoinDaemonConstant.BTCAPI_GET_TRANSACTION,
				parameters);
		return String.valueOf(results);
	}

	@Override
	public String getTransactionOutput(String transactionId, int n,
			boolean includeMemoryPool) throws BitcoinException {
		throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
				BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
						+ BitcoinConstant.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public BitcoinTransactionOutputSet getTransactionOutputSetInformation()
			throws BitcoinException {
		JsonObject results = (JsonObject) invoke(BitcoinDaemonConstant.BTCAPI_GET_TRANSACTION_OUTPUT_SET_INFORMATION);
		return jsonBitcoinTransactionOutputSet(results);
	}

	@Override
	public String getWork(String data) throws BitcoinException {
		throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
				BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
						+ BitcoinConstant.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	public String help() throws BitcoinException {
		return help("");
	}

	@Override
	public String help(String command) throws BitcoinException {
		JsonArray parameters = null;
		if ((command != null) && (command.length() > 0)) {
			parameters = Json.createArrayBuilder().add(command).build();
		}
		JsonString results = (JsonString) invoke(BitcoinDaemonConstant.BTCAPI_HELP,
				parameters);
		return results.getString();
	}

	@Override
	public String importPrivateKey(String privateKey, String label,
			boolean reScan) throws BitcoinException {
		throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
				BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
						+ BitcoinConstant.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public void keyPoolRefill() throws BitcoinException {
		throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
				BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
						+ BitcoinConstant.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	public Map<String, BitcoinAccount> listAccounts() throws BitcoinException {
		return listAccounts(1);
	}

	@Override
	public Map<String, BitcoinAccount> listAccounts(int minConfirms)
			throws BitcoinException {
		if (minConfirms < 1) {
			minConfirms = 1;
		}
		JsonArray parameters = Json.createArrayBuilder().add(minConfirms)
				.build();
		JsonObject results = (JsonObject) invoke(
				BitcoinDaemonConstant.BTCAPI_LIST_ACCOUNTS, parameters);
		Map<String, BitcoinAccount> accounts = new HashMap<String, BitcoinAccount>();
		for (String account : results.keySet()) {
			JsonNumber amount = results.getJsonNumber(account);
			BitcoinAccount acct = new BitcoinAccount();
			acct.setAccount(account);
			acct.setAmount(amount.doubleValue());
			accounts.put(account, acct);
		}
		return accounts;
	}

	@Override
	public List<String> listAddressGroupings() throws BitcoinException {
		JsonArray results = (JsonArray) invoke(BitcoinDaemonConstant.BTCAPI_LIST_ADDRESS_GROUPINGS);
		List<String> groupings = new ArrayList<String>();
		for (JsonObject grouping : results.getValuesAs(JsonObject.class)) {
			groupings.add(String.valueOf(grouping));
		}
		return groupings;
	}

	@Override
	public List<String> listLockUnspent() throws BitcoinException {
		JsonArray results = (JsonArray) invoke(BitcoinDaemonConstant.BTCAPI_LIST_LOCK_UNSPENT);
		List<String> unspents = new ArrayList<String>();
		for (JsonObject unspent : results.getValuesAs(JsonObject.class)) {
			unspents.add(String.valueOf(unspent));
		}
		return unspents;
	}

	public List<BitcoinAccount> listReceivedByAccount() throws BitcoinException {
		return listReceivedByAccount(1, false);
	}
	
	@Override
	public List<BitcoinAccount> listReceivedByAccount(int minConfirms,
			boolean includeEmpty) throws BitcoinException {
		if (minConfirms < 0) {
			minConfirms = 1;
		}
		JsonArray parameters = Json.createArrayBuilder().add(minConfirms).add(includeEmpty)
				.build();
		JsonArray results = (JsonArray) invoke(BitcoinDaemonConstant.BTCAPI_LIST_RECEIVED_BY_ACCOUNT, parameters);
		List<BitcoinAccount> accounts = new ArrayList<BitcoinAccount>();
		for (JsonObject result : results.getValuesAs(JsonObject.class)) {
			accounts.add(jsonBitcoinAccount(result));
		}
		return accounts;
	}

	public List<BitcoinAddress> listReceivedByAddress() throws BitcoinException {
		return listReceivedByAddress(1, false);
	}
	
	@Override
	public List<BitcoinAddress> listReceivedByAddress(int minConfirms,
			boolean includeEmpty) throws BitcoinException {
		if (minConfirms < 0) {
			minConfirms = 1;
		}
		JsonArray parameters = Json.createArrayBuilder().add(minConfirms).add(includeEmpty)
				.build();
		JsonArray results = (JsonArray) invoke(BitcoinDaemonConstant.BTCAPI_LIST_RECEIVED_BY_ADDRESS, parameters);
		List<BitcoinAddress> addresses = new ArrayList<BitcoinAddress>();
		for (JsonObject result : results.getValuesAs(JsonObject.class)) {
			addresses.add(jsonBitcoinAddress(result));
		}
		return addresses;
	}

	public List<String> listSinceBlock() throws BitcoinException {
		return listSinceBlock("", 1);
	}
	
	@Override
	public List<String> listSinceBlock(String blockHash, int targetConfirms)
			throws BitcoinException {
		// TODO
		// listsinceblock [blockhash] [target-confirmations]
		// Get all transactions in blocks since block [blockhash], or all transactions if omitted
		throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
				BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
						+ BitcoinConstant.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	public List<String> listTransactions() throws BitcoinException {
		return listTransactions("", 10, 0);
	}
	
	public List<String> listTransactions(String account) throws BitcoinException {
		return listTransactions(account, 10, 0);
	}
	
	public List<String> listTransactions(String account, int count) throws BitcoinException {
		return listTransactions(account, count, 0);
	}
			
	@Override
	public List<String> listTransactions(String account, int count, int from)
			throws BitcoinException {
		if (account == null) {
			account = "";
		}
		if (count < 1) {
			count = 10;
		}
		if (from < 0) {
			from = 0;
		}
		JsonArray parameters = Json.createArrayBuilder().add(account).add(count).add(from)
				.build();
		JsonArray results = (JsonArray) invoke(BitcoinDaemonConstant.BTCAPI_LIST_TRANSACTIONS, parameters);
		List<String> transactions = new ArrayList<String>();
		for (JsonObject transaction : results.getValuesAs(JsonObject.class)) {
			transactions.add(String.valueOf(transaction));
		}
		return transactions;
	}

	@Override
	public List<String> listUnspent(int minConfirms, int maxConfirms)
			throws BitcoinException {
		throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
				BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
						+ BitcoinConstant.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public void lockUnspent(boolean unlock, List<Object> outputs)
			throws BitcoinException {
		throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
				BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
						+ BitcoinConstant.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public void move(String fromAccount, String toAccount, double amount,
			int minConfirms, String comment) throws BitcoinException {
		throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
				BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
						+ BitcoinConstant.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public String sendFrom(String fromAccount, String toAddress, double amount,
			int minConfirms, String commentFrom, String commentTo)
			throws BitcoinException {
		throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
				BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
						+ BitcoinConstant.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public String sendMany(String fromAccount, List<Object> addresses,
			int minConfirms, String commentFrom, String commentTo)
			throws BitcoinException {
		throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
				BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
						+ BitcoinConstant.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public void sendRawTransaction(String transactionId)
			throws BitcoinException {
		throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
				BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
						+ BitcoinConstant.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public String sendToAddress(String toAddress, double amount,
			String commentFrom, String commentTo) throws BitcoinException {
		throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
				BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
						+ BitcoinConstant.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public void setAccount(String address, String account)
			throws BitcoinException {
		if (address == null) {
			address = "";
		}
		if (account == null) {
			account = "";
		}
		JsonArray parameters = Json.createArrayBuilder().add(address)
				.add(account).build();
		invoke(BitcoinDaemonConstant.BTCAPI_SET_ACCOUNT, parameters);
	}

	@Override
	public void setGenerate(boolean generate, int generateProcessorsLimit)
			throws BitcoinException {
		if (generateProcessorsLimit < 1) {
			generateProcessorsLimit = -1;
		}
		JsonArray parameters = Json.createArrayBuilder().add(generate)
				.add(generateProcessorsLimit).build();
		invoke(BitcoinDaemonConstant.BTCAPI_SET_GENERATE, parameters);
	}

	public void setGenerate(boolean generate) throws BitcoinException {
		setGenerate(generate, -1);
	}

	@Override
	public boolean setTransactionFee(double amount) throws BitcoinException {
		if (amount < 0) {
			amount = 0;
		}
		JsonArray parameters = Json.createArrayBuilder().add(amount).build();
		JsonValue results = invoke(BitcoinDaemonConstant.BTCAPI_SET_TRANSACTION_FEE, parameters);
		return Boolean.valueOf(String.valueOf(results));
	}

	@Override
	public void signMessage(String address, String message)
			throws BitcoinException {
		throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
				BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
						+ BitcoinConstant.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public void signRawTransaction(String transactionId,
			List<Object> signatures, List<String> keys) throws BitcoinException {
		throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
				BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
						+ BitcoinConstant.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public String stop() throws BitcoinException {
		JsonString results = (JsonString) invoke(BitcoinDaemonConstant.BTCAPI_STOP);
		return results.getString();
	}

	@Override
	public void submitBlock(String data, List<Object> params)
			throws BitcoinException {
		throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
				BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
						+ BitcoinConstant.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}

	@Override
	public BitcoinAddress validateAddress(String address)
			throws BitcoinException {
		if (address == null) {
			address = "";
		}
		JsonArray parameters = Json.createArrayBuilder().add(address).build();
		JsonObject results = (JsonObject) invoke(
				BitcoinDaemonConstant.BTCAPI_VALIDATE_ADDRESS, parameters);
		return jsonBitcoinAddress(results);
	}

	@Override
	public String verifyMessage(String address, String signature, String message)
			throws BitcoinException {
		throw new BitcoinException(BitcoinConstant.BTC4J_ERROR_CODE,
				BitcoinConstant.BTC4J_ERROR_MESSAGE + ": "
						+ BitcoinConstant.BTC4J_ERROR_DATA_NOT_IMPLEMENTED);
	}
}