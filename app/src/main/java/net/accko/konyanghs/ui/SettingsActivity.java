package net.accko.konyanghs.ui;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import net.accko.konyanghs.R;
import net.accko.konyanghs.util.PreferenceView;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    PreferenceView preferenceosp, preferenceosl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferenceosp = findViewById(R.id.preferenceosp);
        preferenceosp.setOnClickListener(this);
        preferenceosl = findViewById(R.id.preferenceosl);
        preferenceosl.setOnClickListener(this);

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.preferenceosp:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/accko199806/konyanghs-android")));
                break;

            case R.id.preferenceosl:
                View osl_view = this.getLayoutInflater().inflate(R.layout.dialog_license, null);
                TextView oslText = osl_view.findViewById(R.id.osl_text);
                oslText.setText(Html.fromHtml("Any questions about our use of licensed work can be sent to " + "<a href=\"mailto:me@accko.net\">me@accko.net</a>"));

                AlertDialog.Builder osl_dialog = new AlertDialog.Builder(this)
                        .setView(osl_view);
                osl_dialog.show();
                break;
        }
    }
}