package com.samsung.chipsqrcode;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.util.List;

public class Informacoes extends Activity {

    private GridView gridViewTitulos, gridViewInformacoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacoes);

        this.gridViewTitulos = (GridView) findViewById(R.id.gridViewTitulo);
        this.gridViewInformacoes = (GridView) findViewById(R.id.gridViewInformacoes);

        String[] titulos = new String[]{"CHIP", "OPER.", "TAM.", "TECN.", "VC", "CB", "HIDE", "INTER.", "SIMAT"};
        ArrayAdapter<String> adapterInfo = new ArrayAdapter<String>(this, android.R.layout.simple_gallery_item, titulos);
        adapterInfo.setDropDownViewResource(android.R.layout.simple_list_item_1);
        gridViewTitulos.setBackgroundColor(Color.LTGRAY);
        gridViewTitulos.setAdapter(adapterInfo);

        BancoController crudInformacoes = new BancoController(this);
        List<String> listaInformacoes = crudInformacoes.carregaInformacoesChip();
        ArrayAdapter<String> adapterInfo2 = new ArrayAdapter<String>(this, android.R.layout.simple_gallery_item, listaInformacoes);
        adapterInfo2.setDropDownViewResource(android.R.layout.simple_list_item_1);
        gridViewInformacoes.setBackgroundColor(Color.LTGRAY);
        gridViewInformacoes.setAdapter(adapterInfo2);
    }

}
