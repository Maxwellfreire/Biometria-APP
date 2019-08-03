package br.com.well.biometria;

import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.widget.Toast;

public class AuthenticationHandler extends FingerprintManager.AuthenticationCallback {


    private MainActivity mainActivity;


    public AuthenticationHandler (MainActivity mainActivity) {
            this.mainActivity = mainActivity;


    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
        Toast.makeText(mainActivity, " Erro: " + errString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        super.onAuthenticationHelp(helpCode, helpString);
        Toast.makeText(mainActivity, "Auth Help: " + helpString, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        Toast.makeText(mainActivity, "Bem Vindo Maxwell :)" , Toast.LENGTH_SHORT).show();
        Intent i = new Intent(mainActivity.getApplicationContext(), segundaActivity.class);
        mainActivity.startActivity(i);



    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        Toast.makeText(mainActivity, "Você não e o Maxwell :( " , Toast.LENGTH_SHORT).show();
    }
}
