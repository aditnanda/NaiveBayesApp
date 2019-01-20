package com.nand_project.www.naivebayes.Model;

public class Key {
    private int k;
    private String info;
    private double value;

    public Key(){
        this.k = 0;
        this.info = null;
        this.value = 0;
    }
    
    public Key(int k, String info){
        this.k = k;
        this.info = info;
    }

    public Key(int k, String info, double value){
        this.k = k;
        this.info = info;
        this.value = value;
    }

    public int getK(){
        return k;
    }

    public String getInfo(){
        return info;
    }

    public double getValue(){
        return value;
    }

    public void setK(int k) {
        this.k = k;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setValue(double value) {
        this.value = value;
    }
    
}
