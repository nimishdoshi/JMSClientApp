############################## DISCLAIMER ##############################
This code and application is provided as is with no warranty and
the author is responsible for its use or misuse.
########################################################################

The purpose of this project is to demostrate how to use interface inheritance
as the basis for sending out generic Unit of Work objects over a JMS Queue to
allow underutilized computers to execute the code in the Unit of Work Objects
and send results back to the JMS server. This effectively utilizes the
computingresources of the environment and frees up the server to do other
things. 

The project has been tested on WebLogic Server 10.3 on Windows, but it should
work with any JMS implemetation.

Installation Quick Notes:


Download the distribution demo and place it into a directory. Then follow the
instructions to to set the environment variables for setup.bat in install.txt
Run setup.bat. Finally, go back to install.txt to follow the few steps
to install this into a running WebLogic 10.3 server. Keep in mind that
the supplied code uses the Examples Server shipped with WebLogic and if you use
another Domain, you'll have to create some of the entries such as a JMS
server yourself. This has been tested with the BEA WebLogic Server 10.3,
but the code is generic enough to run on any J2EE compliant application server.


-Nimish Doshi

