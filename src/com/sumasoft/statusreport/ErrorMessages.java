package com.sumasoft.statusreport;

/**
 * This class contains error messages.
 */
public class ErrorMessages 
{
	// Error message when the system is unable to write log.
	public static final String UNBALE_TO_WRITE_LOG = "An error occured while writing log.";
	
	// Error message when the date contains invalid characters
	public static final String UNABLE_TO_PARSE_DATE = " should be in dd/MM/yyyy format and contain only digits.";
	public static final String UNABLE_TO_PARSE_DATES = "Start date and End date should be in dd/MM/yyyy format and contain only digits.";
	
	// Error message when an unexpected error occurs
	public static final String UNEXPECTED_ERROR_OCCURED = "An error is occured. Please check the log file.";
	
	// Error message when Start date is after End date
	public static final String START_DATE_AFTER_END_DATE = "Start Date should not be Greater than End Date.";
	
	// Error message when Start date is after Current date
	public static final String DATE_AFTER_CURRENT_DATE = " should not be a future date.";
	
	// Error message when difference between Start date and End date is more than 30 days.
	public static final String DIFFERENCE_BETWEEN_START_DATE_END_DATE_EXCEEDS = 
												"Difference between Start Date and End Date should not be more than 31 Days.";
	
	// Error message when time is in invalid format
	public static final String IMPROPER_TIME_FORMAT = "Enter %s in proper format [HH24:MM]";
	
	// Error message when hour part of time is incorrect
	public static final String INCORRECT_HOUR_VALUE = "Invalid value for hour in %s. It should be greater than 00 and less than 24.";
	
	// Error message when minutes part of time is incorrect
	public static final String INCORRECT_MINUTES_VALUE = "Invalid value for minutes in %s should be greater than or equal to 00 and " +
														 "less than or equal to 59";
	
	// Error message when minutes part exceeds other minutes part for same Start date and End date.
	public static final String MINUTES_VALUE_EXCEEDS_FOR_SAME_DATES = "Start Time should be less than End Time for Same Dates."; 
	
	// Error message when hours part exceeds other hours part for different Start date and End date.
	public static final String HOURS_VALUE_EXCEEDS_FOR_SAME_DATES = "Start Time should be less than End Time for Same Dates.";
	
	// Error message when an error occurs
	public static final String ERROR_OCCURED = "An error is occurred";
	
	// Error message when connection to database failed
	public static final String UNABLE_TO_ESTABLISH_CONNECTION = "Unable to establish connection with database.";
	
	// Error message when report is successfully saved
	public static final String REPORT_SAVED_SUCCESSFULLY = "Reports are saved successfully.";
	
	// Error message when no report data is available for the for given dates.
	public static final String NO_RECORDS_AVAILABLE = "No Records Found.";
	
	public static final String ENTER_VALID_START_DATE = "Enter valid Start Date.";
	public static final String ENTER_VALID_END_DATE = "Enter valid End Date.";
	
	public static final String ERROR_LOG_FILE_PATH_NOT_AVAIALABLE = "The path for Error Log file is not available.";
}
