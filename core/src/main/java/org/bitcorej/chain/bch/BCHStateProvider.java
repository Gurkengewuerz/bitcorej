package org.bitcorej.chain.bch;

import org.bitcoinj.core.*;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcorej.chain.bitcoin.BitcoinStateProvider;
import org.bitcorej.core.Network;
import org.bitcorej.utils.NumericUtil;

import java.util.List;

public class BCHStateProvider extends BitcoinStateProvider {
    public BCHStateProvider(Network network) {
        super(network);
    }

    @Override
    public String signRawTransaction(String rawTx, List<String> keys) {
        Transaction tx = super.buildTransaction(rawTx);

        List<TransactionInput> inputs = tx.getInputs();

        for (int i = 0; i < inputs.size(); i++) {
            TransactionInput input = tx.getInput(i);

            Script scriptPubKey = new Script(input.getScriptBytes());

            ECKey ecKey = ECKey.fromPrivate(NumericUtil.hexToBytes(this.selectPrivateKeys(scriptPubKey, keys)));

            Sha256Hash hash = tx.hashForSignatureWitness(i, scriptPubKey, Coin.valueOf(52005704l), Transaction.SigHash.ALL, false);
            ECKey.ECDSASignature ecSig = ecKey.sign(hash);
            TransactionSignature txSig = new TransactionSignature(ecSig, Transaction.SigHash.ALL, false, true);

            if (scriptPubKey.isSentToRawPubKey()) {
                input.setScriptSig(ScriptBuilder.createInputScript(txSig));
            } else {
                if (!scriptPubKey.isSentToAddress()) {
                    return null;
                }
                input.setScriptSig(ScriptBuilder.createInputScript(txSig, ecKey));
            }

            // tx.addSignedInput(input.getOutpoint(), scriptPubKey, ecKey, Transaction.SigHash.ALL,false, true);
        }

        return NumericUtil.bytesToHex(tx.bitcoinSerialize());
    }
}
