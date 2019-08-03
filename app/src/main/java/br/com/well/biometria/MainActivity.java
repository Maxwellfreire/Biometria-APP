package br.com.well.biometria;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class MainActivity extends AppCompatActivity {

    private String KEY_NAME = "somekeyname";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        if(!fingerprintManager.isHardwareDetected()){

            Log.e("Hardware", "Hardware não detectado");
            return;


        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT)
                != PackageManager.PERMISSION_GRANTED){
            Log.e("Permission","permissão de impressão digital rejeitada");
            return;

        }
        if(!keyguardManager.isDeviceSecure()){

            Log.e("Keyguard", "proteção de tecla não ativada");
            return;


        }
        KeyStore keyStore;

        try{
            keyStore = KeyStore.getInstance("AndroidKeyStore");

        } catch(Exception e) {
            Log.e("KeyStore", e.getMessage());
            return;

        }
        KeyGenerator keyGenerator;

        try{
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch(Exception e) {
            Log.e("KeyGenerator", e.getMessage());
            return;

        }
        try{
            keyStore.load(null);
            keyGenerator.init(
                    new KeyGenParameterSpec.Builder(KEY_NAME,
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                            .setUserAuthenticationRequired(true)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                            .build());
            keyGenerator.generateKey();

        } catch(Exception e) {
            Log.e("Generating keys", e.getMessage());
            return;

        }
        Cipher cipher;
        try{
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES
                    + "/" + KeyProperties.BLOCK_MODE_CBC
                    + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            keyGenerator.generateKey();

        } catch(Exception e) {
            Log.e("Cipher", e.getMessage());
            return;

        }
        try{
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);

        } catch(Exception e) {
            Log.e("Secret key", e.getMessage());
            return;

        }

        FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);

        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0 , new AuthenticationHandler(this),null);



    }
}
