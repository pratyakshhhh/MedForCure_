package com.example.MedforCure;

public class Product_Other_Details_Model {

    public static final int OTHER_DETAILS_TITLE = 0;
    public static final int OTHER_DETAILS_BODY =1;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    ///////Other Details title
    private String title;

    public Product_Other_Details_Model(int type, String title) {
        this.type = type;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    ///////Other Details title

    ///////Other Details Body
    private String argumentName;
    private String argumentValue;

    public Product_Other_Details_Model(int type, String argumentName, String argumentValue) {
        this.type = type;
        this.argumentName = argumentName;
        this.argumentValue = argumentValue;
    }

    public String getArgumentName() {
        return argumentName;
    }

    public void setArgumentName(String argumentName) {
        this.argumentName = argumentName;
    }

    public String getArgumentValue() {
        return argumentValue;
    }

    public void setArgumentValue(String argumentValue) {
        this.argumentValue = argumentValue;
    }

    ///////Other Details Body

}
