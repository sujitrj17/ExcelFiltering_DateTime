package com.sumasoft.statusreport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import com.sumasoft.statusreport.DataBaseFolderDeletionDAO;
import com.sumasoft.statusreport.ErrorMessages;
import com.sumasoft.statusreport.DateTimeValidator;
import com.sumasoft.statusreport.ExceptionLogger;

public class DataBaseFolderDeletionService 
{
	DataBaseFolderDeletionDAO dbAccess = new DataBaseFolderDeletionDAO();
	/**
	 * This method initially verifies input parameters and then calls method to create reports
	 */
	public void dataBaseDelete()
	{
		DataBaseFolderDeletionDAO dbAccess = null;
		//System.out.println("In generateReports()");
		try
		{
			new DateTimeValidator();
			
			dbAccess = new DataBaseFolderDeletionDAO();
			dbAccess.dataBaseDelete();
			System.out.println("Cases Deleted from Email_Disburstment Queue........");
			System.out.println("Service Executed successfully........");
		
		}
		catch(Exception exception)
		{
			System.out.println(ErrorMessages.UNEXPECTED_ERROR_OCCURED);
			ExceptionLogger.error(exception);
		}
		finally
		{
			dbAccess = null;
		}
	}

	public void filterExcel() {
		
	    LinkedHashMap<String, ArrayList<String>> hmGreenheckData=new  LinkedHashMap<String, ArrayList<String>>();
	    LinkedHashMap<String, ArrayList<String>> hmCSData=new  LinkedHashMap<String, ArrayList<String>>();
	    LinkedHashMap<String, ArrayList<String>> hmCSData1=new  LinkedHashMap<String, ArrayList<String>>();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		SimpleDateFormat formatterDDMMYY = new SimpleDateFormat("dd/MM/yyyy");
		String strFileName="D:\\EchoTntExcelFilter\\ExcelFiltering_24_March\\Input\\TracknTrace_CS1.xls";
		String ext = strFileName.substring(strFileName.lastIndexOf('.') + 1);
			
		if(ext.equalsIgnoreCase("csv"))
		System.out.println(" csv");
		else if(ext.equalsIgnoreCase("txt"))
		System.out.println("txt ");
		else if(ext.equalsIgnoreCase("xlsx"))
		System.out.println("xlsx ");
		else if(ext.equalsIgnoreCase("xls"))
			System.out.println("xls");
		else
			{
				//errors.add((String)request.getSession().getAttribute("USER_ID"), new ActionMessage("errors.file.type","file"));
				return;
			}
		
		
		//FormFile file ="E:\\TNT Excel\\EchoTNTexcelFilter\\Input\\TracknTrace_CS1.xls";
		try
		 {
		 int fileRowCount=49;
		 System.out.println(strFileName);
		 FileInputStream file1 = new FileInputStream(new File(strFileName));
		
		 //Create Workbook instance holding reference to .xls file
		 HSSFWorkbook workbook = new HSSFWorkbook(file1);
		
		 //Get first/desired sheet from the workbook
		 HSSFSheet sheet = workbook.getSheetAt(0);
		
		 int indexRow = 0;
		 for(Row row : sheet )
		 {
				ArrayList<String> listRowData =  new ArrayList<String>();
				ArrayList<String> listRowDataCS =  new ArrayList<String>();
				
		 	String loadNumber=new String();
		 	if(indexRow == 0 || indexRow == 1)
				{
		 		indexRow++;
					continue;
				}
		 	
		 	for(int i = 0; i < fileRowCount; i++) 
		 	{

			 if (i == 4) 
			{
				row.getCell(i).setCellType(Cell.CELL_TYPE_STRING);
		 		loadNumber = row.getCell(i).getStringCellValue();
			}
			 else if(i == 7)
			{
				if((row.getCell(i).getStringCellValue()) != null && (row.getCell(i).getStringCellValue()).equals("Greenheck PARENT"))
				{
					for(int j=0;j<49; j++)
					{
						if(j==0 )
	            		{
							
	            			if((row.getCell(j).getDateCellValue()) != null)
	            			{
		            		listRowData.add(formatterDDMMYY.format(row.getCell(j).getDateCellValue()));
	            			}
	            			else 
	            			{
	            				listRowData.add("");
	            			}
	            			
	            		}
						else if(j==31 )
	            		{
							System.out.println("Cell type= "+row.getCell(j).getCellType());
	            			if((row.getCell(j).getNumericCellValue())!=0)
	            			{
		            		listRowData.add(formatterDDMMYY.format(row.getCell(j).getNumericCellValue()));
	            			}
	            			else 
	            			{
	            				listRowData.add("");
	            			}
	            			
	            		}
						else if(j==1 || j == 2  || j == 3 || j==16)
	            		{
							 
	            		 if((row.getCell(j).getDateCellValue()) != null)
	            			{
			            		listRowData.add(formatter.format(row.getCell(j).getDateCellValue()));
	            			}
	            			else 
	            			{
	            				listRowData.add("");
	            			}
	            			
	            		}
						 else
						 {
							row.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
				        	listRowData.add(row.getCell(j).toString().replaceAll("\n","").replaceAll(","," "));
						 }
		        	
					}
					//System.out.println("Greenheck"+listRowData);
					hmGreenheckData.put(loadNumber, listRowData);
				}
				else if((row.getCell(i).getStringCellValue()) != null && (!(row.getCell(i).getStringCellValue()).equals("Greenheck PARENT") && !(row.getCell(i).getStringCellValue()).equals("M Holland Co") && !(row.getCell(i).getStringCellValue()).contains("LKQ")))
				{
					for(int j=0;j<49; j++)
					{
						if(j==0 )
	            		{
							
	            			if((row.getCell(j).getDateCellValue()) != null)
	            			{
	            				listRowDataCS.add(formatterDDMMYY.format(row.getCell(j).getDateCellValue()));
	            			}
	            			else 
	            			{
	            				listRowDataCS.add("");
	            			}
	            			
	            		}
						else if(j==31 )
	            		{
							
	            			if((row.getCell(j).getNumericCellValue())!=0)
	            			{
	            				listRowDataCS.add(formatterDDMMYY.format(row.getCell(j).getNumericCellValue()));
	            			}
	            			else 
	            			{
	            				listRowDataCS.add("");
	            			}
	            			
	            		}
						else if(j==1 || j == 2  || j == 3 || j==16)
	            		{
							 
	            		 if((row.getCell(j).getDateCellValue()) != null)
	            			{
	            			 listRowDataCS.add(formatter.format(row.getCell(j).getDateCellValue()));
	            			}
	            			else 
	            			{
	            				listRowDataCS.add("");
	            			}
	            			
	            		}
						 else
						 {
							row.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
							listRowDataCS.add(row.getCell(j).toString().replaceAll("\n","").replaceAll(","," "));
						 }
		        	
					}
					 hmCSData.put(loadNumber, listRowDataCS);
				}
		 		}		 		
			
		 	}
		 	
		 	
		 	indexRow++;
		
		 }
		 //System.out.println(hmCSData);
		 //System.out.println(hmCSData.size());
		 
		 hmCSData1=dbAccess.compareCustomerMaster(hmCSData);
		 hmCSData1=dbAccess.compareTodayDate(hmCSData1);
		 hmCSData1=dbAccess.compareCaseCreated(hmCSData1);
		 hmCSData1=dbAccess.compareOwnerName(hmCSData1);
		 hmCSData1=dbAccess.filterPickupByDate(hmCSData1);
		 hmCSData1 = dbAccess.filterForNuvozymes_AndPastPickUpDate(hmCSData1);
		 hmCSData1 = dbAccess.filterForSamsungNeoviaLogistics(hmCSData1);
		 hmCSData1 = dbAccess.filterForRemovingCarriers(hmCSData1);
		 hmCSData1 = dbAccess.filterManifestCarrier(hmCSData1);
		 file1.close();
		 createGreenHeckExcel(hmGreenheckData);
		 }
		 
		
		 
		 catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	     
	        
	    		
			
}
		
		private void createGreenHeckExcel(LinkedHashMap<String, ArrayList<String>> hmGreenheckData) throws IOException {
		
		  //create blank workbook
		  HSSFWorkbook workbook = new HSSFWorkbook(); 
		 
		  //Create a blank sheet
		  HSSFSheet sheet = workbook.createSheet("GreenheckOriginal");
		  
		  //Create row object
		  HSSFRow row1;
		  
		  String[] headers = {"Pickup By Date","Requested Deliver By","Original Delivery Date","Delivery By Date","Load ID","BOL #","Customer Name","Parent Company","Actual Carrier","Pro #",
				  "Booking Rep","Mode","Rating Code","Status","Owner","Notes","Note Created Date","Case Created?","Origin Name","Origin Address","Origin City","Origin State",
				  "Origin Zip","Origin Phone","Dest Name","Dest Address","Dest City","Dest State","Dest Zip","Dest Phone","Weight","Pallet Count","Pieces Count","Pick Up No","Load PO",
				  "Order Num","Trailer Number","Booked With","Pick 1","Pick 2","Pick 3","Drop 1","Drop 2","Drop 3","Load Reserve","Lift Gate Required","Inside Pickup Delivery",
				  "Notify Prior To Del","Location Type"};
		  
				  CellStyle style = workbook.createCellStyle();  
		          // Setting Background color  
		        //  style.setFillBackgroundColor(IndexedColors.BLUE.getIndex()); 
		          style.setFillForegroundColor(IndexedColors.BLUE.getIndex());

		          style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		          //  style.setFillPattern(FillPatternType.BIG_SPOTS);  

		           row1 = sheet.createRow(0);  
		           int cellid = 0;
		          for(String header:headers) {
		        	  Cell cell = row1.createCell(cellid++);
		        	  cell.setCellStyle(style);
		        	  cell.setCellValue(header);
		        	

		          }
		  
		      //Iterate over data and write to sheet
		      Set<String> keyid = hmGreenheckData.keySet();
		      int rowid = 1;
		      
		      for (String key : keyid) {
		         row1 = sheet.createRow(rowid++);
		         ArrayList<String> objectArr = hmGreenheckData.get(key);
		          cellid = 0;
		         
		         for (Object obj : objectArr){
		            Cell cell = row1.createCell(cellid++);
		            if(obj instanceof Date)
			            {
			                cell.setCellValue((Date) obj);
			            }
			            else if(obj instanceof Boolean)
			            {
			                cell.setCellValue((Boolean) obj);
			            }
			            else if(obj instanceof String)
			            {
			                cell.setCellValue((String) obj);
			            }
			            else if(obj instanceof Double)
			            {
			                 cell.setCellValue((Double) obj);
			            }
		         }
		      }
		   
			try {
				   FileOutputStream fileOut = new FileOutputStream(new File("D:\\EchoTntExcelFilter\\ExcelFiltering_24_March\\Output\\Greenheck.xls"));
				    //Write the workbook in file system
				    workbook.write(fileOut);
					fileOut.flush();
					fileOut.close();
			}
		    catch (Exception e) 
		    {
		     e.printStackTrace();
		    }
		    
		
		}
		}
		
	
