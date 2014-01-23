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

import java.net.URL;

public class BtcDaemonMain {

	public static void main(String[] args) {
		try {
			BtcDaemon daemon = new BtcDaemon(new URL("https://127.0.0.1:18332"), "user", "password");
			//daemon.help("sendtoaddress");
			//daemon.walletLock();
			//daemon.walletPassphrase("password");
			//daemon.walletPassphrase("password");
			//System.out.println("getbalance: " + daemon.getBalance(""));
			//System.out.println("sendtoaddress: " + daemon.sendToAddress("mvDicKjyxUxJFt1icwbZjsJ7HnkAgVgbHj", 0.06));
			//System.out.println("getbalance: " + daemon.getBalance(""));
			//System.out.println("getbalance user: " + daemon.getBalance("user"));
			//System.out.println("getrawtransaction: " + daemon.getRawTransaction("e96404552c900fcf2d8ae797babc1ae0dac7e849856162da9fd90e35a18a6788", true));
			//System.out.println("gettransaction: " + daemon.getTransaction("e96404552c900fcf2d8ae797babc1ae0dac7e849856162da9fd90e35a18a6788"));
			daemon.stop();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
/*
addmultisigaddress <nrequired> <'["key","key"]'> [account]
addnode <node> <add|remove|onetry>
backupwallet <destination>
createmultisig <nrequired> <'["key","key"]'>
createrawtransaction [{"txid":txid,"vout":n},...] {address:amount,...}
decoderawtransaction <hex string>
dumpprivkey <bitcoinaddress>
getaccount <bitcoinaddress>
getaccountaddress <account>
getaddednodeinfo <dns> [node]
getaddressesbyaccount <account>
getbalance [account] [minconf=1]
getblock <hash>
getblockcount
getblockhash <index>
getblocktemplate [params]
getconnectioncount
getdifficulty
getgenerate
gethashespersec
getinfo
getmininginfo
getnewaddress [account]
getpeerinfo
getrawmempool
getrawtransaction <txid> [verbose=0]
getreceivedbyaccount <account> [minconf=1]
getreceivedbyaddress <bitcoinaddress> [minconf=1]
gettransaction <txid>
gettxout <txid> <n> [includemempool=true]
gettxoutsetinfo
getwork [data]
help [command]
importprivkey <bitcoinprivkey> [label] [rescan=true]
keypoolrefill
listaccounts [minconf=1]
listaddressgroupings
listlockunspent
listreceivedbyaccount [minconf=1] [includeempty=false]
listreceivedbyaddress [minconf=1] [includeempty=false]
listsinceblock [blockhash] [target-confirmations]
listtransactions [account] [count=10] [from=0]
listunspent [minconf=1] [maxconf=9999999]  ["address",...]
lockunspent unlock? [array-of-Objects]
move <fromaccount> <toaccount> <amount> [minconf=1] [comment]
sendfrom <fromaccount> <tobitcoinaddress> <amount> [minconf=1] [comment] [comment-to]
sendmany <fromaccount> {address:amount,...} [minconf=1] [comment]
sendrawtransaction <hex string>
sendtoaddress <bitcoinaddress> <amount> [comment] [comment-to]
setaccount <bitcoinaddress> <account>
setgenerate <generate> [genproclimit]
settxfee <amount>
signmessage <bitcoinaddress> <message>
signrawtransaction <hex string> [{"txid":txid,"vout":n,"scriptPubKey":hex,"redeemScript":hex},...] [<privatekey1>,...] [sighashtype="ALL"]
stop
submitblock <hex data> [optional-params-obj]
validateaddress <bitcoinaddress>
verifymessage <bitcoinaddress> <signature> <message>
walletlock
walletpassphrase <passphrase> <timeout>
walletpassphrasechange <oldpassphrase> <newpassphrase>
*/

