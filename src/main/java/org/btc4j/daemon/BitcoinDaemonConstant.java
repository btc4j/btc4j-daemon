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

public class BitcoinDaemonConstant {
	public static final String BTCAPI_ADD_MULTI_SIGNATURE_ADDRESS = "addmultisigaddress";
	public static final String BTCAPI_ADD_NODE = "addnode";
	public static final String BTCAPI_BACKUP_WALLET = "backupwallet";
	public static final String BTCAPI_CREATE_MULTI_SIGNATURE_ADDRESS = "createmultisig";
	public static final String BTCAPI_CREATE_RAW_TRANSACTION = "createrawtransaction";
	public static final String BTCAPI_DECODE_RAW_TRANSACTION = "decoderawtransaction";
	public static final String BTCAPI_DUMP_PRIVATE_KEY = "dumpprivkey";
	public static final String BTCAPI_ENCRYPT_WALLET = "encryptwallet";
	public static final String BTCAPI_GET_ACCOUNT = "getaccount";
	public static final String BTCAPI_GET_ACCOUNT_ADDRESS = "getaccountaddress";
	public static final String BTCAPI_GET_ADDED_NODE_INFORMATION = "getaddednodeinfo";
	public static final String BTCAPI_GET_ADDRESSES_BY_ACCOUNT = "getaddressesbyaccount";
	public static final String BTCAPI_GET_BALANCE = "getbalance";
	public static final String BTCAPI_GET_BLOCK = "getblock";
	public static final String BTCAPI_GET_BLOCK_COUNT = "getblockcount";
	public static final String BTCAPI_GET_BLOCK_HASH = "getblockhash";
	public static final String BTCAPI_GET_BLOCK_TEMPLATE = "getblocktemplate";
	public static final String BTCAPI_GET_CONNECTION_COUNT = "getconnectioncount";
	public static final String BTCAPI_GET_DIFFICULTY = "getdifficulty";
	public static final String BTCAPI_GET_GENERATE = "getgenerate";
	public static final String BTCAPI_GET_HASHES_PER_SECOND = "gethashespersec";
	public static final String BTCAPI_GET_INFORMATION = "getinfo";
	public static final String BTCAPI_GET_MINING_INFORMATION = "getmininginfo";
	public static final String BTCAPI_GET_NEW_ADDRESS = "getnewaddress";
	public static final String BTCAPI_GET_PEER_INFORMATION = "getpeerinfo";
	public static final String BTCAPI_GET_RAW_MEMORY_POOL = "getrawmempool";
	public static final String BTCAPI_GET_RAW_TRANSACTION = "getrawtransaction";
	public static final String BTCAPI_GET_RECEIVED_BY_ACCOUNT = "getreceivedbyaccount";
	public static final String BTCAPI_GET_RECEIVED_BY_ADDRESS = "getreceivedbyaddress";
	public static final String BTCAPI_GET_TRANSACTION = "gettransaction";
	public static final String BTCAPI_GET_TRANSACTION_OUTPUT = "gettxout";
	public static final String BTCAPI_GET_TRANSACTION_OUTPUT_SET_INFORMATION = "gettxoutsetinfo";
	public static final String BTCAPI_GET_WORK = "getwork";
	public static final String BTCAPI_HELP = "help";
	public static final String BTCAPI_IMPORT_PRIVATE_KEY = "importprivkey";
	public static final String BTCAPI_KEY_POOL_REFILL = "keypoolrefill";
	public static final String BTCAPI_LIST_ACCOUNTS = "listaccounts";
	public static final String BTCAPI_LIST_ADDRESS_GROUPINGS = "listaddressgroupings";
	public static final String BTCAPI_LIST_LOCK_UNSPENT = "listlockunspent";
	public static final String BTCAPI_LIST_RECEIVED_BY_ACCOUNT = "listreceivedbyaccount";
	public static final String BTCAPI_LIST_RECEIVED_BY_ADDRESS = "listreceivedbyaddress";
	public static final String BTCAPI_LIST_SINCE_BLOCK = "listsinceblock";
	public static final String BTCAPI_LIST_TRANSACTIONS = "listtransactions";
	public static final String BTCAPI_LIST_UNSPENT = "listunspent";
	public static final String BTCAPI_LOCK_UNSPENT = "lockunspent";
	public static final String BTCAPI_MOVE = "move";
	public static final String BTCAPI_SEND_FROM = "sendfrom";
	public static final String BTCAPI_SEND_MANY = "sendmany";
	public static final String BTCAPI_SEND_RAW_TRANSACTION = "sendrawtransaction";
	public static final String BTCAPI_SEND_TO_ADDRESS = "sendtoaddress";
	public static final String BTCAPI_SET_ACCOUNT = "setaccount";
	public static final String BTCAPI_SET_GENERATE = "setgenerate";
	public static final String BTCAPI_SET_TRANSACTION_FEE = "settxfee";
	public static final String BTCAPI_SIGN_MESSAGE = "signmessage";
	public static final String BTCAPI_SIGN_RAW_TRANSACTION = "signrawtransaction";
	public static final String BTCAPI_STOP = "stop";
	public static final String BTCAPI_SUBMIT_BLOCK = "submitblock";
	public static final String BTCAPI_VALIDATE_ADDRESS = "validateaddress";
	public static final String BTCAPI_VERIFY_MESSAGE = "verifymessage";
	public static final String BTCOBJ_ACCOUNT_ACCOUNT = "account";
	public static final String BTCOBJ_ACCOUNT_AMOUNT = "amount";
	public static final String BTCOBJ_ACCOUNT_CONFIRMATIONS = "confirmations";
	public static final String BTCOBJ_ADDRESS_VALID = "isvalid";
	public static final String BTCOBJ_ADDRESS_ADDRESS = "address";
	public static final String BTCOBJ_ADDRESS_MINE = "ismine";
	public static final String BTCOBJ_ADDRESS_SCRIPT = "isscript";
	public static final String BTCOBJ_ADDRESS_PUBLIC_KEY = "pubkey";
	public static final String BTCOBJ_ADDRESS_COMPRESSED = "iscompressed";
	public static final String BTCOBJ_ADDRESS_ACCOUNT = "account";
	public static final String BTCOBJ_ADDRESS_AMOUNT = "amount";
	public static final String BTCOBJ_ADDRESS_CONFIRMATIONS = "confirmations";
	public static final String BTCOBJ_BLOCK_HASH = "hash";
	public static final String BTCOBJ_BLOCK_CONFIRMATIONS = "confirmations";
	public static final String BTCOBJ_BLOCK_SIZE = "size";
	public static final String BTCOBJ_BLOCK_HEIGHT = "height";
	public static final String BTCOBJ_BLOCK_VERSION = "version";
	public static final String BTCOBJ_BLOCK_MERKLE_ROOT = "merkleroot";
	public static final String BTCOBJ_BLOCK_TRANSACTIONS = "tx";
	public static final String BTCOBJ_BLOCK_TIME = "time";
	public static final String BTCOBJ_BLOCK_NONCE = "nonce";
	public static final String BTCOBJ_BLOCK_BITS = "bits";
	public static final String BTCOBJ_BLOCK_DIFFICULTY = "difficulty";
	public static final String BTCOBJ_BLOCK_PREVIOUS_BLOCK_HASH = "previousblockhash";
	public static final String BTCOBJ_BLOCK_NEXT_BLOCK_HASH = "nextblockhash";
	public static final String BTCOBJ_LASTBLOCK_LASTBLOCK = "lastblock";
	public static final String BTCOBJ_LASTBLOCK_TRANSACTIONS = "transactions";
	public static final String BTCOBJ_INFO_VERSION = "version";
	public static final String BTCOBJ_INFO_PROTOCOL_VERSION = "protocolversion";
	public static final String BTCOBJ_INFO_WALLET_VERSION = "walletversion";
	public static final String BTCOBJ_INFO_BALANCE = "balance";
	public static final String BTCOBJ_INFO_BLOCKS = "blocks";
	public static final String BTCOBJ_INFO_TIME_OFFSET = "timeoffset";
	public static final String BTCOBJ_INFO_CONNECTIONS = "connections";
	public static final String BTCOBJ_INFO_PROXY = "proxy";
	public static final String BTCOBJ_INFO_DIFFICULTY = "difficulty";
	public static final String BTCOBJ_INFO_TESTNET = "testnet";
	public static final String BTCOBJ_INFO_KEYPOOL_OLDEST = "keypoololdest";
	public static final String BTCOBJ_INFO_KEYPOOL_SIZE = "keypoolsize";
	public static final String BTCOBJ_INFO_TRANSACTION_FEE = "paytxfee";
	public static final String BTCOBJ_INFO_ERRORS = "errors";
	public static final String BTCOBJ_INFO_CURRENT_BLOCK_SIZE = "currentblocksize";
	public static final String BTCOBJ_INFO_CURRENT_BLOCK_TRANSACTIONS = "currentblocktx";
	public static final String BTCOBJ_INFO_GENERATE = "generate";
	public static final String BTCOBJ_INFO_PROCESSOR_LIMIT = "genproclimit";
	public static final String BTCOBJ_INFO_HASHES_PER_SECOND = "hashespersec";
	public static final String BTCOBJ_INFO_POOLED_TRANSACTIONS = "pooledtx";
	public static final String BTCOBJ_PEER_ADDRESS = "addr";
	public static final String BTCOBJ_PEER_SERVICES = "services";
	public static final String BTCOBJ_PEER_LAST_SEND = "lastsend";
	public static final String BTCOBJ_PEER_LAST_RECEIVED = "lastrecv";
	public static final String BTCOBJ_PEER_BYTES_SENT = "bytessent";
	public static final String BTCOBJ_PEER_BYTES_RECEIVED = "bytesrecv";
	public static final String BTCOBJ_PEER_CONNECTION_TIME = "conntime";
	public static final String BTCOBJ_PEER_VERSION = "version";
	public static final String BTCOBJ_PEER_SUBVERSION = "subver";
	public static final String BTCOBJ_PEER_INBOUND = "inbound";
	public static final String BTCOBJ_PEER_START_HEIGHT = "startingheight";
	public static final String BTCOBJ_PEER_BAN_SCORE = "banscore";
	public static final String BTCOBJ_PEER_SYNC_NODE = "syncnode";
	public static final String BTCOBJ_TXOUTPUTSET_HEIGHT = "height";
	public static final String BTCOBJ_TXOUTPUTSET_BEST_BLOCK = "bestblock";
	public static final String BTCOBJ_TXOUTPUTSET_TRANSACTIONS = "transactions";
	public static final String BTCOBJ_TXOUTPUTSET_OUTPUT_TRANSACTIONS = "txouts";
	public static final String BTCOBJ_TXOUTPUTSET_BYTES_SERIALIZED = "bytes_serialized";
	public static final String BTCOBJ_TXOUTPUTSET_HASH_SERIALIZED = "hash_serialized";
	public static final String BTCOBJ_TXOUTPUTSET_TOTAL_AMOUT = "total_amount";
	public static final String JSONRPC_JSONRPC = "jsonrpc";
	public static final String JSONRPC_VERSION = "2.0";
	public static final String JSONRPC_ID = "id";
	public static final String JSONRPC_METHOD = "method";
	public static final String JSONRPC_PARAMS = "params";
	public static final String JSONRPC_RESULT = "result";
	public static final String JSONRPC_ERROR = "error";
	public static final String JSONRPC_CODE = "code";
	public static final String JSONRPC_MESSAGE = "message";
	public static final String JSONRPC_DATA = "data";
	public static final String[] BTC4J_DAEMON_VERSIONS = { "0.8.6" };
	public static final String BTC4J_DAEMON_HTTP = "http";
	public static final String BTC4J_DAEMON_HTTP_HEADER = "Content-Type";
	public static final String BTC4J_DAEMON_JSONRPC_CONTENT_TYPE = "application/json-rpc";
	public static final String BTC4J_DAEMON_JSON_CONTENT_TYPE = "application/json";
	public static final int BTC4J_DAEMON_CONNECT_ATTEMPTS = 3;
	public static final String BTC4J_DAEMON_HOST = "127.0.0.1";
	public static final int BTC4J_DAEMON_PORT = 18332;
	public static final String BTC4J_DAEMON_ARG_TESTNET = "-testnet=1";
	public static final String BTC4J_DAEMON_ARG_ACCOUNT = "-rpcuser=";
	public static final String BTC4J_DAEMON_ARG_PASSWORD = "-rpcpassword=";
	public static final String BTC4J_DAEMON_DATA_NULL_URL = "Server URL is null";
	public static final String BTC4J_DAEMON_DATA_NULL_RESPONSE = "Response is empty";
	public static final String BTC4J_DAEMON_DATA_INVALID_ID = "Response id does not match request id";
	public static final String BTC4J_DAEMON_DATA_INVALID_ERROR = "Response error is empty";
}
