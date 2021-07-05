package com.samsung.chipsqrcode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.List;

public class CadastroChip extends Activity {

    private Button botaoAddChip, botaoAtualizarChip;
    private EditText etId;
    private GridView gridViewChip;
    private String idChip, Tamanho, Operadora, Tecnologia, VideoCall="NAO", CallBarring="NAO", HideId="NAO", ChamadaInternacional="NAO", SimAt="NAO", novoNome, novoOperadora, novoTamanho, novoTecnologia, novoVideoCall,
            novoCallBarring, novoHideId, novoChamadaInternacional, novoSimAt;
    private RadioGroup rgTamanho, rgOperadora, rgTecnologia;
    private RadioButton rbTim, rbClaro, rbVivo, rbOi, rbMicro, rbNano, rb3G, rb3G4G, rb4G;
    private CheckBox cbVideoCall, cbCallBarring, cbHideId, cbChamadaInternacional, cbSimAt;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addchip);

        this.gridViewChip = (GridView) findViewById(R.id.gridViewChip);
        this.botaoAddChip = (Button) findViewById(R.id.btAddChip);
        this.botaoAtualizarChip = (Button) findViewById(R.id.btAtualizarChip);
        this.etId = (EditText) findViewById(R.id.etId);
        this.rgOperadora = (RadioGroup) findViewById(R.id.rgOperadora);
        this.rbClaro = (RadioButton) findViewById(R.id.rbClaro);
        this.rbTim = (RadioButton) findViewById(R.id.rbTim);
        this.rbVivo = (RadioButton) findViewById(R.id.rbVivo);
        this.rbOi = (RadioButton) findViewById(R.id.rbOi);
        this.rgTamanho = (RadioGroup) findViewById(R.id.rgTamanho);
        this.rbMicro = (RadioButton) findViewById(R.id.rbMicro);
        this.rbNano = (RadioButton) findViewById(R.id.rbNano);
        this.rgTecnologia = (RadioGroup) findViewById(R.id.rgTecnologia);
        this.rb3G = (RadioButton) findViewById(R.id.rb3G);
        this.rb3G4G = (RadioButton) findViewById(R.id.rb3G4G);
        this.rb4G = (RadioButton) findViewById(R.id.rb4G);
        this.cbVideoCall = (CheckBox) findViewById(R.id.cbVideocall);
        this.cbCallBarring = (CheckBox) findViewById(R.id.cbCallbarring);
        this.cbHideId = (CheckBox) findViewById(R.id.cbHideId);
        this.cbChamadaInternacional = (CheckBox) findViewById(R.id.cbChamadainternacional);
        this.cbSimAt = (CheckBox) findViewById(R.id.cbSimat);

        //CONFIGURAÇÃO INICIAL
        etId.setText("");
        botaoAtualizarChip.setEnabled(false);

        //GRIDVIEW
        BancoController crud = new BancoController(this);
        List<String> lista = crud.carregaChip();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, lista);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        gridViewChip.setBackgroundColor(Color.BLACK);
        gridViewChip.setAdapter(dataAdapter);

        //CADASTRAR
        botaoAddChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BancoController crud = new BancoController(CadastroChip.this);
                String resultado;
                if(etId.getText().toString().equals("") || (rgOperadora.getCheckedRadioButtonId()==-1) || (rgTamanho.getCheckedRadioButtonId()==-1) || (rgTecnologia.getCheckedRadioButtonId()==-1)){
                    Toast.makeText(getApplicationContext(), "Todos os campos devem ser preenchidos!", Toast.LENGTH_SHORT).show();
                }else{
                    resultado = crud.cadastraChip(etId.getText().toString(), Operadora, Tamanho, Tecnologia, VideoCall, CallBarring, HideId, ChamadaInternacional, SimAt);
                    atualizarActivity();
                    Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //ALTERAR
        botaoAtualizarChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BancoController crud = new BancoController(CadastroChip.this);
                novoNome = etId.getText().toString();
                switch (rgOperadora.getCheckedRadioButtonId()){
                    case R.id.rbTim:
                        novoOperadora = "Tim";
                        break;
                    case R.id.rbClaro:
                        novoOperadora = "Claro";
                        break;
                    case R.id.rbVivo:
                        novoOperadora = "Vivo";
                        break;
                    case R.id.rbOi:
                        novoOperadora = "Oi";
                        break;
                }
                switch (rgTamanho.getCheckedRadioButtonId()){
                    case R.id.rbMicro:
                        novoTamanho = "Micro";
                        break;
                    case R.id.rbNano:
                        novoTamanho = "Nano";
                        break;
                }
                switch (rgTecnologia.getCheckedRadioButtonId()){
                    case R.id.rb3G:
                        novoTecnologia = "3G";
                        break;
                    case R.id.rb3G4G:
                        novoTecnologia = "3G/4G";
                        break;
                    case R.id.rb4G:
                        novoTecnologia = "4G";
                        break;
                }
                if (cbVideoCall.isChecked()){
                    novoVideoCall = "SIM";
                }else{
                    novoVideoCall = "NAO";
                }
                if (cbCallBarring.isChecked()){
                    novoCallBarring = "SIM";
                }else{
                    novoCallBarring = "NAO";
                }
                if (cbHideId.isChecked()){
                    novoHideId = "SIM";
                }else{
                    novoHideId = "NAO";
                }
                if (cbChamadaInternacional.isChecked()){
                    novoChamadaInternacional = "SIM";
                }else{
                    novoChamadaInternacional = "NAO";
                }
                if (cbSimAt.isChecked()){
                    novoSimAt = "SIM";
                }else{
                    novoSimAt = "NAO";
                }
                crud.alteraChip(idChip, novoNome, novoOperadora, novoTamanho, novoTecnologia, novoVideoCall, novoCallBarring, novoHideId, novoChamadaInternacional, novoSimAt);
                atualizarActivity();
                Toast.makeText(getApplicationContext(), "Chip " + idChip + " Atualizado", Toast.LENGTH_SHORT).show();
            }
        });

        //ESCOLHA ALTERAR OU DELETAR NO GRID
        gridViewChip.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                botaoAddChip.setEnabled(false);
                botaoAtualizarChip.setEnabled(true);
                final String chipSelecionado;
                chipSelecionado = parent.getItemAtPosition(position).toString();
                final BancoController crud = new BancoController(CadastroChip.this);

                AlertDialog.Builder adb = new AlertDialog.Builder(context);
                adb.setTitle("Escolha uma Opção");
                final String[] opcoes = new String[]{"Alterar","Deletar"};

                adb.setItems(opcoes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int opcao) {
                        if(opcao == 0){
                            final List<String> infoChip = crud.carregaDadoByChip(chipSelecionado);
                            idChip = infoChip.get(1).toString();
                            etId.setText(infoChip.get(1).toString());
                            switch (infoChip.get(2).toString()){
                                case "Tim":
                                    rbTim.setChecked(true);
                                    break;
                                case "Claro":
                                    rbClaro.setChecked(true);
                                    break;
                                case "Vivo":
                                    rbVivo.setChecked(true);
                                    break;
                                case "Oi":
                                    rbOi.setChecked(true);
                                    break;
                            }
                            switch (infoChip.get(3).toString()){
                                case "Micro":
                                    rbMicro.setChecked(true);
                                    break;
                                case "Nano":
                                    rbNano.setChecked(true);
                                    break;
                            }
                            switch (infoChip.get(4).toString()){
                                case "3G":
                                    rb3G.setChecked(true);
                                    break;
                                case "3G/4G":
                                    rb3G4G.setChecked(true);
                                    break;
                                case "4G":
                                    rb4G.setChecked(true);
                                    break;
                            }
                            switch (infoChip.get(5).toString()){
                                case "SIM":
                                    cbVideoCall.setChecked(true);
                                    break;
                                case "NAO":
                                    cbVideoCall.setChecked(false);
                                    break;
                            }
                            switch (infoChip.get(6).toString()){
                                case "SIM":
                                    cbCallBarring.setChecked(true);
                                    break;
                                case "NAO":
                                    cbCallBarring.setChecked(false);
                                    break;
                            }
                            switch (infoChip.get(7).toString()){
                                case "SIM":
                                    cbHideId.setChecked(true);
                                    break;
                                case "NAO":
                                    cbHideId.setChecked(false);
                                    break;
                            }
                            switch (infoChip.get(8).toString()){
                                case "SIM":
                                    cbChamadaInternacional.setChecked(true);
                                    break;
                                case "NAO":
                                    cbChamadaInternacional.setChecked(false);
                                    break;
                            }
                            switch (infoChip.get(9).toString()){
                                case "SIM":
                                    cbSimAt.setChecked(true);
                                    break;
                                case "NAO":
                                    cbSimAt.setChecked(false);
                                    break;
                            }
                        }else{
                            crud.deletaChip(chipSelecionado);
                            atualizarActivity();
                            Toast.makeText(getApplicationContext(), "Chip " + chipSelecionado + " Excluído", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                adb.show();
                return false;
            }
        });

        //RADIOGROUP TAMANHO
        rgTamanho.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rbMicro:
                        Tamanho = "Micro";
                        break;
                    case R.id.rbNano:
                        Tamanho = "Nano";
                        break;
                }
            }
        });

        //RADIOGROUP OPERADORA
        rgOperadora.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rbTim:
                        Operadora = "Tim";
                        break;
                    case R.id.rbClaro:
                        Operadora = "Claro";
                        break;
                    case R.id.rbVivo:
                        Operadora = "Vivo";
                        break;
                    case R.id.rbOi:
                        Operadora = "Oi";
                        break;
                }
            }
        });

        //RADIOGROUP TECNOLOGIA
        rgTecnologia.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb3G:
                        Tecnologia = "3G";
                        break;
                    case R.id.rb3G4G:
                        Tecnologia = "3G/4G";
                        break;
                    case R.id.rb4G:
                        Tecnologia = "4G";
                        break;
                }
            }
        });

        //CHECKBOX VIDEOCALL
        cbVideoCall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if (isChecked){
                    VideoCall = "SIM";
                }else{
                    VideoCall = "NAO";
                }
            }
        });

        //CHECKBOX CALLBARRING
        cbCallBarring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if (isChecked){
                    CallBarring = "SIM";
                }else{
                    CallBarring = "NAO";
                }
            }
        });

        //CHECKBOX HIDEID
        cbHideId.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if (isChecked){
                    HideId = "SIM";
                }else{
                    HideId = "NAO";
                }
            }
        });

        //CHECKBOX CHAMADAINTERNACIONAL
        cbChamadaInternacional.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if (isChecked){
                    ChamadaInternacional = "SIM";
                }else{
                    ChamadaInternacional = "NAO";
                }
            }
        });

        //CHECKBOX SIMAT
        cbSimAt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if (isChecked){
                    SimAt = "SIM";
                }else{
                    SimAt = "NAO";
                }
            }
        });

    }

    public void atualizarActivity() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

}