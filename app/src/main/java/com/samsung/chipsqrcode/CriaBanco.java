package com.samsung.chipsqrcode;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CriaBanco extends SQLiteOpenHelper {

    //CRIAÇÃO BANCO DE DADOS
    private static final String NOME_BANCO = "banco.db";
    private static final int VERSAO = 1;

    //CRIAÇÃO TABELA EMPRÉSTIMO
    public static final String TABELA = "emprestimo";
    public static final String ID = "_id";
    public static final String TESTER = "tester";
    public static final String OPERADORA = "operadora";
    public static final String CHIP = "chip";
    public static final String DATAHORA = "datahora";

    //CRIAÇÃO TABELA LOGIN
    public static final String TABELA_LOGIN = "login";
    public static final String ID_LOGIN = "_id";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    //CRIAÇÃO TABELA CHIPS
    public static final String TABELA_CHIPQRCODE = "chipqrcode";
    public static final String ID_CHIPQRCODE = "_id";
    public static final String CHIPQRCODE = "chipqrc";
    public static final String OPERADORAQRCODE = "operadoraqrc";
    public static final String TAMANHO = "tamanho";
    public static final String TECNOLOGIA = "tecnologia";
    public static final String VIDEOCALL = "videocall";
    public static final String CALLBARRING = "callbarring";
    public static final String HIDEID = "hideid";
    public static final String CHAMADAINTERNACIONAL = "chamadainternacional";
    public static final String SIMAT = "simat";

    private Context fContext;

    public CriaBanco(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
        fContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //CRIAÇÃO TABELA EMPRESTIMO
        String sql_emprestimo = "CREATE TABLE " + TABELA + " ("
                + ID + " integer primary key autoincrement, "
                + TESTER + " text, "
                + OPERADORA + " text, "
                + CHIP + " text, "
                + DATAHORA + " text"
                + ")";

        //CRIAÇÃO TABELA LOGIN
        String sql_login = "CREATE TABLE " + TABELA_LOGIN + " ("
                + ID_LOGIN + " integer primary key autoincrement, "
                + USERNAME + " text, "
                + PASSWORD + " text"
                + ")";

        //CRIAÇÃO TABELA CHIPS
        String sql_chip = "CREATE TABLE " + TABELA_CHIPQRCODE + " ("
                + ID_CHIPQRCODE + " integer primary key autoincrement, "
                + CHIPQRCODE + " text, "
                + OPERADORAQRCODE + " text, "
                + TAMANHO + " text, "
                + TECNOLOGIA + " text, "
                + VIDEOCALL + " text, "
                + CALLBARRING + " text, "
                + HIDEID + " text, "
                + CHAMADAINTERNACIONAL + " text, "
                + SIMAT + " text"
                + ")";

        db.execSQL(sql_emprestimo);
        db.execSQL(sql_login);
        db.execSQL(sql_chip);

        //POPULANDO TABELAS LOGIN E CHIPS
        ContentValues valoresLogin = new ContentValues();
        ContentValues valoresChips = new ContentValues();
        ContentValues valoresEmprestimo = new ContentValues();
        Resources res = fContext.getResources();
        //VARIAVEIS LOGIN
        String[] usuarios = res.getStringArray(R.array.usuarios);
        String[] passwords = res.getStringArray(R.array.passwords);
        //VARIAVEIS CHIPS
        String[] chips = res.getStringArray(R.array.chips);
        String[] operadoras = res.getStringArray(R.array.operadoras);
        String[] tamanho = res.getStringArray(R.array.tamanho);
        String[] tecnologia = res.getStringArray(R.array.tecnologia);
        String[] videoCall = res.getStringArray(R.array.videocall);
        String[] callBarring = res.getStringArray(R.array.callbarring);
        String[] hideId = res.getStringArray(R.array.hideid);
        String[] chamadaInternacional = res.getStringArray(R.array.chamadainternacional);
        String[] simAt = res.getStringArray(R.array.simat);
        //VARIAVEIS EMPRESTIMO
        String[] tester = res.getStringArray(R.array.tester);
        String[] operadora = res.getStringArray(R.array.operadora);
        String[] chip = res.getStringArray(R.array.chip);
        String[] datahora = res.getStringArray(R.array.datahora);


        //POPULANDO TABELA LOGIN
        int tLogin = usuarios.length;
        for (int i = 0; i < tLogin; i++) {
            valoresLogin.put(USERNAME, usuarios[i]);
            valoresLogin.put(PASSWORD, passwords[i]);
            db.insert(TABELA_LOGIN, null, valoresLogin);
        }

        //POPULANDO TABELA CHIPS
        int tChipQrCode = chips.length;
        for (int i = 0; i < tChipQrCode; i++) {
            valoresChips.put(CHIPQRCODE, chips[i]);
            valoresChips.put(OPERADORAQRCODE, operadoras[i]);
            valoresChips.put(TAMANHO, tamanho[i]);
            valoresChips.put(TECNOLOGIA, tecnologia[i]);
            valoresChips.put(VIDEOCALL, videoCall[i]);
            valoresChips.put(CALLBARRING, callBarring[i]);
            valoresChips.put(HIDEID, hideId[i]);
            valoresChips.put(CHAMADAINTERNACIONAL, chamadaInternacional[i]);
            valoresChips.put(SIMAT, simAt[i]);
            db.insert(TABELA_CHIPQRCODE, null, valoresChips);
        }

        //POPULANDO TABELA EMPRESTIMO
        int tEmprestimo = tester.length;
        for (int i = 0; i < tEmprestimo; i++) {
            valoresEmprestimo.put(TESTER, tester[i]);
            valoresEmprestimo.put(OPERADORA, operadora[i]);
            valoresEmprestimo.put(CHIP, chip[i]);
            valoresEmprestimo.put(DATAHORA, datahora[i]);
            db.insert(TABELA, null, valoresEmprestimo);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("TAG", "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        String alter_query1 = "ALTER TABLE emprestimo RENAME TO temp";
        db.execSQL(alter_query1);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_LOGIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_CHIPQRCODE);
        onCreate(db);
        String alter_query2 = "INSERT INTO emprestimo SELECT * FROM temp";
        String alter_query3 = "DROP TABLE temp";
        db.execSQL(alter_query2);
        db.execSQL(alter_query3);
    }
}