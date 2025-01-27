package com.samsung.chipsqrcode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CadastroUsuario extends Activity{

    private GridView gridViewTester;
    private Button botaoAddTester, botaoAtualizarTester;
    private EditText etNome, etSenha;
    private String nomeTester;
    final Context context = this;
    private BancoController banco = new BancoController(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addusuario);

        this.gridViewTester = (GridView) findViewById(R.id.gridViewTester);
        this.botaoAddTester = (Button) findViewById(R.id.btAddTester);
        this.botaoAtualizarTester = (Button) findViewById(R.id.btAtualizarTester);
        this.etNome = (EditText) findViewById(R.id.etNome);
        this.etSenha = (EditText) findViewById(R.id.etSenha);

        //CONFIGURAÇÃO INICIAL
        etNome.setText("");
        etSenha.setText("");
        botaoAtualizarTester.setEnabled(false);

        //GRIDVIEW
        BancoController crud = new BancoController(this);
        List<String> lista = crud.carregaTester();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, lista);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        gridViewTester.setBackgroundColor(Color.BLACK);
        gridViewTester.setAdapter(dataAdapter);

        //CADASTRAR
        botaoAddTester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BancoController crud = new BancoController(CadastroUsuario.this);
                String resultado;
                if(etNome.getText().toString().equals("") || etSenha.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Insira Nome e Senha", Toast.LENGTH_SHORT).show();
                }else{
                    String pwd = banco.buscarSenha(etSenha.getText().toString());
                    if (pwd != null) {
                        Toast.makeText(getApplicationContext(), "Senha Já Existe!", Toast.LENGTH_SHORT).show();
                    }else{
                        resultado = crud.cadastraTester(etNome.getText().toString(), etSenha.getText().toString());
                        atualizarActivity();
                        Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //ALTERAR
        botaoAtualizarTester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BancoController crud = new BancoController(CadastroUsuario.this);
                String novoNome = etNome.getText().toString();
                String novaSenha = etSenha.getText().toString();
                String pwd = banco.buscarSenha(novaSenha);
                if (pwd != null) {
                    Toast.makeText(getApplicationContext(), "Senha Já Existe!", Toast.LENGTH_SHORT).show();
                }else{
                    crud.alteraUsuario(nomeTester, novoNome, novaSenha);
                    atualizarActivity();
                    Toast.makeText(getApplicationContext(), "Usuário " + nomeTester + " Atualizado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //ESCOLHA ALTERAR OU DELETAR NO GRID
        gridViewTester.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                botaoAddTester.setEnabled(false);
                botaoAtualizarTester.setEnabled(true);
                final String nomeSelecionado;
                nomeSelecionado = parent.getItemAtPosition(position).toString();
                final BancoController crud = new BancoController(CadastroUsuario.this);

                AlertDialog.Builder adb = new AlertDialog.Builder(context);
                adb.setTitle("Escolha uma Opção");
                final String[] opcoes = new String[]{"Alterar","Deletar"};

                adb.setItems(opcoes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int opcao) {
                        if(opcao == 0){
                            final List<String> login = crud.carregaDadoByUsuario(nomeSelecionado);
                            nomeTester = login.get(1).toString();
                            LayoutInflater li = LayoutInflater.from(context);
                            View promptsView = li.inflate(R.layout.prompts, null);
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                            alertDialogBuilder.setView(promptsView);
                            final TextView tvSenha = (TextView) promptsView.findViewById(R.id.tvSenha);
                            final EditText pwd = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
                            tvSenha.setText(nomeTester + ", Informe sua senha ");
                            alertDialogBuilder
                                    .setCancelable(false)
                                    .setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    String password = pwd.getText().toString();
                                                    String username = banco.buscarSenha(password);
                                                    if (nomeTester.equals(username)){
                                                        etNome.setText(login.get(1).toString());
                                                        etSenha.setText(login.get(2).toString());
                                                    } else{
                                                        Toast.makeText(getApplicationContext(), "Senha Incorreta!", Toast.LENGTH_SHORT).show();
                                                        atualizarActivity();
                                                    }
                                                }
                                            })
                                    .setNegativeButton("Cancelar",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                    atualizarActivity();
                                                }
                                            });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }else{
                            crud.deletaUsuario(nomeSelecionado);
                            atualizarActivity();
                            Toast.makeText(getApplicationContext(), "Usuário " + nomeSelecionado + " Excluído", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                adb.show();
                return false;
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
