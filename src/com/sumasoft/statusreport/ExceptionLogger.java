package com.sumasoft.statusreport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import com.sumasoft.statusreport.ErrorMessages;

public class ExceptionLogger 
{

	/**
	*	Get the current date to create log file as 'yyyy-mm-dd.log'.
	*
	*	@param Nothing
	*	@return String : log file for today date 
	*/
	public static String getLogDate()
	{
		String strReturn="";
		strReturn=getCurrentDate();
		strReturn=strReturn+".log";
		return strReturn;
	}
	
	/**
	*	Writes the formatted message to cyclic error log file.
	*
	*	@param Object classObj - class that has requested to log the message.
	*	@param	String strBatch - the batch id.
	*	@param	String strMethod - the class method that has requested to write the message.
	*	@param String strMessage - the message to write in log file.
	*	@return Nothing
	*/
	public static void error(Exception exception)
	{
		File file=null;
		FileOutputStream fileOutputStream=null;
		PrintWriter printWriter=null;
		String strFile="DataDeletion_";
		StringBuffer strBuffer=null;
		Date date=null;
		ResourceBundle resourceBundle = null;
		
		try
		{
			resourceBundle = ResourceBundle.getBundle("DBAccess");
			
			//get the system date
			date=new Date();
			
			//get the name of log file , in which the exception is to be logged
			strFile=strFile+getLogDate();
			
			//see if the log folder exists 
			//if the folder does not exist then make the folder
			file = new File(resourceBundle.getString("LOG_DIRECTORY_ERROR"));
			if(!file.exists())
				file.mkdirs();
			
			file=null;
			//the log file
			file=new File(resourceBundle.getString("LOG_DIRECTORY_ERROR") + strFile);
			//if the file exists 
			if(file.exists())
			{
				//get the last modified time of the log file
				//if the logfile is older than 2 days from todays date then 
				//delete the file
				if((file.lastModified())<(date.getTime()-(48*60*60*1000)))
				{
					file.delete();
				}
			}
			fileOutputStream=new FileOutputStream(resourceBundle.getString("LOG_DIRECTORY_ERROR") + strFile,true);
			printWriter=new PrintWriter(fileOutputStream);
			
			//string buffer to construct the exception description
			strBuffer=new StringBuffer();
			
			strBuffer.append(getTimeNow());
			strBuffer.append(" : ");
			strBuffer.append("ERROR");
			strBuffer.append(" : ");
			printWriter.write(strBuffer.toString());
			printWriter.flush();
			exception.printStackTrace(printWriter);
			printWriter.write(" ");
			printWriter.write(System.getProperty("line.separator"));
			printWriter.flush();
		}catch(Exception e){
			System.out.println(ErrorMessages.UNBALE_TO_WRITE_LOG);
			e.printStackTrace();
		}finally{
			try
			{
				fileOutputStream.close();
				printWriter.close();
			}catch(Exception ex){
				System.out.println(ErrorMessages.UNBALE_TO_WRITE_LOG);
				ex.printStackTrace();
			}
			file=null;
			fileOutputStream=null;
			printWriter=null;
			strFile=null;
			strBuffer=null;
			date=null;
		}
	}

	/**
	*	Writes the formatted message to cyclic service_log file.
	*
	*	@param	String strBatch - the batch id.
	*	@param String strMessage - the message to write in log file.
	*	@return Nothing
	*/
	public static void info(String strMessage)
	{
		File file=null;
		FileOutputStream fileOutputStream=null;
		PrintWriter printWriter=null;
		String strFile="DataDeletion_";
		StringBuffer strBuffer=null;
		Date date=null;
		ResourceBundle resourceBundle = null;
		try
		{
			resourceBundle = ResourceBundle.getBundle("DBAccess");
			
			//get the system date
			date=new Date();
			
			//get the name of log file , in which the exception is to be logged
			strFile=strFile+getLogDate();
			
			//see if the log folder exists 
			//if the folder does not exist then make the folder
			file=new File(resourceBundle.getString("LOG_DIRECTORY_INFO"));
			
			if(!file.exists())
				file.mkdirs();
			
			file=null;
			file=new File(resourceBundle.getString("LOG_DIRECTORY_INFO") + strFile);
		
			if(file.exists())
			{
				//get the last modified time of the log file
				//if the logfile is older than 2 days from todays date then 
				//delete the file
				if((file.lastModified())<(date.getTime()-(48*60*60*1000)))
				{
					file.delete();
				}
			}
			fileOutputStream=new FileOutputStream(resourceBundle.getString("LOG_DIRECTORY_INFO") + strFile,true);
			printWriter=new PrintWriter(fileOutputStream);
			
			strBuffer=new StringBuffer();
			
			strBuffer.append(getTimeNow());
			strBuffer.append(" : ");
			strBuffer.append("INFO");
			strBuffer.append(" : ");

			strBuffer.append(strMessage);
			strBuffer.append(System.getProperty("line.separator"));
			printWriter.write(strBuffer.toString());
			printWriter.flush();
		}
		catch(Exception exception)
		{
			//System.out.println(ErrorMessages.UNBALE_TO_WRITE_LOG);
			exception.printStackTrace();
		}
		finally
		{
			try
			{
				fileOutputStream.close();
				printWriter.close();
			}
			catch(Exception exception)
			{
				System.out.println(ErrorMessages.UNBALE_TO_WRITE_LOG);
				exception.printStackTrace();
			}
			file=null;
			fileOutputStream=null;
			printWriter=null;
			strFile=null;
			strBuffer=null;
			date=null;
		}
	}
	
	/**
	*	Returns the current date and time in 'MMM dd HH-mm-ss' format.
	*	@param Nothing
	*	@return String currentDateTime - current date time in 'MMM dd HH-mm-ss' format.
	*/
	private static String getTimeNow()
	{
		String strReturn="";
		Date date=null;
		SimpleDateFormat sdf=null;
		try
		{
			date=new Date();
			sdf=new SimpleDateFormat("MMM dd HH-mm-ss");
			strReturn=sdf.format(date);
		}
		catch(Exception exception)
		{
			//System.out.println(ErrorMessages.UNBALE_TO_WRITE_LOG);
			exception.printStackTrace();
		}
		finally
		{
			date=null;
			sdf=null;
		}
		return strReturn;
	}
	
	/**
	*  Returns the current date in yyyy-mm-dd format.
	*	@param Nothing
	*	@return String currentDate - current date in 'yyyy-mm-dd' format.
	*/
	private static String getCurrentDate()
	{
		String currentDate="";
		Date date=null;
		SimpleDateFormat sdf=null;
		try
		{
			date=new Date();
			sdf=new SimpleDateFormat("yyyy-MM-dd");
			currentDate=sdf.format(date);
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
		}
		return currentDate;
	}

}
