package com.sumasoft.statusreport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.postgresql.Driver;

//import org.postgresql.Driver;

import com.sumasoft.statusreport.ErrorMessages;
import com.sumasoft.statusreport.ExceptionLogger;

public class DataBaseFolderDeletionDAO 
{
	private static String strDriverName = null;
	private static String strDBUrl = null;
	private static String strUserName = null;
	private static String strPassword = null;
	 int count=0;
	/**
	 * This method generates SLA and History reports for given input date and time parameters
	 * @param strStartDate
	 * @param strStartTime
	 * @param strEndDate
	 * @param strEndTime
	 * @return true or false
	 */
	public boolean dataBaseDelete()
	{
		try
		{
			//System.out.println("Call to generateReports() in DB");
			// Generate SLA Report
			if(!DataBaseFolderDeletionDAO.loadProperties())
			{
				
				return false;
			}
			
			generateSLAReport();
			
		}
		catch(Exception exception)
		{
			ExceptionLogger.error(exception);
		}
		return true;
	}
	
	/**
	 * This method generates SLA report for given input date and time parameters
	 */
	private boolean generateSLAReport()
	{
		//System.out.println("In generateSLAReport() ");
		String startDateTime = null;
		String endDateTime = null;
		String strReportsDirectoryName = null;
		String strCaseId  =null;
		
		boolean deletefolder;
		boolean deletefolderReupload;

		ResultSet resultSet = null;
	
		
		//startDateTime = strStartDate + ":" + strStartTime;
		//endDateTime   = strEndDate + ":" + strEndTime;
		
		
        StringBuffer strBuffer1 = null;

		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		
		//Statement stmt = null; 

		
		strBuffer1 = new StringBuffer();
		
		
		
		Connection connection = null;
		File outputFile = null;
		File outputFileReuploaded = null;
		Statement stmt = null; 
				
		       try
				{
				   
					ResourceBundle rb = ResourceBundle.getBundle("DBAccess");
					//Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
				    String strPath=rb.getString("path");
					connection=createConnection();
					connection.setAutoCommit(false);
					ExceptionLogger.info("--------------------------------------------------------" );
					
					//System.out.println("--------------------------------------------------------" );
					ExceptionLogger.info("Service Started Successfully" );
					//System.out.println("Service Started Successfully" );
              
					if(connection!=null)
					{      
						//delete data from start date to end date. 
						//strBuffer1.append("Select distinct(w.id) from workitem w where w.date_time>=to_timestamp('"+startDateTime+"','DD/MM/YYYY HH24:MI:SS') and  w.date_time<=to_timestamp('"+endDateTime+"','DD/MM/YYYY HH24:MI:SS') union select distinct(w.id) from workitem w,workitem_pdf_name_map om where om.work_id=w.id and w.date_time>=to_timestamp('"+startDateTime+"','DD/MM/YYYY HH24:MI:SS') and w.date_time<=to_timestamp('"+endDateTime+"','DD/MM/YYYY HH24:MI:SS') ");//TVS
						//Select distinct(w.id) from workitem w where w.date_time>=to_timestamp('08/08/2018 00:01:00','DD/MM/YYYY HH24:MI:SS') and  w.date_time<=to_timestamp('08/08/2018 00:50:00','DD/MM/YYYY HH24:MI:SS') union select distinct(w.id) from workitem w,workitem_pdf_name_map om where om.work_id=w.id and w.date_time>=to_timestamp('08/08/2018 00:01:00','DD/MM/YYYY HH24:MI:SS') and w.date_time<=to_timestamp('08/08/2018 00:50:00','DD/MM/YYYY HH24:MI:SS') 

						strBuffer1.append("Select distinct(w.id) from workitem w where w.process_id='10' union select distinct(w.id) from workitem w,workitem_pdf_name_map om where om.work_id=w.id and w.process_id='10' ");// CD 

						stmt = connection.createStatement();
							resultSet = stmt.executeQuery(strBuffer1.toString());
							
							while(resultSet.next())
							{
								
								strCaseId=resultSet.getString(1);
								
								
								ExceptionLogger.info("strCaseId : "+strCaseId +"---strCaseId :");
							   
									 
									
							    outputFile = new File(strPath+File.separator+"var"+File.separator+"uploadedPdf"+File.separator+strCaseId+File.separator);
								
							    outputFileReuploaded = new File(strPath+File.separator+"var"+File.separator+"uploadedPdf"+File.separator+strCaseId+File.separator);
   
							    Statement stmt1 = connection.createStatement();
								
							    String delete="delete from workitemtransaction Where work_item_id='"+strCaseId +"'";
								stmt1.addBatch(delete);
								        
								      
								String delete1="delete from workitemallocation Where work_item_id='"+strCaseId +"'";
								stmt1.addBatch(delete1);
								        
								String delete2="delete from workitem where id='"+strCaseId +"'";
								stmt1.addBatch(delete2);
								        
								String delete3="delete from workitem_pdf_name_map where work_id='"+strCaseId +"'";
								stmt1.addBatch(delete3);
								 
								
								int[] count = stmt1.executeBatch();
								       
								deletefolder=deleteRecursive(outputFile);
								deletefolderReupload=deleteRecursive(outputFileReuploaded);

										//System.out.println("deletefolder"+deletefolder);
								if(deletefolder && deletefolderReupload){
										
										connection.commit();
										if(count.length>0)
											System.out.print("");
										else
											System.out.println("error");
								}
								else
										return false;
								}
							
				        
						strBuffer1 = new StringBuffer();   
						//strBuffer1.append("Select distinct(w.id) from workitem w where w.date_time>=to_timestamp('"+startDateTime+"','DD/MM/YYYY HH24:MI:SS') and w.date_time<=to_timestamp('"+endDateTime+"','DD/MM/YYYY HH24:MI:SS') ");
						strBuffer1.append("Select distinct(w.id) from workitem w where w.process_id='10' union select distinct(w.id) from workitem w,workitem_pdf_name_map om where om.work_id=w.id and w.process_id='10' ");// CD 
	
						stmt = connection.createStatement();
							resultSet = stmt.executeQuery(strBuffer1.toString());
							
							while(resultSet.next())
							{
								
								strCaseId=resultSet.getString(1);
								
								
								
								ExceptionLogger.info("strCaseId : "+strCaseId +"---strCaseId :");
							  
									 
									
							    outputFile = new File(strPath+File.separator+"var"+File.separator+"uploadedPdf"+File.separator+strCaseId+File.separator);
								
								     
							    Statement stmt1 = connection.createStatement();
								
							    String delete="delete from workitemtransaction Where work_item_id='"+strCaseId +"'";
								stmt1.addBatch(delete);
								        
								      
								String delete1="delete from workitemallocation Where work_item_id='"+strCaseId +"'";
								stmt1.addBatch(delete1);
								        
								String delete2="delete from workitem where id='"+strCaseId +"'";
								stmt1.addBatch(delete2);
								        
								String delete3="delete from workitem_pdf_name_map where work_id='"+strCaseId +"'";
								stmt1.addBatch(delete3);
								    
								       
								int[] count = stmt1.executeBatch();
								  
								deletefolder=deleteRecursive(outputFile);
								deletefolderReupload=deleteRecursive(outputFileReuploaded);

									
								if(deletefolder && deletefolderReupload){
										
										connection.commit();
										if(count.length>0)
											System.out.print("");
										else
											System.out.println("error");
								}
								else
										return false;
								} 
								
								
					}
					
					ExceptionLogger.info("--------------------------------------------------------" );
					ExceptionLogger.info("Service Completed Successfully" );
					
					
				}
				catch(Exception exception)
				{
					
					ExceptionLogger.error(exception);
				}
				finally
				{
					strBuffer1 = null;
					startDateTime = null;
					endDateTime = null;
					strReportsDirectoryName = null;
					
					try
					{
						if(pstmt1!=null)
							pstmt1.close();
						if(pstmt2!=null)
							pstmt2.close();
						if(connection!=null)
						{
							connection.close();
						}
						pstmt1 = null;
						pstmt2 = null;
						connection=null;
					
					}
					catch(Exception ex)
					{
						ExceptionLogger.error(ex);
					}
				}
				return true;
			
	}
	
	
	
	   public boolean deleteRecursive(File path){
		   
		   if(path.exists())
		   {
		   
		    File[] c = path.listFiles();
	        //System.out.println("Cleaning out folder:" + path.toString());
	        
	        ExceptionLogger.info("Cleaning out folder:" + path.toString());
			//System.out.println("Cleaning out folder:" + path.toString() );

	        
	        for (File file : c){
	            if (file.isDirectory()){
	                //System.out.println("Deleting file:" + file.toString());
	    	        ExceptionLogger.info("Deleting file:" + file.toString());

	                deleteRecursive(file);
	                file.delete();
	            } else {
	                file.delete();
	               // return true;
	            }
	        }
	        path.delete();
	        return true;
		   }
		   else
		   {
		        ExceptionLogger.info("Deleting folder path doesn't exist"+path);
				//System.out.println("Deleting folder path doesn't exist"+path );
                return true;
		   }
		   
		  
	    }
	/**
	 * This method reads parameters configured in properties file.
	 * @return true or false
	 */
	private static boolean loadProperties()
	{
		ResourceBundle resourceBundle = null;
		boolean bRetVal = true;
		
		try
		{
			resourceBundle = ResourceBundle.getBundle("DBAccess");

			strDriverName = resourceBundle.getString("DATABASE_DRIVER");
			System.out.println("strDriverName:  "+strDriverName);
			strDBUrl = resourceBundle.getString("DATABSE_URL");
			System.out.println("strDBUrl:  "+strDBUrl);
			strUserName = resourceBundle.getString("DATABSE_USER");
			System.out.println("strUserName:  "+strUserName);
			strPassword = resourceBundle.getString("DATABASE_PASSWORD");
			System.out.println("strPassword:  "+strPassword);
			resourceBundle.getString("LOG_DIRECTORY_ERROR");
			resourceBundle.getString("REPORTS_PATH");
		}
		catch(MissingResourceException missingResourceException)
		{
			if(missingResourceException.getKey().equalsIgnoreCase("LOG_DIRECTORY_ERROR"))
			{
				/*System.out.println("-------------------------------------------------");
				System.out.println(ErrorMessages.ERROR_LOG_FILE_PATH_NOT_AVAIALABLE);*/
				
   	            ExceptionLogger.info("----------------------------------------");
   	            ExceptionLogger.info(ErrorMessages.ERROR_LOG_FILE_PATH_NOT_AVAIALABLE);

				
				bRetVal = false;
			}
			else
			{
				/*System.out.println("-------------------------------------------------");
				 System.out.println(ErrorMessages.UNEXPECTED_ERROR_OCCURED);*/
				
				  ExceptionLogger.info("----------------------------------------");
	   	          ExceptionLogger.info(ErrorMessages.UNEXPECTED_ERROR_OCCURED);
	   	           
				 ExceptionLogger.error(missingResourceException);
				
				
				
				bRetVal = false;
			}
		}
		catch (Exception e) 
		{
			ExceptionLogger.info("----------------------------------------");
 	        ExceptionLogger.info(ErrorMessages.UNEXPECTED_ERROR_OCCURED);
			ExceptionLogger.error(e);
			
			bRetVal = false;
		}
		finally
		{
			resourceBundle = null;
		}
		System.out.println("Return value:  "+bRetVal);
		return bRetVal;
	}
	
	/**
	 * This method converts given date in milliseconds
	 * @param strDate
	 * @return long value for given date
	 * @throws Exception
	 */
	private long getSelectedDateInMilis(String strDate) throws Exception
 	{
		long Time1=0;
 		int day=0,month=0,year=0,hh=0,mm=0;
 		GregorianCalendar gregorianCalendar=null; 
 		
		try
		{
	 		StringTokenizer st = new StringTokenizer(strDate,"/ :");
	     	
			while(st.hasMoreTokens())
	     	{
	     		day=Integer.parseInt(st.nextToken());
	     		month=Integer.parseInt(st.nextToken());
	     		year=Integer.parseInt(st.nextToken());
	     		if(st.hasMoreTokens()==true)
	     		{
	     			hh=Integer.parseInt(st.nextToken());
	     			mm=Integer.parseInt(st.nextToken());	
	     		}	
	     	}
			
			month--;
			
	     	if(hh==0 && mm==0)
	     	{
	     		gregorianCalendar = new GregorianCalendar(year,month,day);
	     	}
	     	else
	     		gregorianCalendar = new GregorianCalendar(year,month,day,hh,mm);
	     	
	     	Time1=gregorianCalendar.getTimeInMillis();
	     	
	     	year=0;
	  	    month=0;
	 	    day=0;
	 	    hh=0;
	 	    mm=0;
		}
		catch(Exception exception)
		{
			ExceptionLogger.error(exception);
			throw exception;
		}
		finally
		{
			gregorianCalendar=null; 
		}
 		
 	    return Time1;
 	}
	
	/**
	 * This method converts date time value from milliseconds to appropriate date and time format.
	 * @param millis
	 * @return Date and Time value in string format.
	 * @throws Exception
	 */
	private String convertToDateTime(long millis) throws Exception
 	{
		String date = null;
		GregorianCalendar gregorianCalendar=null; 
		
		try
		{
		 	gregorianCalendar = new GregorianCalendar();
		 	gregorianCalendar.setTimeInMillis(millis);
		 	 
		 	int day=-1;
		 	int month=-1;
		 	int year=-1;
		 	int hour=-1;
		 	int min=-1;
		 	
		 	String strDay = "";
		 	String strMonth = "";
		 	String strHour = "";
		 	String strMin = "";
		 	
		 	day = gregorianCalendar.get(Calendar.DATE);
		 	month = gregorianCalendar.get(Calendar.MONTH);
		 	year = gregorianCalendar.get(Calendar.YEAR);
		 	hour=gregorianCalendar.get(Calendar.HOUR_OF_DAY);
		 	min=gregorianCalendar.get(Calendar.MINUTE);
		 	strDay = getString(day+"");
		 	strMonth = getString((month+1)+"");
		 	strHour = getString(hour+"");
		 	strMin = getString(min+"");
		 	
		 	 if(hour!=-1 && min!=-1)
		 	 {
		 	 	date = strDay+"/"+strMonth+"/"+year+" "+strHour+":"+strMin;
		 	 }
		 	 else
		 	 {
		 	 	date = strDay+"/"+strMonth+"/"+year;
		 	 }
		 	 
		 	 day=-1;
		 	 month=-1;
		 	 year=-1;
		 	 hour=-1;
		 	 min=-1;
		}
		catch(Exception exception)
		{
			ExceptionLogger.error(exception);
			throw exception;
		}
		finally
		{
			gregorianCalendar=null; 
		}
		return (date);
 	}
	
	/**
	 * This method appends '0' to the string parameter and returns modified string 
	 * @param str
	 * @return modified string
	 */
	private String getString(String str)
 	{
 		try
 		{
 			if(str.length() == 1)
 			{
 				str = "0" + str;
 			}
 		}
 		catch(Exception exception)
 		{
 			ExceptionLogger.error(exception);
 		}
 		finally
 		{
 			
 		}
 		return str;
 	}
	
	/**
	 * This method creates SLA report file at given location 
	 * @param strReportsDirectoryName
	 * @param lstReportData
	 * @param strStartDate
	 * @param strStartTime
	 * @param strEndDate
	 * @param strEndTime
	 * @return true or false
	 */
	
	/**
	 * This method creates directory for SLA and History reports files.
	 * @param strStartDate
	 * @param strStartTime
	 * @param strEndDate
	 * @param strEndTime
	 * @return path where SLA and History reports will be created.
	 */
	
	
	/**
	 * This method creates and returns database connection.
	 * @return database connection object
	 */
	private Connection createConnection()
	{
		loadProperties();
		Connection connection = null;
		try
		{
			Driver driver=(Driver)Class.forName(strDriverName).newInstance();
			DriverManager.registerDriver(driver);
			
			connection = DriverManager.getConnection(strDBUrl, strUserName, strPassword);
		}
		catch(Exception exception)
		{
			System.out.println(exception);
			System.out.println(ErrorMessages.UNABLE_TO_ESTABLISH_CONNECTION);
			
			ExceptionLogger.error(exception);	
		}
		return connection;
	}

	public LinkedHashMap<String, ArrayList<String>> compareCustomerMaster(LinkedHashMap<String, ArrayList<String>> hmCSData) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		Connection connection = null;
		connection = createConnection();
		String customerMaster=null;
		String cust_name=null;
		String strLoadID=null;
		ArrayList<String> arrCustomermaster=new ArrayList<String>();
		ArrayList<String> arrMTopsmaster=new ArrayList<String>();

		LinkedHashMap<String, ArrayList<String>> hmCSData1=new  LinkedHashMap<String, ArrayList<String>>();
		LinkedHashMap<String, ArrayList<String>> hmCSData2=new  LinkedHashMap<String, ArrayList<String>>();
		pstmt=connection.prepareStatement("select distinct cust_name from customer_master");
		resultSet=pstmt.executeQuery();
		ArrayList<String> lsManagedList =null;
		while(resultSet.next())
		{
			customerMaster=resultSet.getString("cust_name").trim();
			arrCustomermaster.add(customerMaster);
			
		}
		pstmt=connection.prepareStatement("select distinct cust_name from MT_OPS_CustomerMaster");
		resultSet=pstmt.executeQuery();
		ArrayList<String> lsManagedList1 =null;
		while(resultSet.next())
		{
			customerMaster=resultSet.getString("cust_name").trim();
			arrMTopsmaster.add(customerMaster);
			
		}
		//System.out.println(arrCustomermaster);
		
		for(Map.Entry<String, ArrayList<String>> entry1: hmCSData.entrySet())
		{
			lsManagedList = entry1.getValue();	
			
		    strLoadID=lsManagedList.get(4).trim();
			strLoadID=strLoadID.replace("\n", "");
	
			cust_name=lsManagedList.get(6).trim();
			cust_name=cust_name.replace("\n", "");
		
		if(!arrCustomermaster.contains(cust_name))
		{
			hmCSData1.put(strLoadID, lsManagedList);
			continue;
		
		}
		}
		//System.out.println("hmCSData1"+hmCSData1);
		//System.out.println("hmCSData1"+hmCSData1.size());
		for(Map.Entry<String, ArrayList<String>> entry1: hmCSData1.entrySet())
		{
			lsManagedList = entry1.getValue();	
			
		    strLoadID=lsManagedList.get(4).trim();
			strLoadID=strLoadID.replace("\n", "");
	
			cust_name=lsManagedList.get(6).trim();
			cust_name=cust_name.replace("\n", "");
			//System.out.println(cust_name);
		
		if(arrMTopsmaster.contains(cust_name))
		{
			hmCSData2.put(strLoadID, lsManagedList);
			continue;
		
		}
		}
		//System.out.println("hmCSData2"+hmCSData2);
		//System.out.println("hmCSData2"+hmCSData2.size());
		return hmCSData1;
		
	}
	/**
	 * This method returns case type for given case id.
	 * @param stringCaseNumber
	 * @param connection
	 * @return case type
	 */
	

	public LinkedHashMap<String, ArrayList<String>> compareTodayDate(LinkedHashMap<String, ArrayList<String>> hmCSData1) {
		LinkedHashMap<String, ArrayList<String>> hmCSData2=new  LinkedHashMap<String, ArrayList<String>>();
		ArrayList<String> lsManagedList =null;
		String pickUpDate=null;
		String originalDiliveryDate=null;
		String priorToDelivery=null;
		String locationType=null;
		String strLoadID=null;
		//Find todays date
		 String pattern = "dd/MM/yyyy";
		String todaysDate =new SimpleDateFormat(pattern).format(new Date());
		//System.out.println(todaysDate);
		
		
		for(Map.Entry<String, ArrayList<String>> entry1: hmCSData1.entrySet())
		{
			lsManagedList = entry1.getValue();	
			
			strLoadID=lsManagedList.get(4).trim();
			strLoadID=strLoadID.replace("\n", "");
			
			pickUpDate=lsManagedList.get(0).trim();
			pickUpDate=pickUpDate.replace("\n", "");
	
			originalDiliveryDate=lsManagedList.get(2).trim();
			originalDiliveryDate=originalDiliveryDate.replace("\n", "");
			
			priorToDelivery=lsManagedList.get(47).trim();
			priorToDelivery=priorToDelivery.replace("\n", "");
			
			locationType=lsManagedList.get(48).trim();
			locationType=locationType.replace("\n", "");
		
		if(!(pickUpDate.contains(todaysDate) && originalDiliveryDate.contains(todaysDate) && priorToDelivery.equalsIgnoreCase("TRUE") && locationType.equalsIgnoreCase("Residential")) )
		{
			hmCSData2.put(strLoadID, lsManagedList);
			continue;
		
		}
		}
		//System.out.println("hmCSData2="+hmCSData2);
		return hmCSData2;
	}

	public LinkedHashMap<String, ArrayList<String>> compareCaseCreated(LinkedHashMap<String, ArrayList<String>> hmCSData1) {
		LinkedHashMap<String, ArrayList<String>> hmCSData3=new  LinkedHashMap<String, ArrayList<String>>();
		ArrayList<String> lsManagedList =null;
		String caseCreated=null;
		
		String strLoadID=null;
		
		for(Map.Entry<String, ArrayList<String>> entry1: hmCSData1.entrySet())
		{
			lsManagedList = entry1.getValue();	
			
			strLoadID=lsManagedList.get(4).trim();
			strLoadID=strLoadID.replace("\n", "");
			
			caseCreated=lsManagedList.get(18).trim();
			caseCreated=caseCreated.replace("\n", "");
			
		if(!(caseCreated.equals("Open")) )
		{
			hmCSData3.put(strLoadID, lsManagedList);
			continue;
		
		}
		}
		return hmCSData3;
	}

	public LinkedHashMap<String, ArrayList<String>> compareOwnerName(LinkedHashMap<String, ArrayList<String>> hmCSData1) {

		LinkedHashMap<String, ArrayList<String>> hmCSData4=new  LinkedHashMap<String, ArrayList<String>>();
		ArrayList<String> lsManagedList =null;
		String ownerName=null;
		String notesCreatedDate=null;
		String notes=null;
		String strLoadID=null;
		//Find todays date
		 String pattern = "M/d/yyyy";
		 String pattern2 = "d/MM/yyyy";
		String todaysDate =new SimpleDateFormat(pattern).format(new Date());
		String todaysDate2 =new SimpleDateFormat(pattern2).format(new Date());
		//System.out.println(todaysDate);
		//System.out.println(todaysDate2);
		String notesStr="EDI Status: Estimated Delivery (";
		notesStr=notesStr.concat(todaysDate);
		//System.out.println(notesStr);
		
		for(Map.Entry<String, ArrayList<String>> entry1: hmCSData1.entrySet())
		{
			lsManagedList = entry1.getValue();	
			
			strLoadID=lsManagedList.get(4).trim();
			strLoadID=strLoadID.replace("\n", "");
			
			ownerName=lsManagedList.get(14).trim();
			ownerName=ownerName.replace("\n", "");
			//System.out.println("ownerName="+ownerName);
			notesCreatedDate=lsManagedList.get(16).trim();
			notesCreatedDate=notesCreatedDate.replace("\n", "");
			//System.out.println("notesCreatedDate="+notesCreatedDate);
			
			notes=lsManagedList.get(15).trim();
			notes=notes.replace("\n", "");
			//System.out.println("notes="+notes);
		
		if(!(ownerName.equals("EDI User") && notesCreatedDate.contains(todaysDate2) && (notes.equalsIgnoreCase("EDI Status: Available For Delivery") || notes.equalsIgnoreCase("EDI Status: Arrived at Delivery Location") || notes.contains(notesStr))) )
		{
			hmCSData4.put(strLoadID, lsManagedList);
			continue;
		
		}
		}
		//System.out.println("hmCSData4="+hmCSData4);
		return hmCSData4;
	}
	
	//Filter Step 11
	
		LinkedHashMap<String, ArrayList<String>> filterPickupByDate(LinkedHashMap<String, ArrayList<String>> hmCSData1) throws ParseException {
			// TODO Auto-generated method stub
			LinkedHashMap<String, ArrayList<String>> hmCSData2=new  LinkedHashMap<String, ArrayList<String>>();
			ArrayList<String> lsManagedList =null;
			ArrayList<String> holiday =null;
			String strLoadID=null;
			String pickUpDate=null;
			String DeliverybyDate=null;
			String Notes=null;
			String SrNO=null;
			String businessDay=null;
			String result1=null;		
			//Find todays date
			 String pattern = "dd/MM/yyyy";
			 int iteration=0;
			 String todaysDate =new SimpleDateFormat(pattern).format(new Date());
			 String holidayList="D:\\EchoTntExcelFilter\\ExcelFiltering_24_March\\Input\\Holidaylist.xlsx";
			 //Todays Date
			  SimpleDateFormat format1=new SimpleDateFormat("dd/MM/yyyy");
			  Date dt1=format1.parse(todaysDate);
			  int month=dt1.getMonth();
			  String businessday=null;
			  //GEt yesterday
			  Date yesterday = new Date(dt1.getTime() - 2);
			  String  result = format1.format(yesterday);
			  Date yesterday1=format1.parse(result);
			  LinkedHashMap<String, ArrayList<String>> hmHolidaylist=	HolidayList(holidayList,month);
			 //System.out.println("count "+count);
			 System.out.println("iteration "+iteration);
			 if(count>0) {
				 iteration=count;
			  while(iteration>0) {
				 
				  if(businessday!=null){
					  Date businessdt=format1.parse(businessday);
					 // String  result = format1.format(yesterday);
					  businessday=  lastBusinessDay(businessdt, businessday);
				  }else {
				  businessday=  lastBusinessDay(yesterday1, result);
				  }
				  iteration--;
				 }
			 }else {
				 businessday=  lastBusinessDay(yesterday1, result);
			 }
			  count=0;
			  System.out.println("Final business day "+businessday);
			  for(Map.Entry<String, ArrayList<String>> entry1: hmCSData1.entrySet())
				{
					lsManagedList = entry1.getValue();	
					
					strLoadID=lsManagedList.get(4).trim();
					strLoadID=strLoadID.replace("\n", "");
					
					pickUpDate=lsManagedList.get(0).trim();
					pickUpDate=pickUpDate.replace("\n", "");
					////System.out.println("Pickup date "+pickUpDate);
			
					DeliverybyDate=lsManagedList.get(3).trim();
					DeliverybyDate=DeliverybyDate.replace("\n", "");
					
					Notes=lsManagedList.get(15).trim();
					Notes=Notes.replace("\n", "");
				
				if(!(pickUpDate.contains(businessday) && DeliverybyDate.contains(todaysDate) && (Notes.equalsIgnoreCase("EDI Status: Departed Terminal Location") ||Notes.equalsIgnoreCase("EDI Status: Arrived at Terminal Location") ||Notes.equalsIgnoreCase("EDI Status:En-route to Delivery Location"))) )
				{
					hmCSData2.put(strLoadID, lsManagedList);
					continue;
				
				}
				}
			return hmCSData2;
		}
		//HolidayList

				@SuppressWarnings("deprecation")
				public LinkedHashMap<String, ArrayList<String>> HolidayList(String holidayList, int month) {
					// TODO Auto-generated method stub
					   
					    LinkedHashMap<String, ArrayList<String>> hmHolidaylist=new  LinkedHashMap<String, ArrayList<String>>();
						SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
						SimpleDateFormat formatterDDMMYY = new SimpleDateFormat("dd/MM/yyyy");
						 
						String ext = holidayList.substring(holidayList.lastIndexOf('.') + 1);
							
						try
						 {
						 int fileRowCount=4;
						 
						 FileInputStream file1 = new FileInputStream(new File(holidayList));
						System.out.println(holidayList);
						 //Create Workbook instance holding reference to .xls file
						 XSSFWorkbook workbook = new XSSFWorkbook(file1);
						
						 //Get first/desired sheet from the workbook
						 XSSFSheet sheet = workbook.getSheetAt(0);
						
						 int indexRow = 0;
						 for(Row row : sheet )
						 {
								ArrayList<String> listRowData =  new ArrayList<String>();
								
								
						 	String Srno=new String();
						 	if(indexRow == 0 )
								{
						 		indexRow++;
									continue;
								}
						 	
						 	for(int i = 0; i < fileRowCount; i++) 
						 	{

							    if (i == 0) 
							   {
								    row.getCell(i).setCellType(Cell.CELL_TYPE_STRING);
								   Srno = row.getCell(i).getStringCellValue();
							   }
							  else if(i ==1 )
							  {
								   if((row.getCell(i).getDateCellValue()) != null)
							       {
									   
									     Date dt1=formatterDDMMYY.parse(formatterDDMMYY.format(row.getCell(i).getDateCellValue()));
									     int holidayMonth=dt1.getMonth();
									     //System.out.println("dt1 in holidays list and month "+dt1+" month"+holidayMonth);
		            		        	 listRowData.add(formatterDDMMYY.format(row.getCell(i).getDateCellValue())); 
		            		        	
		            		        	 if(month==holidayMonth) {
		            		        		 count++;
		            		        	 }
		            		         
		        			      }
								   else {
									  listRowData.add("");
								   }
						
							  }
						 	}
						 	hmHolidaylist.put(Srno, listRowData);
						 	
						 }
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
					     
					        
					
					return hmHolidaylist;
				  }
				
				
				@SuppressWarnings("deprecation")
				public String lastBusinessDay(Date yesterday1,String result) throws ParseException{
					String holidayList="D:\\EchoTntExcelFilter\\ExcelFiltering_24_March\\Input\\Holidaylist.xlsx";
					ArrayList<String> holiday =null;
					String businessDay=null;
					String result1=null;
					SimpleDateFormat format1=new SimpleDateFormat("dd-MMM-yyyy");
					//Todays Date
					  String pattern = "dd-MMM-yyyy";
					  String todaysDate =new SimpleDateFormat(pattern).format(new Date());
					  Date dt1=format1.parse(todaysDate);
					  int month=dt1.getMonth();
					  count=0;
					    LinkedHashMap<String, ArrayList<String>> hmHolidaylist=	HolidayList(holidayList,month);
						//System.out.println(hmHolidaylist);
						
						for(Map.Entry<String, ArrayList<String>> entry1: hmHolidaylist.entrySet())
						{
							holiday = entry1.getValue();
							////System.out.println("Holiday date "+holiday);
							boolean ans = holiday.contains(result);
							
							if(ans) {
		                      //  //System.out.println("Holiday present");
								//if yesterday is holiday then one more minus day GEt yesterday day
								  Date dayBeforeHoliday = new Date(yesterday1.getTime() - 2);
								  result1 = format1.format(dayBeforeHoliday);
								  Date dayBeforeHoliday1=format1.parse(result1);
								  //System.out.println("day before holiday "+result1);
						
								//get day
								  DateFormat format2=new SimpleDateFormat("EEEE"); 
								  String finalDay=format2.format(dayBeforeHoliday);
								 //System.out.println("Day dayBeforeHoliday "+finalDay);
								 
								  if(finalDay.equalsIgnoreCase("Saturday")||finalDay.equalsIgnoreCase("sunday")) {
									 
									  if(finalDay.equalsIgnoreCase("saturday")) {
										  //if dayBeforeHoliday is Saturday then  2 minus days as business day
										  Date day1 = new Date(dayBeforeHoliday1.getTime() - 4);
										   businessDay = format1.format(day1);
										  ////System.out.println("day before holiday "+businessDay);
										  
									  }else if(finalDay.equalsIgnoreCase("sunday")) {
										  
										//if dayBeforeHoliday is Sunday then get 1 minus days as business day
										  Date oneDayBefore3 = new Date(dayBeforeHoliday1.getTime() - 2);
										   businessDay = format1.format(oneDayBefore3);
										 // //System.out.println("day before holiday "+businessDay);
										  
									  }
									  
								  }else {
									  businessDay=result1;
								  }
								 break; 
							   }else {
								
								//if yesterday is not holiday then get day
								  
						        //get day
								  DateFormat format2=new SimpleDateFormat("EEEE"); 
								  String finalDay=format2.format(yesterday1);
								 // //System.out.println("Day dayBeforeHoliday "+finalDay);
								 
								  if(finalDay.equalsIgnoreCase("Saturday")||finalDay.equalsIgnoreCase("sunday")) {
									 
									  if(finalDay.equalsIgnoreCase("saturday")) {
										  //if dayBeforeHoliday is Saturday then  2 minus days as business day
										  Date day1 = new Date(yesterday1.getTime() - 4);
										   businessDay = format1.format(day1);
										  ////System.out.println("day before holiday "+businessDay);
										  
									  }else if(finalDay.equalsIgnoreCase("sunday")) {
										  
										//if dayBeforeHoliday is Sunday then get 1 minus days as business day
										  Date oneDayBefore3 = new Date(yesterday1.getTime() - 2);
										   businessDay = format1.format(oneDayBefore3);
										  //System.out.println("day before holiday "+businessDay);
										  
									  }
									  
								  }else {
									  businessDay=result;
								  }
							}
							
						}		  
					  //System.out.println("Last business day is "+businessDay);
					  return businessDay;
					
					
				}
				

				/**
				 * @param LinkedHashMap
				 * @return LinkedHashMap
				 * 
				 *         This method takes LinkedHashmap as argument then apply filter on it
				 *         and return the same linkedhashmap. Removing records with 'parent
				 *         company' as 'Novozymes North America' and pick up date is not equal
				 *         to today.
				 **/
				public LinkedHashMap<String, ArrayList<String>> filterForNuvozymes_AndPastPickUpDate(LinkedHashMap<String, ArrayList<String>> hmCSData_fcco) {

					String pattern = "dd/MM/yyyy";
					String todaysDate = new SimpleDateFormat(pattern).format(new Date());

					Iterator<Map.Entry<String, ArrayList<String>>> itr = hmCSData_fcco.entrySet().iterator();

					/** iterating over hashmap **/
					while (itr.hasNext()) {
						ArrayList<String> row = (ArrayList<String>) itr.next().getValue();

						if (row.get(7).trim().equalsIgnoreCase("Novozymes North America")
								&& !row.get(0).trim().equals(todaysDate)) {
							// System.out.println(row.get(0)+"=="+todaysDate+" "+row.get(4));

							/**
							 * Removing Novozymes North America from parent company with past Pickup By
							 * Date.
							 **/
							itr.remove();
						}
					}
					return hmCSData_fcco;
				}
				
				/** This methods Removs loads Samsung c/o Neovia Logistics from parent company with "Pickup By Date" from last 3 business days **/
				public LinkedHashMap<String, ArrayList<String>> filterForSamsungNeoviaLogistics(LinkedHashMap<String, ArrayList<String>> hmCSData_nuvozymes) throws IOException {

					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

					Iterator<Map.Entry<String, ArrayList<String>>> itr = hmCSData_nuvozymes.entrySet().iterator();
					List<LocalDate> bizDays = getLast3BusinessDays();
							System.out.println("bizdays=>"+bizDays);

					/** iterating over hashmap **/
					while (itr.hasNext()) {
						ArrayList<String> row = (ArrayList<String>) itr.next().getValue();

						if (row.get(7).trim().equals("Samsung c/o Neovia Logistics")) {

							/** Removing loads Samsung c/o Neovia Logistics from parent company with "Pickup By Date" from last 3 business days **/
							if (bizDays.get(0).format(formatter).equals(row.get(0))
									|| bizDays.get(1).format(formatter).equals(row.get(0))
									|| bizDays.get(2).format(formatter).equals(row.get(0))) {

								/** Removing Samsung c/o Neovia Logistics from parent company **/
								itr.remove();
								continue;
							}

							LocalDate todaysDate  = LocalDate.now();
							DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");
							LocalDate deleveryByDate= LocalDate.parse(row.get(3), dtf);

							/** Removing loads Samsung c/o Neovia Logistics from parent company with "Delivery By Date" is ahead from today **/
							if(deleveryByDate.isAfter(todaysDate)) {
								itr.remove();
							}

						}
					}
                    //System.out.println("hmCSData_nuvozymes="+hmCSData_nuvozymes);
					return hmCSData_nuvozymes;
				}
				/** holidays are inluded 
				 * @throws IOException **/
				public List<LocalDate> getLast3BusinessDays() throws IOException {

					/***getting holiday list from master holiday file***/

					String strFileName="D:\\EchoTntExcelFilter\\ExcelFiltering_24_March\\Input\\Holidaylist.xlsx";
					FileInputStream file1 = new FileInputStream(new File(strFileName));
					XSSFSheet sheet = new XSSFWorkbook(file1).getSheetAt(0);
					int rowCount = sheet.getPhysicalNumberOfRows();		
					ArrayList<LocalDate> holidays = new ArrayList<LocalDate>();
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

					for(int i=1; i<rowCount; i++) {
						LocalDate l = LocalDate.parse(sheet.getRow(i).getCell(1).toString(),dtf);
						holidays.add(l);
					}

					/***getting last three working days excluding sunday and saturday***/
					LocalDate today = LocalDate.now();
					List<LocalDate> ListOf3WorkingDays = new ArrayList<LocalDate>();

					int i = 1;
					while (i <= 7) {
						LocalDate dateToCompare = today.minusDays(i);
						if (!dateToCompare.getDayOfWeek().equals(DayOfWeek.SUNDAY)
								&& !dateToCompare.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
							if(!holidays.contains(dateToCompare)) {
								ListOf3WorkingDays.add(dateToCompare);
							}
						}
						i++;
					}

					return ListOf3WorkingDays;
				}
				
				 /*** Removing all actual carriers records which are present in master carrier file and also H&H Express Carrier with pickup by date of last three business days.
				* also all records has blank actual carrier***/
				public LinkedHashMap<String, ArrayList<String>> filterForRemovingCarriers(LinkedHashMap<String, ArrayList<String>> hmCSData_samsung) throws IOException {
					System.out.println("-------------------------------------------");
	
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					List<LocalDate> bizDays = getLast3BusinessDays();// last three biz days excluding holidays
	
					/**creating the list of all the carriers needs to be removed from db master ***/
					List<String> carriersToRemovedb= getListOfActualCarriersFromMaster();
	//					carriersToRemovedb.forEach(System.out::println);
	
					/**iterating over previous filterd hashmap**/
					Iterator<Map.Entry<String, ArrayList<String>>> itr21 = hmCSData_samsung.entrySet().iterator();
					while(itr21.hasNext()){
					ArrayList<String> row = (ArrayList<String>) itr21.next().getValue();
	
					/***removing  Carriers from master carriers file**/
					if(carriersToRemovedb.contains(row.get(8).trim())) {
	//					System.out.println("carriers will be removes--"+row.get(8));
					itr21.remove();
					}
					/***removing H&H Express Carrier with pickup by date of last three business days**/
					else if(row.get(8).trim().equals("H&H Express")) {
	
					if (bizDays.get(0).format(formatter).equals(row.get(0))
					|| bizDays.get(1).format(formatter).equals(row.get(0))
					|| bizDays.get(2).format(formatter).equals(row.get(0))) {
					itr21.remove();
					}
					}
					/**removing all records having blank in column 'actual carrier'**/
					else if(row.get(8).equals("")) {
	//					System.out.println("blank carriers will be removes--"+row.get(8)+" load=+++>"+row.get(4));
					itr21.remove();
					}
					}
	
					/*
					* Iterator<Map.Entry<String, ArrayList<String>>> itrx =
					* hmCSData_samsung.entrySet().iterator(); while(itrx.hasNext()){
					* ArrayList<String> rowx = (ArrayList<String>) itrx.next().getValue();
					* System.out.println(rowx.get(4)); }
					*/	
					return hmCSData_samsung;
				}
				
				
				public List<String> getListOfActualCarriersFromMaster(){
					PreparedStatement pstmt = null;
					ResultSet resultSet = null;
					Connection connection = null;
					connection = createConnection();
					String carrier = null;
					ArrayList<String> carrier_masterList = new ArrayList<String>();
	
					try {
					pstmt = connection.prepareStatement("select carrier_name from actual_carrier_master");
					resultSet = pstmt.executeQuery();
					while (resultSet.next()) {
					carrier = resultSet.getString("carrier_name").trim();
					carrier_masterList.add(carrier);
					}
	
					} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					}

				return carrier_masterList;
				}
				
				public LinkedHashMap<String, ArrayList<String>> filterManifestCarrier(LinkedHashMap<String, ArrayList<String>> hmCSData1) throws IOException 
				{
					System.out.println("main file count before seperation=>"+hmCSData1.size());
				PreparedStatement pstmt = null;
				ResultSet resultSet = null;
				Connection connection = null;
				connection = createConnection();
				String carrier=null;
				String actualCarrier=null;
				String strLoadID=null;

				ArrayList<String> arrCarrier=new ArrayList<String>();
				ArrayList<String> arrMTopsmaster=new ArrayList<String>();
				
				LinkedHashMap<String, ArrayList<String>> hmCSData2=new  LinkedHashMap<String, ArrayList<String>>();
				LinkedHashMap<String, ArrayList<String>> hmManifestData=new  LinkedHashMap<String, ArrayList<String>>();
				try{
				pstmt=connection.prepareStatement("select distinct carrier from manifest_carrier");
				resultSet=pstmt.executeQuery();
				ArrayList<String> lsManagedList =null;
				while(resultSet.next())
				{
				carrier=resultSet.getString("carrier").trim();
				arrCarrier.add(carrier);

				}


				for(Map.Entry<String, ArrayList<String>> entry1: hmCSData1.entrySet())
				{
				lsManagedList = entry1.getValue();	

				   strLoadID=lsManagedList.get(4).trim();
				strLoadID=strLoadID.replace("\n", "");

				actualCarrier=lsManagedList.get(8).trim();
				actualCarrier=actualCarrier.replace("\n", "").trim();

				if(!arrCarrier.contains(actualCarrier))
				{
				hmCSData2.put(strLoadID, lsManagedList);
				continue;
				}
				else
				{
					hmManifestData.put(strLoadID, lsManagedList);
					continue;
				}
				}
				}
				catch(Exception e){
				e.printStackTrace();
				}
				
				System.out.println("hashmap without mainifest count=>"+hmCSData2.size());
				System.out.println("without manifest count=>"+hmManifestData.size());
				
				manifestCarrierOutputFile(hmManifestData);//calling the method which generates the excel output for manifest carriers
				return hmCSData2;
				}
				public void manifestCarrierOutputFile(LinkedHashMap<String, ArrayList<String>> manifestHashmap) throws IOException  {

					
					  //create blank workbook
					  HSSFWorkbook workbook = new HSSFWorkbook(); 
					 
					  //Create a blank sheet
					  HSSFSheet sheet = workbook.createSheet("ManifestCarriers");
					  
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
					      Set<String> keyid = manifestHashmap.keySet();
					      int rowid = 1;
					      
					      for (String key : keyid) {
					         row1 = sheet.createRow(rowid++);
					         ArrayList<String> objectArr = manifestHashmap.get(key);
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
							   FileOutputStream fileOut = new FileOutputStream(new File("D:\\EchoTntExcelFilter\\ExcelFiltering_24_March\\Output\\ManifestCarriers.xls"));
							    //Write the workbook in file system
							    workbook.write(fileOut);
								fileOut.flush();
								fileOut.close();
						}
					    catch (Exception e) 
					    {
					     e.printStackTrace();
					    }
						
						System.out.println("file is written");
					
				}
		
	/**
	 * This method returns customer name for given case id.
	 * @param stringCaseNumber
	 * @param connection
	 * @return customer name
	 */
	/*private String getCustName(String stringCaseNumber, Connection connection)
 	{
 		String strCustName = null;
		StringBuffer strBufferSelectCustName = null;
		PreparedStatement pstmtCustName = null;
		ResultSet resultSetCustName = null;
		String strFirstName = null;
		String strMiddleName = null;
		String strLastName = null;
		
		try
		{
			if(connection!=null)
			{
				strBufferSelectCustName = new StringBuffer();
				strBufferSelectCustName.append("SELECT fd1.field_3,fd1.field_4,fd1.field_5 FROM field_data AS fd1 WHERE fd1.case_id='");
				strBufferSelectCustName.append(stringCaseNumber);
				strBufferSelectCustName.append("' AND fd1.row_id = (SELECT max(row_id) as rowid FROM field_data WHERE case_id='");
				strBufferSelectCustName.append(stringCaseNumber);
				strBufferSelectCustName.append("') ");
				pstmtCustName = connection.prepareStatement(strBufferSelectCustName.toString());

				resultSetCustName = pstmtCustName.executeQuery();
				
				if(resultSetCustName.next())
				{
					strCustName = "";
					strFirstName = resultSetCustName.getString("field_3").trim();
					strMiddleName = resultSetCustName.getString("field_4").trim();
					strLastName = resultSetCustName.getString("field_5").trim();
					
					if(strFirstName!="" && strFirstName!=null && !(strFirstName.trim().equalsIgnoreCase("NULL")))
					{
						strCustName = strCustName+strFirstName.trim()+" ";
					}
					if(strMiddleName!="" && strMiddleName!=null && !(strMiddleName.trim().equalsIgnoreCase("NULL")))
					{
						strCustName = strCustName+strMiddleName.trim()+" ";
					}
					if(strLastName!="" && strLastName!=null && !(strLastName.trim().equalsIgnoreCase("NULL")))
					{
						strCustName = strCustName+strLastName.trim();
					}
				}
			}
		}
		catch(Exception exception)
		{
			ExceptionLogger.error(exception);
		}
		finally
		{
			strFirstName = null;
			strMiddleName = null;
			strLastName = null;
			strBufferSelectCustName = null;
			try
			{
				if(pstmtCustName != null)
					pstmtCustName.close();
				
				pstmtCustName = null;
				resultSetCustName = null;
			}
			catch(Exception e)
			{
				ExceptionLogger.error(e);
			}
		}
		return strCustName;
 	}*/
	/**
	 * This method returns application number for given case id.
	 * @param stringCaseNumber
	 * @param connection
	 * @return application number
	 */
	/*private String getApplicationNumber(String stringCaseNumber, Connection connection)
 	{
 		String strApplicationNumber = null;
		StringBuffer strBufferSelectAppNo = null;
		PreparedStatement pstmtAppNo = null;
		ResultSet resultSetAppNo = null;
		
		try
		{
			if(connection!=null)
			{
				strBufferSelectAppNo = new StringBuffer();
				strBufferSelectAppNo.append("SELECT fd1.field_6 FROM field_data AS fd1 WHERE fd1.case_id='");
				strBufferSelectAppNo.append(stringCaseNumber);
				strBufferSelectAppNo.append("' AND fd1.row_id = (SELECT max(row_id) AS rowid FROM field_data WHERE case_id='");
				strBufferSelectAppNo.append(stringCaseNumber);
				strBufferSelectAppNo.append("') ");
							
				pstmtAppNo = connection.prepareStatement(strBufferSelectAppNo.toString());
				
				resultSetAppNo = pstmtAppNo.executeQuery();
				
				if(resultSetAppNo.next())
				{
					strApplicationNumber = resultSetAppNo.getString("field_6");
				}
			}
		}catch(Exception exception)
		{
			ExceptionLogger.error(exception);
		}
		finally
		{
			strBufferSelectAppNo = null;
			try
			{
				if(pstmtAppNo != null)
					pstmtAppNo.close();
				
				pstmtAppNo = null;
				resultSetAppNo = null;
			}
			catch(Exception e)
			{
				ExceptionLogger.error(e);
			}
		}
		return strApplicationNumber;
 	}*/
	
	/**
	 * This method returns application id for given case id.
	 * @param stringCaseNumber
	 * @param connection
	 * @return application id
	 */
	/*private String getApplicationID(String stringCaseNumber, Connection connection)
 	{
 		String strApplicationID = null;
		StringBuffer strBufferSelectAppID = null;
		PreparedStatement pstmtAppID = null;
		ResultSet resultSetAppID = null;
		try
		{
			if(connection!=null)
			{
				strBufferSelectAppID = new StringBuffer();
				strBufferSelectAppID.append("SELECT fd1.field_7 FROM field_data AS fd1 WHERE fd1.case_id='");
				strBufferSelectAppID.append(stringCaseNumber);
				strBufferSelectAppID.append("' AND fd1.row_id in (SELECT max(row_id) as rowid FROM field_data WHERE case_id='");
				strBufferSelectAppID.append(stringCaseNumber);
				strBufferSelectAppID.append("') ");
							
				pstmtAppID = connection.prepareStatement(strBufferSelectAppID.toString());
				
				resultSetAppID = pstmtAppID.executeQuery();
				
				if(resultSetAppID.next())
				{
					strApplicationID = resultSetAppID.getString("field_7");
				}
			}
		}catch(Exception exception)
		{
			ExceptionLogger.error(exception);
		}
		finally
		{
			strBufferSelectAppID = null;
			try
			{
				if(pstmtAppID != null)
					pstmtAppID.close();
				
				pstmtAppID = null;
				resultSetAppID = null;
			}
			catch (Exception e) 
			{
				ExceptionLogger.error(e);
			}
		}
		return strApplicationID;
 	}
	*/
	/**
	 * This method returns LAN for given case id.
	 * @param stringCaseNumber
	 * @param connection
	 * @return LAN
	 */
	/*private String getLAN(String stringCaseNumber, Connection connection)
 	{

 		String strLAN = null;
 		
		StringBuffer strBufferSelectLAN = null;
		PreparedStatement pstmtLAN = null;
		ResultSet resultSetLAN = null;
		try
		{
			if(connection!=null)
			{
				strBufferSelectLAN = new StringBuffer();
				strBufferSelectLAN.append("SELECT fd1.field_8 FROM field_data AS fd1 WHERE fd1.case_id='");
				strBufferSelectLAN.append(stringCaseNumber);
				strBufferSelectLAN.append("' AND fd1.row_id in (SELECT max(row_id) as rowid FROM field_data WHERE case_id='");
				strBufferSelectLAN.append(stringCaseNumber);
				strBufferSelectLAN.append("') ");
							
				pstmtLAN = connection.prepareStatement(strBufferSelectLAN.toString());
				
				resultSetLAN = pstmtLAN.executeQuery();
				
				if(resultSetLAN.next())
				{
					strLAN = resultSetLAN.getString("field_8");
				}
			}
		}catch(Exception exception)
		{
			ExceptionLogger.error(exception);
		}
		finally
		{
			strBufferSelectLAN = null;
			
			try
			{
				if(pstmtLAN != null)
					pstmtLAN.close();
				
				pstmtLAN = null;
				resultSetLAN = null;
			}
			catch (Exception e) 
			{
				ExceptionLogger.error(e);
			}
		}
		return strLAN;
 	}
	*/
	/**
	 * This method returns case details from Field_Data table for given case_id.
	 * @param cBean
	 * @param iRowId
	 * @param connection
	 * @return form bean object
	 */
	/*private Case_Transaction_Bean getFieldsData(Case_Transaction_Bean cBean, int iRowId, Connection connection)
 	{

 		StringBuffer strBufferSelectFields = null;
		PreparedStatement pstmtFields = null;
		ResultSet rsFields = null;
		String strDlrASCFinCode = null;		
		String strDlrASCName = null;
		String strFinanceAmount = null;
		String strNetDisbAmount = null;
		String strSAPDocNumber = null;
		String strSAPDocDate = null;
		String strAdvancePaidAmt = null;
		String strFirstName = null;
		String strLastName = null;
		String strMiddleName = null;		
		String strApplicationId = null;
		String strApplicationNo = null;
		String strLAN = null;
		String strCustName = "";
		
		try
		{
			if(connection!=null)
			{
				strBufferSelectFields = new StringBuffer();
				strBufferSelectFields.append("SELECT field_3, field_4, field_5, field_6, field_7, field_8,");
				strBufferSelectFields.append(" field_10, field_11, field_12, field_13, field_14, field_15, field_16");
				strBufferSelectFields.append(" FROM field_data WHERE case_id='");
				strBufferSelectFields.append(cBean.getCaseNumber());
				strBufferSelectFields.append("' AND row_id = ");
				strBufferSelectFields.append(iRowId);
							
				pstmtFields = connection.prepareStatement(strBufferSelectFields.toString());
				
				rsFields = pstmtFields.executeQuery();
				
				if(rsFields.next())
				{
					strFirstName = rsFields.getString("field_3");
					if(strFirstName==null || strFirstName.trim().equalsIgnoreCase("null") || strFirstName.trim().equals(""))
						strFirstName = "";
					
					strMiddleName = rsFields.getString("field_4");
					if(strMiddleName==null || strMiddleName.trim().equalsIgnoreCase("null") || strMiddleName.trim().equals(""))
						strMiddleName = "";
					
					strLastName = rsFields.getString("field_5");
					if(strLastName==null || strLastName.trim().equalsIgnoreCase("null") || strLastName.trim().equals(""))
						strLastName = "";
					
					strApplicationNo = rsFields.getString("field_6");
					if(strApplicationNo==null || strApplicationNo.trim().equalsIgnoreCase("null") || strApplicationNo.trim().equals(""))
						strApplicationNo = "";
					
					strApplicationId = rsFields.getString("field_7");
					if(strApplicationId==null || strApplicationId.trim().equalsIgnoreCase("null") || strApplicationId.trim().equals(""))
						strApplicationId = "";
					
					strLAN = rsFields.getString("field_8");
					if(strLAN==null || strLAN.trim().equalsIgnoreCase("null") || strLAN.trim().equals(""))
						strLAN = "";
					
					strDlrASCFinCode = rsFields.getString("field_10");
					if(strDlrASCFinCode==null || strDlrASCFinCode.trim().equalsIgnoreCase("null") || strDlrASCFinCode.trim().equals(""))
						strDlrASCFinCode = "";
					
					strDlrASCName = rsFields.getString("field_11");
					if(strDlrASCName==null || strDlrASCName.trim().equalsIgnoreCase("null") || strDlrASCName.trim().equals(""))
						strDlrASCName = "";
					
					strFinanceAmount = rsFields.getString("field_12");
					if(strFinanceAmount==null || strFinanceAmount.trim().equalsIgnoreCase("null") || strFinanceAmount.trim().equals(""))
						strFinanceAmount = "";
					
					strNetDisbAmount = rsFields.getString("field_13");
					if(strNetDisbAmount==null || strNetDisbAmount.trim().equalsIgnoreCase("null") || strNetDisbAmount.trim().equals(""))
						strNetDisbAmount = "";
					
					strSAPDocNumber = rsFields.getString("field_14");
					if(strSAPDocNumber==null || strSAPDocNumber.trim().equalsIgnoreCase("null") || strSAPDocNumber.trim().equals(""))
						strSAPDocNumber = "";
					
					strSAPDocDate = rsFields.getString("field_15");
					if(strSAPDocDate==null || strSAPDocDate.trim().equalsIgnoreCase("null") || strSAPDocDate.trim().equals(""))
						strSAPDocDate = "";
					
					strAdvancePaidAmt = rsFields.getString("field_16");
					if(strAdvancePaidAmt==null || strAdvancePaidAmt.trim().equalsIgnoreCase("null") || strAdvancePaidAmt.trim().equals(""))
						strAdvancePaidAmt = "";
					
					
					if(!strFirstName.trim().equals(""))
					{
						strCustName = strFirstName+" ";
					}
					
					if(!strMiddleName.trim().equals(""))
					{
						strCustName =strCustName+strMiddleName+" ";
					}
					
					if(!strLastName.trim().equals(""))
					{
						strCustName =strCustName+strLastName+" ";
					}
					
					
					if(strCustName.trim().equals(""))
					{
						strCustName = "";
					}
					
					
					cBean.setCustName(strCustName);
					cBean.setApplicationNumber(strApplicationNo);
					cBean.setApplicationID(strApplicationId);
					cBean.setLAN(strLAN);
					cBean.setDlrASCFinCode(strDlrASCFinCode);
					cBean.setDlrASCName(strDlrASCName);
					cBean.setFinanceAmount(strFinanceAmount);
					cBean.setNetDisbAmount(strNetDisbAmount);
					cBean.setSAPDocNumber(strSAPDocNumber);
					cBean.setSAPDocDate(strSAPDocDate);
					cBean.setAdvancePaidAmt(strAdvancePaidAmt);					
				}else{
					cBean.setCustName("");
					cBean.setApplicationNumber("");
					cBean.setApplicationID("");
					cBean.setLAN("");
					cBean.setDlrASCFinCode("");
					cBean.setDlrASCName("");
					cBean.setFinanceAmount("");
					cBean.setNetDisbAmount("");
					cBean.setSAPDocNumber("");
					cBean.setSAPDocDate("");
					cBean.setAdvancePaidAmt("");	
				}
			}			
		}catch(Exception exception)
		{
			ExceptionLogger.error(exception);
		}
		finally
		{
			strBufferSelectFields = null;
			strDlrASCFinCode = null;		
			strDlrASCName = null;
			strFinanceAmount = null;
			strNetDisbAmount = null;
			strSAPDocNumber = null;
			strSAPDocDate = null;
			strAdvancePaidAmt = null;	
			strFirstName = null;
			strLastName = null;
			strMiddleName = null;		
			strApplicationId = null;
			strApplicationNo = null;
			strLAN = null;
			
			try
			{
				if(pstmtFields != null)
					pstmtFields.close();
				pstmtFields = null;
				rsFields = null;
			}
			catch (Exception e) 
			{
				ExceptionLogger.error(e);
			}
		}
		return cBean;
 	}*/
	
	/**
	 * This method creates History report for given case ids.
	 * @param strReportsDirectoryName
	 * @param reportData
	 * @param strCaseId
	 * @throws Exception
	 */
	/*private void saveCaseHistoryReport(String strReportsDirectoryName, List reportData, String strCaseId) throws Exception
	{
		String strFileName = null;
		String Delimiter =null;
		String quote = null;
		File reportFile = null;
		PrintWriter printWriter=null;
		FileOutputStream fileOutputStream=null;
		StringBuilder stringBuilder = null;
		
		try
		{
			Delimiter =",";
			quote = "\"";
			strFileName = strCaseId+".csv";
			
			reportFile = new File(strReportsDirectoryName+strFileName);
			fileOutputStream=new FileOutputStream(reportFile);
			printWriter=new PrintWriter(fileOutputStream);
			stringBuilder=new StringBuilder();
			printWriter.println();
			printWriter.println();
			printWriter.println();
			printWriter.println();
			//stringBuilder.append("Case Number"+Delimiter+"Date And Time"+Delimiter+"Case Status"+Delimiter+"Reason"+Delimiter+"Case Type"+Delimiter+"Customer Name"+Delimiter+"Application Number"+Delimiter+"Application ID"+Delimiter+"LAN"+Delimiter+"Dealer/ASC Finnone Code"+Delimiter+"Dealer/ASC Name"+Delimiter+"Finance Amount"+Delimiter+"Net Disb. Amount"+Delimiter+"SAP Ref Doc Number"+Delimiter+"SAP Ref Doc Date"+Delimiter+"Advance Paid Amt"+Delimiter+"Login Name\r\n");
			stringBuilder.append("Case ID"+Delimiter+"Org Name"+Delimiter+"Case Type"+Delimiter+"Process Time "+Delimiter+"Status"+Delimiter+"Stage"+Delimiter+"Reason"+Delimiter+"User Name"+Delimiter+"Flag"+Delimiter+"Remarks"+Delimiter+"Prospect Number"+Delimiter+"Applicant Name"+Delimiter+"Agreement Number"+Delimiter+"Employee Code"+Delimiter+"Employee Name"+Delimiter+"Agreement\r\n");
			printWriter.write(stringBuilder.toString());
			printWriter.flush();
			stringBuilder.delete(0, stringBuilder.length());
			
			if(reportData!= null && reportData.size()>0)
			{
				for(int iIndex=0; iIndex < reportData.size(); iIndex++)
				{
					Case_Transaction_Bean dbAccessActionForm = (Case_Transaction_Bean)reportData.get(iIndex);
					
					stringBuilder.append(" "+dbAccessActionForm.getWorkItemId()+Delimiter);
					stringBuilder.append(quote+dbAccessActionForm.getOrgName()+quote+Delimiter);
					stringBuilder.append(quote+dbAccessActionForm.getCaseType()+quote+Delimiter);
					stringBuilder.append(quote+dbAccessActionForm.getProcessTime()+quote+Delimiter);
					stringBuilder.append(quote+dbAccessActionForm.getStatus()+quote+Delimiter);
					stringBuilder.append(quote+dbAccessActionForm.getStage()+quote+Delimiter);
					stringBuilder.append(quote+dbAccessActionForm.getReason()+quote+Delimiter);
					stringBuilder.append(quote+dbAccessActionForm.getUserName()+quote+Delimiter);
					stringBuilder.append(quote+dbAccessActionForm.getFlag()+quote+Delimiter);
					stringBuilder.append(quote+dbAccessActionForm.getRemarks()+quote+Delimiter);
					stringBuilder.append(quote+dbAccessActionForm.getProspectNumber()+quote+Delimiter);
					stringBuilder.append(quote+dbAccessActionForm.getApplicantName()+quote+Delimiter);
					stringBuilder.append(quote+dbAccessActionForm.getAgreementNumber()+quote+Delimiter);
					stringBuilder.append(quote+dbAccessActionForm.getEmployeeCode()+quote+Delimiter);
					stringBuilder.append(quote+dbAccessActionForm.getEmployeeName()+quote+Delimiter);
					stringBuilder.append(quote+dbAccessActionForm.getAgreement()+quote);
					printWriter.write(stringBuilder.toString());
					printWriter.println();
					printWriter.flush();
					stringBuilder.delete(0, stringBuilder.length());
				}
			}
		}
		catch(Exception exception)
		{
			throw exception;
		}
		finally
		{
			try
			{
				fileOutputStream.close();
				printWriter.close();
				
				strFileName = null;
				Delimiter =null;
				quote = null;
				reportFile = null;
				printWriter=null;
				fileOutputStream=null;
				stringBuilder = null;
			}
			catch(IOException ioException)
			{
				throw ioException;
			}
			catch(Exception exception)
			{
				throw exception;
			}
		}
	}*/
}
