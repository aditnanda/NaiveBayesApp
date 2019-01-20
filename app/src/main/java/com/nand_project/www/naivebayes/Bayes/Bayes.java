package com.nand_project.www.naivebayes.Bayes;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.nand_project.www.naivebayes.Model.Key;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Bayes {
    private Object[][] data;
    private Object[] category;
    private Object[][] input;
    private double[][] temp;

    public ArrayList<Key> key = new ArrayList<>();
    private DecimalFormat df = new DecimalFormat("#.###");
    public ArrayList<Key> dataBaru = new ArrayList<>();
    private ArrayList <String> atribut;
    String xkci ="";
    Context context;
    int akhir = 0;

    public Bayes(Object[][] data, Object[] category, Object[][] input, int label, int jlhAtt, Context context, ArrayList<String> atribut){
        this.data = data;
        this.category = category;
        this.input = input;
        temp = new double[label][jlhAtt];
        this.context = context;
        this.atribut = atribut;
    }

    public void classify(int sum[]){
        int label[];
        int total = 0;
        for(int t = 0; t<sum.length; t++)
                total+=sum[t];
        
        for(int k = 0; k<input.length; k++){
            for(int y = 0; y<input[k].length; y++){
                label = new int[sum.length];                
                for(int i = 0; i<data.length; i++){
                    for(int j = 0; j<data[i].length; j++){
                        if(data[i][j].equals(input[k][y])){
                            //System.out.println(data[i][j]+ " "+input[k][y]);
                            for(int v = 0; v<key.size(); v++) {
                                if (category[i].equals(key.get(v).getInfo())) {

                                    label[key.get(v).getK()]++;
                                }
                            }
                        }
                    }
                }
                
                //P(Xk|Ci)
                for(int l = 0; l<label.length; l++){
                        temp[l][y] = (double)label[l]/sum[l];                       

                        xkci = "P("+atribut.get(y)+"="+input[k][y]+" | "+atribut.get(input[k].length)+"="+key.get(l).getInfo()+") = "
                                + "("+(double)label[l]+"/"+sum[l]+") = "+df.format(temp[l][y])+"\n";
                    Intent intent = new Intent("proses1");
                    //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
                    intent.putExtra("xkci",xkci);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                    System.out.println(xkci);
                }
                xkci = "\n";
                Intent intent = new Intent("proses1");
                //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
                intent.putExtra("xkci",xkci);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                System.out.println(xkci);
            }

            // P(X|Ci)
            double X[] = new double[sum.length];
            for(int i = 0; i<temp.length; i++){
                for(int j = 0; j<temp[i].length; j++){
                    if(j!=0) { // (47)
                        X[i] *= (double) temp[i][j];
                        if (j==temp[i].length-1){
                            Intent intent = new Intent("proses2");
                            //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
                            intent.putExtra("xci", df.format((double) temp[i][j]) + "");
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        }else {
                            Intent intent = new Intent("proses2");
                            //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
                            intent.putExtra("xci", df.format((double) temp[i][j]) + "*");
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        }
                        System.out.println(X[i]);
                    }else {
                        X[i] = (double) temp[i][j];
                        Intent intent = new Intent("proses2");
                        //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
                        intent.putExtra("xci",df.format((double) temp[i][j])+"*");
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        System.out.println(X[i]);
                    }
                }
                System.out.println("P(X|Ci) : kelas "+key.get(i).getInfo()+" = "+df.format(X[i]));
                Intent intent = new Intent("proses2");
                //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
                intent.putExtra("xci","\nP(X| "+ atribut.get(atribut.size()-1)+" = "+key.get(i).getInfo()+") = "+df.format(X[i])+"\n\n");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
            System.out.println("------------------------------------------");
            // P(X|Ci) * P(Ci)
            double c[] = new double[sum.length];
            for(int i = 0; i<X.length; i++){
                c[i] = (double)sum[i]/total;

            }
            addNewLabel(c, X, k);
        }
    }

    public void addNewLabel(double c[], double X[], int k){
        double max = -1;
        String info = "";
        for(int i = 0; i<c.length; i++){
            if(c[i]*X[i]>max){
                max = c[i]*X[i];
                info = key.get(i).getInfo();

            }
            if(i==c.length-1)
                dataBaru.add(new Key(k, info, max));
            
            System.out.println("P(X|Ci) * P(Ci) kelas "+key.get(i).getInfo()+" = "+df.format(c[i]*X[i]));
            Intent intent = new Intent("proses3");
            intent.putExtra("xcici", "P(X| "+ atribut.get(atribut.size()-1)+" = "+key.get(i).getInfo()+") * P("+ atribut.get(atribut.size()-1)+" = "+key.get(i).getInfo()+") = "+df.format(c[i]*X[i])+"\n\n");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }

    public ArrayList<Key> getNewLabel(){
        return dataBaru;
    }
}