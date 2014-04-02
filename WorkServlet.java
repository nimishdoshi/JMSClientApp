// Author: Nimish Doshi

package Sender;

import java.io.IOException;
import java.io.PrintWriter;
import javax.naming.*;
import javax.jms.*;
import javax.servlet.*;
import javax.servlet.http.*;
//import examples.utils.common.ExampleUtils;
import examples.work.UnitOfWork;
import examples.work.SimpleMatrix;

/**
 
 * @author Nimish Doshi Copyright (c) 1999-2005 by BEA Systems, Inc. All Rights Reserved.
 */
public class WorkServlet extends HttpServlet
{
  // Defines the JMS connection factory.
  public final static String JMS_FACTORY="weblogic.examples.jms.QueueConnectionFactory";
  
  // Defines the request queue.
  public final static String REQUEST_QUEUE="Worker.Request";

  // Defines the response queue.
  public final static String RESPONSE_QUEUE="Worker.Response";

    private Context ctx = null;
    private QueueConnectionFactory qconFactory;
    private QueueConnection qcon;
    private QueueSession qsession = null;
    private QueueSender qsender;
    private Queue queue;
    private Queue QueueResponder;
    private ObjectMessage msg;
    private int numMessages = 5;
	


  public synchronized boolean connectToJMS()
  {
	if (ctx != null) // already connected
		return false;
	try {
		ctx = new InitialContext();
		qconFactory = (QueueConnectionFactory) ctx.lookup(JMS_FACTORY);
    		qcon = qconFactory.createQueueConnection();
   		qsession = qcon.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
    		queue = (Queue) ctx.lookup(REQUEST_QUEUE);
    		qsender = qsession.createSender(queue);
		QueueResponder = (Queue)ctx.lookup(RESPONSE_QUEUE);
		msg = qsession.createObjectMessage();
		qcon.start();
		return true;
	} catch (Exception e) {
		System.out.println("**********connectToJMS Failed****");
		e.printStackTrace();
		return false;
	}

  }

  public synchronized boolean disconnectJMS()
  {
	if (ctx == null) // already disconnected
		return false;
	try {
		qsender.close();
		qsession.close();
		qcon.close();
		ctx.close();
		qsender = null;
		qsession = null;
		qcon = null;
		ctx = null;	
		return true;
	} catch (Exception e) {
		e.printStackTrace();
		return false;
	}
  }


  public synchronized boolean sendMessages(int numberOfMessages, PrintWriter o)
  {
  	for(int i=0; i<numberOfMessages; i++) {
		SimpleMatrix simple = new SimpleMatrix();
		// Send message to the request Queue
		try {
		    msg.setObject(simple);
		    msg.setJMSReplyTo(QueueResponder);
		    qsender.send(msg);
		    if (o != null)
		    	o.println("Sent a Message to " + REQUEST_QUEUE + "<br>");
		} catch (Exception e) {
		    System.out.println("WorkServlet:sendMessages:Cannot send message, exception raised");
		    e.printStackTrace();
		    return false;
		}    
	}
	return true;
  }

  public void init(ServletConfig config) throws ServletException
  {
	super.init(config);
	connectToJMS();
	sendMessages(numMessages, null);
  }
	
  /**
   * Process the HTTP Get request. It will simply call the post function
  */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doPost( request, response );
  }

  /**
   * Process the HTTP Post request.
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    
     	response.setContentType("text/html");
	response.setHeader("Pragma", "no-cache");
	PrintWriter out = response.getWriter();
	String action = request.getParameter("Action");
	if (action == null) {
      	String errorMessage = "Error, you cannot call the servlet with no paramaters. ";
      	errorMessage += "Use ?Action=CONNECT or ?Action=DISCONNECT or ?Action=SEND.";
      	out.println(errorMessage);
		return;
    	}

	if (action.equals("DISCONNECT")) {
		if (disconnectJMS() == true)
			out.println("Disconnected successfully");
		else
			out.println("Cannot disconnect; Perhaps, you are already disconnected.");
	} else if (action.equals("CONNECT")) {
		if (connectToJMS() == true)
			out.println("Connected successfully");
		else
			out.println("Cannot connect; Perhaps, you are already connected.");
	} else { // Simple try to send messages
		sendMessages(numMessages, out);
		out.println("Attempted to Send " + numMessages + " Message(s)");
	}

	out.flush();

  }

}

