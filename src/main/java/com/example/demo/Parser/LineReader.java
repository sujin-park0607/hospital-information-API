package com.example.demo.Parser;

import com.example.demo.domain.Hospital;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LineReader {
    private HospitalParser hp = new HospitalParser();

    List<Hospital> readLines(String filename) throws IOException {
        List<Hospital> result = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename),"UTF-8"));
        String str;
        br.readLine();
        br.readLine();
        while((str = br.readLine()) != null){
            result.add(hp.parse(str));
        }
        return result;
    }
}
