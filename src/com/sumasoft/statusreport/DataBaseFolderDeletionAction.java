package com.sumasoft.statusreport;

import com.sumasoft.statusreport.DataBaseFolderDeletionService;
import com.sumasoft.statusreport.ExceptionLogger;

public class DataBaseFolderDeletionAction 
{
	/**
	 * This method is entry-point to the application.
	 * @param args
	 */
	public static void main(String[] args) 
	{
//		String strStartDate = null;
//		String strEndDate = null;
//		String strStartTime = null;
//		String strEndTime = null;
		DataBaseFolderDeletionService service = new DataBaseFolderDeletionService();
		
		try
		{
			
			//service = new DataBaseFolderDeletionService();
			//reportsService.generateReports(strStartDate, strStartTime, strEndDate, strEndTime);
			//service.dataBaseDelete();
			
			service.filterExcel();
		}
		
		

		catch(Exception exception)
		{
			exception.printStackTrace();
			ExceptionLogger.error(exception);
		}
		finally
		{
			service = null;
		}
	}
}
