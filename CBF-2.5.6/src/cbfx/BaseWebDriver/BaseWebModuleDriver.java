/******************************************************************************
$Id : BaseWebModuleDriver.java 3/3/2016 6:01:14 PM
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

package cbfx.BaseWebDriver;

import cbf.engine.BaseModuleDriver;
import cbfx.misc.SikuliUIDriver;
import cbfx.selenium.WebUIDriver;

public class BaseWebModuleDriver extends BaseModuleDriver {
	public WebUIDriver uiDriver;
	public SikuliUIDriver sikuliUIDriver;

	public BaseWebModuleDriver() {
		uiDriver = BaseWebAppDriver.uiDriver;
		sikuliUIDriver = BaseWebAppDriver.sikuliUIDriver;
	}
	
	
	
	
	
}
