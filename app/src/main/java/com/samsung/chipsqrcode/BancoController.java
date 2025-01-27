package com.samsung.chipsqrcode;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BancoController {

    private SQLiteDatabase db;
    private CriaBanco banco;

    public BancoController(Context context) {
        banco = new CriaBanco(context);
    }

    //INSERE DADOS NA TABELA EMPRÉSTIMO
    public String insereDado(String tester, String operadora, String chip) {
        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();
        valores = new ContentValues();
        valores.put(CriaBanco.TESTER, tester);
        valores.put(CriaBanco.OPERADORA, operadora);
        valores.put(CriaBanco.CHIP, chip);
        valores.put(CriaBanco.DATAHORA, getDateTime());

        resultado = db.insert(CriaBanco.TABELA, null, valores);
        db.close();

        if (resultado == -1)
            return "Erro ao emprestar chip";
        else
            return "Empréstimo realizado com sucesso";
    }

    //CARREGA INFORMAÇÕES NO GRIDVIEW
    public List<String> carregaDados() {
        String query = "SELECT * FROM emprestimo";
        List<String> emprestimo = new ArrayList<String>();
        SQLiteDatabase database = banco.getReadableDatabase();
        Cursor c = database.rawQuery(query, null);
        if (c != null) {
            while (c.moveToNext()) {
                String code = c.getString(c.getColumnIndex("_id"));
                String name = c.getString(c.getColumnIndex("tester"));
                String email = c.getString(c.getColumnIndex("operadora"));
                String address = c.getString(c.getColumnIndex("chip"));
                String datahora = c.getString(c.getColumnIndex("datahora"));

                emprestimo.add(code);
                emprestimo.add(name);
                emprestimo.add(email);
                emprestimo.add(address);
                emprestimo.add(datahora);
            }
        }
        return emprestimo;
    }

    //CONSULTA CHIPS POR TESTER
    public List<String> carregaDadoByTester(String tester) {
        Cursor cursor;
        List<String> lista = new ArrayList<String>();
        String[] args = new String[]{tester};
        String[] campos = {banco.CHIP};
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELA, campos, "tester=?", args, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                lista.add(cursor.getString(0));
                Log.i("carregaDadoByTester", lista.toString());
            } while (cursor.moveToNext());
        } else {
            lista.add("Nenhum");
        }

        db.close();
        Log.i("return", lista.toString());
        return lista;
    }

    //CONSULTA TESTER EXCETO UM ESPECIFICO
    public List<String> carregaDadoByTesterTroca(String tester) {
        Cursor cursor;
        List<String> lista = new ArrayList<String>();
        String[] args = new String[]{tester};
        db = banco.getReadableDatabase();
        cursor = db.rawQuery("SELECT nometester FROM testers WHERE nometester NOT IN (?)", args);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                lista.add(cursor.getString(0));
                Log.i("DadoByTesterTroca", lista.toString());
            } while (cursor.moveToNext());
        }

        db.close();
        Log.i("DadoByTesterTrocaReslt", lista.toString());
        return lista;
    }

    //CONSULTA CHIPS POR OPERADORA
    public List<String> carregaDadoByOperadora(String operadora) {
        Cursor cursor = null;
        List<String> lista = new ArrayList<String>();
        ;
        String[] args = new String[]{operadora};
        db = banco.getReadableDatabase();
        switch (operadora) {
            case "Tim":
                cursor = db.rawQuery("SELECT chipt FROM chiptim WHERE chipt NOT IN(SELECT chip FROM emprestimo)", null);
                break;
            case "Claro":
                cursor = db.rawQuery("SELECT chipc FROM chipclaro WHERE chipc NOT IN(SELECT chip FROM emprestimo)", null);
                break;
            case "Vivo":
                cursor = db.rawQuery("SELECT chipv FROM chipvivo WHERE chipv NOT IN(SELECT chip FROM emprestimo)", null);
                break;
            case "Oi":
                cursor = db.rawQuery("SELECT chipo FROM chipoi WHERE chipo NOT IN(SELECT chip FROM emprestimo)", null);
                break;
        }

        if (cursor != null && cursor.moveToFirst()) {
            do {
                lista.add(cursor.getString(0));
                Log.i("carregaDadoByOperadora", lista.toString());
            } while (cursor.moveToNext());
        } else {
            lista.add("Indisponivel");
        }

        db.close();
        Log.i("return", lista.toString());
        return lista;
    }

    public List<String> carregaFiltro(String filtro) {
        List<String> emprestimo = new ArrayList<String>();
        Cursor c = null;
        db = banco.getReadableDatabase();
        switch (filtro) {
            case "Tester":
                c = db.rawQuery("SELECT * FROM emprestimo ORDER BY tester", null);
                break;
            case "Operadora":
                c = db.rawQuery("SELECT * FROM emprestimo ORDER BY operadora", null);
                break;
        }

        if (c != null) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndex("_id"));
                String tester = c.getString(c.getColumnIndex("tester"));
                String operadora = c.getString(c.getColumnIndex("operadora"));
                String chip = c.getString(c.getColumnIndex("chip"));
                String datahora = c.getString(c.getColumnIndex("datahora"));

                emprestimo.add(id);
                emprestimo.add(tester);
                emprestimo.add(operadora);
                emprestimo.add(chip);
                emprestimo.add(datahora);
            }
        }
        return emprestimo;
    }

    //ALTERA DADOS DA TABELA EMPRÉSTIMO
    public void alteraRegistro(String testerOrigem, String chip, String testerDestino) {
        ContentValues valores;
        String[] args = new String[]{testerOrigem, chip};
        db = banco.getWritableDatabase();

        valores = new ContentValues();
        valores.put(CriaBanco.TESTER, testerDestino);
        valores.put(CriaBanco.DATAHORA, getDateTime());

        db.update(CriaBanco.TABELA, valores, "tester=? AND chip=?", args);
        db.close();
    }

    //DELETA DADOS DA TABELA EMPRÉSTIMO
    public void deletaRegistro(String testerDevolucao, String chipDevolucao) {
        String[] args = new String[]{testerDevolucao, chipDevolucao};
        db = banco.getReadableDatabase();
        db.delete(CriaBanco.TABELA, "tester=? AND chip=?", args);
        db.close();
    }

    //DELETA USUÁRIO DA TABELA LOGIN
    public void deletaUsuario(String nome) {
        String[] args = new String[]{nome};
        db = banco.getReadableDatabase();
        db.delete(CriaBanco.TABELA_LOGIN, "username=?", args);
        db.close();
    }

    //DELETA CHIP DA TABELA CHIPQRCODE
    public void deletaChip(String nome) {
        String[] args = new String[]{nome};
        db = banco.getReadableDatabase();
        db.delete(CriaBanco.TABELA_CHIPQRCODE, "chipqrc=?", args);
        db.close();
    }

    //RETORNA DATA E HORA
    private String getDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
        String DateTime = sdf.format(new Date());
        return DateTime;
    }

    //CONSULTA USERNAME POR SENHA
    public String buscarSenha(String password) {
        Cursor cursor;
        String[] args = new String[]{password};
        String[] campos = {banco.USERNAME};
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELA_LOGIN, campos, "password=?", args, null, null, null);
        String username = null;
        if (cursor.moveToFirst()) {
            do {
                username = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        db.close();
        return username;
    }

    //CONSULTA OPERADORA POR CHIP
    public String buscarOperadora(String chip) {
        Cursor c = null;
        String[] args = new String[]{chip};
        db = banco.getReadableDatabase();
        c = db.rawQuery("SELECT operadoraqrc FROM chipqrcode WHERE chipqrc = ?", args);
        String operadora = null;
        if (c.moveToFirst()) {
            do {
                operadora = c.getString(0);
            } while (c.moveToNext());
        }
        db.close();
        return operadora;
    }

    //CONSULTA DISPONIBILIDADE PARA EMPRÉSTIMO
    public String disponibilidadeEmprestimo(String chip) {
        Cursor c = null;
        String[] args = new String[]{chip};
        db = banco.getReadableDatabase();
        c = db.rawQuery("SELECT tester FROM emprestimo WHERE chip = ?", args);
        String a = null;
        if (c.moveToFirst()) {
            do {
                a = c.getString(0);
            } while (c.moveToNext());
        }
        db.close();
        return a;
    }

    //CARREGA TESTERS NO GRIDVIEW
    public List<String> carregaTester() {
        String query = "SELECT username FROM login ORDER BY username ASC";
        List<String> tester = new ArrayList<String>();
        SQLiteDatabase database = banco.getReadableDatabase();
        Cursor c = database.rawQuery(query, null);
        if (c != null) {
            while (c.moveToNext()) {
                String name = c.getString(c.getColumnIndex("username"));
                tester.add(name);
            }
        }
        return tester;
    }

    //CADASTRA NOVO USUÁRIO
    public String cadastraTester(String tester, String password) {
        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();
        valores = new ContentValues();
        valores.put(CriaBanco.USERNAME, tester);
        valores.put(CriaBanco.PASSWORD, password);

        resultado = db.insert(CriaBanco.TABELA_LOGIN, null, valores);
        db.close();

        if (resultado == -1)
            return "Erro ao cadastrar Usuário";
        else
            return "Usuário cadastrado com sucesso";
    }

    //CARREGA DADOS DO USUÁRIO
    public List<String> carregaDadoByUsuario(String nome) {
        Cursor c;
        List<String> login = new ArrayList<String>();
        String[] args = {nome};
        db = banco.getReadableDatabase();
        c = db.rawQuery("SELECT * FROM login WHERE username = ?", args);

        if (c != null) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndex("_id"));
                String username = c.getString(c.getColumnIndex("username"));
                String password = c.getString(c.getColumnIndex("password"));

                login.add(id);
                login.add(username);
                login.add(password);
            }
        }

        db.close();
        return login;
    }

    //ALTERA USUÁRIO DA TABELA LOGIN
    public void alteraUsuario(String nome, String novoNome, String novaSenha) {
        ContentValues valores;
        String[] args = new String[]{nome};
        db = banco.getWritableDatabase();

        valores = new ContentValues();
        valores.put(CriaBanco.USERNAME, novoNome);
        valores.put(CriaBanco.PASSWORD, novaSenha);

        db.update(CriaBanco.TABELA_LOGIN, valores, "username=?", args);
        db.close();
    }

    //CARREGA CHIPS NO GRIDVIEW
    public List<String> carregaChip() {
        String query = "SELECT * FROM chipqrcode ORDER BY chipqrc ASC";
        List<String> chip = new ArrayList<String>();
        SQLiteDatabase database = banco.getReadableDatabase();
        Cursor c = database.rawQuery(query, null);
        if (c != null) {
            while (c.moveToNext()) {
                String nome = c.getString(c.getColumnIndex("chipqrc"));
                chip.add(nome);
            }
        }
        return chip;
    }

    //CADASTRA NOVO CHIP
    public String cadastraChip(String id, String operadora, String tamanho, String tecnologia, String videocall, String callbarring, String hideid, String chamadainternacional, String simat) {
        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();
        valores = new ContentValues();
        valores.put(CriaBanco.CHIPQRCODE, id);
        valores.put(CriaBanco.OPERADORAQRCODE, operadora);
        valores.put(CriaBanco.TAMANHO, tamanho);
        valores.put(CriaBanco.TECNOLOGIA, tecnologia);
        valores.put(CriaBanco.VIDEOCALL, videocall);
        valores.put(CriaBanco.CALLBARRING, callbarring);
        valores.put(CriaBanco.HIDEID, hideid);
        valores.put(CriaBanco.CHAMADAINTERNACIONAL, chamadainternacional);
        valores.put(CriaBanco.SIMAT, simat);

        resultado = db.insert(CriaBanco.TABELA_CHIPQRCODE, null, valores);
        db.close();

        if (resultado == -1)
            return "Erro ao cadastrar Chip";
        else
            return "Chip cadastrado com sucesso";
    }

    //ALTERA CHIP DA TABELA CHIPQRCODE
    public void alteraChip(String nome, String novoNome, String novaOperadora, String novoTamanho, String novaTecnologia, String novoVideocall, String novoCallbarring, String novoHideid, String novoChamadainternacional,
                           String novoSimat) {
        ContentValues valores;
        String[] args = new String[]{nome};
        db = banco.getWritableDatabase();

        valores = new ContentValues();
        valores.put(CriaBanco.CHIPQRCODE, novoNome);
        valores.put(CriaBanco.OPERADORAQRCODE, novaOperadora);
        valores.put(CriaBanco.TAMANHO, novoTamanho);
        valores.put(CriaBanco.TECNOLOGIA, novaTecnologia);
        valores.put(CriaBanco.VIDEOCALL, novoVideocall);
        valores.put(CriaBanco.CALLBARRING, novoCallbarring);
        valores.put(CriaBanco.HIDEID, novoHideid);
        valores.put(CriaBanco.CHAMADAINTERNACIONAL, novoChamadainternacional);
        valores.put(CriaBanco.SIMAT, novoSimat);

        db.update(CriaBanco.TABELA_CHIPQRCODE, valores, "chipqrc=?", args);
        db.close();
    }

    //CARREGA DADOS DO CHIP
    public List<String> carregaDadoByChip(String nome) {
        Cursor c;
        List<String> infoChip = new ArrayList<String>();
        String[] args = {nome};
        db = banco.getReadableDatabase();
        c = db.rawQuery("SELECT * FROM chipqrcode WHERE chipqrc = ?", args);

        if (c != null) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndex("_id"));
                String chip = c.getString(c.getColumnIndex("chipqrc"));
                String operadora = c.getString(c.getColumnIndex("operadoraqrc"));
                String tamanho = c.getString(c.getColumnIndex("tamanho"));
                String tecnologia = c.getString(c.getColumnIndex("tecnologia"));
                String videocall = c.getString(c.getColumnIndex("videocall"));
                String callbarring = c.getString(c.getColumnIndex("callbarring"));
                String hideid = c.getString(c.getColumnIndex("hideid"));
                String chamadainternacional = c.getString(c.getColumnIndex("chamadainternacional"));
                String simat = c.getString(c.getColumnIndex("simat"));

                infoChip.add(id);
                infoChip.add(chip);
                infoChip.add(operadora);
                infoChip.add(tamanho);
                infoChip.add(tecnologia);
                infoChip.add(videocall);
                infoChip.add(callbarring);
                infoChip.add(hideid);
                infoChip.add(chamadainternacional);
                infoChip.add(simat);
            }
        }

        db.close();
        return infoChip;
    }

    //CARREGA INFORMAÇÕES NO GRIDVIEW
    public List<String> carregaInformacoesChip() {
        String query = "SELECT * FROM chipqrcode";
        List<String> informacoes = new ArrayList<String>();
        SQLiteDatabase database = banco.getReadableDatabase();
        Cursor c = database.rawQuery(query, null);
        if (c != null) {
            while (c.moveToNext()) {
                String chipqrc = c.getString(c.getColumnIndex("chipqrc"));
                String operadoraqrc = c.getString(c.getColumnIndex("operadoraqrc"));
                String tamanho = c.getString(c.getColumnIndex("tamanho"));
                String tecnologia = c.getString(c.getColumnIndex("tecnologia"));
                String videoCall = c.getString(c.getColumnIndex("videocall"));
                String callBarring = c.getString(c.getColumnIndex("callbarring"));
                String hideId = c.getString(c.getColumnIndex("hideid"));
                String chamadaInternacional = c.getString(c.getColumnIndex("chamadainternacional"));
                String simAt = c.getString(c.getColumnIndex("simat"));

                informacoes.add(chipqrc);
                informacoes.add(operadoraqrc);
                informacoes.add(tamanho);
                informacoes.add(tecnologia);
                informacoes.add(videoCall);
                informacoes.add(callBarring);
                informacoes.add(hideId);
                informacoes.add(chamadaInternacional);
                informacoes.add(simAt);
            }
        }
        return informacoes;
    }

}