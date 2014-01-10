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

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import org.btc4j.core.BtcAccount;
import org.btc4j.core.BtcAddress;
import org.btc4j.core.BtcBlock;
import org.btc4j.core.BtcException;
import org.btc4j.core.BtcLastBlock;
import org.btc4j.core.BtcMining;
import org.btc4j.core.BtcPeer;
import org.btc4j.core.BtcStatus;
import org.btc4j.core.BtcTransaction;
import org.btc4j.core.BtcTransactionOutputSet;

public class BtcJsonRpcHttpClient {
	private final static Logger LOGGER = Logger.getLogger(BtcJsonRpcHttpClient.class
			.getName());
	private HttpState state;
	private HttpClientParams params;
	private URL url;
	
	public BtcJsonRpcHttpClient(URL url, String account, String password,
			int timeoutInMillis) {
		this.url = url;
		state = new HttpState();
		state.setCredentials(new AuthScope(url.getHost(), url.getPort()),
				new UsernamePasswordCredentials(account, password));
		params = new HttpClientParams();
		params.setConnectionManagerTimeout(timeoutInMillis);
	}
	
	public JsonValue invoke(String method) throws BtcException {
		return invoke(method, null);
	}

	public JsonValue invoke(String method, JsonValue parameters)
			throws BtcException {
		if (url == null) {
			LOGGER.severe(BtcDaemonConstant.BTC4J_DAEMON_DATA_NULL_URL);
			throw new BtcException(BtcException.BTC4J_ERROR_CODE,
					BtcException.BTC4J_ERROR_MESSAGE + ": "
							+ BtcDaemonConstant.BTC4J_DAEMON_DATA_NULL_URL);
		}
		PostMethod post = new PostMethod(url.toString());
		try {
			post.setRequestHeader(BtcDaemonConstant.BTC4J_DAEMON_HTTP_HEADER,
					BtcDaemonConstant.BTC4J_DAEMON_JSONRPC_CONTENT_TYPE);
			String guid = UUID.randomUUID().toString();
			JsonObjectBuilder builder = Json.createObjectBuilder();
			builder.add(BtcDaemonConstant.JSONRPC_JSONRPC,
					BtcDaemonConstant.JSONRPC_VERSION).add(
							BtcDaemonConstant.JSONRPC_METHOD, method);
			if (parameters != null) {
				builder.add(BtcDaemonConstant.JSONRPC_PARAMS, parameters);
			} else {
				builder.addNull(BtcDaemonConstant.JSONRPC_PARAMS);
			}
			builder.add(BtcDaemonConstant.JSONRPC_ID, guid);
			JsonObject request = builder.build();
			LOGGER.info("request: " + request);
			post.setRequestEntity(new StringRequestEntity(request.toString(),
					BtcDaemonConstant.BTC4J_DAEMON_JSON_CONTENT_TYPE, null));
			HttpClient client = new HttpClient(params);
			client.setState(state);
			int status = client.executeMethod(post);
			if (status != HttpStatus.SC_OK) {
				LOGGER.severe(status + " " + HttpStatus.getStatusText(status));
				throw new BtcException(BtcException.BTC4J_ERROR_CODE,
						BtcException.BTC4J_ERROR_MESSAGE + ": " + status
								+ " " + HttpStatus.getStatusText(status));
			}
			JsonObject response = (JsonObject) Json.createReader(
					new StringReader(post.getResponseBodyAsString())).read();
			if (response == null) {
				LOGGER.severe(BtcDaemonConstant.BTC4J_DAEMON_DATA_NULL_RESPONSE);
				throw new BtcException(
						BtcException.BTC4J_ERROR_CODE,
						BtcException.BTC4J_ERROR_MESSAGE
								+ ": "
								+ BtcDaemonConstant.BTC4J_DAEMON_DATA_NULL_RESPONSE);
			}
			LOGGER.info("response: " + response);
			JsonString id = response.getJsonString(BtcDaemonConstant.JSONRPC_ID);
			if (id == null) {
				JsonObject error = response
						.getJsonObject(BtcDaemonConstant.JSONRPC_ERROR);
				if (error != null) {
					JsonObject data = error
							.getJsonObject(BtcDaemonConstant.JSONRPC_DATA);
					LOGGER.severe(String.valueOf(data));
					throw new BtcException(
							error.getInt(BtcDaemonConstant.JSONRPC_CODE),
							error.get(BtcDaemonConstant.JSONRPC_MESSAGE) + ": "
									+ data);
				} else {
					LOGGER.severe(BtcDaemonConstant.BTC4J_DAEMON_DATA_INVALID_ERROR);
					throw new BtcException(
							BtcException.BTC4J_ERROR_CODE,
							BtcException.BTC4J_ERROR_MESSAGE
									+ ": "
									+ BtcDaemonConstant.BTC4J_DAEMON_DATA_INVALID_ERROR);
				}
			}
			if (!guid.equals(id.getString())) {
				LOGGER.severe(BtcDaemonConstant.BTC4J_DAEMON_DATA_INVALID_ID);
				throw new BtcException(BtcException.BTC4J_ERROR_CODE,
						BtcException.BTC4J_ERROR_MESSAGE + ": "
								+ BtcDaemonConstant.BTC4J_DAEMON_DATA_INVALID_ID);
			}
			return response.get(BtcDaemonConstant.JSONRPC_RESULT);
		} catch (NullPointerException | ClassCastException | IOException e) {
			LOGGER.severe(String.valueOf(e));
			throw new BtcException(
					BtcException.BTC4J_ERROR_CODE,
					BtcException.BTC4J_ERROR_MESSAGE + ": " + e.getMessage(),
					e);
		} finally {
			post.releaseConnection();
		}
	}
	
	public BtcAccount jsonAccount(JsonObject value) {
		BtcAccount account = new BtcAccount();
		account.setAccount(value.getString(
				BtcDaemonConstant.BTCOBJ_ACCOUNT_ACCOUNT, ""));
		JsonNumber amount = value
				.getJsonNumber(BtcDaemonConstant.BTCOBJ_ACCOUNT_AMOUNT);
		if (amount != null) {
			account.setAmount(amount.doubleValue());
		}
		account.setConfirmations(value.getInt(
				BtcDaemonConstant.BTCOBJ_ACCOUNT_CONFIRMATIONS, 0));
		return account;
	}
	
	public BtcAddress jsonAddress(JsonObject value) {
		BtcAddress address = new BtcAddress();
		address.setValid(value.getBoolean(BtcDaemonConstant.BTCOBJ_ADDRESS_VALID,
				false));
		address.setAddress(value.getString(
				BtcDaemonConstant.BTCOBJ_ADDRESS_ADDRESS, ""));
		address.setMine(value.getBoolean(BtcDaemonConstant.BTCOBJ_ADDRESS_MINE,
				false));
		address.setScript(value.getBoolean(
				BtcDaemonConstant.BTCOBJ_ADDRESS_SCRIPT, false));
		address.setPublicKey(value.getString(
				BtcDaemonConstant.BTCOBJ_ADDRESS_PUBLIC_KEY, ""));
		address.setCompressed(value.getBoolean(
				BtcDaemonConstant.BTCOBJ_ADDRESS_COMPRESSED, false));
		BtcAccount account = new BtcAccount();
		account.setAccount(value.getString(
				BtcDaemonConstant.BTCOBJ_ADDRESS_ACCOUNT, ""));
		address.setAccount(account);
		JsonNumber amount = value
				.getJsonNumber(BtcDaemonConstant.BTCOBJ_ADDRESS_AMOUNT);
		if (amount != null) {
			address.setAmount(amount.doubleValue());
		}
		address.setConfirmations(value.getInt(
				BtcDaemonConstant.BTCOBJ_ADDRESS_CONFIRMATIONS, 0));
		return address;
	}

	public BtcBlock jsonBlock(JsonObject value)
			throws BtcException {
		BtcBlock block = new BtcBlock();
		block.setHash(value.getString(BtcDaemonConstant.BTCOBJ_BLOCK_HASH, ""));
		block.setConfirmations(value.getInt(
				BtcDaemonConstant.BTCOBJ_BLOCK_CONFIRMATIONS, 0));
		block.setSize(value.getInt(BtcDaemonConstant.BTCOBJ_BLOCK_SIZE, 0));
		block.setHeight(value.getInt(BtcDaemonConstant.BTCOBJ_BLOCK_HEIGHT, 0));
		block.setVersion(value.getInt(BtcDaemonConstant.BTCOBJ_BLOCK_VERSION, 0));
		block.setMerkleRoot(value.getString(
				BtcDaemonConstant.BTCOBJ_BLOCK_MERKLE_ROOT, ""));
		List<BtcTransaction> transactions = new ArrayList<BtcTransaction>();
		JsonArray transactionIds = value
				.getJsonArray(BtcDaemonConstant.BTCOBJ_BLOCK_TRANSACTIONS);
		if (transactionIds != null) {
			for (JsonString transactionId : transactionIds
					.getValuesAs(JsonString.class)) {
				BtcTransaction transaction = new BtcTransaction();
				transaction.setTransaction(transactionId
						.getString());
				transactions.add(transaction);
			}
		}
		block.setTransactions(transactions);
		block.setTime(value.getInt(BtcDaemonConstant.BTCOBJ_BLOCK_TIME, 0));
		block.setNonce(value.getInt(BtcDaemonConstant.BTCOBJ_BLOCK_NONCE, 0));
		block.setBits(value.getString(BtcDaemonConstant.BTCOBJ_BLOCK_BITS, ""));
		JsonNumber difficulty = value
				.getJsonNumber(BtcDaemonConstant.BTCOBJ_BLOCK_DIFFICULTY);
		if (difficulty != null) {
			block.setDifficulty(difficulty.doubleValue());
		}
		block.setPreviousBlockHash(value.getString(
				BtcDaemonConstant.BTCOBJ_BLOCK_PREVIOUS_BLOCK_HASH, ""));
		block.setNextBlockHash(value.getString(
				BtcDaemonConstant.BTCOBJ_BLOCK_NEXT_BLOCK_HASH, ""));
		return block;
	}
	
	public BtcLastBlock jsonLastBlock(JsonObject value)
			throws BtcException {
		BtcLastBlock lastBlock = new BtcLastBlock();
		lastBlock.setLastBlock(value.getString(BtcDaemonConstant.BTCOBJ_LASTBLOCK_LASTBLOCK, ""));
		List<BtcTransaction> transactions = new ArrayList<BtcTransaction>();
		JsonArray transactionIds = value
				.getJsonArray(BtcDaemonConstant.BTCOBJ_LASTBLOCK_TRANSACTIONS);
		if (transactionIds != null) {
			for (JsonString transactionId : transactionIds
					.getValuesAs(JsonString.class)) {
				BtcTransaction transaction = new BtcTransaction();
				transaction.setTransaction(transactionId
						.getString());
				transactions.add(transaction);
			}
		}
		lastBlock.setTransactions(transactions);
		return lastBlock;
	}
	
	public BtcMining jsonMining(JsonObject value)
			throws BtcException {
		BtcMining info = new BtcMining();
		info.setBlocks(value.getInt(BtcDaemonConstant.BTCOBJ_INFO_BLOCKS, 0));
		info.setCurrentBlockSize(value.getInt(
				BtcDaemonConstant.BTCOBJ_INFO_CURRENT_BLOCK_SIZE, 0));
		info.setCurrentBlockTransactions(value.getInt(
				BtcDaemonConstant.BTCOBJ_INFO_CURRENT_BLOCK_TRANSACTIONS, 0));
		JsonNumber difficulty = value
				.getJsonNumber(BtcDaemonConstant.BTCOBJ_INFO_DIFFICULTY);
		if (difficulty != null) {
			info.setDifficulty(difficulty.doubleValue());
		}
		info.setErrors(value.getString(BtcDaemonConstant.BTCOBJ_INFO_ERRORS, ""));
		info.setGenerate(value.getBoolean(BtcDaemonConstant.BTCOBJ_INFO_GENERATE,
				false));
		info.setGenProcessorLimit(value.getInt(
				BtcDaemonConstant.BTCOBJ_INFO_PROCESSOR_LIMIT, -1));
		info.setHashesPerSecond(value.getInt(
				BtcDaemonConstant.BTCOBJ_INFO_HASHES_PER_SECOND, 0));
		info.setPooledTransactions(value.getInt(
				BtcDaemonConstant.BTCOBJ_INFO_POOLED_TRANSACTIONS, 0));
		info.setTestnet(value.getBoolean(BtcDaemonConstant.BTCOBJ_INFO_TESTNET,
				false));
		return info;
	}
	
	public BtcPeer jsonPeer(JsonObject value)
			throws BtcException {
		BtcPeer peer = new BtcPeer();
		peer.setNetworkAddress(value
				.getString(BtcDaemonConstant.BTCOBJ_PEER_ADDRESS, ""));
		peer.setServices(value.getString(BtcDaemonConstant.BTCOBJ_PEER_SERVICES,
				""));
		peer.setLastSend(value.getInt(BtcDaemonConstant.BTCOBJ_PEER_LAST_SEND, 0));
		peer.setLastReceived(value.getInt(
				BtcDaemonConstant.BTCOBJ_PEER_LAST_RECEIVED, 0));
		peer.setBytesSent(value.getInt(BtcDaemonConstant.BTCOBJ_PEER_BYTES_SENT,
				0));
		peer.setBytesReceived(value.getInt(
				BtcDaemonConstant.BTCOBJ_PEER_BYTES_RECEIVED, 0));
		peer.setConnectionTime(value.getInt(
				BtcDaemonConstant.BTCOBJ_PEER_CONNECTION_TIME, 0));
		peer.setVersion(value.getInt(BtcDaemonConstant.BTCOBJ_PEER_VERSION, 0));
		peer.setSubVersion(value.getString(
				BtcDaemonConstant.BTCOBJ_PEER_SUBVERSION, ""));
		peer.setInbound(value.getBoolean(BtcDaemonConstant.BTCOBJ_PEER_INBOUND,
				false));
		peer.setStartingHeight(value.getInt(
				BtcDaemonConstant.BTCOBJ_PEER_START_HEIGHT, 0));
		peer.setBanScore(value.getInt(BtcDaemonConstant.BTCOBJ_PEER_BAN_SCORE, 0));
		peer.setSyncNode(value.getBoolean(
				BtcDaemonConstant.BTCOBJ_PEER_SYNC_NODE, false));
		return peer;
	}
	
	public BtcTransaction jsonTransaction(JsonObject value)
			throws BtcException {
		BtcTransaction transaction = new BtcTransaction();
		// TODO load transaction and tx details
		return transaction;
	}
	
	public BtcTransactionOutputSet jsonTransactionOutputSet(JsonObject value)
			throws BtcException {
		BtcTransactionOutputSet output = new BtcTransactionOutputSet();
		output.setHeight(value.getInt(
				BtcDaemonConstant.BTCOBJ_TXOUTPUTSET_HEIGHT, 0));
		output.setBestBlock(value.getString(
				BtcDaemonConstant.BTCOBJ_TXOUTPUTSET_BEST_BLOCK, ""));
		output.setTransactions(value.getInt(
				BtcDaemonConstant.BTCOBJ_TXOUTPUTSET_TRANSACTIONS, 0));
		output.setOutputTransactions(value.getInt(
				BtcDaemonConstant.BTCOBJ_TXOUTPUTSET_OUTPUT_TRANSACTIONS, 0));
		output.setBytesSerialized(value.getInt(
				BtcDaemonConstant.BTCOBJ_TXOUTPUTSET_BYTES_SERIALIZED, 0));
		output.setHashSerialized(value.getString(
				BtcDaemonConstant.BTCOBJ_TXOUTPUTSET_HASH_SERIALIZED, ""));
		JsonNumber amount = value
				.getJsonNumber(BtcDaemonConstant.BTCOBJ_TXOUTPUTSET_TOTAL_AMOUT);
		if (amount != null) {
			output.setTotalAmount(amount.doubleValue());
		}
		return output;
	}
	
	public BtcStatus jsonStatus(JsonObject value)
			throws BtcException {
		BtcStatus info = new BtcStatus();
		info.setVersion(value.getInt(BtcDaemonConstant.BTCOBJ_INFO_VERSION, 0));
		info.setProtocolVersion(value.getInt(
				BtcDaemonConstant.BTCOBJ_INFO_PROTOCOL_VERSION, 0));
		info.setWalletVersion(value.getInt(
				BtcDaemonConstant.BTCOBJ_INFO_WALLET_VERSION, 0));
		JsonNumber balance = value
				.getJsonNumber(BtcDaemonConstant.BTCOBJ_INFO_BALANCE);
		if (balance != null) {
			info.setBalance(balance.doubleValue());
		}
		info.setBlocks(value.getInt(BtcDaemonConstant.BTCOBJ_INFO_BLOCKS, 0));
		info.setTimeOffset(value.getInt(
				BtcDaemonConstant.BTCOBJ_INFO_TIME_OFFSET, 0));
		info.setConnections(value.getInt(
				BtcDaemonConstant.BTCOBJ_INFO_CONNECTIONS, 0));
		info.setProxy(value.getString(BtcDaemonConstant.BTCOBJ_INFO_PROXY, ""));
		JsonNumber difficulty = value
				.getJsonNumber(BtcDaemonConstant.BTCOBJ_INFO_DIFFICULTY);
		if (difficulty != null) {
			info.setDifficulty(difficulty.doubleValue());
		}
		info.setTestnet(value.getBoolean(BtcDaemonConstant.BTCOBJ_INFO_TESTNET,
				false));
		info.setKeyPoolOldest(value.getInt(
				BtcDaemonConstant.BTCOBJ_INFO_KEYPOOL_OLDEST, 0));
		info.setKeyPoolSize(value.getInt(
				BtcDaemonConstant.BTCOBJ_INFO_KEYPOOL_SIZE, 0));
		JsonNumber transactionFee = value
				.getJsonNumber(BtcDaemonConstant.BTCOBJ_INFO_TRANSACTION_FEE);
		if (transactionFee != null) {
			info.setTransactionFee(transactionFee.doubleValue());
		}
		info.setErrors(value.getString(BtcDaemonConstant.BTCOBJ_INFO_ERRORS, ""));
		return info;
	}
}