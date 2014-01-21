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
import java.math.BigDecimal;
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
import javax.json.JsonValue.ValueType;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.btc4j.core.BtcAccount;
import org.btc4j.core.BtcAddedNode;
import org.btc4j.core.BtcAddress;
import org.btc4j.core.BtcBlock;
import org.btc4j.core.BtcException;
import org.btc4j.core.BtcLastBlock;
import org.btc4j.core.BtcMiningInfo;
import org.btc4j.core.BtcMultiSignatureAddress;
import org.btc4j.core.BtcNode;
import org.btc4j.core.BtcPeer;
import org.btc4j.core.BtcInfo;
import org.btc4j.core.BtcRawTransaction;
import org.btc4j.core.BtcScript;
import org.btc4j.core.BtcTransaction;
import org.btc4j.core.BtcTransactionDetail;
import org.btc4j.core.BtcTransactionOutputSet;
import org.btc4j.core.BtcTransactionInput;
import org.btc4j.core.BtcTransactionOutput;

public class BtcJsonRpcHttpClient {
	private static final String BTC4J_DAEMON_DATA_INVALID_ID = "response id does not match request id";
	private static final String BTC4J_DAEMON_DATA_NULL_RESPONSE = "response is empty";
	private static final String BTC4J_DAEMON_DATA_NULL_URL = "server URL is null";
	private static final String BTC4J_DAEMON_HTTP_HEADER = "Content-Type";
	private static final String BTC4J_DAEMON_JSON_CONTENT_TYPE = "application/json";
	private static final String BTC4J_DAEMON_JSONRPC_CONTENT_TYPE = "application/json-rpc";
	private static final String BTC4J_DAEMON_CHARSET = "UTF-8";
	private static final int BTC4J_DAEMON_TIMEOUT = 10000;
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
	private static final String BTCOBJ_LAST_BLOCK_LAST_BLOCK = "lastblock";
	private static final String BTCOBJ_LAST_BLOCK_TRANSACTIONS = "transactions";
	private static final String BTCOBJ_NODE_ADDED_NODE = "addednode";
	private static final String BTCOBJ_NODE_CONNECTED = "connected";
	private static final String BTCOBJ_NODE_ADDRESSES = "addresses";
	private static final String BTCOBJ_NODE_ADDRESS = "address";
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
	private static final String BTCOBJ_SCRIPT_ASM = "asm";
	private static final String BTCOBJ_SCRIPT_HEX = "hex";
	private static final String BTCOBJ_SCRIPT_REQUIRED_SIGNATURES = "reqSigs";
	private static final String BTCOBJ_SCRIPT_TYPE = "type";
	private static final String BTCOBJ_SCRIPT_ADDRESSES = "addresses";
	private static final String BTCOBJ_TX_AMOUNT = "amount";
	private static final String BTCOBJ_TX_FEE = "fee";
	private static final String BTCOBJ_TX_BLOCK_HASH = "blockhash";
	private static final String BTCOBJ_TX_BLOCK_INDEX = "blockindex";
	private static final String BTCOBJ_TX_BLOCK_TIME = "blocktime";
	private static final String BTCOBJ_TX_CONFIRMATIONS = "confirmations";
	private static final String BTCOBJ_TX_DETAILS = "details";
	private static final String BTCOBJ_TX_TIME = "time";
	private static final String BTCOBJ_TX_TIME_RECEIVED = "timereceived";
	private static final String BTCOBJ_TX_TRANSACTION = "txid";
	private static final String BTCOBJ_TX_HEX = "hex";
	private static final String BTCOBJ_TX_VERSION = "version";
	private static final String BTCOBJ_TX_LOCK_TIME = "locktime";
	private static final String BTCOBJ_TX_INPUTS = "vin";
	private static final String BTCOBJ_TX_OUTPUTS = "vout";
	private static final String BTCOBJ_TX_DETAIL_ACCOUNT = "account";
	private static final String BTCOBJ_TX_DETAIL_ADDRESS = "address";
	private static final String BTCOBJ_TX_DETAIL_AMOUNT = "amount";
	private static final String BTCOBJ_TX_DETAIL_CATEGORY = "category";
	private static final String BTCOBJ_TX_DETAIL_FEE = "fee";
	private static final String BTCOBJ_TX_INPUT_TRANSACTION = "txid";
	private static final String BTCOBJ_TX_INPUT_OUTPUT = "vout";
	private static final String BTCOBJ_TX_INPUT_SCRIPT_SIGNATURE = "scriptSig";
	private static final String BTCOBJ_TX_INPUT_SEQUENCE = "sequence";
	private static final String BTCOBJ_TX_OUTPUT_VALUE = "value";
	private static final String BTCOBJ_TX_OUTPUT_INDEX = "n";
	private static final String BTCOBJ_TX_OUTPUT_SCRIPT_PUBLIC_KEY = "scriptPubKey";
	private static final String BTCOBJ_TX_OUTPUT_SET_BEST_BLOCK = "bestblock";
	private static final String BTCOBJ_TX_OUTPUT_SET_BYTES_SERIALIZED = "bytes_serialized";
	private static final String BTCOBJ_TX_OUTPUT_SET_HASH_SERIALIZED = "hash_serialized";
	private static final String BTCOBJ_TX_OUTPUT_SET_HEIGHT = "height";
	private static final String BTCOBJ_TX_OUTPUT_SET_OUTPUT_TRANSACTIONS = "txouts";
	private static final String BTCOBJ_TX_OUTPUT_SET_TOTAL_AMOUNT = "total_amount";
	private static final String BTCOBJ_TX_OUTPUT_SET_TRANSACTIONS = "transactions";
	private static final String JSONRPC_CODE = "code";
	private static final String JSONRPC_DATA = "data";
	private static final String JSONRPC_ERROR = "error";
	private static final String JSONRPC_ID = "id";
	private static final String JSONRPC_REALM = "jsonrpc";
	private static final String JSONRPC_MESSAGE = "message";
	private static final String JSONRPC_METHOD = "method";
	private static final String JSONRPC_PARAMS = "params";
	private static final String JSONRPC_RESULT = "result";
	private static final String JSONRPC_VERSION = "2.0";
	private final static Logger LOGGER = Logger
			.getLogger(BtcJsonRpcHttpClient.class.getName());
	private CredentialsProvider credentialsProvider;
	private RequestConfig requestConfig; 
	private URL url;

	public BtcJsonRpcHttpClient(URL url, String account, String password,
			int timeout) {
		this.url = url;
		credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(
                new AuthScope(url.getHost(), url.getPort(), JSONRPC_REALM),
                new UsernamePasswordCredentials(account, password));
		requestConfig = RequestConfig.custom()
				.setAuthenticationEnabled(true)
				.setConnectionRequestTimeout(timeout)
				.setConnectTimeout(timeout)
				.setSocketTimeout(timeout)
				.setStaleConnectionCheckEnabled(true)
				.build();
	}
	
	public BtcJsonRpcHttpClient(URL url, String account, String password) {
		this(url, account, password, BTC4J_DAEMON_TIMEOUT);
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
		
		try (CloseableHttpClient client = HttpClients.custom()
                .setDefaultCredentialsProvider(credentialsProvider)
                .setDefaultRequestConfig(requestConfig)
                .disableAutomaticRetries()
                .build();) {
			HttpPost post = new HttpPost(url.toString());
			post.addHeader(BTC4J_DAEMON_HTTP_HEADER, BTC4J_DAEMON_JSONRPC_CONTENT_TYPE);
			String guid = UUID.randomUUID().toString();
			JsonObjectBuilder builder = Json.createObjectBuilder();
			builder.add(JSONRPC_REALM, JSONRPC_VERSION).add(JSONRPC_METHOD,
					method);
			if (parameters != null) {
				builder.add(JSONRPC_PARAMS, parameters);
			} else {
				builder.addNull(JSONRPC_PARAMS);
			}
			builder.add(JSONRPC_ID, guid);
			JsonObject request = builder.build();
			LOGGER.info("request: " + request);
			post.setEntity(new StringEntity(request.toString(),
					ContentType.create(BTC4J_DAEMON_JSON_CONTENT_TYPE, BTC4J_DAEMON_CHARSET)));
			ResponseHandler<String> handler = new ResponseHandler<String>() {
				@Override
				public String handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {
					StatusLine status = response.getStatusLine();
					int code = status.getStatusCode();
					String phrase = status.getReasonPhrase();
					HttpEntity entity = response.getEntity();
					String results = (entity != null) ? EntityUtils.toString(entity) : "";
					if ((code != HttpStatus.SC_OK) && (code != HttpStatus.SC_INTERNAL_SERVER_ERROR)) {
						LOGGER.severe(code + " " + phrase);
						throw new ClientProtocolException(code + " " + phrase);
					}
					return results;
				}
			};
			JsonObject results = (JsonObject) Json.createReader(
					new StringReader(client.execute(post, handler))).read();
			if (results == null) {
				LOGGER.severe(BTC4J_DAEMON_DATA_NULL_RESPONSE);
				throw new BtcException(BtcException.BTC4J_ERROR_CODE,
						BtcException.BTC4J_ERROR_MESSAGE + ": "
								+ BTC4J_DAEMON_DATA_NULL_RESPONSE);
			}
			LOGGER.info("response: " + results);
			JsonString id = (JsonString) results.get(JSONRPC_ID);
			if ((id == null) || !(guid.equals(id.getString()))) {
				LOGGER.severe(BTC4J_DAEMON_DATA_INVALID_ID);
				throw new BtcException(BtcException.BTC4J_ERROR_CODE,
						BtcException.BTC4J_ERROR_MESSAGE + ": "
								+ BTC4J_DAEMON_DATA_INVALID_ID);
			}
			JsonValue error = results.get(JSONRPC_ERROR);
			if ((error != null) && (error.getValueType().equals(ValueType.OBJECT))) {
				JsonObject errorObj = (JsonObject) error;
				int code = errorObj.getInt(JSONRPC_CODE);
				String message = errorObj.getString(JSONRPC_MESSAGE); 
				JsonObject data = (JsonObject) errorObj.get(JSONRPC_DATA);
				String dataStr = (data == null)? "": (" " + String.valueOf(data));
				LOGGER.severe("error: " + code + " " + message + dataStr);
				throw new BtcException(code, message + dataStr);
			}
			return results.get(JSONRPC_RESULT);
		} catch (IOException e) {
			LOGGER.severe(String.valueOf(e));
			throw new BtcException(BtcException.BTC4J_ERROR_CODE,
					BtcException.BTC4J_ERROR_MESSAGE + ": " + e.getMessage(), e);
		}
	}

	public BtcAccount jsonAccount(JsonObject value) {
		BtcAccount account = new BtcAccount();
		account.setAccount(value.getString(BTCOBJ_ACCOUNT_ACCOUNT, ""));
		account.setAmount(jsonDouble(value, BTCOBJ_ACCOUNT_AMOUNT));
		account.setConfirmations(jsonLong(value, BTCOBJ_ACCOUNT_CONFIRMATIONS));
		return account;
	}

	public BtcAddedNode jsonAddedNode(JsonObject value) {
		BtcAddedNode addedNode = new BtcAddedNode();
		addedNode.setAddedNode(value.getString(BTCOBJ_NODE_ADDED_NODE, ""));
		addedNode.setConnected(value.getBoolean(BTCOBJ_NODE_CONNECTED, false));
		List<BtcNode> nodes = new ArrayList<BtcNode>();
		JsonArray addresses = value.getJsonArray(BTCOBJ_NODE_ADDRESSES);
		if (addresses != null) {
			for (JsonObject address : addresses.getValuesAs(JsonObject.class)) {
				nodes.add(jsonNode(address));
			}
		}
		addedNode.setAddresses(nodes);
		return addedNode;
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
		address.setAmount(jsonDouble(value, BTCOBJ_ADDRESS_AMOUNT));
		address.setConfirmations(jsonLong(value, BTCOBJ_ADDRESS_CONFIRMATIONS));
		return address;
	}

	public BtcBlock jsonBlock(JsonObject value) throws BtcException {
		BtcBlock block = new BtcBlock();
		block.setHash(value.getString(BTCOBJ_BLOCK_HASH, ""));
		block.setConfirmations(jsonLong(value, BTCOBJ_BLOCK_CONFIRMATIONS));
		block.setSize(jsonLong(value, BTCOBJ_BLOCK_SIZE));
		block.setHeight(jsonLong(value, BTCOBJ_BLOCK_HEIGHT));
		block.setVersion(jsonLong(value, BTCOBJ_BLOCK_VERSION));
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
		block.setTime(jsonLong(value,BTCOBJ_BLOCK_TIME));
		block.setNonce(jsonLong(value, BTCOBJ_BLOCK_NONCE));
		block.setBits(value.getString(BTCOBJ_BLOCK_BITS, ""));
		block.setDifficulty(jsonDouble(value, BTCOBJ_BLOCK_DIFFICULTY));
		block.setPreviousBlockHash(value.getString(
				BTCOBJ_BLOCK_PREVIOUS_BLOCK_HASH, ""));
		block.setNextBlockHash(value
				.getString(BTCOBJ_BLOCK_NEXT_BLOCK_HASH, ""));
		return block;
	}

	public BtcInfo jsonInfo(JsonObject value) throws BtcException {
		BtcInfo info = new BtcInfo();
		info.setVersion(jsonLong(value, BTCOBJ_INFO_VERSION));
		info.setProtocolVersion(jsonLong(value, BTCOBJ_INFO_PROTOCOL_VERSION));
		info.setWalletVersion(jsonLong(value, BTCOBJ_INFO_WALLET_VERSION));
		info.setBalance(jsonDouble(value, BTCOBJ_INFO_BALANCE));
		info.setBlocks(jsonLong(value, BTCOBJ_INFO_BLOCKS));
		info.setTimeOffset(jsonLong(value, BTCOBJ_INFO_TIME_OFFSET));
		info.setConnections(jsonLong(value, BTCOBJ_INFO_CONNECTIONS));
		info.setProxy(value.getString(BTCOBJ_INFO_PROXY, ""));
		info.setDifficulty(jsonDouble(value, BTCOBJ_INFO_DIFFICULTY));
		info.setTestnet(value.getBoolean(BTCOBJ_INFO_TESTNET, false));
		info.setKeyPoolOldest(jsonLong(value, BTCOBJ_INFO_KEYPOOL_OLDEST));
		info.setKeyPoolSize(jsonLong(value, BTCOBJ_INFO_KEYPOOL_SIZE));
		info.setTransactionFee(jsonDouble(value, BTCOBJ_INFO_TRANSACTION_FEE));
		info.setErrors(value.getString(BTCOBJ_INFO_ERRORS, ""));
		return info;
	}

	public BtcLastBlock jsonLastBlock(JsonObject value) throws BtcException {
		BtcLastBlock lastBlock = new BtcLastBlock();
		lastBlock.setLastBlock(value
				.getString(BTCOBJ_LAST_BLOCK_LAST_BLOCK, ""));
		List<BtcTransaction> transactions = new ArrayList<BtcTransaction>();
		JsonArray txs = value.getJsonArray(BTCOBJ_LAST_BLOCK_TRANSACTIONS);
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
		info.setBlocks(jsonLong(value, BTCOBJ_INFO_BLOCKS));
		info.setCurrentBlockSize(jsonLong(value, BTCOBJ_INFO_CURRENT_BLOCK_SIZE));
		info.setCurrentBlockTransactions(jsonLong(value, 
				BTCOBJ_INFO_CURRENT_BLOCK_TRANSACTIONS));
		info.setDifficulty(jsonDouble(value, BTCOBJ_INFO_DIFFICULTY));
		info.setErrors(value.getString(BTCOBJ_INFO_ERRORS, ""));
		info.setGenerate(value.getBoolean(BTCOBJ_INFO_GENERATE, false));
		info.setGenProcessorLimit(jsonLong(value, BTCOBJ_INFO_PROCESSOR_LIMIT, -1));
		info.setHashesPerSecond(jsonLong(value, BTCOBJ_INFO_HASHES_PER_SECOND));
		info.setPooledTransactions(jsonLong(value, 
				BTCOBJ_INFO_POOLED_TRANSACTIONS));
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

	public BtcNode jsonNode(JsonObject value) {
		BtcNode node = new BtcNode();
		node.setAddress(value.getString(BTCOBJ_NODE_ADDRESS, ""));
		node.setConnected(value.getString(BTCOBJ_NODE_CONNECTED, ""));
		return node;
	}

	public BtcPeer jsonPeer(JsonObject value) throws BtcException {
		BtcPeer peer = new BtcPeer();
		peer.setNetworkAddress(value.getString(BTCOBJ_PEER_ADDRESS, ""));
		peer.setServices(value.getString(BTCOBJ_PEER_SERVICES, ""));
		peer.setLastSend(jsonLong(value, BTCOBJ_PEER_LAST_SEND));
		peer.setLastReceived(jsonLong(value, BTCOBJ_PEER_LAST_RECEIVED));
		peer.setBytesSent(jsonLong(value, BTCOBJ_PEER_BYTES_SENT));
		peer.setBytesReceived(jsonLong(value, BTCOBJ_PEER_BYTES_RECEIVED));
		peer.setConnectionTime(jsonLong(value, BTCOBJ_PEER_CONNECTION_TIME));
		peer.setVersion(jsonLong(value, BTCOBJ_PEER_VERSION));
		peer.setSubVersion(value.getString(BTCOBJ_PEER_SUBVERSION, ""));
		peer.setInbound(value.getBoolean(BTCOBJ_PEER_INBOUND, false));
		peer.setStartingHeight(jsonLong(value, BTCOBJ_PEER_START_HEIGHT));
		peer.setBanScore(jsonLong(value, BTCOBJ_PEER_BAN_SCORE));
		peer.setSyncNode(value.getBoolean(BTCOBJ_PEER_SYNC_NODE, false));
		return peer;
	}

	public BtcRawTransaction jsonRawTransaction(JsonObject value)
			throws BtcException {
		BtcRawTransaction transaction = new BtcRawTransaction();
		transaction.setHex(value.getString(BTCOBJ_TX_HEX, ""));
		transaction.setTransaction(value.getString(BTCOBJ_TX_TRANSACTION, ""));
		transaction.setVersion(jsonLong(value, BTCOBJ_TX_VERSION));
		transaction.setLockTime(jsonLong(value, BTCOBJ_TX_LOCK_TIME));
		List<BtcTransactionInput> inputTransactions = new ArrayList<BtcTransactionInput>();
		JsonArray inputs = value.getJsonArray(BTCOBJ_TX_INPUTS);
		if (inputs != null) {
			for (JsonObject input : inputs.getValuesAs(JsonObject.class)) {
				inputTransactions.add(jsonTransactionInput(input));
			}
		}
		transaction.setInputTransactions(inputTransactions);
		List<BtcTransactionOutput> outputTransactions = new ArrayList<BtcTransactionOutput>();
		JsonArray outputs = value.getJsonArray(BTCOBJ_TX_OUTPUTS);
		if (outputs != null) {
			for (JsonObject output : outputs.getValuesAs(JsonObject.class)) {
				outputTransactions.add(jsonTransactionOutput(output));
			}
		}
		transaction.setOutputTransactions(outputTransactions);
		transaction.setBlockHash(value.getString(BTCOBJ_TX_BLOCK_HASH, ""));
		transaction.setConfirmations(jsonLong(value, BTCOBJ_TX_CONFIRMATIONS));
		transaction.setTime(jsonLong(value, BTCOBJ_TX_TIME));
		transaction.setBlockTime(jsonLong(value, BTCOBJ_TX_BLOCK_TIME));
		return transaction;
	}

	public BtcScript jsonScript(JsonObject value) throws BtcException {
		BtcScript script = new BtcScript();
		script.setAsm(value.getString(BTCOBJ_SCRIPT_ASM, ""));
		script.setHex(value.getString(BTCOBJ_SCRIPT_HEX, ""));
		script.setRequiredSignatures(jsonLong(value, 
				BTCOBJ_SCRIPT_REQUIRED_SIGNATURES));
		script.setType(BtcScript.Type.getValue(value.getString(
				BTCOBJ_SCRIPT_TYPE, "")));
		List<String> addresses = new ArrayList<String>();
		JsonArray addrs = value.getJsonArray(BTCOBJ_SCRIPT_ADDRESSES);
		if (addrs != null) {
			for (JsonString addr : addrs.getValuesAs(JsonString.class)) {
				addresses.add(addr.getString());
			}
		}
		script.setAddresses(addresses);
		return script;
	}

	public BtcTransaction jsonTransaction(JsonObject value) throws BtcException {
		BtcTransaction transaction = new BtcTransaction();
		transaction.setTransaction(value.getString(BTCOBJ_TX_TRANSACTION, ""));
		transaction.setAmount(jsonDouble(value, BTCOBJ_TX_AMOUNT));
		transaction.setFee(jsonDouble(value, BTCOBJ_TX_FEE));
		transaction.setConfirmations(jsonLong(value, BTCOBJ_TX_CONFIRMATIONS));
		transaction.setTime(jsonLong(value, BTCOBJ_TX_TIME));
		transaction.setTimeReceived(jsonLong(value, BTCOBJ_TX_TIME_RECEIVED));
		transaction.setBlockHash(value.getString(BTCOBJ_TX_BLOCK_HASH, ""));
		transaction.setBlockIndex(jsonLong(value, BTCOBJ_TX_BLOCK_INDEX));
		transaction.setBlockTime(jsonLong(value, BTCOBJ_TX_BLOCK_TIME));
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
		detail.setAccount(value.getString(BTCOBJ_TX_DETAIL_ACCOUNT, ""));
		detail.setAddress(value.getString(BTCOBJ_TX_DETAIL_ADDRESS, ""));
		detail.setCategory(BtcTransaction.Category.getValue(value.getString(
				BTCOBJ_TX_DETAIL_CATEGORY, "")));
		detail.setAmount(jsonDouble(value, BTCOBJ_TX_DETAIL_AMOUNT));
		detail.setFee(jsonDouble(value, BTCOBJ_TX_DETAIL_FEE));
		return detail;
	}

	public BtcTransactionInput jsonTransactionInput(JsonObject value)
			throws BtcException {
		BtcTransactionInput input = new BtcTransactionInput();
		input.setTransaction(value.getString(BTCOBJ_TX_INPUT_TRANSACTION, ""));
		input.setOutput(jsonLong(value, BTCOBJ_TX_INPUT_OUTPUT));
		JsonObject scriptSignature = value
				.getJsonObject(BTCOBJ_TX_INPUT_SCRIPT_SIGNATURE);
		if (scriptSignature != null) {
			input.setScriptSignature(jsonScript(scriptSignature));
		}
		input.setSequence(jsonLong(value, BTCOBJ_TX_INPUT_SEQUENCE));
		return input;
	}

	public BtcTransactionOutput jsonTransactionOutput(JsonObject value)
			throws BtcException {
		BtcTransactionOutput output = new BtcTransactionOutput();
		output.setAmount(jsonDouble(value, BTCOBJ_TX_OUTPUT_VALUE));
		output.setIndex(jsonLong(value, BTCOBJ_TX_OUTPUT_INDEX));
		JsonObject scriptPublicKey = value
				.getJsonObject(BTCOBJ_TX_OUTPUT_SCRIPT_PUBLIC_KEY);
		if (scriptPublicKey != null) {
			output.setScriptPublicKey(jsonScript(scriptPublicKey));
		}
		return output;
	}

	public BtcTransactionOutputSet jsonTransactionOutputSet(JsonObject value)
			throws BtcException {
		BtcTransactionOutputSet output = new BtcTransactionOutputSet();
		output.setHeight(jsonLong(value, BTCOBJ_TX_OUTPUT_SET_HEIGHT));
		output.setBestBlock(value
				.getString(BTCOBJ_TX_OUTPUT_SET_BEST_BLOCK, ""));
		output.setTransactions(jsonLong(value, BTCOBJ_TX_OUTPUT_SET_TRANSACTIONS));
		output.setOutputTransactions(jsonLong(value, 
				BTCOBJ_TX_OUTPUT_SET_OUTPUT_TRANSACTIONS));
		output.setBytesSerialized(jsonLong(value, 
				BTCOBJ_TX_OUTPUT_SET_BYTES_SERIALIZED));
		output.setHashSerialized(value.getString(
				BTCOBJ_TX_OUTPUT_SET_HASH_SERIALIZED, ""));
		output.setTotalAmount(jsonDouble(value, BTCOBJ_TX_OUTPUT_SET_TOTAL_AMOUNT));
		return output;
	}
	
	public long jsonLong(JsonObject value, String key) {
		JsonNumber number = value.getJsonNumber(key);
		return (number == null)? 0: number.longValueExact();
	}
	
	public long jsonLong(JsonObject value, String key, long defaultValue) {
		JsonNumber number = value.getJsonNumber(key);
		return (number == null)? defaultValue: number.longValueExact();
	}
	
	public BigDecimal jsonDouble(JsonObject value, String key) {
		JsonNumber number = value.getJsonNumber(key);
		return (number == null)? BigDecimal.ZERO: number.bigDecimalValue();
	}
}