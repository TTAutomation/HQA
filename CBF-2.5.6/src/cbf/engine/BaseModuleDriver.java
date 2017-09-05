/******************************************************************************
$Id : BaseModuleDriver.java 3/3/2016 6:00:13 PM
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

package cbf.engine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cbf.model.ModuleDriver;
import cbf.utils.DataRow;
import cbf.utils.FrameworkException;
import cbf.utils.LogUtils;

/**
 * 
 * Implements ModuleDriver and initializes logger
 * 
 */
public class BaseModuleDriver implements ModuleDriver {

	private LogUtils logger = new LogUtils(this);

	/**
	 * Performs mapping with DataRow
	 * 
	 * @param componentCode
	 *            takes component code value
	 * @param input
	 *            DataRow of input parameters
	 * @param output
	 *            empty DataRow passed to capture any runtime output during
	 *            execution of component
	 */
	public void perform(String componentCode, DataRow input, DataRow output) {
		eval(componentCode, input, output);
	}

	private void eval(String componentCode, DataRow input, DataRow output) {

		Method m = findMethod(componentCode, input, output);
		invokeMethod(m, componentCode, input, output);

	}

	private Method findMethod(String componentCode, DataRow input,
			DataRow output) {
		try {
			Class<?> clazz = this.getClass();
			boolean found = false;
			for (Method m : clazz.getDeclaredMethods()) {
				if (m.getName().equalsIgnoreCase(componentCode)) {
					Class<?>[] pType = m.getParameterTypes();
					if (pType.length == 2) {
						if (!(pType[0].isInstance(input))
								|| !(pType[1].isInstance(output))) {
							logger
									.handleError("Error while finding method due to argument mismatch for "+ componentCode);
							return null;
						}

						found = true;
						return m;
					} else {
						logger
								.handleError("Error while finding method due to mismatch in number of arguments for "+ componentCode);
						return null;
					}
				}
			}
			if (found == false) {
				logger
						.handleError(
								"Invalid component code in test case file"+ componentCode,
								new FrameworkException(
										"Method with the component code doesn't exists",
										componentCode));
			}
		} catch (SecurityException e) {
			logger.handleError("Exception occured : ", e);
		}
		return null;

	}

	private void invokeMethod(Method m, String componentCode, DataRow input,
			DataRow output) {
		try {
			logger.trace("Executing............." + componentCode
					+ " with parameters " + input);
			m.invoke(this, input, output);
		} catch (IllegalArgumentException e) {
			logger.handleError(
					"Incorrect argument structure for the component ",
					componentCode, " ", e);
		} catch (IllegalAccessException e) {
			logger.handleError("Access to the component ", componentCode,
					" is restricted ", e);
		} catch (InvocationTargetException e) {
			logger.handleError("Error in invoking component ", componentCode, " ", e);
		}
	}
}
