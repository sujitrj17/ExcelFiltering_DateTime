package com.sumasoft.statusreport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sumasoft.statusreport.ErrorMessages;

public class DateTimeValidator 
{
	/**
	 * This method validates date and time input parameters
	 * @return true or false
	 */
	/*public boolean validateDateTime()
	{
		List<String> lstErrorMessages = null;
		boolean bRetVal = true;
		boolean bCorrectDateFormat = true;
		boolean bCorrectTimeFormat = true;
		boolean bInvalidStartDate = false;
		boolean bInvalidEndDate = false;
		boolean bInvalidStartTime = false;
		boolean bInvalidEndTime = false;
		String strDatePattern = null;
		
		try
		{
			lstErrorMessages = new ArrayList<String>();
			strDatePattern = "^(0[1-9]|[12][0-9]|3[01])[/](0[1-9]|1[012])[/](20)\\d\\d$";
			
			if(strStartDate==null || strStartDate.trim().length() == 0)
			{
				lstErrorMessages.add("Enter Start Date");
				bRetVal = false;
				bCorrectDateFormat = false;
			}
			else
			{
				strStartDate = strStartDate.trim();
				if(strStartDate!=null && strStartDate.length() == 10)
				{
					if(!Pattern.matches(strDatePattern, strStartDate))
					{
						lstErrorMessages.add(ErrorMessages.ENTER_VALID_START_DATE);
						bCorrectDateFormat = false;
					}
				}
				else
				{
					lstErrorMessages.add(ErrorMessages.ENTER_VALID_START_DATE);
					bCorrectDateFormat = false;
				}
			}
			if(strStartTime==null || strStartTime.trim().length() == 0)
			{
				lstErrorMessages.add("Enter Start Time");
				bRetVal = false;
				bInvalidStartTime = true;
			}
			else
			{
				strStartTime = strStartTime.trim();
				// Checks strStartTime for proper format, correct hours and minutes values
				if(!validateTime(strStartTime, 1, lstErrorMessages))
				{
					bRetVal = false;
					bInvalidStartTime = true;
				}
			}
			if(strEndDate==null || strEndDate.trim().length() == 0)
			{
				lstErrorMessages.add("Enter End Date");
				bRetVal = false;
				bCorrectDateFormat = false;
			}
			else
			{
				strEndDate = strEndDate.trim();
				if(strEndDate!=null && strEndDate.length() == 10)
				{
					if(!Pattern.matches(strDatePattern, strEndDate))
					{
						lstErrorMessages.add(ErrorMessages.ENTER_VALID_END_DATE);
						bCorrectDateFormat = false;
					}
				}
				else
				{
					lstErrorMessages.add(ErrorMessages.ENTER_VALID_END_DATE);
					bCorrectDateFormat = false;
				}
			}
			if(strEndTime==null || strEndTime.trim().length() == 0)
			{
				lstErrorMessages.add("Enter End Time");
				bRetVal = false;
				bInvalidEndTime = true;
			}
			else
			{
				strEndTime = strEndTime.trim();
				// Checks strEndTime for proper format, correct hours and minutes values
				if(!validateTime(strEndTime.trim(), 2, lstErrorMessages))
				{
					bInvalidEndTime = true;
					bRetVal = false;
				}
			}
			
			bCorrectTimeFormat = !bInvalidStartTime && !bInvalidEndTime;
			if(bCorrectDateFormat && bCorrectTimeFormat)
			{
				// Checks whether strStartDate is after current date
				if(!validateDate(strStartDate, 1, lstErrorMessages))
				{
					bInvalidStartDate = true;
					bRetVal = false;
				}
				
				
				
				// Checks whether strEndDate is after current date
				if(!validateDate(strEndDate, 2, lstErrorMessages))
				{
					bInvalidEndDate = true;
					bRetVal = false;
				}
				
				
				
				// Checks whether strStartDate is greater than strEndDate and difference between them exceeds than 30 days
				if(!bInvalidStartDate && !bInvalidEndDate)
				{
					if(!validateDate(strStartDate, strEndDate, lstErrorMessages))
						bRetVal = false;
				}
				
				
				// Start date = End date
				if(strStartDate.equals(strEndDate))
				{
					if(bCorrectTimeFormat)
					{
						// Hour (Start date) = Hour (End date)
						if(Integer.parseInt(strStartTime.split(":")[0]) == Integer.parseInt(strEndTime.split(":")[0]))
						{
							// Minutes (Start date) > Minutes (End date)
							if(Integer.parseInt(strStartTime.split(":")[1]) > Integer.parseInt(strEndTime.split(":")[1]))
							{
								lstErrorMessages.add(ErrorMessages.MINUTES_VALUE_EXCEEDS_FOR_SAME_DATES);
								//System.out.println(ErrorMessages.MINUTES_VALUE_EXCEEDS_FOR_SAME_DATES);
								bRetVal = false;
							}
						}
						// Hour (Start date) != Hour (End date)
						else
						{
							// Hour (Start date) > Hour (End date)
							if(Integer.parseInt(strStartTime.split(":")[0]) > Integer.parseInt(strEndTime.split(":")[0]))
							{
								lstErrorMessages.add(ErrorMessages.HOURS_VALUE_EXCEEDS_FOR_SAME_DATES);
								//System.out.println(ErrorMessages.HOURS_VALUE_EXCEEDS_FOR_SAME_DATES);
								bRetVal = false;
							}
						}
					}
				}
			}
			
			if(!bRetVal || !bCorrectDateFormat || !bCorrectTimeFormat)
			{
				System.out.println("-------------------------------------------------");
				for(String strErrorMessage : lstErrorMessages)
					
					ExceptionLogger.info(strErrorMessage);
					//System.out.println(strErrorMessage);
				System.out.println();
				return (bRetVal && bCorrectDateFormat && bCorrectTimeFormat);
			}
			// strStartDate, strStartTime, strEndDate, strEndTime values are correct. So, return true.
		}
		catch(Exception exception)
		{
			System.out.println(ErrorMessages.UNEXPECTED_ERROR_OCCURED);
			ExceptionLogger.error(exception);
			return false;
		}
		return true;
	}*/
	/**
	 * This method validates time input parameter
	 * @param strTime
	 * @param iFlag
	 * @param lstErrorMessages
	 * @return true or false
	 */
	private boolean validateTime(String strTime, int iFlag, List<String> lstErrorMessages)
	{
		String strTemp = null;
		
		try
		{
			if(iFlag == 1)
				strTemp = "Start Time";
			else if(iFlag == 2)
				strTemp = "End Time";
			
			if(strTime.length() != 5 || strTime.charAt(2) != ':')
			{
				lstErrorMessages.add(new String(ErrorMessages.IMPROPER_TIME_FORMAT).replace("%s", strTemp));
				return false;
			}
			if(Integer.parseInt(strTime.substring(0,2)) > 23)
			{
				lstErrorMessages.add(new String(ErrorMessages.INCORRECT_HOUR_VALUE).replace("%s", strTemp));
				return false;
			}
			if(Integer.parseInt(strTime.substring(3,5)) > 59)
			{
				lstErrorMessages.add(new String(ErrorMessages.INCORRECT_MINUTES_VALUE).replace("%s", strTemp));
				return false;
			}
		}
		catch(NumberFormatException numberFormatException)
		{
			lstErrorMessages.add(new String(ErrorMessages.IMPROPER_TIME_FORMAT).replace("%s", strTemp));
			return false;
		}
		catch(Exception exception)
		{
			System.out.println(ErrorMessages.UNEXPECTED_ERROR_OCCURED);
			ExceptionLogger.error(exception);
			return false;
		}
		return true;
	}
	/**
	 * This method validates date input parameter
	 * @param strDate
	 * @param iFlag
	 * @param lstErrorMessages
	 * @return true or false.
	 */
	private boolean validateDate(String strDate, int iFlag, List<String> lstErrorMessages)
	{
		Date currentDate = null;
		Date targetDate = null;
		SimpleDateFormat simpleDateFormat = null;
		String strTemp = null;
		
		try
		{
			if(iFlag == 1)
				strTemp = "Start Date";
			else if(iFlag == 2)
				strTemp = "End Date";
			
			currentDate = Calendar.getInstance().getTime();
			simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			targetDate = (Date)simpleDateFormat.parse(strDate);
			
			if(targetDate.after(currentDate))
			{
				lstErrorMessages.add(strTemp + ErrorMessages.DATE_AFTER_CURRENT_DATE);
				return false;
			}
		}
		catch(ParseException parseException)
		{
			lstErrorMessages.add(strTemp + ErrorMessages.UNABLE_TO_PARSE_DATE);
			return false;
		}
		catch(Exception exception)
		{
			System.out.println(ErrorMessages.UNEXPECTED_ERROR_OCCURED);
			ExceptionLogger.error(exception);
			return false;
		}
		finally
		{
			currentDate = null;
			targetDate = null;
			simpleDateFormat = null;
		}
		return true;
	}
	
	/**
	 * This method validates date input parameter
	 * @param strStartDate
	 * @param strEndDate
	 * @param lstErrorMessages
	 * @return true or false.
	 */
	private boolean validateDate(String strStartDate, String strEndDate, List<String> lstErrorMessages)
	{
		double slaDateDiff;
		int one_day;
		
		Date dtStartDate = null;
		Date dtEndDate = null;
		SimpleDateFormat simpleDateFormat = null;
		int configDateDiff = 31;
		
		try
		{
			one_day=1000*60*60*24;
			
			simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			dtStartDate = (Date)simpleDateFormat.parse(strStartDate);
			dtEndDate = (Date)simpleDateFormat.parse(strEndDate);
			
			if(dtStartDate.after(dtEndDate))
			{
				lstErrorMessages.add(ErrorMessages.START_DATE_AFTER_END_DATE);
				return false;
			}
			
			slaDateDiff=Math.ceil((dtEndDate.getTime()-dtStartDate.getTime())/(one_day)); 
			
			if(slaDateDiff >= configDateDiff)
			{
				lstErrorMessages.add(ErrorMessages.DIFFERENCE_BETWEEN_START_DATE_END_DATE_EXCEEDS);
				return false;
			}
			
		}
		catch(ParseException parseException)
		{
			lstErrorMessages.add(ErrorMessages.UNABLE_TO_PARSE_DATES);
			return false;
		}
		catch(Exception exception)
		{
			System.out.println(ErrorMessages.UNEXPECTED_ERROR_OCCURED);
			ExceptionLogger.error(exception);
			return false;
		}
		finally
		{
			simpleDateFormat = null;
		}
		return true;
	}
}
