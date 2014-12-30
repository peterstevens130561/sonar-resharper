/*
 * SonarQube ReSharper Plugin
 * Copyright (C) 2014 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.resharper;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandException;
import org.sonar.api.utils.command.CommandExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReSharperExecutor {

	private static final Logger LOG = LoggerFactory
			.getLogger(ReSharperExecutor.class);

	private File resharperReportFile;
	protected File executable;

	private ArrayList<String> arguments = new ArrayList<String>();

	private List<String> properties = new ArrayList<String>();

	private String projectName;

	private File solution;

	private int timeout;


	/**
	 * Sets the report file to generate
	 * 
	 * @param reportFile
	 *            the report file
	 * @return the current builder
	 */
	public ReSharperExecutor setReportFile(File reportFile) {
		this.resharperReportFile = reportFile;
		return this;
	}
	
	public ReSharperExecutor setSolution(File solution) {
		this.solution = solution;
		return this;
	}
	
	/**
	 * set the name of the project to analyse, as found in the solution. If not set, then all projects will be analysed
	 * @param projectName
	 * @return this
	 */
	public ReSharperExecutor setProject(String projectName) {
		this.projectName= projectName;
		return this;
	}
	
	public ReSharperExecutor setTimeOut(int timeout) {
		this.timeout = timeout;
		return this;
	}
	
	/**
	 * Adds the properties to the internal list of properties. Does not check for duplicates!
	 * @param properties 
	 * @return this
	 */
	public ReSharperExecutor addProperties(List<String> properties) {
		this.properties.addAll(properties);
		return this;
	}


	/**
	 * Sets the executable
	 * 
	 * @param executable
	 *            the executable
	 * 
	 */
	public void setExecutable(File executable) {
		this.executable = executable;
	}

	/**
	 * Transforms this command object into a array of string that can be passed
	 * to the CommandExecutor.
	 * 
	 * @return the Command that represent the command to launch.
	 */
	public Command toCommand() {

		// $> c:\ThirdPartyTools\jb-commandline-8.0.0.39\inspectcode.exe /help
		// InspectCode for .NET
		// Running in 32-bit mode, .NET runtime 4.0.30319.18051 under Microsoft
		// Windows NT 6.2.9200.0
		// Usage: InspectCode [options] SolutionFile
		//
		// Options:
		// /output (/o) : Write inspections report to specified file.
		// /no-swea : Disable solution-wide analysis (default: False)
		// /project : Analyze only projects selected by provided wildcards
		// (default: analyze all solution)
		// /profile (/p) : Path to the file to use custom settings from
		// (default: Use R#'s solution shared settings if exists)
		// /no-buildin-settings : Supress solution shared settings profile usage
		// (default: False)
		// /caches-home : Path to the directory where produced cashes will be
		// stored.
		// /debug (/d) : Show debugging messages (default: False)
		// /help (/h) : Show help and exit
		// /version (/v) : Show tool version and exit
		// /dumpPlatforms (/dpl) : Dump platforms description to file and exit
		// /dumpProject (/dpm) : Dump project model description to file and exit

		LOG.debug("- ReSharper program         : " + executable);
		Command command = Command.create(executable.getAbsolutePath());

		if(StringUtils.isNotEmpty(projectName)) {
			LOG.debug("- Project name              : " + projectName);
			command.addArgument("/project=" + projectName);
		}
		LOG.debug("- Report file               : " + resharperReportFile);
		command.addArgument("/output=" + resharperReportFile.getAbsolutePath());

		LOG.debug("- Solution file               : " + solution);

		for (String argument : arguments) {
			LOG.debug(" - Argument : " + argument);
			command.addArgument(argument);
		}

		addPropertiesIfSet(command);
		command.addArgument(solution.getAbsolutePath());

		return command;
	}

	private void addPropertiesIfSet(Command command) {
		if (properties != null && properties.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (String property : properties) {
				sb.append(property).append(";");
			}
			String dirtyArgument = sb.toString();
			String argument = dirtyArgument.substring(0,
					dirtyArgument.length() - 1);
			LOG.debug("- Properties                 : " + argument);
			command.addArgument("/properties:" + argument);
		}
	}

	/**
	 * if value is not empty, the concatenation of name & value are added to the
	 * arguments When building the arguments are added before the solution
	 * argument.
	 * 
	 * @param name
	 *            of the argument
	 * @param value
	 *            of the agument
	 */
	public void addArgument(String name, String value) {
		if (StringUtils.isEmpty(value))
			return;
		arguments.add(name + value);
	}

	public void setCachesHome(String cachesHome) {
		if(StringUtils.isEmpty(cachesHome)) return;
		arguments.add("/caches-home=" + cachesHome);
	}

	public void setProfile(File file) {
		if((file==null)) return;
		arguments.add("/profile=" + file.getAbsolutePath());
	}

	public int  execute() {
		Command cmd=toCommand();
		int exitCode = CommandExecutor.create().execute(cmd, TimeUnit.MINUTES.toMillis(timeout));
		return exitCode;
	}

}