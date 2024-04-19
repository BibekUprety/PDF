package com.pdf.view;

import java.util.*;

public class PdfData {

    public static List<Map<String, Object>> getData() {



        Map<String, Object> l = new HashMap<>();
        l.put("id", 1);
        l.put("transaction name", 1);
        l.put("Transaction Date", 1);
        l.put("Customer Name", 1);
        l.put("Customer Mobile", 1);
        l.put("Customer Address", 1);
        l.put("Customer Net Commission (Rs.)", 1);
        l.put("Transaction Status", 1);
        l.put("Account No.", 1);
        l.put("name", "Bibek Upreti1");
        l.put("address", "kathmandu1");
        l.put("email", "bibek@gmail.com1");
        l.put("mobileNo", "98403489371");


        Map<String, Object> l1 = new HashMap<>();

        l1.put("id", 1);
        l1.put("transaction name", 1);
        l1.put("Transaction Date", 1);
        l1.put("Customer Name", 1);
        l1.put("Customer Mobile", 1);
        l1.put("Customer Address", 1);
        l1.put("Customer Net Commission (Rs.)", 1);
        l1.put("Transaction Status", 1);
        l1.put("Account No.", 1);
        l1.put("name", "Bibek Upreti1");
        l1.put("address", "kathmandu1");
        l1.put("email", "bibek@gmail.com1");
        l1.put("mobileNo", "98403489371");


        return new ArrayList<>(Arrays.asList(l, l1));
    }


}
