#Project 3
#Java RMI Server & Client with Coordinator 

##Assignment Overview
The purpose and scope of this assignment was to extend on our work from Project 2 by implementing 2 phase commit, 
with the servers voting on whether to make a global commit, or abort. Fulfilling the requirements of this project 
involved changing the architecture of the last project. Instead of having only the client and server communicate with each other, 
we also have the server communicate with the coordinator, which in turn communicates with all the other servers, 
gathering votes from each server on whether to commit or abort, and pushing changes to each server in the event of a commit.
Similar to in project 2, the client and server communicate via Java RMI (Remote Method Invocation). 
Specific to project 3 is the introduction of multiple servers, with the coordinator controlling the state of all servers. 
The way that coordinator and server communication was handled in this project was the coordinator holds a list of all the servers, 
and each server holds an instance of the coordinator. Thus, PUT, GET, and DELETE commands travel from the client to a server, 
then to a coordinator, which can then gather votes, and communicate the commands to all servers. This maintains consistency and 
ensures all servers have the same map. A new Java concept used in this project was TimeoutException, which is used to defensively 
code against hypothetical stalling nodes.

##Technical impression
The general theme for project 3 was the same as in project 2: The client is an HR person at a large company, trying to keep track of 
employee’s salaries. For availability reasons, the company has chosen to have multiple servers, all containing the same employee salaries 
map, running at the same time. This project was significantly harder than projects 1&2 because the requirements for this project were quite 
unique. As a result, Google did not yield many immediately useful resources, and even some of the provided code samples didn’t 
help out too much. I also tried Youtube at least to get some idea of the approach to use but that did not yield any results. 
Something that was never well explained in the project guidelines is that the SERVERS are the ones which are voting. In the resources provided,
 it’s the CLIENTS who are making the votes. A related point is that in the provided resources, there are multiple clients, but only one server. 
This could also lead students down the wrong path, as they might erroneously conclude that they need to build multiple clients. 
If I could offer suggestions for this project, I would recommend additional code resources, as well as clearer project guidelines. 
The hardest part of this project was the lack of material to figure out how to approach the project and also lack of information in the way to do it.


##How to Run

Here are the steps to run the program:

1. Run the Coordinator via "java -jar RunCoordinator.jar <PORT_NUMBER>"

2. Run the Client via "java -jar RunClient.jar <IP_ADDRESS> <PORT_NUMBER>"

3. To execute a PUT command, type "PUT <String> <Integer>"

4. To execute a GET command, type "GET <String>"

5. To execute a DELETE command, type "DELETE <String>"

##Examples with description
Any of the servers can be choosen for the runs
This is just a quick explanation of how the code runs, I had a test run with:
PORT_NUMBER = 32000
IP_ADDRESS = 127.0.0.1
The IP_ADDRESS was my device's address as i ran this on my device. 
With all functionality being met. 
PUT Abdul 5000 - Adds abdul to the map
GET Abdul - returns the value of Abdul
DELETE Abdul - removes Abdul from the map
 
##Limitations/Notes
This project has some limitions and notes that would be listed below:
If the PORT_NUMBER isn't an Integer then there would be a flag.
If the PORT_NUMBER isn't an non-negative Integer less than 65536 then there would be a flag.
If the IP_ADDRESS isn't the traditional numbers seperated by "." then it would be flagged. 
You can only use GET, PUT, DELETE opperations followed by the required and valid inputs. 
As demonstrated above in examples with description.

##Citation
Used https://stackoverflow.com/questions/1459656/how-to-get-the-current-time-in-yyyy-mm-dd-hhmisec-millisecond-format-in-java
for a part of my code, which helped with getting date and time in the right format

used the Notes provided on canvas and class videos, used multiple of them.