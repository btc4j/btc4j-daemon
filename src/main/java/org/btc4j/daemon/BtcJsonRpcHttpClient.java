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
import org.btc4j.core.BtcMiningInfo;
import org.btc4j.core.BtcMultiSignatureAddress;
import org.btc4j.core.BtcPeer;
import org.btc4j.core.BtcInfo;
import org.btc4j.core.BtcTransaction;
import org.btc4j.core.BtcTransactionCategory;
import org.btc4j.core.BtcTransactionDetail;
import org.btc4j.core.BtcTransactionOutputSet;

public class BtcJsonRpcHttpClient {
	private static final String BTC4J_DAEMON_DATA_INVALID_ERROR = "response error is empty";
	private static final String BTC4J_DAEMON_DATA_INVALID_ID = "response id does not match request id";
	private static final String BTC4J_DAEMON_DATA_NULL_RESPONSE = "response is empty";
	private static final String BTC4J_DAEMON_DATA_NULL_URL = "server URL is null";
	private static final String BTC4J_DAEMON_HTTP_HEADER = "Content-Type";
	private static final String BTC4J_DAEMON_JSON_CONTENT_TYPE = "application/json";
	private static final String BTC4J_DAEMON_JSONRPC_CONTENT_TYPE = "application/json-rpc";
	private static final String BTCOBJ_ACCOUNT_ACCOUNT = "account";
	private static final String BTCOBJ_ACCOUNT_AMOUNT = "amount";
	private static final String BTCOBJ_ACCOUNT_CONFIRMATIONS = "confirmations";
	private static final String BTCOBJ_ADDRESS_ACCOUNT = "account";
	private static final String BTCOBJ_ADDRESS_ADDRESS = "address";
	private static final String BTCOBJ_ADDRESS_AMOUNT = "amount";
	private static final String BTCOBJ_ADDRESS_COMPRESSED = "iscompressed";
	private static final String BTCOBJ_ADDRESS_CONFIRMATIONS = "confirmations";
	private static final String BTCOBJ_ADDRESS_MINE = "ismine";
	private static final String BTCOBJ_ADDRESS_PUBLIC_KEY = "pubkey";
	private static final String BTCOBJ_ADDRESS_REDEEM_SCRIPT = "redeemScript";
	private static final String BTCOBJ_ADDRESS_SCRIPT = "isscript";
	private static final String BTCOBJ_ADDRESS_VALID = "isvalid";
	private static final String BTCOBJ_BLOCK_BITS = "bits";
	private static final String BTCOBJ_BLOCK_CONFIRMATIONS = "confirmations";
	private static final String BTCOBJ_BLOCK_DIFFICULTY = "difficulty";
	private static final String BTCOBJ_BLOCK_HASH = "hash";
	private static final String BTCOBJ_BLOCK_HEIGHT = "height";
	private static final String BTCOBJ_BLOCK_MERKLE_ROOT = "merkleroot";
	private static final String BTCOBJ_BLOCK_NEXT_BLOCK_HASH = "nextblockhash";
	private static final String BTCOBJ_BLOCK_NONCE = "nonce";
	private static final String BTCOBJ_BLOCK_PREVIOUS_BLOCK_HASH = "previousblockhash";
	private static final String BTCOBJ_BLOCK_SIZE = "size";
	private static final String BTCOBJ_BLOCK_TIME = "time";
	private static final String BTCOBJ_BLOCK_TRANSACTIONS = "tx";
	private static final String BTCOBJ_BLOCK_VERSION = "version";
	private static final String BTCOBJ_INFO_BALANCE = "balance";
	private static final String BTCOBJ_INFO_BLOCKS = "blocks";
	private static final String BTCOBJ_INFO_CONNECTIONS = "connections";
	private static final String BTCOBJ_INFO_CURRENT_BLOCK_SIZE = "currentblocksize";
	private static final String BTCOBJ_INFO_CURRENT_BLOCK_TRANSACTIONS = "currentblocktx";
	private static final String BTCOBJ_INFO_DIFFICULTY = "difficulty";
	private static final String BTCOBJ_INFO_ERRORS = "errors";
	private static final String BTCOBJ_INFO_GENERATE = "generate";
	private static final String BTCOBJ_INFO_HASHES_PER_SECOND = "hashespersec";
	private static final String BTCOBJ_INFO_KEYPOOL_OLDEST = "keypoololdest";
	private static final String BTCOBJ_INFO_KEYPOOL_SIZE = "keypoolsize";
	private static final String BTCOBJ_INFO_POOLED_TRANSACTIONS = "pooledtx";
	private static final String BTCOBJ_INFO_PROCESSOR_LIMIT = "genproclimit";
	private static final String BTCOBJ_INFO_PROTOCOL_VERSION = "protocolversion";
	private static final String BTCOBJ_INFO_PROXY = "proxy";
	private static final String BTCOBJ_INFO_TESTNET = "testnet";
	private static final String BTCOBJ_INFO_TIME_OFFSET = "timeoffset";
	private static final String BTCOBJ_INFO_TRANSACTION_FEE = "paytxfee";
	private static final String BTCOBJ_INFO_VERSION = "version";
	private static final String BTCOBJ_INFO_WALLET_VERSION = "walletversion";
	private static final String BTCOBJ_LASTBLOCK_LASTBLOCK = "lastblock";
	private static final String BTCOBJ_LASTBLOCK_TRANSACTIONS = "transactions";
	private static final String BTCOBJ_PEER_ADDRESS = "addr";
	private static final String BTCOBJ_PEER_BAN_SCORE = "banscore";
	private static final String BTCOBJ_PEER_BYTES_RECEIVED = "bytesrecv";
	private static final String BTCOBJ_PEER_BYTES_SENT = "bytessent";
	private static final String BTCOBJ_PEER_CONNECTION_TIME = "conntime";
	private static final String BTCOBJ_PEER_INBOUND = "inbound";
	private static final String BTCOBJ_PEER_LAST_RECEIVED = "lastrecv";
	private static final String BTCOBJ_PEER_LAST_SEND = "lastsend";
	private static final String BTCOBJ_PEER_SERVICES = "services";
	private static final String BTCOBJ_PEER_START_HEIGHT = "startingheight";
	private static final String BTCOBJ_PEER_SUBVERSION = "subver";
	private static final String BTCOBJ_PEER_SYNC_NODE = "syncnode";
	private static final String BTCOBJ_PEER_VERSION = "version";
	private static final String BTCOBJ_TX_AMOUNT = "amount";
	private static final String BTCOBJ_TX_BLOCK_HASH = "blockhash";
	private static final String BTCOBJ_TX_BLOCK_INDEX = "blockindex";
	private static final String BTCOBJ_TX_BLOCK_TIME = "blocktime";
	private static final String BTCOBJ_TX_CONFIRMATIONS = "confirmations";
	private static final String BTCOBJ_TX_DETAILS = "details";
	private static final String BTCOBJ_TX_TIME = "time";
	private static final String BTCOBJ_TX_TIME_RECEIVED = "timereceived";
	private static final String BTCOBJ_TX_TRANSACTION = "txid";
	private static final String BTCOBJ_TXDETAIL_ACCOUNT = "account";
	private static final String BTCOBJ_TXDETAIL_ADDRESS = "address";
	private static final String BTCOBJ_TXDETAIL_AMOUNT = "amount";
	private static final String BTCOBJ_TXDETAIL_CATEGORY = "category";
	private static final String BTCOBJ_TXDETAIL_FEE = "fee";
	private static final String BTCOBJ_TXOUTPUTSET_BEST_BLOCK = "bestblock";
	private static final String BTCOBJ_TXOUTPUTSET_BYTES_SERIALIZED = "bytes_serialized";
	private static final String BTCOBJ_TXOUTPUTSET_HASH_SERIALIZED = "hash_serialized";
	private static final String BTCOBJ_TXOUTPUTSET_HEIGHT = "height";
	private static final String BTCOBJ_TXOUTPUTSET_OUTPUT_TRANSACTIONS = "txouts";
	private static final String BTCOBJ_TXOUTPUTSET_TOTAL_AMOUT = "total_amount";
	private static final String BTCOBJ_TXOUTPUTSET_TRANSACTIONS = "transactions";
	private static final String JSONRPC_CODE = "code";
	private static final String JSONRPC_DATA = "data";
	private static final String JSONRPC_ERROR = "error";
	private static final String JSONRPC_ID = "id";
	private static final String JSONRPC_JSONRPC = "jsonrpc";
	private static final String JSONRPC_MESSAGE = "message";
	private static final String JSONRPC_METHOD = "method";
	private static final String JSONRPC_PARAMS = "params";
	private static final String JSONRPC_RESULT = "result";
	private static final String JSONRPC_VERSION = "2.0";
	private final static Logger LOGGER = Logger
			.getLogger(BtcJsonRpcHttpClient.class.getName());
	private HttpClientParams params;
	private HttpState state;
	private URL url;

	public BtcJsonRpcHttpClient(URL url, String account, String password,
			long timeout) {
		this.url = url;
		state = new HttpState();
		state.setCredentials(new AuthScope(url.getHost(), url.getPort()),
				new UsernamePasswordCredentials(account, password));
		params = new HttpClientParams();
		params.setConnectionManagerTimeout(timeout);
	}

	public JsonValue invoke(String method) throws BtcException {
		return invoke(method, null);
	}

	public JsonValue invoke(String method, JsonValue parameters)
			throws BtcException {
		if (url == null) {
			LOGGER.severe(BTC4J_DAEMON_DATA_NULL_URL);
			throw new BtcException(BtcException.BTC4J_ERROR_CODE,
					BtcException.BTC4J_ERROR_MESSAGE + ": "
							+ BTC4J_DAEMON_DATA_NULL_URL);
		}
		PostMethod post = new PostMethod(url.toString());
		try {
			post.setRequestHeader(BTC4J_DAEMON_HTTP_HEADER,
					BTC4J_DAEMON_JSONRPC_CONTENT_TYPE);
			String guid = UUID.randomUUID().toString();
			JsonObjectBuilder builder = Json.createObjectBuilder();
			builder.add(JSONRPC_JSONRPC, JSONRPC_VERSION).add(JSONRPC_METHOD,
					method);
			if (parameters != null) {
				builder.add(JSONRPC_PARAMS, parameters);
			} else {
				builder.addNull(JSONRPC_PARAMS);
			}
			builder.add(JSONRPC_ID, guid);
			JsonObject request = builder.build();
			LOGGER.info("request: " + request);
			post.setRequestEntity(new StringRequestEntity(request.toString(),
					BTC4J_DAEMON_JSON_CONTENT_TYPE, null));
			HttpClient client = new HttpClient(params);
			client.setState(state);
			int status = client.executeMethod(post);
			if (status != HttpStatus.SC_OK) {
				LOGGER.severe(status + " " + HttpStatus.getStatusText(status));
				throw new BtcException(BtcException.BTC4J_ERROR_CODE,
						BtcException.BTC4J_ERROR_MESSAGE + ": " + status + " "
								+ HttpStatus.getStatusText(status));
			}
			JsonObject response = (JsonObject) Json.createReader(
					new StringReader(post.getResponseBodyAsString())).read();
			if (response == null) {
				LOGGER.severe(BTC4J_DAEMON_DATA_NULL_RESPONSE);
				throw new BtcException(BtcException.BTC4J_ERROR_CODE,
						BtcException.BTC4J_ERROR_MESSAGE + ": "
								+ BTC4J_DAEMON_DATA_NULL_RESPONSE);
			}
			LOGGER.info("response: " + response);
			JsonString id = response.getJsonString(JSONRPC_ID);
			if (id == null) {
				JsonObject error = response.getJsonObject(JSONRPC_ERROR);
				if (error != null) {
					JsonObject data = error.getJsonObject(JSONRPC_DATA);
					LOGGER.severe(String.valueOf(data));
					throw new BtcException(error.getInt(JSONRPC_CODE),
							error.get(JSONRPC_MESSAGE) + ": " + data);
				} else {
					LOGGER.severe(BTC4J_DAEMON_DATA_INVALID_ERROR);
					throw new BtcException(BtcException.BTC4J_ERROR_CODE,
							BtcException.BTC4J_ERROR_MESSAGE + ": "
									+ BTC4J_DAEMON_DATA_INVALID_ERROR);
				}
			}
			if (!guid.equals(id.getString())) {
				LOGGER.severe(BTC4J_DAEMON_DATA_INVALID_ID);
				throw new BtcException(BtcException.BTC4J_ERROR_CODE,
						BtcException.BTC4J_ERROR_MESSAGE + ": "
								+ BTC4J_DAEMON_DATA_INVALID_ID);
			}
			return response.get(JSONRPC_RESULT);
		} catch (NullPointerException | ClassCastException | IOException e) {
			LOGGER.severe(String.valueOf(e));
			throw new BtcException(BtcException.BTC4J_ERROR_CODE,
					BtcException.BTC4J_ERROR_MESSAGE + ": " + e.getMessage(), e);
		} finally {
			post.releaseConnection();
		}
	}

	public BtcAccount jsonAccount(JsonObject value) {
		BtcAccount account = new BtcAccount();
		account.setAccount(value.getString(BTCOBJ_ACCOUNT_ACCOUNT, ""));
		JsonNumber amount = value.getJsonNumber(BTCOBJ_ACCOUNT_AMOUNT);
		if (amount != null) {
			account.setAmount(amount.doubleValue());
		}
		account.setConfirmations(value.getInt(BTCOBJ_ACCOUNT_CONFIRMATIONS, 0));
		return account;
	}

	public BtcAddress jsonAddress(JsonObject value) {
		BtcAddress address = new BtcAddress();
		address.setValid(value.getBoolean(BTCOBJ_ADDRESS_VALID, false));
		address.setAddress(value.getString(BTCOBJ_ADDRESS_ADDRESS, ""));
		address.setMine(value.getBoolean(BTCOBJ_ADDRESS_MINE, false));
		address.setScript(value.getBoolean(BTCOBJ_ADDRESS_SCRIPT, false));
		address.setPublicKey(value.getString(BTCOBJ_ADDRESS_PUBLIC_KEY, ""));
		address.setCompressed(value
				.getBoolean(BTCOBJ_ADDRESS_COMPRESSED, false));
		BtcAccount account = new BtcAccount();
		account.setAccount(value.getString(BTCOBJ_ADDRESS_ACCOUNT, ""));
		address.setAccount(account);
		JsonNumber amount = value.getJsonNumber(BTCOBJ_ADDRESS_AMOUNT);
		if (amount != null) {
			address.setAmount(amount.doubleValue());
		}
		address.setConfirmations(value.getInt(BTCOBJ_ADDRESS_CONFIRMATIONS, 0));
		return address;
	}

	public BtcBlock jsonBlock(JsonObject value) throws BtcException {
		BtcBlock block = new BtcBlock();
		block.setHash(value.getString(BTCOBJ_BLOCK_HASH, ""));
		block.setConfirmations(value.getInt(BTCOBJ_BLOCK_CONFIRMATIONS, 0));
		block.setSize(value.getInt(BTCOBJ_BLOCK_SIZE, 0));
		block.setHeight(value.getInt(BTCOBJ_BLOCK_HEIGHT, 0));
		block.setVersion(value.getInt(BTCOBJ_BLOCK_VERSION, 0));
		block.setMerkleRoot(value.getString(BTCOBJ_BLOCK_MERKLE_ROOT, ""));
		List<BtcTransaction> transactions = new ArrayList<BtcTransaction>();
		JsonArray transactionIds = value
				.getJsonArray(BTCOBJ_BLOCK_TRANSACTIONS);
		if (transactionIds != null) {
			for (JsonString transactionId : transactionIds
					.getValuesAs(JsonString.class)) {
				BtcTransaction transaction = new BtcTransaction();
				transaction.setTransaction(transactionId.getString());
				transactions.add(transaction);
			}
		}
		block.setTransactions(transactions);
		block.setTime(value.getInt(BTCOBJ_BLOCK_TIME, 0));
		block.setNonce(value.getInt(BTCOBJ_BLOCK_NONCE, 0));
		block.setBits(value.getString(BTCOBJ_BLOCK_BITS, ""));
		JsonNumber difficulty = value.getJsonNumber(BTCOBJ_BLOCK_DIFFICULTY);
		if (difficulty != null) {
			block.setDifficulty(difficulty.doubleValue());
		}
		block.setPreviousBlockHash(value.getString(
				BTCOBJ_BLOCK_PREVIOUS_BLOCK_HASH, ""));
		block.setNextBlockHash(value
				.getString(BTCOBJ_BLOCK_NEXT_BLOCK_HASH, ""));
		return block;
	}

	public BtcInfo jsonInfo(JsonObject value) throws BtcException {
		BtcInfo info = new BtcInfo();
		info.setVersion(value.getInt(BTCOBJ_INFO_VERSION, 0));
		info.setProtocolVersion(value.getInt(BTCOBJ_INFO_PROTOCOL_VERSION, 0));
		info.setWalletVersion(value.getInt(BTCOBJ_INFO_WALLET_VERSION, 0));
		JsonNumber balance = value.getJsonNumber(BTCOBJ_INFO_BALANCE);
		if (balance != null) {
			info.setBalance(balance.doubleValue());
		}
		info.setBlocks(value.getInt(BTCOBJ_INFO_BLOCKS, 0));
		info.setTimeOffset(value.getInt(BTCOBJ_INFO_TIME_OFFSET, 0));
		info.setConnections(value.getInt(BTCOBJ_INFO_CONNECTIONS, 0));
		info.setProxy(value.getString(BTCOBJ_INFO_PROXY, ""));
		JsonNumber difficulty = value.getJsonNumber(BTCOBJ_INFO_DIFFICULTY);
		if (difficulty != null) {
			info.setDifficulty(difficulty.doubleValue());
		}
		info.setTestnet(value.getBoolean(BTCOBJ_INFO_TESTNET, false));
		info.setKeyPoolOldest(value.getInt(BTCOBJ_INFO_KEYPOOL_OLDEST, 0));
		info.setKeyPoolSize(value.getInt(BTCOBJ_INFO_KEYPOOL_SIZE, 0));
		JsonNumber transactionFee = value
				.getJsonNumber(BTCOBJ_INFO_TRANSACTION_FEE);
		if (transactionFee != null) {
			info.setTransactionFee(transactionFee.doubleValue());
		}
		info.setErrors(value.getString(BTCOBJ_INFO_ERRORS, ""));
		return info;
	}

	public BtcLastBlock jsonLastBlock(JsonObject value) throws BtcException {
		BtcLastBlock lastBlock = new BtcLastBlock();
		lastBlock.setLastBlock(value.getString(BTCOBJ_LASTBLOCK_LASTBLOCK, ""));
		List<BtcTransaction> transactions = new ArrayList<BtcTransaction>();
		JsonArray txs = value.getJsonArray(BTCOBJ_LASTBLOCK_TRANSACTIONS);
		if (txs != null) {
			for (JsonObject tx : txs.getValuesAs(JsonObject.class)) {
				transactions.add(jsonTransaction(tx));
			}
		}
		lastBlock.setTransactions(transactions);
		return lastBlock;
	}

	public BtcMiningInfo jsonMiningInfo(JsonObject value) throws BtcException {
		BtcMiningInfo info = new BtcMiningInfo();
		info.setBlocks(value.getInt(BTCOBJ_INFO_BLOCKS, 0));
		info.setCurrentBlockSize(value
				.getInt(BTCOBJ_INFO_CURRENT_BLOCK_SIZE, 0));
		info.setCurrentBlockTransactions(value.getInt(
				BTCOBJ_INFO_CURRENT_BLOCK_TRANSACTIONS, 0));
		JsonNumber difficulty = value.getJsonNumber(BTCOBJ_INFO_DIFFICULTY);
		if (difficulty != null) {
			info.setDifficulty(difficulty.doubleValue());
		}
		info.setErrors(value.getString(BTCOBJ_INFO_ERRORS, ""));
		info.setGenerate(value.getBoolean(BTCOBJ_INFO_GENERATE, false));
		info.setGenProcessorLimit(value.getInt(BTCOBJ_INFO_PROCESSOR_LIMIT, -1));
		info.setHashesPerSecond(value.getInt(BTCOBJ_INFO_HASHES_PER_SECOND, 0));
		info.setPooledTransactions(value.getInt(
				BTCOBJ_INFO_POOLED_TRANSACTIONS, 0));
		info.setTestnet(value.getBoolean(BTCOBJ_INFO_TESTNET, false));
		return info;
	}

	public BtcMultiSignatureAddress jsonMultiSignatureAddress(JsonObject value)
			throws BtcException {
		BtcMultiSignatureAddress address = new BtcMultiSignatureAddress();
		address.setAddress(value.getString(BTCOBJ_ADDRESS_ADDRESS, ""));
		address.setRedeemScript(value.getString(BTCOBJ_ADDRESS_REDEEM_SCRIPT,
				""));
		return address;
	}

	public BtcPeer jsonPeer(JsonObject value) throws BtcException {
		BtcPeer peer = new BtcPeer();
		peer.setNetworkAddress(value.getString(BTCOBJ_PEER_ADDRESS, ""));
		peer.setServices(value.getString(BTCOBJ_PEER_SERVICES, ""));
		peer.setLastSend(value.getInt(BTCOBJ_PEER_LAST_SEND, 0));
		peer.setLastReceived(value.getInt(BTCOBJ_PEER_LAST_RECEIVED, 0));
		peer.setBytesSent(value.getInt(BTCOBJ_PEER_BYTES_SENT, 0));
		peer.setBytesReceived(value.getInt(BTCOBJ_PEER_BYTES_RECEIVED, 0));
		peer.setConnectionTime(value.getInt(BTCOBJ_PEER_CONNECTION_TIME, 0));
		peer.setVersion(value.getInt(BTCOBJ_PEER_VERSION, 0));
		peer.setSubVersion(value.getString(BTCOBJ_PEER_SUBVERSION, ""));
		peer.setInbound(value.getBoolean(BTCOBJ_PEER_INBOUND, false));
		peer.setStartingHeight(value.getInt(BTCOBJ_PEER_START_HEIGHT, 0));
		peer.setBanScore(value.getInt(BTCOBJ_PEER_BAN_SCORE, 0));
		peer.setSyncNode(value.getBoolean(BTCOBJ_PEER_SYNC_NODE, false));
		return peer;
	}

	public BtcTransaction jsonTransaction(JsonObject value) throws BtcException {
		BtcTransaction transaction = new BtcTransaction();
		transaction.setTransaction(value.getString(BTCOBJ_TX_TRANSACTION, ""));
		JsonNumber amount = value.getJsonNumber(BTCOBJ_TX_AMOUNT);
		if (amount != null) {
			transaction.setAmount(amount.doubleValue());
		}
		transaction.setConfirmations(value.getInt(BTCOBJ_TX_CONFIRMATIONS, 0));
		transaction.setTime(value.getInt(BTCOBJ_TX_TIME, 0));
		transaction.setTimeReceived(value.getInt(BTCOBJ_TX_TIME_RECEIVED, 0));
		transaction.setTransaction(value.getString(BTCOBJ_TX_BLOCK_HASH, ""));
		transaction.setBlockIndex(value.getInt(BTCOBJ_TX_BLOCK_INDEX, 0));
		transaction.setBlockTime(value.getInt(BTCOBJ_TX_BLOCK_TIME, 0));
		List<BtcTransactionDetail> details = new ArrayList<BtcTransactionDetail>();
		JsonArray txDetails = (JsonArray) value.get(BTCOBJ_TX_DETAILS);
		if (txDetails != null) {
			for (JsonObject txDetail : txDetails.getValuesAs(JsonObject.class)) {
				details.add(jsonTransactionDetail(txDetail));
			}
		} else {
			details.add(jsonTransactionDetail(value));
		}
		transaction.setDetails(details);
		return transaction;
	}

	public BtcTransactionDetail jsonTransactionDetail(JsonObject value)
			throws BtcException {
		BtcTransactionDetail detail = new BtcTransactionDetail();
		detail.setAccount(value.getString(BTCOBJ_TXDETAIL_ACCOUNT, ""));
		detail.setAddress(value.getString(BTCOBJ_TXDETAIL_ADDRESS, ""));
		detail.setCategory(BtcTransactionCategory.getValue(value.getString(
				BTCOBJ_TXDETAIL_CATEGORY, "")));
		JsonNumber amount = value.getJsonNumber(BTCOBJ_TXDETAIL_AMOUNT);
		if (amount != null) {
			detail.setAmount(amount.doubleValue());
		}
		JsonNumber fee = value.getJsonNumber(BTCOBJ_TXDETAIL_FEE);
		if (fee != null) {
			detail.setFee(amount.doubleValue());
		}
		return detail;
	}

	public BtcTransactionOutputSet jsonTransactionOutputSet(JsonObject value)
			throws BtcException {
		BtcTransactionOutputSet output = new BtcTransactionOutputSet();
		output.setHeight(value.getInt(BTCOBJ_TXOUTPUTSET_HEIGHT, 0));
		output.setBestBlock(value.getString(BTCOBJ_TXOUTPUTSET_BEST_BLOCK, ""));
		output.setTransactions(value.getInt(BTCOBJ_TXOUTPUTSET_TRANSACTIONS, 0));
		output.setOutputTransactions(value.getInt(
				BTCOBJ_TXOUTPUTSET_OUTPUT_TRANSACTIONS, 0));
		output.setBytesSerialized(value.getInt(
				BTCOBJ_TXOUTPUTSET_BYTES_SERIALIZED, 0));
		output.setHashSerialized(value.getString(
				BTCOBJ_TXOUTPUTSET_HASH_SERIALIZED, ""));
		JsonNumber amount = value.getJsonNumber(BTCOBJ_TXOUTPUTSET_TOTAL_AMOUT);
		if (amount != null) {
			output.setTotalAmount(amount.doubleValue());
		}
		return output;
	}
}