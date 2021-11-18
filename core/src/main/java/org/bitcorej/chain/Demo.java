package org.bitcorej.chain;

/**
 * Created by gurkengewuerz.de on 18.11.2021.
 */
public class Demo {

    public static void main(String[] args) throws Exception {
        ChainStateProxy provider = new ChainStateProxy("btc", "main");
        KeyPair key = provider.generateKeyPair();
        System.out.println(key);

    }
}
