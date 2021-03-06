package com.sig.etu.sig.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.sig.etu.sig.R;
import com.sig.etu.sig.bdd.BDDManager;
import com.sig.etu.sig.modeles.Batiment;
import com.sig.etu.sig.modeles.Metier;
import com.sig.etu.sig.util.StringFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by o2121076 on 08/01/18.
 */

public class FormulairePersonneActivity extends AppCompatActivity {

    private BDDManager datasource;
    private String choixType;
    private String nomBatiment;
    private double latitude;
    private double longitude;

    private Spinner edit_Metier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_formulaire_personne);
        final Context ctx = getApplicationContext();

        Intent intent = getIntent();
        nomBatiment = intent.getStringExtra("batiment");
        latitude = intent.getDoubleExtra("latitude",0);
        longitude = intent.getDoubleExtra("longitude",0);

        datasource = new BDDManager(this);
        datasource.open();

        //Spinner types metiers
        ArrayList<String> types = new ArrayList<String>();
        List<Metier> t = datasource.getAllMetiers();
        for(Metier tb : t)
            types.add(tb.getNom());

        edit_Metier = (Spinner) findViewById(R.id.edit_Metier);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item,
                        types);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        edit_Metier.setAdapter(spinnerArrayAdapter);
        edit_Metier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object o = edit_Metier.getItemAtPosition(position);
                choixType = (String)o;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        datasource.close();

        Button valider = (Button) findViewById(R.id.button_valider);

        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(verifierEditTextNonVide())
                {
                    Intent returnIntent = new Intent();

                    EditText edit_Nom = (EditText) findViewById(R.id.edit_Nom);
                    EditText edit_Adresse = (EditText) findViewById(R.id.edit_Adresse);
                    //On a deja le spinner

                    if(saveInBDD(edit_Nom.getText().toString(),edit_Adresse.getText().toString())) {
                        returnIntent.putExtra("Nom", edit_Nom.getText().toString());
                        returnIntent.putExtra("Adresse", edit_Adresse.getText().toString());
                        returnIntent.putExtra("Metier", choixType);

                        setResult(FormulairePersonneActivity.RESULT_OK, returnIntent);
                    }else{
                        setResult(FormulairePersonneActivity.RESULT_CANCELED, returnIntent);
                    }
                    finish();
                }
            }
        });

        Button annuler = (Button) findViewById(R.id.button_annuler);

        annuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent returnIntent = new Intent();

                setResult(FormulairePersonneActivity.RESULT_CANCELED,returnIntent);
                finish();

            }
        });
    }

    private boolean saveInBDD(String nom, String adresse)
    {
        datasource.open();

        Metier metier = datasource.getMetierByName(choixType);
        Batiment batiment = datasource.getBatimentByName(nomBatiment);

        datasource.createPersonne(nom, StringFormat.correction(adresse), batiment.getId(), metier.getId(), latitude, longitude);

        datasource.close();


        return true;
    }

    private boolean verifierEditTextNonVide()
    {

        String messageErreur = "Ce champ doit être rempli.";
        EditText edit_Nom = (EditText) findViewById(R.id.edit_Nom);
        EditText edit_Adresse = (EditText) findViewById(R.id.edit_Adresse);

        String str = edit_Nom.getText().toString();
        if(TextUtils.isEmpty(str)) {
            edit_Nom.setError(messageErreur);
            return false;
        }

        str = edit_Adresse.getText().toString();
        if(TextUtils.isEmpty(str)) {
            edit_Adresse.setError(messageErreur);
            return false;
        }

        return true;
    }
}
