/******************************************************************************
$Id : FrameworkException.java 3/3/2016 6:01:05 PM
Copyright 2016-2017 IGATE GROUP OF COMPANIES. All rights reserved
(Subject to Limited Distribution and Restricted Disclosure Only.)
THIS SOURCE FILE MAY CONTAIN INFORMATION WHICH IS THE PROPRIETARY
INFORMATION OF IGATE GROUP OF COMPANIES AND IS INTENDED FOR USE
ONLY BY THE ENTITY WHO IS ENTITLED TO AND MAY CONTAIN
INFORMATION THAT IS PRIVILEGED, CONFIDENTIAL, OR EXEMPT FROM
DISCLOSURE UNDER APPLICABLE LAW.
YOUR ACCESS TO THIS SOURCE FILE IS GOVERNED BY THE TERMS AND
CONDITIONS OF AN AGREEMENT BETWEEN YOU AND IGATE GROUP OF COMPANIES.
The USE, DISCLOSURE REPRODUCTION OR TRANSFER OF THIS PROGRAM IS
RESTRICTED AS SET FORTH THEREIN.
******************************************************************************/

package cbf.utils;

/**
 * 
 * Utility for dealing with framework exceptions.
 * 
 */
public class FrameworkException extends RuntimeException {

	public final Object data;
	public final String msg;

	/**
	 * Constructor to initialize message and data parameters
	 * 
	 * @param msg
	 *            message for exception
	 * @param data
	 *            data of exception
	 */
	public FrameworkException(String msg, Object... data) {
		this(msg, null, data);
	}

	/**
	 * Overloaded constructor to initialize message, exception and data
	 * parameters
	 * 
	 * @param msg
	 *            message for exception
	 * @param e
	 *            object of Exception
	 * @param data
	 *            data of exception
	 */
	public FrameworkException(String msg, Exception e, Object... data) {
		super(msg, e);
		this.data = data;
		this.msg = msg;

	}

	/**
	 * Returns exception format string
	 * 
	 */
	public String toString() {
		return StringUtils.mapString(this, msg, super.getCause());

	}
}
