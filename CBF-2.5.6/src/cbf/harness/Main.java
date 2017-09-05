/******************************************************************************
$Id : Main.java 3/3/2016 6:00:21 PM
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

package cbf.harness;


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cbf.utils.LogUtils;

public class Main {

	/**
	 * Main method to start the execution
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String args[]) {
		
		///File file=new File(".");
		//file.getAbsolutePath();		
		
		Map<String, String> runMap = parseCommandLineArgs(args);
		logger.trace("Arguments", args, runMap);
		TestSetRunner.run(runMap, runMap.get("testSetSheet"));
	}

	@SuppressWarnings("static-access")
	private static Map<String, String> parseCommandLineArgs(String[] args) {
		CommandLine commandLine;
		Map<String, String> runMap = new HashMap<String, String>();
		Options options = new Options();

		options.addOption(OptionBuilder.withArgName("configFilePath").hasArg()
				.create("configfilepath"));
		options.addOption(OptionBuilder.withArgName("testSetFile").hasArg()
				.create("testsetfile"));
		options.addOption(OptionBuilder.withArgName("nodeLabel").hasArg()
				.create("nodelabel"));
		options.addOption(OptionBuilder.withArgName("testSetSheet").hasArg()
				.create("testsetsheet"));
		options.addOption(OptionBuilder.withArgName("almTestFolderPath")
				.hasArg().create("almTestFolderPath"));
		options.addOption(OptionBuilder.withArgName("almTestSet").hasArg()
				.create("almTestSet"));
		options.addOption(OptionBuilder.withArgName("browser").hasArg()
				.create("browser"));
		

		CommandLineParser parser = new GnuParser();

		try {
			commandLine = parser.parse(options, args);

			for (Option option : commandLine.getOptions()) {
				runMap.put(option.getArgName(), option.getValue());
			}

			if (!commandLine.hasOption("configfilepath")) {
				logger.handleError("Parameter 'configfilepath' must be defined");
			}

		} catch (ParseException e) {
			logger.handleError("Error in parsing arguments ", e, args);
		}

		return runMap;
	}

	// CHECKME: see if this has to be done explicitly
	// Working properly.
	private static LogUtils logger = new LogUtils("Main");
}
