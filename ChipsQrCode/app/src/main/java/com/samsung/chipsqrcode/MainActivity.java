package com.samsung.chipsqrcode;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends Activity implements OnItemSelectedListener, ZXingScannerView.ResultHandler {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static int currentTab = 0;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    final Context context = this;
    private Spinner spFiltrar;
    private Button botaoExportar, botaoFiltrar, botaoListaNegra, botaoqrcodeemprestimo, botapqrcodedevolver;
    private TabHost abas;
    private GridView gridViewPrincipal, gridViewCadastro;
    private BancoController banco = new BancoController(this);
    private ZXingScannerView mScannerView;
    private int menu;
    private CriaBanco banco2;

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE); // Check if we have write permission

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE); // We don't have permission so prompt the user
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.gridViewPrincipal = (GridView) findViewById(R.id.gridViewPrincipal);
        this.gridViewCadastro = (GridView) findViewById(R.id.gridViewCadastro);
        this.botaoExportar = (Button) findViewById(R.id.btExportar);
        this.botaoFiltrar = (Button) findViewById(R.id.btFiltrar);
        this.botaoListaNegra = (Button) findViewById(R.id.btListaNegra);
        this.botaoqrcodeemprestimo = (Button) findViewById(R.id.btQrCodeEmprestimo);
        this.botapqrcodedevolver = (Button) findViewById(R.id.btQrCodeDevolucao);
        this.spFiltrar = (Spinner) findViewById(R.id.sp_Filtrar);

        //ADICIONANDO ABAS
        this.abas = (TabHost) findViewById(R.id.tabhost);
        abas.setup();

        TabHost.TabSpec tab1 = abas.newTabSpec("abaChip");
        tab1.setIndicator("SIMCARD");
        tab1.setContent(R.id.layout1);
        abas.addTab(tab1);

        TabHost.TabSpec tab5 = abas.newTabSpec("abaAvancado");
        tab5.setIndicator("AVANÇADO");
        tab5.setContent(R.id.layout5);
        abas.addTab(tab5);

        //MANTER ABA USADA
        abas.setCurrentTab(getCurrentTab());

        //GRIDVIEW PRINCIPAL
        BancoController crud = new BancoController(this);
        List<String> li = crud.carregaDados();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_gallery_item, li);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        gridViewPrincipal.setBackgroundColor(Color.BLACK);
        gridViewPrincipal.setAdapter(dataAdapter);

        //GRIDVIEW CADASTRO
        String[] listaCadastro = new String[]{"ADD TESTER", "ADD CHIP", "INFORMAÇÕES", "VERIFICAR CHIP", "EXPORTAR BD"};
        ArrayAdapter<String> dataAdapterCadastro = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaCadastro);
        dataAdapterCadastro.setDropDownViewResource(android.R.layout.simple_list_item_1);
        gridViewCadastro.setBackgroundColor(Color.LTGRAY);
        gridViewCadastro.setAdapter(dataAdapterCadastro);

        //CADASTRAR EMPRÉSTIMO
        botaoqrcodeemprestimo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leitorQrCode();
                menu = 1;
            }
        });

        //DELETAR EMPRÉSTIMO
        botapqrcodedevolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leitorQrCode();
                menu = 2;
            }
        });

        //FILTRAR EMPRÉSTIMO
        botaoFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BancoController crud2 = new BancoController(getBaseContext());
                String filtro = spFiltrar.getSelectedItem().toString();

                List<String> li = new ArrayList<String>();
                li = crud2.carregaFiltro(filtro);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_gallery_item, li);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                gridViewPrincipal.setBackgroundColor(Color.BLACK);
                gridViewPrincipal.setAdapter(dataAdapter);
            }
        });

/*
        //ENVIAR LISTA NEGRA
        botaoListaNegra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BancoController crud = new BancoController(MainActivity.this);
                List<String> li = crud.carregaDados();

                Log.i("Lista Negra", li.toString());
                //EnviarEmail("Lista Negra", li.toString());
            }
        });
*/
        //ESCOLHA OPÇÃO DE CADASTRO
        gridViewCadastro.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                switch(position){
                    case 0:
                        Intent it0 = new Intent(MainActivity.this, CadastroUsuario.class);
                        startActivity(it0);
                        break;
                    case 1:
                        Intent it1 = new Intent(MainActivity.this, CadastroChip.class);
                        startActivity(it1);
                        break;
                    case 2:
                        Intent it2 = new Intent(MainActivity.this, Informacoes.class);
                        startActivity(it2);
                        break;
                    case 3:
                        leitorQrCode();
                        menu = 3;
                        break;
                    case 4:
                        exportBDEmprestimo();
                        exportBDUsuarios();
                        exportBDChips();
                        Toast.makeText(getApplicationContext(), "Banco de Dados Salvo!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        loadSpinnerFiltro();
    }

    //POPULAR SPINNER FILTRO
    private void loadSpinnerFiltro() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.filtro, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        this.spFiltrar.setAdapter(adapter);
        this.spFiltrar.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        switch (parent.getId()) {
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void EnviarEmail(String assuntoEmail, String corpoEmail) {
        List<String> destinatarios = new ArrayList<String>();
        //destinatarios.add("claudio.ag@samsung.com");
        //destinatarios.add("r.sampaio@samsung.com");
        //destinatarios.add("juliana.d@samsung.com");
        //destinatarios.add("r.branchini@samsung.com");
        new SendMailTask(MainActivity.this).execute("sidiaclaudio4@gmail.com", "sidia12345", destinatarios, assuntoEmail, corpoEmail);
    }

    public int getCurrentTab() {
        return currentTab;
    }

    public void setCurrentTab(int currentTab) {
        this.currentTab = currentTab;
    }

    //*************************************************************************************************************************************
    public static String DB_FILEPATH = "/data/data/com.samsung.chipsqrcode/databases/database.db";

    /**
     * Copies the database file at the specified location over the current
     * internal application database.
     * */
/*    public boolean importDatabase(String dbPath) throws IOException {
        //CriaBanco banco;
        // Close the SQLiteOpenHelper so it will commit the created empty
        // database to internal storage.
        banco2.close();
        File newDb = new File(dbPath);
        File oldDb = new File(DB_FILEPATH);
        if (newDb.exists()) {
            FileUtils.copyFile(new FileInputStream(newDb), new FileOutputStream(oldDb));
            // Access the copied database so SQLiteHelper will cache it and mark
            // it as created.
            banco2.getWritableDatabase().close();
            return true;
        }
        return false;
    }

   /* public class FileUtils {
        /**
         * Creates the specified <code>toFile</code> as a byte for byte copy of the
         * <code>fromFile</code>. If <code>toFile</code> already exists, then it
         * will be replaced with a copy of <code>fromFile</code>. The name and path
         * of <code>toFile</code> will be that of <code>toFile</code>.<br/>
         * <br/>
         * <i> Note: <code>fromFile</code> and <code>toFile</code> will be closed by
         * this function.</i>
         *
         * @param fromFile
         *            - FileInputStream for the file to copy from.
         * @param toFile
         *            - FileInputStream for the file to copy to.
         */
   /*     public static void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws IOException {
            FileChannel fromChannel = null;
            FileChannel toChannel = null;
            try {
                fromChannel = fromFile.getChannel();
                toChannel = toFile.getChannel();
                fromChannel.transferTo(0, fromChannel.size(), toChannel);
            } finally {
                try {
                    if (fromChannel != null) {
                        fromChannel.close();
                    }
                } finally {
                    if (toChannel != null) {
                        toChannel.close();
                    }
                }
            }
        }
    }*/

    //*************************************************************************************************************************************

    private void exportBDEmprestimo() {
        //File dbFile = getDatabasePath("banco.db");
        CriaBanco dbhelper = new CriaBanco(getApplicationContext());
        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_HHmmss");
        String DateTime = sdf.format(new Date());
        File file = new File(exportDir, "Emprestimos_" + DateTime + ".csv");
        try {
            verifyStoragePermissions(MainActivity.this);
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = dbhelper.getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT * FROM emprestimo", null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                //Which column you want to export
                String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2), curCSV.getString(3), curCSV.getString(4)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }

    private void exportBDUsuarios() {
        //File dbFile = getDatabasePath("banco.db");
        CriaBanco dbhelper = new CriaBanco(getApplicationContext());
        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_HHmmss");
        String DateTime = sdf.format(new Date());
        File file = new File(exportDir, "Usuarios_" + DateTime + ".csv");
        try {
            verifyStoragePermissions(MainActivity.this);
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = dbhelper.getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT * FROM login", null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                //Which column you want to export
                String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }

    private void exportBDChips() {
        //File dbFile = getDatabasePath("banco.db");
        CriaBanco dbhelper = new CriaBanco(getApplicationContext());
        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_HHmmss");
        String DateTime = sdf.format(new Date());
        File file = new File(exportDir, "Chips_" + DateTime + ".csv");
        try {
            verifyStoragePermissions(MainActivity.this);
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = dbhelper.getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT * FROM chipqrcode", null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                //Which column you want to export
                String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2), curCSV.getString(3), curCSV.getString(4), curCSV.getString(5), curCSV.getString(6), curCSV.getString(7), curCSV.getString(8), curCSV.getString(9)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }

    @Override
    public void handleResult(Result result) {
        mScannerView.stopCamera();
        final String resp = result.getText();

        switch (menu) {
            case 1:
                //VERIFICA SE O CHIP ESTÁ SENDO UTILIZADO
                String responsavelEmprestimo = banco.disponibilidadeEmprestimo(resp);
                if (responsavelEmprestimo == null) {
                    //VERIFICA OPERADORA DO CHIP
                    final String buscaOperadora = banco.buscarOperadora(resp);
                    if (buscaOperadora != null) {

                        //get prompts.xml view
                        LayoutInflater li = LayoutInflater.from(context);
                        View promptsView = li.inflate(R.layout.prompts, null);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                        // set prompts.xml to alertdialog builder
                        alertDialogBuilder.setView(promptsView);

                        final TextView tvSenha = (TextView) promptsView.findViewById(R.id.tvSenha);
                        final EditText pwd = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
                        tvSenha.setText("Chip " + buscaOperadora + " " + resp + "\nInforme sua senha ");

                        // set dialog message
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                String password = pwd.getText().toString();
                                                String username = banco.buscarSenha(password);
                                                //if (password.equals(passwordBanco)) {
                                                if (username != null) {
                                                    BancoController crud = new BancoController(getBaseContext());
                                                    //String resultado = crud.insereDado(testerString, buscaOperadora, resp);
                                                    crud.insereDado(username, buscaOperadora, resp);
                                                    atualizarActivity(0);
                                                    //Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_SHORT).show();
                                                    Toast.makeText(getApplicationContext(), buscaOperadora + " - " + resp + " => " + username, Toast.LENGTH_SHORT).show();
                                                    //EnviarEmail("App Chip - Empréstimo", testerString + " => " + resp);
                                                    EnviarEmail("App Chip - Empréstimo", buscaOperadora + " - " + resp + " => " + username);
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Senha Inexistente!", Toast.LENGTH_SHORT).show();
                                                    atualizarActivity(0);
                                                }
                                            }
                                        })
                                .setNegativeButton("Cancelar",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                atualizarActivity(0);
                                            }
                                        });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();

                    } else {
                        atualizarActivity(0);
                        Toast.makeText(getApplicationContext(), "Chip Inválido!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    atualizarActivity(0);
                    Toast.makeText(getApplicationContext(), "Chip já emprestado por " + responsavelEmprestimo, Toast.LENGTH_SHORT).show();
                }

                break;

            case 2:
                //VERIFICA OPERADORA DO CHIP
                final String buscaOperadora2 = banco.buscarOperadora(resp);
                if (buscaOperadora2 != null) {
                    //VERIFICA SE O CHIP ESTÁ SENDO UTILIZADO
                    String responsavelEmprestimo2 = banco.disponibilidadeEmprestimo(resp);
                    if (responsavelEmprestimo2 != null) {
                        BancoController crud2 = new BancoController(getBaseContext());
                        crud2.deletaRegistro(responsavelEmprestimo2, resp);
                        atualizarActivity(0);
                        Toast.makeText(getApplicationContext(), responsavelEmprestimo2 + " => " + buscaOperadora2 + " - " + resp, Toast.LENGTH_SHORT).show();
                        EnviarEmail("App Chip - Devolução", responsavelEmprestimo2 + " => " + buscaOperadora2 + " - " + resp);
                    } else {
                        atualizarActivity(0);
                        Toast.makeText(getApplicationContext(), "Chip está disponível!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    atualizarActivity(0);
                    Toast.makeText(getApplicationContext(), "Chip Inválido!", Toast.LENGTH_SHORT).show();
                }

                break;

            case 3:
                List<String> consulta = banco.carregaDadoByChip(resp);

                //get prompts.xml view
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                String a = consulta.get(0).toString();
                String b = consulta.get(1).toString();
                String c = consulta.get(2).toString();
                String d = consulta.get(3).toString();
                final TextView tvSenha = (TextView) promptsView.findViewById(R.id.tvSenha);
                final EditText pwd = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
                tvSenha.setText("->" + a + "\n->" + b + "\n->" + c + "\n->" + d);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        atualizarActivity(1);
                                     /*   String password = pwd.getText().toString();
                                        String username = banco.buscarSenha(password);
                                        //if (password.equals(passwordBanco)) {
                                        if (username != null) {
                                            BancoController crud = new BancoController(getBaseContext());
                                            //String resultado = crud.insereDado(testerString, buscaOperadora, resp);
                                            //crud.insereDado(username, buscaOperadora, resp);
                                            atualizarActivity(0);
                                            //Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_SHORT).show();
                                            //Toast.makeText(getApplicationContext(), buscaOperadora + " - " + resp + " => " + username, Toast.LENGTH_SHORT).show();
                                            //EnviarEmail("App Chip - Empréstimo", testerString + " => " + resp);
                                            //EnviarEmail("App Chip - Empréstimo", buscaOperadora + " - " + resp + " => " + username);
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Senha Inexistente!", Toast.LENGTH_SHORT).show();
                                            atualizarActivity(0);
                                        }*/
                                    }
                                });
                       /* .setNegativeButton("Cancelar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        atualizarActivity(0);
                                    }
                                });*/

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent principal = new Intent(this, MainActivity.class);
        startActivity(principal);
        if (mScannerView != null) {
            mScannerView.stopCamera();
        }
    }

    public void atualizarActivity(int aba) {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
        setCurrentTab(aba);
    }

    public void leitorQrCode() {
        mScannerView = new ZXingScannerView(MainActivity.this);
        setContentView(mScannerView);
        mScannerView.setResultHandler(MainActivity.this);
        mScannerView.startCamera(0);
    }

}