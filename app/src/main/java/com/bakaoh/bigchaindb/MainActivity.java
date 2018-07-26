package com.bakaoh.bigchaindb;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.authenteq.api.AssetsApi;
import com.authenteq.builders.BigchainDbConfigBuilder;
import com.authenteq.builders.BigchainDbTransactionBuilder;
import com.authenteq.constants.Operations;
import com.authenteq.json.strategy.MetaDataDeserializer;
import com.authenteq.json.strategy.MetaDataSerializer;
import com.authenteq.model.Account;
import com.authenteq.model.Assets;
import com.authenteq.model.MetaData;
import com.authenteq.model.MetaDatas;
import com.authenteq.model.Transaction;
import com.authenteq.util.JsonUtils;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;

import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private static final String publicKey = "302a300506032b657003210033c43dc2180936a2a9138a05f06c892d2fb1cfda4562cbc35373bf13cd8ed373";
    private static final String privateKey = "302e020100300506032b6570042204206f6b0cd095f1e83fc5f08bffb79c7c8a30e77a3ab65f4bc659026b76394fcea8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        Map<String, String> assetData = new TreeMap<String, String>() {{
            put("msg", "Test");
        }};

        // JsonUtils.setJsonDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");

        BigchainDbConfigBuilder
                .baseUrl("https://test.bigchaindb.com")
                .addToken("app_id", "")
                .addToken("app_key", "").setup();

        MetaData metaData = new MetaData();
        metaData.setId("51ce82a14ca274d43e4992bbce41f6fdeb755f846e48e710a3bbb3b0cf8e4204");
        metaData.setMetaData("msg", "Testing Android");


        setContentView(R.layout.activity_main);

        try {
            Transaction transaction = BigchainDbTransactionBuilder
                    .init()
                    .addAssets(assetData, TreeMap.class)
                    .addAssetDataClass(TreeMap.class, null)
                    .addMetaData(metaData)
                    .addMetaDataClassSerializer(MetaData.class, new MetaDataSerializer())
                    .addMetaDataClassDeserializer(MetaDatas.class, new MetaDataDeserializer())
                    .operation(Operations.CREATE)
                    .buildAndSign(
                            (EdDSAPublicKey) Account.publicKeyFromHex(publicKey),
                            (EdDSAPrivateKey) Account.privateKeyFromHex(privateKey))
                    .sendTransaction();

            Assets assets = AssetsApi.getAssets("Test");


            TextView text = findViewById(R.id.hello);
            String data = "";
            if(assets.getAssets().size() > 0){
                data = assets.getAssets().get(0).getData().toString();
            }
            else {
                data = "No Assets found";
            }

            text.setText(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}