/*
 The MIT License (MIT)
 
 Copyright (c) 2013, 2014 by ggbusto@gmx.com

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
import javax.json.JsonReader;
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
import org.btc4j.core.BtcBlockSubmission;
import org.btc4j.core.BtcBlockTemplate;
import org.btc4j.core.BtcCoinbase;
import org.btc4j.core.BtcException;
import org.btc4j.core.BtcInfo;
import org.btc4j.core.BtcLastBlock;
import org.btc4j.core.BtcMiningInfo;
import org.btc4j.core.BtcMultiSignatureAddress;
import org.btc4j.core.BtcNode;
import org.btc4j.core.BtcOutputPart;
import org.btc4j.core.BtcPeer;
import org.btc4j.core.BtcRawTransaction;
import org.btc4j.core.BtcScript;
import org.btc4j.core.BtcTransaction;
import org.btc4j.core.BtcTransactionDetail;
import org.btc4j.core.BtcInput;
import org.btc4j.core.BtcOutput;
import org.btc4j.core.BtcOutputSet;
import org.btc4j.core.BtcTransactionTemplate;
import org.btc4j.core.BtcWork;

public class BtcJsonRpcHttpClient {
	private static final String BTC4J_DAEMON_DATA_INVALID_ID = "invalid json id";
	private static final String BTC4J_DAEMON_DATA_NULL_JSON = "json value is empty";
	private static final String BTC4J_DAEMON_DATA_NULL_URL = "server URL is null";
	private static final String BTC4J_DAEMON_DATA_INVALID_TYPE = "unexpected return type ";
	private static final String BTC4J_DAEMON_HTTP_HEADER = "Content-Type";
	private static final String BTC4J_DAEMON_JSON_CONTENT_TYPE = "application/json";
	private static final String BTC4J_DAEMON_JSONRPC_CONTENT_TYPE = "application/json-rpc";
	private static final String BTC4J_DAEMON_CHARSET = "UTF-8";
	private static final int BTC4J_DAEMON_TIMEOUT = 60000;
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
	private static final String BTCOBJ_BLOCK_TEMPLATE_TRANSACTIONS = "transactions";
	private static final String BTCOBJ_BLOCK_TEMPLATE_TARGET = "target";
	private static final String BTCOBJ_BLOCK_TEMPLATE_MIN_TIME = "mintime";
	private static final String BTCOBJ_BLOCK_TEMPLATE_MUTABLE = "mutable";
	private static final String BTCOBJ_BLOCK_TEMPLATE_NONCE_RANGE = "noncerange";
	private static final String BTCOBJ_BLOCK_TEMPLATE_SIGNATURE_OPERATIONS = "sigoplimit";
	private static final String BTCOBJ_BLOCK_TEMPLATE_SIZE = "sizelimit";
	private static final String BTCOBJ_BLOCK_TEMPLATE_TIME = "curtime";
	private static final String BTCOBJ_COIN_AUX = "coinbaseaux";
	private static final String BTCOBJ_COIN_VALUE = "coinbasevalue";
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
	private static final String BTCOBJ_SCRIPT_PUBLIC_KEY = "hex";
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
	private static final String BTCOBJ_TX_COMPLETE = "complete";
	private static final String BTCOBJ_TX_DETAIL_ACCOUNT = "account";
	private static final String BTCOBJ_TX_DETAIL_ADDRESS = "address";
	private static final String BTCOBJ_TX_DETAIL_AMOUNT = "amount";
	private static final String BTCOBJ_TX_DETAIL_CATEGORY = "category";
	private static final String BTCOBJ_TX_DETAIL_FEE = "fee";
	private static final String BTCOBJ_TX_INPUT_TRANSACTION = "txid";
	private static final String BTCOBJ_TX_INPUT_OUTPUT = "vout";
	private static final String BTCOBJ_TX_INPUT_SCRIPT_SIGNATURE = "scriptSig";
	private static final String BTCOBJ_TX_INPUT_SEQUENCE = "sequence";
	private static final String BTCOBJ_TX_OUTPUT_TRANSACTION = "txid";
	private static final String BTCOBJ_TX_OUTPUT_BEST_BLOCK = "bestblock";
	private static final String BTCOBJ_TX_OUTPUT_CONFIRMATIONS = "confirmations";
	private static final String BTCOBJ_TX_OUTPUT_VALUE = "value";
	private static final String BTCOBJ_TX_OUTPUT_INDEX = "n";
	private static final String BTCOBJ_TX_OUTPUT_SCRIPT_PUBLIC_KEY = "scriptPubKey";
	private static final String BTCOBJ_TX_OUTPUT_VERSION = "version";
	private static final String BTCOBJ_TX_OUTPUT_COINBASE = "coinbase";
	private static final String BTCOBJ_TX_OUTPUT_OUTPUT = "vout";
	private static final String BTCOBJ_TX_OUTPUT_SET_BEST_BLOCK = "bestblock";
	private static final String BTCOBJ_TX_OUTPUT_SET_BYTES_SERIALIZED = "bytes_serialized";
	private static final String BTCOBJ_TX_OUTPUT_SET_HASH_SERIALIZED = "hash_serialized";
	private static final String BTCOBJ_TX_OUTPUT_SET_HEIGHT = "height";
	private static final String BTCOBJ_TX_OUTPUT_SET_OUTPUT_TRANSACTIONS = "txouts";
	private static final String BTCOBJ_TX_OUTPUT_SET_TOTAL_AMOUNT = "total_amount";
	private static final String BTCOBJ_TX_OUTPUT_SET_TRANSACTIONS = "transactions";
	private static final String BTCOBJ_WORK_MIDSTATE = "midstate";
	private static final String BTCOBJ_WORK_DATA = "data";
	private static final String BTCOBJ_WORK_HASH = "hash1";
	private static final String BTCOBJ_WORK_TARGET = "target";
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
	private final static Logger LOG = Logger
			.getLogger(BtcJsonRpcHttpClient.class.getName());
	private CredentialsProvider credentialsProvider;
	private RequestConfig requestConfig;
	private URL url;

	public BtcJsonRpcHttpClient(URL url, int timeout) {
		this.url = url;
		requestConfig = RequestConfig.custom().setAuthenticationEnabled(true)
				.setConnectionRequestTimeout(timeout)
				.setConnectTimeout(timeout).setSocketTimeout(timeout)
				.setStaleConnectionCheckEnabled(true).build();
	}

	public BtcJsonRpcHttpClient(URL url) {
		this(url, BTC4J_DAEMON_TIMEOUT);
	}

	public BtcJsonRpcHttpClient(URL url, String account, String password,
			int timeout) {
		this(url, timeout);
		setCredentials(account, password);
	}

	public BtcJsonRpcHttpClient(URL url, String account, String password) {
		this(url, account, password, BTC4J_DAEMON_TIMEOUT);
	}

	public void setCredentials(String account, String password) {
		credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(
				new AuthScope(url.getHost(), url.getPort(), JSONRPC_REALM),
				new UsernamePasswordCredentials(account, password));
	}

	public String jsonInvoke(String request) throws BtcException {
		LOG.info("request: " + request);
		String reply = "";
		if (url == null) {
			LOG.severe(BTC4J_DAEMON_DATA_NULL_URL);
			throw new BtcException(BtcException.BTC4J_ERROR_CODE,
					BtcException.BTC4J_ERROR_MESSAGE + ": "
							+ BTC4J_DAEMON_DATA_NULL_URL);
		}
		try (CloseableHttpClient client = HttpClients.custom()
				.setDefaultCredentialsProvider(credentialsProvider)
				.setDefaultRequestConfig(requestConfig)
				.disableAutomaticRetries().build();) {
			HttpPost post = new HttpPost(url.toString());
			post.addHeader(BTC4J_DAEMON_HTTP_HEADER,
					BTC4J_DAEMON_JSONRPC_CONTENT_TYPE);
			post.setEntity(new StringEntity(request, ContentType
					.create(BTC4J_DAEMON_JSON_CONTENT_TYPE,
							BTC4J_DAEMON_CHARSET)));
			ResponseHandler<String> handler = new ResponseHandler<String>() {
				@Override
				public String handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {
					StatusLine status = response.getStatusLine();
					int code = status.getStatusCode();
					String phrase = status.getReasonPhrase();
					HttpEntity entity = response.getEntity();
					String results = (entity != null) ? EntityUtils
							.toString(entity) : "";
					if ((code != HttpStatus.SC_OK)
							&& (code != HttpStatus.SC_INTERNAL_SERVER_ERROR)) {
						LOG.severe(code + " " + phrase);
						throw new ClientProtocolException(code + " " + phrase);
					}
					return results;
				}
			};
			reply = client.execute(post, handler);
		} catch (IOException e) {
			LOG.severe(String.valueOf(e));
			throw new BtcException(BtcException.BTC4J_ERROR_CODE,
					BtcException.BTC4J_ERROR_MESSAGE + ": " + e.getMessage(), e);
		}
		LOG.info("response: " + reply);	
		return reply;
	}
	
	public JsonValue invoke(String method) throws BtcException {
		return invoke(method, null);
	}

	public JsonValue invoke(String method, JsonArray parameters)
			throws BtcException {
			JsonObjectBuilder builder = Json.createObjectBuilder();
			builder.add(JSONRPC_REALM, JSONRPC_VERSION).add(JSONRPC_METHOD,
					method);
			if (parameters != null) {
				builder.add(JSONRPC_PARAMS, parameters);
			} else {
				builder.addNull(JSONRPC_PARAMS);
			}
			String guid = UUID.randomUUID().toString();
			builder.add(JSONRPC_ID, guid);
			JsonObject request = builder.build();
			JsonObject response = jsonObject(jsonValue(jsonInvoke(String.valueOf(request))));
			if (response == null) {
				LOG.severe(BTC4J_DAEMON_DATA_NULL_JSON);
				throw new BtcException(BtcException.BTC4J_ERROR_CODE,
						BtcException.BTC4J_ERROR_MESSAGE + ": "
								+ BTC4J_DAEMON_DATA_NULL_JSON);
			}
			if (!(guid.equals(jsonId(response)))) {
				LOG.severe(BTC4J_DAEMON_DATA_INVALID_ID);
				throw new BtcException(BtcException.BTC4J_ERROR_CODE,
						BtcException.BTC4J_ERROR_MESSAGE + ": "
								+ BTC4J_DAEMON_DATA_INVALID_ID);
			}
			JsonValue error = response.get(JSONRPC_ERROR);
			if ((error != null)
					&& (error.getValueType().equals(ValueType.OBJECT))) {
				JsonObject errorObj = (JsonObject) error;
				int code = errorObj.getInt(JSONRPC_CODE);
				String message = errorObj.getString(JSONRPC_MESSAGE);
				JsonObject data = (JsonObject) errorObj.get(JSONRPC_DATA);
				String dataStr = (data == null) ? "" : (" " + String
						.valueOf(data));
				LOG.severe("error: " + code + " " + message + dataStr);
				throw new BtcException(code, message + dataStr);
			}
			return response.get(JSONRPC_RESULT);
		
	}

	public BtcAccount jsonAccount(JsonValue value) throws BtcException {
		JsonObject object = jsonObject(value);
		if (object == null) {
			return null;
		}
		BtcAccount account = new BtcAccount();
		account.setAccount(object.getString(BTCOBJ_ACCOUNT_ACCOUNT, ""));
		account.setAmount(jsonDouble(object, BTCOBJ_ACCOUNT_AMOUNT));
		account.setConfirmations(jsonLong(object,
				BTCOBJ_ACCOUNT_CONFIRMATIONS));
		return account;
	}

	public BtcAddedNode jsonAddedNode(JsonValue value) throws BtcException {
		JsonObject object = jsonObject(value);
		if (object == null) {
			return null;
		}
		BtcAddedNode addedNode = new BtcAddedNode();
		addedNode.setAddedNode(object.getString(BTCOBJ_NODE_ADDED_NODE, ""));
		addedNode.setConnected(object.getBoolean(BTCOBJ_NODE_CONNECTED,
				false));
		List<BtcNode> nodes = new ArrayList<BtcNode>();
		JsonValue addresses = object.get(BTCOBJ_NODE_ADDRESSES);
		if ((addresses != null) && (addresses.getValueType() == JsonValue.ValueType.ARRAY) && (addresses instanceof JsonArray)) {
			JsonArray addressesArray = (JsonArray) addresses;
			for (JsonValue address : addressesArray
					.getValuesAs(JsonValue.class)) {
				nodes.add(jsonNode(address));
			}
		}
		addedNode.setAddresses(nodes);
		return addedNode;
	}

	public BtcAddress jsonAddress(JsonValue value) throws BtcException {
		JsonObject object = jsonObject(value);
		if (object == null) {
			return null;
		}
		BtcAddress address = new BtcAddress();
		address.setValid(object.getBoolean(BTCOBJ_ADDRESS_VALID, false));
		address.setAddress(object.getString(BTCOBJ_ADDRESS_ADDRESS, ""));
		address.setMine(object.getBoolean(BTCOBJ_ADDRESS_MINE, false));
		address.setScript(object.getBoolean(BTCOBJ_ADDRESS_SCRIPT, false));
		address.setPublicKey(object.getString(BTCOBJ_ADDRESS_PUBLIC_KEY, ""));
		address.setCompressed(object.getBoolean(BTCOBJ_ADDRESS_COMPRESSED,
				false));
		BtcAccount account = new BtcAccount();
		account.setAccount(object.getString(BTCOBJ_ADDRESS_ACCOUNT, ""));
		address.setAccount(account);
		address.setAmount(jsonDouble(object, BTCOBJ_ADDRESS_AMOUNT));
		address.setConfirmations(jsonLong(object,
				BTCOBJ_ADDRESS_CONFIRMATIONS));
		return address;
	}

	public BtcBlock jsonBlock(JsonValue value) throws BtcException {
		JsonObject object = jsonObject(value);
		if (object == null) {
			return null;
		}
		BtcBlock block = new BtcBlock();
		block.setHash(object.getString(BTCOBJ_BLOCK_HASH, ""));
		block.setConfirmations(jsonLong(object, BTCOBJ_BLOCK_CONFIRMATIONS));
		block.setSize(jsonLong(object, BTCOBJ_BLOCK_SIZE));
		block.setHeight(jsonLong(object, BTCOBJ_BLOCK_HEIGHT));
		block.setVersion(jsonLong(object, BTCOBJ_BLOCK_VERSION));
		block.setMerkleRoot(object.getString(BTCOBJ_BLOCK_MERKLE_ROOT, ""));
		List<BtcTransaction> transactions = new ArrayList<BtcTransaction>();
		JsonValue txIds = object
				.get(BTCOBJ_BLOCK_TRANSACTIONS);
		if ((txIds != null) && (txIds.getValueType() == JsonValue.ValueType.ARRAY) && (txIds instanceof JsonArray)) {
			JsonArray txIdsArray = (JsonArray) txIds;
			for (JsonValue transactionId : txIdsArray
					.getValuesAs(JsonValue.class)) {
				BtcTransaction transaction = new BtcTransaction();
				transaction.setTransaction(jsonString(transactionId));
				transactions.add(transaction);
			}
		}
		block.setTransactions(transactions);
		block.setTime(jsonLong(object, BTCOBJ_BLOCK_TIME));
		block.setNonce(jsonLong(object, BTCOBJ_BLOCK_NONCE));
		block.setBits(object.getString(BTCOBJ_BLOCK_BITS, ""));
		block.setDifficulty(jsonDouble(object, BTCOBJ_BLOCK_DIFFICULTY));
		block.setPreviousBlockHash(object.getString(
				BTCOBJ_BLOCK_PREVIOUS_BLOCK_HASH, ""));
		block.setNextBlockHash(object.getString(
				BTCOBJ_BLOCK_NEXT_BLOCK_HASH, ""));
		return block;
	}

	public BtcBlockSubmission jsonBlockSubmission(JsonValue value)
			throws BtcException {
		JsonObject object = jsonObject(value);
		if (object == null) {
			return null;
		}
		BtcBlockSubmission submission = new BtcBlockSubmission();
		// TODO
		return submission;
	}

	public BtcBlockTemplate jsonBlockTemplate(JsonValue value)
			throws BtcException {
		JsonObject object = jsonObject(value);
		if (object == null) {
			return null;
		}
		BtcBlockTemplate block = new BtcBlockTemplate();
		block.setVersion(jsonLong(object, BTCOBJ_BLOCK_VERSION));
		block.setPreviousBlockHash(object.getString(
				BTCOBJ_BLOCK_PREVIOUS_BLOCK_HASH, ""));
		List<BtcTransactionTemplate> transactions = new ArrayList<BtcTransactionTemplate>();
		JsonValue txs = object
				.get(BTCOBJ_BLOCK_TEMPLATE_TRANSACTIONS);
		if ((txs != null) && (txs.getValueType() == JsonValue.ValueType.ARRAY) && (txs instanceof JsonArray)) {
			JsonArray txsArray = (JsonArray) txs;
			for (JsonValue tx : txsArray.getValuesAs(JsonValue.class)) {
				transactions.add(jsonTransactionTemplate(tx));
			}
		}
		block.setTransactions(transactions);
		block.setCoinbase(jsonCoinbase(value));
		block.setTarget(object.getString(BTCOBJ_BLOCK_TEMPLATE_TARGET, ""));
		block.setMinimumTime(jsonLong(object, BTCOBJ_BLOCK_TEMPLATE_MIN_TIME));
		List<String> mutable = new ArrayList<String>();
		JsonValue mutableIds = object
				.get(BTCOBJ_BLOCK_TEMPLATE_MUTABLE);
		if ((mutableIds != null) && (mutableIds.getValueType() == JsonValue.ValueType.ARRAY) && (mutableIds instanceof JsonArray)) {
			JsonArray mutableIdsArray = (JsonArray) mutableIds;
			for (JsonValue mutableId : mutableIdsArray
					.getValuesAs(JsonValue.class)) {
				mutable.add(jsonString(mutableId));
			}
		}
		block.setMutable(mutable);
		block.setNonceRange(object.getString(
				BTCOBJ_BLOCK_TEMPLATE_NONCE_RANGE, ""));
		block.setSignatureOperations(jsonLong(object,
				BTCOBJ_BLOCK_TEMPLATE_SIGNATURE_OPERATIONS));
		block.setSize(jsonLong(object, BTCOBJ_BLOCK_TEMPLATE_SIZE));
		block.setTime(jsonLong(object, BTCOBJ_BLOCK_TEMPLATE_TIME));
		block.setBits(object.getString(BTCOBJ_BLOCK_BITS, ""));
		block.setHeight(jsonLong(object, BTCOBJ_BLOCK_HEIGHT));
		return block;
	}

	public BtcCoinbase jsonCoinbase(JsonValue value) throws BtcException {
		JsonObject object = jsonObject(value);
		if (object == null) {
			return null;
		}
		BtcCoinbase coin = new BtcCoinbase();
		Map<String, String> auxiliary = new HashMap<String, String>();
		JsonValue aux = object.get(BTCOBJ_COIN_AUX);
		if ((aux != null) && (aux.getValueType() == JsonValue.ValueType.OBJECT) && (aux instanceof JsonObject)) {
			JsonObject auxObject = (JsonObject) aux;
			for (String key : auxObject.keySet()) {
				auxiliary.put(key, auxObject.getString(key, ""));
			}
		}
		coin.setAux(auxiliary);
		coin.setValue(jsonDouble(object, BTCOBJ_COIN_VALUE));
		return coin;
	}

	public BtcInfo jsonInfo(JsonValue value) throws BtcException {
		JsonObject object = jsonObject(value);
		if (object == null) {
			return null;
		}
		BtcInfo info = new BtcInfo();
		info.setVersion(jsonLong(object, BTCOBJ_INFO_VERSION));
		info.setProtocolVersion(jsonLong(object,
				BTCOBJ_INFO_PROTOCOL_VERSION));
		info.setWalletVersion(jsonLong(object, BTCOBJ_INFO_WALLET_VERSION));
		info.setBalance(jsonDouble(object, BTCOBJ_INFO_BALANCE));
		info.setBlocks(jsonLong(object, BTCOBJ_INFO_BLOCKS));
		info.setTimeOffset(jsonLong(object, BTCOBJ_INFO_TIME_OFFSET));
		info.setConnections(jsonLong(object, BTCOBJ_INFO_CONNECTIONS));
		info.setProxy(object.getString(BTCOBJ_INFO_PROXY, ""));
		info.setDifficulty(jsonDouble(object, BTCOBJ_INFO_DIFFICULTY));
		info.setTestnet(object.getBoolean(BTCOBJ_INFO_TESTNET, false));
		info.setKeyPoolOldest(jsonLong(object, BTCOBJ_INFO_KEYPOOL_OLDEST));
		info.setKeyPoolSize(jsonLong(object, BTCOBJ_INFO_KEYPOOL_SIZE));
		info.setTransactionFee(jsonDouble(object,
				BTCOBJ_INFO_TRANSACTION_FEE));
		info.setErrors(object.getString(BTCOBJ_INFO_ERRORS, ""));
		return info;
	}

	public BtcLastBlock jsonLastBlock(JsonValue value) throws BtcException {
		JsonObject object = jsonObject(value);
		if (object == null) {
			return null;
		}
		BtcLastBlock lastBlock = new BtcLastBlock();
		lastBlock.setLastBlock(object.getString(
				BTCOBJ_LAST_BLOCK_LAST_BLOCK, ""));
		List<BtcTransaction> transactions = new ArrayList<BtcTransaction>();
		JsonValue txs = object.get(BTCOBJ_LAST_BLOCK_TRANSACTIONS);
		if ((txs != null) && (txs.getValueType() == JsonValue.ValueType.ARRAY) && (txs instanceof JsonArray)) {
			JsonArray txsArray = (JsonArray) txs;
			for (JsonValue tx : txsArray.getValuesAs(JsonValue.class)) {
				transactions.add(jsonTransaction(tx));
			}
		}
		lastBlock.setTransactions(transactions);
		return lastBlock;
	}

	public BtcMiningInfo jsonMiningInfo(JsonValue value) throws BtcException {
		JsonObject object = jsonObject(value);
		if (object == null) {
			return null;
		}
		BtcMiningInfo info = new BtcMiningInfo();
		info.setBlocks(jsonLong(object, BTCOBJ_INFO_BLOCKS));
		info.setCurrentBlockSize(jsonLong(object,
				BTCOBJ_INFO_CURRENT_BLOCK_SIZE));
		info.setCurrentBlockTransactions(jsonLong(object,
				BTCOBJ_INFO_CURRENT_BLOCK_TRANSACTIONS));
		info.setDifficulty(jsonDouble(object, BTCOBJ_INFO_DIFFICULTY));
		info.setErrors(object.getString(BTCOBJ_INFO_ERRORS, ""));
		info.setGenerate(object.getBoolean(BTCOBJ_INFO_GENERATE, false));
		info.setGenProcessorLimit(jsonLong(object,
				BTCOBJ_INFO_PROCESSOR_LIMIT, -1));
		info.setHashesPerSecond(jsonLong(object,
				BTCOBJ_INFO_HASHES_PER_SECOND));
		info.setPooledTransactions(jsonLong(object,
				BTCOBJ_INFO_POOLED_TRANSACTIONS));
		info.setTestnet(object.getBoolean(BTCOBJ_INFO_TESTNET, false));
		return info;
	}

	public BtcMultiSignatureAddress jsonMultiSignatureAddress(JsonValue value)
			throws BtcException {
		JsonObject object = jsonObject(value);
		if (object == null) {
			return null;
		}
		BtcMultiSignatureAddress address = new BtcMultiSignatureAddress();
		address.setAddress(object.getString(BTCOBJ_ADDRESS_ADDRESS, ""));
		address.setRedeemScript(object.getString(
				BTCOBJ_ADDRESS_REDEEM_SCRIPT, ""));
		return address;
	}

	public BtcNode jsonNode(JsonValue value) throws BtcException {
		JsonObject object = jsonObject(value);
		if (object == null) {
			return null;
		}
		BtcNode node = new BtcNode();
		node.setAddress(object.getString(BTCOBJ_NODE_ADDRESS, ""));
		node.setConnected(object.getString(BTCOBJ_NODE_CONNECTED, ""));
		return node;
	}

	public BtcPeer jsonPeer(JsonValue value) throws BtcException {
		JsonObject object = jsonObject(value);
		if (object == null) {
			return null;
		}
		BtcPeer peer = new BtcPeer();
		peer.setNetworkAddress(object.getString(BTCOBJ_PEER_ADDRESS, ""));
		peer.setServices(object.getString(BTCOBJ_PEER_SERVICES, ""));
		peer.setLastSend(jsonLong(object, BTCOBJ_PEER_LAST_SEND));
		peer.setLastReceived(jsonLong(object, BTCOBJ_PEER_LAST_RECEIVED));
		peer.setBytesSent(jsonLong(object, BTCOBJ_PEER_BYTES_SENT));
		peer.setBytesReceived(jsonLong(object, BTCOBJ_PEER_BYTES_RECEIVED));
		peer.setConnectionTime(jsonLong(object, BTCOBJ_PEER_CONNECTION_TIME));
		peer.setVersion(jsonLong(object, BTCOBJ_PEER_VERSION));
		peer.setSubVersion(object.getString(BTCOBJ_PEER_SUBVERSION, ""));
		peer.setInbound(object.getBoolean(BTCOBJ_PEER_INBOUND, false));
		peer.setStartingHeight(jsonLong(object, BTCOBJ_PEER_START_HEIGHT));
		peer.setBanScore(jsonLong(object, BTCOBJ_PEER_BAN_SCORE));
		peer.setSyncNode(object.getBoolean(BTCOBJ_PEER_SYNC_NODE, false));
		return peer;
	}

	public BtcRawTransaction jsonRawTransaction(JsonValue value)
			throws BtcException {
		JsonObject object = jsonObject(value);
		if (object == null) {
			return null;
		}
		BtcRawTransaction transaction = new BtcRawTransaction();
		transaction.setHex(object.getString(BTCOBJ_TX_HEX, ""));
		transaction.setTransaction(object.getString(BTCOBJ_TX_TRANSACTION,
				""));
		transaction.setVersion(jsonLong(object, BTCOBJ_TX_VERSION));
		transaction.setLockTime(jsonLong(object, BTCOBJ_TX_LOCK_TIME));
		List<BtcInput> inputTransactions = new ArrayList<BtcInput>();
		JsonValue inputs = object.get(BTCOBJ_TX_INPUTS);
		if ((inputs != null) && (inputs.getValueType() == JsonValue.ValueType.ARRAY) && (inputs instanceof JsonArray)) {
			JsonArray inputsArray = (JsonArray) inputs;
			for (JsonValue input : inputsArray.getValuesAs(JsonValue.class)) {
				inputTransactions.add(jsonInput(input));
			}
		}
		transaction.setInputs(inputTransactions);
		List<BtcOutput> outputTransactions = new ArrayList<BtcOutput>();
		JsonValue outputs = object.get(BTCOBJ_TX_OUTPUTS);
		if ((outputs != null) && (outputs.getValueType() == JsonValue.ValueType.ARRAY) && (outputs instanceof JsonArray)) {
			JsonArray outputsArray = (JsonArray) outputs;
			for (JsonValue output : outputsArray.getValuesAs(JsonValue.class)) {
				outputTransactions.add(jsonOutput(output));
			}
		}
		transaction.setOutputs(outputTransactions);
		transaction.setBlockHash(object.getString(BTCOBJ_TX_BLOCK_HASH, ""));
		transaction.setConfirmations(jsonLong(object,
				BTCOBJ_TX_CONFIRMATIONS));
		transaction.setTime(jsonLong(object, BTCOBJ_TX_TIME));
		transaction.setBlockTime(jsonLong(object, BTCOBJ_TX_BLOCK_TIME));
		transaction.setComplete(object.getBoolean(BTCOBJ_TX_COMPLETE, true));
		return transaction;
	}

	public BtcScript jsonScript(JsonValue value) throws BtcException {
		JsonObject object = jsonObject(value);
		if (object == null) {
			return null;
		}
		BtcScript script = new BtcScript();
		script.setAsm(object.getString(BTCOBJ_SCRIPT_ASM, ""));
		script.setPublicKey(object.getString(BTCOBJ_SCRIPT_PUBLIC_KEY, ""));
		script.setRequiredSignatures(jsonLong(object,
				BTCOBJ_SCRIPT_REQUIRED_SIGNATURES));
		script.setType(BtcScript.Type.getValue(object.getString(
				BTCOBJ_SCRIPT_TYPE, "")));
		List<String> addresses = new ArrayList<String>();
		JsonValue addrs = object.get(BTCOBJ_SCRIPT_ADDRESSES);
		if ((addrs) != null && (addrs.getValueType() == JsonValue.ValueType.ARRAY) && (addrs instanceof JsonArray)) {
			JsonArray addrsArray = (JsonArray) addrs;
			for (JsonValue addr : addrsArray.getValuesAs(JsonValue.class)) {
				addresses.add(jsonString(addr));
			}
		}
		script.setAddresses(addresses);
		return script;
	}

	public BtcTransaction jsonTransaction(JsonValue value) throws BtcException {
		JsonObject object = jsonObject(value);
		if (object == null) {
			return null;
		}
		BtcTransaction transaction = new BtcTransaction();
		transaction.setTransaction(object.getString(BTCOBJ_TX_TRANSACTION,
				""));
		transaction.setAmount(jsonDouble(object, BTCOBJ_TX_AMOUNT));
		transaction.setFee(jsonDouble(object, BTCOBJ_TX_FEE));
		transaction.setConfirmations(jsonLong(object,
				BTCOBJ_TX_CONFIRMATIONS));
		transaction.setTime(jsonLong(object, BTCOBJ_TX_TIME));
		transaction
				.setTimeReceived(jsonLong(object, BTCOBJ_TX_TIME_RECEIVED));
		transaction.setBlockHash(object.getString(BTCOBJ_TX_BLOCK_HASH, ""));
		transaction.setBlockIndex(jsonLong(object, BTCOBJ_TX_BLOCK_INDEX));
		transaction.setBlockTime(jsonLong(object, BTCOBJ_TX_BLOCK_TIME));
		List<BtcTransactionDetail> details = new ArrayList<BtcTransactionDetail>();
		JsonValue txDetails = object.get(BTCOBJ_TX_DETAILS);
		if ((txDetails != null) && (txDetails.getValueType() == JsonValue.ValueType.ARRAY) && (txDetails instanceof JsonArray)) {
			JsonArray txDetailsArray = (JsonArray) txDetails;
			for (JsonValue txDetail : txDetailsArray
					.getValuesAs(JsonValue.class)) {
				details.add(jsonTransactionDetail(txDetail));
			}
		} else {
			details.add(jsonTransactionDetail(value));
		}
		transaction.setDetails(details);
		return transaction;
	}

	public BtcTransactionDetail jsonTransactionDetail(JsonValue value)
			throws BtcException {
		JsonObject object = jsonObject(value);
		if (object == null) {
			return null;
		}
		BtcTransactionDetail detail = new BtcTransactionDetail();
		detail.setAccount(object.getString(BTCOBJ_TX_DETAIL_ACCOUNT, ""));
		detail.setAddress(object.getString(BTCOBJ_TX_DETAIL_ADDRESS, ""));
		detail.setCategory(BtcTransaction.Category.getValue(object
				.getString(BTCOBJ_TX_DETAIL_CATEGORY, "")));
		detail.setAmount(jsonDouble(object, BTCOBJ_TX_DETAIL_AMOUNT));
		detail.setFee(jsonDouble(object, BTCOBJ_TX_DETAIL_FEE));
		return detail;
	}

	public BtcInput jsonInput(JsonValue value)
			throws BtcException {
		JsonObject object = jsonObject(value);
		if (object == null) {
			return null;
		}
		BtcInput input = new BtcInput();
		input.setTransaction(object.getString(BTCOBJ_TX_INPUT_TRANSACTION,
				""));
		input.setOutput(jsonLong(object, BTCOBJ_TX_INPUT_OUTPUT));
		JsonValue script = object
				.get(BTCOBJ_TX_INPUT_SCRIPT_SIGNATURE);
		if ((script != null) && (script.getValueType() == JsonValue.ValueType.OBJECT) && (script instanceof JsonObject)) {
			input.setScript(jsonScript(script));
		}
		input.setSequence(jsonLong(object, BTCOBJ_TX_INPUT_SEQUENCE));
		return input;
	}

	public BtcOutput jsonOutput(JsonValue value) throws BtcException {
		JsonObject object = jsonObject(value);
		if (object == null) {
			return null;
		}
		BtcOutput output = new BtcOutput();
		output.setTransaction(object.getString(BTCOBJ_TX_OUTPUT_TRANSACTION,
				""));
		output.setBestBlock(object
				.getString(BTCOBJ_TX_OUTPUT_BEST_BLOCK, ""));
		output.setConfirmations(jsonLong(object,
				BTCOBJ_TX_OUTPUT_CONFIRMATIONS));
		output.setValue(jsonDouble(object, BTCOBJ_TX_OUTPUT_VALUE));
		output.setIndex(jsonLong(object, BTCOBJ_TX_OUTPUT_INDEX));
		output.setOutput(jsonLong(object, BTCOBJ_TX_OUTPUT_OUTPUT));
		JsonValue scriptPubKey = object
				.get(BTCOBJ_TX_OUTPUT_SCRIPT_PUBLIC_KEY);
		if (scriptPubKey != null) {
			if ((scriptPubKey.getValueType() == JsonValue.ValueType.OBJECT) && (scriptPubKey instanceof JsonObject)) {
				output.setScript(jsonScript(scriptPubKey));
			}
			if ((scriptPubKey.getValueType() == JsonValue.ValueType.STRING) && (scriptPubKey instanceof JsonString)) {
				BtcScript script = new BtcScript();
				script.setPublicKey(jsonString(scriptPubKey));
				output.setScript(script);
			}
		}
		output.setVersion(jsonLong(object, BTCOBJ_TX_OUTPUT_VERSION));
		output.setCoinbase(object.getBoolean(BTCOBJ_TX_OUTPUT_COINBASE,
				false));
		output.setDetail(jsonTransactionDetail(object));
		return output;
	}
	
	public BtcOutputPart jsonOutputPart(JsonValue value) throws BtcException {
		JsonObject object = jsonObject(value);
		if (object == null) {
			return null;
		}
		BtcOutput output = new BtcOutput();
		output.setTransaction(object.getString(BTCOBJ_TX_OUTPUT_TRANSACTION,
				""));
		output.setOutput(jsonLong(object, BTCOBJ_TX_OUTPUT_OUTPUT));
		return output;
	}

	public BtcOutputSet jsonOutputSet(JsonValue value)
			throws BtcException {
		JsonObject object = jsonObject(value);
		if (object == null) {
			return null;
		}
		BtcOutputSet output = new BtcOutputSet();
		output.setHeight(jsonLong(object, BTCOBJ_TX_OUTPUT_SET_HEIGHT));
		output.setBestBlock(object.getString(
				BTCOBJ_TX_OUTPUT_SET_BEST_BLOCK, ""));
		output.setTransactions(jsonLong(object,
				BTCOBJ_TX_OUTPUT_SET_TRANSACTIONS));
		output.setOutputs(jsonLong(object,
				BTCOBJ_TX_OUTPUT_SET_OUTPUT_TRANSACTIONS));
		output.setBytesSerialized(jsonLong(object,
				BTCOBJ_TX_OUTPUT_SET_BYTES_SERIALIZED));
		output.setHashSerialized(object.getString(
				BTCOBJ_TX_OUTPUT_SET_HASH_SERIALIZED, ""));
		output.setTotalAmount(jsonDouble(object,
				BTCOBJ_TX_OUTPUT_SET_TOTAL_AMOUNT));
		return output;
	}

	public BtcTransactionTemplate jsonTransactionTemplate(JsonValue value)
			throws BtcException {
		JsonObject object = jsonObject(value);
		if (object == null) {
			return null;
		}
		BtcTransactionTemplate template = new BtcTransactionTemplate();
		// TODO
		return template;
	}

	public BtcWork jsonWork(JsonValue value) throws BtcException {
		JsonObject object = jsonObject(value);
		if (object == null) {
			return null;
		}
		BtcWork work = new BtcWork();
		work.setMidState(object.getString(BTCOBJ_WORK_MIDSTATE, ""));
		work.setData(object.getString(BTCOBJ_WORK_DATA, ""));
		work.setHash(object.getString(BTCOBJ_WORK_HASH, ""));
		work.setTarget(object.getString(BTCOBJ_WORK_TARGET, ""));
		return work;
	}

	public long jsonLong(JsonObject object, String key) throws BtcException {
		JsonNumber number = object.getJsonNumber(key);
		return (number == null) ? 0 : number.longValueExact();
	}

	public long jsonLong(JsonObject object, String key, long defaultValue)
			throws BtcException {
		JsonNumber number = object.getJsonNumber(key);
		return (number == null) ? defaultValue : number.longValueExact();
	}

	public long jsonLong(JsonValue value) throws BtcException {
		if ((value == null) || (value.getValueType() == JsonValue.ValueType.NULL)) {
			return 0;
		}
		if ((value.getValueType() == JsonValue.ValueType.NUMBER) && (value instanceof JsonNumber)) {
			return ((JsonNumber) value).longValue();
		}
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BTC4J_DAEMON_DATA_INVALID_TYPE + value.getValueType());
	}
	
	public BigDecimal jsonDouble(JsonObject object, String key)
			throws BtcException {
		JsonNumber number = object.getJsonNumber(key);
		return (number == null) ? BigDecimal.ZERO : number.bigDecimalValue();
	}
	
	public BigDecimal jsonDouble(JsonValue value) throws BtcException {
		if ((value == null) || (value.getValueType() == JsonValue.ValueType.NULL)) {
			return null;
		}
		if ((value.getValueType() == JsonValue.ValueType.NUMBER) && (value instanceof JsonNumber)) {
			return ((JsonNumber) value).bigDecimalValue();
		}
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BTC4J_DAEMON_DATA_INVALID_TYPE + value.getValueType());
	}

	public String jsonString(JsonValue value) throws BtcException {
		return ((value != null)
				&& (value.getValueType() == JsonValue.ValueType.STRING)
				&& (value instanceof JsonString)) ? ((JsonString) value)
				.getString() : "";
	}
	
	public JsonObject jsonObject(JsonValue value) throws BtcException {
		if ((value == null) || (value.getValueType() == JsonValue.ValueType.NULL)) {
			return null;
		}
		if ((value.getValueType() == JsonValue.ValueType.OBJECT) && (value instanceof JsonObject)) {
			return (JsonObject) value;
		}
		throw new BtcException(BtcException.BTC4J_ERROR_CODE,
				BtcException.BTC4J_ERROR_MESSAGE + ": "
						+ BTC4J_DAEMON_DATA_INVALID_TYPE + value.getValueType());
	}
	
	public JsonValue jsonValue(String value) throws BtcException {
		try (JsonReader reader = Json.createReader(new StringReader(value))) {
			return reader.read();
		} catch (Throwable t) {
			throw new BtcException(BtcException.BTC4J_ERROR_CODE,
					BtcException.BTC4J_ERROR_MESSAGE + ": " + t.getMessage(), t);
		} 
	}
	
	public String jsonId(JsonValue value) throws BtcException {
		JsonObject object = jsonObject(value);
		if (object == null) {
			return "";
		}
		return object.getString(JSONRPC_ID, "");
	}
	
	public String jsonId(String value) throws BtcException {
		return jsonId(jsonValue(value));
	}
	
	public String jsonError(String id, int code, String message) {
		JsonObjectBuilder builder = Json.createObjectBuilder()
				.add(JSONRPC_REALM, JSONRPC_VERSION)
				.addNull(JSONRPC_RESULT);
		JsonObject error = Json.createObjectBuilder()
				.add(JSONRPC_CODE, code)
				.add(JSONRPC_MESSAGE, message)
				.addNull(JSONRPC_DATA)
				.build();
		builder.add(JSONRPC_ERROR, error).add(JSONRPC_ID, id);
		return String.valueOf(builder.build());
	}
}