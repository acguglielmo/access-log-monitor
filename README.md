Command line application to read and save access.log entries into a MySQL database and block IP addresses that made more requests than the allowed threshold.


### Generated artifact CLI arguments
usage: parser

 -a,--accessLog <arg>    Path to log file. Default value is access.log (in the working directory)

 -c,--configFile <arg>   Path to config file. Default value is config.properties (in the working directory)

 -d,--duration <arg>     Required. Options: [hourly, daily]

 -s,--startDate <arg>    Required. Start date to analysis in the following format: yyyy-MM-dd.HH:mm:ss

 -t,--threshold <arg>    Required. Threshold value to block. Only integer values.
	
