package org.example.kafka.producer;

import java.io.Serializable;
import java.util.List;

public class Request implements Serializable {
    String func;

    List<String> params;

    public Request(String func, List<String> params) {
        this.func = func;
        this.params = params;
    }


    public String getFunc() {
        return func;
    }

    public List<String> getParams() {
        return params;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }
}
