package com.sumasoft.statusreport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//yyyy-mm-dd to dd/mm/yyyy
public class Test {
	public static void main(String[] args) throws IOException {
//30868501 ,30883865 ,32196313 ,32503567 ,32536386 ,32624604 ,32699648 ,32699650 ,33474035 ,33484166 ,33505770 ,124 ,129 ,36031585 ,36110617 ,37386939 ,37681477 ,37817337 ,37948198 ,37950283 ,38146178 ,38192831 ,38192839 ,38286109 ,38262937 ,38262943 ,38262946 ,38262948 ,38262949 ,38262952 ,38262954 ,38262956 ,38262915 ,38262917 ,38262919 ,38262922 ,38262923 ,38262924 ,38262925 ,38262928 ,38262930 ,38262932 ,38262933 ,38301376 ,38269994 ,38347971 ,38379553 ,38381241 ,38410948 ,38385173 ,38471638 ,38502589 ,38518440 ,38549275 ,38549356 ,38626523 ,38642039 ,38704153 ,38653961 ,38719947 ,38755039 ,38840898 ,38785244 ,38857366 ,38873729 ,38906876 ,38918609 ,38971943 ,38953112 ,39052878 ,17691 ,17692 ,
		String strFileName="D:\\EchoTntExcelFilter\\ExcelFiltering_24_March\\Input\\Holidaylist.xlsx";
		FileInputStream file1 = new FileInputStream(new File(strFileName));
		XSSFSheet sheet = new XSSFWorkbook(file1).getSheetAt(0);
		int lastRow = sheet.getPhysicalNumberOfRows();		
		 ArrayList<LocalDate> holidays = new ArrayList<LocalDate>();
		 DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
		 
		 LocalDate dateToCompare = LocalDate.now();
		 
		for(int i=1; i<lastRow; i++) {
			LocalDate l = LocalDate.parse(sheet.getRow(i).getCell(1).toString(),dtf);
			holidays.add(l);
		}
		System.out.println("hoildays list"+holidays);
		if(holidays.contains(dateToCompare)) {
			System.out.println("today is holiday "+dateToCompare);
		}
	}

}
