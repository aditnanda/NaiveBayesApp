package com.nand_project.www.naivebayes;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nand_project.www.naivebayes.Bayes.Bayes;
import com.nand_project.www.naivebayes.Model.Key;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {
    private Object[][] data;
    private Object[] category;
    //private Object[][] klasifikasi = {{"<=30","sedang","ya","fair"}};// isi data input disini
    private HashSet<String> hash;
    int sum[];
    private Bayes b;
    int baris=0;
    int kolom=0;
    int attr = 0;
    String [] par_kolom;
    String [] par_atrrib;
    private Object[][] klasifikasi ;
    private ArrayList<String> atribut ;
    String DataSet = null;

    private EditText et_input;
    private Button btn_proses;
    private TextView tv_hasil, tv_proses_1, tv_proses_2, tv_proses_3, tv_dataset;
    private Dialog myDialogShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_input = findViewById(R.id.input);
        btn_proses = findViewById(R.id.proses);
        tv_hasil = findViewById(R.id.hasil);
        tv_proses_1 = findViewById(R.id.proses1);
        tv_proses_2 = findViewById(R.id.proses2);
        tv_proses_3 = findViewById(R.id.proses3);

        btn_proses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_input.getText().toString().equals("")){
                    et_input.setError("Data baru harus diisi");
                }else {
                    baris =0;
                    kolom = 0;
                    atribut = new ArrayList<>();
                    atribut.clear();
                    hash = new HashSet<>();
                    tv_hasil.setText("");
                    tv_proses_1.setText("");
                    tv_proses_2.setText("");
                    tv_proses_3.setText("");
                    String input[] = et_input.getText().toString().split(",");
                    klasifikasi = new Object[1][input.length];
                    for (int i =0;i<input.length;i++){
                        klasifikasi[0][i] = input[i];
                    }

                    try {
                        read_db();
                        proses();
                    } catch (Exception ex) {
                        Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(proses1Receiver,
                new IntentFilter("proses1"));

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(proses2Receiver,
                new IntentFilter("proses2"));

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(proses3Receiver,
                new IntentFilter("proses3"));

    }

    public BroadcastReceiver proses1Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            tv_proses_1.append(intent.getStringExtra("xkci"));

        }
    };

    public BroadcastReceiver proses2Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            tv_proses_2.append(intent.getStringExtra("xci"));

        }
    };

    public BroadcastReceiver proses3Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            tv_proses_3.append(intent.getStringExtra("xcici"));

        }
    };

    public void read_db(){

        readFile(false);

    }

    private void readFile(boolean b) {
        try {
            InputStream is = MainActivity.this.getAssets().open("dataset.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            DataSet = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        //File f = new File("dataset.txt");
        Scanner sc = null;
        try {
            sc = new Scanner(DataSet);
            while(sc.hasNextLine()){
                String line = sc.nextLine();
//                if (attr ==0){
//                    attr++;
//                }else{
                if (!line.contains("/")){
                    par_kolom = line.split(",");
                    baris++;
                }else{
                    par_atrrib = line.split("/");
                }
//                }

            }

            System.out.println("baris "+baris+" kolom "+par_kolom.length);

        } catch (Exception e) {
            e.printStackTrace();
        }

        data = new Object[baris][par_kolom.length];

        Scanner scanner = null;
        int i = 0;
        try {
            scanner = new Scanner(DataSet);

            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
//                if (attr == 1){
//                    par_kolom = line.split(",");
//                    for (int j = 0; j < par_kolom.length; j++) {
//                        atribut[j] = par_kolom[j];
//                        System.out.print(atribut[j] + ", ");
//                    }
//                    System.out.println();
//                }else {
                if (!line.contains("/")){
                    par_kolom = line.split(",");
                    for (int j = 0; j < par_kolom.length; j++) {
                        data[i][j] = par_kolom[j];
                        System.out.print(data[i][j] + ", ");
                        if (b == true){
                            tv_dataset.append(data[i][j] + ", ");
                        }
                    }
                    System.out.println();
                    if (b == true){
                        tv_dataset.append("\n");
                    }
                    i++;
                }else {
                    par_atrrib = line.split("/");
                    for (int j = 0;j<par_atrrib.length;j++){
                        atribut.add(par_atrrib[j]);
                        if (b == true){
                            tv_dataset.append(par_atrrib[j] + ", ");
                        }
                    }
                    Log.d("hasil",atribut.toString());
                    if (b == true){
                        tv_dataset.append("\n\n");
                    }
                }
//                }
            }

            System.out.println("-----------------------------------");
            category = new Object[data.length];
            for(i = 0; i<data.length; i++){
                category[i] = data[i][data[i].length-1];
                hash.add(String.valueOf(category[i]));
            }
            sum = new int[hash.size()];
            //System.out.println("baris "+baris+" kolom "+par_kolom.length);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void proses(){
        System.out.println(data.toString()+" "+category.toString());
        b= new Bayes(data, category, klasifikasi, hash.size(), 4, this, atribut);
        if(!b.dataBaru.isEmpty()){
            b.dataBaru.clear();
            b.key.clear();
        }
        int idx = 0;
        for(String s:hash){
            for(int ii = 0; ii<category.length; ii++){
                if(s.equals(String.valueOf(category[ii]))){
                    sum[idx]+=1;
                    //System.out.println(sum.toString());
                }
            }
            b.key.add(new Key(idx, s));
            idx++;
        }
        b.classify(sum);
        System.out.println("\nHasil Klasifikasi :");
        int row = 0;
        for(Key key:b.getNewLabel()){
            System.out.println("Data ke "+(row+1)+" diklasifikasikan ke kelas :\t"+key.getInfo());
            tv_hasil.append("Data diklasifikasikan ke kelas : "+atribut.get(atribut.size()-1)+" = "+key.getInfo()+"\n");
            tv_hasil.append("---------------------------------------------------------\n");
            tv_hasil.append(et_input.getText()+","+key.getInfo());
            row++;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate dari menu; disini akan menambahkan item menu pada Actionbar
        getMenuInflater().inflate(R.menu.dataset, menu);//Memanggil file bernama menu di folder menu
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_dataset:
                showDataset();
                Toast.makeText(this,"Dibuat oleh Aditya Nanda Utama\n1461600030\nwww.nand-project.com",Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDataset() {
        myDialogShow = new Dialog(this);
        myDialogShow.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialogShow.setContentView(R.layout.popup_dataset);

        tv_dataset = myDialogShow.findViewById(R.id.dataset);
        tv_dataset.setText("");

        readFile(true);

        myDialogShow.setCancelable(true);
        myDialogShow.show();
        Window window = myDialogShow.getWindow();
        window.setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
    }
}
